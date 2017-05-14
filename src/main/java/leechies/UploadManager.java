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

public class UploadManager {

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
            AnnonceCleaner.cleanAnnonce(annonce);
        
            String idAd=null;
            annonce.hasError = false;
            annonce.error = "";
            annonce.uploadedTime = null;
             try {
                idAd = uploadAnnonce(annonce); 
            } catch (IOException e) {
                annonce.hasError = true;
                annonce.error = "Err upload AD: " + e.getMessage();
                DBManager.saveAnnonce(annonce);
                return;
            }
      
            int imageSucceedUpload=0;            
                for (String img : annonce.imgs) {
                   try {
                    uploadImage(img, idAd);                   
                      } catch (IOException | URISyntaxException e) {
                        annonce.hasError = true;
                        annonce.error += "Err image: " + img + " - ";
                        }
                   imageSucceedUpload++;
                }
      
            
            // on a réussit à uploader au moins une image
            if (imageSucceedUpload > 0) {            
                annonce.uploadedTime = new Date();                
            } else { // aucune image -> on delete
                try {                    
                    deleteAnnonce(idAd);
                } catch (IOException e) {
                    System.out.println("Erreru deleting ad : " + e);
                  }
            }
            DBManager.saveAnnonce(annonce);  
        }

    public static String uploadAnnonce(Annonce annonce) throws IOException {
        int cat = Category.getCategoryFromLibelle(annonce.category).id;
        Document doc = getConnectionAdService(URL_ADS)
                .data("id_user", IDU)
                .data("id_category", ""+cat)
                .data("title", annonce.titre)
                .data("description", annonce.texte) 
                .data("user_token", UT)
                .data("website", annonce.url)
                .data("price", annonce.prix)
                .post();

        String myJSONString = doc.text();
        JsonObject jobj = new Gson().fromJson(myJSONString, JsonObject.class);
        return "" + jobj.get("ad").getAsJsonObject().get("id_ad");
    }
    
    public static void uploadImage(String u, String adId) throws IOException, URISyntaxException {
        // create file
        URL url = new URL(u.replaceAll(" ", "%20"));
        File file = new File("./temp.jpg");
        FileUtils.copyURLToFile(url, file);        
        
        // upload image
        getConnectionAdService(URL_ADS_IMAGE+adId) 
        .data("image", file.getName(), new FileInputStream(file))
        .data("user_token", UT)
        .post();       
        }
    
    
    public static void deleteAnnonce (String idAd) throws IOException {
        getConnectionAdService(URL_ADS+"/delete/"+idAd) 
        .data("user_token", UT)
        .post();  
        }

    public static void main(String[] args) throws IOException, URISyntaxException {
        
       // System.out.println("wtf");
       // deleteAnnonce("6651"); 

       /*URL url = new URL("http://immonc.com/photos/photos_big/5265305_1461800535_port du sud chaaf (6).JPG");
        File file = new File("/projects/Leechies/temp2.jpg");
        
        System.out.println("Creating file...");    
        FileUtils.copyURLToFile(url, file);
        System.out.println("File created!");  */
        
        //
      
      
      Annonce test = new Annonce();
        test.titre="testTitle 2";
        test.texte="<a href='www.google.com'>annonce d'origine</a>";
        test.category = Category.ACCESSOIRES_BIJOUX.libelle;
        test.url="http://www.google.com";
        test.prix="0";
        String newAdId = uploadAnnonce(test); 
        
        System.out.println("Annonce créée : " + newAdId);      
        //uploadImage("http://thiswallpaper.com/cdn/hdwallpapers/747/cute%20pomeranian%20small%20dog%20high%20resolution%20wallpaper.jpg", "5545");
    }
}
