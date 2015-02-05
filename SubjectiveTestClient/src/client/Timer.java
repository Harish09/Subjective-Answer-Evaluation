/*
 * Timer for test. Computes and displays time.
 * Expires to indicate timeup.
 */
package client;

import javax.swing.JOptionPane;

/**
 *
 * @author Parag Anand Guruji
 */
public class Timer extends Thread{
    private int time;
    private ClientUI ui;

    public Timer(int time, ClientUI ui) {
        this.time = time;
        this.ui = ui;
    }
    @Override
    public void run(){
        long start = System.currentTimeMillis(),remainingSeconds=0, runner = start, end = start+(getTime()*60000);
        while(runner!=end){

            runner=System.currentTimeMillis();
            if((end-runner)%1000==0){
                remainingSeconds=(end-runner)/1000;
            }
            getUi().updateTimerDisplay(remainingSeconds/60, remainingSeconds%60);
            /*try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        }
        JOptionPane.showMessageDialog(getUi(), "TIME UP!! Answers will now be submitted");
        getUi().sendAnswers();
    }

    /**
     * @return the time
     */
    public int getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * @return the ui
     */
    public ClientUI getUi() {
        return ui;
    }

    /**
     * @param ui the ui to set
     */
    public void setUi(ClientUI ui) {
        this.ui = ui;
    }
}
