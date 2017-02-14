package ComeToLeech.model;

import java.util.Objects;

public class Annonce {
    public String[] imgs;
    public String url;
    public String category;
    public String titre;
    public String texte;   
    public String prix;
    public boolean isCommerciale;   
    
    @Override
    public boolean equals(Object o) {
        Annonce a = (Annonce)o;
        return texte !=null && a.texte!=null && texte.equalsIgnoreCase(a.texte);       
    }

    @Override
    public int hashCode() {
        return Objects.hash(texte);
    }    
}
