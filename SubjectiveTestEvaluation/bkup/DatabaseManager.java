/*
 * Carries out all interaction with database using single connection and operation-specific static methods.
 */
package subjectivetestevaluation;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static String[] fetchWordnetSynonyms(String get) {

        String[] synsetWordforms;
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(get);

        WordNetDatabase database = WordNetDatabase.getFileInstance();

        Synset[] synsets = database.getSynsets(get);
        for(int i=0; i<synsets.length; i++){
            if(synsets[i].getDefinition().startsWith("(computer science)")){
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

    private static int insertToAllwords(String word) {

        PreparedStatement ps;
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

                ps = getConnectionObject().prepareStatement("INSERT INTO testdb.allwords(wordid, word) VALUES ("+wordId+", '"+word+"');");
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            //ex.printStackTrace();
        }
        return wordId;
    }

    private static int getWordId(String word){
        PreparedStatement ps = null;
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
            ps = getConnectionObject().prepareStatement("SELECT wordid FROM allwords WHERE word = '"+word+"';");
            rs = ps.executeQuery();
            if(rs.next()){
                wordId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            //System.out.println("word:"+word);
            ex.printStackTrace();
        }
        return wordId;
    }

    private static int getMaxWordId() {
        int wordId=0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnectionObject().prepareStatement("SELECT MAX(wordid) FROM testdb.allwords;");
            rs = ps.executeQuery();
            if(rs.next()){
                wordId = rs.getInt(1);
            }
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
                    getConnectionObject().prepareStatement("UPDATE allwords set setid="+targetSetId+" WHERE wordid="+keyWordId+";").executeUpdate();
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
            }
        }
        ////System.out.println("wordIdSet length: "+wordIdSet.length);
    }

    private static void insertATupleToSynonymset(int targetSetId, int targetWordId) {

        try{
            getConnectionObject().prepareStatement("INSERT INTO Synonymset (setid, wordid) VALUES ("+targetSetId+", "+targetWordId+");").executeUpdate();
        }catch(SQLException ex){
            //ex.printStackTrace();
        }
    }

    private static int getSetIdFromAllwords(int keyWordId){
        int setId = 0;
        try{
            ResultSet rs = getConnectionObject().prepareStatement("SELECT setid FROM allwords WHERE wordid ="+keyWordId+";").executeQuery();
            if(rs.next()){
                setId = rs.getInt(1);
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return setId;
    }

    private static int getMaxSetId(){
        PreparedStatement ps = null;
        ResultSet rs = null;
        int setId = 0;
        try {
            ps = getConnectionObject().prepareStatement("SELECT MAX(setid) FROM testdb.synonymset;");
            rs = ps.executeQuery();
            if(rs.next()){
                setId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return setId;
    }

    public static void insertToAlldocuments(int qno, int rno, String word, int docWordSeq) {

        int wordId = getWordId(word), docId = ((qno-1)*Controller.rnoRange)+rno;
        try {
            getConnectionObject().prepareStatement("INSERT INTO alldocuments(docid, docwordseq, wordid) VALUES("+docId+", "+docWordSeq+", "+wordId +");").executeUpdate();
            getConnectionObject().prepareStatement("UPDATE answers SET docid ="+docId+" WHERE qno="+qno+" AND rno="+rno+";").executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("\n\nError Word: "+word+"\nERROR WORDID: "+wordId+"\n\n");
        }
    }

    private static int getMaxNgramidFromAllngrams() {
        ResultSet rs = null;
        int ngramId = 0;
        try {
            rs = getConnectionObject().prepareStatement("SELECT MAX(ngramid) FROM testdb.allngrams;").executeQuery();
            if(rs.next()){
                ngramId = rs.getInt(1);
            }
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

    static void populateAllngrams() {
        ArrayList<String> setIds = new ArrayList<>();
        int docId, ngramId;
        System.gc();
        try{
            ResultSet rs2, rs1 = getConnectionObject().prepareStatement("SELECT DISTINCT docid FROM alldocuments;").executeQuery();
            ngramId = getMaxNgramidFromAllngrams() + 1;

            while(rs1.next()){
                docId = rs1.getInt(1);
                try{
                    ResultSet rs = getConnectionObject().prepareStatement("SELECT A.wordid, testdb.allwords.setid FROM allwords, (SELECT docwordseq, wordid FROM testdb.alldocuments WHERE docid = "+docId+") as A WHERE allwords.wordid=A.wordid ORDER BY A.docwordseq").executeQuery();
                    while(rs.next()){
                        setIds.add(""+rs.getInt(1));
                    }
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
                String ngram;
                for(int n=1; n<=3; n++){
                    for (Iterator<String> it = createNgrams(n, setIds.toArray(new String[setIds.size()])).iterator(); it.hasNext();) {
                        ngram = it.next();
                        try{
                            rs2 = getConnectionObject().prepareStatement("SELECT ngramid FROM allngrams WHERE setids = '"+ngram+"';").executeQuery();
                            if(rs2.next()){
                            }else{
                                getConnectionObject().prepareStatement("INSERT INTO allngrams (ngramid, setids) VALUES("+ngramId+", '"+ngram+"')").executeUpdate();
                                ngramId++;
                            }
                        }catch(SQLException ex){
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }

    }

    static String fetchDocumentFromAnswers(int qno, int rno) throws SQLException {
        String returnText="";
        ResultSet rs;
        rs = getConnectionObject().prepareStatement("SELECT answer FROM testdb.answers WHERE qno = "+qno+" AND rno = "+rno+";").executeQuery();
        if(rs.next()){
            returnText = rs.getString(1);
        }
        return returnText;
    }

    static String fetchDocumentFromTestAnswers(int qno, int rno) throws SQLException {
        String returnText="";
        ResultSet rs;
        rs = getConnectionObject().prepareStatement("SELECT answer FROM testanswers WHERE qno = "+qno+" AND rno = "+rno+";").executeQuery();
        if(rs.next()){
            returnText = rs.getString(1);
        }
        return returnText;
    }

    public static Integer[] getRollNumbersWhoSubmittedAnswerOfQueNo(int qno) throws SQLException{

        ArrayList<Integer> retVals = new ArrayList<>();
        ResultSet rs;
        rs = getConnectionObject().prepareStatement("SELECT rno FROM answers WHERE qno="+qno+" AND answer IS NOT NULL ORDER BY(rno);").executeQuery();

        while(rs.next()){
            retVals.add(rs.getInt(1));
        }
        return retVals.toArray(new Integer[retVals.size()]);
    }

    public static Integer[] getRollNumbersToBeEvaluatedForQueNo(int qno) throws SQLException{

        ArrayList<Integer> retVals = new ArrayList<>();
        ResultSet rs;
        rs = getConnectionObject().prepareStatement("SELECT rno FROM testanswers WHERE qno="+qno+" AND answer IS NOT NULL;").executeQuery();

        while(rs.next()){
            retVals.add(rs.getInt(1));
        }
        return retVals.toArray(new Integer[retVals.size()]);
    }

    public static void main(String[] args){
        //createDatabaseSchema();

    }

    public static int getGlobalNgramCount() {
        if(numberOfNgrams == 0){
            try{
                ResultSet rs = getConnectionObject().prepareStatement("SELECT COUNT(*) FROM allngrams;").executeQuery();
                if(rs.next()){
                    numberOfNgrams = rs.getInt(1);
                }
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        return numberOfNgrams;
    }

    static void populateNgramByDocMatrix() throws SQLException{

        ResultSet rs  = DatabaseManager.getConnectionObject().prepareStatement("SELECT ngramid, setids FROM allngrams").executeQuery();
        ResultSet rs1 = DatabaseManager.getConnectionObject().prepareStatement("SELECT DISTINCT docid FROM alldocuments").executeQuery();
        ResultSet rs2 = null;
        int ngramId, docId, ngramFrequency, numberOfDocsContainingThisNgram;
        double maxNgramFrequency;
        String ngram;
        ArrayList<Integer> aDocument;
        int noOfRows;

        while(rs.next()){
            ngramId = rs.getInt(1);
            ngram = rs.getString(2);
//            //System.out.println("NGRAMID = " +ngramId);
//            //System.out.println("NGRAM = " +ngram);

            rs1.beforeFirst();

            while(rs1.next()){
                docId = rs1.getInt(1);
                rs2 = getConnectionObject().prepareStatement("SELECT wordid FROM alldocuments WHERE docid = "+docId+" ORDER BY docwordseq;").executeQuery();

                aDocument = new ArrayList<>();
                while(rs2.next()){
                    aDocument.add(rs2.getInt(1));
                }

                Integer[] docWords = new Integer[aDocument.size()];
                for(int i=0; i<docWords.length; i++){
                    docWords[i]=aDocument.get(i);
                    //System.out.println("wordId-"+(i+1)+": "+docWords[i]);
                }
                ngramFrequency = getOccuranceCount(docWords, ngram);
                //System.out.println("ngram:"+ngram+", frequency = "+ngramFrequency);
//                //System.out.println("Inserting: NGRAMID= "+ngramId+" DOCID= "+docId+" NGRAMFREQ= "+ngramFrequency);
                noOfRows= getConnectionObject().prepareStatement("INSERT INTO ngrambydocmatrix (ngramid, docid, frequency) VALUES("+ngramId+", "+docId+", "+ngramFrequency+");").executeUpdate();
//                //System.out.println("No. of Rows Inserted: "+noOfRows);

            }
        }

//        //System.out.println("Total rows inserted: "+totalRows+"\n\n");


        rs1.beforeFirst();
        while(rs1.next()){
            docId = rs1.getInt(1);
            rs2 = getConnectionObject().prepareStatement("SELECT MAX(A.frequency) FROM (SELECT frequency FROM ngrambydocmatrix WHERE docid="+docId+") as A;").executeQuery();
            if(rs2.next()){
                maxNgramFrequency = (double)rs2.getInt(1);
            }else{
                maxNgramFrequency = 0.0;
            }
            //System.out.println("docId = "+docId+", maxNgramFrequency = "+maxNgramFrequency);
            if(maxNgramFrequency > 0){
                getConnectionObject().prepareStatement("UPDATE ngrambydocmatrix SET frequency = frequency/"+maxNgramFrequency+" WHERE docid="+docId+";").executeUpdate();
            }
        }
        rs.beforeFirst();
        while(rs.next()){

            ngramId = rs.getInt(1);
            rs2 = getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT docid) FROM ngrambydocmatrix WHERE ngramid="+ngramId+" AND frequency > 0.0;").executeQuery();
            if(rs2.next()){
                numberOfDocsContainingThisNgram = rs2.getInt(1);
            }else{
                numberOfDocsContainingThisNgram = 0;
            }
            if(numberOfDocsContainingThisNgram>0){
                noOfRows = getConnectionObject().prepareStatement("UPDATE ngrambydocmatrix SET tfidf = frequency*"+(Math.log(getGlobalNgramCount()/numberOfDocsContainingThisNgram))+" WHERE ngramid="+ngramId+";").executeUpdate();
            }

//            //System.out.println("ngramId = "+ngramId+" No. of rows = "+noOfRows);

        }
    }

    static Integer[] getWordIdsOfQuery(String[] wordsOfQuery){
        ResultSet rs=null;
        ArrayList<Integer> retVal = new ArrayList<>();
        for(int i=0; i<wordsOfQuery.length; i++){
            try {
                rs = getConnectionObject().prepareStatement("SELECT wordid FROM allwords WHERE word = '"+wordsOfQuery[i]+"'").executeQuery();
                if(rs.next()){
                    retVal.add(rs.getInt(1));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return retVal.toArray(new Integer[retVal.size()]);
    }

    static void populateNgramByQueryMatrix(int queryId, String[] queryWords) throws SQLException{

        ResultSet rs2 =null;
        int ngramId, ngramFrequency, numberOfDocsContainingThisNgram;
        double maxNgramFrequency;
        String ngram;

        Integer[] wordIdsOfQuery=getWordIdsOfQuery(queryWords);

        for(int i=0; i<wordIdsOfQuery.length; i++){
            //System.out.println("wordId-"+(i+1)+": "+wordIdsOfQuery[i]);
        }
        ResultSet rs  = DatabaseManager.getConnectionObject().prepareStatement("SELECT ngramid, setids FROM allngrams").executeQuery();
        while(rs.next()){
            ngramId = rs.getInt(1);
            ngram   = rs.getString(2);
            ngramFrequency = getOccuranceCount(wordIdsOfQuery, ngram);
            //System.out.println("ngram:"+ngram+", frequency = "+ngramFrequency);
            getConnectionObject().prepareStatement("INSERT INTO ngrambyquerymatrix (ngramid, queryid, frequency) VALUES("+ngramId+", "+queryId+", "+ngramFrequency+");").executeUpdate();
        }

        rs2 = getConnectionObject().prepareStatement("SELECT MAX(A.frequency) FROM (SELECT frequency FROM ngrambyquerymatrix WHERE queryid="+queryId+") AS A;").executeQuery();
        if(rs2.next()){
            maxNgramFrequency = (double)rs2.getInt(1);
        }else{
            maxNgramFrequency = 0.0;
        }
        //System.out.println("max Ngram-Frequency: "+maxNgramFrequency);
        if(maxNgramFrequency > 0){
            getConnectionObject().prepareStatement("UPDATE ngrambyquerymatrix SET frequency = frequency/"+maxNgramFrequency+" WHERE queryid="+queryId+";").executeUpdate();
        }

        rs.beforeFirst();
        while(rs.next()){
            ngramId = rs.getInt(1);
            rs2 = getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT docid) FROM ngrambydocmatrix WHERE ngramid="+ngramId+" AND frequency > 0.0;").executeQuery();
            if(rs2.next()){
                numberOfDocsContainingThisNgram = rs2.getInt(1);
            }else{
                numberOfDocsContainingThisNgram = 0;
            }
            if(numberOfDocsContainingThisNgram>0){
                getConnectionObject().prepareStatement("UPDATE ngrambyquerymatrix SET tfidf = frequency*"+(Math.log(getGlobalNgramCount()/numberOfDocsContainingThisNgram))+" WHERE ngramid="+ngramId+" AND queryid="+queryId+";").executeUpdate();
            }
        }
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
            rs = getConnectionObject().prepareStatement("SELECT wordid FROM synonymset WHERE setid = "+ ngramSetIds[i] +";").executeQuery();
            temp= new ArrayList<>();
            while(rs.next()){
                temp.add(rs.getInt(1));
            }
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

    static void populateMatrix(String matrixName, Matrix matrix) throws SQLException {
        for(int i=0; i<matrix.getRowDimension(); i++){
            for(int j=0; j<matrix.getColumnDimension(); j++){
                getConnectionObject().prepareStatement("INSERT INTO "+matrixName+"(rowid, columnid, cellvalue) VALUES("+i+", "+j+", "+matrix.getArray()[i][j]+")").executeUpdate();
            }
        }
    }

    static double[][] getMatrix(String matrixName) throws SQLException {
        ResultSet rs = getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT rowid) FROM "+matrixName+";").executeQuery();
        rs.next();
        int rowCount = rs.getInt(1);

        rs = getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT columnid) FROM "+matrixName+";").executeQuery();
        rs.next();
        int columnCount = rs.getInt(1);

        double[][] retVal = new double[rowCount][columnCount];

        rs = getConnectionObject().prepareStatement("SELECT cellvalue FROM "+matrixName+";").executeQuery();
        for(int i=0; i<rowCount; i++){
            for(int j=0; j<columnCount && rs.next(); j++){
                retVal[i][j]=rs.getDouble(1);
            }
        }
        return retVal;
    }

    static void insertGradeToTestAnswers(int qno, int rno, double score) throws SQLException{
        getConnectionObject().prepareStatement("UPDATE testanswers SET grade="+score+"WHERE qno="+qno+" AND rno="+rno+";").executeUpdate();
    }

    
}



/**
 *
    public static void createDatabaseSchema(){
        PreparedStatement ps = null;
        String[] statements;
        statements = new String[]{

        "DROP TABLE IF EXISTS `testdb`.`alldocuments`;",
        "CREATE TABLE  `testdb`.`alldocuments` (`docid` int(10) unsigned NOT NULL, `docwordseq` int(10) unsigned NOT NULL, `supersetid` int(10) unsigned NOT NULL, PRIMARY KEY (`docid`,`docwordseq`, `supersetid`) USING BTREE) ENGINE=InnoDB DEFAULT CHARSET=latin1;",

        "DROP TABLE IF EXISTS `testdb`.`allngrams`;",
        "CREATE TABLE  `testdb`.`allngrams` ( `ngramid` int(10) unsigned NOT NULL, `supersetids` varchar(100) NOT NULL, PRIMARY KEY (`ngramid`) USING BTREE ) ENGINE=InnoDB DEFAULT CHARSET=latin1;",

        "DROP TABLE IF EXISTS `testdb`.`allwords`;",
        "CREATE TABLE  `testdb`.`allwords` ( `wordid` int(10) unsigned NOT NULL, `word` varchar(50) NOT NULL, PRIMARY KEY (`wordid`) USING BTREE ) ENGINE=InnoDB DEFAULT CHARSET=latin1;",

        "DROP TABLE IF EXISTS `testdb`.`ngrambydocmatrix`;",
        "CREATE TABLE  `testdb`.`ngrambydocmatrix` ( `ngramid` int(10) unsigned NOT NULL, `docid` int(10) unsigned NOT NULL, `frequency` double NOT NULL, `tfidf` double NOT NULL, PRIMARY KEY (`ngramid`,`docid`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;",

        "DROP TABLE IF EXISTS `testdb`.`synonymset`;",
        "CREATE TABLE  `testdb`.`synonymset` ( `setid` int(10) unsigned NOT NULL, `wordid` int(10) unsigned NOT NULL, PRIMARY KEY (`setid`, `wordid`) USING BTREE ) ENGINE=InnoDB DEFAULT CHARSET=latin1;",

        "DROP TABLE IF EXISTS `testdb`.`synonymsupersets`;",
        "CREATE TABLE  `testdb`.`synonymsupersets` ( `supersetid` int(10) unsigned NOT NULL, `setids` VARCHAR(100) unsigned NOT NULL, PRIMARY KEY (`setids`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;",

        "DROP TABLE IF EXISTS `testdb`.`answers`;",
        "CREATE TABLE  `testdb`.`answers` ( `rno` varchar(20) NOT NULL, `qno` int(10) unsigned NOT NULL, `answer` varchar(40000) DEFAULT NULL, `grade` int(10) unsigned DEFAULT NULL, PRIMARY KEY (`rno`,`qno`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;",

        "DROP TABLE IF EXISTS `testdb`.`student`;",
        "CREATE TABLE  `testdb`.`student` ( `rno` varchar(20) NOT NULL, `connection_status` varchar(15) NOT NULL, `test_status` varchar(10) NOT NULL, `ip_port` varchar(45) NOT NULL, PRIMARY KEY (`rno`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;",

        "DROP TABLE IF EXISTS `testdb`.`question_paper`;",
        "CREATE TABLE  `testdb`.`question_paper` ( `qno` int(10) unsigned NOT NULL, `question` varchar(400) NOT NULL, PRIMARY KEY (`qno`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;"

        };

        for(int i=0; i<statements.length; i++){
            try {
                ps=getConnectionObject().prepareStatement(statements[i]);
                ps.executeUpdate();
            } catch (SQLException ex) {
                //System.out.println("Statement - "+(i+1)+"\nException Message: "+ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }

    }
*/



/**
    private static int insertToSynonymset(int[] wordIdSet) {
        ResultSet rs;
        HashMap<Integer, ArrayList<Integer>> wordIdBySetIds = new HashMap<>();
        ArrayList<Integer> setIdsFound;
        int targetSetId = -1;

        for(int i=0; i<wordIdSet.length; i++){

            try{
                rs = getConnectionObject().prepareStatement("SELECT setid FROM synonymset WHERE wordid =" + wordIdSet[i] + ";").executeQuery();

                setIdsFound = new ArrayList<>();

                while(rs.next()){
                    setIdsFound.add(rs.getInt(1));
                }
                wordIdBySetIds.put(wordIdSet[i], setIdsFound);

            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }

        setIdsFound = wordIdBySetIds.get(wordIdSet[0]);
        for(int j=0; (j< setIdsFound.size()) && (targetSetId == -1) ; j++){
            for(int k=1; k<wordIdBySetIds.size(); k++){
                if(wordIdBySetIds.get(wordIdSet[k]).contains(setIdsFound.get(j))){
                    targetSetId = setIdsFound.get(j);
                }else{
                    targetSetId = -1;
                    break;
                }
            }
        }

        if(targetSetId==-1){
            targetSetId = insertNewSynonymSet(wordIdSet);
        }else{
            updateSynonymSet(targetSetId, wordIdSet);
        }
        return targetSetId;
    }

    private static int insertNewSynonymSet(int[] wordIdSet) {
        PreparedStatement ps = null;
        int setId=getMaxSetId()+1;
        for(int i=0; i<wordIdSet.length; i++){
            try {
                ps = getConnectionObject().prepareStatement("INSERT INTO testdb.synonymset(setid, wordid) VALUES("+setId+", "+wordIdSet[i]+");");
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return setId;
    }

    private static void updateSynonymSet(int targetSetId, int[] wordIdSet) {
        PreparedStatement ps = null;
        for(int i=0; i<wordIdSet.length; i++){
                try {
                    if(!setIdWordIdPairExists(targetSetId, wordIdSet[i])){
                        ps = getConnectionObject().prepareStatement("INSERT INTO testdb.synonymset(setid, wordid) VALUES("+targetSetId+", "+wordIdSet[i]+");");
                        ps.executeUpdate();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        }
    }

    private static boolean setIdWordIdPairExists(int targetSetId, int wordId) throws SQLException{
        ResultSet rs;
        rs = getConnectionObject().prepareStatement("SELECT * FROM synonymset WHERE wordid = " + wordId + " AND setid ="+ targetSetId +" ;").executeQuery();
        if(rs.next()){
            return true;
        }else{
            return false;
        }
    }

    private static int getMaxSupersetIdFromSupersets(){
        ResultSet rs = null;
        int supersetId = 0;
        try {
            rs = getConnectionObject().prepareStatement("SELECT MAX(supersetid) FROM testdb.synonymsupersets;").executeQuery();
            if(rs.next()){
                supersetId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return supersetId;
    }

    static void populateSynonymsupersets() {
        int wordId;
        ArrayList<Integer> setIds = new ArrayList<>(), setIds = new ArrayList<>();

        ResultSet rs, rs1,rs2;
        try {
            rs = getConnectionObject().prepareStatement("SELECT wordid FROM allwords").executeQuery();
            while(rs.next()){
                setIds.removeAll(setIds);
                setIds.removeAll(setIds);
                wordId=rs.getInt(1);

                rs1 = getConnectionObject().prepareStatement("SELECT setid FROM synonymset WHERE wordid ="+wordId+"").executeQuery();
                while(rs1.next()){
                    setIds.add(rs1.getInt(1));
                }

                for(int i=0; i<setIds.size(); i++){
                    rs2 = getConnectionObject().prepareStatement("SELECT supersetid FROM synonymsupersets WHERE setid ="+setIds.get(i).intValue()+"").executeQuery();
                    if(rs2.next()){
                        setIds.add(rs2.getInt(1));
                        setIds.remove(i);
                    }
                }

                insertNewSuperset(setIds, setIds);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void insertNewSuperset(ArrayList<Integer> setIds, ArrayList<Integer> setIds) {
        int newSupersetid;
        if(setIds.size()==1){
            newSupersetid = setIds.get(0);
        }else{
            newSupersetid = getMaxSupersetIdFromSupersets() + 1;
        }
        for(int i=0; i<supersetIds.size(); i++){
            try{
                getConnectionObject().prepareStatement("UPDATE synonymsupersets SET supersetid = "+newSupersetid+" WHERE synonymsupersets.supersetid="+setIds.get(i)+";").executeUpdate();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        for(int i=0; i<setIds.size(); i++){
            try{
                getConnectionObject().prepareStatement("INSERT INTO synonymsupersets(supersetid, setid) VALUES("+newSupersetid+", "+setIds.get(i) +");").executeUpdate();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }
*/






/**
 *
 *
    private static String[] explode(String ngram) {

        String[] componentSetIds= ngram.split(" ");
        switch(componentSetIds.length){
            case 1:
                return explode1(componentSetIds);
            case 2:
                return explode2(componentSetIds);
            case 3:
                return explode3(componentSetIds);
        }
        return null;

    }

    private static String[] explode1(String[] componentSetIds) {

        ArrayList<String> temp = new ArrayList<>();
        ResultSet rs;

        try{
            rs = getConnectionObject().prepareStatement("SELECT wordid FROM synonymset WHERE setid = "+Integer.parseInt(componentSetIds[0]) +";").executeQuery();
            while(rs.next()){
                temp.add(""+rs.getInt(1));
            }
            return temp.toArray(new String[temp.size()]);
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return new String[]{componentSetIds[0]};
    }

    private static String[] explode2(String[] componentSetIds) {

        ArrayList<String> place1 = new ArrayList<>();
        ArrayList<String> place2 = new ArrayList<>();
        String[] retVal=null;

        try{
            ResultSet rs = getConnectionObject().prepareStatement("SELECT wordid FROM synonymset WHERE setid = "+Integer.parseInt(componentSetIds[0]) +";").executeQuery();
            while(rs.next()){
                place1.add(rs.getString(1));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }

        try{
            ResultSet rs = getConnectionObject().prepareStatement("SELECT wordid FROM synonymset WHERE setid = "+Integer.parseInt(componentSetIds[1]) +";").executeQuery();
            while(rs.next()){
                place2.add(rs.getString(1));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }

        retVal = new String[place1.size()*place2.size()];
        for(int i=0; i<place1.size();i++){
            for(int j=0; j<place2.size(); j++){
                retVal[ i*place2.size() + j] = place1.get(i)+" "+place2.get(j);
            }
        }
        return retVal;
    }

    private static String[] explode3(String[] componentSetIds) {

        ArrayList<String> place1 = new ArrayList<>();
        ArrayList<String> place2 = new ArrayList<>();
        ArrayList<String> place3 = new ArrayList<>();
        String[] retVal=null;

        try{
            ResultSet rs = getConnectionObject().prepareStatement("SELECT wordid FROM synonymset WHERE setid = "+Integer.parseInt(componentSetIds[0]) +";").executeQuery();
            while(rs.next()){
                place1.add(rs.getString(1));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }

        try{
            ResultSet rs = getConnectionObject().prepareStatement("SELECT wordid FROM synonymset WHERE setid = "+Integer.parseInt(componentSetIds[1]) +";").executeQuery();
            while(rs.next()){
                place2.add(rs.getString(1));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }

        try{
            ResultSet rs = getConnectionObject().prepareStatement("SELECT wordid FROM synonymset WHERE setid = "+Integer.parseInt(componentSetIds[2]) +";").executeQuery();
            while(rs.next()){
                place3.add(rs.getString(1));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }

        retVal = new String[place1.size()*place2.size()*place3.size()];
        for(int i=0; i<place1.size();i++){
            for(int j=0; j<place2.size(); j++){
                for(int k=0; k<place3.size(); k++){
                    retVal[ ( i*place2.size() + j)*place3.size() + k] = place1.get(i)+" "+place2.get(j)+" "+place3.get(k);
                }
            }
        }
        return retVal;
    }

    private static int getMinPatternLength(String[] possibilities) {
        int len;
        if(possibilities==null){
            return 1;
        }else{
            len=possibilities[0].length();
            for(int i=0; i< possibilities.length; i++){
                if(possibilities[i].length()<len){
                    len = possibilities[i].length();
                }
            }
        }
        return len;
    }
*/