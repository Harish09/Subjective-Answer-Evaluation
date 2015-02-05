/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subjectivetestevaluation;

import java.io.Serializable;
import java.sql.SQLException;
import matrix.Matrix;

/**
 *
 * @author Parag Anand Guruji
 */
public class TrainingSet implements Serializable{

    int [] docIds, humanGrades;
    Matrix djDash;

    public TrainingSet(int[] docIds, int[] humanGrades, double[][] djDash) {
        this.docIds = docIds;
        this.humanGrades = humanGrades;
        this.djDash = new Matrix(djDash);
    }

    public double[] getDjDashVector(int qno, int rno){
        return getDjDashVector(((qno-1)*Controller.rnoRange)+rno);
    }

    public double[] getDjDashVector(int docId) {
        int coulumnIndex = getIndexInDocIds(docId);
        int rowCount = djDash.getRowDimension();
        double[] retVal = new double[rowCount];
        for(int i=0; i<rowCount; i++){
            retVal[i]=djDash.getArray()[i][coulumnIndex];
        }
        return retVal;
    }

    public int getHumanGrade(int qno, int rno){
        
        return getHumanGrade(((qno-1)*Controller.rnoRange)+rno);
    
    }

    public int getHumanGrade(int docId) {
        return humanGrades[getIndexInDocIds(docId)];
    }

    private int getIndexInDocIds(int docId) {
        for(int i=0; i< docIds.length; i++){
            if(docIds[i]==docId){
                return i;
            }
        }
        return -1;
    }

    double getScore(double[] queryVector, int rno, int qno) {
        double cosineSimilarity=0.0, temp;
        int targetIndex=0;
        for(int i=0; i<docIds.length; i++){
            temp = computeCosineSimilarity(queryVector, getDjDashVector(docIds[i]), docIds.length);
            //System.out.println("Cosine Similarity of Answer by roll no. "+rno+" with Training Essay "+i+" = "+temp);
            try{
                DatabaseManager.getConnectionObject().prepareStatement("INSERT INTO cosinesimilarities(rno, qno, training_essay_no, cosine_similarity) VALUES("+rno+", "+qno+", "+(i+1)+", "+temp+");").executeUpdate();
            }catch(SQLException ex){
                ex.printStackTrace();
            }
            if(temp > cosineSimilarity){
                cosineSimilarity = temp;
                targetIndex=i;
            }
        }
        return cosineSimilarity*humanGrades[targetIndex];
    }

    private double computeCosineSimilarity(double[] queryVector, double[] djDashVector, int dimension) {
        /*int count=0;
        for(int i=0; i<dimension; i++){
            if(queryVector[i]==djDashVector[i]){
                count++;
            }
        }
        return count/dimension;*/
        double dotProduct = 0.0, magnitude1= 0.0, magnitude2 = 0.0, cosineSimilarity = 0.0;

        for(int i=0; i<dimension; i++){
            dotProduct += queryVector[i]*djDashVector[i];
            magnitude1 += Math.pow(queryVector[i], 2.0);
            magnitude2 += Math.pow(djDashVector[i], 2.0);
        }

        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        if(magnitude1 != 0.0 || magnitude2 != 0.0){
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        }else{
            cosineSimilarity = 0.0;
        }
        return cosineSimilarity;
    }
}
