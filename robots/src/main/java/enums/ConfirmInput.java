package enums;

import java.util.Arrays;

public enum ConfirmInput {
    YES, NO;

    public static String[] getStringValues() {
        return Arrays.stream(ConfirmInput.values())
                .map(Enum::name)
                .toArray(String[]::new);
    }
}
