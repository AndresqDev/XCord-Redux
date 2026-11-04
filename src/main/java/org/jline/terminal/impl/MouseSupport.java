package org.jline.terminal.impl;

import java.io.EOFException;
import java.io.IOError;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.function.IntSupplier;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.jline.utils.InputStreamReader;

public class MouseSupport {
   public static boolean hasMouseSupport(Terminal var0) {
      return var0.getStringCapability(InfoCmp.Capability.key_mouse) != null;
   }

   public static boolean trackMouse(Terminal var0, Terminal.MouseTracking var1) {
      if (hasMouseSupport(var0)) {
         switch (var1) {
            case Off:
               var0.writer().write("\u001b[?1000l");
               break;
            case Normal:
               var0.writer().write("\u001b[?1005h\u001b[?1000h");
               break;
            case Button:
               var0.writer().write("\u001b[?1005h\u001b[?1002h");
               break;
            case Any:
               var0.writer().write("\u001b[?1005h\u001b[?1003h");
         }

         var0.flush();
         return true;
      } else {
         return false;
      }
   }

   public static MouseEvent readMouse(Terminal var0, MouseEvent var1) {
      return readMouse(() -> readExt(var0), var1);
   }

   public static MouseEvent readMouse(IntSupplier var0, MouseEvent var1) {
      int var2 = var0.getAsInt() - 32;
      int var3 = var0.getAsInt() - 32 - 1;
      int var4 = var0.getAsInt() - 32 - 1;
      EnumSet var7 = EnumSet.noneOf(MouseEvent.Modifier.class);
      if ((var2 & 4) == 4) {
         var7.add(MouseEvent.Modifier.Shift);
      }

      if ((var2 & 8) == 8) {
         var7.add(MouseEvent.Modifier.Alt);
      }

      if ((var2 & 16) == 16) {
         var7.add(MouseEvent.Modifier.Control);
      }

      MouseEvent.Type var5;
      MouseEvent.Button var6;
      if ((var2 & 64) == 64) {
         var5 = MouseEvent.Type.Wheel;
         var6 = (var2 & 1) == 1 ? MouseEvent.Button.WheelDown : MouseEvent.Button.WheelUp;
      } else {
         int var8 = var2 & 3;
         switch (var8) {
            case 0:
               var6 = MouseEvent.Button.Button1;
               if (var1.getButton() != var6 || var1.getType() != MouseEvent.Type.Pressed && var1.getType() != MouseEvent.Type.Dragged) {
                  var5 = MouseEvent.Type.Pressed;
               } else {
                  var5 = MouseEvent.Type.Dragged;
               }
               break;
            case 1:
               var6 = MouseEvent.Button.Button2;
               if (var1.getButton() != var6 || var1.getType() != MouseEvent.Type.Pressed && var1.getType() != MouseEvent.Type.Dragged) {
                  var5 = MouseEvent.Type.Pressed;
               } else {
                  var5 = MouseEvent.Type.Dragged;
               }
               break;
            case 2:
               var6 = MouseEvent.Button.Button3;
               if (var1.getButton() != var6 || var1.getType() != MouseEvent.Type.Pressed && var1.getType() != MouseEvent.Type.Dragged) {
                  var5 = MouseEvent.Type.Pressed;
               } else {
                  var5 = MouseEvent.Type.Dragged;
               }
               break;
            default:
               if (var1.getType() != MouseEvent.Type.Pressed && var1.getType() != MouseEvent.Type.Dragged) {
                  var6 = MouseEvent.Button.NoButton;
                  var5 = MouseEvent.Type.Moved;
               } else {
                  var6 = var1.getButton();
                  var5 = MouseEvent.Type.Released;
               }
         }
      }

      return new MouseEvent(var5, var6, var7, var3, var4);
   }

   private static int readExt(Terminal var0) {
      try {
         int var1;
         if (var0.encoding() != StandardCharsets.UTF_8) {
            var1 = new InputStreamReader(var0.input(), StandardCharsets.UTF_8).read();
         } else {
            var1 = var0.reader().read();
         }

         if (var1 < 0) {
            throw new EOFException();
         } else {
            return var1;
         }
      } catch (IOException var2) {
         throw new IOError(var2);
      }
   }
}
