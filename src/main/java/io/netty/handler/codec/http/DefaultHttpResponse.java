package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;

public class DefaultHttpResponse extends DefaultHttpMessage implements HttpResponse {
   private HttpResponseStatus status;

   public DefaultHttpResponse(HttpVersion var1, HttpResponseStatus var2) {
      this(var1, var2, true, false);
   }

   public DefaultHttpResponse(HttpVersion var1, HttpResponseStatus var2, boolean var3) {
      this(var1, var2, var3, false);
   }

   public DefaultHttpResponse(HttpVersion var1, HttpResponseStatus var2, boolean var3, boolean var4) {
      super(var1, var3, var4);
      this.status = ObjectUtil.checkNotNull(var2, "status");
   }

   public DefaultHttpResponse(HttpVersion var1, HttpResponseStatus var2, HttpHeaders var3) {
      super(var1, var3);
      this.status = ObjectUtil.checkNotNull(var2, "status");
   }

   @Deprecated
   @Override
   public HttpResponseStatus getStatus() {
      return this.status();
   }

   @Override
   public HttpResponseStatus status() {
      return this.status;
   }

   @Override
   public HttpResponse setStatus(HttpResponseStatus var1) {
      this.status = ObjectUtil.checkNotNull(var1, "status");
      return this;
   }

   @Override
   public HttpResponse setProtocolVersion(HttpVersion var1) {
      super.setProtocolVersion(var1);
      return this;
   }

   @Override
   public String toString() {
      return HttpMessageUtil.appendResponse(new StringBuilder(256), this).toString();
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 = 31 * var1 + this.status.hashCode();
      return 31 * var1 + super.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultHttpResponse)) {
         return false;
      } else {
         DefaultHttpResponse var2 = (DefaultHttpResponse)var1;
         return this.status.equals(var2.status()) && super.equals(var1);
      }
   }
}
