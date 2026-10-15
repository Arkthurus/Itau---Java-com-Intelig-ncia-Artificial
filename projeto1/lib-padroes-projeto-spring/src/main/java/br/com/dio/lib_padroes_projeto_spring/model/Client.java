package br.com.dio.lib_padroes_projeto_spring.model;

import jakarta.persistence.ManyToOne;

public class Client {

    private Long id;
    private String name;
    @ManyToOne
    private Address address;
}
