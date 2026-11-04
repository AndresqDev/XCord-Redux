package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;

public abstract class DefaultHttpMessage extends DefaultHttpObject implements HttpMessage {
   private static final int HASH_CODE_PRIME = 31;
   private HttpVersion version;
   private final HttpHeaders headers;

   protected DefaultHttpMessage(HttpVersion var1) {
      this(var1, true, false);
   }

   protected DefaultHttpMessage(HttpVersion var1, boolean var2, boolean var3) {
      this(var1, (HttpHeaders)(var3 ? new CombinedHttpHeaders(var2) : new DefaultHttpHeaders(var2)));
   }

   protected DefaultHttpMessage(HttpVersion var1, HttpHeaders var2) {
      this.version = ObjectUtil.checkNotNull(var1, "version");
      this.headers = ObjectUtil.checkNotNull(var2, "headers");
   }

   @Override
   public HttpHeaders headers() {
      return this.headers;
   }

   @Deprecated
   @Override
   public HttpVersion getProtocolVersion() {
      return this.protocolVersion();
   }

   @Override
   public HttpVersion protocolVersion() {
      return this.version;
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 = 31 * var1 + this.headers.hashCode();
      var1 = 31 * var1 + this.version.hashCode();
      return 31 * var1 + super.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultHttpMessage)) {
         return false;
      } else {
         DefaultHttpMessage var2 = (DefaultHttpMessage)var1;
         return this.headers().equals(var2.headers()) && this.protocolVersion().equals(var2.protocolVersion()) && super.equals(var1);
      }
   }

   @Override
   public HttpMessage setProtocolVersion(HttpVersion var1) {
      this.version = ObjectUtil.checkNotNull(var1, "version");
      return this;
   }
}
