package leechies;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import com.esotericsoftware.yamlbeans.YamlReader;

import leechies.model.Annonce;
import leechies.model.Category;
import leechies.model.Source;
import leechies.sites.AbstractSite;

public class App {
	// public static String SOURCE = "sources-nautisme.yml";
	 //public static String SOURCE = "sources-vehicules.yml";
	// public static String SOURCE = "sources-all.yml";
	// public static String SOURCE = "sources-mode.yml";
	public static String SOURCES[] = {"sources-immonc.yml", "sources-mode.yml", "sources-vehicules.yml", "sources-vehicules.yml"};
	//public static String SOURCES[] = {"sources-vehicules.yml"};

	public static boolean MODE_HORS_LIGNE = false;
	public static int NB_LIMITE_ANNONCE = 10000;

	public static void main(String[] args) throws IOException {
		System.out.println("Start...");

		System.out.println("- create file");
		String csvFile = "importAds.csv";
		FileWriter writer = new FileWriter(csvFile);
		CSVUtils.writeFirstLine(writer);

		System.out.println("- get ads");
		App.getSourceStream().flatMap(s -> {
			System.out.println("s: " + s); 
		return MODE_HORS_LIGNE ? getAnnonceFromSourceStub(s) : getAnnonceFromSource(s);
		}) // récupération des annonces
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
				.flatMap(r -> r.subUrls.stream().flatMap(u -> App.getAnnonce(source, u, r.category.libelle)));
	}

	public static Stream<Annonce> getAnnonceFromSourceStub(Source source) {

		Annonce[] array = { createAnnonceStub("+COM +IMG", "1", true, true),
				createAnnonceStub("+COM -IMG", "2", true, false), createAnnonceStub("-COM -IMG", "3", false, false),
				createAnnonceStub("-COM +IMG", "4", false, true),
				createAnnonceStub("-COM +IMG", "doublon", false, true),
				createAnnonceStub("-COM +IMG", "doublon", false, true), };

		return Arrays.stream(array);
	}

	private static Annonce createAnnonceStub(String titre, String texte, boolean commerciale, boolean withImg) {
		Annonce annonce = new Annonce();
		annonce.category = Category.MOTOS.libelle;
		annonce.isCommerciale = commerciale;
		annonce.url = "";
		annonce.titre = titre;
		annonce.texte = texte;
		annonce.imgs = withImg ? new String[] { "img1", "img2" } : null;
		return annonce;
	}

	public static Stream<Annonce> getAnnonce(Source source, String url, String rub) {
		Class<?> clazz;
		try {
			clazz = Class.forName(source.className);
			AbstractSite site = (AbstractSite) clazz.newInstance();
			return site.getAnnonces(source.rootUrl, url, rub);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {		
			System.err.println("Zuuut !! " + e);
		}
		return null;
	}

	public static Stream<Source> getSourceStream() {
		Stream<Source> ret = Stream.empty();
		try {
			for (String source : SOURCES) {
				YamlReader reader = new YamlReader(new FileReader(source));
				@SuppressWarnings("unchecked")
				ArrayList<Source> wtf = (ArrayList<Source>) reader.read();
				ret = Stream.concat(ret, wtf.stream());
			}
		} catch (Exception e) {
			System.err.println("Ooops!! " + e);
		}
		return ret;
	}
}
