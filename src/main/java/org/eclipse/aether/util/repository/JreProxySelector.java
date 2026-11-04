package org.eclipse.aether.util.repository;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.Authenticator.RequestorType;
import java.net.Proxy.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationContext;
import org.eclipse.aether.repository.AuthenticationDigest;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.repository.RemoteRepository;

public final class JreProxySelector implements ProxySelector {
   @Override
   public Proxy getProxy(RemoteRepository var1) {
      List var2 = null;

      try {
         URI var3 = new URI(var1.getUrl()).parseServerAuthority();
         var2 = java.net.ProxySelector.getDefault().select(var3);
      } catch (Exception var6) {
      }

      if (var2 != null) {
         for (java.net.Proxy var4 : var2) {
            if (Type.DIRECT.equals(var4.type())) {
               break;
            }

            if (Type.HTTP.equals(var4.type()) && isValid(var4.address())) {
               InetSocketAddress var5 = (InetSocketAddress)var4.address();
               return new Proxy("http", var5.getHostName(), var5.getPort(), JreProxySelector.JreProxyAuthentication.INSTANCE);
            }
         }
      }

      return null;
   }

   private static boolean isValid(SocketAddress var0) {
      if (var0 instanceof InetSocketAddress) {
         InetSocketAddress var1 = (InetSocketAddress)var0;
         return var1.getPort() <= 0 ? false : var1.getHostName() != null && var1.getHostName().length() > 0;
      } else {
         return false;
      }
   }

   private static final class JreProxyAuthentication implements Authentication {
      public static final Authentication INSTANCE = new JreProxySelector.JreProxyAuthentication();

      @Override
      public void fill(AuthenticationContext var1, String var2, Map<String, String> var3) {
         Proxy var4 = var1.getProxy();
         if (var4 != null) {
            if ("username".equals(var2) || "password".equals(var2)) {
               try {
                  URL var5;
                  try {
                     var5 = new URL(var1.getRepository().getUrl());
                  } catch (Exception var7) {
                     var5 = null;
                  }

                  PasswordAuthentication var6 = Authenticator.requestPasswordAuthentication(
                     var4.getHost(), null, var4.getPort(), "http", "Credentials for proxy " + var4, null, var5, RequestorType.PROXY
                  );
                  if (var6 != null) {
                     var1.put("username", var6.getUserName());
                     var1.put("password", var6.getPassword());
                  } else {
                     var1.put("username", System.getProperty("http.proxyUser"));
                     var1.put("password", System.getProperty("http.proxyPassword"));
                  }
               } catch (SecurityException var8) {
               }
            }
         }
      }

      @Override
      public void digest(AuthenticationDigest var1) {
         var1.update(UUID.randomUUID().toString());
      }

      @Override
      public boolean equals(Object var1) {
         return this == var1 || var1 != null && this.getClass().equals(var1.getClass());
      }

      @Override
      public int hashCode() {
         return this.getClass().hashCode();
      }
   }
}
