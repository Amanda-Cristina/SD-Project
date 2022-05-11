package thread.utils;

import dao.UserDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.table.DefaultTableModel;
import main.ServerView;
import model.ActiveUser;
import model.TCPServer;
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
    
    public TCPServerThread(Socket userSocket, TCPServer server){
        this.server = server;
        this.userSocket = userSocket;
        this.view = server.getServerView();
    }
    
    public void updateTable(){
        for(ActiveUser user_ : server.getActiveUsers()){
            table = (DefaultTableModel)view.getTable().getModel();
            table.setRowCount(0);
            if(user_.getLoggedUser()){
                table.insertRow(0,new Object[]{user_.getIp(),user_.getPorta(),user_.getUser().getName(),"true"});
            }else{
                table.insertRow(0,new Object[]{user_.getIp(),user_.getPorta(),"--","false"});
            }
        }
    }
    
    private JSONObject signup(JSONObject msg_json) throws JSONException, IOException{
        JSONObject reply = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject data_ = msg_json.getJSONObject("register");
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
                reply.put("register", "");
            }catch(IOException e){
                data.put("error", "Database error");
                reply.put("register", data);
            }
        }
        return reply;
    }
    
    private JSONObject login(JSONObject msg_json) throws JSONException, IOException{
        JSONObject reply = new JSONObject();
        JSONObject data = new JSONObject();
        String cpf = msg_json.getJSONObject("login").getString("cpf");
        String password = msg_json.getJSONObject("login").getString("password");
        User user_ = User.login(cpf, password);
        if(user_ != null){
            data.put("password",user_.getPassword());
            data.put("phone",user_.getPhone());
            data.put("cpf",user_.getCpf());
            data.put("name",user_.getName());
            data.put("id",user_.getId());
            reply.put("login", data);
            
            this.loggedUser = true;
            int user_index = this.server.getActiveUsers().indexOf(this.user);
            this.user.setLoggedUser(true);
            this.user.setUser(user_);
            this.server.updateActiveUser(user_index, user);
            updateTable();
        }else{
            data.put("error", "Invalid login");
            reply.put("login", data);
        }
        
        return reply;
    }
    
    private JSONObject logout(JSONObject msg_json) throws JSONException{
        JSONObject reply = new JSONObject();
        reply.put("close", "");
        this.loggedUser = false;
        //this.server.removeActiveUsers(this.userSocket.getInetAddress().getHostName());
        int user_index = this.server.getActiveUsers().indexOf(this.user);
        this.user.setLoggedUser(false);
        this.server.updateActiveUser(user_index, user);
        updateTable();
        return reply;    
    }
    
    private JSONObject getReply(JSONObject msg_json) throws JSONException, IOException{
        String operation = msg_json.keys().next().toString();
        JSONObject reply = new JSONObject();
        switch (operation) {
            case "login" -> reply = login(msg_json);
            case "register" -> reply = signup(msg_json);
            case "close" -> reply = logout(msg_json);
            default -> {
            }
        }
        return reply;
    }
    
    public JSONObject sendMessage(JSONObject msg_json) throws IOException, JSONException{
        this.outputData.print(msg_json.toString());
        this.outputData.flush();
        System.out.println("Message sent to "+ this.userSocket.getInetAddress().getHostAddress() + ":" + this.userSocket.getPort() + " = " + msg_json);
        char[] cbuf = new char[2048];
        inputData.read(cbuf);
        JSONObject reply = new JSONObject(String.valueOf(cbuf));
        System.out.println("Message received from "+ this.userSocket.getInetAddress().getHostAddress() + ":" + this.userSocket.getPort() + " = " + msg_json);
        return reply;
    }
    
    @Override
    public void run(){
        try{
            outputData = new PrintWriter(this.userSocket.getOutputStream(), true);
            inputData = new BufferedReader(new InputStreamReader(this.userSocket.getInputStream()));
            System.out.println("New client connected: " + this.userSocket.getInetAddress().getHostAddress() + this.userSocket.getPort());
            ActiveUser activeUser = new ActiveUser(this.userSocket.getInetAddress().getHostAddress(), 
                                                   this.userSocket.getLocalPort(), false);
            this.user = activeUser;
            this.server.addActiveUser(activeUser);
            this.updateTable();
            char[] cbuf = new char[2048];
            while(true){
                int flag = inputData.read(cbuf);
                if (flag == -1 || userSocket.isClosed()) {
                    this.loggedUser = false;
                    this.server.removeActiveUsers(this.userSocket.getInetAddress().getHostAddress());
                    break;
                }else{
                    //deal with received message
                    String msg = String.valueOf(cbuf);
                    cbuf = new char[2048];
                    JSONObject JSONMsg = new JSONObject(msg);
                    System.out.println("Message received from " + this.userSocket.getInetAddress().getHostAddress() + ":" +
                                        this.userSocket.getPort() + " = " + msg);
                    //get the reply
                    JSONObject reply = getReply(JSONMsg);
                    //sending reply to client
                    outputData.print(reply);
                    outputData.flush();
                    System.out.println("Message sent to " + this.userSocket.getInetAddress().getHostAddress() + ":" + 
                                        this.userSocket.getPort() + " = " + reply);
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
            System.out.println("JSON error");
        }catch(IOException e){
            if(e.getMessage().equals("Connection reset")){
                this.loggedUser = false;
                System.out.println("Client desconected");
                this.server.removeActiveUsers(this.userSocket.getInetAddress().getHostAddress());
            }
        }
    }
}
