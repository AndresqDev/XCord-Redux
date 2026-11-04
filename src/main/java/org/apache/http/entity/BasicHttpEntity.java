package org.apache.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.impl.io.EmptyInputStream;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

public class BasicHttpEntity extends AbstractHttpEntity {
   private InputStream content;
   private long length = -1L;

   @Override
   public long getContentLength() {
      return this.length;
   }

   @Override
   public InputStream getContent() throws IllegalStateException {
      Asserts.check(this.content != null, "Content has not been provided");
      return this.content;
   }

   @Override
   public boolean isRepeatable() {
      return false;
   }

   public void setContentLength(long var1) {
      this.length = var1;
   }

   public void setContent(InputStream var1) {
      this.content = var1;
   }

   @Override
   public void writeTo(OutputStream var1) throws IOException {
      Args.notNull(var1, "Output stream");
      InputStream var2 = this.getContent();

      try {
         byte[] var4 = new byte[4096];

         int var3;
         while ((var3 = var2.read(var4)) != -1) {
            var1.write(var4, 0, var3);
         }
      } finally {
         var2.close();
      }
   }

   @Override
   public boolean isStreaming() {
      return this.content != null && this.content != EmptyInputStream.INSTANCE;
   }
}
