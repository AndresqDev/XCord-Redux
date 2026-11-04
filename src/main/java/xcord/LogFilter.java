package xcord;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import net.md_5.bungee.BungeeCord;
import xcord.ReloadableService;
import xcord.XCord;

public class LogFilter
implements Filter,
ReloadableService {
    @Override
    public boolean isLoggable(LogRecord logRecord) {
        for (String string : XCord.getConfig().getBlockedIsoCodesList()) {
            if (!logRecord.getMessage().contains(string)) continue;
            return false;
        }
        if (logRecord.getParameters() != null && logRecord.getParameters().length > 0) {
            for (Object object : logRecord.getParameters()) {
                if (object == null) continue;
                for (String string : XCord.getConfig().getBlockedIsoCodesList()) {
                    if (!object.toString().contains(string)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void reload() throws Exception {
        Logger logger = BungeeCord.getInstance().getLogger();
        if (logger.getFilter() instanceof LogFilter) {
            if (!XCord.getConfig().getLogFilterEnabled()) {
                logger.setFilter(null);
            }
        } else if (XCord.getConfig().getLogFilterEnabled()) {
            logger.setFilter(this);
        }
    }
}

