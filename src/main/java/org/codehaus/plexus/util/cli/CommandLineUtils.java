package org.codehaus.plexus.util.cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;

public abstract class CommandLineUtils {
   private static final long MILLIS_PER_SECOND = 1000L;
   private static final long NANOS_PER_SECOND = 1000000000L;

   public static int executeCommandLine(Commandline var0, StreamConsumer var1, StreamConsumer var2) throws CommandLineException {
      return executeCommandLine(var0, null, var1, var2, 0);
   }

   public static int executeCommandLine(Commandline var0, StreamConsumer var1, StreamConsumer var2, int var3) throws CommandLineException {
      return executeCommandLine(var0, null, var1, var2, var3);
   }

   public static int executeCommandLine(Commandline var0, InputStream var1, StreamConsumer var2, StreamConsumer var3) throws CommandLineException {
      return executeCommandLine(var0, var1, var2, var3, 0);
   }

   public static int executeCommandLine(Commandline var0, InputStream var1, StreamConsumer var2, StreamConsumer var3, int var4) throws CommandLineException {
      CommandLineCallable var5 = executeCommandLineAsCallable(var0, var1, var2, var3, var4);
      return var5.call();
   }

   public static CommandLineCallable executeCommandLineAsCallable(
      Commandline var0, final InputStream var1, final StreamConsumer var2, final StreamConsumer var3, final int var4
   ) throws CommandLineException {
      if (var0 == null) {
         throw new IllegalArgumentException("cl cannot be null.");
      } else {
         final Process var5 = var0.execute();
         final Thread var6 = new Thread() {
            {
               this.setName("CommandLineUtils process shutdown hook");
               this.setContextClassLoader(null);
            }

            @Override
            public void run() {
               var5.destroy();
            }
         };
         ShutdownHookUtils.addShutDownHook(var6);
         return new CommandLineCallable() {
            @Override
            public Integer call() throws CommandLineException {
               StreamFeeder var1x = null;
               StreamPumper var2x = null;
               StreamPumper var3x = null;
               boolean var4x = false;

               Integer var224;
               try {
                  if (var1 != null) {
                     var1x = new StreamFeeder(var1, var5.getOutputStream());
                     var1x.start();
                  }

                  var2x = new StreamPumper(var5.getInputStream(), var2);
                  var2x.start();
                  var3x = new StreamPumper(var5.getErrorStream(), var3);
                  var3x.start();
                  int var5x;
                  if (var4 <= 0) {
                     var5x = var5.waitFor();
                  } else {
                     long var6x = System.nanoTime();
                     long var8 = var6x + 1000000000L * (long)var4;

                     while (CommandLineUtils.isAlive(var5) && System.nanoTime() < var8) {
                        Thread.sleep(999L);
                     }

                     if (CommandLineUtils.isAlive(var5)) {
                        throw new InterruptedException(String.format("Process timed out after %d seconds.", var4));
                     }

                     var5x = var5.exitValue();
                  }

                  if (var1x != null) {
                     var1x.waitUntilDone();
                  }

                  var2x.waitUntilDone();
                  var3x.waitUntilDone();
                  if (var1x != null) {
                     var1x.close();
                     CommandLineUtils.handleException(var1x, "stdin");
                  }

                  var2x.close();
                  CommandLineUtils.handleException(var2x, "stdout");
                  var3x.close();
                  CommandLineUtils.handleException(var3x, "stderr");
                  var4x = true;
                  var224 = var5x;
               } catch (InterruptedException var222) {
                  throw new CommandLineTimeOutException("Error while executing external command, process killed.", var222);
               } finally {
                  if (var1x != null) {
                     var1x.disable();
                  }

                  if (var2x != null) {
                     var2x.disable();
                  }

                  if (var3x != null) {
                     var3x.disable();
                  }

                  try {
                     ShutdownHookUtils.removeShutdownHook(var6);
                     var6.run();
                  } finally {
                     try {
                        if (var1x != null) {
                           var1x.close();
                           if (var4x) {
                              var4x = false;
                              CommandLineUtils.handleException(var1x, "stdin");
                              var4x = true;
                           }
                        }
                     } finally {
                        try {
                           if (var2x != null) {
                              var2x.close();
                              if (var4x) {
                                 var4x = false;
                                 CommandLineUtils.handleException(var2x, "stdout");
                                 var4x = true;
                              }
                           }
                        } finally {
                           if (var3x != null) {
                              var3x.close();
                              if (var4x) {
                                 CommandLineUtils.handleException(var3x, "stderr");
                              }
                           }
                        }
                     }
                  }
               }

               return var224;
            }
         };
      }
   }

   private static void handleException(StreamPumper var0, String var1) throws CommandLineException {
      if (var0.getException() != null) {
         throw new CommandLineException(String.format("Failure processing %s.", var1), var0.getException());
      }
   }

   private static void handleException(StreamFeeder var0, String var1) throws CommandLineException {
      if (var0.getException() != null) {
         throw new CommandLineException(String.format("Failure processing %s.", var1), var0.getException());
      }
   }

   public static Properties getSystemEnvVars() throws IOException {
      return getSystemEnvVars(!Os.isFamily("windows"));
   }

   public static Properties getSystemEnvVars(boolean var0) throws IOException {
      Properties var1 = new Properties();
      Map var2 = System.getenv();

      for (String var4 : var2.keySet()) {
         String var5 = (String)var2.get(var4);
         if (!var0) {
            var4 = var4.toUpperCase(Locale.ENGLISH);
         }

         var1.put(var4, var5);
      }

      return var1;
   }

   public static boolean isAlive(Process var0) {
      if (var0 == null) {
         return false;
      } else {
         try {
            var0.exitValue();
            return false;
         } catch (IllegalThreadStateException var2) {
            return true;
         }
      }
   }

   public static String[] translateCommandline(String var0) throws Exception {
      if (var0 != null && var0.length() != 0) {
         boolean var1 = false;
         boolean var2 = true;
         byte var3 = 2;
         byte var4 = 0;
         StringTokenizer var5 = new StringTokenizer(var0, "\"' ", true);
         Vector var6 = new Vector();
         StringBuilder var7 = new StringBuilder();

         while (var5.hasMoreTokens()) {
            String var8 = var5.nextToken();
            switch (var4) {
               case 1:
                  if ("'".equals(var8)) {
                     var4 = 0;
                  } else {
                     var7.append(var8);
                  }
                  break;
               case 2:
                  if ("\"".equals(var8)) {
                     var4 = 0;
                  } else {
                     var7.append(var8);
                  }
                  break;
               default:
                  if ("'".equals(var8)) {
                     var4 = 1;
                  } else if ("\"".equals(var8)) {
                     var4 = 2;
                  } else if (" ".equals(var8)) {
                     if (var7.length() != 0) {
                        var6.addElement(var7.toString());
                        var7.setLength(0);
                     }
                  } else {
                     var7.append(var8);
                  }
            }
         }

         if (var7.length() != 0) {
            var6.addElement(var7.toString());
         }

         if (var4 != 1 && var4 != 2) {
            String[] var9 = new String[var6.size()];
            var6.copyInto(var9);
            return var9;
         } else {
            throw new CommandLineException("unbalanced quotes in " + var0);
         }
      } else {
         return new String[0];
      }
   }

      public static String quote(String var0) throws CommandLineException {
      return quote(var0, false, false, true);
   }

      public static String quote(String var0, boolean var1) throws CommandLineException {
      return quote(var0, false, false, var1);
   }

      public static String quote(String var0, boolean var1, boolean var2, boolean var3) throws CommandLineException {
      if (var0.contains("\"")) {
         if (var0.contains("'")) {
            throw new CommandLineException("Can't handle single and double quotes in same argument");
         }

         if (var1) {
            return "\\'" + var0 + "\\'";
         }

         if (var3) {
            return '\'' + var0 + '\'';
         }
      } else if (var0.contains("'")) {
         if (var2) {
            return "\\\"" + var0 + "\\\"";
         }

         if (var3) {
            return '"' + var0 + '"';
         }
      } else if (var0.contains(" ")) {
         if (var2) {
            return "\\\"" + var0 + "\\\"";
         }

         return '"' + var0 + '"';
      }

      return var0;
   }

   public static String toString(String[] var0) {
      if (var0 != null && var0.length != 0) {
         StringBuilder var1 = new StringBuilder();

         for (int var2 = 0; var2 < var0.length; var2++) {
            if (var2 > 0) {
               var1.append(' ');
            }

            try {
               var1.append(StringUtils.quoteAndEscape(var0[var2], '"'));
            } catch (Exception var4) {
               System.err.println("Error quoting argument: " + var4.getMessage());
            }
         }

         return var1.toString();
      } else {
         return "";
      }
   }

   public static class StringStreamConsumer implements StreamConsumer {
      private StringBuffer string = new StringBuffer();
      private String ls = System.getProperty("line.separator");

      @Override
      public void consumeLine(String var1) {
         this.string.append(var1).append(this.ls);
      }

      public String getOutput() {
         return this.string.toString();
      }
   }
}
