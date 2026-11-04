package org.jline.keymap;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import org.jline.reader.EndOfFileException;
import org.jline.utils.ClosedException;
import org.jline.utils.NonBlockingReader;

public class BindingReader {
   protected final NonBlockingReader reader;
   protected final StringBuilder opBuffer = new StringBuilder();
   protected final Deque<Integer> pushBackChar = new ArrayDeque<>();
   protected String lastBinding;

   public BindingReader(NonBlockingReader var1) {
      this.reader = var1;
   }

   public <T> T readBinding(KeyMap<T> var1) {
      return this.readBinding(var1, null, true);
   }

   public <T> T readBinding(KeyMap<T> var1, KeyMap<T> var2) {
      return this.readBinding(var1, var2, true);
   }

   public <T> T readBinding(KeyMap<T> var1, KeyMap<T> var2, boolean var3) {
      this.lastBinding = null;
      Object var4 = null;
      int[] var5 = new int[1];
      boolean var6 = false;

      while (true) {
         if (var2 != null) {
            var4 = var2.getBound(this.opBuffer, var5);
         }

         if (var4 == null && (var2 == null || var5[0] >= 0)) {
            var4 = var1.getBound(this.opBuffer, var5);
         }

         if (var4 != null) {
            if (var5[0] >= 0) {
               this.runMacro(this.opBuffer.substring(this.opBuffer.length() - var5[0]));
               this.opBuffer.setLength(this.opBuffer.length() - var5[0]);
            } else {
               long var7 = var1.getAmbiguousTimeout();
               if (var7 > 0L && this.peekCharacter(var7) != -2) {
                  var4 = null;
               }
            }

            if (var4 != null) {
               this.lastBinding = this.opBuffer.toString();
               this.opBuffer.setLength(0);
               return (T)var4;
            }
         } else if (var5[0] > 0) {
            int var9 = this.opBuffer.codePointAt(0);
            String var8 = this.opBuffer.substring(Character.charCount(var9));
            this.lastBinding = this.opBuffer.substring(0, Character.charCount(var9));
            var4 = var9 >= 128 ? var1.getUnicode() : var1.getNomatch();
            this.opBuffer.setLength(0);
            this.opBuffer.append(var8);
            if (var4 != null) {
               return (T)var4;
            }
         }

         if (!var3 && var6) {
            return null;
         }

         int var10 = this.readCharacter();
         if (var10 == -1) {
            return null;
         }

         this.opBuffer.appendCodePoint(var10);
         var6 = true;
      }
   }

   public int readCharacter() {
      if (!this.pushBackChar.isEmpty()) {
         return this.pushBackChar.pop();
      } else {
         try {
            int var1 = -2;
            int var2 = 0;

            while (var1 == -2) {
               var1 = this.reader.read(100L);
               if (var1 >= 0 && Character.isHighSurrogate((char)var1)) {
                  var2 = var1;
                  var1 = -2;
               }
            }

            return var2 != 0 ? Character.toCodePoint((char)var2, (char)var1) : var1;
         } catch (ClosedException var3) {
            throw new EndOfFileException(var3);
         } catch (IOException var4) {
            throw new IOError(var4);
         }
      }
   }

   public int peekCharacter(long var1) {
      if (!this.pushBackChar.isEmpty()) {
         return this.pushBackChar.peek();
      } else {
         try {
            return this.reader.peek(var1);
         } catch (IOException var4) {
            throw new IOError(var4);
         }
      }
   }

   public void runMacro(String var1) {
      var1.codePoints().forEachOrdered(this.pushBackChar::addLast);
   }

   public String getCurrentBuffer() {
      return this.opBuffer.toString();
   }

   public String getLastBinding() {
      return this.lastBinding;
   }
}
