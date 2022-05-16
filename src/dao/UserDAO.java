/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import database.Database;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import model.User;

/**
 *
 * @author gilson
 */
public class UserDAO {
    public void save(User user) throws IOException{
        Database db = Database.getInstance();
        user.setId(String.valueOf(getID()));
        db.getUsers().add(user);
        db.saveState();
    }
    private int getID() throws IOException{
        ObjectInputStream inputobj = null;
        ObjectOutputStream outputobj = null;
        AtomicInteger id;
        try{
            inputobj = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File("index.dat"))));
            id = (AtomicInteger) inputobj.readObject();
            inputobj.close();
            return id.getAndIncrement();
        }catch(ClassNotFoundException | IOException ex){
            id = new AtomicInteger();
            int id_ = id.getAndIncrement();
            outputobj = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("index.dat")));
            outputobj.writeObject(id);
            outputobj.close();
            return id_;
        }
    }
    public List<User> select() throws IOException{
        Database db = Database.getInstance();
        return db.getUsers();
    }
    
    public List<User> selectAll() throws IOException{
        Database db = Database.getInstance();
        return (List<User>)db.getUsers();
    }
    
    public void delete(User user) throws IOException{
        Database db = Database.getInstance();
        db.getUsers().remove(user);
        db.saveState();
    }
    
    public boolean update(User user) throws IOException{
        Database db = Database.getInstance();
        int index = db.getUsers().indexOf(user);
        if(index == -1){
            return false;
        }
        db.getUsers().set(index, user);
        db.saveState();
        return true;
    }
    
    public boolean updateById(User user) throws IOException{
        Database db = Database.getInstance();
        List<User> users = db.getUsers();
        int index=-1;
        String id = user.getId();
        for(int i=0;i<users.size();i++){
            if(users.get(i).getId().equals(id)){
                index = i;
            }
        }
        if(index == -1){
            return false;
        }
        db.getUsers().set(index, user);
        db.saveState();
        return true;
    }
}
