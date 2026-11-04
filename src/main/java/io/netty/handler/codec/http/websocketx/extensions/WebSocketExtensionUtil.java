package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebSocketExtensionUtil {
   private static final String EXTENSION_SEPARATOR = ",";
   private static final String PARAMETER_SEPARATOR = ";";
   private static final char PARAMETER_EQUAL = '=';
   private static final Pattern PARAMETER = Pattern.compile("^([^=]+)(=[\\\"]?([^\\\"]+)[\\\"]?)?$");

   static boolean isWebsocketUpgrade(HttpHeaders var0) {
      return var0.contains(HttpHeaderNames.UPGRADE)
         && var0.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)
         && var0.contains(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET, true);
   }

   public static List<WebSocketExtensionData> extractExtensions(String var0) {
      String[] var1 = var0.split(",");
      if (var1.length <= 0) {
         return Collections.emptyList();
      } else {
         ArrayList var2 = new ArrayList(var1.length);

         for (String var6 : var1) {
            String[] var7 = var6.split(";");
            String var8 = var7[0].trim();
            Object var9;
            if (var7.length > 1) {
               var9 = new HashMap(var7.length - 1);

               for (int var10 = 1; var10 < var7.length; var10++) {
                  String var11 = var7[var10].trim();
                  Matcher var12 = PARAMETER.matcher(var11);
                  if (var12.matches() && var12.group(1) != null) {
                     var9.put(var12.group(1), var12.group(3));
                  }
               }
            } else {
               var9 = Collections.emptyMap();
            }

            var2.add(new WebSocketExtensionData(var8, (Map<String, String>)var9));
         }

         return var2;
      }
   }

   static String computeMergeExtensionsHeaderValue(String var0, List<WebSocketExtensionData> var1) {
      for (WebSocketExtensionData var4 : var0 != null ? extractExtensions(var0) : Collections.emptyList()) {
         WebSocketExtensionData var5 = null;

         int var6;
         for (var6 = 0; var6 < var1.size(); var6++) {
            WebSocketExtensionData var7 = (WebSocketExtensionData)var1.get(var6);
            if (var7.name().equals(var4.name())) {
               var5 = var7;
               break;
            }
         }

         if (var5 == null) {
            var1.add(var4);
         } else {
            HashMap var12 = new HashMap<>(var5.parameters());
            var12.putAll(var4.parameters());
            var1.set(var6, new WebSocketExtensionData(var5.name(), var12));
         }
      }

      StringBuilder var8 = new StringBuilder(150);

      for (WebSocketExtensionData var10 : var1) {
         var8.append(var10.name());

         for (Entry var13 : var10.parameters().entrySet()) {
            var8.append(";");
            var8.append((String)var13.getKey());
            if (var13.getValue() != null) {
               var8.append('=');
               var8.append((String)var13.getValue());
            }
         }

         var8.append(",");
      }

      if (!var1.isEmpty()) {
         var8.setLength(var8.length() - ",".length());
      }

      return var8.toString();
   }

   private WebSocketExtensionUtil() {
   }
}
