/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import javax.swing.UIManager;



/**
 *
 * @author Parag Anand Guruji
 */



class ClientUICreator implements Runnable
{
    MainClass caller;
    public ClientUICreator(MainClass caller) {
        this.caller=caller;
    }

    @Override
    public void run() {
        caller.ui = new ClientUI(caller);
        caller.ui.setVisible(true);
    }
}


class ConfigurationWindowCreator implements Runnable
{
    MainClass caller;
    public ConfigurationWindowCreator(MainClass caller) {
        this.caller=caller;
    }

    @Override
    public void run() {
        caller.configurationWindow = new ConfigurationWindow(caller);
        caller.configurationWindow.setVisible(true);
    }
}


public class MainClass {

    ClientUI ui;
    ClientUICreator uiCreator;

    ConfigurationWindow configurationWindow;
    ConfigurationWindowCreator configurationWindowCreator;
    String serverIP;
    int serverPort;
    int clientPort;

    public MainClass() {

        uiCreator = new ClientUICreator(this);
        configurationWindowCreator = new ConfigurationWindowCreator(this);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }

        createConfigurationWindow();
    }

    public final void createUI(String ip, int serverPort, int clientPort) {

        this.serverIP=ip;
        this.serverPort=serverPort;
        this.clientPort=clientPort;

        configurationWindow.dispose();

        java.awt.EventQueue.invokeLater(uiCreator);
    }

    public final void createConfigurationWindow() {
        java.awt.EventQueue.invokeLater(configurationWindowCreator);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args){
        new MainClass();
    }

}
