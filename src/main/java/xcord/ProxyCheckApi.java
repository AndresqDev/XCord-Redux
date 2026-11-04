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

public class ProxyCheckApi
implements ProxyCheckProvider {
    @Override
    public ProxyCheckResult check(String string, String string2) {
        ProxyResult proxyResult;
        ProxyCheckResult proxyCheckResult;
        block7: {
            proxyCheckResult = new ProxyCheckResult();
            proxyResult = ProxyResult.UNKNOWN;
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection)new URL("http://proxycheck.io/v2/" + string2 + "?key=" + XCord.getConfig().getLoginAntiVpnProxycheckerioApiKey() + XCord.getConfig().getProxyCheckArguments().replace("{name}", string)).openConnection();
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() == 429) {
                    BungeeCord.getInstance().getLogger().warning("ProxyChecker daily quota has been exceeded (Failed: " + string2 + ")");
                    return proxyCheckResult;
                }
                JsonObject jsonObject = new JsonParser().parse(new InputStreamReader((InputStream)httpURLConnection.getContent())).getAsJsonObject();
                if (jsonObject.get("status").getAsString().equals("ok")) {
                    JsonObject jsonObject2 = jsonObject.getAsJsonObject(string2);
                    proxyResult = jsonObject2.get("proxy").getAsString().equals("yes") || jsonObject2.get("type").getAsString().equalsIgnoreCase("VPN") || jsonObject2.get("type").getAsString().equalsIgnoreCase("proxy") ? ProxyResult.DETECTED : ProxyResult.WHITELISTED;
                    if (jsonObject2.has("isocode")) {
                        proxyCheckResult.setCountryCode(jsonObject2.get("isocode").getAsString());
                    }
                    if (jsonObject2.has("asn")) {
                        proxyCheckResult.setAsn(jsonObject2.get("asn").getAsString());
                    }
                }
                if (XCord.getConfig().getDebugDebugMode()) {
                    System.out.println(jsonObject);
                }
            }
            catch (Exception exception) {
                if (!XCord.getConfig().getDebugDebugMode()) break block7;
                exception.printStackTrace();
            }
        }
        proxyCheckResult.setResult(proxyResult);
        return proxyCheckResult;
    }
}

