package org.jline.reader.impl.completer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

@Deprecated
public class FileNameCompleter implements Completer {
   @Override
   public void complete(LineReader var1, ParsedLine var2, List<Candidate> var3) {
      assert var2 != null;

      assert var3 != null;

      String var4 = var2.word().substring(0, var2.wordCursor());
      String var7 = this.getUserDir().getFileSystem().getSeparator();
      int var8 = var4.lastIndexOf(var7);
      Path var5;
      String var6;
      if (var8 >= 0) {
         var6 = var4.substring(0, var8 + 1);
         if (var6.startsWith("~")) {
            if (var6.startsWith("~" + var7)) {
               var5 = this.getUserHome().resolve(var6.substring(2));
            } else {
               var5 = this.getUserHome().getParent().resolve(var6.substring(1));
            }
         } else {
            var5 = this.getUserDir().resolve(var6);
         }
      } else {
         var6 = "";
         var5 = this.getUserDir();
      }

      try {
         Files.newDirectoryStream(var5, this::accept)
            .forEach(
               var5x -> {
                  String var6x = var6 + var5x.getFileName().toString();
                  if (Files.isDirectory(var5x)) {
                     var3.add(
                        new Candidate(
                           var6x + (var1.isSet(LineReader.Option.AUTO_PARAM_SLASH) ? var7 : ""),
                           this.getDisplay(var1.getTerminal(), var5x),
                           null,
                           null,
                           var1.isSet(LineReader.Option.AUTO_REMOVE_SLASH) ? var7 : null,
                           null,
                           false
                        )
                     );
                  } else {
                     var3.add(new Candidate(var6x, this.getDisplay(var1.getTerminal(), var5x), null, null, null, null, true));
                  }
               }
            );
      } catch (IOException var10) {
      }
   }

   protected boolean accept(Path var1) {
      try {
         return !Files.isHidden(var1);
      } catch (IOException var3) {
         return false;
      }
   }

   protected Path getUserDir() {
      return Paths.get(System.getProperty("user.dir"));
   }

   protected Path getUserHome() {
      return Paths.get(System.getProperty("user.home"));
   }

   protected String getDisplay(Terminal var1, Path var2) {
      String var3 = var2.getFileName().toString();
      if (Files.isDirectory(var2)) {
         AttributedStringBuilder var4 = new AttributedStringBuilder();
         var4.styled(AttributedStyle.BOLD.foreground(1), var3);
         var4.append("/");
         var3 = var4.toAnsi(var1);
      } else if (Files.isSymbolicLink(var2)) {
         AttributedStringBuilder var5 = new AttributedStringBuilder();
         var5.styled(AttributedStyle.BOLD.foreground(1), var3);
         var5.append("@");
         var3 = var5.toAnsi(var1);
      }

      return var3;
   }
}
