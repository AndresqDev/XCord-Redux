package org.apache.http.impl.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.AuthScheme;
import org.apache.http.client.AuthCache;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class BasicAuthCache implements AuthCache {
   private final Log log = LogFactory.getLog(this.getClass());
   private final Map<HttpHost, byte[]> map = new ConcurrentHashMap<>();
   private final SchemePortResolver schemePortResolver;

   public BasicAuthCache(SchemePortResolver var1) {
      this.schemePortResolver = (SchemePortResolver)(var1 != null ? var1 : DefaultSchemePortResolver.INSTANCE);
   }

   public BasicAuthCache() {
      this(null);
   }

   protected HttpHost getKey(HttpHost var1) {
      if (var1.getPort() <= 0) {
         int var2;
         try {
            var2 = this.schemePortResolver.resolve(var1);
         } catch (UnsupportedSchemeException var4) {
            return var1;
         }

         return new HttpHost(var1.getHostName(), var2, var1.getSchemeName());
      } else {
         return var1;
      }
   }

   @Override
   public void put(HttpHost var1, AuthScheme var2) {
      Args.notNull(var1, "HTTP host");
      if (var2 != null) {
         if (var2 instanceof Serializable) {
            try {
               ByteArrayOutputStream var3 = new ByteArrayOutputStream();
               ObjectOutputStream var4 = new ObjectOutputStream(var3);
               var4.writeObject(var2);
               var4.close();
               this.map.put(this.getKey(var1), var3.toByteArray());
            } catch (IOException var5) {
               if (this.log.isWarnEnabled()) {
                  this.log.warn("Unexpected I/O error while serializing auth scheme", var5);
               }
            }
         } else if (this.log.isDebugEnabled()) {
            this.log.debug("Auth scheme " + var2.getClass() + " is not serializable");
         }
      }
   }

   @Override
   public AuthScheme get(HttpHost var1) {
      Args.notNull(var1, "HTTP host");
      byte[] var2 = this.map.get(this.getKey(var1));
      if (var2 != null) {
         try {
            ByteArrayInputStream var3 = new ByteArrayInputStream(var2);
            ObjectInputStream var4 = new ObjectInputStream(var3);
            AuthScheme var5 = (AuthScheme)var4.readObject();
            var4.close();
            return var5;
         } catch (IOException var6) {
            if (this.log.isWarnEnabled()) {
               this.log.warn("Unexpected I/O error while de-serializing auth scheme", var6);
            }

            return null;
         } catch (ClassNotFoundException var7) {
            if (this.log.isWarnEnabled()) {
               this.log.warn("Unexpected error while de-serializing auth scheme", var7);
            }

            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public void remove(HttpHost var1) {
      Args.notNull(var1, "HTTP host");
      this.map.remove(this.getKey(var1));
   }

   @Override
   public void clear() {
      this.map.clear();
   }

   @Override
   public String toString() {
      return this.map.toString();
   }
}
