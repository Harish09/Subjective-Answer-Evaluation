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
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import matrix.Matrix;

/**
 *
 * @author Parag Anand Guruji
 */
public class DatabaseManager {

    private static String driverName = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost:3306/";
    private static String dbName = "testdb";
    private static String userName = "root";
    private static String password = "root";
    private static Connection connection = null;
    private static int numberOfNgrams = 0;
private static PreparedStatement psInserToAllWords1 = null;
    private static PreparedStatement psGetWordId1 = null;
    private static PreparedStatement psGetMaxWordId1 = null;
    private static PreparedStatement psInsertToSynonymsetAndUpdateAllwords1 = null;
    private static PreparedStatement psInsertATupleToSynonymset1 = null;
    private static PreparedStatement psGetSetIdFromAllwords1 = null;
    private static PreparedStatement psGetMaxSetId1 = null;
    private static PreparedStatement psInsertToAlldocuments1 = null;
    private static PreparedStatement psInsertToAlldocuments2 = null;
    private static PreparedStatement psGetMaxNgramidFromAllngrams1 = null;
    private static PreparedStatement psPopulateAllngrams1 = null;
    private static PreparedStatement psPopulateAllngrams2 = null;
    private static PreparedStatement psPopulateAllngrams3 = null;
    private static PreparedStatement psPopulateAllngrams4 = null;
    private static PreparedStatement psFetchDocumentFromAnswers1 = null;
    private static PreparedStatement psFetchDocumentFromTestanswers1 = null;
    private static PreparedStatement psGetRollNumbersWhoSubmittedAnswer1 = null;
    private static PreparedStatement psGetRollNumbersToBeEvaluated1 = null;
    private static PreparedStatement psGetGlobalNgramCount1 = null;
    private static PreparedStatement psPopulateNgramByDocMatrix1 = null;
    private static PreparedStatement psPopulateNgramByDocMatrix2 = null;
    private static PreparedStatement psPopulateNgramByDocMatrix3 = null;
    private static PreparedStatement psPopulateNgramByDocMatrix4 = null;
    private static PreparedStatement psPopulateNgramByDocMatrix5 = null;
    private static PreparedStatement psPopulateNgramByDocMatrix6 = null;
    private static PreparedStatement psPopulateNgramByDocMatrix7 = null;
    private static PreparedStatement psPopulateNgramByDocMatrix8 = null;
    private static PreparedStatement psGetWordIdsOfQuery1 = null;
    private static PreparedStatement psPopulatengramByQueryMatrix1 = null;
    private static PreparedStatement psPopulatengramByQueryMatrix2 = null;
    private static PreparedStatement psPopulatengramByQueryMatrix3 = null;
    private static PreparedStatement psPopulatengramByQueryMatrix4 = null;
    private static PreparedStatement psPopulatengramByQueryMatrix5 = null;
    private static PreparedStatement psPopulatengramByQueryMatrix6 = null;
    private static PreparedStatement psGetOccuranceCount1 = null;
    private static PreparedStatement psPopulateMatrix1 = null;
    private static PreparedStatement psPopulateMatrix2 = null;
    private static PreparedStatement psPopulateMatrix3 = null;
    private static PreparedStatement psGetMatrixdjdashFromDatabase1 = null;
    private static PreparedStatement psGetMatrixdjdashFromDatabase2 = null;
    private static PreparedStatement psGetMatrixdjdashFromDatabase3 = null;
    private static PreparedStatement psGetMatrixsFromDatabase1 = null;
    private static PreparedStatement psGetMatrixsFromDatabase2 = null;
    private static PreparedStatement psGetMatrixsFromDatabase3 = null;
    private static PreparedStatement psGetMatrixuFromDatabase1 = null;
    private static PreparedStatement psGetMatrixuFromDatabase2 = null;
    private static PreparedStatement psGetMatrixuFromDatabase3 = null;
    private static PreparedStatement psInsertGradeToTestAnswers1 = null;

    public static void configure(){
        driverName = "com.mysql.jdbc.Driver";
        url = "jdbc:mysql://localhost:3306/";
        dbName = "testdb";
        userName = "root";
        password = "root";
    }

    public static Connection getConnectionObject(){
        while(connection==null) {
            try
            {
                Class.forName(driverName);
                connection = DriverManager.getConnection(url+dbName,userName,password);
            }
            catch(ClassNotFoundException ex)
            {
                JOptionPane.showMessageDialog(null, "CONNECTION NOT ESTABLISHED!!\n"+ex);
            }catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "CONNECTION NOT ESTABLISHED!!\n"+ex);
            }
        }
        try {
            connection.setAutoCommit(true);
            connection.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return connection;
    }

    private static String subjectDomain = "(computer science)";
    private static String[] fetchWordnetSynonyms(String get) {

        String[] synsetWordforms;
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(get);

        WordNetDatabase database = WordNetDatabase.getFileInstance();

        Synset[] synsets = database.getSynsets(get);
        for(int i=0; i<synsets.length; i++){
            if(synsets[i].getDefinition().startsWith(subjectDomain)){
                synsetWordforms = synsets[i].getWordForms();
                for(int j=0; j<synsetWordforms.length; j++){
                    if( ! retVal.contains(synsetWordforms[j]) ){
                        retVal.add(synsetWordforms[j]);
                        ////System.out.println("Synonym of "+get+" = "+synsetWordforms[j]);
                    }
                }
            }

        }
        return retVal.toArray(new String[retVal.size()]);
    }

    public static void populateDatabaseWithOneDocument(String[] words){

        String[] synonyms;
        int[] wordIdSet;

        for(int i=0; i<words.length; i++){
            synonyms = fetchWordnetSynonyms(words[i]);
            wordIdSet = new int[synonyms.length];

            for(int j=0; j<synonyms.length; j++){
                wordIdSet[j] = insertToAllwords(synonyms[j]);
            }
            insertToSynonymsetAndUpdateAllwords(wordIdSet);
        }
    }

    static{
        try{
            psInserToAllWords1                      =   getConnectionObject().prepareStatement("INSERT INTO allwords(wordid, word) VALUES (?, ?);");
            psGetWordId1                            =   getConnectionObject().prepareStatement("SELECT wordid FROM allwords WHERE word = ? ;");
            psGetMaxWordId1                         =   getConnectionObject().prepareStatement("SELECT MAX(wordid) FROM allwords;");
            psInsertToSynonymsetAndUpdateAllwords1  =   getConnectionObject().prepareStatement("UPDATE allwords set setid=? WHERE wordid=? ;");
            psInsertATupleToSynonymset1             =   getConnectionObject().prepareStatement("INSERT INTO Synonymset (setid, wordid) VALUES (?, ?);");
            psGetSetIdFromAllwords1                 =   getConnectionObject().prepareStatement("SELECT setid FROM allwords WHERE wordid = ? ;");
            psGetMaxSetId1                          =   getConnectionObject().prepareStatement("SELECT MAX(setid) FROM synonymset;");
            psInsertToAlldocuments1                 =   getConnectionObject().prepareStatement("INSERT INTO alldocuments(docid, docwordseq, wordid) VALUES(?, ?, ?);");
            psInsertToAlldocuments2                 =   getConnectionObject().prepareStatement("UPDATE answers SET docid =? WHERE qno=? AND rno=?;");
            psGetMaxNgramidFromAllngrams1           =   getConnectionObject().prepareStatement("SELECT MAX(ngramid) FROM allngrams;");
            psPopulateAllngrams1                    =   getConnectionObject().prepareStatement("SELECT DISTINCT docid FROM alldocuments;");
            psPopulateAllngrams2                    =   getConnectionObject().prepareStatement("SELECT A.wordid, allwords.setid FROM allwords, (SELECT docwordseq, wordid FROM alldocuments WHERE docid = ?) as A WHERE allwords.wordid=A.wordid ORDER BY A.docwordseq");
            psPopulateAllngrams3                    =   getConnectionObject().prepareStatement("SELECT ngramid FROM allngrams WHERE setids = ?;");
            psPopulateAllngrams4                    =   getConnectionObject().prepareStatement("INSERT INTO allngrams (ngramid, setids) VALUES(?, ?)");
            psFetchDocumentFromAnswers1             =   getConnectionObject().prepareStatement("SELECT answer FROM answers WHERE qno =? AND rno = ?;");
            psFetchDocumentFromTestanswers1         =   getConnectionObject().prepareStatement("SELECT answer FROM testanswers WHERE qno = ? AND rno = ? ;");
            psGetRollNumbersWhoSubmittedAnswer1     =   getConnectionObject().prepareStatement("SELECT rno FROM answers WHERE qno=? AND answer IS NOT NULL AND docid<=?;");
            psGetRollNumbersToBeEvaluated1          =   getConnectionObject().prepareStatement("SELECT rno FROM testanswers WHERE qno=? AND answer IS NOT NULL;");
            psGetGlobalNgramCount1                  =   getConnectionObject().prepareStatement("SELECT COUNT(*) FROM allngrams;");
            psPopulateNgramByDocMatrix1             =   getConnectionObject().prepareStatement("SELECT ngramid, setids FROM allngrams");
            psPopulateNgramByDocMatrix2             =   getConnectionObject().prepareStatement("SELECT DISTINCT docid FROM alldocuments");
            psPopulateNgramByDocMatrix3             =   getConnectionObject().prepareStatement("SELECT wordid FROM alldocuments WHERE docid = ? ORDER BY docwordseq;");
            psPopulateNgramByDocMatrix4             =   getConnectionObject().prepareStatement("INSERT INTO ngrambydocmatrix (ngramid, docid, frequency) VALUES(?, ?, ?);");
            psPopulateNgramByDocMatrix5             =   getConnectionObject().prepareStatement("SELECT MAX(A.frequency) FROM (SELECT frequency FROM ngrambydocmatrix WHERE docid=?) as A;");
            psPopulateNgramByDocMatrix6             =   getConnectionObject().prepareStatement("UPDATE ngrambydocmatrix SET frequency = frequency/? WHERE docid=?;");
            psPopulateNgramByDocMatrix7             =   getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT docid) FROM ngrambydocmatrix WHERE ngramid=? AND frequency > 0.0;");
            psPopulateNgramByDocMatrix8             =   getConnectionObject().prepareStatement("UPDATE ngrambydocmatrix SET tfidf = frequency * ? WHERE ngramid=?;");
            psGetWordIdsOfQuery1                    =   getConnectionObject().prepareStatement("SELECT wordid FROM allwords WHERE word = ?");
            psPopulatengramByQueryMatrix1           =   getConnectionObject().prepareStatement("SELECT ngramid, setids FROM allngrams");
            psPopulatengramByQueryMatrix2           =   getConnectionObject().prepareStatement("INSERT INTO ngrambyquerymatrix (ngramid, queryid, frequency) VALUES(?, ?, ?);");
            psPopulatengramByQueryMatrix3           =   getConnectionObject().prepareStatement("SELECT MAX(A.frequency) FROM (SELECT frequency FROM ngrambyquerymatrix WHERE queryid=?) AS A;");
            psPopulatengramByQueryMatrix4           =   getConnectionObject().prepareStatement("UPDATE ngrambyquerymatrix SET frequency = frequency/? WHERE queryid=?;");
            psPopulatengramByQueryMatrix5           =   getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT docid) FROM ngrambydocmatrix WHERE ngramid=? AND frequency > 0.0;");
            psPopulatengramByQueryMatrix6           =   getConnectionObject().prepareStatement("UPDATE ngrambyquerymatrix SET tfidf = frequency*? WHERE ngramid=? AND queryid=?;");
            psGetOccuranceCount1                    =   getConnectionObject().prepareStatement("SELECT wordid FROM synonymset WHERE setid = ?;");
            psPopulateMatrix1                       =   getConnectionObject().prepareStatement("INSERT INTO matrixdjdash (rowid, columnid, cellvalue) VALUES(?, ?, ?)");
            psPopulateMatrix2                       =   getConnectionObject().prepareStatement("INSERT INTO matrixs (rowid, columnid, cellvalue) VALUES(?, ?, ?)");
            psPopulateMatrix3                       =   getConnectionObject().prepareStatement("INSERT INTO matrixu (rowid, columnid, cellvalue) VALUES(?, ?, ?)");
            psGetMatrixdjdashFromDatabase1          =   getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT rowid) FROM matrixdjdash;");
            psGetMatrixdjdashFromDatabase2          =   getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT columnid) FROM matrixdjdash;");
            psGetMatrixdjdashFromDatabase3          =   getConnectionObject().prepareStatement("SELECT cellvalue FROM matrixdjdash;");
            psGetMatrixsFromDatabase1               =   getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT rowid) FROM matrixs;");
            psGetMatrixsFromDatabase2               =   getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT columnid) FROM matrixs;");
            psGetMatrixsFromDatabase3               =   getConnectionObject().prepareStatement("SELECT cellvalue FROM matrixs;");
            psGetMatrixuFromDatabase1               =   getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT rowid) FROM matrixu;");
            psGetMatrixuFromDatabase2               =   getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT columnid) FROM matrixu;");
            psGetMatrixuFromDatabase3               =   getConnectionObject().prepareStatement("SELECT cellvalue FROM matrixu;");
            psInsertGradeToTestAnswers1             =   getConnectionObject().prepareStatement("UPDATE testanswers SET grade=? WHERE qno=? AND rno=?;");
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    private static int insertToAllwords(String word) {

//        PreparedStatement ps;
        int wordId = getWordId(word);

        try {
            if(wordId == -1){
                wordId = getMaxWordId()+1;


                if(word.contains("'")){
                    StringBuilder output=new StringBuilder();
                    for(int i=0; i<word.length(); i++){
                        if(word.charAt(i)=='\''){
                            if(i==0 || word.charAt(i-1)!='\\'){
                                output.append('\\');
                            }
                        }
                        output.append(word.charAt(i));
                    }
                    word=output.toString();
                }

                psInserToAllWords1.setString(1, ""+wordId);
                psInserToAllWords1.setString(2, word);
                psInserToAllWords1.executeUpdate();
//                ps = getConnectionObject().prepareStatement("INSERT INTO allwords(wordid, word) VALUES ("+wordId+", '"+word+"');");
//                ps.executeUpdate();

            }
        } catch (SQLException ex) {
            //ex.printStackTrace();
        }
        return wordId;
    }

    private static int getWordId(String word){
//        PreparedStatement ps = null;
        ResultSet rs = null;
        int wordId = -1;

        if(word.contains("'")){
            StringBuilder output=new StringBuilder();
            for(int i=0; i<word.length(); i++){
                if(word.charAt(i)=='\''){
                    if(i==0 || word.charAt(i-1)!='\\'){
                        output.append('\\');
                    }
                }
                output.append(word.charAt(i));
            }
            word=output.toString();
        }

        try {
//          ps = getConnectionObject().prepareStatement("SELECT wordid FROM allwords WHERE word = '"+word+"';");
//          rs = ps.executeQuery();
            psGetWordId1.setString(1, word);
            rs = psGetWordId1.executeQuery();
            if(rs.next()){
                wordId = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException ex) {
            //System.out.println("word:"+word);
            ex.printStackTrace();
        }
        return wordId;
    }

    private static int getMaxWordId() {
        int wordId=0;
//      PreparedStatement ps = null;
        ResultSet rs = null;
        try {
//          ps = getConnectionObject().prepareStatement("SELECT MAX(wordid) FROM allwords;");
//          rs = ps.executeQuery();
            rs = psGetMaxWordId1.executeQuery();

            if(rs.next()){
                wordId = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return wordId;
    }

    private static void insertToSynonymsetAndUpdateAllwords(int[] wordIdSet) {
        if(wordIdSet.length>=1){
            int keyWordId = wordIdSet[0];
            int targetSetId =getSetIdFromAllwords(keyWordId);
            if(targetSetId==0){
                targetSetId = getMaxSetId()+1;
                for(int i=0; i<wordIdSet.length; i++){
                    insertATupleToSynonymset(targetSetId, wordIdSet[i]);
                }
                try{
//                    getConnectionObject().prepareStatement("UPDATE allwords set setid="+targetSetId+" WHERE wordid="+keyWordId+";").executeUpdate();
                    psInsertToSynonymsetAndUpdateAllwords1.setInt(1, targetSetId);
                    psInsertToSynonymsetAndUpdateAllwords1.setInt(2, keyWordId);
                    psInsertToSynonymsetAndUpdateAllwords1.executeUpdate();
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
            }
        }
        ////System.out.println("wordIdSet length: "+wordIdSet.length);
    }

    private static void insertATupleToSynonymset(int targetSetId, int targetWordId) {

        try{
//            getConnectionObject().prepareStatement("INSERT INTO Synonymset (setid, wordid) VALUES ("+targetSetId+", "+targetWordId+");").executeUpdate();
            psInsertATupleToSynonymset1.setInt(1, targetSetId);
            psInsertATupleToSynonymset1.setInt(2, targetWordId);
            psInsertATupleToSynonymset1.executeUpdate();
        }catch(SQLException ex){
            //ex.printStackTrace();
        }
    }

    private static int getSetIdFromAllwords(int keyWordId){
        int setId = 0;
        try{
            psGetSetIdFromAllwords1.setInt(1, keyWordId);
            ResultSet rs = psGetSetIdFromAllwords1.executeQuery();
//            ResultSet rs = getConnectionObject().prepareStatement("SELECT setid FROM allwords WHERE wordid ="+keyWordId+";").executeQuery();
            if(rs.next()){
                setId = rs.getInt(1);
            }
            rs.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return setId;
    }

    private static int getMaxSetId(){
//        PreparedStatement ps = null;
        ResultSet rs = null;
        int setId = 0;
        try {
//            ps = getConnectionObject().prepareStatement("SELECT MAX(setid) FROM synonymset;");
//            rs = ps.executeQuery();
            rs = psGetMaxSetId1.executeQuery();
            if(rs.next()){
                setId = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return setId;
    }

    public static void insertToAlldocuments(int qno, int rno, String word, int docWordSeq) {

        int wordId = getWordId(word), docId = ((qno-1)*Controller.maxRollNoValue)+rno;
        try {
//            getConnectionObject().prepareStatement("INSERT INTO alldocuments(docid, docwordseq, wordid) VALUES("+docId+", "+docWordSeq+", "+wordId +");").executeUpdate();
//            getConnectionObject().prepareStatement("UPDATE answers SET docid ="+docId+" WHERE qno="+qno+" AND rno="+rno+";").executeUpdate();

            psInsertToAlldocuments1.setInt(1, docId);
            psInsertToAlldocuments1.setInt(2, docWordSeq);
            psInsertToAlldocuments1.setInt(3, wordId);
            psInsertToAlldocuments1.executeUpdate();

            psInsertToAlldocuments2.setInt(1, docId);
            psInsertToAlldocuments2.setInt(2, qno);
            psInsertToAlldocuments2.setInt(3, rno);
            psInsertToAlldocuments2.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
//            System.err.println("\n\nError Word: "+word+"\nERROR WORDID: "+wordId+"\n\n");
        }
    }

    private static int getMaxNgramidFromAllngrams() {
        ResultSet rs = null;
        int ngramId = 0;
        try {
//            rs = getConnectionObject().prepareStatement("SELECT MAX(ngramid) FROM allngrams;").executeQuery();
            rs = psGetMaxNgramidFromAllngrams1.executeQuery();
            if(rs.next()){
                ngramId = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ngramId;
    }

    private static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            sb.append(i > start ? " " : "").append(words[i]);
        }
        return sb.toString();
    }

    private static List<String> createNgrams(int n, String[] words) {
        List<String> ngrams = new ArrayList<>();
        for (int i = 0; i < words.length - n + 1; i++) {
            ngrams.add(concat(words, i, i+n));
        }
        return ngrams;
    }

    public static void populateAllngrams() {
        ArrayList<String> setIds = new ArrayList<>();
        int docId, ngramId;
        ResultSet rs, rs1, rs2;
        System.gc();
        try{
//          rs1 = getConnectionObject().prepareStatement("SELECT DISTINCT docid FROM alldocuments;").executeQuery();
            rs1 = psPopulateAllngrams1.executeQuery();
            ngramId = getMaxNgramidFromAllngrams() + 1;

            while(rs1.next()){
                docId = rs1.getInt(1);
                try{
//                  rs = getConnectionObject().prepareStatement("SELECT A.wordid, allwords.setid FROM allwords, (SELECT docwordseq, wordid FROM alldocuments WHERE docid = "+docId+") as A WHERE allwords.wordid=A.wordid ORDER BY A.docwordseq").executeQuery();
                    psPopulateAllngrams2.setInt(1, docId);
                    rs = psPopulateAllngrams2.executeQuery();
                    while(rs.next()){
                        setIds.add(""+rs.getInt(1));
                    }
                    rs.close();
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
                String ngram;

                for(int n=1; n<=3; n++){
                    for (Iterator<String> it = createNgrams(n, setIds.toArray(new String[setIds.size()])).iterator(); it.hasNext();) {
                        ngram = it.next();
                        try{
//                            rs2 = getConnectionObject().prepareStatement("SELECT ngramid FROM allngrams WHERE setids = '"+ngram+"';").executeQuery();
                            psPopulateAllngrams3.setString(1, ngram);
                            rs2 = psPopulateAllngrams3.executeQuery();
                            if(rs2.next()){
                            }else{
//                                getConnectionObject().prepareStatement("INSERT INTO allngrams (ngramid, setids) VALUES("+ngramId+", '"+ngram+"')").executeUpdate();
                                psPopulateAllngrams4.setInt(1, ngramId);
                                psPopulateAllngrams4.setString(2, ngram);
                                psPopulateAllngrams4.executeUpdate();
                                ngramId++;
                            }
                            rs2.close();
                        }catch(SQLException ex){
                            ex.printStackTrace();
                        }
                    }
                }
            }
            rs1.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }

    }

    public static String fetchDocumentFromAnswers(int qno, int rno) throws SQLException {
        String returnText="";
        ResultSet rs;
//        rs = getConnectionObject().prepareStatement("SELECT answer FROM answers WHERE qno = "+qno+" AND rno = "+rno+";").executeQuery();

        psFetchDocumentFromAnswers1.setInt(1, qno);
        psFetchDocumentFromAnswers1.setInt(2, rno);
        rs = psFetchDocumentFromAnswers1.executeQuery();

        if(rs.next()){
            returnText = rs.getString(1);
        }
        rs.close();
        return returnText;
    }

    public static String fetchDocumentFromTestanswers(int qno, int rno) throws SQLException {
        String returnText="";
        ResultSet rs;
//        rs = getConnectionObject().prepareStatement("SELECT answer FROM testanswers WHERE qno = "+qno+" AND rno = "+rno+";").executeQuery();
        psFetchDocumentFromTestanswers1.setInt(1, qno);
        psFetchDocumentFromTestanswers1.setInt(2, rno);
        rs = psFetchDocumentFromTestanswers1.executeQuery();
        if(rs.next()){
            returnText = rs.getString(1);
        }
        rs.close();
        return returnText;
    }

    public static Integer[] getRollNumbersWhoSubmittedAnswer(int qno, int maxRno) throws SQLException{

        ArrayList<Integer> retVals = new ArrayList<>();
        ResultSet rs;
//        rs = getConnectionObject().prepareStatement("SELECT rno FROM answers WHERE qno="+qno+" AND answer IS NOT NULL AND docid<="+maxRno+";").executeQuery();
        psGetRollNumbersWhoSubmittedAnswer1.setInt(1, qno);
        psGetRollNumbersWhoSubmittedAnswer1.setInt(2, maxRno);
        rs = psGetRollNumbersWhoSubmittedAnswer1.executeQuery();
        while(rs.next()){
            retVals.add(rs.getInt(1));
        }
        rs.close();
        return retVals.toArray(new Integer[retVals.size()]);
    }

    public static Integer[] getRollNumbersToBeEvaluated(int qno) throws SQLException{

        ArrayList<Integer> retVals = new ArrayList<>();
        int val = Integer.parseInt(JOptionPane.showInputDialog("Input Roll No. between 101 to 131: "));

        if(val>100 && val<132){
            retVals.add(val);
        }
        /*ResultSet rs;
//        rs = getConnectionObject().prepareStatement("SELECT rno FROM testanswers WHERE qno="+qno+" AND answer IS NOT NULL;").executeQuery();
        psGetRollNumbersToBeEvaluated1.setInt(1, qno);
        rs = psGetRollNumbersToBeEvaluated1.executeQuery();
        while(rs.next()){
            retVals.add(rs.getInt(1));
        }
        rs.close();*/
        return retVals.toArray(new Integer[retVals.size()]);
    }

    public static int getGlobalNgramCount() {
        if(numberOfNgrams == 0){
            try{
                ResultSet rs;
//                rs = getConnectionObject().prepareStatement("SELECT COUNT(*) FROM allngrams;").executeQuery();
                rs = psGetGlobalNgramCount1.executeQuery();
                if(rs.next()){
                    numberOfNgrams = rs.getInt(1);
                }
                rs.close();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        return numberOfNgrams;
    }

    public static void populateNgramByDocMatrix() throws SQLException{

//        ResultSet rs  = getConnectionObject().prepareStatement("SELECT ngramid, setids FROM allngrams").executeQuery();
//        ResultSet rs1 = getConnectionObject().prepareStatement("SELECT DISTINCT docid FROM alldocuments").executeQuery();

        ResultSet rs = psPopulateNgramByDocMatrix1.executeQuery();
        ResultSet rs1 = psPopulateNgramByDocMatrix2.executeQuery();
        ResultSet rs2 = null;

        int ngramId, docId, ngramFrequency, numberOfDocsContainingThisNgram;
        double maxNgramFrequency;
        String ngram;
        ArrayList<Integer> aDocument;

        while(rs.next()){
            ngramId = rs.getInt(1);
            ngram = rs.getString(2);
//            //System.out.println("NGRAMID = " +ngramId);
//            //System.out.println("NGRAM = " +ngram);

            rs1.beforeFirst();

            while(rs1.next()){
                docId = rs1.getInt(1);
//                rs2 = getConnectionObject().prepareStatement("SELECT wordid FROM alldocuments WHERE docid = "+docId+" ORDER BY docwordseq;").executeQuery();
                psPopulateNgramByDocMatrix3.setInt(1, docId);
                rs2 = psPopulateNgramByDocMatrix3.executeQuery();
                aDocument = new ArrayList<>();
                while(rs2.next()){
                    aDocument.add(rs2.getInt(1));
                }
                rs2.close();

                Integer[] docWords = new Integer[aDocument.size()];
                for(int i=0; i<docWords.length; i++){
                    docWords[i]=aDocument.get(i);
                    //System.out.println("wordId-"+(i+1)+": "+docWords[i]);
                }
                ngramFrequency = getOccuranceCount(docWords, ngram);
                //System.out.println("ngram:"+ngram+", frequency = "+ngramFrequency);
//                //System.out.println("Inserting: NGRAMID= "+ngramId+" DOCID= "+docId+" NGRAMFREQ= "+ngramFrequency);

//                getConnectionObject().prepareStatement("INSERT INTO ngrambydocmatrix (ngramid, docid, frequency) VALUES("+ngramId+", "+docId+", "+ngramFrequency+");").executeUpdate();

                psPopulateNgramByDocMatrix4.setInt(1, ngramId);
                psPopulateNgramByDocMatrix4.setInt(2, docId);
                psPopulateNgramByDocMatrix4.setDouble(3, ngramFrequency);
                psPopulateNgramByDocMatrix4.executeUpdate();

//                //System.out.println("No. of Rows Inserted: "+noOfRows);

            }
        }

//        //System.out.println("Total rows inserted: "+totalRows+"\n\n");


        rs1.beforeFirst();
        while(rs1.next()){
            docId = rs1.getInt(1);
//            rs2 = getConnectionObject().prepareStatement("SELECT MAX(A.frequency) FROM (SELECT frequency FROM ngrambydocmatrix WHERE docid="+docId+") as A;").executeQuery();
            psPopulateNgramByDocMatrix5.setInt(1, docId);
            rs2 = psPopulateNgramByDocMatrix5.executeQuery();
            if(rs2.next()){
                maxNgramFrequency = (double)rs2.getInt(1);
            }else{
                maxNgramFrequency = 0.0;
            }
            //System.out.println("docId = "+docId+", maxNgramFrequency = "+maxNgramFrequency);
            if(maxNgramFrequency > 0){
//                getConnectionObject().prepareStatement("UPDATE ngrambydocmatrix SET frequency = frequency/"+maxNgramFrequency+" WHERE docid="+docId+";").executeUpdate();
                psPopulateNgramByDocMatrix6.setDouble(1, maxNgramFrequency);
                psPopulateNgramByDocMatrix6.setInt(2, docId);
                psPopulateNgramByDocMatrix6.executeUpdate();
            }
        }
        rs1.close();

        rs.beforeFirst();
        while(rs.next()){

            ngramId = rs.getInt(1);
//            rs2 = getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT docid) FROM ngrambydocmatrix WHERE ngramid="+ngramId+" AND frequency > 0.0;").executeQuery();
            psPopulateNgramByDocMatrix7.setInt(1, ngramId);
            rs2 = psPopulateNgramByDocMatrix7.executeQuery();
            if(rs2.next()){
                numberOfDocsContainingThisNgram = rs2.getInt(1);
            }else{
                numberOfDocsContainingThisNgram = 0;
            }
            if(numberOfDocsContainingThisNgram>0){
//                getConnectionObject().prepareStatement("UPDATE ngrambydocmatrix SET tfidf = frequency*"+(Math.log(getGlobalNgramCount()/numberOfDocsContainingThisNgram))+" WHERE ngramid="+ngramId+";").executeUpdate();
                psPopulateNgramByDocMatrix8.setDouble(1, (Math.log(getGlobalNgramCount()/numberOfDocsContainingThisNgram)));
                psPopulateNgramByDocMatrix8.setInt(2, ngramId);
                psPopulateNgramByDocMatrix8.executeUpdate();
            }

//            //System.out.println("ngramId = "+ngramId+" No. of rows = "+noOfRows);

        }
        rs.close();
    }

    public static Integer[] getWordIdsOfQuery(String[] wordsOfQuery){
        ResultSet rs=null;
        ArrayList<Integer> retVal = new ArrayList<>();
        for(int i=0; i<wordsOfQuery.length; i++){
            try {
//                rs = getConnectionObject().prepareStatement("SELECT wordid FROM allwords WHERE word = '"+wordsOfQuery[i]+"'").executeQuery();
                psGetWordIdsOfQuery1.setString(1, wordsOfQuery[i]);
                rs = psGetWordIdsOfQuery1.executeQuery();
                if(rs.next()){
                    retVal.add(rs.getInt(1));
                }
                rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return retVal.toArray(new Integer[retVal.size()]);
    }

    public static void populateNgramByQueryMatrix(int queryId, String[] queryWords) throws SQLException{

        ResultSet rs2 =null;
        int ngramId, ngramFrequency, numberOfDocsContainingThisNgram;
        double maxNgramFrequency;
        String ngram;

        Integer[] wordIdsOfQuery=getWordIdsOfQuery(queryWords);

        /*
        for(int i=0; i<wordIdsOfQuery.length; i++){
            System.out.println("wordId-"+(i+1)+": "+wordIdsOfQuery[i]);
        }
        */

//        ResultSet rs = getConnectionObject().prepareStatement("SELECT ngramid, setids FROM allngrams").executeQuery();
        ResultSet rs = psPopulatengramByQueryMatrix1.executeQuery();

        while(rs.next()){
            ngramId = rs.getInt(1);
            ngram   = rs.getString(2);
            ngramFrequency = getOccuranceCount(wordIdsOfQuery, ngram);
            //System.out.println("ngram:"+ngram+", frequency = "+ngramFrequency);
//            getConnectionObject().prepareStatement("INSERT INTO ngrambyquerymatrix (ngramid, queryid, frequency) VALUES("+ngramId+", "+queryId+", "+ngramFrequency+");").executeUpdate();
            psPopulatengramByQueryMatrix2.setInt(1, ngramId);
            psPopulatengramByQueryMatrix2.setInt(2, queryId);
            psPopulatengramByQueryMatrix2.setInt(3, ngramFrequency);
            psPopulatengramByQueryMatrix2.executeUpdate();
        }

//        rs2 = getConnectionObject().prepareStatement("SELECT MAX(A.frequency) FROM (SELECT frequency FROM ngrambyquerymatrix WHERE queryid="+queryId+") AS A;").executeQuery();
        psPopulatengramByQueryMatrix3.setInt(1, queryId);
        rs2 = psPopulatengramByQueryMatrix3.executeQuery();

        if(rs2.next()){
            maxNgramFrequency = (double)rs2.getInt(1);
        }else{
            maxNgramFrequency = 0.0;
        }
        rs2.close();
        //System.out.println("max Ngram-Frequency: "+maxNgramFrequency);
        if(maxNgramFrequency > 0){
//            getConnectionObject().prepareStatement("UPDATE ngrambyquerymatrix SET frequency = frequency/"+maxNgramFrequency+" WHERE queryid="+queryId+";").executeUpdate();
            psPopulatengramByQueryMatrix4.setDouble(1, maxNgramFrequency);
            psPopulatengramByQueryMatrix4.setInt(2, queryId);
            psPopulatengramByQueryMatrix4.executeUpdate();
        }

        rs.beforeFirst();
        while(rs.next()){
            ngramId = rs.getInt(1);
//            rs2 = getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT docid) FROM ngrambydocmatrix WHERE ngramid="+ngramId+" AND frequency > 0.0;").executeQuery();
            psPopulatengramByQueryMatrix5.setInt(1, ngramId);
            rs2 = psPopulatengramByQueryMatrix5.executeQuery();
            if(rs2.next()){
                numberOfDocsContainingThisNgram = rs2.getInt(1);
            }else{
                numberOfDocsContainingThisNgram = 0;
            }
            rs2.close();
            if(numberOfDocsContainingThisNgram>0){
//                getConnectionObject().prepareStatement("UPDATE ngrambyquerymatrix SET tfidf = frequency*"+(Math.log(getGlobalNgramCount()/numberOfDocsContainingThisNgram))+" WHERE ngramid="+ngramId+" AND queryid="+queryId+";").executeUpdate();

                psPopulatengramByQueryMatrix6.setDouble(1, Math.log(getGlobalNgramCount()/numberOfDocsContainingThisNgram));
                psPopulatengramByQueryMatrix6.setInt(2, ngramId);
                psPopulatengramByQueryMatrix6.setInt(3, queryId);
                psPopulatengramByQueryMatrix6.executeUpdate();
            }
        }
        rs.close();
    }

    private static int getOccuranceCount(Integer[] aDocument, String ngram) throws SQLException{
        int count=0;
        String[] ngramComponents = ngram.split(" ");
        Integer[] ngramSetIds = new Integer[ngramComponents.length];
        for(int i=0; i<ngramComponents.length; i++){
            ngramSetIds[i]=Integer.parseInt(ngramComponents[i]);
            //System.out.println("ngram Split "+i+"="+ngramComponents[i]);
        }

        ResultSet rs = null;
        ArrayList<Integer> temp= null;
        HashMap<Integer, ArrayList<Integer>> setIdsByWordIds = new HashMap<>();

        for(int i=0; i<ngramSetIds.length; i++){
//            rs = getConnectionObject().prepareStatement("SELECT wordid FROM synonymset WHERE setid = "+ ngramSetIds[i] +";").executeQuery();
            psGetOccuranceCount1.setInt(1, ngramSetIds[i]);
            rs = psGetOccuranceCount1.executeQuery();
            temp= new ArrayList<>();
            while(rs.next()){
                temp.add(rs.getInt(1));
            }
            rs.close();
            setIdsByWordIds.put(ngramSetIds[i], temp);
        }

        for(int i=0; i<= (aDocument.length-ngramSetIds.length);){
            int j=0;
            for(; j<ngramSetIds.length; j++){
                if(! setIdsByWordIds.get(ngramSetIds[j]).contains(aDocument[i+j])){
                    break;
                }
            }
            if(j==ngramSetIds.length){
                count++;
                i+=ngramSetIds.length;
            }else{
                i++;
            }
        }

        return count;
     }

    static void clearAllExceptTestData() {
        try{
            getConnectionObject().prepareStatement("DELETE FROM allwords").executeUpdate();
            getConnectionObject().prepareStatement("DELETE FROM alldocuments").executeUpdate();
            getConnectionObject().prepareStatement("DELETE FROM allngrams").executeUpdate();
            getConnectionObject().prepareStatement("DELETE FROM ngrambydocmatrix").executeUpdate();
            getConnectionObject().prepareStatement("DELETE FROM synonymset").executeUpdate();
            getConnectionObject().prepareStatement("DELETE FROM matrixdjdash").executeUpdate();
            getConnectionObject().prepareStatement("DELETE FROM matrixs").executeUpdate();
            getConnectionObject().prepareStatement("DELETE FROM matrixu").executeUpdate();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public static void populateMatrix(String matrixName, Matrix matrix) throws SQLException {
        if(matrixName.equals("matrixdjdash")){
            for(int i=0; i<matrix.getRowDimension(); i++){
                for(int j=0; j<matrix.getColumnDimension(); j++){
    //                getConnectionObject().prepareStatement("INSERT INTO "+matrixName+"(rowid, columnid, cellvalue) VALUES("+i+", "+j+", "+matrix.getArray()[i][j]+")").executeUpdate();
                    psPopulateMatrix1.setInt(1, i);
                    psPopulateMatrix1.setInt(2, j);
                    psPopulateMatrix1.setDouble(3, matrix.getArray()[i][j]);
                    psPopulateMatrix1.executeUpdate();
                }
            }
        }
        if(matrixName.equals("matrixs")){
            for(int i=0; i<matrix.getRowDimension(); i++){
                for(int j=0; j<matrix.getColumnDimension(); j++){
    //                getConnectionObject().prepareStatement("INSERT INTO "+matrixName+"(rowid, columnid, cellvalue) VALUES("+i+", "+j+", "+matrix.getArray()[i][j]+")").executeUpdate();
                    psPopulateMatrix2.setInt(1, i);
                    psPopulateMatrix2.setInt(2, j);
                    psPopulateMatrix2.setDouble(3, matrix.getArray()[i][j]);
                    psPopulateMatrix2.executeUpdate();
                }
            }
        }
        if(matrixName.equals("matrixu")){
            for(int i=0; i<matrix.getRowDimension(); i++){
                for(int j=0; j<matrix.getColumnDimension(); j++){
    //                getConnectionObject().prepareStatement("INSERT INTO "+matrixName+"(rowid, columnid, cellvalue) VALUES("+i+", "+j+", "+matrix.getArray()[i][j]+")").executeUpdate();
                    psPopulateMatrix3.setInt(1, i);
                    psPopulateMatrix3.setInt(2, j);
                    psPopulateMatrix3.setDouble(3, matrix.getArray()[i][j]);
                    psPopulateMatrix3.executeUpdate();
                }
            }
        }
    }

    public static double[][] getMatrixdjdashFromDatabase() throws SQLException {
//        ResultSet rs = getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT rowid) FROM "+matrixName+";").executeQuery();
        ResultSet rs = psGetMatrixdjdashFromDatabase1.executeQuery();
        rs.next();
        int rowCount = rs.getInt(1);
        rs.close();

//        rs = getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT columnid) FROM "+matrixName+";").executeQuery();
        rs = psGetMatrixdjdashFromDatabase2.executeQuery();
        rs.next();
        int columnCount = rs.getInt(1);
        rs.close();

        double[][] retVal = new double[rowCount][columnCount];

//        rs = getConnectionObject().prepareStatement("SELECT cellvalue FROM "+matrixName+";").executeQuery();
        rs = psGetMatrixdjdashFromDatabase3.executeQuery();
        for(int i=0; i<rowCount; i++){
            for(int j=0; j<columnCount && rs.next(); j++){
                retVal[i][j]=rs.getDouble(1);
            }
        }
        rs.close();
        return retVal;
    }


    public static double[][] getMatrixsFromDatabase() throws SQLException {
//        ResultSet rs = getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT rowid) FROM "+matrixName+";").executeQuery();
        ResultSet rs = psGetMatrixsFromDatabase1.executeQuery();
        rs.next();
        int rowCount = rs.getInt(1);
        rs.close();

//        rs = getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT columnid) FROM "+matrixName+";").executeQuery();
        rs = psGetMatrixsFromDatabase2.executeQuery();
        rs.next();
        int columnCount = rs.getInt(1);
        rs.close();

        double[][] retVal = new double[rowCount][columnCount];

//        rs = getConnectionObject().prepareStatement("SELECT cellvalue FROM "+matrixName+";").executeQuery();
        rs = psGetMatrixsFromDatabase3.executeQuery();
        for(int i=0; i<rowCount; i++){
            for(int j=0; j<columnCount && rs.next(); j++){
                retVal[i][j]=rs.getDouble(1);
            }
        }
        rs.close();
        return retVal;
    }


    public static double[][] getMatrixuFromDatabase() throws SQLException {
//        ResultSet rs = getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT rowid) FROM "+matrixName+";").executeQuery();
        ResultSet rs = psGetMatrixuFromDatabase1.executeQuery();
        rs.next();
        int rowCount = rs.getInt(1);
        rs.close();

//        rs = getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT columnid) FROM "+matrixName+";").executeQuery();
        rs = psGetMatrixuFromDatabase2.executeQuery();
        rs.next();
        int columnCount = rs.getInt(1);
        rs.close();

        double[][] retVal = new double[rowCount][columnCount];

//        rs = getConnectionObject().prepareStatement("SELECT cellvalue FROM "+matrixName+";").executeQuery();
        rs = psGetMatrixuFromDatabase3.executeQuery();
        for(int i=0; i<rowCount; i++){
            for(int j=0; j<columnCount && rs.next(); j++){
                retVal[i][j]=rs.getDouble(1);
            }
        }
        rs.close();
        return retVal;
    }


    public static void insertGradeToTestAnswers(int qno, int rno, double score) throws SQLException{
        psInsertGradeToTestAnswers1.setDouble(1, score);
        psInsertGradeToTestAnswers1.setInt(2, qno);
        psInsertGradeToTestAnswers1.setInt(3, rno);
        psInsertGradeToTestAnswers1.executeUpdate();
//        getConnectionObject().prepareStatement("UPDATE testanswers SET grade="+score+"WHERE qno="+qno+" AND rno="+rno+";").executeUpdate();
    }

}
