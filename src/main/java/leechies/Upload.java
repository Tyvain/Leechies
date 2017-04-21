package leechies;

import java.io.IOException;

import leechies.model.Annonce;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Upload {

private static String UT="39924a52f759ee5de2b10285f8daaadf12a59d4d";
private static String IDU="15"; //user import annonces
private static String URL_ADS="http://finvalab.com/api/v1/ads";


private static Connection getConnectionAdService() {
    return Jsoup.connect(URL_ADS)
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                            .header("User-Agent",  "Mozilla/5.0")
                            .ignoreContentType(true);                            }    

public static String uploadAnnonce (Annonce annonce) throws IOException {
                            
                                 Document doc = getConnectionAdService()
                             .data("id_user", IDU)
                            .data("id_category", annonce.category)
                            .data("title", annonce.titre)
                            .data("description", annonce.texte)
                            .data("website", annonce.url)
                            .data("user_token", UT)
                            .post();
                            
                            doc.attr("id_ad");
return "";

}

    public static void main(String[] args) throws IOException {
        Document doc =
                       Jsoup.connect("http://finvalab.com/api/v1/ads")
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                            .header("User-Agent",  "Mozilla/5.0")
                            .ignoreContentType(true)
                            .data("id_user", "1")
                            .data("id_category", "41")
                            .data("title", "testTitle")
                            .data("description", "testDescription")
                            .data("user_token", "39924a52f759ee5de2b10285f8daaadf12a59d4d").post();

String myJSONString = doc.;
//JsonObject jobj = new Gson().fromJson(myJSONString, JsonObject.class);

        //System.out.println(jobj.get("id_ad"));

    }
}
