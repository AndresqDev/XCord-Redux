package org.apache.logging.log4j.io;

import java.nio.CharBuffer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class CharStreamLogger {
   private final ExtendedLogger logger;
   private final Level level;
   private final Marker marker;
   private final StringBuilder msg = new StringBuilder();
   private boolean closed = false;

   public CharStreamLogger(ExtendedLogger var1, Level var2, Marker var3) {
      this.logger = var1;
      this.level = var2 == null ? var1.getLevel() : var2;
      this.marker = var3;
   }

   public void close(String var1) {
      synchronized (this.msg) {
         this.closed = true;
         this.logEnd(var1);
      }
   }

   private void log(String var1) {
      this.logger.logIfEnabled(var1, this.level, this.marker, this.msg.toString());
      this.msg.setLength(0);
   }

   private void logEnd(String var1) {
      if (this.msg.length() > 0) {
         this.log(var1);
      }
   }

   public void put(String var1, char[] var2, int var3, int var4) {
      this.put(var1, CharBuffer.wrap(var2), var3, var4);
   }

   public void put(String var1, CharSequence var2, int var3, int var4) {
      if (var4 >= 0) {
         synchronized (this.msg) {
            if (this.closed) {
               return;
            }

            int var6 = var3;
            int var7 = var3 + var4;
            int var8 = var3;

            while (var8 < var7) {
               char var9 = var2.charAt(var8);
               switch (var9) {
                  case '\n':
                  case '\r':
                     this.msg.append(var2, var6, var8);
                     var6 = var8 + 1;
                     if (var9 == '\n') {
                        this.log(var1);
                     }
                  default:
                     var8++;
               }
            }

            this.msg.append(var2, var6, var7);
         }
      } else {
         this.logEnd(var1);
      }
   }

   public void put(String var1, int var2) {
      if (var2 >= 0) {
         synchronized (this.msg) {
            if (this.closed) {
               return;
            }

            switch (var2) {
               case 10:
                  this.log(var1);
               case 13:
                  break;
               default:
                  this.msg.append((char)var2);
            }
         }
      } else {
         this.logEnd(var1);
      }
   }
}
