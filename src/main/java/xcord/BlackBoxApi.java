package xcord;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import me.minebuilders.proxyfork.antiproxy.ProxyCheckResult;
import me.minebuilders.proxyfork.antiproxy.ProxyResult;
import net.md_5.bungee.BungeeCord;
import xcord.ProxyCheckProvider;
import xcord.XCord;

public class BlackBoxApi
implements ProxyCheckProvider {
    @Override
    public ProxyCheckResult check(String string, String string2) {
        ProxyResult proxyResult;
        ProxyCheckResult proxyCheckResult;
        block5: {
            proxyCheckResult = new ProxyCheckResult();
            proxyResult = ProxyResult.UNKNOWN;
            try {
                String string3;
                HttpURLConnection httpURLConnection = (HttpURLConnection)new URL("https://blackbox.ipinfo.app/lookup/" + string2).openConnection();
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() != 200) {
                    BungeeCord.getInstance().getLogger().warning("BlackBox returned a " + httpURLConnection.getResponseCode() + " HTTP response. Ratelimited maybe?");
                    return proxyCheckResult;
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                while ((string3 = bufferedReader.readLine()) != null) {
                    stringBuilder.append(string3);
                }
                String string4 = stringBuilder.toString();
                if (XCord.getConfig().getDebugDebugMode()) {
                    System.out.println("Blackbox query for " + string2 + " = " + string4);
                }
                proxyCheckResult.setAsn("NA");
                proxyCheckResult.setCountryCode("NA");
                proxyResult = string4.contains("Y") ? ProxyResult.DETECTED : ProxyResult.WHITELISTED;
            }
            catch (Exception exception) {
                if (!XCord.getConfig().getDebugDebugMode()) break block5;
                exception.printStackTrace();
            }
        }
        proxyCheckResult.setResult(proxyResult);
        return proxyCheckResult;
    }
}

