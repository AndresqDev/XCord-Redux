package org.eclipse.sisu.space;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.NoSuchElementException;
import org.eclipse.sisu.inject.DeferredClass;
import org.osgi.framework.Bundle;

public final class BundleClassSpace implements ClassSpace {
   private static final URL[] NO_URLS = new URL[0];
   private static final Enumeration<URL> NO_ENTRIES = Collections.enumeration(Collections.emptySet());
   private final Bundle bundle;
   private URL[] bundleClassPath;

   public BundleClassSpace(Bundle var1) {
      this.bundle = var1;
   }

   @Override
   public Class<?> loadClass(String var1) {
      try {
         return this.bundle.loadClass(var1);
      } catch (Exception var3) {
         throw new TypeNotPresentException(var1, var3);
      } catch (LinkageError var4) {
         throw new TypeNotPresentException(var1, var4);
      }
   }

   @Override
   public DeferredClass<?> deferLoadClass(String var1) {
      return new NamedClass(this, var1);
   }

   @Override
   public URL getResource(String var1) {
      return this.bundle.getResource(var1);
   }

   @Override
   public Enumeration<URL> getResources(String var1) {
      try {
         Enumeration var2 = this.bundle.getResources(var1);
         return var2 != null ? var2 : NO_ENTRIES;
      } catch (IOException var3) {
         return NO_ENTRIES;
      }
   }

   @Override
   public Enumeration<URL> findEntries(String var1, String var2, boolean var3) {
      URL[] var4 = this.getBundleClassPath();
      Enumeration var5 = this.bundle.findEntries(var1 != null ? var1 : "/", var2, var3);
      if (var4.length > 0) {
         return new BundleClassSpace.ChainedEnumeration<>(var5, new ResourceEnumeration(var1, var2, var3, var4));
      } else {
         return var5 != null ? var5 : NO_ENTRIES;
      }
   }

   public Bundle getBundle() {
      return this.bundle;
   }

   @Override
   public int hashCode() {
      return this.bundle.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof BundleClassSpace ? this.bundle.equals(((BundleClassSpace)var1).bundle) : false;
      }
   }

   @Override
   public String toString() {
      return this.bundle.toString();
   }

   private synchronized URL[] getBundleClassPath() {
      if (this.bundleClassPath == null) {
         String var1 = (String)this.bundle.getHeaders().get("Bundle-ClassPath");
         if (var1 == null) {
            this.bundleClassPath = NO_URLS;
         } else {
            ArrayList var2 = new ArrayList();
            HashSet var3 = new HashSet();
            var3.add(".");

            String[] var7;
            for (String var4 : var7 = var1.trim().split("\\s*,\\s*")) {
               if (var3.add(var4)) {
                  URL var8 = this.bundle.getEntry(var4);
                  if (var8 != null) {
                     var2.add(var8);
                  }
               }
            }

            this.bundleClassPath = var2.isEmpty() ? NO_URLS : var2.toArray(new URL[var2.size()]);
         }
      }

      return this.bundleClassPath;
   }

   private static final class ChainedEnumeration<T> implements Enumeration<T> {
      private final Enumeration<T>[] enumerations;
      private int index;

      ChainedEnumeration(Enumeration<T>... var1) {
         this.enumerations = var1;
      }

      @Override
      public boolean hasMoreElements() {
         while (this.index < this.enumerations.length) {
            if (this.enumerations[this.index] != null && this.enumerations[this.index].hasMoreElements()) {
               return true;
            }

            this.index++;
         }

         return false;
      }

      @Override
      public T nextElement() {
         if (this.hasMoreElements()) {
            return this.enumerations[this.index].nextElement();
         } else {
            throw new NoSuchElementException();
         }
      }
   }
}
