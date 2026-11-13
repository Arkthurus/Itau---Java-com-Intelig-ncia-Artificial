package br.com.dio;

import java.lang.reflect.InvocationTargetException;

import br.com.dio.models.Person;
import br.com.dio.models.User;
import br.com.dio.processors.SerializerProcessor;

public class Main {
    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        var processor = new SerializerProcessor();
        System.out.println(processor.serializer(new Person(1, "Joao Kleber", 22)));
        System.out.println(processor.serializer(new User(2, "Maria Kreusa", 19, 24568)));
    }   
}