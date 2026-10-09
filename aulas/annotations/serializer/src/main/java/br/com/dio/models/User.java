package br.com.dio.models;

import br.com.dio.annotations.SerializerType;

@SerializerType 
public record User(
    long id,
    String fullName,
    int age,
    double salary
){
}
