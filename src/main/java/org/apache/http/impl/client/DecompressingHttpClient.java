package org.apache.http.impl.client;

import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

@Deprecated
public class DecompressingHttpClient implements HttpClient {
   private final HttpClient backend;
   private final HttpRequestInterceptor acceptEncodingInterceptor;
   private final HttpResponseInterceptor contentEncodingInterceptor;

   public DecompressingHttpClient() {
      this(new DefaultHttpClient());
   }

   public DecompressingHttpClient(HttpClient var1) {
      this(var1, new RequestAcceptEncoding(), new ResponseContentEncoding());
   }

   DecompressingHttpClient(HttpClient var1, HttpRequestInterceptor var2, HttpResponseInterceptor var3) {
      this.backend = var1;
      this.acceptEncodingInterceptor = var2;
      this.contentEncodingInterceptor = var3;
   }

   @Override
   public HttpParams getParams() {
      return this.backend.getParams();
   }

   @Override
   public ClientConnectionManager getConnectionManager() {
      return this.backend.getConnectionManager();
   }

   @Override
   public HttpResponse execute(HttpUriRequest var1) throws IOException, ClientProtocolException {
      return this.execute(this.getHttpHost(var1), var1, (HttpContext)null);
   }

   public HttpClient getHttpClient() {
      return this.backend;
   }

   HttpHost getHttpHost(HttpUriRequest var1) {
      URI var2 = var1.getURI();
      return URIUtils.extractHost(var2);
   }

   @Override
   public HttpResponse execute(HttpUriRequest var1, HttpContext var2) throws IOException, ClientProtocolException {
      return this.execute(this.getHttpHost(var1), var1, var2);
   }

   @Override
   public HttpResponse execute(HttpHost var1, HttpRequest var2) throws IOException, ClientProtocolException {
      return this.execute(var1, var2, (HttpContext)null);
   }

   @Override
   public HttpResponse execute(HttpHost var1, HttpRequest var2, HttpContext var3) throws IOException, ClientProtocolException {
      try {
         Object var4 = var3 != null ? var3 : new BasicHttpContext();
         Object var5;
         if (var2 instanceof HttpEntityEnclosingRequest) {
            var5 = new EntityEnclosingRequestWrapper((HttpEntityEnclosingRequest)var2);
         } else {
            var5 = new RequestWrapper(var2);
         }

         this.acceptEncodingInterceptor.process((HttpRequest)var5, (HttpContext)var4);
         HttpResponse var6 = this.backend.execute(var1, (HttpRequest)var5, (HttpContext)var4);

         try {
            this.contentEncodingInterceptor.process(var6, (HttpContext)var4);
            if (Boolean.TRUE.equals(((HttpContext)var4).getAttribute("http.client.response.uncompressed"))) {
               var6.removeHeaders("Content-Length");
               var6.removeHeaders("Content-Encoding");
               var6.removeHeaders("Content-MD5");
            }

            return var6;
         } catch (HttpException var8) {
            EntityUtils.consume(var6.getEntity());
            throw var8;
         } catch (IOException var9) {
            EntityUtils.consume(var6.getEntity());
            throw var9;
         } catch (RuntimeException var10) {
            EntityUtils.consume(var6.getEntity());
            throw var10;
         }
      } catch (HttpException var11) {
         throw new ClientProtocolException(var11);
      }
   }

   @Override
   public <T> T execute(HttpUriRequest var1, ResponseHandler<? extends T> var2) throws IOException, ClientProtocolException {
      return this.execute(this.getHttpHost(var1), var1, var2);
   }

   @Override
   public <T> T execute(HttpUriRequest var1, ResponseHandler<? extends T> var2, HttpContext var3) throws IOException, ClientProtocolException {
      return this.execute(this.getHttpHost(var1), var1, var2, var3);
   }

   @Override
   public <T> T execute(HttpHost var1, HttpRequest var2, ResponseHandler<? extends T> var3) throws IOException, ClientProtocolException {
      return this.execute(var1, var2, var3, null);
   }

   @Override
   public <T> T execute(HttpHost var1, HttpRequest var2, ResponseHandler<? extends T> var3, HttpContext var4) throws IOException, ClientProtocolException {
      HttpResponse var5 = this.execute(var1, var2, var4);

      Object var6;
      try {
         var6 = var3.handleResponse(var5);
      } finally {
         HttpEntity var9 = var5.getEntity();
         if (var9 != null) {
            EntityUtils.consume(var9);
         }
      }

      return (T)var6;
   }
}
