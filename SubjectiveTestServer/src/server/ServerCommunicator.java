/*
 * This class is resposible for all connecting with clients. Listens at port 7000. backlog length = 120
 * After connecting, it creates an agent on separate thread and delagates the connection to it.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 *
 * @author Parag Anand Guruji
 */
public class ServerCommunicator implements Runnable{

    private ServerUI caller;
    private ServerSocket serverSocket;


    public ServerCommunicator(ServerUI caller) {
        this.caller                 = caller;
        this.serverSocket           = null;
    }

    public void startCommunication() {
        new Thread(this).start();
        System.out.println("Out of ServerCommunicator.startCommunication()");
    }

    @Override
    @SuppressWarnings("CallToThreadDumpStack")
    public void run() {

        try {
            setServerSocket(new ServerSocket(caller.caller.serverPort, caller.caller.backlog));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(caller, "Something went wrong :( :( Please Retry");
            System.exit(0);
        }
        Socket socket;
        while(true) {

            try {
                System.out.println("Listening at "+getServerSocket().toString()+"...");

                socket = getServerSocket().accept();
                ClientHandler clientHandler = new ClientHandler(getCaller(), socket);
                clientHandler.start();
                System.out.println("Connected to "+socket.getInetAddress()+":"+socket.getPort());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @return the caller
     */
    public ServerUI getCaller() {
        return caller;
    }

    /**
     * @param caller the caller to set
     */
    public void setCaller(ServerUI caller) {
        this.caller = caller;
    }

    /**
     * @return the serverSocket
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * @param serverSocket the serverSocket to set
     */
    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
}