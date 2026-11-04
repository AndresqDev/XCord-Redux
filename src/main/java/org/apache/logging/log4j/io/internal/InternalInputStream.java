package org.apache.logging.log4j.io.internal;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.ByteStreamLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalInputStream extends FilterInputStream {
   private final String fqcn;
   private final ByteStreamLogger logger;

   public InternalInputStream(InputStream var1, Charset var2, ExtendedLogger var3, String var4, Level var5, Marker var6) {
      super(var1);
      this.logger = new ByteStreamLogger(var3, var5, var6, var2);
      this.fqcn = var4;
   }

   @Override
   public void close() throws IOException {
      this.logger.close(this.fqcn);
      super.close();
   }

   @Override
   public int read() throws IOException {
      int var1 = super.read();
      this.logger.put(this.fqcn, var1);
      return var1;
   }

   @Override
   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = super.read(var1, var2, var3);
      this.logger.put(this.fqcn, var1, var2, var4);
      return var4;
   }

   @Override
   public String toString() {
      return "{stream=" + this.in + '}';
   }
}
