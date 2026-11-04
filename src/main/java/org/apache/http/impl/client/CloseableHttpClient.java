package org.apache.http.impl.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public abstract class CloseableHttpClient implements Closeable, HttpClient {
   private final Log log = LogFactory.getLog(this.getClass());

   protected abstract CloseableHttpResponse doExecute(HttpHost var1, HttpRequest var2, HttpContext var3) throws IOException, ClientProtocolException;

   public CloseableHttpResponse execute(HttpHost var1, HttpRequest var2, HttpContext var3) throws IOException, ClientProtocolException {
      return this.doExecute(var1, var2, var3);
   }

   public CloseableHttpResponse execute(HttpUriRequest var1, HttpContext var2) throws IOException, ClientProtocolException {
      Args.notNull(var1, "HTTP request");
      return this.doExecute(determineTarget(var1), var1, var2);
   }

   private static HttpHost determineTarget(HttpUriRequest var0) throws ClientProtocolException {
      HttpHost var1 = null;
      URI var2 = var0.getURI();
      if (var2.isAbsolute()) {
         var1 = URIUtils.extractHost(var2);
         if (var1 == null) {
            throw new ClientProtocolException("URI does not specify a valid host name: " + var2);
         }
      }

      return var1;
   }

   public CloseableHttpResponse execute(HttpUriRequest var1) throws IOException, ClientProtocolException {
      return this.execute(var1, (HttpContext)null);
   }

   public CloseableHttpResponse execute(HttpHost var1, HttpRequest var2) throws IOException, ClientProtocolException {
      return this.doExecute(var1, var2, null);
   }

   @Override
   public <T> T execute(HttpUriRequest var1, ResponseHandler<? extends T> var2) throws IOException, ClientProtocolException {
      return this.execute(var1, var2, null);
   }

   @Override
   public <T> T execute(HttpUriRequest var1, ResponseHandler<? extends T> var2, HttpContext var3) throws IOException, ClientProtocolException {
      HttpHost var4 = determineTarget(var1);
      return this.execute(var4, var1, var2, var3);
   }

   @Override
   public <T> T execute(HttpHost var1, HttpRequest var2, ResponseHandler<? extends T> var3) throws IOException, ClientProtocolException {
      return this.execute(var1, var2, var3, null);
   }

   @Override
   public <T> T execute(HttpHost var1, HttpRequest var2, ResponseHandler<? extends T> var3, HttpContext var4) throws IOException, ClientProtocolException {
      Args.notNull(var3, "Response handler");
      CloseableHttpResponse var5 = this.execute(var1, var2, var4);

      Object var8;
      try {
         Object var6 = var3.handleResponse(var5);
         HttpEntity var16 = var5.getEntity();
         EntityUtils.consume(var16);
         var8 = var6;
      } catch (ClientProtocolException var14) {
         HttpEntity var7 = var5.getEntity();

         try {
            EntityUtils.consume(var7);
         } catch (Exception var13) {
            this.log.warn("Error consuming content after an exception.", var13);
         }

         throw var14;
      } finally {
         var5.close();
      }

      return (T)var8;
   }
}
