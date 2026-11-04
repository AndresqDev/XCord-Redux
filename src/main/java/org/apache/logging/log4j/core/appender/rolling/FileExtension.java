package org.apache.logging.log4j.core.appender.rolling;

import java.io.File;
import java.util.Objects;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.appender.rolling.action.CommonsCompressAction;
import org.apache.logging.log4j.core.appender.rolling.action.GzCompressAction;
import org.apache.logging.log4j.core.appender.rolling.action.ZipCompressAction;

public enum FileExtension {
   ZIP(".zip") {
      @Override
      Action createCompressAction(String var1, String var2, boolean var3, int var4) {
         return new ZipCompressAction(this.source(var1), this.target(var2), var3, var4);
      }
   },
   GZ(".gz") {
      @Override
      Action createCompressAction(String var1, String var2, boolean var3, int var4) {
         return new GzCompressAction(this.source(var1), this.target(var2), var3, var4);
      }
   },
   BZIP2(".bz2") {
      @Override
      Action createCompressAction(String var1, String var2, boolean var3, int var4) {
         return new CommonsCompressAction("bzip2", this.source(var1), this.target(var2), var3);
      }
   },
   DEFLATE(".deflate") {
      @Override
      Action createCompressAction(String var1, String var2, boolean var3, int var4) {
         return new CommonsCompressAction("deflate", this.source(var1), this.target(var2), var3);
      }
   },
   PACK200(".pack200") {
      @Override
      Action createCompressAction(String var1, String var2, boolean var3, int var4) {
         return new CommonsCompressAction("pack200", this.source(var1), this.target(var2), var3);
      }
   },
   XZ(".xz") {
      @Override
      Action createCompressAction(String var1, String var2, boolean var3, int var4) {
         return new CommonsCompressAction("xz", this.source(var1), this.target(var2), var3);
      }
   };

   private final String extension;

   public static FileExtension lookup(String var0) {
      for (FileExtension var4 : values()) {
         if (var4.isExtensionFor(var0)) {
            return var4;
         }
      }

      return null;
   }

   public static FileExtension lookupForFile(String var0) {
      for (FileExtension var4 : values()) {
         if (var0.endsWith(var4.extension)) {
            return var4;
         }
      }

      return null;
   }

   private FileExtension(String var3) {
      Objects.requireNonNull(var3, "extension");
      this.extension = var3;
   }

   abstract Action createCompressAction(String var1, String var2, boolean var3, int var4);

   String getExtension() {
      return this.extension;
   }

   boolean isExtensionFor(String var1) {
      return var1.endsWith(this.extension);
   }

   int length() {
      return this.extension.length();
   }

   File source(String var1) {
      return new File(var1);
   }

   File target(String var1) {
      return new File(var1);
   }
}
