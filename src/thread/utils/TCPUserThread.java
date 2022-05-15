package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import main.ClientView;
import org.json.JSONException;
import org.json.JSONObject;
import thread.utils.TCPServerThread;

/**
 *
 * @author gilson
 */
public class TCPUserThread extends Thread{
    public Socket serverSocket;
    public PrintWriter output;
    public BufferedReader input;
    private ClientView clientView;
    
    public TCPUserThread(Socket serverSocket, PrintWriter output, BufferedReader input, ClientView clientView){
        this.serverSocket = serverSocket;
        this.output = output;
        this.input = input;
        this.clientView = clientView;
    }
    public void sendMessage(JSONObject msg_json) throws IOException, JSONException{
        this.output.print(msg_json.toString());
        this.output.flush();
        System.out.println("Message sent to "+ this.serverSocket.getInetAddress().getHostAddress() + ":" + this.serverSocket.getPort() + " = " + msg_json);
        //char[] cbuf = new char[2048];
        //input.read(cbuf);
        //JSONObject reply = new JSONObject(String.valueOf(cbuf));
        //System.out.println("Message received from "+ this.serverSocket.getInetAddress().getHostAddress() + ":" + this.serverSocket.getPort() + " = " + msg_json);
        //return reply;
    }
    
    public void treatPing() throws JSONException, IOException{
        JSONObject msg = new JSONObject();
        msg.put("ping", new JSONObject());
        this.sendMessage(msg);
    }
    
    public synchronized void treatLogin(JSONObject json_msg, ClientView clientView) throws JSONException{
        json_msg = (JSONObject)json_msg.get("login");
        if(json_msg.has("error")){
            JOptionPane.showMessageDialog(null, json_msg.get("error"), "Login error",
                    JOptionPane.WARNING_MESSAGE);
        }else{
            this.clientView.setUser(new User(json_msg.getString("id"), json_msg.getString("name"), json_msg.getString("cpf"), 
                                 json_msg.getString("phone"), json_msg.getString("password")));
            clientView.setLoggedUser(true);
            clientView.setHomepanelVisibility(true);
        }
    }
    
    public void treatSignup(JSONObject json_msg, ClientView clientView) throws JSONException{
        json_msg = (JSONObject)json_msg.get("register");
        if(json_msg.has("error")){
            JOptionPane.showMessageDialog(null, json_msg.get("error"), "Signup error",
                    JOptionPane.WARNING_MESSAGE);
        }else{
            clientView.setLoggedUser(true);
            clientView.setLoginpanelVisibility(true);
        }
    }
    
    public void treatLogout(JSONObject json_msg, ClientView clientView){
        if(json_msg.has("close")){
            clientView.setHomepanelVisibility(false);
            clientView.setLoginpanelVisibility(true);
        }
    }
    
    private synchronized Runnable createRunnable(final JSONObject json_msg, final ClientView clientView){
        Runnable runnable = () -> {
            String operation = json_msg.keys().next().toString();
            switch (operation) {
                case "ping" -> {
                    try {
                        treatPing();
                    } catch (JSONException | IOException ex) {
                        Logger.getLogger(TCPUserThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                case "login" -> {
                    try {
                        treatLogin(json_msg,clientView);
                    } catch (JSONException ex) {
                        Logger.getLogger(TCPUserThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                case "register" -> {
                    try{
                        treatSignup(json_msg, clientView);
                    }catch(JSONException ex){
                        Logger.getLogger(TCPUserThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                case "close" -> {
                    treatLogout(json_msg, clientView);
                }
                default -> throw new AssertionError();
            }
            return;
        };
        return runnable;
    }
    
    @Override 
    public void run(){
        char[] cbuf = new char[2048];
        try{
            while(true){
                int flag = input.read(cbuf);
                if (flag == -1 || serverSocket.isClosed()) {
                    System.out.println("Connection closed");
                    try {
                        this.serverSocket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TCPServerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }else{
                    //deal with received message
                    String msg = String.valueOf(cbuf);
                    cbuf = new char[2048];
                    JSONObject JSONMsg = new JSONObject(msg);
                    System.out.println("Message received from " + this.serverSocket.getInetAddress().getHostAddress() + ":" +
                                        this.serverSocket.getPort() + " = " + msg);
             
                    //sending reply to client
                    Thread thread = new Thread(createRunnable(JSONMsg, clientView));
                    thread.start();
                }
            }
        }catch(JSONException e){
            System.out.println("JSON error");
        }catch(IOException e){
            if(e.getMessage().equals("Connection reset")){
                System.out.println("Client desconected");
                //this.server.removeActiveUsers(this.serverSocket.getInetAddress().getHostAddress());
                try {
                    this.serverSocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(TCPServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.interrupt();
            }
        }
    }
}
