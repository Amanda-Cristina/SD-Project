package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import main.ClientView;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gilson
 */
public class TCPUser{
    public Socket serverSocket;
    public PrintWriter output;
    public BufferedReader input;
    private TCPUserThread tCPUserThread;
    private ClientView clientView;
    
    public TCPUser(ClientView clientView){
        this.clientView = clientView;
    }
    
    public void connect(String ip, int port) throws IOException{
        this.serverSocket = new Socket(ip, port);
        output = new PrintWriter(this.serverSocket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream()));
        this.tCPUserThread = new TCPUserThread(serverSocket, output, input, this.clientView);
        this.tCPUserThread.start();
    }
    
    public void sendMessage(JSONObject msg_json) throws IOException, JSONException{
        this.tCPUserThread.sendMessage(msg_json);
    }
    
    /*public JSONObject sendMessage(JSONObject msg_json) throws IOException, JSONException{
        this.output.print(msg_json.toString());
        this.output.flush();
        System.out.println("Message sent to "+ this.serverSocket.getInetAddress().getHostAddress() + ":" + this.serverSocket.getPort() + " = " + msg_json);
        char[] cbuf = new char[2048];
        input.read(cbuf);
        JSONObject reply = new JSONObject(String.valueOf(cbuf));
        System.out.println("Message received from "+ this.serverSocket.getInetAddress().getHostAddress() + ":" + this.serverSocket.getPort() + " = " + msg_json);
        return reply;
    }*/
}
