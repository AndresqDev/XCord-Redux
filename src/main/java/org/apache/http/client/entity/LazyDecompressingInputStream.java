package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;

class LazyDecompressingInputStream extends InputStream {
   private final InputStream wrappedStream;
   private final InputStreamFactory inputStreamFactory;
   private InputStream wrapperStream;

   public LazyDecompressingInputStream(InputStream var1, InputStreamFactory var2) {
      this.wrappedStream = var1;
      this.inputStreamFactory = var2;
   }

   private void initWrapper() throws IOException {
      if (this.wrapperStream == null) {
         this.wrapperStream = this.inputStreamFactory.create(this.wrappedStream);
      }
   }

   @Override
   public int read() throws IOException {
      this.initWrapper();
      return this.wrapperStream.read();
   }

   @Override
   public int read(byte[] var1) throws IOException {
      this.initWrapper();
      return this.wrapperStream.read(var1);
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      this.initWrapper();
      return this.wrapperStream.read(var1, var2, var3);
   }

   @Override
   public long skip(long var1) throws IOException {
      this.initWrapper();
      return this.wrapperStream.skip(var1);
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   @Override
   public int available() throws IOException {
      this.initWrapper();
      return this.wrapperStream.available();
   }

   @Override
   public void close() throws IOException {
      try {
         if (this.wrapperStream != null) {
            this.wrapperStream.close();
         }
      } finally {
         this.wrappedStream.close();
      }
   }
}
