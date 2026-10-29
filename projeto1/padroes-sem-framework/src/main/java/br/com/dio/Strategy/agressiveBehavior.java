package br.com.dio.Strategy;

public class agressiveBehavior implements Behavior{
    @Override
    public void move() {
        System.out.println("Movendo-se agressivamente");
    }
}
