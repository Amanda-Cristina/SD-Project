/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author gilson
 */
public class TCPUser {
    public Socket userSocket;
    public PrintWriter output;
    public BufferedReader input;
    
    public TCPUser(){
        
    }
    
    public void conect(String ip, int port){
        this.userSocket = new Socket(ip, port);
        output = 
    }
}
