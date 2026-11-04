package xcord.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StringInterner {
    private static final Map<String, String> CACHE = new ConcurrentHashMap<String, String>();

    public static String intern(String value) {
        String cached = CACHE.get(value);
        if (cached != null) {
            return cached;
        }
        CACHE.put(value, value);
        return value;
    }
}
