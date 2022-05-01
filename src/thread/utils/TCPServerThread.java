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
    
    public TCPServerThread(Socket userSocket, TCPServer server){
        this.server = server;
        this.userSocket = userSocket;
        this.view = server.getServerView();
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
        User user = User.login(cpf, password);
        if(user != null){
            data.put("password",user.getPassword());
            data.put("phone",user.getPhone());
            data.put("cpf",user.getCpf());
            data.put("name",user.getName());
            data.put("id",user.getId());
            reply.put("login", data);
            ActiveUser activeUser = new ActiveUser(this.userSocket.getInetAddress().getHostAddress(), 
                                                   this.userSocket.getLocalPort(), user);
            this.server.addActiveUser(activeUser);
        }else{
            data.put("error", "Invalid login");
            reply.put("login", data);
        }
        return reply;
    }
    
    private JSONObject logout(JSONObject msg_json){
        JSONObject reply = new JSONObject();
        JSONObject data = new JSONObject();
        this.loggedUser = false;
        this.server.removeActiveUsers(this.userSocket.getInetAddress().getHostName());
        return reply;
    }
    
    private JSONObject getReply(JSONObject msg_json) throws JSONException, IOException{
        String operation = msg_json.keys().next().toString();
        JSONObject reply = new JSONObject();
        if(operation.equals("login")){
            reply = login(msg_json);
        }else if(operation.equals("register")){
            reply = signup(msg_json);
        }
        return reply;
    }
    
    @Override
    public void run(){
        System.out.println("a");
        try{
            outputData = new PrintWriter(this.userSocket.getOutputStream(), true);
            inputData = new BufferedReader(new InputStreamReader(this.userSocket.getInputStream()));
            System.out.println("Novo usuário conectado: " + this.userSocket.getInetAddress().getHostAddress());
            DefaultTableModel table = (DefaultTableModel)view.getTable().getModel();
            table.addRow(new Object[]{this.userSocket.getInetAddress().getHostAddress(),
                                      this.userSocket.getPort(),
                                      "--","false"});
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
                    System.out.println("Message received from "+ this.userSocket.getInetAddress().getHostAddress() +" = " + msg);
                    //get the reply
                    JSONObject reply = getReply(JSONMsg);
                    //sending reply to client
                    outputData.print(reply);
                    outputData.flush();
                    System.out.println("Message sent to "+ this.userSocket.getInetAddress().getHostAddress() +" = " + reply);
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
