package io.github.waterfallmc.waterfall.console;

import java.util.List;
import net.md_5.bungee.api.ProxyServer;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

final class ConsoleCommandCompleter implements Completer {
   private final ProxyServer proxy;

   ConsoleCommandCompleter(ProxyServer var1) {
      this.proxy = var1;
   }

   public void complete(LineReader var1, ParsedLine var2, List<Candidate> var3) {
      List var4 = this.proxy.getPluginManager().tabCompleteCommand(this.proxy.getConsole(), var2.line());
      if (!var4.isEmpty()) {
         for (String var6 : var4) {
            var3.add(new Candidate(var6));
         }
      }
   }
}
