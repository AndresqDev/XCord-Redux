package org.apache.logging.log4j.io;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalFilterWriter;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerFilterWriter extends FilterWriter {
   private static final String FQCN = LoggerFilterWriter.class.getName();
   private final InternalFilterWriter logger;

   protected LoggerFilterWriter(Writer var1, ExtendedLogger var2, String var3, Level var4, Marker var5) {
      super(var1);
      this.logger = new InternalFilterWriter(var1, var2, var3 == null ? FQCN : var3, var4, var5);
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
      return LoggerFilterWriter.class.getSimpleName() + this.logger.toString();
   }

   @Override
   public void write(char[] var1) throws IOException {
      this.logger.write(var1);
   }

   @Override
   public void write(char[] var1, int var2, int var3) throws IOException {
      this.logger.write(var1, var2, var3);
   }

   @Override
   public void write(int var1) throws IOException {
      this.logger.write(var1);
   }

   @Override
   public void write(String var1) throws IOException {
      this.logger.write(var1);
   }

   @Override
   public void write(String var1, int var2, int var3) throws IOException {
      this.logger.write(var1, var2, var3);
   }
}
