package br.com.dio;

import br.com.dio.model.PersonBuilder;

public class Main {

    public static void main(String[] args) {
        var person = new PersonBuilder()
                .id(1)
                .name("João")
                .build();
        System.out.println(person);
    }

}
