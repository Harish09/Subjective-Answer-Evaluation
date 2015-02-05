/*
 * Carries out all interaction with database using single connection and operation-specific static methods.
 */
package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

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

    public static void configure(MainClass mainClass){
        driverName = mainClass.driverName;
        url = mainClass.url;
        dbName = mainClass.dbName;
        userName = mainClass.userName;
        password = mainClass.password;
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
        return connection;
    }

    static boolean insertNewStudentOnConnection(String rollNo, String ip) throws RollNoDuplicationException{
        PreparedStatement ps;
        ResultSet rs;
        try {
            rs = retrieve("select * from student where rno='"+rollNo+"'");
            if(rs.next()){
                throw new RollNoDuplicationException(rollNo);
            }else{
                try{
                    ps = getConnectionObject().prepareStatement("insert into testdb.student(rno, connection_status, test_status, ip_port) values('"+rollNo+"', 'CONNECTED' , 'ON', '"+ip+"')");
                    ps.executeUpdate();
                }catch(SQLException ex){
                    return false;
                }
            }
        } catch (SQLException ex) {
            return false;
        }
        return true;
    }


    @SuppressWarnings("CallToThreadDumpStack")
    static void disconnectStudent(String rollNo) {
        PreparedStatement ps;
        ResultSet rs;
        try {
            rs = retrieve("select * from student where rno='"+rollNo+"'");
            if(rs.next()){
                try {
                    ps = getConnectionObject().prepareStatement("update testdb.student set connection_status='DISCONNECTED', test_status='OFF' where rno='"+rollNo+"'");
                    ps.executeUpdate();
                } catch (SQLException ex) {
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("CallToThreadDumpStack")
    static void storeSubmittedAnswers(ClientHandler handler) {
        PreparedStatement ps;
        ResultSet rs;
        String rollNo = handler.getRollNo();
        try {
            rs = retrieve("select * from student where rno='"+rollNo+"'");
            if(rs.next()){
                disconnectStudent(rollNo);
                try {
                    ps = getConnectionObject().prepareStatement("insert into answers (rno, qno, answer) values ('"+rollNo+"', ?, ?)");
                    for(int i=0; i<handler.getQuestionPaper().length; i++){
                        ps.setInt(1, i+1);
                        ps.setString(2, handler.getAnswerPaper()[i]);
                        ps.executeUpdate();
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static ResultSet retrieve(String statement) throws SQLException{
        PreparedStatement ps;
        ps = getConnectionObject().prepareStatement(statement);
        return ps.executeQuery(statement);
    }

    public static void clearAll() throws SQLException {

        PreparedStatement ps;

        ps = getConnectionObject().prepareStatement("delete from student");
        ps.executeUpdate();

        ps = getConnectionObject().prepareStatement("delete from question_paper");
        ps.executeUpdate();

        ps = getConnectionObject().prepareStatement("delete from answers");
        ps.executeUpdate();
    }

    static void insertQuestionPaper(ArrayList<String> questions) {

        PreparedStatement ps;
        try {
            ps = getConnectionObject().prepareStatement("insert into testdb.question_paper (qno, question) values (?, ?)");
            for(int i=0; i<questions.size(); i++){
                ps.setInt(1, i+1);
                ps.setString(2, questions.get(i));
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("CallToThreadDumpStack")
    static String getAnswer(String rno, int qno) {
        ResultSet rs;
        try {
            rs = retrieve("select answers.answer from answers where answers.rno='"+rno+"' AND answers.qno="+qno);
            if(rs.next()){
                return rs.getString(1);
            }else{
                return "*********** Answer Not Available ************";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "ERROR IN RETREIVAL OF THE ANSWER FROM THE DATABASE";
        }
    }

    @SuppressWarnings("CallToThreadDumpStack")
    static String getGrade(String rno, int qno) {
        ResultSet rs;
        int grade=-1;
        try {
            rs = retrieve("select grade from answers where rno='"+rno+"' AND qno="+qno);
            if(rs.next()){
                grade = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if(grade==-1){
            return "";
        }else{
            return ""+grade;
        }
    }

    @SuppressWarnings("CallToThreadDumpStack")
    static void updateGrade(String rno, int qno, int grade) {
        PreparedStatement ps;
        try {
            ps = getConnectionObject().prepareStatement("update testdb.answers set grade="+grade+" where answers.rno='"+rno+"' AND answers.qno="+qno);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("CallToThreadDumpStack")
    static void rollBack(ClientHandler handler) {
        PreparedStatement ps;
        String rollNo = handler.getRollNo();
        try {
            ps = getConnectionObject().prepareStatement("delete from student where rno="+rollNo);
            ps.executeUpdate();

            ps = getConnectionObject().prepareStatement("delete from answers where rno="+rollNo);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
