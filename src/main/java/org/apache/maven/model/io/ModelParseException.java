package org.apache.maven.model.io;

import java.io.IOException;

public class ModelParseException extends IOException {
   private final int lineNumber;
   private final int columnNumber;

   public ModelParseException(String var1, int var2, int var3) {
      super(var1);
      this.lineNumber = var2;
      this.columnNumber = var3;
   }

   public ModelParseException(String var1, int var2, int var3, Throwable var4) {
      super(var1);
      this.initCause(var4);
      this.lineNumber = var2;
      this.columnNumber = var3;
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   public int getColumnNumber() {
      return this.columnNumber;
   }
}
