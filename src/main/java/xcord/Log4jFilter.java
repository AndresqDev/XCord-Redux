package xcord;

import net.md_5.bungee.protocol.BadPacketException;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Log4jFilter {
    private static final BadPacketException a = new BadPacketException("Detected a Log4j expression!");

    public static String a(String string) {
        int n = string.indexOf("${");
        if (n >= 0 && string.indexOf(125, n + 1) > 0) {
            throw a;
        }
        return string;
    }

    public static String a(ProtocolConstants.Direction direction, String string) {
        if (direction == ProtocolConstants.Direction.TO_SERVER) {
            return Log4jFilter.a(string);
        }
        return string;
    }
}

