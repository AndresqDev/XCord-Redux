package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.internal.tcnative.SessionTicketKey;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;

public abstract class OpenSslSessionContext implements SSLSessionContext {
   private final OpenSslSessionStats stats;
   private final OpenSslKeyMaterialProvider provider;
   final ReferenceCountedOpenSslContext context;
   private final OpenSslSessionCache sessionCache;
   private final long mask;

   OpenSslSessionContext(ReferenceCountedOpenSslContext var1, OpenSslKeyMaterialProvider var2, long var3, OpenSslSessionCache var5) {
      this.context = var1;
      this.provider = var2;
      this.mask = var3;
      this.stats = new OpenSslSessionStats(var1);
      this.sessionCache = var5;
      SSLContext.setSSLSessionCache(var1.ctx, var5);
   }

   final boolean useKeyManager() {
      return this.provider != null;
   }

   @Override
   public void setSessionCacheSize(int var1) {
      ObjectUtil.checkPositiveOrZero(var1, "size");
      this.sessionCache.setSessionCacheSize(var1);
   }

   @Override
   public int getSessionCacheSize() {
      return this.sessionCache.getSessionCacheSize();
   }

   @Override
   public void setSessionTimeout(int var1) {
      ObjectUtil.checkPositiveOrZero(var1, "seconds");
      Lock var2 = this.context.ctxLock.writeLock();
      var2.lock();

      try {
         SSLContext.setSessionCacheTimeout(this.context.ctx, (long)var1);
         this.sessionCache.setSessionTimeout(var1);
      } finally {
         var2.unlock();
      }
   }

   @Override
   public int getSessionTimeout() {
      return this.sessionCache.getSessionTimeout();
   }

   @Override
   public SSLSession getSession(byte[] var1) {
      return this.sessionCache.getSession(new OpenSslSessionId(var1));
   }

   @Override
   public Enumeration<byte[]> getIds() {
      return new Enumeration<byte[]>() {
         private final Iterator<OpenSslSessionId> ids = OpenSslSessionContext.this.sessionCache.getIds().iterator();

         @Override
         public boolean hasMoreElements() {
            return this.ids.hasNext();
         }

         public byte[] nextElement() {
            return this.ids.next().cloneBytes();
         }
      };
   }

   @Deprecated
   public void setTicketKeys(byte[] var1) {
      if (var1.length % 48 != 0) {
         throw new IllegalArgumentException("keys.length % 48 != 0");
      } else {
         SessionTicketKey[] var2 = new SessionTicketKey[var1.length / 48];
         int var11 = 0;

         for (int var4 = 0; var11 < var2.length; var11++) {
            byte[] var5 = Arrays.copyOfRange(var1, var4, 16);
            var4 += 16;
            byte[] var6 = Arrays.copyOfRange(var1, var4, 16);
            var11 += 16;
            byte[] var7 = Arrays.copyOfRange(var1, var4, 16);
            var4 += 16;
            var2[var11] = new SessionTicketKey(var5, var6, var7);
         }

         Lock var12 = this.context.ctxLock.writeLock();
         var12.lock();

         try {
            SSLContext.clearOptions(this.context.ctx, SSL.SSL_OP_NO_TICKET);
            SSLContext.setSessionTicketKeys(this.context.ctx, var2);
         } finally {
            var12.unlock();
         }
      }
   }

   public void setTicketKeys(OpenSslSessionTicketKey... var1) {
      ObjectUtil.checkNotNull(var1, "keys");
      SessionTicketKey[] var2 = new SessionTicketKey[var1.length];

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = var1[var3].key;
      }

      Lock var7 = this.context.ctxLock.writeLock();
      var7.lock();

      try {
         SSLContext.clearOptions(this.context.ctx, SSL.SSL_OP_NO_TICKET);
         if (var2.length > 0) {
            SSLContext.setSessionTicketKeys(this.context.ctx, var2);
         }
      } finally {
         var7.unlock();
      }
   }

   public void setSessionCacheEnabled(boolean var1) {
      long var2 = var1 ? this.mask | SSL.SSL_SESS_CACHE_NO_INTERNAL_LOOKUP | SSL.SSL_SESS_CACHE_NO_INTERNAL_STORE : SSL.SSL_SESS_CACHE_OFF;
      Lock var4 = this.context.ctxLock.writeLock();
      var4.lock();

      try {
         SSLContext.setSessionCacheMode(this.context.ctx, var2);
         if (!var1) {
            this.sessionCache.clear();
         }
      } finally {
         var4.unlock();
      }
   }

   public boolean isSessionCacheEnabled() {
      Lock var1 = this.context.ctxLock.readLock();
      var1.lock();

      boolean var2;
      try {
         var2 = (SSLContext.getSessionCacheMode(this.context.ctx) & this.mask) != 0L;
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public OpenSslSessionStats stats() {
      return this.stats;
   }

   final void removeFromCache(OpenSslSessionId var1) {
      this.sessionCache.removeSessionWithId(var1);
   }

   final boolean isInCache(OpenSslSessionId var1) {
      return this.sessionCache.containsSessionWithId(var1);
   }

   void setSessionFromCache(String var1, int var2, long var3) {
      this.sessionCache.setSession(var3, var1, var2);
   }

   final void destroy() {
      if (this.provider != null) {
         this.provider.destroy();
      }

      this.sessionCache.clear();
   }
}
