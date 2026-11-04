package me.minebuilders.proxyfork.antiproxy;

import com.google.common.collect.Sets;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;
import xcord.ProcessUtils;

public class AntiProxyWhitelistManager implements Serializable {
   private static final long serialVersionUID = 5518619159099964693L;
   private final Set<String> whitelistedUsernames = Sets.newConcurrentHashSet();
   private TIntSet whitelistedIps = new TIntHashSet();
   private File backendFile;

   public AntiProxyWhitelistManager(File var1) {
      this.backendFile = var1;
   }

   public AntiProxyWhitelistManager() {
   }

   public void setBackendFile(File var1) {
      this.backendFile = var1;
   }

   private void saveToFile() {
      new Thread(() -> {
         try {
            ObjectOutputStream var1 = new ObjectOutputStream(new FileOutputStream(this.backendFile));

            try {
               var1.writeObject(this);
            } catch (Throwable var5) {
               try {
                  var1.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }

               throw var5;
            }

            var1.close();
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }).start();
   }

   public void whitelistEntry(String var1) {
      if (ProcessUtils.isValidIp(var1)) {
         int var2 = ProcessUtils.ipToInt(var1);
         TIntHashSet var3 = new TIntHashSet(this.whitelistedIps);
         var3.add(var2);
         this.whitelistedIps = var3;
      } else {
         this.whitelistedUsernames.add(var1);
      }

      this.saveToFile();
   }

   public boolean removeWhitelistedEntry(String var1) {
      boolean var5;
      try {
         if (!ProcessUtils.isValidIp(var1)) {
            return this.whitelistedUsernames.remove(var1);
         }

         int var2 = ProcessUtils.ipToInt(var1);
         TIntHashSet var3 = new TIntHashSet(this.whitelistedIps);
         boolean var4 = var3.remove(var2);
         this.whitelistedIps = var3;
         var5 = var4;
      } finally {
         this.saveToFile();
      }

      return var5;
   }

   public boolean isEntryWhitelisted(String var1) {
      return ProcessUtils.isValidIp(var1) ? this.whitelistedIps.contains(ProcessUtils.ipToInt(var1)) : this.whitelistedUsernames.contains(var1);
   }

   public boolean isIPWhitelisted(int var1) {
      return this.whitelistedIps.contains(var1);
   }

   public boolean isUsernameWhitelisted(String var1) {
      return this.whitelistedUsernames.contains(var1);
   }

   public Set<String> getWhitelistedUsernames() {
      return this.whitelistedUsernames;
   }

   public TIntSet getWhitelistedIps() {
      return this.whitelistedIps;
   }
}
