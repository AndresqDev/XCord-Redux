package org.apache.logging.log4j.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalBufferedInputStream;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerBufferedInputStream extends BufferedInputStream {
   private static final String FQCN = LoggerBufferedInputStream.class.getName();
   private InternalBufferedInputStream stream;

   protected LoggerBufferedInputStream(InputStream var1, Charset var2, ExtendedLogger var3, String var4, Level var5, Marker var6) {
      super(var1);
      this.stream = new InternalBufferedInputStream(var1, var2, var3, var4 == null ? FQCN : var4, var5, var6);
   }

   protected LoggerBufferedInputStream(InputStream var1, Charset var2, int var3, ExtendedLogger var4, String var5, Level var6, Marker var7) {
      super(var1);
      this.stream = new InternalBufferedInputStream(var1, var2, var3, var4, var5 == null ? FQCN : var5, var6, var7);
   }

   @Override
   public void close() throws IOException {
      this.stream.close();
   }

   @Override
   public synchronized int read() throws IOException {
      return this.stream.read();
   }

   @Override
   public int read(byte[] var1) throws IOException {
      return this.stream.read(var1);
   }

   @Override
   public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
      return this.stream.read(var1, var2, var3);
   }

   @Override
   public String toString() {
      return LoggerBufferedInputStream.class.getSimpleName() + this.stream.toString();
   }
}
