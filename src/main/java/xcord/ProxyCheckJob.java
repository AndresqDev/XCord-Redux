package xcord;

import me.minebuilders.proxyfork.antiproxy.ProxyResult;
import net.md_5.bungee.api.Callback;
import xcord.AsyncJob;
import xcord.XCord;

public class ProxyCheckJob
implements AsyncJob {
    private final String name;
    private final String ip;
    private Callback<ProxyResult> callback;

    public ProxyCheckJob(String string, String string2, Callback<ProxyResult> callback) {
        this.name = string;
        this.ip = string2;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            this.callback.done(XCord.getAntiProxyManager().check(this.name, this.ip, false), null);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

