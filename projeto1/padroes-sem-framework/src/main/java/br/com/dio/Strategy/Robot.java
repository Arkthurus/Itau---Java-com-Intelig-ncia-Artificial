package br.com.dio.Strategy;

public class Robot {
    private Behavior strategy;

    public void setStrategy(Behavior strategy) {
        this.strategy = strategy;
    }

    public void move(){
        strategy.move();
    }
}
