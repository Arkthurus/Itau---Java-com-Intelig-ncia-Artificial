import dao.GenericDAO;
import dao.UserDAO;
import domain.UserDomain;

public class App {
    private static GenericDAO<Integer, UserDomain>  dao = new UserDAO();
    public static void main(String[] args) throws Exception {
        System.out.println(dao.count());
        var user1 = new UserDomain(1, "Arthur", 22);
        System.out.println(dao.save(user1));
        System.out.println(dao.find(d -> d.getId().equals(1)));
        System.out.println(dao.find(d -> d.getId().equals(2)));
        System.out.println(dao.findAll());
        System.out.println(dao.delete(user1));
        System.out.println(dao.count());
    }
}
