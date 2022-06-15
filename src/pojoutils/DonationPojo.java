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
public class DonationPojo {
    private float quantity;
    private String description;
    private String measureUnit;
    private String id;
    private String idDonor;
    
    public DonationPojo(float quantity, String description, String measureUnity, String id, String donorId){
        this.quantity = quantity;
        this.description = description;
        this.measureUnit = measureUnity;
        this.id = id;
        this.idDonor = donorId;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public String getId() {
        return id;
    }
    
    @Override
    public String toString(){
        return MessageFormat.format("{0} - {1} - {2}", getDescription(),
        String.valueOf(getQuantity()), getMeasureUnit());
    }

    /**
     * @return the idDonor
     */
    public String getIdDonor() {
        return idDonor;
    }

    /**
     * @param idDonor the idDonor to set
     */
    public void setIdDonor(String idDonor) {
        this.idDonor = idDonor;
    }
}
