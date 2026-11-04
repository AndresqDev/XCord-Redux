package org.eclipse.sisu.space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.sisu.inject.Logs;

public final class IndexedClassFinder implements ClassFinder {
   private static final Pattern LINE_PATTERN = Pattern.compile("\\s*([^#\\s]+).*");
   private final String localPath;
   private final String indexName;

   public IndexedClassFinder(String var1, boolean var2) {
      if (var2) {
         this.localPath = null;
         this.indexName = var1;
      } else {
         int var3 = var1.lastIndexOf(47) + 1;
         this.localPath = var1.substring(0, var3);
         this.indexName = var1.substring(var3);
      }
   }

   public Iterable<String> indexedNames(ClassSpace var1) {
      Enumeration var2;
      if (this.localPath == null) {
         var2 = var1.getResources(this.indexName);
      } else {
         var2 = var1.findEntries(this.localPath, this.indexName, false);
      }

      LinkedHashSet var3 = new LinkedHashSet();

      while (var2.hasMoreElements()) {
         URL var4 = (URL)var2.nextElement();

         try {
            BufferedReader var5 = new BufferedReader(new InputStreamReader(Streams.open(var4), "UTF-8"));

            try {
               for (String var6 = var5.readLine(); var6 != null; var6 = var5.readLine()) {
                  Matcher var7 = LINE_PATTERN.matcher(var6);
                  if (var7.matches()) {
                     var3.add(var7.group(1));
                  }
               }
            } finally {
               var5.close();
            }
         } catch (IOException var12) {
            Logs.warn("Problem reading: {}", var4, var12);
         }
      }

      return var3;
   }

   @Override
   public Enumeration<URL> findClasses(final ClassSpace var1) {
      final Iterator var2 = this.indexedNames(var1).iterator();
      return new Enumeration<URL>() {
         private URL nextURL;

         @Override
         public boolean hasMoreElements() {
            while (this.nextURL == null && var2.hasNext()) {
               this.nextURL = var1.getResource(((String)var2.next()).replace('.', '/') + ".class");
            }

            return this.nextURL != null;
         }

         public URL nextElement() {
            if (this.hasMoreElements()) {
               URL var1x = this.nextURL;
               this.nextURL = null;
               return var1x;
            } else {
               throw new NoSuchElementException();
            }
         }
      };
   }
}
