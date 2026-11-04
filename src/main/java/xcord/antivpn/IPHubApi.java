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

public class IPHubApi implements ProxyCheckProvider {
    @Override
    public ProxyCheckResult check(String name, String ip) {
        ProxyCheckResult result = new ProxyCheckResult();
        ProxyResult proxyResult = ProxyResult.UNKNOWN;
        try {
            HttpURLConnection connection = (HttpURLConnection)new URL("http://v2.api.iphub.info/ip/" + ip).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            if (XCord.getConfig().getLoginAntiVpnIphubApiKey().length() > 4) {
                connection.setRequestProperty("X-Key", XCord.getConfig().getLoginAntiVpnIphubApiKey());
            }
            connection.connect();
            if (connection.getResponseCode() == 429) {
                BungeeCord.getInstance().getLogger().warning("IPHub daily quota has been exceeded (Failed: " + ip + ")");
                return result;
            }
            JsonObject json = new JsonParser().parse(new InputStreamReader((InputStream)connection.getContent())).getAsJsonObject();
            if (json.has("countryCode")) {
                result.setCountryCode(json.get("countryCode").getAsString());
            }
            if (json.has("asn")) {
                result.setAsn(json.get("asn").getAsString());
            }
            if (json.has("block") && json.get("block").getAsInt() == 1) {
                proxyResult = ProxyResult.DETECTED;
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
