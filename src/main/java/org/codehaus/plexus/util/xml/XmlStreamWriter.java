package org.codehaus.plexus.util.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlStreamWriter extends Writer {
   private static final int BUFFER_SIZE = 4096;
   private StringWriter xmlPrologWriter = new StringWriter(4096);
   private OutputStream out;
   private Writer writer;
   private String encoding;
   static final Pattern ENCODING_PATTERN = XmlReader.ENCODING_PATTERN;

   public XmlStreamWriter(OutputStream var1) {
      this.out = var1;
   }

   public XmlStreamWriter(File var1) throws FileNotFoundException {
      this(new FileOutputStream(var1));
   }

   public String getEncoding() {
      return this.encoding;
   }

   @Override
   public void close() throws IOException {
      if (this.writer == null) {
         this.encoding = "UTF-8";
         this.writer = new OutputStreamWriter(this.out, this.encoding);
         this.writer.write(this.xmlPrologWriter.toString());
      }

      this.writer.close();
   }

   @Override
   public void flush() throws IOException {
      if (this.writer != null) {
         this.writer.flush();
      }
   }

   private void detectEncoding(char[] var1, int var2, int var3) throws IOException {
      int var4 = var3;
      StringBuffer var5 = this.xmlPrologWriter.getBuffer();
      if (var5.length() + var3 > 4096) {
         var4 = 4096 - var5.length();
      }

      this.xmlPrologWriter.write(var1, var2, var4);
      if (var5.length() >= 5) {
         if (var5.substring(0, 5).equals("<?xml")) {
            int var6 = var5.indexOf("?>");
            if (var6 > 0) {
               Matcher var7 = ENCODING_PATTERN.matcher(var5.substring(0, var6));
               if (var7.find()) {
                  this.encoding = var7.group(1).toUpperCase(Locale.ENGLISH);
                  this.encoding = this.encoding.substring(1, this.encoding.length() - 1);
               } else {
                  this.encoding = "UTF-8";
               }
            } else if (var5.length() >= 4096) {
               this.encoding = "UTF-8";
            }
         } else {
            this.encoding = "UTF-8";
         }

         if (this.encoding != null) {
            this.xmlPrologWriter = null;
            this.writer = new OutputStreamWriter(this.out, this.encoding);
            this.writer.write(var5.toString());
            if (var3 > var4) {
               this.writer.write(var1, var2 + var4, var3 - var4);
            }
         }
      }
   }

   @Override
   public void write(char[] var1, int var2, int var3) throws IOException {
      if (this.xmlPrologWriter != null) {
         this.detectEncoding(var1, var2, var3);
      } else {
         this.writer.write(var1, var2, var3);
      }
   }
}
