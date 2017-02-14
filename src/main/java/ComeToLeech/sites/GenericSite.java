package ComeToLeech.sites;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;

public class GenericSite extends AbstractSite {

	@Override
	protected String getImageSelector() {
		return "#gallery > ul > li > a";
	}

	@Override
	protected String getLinkSelector() {
		return "#div_centre_0 > span > table";
	}

	@Override
	protected String getTitreSelector() {
		return "#div_centre_0 > table > tbody > tr > td:nth-child(2) > table > tbody > tr:nth-child(1) > td > table > tbody > tr > td:nth-child(3) > b";
	}

	@Override
	protected String getTexteSelector() {
		return "#div_centre_0 > table > tbody > tr > td:nth-child(2) > table > tbody > tr:nth-child(3) > td > table > tbody > tr > td.andmm > div";
	}

	@Override
	protected String getPrixFromDoc(Document doc) {
		Pattern regex = Pattern.compile(".*:(.*) cfp");
		Matcher m = regex.matcher(doc.text());
		if (m.find()) {
			return m.group(1);
		}
		
		return "";
	}

}
