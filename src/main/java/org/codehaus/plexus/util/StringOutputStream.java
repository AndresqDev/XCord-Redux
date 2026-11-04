package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream {
   private StringBuffer buf = new StringBuffer();

   @Override
   public void write(byte[] var1) throws IOException {
      this.buf.append(new String(var1));
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.buf.append(new String(var1, var2, var3));
   }

   @Override
   public void write(int var1) throws IOException {
      byte[] var2 = new byte[]{(byte)var1};
      this.buf.append(new String(var2));
   }

   @Override
   public String toString() {
      return this.buf.toString();
   }
}
