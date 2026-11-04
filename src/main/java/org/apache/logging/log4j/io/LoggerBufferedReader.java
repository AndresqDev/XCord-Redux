package org.apache.logging.log4j.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalBufferedReader;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerBufferedReader extends BufferedReader {
   private static final String FQCN = LoggerBufferedReader.class.getName();
   private final InternalBufferedReader reader;

   protected LoggerBufferedReader(Reader var1, ExtendedLogger var2, String var3, Level var4, Marker var5) {
      super(var1);
      this.reader = new InternalBufferedReader(var1, var2, var3 == null ? FQCN : var3, var4, var5);
   }

   protected LoggerBufferedReader(Reader var1, int var2, ExtendedLogger var3, String var4, Level var5, Marker var6) {
      super(var1);
      this.reader = new InternalBufferedReader(var1, var2, var3, var4 == null ? FQCN : var4, var5, var6);
   }

   @Override
   public void close() throws IOException {
      this.reader.close();
   }

   @Override
   public int read() throws IOException {
      return this.reader.read();
   }

   @Override
   public int read(char[] var1) throws IOException {
      return this.reader.read(var1);
   }

   @Override
   public int read(char[] var1, int var2, int var3) throws IOException {
      return this.reader.read(var1, var2, var3);
   }

   @Override
   public int read(CharBuffer var1) throws IOException {
      return this.reader.read(var1);
   }

   @Override
   public String readLine() throws IOException {
      return this.reader.readLine();
   }
}
