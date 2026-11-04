package org.apache.http.entity;

import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

public abstract class AbstractHttpEntity implements HttpEntity {
   protected static final int OUTPUT_BUFFER_SIZE = 4096;
   protected Header contentType;
   protected Header contentEncoding;
   protected boolean chunked;

   protected AbstractHttpEntity() {
   }

   @Override
   public Header getContentType() {
      return this.contentType;
   }

   @Override
   public Header getContentEncoding() {
      return this.contentEncoding;
   }

   @Override
   public boolean isChunked() {
      return this.chunked;
   }

   public void setContentType(Header var1) {
      this.contentType = var1;
   }

   public void setContentType(String var1) {
      BasicHeader var2 = null;
      if (var1 != null) {
         var2 = new BasicHeader("Content-Type", var1);
      }

      this.setContentType(var2);
   }

   public void setContentEncoding(Header var1) {
      this.contentEncoding = var1;
   }

   public void setContentEncoding(String var1) {
      BasicHeader var2 = null;
      if (var1 != null) {
         var2 = new BasicHeader("Content-Encoding", var1);
      }

      this.setContentEncoding(var2);
   }

   public void setChunked(boolean var1) {
      this.chunked = var1;
   }

   @Deprecated
   @Override
   public void consumeContent() throws IOException {
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append('[');
      if (this.contentType != null) {
         var1.append("Content-Type: ");
         var1.append(this.contentType.getValue());
         var1.append(',');
      }

      if (this.contentEncoding != null) {
         var1.append("Content-Encoding: ");
         var1.append(this.contentEncoding.getValue());
         var1.append(',');
      }

      long var2 = this.getContentLength();
      if (var2 >= 0L) {
         var1.append("Content-Length: ");
         var1.append(var2);
         var1.append(',');
      }

      var1.append("Chunked: ");
      var1.append(this.chunked);
      var1.append(']');
      return var1.toString();
   }
}
