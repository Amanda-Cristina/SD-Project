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
    
    ///INÍCIO: TESTE FUNÇÃO VALIDAR CPF
    public static boolean valida(String cpf) { 
        CPFValidator cpfValidator = new CPFValidator(); 
        List<ValidationMessage> erros = cpfValidator.invalidMessagesFor(cpf);
        return erros.isEmpty();
    }
    
    public static void main(String[] args) throws JSONException {
       
    /*System.out.println(valida("444.515.248-02"));
    ///FIM: TESTE VALIDAR CPF
        
        String json = "{\n" +
"  \"login\":\n" +
"               {\n" +
"                    \"password\":\"Senha teste\",\n" +
"                     \"cpf\":\"000.000.000-00\"\n" +
"               }\n" +
"}";
        JSONObject jsonobj = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("password","password");
        data.put("phone","phone");
        data.put("cpf","cpf");
        data.put("name","name");
        jsonobj.put("register",data);
        System.out.println(jsonobj);
    }*/
        JSONObject jsonobj = new JSONObject();
        //JSONObject data = new JSONObject();
        //data.put("error","error");
        jsonobj.put("ping","");
        System.out.println(jsonobj);
    }
}
