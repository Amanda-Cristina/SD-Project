package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gilson
 */
public class TCPUser {
    public Socket userSocket;
    public PrintWriter output;
    public BufferedReader input;
    
    public TCPUser(){
        
    }
    
    public void connect(String ip, int port) throws IOException{
        this.userSocket = new Socket(ip, port);
        output = new PrintWriter(this.userSocket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(this.userSocket.getInputStream()));
    }
    
    public JSONObject sendMessage(JSONObject msg_json) throws IOException, JSONException{
        this.output.print(msg_json.toString());
        this.output.flush();
        System.out.println("Message sent to "+ this.userSocket.getInetAddress().getHostAddress() +" = " + msg_json);
        char[] cbuf = new char[2048];
        input.read(cbuf);
        JSONObject reply = new JSONObject(String.valueOf(cbuf));
        return reply;
    }
}
