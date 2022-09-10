/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojoutils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import main.Chat;
import main.ClientView;
import model.Donation;
import model.server.TCPUser;
import org.json.JSONException;
import org.json.JSONObject;
import thread.utils.TCPServerThread;
import thread.utils.TCPUserThread;

/**
 *
 * @author gilson
 */
public class DonationPojoList {
    DefaultListModel<DonationPojo> model;
    ClientView view;
    TCPUserThread server;
    
    public DonationPojoList(ClientView view, TCPUserThread server){
        this.view = view;
        this.server = server;
    }
    public void setPojoList(boolean listener){
        if(listener){
            this.view.setReceptionsList(this.model);
            JList viewList = view.getReceptionList();
            viewList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent levent) {
                    if(levent.getValueIsAdjusting()){
                        DonationPojo pojo = (DonationPojo) ((JList) levent.getSource()).getSelectedValue();
                        JSONObject msg_json = new JSONObject();
                        JSONObject data = new JSONObject();
                        try {
                            data.put("idReceptor", server.getClientView().getUser().getId());
                            data.put("idDonation", pojo.getId());
                            msg_json.put("startChat", data);
                        } catch (JSONException ex) {
                            Logger.getLogger(DonationPojoList.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                            server.sendMessage(msg_json);
                        } catch (IOException ex) {
                            Logger.getLogger(DonationPojoList.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (JSONException ex) {
                            Logger.getLogger(DonationPojoList.class.getName()).log(Level.SEVERE, null, ex);
                        }
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
