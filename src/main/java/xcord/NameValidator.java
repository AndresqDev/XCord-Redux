package xcord;

public final class NameValidator {
    public static boolean isAllowedChar(char c) {
        return c != '\u00a7' && c >= ' ' && c != '\u007f';
    }

    private static boolean isAllowedChar(char c, boolean bl) {
        if (bl) {
            return c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c == '_' || c == '.' || c == '-';
        }
        return NameValidator.isAllowedChar(c) && c != ' ';
    }

    public static boolean isValidName(String string, boolean bl) {
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            if (NameValidator.isAllowedChar(string.charAt(i), bl)) continue;
            return false;
        }
        return true;
    }
}

