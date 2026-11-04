package org.apache.logging.log4j.io;

import java.io.IOException;
import java.io.Writer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalWriter;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerWriter extends Writer {
   private static final String FQCN = LoggerWriter.class.getName();
   private final InternalWriter writer;

   protected LoggerWriter(ExtendedLogger var1, String var2, Level var3, Marker var4) {
      this.writer = new InternalWriter(var1, var2 == null ? FQCN : var2, var3, var4);
   }

   @Override
   public void close() throws IOException {
      this.writer.close();
   }

   @Override
   public void flush() throws IOException {
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + "[fqcn=" + this.writer.toString();
   }

   @Override
   public void write(char[] var1) throws IOException {
      this.writer.write(var1);
   }

   @Override
   public void write(char[] var1, int var2, int var3) throws IOException {
      this.writer.write(var1, var2, var3);
   }

   @Override
   public void write(int var1) throws IOException {
      this.writer.write(var1);
   }

   @Override
   public void write(String var1) throws IOException {
      this.writer.write(var1);
   }

   @Override
   public void write(String var1, int var2, int var3) throws IOException {
      this.writer.write(var1, var2, var3);
   }
}
