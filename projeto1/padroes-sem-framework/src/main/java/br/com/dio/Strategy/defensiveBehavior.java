package br.com.dio.Strategy;

public class defensiveBehavior implements Behavior{
    @Override
    public void move() {
        System.out.println("Movendo-se defensivamente");
    }
}
