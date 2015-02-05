/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package subjectiveevaluationtest;

/**
 *
 * @author Mahesh
 */

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
