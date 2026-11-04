package xcord.antibot;

public final class NameValidator {
    public static boolean isAllowedChar(char character) {
        return character != '\u00a7' && character >= ' ' && character != '\u007f';
    }

    private static boolean isAllowedChar(char character, boolean strict) {
        if (strict) {
            return character >= 'a' && character <= 'z'
                || character >= '0' && character <= '9'
                || character >= 'A' && character <= 'Z'
                || character == '_'
                || character == '.'
                || character == '-';
        }
        return isAllowedChar(character) && character != ' ';
    }

    public static boolean isValidName(String name, boolean strict) {
        int length = name.length();
        for (int i = 0; i < length; ++i) {
            if (!isAllowedChar(name.charAt(i), strict)) {
                return false;
            }
        }
        return true;
    }
}
