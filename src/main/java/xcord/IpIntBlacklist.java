package xcord;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class IpIntBlacklist implements Blacklist, FileBlacklist {
   private TIntSet addresses = new TIntHashSet(120000);

   @Override
   public boolean add(String var1) {
      throw new UnsupportedOperationException("This blacklist type cannot support dynamic writing!");
   }

   @Override
   public boolean contains(String var1) {
      return this.addresses.contains(ProcessUtils.ipToInt(var1));
   }

   @Override
   public void load(File var1, String var2) {
      String var3 = var2.trim() + " ";
      TIntHashSet var4 = new TIntHashSet(120000);
      int var5 = 0;

      try {
         BufferedReader var6 = new BufferedReader(new InputStreamReader(new FileInputStream(var1), StandardCharsets.UTF_8));

         String var7;
         try {
            while ((var7 = var6.readLine()) != null) {
               int var8 = var7.indexOf(var3);
               if (var8 > 1) {
                  String var9 = var7.substring(var8 + var3.length()).trim();
                  if (var9.length() < 20) {
                     try {
                        var4.add(ProcessUtils.ipToInt(var9));
                     } catch (Exception var12) {
                        var5++;
                     }
                  }
               }
            }
         } catch (Throwable var13) {
            try {
               var6.close();
            } catch (Throwable var11) {
               var13.addSuppressed(var11);
            }

            throw var13;
         }

         var6.close();
      } catch (Exception var14) {
         var14.printStackTrace();
      }

      this.addresses = var4;
      if (XCord.getConfig().getDebugDebugMode()) {
         System.out.println("Loaded " + var4.size() + " compatible IPV4 addresses to the global blacklist (" + var5 + " IP ranges could not be loaded)");
      }
   }

   @Override
   public int purge() {
      return 0;
   }

   @Override
   public boolean isEmpty() {
      return this.addresses.isEmpty();
   }
}
