package org.eclipse.aether.util.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.repository.RemoteRepository;

public final class DefaultProxySelector implements ProxySelector {
   private List<DefaultProxySelector.ProxyDef> proxies = new ArrayList<>();

   public DefaultProxySelector add(Proxy var1, String var2) {
      Objects.requireNonNull(var1, "proxy cannot be null");
      this.proxies.add(new DefaultProxySelector.ProxyDef(var1, var2));
      return this;
   }

   @Override
   public Proxy getProxy(RemoteRepository var1) {
      HashMap var2 = new HashMap();
      String var3 = var1.getHost();

      for (DefaultProxySelector.ProxyDef var5 : this.proxies) {
         if (!var5.nonProxyHosts.isNonProxyHost(var3)) {
            String var6 = var5.proxy.getType().toLowerCase(Locale.ENGLISH);
            if (!var2.containsKey(var6)) {
               var2.put(var6, var5);
            }
         }
      }

      String var7 = var1.getProtocol().toLowerCase(Locale.ENGLISH);
      if ("davs".equals(var7)) {
         var7 = "https";
      } else if ("dav".equals(var7)) {
         var7 = "http";
      } else if (var7.startsWith("dav:")) {
         var7 = var7.substring("dav:".length());
      }

      DefaultProxySelector.ProxyDef var8 = (DefaultProxySelector.ProxyDef)var2.get(var7);
      if (var8 == null && "https".equals(var7)) {
         var8 = (DefaultProxySelector.ProxyDef)var2.get("http");
      }

      return var8 != null ? var8.proxy : null;
   }

   static class NonProxyHosts {
      private final Pattern[] patterns;

      NonProxyHosts(String var1) {
         ArrayList var2 = new ArrayList();
         if (var1 != null) {
            StringTokenizer var3 = new StringTokenizer(var1, "|");

            while (var3.hasMoreTokens()) {
               String var4 = var3.nextToken();
               var4 = var4.replace(".", "\\.").replace("*", ".*");
               var2.add(Pattern.compile(var4, 2));
            }
         }

         this.patterns = var2.toArray(new Pattern[var2.size()]);
      }

      boolean isNonProxyHost(String var1) {
         if (var1 != null) {
            for (Pattern var5 : this.patterns) {
               if (var5.matcher(var1).matches()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   static class ProxyDef {
      final Proxy proxy;
      final DefaultProxySelector.NonProxyHosts nonProxyHosts;

      ProxyDef(Proxy var1, String var2) {
         this.proxy = var1;
         this.nonProxyHosts = new DefaultProxySelector.NonProxyHosts(var2);
      }
   }
}
