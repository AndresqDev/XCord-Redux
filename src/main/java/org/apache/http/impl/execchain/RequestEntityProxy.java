package org.apache.http.impl.execchain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;

class RequestEntityProxy implements HttpEntity {
   private final HttpEntity original;
   private boolean consumed = false;

   static void enhance(HttpEntityEnclosingRequest var0) {
      HttpEntity var1 = var0.getEntity();
      if (var1 != null && !var1.isRepeatable() && !isEnhanced(var1)) {
         var0.setEntity(new RequestEntityProxy(var1));
      }
   }

   static boolean isEnhanced(HttpEntity var0) {
      return var0 instanceof RequestEntityProxy;
   }

   static boolean isRepeatable(HttpRequest var0) {
      if (var0 instanceof HttpEntityEnclosingRequest) {
         HttpEntity var1 = ((HttpEntityEnclosingRequest)var0).getEntity();
         if (var1 != null) {
            if (isEnhanced(var1)) {
               RequestEntityProxy var2 = (RequestEntityProxy)var1;
               if (!var2.isConsumed()) {
                  return true;
               }
            }

            return var1.isRepeatable();
         }
      }

      return true;
   }

   RequestEntityProxy(HttpEntity var1) {
      this.original = var1;
   }

   public HttpEntity getOriginal() {
      return this.original;
   }

   public boolean isConsumed() {
      return this.consumed;
   }

   @Override
   public boolean isRepeatable() {
      return this.original.isRepeatable();
   }

   @Override
   public boolean isChunked() {
      return this.original.isChunked();
   }

   @Override
   public long getContentLength() {
      return this.original.getContentLength();
   }

   @Override
   public Header getContentType() {
      return this.original.getContentType();
   }

   @Override
   public Header getContentEncoding() {
      return this.original.getContentEncoding();
   }

   @Override
   public InputStream getContent() throws IOException, IllegalStateException {
      return this.original.getContent();
   }

   @Override
   public void writeTo(OutputStream var1) throws IOException {
      this.consumed = true;
      this.original.writeTo(var1);
   }

   @Override
   public boolean isStreaming() {
      return this.original.isStreaming();
   }

   @Override
   public void consumeContent() throws IOException {
      this.consumed = true;
      this.original.consumeContent();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("RequestEntityProxy{");
      var1.append(this.original);
      var1.append('}');
      return var1.toString();
   }
}
