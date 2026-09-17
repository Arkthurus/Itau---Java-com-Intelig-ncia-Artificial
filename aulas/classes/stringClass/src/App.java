import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) throws Exception {
        var value = """
            {"name":"João", "age":"18"}""";
        System.out.println("string original: " + value);            
        Map<String, String> map = new HashMap<>();
        value = value.replace("{", "").replace("}","").replace("\"", "");
        System.out.println("string replaced: " + value);            
        var valueArr = value.split(",");
        System.out.println("string splited: " + valueArr[0]);            
        System.out.println("string splited: " + valueArr[1]);            
        System.out.printf("------------------\n");
        for (var v : valueArr) {
            var keyValue = v.split(":");
            System.out.println("KeyName : " + keyValue[0]);
            System.out.println("Value : " + keyValue[1]);
            System.out.printf("------------------\n");
            map.put(keyValue[0], keyValue[1]);
        }        
        System.out.println("extraido do Map : " + map);
    }
}
