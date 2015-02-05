/*
 * Delegated agent from server to communicate with one connected client
 */
package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Parag Anand Guruji
 */
public class ClientHandler extends Thread{
    
    private ServerUI ui;
    private String rollNo;
    private Socket workingSocket;
    private String[] questionPaper;
    private String[] answerPaper;
    private boolean goodToGo;

    public ClientHandler(ServerUI serverUI, Socket workingSocket){
        this.ui=serverUI;
        this.workingSocket = workingSocket;
        this.questionPaper = serverUI.getQuestions().toArray(new String[1]);
        
        goodToGo=false;
        
        if(questionPaper==null || workingSocket==null || ui.getTestTime()<=0){
        }else if(questionPaper.length==0 || workingSocket.isClosed() || !workingSocket.isConnected()){
        }else{
            
            goodToGo=true;
            rollNo = "";
            answerPaper=new String[questionPaper.length];
        }
    }

    /**
     * @return the ui
     */
    public ServerUI getUi() {
        return ui;
    }

    /**
     * @param ui the ui to set
     */
    public void setUi(ServerUI ui) {
        this.ui = ui;
    }

    /**
     * @return the rollNo
     */
    public String getRollNo() {
        return rollNo;
    }

    /**
     * @param rollNo the rollNo to set
     */
    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    /**
     * @return the workingSocket
     */
    public Socket getWorkingSocket() {
        return workingSocket;
    }

    /**
     * @param workingSocket the workingSocket to set
     */
    public void setWorkingSocket(Socket workingSocket) {
        this.workingSocket = workingSocket;
    }

    /**
     * @return the questionPaper
     */
    public String[] getQuestionPaper() {
        return questionPaper;
    }

    /**
     * @param questionPaper the questionPaper to set
     */
    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setQuestionPaper(String[] questionPaper) {
        this.questionPaper = questionPaper;
    }

    /**
     * @return the answerPaper
     */
    public String[] getAnswerPaper() {
        return answerPaper;
    }

    /**
     * @param answerPaper the answerPaper to set
     */
    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setAnswerPaper(String[] answerPaper) {
        this.answerPaper = answerPaper;
    }

    /**
     * @return the goodToGo
     */
    public boolean isGoodToGo() {
        return goodToGo;
    }

    /**
     * @param goodToGo the goodToGo to set
     */
    public void setGoodToGo(boolean goodToGo) {
        this.goodToGo = goodToGo;
    }
    
    @Override
    public void run(){
        
        if(!isGoodToGo()){
            return;
        }
        
        ObjectInputStream ois;
        ObjectOutputStream oos;
 
        try {
            ois = new ObjectInputStream(getWorkingSocket().getInputStream());
        } catch (IOException ex) {
            cleanup();return;
        }
        try {
            setRollNo(ois.readUTF());
        } catch (IOException ex) {
            cleanup();return;
        }
        try {
            oos = new ObjectOutputStream(getWorkingSocket().getOutputStream());
        } catch (IOException ex) {
            cleanup();return;
        }
        try{
            if(registerNewStudent(getRollNo())){
            }else{
                cleanup();return;
            }
        }catch(RollNoDuplicationException rex){
            try {
                oos.writeInt(-1);
                oos.flush();
                justShutIt();return;
            } catch (IOException ex) {

            }
        }

        getUi().getConnectedClients().put(getRollNo(), this);
        getUi().addToJComboBoxRollNo(getRollNo());
        try {
            oos.writeInt(getUi().getTestTime());
        } catch (IOException ex) {
            cleanup();return;
        }
        try {
            oos.writeInt(getQuestionPaper().length);
        } catch (IOException ex) {
            cleanup();return;
        }
        for(int i=0; i<getQuestionPaper().length;i++){
            try {
                oos.writeUTF(getQuestionPaper()[i]);
            } catch (IOException ex) {
                cleanup();return;
            }
        }
        try {
            oos.flush();
        } catch (IOException ex) {
            cleanup();return;
        }

        for(int i=0; i<getAnswerPaper().length;i++){
            try {
                getAnswerPaper()[i] = ois.readUTF();
            } catch (IOException ex) {
                cleanup();return;
            }
        }
        try {
            oos.close();
        } catch (IOException ex) {
            cleanup();return;
        }
        try {
            ois.close();
        } catch (IOException ex) {
            cleanup();return;
        }        
        complete();
    }

    private boolean registerNewStudent(String rollNo) throws RollNoDuplicationException{
        return DatabaseManager.insertNewStudentOnConnection(rollNo, getWorkingSocket().getInetAddress().getHostName());
    }

    private void cleanup() {
        try {
            getWorkingSocket().close();
            getUi().getConnectedClients().remove(getRollNo());
            DatabaseManager.rollBack(this);
        } catch (IOException ex) {
        }
    }

    private void complete() {
        try {
            getWorkingSocket().close();
            getUi().getConnectedClients().remove(getRollNo());
            DatabaseManager.storeSubmittedAnswers(this);
            System.gc();
        } catch (IOException ex) {
        }
    }

    private void justShutIt() {
        try {
            getWorkingSocket().close();
        } catch (IOException ex) {
        }
    }
}
