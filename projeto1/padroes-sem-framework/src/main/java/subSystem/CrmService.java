package subSystem;

public class CrmService {

    private CrmService(){
        super();
    }
    public static void registerClient(
            String name,
            String cep,
            String city,
            String estate
    ) {
        System.out.println("Cliente salvo no sistema de CRM");
        System.out.println("""
                {
                    "name": "%s",
                    "cep: "%s",
                    "city": "%s",
                    "estate": "%s"
                }
                """.formatted(name,cep,city,estate));
    }
}
