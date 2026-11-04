package org.apache.logging.log4j.io.internal;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.ByteStreamLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalFilterOutputStream extends FilterOutputStream {
   private final ByteStreamLogger logger;
   private final String fqcn;

   public InternalFilterOutputStream(OutputStream var1, Charset var2, ExtendedLogger var3, String var4, Level var5, Marker var6) {
      super(var1);
      this.logger = new ByteStreamLogger(var3, var5, var6, var2);
      this.fqcn = var4;
   }

   @Override
   public void close() throws IOException {
      this.out.close();
      this.logger.close(this.fqcn);
   }

   @Override
   public void flush() throws IOException {
      this.out.flush();
   }

   @Override
   public String toString() {
      return "{stream=" + this.out + '}';
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.out.write(var1);
      this.logger.put(this.fqcn, var1, 0, var1.length);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
      this.logger.put(this.fqcn, var1, var2, var3);
   }

   @Override
   public void write(int var1) throws IOException {
      this.out.write(var1);
      this.logger.put(this.fqcn, (byte)(var1 & 0xFF));
   }
}
