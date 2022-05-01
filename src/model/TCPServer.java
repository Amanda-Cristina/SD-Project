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
import main.ServerView;
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
    
    @Override
    public void run(){
        while(true){
            try{
                userSocket = serverSocket.accept();
                TCPServerThread thread = new TCPServerThread(clientSocket, this);
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
}
