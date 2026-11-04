package org.codehaus.plexus.util.cli.shell;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.plexus.util.Os;

public class BourneShell extends Shell {
   public BourneShell() {
      this(false);
   }

   public BourneShell(boolean var1) {
      this.setUnconditionalQuoting(true);
      this.setShellCommand("/bin/sh");
      this.setArgumentQuoteDelimiter('\'');
      this.setExecutableQuoteDelimiter('\'');
      this.setSingleQuotedArgumentEscaped(true);
      this.setSingleQuotedExecutableEscaped(false);
      this.setQuotedExecutableEnabled(true);
      this.setArgumentEscapePattern("'\\%s'");
      if (var1) {
         this.addShellArg("-l");
      }
   }

   @Override
   public String getExecutable() {
      return Os.isFamily("windows") ? super.getExecutable() : this.quoteOneItem(super.getOriginalExecutable(), true);
   }

   @Override
   public List<String> getShellArgsList() {
      ArrayList var1 = new ArrayList();
      List var2 = super.getShellArgsList();
      if (var2 != null && !var2.isEmpty()) {
         var1.addAll(var2);
      }

      var1.add("-c");
      return var1;
   }

   @Override
   public String[] getShellArgs() {
      String[] var1 = super.getShellArgs();
      if (var1 == null) {
         var1 = new String[0];
      }

      if (var1.length > 0 && !var1[var1.length - 1].equals("-c")) {
         String[] var2 = new String[var1.length + 1];
         System.arraycopy(var1, 0, var2, 0, var1.length);
         var2[var1.length] = "-c";
         var1 = var2;
      }

      return var1;
   }

   @Override
   protected String getExecutionPreamble() {
      if (this.getWorkingDirectoryAsString() == null) {
         return null;
      } else {
         String var1 = this.getWorkingDirectoryAsString();
         StringBuilder var2 = new StringBuilder();
         var2.append("cd ");
         var2.append(this.quoteOneItem(var1, false));
         var2.append(" && ");
         return var2.toString();
      }
   }

   @Override
   protected String quoteOneItem(String var1, boolean var2) {
      if (var1 == null) {
         return null;
      } else {
         StringBuilder var3 = new StringBuilder();
         var3.append("'");
         var3.append(var1.replace("'", "'\"'\"'"));
         var3.append("'");
         return var3.toString();
      }
   }
}
