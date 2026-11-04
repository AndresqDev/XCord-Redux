package org.fusesource.jansi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class HtmlAnsiOutputStream extends AnsiOutputStream {
   private boolean concealOn = false;
   private static final String[] ANSI_COLOR_MAP = new String[]{"black", "red", "green", "yellow", "blue", "magenta", "cyan", "white"};
   private static final byte[] BYTES_QUOT = "&quot;".getBytes();
   private static final byte[] BYTES_AMP = "&amp;".getBytes();
   private static final byte[] BYTES_LT = "&lt;".getBytes();
   private static final byte[] BYTES_GT = "&gt;".getBytes();
   private final List<String> closingAttributes = new ArrayList<>();

   @Override
   public void close() throws IOException {
      this.closeAttributes();
      super.close();
   }

   public HtmlAnsiOutputStream(OutputStream var1) {
      super(var1);
   }

   private void write(String var1) throws IOException {
      super.out.write(var1.getBytes());
   }

   private void writeAttribute(String var1) throws IOException {
      this.write("<" + var1 + ">");
      this.closingAttributes.add(0, var1.split(" ", 2)[0]);
   }

   private void closeAttributes() throws IOException {
      for (String var2 : this.closingAttributes) {
         this.write("</" + var2 + ">");
      }

      this.closingAttributes.clear();
   }

   @Override
   public void write(int var1) throws IOException {
      switch (var1) {
         case 34:
            this.out.write(BYTES_QUOT);
            break;
         case 38:
            this.out.write(BYTES_AMP);
            break;
         case 60:
            this.out.write(BYTES_LT);
            break;
         case 62:
            this.out.write(BYTES_GT);
            break;
         default:
            super.write(var1);
      }
   }

   public void writeLine(byte[] var1, int var2, int var3) throws IOException {
      this.write(var1, var2, var3);
      this.closeAttributes();
   }

   @Override
   protected void processSetAttribute(int var1) throws IOException {
      switch (var1) {
         case 1:
            this.writeAttribute("b");
            break;
         case 4:
            this.writeAttribute("u");
         case 7:
         case 27:
         default:
            break;
         case 8:
            this.write("\u001b[8m");
            this.concealOn = true;
            break;
         case 22:
            this.closeAttributes();
            break;
         case 24:
            this.closeAttributes();
      }
   }

   @Override
   protected void processAttributeRest() throws IOException {
      if (this.concealOn) {
         this.write("\u001b[0m");
         this.concealOn = false;
      }

      this.closeAttributes();
   }

   @Override
   protected void processSetForegroundColor(int var1, boolean var2) throws IOException {
      this.writeAttribute("span style=\"color: " + ANSI_COLOR_MAP[var1] + ";\"");
   }

   @Override
   protected void processSetBackgroundColor(int var1, boolean var2) throws IOException {
      this.writeAttribute("span style=\"background-color: " + ANSI_COLOR_MAP[var1] + ";\"");
   }
}
