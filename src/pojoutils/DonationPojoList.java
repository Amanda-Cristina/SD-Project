/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojoutils;

import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import main.ClientView;
import model.Donation;

/**
 *
 * @author gilson
 */
public class DonationPojoList {
    DefaultListModel<DonationPojo> model;
    ClientView view;
    
    public DonationPojoList(ClientView view){
        this.view = view;
    }
    public void setPojoList(boolean listener){
        if(listener){
            this.view.setReceptionsList(this.model);
            JList viewList = view.getReceptionList();
            viewList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent levent) {
                    if(levent.getValueIsAdjusting()){
                        ReceptionPojo pojo = (ReceptionPojo) ((JList) levent.getSource()).getSelectedValue();
                        System.out.println(pojo);
                    }
                }
            });
        }else{
            this.view.setReceiveOrDonateList(this.model);
        }
    }
    
    public void updateList(ArrayList<DonationPojo> donationsPojo){
        if(this.model == null){
            this.model = new DefaultListModel<>();
        }
        for(DonationPojo donation : donationsPojo){
            this.model.addElement(donation);
        }
    }
    public void updateList(DonationPojo donationPojo){
        if(this.model == null){
            this.model = new DefaultListModel<>();
        }
        this.model.addElement(donationPojo);
    }
    public void clear(){
        this.model.removeAllElements();
    }
    public DefaultListModel getModel(){
        return this.model;
    }
}
