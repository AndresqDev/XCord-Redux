package org.apache.logging.log4j.io;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalPrintWriter;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerPrintWriter extends PrintWriter {
   private static final String FQCN = LoggerPrintWriter.class.getName();
   private final InternalPrintWriter writer;

   protected LoggerPrintWriter(ExtendedLogger var1, boolean var2, String var3, Level var4, Marker var5) {
      super(new StringWriter());
      this.writer = new InternalPrintWriter(var1, var2, var3 == null ? FQCN : var3, var4, var5);
   }

   protected LoggerPrintWriter(Writer var1, boolean var2, ExtendedLogger var3, String var4, Level var5, Marker var6) {
      super(var1);
      this.writer = new InternalPrintWriter(var1, var2, var3, var4 == null ? FQCN : var4, var5, var6);
   }

   public LoggerPrintWriter append(char var1) {
      this.writer.append(var1);
      return this;
   }

   public LoggerPrintWriter append(CharSequence var1) {
      this.writer.append(var1);
      return this;
   }

   public LoggerPrintWriter append(CharSequence var1, int var2, int var3) {
      this.writer.append(var1, var2, var3);
      return this;
   }

   @Override
   public boolean checkError() {
      return this.writer.checkError();
   }

   @Override
   public void close() {
      this.writer.close();
   }

   @Override
   public void flush() {
      this.writer.flush();
   }

   public LoggerPrintWriter format(Locale var1, String var2, Object... var3) {
      this.writer.format(var1, var2, var3);
      return this;
   }

   public LoggerPrintWriter format(String var1, Object... var2) {
      this.writer.format(var1, var2);
      return this;
   }

   @Override
   public void print(boolean var1) {
      this.writer.print(var1);
   }

   @Override
   public void print(char var1) {
      this.writer.print(var1);
   }

   @Override
   public void print(char[] var1) {
      this.writer.print(var1);
   }

   @Override
   public void print(double var1) {
      this.writer.print(var1);
   }

   @Override
   public void print(float var1) {
      this.writer.print(var1);
   }

   @Override
   public void print(int var1) {
      this.writer.print(var1);
   }

   @Override
   public void print(long var1) {
      this.writer.print(var1);
   }

   @Override
   public void print(Object var1) {
      this.writer.print(var1);
   }

   @Override
   public void print(String var1) {
      this.writer.print(var1);
   }

   public LoggerPrintWriter printf(Locale var1, String var2, Object... var3) {
      this.writer.printf(var1, var2, var3);
      return this;
   }

   public LoggerPrintWriter printf(String var1, Object... var2) {
      this.writer.printf(var1, var2);
      return this;
   }

   @Override
   public void println() {
      this.writer.println();
   }

   @Override
   public void println(boolean var1) {
      this.writer.println(var1);
   }

   @Override
   public void println(char var1) {
      this.writer.println(var1);
   }

   @Override
   public void println(char[] var1) {
      this.writer.println(var1);
   }

   @Override
   public void println(double var1) {
      this.writer.println(var1);
   }

   @Override
   public void println(float var1) {
      this.writer.println(var1);
   }

   @Override
   public void println(int var1) {
      this.writer.println(var1);
   }

   @Override
   public void println(long var1) {
      this.writer.println(var1);
   }

   @Override
   public void println(Object var1) {
      this.writer.println(var1);
   }

   @Override
   public void println(String var1) {
      this.writer.println(var1);
   }

   @Override
   public String toString() {
      return LoggerPrintWriter.class.getSimpleName() + this.writer.toString();
   }

   @Override
   public void write(char[] var1) {
      this.writer.write(var1);
   }

   @Override
   public void write(char[] var1, int var2, int var3) {
      this.writer.write(var1, var2, var3);
   }

   @Override
   public void write(int var1) {
      this.writer.write(var1);
   }

   @Override
   public void write(String var1) {
      this.writer.write(var1);
   }

   @Override
   public void write(String var1, int var2, int var3) {
      this.writer.write(var1, var2, var3);
   }
}
