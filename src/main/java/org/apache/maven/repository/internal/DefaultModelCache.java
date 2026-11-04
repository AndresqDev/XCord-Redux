package org.apache.maven.repository.internal;

import org.apache.maven.model.building.ModelCache;
import org.eclipse.aether.RepositoryCache;
import org.eclipse.aether.RepositorySystemSession;

class DefaultModelCache implements ModelCache {
   private final RepositorySystemSession session;
   private final RepositoryCache cache;

   public static ModelCache newInstance(RepositorySystemSession var0) {
      return var0.getCache() == null ? null : new DefaultModelCache(var0);
   }

   private DefaultModelCache(RepositorySystemSession var1) {
      this.session = var1;
      this.cache = var1.getCache();
   }

   @Override
   public Object get(String var1, String var2, String var3, String var4) {
      return this.cache.get(this.session, new DefaultModelCache.Key(var1, var2, var3, var4));
   }

   @Override
   public void put(String var1, String var2, String var3, String var4, Object var5) {
      this.cache.put(this.session, new DefaultModelCache.Key(var1, var2, var3, var4), var5);
   }

   static class Key {
      private final String groupId;
      private final String artifactId;
      private final String version;
      private final String tag;
      private final int hash;

      Key(String var1, String var2, String var3, String var4) {
         this.groupId = var1;
         this.artifactId = var2;
         this.version = var3;
         this.tag = var4;
         int var5 = 17;
         var5 = var5 * 31 + this.groupId.hashCode();
         var5 = var5 * 31 + this.artifactId.hashCode();
         var5 = var5 * 31 + this.version.hashCode();
         var5 = var5 * 31 + this.tag.hashCode();
         this.hash = var5;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (null != var1 && this.getClass().equals(var1.getClass())) {
            DefaultModelCache.Key var2 = (DefaultModelCache.Key)var1;
            return this.artifactId.equals(var2.artifactId)
               && this.groupId.equals(var2.groupId)
               && this.version.equals(var2.version)
               && this.tag.equals(var2.tag);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.hash;
      }
   }
}
