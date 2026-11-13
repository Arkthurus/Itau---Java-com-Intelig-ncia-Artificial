import java.util.Optional;

public class App {
    public static void main(String[] args) throws Exception {
        Optional<User> optional = Optional.of(new User("Arthur", 22, SexEnum.MALE));    
        
        //o or.Else usa o metodo direto / valor padrão
        System.out.println(optional.orElse(defaultUser()));

        //o orElseGet usa Lambdas para passar alguma logica para uma possivel busca
        System.out.println(optional.orElseGet(App::defaultUser));
    }

    public  static  User defaultUser(){
        System.out.println("Buscando valor default");
        return new  User("Maria", 19, SexEnum.FEMALE);
    }
} 
