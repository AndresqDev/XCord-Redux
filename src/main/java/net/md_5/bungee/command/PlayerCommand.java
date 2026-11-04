package net.md_5.bungee.command;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.Locale;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

@Deprecated
public abstract class PlayerCommand extends Command implements TabExecutor {
   public PlayerCommand(String var1) {
      super(var1);
   }

   public PlayerCommand(String var1, String var2, String... var3) {
      super(var1, var2, var3);
   }

   @Override
   public Iterable<String> onTabComplete(CommandSender var1, String[] var2) {
      final String var3 = var2.length > 0 ? var2[var2.length - 1].toLowerCase(Locale.ROOT) : "";
      return Iterables.transform(Iterables.filter(ProxyServer.getInstance().getPlayers(), new Predicate<ProxiedPlayer>() {
         public boolean apply(ProxiedPlayer var1) {
            return var1.getName().toLowerCase(Locale.ROOT).startsWith(var3);
         }
      }), new Function<ProxiedPlayer, String>() {
         public String apply(ProxiedPlayer var1) {
            return var1.getName();
         }
      });
   }
}
