package com.google.protobuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class TypeRegistry {
   private static final Logger logger = Logger.getLogger(TypeRegistry.class.getName());
   private final Map<String, Descriptors.Descriptor> types;

   public static TypeRegistry getEmptyTypeRegistry() {
      return TypeRegistry.EmptyTypeRegistryHolder.EMPTY;
   }

   public static TypeRegistry.Builder newBuilder() {
      return new TypeRegistry.Builder();
   }

   public Descriptors.Descriptor find(String var1) {
      return this.types.get(var1);
   }

   public final Descriptors.Descriptor getDescriptorForTypeUrl(String var1) throws InvalidProtocolBufferException {
      return this.find(getTypeName(var1));
   }

   TypeRegistry(Map<String, Descriptors.Descriptor> var1) {
      this.types = var1;
   }

   private static String getTypeName(String var0) throws InvalidProtocolBufferException {
      String[] var1 = var0.split("/");
      if (var1.length == 1) {
         throw new InvalidProtocolBufferException("Invalid type url found: " + var0);
      } else {
         return var1[var1.length - 1];
      }
   }

   public static final class Builder {
      private final Set<String> files = new HashSet<>();
      private Map<String, Descriptors.Descriptor> types = new HashMap<>();

      private Builder() {
      }

      public TypeRegistry.Builder add(Descriptors.Descriptor var1) {
         if (this.types == null) {
            throw new IllegalStateException("A TypeRegistry.Builder can only be used once.");
         } else {
            this.addFile(var1.getFile());
            return this;
         }
      }

      public TypeRegistry.Builder add(Iterable<Descriptors.Descriptor> var1) {
         if (this.types == null) {
            throw new IllegalStateException("A TypeRegistry.Builder can only be used once.");
         } else {
            for (Descriptors.Descriptor var3 : var1) {
               this.addFile(var3.getFile());
            }

            return this;
         }
      }

      public TypeRegistry build() {
         TypeRegistry var1 = new TypeRegistry(this.types);
         this.types = null;
         return var1;
      }

      private void addFile(Descriptors.FileDescriptor var1) {
         if (this.files.add(var1.getFullName())) {
            for (Descriptors.FileDescriptor var3 : var1.getDependencies()) {
               this.addFile(var3);
            }

            for (Descriptors.Descriptor var5 : var1.getMessageTypes()) {
               this.addMessage(var5);
            }
         }
      }

      private void addMessage(Descriptors.Descriptor var1) {
         for (Descriptors.Descriptor var3 : var1.getNestedTypes()) {
            this.addMessage(var3);
         }

         if (this.types.containsKey(var1.getFullName())) {
            TypeRegistry.logger.warning("Type " + var1.getFullName() + " is added multiple times.");
         } else {
            this.types.put(var1.getFullName(), var1);
         }
      }
   }

   private static class EmptyTypeRegistryHolder {
      private static final TypeRegistry EMPTY = new TypeRegistry(Collections.emptyMap());
   }
}
