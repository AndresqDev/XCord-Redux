package org.jline.terminal.impl.jansi.win;

import java.io.IOException;
import java.io.Writer;
import org.fusesource.jansi.internal.Kernel32;
import org.fusesource.jansi.internal.WindowsSupport;
import org.jline.utils.AnsiWriter;
import org.jline.utils.Colors;

public final class WindowsAnsiWriter extends AnsiWriter {
   private static final long console = Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
   private static final short FOREGROUND_BLACK = 0;
   private static final short FOREGROUND_YELLOW = (short)(Kernel32.FOREGROUND_RED | Kernel32.FOREGROUND_GREEN);
   private static final short FOREGROUND_MAGENTA = (short)(Kernel32.FOREGROUND_BLUE | Kernel32.FOREGROUND_RED);
   private static final short FOREGROUND_CYAN = (short)(Kernel32.FOREGROUND_BLUE | Kernel32.FOREGROUND_GREEN);
   private static final short FOREGROUND_WHITE = (short)(Kernel32.FOREGROUND_RED | Kernel32.FOREGROUND_GREEN | Kernel32.FOREGROUND_BLUE);
   private static final short BACKGROUND_BLACK = 0;
   private static final short BACKGROUND_YELLOW = (short)(Kernel32.BACKGROUND_RED | Kernel32.BACKGROUND_GREEN);
   private static final short BACKGROUND_MAGENTA = (short)(Kernel32.BACKGROUND_BLUE | Kernel32.BACKGROUND_RED);
   private static final short BACKGROUND_CYAN = (short)(Kernel32.BACKGROUND_BLUE | Kernel32.BACKGROUND_GREEN);
   private static final short BACKGROUND_WHITE = (short)(Kernel32.BACKGROUND_RED | Kernel32.BACKGROUND_GREEN | Kernel32.BACKGROUND_BLUE);
   private static final short[] ANSI_FOREGROUND_COLOR_MAP = new short[]{
      0, Kernel32.FOREGROUND_RED, Kernel32.FOREGROUND_GREEN, FOREGROUND_YELLOW, Kernel32.FOREGROUND_BLUE, FOREGROUND_MAGENTA, FOREGROUND_CYAN, FOREGROUND_WHITE
   };
   private static final short[] ANSI_BACKGROUND_COLOR_MAP = new short[]{
      0, Kernel32.BACKGROUND_RED, Kernel32.BACKGROUND_GREEN, BACKGROUND_YELLOW, Kernel32.BACKGROUND_BLUE, BACKGROUND_MAGENTA, BACKGROUND_CYAN, BACKGROUND_WHITE
   };
   private final Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
   private final short originalColors;
   private boolean negative;
   private boolean bold;
   private boolean underline;
   private short savedX = -1;
   private short savedY = -1;

   public WindowsAnsiWriter(Writer var1) throws IOException {
      super(var1);
      this.getConsoleInfo();
      this.originalColors = this.info.attributes;
   }

   private void getConsoleInfo() throws IOException {
      this.out.flush();
      if (Kernel32.GetConsoleScreenBufferInfo(console, this.info) == 0) {
         throw new IOException("Could not get the screen info: " + WindowsSupport.getLastErrorMessage());
      } else {
         if (this.negative) {
            this.info.attributes = this.invertAttributeColors(this.info.attributes);
         }
      }
   }

   private void applyAttribute() throws IOException {
      this.out.flush();
      short var1 = this.info.attributes;
      if (this.bold) {
         var1 |= Kernel32.FOREGROUND_INTENSITY;
      }

      if (this.underline) {
         var1 |= Kernel32.BACKGROUND_INTENSITY;
      }

      if (this.negative) {
         var1 = this.invertAttributeColors(var1);
      }

      if (Kernel32.SetConsoleTextAttribute(console, var1) == 0) {
         throw new IOException(WindowsSupport.getLastErrorMessage());
      }
   }

   private short invertAttributeColors(short var1) {
      int var2 = 15 & var1;
      var2 <<= 4;
      int var3 = 240 & var1;
      var3 >>= 4;
      return (short)(var1 & '\uff00' | var2 | var3);
   }

   private void applyCursorPosition() throws IOException {
      this.info.cursorPosition.x = (short)Math.max(0, Math.min(this.info.size.x - 1, this.info.cursorPosition.x));
      this.info.cursorPosition.y = (short)Math.max(0, Math.min(this.info.size.y - 1, this.info.cursorPosition.y));
      if (Kernel32.SetConsoleCursorPosition(console, this.info.cursorPosition.copy()) == 0) {
         throw new IOException(WindowsSupport.getLastErrorMessage());
      }
   }

   @Override
   protected void processEraseScreen(int var1) throws IOException {
      this.getConsoleInfo();
      int[] var2 = new int[1];
      switch (var1) {
         case 0:
            int var7 = (this.info.window.bottom - this.info.cursorPosition.y) * this.info.size.x + (this.info.size.x - this.info.cursorPosition.x);
            Kernel32.FillConsoleOutputAttribute(console, this.originalColors, var7, this.info.cursorPosition.copy(), var2);
            Kernel32.FillConsoleOutputCharacterW(console, ' ', var7, this.info.cursorPosition.copy(), var2);
            break;
         case 1:
            Kernel32.COORD var5 = new Kernel32.COORD();
            var5.x = 0;
            var5.y = this.info.window.top;
            int var6 = (this.info.cursorPosition.y - this.info.window.top) * this.info.size.x + this.info.cursorPosition.x;
            Kernel32.FillConsoleOutputAttribute(console, this.originalColors, var6, var5, var2);
            Kernel32.FillConsoleOutputCharacterW(console, ' ', var6, var5, var2);
            break;
         case 2:
            Kernel32.COORD var3 = new Kernel32.COORD();
            var3.x = 0;
            var3.y = this.info.window.top;
            int var4 = this.info.window.height() * this.info.size.x;
            Kernel32.FillConsoleOutputAttribute(console, this.originalColors, var4, var3, var2);
            Kernel32.FillConsoleOutputCharacterW(console, ' ', var4, var3, var2);
      }
   }

   @Override
   protected void processEraseLine(int var1) throws IOException {
      this.getConsoleInfo();
      int[] var2 = new int[1];
      switch (var1) {
         case 0:
            int var5 = this.info.size.x - this.info.cursorPosition.x;
            Kernel32.FillConsoleOutputAttribute(console, this.originalColors, var5, this.info.cursorPosition.copy(), var2);
            Kernel32.FillConsoleOutputCharacterW(console, ' ', var5, this.info.cursorPosition.copy(), var2);
            break;
         case 1:
            Kernel32.COORD var4 = this.info.cursorPosition.copy();
            var4.x = 0;
            Kernel32.FillConsoleOutputAttribute(console, this.originalColors, this.info.cursorPosition.x, var4, var2);
            Kernel32.FillConsoleOutputCharacterW(console, ' ', this.info.cursorPosition.x, var4, var2);
            break;
         case 2:
            Kernel32.COORD var3 = this.info.cursorPosition.copy();
            var3.x = 0;
            Kernel32.FillConsoleOutputAttribute(console, this.originalColors, this.info.size.x, var3, var2);
            Kernel32.FillConsoleOutputCharacterW(console, ' ', this.info.size.x, var3, var2);
      }
   }

   @Override
   protected void processCursorUpLine(int var1) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.x = 0;
      this.info.cursorPosition.y = (short)(this.info.cursorPosition.y - var1);
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorDownLine(int var1) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.x = 0;
      this.info.cursorPosition.y = (short)(this.info.cursorPosition.y + var1);
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorLeft(int var1) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.x = (short)(this.info.cursorPosition.x - var1);
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorRight(int var1) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.x = (short)(this.info.cursorPosition.x + var1);
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorDown(int var1) throws IOException {
      this.getConsoleInfo();
      int var2 = Math.max(0, this.info.cursorPosition.y + var1 - this.info.size.y + 1);
      if (var2 != var1) {
         this.info.cursorPosition.y = (short)(this.info.cursorPosition.y + var1);
         this.applyCursorPosition();
      }

      if (var2 > 0) {
         Kernel32.SMALL_RECT var3 = this.info.window.copy();
         var3.top = 0;
         Kernel32.COORD var4 = new Kernel32.COORD();
         var4.x = 0;
         var4.y = (short)(-var2);
         Kernel32.CHAR_INFO var5 = new Kernel32.CHAR_INFO();
         var5.unicodeChar = ' ';
         var5.attributes = this.originalColors;
         Kernel32.ScrollConsoleScreenBuffer(console, var3, var3, var4, var5);
      }
   }

   @Override
   protected void processCursorUp(int var1) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.y = (short)(this.info.cursorPosition.y - var1);
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorTo(int var1, int var2) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.y = (short)(this.info.window.top + var1 - 1);
      this.info.cursorPosition.x = (short)(var2 - 1);
      this.applyCursorPosition();
   }

   @Override
   protected void processCursorToColumn(int var1) throws IOException {
      this.getConsoleInfo();
      this.info.cursorPosition.x = (short)(var1 - 1);
      this.applyCursorPosition();
   }

   @Override
   protected void processSetForegroundColorExt(int var1) throws IOException {
      int var2 = Colors.roundColor(var1, 16);
      this.info.attributes = (short)(this.info.attributes & -8 | ANSI_FOREGROUND_COLOR_MAP[var2 & 7]);
      this.info.attributes = (short)(this.info.attributes & ~Kernel32.FOREGROUND_INTENSITY | (var2 >= 8 ? Kernel32.FOREGROUND_INTENSITY : 0));
      this.applyAttribute();
   }

   @Override
   protected void processSetBackgroundColorExt(int var1) throws IOException {
      int var2 = Colors.roundColor(var1, 16);
      this.info.attributes = (short)(this.info.attributes & -113 | ANSI_BACKGROUND_COLOR_MAP[var2 & 7]);
      this.info.attributes = (short)(this.info.attributes & ~Kernel32.BACKGROUND_INTENSITY | (var2 >= 8 ? Kernel32.BACKGROUND_INTENSITY : 0));
      this.applyAttribute();
   }

   @Override
   protected void processDefaultTextColor() throws IOException {
      this.info.attributes = (short)(this.info.attributes & -16 | this.originalColors & 15);
      this.info.attributes = (short)(this.info.attributes & ~Kernel32.FOREGROUND_INTENSITY);
      this.applyAttribute();
   }

   @Override
   protected void processDefaultBackgroundColor() throws IOException {
      this.info.attributes = (short)(this.info.attributes & -241 | this.originalColors & 240);
      this.info.attributes = (short)(this.info.attributes & ~Kernel32.BACKGROUND_INTENSITY);
      this.applyAttribute();
   }

   @Override
   protected void processAttributeRest() throws IOException {
      this.info.attributes = (short)(this.info.attributes & -256 | this.originalColors);
      this.negative = false;
      this.bold = false;
      this.underline = false;
      this.applyAttribute();
   }

   @Override
   protected void processSetAttribute(int var1) throws IOException {
      switch (var1) {
         case 1:
            this.bold = true;
            this.applyAttribute();
            break;
         case 4:
            this.underline = true;
            this.applyAttribute();
            break;
         case 7:
            this.negative = true;
            this.applyAttribute();
            break;
         case 22:
            this.bold = false;
            this.applyAttribute();
            break;
         case 24:
            this.underline = false;
            this.applyAttribute();
            break;
         case 27:
            this.negative = false;
            this.applyAttribute();
      }
   }

   @Override
   protected void processSaveCursorPosition() throws IOException {
      this.getConsoleInfo();
      this.savedX = this.info.cursorPosition.x;
      this.savedY = this.info.cursorPosition.y;
   }

   @Override
   protected void processRestoreCursorPosition() throws IOException {
      if (this.savedX != -1 && this.savedY != -1) {
         this.out.flush();
         this.info.cursorPosition.x = this.savedX;
         this.info.cursorPosition.y = this.savedY;
         this.applyCursorPosition();
      }
   }

   @Override
   protected void processInsertLine(int var1) throws IOException {
      this.getConsoleInfo();
      Kernel32.SMALL_RECT var2 = this.info.window.copy();
      var2.top = this.info.cursorPosition.y;
      Kernel32.COORD var3 = new Kernel32.COORD();
      var3.x = 0;
      var3.y = (short)(this.info.cursorPosition.y + var1);
      Kernel32.CHAR_INFO var4 = new Kernel32.CHAR_INFO();
      var4.attributes = this.originalColors;
      var4.unicodeChar = ' ';
      if (Kernel32.ScrollConsoleScreenBuffer(console, var2, var2, var3, var4) == 0) {
         throw new IOException(WindowsSupport.getLastErrorMessage());
      }
   }

   @Override
   protected void processDeleteLine(int var1) throws IOException {
      this.getConsoleInfo();
      Kernel32.SMALL_RECT var2 = this.info.window.copy();
      var2.top = this.info.cursorPosition.y;
      Kernel32.COORD var3 = new Kernel32.COORD();
      var3.x = 0;
      var3.y = (short)(this.info.cursorPosition.y - var1);
      Kernel32.CHAR_INFO var4 = new Kernel32.CHAR_INFO();
      var4.attributes = this.originalColors;
      var4.unicodeChar = ' ';
      if (Kernel32.ScrollConsoleScreenBuffer(console, var2, var2, var3, var4) == 0) {
         throw new IOException(WindowsSupport.getLastErrorMessage());
      }
   }

   @Override
   protected void processChangeWindowTitle(String var1) {
      Kernel32.SetConsoleTitle(var1);
   }
}
