package leechies;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import leechies.model.Annonce;
import leechies.model.Category;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Upload {

    private static String UT      = "39924a52f759ee5de2b10285f8daaadf12a59d4d";
    private static String IDU     = "1"; // id user
    private static String URL_ADS = "http://finvalab.com/api/v1/ads";
    private static String URL_ADS_IMAGE = "http://finvalab.com/api/v1/ads/image/";


    private static Connection getConnectionAdService(String url) {
        return Jsoup.connect(url)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("User-Agent", "Mozilla/5.0")
                    .ignoreContentType(true);
    }

    public static void uploadAnnonceWithImage(Annonce annonce) {
            String idAd;
            try {
                idAd = uploadAnnonce(annonce); 
                for (String img : annonce.imgs) {
                    uploadImage(img, idAd);
                    }
            } catch (IOException | URISyntaxException e) {
                annonce.hasError = true;
                annonce.error = e.getMessage();
                DBManager.saveAnnonce(annonce);
                return;
            }
            annonce.uploadedTime = new Date();
            annonce.error = "";
            annonce.hasError = false;
             DBManager.saveAnnonce(annonce);            
        }

    public static String uploadAnnonce(Annonce annonce) throws IOException {
        int cat = Category.getCategoryFromLibelle(annonce.category).id;
        Document doc = getConnectionAdService(URL_ADS)
                .data("id_user", IDU)
                .data("id_category", ""+cat)
                .data("title", annonce.titre)
                .data("description", annonce.texte + "<br><a href='"+annonce.url+"'>annonce d'origine</a>") 
                .data("user_token", UT)
                .data("website ", annonce.url)
                .data("price  ", annonce.prix)
                .post();

        String myJSONString = doc.text();
        JsonObject jobj = new Gson().fromJson(myJSONString, JsonObject.class);
        return "" + jobj.get("ad").getAsJsonObject().get("id_ad");
    }
    
    public static void uploadImage(String u, String adId) throws IOException, URISyntaxException {
        // create file
        URL url = new URL(u);
        File file = new File("./temp.jpg");
        FileUtils.copyURLToFile(url, file);        
        
        // upload image
        getConnectionAdService(URL_ADS_IMAGE+adId) 
        .data("image", file.getName(), new FileInputStream(file))
        .data("user_token", UT)
        .post();       
        }
    

    public static void main(String[] args) throws IOException, URISyntaxException {       
     /*  URL url = new URL("http://thiswallpaper.com/cdn/hdwallpapers/747/cute%20pomeranian%20small%20dog%20high%20resolution%20wallpaper.jpg");
        File file = new File("./temp.jpg");
        
        System.out.println("Creating file...");    
        FileUtils.copyURLToFile(url, file);
        System.out.println("File created!");  */
      
      
   /*   Annonce test = new Annonce();
        test.titre="testTitle";
        test.texte="testDescription: ";
        test.category = Category.ACCESSOIRES_BIJOUX.libelle;
        test.url="www.google.com";
        String newAdId = uploadAnnonce(test); 
        
        System.out.println("Annonce créée : " + newAdId);*/         
        //uploadImage("http://thiswallpaper.com/cdn/hdwallpapers/747/cute%20pomeranian%20small%20dog%20high%20resolution%20wallpaper.jpg", "5545");
    }
}
