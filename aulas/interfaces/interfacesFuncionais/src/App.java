import java.util.List;
import java.util.function.Consumer;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        List<User> users = List.of(new User("Arthur Cruz", 22),
                                   new User("Cesar Voador", 34),
                                   new User("Adilson Perfurante", 41));
        users.forEach(user -> System.out.println(user));                          

    }
}
