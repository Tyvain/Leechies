package leechies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import leechies.model.Annonce;

@SuppressWarnings("unchecked")
public class DBManager {
	private static File fileDB = new File("AllAdsDB");
	private static FileInputStream fis;
	private static ObjectInputStream ois;
	private static FileOutputStream fos;
	private static ObjectOutputStream oos;
	private static Map<String, Annonce> allAds;

	public static void saveAnnonce(Annonce annonce) {
		// read from file
		try {
			Map<String, Annonce> allAds = getAllAnnonces();
			System.out.println("DB Size: " + allAds.size() + " saving " + annonce.url);
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

	public static Map<String, Annonce> getAllAnnonces() {
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

	public static boolean annonceExists(String url) {
		boolean rez = getAllAnnonces() != null ? getAllAnnonces().containsKey(url) : false;
		System.out.println(url + " Exists? " + rez);
		return rez;
	}

	public static void uplodAd(Annonce annonce) {
		// /api/v1/ads
		// http://finvalab.com/oc-panel/settings/general
		//
		// Document doc = Jsoup.connect("https://finvalab.com/api/v1/ads")
		// .data("email", "myemailid")
		// .data("pass", "mypassword")
		// // and other hidden fields which are being passed in post request.
		// .userAgent("Mozilla")
		// .post();
		// System.out.println(doc);
		// pgWnFgikcjLKUQxRUY16FNSxzSttVQjS
	}
}
