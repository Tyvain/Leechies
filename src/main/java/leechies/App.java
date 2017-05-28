package leechies;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.stream.Stream;

import leechies.model.Annonce;
import leechies.model.Source;
import leechies.sites.AbstractSite;

import com.esotericsoftware.yamlbeans.YamlReader;

public class App {

	public static String ALL_SOURCES[] =  { "sources-annonces.yml", "sources-nautisme.yml", "sources-mode.yml", "sources-vehicules.yml", "sources-immonc.yml" };
	//public static String ALL_SOURCES[] =  { "sources-vehicules.yml" };
	public static String SOURCES[] = ALL_SOURCES;

	// # !!!
	private static int FORCE_REMOVE_UPLOAD_ADS= 0; // remove the last x ads from website
	private static boolean RESET_DB = false; // reset local DB (backup old one)
	// # !!!
	
	private static int MAX_UPLOAD_ADS = 4000; // max ads on website	
	private static boolean GO_LEECH = true; // leech + DB insert
	private static boolean GO_UPLOAD = true; // upload ads

	public static void main(String[] args) throws IOException, URISyntaxException {
		if (RESET_DB) {
			System.out.println("Reseting DB");
			DBManager.resetDB();
		}
	   int totalUpAnnonces = UploadManager.countAnnonces();
	   System.out.println("Total annonces: " + totalUpAnnonces);
	   System.out.println("MAX_UPLOAD_ADS: " + MAX_UPLOAD_ADS);
	   int diff = totalUpAnnonces - MAX_UPLOAD_ADS;
	   System.out.println("diff: " + diff);
	   
	   if (diff > 0) {	       
	       UploadManager.removeLastAnnonces(diff);
	       }
	   
	   if (FORCE_REMOVE_UPLOAD_ADS > 0) {
		   UploadManager.removeLastAnnonces(FORCE_REMOVE_UPLOAD_ADS);
	   }
	   
	   if (GO_LEECH) {
		   System.out.println("Go leech");
		   goLeech();
	   }
	   
	   if (GO_UPLOAD) {
		System.out.println("Go upload");
        goUpload();    
	   }
	}

    private static void goUpload () {
            System.out.println("Start goUpload...");
            DBManager.getAnnoncesByCriteria(false, false, false, true).forEach(a -> UploadManager.uploadAnnonceWithImage(a));
            System.out.println("... goUpload finished!");
   }

    private static void goLeech() {
		System.out.println("Start goLeech...");
		App.getSourceStream().flatMap(s -> {
			return getAnnonceFromSource(s);
		}).forEach(a -> DBManager.saveAnnonce(a));
		System.out.println("... goLeech finished!");
        }

	private static Stream<Annonce> getAnnonceFromSource(Source source) {
		return source.rubriques.stream()
				.flatMap(r -> r.subUrls.stream().flatMap(u -> App.getAnnonce(source, u, r.category.libelle)));
	}

	private static Stream<Annonce> getAnnonce(Source source, String url, String rub) {
		Class<?> clazz;
		try {
			clazz = Class.forName(source.className);
			AbstractSite site = (AbstractSite) clazz.newInstance();
			return site.getAnnonces(source.rootUrl, url, rub);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			System.err.println("Zuuut !! " + e);
		}
		return null;
	}

	private static Stream<Source> getSourceStream() {
		Stream<Source> ret = Stream.empty();
		try {
			for (String source : SOURCES) {
			    
			    ClassLoader classLoader = App.class.getClassLoader();
			    File file = new File(classLoader.getResource(source).getFile());
			    System.out.println("file : " + file);
			    YamlReader reader = new YamlReader(new FileReader(file));
			    
				//YamlReader reader = new YamlReader(new FileReader(source));
				@SuppressWarnings("unchecked")
				ArrayList<Source> wtf = (ArrayList<Source>) reader.read();
				ret = Stream.concat(ret, wtf.stream());
			}
		} catch (Exception e) {
			System.err.println("Ooops!! " + e);
		}
		return ret;
	}
}
