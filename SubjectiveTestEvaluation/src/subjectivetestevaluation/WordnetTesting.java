/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subjectivetestevaluation;

import edu.smu.tspell.wordnet.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import matrix.Matrix;


/**
 *
 * @author Parag Anand Guruji
 */
public class WordnetTesting {

    public static void main(String[] args){
        System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\2.1\\dict\\");
        System.out.println("Properties: "+System.getProperty("wordnet.database.dir"));

        /*String get = "node";
        NounSynset nounSynset;
        NounSynset[] hyponyms;

        WordNetDatabase database = WordNetDatabase.getFileInstance();

        Synset[] synsets = database.getSynsets(get, SynsetType.NOUN);
        for(int i=0; i<synsets.length; i++){
            nounSynset = (NounSynset)(synsets[i]);
            hyponyms = nounSynset.getHyponyms();
            System.err.println(nounSynset.getWordForms()[0] + ": " + nounSynset.getDefinition() + ") has "+ hyponyms.length + " hyponyms");
        }
        */
        String get="image";

        String[] synsetWordforms;
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(get);

        WordNetDatabase database = WordNetDatabase.getFileInstance();

        Synset[] synsets = database.getSynsets(get);
        for(int i=0; i<synsets.length; i++){
//            if(synsets[i].getDefinition().contains("science") || synsets[i].getDefinition().contains("computer") || synsets[i].getDefinition().contains("engineering")){
                synsetWordforms = synsets[i].getWordForms();
                for(int j=0; j<synsetWordforms.length; j++){
                    if( ! retVal.contains(synsetWordforms[j]) ){
                        retVal.add(synsetWordforms[j]);
                        System.out.println("Synonym of "+get+" = "+synsetWordforms[j]);
                    }
                }
//            }
        }


    }
}
