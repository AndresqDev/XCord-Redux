package com.google.protobuf;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class Protobuf {
   private static final Protobuf INSTANCE = new Protobuf();
   private final SchemaFactory schemaFactory;
   private final ConcurrentMap<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

   public static Protobuf getInstance() {
      return INSTANCE;
   }

   public <T> void writeTo(T var1, Writer var2) throws IOException {
      this.schemaFor(var1).writeTo(var1, var2);
   }

   public <T> void mergeFrom(T var1, Reader var2) throws IOException {
      this.mergeFrom(var1, var2, ExtensionRegistryLite.getEmptyRegistry());
   }

   public <T> void mergeFrom(T var1, Reader var2, ExtensionRegistryLite var3) throws IOException {
      this.schemaFor(var1).mergeFrom(var1, var2, var3);
   }

   public <T> void makeImmutable(T var1) {
      this.schemaFor(var1).makeImmutable(var1);
   }

   public <T> boolean isInitialized(T var1) {
      return this.schemaFor(var1).isInitialized(var1);
   }

   public <T> Schema<T> schemaFor(Class<T> var1) {
      Internal.checkNotNull(var1, "messageType");
      Schema var2 = this.schemaCache.get(var1);
      if (var2 == null) {
         var2 = this.schemaFactory.createSchema(var1);
         Schema var3 = this.registerSchema(var1, var2);
         if (var3 != null) {
            var2 = var3;
         }
      }

      return var2;
   }

   public <T> Schema<T> schemaFor(T var1) {
      return this.schemaFor((Class<T>)var1.getClass());
   }

   public Schema<?> registerSchema(Class<?> var1, Schema<?> var2) {
      Internal.checkNotNull(var1, "messageType");
      Internal.checkNotNull(var2, "schema");
      return this.schemaCache.putIfAbsent(var1, var2);
   }

   public Schema<?> registerSchemaOverride(Class<?> var1, Schema<?> var2) {
      Internal.checkNotNull(var1, "messageType");
      Internal.checkNotNull(var2, "schema");
      return this.schemaCache.put(var1, var2);
   }

   private Protobuf() {
      this.schemaFactory = new ManifestSchemaFactory();
   }

   int getTotalSchemaSize() {
      int var1 = 0;

      for (Schema var3 : this.schemaCache.values()) {
         if (var3 instanceof MessageSchema) {
            var1 += ((MessageSchema)var3).getSchemaSize();
         }
      }

      return var1;
   }
}
