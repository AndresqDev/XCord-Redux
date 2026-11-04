package xcord.antivpn;

import me.minebuilders.proxyfork.antiproxy.ProxyResult;
import net.md_5.bungee.api.Callback;
import xcord.XCord;
import xcord.service.AsyncJob;

public class ProxyCheckJob implements AsyncJob {
    private final String name;
    private final String ip;
    private Callback<ProxyResult> callback;

    public ProxyCheckJob(String name, String ip, Callback<ProxyResult> callback) {
        this.name = name;
        this.ip = ip;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            this.callback.done(XCord.getAntiProxyManager().check(this.name, this.ip, false), null);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
