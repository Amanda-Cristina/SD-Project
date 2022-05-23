/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import dao.DonationDAO;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gilson
 */
public class Donation implements Serializable{
    private static long serialVersionUID = 1L;
    
    private float quantity;
    private String measureUnit;
    private String description;
    private String idDonor;
    private String id;
    
    public Donation(float quantity, String measureUnity, String description, String idDonor) throws IOException{
        this.quantity = quantity;
        this.measureUnit = measureUnity;
        this.description = description;
        this.idDonor = idDonor;
        this.id = String.valueOf(this.getID_());
    }
    
    public Donation(String id, float quantity, String measureUnity, String description, String idDonor){
        this.quantity = quantity;
        this.measureUnit = measureUnity;
        this.description = description;
        this.idDonor = idDonor;
        this.id = id;
    }
    
    public static List<Donation> getAllDonations() throws IOException{
        DonationDAO donationDAO = new DonationDAO();
        return donationDAO.selectAll();
    }
    
    public static Donation getDonationbyDonerId(String idDonor) throws IOException{
        DonationDAO donationDAO = new DonationDAO();
        List<Donation> donations = donationDAO.selectAll();
        Donation donation = null;
        for(Donation u : donations){
            if(u.getIdDonor().equals(idDonor)){
                return u;
            }
        }
        return donation;
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

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
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
    
    public JSONObject getJSON() throws JSONException{
        JSONObject json = new JSONObject();
        DecimalFormat df = new DecimalFormat("0.0000");
        json.put("quantity", df.format(this.quantity));
        json.put("measureUnit", this.measureUnit);
        json.put("description", this.description);
        json.put("idDonor", this.idDonor);
        return json;
    }
}
