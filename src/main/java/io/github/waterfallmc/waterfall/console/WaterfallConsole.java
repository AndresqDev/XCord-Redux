package io.github.waterfallmc.waterfall.console;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

public final class WaterfallConsole extends SimpleTerminalConsole {
   protected LineReader buildReader(LineReaderBuilder var1) {
      ProxyServer var2 = ProxyServer.getInstance();
      return super.buildReader(var1.appName(var2.getName()).completer(new ConsoleCommandCompleter(var2)));
   }

   protected boolean isRunning() {
      return BungeeCord.getInstance().isRunning;
   }

   protected void runCommand(String var1) {
      ProxyServer var2 = ProxyServer.getInstance();
      if (!var2.getPluginManager().dispatchCommand(var2.getConsole(), var1)) {
         var2.getConsole().sendMessage(new ComponentBuilder("Command not found").color(ChatColor.RED).create());
      }
   }

   protected void shutdown() {
      ProxyServer.getInstance().stop();
   }
}
