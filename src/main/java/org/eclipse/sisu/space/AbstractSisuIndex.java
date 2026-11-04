package org.eclipse.sisu.space;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

abstract class AbstractSisuIndex {
   static final String INDEX_FOLDER = "META-INF/sisu/";
   static final String QUALIFIER = "d";
   static final String NAMED = "b";
   private final Map<Object, Set<String>> index = new LinkedHashMap<>();

   protected final synchronized void addClassToIndex(Object var1, Object var2) {
      Set var3 = this.index.get(var1);
      if (var3 == null) {
         var3 = this.readTable(var1);
         this.index.put(var1, var3);
      }

      var3.add(String.valueOf(var2));
   }

   protected final synchronized void flushIndex() {
      for (Entry var1 : this.index.entrySet()) {
         this.writeTable(var1.getKey(), (Set<String>)var1.getValue());
      }
   }

   protected abstract void info(String var1);

   protected abstract void warn(String var1);

   protected abstract Reader getReader(String var1) throws IOException;

   protected abstract Writer getWriter(String var1) throws IOException;

   private Set<String> readTable(Object var1) {
      TreeSet var2 = new TreeSet();

      try {
         BufferedReader var3 = new BufferedReader(this.getReader("META-INF/sisu/" + var1));

         try {
            for (String var4 = var3.readLine(); var4 != null; var4 = var3.readLine()) {
               var2.add(var4);
            }
         } finally {
            var3.close();
         }
      } catch (IOException var9) {
      }

      return var2;
   }

   private void writeTable(Object var1, Set<String> var2) {
      try {
         BufferedWriter var3 = new BufferedWriter(this.getWriter("META-INF/sisu/" + var1));

         try {
            for (String var4 : var2) {
               var3.write(var4);
               var3.newLine();
            }
         } finally {
            var3.close();
         }
      } catch (IOException var10) {
         this.warn(var10.toString());
      }
   }
}
