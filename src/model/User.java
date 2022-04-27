/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;
/**
 *
 * @author gilson
 */
public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String cpf;
    private String phone;
    private String password;
    
    public User(String id,
                String name,
                String cpf,
                String phone,
                String password){
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.phone = phone;
        this.password = password;
    }
    
    public JSONObject getLoginJSON() throws JSONException{
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("id", this.id);
        jsonobj.put("cpf", this.cpf);
        jsonobj.put("phone", this.phone);
        jsonobj.put("password", this.password);
        return jsonobj;
    }
}
