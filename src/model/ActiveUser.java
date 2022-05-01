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
    
    public ActiveUser(String ip, int porta, User user){
        this.ip = ip;
        this.port = porta;
        this.user = user;
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
    public int getPorta() {
        return port;
    }

    /**
     * @param porta the port to set
     */
    public void setPorta(int porta) {
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
}
