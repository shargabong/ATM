package atm.util;

import atm.model.Bank;
import java.io.*;

public class DataManager {

    private static final String DATA_FILE = "bank.dat";

    public static void saveData(Bank bank) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(bank);
            System.out.println("Данные успешно сохранены в " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

    public static Bank loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Bank) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке данных: " + e.getMessage());
            return null;
        }
    }
}