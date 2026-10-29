public class App {
    public static void main(String[] args) throws Exception {
        int option;
        boolean on = true; 
        while (on) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            System.out.println("Escolha uma operação:");
            System.out.println("1 - Soma");
            System.out.println("2 - Subtração");
            System.out.println("3 - Multiplicação");
            System.out.println("4 - Divisão");
            System.out.println("5 - Sair");

            option = Integer.parseInt(System.console().readLine());

            switch (option) {
                case 1:
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();    
                    System.out.println("Você escolheu a operação de soma.");
                    int num1 = Integer.parseInt(System.console().readLine("Digite o primeiro número: "));
                    int num2 = Integer.parseInt(System.console().readLine("Digite o segundo número: "));
                    int result = operationEnum.SUM.getCalculate().apply(num1, num2);
                    System.out.println("O resultado da soma é: " + result);
                    Thread.sleep(1000);
                    break;
                case 2:
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();    
                    System.out.println("Você escolheu a operação de subtração.");
                    int num1_sub = Integer.parseInt(System.console().readLine("Digite o primeiro número: "));
                    int num2_sub = Integer.parseInt(System.console().readLine("Digite o segundo número: "));
                    int result_sub = operationEnum.SUBTRACTION.getCalculate().apply(num1_sub, num2_sub);
                    System.out.println("O resultado da subtração é: " + result_sub);
                    Thread.sleep(1000);
                    break;
                case 3:
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();    
                    System.out.println("Você escolheu a operação de multiplicação.");
                    int num1_mul = Integer.parseInt(System.console().readLine("Digite o primeiro número: "));
                    int num2_mul = Integer.parseInt(System.console().readLine("Digite o segundo número: "));
                    int result_mul = operationEnum.MULTIPLICATION.getCalculate().apply(num1_mul, num2_mul);
                    System.out.println("O resultado da multiplicação é: " + result_mul);
                    Thread.sleep(1000);
                    break;
                case 4:
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();    
                    System.out.println("Você escolheu a operação de divisão.");
                    int num1_div = Integer.parseInt(System.console().readLine("Digite o primeiro número: "));
                    int num2_div = Integer.parseInt(System.console().readLine("Digite o segundo número: "));
                    int result_div = operationEnum.DIVISION.getCalculate().apply(num1_div, num2_div);
                    System.out.println("O resultado da divisão é: " + result_div);
                    Thread.sleep(1000);
                    break;
                case 5:
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();    
                    Thread.sleep(1000);
                    System.out.println("Saindo do programa...");
                    on = false;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }            
        }
    }
}
