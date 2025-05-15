import java.util.ArrayList;
import java.util.Random;

/**
 * Класс, представляющий банк.
 * Управляет списком пользователей и счетов, а также генерирует для них уникальные идентификаторы.
 */
public class Bank {

    private String name;
    private ArrayList<User> users;
    private ArrayList<Account> accounts;

    /**
     * Конструктор для создания объекта Bank.
     * @param name название банка.
     */
    public Bank(String name) {
        this.name = name;
        this.users = new ArrayList<User>();
        this.accounts = new ArrayList<Account>();
    }

    /**
     * Генерирует новый уникальный UUID для пользователя.
     * Гарантирует, что UUID не будет дублироваться среди существующих пользователей.
     * @return строковое представление уникального UUID пользователя (6 цифр).
     */
    public String getNewUserUUID() {
        String uuid;
        Random rng = new Random();
        int len = 6;
        boolean nonUnique;

        do {
            uuid = "";
            for (int c = 0; c < len; c++) {
                uuid += ((Integer) rng.nextInt(10)).toString();
            }

            nonUnique = false;
            for (User u : this.users) {
                if (uuid.equals(u.getUUID())) {
                    nonUnique = true;
                    break;
                }
            }
        } while (nonUnique);
        return uuid;
    }

    /**
     * Генерирует новый уникальный UUID для счета.
     * Гарантирует, что UUID не будет дублироваться среди существующих счетов.
     * @return строковое представление уникального UUID счета (10 цифр).
     */
    public String getNewAccountUUID() {
        String uuid;
        Random rng = new Random();
        int len = 10;
        boolean nonUnique;

        do {
            uuid = "";
            for (int c = 0; c < len; c++) {
                uuid += ((Integer) rng.nextInt(10)).toString();
            }

            nonUnique = false;
            for (Account a : this.accounts) {
                if (uuid.equals(a.getUUID())) {
                    nonUnique = true;
                    break;
                }
            }
        } while (nonUnique);
        return uuid;
    }

    /**
     * Добавляет существующий счет в список счетов банка.
     * @param anAcct объект Account для добавления.
     */
    public void addAccount(Account anAcct) {
        this.accounts.add(anAcct);
    }

    /**
     * Создает нового пользователя, добавляет его в список пользователей банка,
     * а также создает для него счет "Savings" по умолчанию.
     * @param firstName имя пользователя.
     * @param lastName  фамилия пользователя.
     * @param pin       PIN-код пользователя.
     * @return созданный объект User.
     */
    public User addUser(String firstName, String lastName, String pin) {
        User newUser = new User(firstName, lastName, pin, this);
        this.users.add(newUser);

        Account newAccount = new Account("Savings", newUser, this);
        newUser.addAccount(newAccount);
        this.addAccount(newAccount);

        return newUser;
    }

    /**
     * Осуществляет аутентификацию пользователя по его ID и PIN-коду.
     * @param userId ID пользователя для входа.
     * @param pin    PIN-код пользователя.
     * @return объект User в случае успешной аутентификации, иначе null.
     */
    public User userLogin(String userId, String pin) {
        for (User u : this.users) {
            if (u.getUUID().equals(userId) && u.validatePin(pin)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Возвращает название банка.
     * @return название банка.
     */
    public String getName() {
        return this.name;
    }
}