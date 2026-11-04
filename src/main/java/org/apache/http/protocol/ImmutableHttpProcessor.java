package org.apache.http.protocol;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public final class ImmutableHttpProcessor implements HttpProcessor {
   private final HttpRequestInterceptor[] requestInterceptors;
   private final HttpResponseInterceptor[] responseInterceptors;

   public ImmutableHttpProcessor(HttpRequestInterceptor[] var1, HttpResponseInterceptor[] var2) {
      if (var1 != null) {
         int var3 = var1.length;
         this.requestInterceptors = new HttpRequestInterceptor[var3];
         System.arraycopy(var1, 0, this.requestInterceptors, 0, var3);
      } else {
         this.requestInterceptors = new HttpRequestInterceptor[0];
      }

      if (var2 != null) {
         int var4 = var2.length;
         this.responseInterceptors = new HttpResponseInterceptor[var4];
         System.arraycopy(var2, 0, this.responseInterceptors, 0, var4);
      } else {
         this.responseInterceptors = new HttpResponseInterceptor[0];
      }
   }

   public ImmutableHttpProcessor(List<HttpRequestInterceptor> var1, List<HttpResponseInterceptor> var2) {
      if (var1 != null) {
         int var3 = var1.size();
         this.requestInterceptors = var1.toArray(new HttpRequestInterceptor[var3]);
      } else {
         this.requestInterceptors = new HttpRequestInterceptor[0];
      }

      if (var2 != null) {
         int var4 = var2.size();
         this.responseInterceptors = var2.toArray(new HttpResponseInterceptor[var4]);
      } else {
         this.responseInterceptors = new HttpResponseInterceptor[0];
      }
   }

   @Deprecated
   public ImmutableHttpProcessor(HttpRequestInterceptorList var1, HttpResponseInterceptorList var2) {
      if (var1 != null) {
         int var3 = var1.getRequestInterceptorCount();
         this.requestInterceptors = new HttpRequestInterceptor[var3];

         for (int var4 = 0; var4 < var3; var4++) {
            this.requestInterceptors[var4] = var1.getRequestInterceptor(var4);
         }
      } else {
         this.requestInterceptors = new HttpRequestInterceptor[0];
      }

      if (var2 != null) {
         int var5 = var2.getResponseInterceptorCount();
         this.responseInterceptors = new HttpResponseInterceptor[var5];

         for (int var6 = 0; var6 < var5; var6++) {
            this.responseInterceptors[var6] = var2.getResponseInterceptor(var6);
         }
      } else {
         this.responseInterceptors = new HttpResponseInterceptor[0];
      }
   }

   public ImmutableHttpProcessor(HttpRequestInterceptor... var1) {
      this(var1, null);
   }

   public ImmutableHttpProcessor(HttpResponseInterceptor... var1) {
      this(null, var1);
   }

   @Override
   public void process(HttpRequest var1, HttpContext var2) throws IOException, HttpException {
      for (HttpRequestInterceptor var6 : this.requestInterceptors) {
         var6.process(var1, var2);
      }
   }

   @Override
   public void process(HttpResponse var1, HttpContext var2) throws IOException, HttpException {
      for (HttpResponseInterceptor var6 : this.responseInterceptors) {
         var6.process(var1, var2);
      }
   }
}
