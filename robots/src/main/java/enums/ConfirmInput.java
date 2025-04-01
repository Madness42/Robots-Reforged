package enums;

import java.util.Arrays;

public enum ConfirmInput {
    YES, NO;

    public static String[] getValuesTranslated() {
        return new String[]{"Да", "Нет"};
    }
}
