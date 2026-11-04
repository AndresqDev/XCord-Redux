package org.fusesource.jansi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class FilterPrintStream extends PrintStream {
   private static final String NEWLINE = System.getProperty("line.separator");
   protected final PrintStream ps;

   public FilterPrintStream(PrintStream var1) {
      super(new OutputStream() {
         @Override
         public void write(int var1) throws IOException {
            throw new RuntimeException("Direct OutputStream use forbidden: must go through delegate PrintStream");
         }
      });
      this.ps = var1;
   }

   protected boolean filter(int var1) {
      return true;
   }

   @Override
   public void write(int var1) {
      if (this.filter(var1)) {
         this.ps.write(var1);
      }
   }

   @Override
   public void write(byte[] var1, int var2, int var3) {
      for (int var4 = 0; var4 < var3; var4++) {
         this.write(var1[var2 + var4]);
      }
   }

   @Override
   public boolean checkError() {
      return super.checkError() || this.ps.checkError();
   }

   @Override
   public void close() {
      super.close();
      this.ps.close();
   }

   @Override
   public void flush() {
      super.flush();
      this.ps.flush();
   }

   private void write(char[] var1) {
      for (char var5 : var1) {
         if (this.filter(var5)) {
            this.ps.print(var5);
         }
      }
   }

   private void write(String var1) {
      char[] var2 = new char[var1.length()];
      var1.getChars(0, var1.length(), var2, 0);
      this.write(var2);
   }

   private void newLine() {
      this.write(NEWLINE);
   }

   @Override
   public void print(boolean var1) {
      this.write(var1 ? "true" : "false");
   }

   @Override
   public void print(char var1) {
      this.write(String.valueOf(var1));
   }

   @Override
   public void print(int var1) {
      this.write(String.valueOf(var1));
   }

   @Override
   public void print(long var1) {
      this.write(String.valueOf(var1));
   }

   @Override
   public void print(float var1) {
      this.write(String.valueOf(var1));
   }

   @Override
   public void print(double var1) {
      this.write(String.valueOf(var1));
   }

   @Override
   public void print(char[] var1) {
      this.write(var1);
   }

   @Override
   public void print(String var1) {
      if (var1 == null) {
         var1 = "null";
      }

      this.write(var1);
   }

   @Override
   public void print(Object var1) {
      this.write(String.valueOf(var1));
   }

   @Override
   public void println() {
      this.newLine();
   }

   @Override
   public void println(boolean var1) {
      synchronized (this) {
         this.print(var1);
         this.newLine();
      }
   }

   @Override
   public void println(char var1) {
      synchronized (this) {
         this.print(var1);
         this.newLine();
      }
   }

   @Override
   public void println(int var1) {
      synchronized (this) {
         this.print(var1);
         this.newLine();
      }
   }

   @Override
   public void println(long var1) {
      synchronized (this) {
         this.print(var1);
         this.newLine();
      }
   }

   @Override
   public void println(float var1) {
      synchronized (this) {
         this.print(var1);
         this.newLine();
      }
   }

   @Override
   public void println(double var1) {
      synchronized (this) {
         this.print(var1);
         this.newLine();
      }
   }

   @Override
   public void println(char[] var1) {
      synchronized (this) {
         this.print(var1);
         this.newLine();
      }
   }

   @Override
   public void println(String var1) {
      synchronized (this) {
         this.print(var1);
         this.newLine();
      }
   }

   @Override
   public void println(Object var1) {
      String var2 = String.valueOf(var1);
      synchronized (this) {
         this.print(var2);
         this.newLine();
      }
   }
}
