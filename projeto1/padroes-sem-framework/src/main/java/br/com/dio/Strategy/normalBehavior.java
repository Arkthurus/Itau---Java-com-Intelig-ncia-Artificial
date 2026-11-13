package br.com.dio.Strategy;

public class normalBehavior implements Behavior{
    @Override
    public void move() {
        System.out.println("Movendo-se normalmente");
    }
}
