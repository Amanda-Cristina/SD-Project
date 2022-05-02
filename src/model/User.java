/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import dao.UserDAO;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
/**
 *
 * @author gilson
 */
public class User implements Serializable{
    private static long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String cpf;
    private String phone;
    private String password;
    
    public User(String name,
                String cpf,
                String phone,
                String password){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.cpf = cpf;
        this.phone = phone;
        this.password = password;
    }
    
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
    
    public static User getUserByCpf(String cpf) throws IOException{
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.selectAll();
        User user = null;
        for(User u : users){
            if(u.getCpf().equals(cpf)){
                return u;
            }
        }
        return user;
    }
    
    public JSONObject getLoginJSON() throws JSONException{
        JSONObject jsonobj = new JSONObject();
        jsonobj.put("id", this.getId());
        jsonobj.put("cpf", this.getCpf());
        jsonobj.put("phone", this.getPhone());
        jsonobj.put("password", this.getPassword());
        return jsonobj;
    }
    
    public static User login(String cpf, String password) throws IOException{
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.selectAll();
        User user = null;
        for(User u : users){
            if(u.getCpf().equals(cpf)){
                if(u.getPassword().equals(password)){
                    return u;
                }
            }
        }
        return user;
    }

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @param aSerialVersionUID the serialVersionUID to set
     */
    public static void setSerialVersionUID(long aSerialVersionUID) {
        serialVersionUID = aSerialVersionUID;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the cpf
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * @param cpf the cpf to set
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
