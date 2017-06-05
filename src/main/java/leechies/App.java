package leechies;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import leechies.model.Annonce;
import leechies.model.Source;
import leechies.sites.AbstractSite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.yamlbeans.YamlReader;


public class App {
    final static Logger logger = LoggerFactory.getLogger("App");

	//public static String ALL_SOURCES[] =  { "sources-immonc.yml", "sources-annonces.yml", "sources-nautisme.yml", "sources-mode.yml", "sources-vehicules.yml" };
	public static String ALL_SOURCES[] =  { "sources-immonc.yml" };
	public static String SOURCES[] = ALL_SOURCES;

	// # !!!
	private static int FORCE_REMOVE_UPLOAD_ADS= 0; // remove the last x ads from website
	private static boolean RESET_DB = false; // reset local DB (backup old one)
	// # !!!
	
	private static int MAX_UPLOAD_ADS = 4000; // max ads on website	
	private static int LOG_ADS_EVERY  = 10; // log every x ads

	public static void main(String[] args) throws IOException, URISyntaxException {
	    int counter = 1;	    
	    
	    while (true) {
	    int totalUpAnnonces = UploadManager.countAnnonces();
	    int diff = totalUpAnnonces - MAX_UPLOAD_ADS;
	    	    
	    String initTrace = "\n### PARAMS ### Lancement N° " + counter++;
	    initTrace += "\nSOURCES: " + SOURCES.length;
	    initTrace += "\nFORCE_REMOVE_UPLOAD_ADS: " + FORCE_REMOVE_UPLOAD_ADS;
	    initTrace += "\nRESET_DB: " + RESET_DB;
	    initTrace += "\nMAX_UPLOAD_ADS: " + MAX_UPLOAD_ADS;	    
	    initTrace += "\n### INFOS ### " ;	    
	    initTrace += "\n-- Total Ads online: " + totalUpAnnonces;	   	    
	    initTrace += "\n-- LOCAL DB";
        initTrace += "\n  - avec images: " + DBManager.getAnnoncesByCriteria(null, null, null, true).count();
        initTrace += "\n  - sans images: " + DBManager.getAnnoncesByCriteria(null, null, null, false).count();
        initTrace += "\n  - uploaded: " + DBManager.getAnnoncesByCriteria(null, true, null, null).count();
        initTrace += "\n  - non uploaded: " + DBManager.getAnnoncesByCriteria(null, false, null, null).count();
        initTrace += "\n  - commerciales: " + DBManager.getAnnoncesByCriteria(null, null, true, null).count();
        initTrace += "\n  - non commerciales: " + DBManager.getAnnoncesByCriteria(null, null, false, null).count();
        initTrace += "\n  - avec erreurs: " + DBManager.getAnnoncesByCriteria(true, null, null, null).count();
        initTrace += "\n  - sans erreurs: " + DBManager.getAnnoncesByCriteria(false, null, null, null).count();    
	    
	    logger.info(initTrace);
	    
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
	   
	   goLeech();	   
	   }
	}

/*    private static void goUpload () {
            logger.info("Starting goUpload...");
            DBManager.getAnnoncesByCriteria(false, false, false, true).forEach(a -> UploadManager.uploadAnnonceWithImage(a));
            logger.info("... goUpload finished!");
   }*/

    private static void goLeech() {
        final AtomicInteger count = new AtomicInteger();
        final AtomicLong avgTimeByAds = new AtomicLong();
        final Instant start = Instant.now();
        
		logger.info("Starting goLeech...");
		App.getSourceStream()
		.flatMap(s -> {
			return getAnnonceFromSource(s);
		})
		.forEach(a -> {
		      DBManager.saveAnnonce(a);
		      
		      
		      if (a.hasError == false && a.isCommerciale == false && (a.imgs != null && a.imgs.length > 0)) {
		          System.out.println("uploading ad: " + a);
		          UploadManager.uploadAnnonceWithImage(a);		
		      }
		      
		      Duration duration = Duration.between(start, Instant.now());
		      avgTimeByAds.set(duration.getSeconds() / count.incrementAndGet());
		      		      
		      // on trace toutes les x annonces
		      if (count.get() % 10 == 0) {
		          String msg = "Nb annonces traitées: " + count.get() + "\nTemps moyen par annonce: " + avgTimeByAds + " sec"; 
		          logger.info(msg);
		          System.out.println(msg);
		      }
		    });
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
