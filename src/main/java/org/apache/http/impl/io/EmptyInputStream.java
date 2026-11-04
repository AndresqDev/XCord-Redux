package org.apache.http.impl.io;

import java.io.InputStream;

public final class EmptyInputStream extends InputStream {
   public static final EmptyInputStream INSTANCE = new EmptyInputStream();

   private EmptyInputStream() {
   }

   @Override
   public int available() {
      return 0;
   }

   @Override
   public void close() {
   }

   @Override
   public void mark(int var1) {
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   @Override
   public int read() {
      return -1;
   }

   @Override
   public int read(byte[] var1) {
      return -1;
   }

   @Override
   public int read(byte[] var1, int var2, int var3) {
      return -1;
   }

   @Override
   public void reset() {
   }

   @Override
   public long skip(long var1) {
      return 0L;
   }
}
