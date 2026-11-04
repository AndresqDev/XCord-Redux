package net.md_5.bungee;

public class Bootstrap {
   public static void main(String[] var0) throws Exception {
      if ((double)Float.parseFloat(System.getProperty("java.class.version")) < 52.0) {
         System.err.println("*** ERROR *** Travertine requires Java 8 or above to function! Please download and install it!");
         System.out.println("You can check your Java version with the command: java -version");
      } else {
         BungeeCordLauncher.main(var0);
      }
   }
}
