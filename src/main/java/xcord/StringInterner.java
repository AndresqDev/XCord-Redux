package xcord;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StringInterner {
    private static final Map<String, String> CACHE = new ConcurrentHashMap<String, String>();

    public static String intern(String string) {
        String string2 = CACHE.get(string);
        if (string2 != null) {
            return string2;
        }
        CACHE.put(string, string);
        return string;
    }
}

