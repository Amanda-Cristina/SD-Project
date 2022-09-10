/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.ServerView;
import model.ActiveUser;
import org.json.JSONException;
import org.json.JSONObject;
import thread.utils.TCPServerThread;
import utils.ConsoleDate;

/**
 *
 * @author gilson
 */
public class TCPServer extends Thread{
    private ServerSocket serverSocket;
    private Socket userSocket;
    private Socket clientSocket;
    private ArrayList<TCPServerThread> threads;
    private ArrayList<ActiveUser> connectedUsers;
    private ArrayList<ActiveUser> onlineUsers;
    private ServerView view;
    private boolean alternate;
    
    public TCPServer(ServerView view){
        this.threads = new ArrayList<>();
        this.connectedUsers = new ArrayList<>();
        this.onlineUsers = new ArrayList<>();
        this.view = view;
        this.alternate = true;
    }
    
    public void startServer(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        System.out.println(ConsoleDate.getConsoleDate()+"Server start at port: " + port);
        this.start();
    }
    
    public void addActiveUser(ActiveUser activeUser){
        this.connectedUsers.add(activeUser);
    }
    
    public ActiveUser getActiveUserByID(String id){
        for(ActiveUser u:connectedUsers){
            if(u.getUser().getId().equals(id)){
                return u;
            }
        } 
        return null;
    }
    
    public ActiveUser getActiveUserByIP(String ip){
        for(ActiveUser u:connectedUsers){
            if(u.getIp().equals(ip)){
                return u;
            }
        } 
        return null;
    }
    
    public void removeActiveUsers(ActiveUser user){
        this.connectedUsers.remove(user);
    }
    
    public void removeThread(TCPServerThread thread){
        this.threads.remove(thread);
    }
    
    public ArrayList<ActiveUser> getConnectedUsers(){
        return this.connectedUsers;
    }
    
    public void updateActiveUsers(int user_index, ActiveUser activeUser){
        this.connectedUsers.set(user_index, activeUser);
    }
    
    public void addOnlineUser(ActiveUser activeUser){
        this.onlineUsers.add(activeUser);
    }
    
    public void removeOnlineUser(ActiveUser activeUser){
        this.onlineUsers.remove(activeUser);
    }
    
    public ActiveUser getOnlineUserByID(String id){
        for(ActiveUser user : onlineUsers){
            if(user.getUser().getId().equals(id)){
                return user;
            }
        }
        return null;
    }
    
    public boolean isUserOnline(String id){
        for(ActiveUser user : onlineUsers){
            if(user.getUser().getId().equals(id)){
                return true;
            }
        }
        return false;
    }
    
    private Runnable createRunnable(final TCPServer tCPServer, boolean send, ScheduledExecutorService executor){
        Runnable ping = null;
        if(send){
            ping = new Runnable() {
                @Override
                public void run() {
                    if(!tCPServer.threads.isEmpty()){
                        for(TCPServerThread i : tCPServer.threads){
                            if(!i.getActiveUser().getLoggedUser()) continue;
                            JSONObject msg = new JSONObject();
                            try {
                                msg.put("ping", new JSONObject());
                            } catch (JSONException ex) {
                                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            JSONObject reply;
                            try {
                                i.sendMessageWithoutRet(msg);
                                i.setUserPing(false);
                            } catch (IOException ex) {
                                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (JSONException ex) {
                                Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    executor.schedule(tCPServer.createRunnable(tCPServer, false, null), 29, TimeUnit.SECONDS);
                }
            };
        }else{
            ping = new Runnable() {
                @Override
                public void run() {
                    if(!tCPServer.threads.isEmpty()){
                        for(TCPServerThread i : tCPServer.threads){
                            if(!i.getUserPing()){
                                try {
                                    i.userSocket.close();
                                } catch (IOException ex) {
                                    Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                tCPServer.threads.remove(i);
                                tCPServer.connectedUsers.remove(i.getActiveUser());
                                System.out.println(ConsoleDate.getConsoleDate()+i.getActiveUser().getIp() + ":" +
                                        i.getActiveUser().getPort()+" removed for not responding to ping ");
                                i.interrupt();
                            }
                        }
                    }
                }
            };
        }
        
        return ping;
    }
    
    @Override
    public void run(){
        //ScheduledExecutorService executor_1 = Executors.newSingleThreadScheduledExecutor();
        //ScheduledExecutorService executor_2 = Executors.newSingleThreadScheduledExecutor(); 
        //executor_1.scheduleAtFixedRate(createRunnable(this, true, executor_2), 0, 30, TimeUnit.SECONDS);
        while(true){
            try{
                userSocket = serverSocket.accept();
                TCPServerThread thread = new TCPServerThread(userSocket, this);
                this.threads.add(thread);
                thread.start();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        //executor.shutdown();
    }
    
    public ServerView getServerView(){
        return this.view;
    }
    
    public ArrayList<TCPServerThread> getServerThreadList(){
        return this.threads;
    }
    
    public boolean getAlternate(){
        return this.alternate;
    }
    
    public void setAlternate(boolean alt){
        this.alternate = alt;
    }
}
