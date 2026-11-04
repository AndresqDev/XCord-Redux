package org.apache.logging.log4j.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalFilterOutputStream;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerFilterOutputStream extends FilterOutputStream {
   private static final String FQCN = LoggerFilterOutputStream.class.getName();
   private final InternalFilterOutputStream logger;

   protected LoggerFilterOutputStream(OutputStream var1, Charset var2, ExtendedLogger var3, String var4, Level var5, Marker var6) {
      super(var1);
      this.logger = new InternalFilterOutputStream(var1, var2, var3, var4 == null ? FQCN : var4, var5, var6);
   }

   @Override
   public void close() throws IOException {
      this.logger.close();
   }

   @Override
   public void flush() throws IOException {
      this.logger.flush();
   }

   @Override
   public String toString() {
      return LoggerFilterOutputStream.class.getSimpleName() + this.logger.toString();
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
