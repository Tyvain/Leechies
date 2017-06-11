package leechies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import leechies.model.Annonce;
import leechies.model.Category;
import leechies.model.Location;

public class UploadManager {
    final static Logger logger = LoggerFactory.getLogger("UploadManager");

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

//    public static void removeDefinitlyAds(String idAd) {
//        try{
//         Connection.Response response = Jsoup
//			        .connect("https://yclas.com/panel/auth/login")
//			        .header("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//					.header("content-type","application/x-www-form-urlencoded")
//					.header("origin","https://yclas.com")
//					.header("referer","https://yclas.com/")
//					.header("upgrade-insecure-requests","1")
//					.header("user-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
//					.data("email", "tyvain@gmail.com")
//					.data("password", "billy2000")
//					.data("remember", "on")
//					.data("auth_redirect", "https://yclas.com/")
//					.data("csrf_login", "Rn6h3tmdU2zVEK9QVec09b")
//			        .method(Connection.Method.POST)
//			        .followRedirects(false)
//			        .execute();
//
//		 String session = response.cookies().get("session");
//		 String cfduid = response.cookies().get("__cfduid");
//				 Jsoup
//			        .connect("http://finvalab.com/oc-panel/ad/delete?id_ads%5B%5D="+idAd)
//			        .header("Accept-Language","en-US,en;q=0.8,fr;q=0.6")
//					.header("Upgrade-Insecure-Requests","1")
//					.header("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Safari/537.36")
//					.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//					.header("Referer","http://finvalab.com/oc-panel/ad?status=50")
//					.header("Cookie","sentry-cookies-test=1493268748648; sentry-cookies-test=1493269907979; __cfduid="+cfduid+"; yclasBarMinimized=1; sentry-cookies-test=1492720481433; viewedOuibounceModal=true; list/grid=1; __unam=7639673-15a0feef3d0-26062f01-208; _ga=GA1.2.1810652207.1485810373; _gid=GA1.2.1684047997.1495053567; _gat=1; authautologin=5b2417368f5e54876e9eeece79781ef62057e938%7Ef9fd679b48576e5d7e00ab2e921075560128c783; session="+session+"; sidebar_state=not-collapsed; user_language=ae4e139c2d7dc60f1ce21d13c230d568754ecc19%7Efr_FR; theme=57323fbc3039feb5cae3d8a9c7c59efd57801cb5%7Ereclassifieds; skin_reclassifieds=e1badbc5175bf41293c957aad5ebf362876e3915%7Eblue")				
//			        .method(Connection.Method.GET)
//			        .followRedirects(false)
//			        .execute();
//			        } catch (Exception e) {
//			         logger.error("RemoveDefinitlyAds - " + e);
//		          }		 
//        }

    private static HttpURLConnection getFVConnection (int nbLastAnnonces) {
            try {
            URL myUrl = new URL(URL_ADS+"?user_token=39924a52f759ee5de2b10285f8daaadf12a59d4d&items_per_page="+nbLastAnnonces+"&sort=created&status!=50");
            //System.out.println("Calling : " + myUrl);
            HttpURLConnection myURLConnection = (HttpURLConnection)myUrl.openConnection();      
            myURLConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            myURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            myURLConnection.setRequestMethod("GET");
            return myURLConnection;
            } catch (IOException e) {
                logger.error("GetFVConnection - " + e);
            }
            return null;
         }

        public static void removeLastAnnonces (int nbLastAnnonces) {             
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
            logger.error("GetLastAnnonces - " + e);
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

    public static boolean uploadAnnonceWithImage(Annonce annonce) {
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
                deleteAnnonce(idAd);
                DBManager.saveAnnonce(annonce);
                logger.error("uploadAnnonceWithImage - Up Ad - " + annonce + "\n" + e);
                return false;
            }
      
            int imageSucceedUpload=0;            
                for (String img : annonce.imgs) {
                   try {
                    uploadImage(img, idAd);
                    imageSucceedUpload++;
                      } catch (IOException e) {
                        annonce.hasError = true;
                        annonce.error += "Err image: " + img + " - " + e;
                        }                  
                }
      
            
            // on a réussit à uploader au moins une image
            if (imageSucceedUpload > 0) {            
                annonce.uploadedTime = new Date();                
            } else { // aucune image -> on delete                 
                    deleteAnnonce(idAd);
                    return false;
            }
            DBManager.saveAnnonce(annonce);  
            return true;
        }

    public static String uploadAnnonce(Annonce annonce) throws IOException {
        int cat = Category.getCategoryFromLibelle(annonce.category).id;
        String location =  Location.getIdByLocation(annonce.ville);
        Document doc = getConnectionAdService(URL_ADS)
                .data("id_user", IDU)
                .data("id_category", ""+cat)
                .data("title", annonce.titre)
                .data("description", annonce.texte) 
                .data("user_token", UT)
                .data("website", annonce.url)
                .data("price", annonce.prix)
                .data("id_location",location!=null?location:"")
                .timeout(60000)
                .post();

        String myJSONString = doc.text();
        JsonObject jobj = new Gson().fromJson(myJSONString, JsonObject.class);
        return "" + jobj.get("ad").getAsJsonObject().get("id_ad");
    }
    
    public static void uploadImage(String u, String adId) throws IOException {
        // create file
        URL url = null;
		try {
			url = new URL(u.replaceAll(" ", "%20"));
		} catch (MalformedURLException e1) {
			logger.error("UploadImage - URL" + "\nu: " + u + "\nadId:" + adId + "\nurl: " + url  + "\nerr:" + e1);
			throw e1;
		}
        File file = new File("./temp.jpg");
        try {
			FileUtils.copyURLToFile(url, file);
		} catch (IOException e) {
			logger.error("UploadImage - copyURLToFile - " + u + " - " + adId + "\n" + e);
			throw e;
		}        
        
        // upload image
        try {
			getConnectionAdService(URL_ADS_IMAGE+adId) 
			.data("image", file.getName(), new FileInputStream(file))
			.data("user_token", UT)
			.timeout(60000)
			.post();
		} catch (IOException e) {
			logger.error("UploadImage - getConnectionAdService - " + u + " - " + adId + "\n" + e);
			throw e;
	    }
    }
    
    
	public static void deleteAnnonce(String idAd) {
		if (!StringUtils.isEmpty(idAd)) {
			try {
				getConnectionAdService(URL_ADS + "/delete/" + idAd).data("user_token", UT).post();
			} catch (IOException e) {
				logger.error("DeleteAnnonce - " + e);
			}
		}
	}
}
