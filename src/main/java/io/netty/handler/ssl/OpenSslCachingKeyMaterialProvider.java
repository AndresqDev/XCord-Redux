package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.net.ssl.X509KeyManager;

final class OpenSslCachingKeyMaterialProvider extends OpenSslKeyMaterialProvider {
   private final int maxCachedEntries;
   private volatile boolean full;
   private final ConcurrentMap<String, OpenSslKeyMaterial> cache = new ConcurrentHashMap<>();

   OpenSslCachingKeyMaterialProvider(X509KeyManager var1, String var2, int var3) {
      super(var1, var2);
      this.maxCachedEntries = var3;
   }

   @Override
   OpenSslKeyMaterial chooseKeyMaterial(ByteBufAllocator var1, String var2) throws Exception {
      OpenSslKeyMaterial var3 = this.cache.get(var2);
      if (var3 == null) {
         var3 = super.chooseKeyMaterial(var1, var2);
         if (var3 == null) {
            return null;
         }

         if (this.full) {
            return var3;
         }

         if (this.cache.size() > this.maxCachedEntries) {
            this.full = true;
            return var3;
         }

         OpenSslKeyMaterial var4 = this.cache.putIfAbsent(var2, var3);
         if (var4 != null) {
            var3.release();
            var3 = var4;
         }
      }

      return var3.retain();
   }

   @Override
   void destroy() {
      do {
         Iterator var1 = this.cache.values().iterator();

         while (var1.hasNext()) {
            ((OpenSslKeyMaterial)var1.next()).release();
            var1.remove();
         }
      } while (!this.cache.isEmpty());
   }
}
