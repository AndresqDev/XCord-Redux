package org.apache.logging.log4j.io.internal;

import java.io.IOException;
import java.io.Writer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.CharStreamLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalWriter extends Writer {
   private final CharStreamLogger logger;
   private final String fqcn;

   public InternalWriter(ExtendedLogger var1, String var2, Level var3, Marker var4) {
      this.logger = new CharStreamLogger(var1, var3, var4);
      this.fqcn = var2;
   }

   @Override
   public void close() throws IOException {
      this.logger.close(this.fqcn);
   }

   @Override
   public void flush() throws IOException {
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + "[fqcn=" + this.fqcn + ", logger=" + this.logger + "]";
   }

   @Override
   public void write(char[] var1) throws IOException {
      this.logger.put(this.fqcn, var1, 0, var1.length);
   }

   @Override
   public void write(char[] var1, int var2, int var3) throws IOException {
      this.logger.put(this.fqcn, var1, var2, var3);
   }

   @Override
   public void write(int var1) throws IOException {
      this.logger.put(this.fqcn, (char)var1);
   }

   @Override
   public void write(String var1) throws IOException {
      this.logger.put(this.fqcn, var1, 0, var1.length());
   }

   @Override
   public void write(String var1, int var2, int var3) throws IOException {
      this.logger.put(this.fqcn, var1, var2, var3);
   }
}
