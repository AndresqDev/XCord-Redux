package xcord;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import me.minebuilders.proxyfork.antiproxy.ProxyCheckResult;
import me.minebuilders.proxyfork.antiproxy.ProxyResult;
import net.md_5.bungee.BungeeCord;
import xcord.ProxyCheckProvider;
import xcord.XCord;

public class IPHubApi
implements ProxyCheckProvider {
    @Override
    public ProxyCheckResult check(String string, String string2) {
        ProxyResult proxyResult;
        ProxyCheckResult proxyCheckResult;
        block8: {
            proxyCheckResult = new ProxyCheckResult();
            proxyResult = ProxyResult.UNKNOWN;
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection)new URL("http://v2.api.iphub.info/ip/" + string2).openConnection();
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                if (XCord.getConfig().getLoginAntiVpnIphubApiKey().length() > 4) {
                    httpURLConnection.setRequestProperty("X-Key", XCord.getConfig().getLoginAntiVpnIphubApiKey());
                }
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() == 429) {
                    BungeeCord.getInstance().getLogger().warning("IPHub daily quota has been exceeded (Failed: " + string2 + ")");
                    return proxyCheckResult;
                }
                JsonObject jsonObject = new JsonParser().parse(new InputStreamReader((InputStream)httpURLConnection.getContent())).getAsJsonObject();
                if (jsonObject.has("countryCode")) {
                    proxyCheckResult.setCountryCode(jsonObject.get("countryCode").getAsString());
                }
                if (jsonObject.has("asn")) {
                    proxyCheckResult.setAsn(jsonObject.get("asn").getAsString());
                }
                if (jsonObject.has("block") && jsonObject.get("block").getAsInt() == 1) {
                    proxyResult = ProxyResult.DETECTED;
                }
                if (XCord.getConfig().getDebugDebugMode()) {
                    System.out.println(jsonObject);
                }
            }
            catch (Exception exception) {
                if (!XCord.getConfig().getDebugDebugMode()) break block8;
                exception.printStackTrace();
            }
        }
        proxyCheckResult.setResult(proxyResult);
        return proxyCheckResult;
    }
}

