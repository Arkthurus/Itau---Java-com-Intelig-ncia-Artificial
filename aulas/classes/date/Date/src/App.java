import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class App {
    public static void main(String[] args) throws Exception {
        
        //APIs de data mais antiga
        //Date mais antida
        var Date = new Date();
        DateFormat formatter =  new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        System.out.printf("-----DATE-----\n" );
        System.out.println(Date);
        System.out.println(formatter.format(Date));
        
        //Calendar mais perfomatica
        var calendar = Calendar.getInstance();
        DateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss Z");
        Date today = calendar.getTime();
        System.out.printf("-----CALENDAR-----\n" );
        System.out.println(formatter2.format(today));

        //Api mais nova
        //OffsetDateTime
        LocalDate  localDate =  LocalDate.now();
        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.printf("-----OffsetDateTime-----\n" );
        System.out.println(localDate);
        System.out.println(formatter3.format(localDate));
        System.out.println(formatter3.format(localDate.plus(1000, ChronoUnit.YEARS)));
        System.out.println(formatter3.format(localDate.plus(1000, ChronoUnit.YEARS).minus(50, ChronoUnit.DAYS)));
        System.out.println(localDate.getMonth());
        System.out.println(localDate.plus(1000, ChronoUnit.YEARS).minus(50, ChronoUnit.DAYS).getDayOfWeek());
    }
}
