package org.codehaus.plexus.util.cli.shell;

import java.util.Arrays;
import java.util.List;

public class CmdShell extends Shell {
   public CmdShell() {
      this.setShellCommand("cmd.exe");
      this.setQuotedExecutableEnabled(true);
      this.setShellArgs(new String[]{"/X", "/C"});
   }

   @Override
   public List<String> getCommandLine(String var1, String[] var2) {
      StringBuilder var3 = new StringBuilder();
      var3.append("\"");
      var3.append(super.getCommandLine(var1, var2).get(0));
      var3.append("\"");
      return Arrays.asList(var3.toString());
   }
}
