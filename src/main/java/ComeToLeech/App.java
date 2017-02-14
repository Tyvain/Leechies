package ComeToLeech;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import com.esotericsoftware.yamlbeans.YamlReader;

import ComeToLeech.model.Annonce;
import ComeToLeech.model.Category;
import ComeToLeech.model.Source;
import ComeToLeech.sites.AbstractSite;
import ComeToLeech.sites.ImmoncSite;

public class App {
	//public static String SOURCE = "sources-nautisme.yml";
    //public static String SOURCE = "sources-vehicules.yml";
	//public static String SOURCE = "sources-all.yml";
	//public static String SOURCE = "sources-mode.yml";
	public static String SOURCE = "sources-immonc.yml";
    
    public static boolean MODE_HORS_LIGNE = false;
    public static int NB_LIMITE_ANNONCE = 10000;
    
    private static AbstractSite site = new ImmoncSite();
    
    public static void main(String[] args) throws IOException {
        System.out.println("Start...");

        System.out.println("- create file");
        String csvFile = "importAds.csv";
        FileWriter writer = new FileWriter(csvFile);
        CSVUtils.writeLine(writer, Arrays.asList("user_name", "user_email", "title", "description", "date", "category", "location",
                                                 "price", "address", "phone", "website", "image_1", "image_2", "image_3", "image_4"));

        System.out.println("- get ads");
        App.getSourceStream().flatMap(s -> MODE_HORS_LIGNE?getAnnonceFromSourceStub(s):getAnnonceFromSource(s)) // récupération des annonces
           .filter(f -> !f.isCommerciale) // anonces non commerciales
           .filter(f -> f.imgs != null && f.imgs.length > 0) // annonces avec images
           .distinct() // suppression des doublons
           .map(a -> new AnnonceCleaner().cleanAnnonce(a)) // nettoyage
           .forEach(a -> CSVUtils.writeAnnonce(writer, a)); // ecriture dans le fichier

        System.out.println("close file");
        CSVUtils.closeFile(writer);        
        System.out.println("...finished!");

    }

    public static Stream<Annonce> getAnnonceFromSource(Source source) {
        return source.rubriques.stream()
        .flatMap(r -> r.subUrls.stream()
                      .flatMap(u -> App.getAnnonce(source.rootUrl, u, r.category.libelle)));
    }

    public static Stream<Annonce> getAnnonceFromSourceStub(Source source) {
 
        Annonce[] array = {
            createAnnonceStub("+COM +IMG", "1", true, true),
            createAnnonceStub("+COM -IMG", "2", true, false),
            createAnnonceStub("-COM -IMG", "3", false, false),
            createAnnonceStub("-COM +IMG", "4",false, true),
            createAnnonceStub("-COM +IMG", "doublon",false, true),
            createAnnonceStub("-COM +IMG", "doublon",false, true),
            };

        return Arrays.stream(array);
    }

    private static Annonce createAnnonceStub (String titre, String texte, boolean commerciale, boolean withImg) {
        Annonce annonce = new Annonce();
        annonce.category = Category.MOTOS.libelle;
        annonce.isCommerciale = commerciale;
        annonce.url = "";
        annonce.titre = titre;
        annonce.texte = texte;
        annonce.imgs = withImg?new String[]{"img1", "img2"}:null;
        return annonce;
    }

    public static Stream<Annonce> getAnnonce(String rootUrl, String url, String rub) {
        return site.getAnnonces(rootUrl, url, rub);
    }

    public static Stream<Source> getSourceStream() {
        Stream<Source> ret = null;
        try {
            YamlReader reader = new YamlReader(new FileReader(SOURCE));
            @SuppressWarnings("unchecked")
            ArrayList<Source> contact = (ArrayList<Source>)reader.read();
            ret = contact.stream();
        } catch (Exception e) {
            System.err.println("Ooops!! " + e);
        }
        return ret;
    }
}
