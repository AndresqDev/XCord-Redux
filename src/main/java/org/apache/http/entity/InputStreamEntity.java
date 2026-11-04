package org.apache.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.util.Args;

public class InputStreamEntity extends AbstractHttpEntity {
   private final InputStream content;
   private final long length;

   public InputStreamEntity(InputStream var1) {
      this(var1, -1L);
   }

   public InputStreamEntity(InputStream var1, long var2) {
      this(var1, var2, null);
   }

   public InputStreamEntity(InputStream var1, ContentType var2) {
      this(var1, -1L, var2);
   }

   public InputStreamEntity(InputStream var1, long var2, ContentType var4) {
      this.content = Args.notNull(var1, "Source input stream");
      this.length = var2;
      if (var4 != null) {
         this.setContentType(var4.toString());
      }
   }

   @Override
   public boolean isRepeatable() {
      return false;
   }

   @Override
   public long getContentLength() {
      return this.length;
   }

   @Override
   public InputStream getContent() throws IOException {
      return this.content;
   }

   @Override
   public void writeTo(OutputStream var1) throws IOException {
      Args.notNull(var1, "Output stream");
      InputStream var2 = this.content;

      try {
         byte[] var3 = new byte[4096];
         int var10;
         if (this.length < 0L) {
            while ((var10 = var2.read(var3)) != -1) {
               var1.write(var3, 0, var10);
            }
         } else {
            long var5 = this.length;

            while (var5 > 0L) {
               var10 = var2.read(var3, 0, (int)Math.min(4096L, var5));
               if (var10 == -1) {
                  break;
               }

               var1.write(var3, 0, var10);
               var5 -= (long)var10;
            }
         }
      } finally {
         var2.close();
      }
   }

   @Override
   public boolean isStreaming() {
      return true;
   }
}
