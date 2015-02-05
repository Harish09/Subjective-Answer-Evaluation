/*
 * Preprocesses input answer to populate the database tables -
 * 1. allwords
 * 2. synonymset
 * 3. alldocuments
 * 4. allngrams
 *
 * Steps covered -
 * 1. spell checking (delegated to another object)
 * 2. stopword removal
 * 3. fetch wordnet synonyms
 * 4. stemming (presently bypassed)
 * 5. populate database steps 1,2,3
 * 6. populate database step 4 after 1,2,3 is done for all docs
 */

package subjectivetestevaluation;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Parag Anand Guruji
 */
public class Preprocessor {

    private static HashSet<String> stopwords;

    public static void preProcessTrainingEssays(int qno, Integer[] rnoArray){
        String[] aDocument;
        for(int i=0; i<rnoArray.length; i++){
            aDocument = preprocessADocument(qno, rnoArray[i].intValue());
            DatabaseManager.populateDatabaseWithOneDocument(aDocument);
            System.gc();
            for(int j=0; j<aDocument.length; j++){
                DatabaseManager.insertToAlldocuments(qno, rnoArray[i].intValue(), aDocument[j], j+1);
            }
        }
    }

    public static String[] preprocessADocument(int qno, int rno){

        String doc;
        String[] words = null;
        try {
            doc = correctSpellings(DatabaseManager.fetchDocumentFromAnswers(qno, rno));
            words = removeStopwords(doc);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return words;
    }

    public static String[] preprocessAQuery(int qno, int rno){

        String doc;
        String[] words = null;
        try {
            //System.out.println("Answer of Q."+qno+" By Roll No. "+rno+": \n"+DatabaseManager.fetchDocumentFromTestanswers(qno, rno)+"\n\n");
            doc = correctSpellings(DatabaseManager.fetchDocumentFromTestanswers(qno, rno));
            words = removeStopwords(doc);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return words;
    }

    private static String correctSpellings(String text) {
        return text;
    }

    public static void fetchStopwordsFromFile() {
        try{
            BufferedReader br;
            File f = new File("stopwords.txt");
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            while(br.ready()) {
                stopwords.add(br.readLine());
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String[] removeStopwords(String inputText) {

        if(stopwords==null){
            stopwords = new HashSet<>();
        }
        if(stopwords.isEmpty()) {
            fetchStopwordsFromFile();
        }

        ArrayList<String> result;
        result = new ArrayList<>();

        String[] words = inputText.split("[ \t\n,\\.\"!?$~()\\[\\]\\{\\}:;/\\\\<>+=%*]");
        for(int i=0; i < words.length; i++) {
            if(words[i] != null && !words[i].equals("")) {
                String word = words[i].toLowerCase();
                if(!stopwords.contains(word)) {
                    if(word.contains("'")){
                        StringBuilder output=new StringBuilder();
                        for(int j=0; j<word.length(); j++){
                            if(word.charAt(j)=='\''){
                                if(j==0 || word.charAt(j-1)!='\\'){
                                    output.append('\\');
                                }
                            }
                            output.append(word.charAt(j));
                        }
                        word=output.toString();
                    }
                    result.add(word);
                }
            }
        }
        return result.toArray(new String[result.size()]);
    }
}
