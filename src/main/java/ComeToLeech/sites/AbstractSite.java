package ComeToLeech.sites;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import ComeToLeech.model.Annonce;

public abstract class AbstractSite {

    protected abstract String getTitreSelector();

    protected abstract String getTexteSelector();

    protected abstract String getLinkSelector();

    protected abstract String getImageSelector();

    protected abstract String getPrixFromDoc (Document doc);
    
    public Stream<Annonce> getAnnonces(String rootUrl, String rubUrl, String rub) {
        // liste des docs (cas des pages contenant les liens)  
        System.out.println("       from " + rootUrl + rubUrl + " into " + rub + "...");
        Document doc = getDocumentFromUrl(rootUrl + rubUrl);

        // liste des elements (cad liens des annonces)
        Elements elemz = doc.select(getLinkSelector());

        // listes des ids des annones mettre map pour tout
        Stream<String> idz = elemz.stream().map(e -> e.attr("href"));

        // liste des urls
        Stream<String> urlz = idz.map(s -> buildUrl(rootUrl, s));

        // liste des annonces        
        Stream<Annonce> ret = urlz.map(u -> getAnnonceFromUrl(u, rootUrl, rub));
        return ret;
    }
    
	protected String buildUrl(String rootUrl, String s) {
		return rootUrl + s;
	}

    protected Annonce getAnnonceFromUrl(String url, String rootUrl, String rub) {
        Document doc = getDocumentFromUrl(url);
        return getAnnonce(doc, url, rootUrl, rub);
    }

    protected Annonce getAnnonce(Document doc, String url, String rootUrl, String rub) {
        Annonce ret = new Annonce();
        System.out.println("url: " + url);
        ret.titre = doc.select(getTitreSelector()).first().text();
        ret.texte = doc.select(getTexteSelector()).first().html();
        ret.category = rub;
        ret.url = url;
        ret.imgs = getImagesFromDoc(doc, rootUrl);
        ret.isCommerciale = ret.texte.contains("Annonce Commerciale");
        ret.prix = getPrixFromDoc(doc);
        return ret;
    }

    protected static Document getDocumentFromUrl(String url) {
        try {
            // random pause
            long pause = (long)(Math.random() * 200);
            //System.out.print("       pausing " + pause / 1000 + " s...");
            Thread.sleep(pause);           
            
           return Jsoup.connect(url).timeout(10000).validateTLSCertificates(false).get();
            
           // return Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
        } catch (Exception e) {
            System.err.println("getDocumentFromUrl err: " + e);
        }
        return null;
    }

    protected String[] getImagesFromDoc(Document doc, String rootUrl) {
        Elements els = doc.select(getImageSelector());
        Stream<String> imgz = els.stream().map(e -> {            
            String href = e.attr("href");            
            String img = StringUtils.substringBetween(href, "big&src=", "&title");
            String rez = buildUrl(rootUrl, img);
           /* System.out.println("rootUrl: " + rootUrl);
            System.out.println("img: " + img);
            System.out.println("rez: " + rez);*/
            return rez;
            });        
        String[] stringArray = imgz.toArray(size -> new String[size]);
        return stringArray;
    }
}
