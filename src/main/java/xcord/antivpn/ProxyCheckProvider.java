package xcord.antivpn;

import me.minebuilders.proxyfork.antiproxy.ProxyCheckResult;

public interface ProxyCheckProvider {
    public ProxyCheckResult check(String name, String ip);
}
