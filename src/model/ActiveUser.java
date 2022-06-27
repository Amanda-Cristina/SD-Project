package model;

import java.net.Socket;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author gilson
 */
public class ActiveUser {
    private String ip;
    private int port;
    private User user;
    private boolean loggedUser;
    private boolean connected;
    private Socket connection;
    
    public ActiveUser(String ip, int porta, boolean loggedUser, User user, boolean connected){
        this.ip = ip;
        this.port = porta;
        this.user = user;
        this.loggedUser = loggedUser;
        this.connected = connected;
    }
    
    public ActiveUser(String ip, int porta, boolean loggedUser, boolean connected){
        this.ip = ip;
        this.port = porta;
        this.loggedUser = loggedUser;
        this.connected = connected;
    }
    
    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param porta the port to set
     */
    public void setPort(int porta) {
        this.port = porta;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }
    
    public boolean getConnected(){
        return this.connected;
    }
    
    public void setConnected(boolean connected){
        this.connected = connected;
    }
    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    public void setLoggedUser(boolean logged){
        this.loggedUser = logged;
    }
    
    public boolean getLoggedUser(){
        return this.loggedUser;
    }
    
    public void setConnection(Socket connection){
        this.connection = connection;
    }
    
    public Socket getConnection(){
        return this.connection;
    }
}
