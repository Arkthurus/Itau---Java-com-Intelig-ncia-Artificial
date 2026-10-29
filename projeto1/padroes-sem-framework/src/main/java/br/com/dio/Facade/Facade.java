package br.com.dio.Facade;

import subSystem.CrmService;
import subSystem2.cep.CepAPI;

public class Facade {
    public void migrateClient(String name, String cep){

        String city   = CepAPI.getInstance().recoverCity(cep);
        String estate = CepAPI.getInstance().recoverEstate(cep);

        CrmService.registerClient(name, cep, city,estate);
    }
}
