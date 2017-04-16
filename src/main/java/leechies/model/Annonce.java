package leechies.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Annonce implements Serializable {
	private static final long serialVersionUID = 1L;

	public String[] imgs;
	public String url;
	public String category;
	public String titre;
	public String texte;
	public String prix;
	public boolean isCommerciale;
	public Date uploadedTime; // date d'envoi sur finvalab

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
