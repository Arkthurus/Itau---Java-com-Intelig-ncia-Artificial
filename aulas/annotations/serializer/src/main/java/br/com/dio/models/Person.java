package br.com.dio.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.dio.annotations.FieldFormatEnum;
import br.com.dio.annotations.SerializerMethod;
import br.com.dio.annotations.SerializerType;

@SerializerType(fieldFormat = FieldFormatEnum.KEBAB_CASE, prettify = false)
public class Person {

    private long id;
    private String name;
    private int age;
    
    public Person() {
    }
    
    public Person(final long id,final String name,final int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    @SerializerMethod("personFirstName")
    public String getFirstName(){
        return name.split(" ")[0];
    }
}
