package org.apache.logging.log4j.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalReader;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerReader extends FilterReader {
   private static final String FQCN = LoggerReader.class.getName();
   private final InternalReader reader;

   protected LoggerReader(Reader var1, ExtendedLogger var2, String var3, Level var4, Marker var5) {
      super(var1);
      this.reader = new InternalReader(var1, var2, var3 == null ? FQCN : var3, var4, var5);
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
   public String toString() {
      return LoggerReader.class.getSimpleName() + this.reader.toString();
   }
}
