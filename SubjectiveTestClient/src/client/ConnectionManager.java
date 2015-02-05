/*
 * Class responsible for establishment of connection with server.
 */
package client;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

/**
 *
 * @author Parag Anand Guruji
 */
public class ConnectionManager {

    private String hostName;
    private int port;
    private Socket workingSocket;
    private int portOfClient;

    public ConnectionManager(String hostName, int port, int clientPort) throws UnknownHostException, IOException {
        this.hostName   =   hostName;
        this.port       =   port;
        portOfClient    =   clientPort;
        workingSocket   =   null;
        while(workingSocket==null) {
            try{
                workingSocket=new Socket();//hostName, this.port, null, portOfClient++);
                workingSocket.setReuseAddress(true);
                workingSocket.connect(new InetSocketAddress(hostName, this.getPort()));//
            }catch(BindException bex){
                System.gc();
                JOptionPane.showMessageDialog(null, "caught - "+bex);
                workingSocket=null;
            }
        }
        try {
            workingSocket.setTcpNoDelay(true);
            workingSocket.setTrafficClass(0x04);
        } catch(Exception e) {
        }
    }

    public void closeFromClient() throws IOException {
        getWorkingSocket().close();
    }

    /**
     * @return the hostName
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @param hostName the hostName to set
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * @return the port
     */
    public final int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
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
     * @return the portOfClient
     */
    public int getCLIENTPORT() {
        return portOfClient;
    }

    /**
     * @param portOfClient the portOfClient to set
     */
    public void setCLIENTPORT(int CLIENTPORT) {
        this.portOfClient = CLIENTPORT;
    }

}