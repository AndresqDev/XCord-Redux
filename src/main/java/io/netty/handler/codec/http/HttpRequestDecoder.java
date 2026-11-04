package io.netty.handler.codec.http;

public class HttpRequestDecoder extends HttpObjectDecoder {
   public HttpRequestDecoder() {
   }

   public HttpRequestDecoder(int var1, int var2, int var3) {
      super(var1, var2, var3, true);
   }

   public HttpRequestDecoder(int var1, int var2, int var3, boolean var4) {
      super(var1, var2, var3, true, var4);
   }

   public HttpRequestDecoder(int var1, int var2, int var3, boolean var4, int var5) {
      super(var1, var2, var3, true, var4, var5);
   }

   public HttpRequestDecoder(int var1, int var2, int var3, boolean var4, int var5, boolean var6) {
      super(var1, var2, var3, true, var4, var5, var6);
   }

   public HttpRequestDecoder(int var1, int var2, int var3, boolean var4, int var5, boolean var6, boolean var7) {
      super(var1, var2, var3, true, var4, var5, var6, var7);
   }

   @Override
   protected HttpMessage createMessage(String[] var1) throws Exception {
      return new DefaultHttpRequest(HttpVersion.valueOf(var1[2]), HttpMethod.valueOf(var1[0]), var1[1], this.validateHeaders);
   }

   @Override
   protected HttpMessage createInvalidMessage() {
      return new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, "/bad-request", this.validateHeaders);
   }

   @Override
   protected boolean isDecodingRequest() {
      return true;
   }
}
