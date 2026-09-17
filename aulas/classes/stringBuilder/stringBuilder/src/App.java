import java.time.Duration;
import java.time.OffsetDateTime;

public class App {
    public static void main(String[] args) throws Exception {
        String str = "value";
        var stringStart = OffsetDateTime.now();
        for (int i = 0; i < 400_000; i++) {
            str += i;
        }
        var stringEnd = OffsetDateTime.now();
        System.out.printf("String time: %s ms\n", Duration.between(stringStart, stringEnd).toSeconds());

        var builderStart = OffsetDateTime.now();
        StringBuilder stringConcat = new StringBuilder("value");
        for (int i = 0; i < 400_000; i++) {
            stringConcat.append(i);
        }
        var builderEnd = OffsetDateTime.now();
        System.out.printf("StringBuilder time: %s ms\n", Duration.between(builderStart, builderEnd).toSeconds());
        
        StringBuffer stringBuffer = new StringBuffer("value");
        var bufferStart = OffsetDateTime.now();
        for (int i = 0; i < 400_000; i++) {
            stringBuffer.append(i);
        }
        var bufferEnd = OffsetDateTime.now();
        System.out.printf("StringBuffer time: %s ms\n", Duration.between(bufferStart, bufferEnd).toSeconds());
    }
}
