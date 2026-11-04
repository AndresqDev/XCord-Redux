package org.jline.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jline.terminal.Terminal;

public class Display {
   protected final Terminal terminal;
   protected final boolean fullScreen;
   protected List<AttributedString> oldLines = Collections.emptyList();
   protected int cursorPos;
   private int columns;
   private int columns1;
   protected int rows;
   protected boolean reset;
   protected boolean delayLineWrap;
   protected final Map<InfoCmp.Capability, Integer> cost = new HashMap<>();
   protected final boolean canScroll;
   protected final boolean wrapAtEol;
   protected final boolean delayedWrapAtEol;
   protected final boolean cursorDownIsNewLine;

   public Display(Terminal var1, boolean var2) {
      this.terminal = var1;
      this.fullScreen = var2;
      this.canScroll = this.can(InfoCmp.Capability.insert_line, InfoCmp.Capability.parm_insert_line)
         && this.can(InfoCmp.Capability.delete_line, InfoCmp.Capability.parm_delete_line);
      this.wrapAtEol = var1.getBooleanCapability(InfoCmp.Capability.auto_right_margin);
      this.delayedWrapAtEol = this.wrapAtEol && var1.getBooleanCapability(InfoCmp.Capability.eat_newline_glitch);
      this.cursorDownIsNewLine = "\n".equals(Curses.tputs(var1.getStringCapability(InfoCmp.Capability.cursor_down)));
   }

   public boolean delayLineWrap() {
      return this.delayLineWrap;
   }

   public void setDelayLineWrap(boolean var1) {
      this.delayLineWrap = var1;
   }

   public void resize(int var1, int var2) {
      if (this.rows != var1 || this.columns != var2) {
         this.rows = var1;
         this.columns = var2;
         this.columns1 = var2 + 1;
         this.oldLines = AttributedString.join(AttributedString.EMPTY, this.oldLines).columnSplitLength(var2, true, this.delayLineWrap());
      }
   }

   public void reset() {
      this.oldLines = Collections.emptyList();
   }

   public void clear() {
      if (this.fullScreen) {
         this.reset = true;
      }
   }

   public void updateAnsi(List<String> var1, int var2) {
      this.update(var1.stream().map(AttributedString::fromAnsi).collect(Collectors.toList()), var2);
   }

   public void update(List<AttributedString> var1, int var2) {
      this.update(var1, var2, true);
   }

   public void update(List<AttributedString> var1, int var2, boolean var3) {
      if (this.reset) {
         this.terminal.puts(InfoCmp.Capability.clear_screen);
         this.oldLines.clear();
         this.cursorPos = 0;
         this.reset = false;
      }

      Integer var4 = this.terminal.getNumericCapability(InfoCmp.Capability.max_colors);
      if (var4 == null || var4 < 8) {
         var1 = var1.stream().map(var0 -> new AttributedString(var0.toString())).collect(Collectors.toList());
      }

      if ((this.fullScreen || var1.size() >= this.rows) && var1.size() == this.oldLines.size() && this.canScroll) {
         int var5 = 0;
         int var6 = 0;
         int var7 = var1.size();

         while (var5 < var7 && Objects.equals(var1.get(var5), this.oldLines.get(var5))) {
            var5++;
         }

         while (var6 < var7 - var5 - 1 && Objects.equals(var1.get(var1.size() - var6 - 1), this.oldLines.get(this.oldLines.size() - var6 - 1))) {
            var6++;
         }

         List var8 = var1.subList(var5, var1.size() - var6);
         List var9 = this.oldLines.subList(var5, this.oldLines.size() - var6);
         int[] var10 = longestCommon(var8, var9);
         if (var10 != null) {
            int var11 = var10[0];
            int var12 = var10[1];
            int var13 = var10[2];
            if (var13 > 1 && var11 < var12) {
               this.moveVisualCursorTo((var5 + var11) * this.columns1);
               int var37 = var12 - var11;
               this.deleteLines(var37);

               for (int var40 = 0; var40 < var37; var40++) {
                  this.oldLines.remove(var5 + var11);
               }

               if (var6 > 0) {
                  this.moveVisualCursorTo((var5 + var11 + var13) * this.columns1);
                  this.insertLines(var37);

                  for (int var41 = 0; var41 < var37; var41++) {
                     this.oldLines.add(var5 + var11 + var13, new AttributedString(""));
                  }
               }
            } else if (var13 > 1 && var11 > var12) {
               int var14 = var11 - var12;
               if (var6 > 0) {
                  this.moveVisualCursorTo((var5 + var12 + var13) * this.columns1);
                  this.deleteLines(var14);

                  for (int var15 = 0; var15 < var14; var15++) {
                     this.oldLines.remove(var5 + var12 + var13);
                  }
               }

               this.moveVisualCursorTo((var5 + var12) * this.columns1);
               this.insertLines(var14);

               for (int var39 = 0; var39 < var14; var39++) {
                  this.oldLines.add(var5 + var12, new AttributedString(""));
               }
            }
         }
      }

      int var25 = 0;
      int var26 = 0;
      int var27 = Math.max(this.oldLines.size(), var1.size());
      boolean var28 = false;

      while (var25 < var27) {
         AttributedString var29 = var25 < this.oldLines.size() ? this.oldLines.get(var25) : AttributedString.NEWLINE;
         AttributedString var31 = var25 < var1.size() ? (AttributedString)var1.get(var25) : AttributedString.NEWLINE;
         var26 = var25 * this.columns1;
         int var32 = var26;
         int var33 = var29.length();
         int var35 = var31.length();
         boolean var38 = var33 > 0 && var29.charAt(var33 - 1) == '\n';
         boolean var42 = var35 > 0 && var31.charAt(var35 - 1) == '\n';
         if (var38) {
            var29 = var29.substring(0, --var33);
         }

         if (var42) {
            var31 = var31.substring(0, --var35);
         }

         if (var28 && var25 == (this.cursorPos + 1) / this.columns1 && var25 < var1.size()) {
            this.cursorPos++;
            if (var35 != 0 && !var31.isHidden(0)) {
               AttributedString var16 = var31.columnSubSequence(0, 1);
               this.rawPrint(var16);
               this.cursorPos++;
               int var17 = var16.length();
               var31 = var31.substring(var17, var35);
               var35 -= var17;
               if (var33 >= var17) {
                  var29 = var29.substring(var17, var33);
                  var33 -= var17;
               }

               var26 = this.cursorPos;
            } else {
               this.rawPrint(new AttributedString(" \b"));
            }
         }

         List var43 = DiffHelper.diff(var29, var31);
         boolean var44 = true;
         boolean var18 = false;

         for (int var19 = 0; var19 < var43.size(); var19++) {
            DiffHelper.Diff var20 = (DiffHelper.Diff)var43.get(var19);
            int var21 = var20.text.columnLength();
            switch (var20.operation) {
               case EQUAL:
                  if (!var44) {
                     this.cursorPos = this.moveVisualCursorTo(var26);
                     this.rawPrint(var20.text);
                     this.cursorPos += var21;
                     var26 = this.cursorPos;
                  } else {
                     var26 += var21;
                  }
                  break;
               case INSERT:
                  if (var19 <= var43.size() - 2 && ((DiffHelper.Diff)var43.get(var19 + 1)).operation == DiffHelper.Operation.EQUAL) {
                     this.cursorPos = this.moveVisualCursorTo(var26);
                     if (this.insertChars(var21)) {
                        this.rawPrint(var20.text);
                        this.cursorPos += var21;
                        var26 = this.cursorPos;
                        break;
                     }
                  } else if (var19 <= var43.size() - 2
                     && ((DiffHelper.Diff)var43.get(var19 + 1)).operation == DiffHelper.Operation.DELETE
                     && var21 == ((DiffHelper.Diff)var43.get(var19 + 1)).text.columnLength()) {
                     this.moveVisualCursorTo(var26);
                     this.rawPrint(var20.text);
                     this.cursorPos += var21;
                     var26 = this.cursorPos;
                     var19++;
                     break;
                  }

                  this.moveVisualCursorTo(var26);
                  this.rawPrint(var20.text);
                  this.cursorPos += var21;
                  var26 = this.cursorPos;
                  var44 = false;
                  break;
               case DELETE:
                  if (!var18 && var26 - var32 < this.columns) {
                     if (var19 <= var43.size() - 2
                        && ((DiffHelper.Diff)var43.get(var19 + 1)).operation == DiffHelper.Operation.EQUAL
                        && var26 + ((DiffHelper.Diff)var43.get(var19 + 1)).text.columnLength() < this.columns) {
                        this.moveVisualCursorTo(var26);
                        if (this.deleteChars(var21)) {
                           continue;
                        }
                     }

                     int var22 = var29.columnLength();
                     int var23 = var31.columnLength();
                     int var24 = Math.max(var22, var23) - (var26 - var32);
                     this.moveVisualCursorTo(var26);
                     if (!this.terminal.puts(InfoCmp.Capability.clr_eol)) {
                        this.rawPrint(' ', var24);
                        this.cursorPos += var24;
                     }

                     var18 = true;
                     var44 = false;
                  }
            }
         }

         var25++;
         boolean var45 = !var42 && var25 < var1.size();
         if (var2 + 1 == var25 * this.columns1 && (var45 || !this.delayLineWrap)) {
            var2++;
         }

         boolean var46 = (this.cursorPos - var32) % this.columns1 == this.columns;
         var28 = false;
         if (!this.delayedWrapAtEol) {
            if (var46) {
               if (this.wrapAtEol) {
                  this.terminal.writer().write(" \b");
                  this.cursorPos++;
               } else {
                  this.terminal.puts(InfoCmp.Capability.carriage_return);
                  this.cursorPos = var32;
               }

               var26 = this.cursorPos;
            }
         } else {
            boolean var47 = !var38 && var25 < this.oldLines.size();
            if (var45 != var47 && (!var47 || !var18)) {
               this.moveVisualCursorTo(var25 * this.columns1 - 1, var1);
               if (var45) {
                  var28 = true;
               } else {
                  this.terminal.puts(InfoCmp.Capability.clr_eol);
               }
            }
         }
      }

      int var30 = this.cursorPos;
      if (this.cursorPos != var2) {
         this.moveVisualCursorTo(var2 < 0 ? var26 : var2, var1);
      }

      this.oldLines = var1;
      if (var3) {
         this.terminal.flush();
      }
   }

   protected boolean deleteLines(int var1) {
      return this.perform(InfoCmp.Capability.delete_line, InfoCmp.Capability.parm_delete_line, var1);
   }

   protected boolean insertLines(int var1) {
      return this.perform(InfoCmp.Capability.insert_line, InfoCmp.Capability.parm_insert_line, var1);
   }

   protected boolean insertChars(int var1) {
      return this.perform(InfoCmp.Capability.insert_character, InfoCmp.Capability.parm_ich, var1);
   }

   protected boolean deleteChars(int var1) {
      return this.perform(InfoCmp.Capability.delete_character, InfoCmp.Capability.parm_dch, var1);
   }

   protected boolean can(InfoCmp.Capability var1, InfoCmp.Capability var2) {
      return this.terminal.getStringCapability(var1) != null || this.terminal.getStringCapability(var2) != null;
   }

   protected boolean perform(InfoCmp.Capability var1, InfoCmp.Capability var2, int var3) {
      boolean var4 = this.terminal.getStringCapability(var2) != null;
      boolean var5 = this.terminal.getStringCapability(var1) != null;
      if (!var4 || var5 && this.cost(var1) * var3 <= this.cost(var2)) {
         if (!var5) {
            return false;
         } else {
            for (int var6 = 0; var6 < var3; var6++) {
               this.terminal.puts(var1);
            }

            return true;
         }
      } else {
         this.terminal.puts(var2, var3);
         return true;
      }
   }

   private int cost(InfoCmp.Capability var1) {
      return this.cost.computeIfAbsent(var1, this::computeCost);
   }

   private int computeCost(InfoCmp.Capability var1) {
      String var2 = Curses.tputs(this.terminal.getStringCapability(var1), 0);
      return var2 != null ? var2.length() : Integer.MAX_VALUE;
   }

   private static int[] longestCommon(List<AttributedString> var0, List<AttributedString> var1) {
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      for (int var5 = 0; var5 < var0.size(); var5++) {
         for (int var6 = 0; var6 < var1.size(); var6++) {
            int var7 = 0;

            while (Objects.equals(var0.get(var5 + var7), var1.get(var6 + var7))) {
               var7++;
               if (var5 + var7 >= var0.size() || var6 + var7 >= var1.size()) {
                  break;
               }
            }

            if (var7 > var4) {
               var4 = var7;
               var2 = var5;
               var3 = var6;
            }
         }
      }

      return var4 != 0 ? new int[]{var2, var3, var4} : null;
   }

   protected void moveVisualCursorTo(int var1, List<AttributedString> var2) {
      if (this.cursorPos != var1) {
         boolean var3 = var1 % this.columns1 == this.columns;
         this.moveVisualCursorTo(var1 - (var3 ? 1 : 0));
         if (var3) {
            int var4 = var1 / this.columns1;
            AttributedString var5 = var4 >= var2.size()
               ? AttributedString.EMPTY
               : ((AttributedString)var2.get(var4)).columnSubSequence(this.columns - 1, this.columns);
            if (var5.length() == 0) {
               this.rawPrint(32);
            } else {
               this.rawPrint(var5);
            }

            this.cursorPos++;
         }
      }
   }

   protected int moveVisualCursorTo(int var1) {
      int var2 = this.cursorPos;
      if (var2 == var1) {
         return var1;
      } else {
         int var3 = this.columns1;
         int var4 = var2 / var3;
         int var5 = var2 % var3;
         int var6 = var1 / var3;
         int var7 = var1 % var3;
         if (var5 == this.columns) {
            this.terminal.puts(InfoCmp.Capability.carriage_return);
            var5 = 0;
         }

         if (var4 > var6) {
            this.perform(InfoCmp.Capability.cursor_up, InfoCmp.Capability.parm_up_cursor, var4 - var6);
         } else if (var4 < var6) {
            if (this.fullScreen) {
               if (!this.terminal.puts(InfoCmp.Capability.parm_down_cursor, var6 - var4)) {
                  for (int var8 = var4; var8 < var6; var8++) {
                     this.terminal.puts(InfoCmp.Capability.cursor_down);
                  }

                  if (this.cursorDownIsNewLine) {
                     var5 = 0;
                  }
               }
            } else {
               this.terminal.puts(InfoCmp.Capability.carriage_return);
               this.rawPrint('\n', var6 - var4);
               var5 = 0;
            }
         }

         if (var5 != 0 && var7 == 0) {
            this.terminal.puts(InfoCmp.Capability.carriage_return);
         } else if (var5 < var7) {
            this.perform(InfoCmp.Capability.cursor_right, InfoCmp.Capability.parm_right_cursor, var7 - var5);
         } else if (var5 > var7) {
            this.perform(InfoCmp.Capability.cursor_left, InfoCmp.Capability.parm_left_cursor, var5 - var7);
         }

         this.cursorPos = var1;
         return var1;
      }
   }

   void rawPrint(char var1, int var2) {
      for (int var3 = 0; var3 < var2; var3++) {
         this.rawPrint(var1);
      }
   }

   void rawPrint(int var1) {
      this.terminal.writer().write(var1);
   }

   void rawPrint(AttributedString var1) {
      var1.print(this.terminal);
   }

   public int wcwidth(String var1) {
      return AttributedString.fromAnsi(var1).columnLength();
   }
}
