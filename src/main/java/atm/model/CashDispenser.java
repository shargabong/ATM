package atm.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CashDispenser {
    private ArrayList<CashCassette> cassettes;

    public CashDispenser() {
        cassettes = new ArrayList<>();
        cassettes.add(new CashCassette(5000, 20));
        cassettes.add(new CashCassette(2000, 30));
        cassettes.add(new CashCassette(1000, 50));
        cassettes.add(new CashCassette(500, 100));
        cassettes.add(new CashCassette(100, 200));
        cassettes.sort(Comparator.comparingInt(CashCassette::getDenomination).reversed());
    }

    public Map<Integer, Integer> calculateWithdrawal(int amount) {
        Map<Integer, Integer> toDispense = new HashMap<>();
        int remainingAmount = amount;
        for (CashCassette cassette : cassettes) {
            if (remainingAmount == 0) break;
            int billValue = cassette.getDenomination();
            int billsAvailable = cassette.getCount();
            if (remainingAmount >= billValue && billsAvailable > 0) {
                int billsNeeded = remainingAmount / billValue;
                int billsToTake = Math.min(billsNeeded, billsAvailable);
                if (billsToTake > 0) {
                    toDispense.put(billValue, billsToTake);
                    remainingAmount -= (billsToTake * billValue);
                }
            }
        }
        return (remainingAmount == 0) ? toDispense : null;
    }

    public void dispense(Map<Integer, Integer> billsToDispense) {
        for (Map.Entry<Integer, Integer> entry : billsToDispense.entrySet()) {
            for (CashCassette cassette : cassettes) {
                if (cassette.getDenomination() == entry.getKey()) {
                    cassette.removeBills(entry.getValue());
                    break;
                }
            }
        }
    }
}