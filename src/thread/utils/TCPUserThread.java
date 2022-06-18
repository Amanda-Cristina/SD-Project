package thread.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import main.ClientView;
import model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pojoutils.DonationPojo;
import pojoutils.DonationPojoList;
import pojoutils.ReceptionPojoList;
import pojoutils.ReceptionPojo;
import thread.utils.TCPServerThread;
import utils.ConsoleDate;

/**
 *
 * @author gilson
 */
public class TCPUserThread extends Thread{
    public Socket serverSocket;
    public PrintWriter output;
    public BufferedReader input;
    private ClientView clientView;
    private DonationPojoList donationPojoList = null;
    private ReceptionPojoList receptionPojoList = null;
    
    public TCPUserThread(Socket serverSocket, PrintWriter output, BufferedReader input, ClientView clientView){
        this.serverSocket = serverSocket;
        this.output = output;
        this.input = input;
        this.clientView = clientView;
    }
    public void sendMessage(JSONObject msg_json) throws IOException, JSONException{
        this.output.println(msg_json.toString());
        this.output.flush();
        System.out.println(ConsoleDate.getConsoleDate()+"Message sent to "+ this.serverSocket.getInetAddress().getHostAddress() + ":" + this.serverSocket.getPort() + " = " + msg_json);
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
            clientView.setLoggedUser(false);
            clientView.setLoginpanelVisibility(true);
        }
    }
    
    public void treatUpdateUser(JSONObject json_msg, ClientView clientView) throws JSONException{
        json_msg = (JSONObject)json_msg.get("userUpdate");
        if(json_msg.has("error")){
            JOptionPane.showMessageDialog(null, json_msg.get("error"), "Update error",
                    JOptionPane.WARNING_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "Update user information saved", "Update sucess",
                    JOptionPane.INFORMATION_MESSAGE);
            clientView.setUpdateUserVisibility(true);
        }
    }
    
    public void treatDonation(JSONObject json_msg, ClientView clientView) throws JSONException{
        json_msg = (JSONObject)json_msg.get("donation");
        if(json_msg.has("error")){
            JOptionPane.showMessageDialog(null, json_msg.get("error"), "Donation error",
                    JOptionPane.WARNING_MESSAGE);
        }else{
            JOptionPane.showMessageDialog(null, "Donation created", "Donation sucess",
                    JOptionPane.INFORMATION_MESSAGE);
            clientView.setCreateDonationVisibility(true);
        }
    }
    
    public void treatDonationUpdate(JSONObject json_msg, ClientView clientView) throws JSONException{
        json_msg = (JSONObject)json_msg.get("donationUpdate");
        if(json_msg.has("error")){
            JOptionPane.showMessageDialog(null, json_msg.get("error"), "Donation error",
                    JOptionPane.WARNING_MESSAGE);
        }else{
            clientView.setHomepanelVisibility(true);
            try{
                JSONObject jsonobj = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("idClient", this.clientView.getUser().getId());
                jsonobj.put("clientTransactions", data);
                this.sendMessage(jsonobj);
            }catch(JSONException | IOException ex){
                System.out.println(ConsoleDate.getConsoleDate()+"Transactions server fetch error");
            }
        }
    }
    
    public void treatReceptions(JSONObject json_msg, ClientView clientView) throws JSONException{
        json_msg = (JSONObject)json_msg.get("receptions");
        if(json_msg.has("error")){
            System.out.println(ConsoleDate.getConsoleDate()+"Error");
        }else if(((JSONArray)json_msg.get("donations")).length()==0){
            System.out.println(ConsoleDate.getConsoleDate()+"Vazio");
        }else{
            DonationPojoList donationPojoList_ = new DonationPojoList(clientView);
            JSONArray data = (JSONArray)json_msg.get("donations");
            for(int i=0;i<data.length();i++){
                JSONObject j = data.getJSONObject(i);
                if(!j.get("idDonor").toString().equals(this.clientView.getUser().getId())){
                    donationPojoList_.updateList(new DonationPojo(
                            Float.valueOf(j.get("quantity").toString()),
                            j.get("description").toString(), 
                            j.get("measureUnit").toString(), 
                            j.get("id").toString(),
                            j.get("idDonor").toString()
                    ));
                }
            }
            if(donationPojoList_.getModel() != null)
                donationPojoList_.setPojoList(true);
        }
    }
    
    public void treatUserTransactions(JSONObject json_msg, ClientView clientView) throws JSONException{
        json_msg = (JSONObject)json_msg.get("clientTransactions");
        if(json_msg.has("error")){
            System.out.println(ConsoleDate.getConsoleDate()+"Error");
        }else if(json_msg.isNull("donations") && json_msg.isNull("receives")){
            System.out.println(ConsoleDate.getConsoleDate()+"No donations or receives");
        }else{
            if(receptionPojoList!=null)
                receptionPojoList.clear();
            if(donationPojoList!=null)
                donationPojoList.clear();
            if(!clientView.getHomeOperation()){
                JSONArray data = (JSONArray)json_msg.get("receives");
                if(data.length()>0){
                    if(receptionPojoList == null)
                        receptionPojoList = new ReceptionPojoList(clientView);
                    for(int i=0;i<data.length();i++){
                        JSONObject j = data.getJSONObject(i);
                        receptionPojoList.updateList(new ReceptionPojo(
                                Float.valueOf(j.get("quantity").toString()),
                                j.get("description").toString(), 
                                j.get("measureUnit").toString(), 
                                j.get("id").toString(),
                                j.get("idReceiver").toString(),
                                j.get("idDonation").toString()
                        ));
                    }
                    receptionPojoList.setPojoList(false);
                }
            }else if(clientView.getHomeOperation()){
                JSONArray data = (JSONArray)json_msg.get("donations");
                if(data.length()>0){
                    if(donationPojoList == null)
                        donationPojoList = new DonationPojoList(clientView);
                    for(int i=0;i<data.length();i++){
                        JSONObject j = data.getJSONObject(i);
                        donationPojoList.updateList(new DonationPojo(
                                Float.valueOf(j.get("quantity").toString()),
                                j.get("description").toString(), 
                                j.get("measureUnit").toString(), 
                                j.get("id").toString(),
                                j.get("idDonor").toString()
                        ));
                    }
                    donationPojoList.setPojoList(false);
                }
            }
        }
    }
    
    public void treatLogout(JSONObject json_msg, ClientView clientView) throws JSONException{
        json_msg = (JSONObject)json_msg.get("close");
        if(!json_msg.has("error")){
            clientView.setHomepanelVisibility(false);
            clientView.setLoginpanelVisibility(true);
        }else{
            JOptionPane.showMessageDialog(null, json_msg.get("error"), "Logout error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public void treatDonationDelete(JSONObject json_msg, ClientView clientView) throws JSONException{
        json_msg = (JSONObject)json_msg.get("donationDelete");
        if(!json_msg.has("error")){
            try{
                JSONObject jsonobj = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("idClient", this.clientView.getUser().getId());
                jsonobj.put("clientTransactions", data);
                this.sendMessage(jsonobj);
            }catch(JSONException | IOException ex){
                System.out.println(ConsoleDate.getConsoleDate()+"Transactions server fetch error");
            }
        }else{
            JOptionPane.showMessageDialog(null, json_msg.get("error"), "Delete error",
                    JOptionPane.WARNING_MESSAGE);
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
                
                case "userUpdate" -> {
                    try{
                        treatUpdateUser(json_msg, clientView);
                    }catch(JSONException ex){
                        Logger.getLogger(TCPUserThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                case "donation" -> {
                    try{
                        treatDonation(json_msg, clientView);
                    }catch(JSONException ex){
                        Logger.getLogger(TCPUserThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                case "receptions" ->{
                    try{
                        treatReceptions(json_msg, clientView);
                    }catch(JSONException ex){
                        Logger.getLogger(TCPUserThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                case "clientTransactions" ->{
                    try{
                        treatUserTransactions(json_msg, clientView);
                    }catch(JSONException ex){
                        Logger.getLogger(TCPUserThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                case "donationDelete" ->{
                    try{
                        treatDonationDelete(json_msg, clientView);
                    }catch(JSONException ex){
                        Logger.getLogger(TCPUserThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                case "donationUpdate" ->{
                    try{
                        treatDonationUpdate(json_msg, clientView);
                    }catch(JSONException ex){
                        Logger.getLogger(TCPUserThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                case "close" -> {
                try {
                    treatLogout(json_msg, clientView);
                } catch (JSONException ex) {
                    Logger.getLogger(TCPUserThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
                default -> {
                        System.out.println(ConsoleDate.getConsoleDate()+"JSON Key error");
                }
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
                String data = input.readLine();
                //int flag = input.read(cbuf);
                if (data == null || serverSocket.isClosed()) {
                    System.out.println(ConsoleDate.getConsoleDate()+"Connection closed");
                    try {
                        this.serverSocket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TCPServerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    this.clientView.setServerconnectionVisibility(true);
                    break;
                }else{
                    //deal with received message
                    //String msg = String.valueOf(cbuf);
                    String msg = data;
                    cbuf = new char[2048];
                    JSONObject JSONMsg = new JSONObject(msg);
                    System.out.println(ConsoleDate.getConsoleDate()+"Message received from " + this.serverSocket.getInetAddress().getHostAddress() + ":" +
                                        this.serverSocket.getPort() + " = " + msg);
             
                    //sending reply to client
                    Thread thread = new Thread(createRunnable(JSONMsg, clientView));
                    thread.start();
                }
            }
        }catch(JSONException e){
            System.out.println(ConsoleDate.getConsoleDate()+"JSON error");
        }catch(IOException e){
            if(e.getMessage().equals("Connection reset")){
                System.out.println(ConsoleDate.getConsoleDate()+"Client desconected");
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
