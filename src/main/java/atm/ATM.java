package atm;

import java.util.Map;
import java.util.Scanner;

/**
 * Основной класс для эмуляции работы банкомата (ATM) в консольном режиме.
 * <p>
 * <b>Примечание:</b> Этот класс не используется в GUI-версии приложения.
 * Главным классом для графического интерфейса является {@code App.java}.
 * Этот файл сохранен для демонстрации первоначальной логики и для возможности
 * тестирования бэкенда в консоли.
 * </p>
 */
public class ATM {

    private static CashDispenser dispenser = new CashDispenser();

    /**
     * Главный метод, точка входа в консольное приложение ATM.
     * @param args аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Bank theBank = new Bank("Bank of BOMBA");
        User aUser = theBank.addUser("Иван", "Иванов", "1234");

        Account newAccount = new Account("Текущий", aUser, theBank);
        aUser.addAccount(newAccount);
        theBank.addAccount(newAccount);

        User curUser;
        while (true) {
            curUser = ATM.mainMenuPrompt(theBank, sc);
            ATM.printUserMenu(curUser, sc);
        }
    }

    /**
     * Отображает приветственное сообщение и запрашивает у пользователя ID и PIN.
     * @param theBank банк, в котором производится аутентификация.
     * @param sc      сканер для чтения ввода пользователя.
     * @return        объект User в случае успешной аутентификации.
     */
    public static User mainMenuPrompt(Bank theBank, Scanner sc) {
        String userID;
        String pin;
        User authUser;

        do {
            System.out.printf("\n\nДобро пожаловать в %s\n\n", theBank.getName());
            System.out.print("Введите ID пользователя: ");
            userID = sc.nextLine();
            System.out.print("Введите PIN-код: ");
            pin = sc.nextLine();

            authUser = theBank.userLogin(userID, pin);
            if (authUser == null) {
                System.out.println("Неверная комбинация ID/PIN. Пожалуйста, попробуйте снова.");
            }
        } while (authUser == null);

        return authUser;
    }

    /**
     * Отображает меню опций для аутентифицированного пользователя.
     * @param theUser пользователь, для которого отображается меню.
     * @param sc      сканер для чтения ввода пользователя.
     */
    public static void printUserMenu(User theUser, Scanner sc) {
        theUser.getAccount(0).getBalance(); // Пример вызова, чтобы показать, что User работает
        int choice;

        do {
            System.out.printf("Добро пожаловать, %s! Что вы хотите сделать?\n", theUser.getFirstName());
            System.out.println("  1) Показать историю транзакций");
            System.out.println("  2) Снять наличные");
            System.out.println("  3) Внести наличные");
            System.out.println("  4) Перевод");
            System.out.println("  5) Выход");
            System.out.println();
            System.out.print("Ваш выбор: ");
            choice = sc.nextInt();
            sc.nextLine();

            if (choice < 1 || choice > 5) {
                System.out.println("Неверный выбор. Пожалуйста, выберите от 1 до 5.");
            }
        } while (choice < 1 || choice > 5);

        switch (choice) {
            case 1:
                ATM.showTransHistory(theUser, sc);
                break;
            case 2:
                ATM.withdrawFunds(theUser, sc);
                break;
            case 3:
                ATM.depositFunds(theUser, sc);
                break;
            case 4:
                ATM.transferFunds(theUser, sc);
                break;
            case 5:
                break;
        }

        if (choice != 5) {
            ATM.printUserMenu(theUser, sc);
        }
    }

    /**
     * Отображает историю транзакций для выбранного счета.
     * @param theUser пользователь.
     * @param sc      сканер.
     */
    public static void showTransHistory(User theUser, Scanner sc) {
        int theAcct;
        do {
            System.out.printf("Введите номер счета (1-%d): ", theUser.numAccounts());
            theAcct = sc.nextInt() - 1;
            sc.nextLine();
            if (theAcct < 0 || theAcct >= theUser.numAccounts()) {
                System.out.println("Неверный счет. Пожалуйста, попробуйте снова.");
            }
        } while (theAcct < 0 || theAcct >= theUser.numAccounts());
        // theUser.printAcctTransHistory(theAcct); // если он нужен
    }

    /**
     * Обрабатывает перевод средств между счетами.
     * @param theUser пользователь.
     * @param sc      сканер.
     */
    public static void transferFunds(User theUser, Scanner sc) {
        int fromAcct, toAcct;
        double amount, acctBal;

        do {
            System.out.printf("Счет списания (1-%d): ", theUser.numAccounts());
            fromAcct = sc.nextInt() - 1;
            if (fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
                System.out.println("Неверный счет.");
            }
        } while (fromAcct < 0 || fromAcct >= theUser.numAccounts());
        acctBal = theUser.getAcctBalance(fromAcct);

        do {
            System.out.printf("Счет зачисления (1-%d): ", theUser.numAccounts());
            toAcct = sc.nextInt() - 1;
            if (toAcct < 0 || toAcct >= theUser.numAccounts()) {
                System.out.println("Неверный счет.");
            } else if (toAcct == fromAcct) {
                System.out.println("Нельзя перевести на тот же счет.");
            }
        } while (toAcct < 0 || toAcct >= theUser.numAccounts() || toAcct == fromAcct);

        do {
            System.out.printf("Введите сумму (макс. %.02f руб.): ", acctBal);
            amount = sc.nextDouble();
            if (amount < 0) {
                System.out.println("Сумма должна быть больше нуля.");
            } else if (amount > acctBal) {
                System.out.printf("Сумма не должна превышать баланс в %.02f руб.\n", acctBal);
            }
        } while (amount < 0 || amount > acctBal);

        sc.nextLine();
        theUser.addAcctTransaction(fromAcct, -1 * amount, "Перевод на счет " + theUser.getAcctUUID(toAcct));
        theUser.addAcctTransaction(toAcct, amount, "Перевод со счета " + theUser.getAcctUUID(fromAcct));
    }

    /**
     * Обрабатывает снятие средств со счета.
     * @param theUser пользователь.
     * @param sc      сканер.
     */
    public static void withdrawFunds(User theUser, Scanner sc) {
        int fromAcct;
        double amount, acctBal;
        String memo;

        do {
            System.out.printf("Счет списания (1-%d): ", theUser.numAccounts());
            fromAcct = sc.nextInt() - 1;
            if (fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
                System.out.println("Неверный счет.");
            }
        } while (fromAcct < 0 || fromAcct >= theUser.numAccounts());
        acctBal = theUser.getAcctBalance(fromAcct);

        do {
            System.out.printf("Введите сумму для снятия (макс. %.02f руб.): ", acctBal);
            amount = sc.nextDouble();
            if (amount < 0) {
                System.out.println("Сумма должна быть больше нуля.");
            } else if (amount > acctBal) {
                System.out.printf("Сумма не должна превышать баланс в %.02f руб.\n", acctBal);
            }
        } while (amount < 0 || amount > acctBal);
        sc.nextLine();

        Map<Integer, Integer> billsToDispense = dispenser.calculateWithdrawal((int) amount);

        if (billsToDispense == null) {
            System.out.println("Ошибка: Невозможно выдать данную сумму имеющимися купюрами.");
            return;
        }
        
        System.out.print("Введите комментарий: ");
        memo = sc.nextLine();

        theUser.addAcctTransaction(fromAcct, -1 * amount, memo);
        dispenser.dispense(billsToDispense);

        System.out.println("Пожалуйста, возьмите ваши наличные:");
        for (Map.Entry<Integer, Integer> entry : billsToDispense.entrySet()) {
            System.out.printf("  %d x %d руб.\n", entry.getValue(), entry.getKey());
        }
    }

    /**
     * Обрабатывает внесение средств на счет.
     * @param theUser пользователь.
     * @param sc      сканер.
     */
    public static void depositFunds(User theUser, Scanner sc) {
        int toAcct;
        double amount;
        String memo;
        
        do {
            System.out.printf("Счет зачисления (1-%d): ", theUser.numAccounts());
            toAcct = sc.nextInt() - 1;
            if (toAcct < 0 || toAcct >= theUser.numAccounts()) {
                System.out.println("Неверный счет.");
            }
        } while (toAcct < 0 || toAcct >= theUser.numAccounts());
        
        do {
            System.out.print("Введите сумму для внесения: ");
            amount = sc.nextDouble();
            if (amount < 0) {
                System.out.println("Сумма должна быть больше нуля.");
            }
        } while (amount < 0);
        sc.nextLine();
        
        System.out.print("Введите комментарий: ");
        memo = sc.nextLine();

        theUser.addAcctTransaction(toAcct, amount, memo);
    }
}