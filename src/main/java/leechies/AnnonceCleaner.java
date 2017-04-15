package leechies;

import org.apache.commons.lang3.StringUtils;

import leechies.model.Annonce;

public class AnnonceCleaner {
    
    public Annonce cleanAnnonce (Annonce a){
        a.titre = cleanAll(a.titre);
        a.texte = cleanAll(a.texte);
        a.prix = cleanPrix(a.prix);
        return a;
    }
        
    private String cleanPrix(String s) {    	
		// 11 900 000 F
    	 s = s.replaceAll(" ","");
         s = s.replace("F","");
         s = s.replaceAll("cfp.*", "");
         
        return StringUtils.isNumeric(s)?s:"";
	}

	private String cleanAll (String s) {
        s = s.replaceAll("[\r\n]+", "\n");
        s = s.replaceAll("\\s+", " ");
        s = s.replaceAll("<br> <br>", "<br>");
        s = s.replaceAll("<br> <br>", "<br>");
        s = s.replaceAll("<i>\\w\\w-.* </i>","");
        s = s.replace(",","");
        s = s.replace("\"","");
       return s;
     }  
}
