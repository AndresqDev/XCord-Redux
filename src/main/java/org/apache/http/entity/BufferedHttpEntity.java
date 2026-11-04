package org.apache.http.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.util.Args;

public class BufferedHttpEntity extends HttpEntityWrapper {
   private final byte[] buffer;

   public BufferedHttpEntity(HttpEntity var1) throws IOException {
      super(var1);
      if (var1.isRepeatable() && var1.getContentLength() >= 0L) {
         this.buffer = null;
      } else {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream();
         var1.writeTo(var2);
         var2.flush();
         this.buffer = var2.toByteArray();
      }
   }

   @Override
   public long getContentLength() {
      return this.buffer != null ? (long)this.buffer.length : super.getContentLength();
   }

   @Override
   public InputStream getContent() throws IOException {
      return (InputStream)(this.buffer != null ? new ByteArrayInputStream(this.buffer) : super.getContent());
   }

   @Override
   public boolean isChunked() {
      return this.buffer == null && super.isChunked();
   }

   @Override
   public boolean isRepeatable() {
      return true;
   }

   @Override
   public void writeTo(OutputStream var1) throws IOException {
      Args.notNull(var1, "Output stream");
      if (this.buffer != null) {
         var1.write(this.buffer);
      } else {
         super.writeTo(var1);
      }
   }

   @Override
   public boolean isStreaming() {
      return this.buffer == null && super.isStreaming();
   }
}
