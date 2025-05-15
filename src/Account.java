import java.util.ArrayList;

/**
 * Класс, представляющий банковский счет.
 * Хранит информацию о названии счета, его уникальном идентификаторе (UUID),
 * владельце и списке транзакций.
 */
public class Account {

    private String name;
    private String uuid;
    private User holder;
    private ArrayList<Transaction> transactions;

    /**
     * Конструктор для создания нового счета.
     * @param name    название счета.
     * @param holder  объект User, который является владельцем счета.
     * @param theBank объект Bank, в котором создается счет (для генерации UUID).
     */
    public Account(String name, User holder, Bank theBank) {
        this.name = name;
        this.holder = holder;
        this.uuid = theBank.getNewAccountUUID();
        this.transactions = new ArrayList<Transaction>();
    }

    /**
     * Возвращает UUID счета.
     * @return строковое представление UUID счета.
     */
    public String getUUID() {
        return this.uuid;
    }

    /**
     * Формирует сводную строку с информацией о счете (UUID, баланс, название).
     * @return строка с краткой информацией о счете.
     */
    public String getSummaryLine() {
        double balance = this.getBalance();
        if (balance >= 0) {
            return String.format("%s : $%.02f : %s", this.uuid, balance,
                    this.name);
        } else {
            return String.format("%s : $(%.02f) : %s", this.uuid, Math.abs(balance),
                    this.name);
        }
    }

    /**
     * Рассчитывает и возвращает текущий баланс счета на основе всех транзакций.
     * @return текущий баланс счета.
     */
    public double getBalance() {
        double balance = 0;
        for (Transaction t : this.transactions) {
            balance += t.getAmount();
        }
        return balance;
    }

    /**
     * Выводит историю транзакций для данного счета в консоль.
     * Транзакции выводятся в обратном хронологическом порядке (самые новые сначала).
     */
    public void printTransHistory() {
        System.out.printf("\nTransaction history for account %s (%s):\n", this.uuid, this.name);
        if (this.transactions.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            for (int t = this.transactions.size() - 1; t >= 0; t--) {
                System.out.println(this.transactions.get(t).getSummaryLine());
            }
        }
        System.out.println();
    }

    /**
     * Добавляет новую транзакцию к счету.
     * @param amount сумма транзакции (положительная для депозита, отрицательная для снятия).
     * @param memo   описание (заметка) для транзакции.
     */
    public void addTransaction(double amount, String memo) {
        Transaction newTrans = new Transaction(amount, memo, this);
        this.transactions.add(newTrans);
    }
}