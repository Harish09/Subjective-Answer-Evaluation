/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;



/**
 *
 * @author Parag Anand Guruji
 */



class ServerUICreator implements Runnable
{
    MainClass caller;
    public ServerUICreator(MainClass caller) {
        this.caller=caller;
    }

    @Override
    public void run() {
        caller.ui = new ServerUI(caller);
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

    ServerUI ui;
    ServerUICreator uiCreator;

    ConfigurationWindow configurationWindow;
    ConfigurationWindowCreator configurationWindowCreator;

    int serverPort;
    int backlog;

    String driverName;
    String url;
    String dbName;
    String userName;
    String password;


    public MainClass() {

        serverPort = 7000;
        backlog = 100;
        driverName = "com.mysql.jdbc.driver";
        url = "jdbc:mysql://localhost:3306/";
        dbName = "testdb";
        userName = "root";
        password = "root";

        uiCreator = new ServerUICreator(this);
        configurationWindowCreator = new ConfigurationWindowCreator(this);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }

        createConfigurationWindow();
    }

    public final void createUI(int serverPort, int backlog, String driverName, String url, String dbName, String userName, String password ) {

        this.serverPort=serverPort;
        this.backlog=backlog;
        this.driverName = driverName;
        this.url=url;
        this.dbName=dbName;
        this.userName=userName;
        this.password=password;

        configurationWindow.dispose();

        DatabaseManager.configure(this);

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
