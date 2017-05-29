package leechies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import leechies.model.Annonce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class DBManager {
    final static Logger logger = LoggerFactory.getLogger("DBManager");
    
    private static String DB_URL = "/projects/Leechies/src/main/resources/AllAdsDB";
	private static File fileDB = new File(DB_URL);
	private static FileInputStream fis;
	private static ObjectInputStream ois;
	private static FileOutputStream fos;
	private static ObjectOutputStream oos;
	private static Map<String, Annonce> allAds;

    public static void resetDB () throws IOException {
        Files.copy(fileDB.toPath(), Paths.get(DB_URL+"_BACKUP_"+new Date()), StandardCopyOption.REPLACE_EXISTING);
		try {
			fos = new FileOutputStream(fileDB);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(new HashMap<String, Annonce>());
			oos.flush();
			fos.close();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public static void saveAnnonce(Annonce annonce) {
		// read from file
		try {
			Map<String, Annonce> allAds = getAllAnnoncesMap();
			//logger.info(" Saving " + annonce.url + "DB Size: " + allAds.size() );
			allAds.put(annonce.url, annonce);
			fos = new FileOutputStream(fileDB);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(allAds);
			oos.flush();
			fos.close();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String, Annonce> getAllAnnoncesMap() {
		// read from file
		try {
			fis = new FileInputStream(fileDB);
			ois = new ObjectInputStream(fis);
			if (allAds == null) {
				allAds = (Map<String, Annonce>) ois.readObject();
			}
			fis.close();
			ois.close();
			return allAds;
		} catch (ClassNotFoundException | IOException e) {
			
			e.printStackTrace();
			return null;
		}

	}

	public static Stream<Annonce> getAllAnnonces() {
			return getAllAnnoncesMap().values().stream();		
	}

    public static Optional<Annonce> getAnnoncesByUrl(String url) {
        return getAllAnnonces()
        .filter(f -> url.equalsIgnoreCase(f.url)).findFirst();
    }
	
    public static Stream<Annonce> getAnnoncesByCriteria(Boolean hasError, Boolean isUploaded, Boolean isCommerciale, Boolean hasImages) {
        return getAllAnnonces()
        .filter(f -> isCommerciale !=null? f.isCommerciale == isCommerciale:true)
        .filter(f -> hasImages!=null? (!hasImages && (f.imgs == null || f.imgs.length == 0)) || (hasImages && f.imgs != null && f.imgs.length > 0):true)
        .filter(f -> hasError!=null?f.hasError == hasError:true)
        .filter(f -> isUploaded!=null? (!isUploaded && f.uploadedTime == null) || (isUploaded && f.uploadedTime != null):true);
    }

	public static boolean annonceExists(String url) {
		boolean rez = getAllAnnoncesMap() != null ? getAllAnnoncesMap().containsKey(url) : false;
		return rez;
	}

}
