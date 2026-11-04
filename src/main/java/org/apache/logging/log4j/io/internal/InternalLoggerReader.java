package org.apache.logging.log4j.io.internal;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.CharStreamLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalLoggerReader extends FilterReader {
   private final CharStreamLogger logger;
   private final String fqcn;

   public InternalLoggerReader(Reader var1, ExtendedLogger var2, String var3, Level var4, Marker var5) {
      super(var1);
      this.logger = new CharStreamLogger(var2, var4, var5);
      this.fqcn = var3;
   }

   @Override
   public void close() throws IOException {
      super.close();
      this.logger.close(this.fqcn);
   }

   @Override
   public int read() throws IOException {
      int var1 = super.read();
      this.logger.put(this.fqcn, var1);
      return var1;
   }

   @Override
   public int read(char[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   @Override
   public int read(char[] var1, int var2, int var3) throws IOException {
      int var4 = super.read(var1, var2, var3);
      this.logger.put(this.fqcn, var1, var2, var4);
      return var4;
   }

   @Override
   public int read(CharBuffer var1) throws IOException {
      int var2 = var1.remaining();
      char[] var3 = new char[var2];
      int var4 = this.read(var3, 0, var2);
      if (var4 > 0) {
         var1.put(var3, 0, var4);
      }

      return var4;
   }

   @Override
   public String toString() {
      return "{stream=" + this.in + '}';
   }
}
