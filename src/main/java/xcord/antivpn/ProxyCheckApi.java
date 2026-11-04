package xcord.antivpn;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import me.minebuilders.proxyfork.antiproxy.ProxyCheckResult;
import me.minebuilders.proxyfork.antiproxy.ProxyResult;
import net.md_5.bungee.BungeeCord;
import xcord.XCord;

public class ProxyCheckApi implements ProxyCheckProvider {
    @Override
    public ProxyCheckResult check(String name, String ip) {
        ProxyCheckResult result = new ProxyCheckResult();
        ProxyResult proxyResult = ProxyResult.UNKNOWN;
        try {
            HttpURLConnection connection = (HttpURLConnection)new URL("http://proxycheck.io/v2/" + ip + "?key=" + XCord.getConfig().getLoginAntiVpnProxycheckerioApiKey() + XCord.getConfig().getProxyCheckArguments().replace("{name}", name)).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            if (connection.getResponseCode() == 429) {
                BungeeCord.getInstance().getLogger().warning("ProxyChecker daily quota has been exceeded (Failed: " + ip + ")");
                return result;
            }
            JsonObject json = new JsonParser().parse(new InputStreamReader((InputStream)connection.getContent())).getAsJsonObject();
            if (json.get("status").getAsString().equals("ok")) {
                JsonObject details = json.getAsJsonObject(ip);
                proxyResult = details.get("proxy").getAsString().equals("yes")
                    || details.get("type").getAsString().equalsIgnoreCase("VPN")
                    || details.get("type").getAsString().equalsIgnoreCase("proxy")
                    ? ProxyResult.DETECTED
                    : ProxyResult.WHITELISTED;
                if (details.has("isocode")) {
                    result.setCountryCode(details.get("isocode").getAsString());
                }
                if (details.has("asn")) {
                    result.setAsn(details.get("asn").getAsString());
                }
            }
            if (XCord.getConfig().getDebugDebugMode()) {
                System.out.println(json);
            }
        } catch (Exception exception) {
            if (XCord.getConfig().getDebugDebugMode()) {
                exception.printStackTrace();
            }
        }
        result.setResult(proxyResult);
        return result;
    }
}
