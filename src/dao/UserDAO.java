/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import database.Database;
import java.io.IOException;
import java.util.List;
import model.User;

/**
 *
 * @author gilson
 */
public class UserDAO {
    public void save(User user) throws IOException{
        Database bd = Database.getInstance();
        bd.getUsers().add(user);
        bd.saveState();
    }
    public List<User> select() throws IOException{
        Database bd = Database.getInstance();
        return bd.getUsers();
    }
    
}
