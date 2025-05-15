import java.util.Scanner;

/**
 * Основной класс для эмуляции работы банкомата (ATM).
 * Обрабатывает взаимодействие с пользователем, включая вход в систему и выполнение операций.
 */
public class ATM {

    /**
     * Главный метод, точка входа в приложение ATM.
     * Инициализирует банк, тестового пользователя и его счета, затем входит в основной цикл операций.
     * @param args аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Bank theBank = new Bank("Bank of Drausin");
        User aUser = theBank.addUser("Bob", "Pencil", "1234");

        Account newAccount = new Account("Checking", aUser, theBank);
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
     * Проверяет учетные данные через объект Bank.
     * @param theBank банк, в котором производится аутентификация.
     * @param sc      сканер для чтения ввода пользователя.
     * @return        объект User в случае успешной аутентификации, иначе повторяет запрос.
     */
    public static User mainMenuPrompt(Bank theBank, Scanner sc) {
        String userID;
        String pin;
        User authUser;

        do {
            System.out.printf("\n\nWelcome to %s\n\n", theBank.getName());
            System.out.print("Enter user ID: ");
            userID = sc.nextLine();
            System.out.print("Enter pin: ");
            pin = sc.nextLine();

            authUser = theBank.userLogin(userID, pin);
            if (authUser == null) {
                System.out.println("Incorrect user ID/pin combination. " +
                        "Please try again.");
            }
        } while (authUser == null);

        return authUser;
    }

    /**
     * Отображает меню опций для аутентифицированного пользователя.
     * Обрабатывает выбор пользователя и вызывает соответствующие методы.
     * @param theUser пользователь, для которого отображается меню.
     * @param sc      сканер для чтения ввода пользователя.
     */
    public static void printUserMenu(User theUser, Scanner sc) {
        theUser.printAccountsSummary();
        int choice;

        do {
            System.out.printf("Welcome %s, what would you like to do?\n",
                    theUser.getFirstName());
            System.out.println("  1) Show account transaction history");
            System.out.println("  2) Withdraw");
            System.out.println("  3) Deposit");
            System.out.println("  4) Transfer");
            System.out.println("  5) Quit");
            System.out.println();
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            if (choice < 1 || choice > 5) {
                System.out.println("Invalid choice. Please choose 1-5");
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
     * Отображает историю транзакций для выбранного счета пользователя.
     * @param theUser пользователь, чьи транзакции просматриваются.
     * @param sc      сканер для чтения ввода пользователя (выбор счета).
     */
    public static void showTransHistory(User theUser, Scanner sc) {
        int theAcct;

        do {
            System.out.printf("Enter the number (1-%d) of the account\n" +
                            "whose transactions you want to see: ",
                    theUser.numAccounts());
            theAcct = sc.nextInt() - 1;
            sc.nextLine();
            if (theAcct < 0 || theAcct >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while (theAcct < 0 || theAcct >= theUser.numAccounts());

        theUser.printAcctTransHistory(theAcct);
    }

    /**
     * Обрабатывает перевод средств между счетами пользователя.
     * @param theUser пользователь, выполняющий перевод.
     * @param sc      сканер для чтения ввода пользователя (счета, сумма).
     */
    public static void transferFunds(User theUser, Scanner sc) {
        int fromAcct;
        int toAcct;
        double amount;
        double acctBal;

        do {
            System.out.printf("Enter the number (1-%d) of the account\n" +
                    "to transfer from: ", theUser.numAccounts());
            fromAcct = sc.nextInt() - 1;
            sc.nextLine();
            if (fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while (fromAcct < 0 || fromAcct >= theUser.numAccounts());
        acctBal = theUser.getAcctBalance(fromAcct);

        do {
            System.out.printf("Enter the number (1-%d) of the account\n" +
                    "to transfer to: ", theUser.numAccounts());
            toAcct = sc.nextInt() - 1;
            sc.nextLine();
            if (toAcct < 0 || toAcct >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            } else if (toAcct == fromAcct) {
                System.out.println("Cannot transfer to the same account. Please try again.");
            }
        } while (toAcct < 0 || toAcct >= theUser.numAccounts() || toAcct == fromAcct);

        do {
            System.out.printf("Enter the amount to transfer (max $%.02f): $",
                    acctBal);
            amount = sc.nextDouble();
            sc.nextLine();
            if (amount < 0) {
                System.out.println("Amount must be greater than zero.");
            } else if (amount > acctBal) {
                System.out.printf("Amount must not be greater than\n" +
                        "balance of $%.02f.\n", acctBal);
            }
        } while (amount < 0 || amount > acctBal);

        theUser.addAcctTransaction(fromAcct, -1 * amount, String.format(
                "Transfer to account %s", theUser.getAcctUUID(toAcct)));
        theUser.addAcctTransaction(toAcct, amount, String.format(
                "Transfer from account %s", theUser.getAcctUUID(fromAcct)));
    }

    /**
     * Обрабатывает снятие средств со счета пользователя.
     * @param theUser пользователь, снимающий средства.
     * @param sc      сканер для чтения ввода пользователя (счет, сумма, заметка).
     */
    public static void withdrawFunds(User theUser, Scanner sc) {
        int fromAcct;
        double amount;
        double acctBal;
        String memo;

        do {
            System.out.printf("Enter the number (1-%d) of the account\n" +
                    "to withdraw from: ", theUser.numAccounts());
            fromAcct = sc.nextInt() - 1;
            sc.nextLine();
            if (fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while (fromAcct < 0 || fromAcct >= theUser.numAccounts());
        acctBal = theUser.getAcctBalance(fromAcct);

        do {
            System.out.printf("Enter the amount to withdraw (max $%.02f): $",
                    acctBal);
            amount = sc.nextDouble();
            sc.nextLine();
            if (amount < 0) {
                System.out.println("Amount must be greater than zero.");
            } else if (amount > acctBal) {
                System.out.printf("Amount must not be greater than\n" +
                        "balance of $%.02f.\n", acctBal);
            }
        } while (amount < 0 || amount > acctBal);

        System.out.print("Enter a memo: ");
        memo = sc.nextLine();

        theUser.addAcctTransaction(fromAcct, -1 * amount, memo);
    }

    /**
     * Обрабатывает внесение средств на счет пользователя.
     * @param theUser пользователь, вносящий средства.
     * @param sc      сканер для чтения ввода пользователя (счет, сумма, заметка).
     */
    public static void depositFunds(User theUser, Scanner sc) {
        int toAcct;
        double amount;
        String memo;

        do {
            System.out.printf("Enter the number (1-%d) of the account\n" +
                    "to deposit in: ", theUser.numAccounts());
            toAcct = sc.nextInt() - 1;
            sc.nextLine();
            if (toAcct < 0 || toAcct >= theUser.numAccounts()) {
                System.out.println("Invalid account. Please try again.");
            }
        } while (toAcct < 0 || toAcct >= theUser.numAccounts());

        do {
            System.out.print("Enter the amount to deposit: $");
            amount = sc.nextDouble();
            sc.nextLine();
            if (amount < 0) {
                System.out.println("Amount must be greater than zero.");
            }
        } while (amount < 0);

        System.out.print("Enter a memo: ");
        memo = sc.nextLine();

        theUser.addAcctTransaction(toAcct, amount, memo);
    }
}