package org.codehaus.plexus.util.cli;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.shell.BourneShell;
import org.codehaus.plexus.util.cli.shell.CmdShell;
import org.codehaus.plexus.util.cli.shell.CommandShell;
import org.codehaus.plexus.util.cli.shell.Shell;

public class Commandline implements Cloneable {
      protected static final String OS_NAME = "os.name";
      protected static final String WINDOWS = "Windows";
   protected Vector<Arg> arguments = new Vector<>();
   protected Map<String, String> envVars = Collections.synchronizedMap(new LinkedHashMap<>());
   private long pid = -1L;
   private Shell shell;
      protected String executable;
      private File workingDir;

   public Commandline(String var1, Shell var2) {
      this.shell = var2;
      String[] var3 = new String[0];

      try {
         var3 = CommandLineUtils.translateCommandline(var1);
      } catch (Exception var5) {
         System.err.println("Error translating Commandline.");
      }

      if (var3 != null && var3.length > 0) {
         this.setExecutable(var3[0]);

         for (int var4 = 1; var4 < var3.length; var4++) {
            this.createArgument().setValue(var3[var4]);
         }
      }
   }

   public Commandline(Shell var1) {
      this.shell = var1;
   }

   public Commandline(String var1) {
      this.setDefaultShell();
      String[] var2 = new String[0];

      try {
         var2 = CommandLineUtils.translateCommandline(var1);
      } catch (Exception var4) {
         System.err.println("Error translating Commandline.");
      }

      if (var2 != null && var2.length > 0) {
         this.setExecutable(var2[0]);

         for (int var3 = 1; var3 < var2.length; var3++) {
            this.createArgument().setValue(var2[var3]);
         }
      }
   }

   public Commandline() {
      this.setDefaultShell();
   }

   public long getPid() {
      if (this.pid == -1L) {
         this.pid = Long.parseLong(String.valueOf(System.currentTimeMillis()));
      }

      return this.pid;
   }

   public void setPid(long var1) {
      this.pid = var1;
   }

   private void setDefaultShell() {
      if (Os.isFamily("windows")) {
         if (Os.isFamily("win9x")) {
            this.setShell(new CommandShell());
         } else {
            this.setShell(new CmdShell());
         }
      } else {
         this.setShell(new BourneShell());
      }
   }

      public Commandline.Argument createArgument() {
      return this.createArgument(false);
   }

      public Commandline.Argument createArgument(boolean var1) {
      Commandline.Argument var2 = new Commandline.Argument();
      if (var1) {
         this.arguments.insertElementAt(var2, 0);
      } else {
         this.arguments.addElement(var2);
      }

      return var2;
   }

   public Arg createArg() {
      return this.createArg(false);
   }

   public Arg createArg(boolean var1) {
      Commandline.Argument var2 = new Commandline.Argument();
      if (var1) {
         this.arguments.insertElementAt(var2, 0);
      } else {
         this.arguments.addElement(var2);
      }

      return var2;
   }

   public void addArg(Arg var1) {
      this.addArg(var1, false);
   }

   public void addArg(Arg var1, boolean var2) {
      if (var2) {
         this.arguments.insertElementAt(var1, 0);
      } else {
         this.arguments.addElement(var1);
      }
   }

   public void setExecutable(String var1) {
      this.shell.setExecutable(var1);
      this.executable = var1;
   }

   public String getLiteralExecutable() {
      return this.executable;
   }

   public String getExecutable() {
      String var1 = this.shell.getExecutable();
      if (var1 == null) {
         var1 = this.executable;
      }

      return var1;
   }

   public void addArguments(String[] var1) {
      for (String var5 : var1) {
         this.createArgument().setValue(var5);
      }
   }

   public void addEnvironment(String var1, String var2) {
      this.envVars.put(var1, var2);
   }

   public void addSystemEnvironment() throws Exception {
      Properties var1 = CommandLineUtils.getSystemEnvVars();

      for (Object var3 : var1.keySet()) {
         String var4 = (String)var3;
         if (!this.envVars.containsKey(var4)) {
            this.addEnvironment(var4, var1.getProperty(var4));
         }
      }
   }

   public String[] getEnvironmentVariables() throws CommandLineException {
      try {
         this.addSystemEnvironment();
      } catch (Exception var7) {
         throw new CommandLineException("Error setting up environmental variables", var7);
      }

      String[] var1 = new String[this.envVars.size()];
      int var2 = 0;

      for (Object var4 : this.envVars.keySet()) {
         String var5 = (String)var4;
         String var6 = this.envVars.get(var5);
         var1[var2] = var5 + "=" + var6;
         var2++;
      }

      return var1;
   }

   public String[] getCommandline() {
      return Os.isFamily("windows") ? this.getShellCommandline() : this.getRawCommandline();
   }

   public String[] getRawCommandline() {
      String[] var1 = this.getArguments();
      String var2 = this.getLiteralExecutable();
      if (var2 == null) {
         return var1;
      } else {
         String[] var3 = new String[var1.length + 1];
         var3[0] = var2;
         System.arraycopy(var1, 0, var3, 1, var1.length);
         return var3;
      }
   }

   public String[] getShellCommandline() {
      this.verifyShellState();
      return this.getShell().getShellCommandLine(this.getArguments()).toArray(new String[0]);
   }

   public String[] getArguments() {
      Vector var1 = new Vector(this.arguments.size() * 2);

      for (int var2 = 0; var2 < this.arguments.size(); var2++) {
         Arg var3 = this.arguments.elementAt(var2);
         String[] var4 = var3.getParts();
         if (var4 != null) {
            for (String var8 : var4) {
               var1.addElement(var8);
            }
         }
      }

      String[] var9 = new String[var1.size()];
      var1.copyInto(var9);
      return var9;
   }

   @Override
   public String toString() {
      return StringUtils.join(this.getShellCommandline(), " ");
   }

   public int size() {
      return this.getCommandline().length;
   }

   @Override
   public Object clone() {
      Commandline var1 = new Commandline((Shell)this.shell.clone());
      var1.executable = this.executable;
      var1.workingDir = this.workingDir;
      var1.addArguments(this.getArguments());
      return var1;
   }

   public void clear() {
      this.executable = null;
      this.workingDir = null;
      this.shell.setExecutable(null);
      this.shell.clearArguments();
      this.arguments.removeAllElements();
   }

   public void clearArgs() {
      this.arguments.removeAllElements();
   }

   public Commandline.Marker createMarker() {
      return new Commandline.Marker(this.arguments.size());
   }

   public void setWorkingDirectory(String var1) {
      this.shell.setWorkingDirectory(var1);
      this.workingDir = new File(var1);
   }

   public void setWorkingDirectory(File var1) {
      this.shell.setWorkingDirectory(var1);
      this.workingDir = var1;
   }

   public File getWorkingDirectory() {
      File var1 = this.shell.getWorkingDirectory();
      if (var1 == null) {
         var1 = this.workingDir;
      }

      return var1;
   }

   public Process execute() throws CommandLineException {
      this.verifyShellState();
      String[] var2 = this.getEnvironmentVariables();
      File var3 = this.shell.getWorkingDirectory();

      try {
         Process var1;
         if (var3 == null) {
            var1 = Runtime.getRuntime().exec(this.getCommandline(), var2, var3);
         } else {
            if (!var3.exists()) {
               throw new CommandLineException("Working directory \"" + var3.getPath() + "\" does not exist!");
            }

            if (!var3.isDirectory()) {
               throw new CommandLineException("Path \"" + var3.getPath() + "\" does not specify a directory.");
            }

            var1 = Runtime.getRuntime().exec(this.getCommandline(), var2, var3);
         }

         return var1;
      } catch (IOException var5) {
         throw new CommandLineException("Error while executing process.", var5);
      }
   }

      private void verifyShellState() {
      if (this.shell.getWorkingDirectory() == null) {
         this.shell.setWorkingDirectory(this.workingDir);
      }

      if (this.shell.getOriginalExecutable() == null) {
         this.shell.setExecutable(this.executable);
      }
   }

   public Properties getSystemEnvVars() throws Exception {
      return CommandLineUtils.getSystemEnvVars();
   }

   public void setShell(Shell var1) {
      this.shell = var1;
   }

   public Shell getShell() {
      return this.shell;
   }

      public static String[] translateCommandline(String var0) throws Exception {
      return CommandLineUtils.translateCommandline(var0);
   }

      public static String quoteArgument(String var0) throws CommandLineException {
      return CommandLineUtils.quote(var0);
   }

      public static String toString(String[] var0) {
      return CommandLineUtils.toString(var0);
   }

   public static class Argument implements Arg {
      private String[] parts;

      @Override
      public void setValue(String var1) {
         if (var1 != null) {
            this.parts = new String[]{var1};
         }
      }

      @Override
      public void setLine(String var1) {
         if (var1 != null) {
            try {
               this.parts = CommandLineUtils.translateCommandline(var1);
            } catch (Exception var3) {
               System.err.println("Error translating Commandline.");
            }
         }
      }

      @Override
      public void setFile(File var1) {
         this.parts = new String[]{var1.getAbsolutePath()};
      }

      @Override
      public String[] getParts() {
         return this.parts;
      }
   }

   public class Marker {
      private int position;
      private int realPos = -1;

      Marker(int var2) {
         this.position = var2;
      }

      public int getPosition() {
         if (this.realPos == -1) {
            this.realPos = Commandline.this.getLiteralExecutable() == null ? 0 : 1;

            for (int var1 = 0; var1 < this.position; var1++) {
               Arg var2 = Commandline.this.arguments.elementAt(var1);
               this.realPos = this.realPos + var2.getParts().length;
            }
         }

         return this.realPos;
      }
   }
}
