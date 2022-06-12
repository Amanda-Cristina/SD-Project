/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojoutils;

import java.text.MessageFormat;

/**
 *
 * @author gilson
 */
public class ReceptionPojo {
    private float quantity;
    private String description;
    private String measureUnity;
    private String id;
    private String idReceiver;
    private String idDonation;
    
    public ReceptionPojo(float quantity, String description, String measureUnity, String id, String idReceiver, String idDonation){
        this.quantity = quantity;
        this.description = description;
        this.measureUnity = measureUnity;
        this.id = id;
        this.idReceiver = idReceiver;
        this.idDonation = idDonation;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getMeasureUnity() {
        return measureUnity;
    }

    public String getId() {
        return id;
    }
    
    @Override
    public String toString(){
        return MessageFormat.format("{0} - {1} - {2}", getDescription(),
        String.valueOf(getQuantity()), getMeasureUnity());
    }
}
