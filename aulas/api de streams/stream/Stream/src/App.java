import java.util.ArrayList;
import java.util.List;

import domain.Contact;
import domain.ContactType;
import domain.Sex;
import domain.User;

public class App {
    public static void main(String[] args) throws Exception {
        List<User> users = generateUsers();
        
        System.out.printf("Lista completa:\n");
        users.forEach(System.out::println);    

        System.out.println("============================================================================================================");
        System.out.println("Lista com user que tem 2 formas de contato:");

        var valuesWith2Contacts = users.stream().filter(u -> u.contacts().size() > 1);
        valuesWith2Contacts.forEach(System.out::println);

        System.out.println("============================================================================================================");
        System.out.println("Lista com user apenas email:");

        var valuesWithEmail = users.stream().filter(u -> u.contacts().stream().anyMatch(c -> c.type() == ContactType.EMAIL ));
        valuesWithEmail.forEach(System.out::println);

        System.out.println("============================================================================================================");
        System.out.println("Lista com user apenas celular:");

        var valuesWithPhone = users.stream().filter(u -> u.contacts().stream().anyMatch(c -> c.type() == ContactType.PHONE ));
        valuesWithPhone.forEach(System.out::println);
         
    }

    private static List<User> generateUsers(){
        var contacts1 = List.of(
            new Contact("12121212", ContactType.PHONE)
        );

        var contacts2 = List.of(
            new Contact("Maria@email.com", ContactType.EMAIL)
        );

        var contacts3 = List.of(
            new Contact("171717", ContactType.PHONE),
            new Contact("Arthur@email.com", ContactType.EMAIL)
        );

        var user1 = new User("Douglgas", 24, Sex.MALE, new ArrayList<>(contacts1));
        var user2 = new User("Maria", 19, Sex.MALE, new ArrayList<>(contacts2));
        var user3 = new User("Arthur", 22, Sex.MALE, new ArrayList<>(contacts3));

        return List.of(user1, user2, user3);
    }
}
