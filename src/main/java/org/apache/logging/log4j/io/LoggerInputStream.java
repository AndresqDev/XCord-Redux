package org.apache.logging.log4j.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalInputStream;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerInputStream extends FilterInputStream {
   private static final String FQCN = LoggerInputStream.class.getName();
   private final InternalInputStream logger;

   protected LoggerInputStream(InputStream var1, Charset var2, ExtendedLogger var3, String var4, Level var5, Marker var6) {
      super(var1);
      this.logger = new InternalInputStream(var1, var2, var3, var4 == null ? FQCN : var4, var5, var6);
   }

   @Override
   public void close() throws IOException {
      this.logger.close();
   }

   @Override
   public int read() throws IOException {
      return this.logger.read();
   }

   @Override
   public int read(byte[] var1) throws IOException {
      return this.logger.read(var1);
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      return this.logger.read(var1, var2, var3);
   }

   @Override
   public String toString() {
      return LoggerInputStream.class.getSimpleName() + this.logger.toString();
   }
}
