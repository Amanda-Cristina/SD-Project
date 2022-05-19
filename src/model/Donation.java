/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author gilson
 */
public class Donation {
    private static long serialVersionUID = 1L;
    
    private float quantity;
    private String measureUnity;
    private String description;
    private String idDonor;
    private String id;
    
    public Donation(float quantity, String measureUnity, String description, String idDonor) throws IOException{
        this.quantity = quantity;
        this.measureUnity = measureUnity;
        this.description = description;
        this.idDonor = idDonor;
        this.id = String.valueOf(this.getID_());
    }
    
    public Donation(String id, float quantity, String measureUnity, String description, String idDonor){
        this.quantity = quantity;
        this.measureUnity = measureUnity;
        this.description = description;
        this.idDonor = idDonor;
        this.id = id;
    }
    
    private int getID_() throws IOException{
        ObjectInputStream inputobj = null;
        ObjectOutputStream outputobj = null;
        AtomicInteger id;
        try{
            inputobj = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File("index_donation.dat"))));
            id = (AtomicInteger) inputobj.readObject();
            inputobj.close();
            return id.getAndIncrement();
        }catch(ClassNotFoundException | IOException ex){
            id = new AtomicInteger();
            int id_ = id.getAndIncrement();
            outputobj = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("index_donation.dat")));
            outputobj.writeObject(id);
            outputobj.close();
            return id_;
        }
    }
    
    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getMeasureUnity() {
        return measureUnity;
    }

    public void setMeasureUnity(String measureUnity) {
        this.measureUnity = measureUnity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getIdDonor() {
        return idDonor;
    }

    public void setIdDonor(String id) {
        this.idDonor = id;
    }
}
