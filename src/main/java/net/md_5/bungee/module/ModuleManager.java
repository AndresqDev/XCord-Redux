package net.md_5.bungee.module;

import com.google.common.base.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.util.CaseInsensitiveMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

public class ModuleManager {
   private final Map<String, ModuleSource> knownSources = new HashMap<>();

   public ModuleManager() {
      this.knownSources.put("jenkins", new JenkinsModuleSource());
   }

   @SuppressFBWarnings({"SF_SWITCH_FALLTHROUGH", "SF_SWITCH_NO_DEFAULT"})
   public void load(ProxyServer var1, File var2) throws Exception {
      var2.mkdir();
      ModuleVersion var3 = ModuleVersion.parse(var1.getVersion());
      if (var3 == null) {
         var1.getLogger().warning("Couldn't detect bungee version. Custom build?");
      } else {
         ArrayList var4 = new ArrayList();
         File var5 = new File("modules.yml");
         DumperOptions var6 = new DumperOptions();
         var6.setDefaultFlowStyle(FlowStyle.BLOCK);
         Yaml var7 = new Yaml(var6);
         var5.createNewFile();
         FileInputStream var9 = new FileInputStream(var5);

         Map var8;
         try {
            var8 = (Map)var7.load(var9);
         } catch (Throwable var21) {
            try {
               var9.close();
            } catch (Throwable var19) {
               var21.addSuppressed(var19);
            }

            throw var21;
         }

         var9.close();
         CaseInsensitiveMap var22;
         if (var8 == null) {
            var22 = new CaseInsensitiveMap();
         } else {
            var22 = new CaseInsensitiveMap(var8);
         }

         ArrayList var23 = new ArrayList();
         Object var10 = var22.get("modules");
         if (var10 != null) {
            var23.addAll((Collection)var10);
         }

         int var11 = var22.containsKey("version") ? (Integer)var22.get("version") : 0;
         switch (var11) {
            case 0:
               var23.add("jenkins://cmd_alert");
               var23.add("jenkins://cmd_find");
               var23.add("jenkins://cmd_list");
               var23.add("jenkins://cmd_send");
               var23.add("jenkins://cmd_server");
            case 1:
               var23.add("jenkins://reconnect_yaml");
            default:
               var22.put("modules", var23);
               var22.put("version", 2);
               FileWriter var12 = new FileWriter(var5);

               try {
                  var7.dump(var22, var12);
               } catch (Throwable var20) {
                  try {
                     var12.close();
                  } catch (Throwable var18) {
                     var20.addSuppressed(var18);
                  }

                  throw var20;
               }

               var12.close();

               for (String var13 : (List)var22.get("modules")) {
                  URI var14 = new URI(var13);
                  ModuleSource var15 = this.knownSources.get(var14.getScheme());
                  if (var15 == null) {
                     System.out.println("Unknown module source: " + var13);
                  } else {
                     String var16 = var14.getAuthority();
                     if (var16 == null) {
                        System.out.println("Unknown module host: " + var13);
                     } else {
                        ModuleSpec var17 = new ModuleSpec(var16, new File(var2, var16 + ".jar"), var15);
                        var4.add(var17);
                        System.out.println("Discovered module: " + var17);
                     }
                  }
               }

               for (ModuleSpec var26 : var4) {
                  ModuleVersion var27 = var26.getFile().exists() ? this.getVersion(var26.getFile()) : null;
                  if (!var3.equals(var27)) {
                     System.out.println("Attempting to update plugin from " + var27 + " to " + var3);
                     var26.getProvider().retrieve(var26, var3);
                  }
               }
         }
      }
   }

   @SuppressFBWarnings({"REC_CATCH_EXCEPTION"})
   private ModuleVersion getVersion(File var1) {
      try {
         JarFile var2 = new JarFile(var1);

         ModuleVersion var6;
         try {
            JarEntry var3 = var2.getJarEntry("plugin.yml");
            Preconditions.checkNotNull(var3, "Plugin must have a plugin.yml");
            InputStream var4 = var2.getInputStream(var3);

            try {
               PluginDescription var5 = (PluginDescription)new Yaml().loadAs(var4, PluginDescription.class);
               var6 = ModuleVersion.parse(var5.getVersion());
            } catch (Throwable var9) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (var4 != null) {
               var4.close();
            }
         } catch (Throwable var10) {
            try {
               var2.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }

            throw var10;
         }

         var2.close();
         return var6;
      } catch (Exception var11) {
         ProxyServer.getInstance().getLogger().log(Level.WARNING, "Could not check module from file " + var1, (Throwable)var11);
         return null;
      }
   }
}
