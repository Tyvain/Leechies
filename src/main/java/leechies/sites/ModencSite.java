package leechies.sites;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ModencSite extends GenericSite {
	
	 @Override
     protected String[] getImagesFromDoc(Document doc, String rootUrl) {
	        Elements els = doc.select(getImageSelector());
	        Stream<String> imgz = els.stream().map(e -> {            
	            String href = e.attr("href");            
	            String img = StringUtils.substringBetween(href, "big&src=", "&title");
	            System.out.println("img: " + img);
	            return img!=null?img:"";
	            });        
	        String[] stringArray = imgz.toArray(size -> new String[size]);
	        return stringArray;
    }
}
