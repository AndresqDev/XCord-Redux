package org.apache.http;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Locale;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public final class HttpHost implements Serializable, Cloneable {
   private static final long serialVersionUID = -7529410654042457626L;
   public static final String DEFAULT_SCHEME_NAME = "http";
   protected final String hostname;
   protected final String lcHostname;
   protected final int port;
   protected final String schemeName;
   protected final InetAddress address;

   public HttpHost(String var1, int var2, String var3) {
      this.hostname = Args.containsNoBlanks(var1, "Host name");
      this.lcHostname = var1.toLowerCase(Locale.ROOT);
      if (var3 != null) {
         this.schemeName = var3.toLowerCase(Locale.ROOT);
      } else {
         this.schemeName = "http";
      }

      this.port = var2;
      this.address = null;
   }

   public HttpHost(String var1, int var2) {
      this(var1, var2, null);
   }

   public static HttpHost create(String var0) {
      Args.containsNoBlanks(var0, "HTTP Host");
      String var1 = var0;
      String var2 = null;
      int var3 = var0.indexOf("://");
      if (var3 > 0) {
         var2 = var0.substring(0, var3);
         var1 = var0.substring(var3 + 3);
      }

      int var4 = -1;
      int var5 = var1.lastIndexOf(":");
      if (var5 > 0) {
         try {
            var4 = Integer.parseInt(var1.substring(var5 + 1));
         } catch (NumberFormatException var7) {
            throw new IllegalArgumentException("Invalid HTTP host: " + var1);
         }

         var1 = var1.substring(0, var5);
      }

      return new HttpHost(var1, var4, var2);
   }

   public HttpHost(String var1) {
      this(var1, -1, null);
   }

   public HttpHost(InetAddress var1, int var2, String var3) {
      this(Args.notNull(var1, "Inet address"), var1.getHostName(), var2, var3);
   }

   public HttpHost(InetAddress var1, String var2, int var3, String var4) {
      this.address = Args.notNull(var1, "Inet address");
      this.hostname = Args.notNull(var2, "Hostname");
      this.lcHostname = this.hostname.toLowerCase(Locale.ROOT);
      if (var4 != null) {
         this.schemeName = var4.toLowerCase(Locale.ROOT);
      } else {
         this.schemeName = "http";
      }

      this.port = var3;
   }

   public HttpHost(InetAddress var1, int var2) {
      this(var1, var2, null);
   }

   public HttpHost(InetAddress var1) {
      this(var1, -1, null);
   }

   public HttpHost(HttpHost var1) {
      Args.notNull(var1, "HTTP host");
      this.hostname = var1.hostname;
      this.lcHostname = var1.lcHostname;
      this.schemeName = var1.schemeName;
      this.port = var1.port;
      this.address = var1.address;
   }

   public String getHostName() {
      return this.hostname;
   }

   public int getPort() {
      return this.port;
   }

   public String getSchemeName() {
      return this.schemeName;
   }

   public InetAddress getAddress() {
      return this.address;
   }

   public String toURI() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.schemeName);
      var1.append("://");
      var1.append(this.hostname);
      if (this.port != -1) {
         var1.append(':');
         var1.append(Integer.toString(this.port));
      }

      return var1.toString();
   }

   public String toHostString() {
      if (this.port != -1) {
         StringBuilder var1 = new StringBuilder(this.hostname.length() + 6);
         var1.append(this.hostname);
         var1.append(":");
         var1.append(Integer.toString(this.port));
         return var1.toString();
      } else {
         return this.hostname;
      }
   }

   @Override
   public String toString() {
      return this.toURI();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HttpHost)) {
         return false;
      } else {
         HttpHost var2 = (HttpHost)var1;
         return this.lcHostname.equals(var2.lcHostname)
            && this.port == var2.port
            && this.schemeName.equals(var2.schemeName)
            && (this.address == null ? var2.address == null : this.address.equals(var2.address));
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = LangUtils.hashCode(var1, this.lcHostname);
      var1 = LangUtils.hashCode(var1, this.port);
      var1 = LangUtils.hashCode(var1, this.schemeName);
      if (this.address != null) {
         var1 = LangUtils.hashCode(var1, this.address);
      }

      return var1;
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
