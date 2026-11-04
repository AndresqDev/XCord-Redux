package com.google.protobuf;

final class NewInstanceSchemas {
   private static final NewInstanceSchema FULL_SCHEMA = loadSchemaForFullRuntime();
   private static final NewInstanceSchema LITE_SCHEMA = new NewInstanceSchemaLite();

   static NewInstanceSchema full() {
      return FULL_SCHEMA;
   }

   static NewInstanceSchema lite() {
      return LITE_SCHEMA;
   }

   private static NewInstanceSchema loadSchemaForFullRuntime() {
      try {
         Class var0 = Class.forName("com.google.protobuf.NewInstanceSchemaFull");
         return (NewInstanceSchema)var0.getDeclaredConstructor().newInstance();
      } catch (Exception var1) {
         return null;
      }
   }
}
