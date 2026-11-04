package org.yaml.snakeyaml.reader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.scanner.Constant;

public class StreamReader {
   private String name;
   private final Reader stream;
   private int[] dataWindow;
   private int dataLength;
   private int pointer = 0;
   private boolean eof;
   private int index = 0;
   private int line = 0;
   private int column = 0;
   private char[] buffer;
   private static final int BUFFER_SIZE = 1025;

   public StreamReader(String var1) {
      this(new StringReader(var1));
      this.name = "'string'";
   }

   public StreamReader(Reader var1) {
      this.name = "'reader'";
      this.dataWindow = new int[0];
      this.dataLength = 0;
      this.stream = var1;
      this.eof = false;
      this.buffer = new char[1025];
   }

   public static boolean isPrintable(String var0) {
      int var1 = var0.length();
      int var2 = 0;

      while (var2 < var1) {
         int var3 = var0.codePointAt(var2);
         if (!isPrintable(var3)) {
            return false;
         }

         var2 += Character.charCount(var3);
      }

      return true;
   }

   public static boolean isPrintable(int var0) {
      return var0 >= 32 && var0 <= 126
         || var0 == 9
         || var0 == 10
         || var0 == 13
         || var0 == 133
         || var0 >= 160 && var0 <= 55295
         || var0 >= 57344 && var0 <= 65533
         || var0 >= 65536 && var0 <= 1114111;
   }

   public Mark getMark() {
      return new Mark(this.name, this.index, this.line, this.column, this.dataWindow, this.pointer);
   }

   public void forward() {
      this.forward(1);
   }

   public void forward(int var1) {
      for (int var2 = 0; var2 < var1 && this.ensureEnoughData(); var2++) {
         int var3 = this.dataWindow[this.pointer++];
         this.index++;
         if (!Constant.LINEBR.has(var3) && (var3 != 13 || !this.ensureEnoughData() || this.dataWindow[this.pointer] == 10)) {
            if (var3 != 65279) {
               this.column++;
            }
         } else {
            this.line++;
            this.column = 0;
         }
      }
   }

   public int peek() {
      return this.ensureEnoughData() ? this.dataWindow[this.pointer] : 0;
   }

   public int peek(int var1) {
      return this.ensureEnoughData(var1) ? this.dataWindow[this.pointer + var1] : 0;
   }

   public String prefix(int var1) {
      if (var1 == 0) {
         return "";
      } else {
         return this.ensureEnoughData(var1)
            ? new String(this.dataWindow, this.pointer, var1)
            : new String(this.dataWindow, this.pointer, Math.min(var1, this.dataLength - this.pointer));
      }
   }

   public String prefixForward(int var1) {
      String var2 = this.prefix(var1);
      this.pointer += var1;
      this.index += var1;
      this.column += var1;
      return var2;
   }

   private boolean ensureEnoughData() {
      return this.ensureEnoughData(0);
   }

   private boolean ensureEnoughData(int var1) {
      if (!this.eof && this.pointer + var1 >= this.dataLength) {
         this.update();
      }

      return this.pointer + var1 < this.dataLength;
   }

   private void update() {
      try {
         int var1 = this.stream.read(this.buffer, 0, 1024);
         if (var1 > 0) {
            int var2 = this.dataLength - this.pointer;
            this.dataWindow = Arrays.copyOfRange(this.dataWindow, this.pointer, this.dataLength + var1);
            if (Character.isHighSurrogate(this.buffer[var1 - 1])) {
               if (this.stream.read(this.buffer, var1, 1) == -1) {
                  this.eof = true;
               } else {
                  var1++;
               }
            }

            int var3 = 32;

            for (int var4 = 0; var4 < var1; var2++) {
               int var5 = Character.codePointAt(this.buffer, var4);
               this.dataWindow[var2] = var5;
               if (isPrintable(var5)) {
                  var4 += Character.charCount(var5);
               } else {
                  var3 = var5;
                  var4 = var1;
               }
            }

            this.dataLength = var2;
            this.pointer = 0;
            if (var3 != 32) {
               throw new ReaderException(this.name, var2 - 1, var3, "special characters are not allowed");
            }
         } else {
            this.eof = true;
         }
      } catch (IOException var6) {
         throw new YAMLException(var6);
      }
   }

   public int getColumn() {
      return this.column;
   }

   public int getIndex() {
      return this.index;
   }

   public int getLine() {
      return this.line;
   }
}
