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
import main.Chat;
import main.ClientView;
import model.Donation;

/**
 *
 * @author gilson
 */
public class ReceptionPojoList {
    DefaultListModel<ReceptionPojo> model;
    ClientView view;
    
    public ReceptionPojoList(ClientView view){
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
                        
                    }
                }
            });
        }else{
            this.view.setReceiveOrDonateList(this.model);
        }
    }
    
    public void updateList(ArrayList<ReceptionPojo> receptionsPojo){
        if(this.model == null){
            this.model = new DefaultListModel<>();
        }
        for(ReceptionPojo donation : receptionsPojo){
            this.model.addElement(donation);
        }
    }
    public void updateList(ReceptionPojo receptionsPojo){
        if(this.model == null){
            this.model = new DefaultListModel<>();
        }
        this.model.addElement(receptionsPojo);
    }
    public void clear(){
        this.model.removeAllElements();
    }
}
