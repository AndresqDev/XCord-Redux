package org.apache.logging.log4j.core.config.plugins.processor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class PluginCache {
   private final Map<String, Map<String, PluginEntry>> categories = new TreeMap<>();

   public Map<String, Map<String, PluginEntry>> getAllCategories() {
      return this.categories;
   }

   public Map<String, PluginEntry> getCategory(String var1) {
      String var2 = var1.toLowerCase();
      return this.categories.computeIfAbsent(var2, var0 -> new TreeMap<>());
   }

   public void writeCache(OutputStream var1) throws IOException {
      try (DataOutputStream var2 = new DataOutputStream(new BufferedOutputStream(var1))) {
         var2.writeInt(this.categories.size());

         for (Entry var5 : this.categories.entrySet()) {
            var2.writeUTF((String)var5.getKey());
            Map var6 = (Map)var5.getValue();
            var2.writeInt(var6.size());

            for (Entry var8 : var6.entrySet()) {
               PluginEntry var9 = (PluginEntry)var8.getValue();
               var2.writeUTF(var9.getKey());
               var2.writeUTF(var9.getClassName());
               var2.writeUTF(var9.getName());
               var2.writeBoolean(var9.isPrintable());
               var2.writeBoolean(var9.isDefer());
            }
         }
      }
   }

   public void loadCacheFiles(Enumeration<URL> var1) throws IOException {
      this.categories.clear();

      while (var1.hasMoreElements()) {
         URL var2 = (URL)var1.nextElement();

         try (DataInputStream var3 = new DataInputStream(new BufferedInputStream(var2.openStream()))) {
            int var5 = var3.readInt();

            for (int var6 = 0; var6 < var5; var6++) {
               String var7 = var3.readUTF();
               Map var8 = this.getCategory(var7);
               int var9 = var3.readInt();

               for (int var10 = 0; var10 < var9; var10++) {
                  String var11 = var3.readUTF();
                  String var12 = var3.readUTF();
                  String var13 = var3.readUTF();
                  boolean var14 = var3.readBoolean();
                  boolean var15 = var3.readBoolean();
                  var8.computeIfAbsent(var11, var5x -> {
                     PluginEntry var6x = new PluginEntry();
                     var6x.setKey(var5x);
                     var6x.setClassName(var12);
                     var6x.setName(var13);
                     var6x.setPrintable(var14);
                     var6x.setDefer(var15);
                     var6x.setCategory(var7);
                     return var6x;
                  });
               }
            }
         }
      }
   }

   public int size() {
      return this.categories.size();
   }
}
