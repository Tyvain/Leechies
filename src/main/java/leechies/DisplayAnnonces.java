package leechies;

import java.io.IOException;
import java.util.Optional;

import leechies.model.Annonce;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisplayAnnonces {
    final static Logger logger = LoggerFactory.getLogger("DisplayAnnonces");
    
    public static void main(String[] args) throws JSONException, IOException {
       
        System.out.println("-- LOCAL DB");
        System.out.println("Nombre d'annonces: " + DBManager.getAllAnnonces().count());
        System.out.println("  - avec images: " + DBManager.getAnnoncesByCriteria(null, null, null, true).count());
        System.out.println("  - sans images: " + DBManager.getAnnoncesByCriteria(null, null, null, false).count());
        System.out.println("  - uploaded: " + DBManager.getAnnoncesByCriteria(null, true, null, null).count());
        System.out.println("  - non uploaded: " + DBManager.getAnnoncesByCriteria(null, false, null, null).count());
        System.out.println("  - commerciales: " + DBManager.getAnnoncesByCriteria(null, null, true, null).count());
        System.out.println("  - non commerciales: " + DBManager.getAnnoncesByCriteria(null, null, false, null).count());
        System.out.println("  - avec erreurs: " + DBManager.getAnnoncesByCriteria(true, null, null, null).count());
        System.out.println("  - sans erreurs: " + DBManager.getAnnoncesByCriteria(false, null, null, null).count());
        System.out.println("     - (à uploaded) non commerciales avec images non uploaded sans erreur: "
                           + DBManager.getAnnoncesByCriteria(false, false, false, true).count());

        System.out.println("-- FINVALAB");
        // System.out.println("Nb annonces : " + UploadManager.countAnnonces());   
        Optional<Annonce> annonce = DBManager.getAnnoncesByUrl("https://automobiles.nc/a_an.php?g=detail_annonce&id=2429339");
        String rep =  "Annonce non trouvée";
        if (annonce.isPresent()) {
            rep = ""+annonce.get();
        }
        System.out.println("-- Detail annonce : " + rep);
        // DBManager.getAnnoncesByCriteria(null, true, null, null).forEach(item -> System.out.println(""+item));
    }
}
