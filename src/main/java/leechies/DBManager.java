package leechies;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import leechies.model.Annonce;

public class DBManager {
	final static Logger logger = LoggerFactory.getLogger("DBManager");
	
	private static MongoClientURI uri  = new MongoClientURI("mongodb://finvalab:fv2017@ds119524.mlab.com:19524/finvalab"); 
	private static MongoClient client = new MongoClient(uri);
	private static MongoDatabase db = client.getDatabase(uri.getDatabase());
	
/*	private static MongoClient mongo = new MongoClient("mongodb://finvalab:fv2017@ds119524.mlab.com", 19524);
	private static MongoDatabase db = mongo.getDatabase("finvalab");*/
	private static MongoCollection<Document> table = db.getCollection("annonces");
	
	public static void saveAnnonce(Annonce annonce) {
		try {
			table.insertOne(annonce.toDocument());		
		} catch (Exception e) {
			logger.error("saveAnnonce: " + e);
		}
	}

	public static Map<String, Annonce> getAllAnnoncesMap() {
		try {
			HashMap<String, Annonce> rez = new HashMap<>();
			table.find().forEach((Block<Document>) document -> {
			    Annonce annoce = Annonce.toAnnonce(document);
			    rez.put(annoce.url, annoce);
			});
			return rez;
		} catch (Exception e) {			
			logger.error("getAllAnnoncesMap" + e);
			return new HashMap<String, Annonce>();
		}
	}

	public static Stream<Annonce> getAllAnnonces() {
			return getAllAnnoncesMap().values().stream();		
	}

    public static Annonce getAnnoncesByUrl(String url) {
       // return getAllAnnonces().filter(f -> url.equalsIgnoreCase(f.url)).findFirst();
    	return getAllAnnoncesMap().get(url);
    }
	
    public static Stream<Annonce> getAnnoncesByCriteria(Boolean hasError, Boolean isUploaded, Boolean isCommerciale, Boolean hasImages) {
        return getAllAnnonces()
        .filter(f -> isCommerciale !=null? f.isCommerciale == isCommerciale:true)
        .filter(f -> hasImages!=null? (!hasImages && (f.imgs == null || f.imgs.size() == 0)) || (hasImages && f.imgs != null && f.imgs.size() > 0):true)
        .filter(f -> hasError!=null?f.hasError == hasError:true)
        .filter(f -> isUploaded!=null? (!isUploaded && f.uploadedTime == null) || (isUploaded && f.uploadedTime != null):true);
    }

	public static boolean annonceExists(String url) {
		boolean rez = getAllAnnoncesMap() != null ? getAllAnnoncesMap().containsKey(url) : false;
		return rez;
	}
}
