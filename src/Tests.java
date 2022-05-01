
import java.util.HashMap;
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
        String json = "{\n" +
"  \"login\":\n" +
"               {\n" +
"                    \"password\":\"Senha teste\",\n" +
"                     \"cpf\":\"000.000.000-00\"\n" +
"               }\n" +
"}";
        JSONObject jsonobj = new JSONObject();
        JSONObject data = new JSONObject();
        //data.put("password","password");
        //data.put("phone","phone");
        //data.put("cpf","cpf");
        //data.put("name","name");
        //data.put("","");
        jsonobj.put("login", "");
        System.out.println(jsonobj);
    }
}
