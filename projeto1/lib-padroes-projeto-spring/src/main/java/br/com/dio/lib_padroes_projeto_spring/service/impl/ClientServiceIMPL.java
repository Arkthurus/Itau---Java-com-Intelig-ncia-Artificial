package br.com.dio.lib_padroes_projeto_spring.service.impl;

import br.com.dio.lib_padroes_projeto_spring.model.Address;
import br.com.dio.lib_padroes_projeto_spring.model.AddressRepository;
import br.com.dio.lib_padroes_projeto_spring.model.Client;
import br.com.dio.lib_padroes_projeto_spring.model.ClientRepository;
import br.com.dio.lib_padroes_projeto_spring.service.ClientService;
import br.com.dio.lib_padroes_projeto_spring.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientServiceIMPL implements ClientService {

    // TODO Singleton: Injetar os componentes do Spring com @Autowired.
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ViaCepService viaCepService;

    // TODO Strategy: Implementar os metodos definidos na interface.
    // TODO Facade: Abstrair integrações com subsistemas, promovendo uma interface simples.

    @Override
    public Iterable<Client> searchAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client searchById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        return clientRepository.findById(id).orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
    }

    @Override
    public void insert(Client c) {
        cepExistenceCheck(c);
    }

    private void cepExistenceCheck(Client c) {
        String cep = c.getAddress().getCep();
        Address address = addressRepository.findById(cep).orElseGet(() -> {
            Address newAddress = viaCepService.consultCEP(cep);
            addressRepository.save(newAddress);
            return newAddress;
        });
        c.setAddress(address);
        clientRepository.save(c);
    }

    @Override
    public void update(Client c, Long id) {
        Optional<Client> cDB = clientRepository.findById(id);
        if (cDB.isPresent()){
            this.cepExistenceCheck(c);
        }
    }

    @Override
    public void delete(Long id) {
        clientRepository.deleteById(id);
    }
}
