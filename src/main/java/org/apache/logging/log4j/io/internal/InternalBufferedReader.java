package org.apache.logging.log4j.io.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalBufferedReader extends BufferedReader {
   private static final String FQCN = InternalBufferedReader.class.getName();

   public InternalBufferedReader(Reader var1, ExtendedLogger var2, String var3, Level var4, Marker var5) {
      super(new InternalLoggerReader(var1, var2, var3 == null ? FQCN : var3, var4, var5));
   }

   public InternalBufferedReader(Reader var1, int var2, ExtendedLogger var3, String var4, Level var5, Marker var6) {
      super(new InternalLoggerReader(var1, var3, var4 == null ? FQCN : var4, var5, var6), var2);
   }

   @Override
   public void close() throws IOException {
      super.close();
   }

   @Override
   public int read() throws IOException {
      return super.read();
   }

   @Override
   public int read(char[] var1) throws IOException {
      return super.read(var1, 0, var1.length);
   }

   @Override
   public int read(char[] var1, int var2, int var3) throws IOException {
      return super.read(var1, var2, var3);
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
   public String readLine() throws IOException {
      return super.readLine();
   }
}
