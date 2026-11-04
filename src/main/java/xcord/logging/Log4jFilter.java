package xcord.logging;

import net.md_5.bungee.protocol.BadPacketException;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Log4jFilter {
    private static final BadPacketException EXPRESSION_EXCEPTION = new BadPacketException("Detected a Log4j expression!");

    public static String filter(String message) {
        int expressionStart = message.indexOf("${");
        if (expressionStart >= 0 && message.indexOf('}', expressionStart + 1) > 0) {
            throw EXPRESSION_EXCEPTION;
        }
        return message;
    }

    public static String filter(ProtocolConstants.Direction direction, String message) {
        if (direction == ProtocolConstants.Direction.TO_SERVER) {
            return filter(message);
        }
        return message;
    }
}
