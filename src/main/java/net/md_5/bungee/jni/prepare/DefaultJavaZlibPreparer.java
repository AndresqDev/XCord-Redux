package net.md_5.bungee.jni.prepare;

public class DefaultJavaZlibPreparer implements XCordPreparer {
   @Override
   public int prepareCompressorAndGetStatus(int var1, int var2) {
      try {
         Class.forName("java.util.zip.Deflater");
         Class.forName("java.util.zip.Inflater");
      } catch (ClassNotFoundException var4) {
         System.out.println("Warning: Your Java version is missing vital compression classes! Please downgrade");
         return 1;
      }

      if (var1 < 0 || var1 > 9) {
         System.out.println("Warning: JavaZLib compression levels can only be between 0-9!");
      }

      if (var2 > 0 && var2 < 50 && var1 < 5 && var1 > 0) {
         System.out.println("Warning: Your compression level is too low for this threshold to have a purpose!");
      }

      return 0;
   }
}
