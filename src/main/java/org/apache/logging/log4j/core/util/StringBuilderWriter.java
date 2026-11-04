package org.apache.logging.log4j.core.util;

import java.io.Serializable;
import java.io.Writer;

public class StringBuilderWriter extends Writer implements Serializable {
   private static final long serialVersionUID = -146927496096066153L;
   private final StringBuilder builder;

   public StringBuilderWriter() {
      this.builder = new StringBuilder();
   }

   public StringBuilderWriter(int var1) {
      this.builder = new StringBuilder(var1);
   }

   public StringBuilderWriter(StringBuilder var1) {
      this.builder = var1 != null ? var1 : new StringBuilder();
   }

   @Override
   public Writer append(char var1) {
      this.builder.append(var1);
      return this;
   }

   @Override
   public Writer append(CharSequence var1) {
      this.builder.append(var1);
      return this;
   }

   @Override
   public Writer append(CharSequence var1, int var2, int var3) {
      this.builder.append(var1, var2, var3);
      return this;
   }

   @Override
   public void close() {
   }

   @Override
   public void flush() {
   }

   @Override
   public void write(String var1) {
      if (var1 != null) {
         this.builder.append(var1);
      }
   }

   @Override
   public void write(char[] var1, int var2, int var3) {
      if (var1 != null) {
         this.builder.append(var1, var2, var3);
      }
   }

   public StringBuilder getBuilder() {
      return this.builder;
   }

   @Override
   public String toString() {
      return this.builder.toString();
   }
}
