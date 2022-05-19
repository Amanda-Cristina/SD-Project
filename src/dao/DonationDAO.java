/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import database.Database;
import java.io.IOException;
import java.util.List;
import model.Donation;

/**
 *
 * @author gilson
 */
public class DonationDAO {
    public void save(Donation donation) throws IOException{
        Database db = Database.getInstance();
        db.getDonations().add(donation);
        db.saveState();
    }
    
    public List<Donation> select() throws IOException{
        Database db = Database.getInstance();
        return db.getDonations();
    }
    
    public List<Donation> selectAll() throws IOException{
        Database db = Database.getInstance();
        return (List<Donation>)db.getDonations();
    }
    
    public void delete(Donation donation) throws IOException{
        Database db = Database.getInstance();
        db.getDonations().remove(donation);
        db.saveState();
    }
    
    public boolean update(Donation donation) throws IOException{
        Database db = Database.getInstance();
        int index = db.getDonations().indexOf(donation);
        if(index == -1){
            return false;
        }
        db.getDonations().set(index, donation);
        db.saveState();
        return true;
    }
    
    public boolean updateById(Donation donation) throws IOException{
        Database db = Database.getInstance();
        List<Donation> donations = db.getDonations();
        int index=-1;
        String id = donation.getId();
        for(int i=0;i<donations.size();i++){
            if(donations.get(i).getId().equals(id)){
                index = i;
            }
        }
        if(index == -1){
            return false;
        }
        db.getDonations().set(index, donation);
        db.saveState();
        return true;
    }
}
