package org.apache.logging.log4j.io.internal;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalPrintWriter extends PrintWriter {
   public InternalPrintWriter(ExtendedLogger var1, boolean var2, String var3, Level var4, Marker var5) {
      super(new InternalWriter(var1, var3, var4, var5), var2);
   }

   public InternalPrintWriter(Writer var1, boolean var2, ExtendedLogger var3, String var4, Level var5, Marker var6) {
      super(new InternalFilterWriter(var1, var3, var4, var5, var6), var2);
   }

   public InternalPrintWriter append(char var1) {
      super.append(var1);
      return this;
   }

   public InternalPrintWriter append(CharSequence var1) {
      super.append(var1);
      return this;
   }

   public InternalPrintWriter append(CharSequence var1, int var2, int var3) {
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

   public InternalPrintWriter format(Locale var1, String var2, Object... var3) {
      super.format(var1, var2, var3);
      return this;
   }

   public InternalPrintWriter format(String var1, Object... var2) {
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

   public InternalPrintWriter printf(Locale var1, String var2, Object... var3) {
      super.printf(var1, var2, var3);
      return this;
   }

   public InternalPrintWriter printf(String var1, Object... var2) {
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
   public void write(char[] var1) {
      super.write(var1);
   }

   @Override
   public void write(char[] var1, int var2, int var3) {
      super.write(var1, var2, var3);
   }

   @Override
   public void write(int var1) {
      super.write(var1);
   }

   @Override
   public void write(String var1) {
      super.write(var1);
   }

   @Override
   public void write(String var1, int var2, int var3) {
      super.write(var1, var2, var3);
   }
}
