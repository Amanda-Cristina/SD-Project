package model;

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
    
    public ActiveUser(String ip, int porta, boolean loggedUser, User user){
        this.ip = ip;
        this.port = porta;
        this.user = user;
        this.loggedUser = loggedUser;
    }
    
    public ActiveUser(String ip, int porta, boolean loggedUser){
        this.ip = ip;
        this.port = porta;
        this.loggedUser = loggedUser;
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
}
