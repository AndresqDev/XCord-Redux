package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSL;
import io.netty.util.AsciiString;
import java.util.HashMap;
import java.util.Map;

final class OpenSslClientSessionCache extends OpenSslSessionCache {
   private final Map<OpenSslClientSessionCache.HostPort, OpenSslSessionCache.NativeSslSession> sessions = new HashMap<>();

   OpenSslClientSessionCache(OpenSslEngineMap var1) {
      super(var1);
   }

   @Override
   protected boolean sessionCreated(OpenSslSessionCache.NativeSslSession var1) {
      assert Thread.holdsLock(this);

      OpenSslClientSessionCache.HostPort var2 = keyFor(var1.getPeerHost(), var1.getPeerPort());
      if (var2 != null && !this.sessions.containsKey(var2)) {
         this.sessions.put(var2, var1);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void sessionRemoved(OpenSslSessionCache.NativeSslSession var1) {
      assert Thread.holdsLock(this);

      OpenSslClientSessionCache.HostPort var2 = keyFor(var1.getPeerHost(), var1.getPeerPort());
      if (var2 != null) {
         this.sessions.remove(var2);
      }
   }

   @Override
   void setSession(long var1, String var3, int var4) {
      OpenSslClientSessionCache.HostPort var5 = keyFor(var3, var4);
      if (var5 != null) {
         OpenSslSessionCache.NativeSslSession var6;
         boolean var7;
         synchronized (this) {
            var6 = this.sessions.get(var5);
            if (var6 == null) {
               return;
            }

            if (!var6.isValid()) {
               this.removeSessionWithId(var6.sessionId());
               return;
            }

            var7 = SSL.setSession(var1, var6.session());
         }

         if (var7) {
            if (var6.shouldBeSingleUse()) {
               var6.invalidate();
            }

            var6.updateLastAccessedTime();
         }
      }
   }

   private static OpenSslClientSessionCache.HostPort keyFor(String var0, int var1) {
      return var0 == null && var1 < 1 ? null : new OpenSslClientSessionCache.HostPort(var0, var1);
   }

   @Override
   synchronized void clear() {
      super.clear();
      this.sessions.clear();
   }

   private static final class HostPort {
      private final int hash;
      private final String host;
      private final int port;

      HostPort(String var1, int var2) {
         this.host = var1;
         this.port = var2;
         this.hash = 31 * AsciiString.hashCode(var1) + var2;
      }

      @Override
      public int hashCode() {
         return this.hash;
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof OpenSslClientSessionCache.HostPort)) {
            return false;
         } else {
            OpenSslClientSessionCache.HostPort var2 = (OpenSslClientSessionCache.HostPort)var1;
            return this.port == var2.port && this.host.equalsIgnoreCase(var2.host);
         }
      }

      @Override
      public String toString() {
         return "HostPort{host='" + this.host + '\'' + ", port=" + this.port + '}';
      }
   }
}
