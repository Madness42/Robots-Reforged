package enums;

public enum ConfirmInput {
    YES ("Да"),
    NO ("Нет");

    private final String title;

    ConfirmInput(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static String[] getTitles() {
        ConfirmInput[] values = ConfirmInput.values();
        String[] titles = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            titles[i] = values[i].getTitle();
        }
        return titles;
    }
}
