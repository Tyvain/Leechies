package ComeToLeech;

import java.io.FileNotFoundException;

import junit.framework.TestCase;
import ComeToLeech.model.Rubrique;
import ComeToLeech.model.Source;

import com.esotericsoftware.yamlbeans.YamlException;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    public static void main(String args[]) {
        AppTest test = new AppTest() {
            public void runTest() {
                testApp();
            }
        };
        test.run();
    }


    /**
     * Rigourous Test :-)
     * @throws YamlException 
     * @throws FileNotFoundException 
     */
    public void testApp() {

     App.getSourceStream().forEach(s -> displaySource(s));

    }

    public void displaySource(Source source) {
        System.out.println(source.rootUrl);
        source.rubriques.stream().forEach(r -> displayRubrique(r));
    }

    public void displayRubrique(Rubrique rub) {
        System.out.println(rub.category.libelle);
        rub.subUrls.stream().forEach(u -> System.out.println(u));
    }    
}
