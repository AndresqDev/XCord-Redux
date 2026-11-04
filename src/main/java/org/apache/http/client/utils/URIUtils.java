package org.apache.http.client.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

public class URIUtils {
   public static final EnumSet<URIUtils.UriFlag> NO_FLAGS = EnumSet.noneOf(URIUtils.UriFlag.class);
   public static final EnumSet<URIUtils.UriFlag> DROP_FRAGMENT = EnumSet.of(URIUtils.UriFlag.DROP_FRAGMENT);
   public static final EnumSet<URIUtils.UriFlag> NORMALIZE = EnumSet.of(URIUtils.UriFlag.NORMALIZE);
   public static final EnumSet<URIUtils.UriFlag> DROP_FRAGMENT_AND_NORMALIZE = EnumSet.of(URIUtils.UriFlag.DROP_FRAGMENT, URIUtils.UriFlag.NORMALIZE);

   @Deprecated
   public static URI createURI(String var0, String var1, int var2, String var3, String var4, String var5) throws URISyntaxException {
      StringBuilder var6 = new StringBuilder();
      if (var1 != null) {
         if (var0 != null) {
            var6.append(var0);
            var6.append("://");
         }

         var6.append(var1);
         if (var2 > 0) {
            var6.append(':');
            var6.append(var2);
         }
      }

      if (var3 == null || !var3.startsWith("/")) {
         var6.append('/');
      }

      if (var3 != null) {
         var6.append(var3);
      }

      if (var4 != null) {
         var6.append('?');
         var6.append(var4);
      }

      if (var5 != null) {
         var6.append('#');
         var6.append(var5);
      }

      return new URI(var6.toString());
   }

   @Deprecated
   public static URI rewriteURI(URI var0, HttpHost var1, boolean var2) throws URISyntaxException {
      return rewriteURI(var0, var1, var2 ? DROP_FRAGMENT : NO_FLAGS);
   }

   public static URI rewriteURI(URI var0, HttpHost var1, EnumSet<URIUtils.UriFlag> var2) throws URISyntaxException {
      Args.notNull(var0, "URI");
      Args.notNull(var2, "URI flags");
      if (var0.isOpaque()) {
         return var0;
      } else {
         URIBuilder var3 = new URIBuilder(var0);
         if (var1 != null) {
            var3.setScheme(var1.getSchemeName());
            var3.setHost(var1.getHostName());
            var3.setPort(var1.getPort());
         } else {
            var3.setScheme(null);
            var3.setHost(null);
            var3.setPort(-1);
         }

         if (var2.contains(URIUtils.UriFlag.DROP_FRAGMENT)) {
            var3.setFragment(null);
         }

         if (var2.contains(URIUtils.UriFlag.NORMALIZE)) {
            List var4 = var3.getPathSegments();
            ArrayList var5 = new ArrayList(var4);
            Iterator var6 = var5.iterator();

            while (var6.hasNext()) {
               String var7 = (String)var6.next();
               if (var7.isEmpty() && var6.hasNext()) {
                  var6.remove();
               }
            }

            if (var5.size() != var4.size()) {
               var3.setPathSegments(var5);
            }
         }

         if (var3.isPathEmpty()) {
            var3.setPathSegments("");
         }

         return var3.build();
      }
   }

   public static URI rewriteURI(URI var0, HttpHost var1) throws URISyntaxException {
      return rewriteURI(var0, var1, NORMALIZE);
   }

   public static URI rewriteURI(URI var0) throws URISyntaxException {
      Args.notNull(var0, "URI");
      if (var0.isOpaque()) {
         return var0;
      } else {
         URIBuilder var1 = new URIBuilder(var0);
         if (var1.getUserInfo() != null) {
            var1.setUserInfo(null);
         }

         if (var1.getPathSegments().isEmpty()) {
            var1.setPathSegments("");
         }

         if (TextUtils.isEmpty(var1.getPath())) {
            var1.setPath("/");
         }

         if (var1.getHost() != null) {
            var1.setHost(var1.getHost().toLowerCase(Locale.ROOT));
         }

         var1.setFragment(null);
         return var1.build();
      }
   }

   public static URI rewriteURIForRoute(URI var0, RouteInfo var1) throws URISyntaxException {
      return rewriteURIForRoute(var0, var1, true);
   }

   public static URI rewriteURIForRoute(URI var0, RouteInfo var1, boolean var2) throws URISyntaxException {
      if (var0 == null) {
         return null;
      } else if (var1.getProxyHost() != null && !var1.isTunnelled()) {
         return var0.isAbsolute() ? rewriteURI(var0) : rewriteURI(var0, var1.getTargetHost(), var2 ? DROP_FRAGMENT_AND_NORMALIZE : DROP_FRAGMENT);
      } else {
         return var0.isAbsolute() ? rewriteURI(var0, null, var2 ? DROP_FRAGMENT_AND_NORMALIZE : DROP_FRAGMENT) : rewriteURI(var0);
      }
   }

   public static URI resolve(URI var0, String var1) {
      return resolve(var0, URI.create(var1));
   }

   public static URI resolve(URI var0, URI var1) {
      Args.notNull(var0, "Base URI");
      Args.notNull(var1, "Reference URI");
      String var2 = var1.toASCIIString();
      if (var2.startsWith("?")) {
         String var7 = var0.toASCIIString();
         int var10 = var7.indexOf(63);
         var7 = var10 > -1 ? var7.substring(0, var10) : var7;
         return URI.create(var7 + var2);
      } else {
         boolean var3 = var2.isEmpty();
         URI var9;
         if (var3) {
            var9 = var0.resolve(URI.create("#"));
            String var5 = var9.toASCIIString();
            var9 = URI.create(var5.substring(0, var5.indexOf(35)));
         } else {
            var9 = var0.resolve(var1);
         }

         try {
            return normalizeSyntax(var9);
         } catch (URISyntaxException var6) {
            throw new IllegalArgumentException(var6);
         }
      }
   }

   public static URI normalizeSyntax(URI var0) throws URISyntaxException {
      if (!var0.isOpaque() && var0.getAuthority() != null) {
         URIBuilder var1 = new URIBuilder(var0);
         List var2 = var1.getPathSegments();
         Stack var3 = new Stack();

         for (String var5 : var2) {
            if (!".".equals(var5)) {
               if ("..".equals(var5)) {
                  if (!var3.isEmpty()) {
                     var3.pop();
                  }
               } else {
                  var3.push(var5);
               }
            }
         }

         if (var3.size() == 0) {
            var3.add("");
         }

         var1.setPathSegments(var3);
         if (var1.getScheme() != null) {
            var1.setScheme(var1.getScheme().toLowerCase(Locale.ROOT));
         }

         if (var1.getHost() != null) {
            var1.setHost(var1.getHost().toLowerCase(Locale.ROOT));
         }

         return var1.build();
      } else {
         return var0;
      }
   }

   public static HttpHost extractHost(URI var0) {
      if (var0 == null) {
         return null;
      } else {
         if (var0.isAbsolute()) {
            if (var0.getHost() != null) {
               return new HttpHost(var0.getHost(), var0.getPort(), var0.getScheme());
            }

            if (var0.getAuthority() != null) {
               String var1 = var0.getAuthority();
               int var2 = var1.indexOf(64);
               if (var2 != -1) {
                  var1 = var1.substring(var2 + 1);
               }

               String var3 = var0.getScheme();
               var2 = var1.indexOf(":");
               String var4;
               int var5;
               if (var2 != -1) {
                  var4 = var1.substring(0, var2);

                  try {
                     String var6 = var1.substring(var2 + 1);
                     var5 = !TextUtils.isEmpty(var6) ? Integer.parseInt(var6) : -1;
                  } catch (NumberFormatException var8) {
                     return null;
                  }
               } else {
                  var4 = var1;
                  var5 = -1;
               }

               try {
                  return new HttpHost(var4, var5, var3);
               } catch (IllegalArgumentException var7) {
                  return null;
               }
            }
         }

         return null;
      }
   }

   public static URI resolve(URI var0, HttpHost var1, List<URI> var2) throws URISyntaxException {
      Args.notNull(var0, "Request URI");
      URIBuilder var3;
      if (var2 != null && !var2.isEmpty()) {
         var3 = new URIBuilder((URI)var2.get(var2.size() - 1));
         String var4 = var3.getFragment();

         for (int var5 = var2.size() - 1; var4 == null && var5 >= 0; var5--) {
            var4 = ((URI)var2.get(var5)).getFragment();
         }

         var3.setFragment(var4);
      } else {
         var3 = new URIBuilder(var0);
      }

      if (var3.getFragment() == null) {
         var3.setFragment(var0.getFragment());
      }

      if (var1 != null && !var3.isAbsolute()) {
         var3.setScheme(var1.getSchemeName());
         var3.setHost(var1.getHostName());
         var3.setPort(var1.getPort());
      }

      return var3.build();
   }

   private URIUtils() {
   }

   public static enum UriFlag {
      DROP_FRAGMENT,
      NORMALIZE;
   }
}
