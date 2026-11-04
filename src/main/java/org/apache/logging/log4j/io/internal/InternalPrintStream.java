package org.apache.logging.log4j.io.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalPrintStream extends PrintStream {
   public InternalPrintStream(ExtendedLogger var1, boolean var2, Charset var3, String var4, Level var5, Marker var6) throws UnsupportedEncodingException {
      super(new InternalOutputStream(var1, var5, var6, ensureNonNull(var3), var4), var2, ensureNonNull(var3).name());
   }

   public InternalPrintStream(OutputStream var1, boolean var2, Charset var3, ExtendedLogger var4, String var5, Level var6, Marker var7) throws UnsupportedEncodingException {
      super(new InternalFilterOutputStream(var1, ensureNonNull(var3), var4, var5, var6, var7), var2, ensureNonNull(var3).name());
   }

   private static Charset ensureNonNull(Charset var0) {
      return var0 == null ? Charset.defaultCharset() : var0;
   }

   public InternalPrintStream append(char var1) {
      super.append(var1);
      return this;
   }

   public InternalPrintStream append(CharSequence var1) {
      super.append(var1);
      return this;
   }

   public InternalPrintStream append(CharSequence var1, int var2, int var3) {
      super.append(var1, var2, var3);
      return this;
   }

   @Override
   public boolean checkError() {
      return super.checkError();
   }

   @Override
   public void close() {
      super.close();
   }

   @Override
   public void flush() {
      super.flush();
   }

   public InternalPrintStream format(Locale var1, String var2, Object... var3) {
      super.format(var1, var2, var3);
      return this;
   }

   public InternalPrintStream format(String var1, Object... var2) {
      super.format(var1, var2);
      return this;
   }

   @Override
   public void print(boolean var1) {
      super.print(var1);
   }

   @Override
   public void print(char var1) {
      super.print(var1);
   }

   @Override
   public void print(char[] var1) {
      super.print(var1);
   }

   @Override
   public void print(double var1) {
      super.print(var1);
   }

   @Override
   public void print(float var1) {
      super.print(var1);
   }

   @Override
   public void print(int var1) {
      super.print(var1);
   }

   @Override
   public void print(long var1) {
      super.print(var1);
   }

   @Override
   public void print(Object var1) {
      super.print(var1);
   }

   @Override
   public void print(String var1) {
      super.print(var1);
   }

   public InternalPrintStream printf(Locale var1, String var2, Object... var3) {
      super.printf(var1, var2, var3);
      return this;
   }

   public InternalPrintStream printf(String var1, Object... var2) {
      super.printf(var1, var2);
      return this;
   }

   @Override
   public void println() {
      super.println();
   }

   @Override
   public void println(boolean var1) {
      super.println(var1);
   }

   @Override
   public void println(char var1) {
      super.println(var1);
   }

   @Override
   public void println(char[] var1) {
      super.println(var1);
   }

   @Override
   public void println(double var1) {
      super.println(var1);
   }

   @Override
   public void println(float var1) {
      super.println(var1);
   }

   @Override
   public void println(int var1) {
      super.println(var1);
   }

   @Override
   public void println(long var1) {
      super.println(var1);
   }

   @Override
   public void println(Object var1) {
      super.println(var1);
   }

   @Override
   public void println(String var1) {
      super.println(var1);
   }

   @Override
   public String toString() {
      return "{stream=" + this.out + '}';
   }

   @Override
   public void write(byte[] var1) throws IOException {
      super.write(var1);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) {
      super.write(var1, var2, var3);
   }

   @Override
   public void write(int var1) {
      super.write(var1);
   }
}
