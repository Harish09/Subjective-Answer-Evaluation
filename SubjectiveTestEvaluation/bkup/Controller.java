/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subjectivetestevaluation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import matrix.Matrix;
import matrix.SingularValueDecomposition;

/**
 *
 * @author Parag Anand Guruji
 */
public class Controller {
    protected static int rnoRange=100;
    private static TrainingSet trainingSet=null;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            trainingSet = train();
            //evaluate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static TrainingSet train() throws SQLException{

        System.setProperty("wordnet.database.dir", "C:\\Program Files\\WordNet\\2.1\\dict\\");
        DatabaseManager.clearAllExceptTestData();

        // Step-1: Preprocessing
        Integer[] rnos;
        rnos = DatabaseManager.getRollNumbersWhoSubmittedAnswerOfQueNo(1);
        DatabaseManager.clearAllExceptTestData();
        Preprocessor.preProcessTrainingEssays(1, rnos);

        //Step-2: Ngram Creation
        DatabaseManager.populateAllngrams();

        //Step-3: TFIDF Calculation and Filling NgramByDocMatrix
        DatabaseManager.populateNgramByDocMatrix();

        //Step-4: SVD Calculations
        createSVDAndDjDash();

        return generateTrainingSet();
    }

    public static void createSVDAndDjDash() throws SQLException{
        SingularValueDecomposition svd;
        Matrix djDash;
        double a[][] = getNgramByDocMatrixAsDoubleArrayFromDatabase();

        Matrix ngramByDocMatrix = new Matrix(a);
        svd = ngramByDocMatrix.svd();
        Matrix djTranspose = ngramByDocMatrix.transpose();
        Matrix U  = svd.getU();
        Matrix S  = svd.getS();
        //Matrix V  = svd.getV();

        double[][] djDashArray = new double[djTranspose.getRowDimension()][djTranspose.getRowDimension()];
        double[][] documentVector = new double[1][djTranspose.getColumnDimension()];

        for(int i=0; i<djDashArray.length; i++){
            for(int j=0; j<documentVector[0].length; j++){
                documentVector[0][j] = djTranspose.getArray()[i][j];
            }
            djDashArray[i] = ((new Matrix(documentVector)).times(U).times(S.inverseSingular())).getRowPackedCopy();
        }

        djDash    = new Matrix(djDashArray).transpose();//djTranspose.times(U).times(S.inverseSingular());

        DatabaseManager.populateMatrix("matrixdjdash", djDash);
        DatabaseManager.populateMatrix("matrixu", U);
        DatabaseManager.populateMatrix("matrixs", S);

            //System.out.println("S :");
            S.print(30, 20);
            //System.out.println("Dj' :");
            djDash.print(30, 20);


    }

    private static double[][] getNgramByDocMatrixAsDoubleArrayFromDatabase() throws SQLException{

        ResultSet rs;

        rs = DatabaseManager.getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT docid) FROM ngrambydocmatrix").executeQuery();
        rs.next();
        int docCount=rs.getInt(1);

        rs = DatabaseManager.getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT ngramid) FROM ngrambydocmatrix").executeQuery();
        rs.next();
        int ngramCount=rs.getInt(1);

        rs = DatabaseManager.getConnectionObject().prepareStatement("SELECT tfidf FROM ngrambydocmatrix ORDER BY ngramid, docid;").executeQuery();

        double[][] ngramByDocMatrix = new double[ngramCount][docCount];

        for(int i=0; i<ngramCount; i++){
            for(int j=0; j<docCount && rs.next(); j++){
                ngramByDocMatrix[i][j] = rs.getDouble(1);
                ////System.out.println("DjDashMatrix["+i+"]["+j+"] : "+DjDashMatrix[i][j]);
            }
        }

        rs = DatabaseManager.getConnectionObject().prepareStatement("SELECT DISTINCT docid FROM ngrambydocmatrix ORDER BY ngramid, docid;").executeQuery();
        for(int j=0; j<docCount && rs.next(); j++){
            DatabaseManager.getConnectionObject().prepareStatement("UPDATE answers SET columnindex = "+j+" WHERE docid="+rs.getInt(1) +";").executeUpdate();
            ////System.out.println("DjDashMatrix["+i+"]["+j+"] : "+DjDashMatrix[i][j]);
        }

        return ngramByDocMatrix;
    }

    private static double[][] getDjDashMatrixAsDoubleArrayFromDatabase() throws SQLException{

        ResultSet rs;

        rs = DatabaseManager.getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT columnid) FROM matrixdjdash").executeQuery();
        rs.next();
        int columnCount=rs.getInt(1);

        rs = DatabaseManager.getConnectionObject().prepareStatement("SELECT COUNT(DISTINCT rowid) FROM matrixdjdash").executeQuery();
        rs.next();
        int rowCount=rs.getInt(1);

        rs = DatabaseManager.getConnectionObject().prepareStatement("SELECT cellvalue FROM matrixdjdash ORDER BY rowid, columnid;").executeQuery();

        double[][] DjDashMatrix = new double[rowCount][columnCount];

        for(int i=0; i<rowCount; i++){
            for(int j=0; j<columnCount && rs.next(); j++){
                DjDashMatrix[i][j] = rs.getDouble(1);
                ////System.out.println("DjDashMatrix["+i+"]["+j+"] : "+DjDashMatrix[i][j]);
            }
        }
        return DjDashMatrix;
    }

    private static void getDocIdsAndGradesFromDatabase(int[] docIds, int[] humanGrades) throws SQLException {
        ResultSet rs = DatabaseManager.getConnectionObject().prepareStatement("SELECT docid, grade FROM answers ORDER BY columnindex").executeQuery();
        for(int j=0; j<docIds.length && rs.next(); j++){
            docIds[j] = rs.getInt(1);
            humanGrades[j] = rs.getInt(2);
        }
    }

    private static TrainingSet generateTrainingSet() throws SQLException {
        ResultSet rs = DatabaseManager.getConnectionObject().prepareStatement("SELECT DISTINCT docid FROM answers ORDER BY columnindex").executeQuery();
        int count=0;
        while(rs.next()){
            count++;
        }
        double[][] DjDash = getDjDashMatrixAsDoubleArrayFromDatabase();
        int[] docIds = new int[count], humanGrades = new int[count];
        getDocIdsAndGradesFromDatabase(docIds, humanGrades);
        return new TrainingSet(docIds, humanGrades, DjDash);
    }

    private static void evaluate() throws SQLException {

        int qno=1, rno;//=Integer.parseInt(JOptionPane.showInputDialog("Input Roll No. To Be Evaluated:\n"));
        Integer[] rnos = DatabaseManager.getRollNumbersToBeEvaluatedForQueNo(qno);
        int queryid;
        double score=0;

        for(int i=0; i<rnos.length; i++){
            rno = rnos[i].intValue();
            queryid = ((qno-1)*rnoRange)+rno;


            DatabaseManager.getConnectionObject().prepareStatement("UPDATE testanswers SET grade=NULL WHERE rno="+rno+" AND qno="+qno+";").executeUpdate();
            DatabaseManager.getConnectionObject().prepareStatement("DELETE FROM ngrambyquerymatrix WHERE queryid = "+queryid+";").executeUpdate();
            DatabaseManager.getConnectionObject().prepareStatement("DELETE FROM cosinesimilarities WHERE qno = "+qno+" AND rno = "+rno+";").executeUpdate();

            //System.out.println("query ID = "+queryid);
            String[] queryWords = Preprocessor.preprocessAQuery(qno, rno);
            /*for(int i=0; i<queryWords.length; i++){
                //System.out.println("query word "+(i+1)+"= "+queryWords[i]);
            }*/
            DatabaseManager.populateNgramByQueryMatrix(queryid, queryWords);
            double[] queryVector=getQueryVectorFromNgramByQueryMatrix(queryid);
            if(trainingSet==null){
                trainingSet=generateTrainingSet();
            }
            score = trainingSet.getScore(queryVector, rno, qno);
            DatabaseManager.insertGradeToTestAnswers(qno, rno, score);
            //JOptionPane.showMessageDialog(null, "Roll No.:"+rno+"\nScore: "+score);
            System.out.println("Roll No.:"+rno+"\nScore: "+score+"\n");
        }
    }

    private static double[] getQueryVectorFromNgramByQueryMatrix(int queryid) throws SQLException {
        ResultSet rs = DatabaseManager.getConnectionObject().prepareStatement("SELECT COUNT(ngramid) FROM ngrambyquerymatrix WHERE queryid="+queryid+";").executeQuery();
        int ngramCount=0;
        if(rs.next()){
            ngramCount = rs.getInt(1);
        }
        double[][] q = new double[ngramCount][1];
        rs = DatabaseManager.getConnectionObject().prepareStatement("SELECT tfidf FROM ngrambyquerymatrix WHERE queryid="+queryid+" ORDER BY ngramid;").executeQuery();
        int i=0;
        while(rs.next()){
            q[i][0]=rs.getDouble(1);
            i++;
        }
        Matrix Q = new Matrix(q).transpose();
        Matrix U = new Matrix(DatabaseManager.getMatrix("matrixu"));
        Matrix S = new Matrix(DatabaseManager.getMatrix("matrixs"));
        Matrix qDash = Q.times(U).times(S.inverseSingular());
/*
        //System.out.println("Dimensions of qDash: row="+qDash.getRowDimension()+" column="+qDash.getColumnDimension());
*/
        double[] retVal = new double[qDash.getColumnDimension()];
        for(int j=0; j<qDash.getColumnDimension();j++){
            retVal[j] = qDash.getArray()[0][j];
        }
        return retVal;
    }
    
    public static void applyReliabilityMeasure(){
        double error[]=null;
        double sd=0.0;
        double mean_error=0.0;
        ResultSet rs=null;
        double x = 0.0;
        int i=0,n=0;
        try {
            rs = DatabaseManager.getConnectionObject().prepareStatement("select grade,human_grade from testanswers").executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            while(rs.next()){
                error[i]=rs.getDouble(1)-rs.getDouble(2);
                mean_error+=error[i]; 
                i++;
            }
            mean_error=mean_error/i;
            n=i;
            while(i>0)
            {
                i--;
                x+=(error[i]-mean_error)*(error[i]-mean_error);
                
            }
            sd = Math.sqrt(x/n);
        } catch (SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}


/*
    private static void evaluateTemp() throws SQLException {
        int qno=1, rno=Integer.parseInt(JOptionPane.showInputDialog("Input Roll No. To Be Evaluated:\n"));
        int queryid = ((qno-1)*rnoRange)+rno;
        double score=0;

        //System.out.println("query ID = "+queryid);
        String[] queryWords = Preprocessor.preprocessADocument(qno, rno);
        /*for(int i=0; i<queryWords.length; i++){
            //System.out.println("query word "+(i+1)+"= "+queryWords[i]);
        }*//*
        DatabaseManager.populateNgramByQueryMatrix(queryid, queryWords);
        double[] queryVector=getQueryVectorFromNgramByQueryMatrix(queryid);
        if(trainingSet==null){
            trainingSet=generateTrainingSet();
        }
        score = trainingSet.getScore(queryVector);
        JOptionPane.showMessageDialog(null, "The Score: "+score);
    }
*/
