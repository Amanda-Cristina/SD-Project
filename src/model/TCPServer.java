/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.ServerView;
import org.json.JSONException;
import org.json.JSONObject;
import thread.utils.TCPServerThread;

/**
 *
 * @author gilson
 */
public class TCPServer extends Thread{
    private ServerSocket serverSocket;
    private Socket userSocket;
    private Socket clientSocket;
    private ArrayList<TCPServerThread> threads;
    private ArrayList<ActiveUser> activeUsers;
    private ServerView view;
    
    public TCPServer(ServerView view){
        this.threads = new ArrayList<>();
        this.activeUsers = new ArrayList<>();
        this.view = view;
    }
    
    public void startServer(int port) throws IOException{
        System.out.println("Server start at port: " + port);
        serverSocket = new ServerSocket(port);
        this.start();
    }
    
    public void addActiveUser(ActiveUser activeUser){
        this.activeUsers.add(activeUser);
    }
    
    public void removeActiveUsers(String userIP){
        for(int i = 0; i < activeUsers.size();i++){
            ActiveUser au = this.activeUsers.get(i);
            if(au.getIp().equals(userIP)){
                this.activeUsers.remove(i);
            }
        }
    }
    
    public ArrayList<ActiveUser> getActiveUsers(){
        return this.activeUsers;
    }
    
    public void updateActiveUser(int user_index, ActiveUser activeUser){
        this.activeUsers.set(user_index, activeUser);
    }
    
    private Runnable createRunnable(final TCPServer tCPServer){
        Runnable ping = new Runnable() {
            @Override
            public void run() {
                if(!tCPServer.threads.isEmpty()){
                    for(TCPServerThread i : tCPServer.threads){
                        JSONObject msg = new JSONObject();
                        try {
                            msg.put("ping", "");
                        } catch (JSONException ex) {
                            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        JSONObject reply;
                        try {
                            reply = i.sendMessage(msg);
                        } catch (IOException ex) {
                            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (JSONException ex) {
                            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        };
        return ping;
    }
    
    @Override
    public void run(){
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(createRunnable(this), 0, 30, TimeUnit.HOURS);
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
    }
    
    public ServerView getServerView(){
        return this.view;
    }
    
    public ArrayList<TCPServerThread> getServerThreadList(){
        return this.threads;
    }
}
