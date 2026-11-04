package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSLSession;
import io.netty.internal.tcnative.SSLSessionCache;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.SystemPropertyUtil;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import javax.security.cert.X509Certificate;

class OpenSslSessionCache implements SSLSessionCache {
   private static final OpenSslSession[] EMPTY_SESSIONS = new OpenSslSession[0];
   private static final int DEFAULT_CACHE_SIZE;
   private final OpenSslEngineMap engineMap;
   private final Map<OpenSslSessionId, OpenSslSessionCache.NativeSslSession> sessions = new LinkedHashMap<OpenSslSessionId, OpenSslSessionCache.NativeSslSession>(
      
   ) {
      private static final long serialVersionUID = -7773696788135734448L;

      @Override
      protected boolean removeEldestEntry(Entry<OpenSslSessionId, OpenSslSessionCache.NativeSslSession> var1) {
         int var2 = OpenSslSessionCache.this.maximumCacheSize.get();
         if (var2 >= 0 && this.size() > var2) {
            OpenSslSessionCache.this.removeSessionWithId((OpenSslSessionId)var1.getKey());
         }

         return false;
      }
   };
   private final AtomicInteger maximumCacheSize;
   private final AtomicInteger sessionTimeout;
   private int sessionCounter;

   OpenSslSessionCache(OpenSslEngineMap var1) {
      this.maximumCacheSize = new AtomicInteger(DEFAULT_CACHE_SIZE);
      this.sessionTimeout = new AtomicInteger(300);
      this.engineMap = var1;
   }

   final void setSessionTimeout(int var1) {
      int var2 = this.sessionTimeout.getAndSet(var1);
      if (var2 > var1) {
         this.clear();
      }
   }

   final int getSessionTimeout() {
      return this.sessionTimeout.get();
   }

   protected boolean sessionCreated(OpenSslSessionCache.NativeSslSession var1) {
      return true;
   }

   protected void sessionRemoved(OpenSslSessionCache.NativeSslSession var1) {
   }

   final void setSessionCacheSize(int var1) {
      long var2 = (long)this.maximumCacheSize.getAndSet(var1);
      if (var2 > (long)var1 || var1 == 0) {
         this.clear();
      }
   }

   final int getSessionCacheSize() {
      return this.maximumCacheSize.get();
   }

   private void expungeInvalidSessions() {
      if (!this.sessions.isEmpty()) {
         long var1 = System.currentTimeMillis();
         Iterator var3 = this.sessions.entrySet().iterator();

         while (var3.hasNext()) {
            OpenSslSessionCache.NativeSslSession var4 = (OpenSslSessionCache.NativeSslSession)((Entry)var3.next()).getValue();
            if (var4.isValid(var1)) {
               break;
            }

            var3.remove();
            this.notifyRemovalAndFree(var4);
         }
      }
   }

   public final boolean sessionCreated(long var1, long var3) {
      ReferenceCountedOpenSslEngine var5 = this.engineMap.get(var1);
      if (var5 == null) {
         return false;
      } else {
         OpenSslSessionCache.NativeSslSession var6 = new OpenSslSessionCache.NativeSslSession(
            var3, var5.getPeerHost(), var5.getPeerPort(), (long)this.getSessionTimeout() * 1000L
         );
         var5.setSessionId(var6.sessionId());
         synchronized (this) {
            if (++this.sessionCounter == 255) {
               this.sessionCounter = 0;
               this.expungeInvalidSessions();
            }

            if (!this.sessionCreated(var6)) {
               var6.close();
               return false;
            } else {
               OpenSslSessionCache.NativeSslSession var8 = this.sessions.put(var6.sessionId(), var6);
               if (var8 != null) {
                  this.notifyRemovalAndFree(var8);
               }

               return true;
            }
         }
      }
   }

   public final long getSession(long var1, byte[] var3) {
      OpenSslSessionId var4 = new OpenSslSessionId(var3);
      OpenSslSessionCache.NativeSslSession var5;
      synchronized (this) {
         var5 = this.sessions.get(var4);
         if (var5 == null) {
            return -1L;
         }

         if (!var5.isValid() || !var5.upRef()) {
            this.removeSessionWithId(var5.sessionId());
            return -1L;
         }

         if (var5.shouldBeSingleUse()) {
            this.removeSessionWithId(var5.sessionId());
         }
      }

      var5.updateLastAccessedTime();
      return var5.session();
   }

   void setSession(long var1, String var3, int var4) {
   }

   final synchronized void removeSessionWithId(OpenSslSessionId var1) {
      OpenSslSessionCache.NativeSslSession var2 = this.sessions.remove(var1);
      if (var2 != null) {
         this.notifyRemovalAndFree(var2);
      }
   }

   final synchronized boolean containsSessionWithId(OpenSslSessionId var1) {
      return this.sessions.containsKey(var1);
   }

   private void notifyRemovalAndFree(OpenSslSessionCache.NativeSslSession var1) {
      this.sessionRemoved(var1);
      var1.free();
   }

   final synchronized OpenSslSession getSession(OpenSslSessionId var1) {
      OpenSslSessionCache.NativeSslSession var2 = this.sessions.get(var1);
      if (var2 != null && !var2.isValid()) {
         this.removeSessionWithId(var2.sessionId());
         return null;
      } else {
         return var2;
      }
   }

   final List<OpenSslSessionId> getIds() {
      OpenSslSession[] var1;
      synchronized (this) {
         var1 = this.sessions.values().toArray(EMPTY_SESSIONS);
      }

      ArrayList var2 = new ArrayList(var1.length);

      for (OpenSslSession var6 : var1) {
         if (var6.isValid()) {
            var2.add(var6.sessionId());
         }
      }

      return var2;
   }

   synchronized void clear() {
      Iterator var1 = this.sessions.entrySet().iterator();

      while (var1.hasNext()) {
         OpenSslSessionCache.NativeSslSession var2 = (OpenSslSessionCache.NativeSslSession)((Entry)var1.next()).getValue();
         var1.remove();
         this.notifyRemovalAndFree(var2);
      }
   }

   static {
      int var0 = SystemPropertyUtil.getInt("javax.net.ssl.sessionCacheSize", 20480);
      if (var0 >= 0) {
         DEFAULT_CACHE_SIZE = var0;
      } else {
         DEFAULT_CACHE_SIZE = 20480;
      }
   }

   static final class NativeSslSession implements OpenSslSession {
      static final ResourceLeakDetector<OpenSslSessionCache.NativeSslSession> LEAK_DETECTOR = ResourceLeakDetectorFactory.instance()
         .newResourceLeakDetector(OpenSslSessionCache.NativeSslSession.class);
      private final ResourceLeakTracker<OpenSslSessionCache.NativeSslSession> leakTracker;
      private final long session;
      private final String peerHost;
      private final int peerPort;
      private final OpenSslSessionId id;
      private final long timeout;
      private final long creationTime = System.currentTimeMillis();
      private volatile long lastAccessedTime = this.creationTime;
      private volatile boolean valid = true;
      private boolean freed;

      NativeSslSession(long var1, String var3, int var4, long var5) {
         this.session = var1;
         this.peerHost = var3;
         this.peerPort = var4;
         this.timeout = var5;
         this.id = new OpenSslSessionId(SSLSession.getSessionId(var1));
         this.leakTracker = LEAK_DETECTOR.track(this);
      }

      @Override
      public void setSessionId(OpenSslSessionId var1) {
         throw new UnsupportedOperationException();
      }

      boolean shouldBeSingleUse() {
         assert !this.freed;

         return SSLSession.shouldBeSingleUse(this.session);
      }

      long session() {
         assert !this.freed;

         return this.session;
      }

      boolean upRef() {
         assert !this.freed;

         return SSLSession.upRef(this.session);
      }

      synchronized void free() {
         this.close();
         SSLSession.free(this.session);
      }

      void close() {
         assert !this.freed;

         this.freed = true;
         this.invalidate();
         if (this.leakTracker != null) {
            this.leakTracker.close(this);
         }
      }

      @Override
      public OpenSslSessionId sessionId() {
         return this.id;
      }

      boolean isValid(long var1) {
         return this.creationTime + this.timeout >= var1 && this.valid;
      }

      @Override
      public void setLocalCertificate(Certificate[] var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public OpenSslSessionContext getSessionContext() {
         return null;
      }

      @Override
      public void tryExpandApplicationBufferSize(int var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void handshakeFinished(byte[] var1, String var2, String var3, byte[] var4, byte[][] var5, long var6, long var8) {
         throw new UnsupportedOperationException();
      }

      @Override
      public byte[] getId() {
         return this.id.cloneBytes();
      }

      @Override
      public long getCreationTime() {
         return this.creationTime;
      }

      void updateLastAccessedTime() {
         this.lastAccessedTime = System.currentTimeMillis();
      }

      @Override
      public long getLastAccessedTime() {
         return this.lastAccessedTime;
      }

      @Override
      public void invalidate() {
         this.valid = false;
      }

      @Override
      public boolean isValid() {
         return this.isValid(System.currentTimeMillis());
      }

      @Override
      public void putValue(String var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Object getValue(String var1) {
         return null;
      }

      @Override
      public void removeValue(String var1) {
      }

      @Override
      public String[] getValueNames() {
         return EmptyArrays.EMPTY_STRINGS;
      }

      @Override
      public Certificate[] getPeerCertificates() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Certificate[] getLocalCertificates() {
         throw new UnsupportedOperationException();
      }

      @Override
      public X509Certificate[] getPeerCertificateChain() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Principal getPeerPrincipal() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Principal getLocalPrincipal() {
         throw new UnsupportedOperationException();
      }

      @Override
      public String getCipherSuite() {
         return null;
      }

      @Override
      public String getProtocol() {
         return null;
      }

      @Override
      public String getPeerHost() {
         return this.peerHost;
      }

      @Override
      public int getPeerPort() {
         return this.peerPort;
      }

      @Override
      public int getPacketBufferSize() {
         return ReferenceCountedOpenSslEngine.MAX_RECORD_SIZE;
      }

      @Override
      public int getApplicationBufferSize() {
         return ReferenceCountedOpenSslEngine.MAX_PLAINTEXT_LENGTH;
      }

      @Override
      public int hashCode() {
         return this.id.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof OpenSslSession)) {
            return false;
         } else {
            OpenSslSession var2 = (OpenSslSession)var1;
            return this.id.equals(var2.sessionId());
         }
      }
   }
}
