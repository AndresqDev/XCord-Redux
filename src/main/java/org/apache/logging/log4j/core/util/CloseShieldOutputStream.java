package org.apache.logging.log4j.core.util;

import java.io.IOException;
import java.io.OutputStream;

public class CloseShieldOutputStream extends OutputStream {
   private final OutputStream delegate;

   public CloseShieldOutputStream(OutputStream var1) {
      this.delegate = var1;
   }

   @Override
   public void close() {
   }

   @Override
   public void flush() throws IOException {
      this.delegate.flush();
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.delegate.write(var1);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.delegate.write(var1, var2, var3);
   }

   @Override
   public void write(int var1) throws IOException {
      this.delegate.write(var1);
   }
}
