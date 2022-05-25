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
import model.Donation;

/**
 *
 * @author gilson
 */
public class ReceptionPojoList {
    DefaultListModel<ReceptionsPojo> model;
    JList list;
    public ReceptionPojoList(JList list){
        this.list = list;
        this.model = (DefaultListModel<ReceptionsPojo>) this.list.getModel();
    }
    public void setPojoList(){
        this.list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent levent) {
                if(levent.getValueIsAdjusting()){
                    ReceptionsPojo pojo = (ReceptionsPojo) ((JList) levent.getSource()).getSelectedValue();
                    System.out.println("Selected:"+pojo.toString());
                }
            }
        });
    }
    public void updateList(ArrayList<ReceptionsPojo> receptionsPojo){
        for(ReceptionsPojo donation : receptionsPojo){
            this.model.addElement(donation);
        }
    }
    public void updateList(ReceptionsPojo receptionsPojo){
        this.model.addElement(receptionsPojo);
    }
}
