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
       
        JSONObject reply = new JSONObject();
        JSONObject data = new JSONObject();
        JSONObject donation = new JSONObject();
        JSONObject error = new JSONObject();
        
        data.put("idReceptor", '2');
        donation.put("quantity", 23);
        data.put("donation", donation);
        //reply.put("startChat", data);
        error.put("error", "error");
        reply.put("startChat", error);
        System.out.println(reply);
    }
}
