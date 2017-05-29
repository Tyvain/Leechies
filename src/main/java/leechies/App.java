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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.yamlbeans.YamlReader;

public class App {
    final static Logger logger = LoggerFactory.getLogger("App");

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
	    
	    logger.info("### PARAMS ### " );
	    logger.info("ALL_SOURCES: " + ALL_SOURCES);
	    logger.info("SOURCES: " + SOURCES);
	    logger.info("FORCE_REMOVE_UPLOAD_ADS: " + FORCE_REMOVE_UPLOAD_ADS);
	    logger.info("RESET_DB: " + RESET_DB);
	    logger.info("MAX_UPLOAD_ADS: " + MAX_UPLOAD_ADS);
	    logger.info("GO_LEECH: " + GO_LEECH);
	    logger.info("GO_UPLOAD: " + GO_UPLOAD);
	    
	    
	    logger.info("### INFOS ### " );
	    int totalUpAnnonces = UploadManager.countAnnonces();
	    logger.info("-- Total Ads online: " + totalUpAnnonces);	   
	    int diff = totalUpAnnonces - MAX_UPLOAD_ADS;
	    
	    logger.info("-- LOCAL DB");
        logger.info("Nombre d'annonces: " + totalUpAnnonces);
        logger.info("  - avec images: " + DBManager.getAnnoncesByCriteria(null, null, null, true).count());
        logger.info("  - sans images: " + DBManager.getAnnoncesByCriteria(null, null, null, false).count());
        logger.info("  - uploaded: " + DBManager.getAnnoncesByCriteria(null, true, null, null).count());
        logger.info("  - non uploaded: " + DBManager.getAnnoncesByCriteria(null, false, null, null).count());
        logger.info("  - commerciales: " + DBManager.getAnnoncesByCriteria(null, null, true, null).count());
        logger.info("  - non commerciales: " + DBManager.getAnnoncesByCriteria(null, null, false, null).count());
        logger.info("  - avec erreurs: " + DBManager.getAnnoncesByCriteria(true, null, null, null).count());
        logger.info("  - sans erreurs: " + DBManager.getAnnoncesByCriteria(false, null, null, null).count());
        logger.info("     - (à uploaded) non commerciales avec images non uploaded sans erreur: "
                           + DBManager.getAnnoncesByCriteria(false, false, false, true).count());
	    
	    
		if (RESET_DB) {
			logger.warn("Reseting DB...");
			DBManager.resetDB();
		}

	   
	   if (diff > 0) {	       
	       UploadManager.removeLastAnnonces(diff);
	       }
	   
	   if (FORCE_REMOVE_UPLOAD_ADS > 0) {
		   UploadManager.removeLastAnnonces(FORCE_REMOVE_UPLOAD_ADS);
	   }
	   
	   if (GO_LEECH) {
		   goLeech();
	   }
	   
	   if (GO_UPLOAD) {
        goUpload();    
	   }
	}

    private static void goUpload () {
            logger.info("Starting goUpload...");
            DBManager.getAnnoncesByCriteria(false, false, false, true).forEach(a -> UploadManager.uploadAnnonceWithImage(a));
            logger.info("... goUpload finished!");
   }

    private static void goLeech() {
		logger.info("Starting goLeech...");
		App.getSourceStream().flatMap(s -> {
			return getAnnonceFromSource(s);
		}).forEach(a -> DBManager.saveAnnonce(a));
		logger.info("... goLeech finished!");
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
			logger.error("App.getAnnonce - " + e);
		}
		return null;
	}

	private static Stream<Source> getSourceStream() {
		Stream<Source> ret = Stream.empty();
		try {
			for (String source : SOURCES) {
			    
			    ClassLoader classLoader = App.class.getClassLoader();
			    File file = new File(classLoader.getResource(source).getFile());
			    logger.info("file : " + file);
			    YamlReader reader = new YamlReader(new FileReader(file));
			    
				//YamlReader reader = new YamlReader(new FileReader(source));
				@SuppressWarnings("unchecked")
				ArrayList<Source> wtf = (ArrayList<Source>) reader.read();
				ret = Stream.concat(ret, wtf.stream());
			}
		} catch (Exception e) {
			logger.error("App.getSourceStream - " + e);
		}
		return ret;
	}
}
