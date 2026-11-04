package xcord.logging;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import net.md_5.bungee.BungeeCord;
import xcord.XCord;
import xcord.service.ReloadableService;

public class LogFilter implements Filter, ReloadableService {
    @Override
    public boolean isLoggable(LogRecord logRecord) {
        for (String blockedIsoCode : XCord.getConfig().getBlockedIsoCodesList()) {
            if (logRecord.getMessage().contains(blockedIsoCode)) {
                return false;
            }
        }
        Object[] parameters = logRecord.getParameters();
        if (parameters != null && parameters.length > 0) {
            for (Object parameter : parameters) {
                if (parameter == null) {
                    continue;
                }
                for (String blockedIsoCode : XCord.getConfig().getBlockedIsoCodesList()) {
                    if (parameter.toString().contains(blockedIsoCode)) {
                        return false;
                    }
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
