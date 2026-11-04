package org.apache.http.impl;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.TokenIterator;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.message.BasicTokenIterator;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class DefaultConnectionReuseStrategy implements ConnectionReuseStrategy {
   public static final DefaultConnectionReuseStrategy INSTANCE = new DefaultConnectionReuseStrategy();

   @Override
   public boolean keepAlive(HttpResponse var1, HttpContext var2) {
      Args.notNull(var1, "HTTP response");
      Args.notNull(var2, "HTTP context");
      if (var1.getStatusLine().getStatusCode() == 204) {
         Header var3 = var1.getFirstHeader("Content-Length");
         if (var3 != null) {
            try {
               int var4 = Integer.parseInt(var3.getValue());
               if (var4 > 0) {
                  return false;
               }
            } catch (NumberFormatException var11) {
            }
         }

         Header var15 = var1.getFirstHeader("Transfer-Encoding");
         if (var15 != null) {
            return false;
         }
      }

      HttpRequest var14 = (HttpRequest)var2.getAttribute("http.request");
      if (var14 != null) {
         try {
            BasicTokenIterator var16 = new BasicTokenIterator(var14.headerIterator("Connection"));

            while (var16.hasNext()) {
               String var5 = var16.nextToken();
               if ("Close".equalsIgnoreCase(var5)) {
                  return false;
               }
            }
         } catch (ParseException var13) {
            return false;
         }
      }

      ProtocolVersion var17 = var1.getStatusLine().getProtocolVersion();
      Header var18 = var1.getFirstHeader("Transfer-Encoding");
      if (var18 != null) {
         if (!"chunked".equalsIgnoreCase(var18.getValue())) {
            return false;
         }
      } else if (this.canResponseHaveBody(var14, var1)) {
         Header[] var6 = var1.getHeaders("Content-Length");
         if (var6.length != 1) {
            return false;
         }

         Header var7 = var6[0];

         try {
            long var8 = Long.parseLong(var7.getValue());
            if (var8 < 0L) {
               return false;
            }
         } catch (NumberFormatException var10) {
            return false;
         }
      }

      HeaderIterator var19 = var1.headerIterator("Connection");
      if (!var19.hasNext()) {
         var19 = var1.headerIterator("Proxy-Connection");
      }

      if (var19.hasNext()) {
         try {
            BasicTokenIterator var20 = new BasicTokenIterator(var19);
            boolean var21 = false;

            while (var20.hasNext()) {
               String var9 = var20.nextToken();
               if ("Close".equalsIgnoreCase(var9)) {
                  return false;
               }

               if ("Keep-Alive".equalsIgnoreCase(var9)) {
                  var21 = true;
               }
            }

            if (var21) {
               return true;
            }
         } catch (ParseException var12) {
            return false;
         }
      }

      return !var17.lessEquals(HttpVersion.HTTP_1_0);
   }

   protected TokenIterator createTokenIterator(HeaderIterator var1) {
      return new BasicTokenIterator(var1);
   }

   private boolean canResponseHaveBody(HttpRequest var1, HttpResponse var2) {
      if (var1 != null && var1.getRequestLine().getMethod().equalsIgnoreCase("HEAD")) {
         return false;
      } else {
         int var3 = var2.getStatusLine().getStatusCode();
         return var3 >= 200 && var3 != 204 && var3 != 304 && var3 != 205;
      }
   }
}
