package br.com.dio.lib_padroes_projeto_spring.service;

import br.com.dio.lib_padroes_projeto_spring.model.Client;

public interface ClientService {

    Iterable<Client> searchAll();

    Client searchById(Long id);

    void insert(Client c);

    void update(Client c, Long id);

    void delete(Long id);
}
