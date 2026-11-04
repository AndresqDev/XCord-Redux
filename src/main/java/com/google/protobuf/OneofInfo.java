package com.google.protobuf;

final class OneofInfo {
   private final int id;
   private final java.lang.reflect.Field caseField;
   private final java.lang.reflect.Field valueField;

   public OneofInfo(int var1, java.lang.reflect.Field var2, java.lang.reflect.Field var3) {
      this.id = var1;
      this.caseField = var2;
      this.valueField = var3;
   }

   public int getId() {
      return this.id;
   }

   public java.lang.reflect.Field getCaseField() {
      return this.caseField;
   }

   public java.lang.reflect.Field getValueField() {
      return this.valueField;
   }
}
