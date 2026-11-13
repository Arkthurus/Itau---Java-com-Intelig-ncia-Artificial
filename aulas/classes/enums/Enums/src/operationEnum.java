import java.util.function.BiFunction;

public enum operationEnum {
    
    SUM((Integer v1, Integer v2) -> v1 + v2),
    SUBTRACTION((Integer v1, Integer v2) -> v1 - v2),
    MULTIPLICATION((Integer v1, Integer v2) -> v1 * v2),
    DIVISION((Integer v1, Integer v2) -> v1 / v2);   

    private final BiFunction<Integer, Integer, Integer> calculate;

    operationEnum(BiFunction<Integer, Integer, Integer> calculate) {
        this.calculate = calculate;
    };

    public BiFunction<Integer, Integer, Integer> getCalculate() {
        return calculate;
    }
}
