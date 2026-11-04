package org.apache.http.client.methods;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.util.Args;

public class HttpOptions extends HttpRequestBase {
   public static final String METHOD_NAME = "OPTIONS";

   public HttpOptions() {
   }

   public HttpOptions(URI var1) {
      this.setURI(var1);
   }

   public HttpOptions(String var1) {
      this.setURI(URI.create(var1));
   }

   @Override
   public String getMethod() {
      return "OPTIONS";
   }

   public Set<String> getAllowedMethods(HttpResponse var1) {
      Args.notNull(var1, "HTTP response");
      HeaderIterator var2 = var1.headerIterator("Allow");
      HashSet var3 = new HashSet();

      while (var2.hasNext()) {
         Header var4 = var2.nextHeader();
         HeaderElement[] var5 = var4.getElements();

         for (HeaderElement var9 : var5) {
            var3.add(var9.getName());
         }
      }

      return var3;
   }
}
