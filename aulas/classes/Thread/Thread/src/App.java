
import java.util.Scanner;

public class App {

    static class Conta {
        private int saldo;

        public Conta(int saldoInicial) {
            this.saldo = saldoInicial;
        }

        public void depositar(int valor) {
            validarValor(valor);
            saldo += valor;
        }

        public void sacar(int valor) {
            validarValor(valor);

            if (valor > saldo) {
                throw new IllegalArgumentException("Erro: saldo insuficiente".toUpperCase());
            }

            saldo -= valor;
        }

        public int getSaldo() {
            return saldo;
        }

        private void validarValor(int valor) {
            if (valor <= 0) {
                throw new IllegalArgumentException("Erro: valor invalido".toUpperCase());
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String[] stringbase = scanner.nextLine().trim().split(" ");
        String operacao = stringbase[0].trim();
        int saldoInicial = Integer.parseInt(stringbase[1].trim());
        int valorOperacao = Integer.parseInt(stringbase[2].trim());

        Conta conta = new Conta(saldoInicial);

        try {
            if ("DEPOSITAR".equals(operacao)) {
                conta.depositar(valorOperacao);
                System.out.println("OK " + conta.getSaldo());
            } else if ("SACAR".equals(operacao)) {
                conta.sacar(valorOperacao);
                System.out.println("OK " + conta.getSaldo());
            } else {
                System.out.println("Erro: operacao invalida".toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        scanner.close();
    }
}