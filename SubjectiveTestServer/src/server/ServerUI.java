/*
 * The main class of Server. Gives UI for all server functionalities and implements them.
 */
package server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author Parag Anand Guruji
 */
public class ServerUI extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private ArrayList<String> questions;
    private ServerCommunicator communicator;
    private int testTime;
    private HashMap<String, ClientHandler> connectedClients;
    MainClass caller;
    /**
     * Creates new form ServerUI
     */
    public ServerUI(MainClass caller) {

        this.caller = caller;
        questions = new ArrayList<String>();
        testTime = 0;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }

        initComponents();
        jTextAreaQuestionPaper.setEditable(false);
        jTextAreaStudentAnswer.setEditable(false);
        jComboBoxRollNo.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"<Select>"}));
        jComboBoxQuestionNo.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"<Select>"}));
        jComboBoxRollNo.setEnabled(false);
        jTextAreaStudentAnswer.setEnabled(false);
        jComboBoxQuestionNo.setEnabled(false);
        jTextFieldGrade.setEnabled(false);
        jButtonSaveScore.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaQuestionPaper = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jButtonAddQuestion = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaNewQuestion = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxRollNo = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jButtonSaveScore = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaStudentAnswer = new javax.swing.JTextArea();
        jTextFieldGrade = new javax.swing.JTextField();
        jButtonStartTest = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldTotalTime = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jComboBoxQuestionNo = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Test Server Portal");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Century Gothic", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 102));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Evaluation");

        jTextAreaQuestionPaper.setColumns(20);
        jTextAreaQuestionPaper.setLineWrap(true);
        jTextAreaQuestionPaper.setRows(5);
        jScrollPane1.setViewportView(jTextAreaQuestionPaper);

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel2.setText("Question :");

        jButtonAddQuestion.setText("Add Question");
        jButtonAddQuestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddQuestionActionPerformed(evt);
            }
        });
        jButtonAddQuestion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButtonAddQuestionKeyPressed(evt);
            }
        });

        jTextAreaNewQuestion.setColumns(20);
        jTextAreaNewQuestion.setLineWrap(true);
        jTextAreaNewQuestion.setRows(5);
        jScrollPane2.setViewportView(jTextAreaNewQuestion);

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel3.setText("Question # :");

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel4.setText("Roll # :");

        jComboBoxRollNo.setOpaque(false);
        jComboBoxRollNo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxRollNoItemStateChanged(evt);
            }
        });
        jComboBoxRollNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxRollNoActionPerformed(evt);
            }
        });
        jComboBoxRollNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jComboBoxRollNoFocusLost(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel5.setText("Grade (out of 10) :");

        jButtonSaveScore.setText("Save Score");
        jButtonSaveScore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveScoreActionPerformed(evt);
            }
        });

        jTextAreaStudentAnswer.setColumns(20);
        jTextAreaStudentAnswer.setLineWrap(true);
        jTextAreaStudentAnswer.setRows(5);
        jScrollPane3.setViewportView(jTextAreaStudentAnswer);

        jButtonStartTest.setText("Start Test");
        jButtonStartTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartTestActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel6.setText("Total Time (minutes) :");

        jLabel8.setFont(new java.awt.Font("Century Gothic", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 102));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Subjective Answers Test - Server");

        jComboBoxQuestionNo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxQuestionNoItemStateChanged(evt);
            }
        });
        jComboBoxQuestionNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jComboBoxQuestionNoFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(2, 2, 2)
                                        .addComponent(jButtonAddQuestion, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTextFieldTotalTime, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButtonStartTest, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane1)))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 848, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 810, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBoxRollNo, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBoxQuestionNo, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextFieldGrade, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButtonSaveScore, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(jButtonAddQuestion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldTotalTime, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonStartTest))
                .addGap(4, 4, 4)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(jTextFieldGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxRollNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonSaveScore)
                        .addComponent(jLabel3)
                        .addComponent(jComboBoxQuestionNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAddQuestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddQuestionActionPerformed
        // TODO add your handling code here:
        if(jTextAreaNewQuestion.getText().equals("")){
        }else{
            int n = getQuestions()==null?1:getQuestions().size()+1;
            getQuestions().add("Q. "+n+":"+jTextAreaNewQuestion.getText()+"\n\n");
            jTextAreaQuestionPaper.setText(jTextAreaQuestionPaper.getText()+getQuestions().get(getQuestions().size()-1));
            jTextAreaNewQuestion.setText("");
        }
    }//GEN-LAST:event_jButtonAddQuestionActionPerformed

    private void jButtonStartTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartTestActionPerformed
        // TODO add your handling code here:
        try{
            setTestTime(Integer.parseInt(jTextFieldTotalTime.getText()));
        }catch(NumberFormatException nex){
        }
        if(getTestTime()==0){
            JOptionPane.showMessageDialog(this, "Test Time Not Acceptable!\n\nPlease enter a valid integer value and retry.");
        }else{

            jButtonAddQuestion.setEnabled(false);
            jTextAreaNewQuestion.setEnabled(false);
            jTextFieldTotalTime.setEnabled(false);
            jButtonStartTest.setEnabled(false);

            jComboBoxRollNo.setEnabled(true);
            for(int i=0; i<getQuestions().size(); i++){
                jComboBoxQuestionNo.addItem(i+1);
            }
            /*try {
                DatabaseManager.clearAll();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error in Clearing previous database. \nPlease Clear it and retry.\n\nExiting! :(");
                System.exit(-1);
            }*/
            DatabaseManager.insertQuestionPaper(getQuestions());
            startCommunication();
        }
    }//GEN-LAST:event_jButtonStartTestActionPerformed

    private void jComboBoxRollNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxRollNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxRollNoActionPerformed

    private void jComboBoxRollNoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxRollNoItemStateChanged
        try {
            // TODO add your handling code here:
            jComboBoxQuestionNo.setSelectedIndex(0);
            jTextFieldGrade.setText("");

            ResultSet rs = DatabaseManager.retrieve("select test_status from student where student.rno='"+jComboBoxRollNo.getSelectedItem().toString()+"'");
            if(rs.next()){
                if(rs.getString(1).equals("ON")){
                jTextAreaStudentAnswer.setText("********************** Answers not submitted yet ********************");
                disAllowGrading();
                }else{
                    allowGrading();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServerUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jComboBoxRollNoItemStateChanged

    private void jComboBoxQuestionNoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxQuestionNoItemStateChanged
        // TODO add your handling code here:
        int questionNo = 0;
        try{
            questionNo=Integer.parseInt(jComboBoxQuestionNo.getSelectedItem().toString());
        }catch(NumberFormatException nex){

        }
        String rno= ""+jComboBoxRollNo.getSelectedItem();
        jTextAreaStudentAnswer.setText(DatabaseManager.getAnswer(rno, questionNo));
        jTextFieldGrade.setText(DatabaseManager.getGrade(rno, questionNo));
    }//GEN-LAST:event_jComboBoxQuestionNoItemStateChanged

    private void jButtonSaveScoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveScoreActionPerformed
        // TODO add your handling code here:
        int grade = -1;
        int questionNo = 0;
        try{
            questionNo=Integer.parseInt(jComboBoxQuestionNo.getSelectedItem().toString());
        }catch(NumberFormatException nex){

        }

        try{
            grade=Integer.parseInt(jTextFieldGrade.getText());
            if(grade<0 || grade>10){
                JOptionPane.showMessageDialog(this, "Invalid Grade ("+grade+"). Please enter value from 0 to 10.");
                jTextFieldGrade.setText("");
            }else{
                DatabaseManager.updateGrade(jComboBoxRollNo.getSelectedItem().toString(), questionNo, grade);
            }
        }catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Invalid Grade ("+grade+"). Please enter value from 0 to 10.");
            jTextFieldGrade.setText("");
        }
    }//GEN-LAST:event_jButtonSaveScoreActionPerformed

    private void jComboBoxRollNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jComboBoxRollNoFocusLost
        // TODO add your handling code here:
        jComboBoxQuestionNo.setSelectedIndex(0);
        jTextFieldGrade.setText("");
    }//GEN-LAST:event_jComboBoxRollNoFocusLost

    private void jComboBoxQuestionNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jComboBoxQuestionNoFocusLost
        // TODO add your handling code here:
        int  qno = 0;
        try{
            qno = Integer.parseInt(jComboBoxQuestionNo.getSelectedItem().toString());
        }catch(NumberFormatException nex){
        }
        jTextFieldGrade.setText(DatabaseManager.getGrade(jComboBoxRollNo.getSelectedItem().toString(), qno));
        jTextAreaStudentAnswer.setText(DatabaseManager.getAnswer(jComboBoxRollNo.getSelectedItem().toString(), qno));
    }//GEN-LAST:event_jComboBoxQuestionNoFocusLost

    private void jButtonAddQuestionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButtonAddQuestionKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonAddQuestionKeyPressed

    private void refreshQuestionPaper() {
        if(getQuestions()==null){
            jTextAreaQuestionPaper.setText("");
        }else{
            for(int i=0;i<getQuestions().size();i++){
                jTextAreaQuestionPaper.setText(jTextAreaQuestionPaper.getText()+getQuestions().get(i));
            }
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ServerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ServerUI(new MainClass()).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddQuestion;
    private javax.swing.JButton jButtonSaveScore;
    private javax.swing.JButton jButtonStartTest;
    private javax.swing.JComboBox jComboBoxQuestionNo;
    private javax.swing.JComboBox jComboBoxRollNo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextAreaNewQuestion;
    private javax.swing.JTextArea jTextAreaQuestionPaper;
    private javax.swing.JTextArea jTextAreaStudentAnswer;
    private javax.swing.JTextField jTextFieldGrade;
    private javax.swing.JTextField jTextFieldTotalTime;
    // End of variables declaration//GEN-END:variables

    private void startCommunication() {
        setCommunicator(new ServerCommunicator(this));
        setConnectedClients(new HashMap<String, ClientHandler>());
        getCommunicator().startCommunication();
    }

    void addToJComboBoxRollNo(String rollNo) {
        jComboBoxRollNo.addItem(rollNo);
    }

    void allowGrading(){
        jTextAreaStudentAnswer.setEnabled(true);
        jComboBoxQuestionNo.setEnabled(true);
        jTextFieldGrade.setEnabled(true);
        jButtonSaveScore.setEnabled(true);
    }

    private void disAllowGrading() {
        jTextAreaStudentAnswer.setEnabled(true);
        jComboBoxQuestionNo.setEnabled(false);
        jTextFieldGrade.setEnabled(false);
        jButtonSaveScore.setEnabled(false);
    }

    /**
     * @return the questions
     */
    public ArrayList<String> getQuestions() {
        return questions;
    }

    /**
     * @param questions the questions to set
     */
    public void setQuestions(ArrayList<String> questions) {
        this.questions = questions;
    }

    /**
     * @return the communicator
     */
    public ServerCommunicator getCommunicator() {
        return communicator;
    }

    /**
     * @param communicator the communicator to set
     */
    public void setCommunicator(ServerCommunicator communicator) {
        this.communicator = communicator;
    }

    /**
     * @return the testTime
     */
    public int getTestTime() {
        return testTime;
    }

    /**
     * @param testTime the testTime to set
     */
    public void setTestTime(int testTime) {
        this.testTime = testTime;
    }

    /**
     * @return the connectedClients
     */
    public HashMap<String, ClientHandler> getConnectedClients() {
        return connectedClients;
    }

    /**
     * @param connectedClients the connectedClients to set
     */
    public void setConnectedClients(HashMap<String, ClientHandler> connectedClients) {
        this.connectedClients = connectedClients;
    }
}
