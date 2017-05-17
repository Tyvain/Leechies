package leechies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import leechies.model.Annonce;
import leechies.model.Category;
import leechies.model.FinValavAnnonceList;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    public static int countAnnonces () {
           return getLastAnnonces(Integer.MAX_VALUE).length();
    }

    private static HttpURLConnection getFVConnection (int nbLastAnnonces) {
            try {
            URL myUrl = new URL(URL_ADS+"?user_token=39924a52f759ee5de2b10285f8daaadf12a59d4d&items_per_page="+nbLastAnnonces+"&sort=created&status!=50");
            System.out.println("Calling : " + myUrl);
            HttpURLConnection myURLConnection = (HttpURLConnection)myUrl.openConnection();      
            myURLConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            myURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            myURLConnection.setRequestMethod("GET");
            return myURLConnection;
            } catch (IOException e) {
                System.err.println("Erreur getFVConnection : " + e);
            }
            return null;
         }

        public static void removeLastAnnonces (int nbLastAnnonces) {
            System.out.println("Removing the last " + nbLastAnnonces + " ads.");
             
           try {
            getLastAnnonces (nbLastAnnonces).forEach(item -> {
                JSONObject obj = (JSONObject) item;
                deleteAnnonce(""+obj.get("id_ad"));
            });
            }
            catch (JSONException e) {
             System.err.println("Erreur removeLastAnnonces : " + e);
            }
        }

      public static JSONArray getLastAnnonces(int nbLastAnnonces) {
         try {           
            HttpURLConnection myURLConnection = getFVConnection(nbLastAnnonces); 
            myURLConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            myURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            myURLConnection.setRequestMethod("GET");           
            BufferedReader rd = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
           myURLConnection.getInputStream().close(); 
           return json.getJSONArray("ads");
        } catch (IOException | JSONException e) {
           System.err.println("Erreur getLastAnnonces : " + e);
          }
          return null;
      }
			
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
        }
        return sb.toString();
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
                    deleteAnnonce(idAd);
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
    
    
    public static void deleteAnnonce (String idAd) {
        try {
            getConnectionAdService(URL_ADS+"/delete/"+idAd) 
            .data("user_token", UT)
            .post();
        } catch (IOException e) {
           System.err.println("Erreur deleteAnnonce : " + e);
        }  
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
