package xcord.antivpn;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import me.minebuilders.proxyfork.antiproxy.ProxyCheckResult;
import me.minebuilders.proxyfork.antiproxy.ProxyResult;
import net.md_5.bungee.BungeeCord;
import xcord.XCord;

public class BlackBoxApi implements ProxyCheckProvider {
    @Override
    public ProxyCheckResult check(String name, String ip) {
        ProxyCheckResult result = new ProxyCheckResult();
        ProxyResult proxyResult = ProxyResult.UNKNOWN;
        try {
            HttpURLConnection connection = (HttpURLConnection)new URL("https://blackbox.ipinfo.app/lookup/" + ip).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            if (connection.getResponseCode() != 200) {
                BungeeCord.getInstance().getLogger().warning("BlackBox returned a " + connection.getResponseCode() + " HTTP response. Ratelimited maybe?");
                return result;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            String body = response.toString();
            if (XCord.getConfig().getDebugDebugMode()) {
                System.out.println("Blackbox query for " + ip + " = " + body);
            }
            result.setAsn("NA");
            result.setCountryCode("NA");
            proxyResult = body.contains("Y") ? ProxyResult.DETECTED : ProxyResult.WHITELISTED;
        } catch (Exception exception) {
            if (XCord.getConfig().getDebugDebugMode()) {
                exception.printStackTrace();
            }
        }
        result.setResult(proxyResult);
        return result;
    }
}
