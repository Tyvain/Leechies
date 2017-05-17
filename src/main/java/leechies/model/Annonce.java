package leechies.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Annonce implements Serializable {
	private static final long serialVersionUID = 1L;

	public Date created=new Date();// date de création (récupération de l'annonce)
	public String url; // url de l'annonce (clef unique)
	public String[] imgs;
	public String category;
	public String titre;
	public String texte;
	public String prix;
	public boolean isCommerciale;
	public Date uploadedTime=null; // date d'envoi sur finvalab
	public boolean hasError=false; // annonce en erreur
	public String error="";

	
	@Override
	public boolean equals(Object o) {
		Annonce a = (Annonce) o;
		return texte != null && a.texte != null && texte.equalsIgnoreCase(a.texte);
	}

	@Override
	public int hashCode() {
		return Objects.hash(texte);
	}

	@Override
	public String toString() {
		return url + " # " + displayImages ();
	}
	
	public String displayImages () {
		String rez = "";
		for (String i : imgs) {
			rez += i + " # ";
		}
		return rez;
	}
}
