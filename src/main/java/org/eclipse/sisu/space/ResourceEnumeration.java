package org.eclipse.sisu.space;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

final class ResourceEnumeration implements Enumeration<URL> {
   private static final Iterator<String> NO_ENTRIES = Collections.<String>emptySet().iterator();
   private final URL[] urls;
   private final String subPath;
   private final GlobberStrategy globber;
   private final Object globPattern;
   private final boolean recurse;
   private int index;
   private URL currentURL;
   private boolean isFolder;
   private Iterator<String> entryNames = NO_ENTRIES;
   private String nextEntryName;

   ResourceEnumeration(String var1, String var2, boolean var3, URL[] var4) {
      this.subPath = normalizeSearchPath(var1);
      this.globber = GlobberStrategy.selectFor(var2);
      this.globPattern = this.globber.compile(var2);
      this.recurse = var3;
      this.urls = var4;
   }

   @Override
   public boolean hasMoreElements() {
      while (this.nextEntryName == null) {
         if (this.entryNames.hasNext()) {
            String var1 = this.entryNames.next();
            if (this.matchesRequest(var1)) {
               this.nextEntryName = var1;
            }
         } else {
            if (this.index >= this.urls.length) {
               return false;
            }

            this.currentURL = this.urls[this.index++];
            this.entryNames = this.scan(this.currentURL);
         }
      }

      return true;
   }

   public URL nextElement() {
      if (this.hasMoreElements()) {
         String var1 = this.nextEntryName;
         this.nextEntryName = null;

         try {
            return this.findResource(var1);
         } catch (MalformedURLException var3) {
            throw new IllegalStateException(var3.toString());
         }
      } else {
         throw new NoSuchElementException();
      }
   }

   static String normalizeSearchPath(String var0) {
      if (var0 != null && !"/".equals(var0)) {
         boolean var1 = false;
         StringBuilder var2 = new StringBuilder();
         int var3 = 0;

         for (int var4 = var0.length(); var3 < var4; var3++) {
            char var5 = var0.charAt(var3);
            boolean var6 = '/' != var5;
            if (var1 || var6) {
               var1 = var6;
               var2.append(var5);
            }
         }

         if (var1) {
            var2.append('/');
         }

         return var2.toString();
      } else {
         return "";
      }
   }

   private Iterator<String> scan(URL var1) {
      this.isFolder = var1.getPath().endsWith("/");
      if (this.globber == GlobberStrategy.EXACT && !this.recurse) {
         try {
            this.nextEntryName = this.subPath + this.globPattern;
            Streams.open(this.findResource(this.nextEntryName)).close();
         } catch (Exception var2) {
            this.nextEntryName = null;
         }

         return NO_ENTRIES;
      } else {
         return (Iterator<String>)(this.isFolder ? new FileEntryIterator(var1, this.subPath, this.recurse) : new ZipEntryIterator(var1));
      }
   }

   private URL findResource(String var1) throws MalformedURLException {
      if (this.isFolder) {
         return new URL(this.currentURL, var1);
      } else {
         return "jar".equals(this.currentURL.getProtocol())
            ? new URL(this.currentURL, "#" + var1, new ResourceEnumeration.NestedJarHandler())
            : new URL("jar:" + this.currentURL + "!/" + var1);
      }
   }

   private boolean matchesRequest(String var1) {
      if (var1.endsWith("/") || !var1.startsWith(this.subPath)) {
         return false;
      } else {
         return !this.recurse && var1.indexOf(47, this.subPath.length()) > 0 ? false : this.globber.matches(this.globPattern, var1);
      }
   }

   static final class NestedJarConnection extends URLConnection {
      NestedJarConnection(URL var1) {
         super(var1);
      }

      @Override
      public void connect() {
      }

      @Override
      public InputStream getInputStream() throws IOException {
         URL var1 = new URL("jar", null, -1, this.url.getFile());
         ZipInputStream var2 = new ZipInputStream(Streams.open(var1));
         String var3 = this.url.getRef();

         for (ZipEntry var4 = var2.getNextEntry(); var4 != null; var4 = var2.getNextEntry()) {
            if (var3.equals(var4.getName())) {
               return var2;
            }
         }

         throw new ZipException("No such entry: " + var3 + " in: " + var1);
      }
   }

   static final class NestedJarHandler extends URLStreamHandler {
      @Override
      protected URLConnection openConnection(URL var1) {
         return new ResourceEnumeration.NestedJarConnection(var1);
      }
   }
}
