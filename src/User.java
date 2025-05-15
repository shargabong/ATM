import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Класс, представляющий пользователя (клиента) банка.
 * Хранит имя, фамилию, UUID, хеш PIN-кода и список счетов пользователя.
 */
public class User {

    private String firstName;
    private String lastName;
    private String uuid;
    private byte pinHash[];
    private ArrayList<Account> accounts;

    /**
     * Конструктор для создания нового пользователя.
     * Генерирует UUID, хеширует PIN-код.
     * @param firstName имя пользователя.
     * @param lastName  фамилия пользователя.
     * @param pin       PIN-код пользователя (будет хеширован).
     * @param theBank   объект Bank, к которому принадлежит пользователь (для генерации UUID).
     * @throws RuntimeException если алгоритм MD5 не найден (что маловероятно в стандартных средах Java).
     */
    public User(String firstName, String lastName, String pin, Bank theBank) {
        this.firstName = firstName;
        this.lastName = lastName;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.pinHash = md.digest(pin.getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error, caught NoSuchAlgorithmException: " + e.getMessage());
            throw new RuntimeException("Could not initialize user due to MD5 hashing error.", e);
        }

        this.uuid = theBank.getNewUserUUID();
        this.accounts = new ArrayList<Account>();
        System.out.printf("New user %s, %s with ID %s created.\n", lastName,
                firstName, this.uuid);
    }

    /**
     * Добавляет счет в список счетов пользователя.
     * @param anAcct объект Account для добавления.
     */
    public void addAccount(Account anAcct) {
        this.accounts.add(anAcct);
    }

    /**
     * Возвращает UUID пользователя.
     * @return строковое представление UUID пользователя.
     */
    public String getUUID() {
        return this.uuid;
    }

    /**
     * Проверяет предоставленный PIN-код путем его хеширования и сравнения с сохраненным хешем.
     * @param aPin PIN-код для проверки.
     * @return true, если PIN-код верен, иначе false.
     */
    public boolean validatePin(String aPin) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return MessageDigest.isEqual(md.digest(aPin.getBytes()),
                    this.pinHash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error, caught NoSuchAlgorithmException during PIN validation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Возвращает имя пользователя.
     * @return имя пользователя.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Выводит в консоль сводку по всем счетам пользователя.
     * Для каждого счета отображается его порядковый номер (для выбора), UUID, баланс и название.
     */
    public void printAccountsSummary() {
        System.out.printf("\n\n%s's accounts summary\n", this.firstName);
        if (this.accounts.isEmpty()) {
            System.out.println("  No accounts yet.");
        } else {
            for (int a = 0; a < this.accounts.size(); a++) {
                System.out.printf("  %d) %s\n", a + 1,
                        this.accounts.get(a).getSummaryLine());
            }
        }
        System.out.println();
    }

    /**
     * Возвращает количество счетов у пользователя.
     * @return количество счетов.
     */
    public int numAccounts() {
        return this.accounts.size();
    }

    /**
     * Выводит историю транзакций для указанного счета пользователя.
     * @param acctIdx индекс счета в списке счетов пользователя (0-индексация).
     */
    public void printAcctTransHistory(int acctIdx) {
        if (acctIdx >= 0 && acctIdx < this.accounts.size()) {
            this.accounts.get(acctIdx).printTransHistory();
        } else {
            System.out.println("Invalid account index provided to printAcctTransHistory.");
        }
    }

    /**
     * Возвращает баланс указанного счета пользователя.
     * @param acctIdx индекс счета (0-индексация).
     * @return баланс счета. Возвращает 0.0 при неверном индексе (можно изменить на выброс исключения).
     */
    public double getAcctBalance(int acctIdx) {
        if (acctIdx >= 0 && acctIdx < this.accounts.size()) {
            return this.accounts.get(acctIdx).getBalance();
        }
        System.err.println("Attempted to get balance for invalid account index: " + acctIdx);
        return 0.0;
    }

    /**
     * Возвращает UUID указанного счета пользователя.
     * @param acctIdx индекс счета (0-индексация).
     * @return UUID счета. Возвращает "INVALID_ACCOUNT_INDEX" при неверном индексе (можно изменить).
     */
    public String getAcctUUID(int acctIdx) {
        if (acctIdx >= 0 && acctIdx < this.accounts.size()) {
            return this.accounts.get(acctIdx).getUUID();
        }
        System.err.println("Attempted to get UUID for invalid account index: " + acctIdx);
        return "INVALID_ACCOUNT_INDEX";
    }

    /**
     * Добавляет транзакцию к указанному счету пользователя.
     * @param acctIdx индекс счета (0-индексация), к которому добавляется транзакция.
     * @param amount  сумма транзакции.
     * @param memo    описание (заметка) для транзакции.
     */
    public void addAcctTransaction(int acctIdx, double amount, String memo) {
        if (acctIdx >= 0 && acctIdx < this.accounts.size()) {
            this.accounts.get(acctIdx).addTransaction(amount, memo);
        } else {
            System.err.println("Attempted to add transaction to invalid account index: " + acctIdx);
        }
    }
}