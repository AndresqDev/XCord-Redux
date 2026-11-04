package org.fusesource.jansi.internal;

import java.io.IOException;

public class WindowsSupport {
   public static String getLastErrorMessage() {
      int var0 = Kernel32.GetLastError();
      short var1 = 160;
      byte[] var2 = new byte[var1];
      Kernel32.FormatMessageW(Kernel32.FORMAT_MESSAGE_FROM_SYSTEM, 0L, var0, 0, var2, var1, null);
      return new String(var2);
   }

   public static int readByte() {
      return Kernel32._getch();
   }

   public static int getConsoleMode() {
      long var0 = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
      if (var0 == (long)Kernel32.INVALID_HANDLE_VALUE) {
         return -1;
      } else {
         int[] var2 = new int[1];
         return Kernel32.GetConsoleMode(var0, var2) == 0 ? -1 : var2[0];
      }
   }

   public static void setConsoleMode(int var0) {
      long var1 = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
      if (var1 != (long)Kernel32.INVALID_HANDLE_VALUE) {
         Kernel32.SetConsoleMode(var1, var0);
      }
   }

   public static int getWindowsTerminalWidth() {
      long var0 = Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
      Kernel32.CONSOLE_SCREEN_BUFFER_INFO var2 = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
      Kernel32.GetConsoleScreenBufferInfo(var0, var2);
      return var2.windowWidth();
   }

   public static int getWindowsTerminalHeight() {
      long var0 = Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
      Kernel32.CONSOLE_SCREEN_BUFFER_INFO var2 = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
      Kernel32.GetConsoleScreenBufferInfo(var0, var2);
      return var2.windowHeight();
   }

   public static int writeConsole(String var0) {
      long var1 = Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
      if (var1 == (long)Kernel32.INVALID_HANDLE_VALUE) {
         return 0;
      } else {
         char[] var3 = var0.toCharArray();
         int[] var4 = new int[1];
         return Kernel32.WriteConsoleW(var1, var3, var3.length, var4, 0L) != 0 ? var4[0] : 0;
      }
   }

   public static Kernel32.INPUT_RECORD[] readConsoleInput(int var0, int var1) throws IOException {
      long var2 = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
      if (var2 == (long)Kernel32.INVALID_HANDLE_VALUE) {
         return null;
      } else {
         return Kernel32.WaitForSingleObject(var2, var1) != 0 ? null : Kernel32.readConsoleInputHelper(var2, var0, false);
      }
   }

   public static Kernel32.INPUT_RECORD[] readConsoleInput(int var0) throws IOException {
      long var1 = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
      return var1 == (long)Kernel32.INVALID_HANDLE_VALUE ? null : Kernel32.readConsoleInputHelper(var1, var0, false);
   }

   public static Kernel32.INPUT_RECORD[] peekConsoleInput(int var0, int var1) throws IOException {
      long var2 = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
      if (var2 == (long)Kernel32.INVALID_HANDLE_VALUE) {
         return null;
      } else {
         return Kernel32.WaitForSingleObject(var2, var1) != 0 ? null : Kernel32.readConsoleInputHelper(var2, var0, true);
      }
   }

   public static Kernel32.INPUT_RECORD[] peekConsoleInput(int var0) throws IOException {
      long var1 = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
      return var1 == (long)Kernel32.INVALID_HANDLE_VALUE ? null : Kernel32.readConsoleInputHelper(var1, var0, true);
   }

   public static void flushConsoleInputBuffer() {
      long var0 = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
      if (var0 != (long)Kernel32.INVALID_HANDLE_VALUE) {
         Kernel32.FlushConsoleInputBuffer(var0);
      }
   }
}
