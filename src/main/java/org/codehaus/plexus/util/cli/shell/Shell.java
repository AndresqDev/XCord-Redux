package org.codehaus.plexus.util.cli.shell;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.codehaus.plexus.util.StringUtils;

public class Shell implements Cloneable {
   private static final char[] DEFAULT_QUOTING_TRIGGER_CHARS = new char[]{' '};
   private String shellCommand;
   private List<String> shellArgs = new ArrayList<>();
   private boolean quotedArgumentsEnabled = true;
   private boolean unconditionallyQuote = false;
   private String executable;
   private String workingDir;
   private boolean quotedExecutableEnabled = true;
   private boolean doubleQuotedArgumentEscaped = false;
   private boolean singleQuotedArgumentEscaped = false;
   private boolean doubleQuotedExecutableEscaped = false;
   private boolean singleQuotedExecutableEscaped = false;
   private char argQuoteDelimiter = '"';
   private char exeQuoteDelimiter = '"';
   private String argumentEscapePattern = "\\%s";

   public void setUnconditionalQuoting(boolean var1) {
      this.unconditionallyQuote = var1;
   }

   public void setShellCommand(String var1) {
      this.shellCommand = var1;
   }

   public String getShellCommand() {
      return this.shellCommand;
   }

   public void setShellArgs(String[] var1) {
      this.shellArgs.clear();
      this.shellArgs.addAll(Arrays.asList(var1));
   }

   public String[] getShellArgs() {
      return this.shellArgs != null && !this.shellArgs.isEmpty() ? this.shellArgs.toArray(new String[this.shellArgs.size()]) : null;
   }

   public List<String> getCommandLine(String var1, String[] var2) {
      return this.getRawCommandLine(var1, var2);
   }

   protected String quoteOneItem(String var1, boolean var2) {
      char[] var3 = this.getEscapeChars(this.isSingleQuotedExecutableEscaped(), this.isDoubleQuotedExecutableEscaped());
      return StringUtils.quoteAndEscape(
         var1,
         var2 ? this.getExecutableQuoteDelimiter() : this.getArgumentQuoteDelimiter(),
         var3,
         this.getQuotingTriggerChars(),
         '\\',
         this.unconditionallyQuote
      );
   }

   protected List<String> getRawCommandLine(String var1, String[] var2) {
      ArrayList var3 = new ArrayList();
      StringBuilder var4 = new StringBuilder();
      if (var1 != null) {
         String var5 = this.getExecutionPreamble();
         if (var5 != null) {
            var4.append(var5);
         }

         if (this.isQuotedExecutableEnabled()) {
            var4.append(this.quoteOneItem(this.getOriginalExecutable(), true));
         } else {
            var4.append(this.getExecutable());
         }
      }

      for (String var8 : var2) {
         if (var4.length() > 0) {
            var4.append(" ");
         }

         if (this.isQuotedArgumentsEnabled()) {
            var4.append(this.quoteOneItem(var8, false));
         } else {
            var4.append(var8);
         }
      }

      var3.add(var4.toString());
      return var3;
   }

   protected char[] getQuotingTriggerChars() {
      return DEFAULT_QUOTING_TRIGGER_CHARS;
   }

   protected String getExecutionPreamble() {
      return null;
   }

   protected char[] getEscapeChars(boolean var1, boolean var2) {
      StringBuilder var3 = new StringBuilder(2);
      if (var1) {
         var3.append('\'');
      }

      if (var2) {
         var3.append('"');
      }

      char[] var4 = new char[var3.length()];
      var3.getChars(0, var3.length(), var4, 0);
      return var4;
   }

   protected boolean isDoubleQuotedArgumentEscaped() {
      return this.doubleQuotedArgumentEscaped;
   }

   protected boolean isSingleQuotedArgumentEscaped() {
      return this.singleQuotedArgumentEscaped;
   }

   protected boolean isDoubleQuotedExecutableEscaped() {
      return this.doubleQuotedExecutableEscaped;
   }

   protected boolean isSingleQuotedExecutableEscaped() {
      return this.singleQuotedExecutableEscaped;
   }

   protected void setArgumentQuoteDelimiter(char var1) {
      this.argQuoteDelimiter = var1;
   }

   protected char getArgumentQuoteDelimiter() {
      return this.argQuoteDelimiter;
   }

   protected void setExecutableQuoteDelimiter(char var1) {
      this.exeQuoteDelimiter = var1;
   }

   protected char getExecutableQuoteDelimiter() {
      return this.exeQuoteDelimiter;
   }

   protected void setArgumentEscapePattern(String var1) {
      this.argumentEscapePattern = var1;
   }

   protected String getArgumentEscapePattern() {
      return this.argumentEscapePattern;
   }

   public List<String> getShellCommandLine(String[] var1) {
      ArrayList var2 = new ArrayList();
      if (this.getShellCommand() != null) {
         var2.add(this.getShellCommand());
      }

      if (this.getShellArgs() != null) {
         var2.addAll(this.getShellArgsList());
      }

      var2.addAll(this.getCommandLine(this.getOriginalExecutable(), var1));
      return var2;
   }

   public List<String> getShellArgsList() {
      return this.shellArgs;
   }

   public void addShellArg(String var1) {
      this.shellArgs.add(var1);
   }

   public void setQuotedArgumentsEnabled(boolean var1) {
      this.quotedArgumentsEnabled = var1;
   }

   public boolean isQuotedArgumentsEnabled() {
      return this.quotedArgumentsEnabled;
   }

   public void setQuotedExecutableEnabled(boolean var1) {
      this.quotedExecutableEnabled = var1;
   }

   public boolean isQuotedExecutableEnabled() {
      return this.quotedExecutableEnabled;
   }

   public void setExecutable(String var1) {
      if (var1 != null && var1.length() != 0) {
         this.executable = var1.replace('/', File.separatorChar).replace('\\', File.separatorChar);
      }
   }

   public String getExecutable() {
      return this.executable;
   }

   public void setWorkingDirectory(String var1) {
      if (var1 != null) {
         this.workingDir = var1;
      }
   }

   public void setWorkingDirectory(File var1) {
      if (var1 != null) {
         this.workingDir = var1.getAbsolutePath();
      }
   }

   public File getWorkingDirectory() {
      return this.workingDir == null ? null : new File(this.workingDir);
   }

   public String getWorkingDirectoryAsString() {
      return this.workingDir;
   }

   public void clearArguments() {
      this.shellArgs.clear();
   }

   @Override
   public Object clone() {
      Shell var1 = new Shell();
      var1.setExecutable(this.getExecutable());
      var1.setWorkingDirectory(this.getWorkingDirectory());
      var1.setShellArgs(this.getShellArgs());
      return var1;
   }

   public String getOriginalExecutable() {
      return this.executable;
   }

   public List<String> getOriginalCommandLine(String var1, String[] var2) {
      return this.getRawCommandLine(var1, var2);
   }

   protected void setDoubleQuotedArgumentEscaped(boolean var1) {
      this.doubleQuotedArgumentEscaped = var1;
   }

   protected void setDoubleQuotedExecutableEscaped(boolean var1) {
      this.doubleQuotedExecutableEscaped = var1;
   }

   protected void setSingleQuotedArgumentEscaped(boolean var1) {
      this.singleQuotedArgumentEscaped = var1;
   }

   protected void setSingleQuotedExecutableEscaped(boolean var1) {
      this.singleQuotedExecutableEscaped = var1;
   }
}
