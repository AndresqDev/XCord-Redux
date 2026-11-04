package org.eclipse.aether.repository;

import java.util.Objects;

public final class Proxy {
   public static final String TYPE_HTTP = "http";
   public static final String TYPE_HTTPS = "https";
   private final String type;
   private final String host;
   private final int port;
   private final Authentication auth;

   public Proxy(String var1, String var2, int var3) {
      this(var1, var2, var3, null);
   }

   public Proxy(String var1, String var2, int var3, Authentication var4) {
      this.type = var1 != null ? var1 : "";
      this.host = var2 != null ? var2 : "";
      this.port = var3;
      this.auth = var4;
   }

   public String getType() {
      return this.type;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public Authentication getAuthentication() {
      return this.auth;
   }

   @Override
   public String toString() {
      return this.getHost() + ':' + this.getPort();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         Proxy var2 = (Proxy)var1;
         return Objects.equals(this.type, var2.type) && Objects.equals(this.host, var2.host) && this.port == var2.port && Objects.equals(this.auth, var2.auth);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + hash(this.host);
      var1 = var1 * 31 + hash(this.type);
      var1 = var1 * 31 + this.port;
      return var1 * 31 + hash(this.auth);
   }

   private static int hash(Object var0) {
      return var0 != null ? var0.hashCode() : 0;
   }
}
