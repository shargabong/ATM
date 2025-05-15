import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Класс, представляющий одну банковскую транзакцию.
 * Хранит сумму, временную метку, описание (memo) и ссылку на счет, к которому относится транзакция.
 */
public class Transaction {

    private double amount;
    private Date timestamp;
    private String memo;
    private Account inAccount;

    /**
     * Конструктор для создания транзакции без явного описания (memo).
     * @param amount    сумма транзакции.
     * @param inAccount счет, на котором происходит транзакция.
     */
    public Transaction(double amount, Account inAccount) {
        this.amount = amount;
        this.inAccount = inAccount;
        this.timestamp = new Date();
        this.memo = "";
    }

    /**
     * Конструктор для создания транзакции с явным описанием (memo).
     * Использует другой конструктор для инициализации общих полей.
     * @param amount    сумма транзакции.
     * @param memo      описание транзакции.
     * @param inAccount счет, на котором происходит транзакция.
     */
    public Transaction(double amount, String memo, Account inAccount) {
        this(amount, inAccount);
        this.memo = memo;
    }

    /**
     * Возвращает сумму транзакции.
     * @return сумма транзакции.
     */
    public double getAmount() {
        return this.amount;
    }

    /**
     * Формирует сводную строку с информацией о транзакции (дата, сумма, описание).
     * @return строка с краткой информацией о транзакции.
     */
    public String getSummaryLine() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = sdf.format(this.timestamp);

        if (this.amount >= 0) {
            return String.format("%s : $%.02f : %s", formattedTimestamp,
                    this.amount, this.memo);
        } else {
            return String.format("%s : $(%.02f) : %s",
                    formattedTimestamp, -this.amount, this.memo);
        }
    }
}