package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpVersion;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.ProtocolException;
import org.apache.http.UnsupportedHttpVersionException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class HttpService {
   private volatile HttpParams params = null;
   private volatile HttpProcessor processor = null;
   private volatile HttpRequestHandlerMapper handlerMapper = null;
   private volatile ConnectionReuseStrategy connStrategy = null;
   private volatile HttpResponseFactory responseFactory = null;
   private volatile HttpExpectationVerifier expectationVerifier = null;

   @Deprecated
   public HttpService(
      HttpProcessor var1,
      ConnectionReuseStrategy var2,
      HttpResponseFactory var3,
      HttpRequestHandlerResolver var4,
      HttpExpectationVerifier var5,
      HttpParams var6
   ) {
      this(var1, var2, var3, new HttpService.HttpRequestHandlerResolverAdapter(var4), var5);
      this.params = var6;
   }

   @Deprecated
   public HttpService(HttpProcessor var1, ConnectionReuseStrategy var2, HttpResponseFactory var3, HttpRequestHandlerResolver var4, HttpParams var5) {
      this(var1, var2, var3, new HttpService.HttpRequestHandlerResolverAdapter(var4), null);
      this.params = var5;
   }

   @Deprecated
   public HttpService(HttpProcessor var1, ConnectionReuseStrategy var2, HttpResponseFactory var3) {
      this.setHttpProcessor(var1);
      this.setConnReuseStrategy(var2);
      this.setResponseFactory(var3);
   }

   public HttpService(HttpProcessor var1, ConnectionReuseStrategy var2, HttpResponseFactory var3, HttpRequestHandlerMapper var4, HttpExpectationVerifier var5) {
      this.processor = Args.notNull(var1, "HTTP processor");
      this.connStrategy = (ConnectionReuseStrategy)(var2 != null ? var2 : DefaultConnectionReuseStrategy.INSTANCE);
      this.responseFactory = (HttpResponseFactory)(var3 != null ? var3 : DefaultHttpResponseFactory.INSTANCE);
      this.handlerMapper = var4;
      this.expectationVerifier = var5;
   }

   public HttpService(HttpProcessor var1, ConnectionReuseStrategy var2, HttpResponseFactory var3, HttpRequestHandlerMapper var4) {
      this(var1, var2, var3, var4, null);
   }

   public HttpService(HttpProcessor var1, HttpRequestHandlerMapper var2) {
      this(var1, null, null, var2, null);
   }

   @Deprecated
   public void setHttpProcessor(HttpProcessor var1) {
      Args.notNull(var1, "HTTP processor");
      this.processor = var1;
   }

   @Deprecated
   public void setConnReuseStrategy(ConnectionReuseStrategy var1) {
      Args.notNull(var1, "Connection reuse strategy");
      this.connStrategy = var1;
   }

   @Deprecated
   public void setResponseFactory(HttpResponseFactory var1) {
      Args.notNull(var1, "Response factory");
      this.responseFactory = var1;
   }

   @Deprecated
   public void setParams(HttpParams var1) {
      this.params = var1;
   }

   @Deprecated
   public void setHandlerResolver(HttpRequestHandlerResolver var1) {
      this.handlerMapper = new HttpService.HttpRequestHandlerResolverAdapter(var1);
   }

   @Deprecated
   public void setExpectationVerifier(HttpExpectationVerifier var1) {
      this.expectationVerifier = var1;
   }

   @Deprecated
   public HttpParams getParams() {
      return this.params;
   }

   public void handleRequest(HttpServerConnection var1, HttpContext var2) throws IOException, HttpException {
      var2.setAttribute("http.connection", var1);
      HttpRequest var3 = null;
      HttpResponse var4 = null;

      try {
         var3 = var1.receiveRequestHeader();
         if (var3 instanceof HttpEntityEnclosingRequest) {
            if (((HttpEntityEnclosingRequest)var3).expectContinue()) {
               var4 = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_1, 100, var2);
               if (this.expectationVerifier != null) {
                  try {
                     this.expectationVerifier.verify(var3, var4, var2);
                  } catch (HttpException var6) {
                     var4 = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_0, 500, var2);
                     this.handleException(var6, var4);
                  }
               }

               if (var4.getStatusLine().getStatusCode() < 200) {
                  var1.sendResponseHeader(var4);
                  var1.flush();
                  var4 = null;
                  var1.receiveRequestEntity((HttpEntityEnclosingRequest)var3);
               }
            } else {
               var1.receiveRequestEntity((HttpEntityEnclosingRequest)var3);
            }
         }

         var2.setAttribute("http.request", var3);
         if (var4 == null) {
            var4 = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_1, 200, var2);
            this.processor.process(var3, var2);
            this.doService(var3, var4, var2);
         }

         if (var3 instanceof HttpEntityEnclosingRequest) {
            HttpEntity var5 = ((HttpEntityEnclosingRequest)var3).getEntity();
            EntityUtils.consume(var5);
         }
      } catch (HttpException var7) {
         var4 = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_0, 500, var2);
         this.handleException(var7, var4);
      }

      var2.setAttribute("http.response", var4);
      this.processor.process(var4, var2);
      var1.sendResponseHeader(var4);
      if (this.canResponseHaveBody(var3, var4)) {
         var1.sendResponseEntity(var4);
      }

      var1.flush();
      if (!this.connStrategy.keepAlive(var4, var2)) {
         var1.close();
      }
   }

   private boolean canResponseHaveBody(HttpRequest var1, HttpResponse var2) {
      if (var1 != null && "HEAD".equalsIgnoreCase(var1.getRequestLine().getMethod())) {
         return false;
      } else {
         int var3 = var2.getStatusLine().getStatusCode();
         return var3 >= 200 && var3 != 204 && var3 != 304 && var3 != 205;
      }
   }

   protected void handleException(HttpException var1, HttpResponse var2) {
      if (var1 instanceof MethodNotSupportedException) {
         var2.setStatusCode(501);
      } else if (var1 instanceof UnsupportedHttpVersionException) {
         var2.setStatusCode(505);
      } else if (var1 instanceof ProtocolException) {
         var2.setStatusCode(400);
      } else {
         var2.setStatusCode(500);
      }

      String var3 = var1.getMessage();
      if (var3 == null) {
         var3 = var1.toString();
      }

      byte[] var4 = EncodingUtils.getAsciiBytes(var3);
      ByteArrayEntity var5 = new ByteArrayEntity(var4);
      var5.setContentType("text/plain; charset=US-ASCII");
      var2.setEntity(var5);
   }

   protected void doService(HttpRequest var1, HttpResponse var2, HttpContext var3) throws HttpException, IOException {
      HttpRequestHandler var4 = null;
      if (this.handlerMapper != null) {
         var4 = this.handlerMapper.lookup(var1);
      }

      if (var4 != null) {
         var4.handle(var1, var2, var3);
      } else {
         var2.setStatusCode(501);
      }
   }

   @Deprecated
   private static class HttpRequestHandlerResolverAdapter implements HttpRequestHandlerMapper {
      private final HttpRequestHandlerResolver resolver;

      public HttpRequestHandlerResolverAdapter(HttpRequestHandlerResolver var1) {
         this.resolver = var1;
      }

      @Override
      public HttpRequestHandler lookup(HttpRequest var1) {
         return this.resolver.lookup(var1.getRequestLine().getUri());
      }
   }
}
