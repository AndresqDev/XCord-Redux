package net.minecrell.terminalconsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.LineReader.Option;
import org.jline.terminal.Terminal;

public abstract class SimpleTerminalConsole {
   protected abstract boolean isRunning();

   protected abstract void runCommand(String var1);

   protected abstract void shutdown();

   protected void processInput(String var1) {
      String var2 = var1.trim();
      if (!var2.isEmpty()) {
         this.runCommand(var2);
      }
   }

   protected LineReader buildReader(LineReaderBuilder var1) {
      LineReader var2 = var1.build();
      var2.setOpt(Option.DISABLE_EVENT_EXPANSION);
      var2.unsetOpt(Option.INSERT_TAB);
      return var2;
   }

   public void start() {
      try {
         Terminal var1 = TerminalConsoleAppender.getTerminal();
         if (var1 != null) {
            this.readCommands(var1);
         } else {
            this.readCommands(System.in);
         }
      } catch (IOException var2) {
         LogManager.getLogger("TerminalConsole").error("Failed to read console input", var2);
      }
   }

   private void readCommands(Terminal var1) {
      LineReader var2 = this.buildReader(LineReaderBuilder.builder().terminal(var1));
      TerminalConsoleAppender.setReader(var2);

      try {
         while (this.isRunning()) {
            String var3;
            try {
               var3 = var2.readLine("> ");
            } catch (EndOfFileException var9) {
               continue;
            }

            if (var3 == null) {
               break;
            }

            this.processInput(var3);
         }
      } catch (UserInterruptException var10) {
         this.shutdown();
      } finally {
         TerminalConsoleAppender.setReader(null);
      }
   }

   private void readCommands(InputStream var1) throws IOException {
      String var4;
      try (BufferedReader var2 = new BufferedReader(new InputStreamReader(var1))) {
         while (this.isRunning() && (var4 = var2.readLine()) != null) {
            this.processInput(var4);
         }
      }
   }
}
