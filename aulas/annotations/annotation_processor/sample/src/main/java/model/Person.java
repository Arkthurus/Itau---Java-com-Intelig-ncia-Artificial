package model;

import br.com.dio.Builder;

@Builder
public class Person {

    private String name;
    private int id;

    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public void setId(final int id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return "Person {name=" + name + ", id=" + id + '\'' + "}";
    }
}
