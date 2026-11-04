package org.apache.http.client.methods;

import java.net.URI;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

public class HttpRequestWrapper extends AbstractHttpMessage implements HttpUriRequest {
   private final HttpRequest original;
   private final HttpHost target;
   private final String method;
   private RequestLine requestLine;
   private ProtocolVersion version;
   private URI uri;

   private HttpRequestWrapper(HttpRequest var1, HttpHost var2) {
      this.original = Args.notNull(var1, "HTTP request");
      this.target = var2;
      this.version = this.original.getRequestLine().getProtocolVersion();
      this.method = this.original.getRequestLine().getMethod();
      if (var1 instanceof HttpUriRequest) {
         this.uri = ((HttpUriRequest)var1).getURI();
      } else {
         this.uri = null;
      }

      this.setHeaders(var1.getAllHeaders());
   }

   @Override
   public ProtocolVersion getProtocolVersion() {
      return this.version != null ? this.version : this.original.getProtocolVersion();
   }

   public void setProtocolVersion(ProtocolVersion var1) {
      this.version = var1;
      this.requestLine = null;
   }

   @Override
   public URI getURI() {
      return this.uri;
   }

   public void setURI(URI var1) {
      this.uri = var1;
      this.requestLine = null;
   }

   @Override
   public String getMethod() {
      return this.method;
   }

   @Override
   public void abort() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isAborted() {
      return false;
   }

   @Override
   public RequestLine getRequestLine() {
      if (this.requestLine == null) {
         String var1;
         if (this.uri != null) {
            var1 = this.uri.toASCIIString();
         } else {
            var1 = this.original.getRequestLine().getUri();
         }

         if (var1 == null || var1.isEmpty()) {
            var1 = "/";
         }

         this.requestLine = new BasicRequestLine(this.method, var1, this.getProtocolVersion());
      }

      return this.requestLine;
   }

   public HttpRequest getOriginal() {
      return this.original;
   }

   public HttpHost getTarget() {
      return this.target;
   }

   @Override
   public String toString() {
      return this.getRequestLine() + " " + this.headergroup;
   }

   public static HttpRequestWrapper wrap(HttpRequest var0) {
      return wrap(var0, null);
   }

   public static HttpRequestWrapper wrap(HttpRequest var0, HttpHost var1) {
      Args.notNull(var0, "HTTP request");
      return (HttpRequestWrapper)(var0 instanceof HttpEntityEnclosingRequest
         ? new HttpRequestWrapper.HttpEntityEnclosingRequestWrapper((HttpEntityEnclosingRequest)var0, var1)
         : new HttpRequestWrapper(var0, var1));
   }

   @Deprecated
   @Override
   public HttpParams getParams() {
      if (this.params == null) {
         this.params = this.original.getParams().copy();
      }

      return this.params;
   }

   static class HttpEntityEnclosingRequestWrapper extends HttpRequestWrapper implements HttpEntityEnclosingRequest {
      private HttpEntity entity;

      HttpEntityEnclosingRequestWrapper(HttpEntityEnclosingRequest var1, HttpHost var2) {
         super(var1, var2);
         this.entity = var1.getEntity();
      }

      @Override
      public HttpEntity getEntity() {
         return this.entity;
      }

      @Override
      public void setEntity(HttpEntity var1) {
         this.entity = var1;
      }

      @Override
      public boolean expectContinue() {
         Header var1 = this.getFirstHeader("Expect");
         return var1 != null && "100-continue".equalsIgnoreCase(var1.getValue());
      }
   }
}
