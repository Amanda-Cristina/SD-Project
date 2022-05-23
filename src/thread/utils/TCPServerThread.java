package thread.utils;

import dao.DonationDAO;
import dao.UserDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import main.ServerView;
import model.ActiveUser;
import model.Donation;
import model.server.TCPServer;
import model.User;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gilson
 */
public class TCPServerThread extends Thread{
    private TCPServer server;
    private ServerView view;
    public Socket userSocket;
    public boolean loggedUser = false;
    public PrintWriter outputData;
    public BufferedReader inputData;
    private DefaultTableModel table;
    private ActiveUser user;
    private boolean userPing;
    
    public TCPServerThread(Socket userSocket, TCPServer server){
        this.server = server;
        this.userSocket = userSocket;
        this.view = server.getServerView();
        this.userPing = true;
    }
    
    public void updateTable(){
        table = (DefaultTableModel)view.getTable().getModel();
        table.setRowCount(0);
        int i = 0;
        for(ActiveUser user_ : server.getConnectedUsers()){
            table = (DefaultTableModel)view.getTable().getModel();
            if(user_.getLoggedUser()){
                table.insertRow(i++,new Object[]{user_.getIp(),user_.getPort(),user_.getUser().getName(),"true"});
            }else{
                table.insertRow(i++,new Object[]{user_.getIp(),user_.getPort(),"--","false"});
            }
        }
    }
    
    private JSONObject signup(JSONObject msg_json) throws JSONException, IOException{
        JSONObject reply = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject data_ = msg_json.getJSONObject("register");
        if(!data_.has("cpf")||!data_.has("name")||
                !data_.has("password")||!data_.has("phone")){
            data.put("error", "Field empty");
            reply.put("register", data);
            return reply;
        }
        String cpf = data_.getString("cpf");
        String phone = data_.getString("phone");
        String name = data_.getString("name");
        String password = data_.getString("password");
        if(User.getUserByCpf(cpf) != null){
            data.put("error", "User already registered");
            reply.put("register", data);
        }else{
            User user = new User(name, cpf, phone, password);
            UserDAO userDAO = new UserDAO();
            try{
                userDAO.save(user);
                reply.put("register", new JSONObject());
            }catch(IOException e){
                data.put("error", "Database error");
                reply.put("register", data);
            }
        }
        return reply;
    }
    
    private JSONObject donation(JSONObject msg_json) throws JSONException, IOException{
        JSONObject reply = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject data_ = msg_json.getJSONObject("donation");
        if(!data_.has("quantity")||!data_.has("measureUnit")
                ||!data_.has("description")){
            data.put("error", "Field empty");
            reply.put("register", data);
            return reply;
        }
        String quantity = data_.getString("quantity");
        String description = data_.getString("description");
        String measureUnit = data_.getString("measureUnit");
        String idDonor = data_.getString("idDonor");
        
        Donation donation = new Donation(Float.parseFloat(quantity), measureUnit, description, idDonor);
        DonationDAO DonationDAO = new DonationDAO();
        try{
            DonationDAO.save(donation);
            reply.put("donation", new JSONObject());
        }catch(IOException e){
            data.put("error", "Database error");
            reply.put("donation", data);
            System.out.println(e);
        }

        return reply;
    }
    
    private JSONObject login(JSONObject msg_json) throws JSONException, IOException{
        JSONObject reply = new JSONObject();
        JSONObject data = new JSONObject();
        String cpf = msg_json.getJSONObject("login").getString("cpf");
        String password = msg_json.getJSONObject("login").getString("password");
        if(cpf.isEmpty() || password.isEmpty()){
            data.put("error", "Field empty");
            reply.put("register", data);
            return reply;
        }
        User user_ = User.login(cpf, password);
        if(user_ != null){
            data.put("password",user_.getPassword());
            data.put("phone",user_.getPhone());
            data.put("cpf",user_.getCpf());
            data.put("name",user_.getName());
            data.put("id",user_.getId());
            reply.put("login", data);
            
            int user_index = this.server.getConnectedUsers().indexOf(this.user);
            this.user.setUser(user_);
            this.user.setLoggedUser(true);
            this.server.updateActiveUsers(user_index, this.user);
            this.server.addOnlineUser(this.user);
            updateTable();
        }else{
            data.put("error", "Invalid login");
            reply.put("login", data);
        }
        
        return reply;
    }
    
    private JSONObject logout(JSONObject msg_json) throws JSONException{
        JSONObject reply = new JSONObject();
        reply.put("close", new JSONObject());
        //this.servethis.loggedUserr.removeActiveUsers(this.userSocket.getInetAddress().getHostName());
        int user_index = this.server.getConnectedUsers().indexOf(this.user);
        this.user.setLoggedUser(false);
        this.server.updateActiveUsers(user_index, user);
        //this.server.removeActiveUsers(user);
        this.server.removeOnlineUser(user);
        updateTable();
        return reply;    
    }
    
    private JSONObject clientTransactions(JSONObject msg_json){
        return null;
    }
    
    private JSONObject receptions(JSONObject msg_json) throws JSONException, IOException{
        ArrayList<Donation> donations = (ArrayList<Donation>) Donation.getAllDonations();
        JSONObject data = new JSONObject();
        for(Donation i : donations){
            data.put("donations", i.getJSON());
        }
        JSONObject reply = new JSONObject();
        reply.put("receptions", data);
        return reply;
    }
    
    private JSONObject ping(JSONObject msg_json) throws JSONException{
        JSONObject reply = new JSONObject();
        reply.put("ping", new JSONObject());
        this.userPing = true;
        return reply;
    }
    
    private JSONObject updateUser(JSONObject msg_json) throws JSONException, IOException{
        JSONObject reply = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject data_ = msg_json.getJSONObject("userUpdate");
        if(!data_.has("cpf")||!data_.has("name")||
                !data_.has("password")||!data_.has("phone")){
            data.put("error", "Field empty");
            reply.put("register", data);
            return reply;
        }
        String cpf = data_.getString("cpf");
        String phone = data_.getString("phone");
        String name = data_.getString("name");
        String password = data_.getString("password");
        User user_ = User.getUserByCpf(cpf);
        UserDAO userDAO = new UserDAO();
        user_.setCpf(cpf);
        user_.setName(name);
        user_.setPassword(password);
        user_.setPhone(phone);
        try{
            userDAO.updateById(user_);
            reply.put("userUpdate", new JSONObject());
        }catch(IOException e){
            data.put("error", "Database error");
            reply.put("userUpdate", data);
        }
        return reply;
    }
    
    private JSONObject getReply(JSONObject msg_json) throws JSONException, IOException{
        String operation = msg_json.keys().next().toString();
        JSONObject reply = new JSONObject();
        switch (operation) {
            case "login" -> reply = login(msg_json);
            case "register" -> reply = signup(msg_json);
            case "donation" -> reply = donation(msg_json);
            case "close" -> reply = logout(msg_json);
            case "clientTransactions" -> reply = clientTransactions(msg_json);
            case "receptions" -> reply = receptions(msg_json);
            case "ping" -> reply = ping(msg_json);
            default -> {
            }
        }
        return reply;
    }
    
    public void sendMessageWithoutRet(JSONObject msg_json) throws IOException, JSONException{
        this.outputData.print(msg_json.toString());
        this.outputData.flush();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");
        System.out.println(sdf.format(date)+" >> Message sent to "+ this.userSocket.getInetAddress().getHostAddress() + ":" + this.userSocket.getPort() + " = " + msg_json);
    }
    
    public JSONObject sendMessage(JSONObject msg_json) throws IOException, JSONException{
        this.outputData.print(msg_json.toString());
        this.outputData.flush();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");
        System.out.println(sdf.format(date)+" >> Message sent to "+ this.userSocket.getInetAddress().getHostAddress() + ":" + this.userSocket.getPort() + " = " + msg_json);
        char[] cbuf = new char[2048];
        inputData.read(cbuf);
        JSONObject reply = new JSONObject(String.valueOf(cbuf));
        System.out.println("Message received from "+ this.userSocket.getInetAddress().getHostAddress() + ":" + this.userSocket.getPort() + " = " + msg_json);
        return reply;
    }
    
    public void setUserPing(boolean userPing){
        this.userPing = userPing;
    }
    
    public boolean getUserPing(){
        return this.userPing;
    }
    
    public ActiveUser getActiveUser(){
        return this.user;
    }
    
    @Override
    public void run(){
        try{
            outputData = new PrintWriter(this.userSocket.getOutputStream(), true);
            inputData = new BufferedReader(new InputStreamReader(this.userSocket.getInputStream()));
            System.out.println("New client connected: " + this.userSocket.getInetAddress().getHostAddress() + this.userSocket.getPort());
            ActiveUser activeUser = new ActiveUser(this.userSocket.getInetAddress().getHostAddress(), 
                                                   this.userSocket.getPort(), false);
            this.user = activeUser;
            this.server.addActiveUser(activeUser);
            this.updateTable();
            char[] cbuf = new char[2048];
            while(true){
                int flag = inputData.read(cbuf);
                if (flag == -1 || userSocket.isClosed()) {
                    System.out.println("Client desconected");
                    this.user.setLoggedUser(false);
                    //this.server.removeActiveUsers(this.userSocket.getInetAddress().getHostAddress());
                    this.server.removeActiveUsers(this.user);
                    this.server.removeOnlineUser(this.user);
                    this.server.removeThread(this);
                    try {
                        this.userSocket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TCPServerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    updateTable();
                    break;
                }else{
                    //deal with received message
                    String msg = String.valueOf(cbuf);
                    cbuf = new char[2048];
                    JSONObject JSONMsg = new JSONObject(msg);
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");
                    System.out.println(sdf.format(date)+" >> Message received from " + this.userSocket.getInetAddress().getHostAddress() + ":" +
                                        this.userSocket.getPort() + " = " + msg);
                    //get the reply
                    JSONObject reply = getReply(JSONMsg);
                    //sending reply to client
                    if(!reply.has("ping")){
                        outputData.print(reply);
                        outputData.flush();
                        date = new Date();
                        System.out.println(sdf.format(date)+" >> Message sent to " + this.userSocket.getInetAddress().getHostAddress() + ":" + 
                                            this.userSocket.getPort() + " = " + reply);
                    }
                }
            }
        }catch(JSONException e){
            System.out.println("JSON error");
        }catch(IOException e){
            if(e.getMessage().equals("Connection reset")){
                this.user.setLoggedUser(false);
                System.out.println("Client desconected");
                //this.server.removeActiveUsers(this.userSocket.getInetAddress().getHostAddress());
                this.server.removeActiveUsers(this.user);
                this.server.removeThread(this);
                try {
                    this.userSocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(TCPServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.interrupt();
            }
        }
    }
}
