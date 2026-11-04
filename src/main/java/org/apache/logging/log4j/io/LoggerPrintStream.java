package org.apache.logging.log4j.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalPrintStream;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerPrintStream extends PrintStream {
   private static final String FQCN = LoggerPrintStream.class.getName();
   private final InternalPrintStream psLogger;

   protected LoggerPrintStream(ExtendedLogger var1, boolean var2, Charset var3, String var4, Level var5, Marker var6) throws UnsupportedEncodingException {
      super(new PrintStream(new ByteArrayOutputStream()));
      this.psLogger = new InternalPrintStream(var1, var2, var3, var4 == null ? FQCN : var4, var5, var6);
   }

   protected LoggerPrintStream(OutputStream var1, boolean var2, Charset var3, ExtendedLogger var4, String var5, Level var6, Marker var7) throws UnsupportedEncodingException {
      super(new PrintStream(var1));
      this.psLogger = new InternalPrintStream(var1, var2, var3, var4, var5 == null ? FQCN : var5, var6, var7);
   }

   private static Charset ensureNonNull(Charset var0) {
      return var0 == null ? Charset.defaultCharset() : var0;
   }

   public LoggerPrintStream append(char var1) {
      this.psLogger.append(var1);
      return this;
   }

   public LoggerPrintStream append(CharSequence var1) {
      this.psLogger.append(var1);
      return this;
   }

   public LoggerPrintStream append(CharSequence var1, int var2, int var3) {
      this.psLogger.append(var1, var2, var3);
      return this;
   }

   @Override
   public boolean checkError() {
      return this.psLogger.checkError();
   }

   @Override
   public void close() {
      this.psLogger.close();
   }

   @Override
   public void flush() {
      this.psLogger.flush();
   }

   public LoggerPrintStream format(Locale var1, String var2, Object... var3) {
      this.psLogger.format(var1, var2, var3);
      return this;
   }

   public LoggerPrintStream format(String var1, Object... var2) {
      this.psLogger.format(var1, var2);
      return this;
   }

   @Override
   public void print(boolean var1) {
      this.psLogger.print(var1);
   }

   @Override
   public void print(char var1) {
      this.psLogger.print(var1);
   }

   @Override
   public void print(char[] var1) {
      this.psLogger.print(var1);
   }

   @Override
   public void print(double var1) {
      this.psLogger.print(var1);
   }

   @Override
   public void print(float var1) {
      this.psLogger.print(var1);
   }

   @Override
   public void print(int var1) {
      this.psLogger.print(var1);
   }

   @Override
   public void print(long var1) {
      this.psLogger.print(var1);
   }

   @Override
   public void print(Object var1) {
      this.psLogger.print(var1);
   }

   @Override
   public void print(String var1) {
      this.psLogger.print(var1);
   }

   public LoggerPrintStream printf(Locale var1, String var2, Object... var3) {
      this.psLogger.printf(var1, var2, var3);
      return this;
   }

   public LoggerPrintStream printf(String var1, Object... var2) {
      this.psLogger.printf(var1, var2);
      return this;
   }

   @Override
   public void println() {
      this.psLogger.println();
   }

   @Override
   public void println(boolean var1) {
      this.psLogger.println(var1);
   }

   @Override
   public void println(char var1) {
      this.psLogger.println(var1);
   }

   @Override
   public void println(char[] var1) {
      this.psLogger.println(var1);
   }

   @Override
   public void println(double var1) {
      this.psLogger.println(var1);
   }

   @Override
   public void println(float var1) {
      this.psLogger.println(var1);
   }

   @Override
   public void println(int var1) {
      this.psLogger.println(var1);
   }

   @Override
   public void println(long var1) {
      this.psLogger.println(var1);
   }

   @Override
   public void println(Object var1) {
      this.psLogger.println(var1);
   }

   @Override
   public void println(String var1) {
      this.psLogger.println(var1);
   }

   @Override
   public String toString() {
      return LoggerPrintStream.class.getSimpleName() + this.psLogger.toString();
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.psLogger.write(var1);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) {
      this.psLogger.write(var1, var2, var3);
   }

   @Override
   public void write(int var1) {
      this.psLogger.write(var1);
   }
}
