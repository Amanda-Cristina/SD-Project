import br.com.caelum.stella.ValidationMessage;
import br.com.caelum.stella.validation.CPFValidator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    public static void main(String[] args) throws JSONException {
       
        JSONObject jsonobj = new JSONObject();
        JSONObject data = new JSONObject();
        //data.put("password","password");
        //data.put("phone","phone");
        //data.put("cpf","cpf");
        //data.put("name","name");
        jsonobj.put("ping", new JSONObject());
        data.put("ping", new JSONObject());
        System.out.println(jsonobj.equals(data));
    }
}
