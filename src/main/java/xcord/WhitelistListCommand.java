package xcord;

import java.math.BigInteger;
import java.net.InetAddress;
import me.minebuilders.proxyfork.antiproxy.AntiProxyWhitelistManager;
import net.md_5.bungee.api.CommandSender;

public class WhitelistListCommand extends SubCommand {
   private final AntiProxyWhitelistManager whitelist;

   public WhitelistListCommand(AntiProxyWhitelistManager var1) {
      this.whitelist = var1;
      this.permission = "xcord.command.vpn";
   }

   @Override
   public void execute(CommandSender var1, String[] var2) throws IllegalArgumentException {
      var1.sendMessage("Whitelisted IP addresses:");

      for (int var6 : this.whitelist.getWhitelistedIps().toArray()) {
         byte[] var7 = BigInteger.valueOf((long)var6).toByteArray();

         try {
            InetAddress var8 = InetAddress.getByAddress(var7);
            var1.sendMessage(" > " + var8.getHostAddress());
         } catch (Exception var9) {
            var9.printStackTrace();
         }
      }

      var1.sendMessage("Whitelisted Usernames:");

      for (String var11 : this.whitelist.getWhitelistedUsernames()) {
         var1.sendMessage(" > " + var11);
      }
   }
}
