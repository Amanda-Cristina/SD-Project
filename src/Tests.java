import br.com.caelum.stella.ValidationMessage;
import br.com.caelum.stella.validation.CPFValidator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Donation;
import model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author gilson
 */
public class Tests {
    
    public static void main(String[] args) throws JSONException, IOException {
       
        JSONObject jsonobj = new JSONObject();
        JSONObject data = new JSONObject();
        //data.put("password","password");
        //data.put("phone","phone");
        //data.put("cpf","cpf");
        //data.put("name","name");        
        
        List<Donation> users = new ArrayList<>();
        users.add(new Donation(0.2f, "12", "12", "12"));
        users.add(new Donation(0.2f, "13", "14", "12"));
        users.add(new Donation(0.2f, "14", "15", "12"));
        
        jsonobj.append("receptions", users.get(0).getJSON());
        jsonobj.append("receptions", users.get(1).getJSON());
        String a = "-1\n";
        int flag;
        try{
            flag = Integer.parseInt(a.replace("\n", "").replace("\r", ""));
        }catch(Exception e){
            flag = 0;
        }
        System.out.println(flag);
        /*JSONArray a = (JSONArray)data.get("donations");
        for(int i=0;i<a.length();i++){
            JSONObject j = a.getJSONObject(i);
            System.out.println(j.get("quantity"));
        }*/
    }
}
