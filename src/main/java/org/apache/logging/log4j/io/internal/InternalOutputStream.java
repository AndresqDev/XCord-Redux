package org.apache.logging.log4j.io.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.ByteStreamLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalOutputStream extends OutputStream {
   private final ByteStreamLogger logger;
   private final String fqcn;

   public InternalOutputStream(ExtendedLogger var1, Level var2, Marker var3, Charset var4, String var5) {
      this.logger = new ByteStreamLogger(var1, var2, var3, var4);
      this.fqcn = var5;
   }

   @Override
   public void close() throws IOException {
      this.logger.close(this.fqcn);
   }

   @Override
   public void flush() throws IOException {
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.logger.put(this.fqcn, var1, 0, var1.length);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.logger.put(this.fqcn, var1, var2, var3);
   }

   @Override
   public void write(int var1) throws IOException {
      this.logger.put(this.fqcn, (byte)(var1 & 0xFF));
   }
}
