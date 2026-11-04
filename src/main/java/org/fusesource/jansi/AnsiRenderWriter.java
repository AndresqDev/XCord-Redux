package org.fusesource.jansi;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

public class AnsiRenderWriter extends PrintWriter {
   public AnsiRenderWriter(OutputStream var1) {
      super(var1);
   }

   public AnsiRenderWriter(OutputStream var1, boolean var2) {
      super(var1, var2);
   }

   public AnsiRenderWriter(Writer var1) {
      super(var1);
   }

   public AnsiRenderWriter(Writer var1, boolean var2) {
      super(var1, var2);
   }

   @Override
   public void write(String var1) {
      if (AnsiRenderer.test(var1)) {
         super.write(AnsiRenderer.render(var1));
      } else {
         super.write(var1);
      }
   }

   @Override
   public PrintWriter format(String var1, Object... var2) {
      this.print(String.format(var1, var2));
      return this;
   }

   @Override
   public PrintWriter format(Locale var1, String var2, Object... var3) {
      this.print(String.format(var1, var2, var3));
      return this;
   }
}
