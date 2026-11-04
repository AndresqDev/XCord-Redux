package com.google.protobuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExtensionRegistry extends ExtensionRegistryLite {
   private final Map<String, ExtensionRegistry.ExtensionInfo> immutableExtensionsByName;
   private final Map<String, ExtensionRegistry.ExtensionInfo> mutableExtensionsByName;
   private final Map<ExtensionRegistry.DescriptorIntPair, ExtensionRegistry.ExtensionInfo> immutableExtensionsByNumber;
   private final Map<ExtensionRegistry.DescriptorIntPair, ExtensionRegistry.ExtensionInfo> mutableExtensionsByNumber;
   static final ExtensionRegistry EMPTY_REGISTRY = new ExtensionRegistry(true);

   public static ExtensionRegistry newInstance() {
      return new ExtensionRegistry();
   }

   public static ExtensionRegistry getEmptyRegistry() {
      return EMPTY_REGISTRY;
   }

   public ExtensionRegistry getUnmodifiable() {
      return new ExtensionRegistry(this);
   }

   @Deprecated
   public ExtensionRegistry.ExtensionInfo findExtensionByName(String var1) {
      return this.findImmutableExtensionByName(var1);
   }

   public ExtensionRegistry.ExtensionInfo findImmutableExtensionByName(String var1) {
      return this.immutableExtensionsByName.get(var1);
   }

   public ExtensionRegistry.ExtensionInfo findMutableExtensionByName(String var1) {
      return this.mutableExtensionsByName.get(var1);
   }

   @Deprecated
   public ExtensionRegistry.ExtensionInfo findExtensionByNumber(Descriptors.Descriptor var1, int var2) {
      return this.findImmutableExtensionByNumber(var1, var2);
   }

   public ExtensionRegistry.ExtensionInfo findImmutableExtensionByNumber(Descriptors.Descriptor var1, int var2) {
      return this.immutableExtensionsByNumber.get(new ExtensionRegistry.DescriptorIntPair(var1, var2));
   }

   public ExtensionRegistry.ExtensionInfo findMutableExtensionByNumber(Descriptors.Descriptor var1, int var2) {
      return this.mutableExtensionsByNumber.get(new ExtensionRegistry.DescriptorIntPair(var1, var2));
   }

   public Set<ExtensionRegistry.ExtensionInfo> getAllMutableExtensionsByExtendedType(String var1) {
      HashSet var2 = new HashSet();

      for (ExtensionRegistry.DescriptorIntPair var4 : this.mutableExtensionsByNumber.keySet()) {
         if (var4.descriptor.getFullName().equals(var1)) {
            var2.add(this.mutableExtensionsByNumber.get(var4));
         }
      }

      return var2;
   }

   public Set<ExtensionRegistry.ExtensionInfo> getAllImmutableExtensionsByExtendedType(String var1) {
      HashSet var2 = new HashSet();

      for (ExtensionRegistry.DescriptorIntPair var4 : this.immutableExtensionsByNumber.keySet()) {
         if (var4.descriptor.getFullName().equals(var1)) {
            var2.add(this.immutableExtensionsByNumber.get(var4));
         }
      }

      return var2;
   }

   public void add(Extension<?, ?> var1) {
      if (var1.getExtensionType() == Extension.ExtensionType.IMMUTABLE || var1.getExtensionType() == Extension.ExtensionType.MUTABLE) {
         this.add(newExtensionInfo(var1), var1.getExtensionType());
      }
   }

   public void add(GeneratedMessage.GeneratedExtension<?, ?> var1) {
      this.add(var1);
   }

   static ExtensionRegistry.ExtensionInfo newExtensionInfo(Extension<?, ?> var0) {
      if (var0.getDescriptor().getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
         if (var0.getMessageDefaultInstance() == null) {
            throw new IllegalStateException("Registered message-type extension had null default instance: " + var0.getDescriptor().getFullName());
         } else {
            return new ExtensionRegistry.ExtensionInfo(var0.getDescriptor(), var0.getMessageDefaultInstance());
         }
      } else {
         return new ExtensionRegistry.ExtensionInfo(var0.getDescriptor(), null);
      }
   }

   public void add(Descriptors.FieldDescriptor var1) {
      if (var1.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
         throw new IllegalArgumentException("ExtensionRegistry.add() must be provided a default instance when adding an embedded message extension.");
      } else {
         ExtensionRegistry.ExtensionInfo var2 = new ExtensionRegistry.ExtensionInfo(var1, null);
         this.add(var2, Extension.ExtensionType.IMMUTABLE);
         this.add(var2, Extension.ExtensionType.MUTABLE);
      }
   }

   public void add(Descriptors.FieldDescriptor var1, Message var2) {
      if (var1.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
         throw new IllegalArgumentException("ExtensionRegistry.add() provided a default instance for a non-message extension.");
      } else {
         this.add(new ExtensionRegistry.ExtensionInfo(var1, var2), Extension.ExtensionType.IMMUTABLE);
      }
   }

   private ExtensionRegistry() {
      this.immutableExtensionsByName = new HashMap<>();
      this.mutableExtensionsByName = new HashMap<>();
      this.immutableExtensionsByNumber = new HashMap<>();
      this.mutableExtensionsByNumber = new HashMap<>();
   }

   private ExtensionRegistry(ExtensionRegistry var1) {
      super(var1);
      this.immutableExtensionsByName = Collections.unmodifiableMap(var1.immutableExtensionsByName);
      this.mutableExtensionsByName = Collections.unmodifiableMap(var1.mutableExtensionsByName);
      this.immutableExtensionsByNumber = Collections.unmodifiableMap(var1.immutableExtensionsByNumber);
      this.mutableExtensionsByNumber = Collections.unmodifiableMap(var1.mutableExtensionsByNumber);
   }

   ExtensionRegistry(boolean var1) {
      super(EMPTY_REGISTRY_LITE);
      this.immutableExtensionsByName = Collections.emptyMap();
      this.mutableExtensionsByName = Collections.emptyMap();
      this.immutableExtensionsByNumber = Collections.emptyMap();
      this.mutableExtensionsByNumber = Collections.emptyMap();
   }

   private void add(ExtensionRegistry.ExtensionInfo var1, Extension.ExtensionType var2) {
      if (!var1.descriptor.isExtension()) {
         throw new IllegalArgumentException("ExtensionRegistry.add() was given a FieldDescriptor for a regular (non-extension) field.");
      } else {
         Map var3;
         Map var4;
         switch (var2) {
            case IMMUTABLE:
               var3 = this.immutableExtensionsByName;
               var4 = this.immutableExtensionsByNumber;
               break;
            case MUTABLE:
               var3 = this.mutableExtensionsByName;
               var4 = this.mutableExtensionsByNumber;
               break;
            default:
               return;
         }

         var3.put(var1.descriptor.getFullName(), var1);
         var4.put(new ExtensionRegistry.DescriptorIntPair(var1.descriptor.getContainingType(), var1.descriptor.getNumber()), var1);
         Descriptors.FieldDescriptor var5 = var1.descriptor;
         if (var5.getContainingType().getOptions().getMessageSetWireFormat()
            && var5.getType() == Descriptors.FieldDescriptor.Type.MESSAGE
            && var5.isOptional()
            && var5.getExtensionScope() == var5.getMessageType()) {
            var3.put(var5.getMessageType().getFullName(), var1);
         }
      }
   }

   private static final class DescriptorIntPair {
      private final Descriptors.Descriptor descriptor;
      private final int number;

      DescriptorIntPair(Descriptors.Descriptor var1, int var2) {
         this.descriptor = var1;
         this.number = var2;
      }

      @Override
      public int hashCode() {
         return this.descriptor.hashCode() * 65535 + this.number;
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof ExtensionRegistry.DescriptorIntPair)) {
            return false;
         } else {
            ExtensionRegistry.DescriptorIntPair var2 = (ExtensionRegistry.DescriptorIntPair)var1;
            return this.descriptor == var2.descriptor && this.number == var2.number;
         }
      }
   }

   public static final class ExtensionInfo {
      public final Descriptors.FieldDescriptor descriptor;
      public final Message defaultInstance;

      private ExtensionInfo(Descriptors.FieldDescriptor var1) {
         this.descriptor = var1;
         this.defaultInstance = null;
      }

      private ExtensionInfo(Descriptors.FieldDescriptor var1, Message var2) {
         this.descriptor = var1;
         this.defaultInstance = var2;
      }
   }
}
