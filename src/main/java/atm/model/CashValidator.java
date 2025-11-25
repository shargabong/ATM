package atm.model;

import java.util.Set;

public class CashValidator {

    private static final Set<Integer> VALID_DENOMINATIONS = Set.of(100, 200, 500, 1000, 2000, 5000);

    public boolean validate(Bill bill) throws IllegalArgumentException {
        if (!VALID_DENOMINATIONS.contains(bill.getDenomination())) {
            throw new IllegalArgumentException("НЕИЗВЕСТНЫЙ НОМИНАЛ");
        }

        if (!bill.checkSecurityMarks()) {
            throw new IllegalArgumentException("ФАЛЬШИВАЯ КУПЮРА (ОШИБКА ЗАЩИТЫ)");
        }

        return true;
    }
}