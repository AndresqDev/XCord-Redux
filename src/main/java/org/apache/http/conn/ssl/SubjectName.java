package org.apache.http.conn.ssl;

import org.apache.http.util.Args;

final class SubjectName {
   static final int DNS = 2;
   static final int IP = 7;
   private final String value;
   private final int type;

   static SubjectName IP(String var0) {
      return new SubjectName(var0, 7);
   }

   static SubjectName DNS(String var0) {
      return new SubjectName(var0, 2);
   }

   SubjectName(String var1, int var2) {
      this.value = Args.notNull(var1, "Value");
      this.type = Args.positive(var2, "Type");
   }

   public int getType() {
      return this.type;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return this.value;
   }
}
