package ComeToLeech;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ComeToLeech.model.Annonce;

public class CSVUtils {

    private static final char DEFAULT_SEPARATOR = ',';
    private static int cpt = 0;

    private static String getImageByIndex(Annonce annonce, int index) {
        if (annonce.imgs == null) {
            return "";
            }
        return annonce.imgs.length > index ? annonce.imgs[index].replaceAll(" ", "%20") : "";
    }

    public static void closeFile(Writer w){
        try {
            w.flush();
             w.close();
        } catch (IOException e) {
            System.err.println("Ooops close file: " +e);
        }
       
    }

    public static void writeAnnonce(Writer w, Annonce annonce) {
        if (App.NB_LIMITE_ANNONCE != 0 && cpt++>=App.NB_LIMITE_ANNONCE) {
            closeFile(w);
            System.exit(0);
            }
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);        
        
       // String lien = "<br><a href='"+annonce.url+"'>annonce d'origine</a>";

        String img1 = getImageByIndex(annonce, 0);
        String img2 = getImageByIndex(annonce, 1);
        String img3 = getImageByIndex(annonce, 2);
        String img4 = getImageByIndex(annonce, 3);

        System.out.println("Prix: " + annonce.prix);
        try {
            String texteTrunc = annonce.texte.substring(0, Math.min(annonce.texte.length(), 5000));
            writeLine(w, Arrays.asList("import-annonces.nc", "import@annonces.nc", annonce.titre, texteTrunc, reportDate,
                                       annonce.category, "", annonce.prix, "", "", annonce.url, img1, img2, img3, img4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }

    //https://tools.ietf.org/html/rfc4180
    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }
}
