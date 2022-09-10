package thread.utils;

import dao.DonationDAO;
import dao.UserDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import main.Chat;
import main.ServerView;
import model.ActiveUser;
import model.Donation;
import model.server.TCPServer;
import model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.ConsoleDate;

/**
 *
 * @author gilson
 */
public class TCPServerThread extends Thread{
    private final TCPServer server;
    private final ServerView view;
    public Socket userSocket;
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
    
    public void updateTable() throws ArrayIndexOutOfBoundsException{
        table = (DefaultTableModel)view.getTable().getModel();
        table.setRowCount(0);
        int i = 0;
        for(ActiveUser user_ : server.getConnectedUsers()){
            table = (DefaultTableModel)view.getTable().getModel();
            if(user_.getLoggedUser()){
                table.insertRow(i++,new Object[]{user_.getIp(),user_.getPort(),user_.getUser().getName(),"true", user_.getConnected()});
            }else{
                table.insertRow(i++,new Object[]{user_.getIp(),user_.getPort(),"--","false", user_.getConnected()});
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
        String quantity = data_.get("quantity").toString();
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
    
    private JSONObject deleteDonation(JSONObject msg_json) throws JSONException, IOException{
        JSONObject data_ = msg_json.getJSONObject("donationDelete");
        String id = data_.get("id").toString();
        JSONObject reply = new JSONObject();
        JSONObject data = new JSONObject();
        Donation donation = null;
        DonationDAO DonationDAO = new DonationDAO();
        List<Donation> donations = DonationDAO.selectAll();
        for(Donation d : donations){
            if(d.getId().equals(id)){
                donation = d;
                break;
            }
        }
        try{
            DonationDAO.delete(donation);
            reply.put("donationDelete", new JSONObject());
        }catch(IOException e){
            data.put("error", "Database error");
            reply.put("donationDelete", data);
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
            this.user.setConnection(this.userSocket);
            this.server.updateActiveUsers(user_index, this.user);
            this.server.addOnlineUser(this.user);
            try{
                updateTable();
            }catch(IndexOutOfBoundsException ex){}
        }else{
            data.put("error", "Invalid login");
            reply.put("login", data);
        }
        
        return reply;
    }
    
    private JSONObject logout(JSONObject msg_json) throws JSONException{
        JSONObject reply = new JSONObject();
        reply.put("close", new JSONObject());
        int user_index = this.server.getConnectedUsers().indexOf(this.user);
        this.user.setLoggedUser(false);
        this.user.setConnection(null);
        this.server.updateActiveUsers(user_index, user);
        this.server.removeOnlineUser(user);
        try{
            updateTable();
        }catch(IndexOutOfBoundsException ex){}
        return reply;    
    }
    
    private JSONObject clientTransactions(JSONObject msg_json) throws JSONException, IOException{
        msg_json = msg_json.getJSONObject("clientTransactions");
        JSONArray data_donations = new JSONArray();
        JSONArray data_receptions = new JSONArray();
        JSONObject data = new JSONObject();
        JSONObject reply = new JSONObject();
        ArrayList<Donation> donations = (ArrayList<Donation>) Donation.getDonationbyDonerId(msg_json.getString("idClient"));
        for(Donation u : donations){
            data_donations.put(u.getJSON(false));
        }
        data.put("donations", data_donations);
        data.put("receives", data_receptions);
        reply.put("clientTransactions", data);
        return reply;
    }
    
    private JSONObject receptions(JSONObject msg_json) throws JSONException, IOException{
        ArrayList<Donation> donations = (ArrayList<Donation>) Donation.getAllDonations();
        JSONObject data = new JSONObject();
        for(Donation i : donations){
            data.append("donations", i.getJSON(true));
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
                !data_.has("password")||!data_.has("phone") ||
                !data_.has("id")){
            data.put("error", "Field empty");
            reply.put("register", data);
            return reply;
        }
        String id = data_.getString("id");
        String cpf = data_.getString("cpf");
        String phone = data_.getString("phone");
        String name = data_.getString("name");
        String password = data_.getString("password");
        User user_ = User.getUserById(id);
        if(user_ == null){
            data.put("error", "User not registered");
            reply.put("userUpdate", data);
            return reply;
        }
        User user_cpf = User.getUserByCpf(cpf);
        if(user_cpf!=null && user_cpf.getId().equals(user_.getCpf())){
            data.put("error", "CPF already registered");
            reply.put("userUpdate", data);
            return reply;
        }
        UserDAO userDAO = new UserDAO();
        user_.setCpf(cpf);
        user_.setName(name);
        user_.setPassword(password);
        user_.setPhone(phone);
        try{
            if(!userDAO.updateById(user_)){
                throw new IOException();
            }
            reply.put("userUpdate", new JSONObject());
        }catch(IOException e){
            data.put("error", "Database error");
            reply.put("userUpdate", data);
        }
        try{
            this.updateTable();
        }catch(IndexOutOfBoundsException ex){}
        return reply;
    }
    
    private JSONObject donationUpdate(JSONObject msg_json) throws JSONException, IOException{
        JSONObject reply = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject data_ = msg_json.getJSONObject("donationUpdate");    
        String id = data_.getString("id");
        String idDonor = data_.getString("idDonor");
        String measureUnit = data_.getString("measureUnit");
        String description = data_.getString("description");
        float quantity = Float.parseFloat(data_.get("quantity").toString());
        Donation donation_ = Donation.getDonationById(id);
        if(donation_ == null){
            data.put("error", "Donation don't exists");
            reply.put("donationUpdate", data);
            return reply;
        }
        DonationDAO donationDao = new DonationDAO();
        donation_.setMeasureUnit(measureUnit);
        donation_.setDescription(description);
        donation_.setQuantity(quantity);
        donation_.setQuantityHistory(quantity);
        try{
            if(!donationDao.update(donation_)){
                throw new IOException();
            }
            reply.put("donationUpdate", new JSONObject());
        }catch(IOException e){
            data.put("error", "Database error");
            reply.put("donationUpdate", data);
        }
        return reply;
    }
    
    private JSONObject startChat(JSONObject msg_json) throws JSONException, IOException{
        msg_json = msg_json.getJSONObject("startChat");
        Donation donation = Donation.getDonationById(msg_json.get("idDonation").toString());
        JSONObject reply = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject donationData = new JSONObject();
        
        try{
            if(!server.isUserOnline(donation.getIdDonor())){
                JSONObject error = new JSONObject();
                error.put("error", "User not online");
                reply.put("startChat", error);
                return reply;
            }

            data.put("idReceptor", msg_json.get("idReceptor"));
            if(donation!=null){
                donationData.put("quantity", donation.getQuantity());
                donationData.put("description", donation.getDescription());
                donationData.put("measureUnit", donation.getMeasureUnit());
                donationData.put("id", donation.getId());
                donationData.put("idDonor", donation.getIdDonor());

                data.put("donation", donationData);
                reply.put("startChat", data);
            }else{
                JSONObject error = new JSONObject();
                error.put("error", "error");
                reply.put("startChat", error);
            }

            ActiveUser user_ = server.getActiveUserByID(donation.getIdDonor());     
            this.sendMessageWithoutRetToIP(reply, user_.getConnection());
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return reply;
    }
    
    private JSONObject chat(JSONObject msg_json) throws JSONException, IOException{
        msg_json = msg_json.getJSONObject("chat");
        JSONObject reply = new JSONObject();
        JSONObject data = new JSONObject();
        
        data.put("message", msg_json.get("message"));
        data.put("idReceptor", msg_json.get("idReceptor"));
        data.put("idDonor", msg_json.get("idDonor"));
        
        reply.put("chatRedirection", data);
        
        String id_;
        if(msg_json.get("idDonor").toString().equals(this.user.getUser().getId())){
            id_ = msg_json.get("idReceptor").toString();
        }else{
            id_ = msg_json.get("idDonor").toString();
        }
        
        ActiveUser user_ = server.getActiveUserByID(id_);
        
        this.sendMessageWithoutRetToIP(reply, user_.getConnection());
        
        return null;
    }
    
    private JSONObject chatCancel(JSONObject msg_json) throws JSONException, IOException{
        return null;
    }
    
    private JSONObject chatConfirmation(JSONObject msg_json) throws JSONException, IOException{
        return null;
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
            case "userUpdate" -> reply = updateUser(msg_json);
            //case "ping" -> reply = ping(msg_json);
            case "donationDelete" -> reply = deleteDonation(msg_json);
            case "donationUpdate" -> reply = donationUpdate(msg_json);
            case "startChat" -> reply = startChat(msg_json);
            case "chat" -> reply = chat(msg_json);
            case "chatCancel" -> reply = chatCancel(msg_json);
            case "chatConfirmation" -> reply = chatConfirmation(msg_json);
            default -> {}
        }
        return reply;
    }
    
    public void sendMessageWithoutRet(JSONObject msg_json) throws IOException, JSONException{
        this.outputData.println(msg_json.toString());
        this.outputData.flush();
        System.out.println(ConsoleDate.getConsoleDate()+"Message sent to "+ this.userSocket.getInetAddress().getHostAddress() + ":" + this.userSocket.getPort() + " = " + msg_json);
    }
    
    public void sendMessageWithoutRetToIP(JSONObject msg_json, Socket socket) throws IOException, JSONException{
        PrintWriter outputData_;
        outputData_ = new PrintWriter(socket.getOutputStream());
        outputData_.println(msg_json.toString());
        outputData_.flush();
        System.out.println(ConsoleDate.getConsoleDate()+"Message sent to "+ socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " = " + msg_json);
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
            outputData = new PrintWriter(this.userSocket.getOutputStream());
            inputData = new BufferedReader(new InputStreamReader(this.userSocket.getInputStream()));
            System.out.println(ConsoleDate.getConsoleDate()+"New client connected: " + this.userSocket.getInetAddress().getHostAddress()+":" + this.userSocket.getPort());
            ActiveUser activeUser = this.server.getActiveUserByIP(this.userSocket.getInetAddress().getHostAddress());
            if(activeUser == null){
                activeUser = new ActiveUser(this.userSocket.getInetAddress().getHostAddress(), 
                                                   this.userSocket.getPort(), false, false);
                this.server.addActiveUser(activeUser);
            }
            activeUser.setConnected(true);
            this.user = activeUser;
            try{
                this.updateTable();
            }catch(IndexOutOfBoundsException ex){}
            char[] cbuf = new char[2048];
            while(true){
                String data = inputData.readLine();
                //int flag = inputData.read(cbuf);
                if (data == null || userSocket.isClosed()) {
                    System.out.println(ConsoleDate.getConsoleDate()+"Client desconected");
                    //this.user.setLoggedUser(false);
                    //this.server.removeActiveUsers(this.userSocket.getInetAddress().getHostAddress());
                    //this.server.removeActiveUsers(this.user);
                    //this.server.removeOnlineUser(this.user);
                    this.user.setConnected(false);
                    try {
                        this.userSocket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TCPServerThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try{
                        updateTable();
                    }catch(IndexOutOfBoundsException ex){}
                    this.server.removeThread(this);
                    break;
                }else{
                    //deal with received message
                    //String msg = String.valueOf(cbuf);
                    String msg = data;
                    cbuf = new char[2048];
                    JSONObject JSONMsg = new JSONObject(msg);
                    System.out.println(ConsoleDate.getConsoleDate()+"Message received from " + this.userSocket.getInetAddress().getHostAddress() + ":" +
                                        this.userSocket.getPort() + " = " + msg);
                    //get the reply
                    JSONObject reply = getReply(JSONMsg);
                    if(reply != null){
                        //sending reply to client
                        if(!reply.has("ping")){
                            outputData.println(reply);
                            outputData.flush();
                            System.out.println(ConsoleDate.getConsoleDate()+"Message sent to " + this.userSocket.getInetAddress().getHostAddress() + ":" + 
                                                this.userSocket.getPort() + " = " + reply);
                        }
                    }
                }
            }
        }catch(JSONException e){
            System.out.println(e);
            System.out.println(ConsoleDate.getConsoleDate()+"JSON error");
        }catch(IOException e){
            if(e.getMessage().equals("Connection reset")){
                System.out.println(ConsoleDate.getConsoleDate()+"Client desconected");
                //this.server.removeActiveUsers(this.userSocket.getInetAddress().getHostAddress());
                this.user.setLoggedUser(false);
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
