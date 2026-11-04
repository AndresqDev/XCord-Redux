package org.apache.http.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.util.Args;

public class FileEntity extends AbstractHttpEntity implements Cloneable {
   protected final File file;

   @Deprecated
   public FileEntity(File var1, String var2) {
      this.file = Args.notNull(var1, "File");
      this.setContentType(var2);
   }

   public FileEntity(File var1, ContentType var2) {
      this.file = Args.notNull(var1, "File");
      if (var2 != null) {
         this.setContentType(var2.toString());
      }
   }

   public FileEntity(File var1) {
      this.file = Args.notNull(var1, "File");
   }

   @Override
   public boolean isRepeatable() {
      return true;
   }

   @Override
   public long getContentLength() {
      return this.file.length();
   }

   @Override
   public InputStream getContent() throws IOException {
      return new FileInputStream(this.file);
   }

   @Override
   public void writeTo(OutputStream var1) throws IOException {
      Args.notNull(var1, "Output stream");
      FileInputStream var2 = new FileInputStream(this.file);

      try {
         byte[] var3 = new byte[4096];

         int var4;
         while ((var4 = var2.read(var3)) != -1) {
            var1.write(var3, 0, var4);
         }

         var1.flush();
      } finally {
         var2.close();
      }
   }

   @Override
   public boolean isStreaming() {
      return false;
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
