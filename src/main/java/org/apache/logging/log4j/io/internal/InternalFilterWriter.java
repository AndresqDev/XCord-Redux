package org.apache.logging.log4j.io.internal;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.CharStreamLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalFilterWriter extends FilterWriter {
   private final CharStreamLogger logger;
   private final String fqcn;

   public InternalFilterWriter(Writer var1, ExtendedLogger var2, String var3, Level var4, Marker var5) {
      super(var1);
      this.logger = new CharStreamLogger(var2, var4, var5);
      this.fqcn = var3;
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
      return "{writer=" + this.out + '}';
   }

   @Override
   public void write(char[] var1) throws IOException {
      this.out.write(var1);
      this.logger.put(this.fqcn, var1, 0, var1.length);
   }

   @Override
   public void write(char[] var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
      this.logger.put(this.fqcn, var1, var2, var3);
   }

   @Override
   public void write(int var1) throws IOException {
      this.out.write(var1);
      this.logger.put(this.fqcn, (char)var1);
   }

   @Override
   public void write(String var1) throws IOException {
      this.out.write(var1);
      this.logger.put(this.fqcn, var1, 0, var1.length());
   }

   @Override
   public void write(String var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
      this.logger.put(this.fqcn, var1, var2, var3);
   }
}
