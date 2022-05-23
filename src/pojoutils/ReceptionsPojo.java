/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojoutils;

/**
 *
 * @author gilson
 */
public class ReceptionsPojo {
    private float quantity;
    private String description;
    private String measureUnity;
    private String id;
    
    public ReceptionsPojo(float quantity, String description, String measureUnity, String id){
        this.quantity = quantity;
        this.description = description;
        this.measureUnity = measureUnity;
        this.id = id;
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
        return "Description: "+getDescription()+"\n"+
               "Quantity: "+String.valueOf(getQuantity())+"\n"+
               "Measure unity: "+getMeasureUnity();
    }
}
