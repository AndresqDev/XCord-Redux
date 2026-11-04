package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;

public class DefaultHttpRequest extends DefaultHttpMessage implements HttpRequest {
   private static final int HASH_CODE_PRIME = 31;
   private HttpMethod method;
   private String uri;

   public DefaultHttpRequest(HttpVersion var1, HttpMethod var2, String var3) {
      this(var1, var2, var3, true);
   }

   public DefaultHttpRequest(HttpVersion var1, HttpMethod var2, String var3, boolean var4) {
      super(var1, var4, false);
      this.method = ObjectUtil.checkNotNull(var2, "method");
      this.uri = ObjectUtil.checkNotNull(var3, "uri");
   }

   public DefaultHttpRequest(HttpVersion var1, HttpMethod var2, String var3, HttpHeaders var4) {
      super(var1, var4);
      this.method = ObjectUtil.checkNotNull(var2, "method");
      this.uri = ObjectUtil.checkNotNull(var3, "uri");
   }

   @Deprecated
   @Override
   public HttpMethod getMethod() {
      return this.method();
   }

   @Override
   public HttpMethod method() {
      return this.method;
   }

   @Deprecated
   @Override
   public String getUri() {
      return this.uri();
   }

   @Override
   public String uri() {
      return this.uri;
   }

   @Override
   public HttpRequest setMethod(HttpMethod var1) {
      this.method = ObjectUtil.checkNotNull(var1, "method");
      return this;
   }

   @Override
   public HttpRequest setUri(String var1) {
      this.uri = ObjectUtil.checkNotNull(var1, "uri");
      return this;
   }

   @Override
   public HttpRequest setProtocolVersion(HttpVersion var1) {
      super.setProtocolVersion(var1);
      return this;
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 = 31 * var1 + this.method.hashCode();
      var1 = 31 * var1 + this.uri.hashCode();
      return 31 * var1 + super.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultHttpRequest)) {
         return false;
      } else {
         DefaultHttpRequest var2 = (DefaultHttpRequest)var1;
         return this.method().equals(var2.method()) && this.uri().equalsIgnoreCase(var2.uri()) && super.equals(var1);
      }
   }

   @Override
   public String toString() {
      return HttpMessageUtil.appendRequest(new StringBuilder(256), this).toString();
   }
}
