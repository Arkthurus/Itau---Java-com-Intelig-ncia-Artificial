package subSystem2.cep;

import br.com.dio.Singletons.SingletonEager;

public class CepAPI {
    private static CepAPI instancia = new CepAPI();

    private CepAPI(){
        super();
    }

    public static CepAPI getInstance(){
        return instancia;
    }

    public String recoverCity(String cep){
        return "Curitiba";
    }

    public String recoverEstate(String cep){
        return "PR";
    }
}
