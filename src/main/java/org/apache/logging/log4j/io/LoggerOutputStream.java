package org.apache.logging.log4j.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalOutputStream;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerOutputStream extends OutputStream {
   private static final String FQCN = LoggerOutputStream.class.getName();
   private final InternalOutputStream logger;

   protected LoggerOutputStream(ExtendedLogger var1, Level var2, Marker var3, Charset var4, String var5) {
      this.logger = new InternalOutputStream(var1, var2, var3, var4, var5 == null ? FQCN : var5);
   }

   @Override
   public void close() throws IOException {
      this.logger.close();
   }

   @Override
   public void flush() throws IOException {
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.logger.write(var1);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.logger.write(var1, var2, var3);
   }

   @Override
   public void write(int var1) throws IOException {
      this.logger.write(var1);
   }
}
