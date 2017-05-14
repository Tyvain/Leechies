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
	public static String ALL_SOURCES[] =  { "sources-annonces.yml", "sources-nautisme.yml", "sources-mode.yml", "sources-vehicules.yml", "sources-immobiliernc.yml" };
	public static String SOURCES[] = ALL_SOURCES;
	
	//public static String SOURCES[] = { "sources-immobiliernc.yml" };

	public static void main(String[] args) throws IOException, URISyntaxException {
	    //DBManager.resetDB();
        goLeech();
        goUpload();        
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
