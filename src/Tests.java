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
       
        JSONObject data2 = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("donations", new JSONArray());
        data.put("receives", new JSONArray());
        data2.put("teste", data);
        
        System.out.println(data2);
    }
}
