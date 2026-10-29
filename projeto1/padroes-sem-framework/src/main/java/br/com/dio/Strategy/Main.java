package br.com.dio.Strategy;

public class Main {
    public static void main(String[] args) {

        Behavior normal = new normalBehavior();
        Behavior defensive = new defensiveBehavior();
        Behavior agressive = new agressiveBehavior();

        Robot r1 = new Robot();
        r1.setStrategy(normal);
        r1.move();

        r1.setStrategy(agressive);
        r1.move();

        r1.setStrategy(defensive);
        r1.move();
    }
}
