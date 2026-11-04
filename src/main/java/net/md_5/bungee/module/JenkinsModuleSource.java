package net.md_5.bungee.module;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;

public class JenkinsModuleSource implements ModuleSource {
   @Override
   public void retrieve(ModuleSpec var1, ModuleVersion var2) {
      System.out.println("Attempting to Jenkins download module " + var1.getName() + " v" + var2.getBuild());

      try {
         String var3 = String.format(
            "https://papermc.io/api/v2/projects/%1$s/versions/%2$s/builds/%3$s/downloads/%4$s-%2$s-%3$s.jar",
            "travertine",
            ProxyServer.getInstance().getVersion().split(":")[2].split("-")[0],
            var2.getBuild(),
            var1.getName()
         );
         URL var4 = new URL(var3);
         URLConnection var5 = var4.openConnection();
         var5.setConnectTimeout(15000);
         var5.setReadTimeout(15000);
         var5.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
         Files.copy(var5.getInputStream(), var1.getFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
         System.out.println("Download complete");
      } catch (IOException var6) {
         System.out.println("Failed to download: " + Util.exception(var6));
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof JenkinsModuleSource)) {
         return false;
      } else {
         JenkinsModuleSource var2 = (JenkinsModuleSource)var1;
         return var2.canEqual(this);
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof JenkinsModuleSource;
   }

   @Override
   public int hashCode() {
      boolean var1 = true;
      return 1;
   }

   @Override
   public String toString() {
      return "JenkinsModuleSource()";
   }
}
