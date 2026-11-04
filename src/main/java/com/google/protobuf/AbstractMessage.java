package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractMessage extends AbstractMessageLite implements Message {
   protected int memoizedSize = -1;

   @Override
   public boolean isInitialized() {
      return MessageReflection.isInitialized(this);
   }

   protected Message.Builder newBuilderForType(AbstractMessage.BuilderParent var1) {
      throw new UnsupportedOperationException("Nested builder is not supported for this type.");
   }

   @Override
   public List<String> findInitializationErrors() {
      return MessageReflection.findMissingFields(this);
   }

   @Override
   public String getInitializationErrorString() {
      return MessageReflection.delimitWithCommas(this.findInitializationErrors());
   }

   @Override
   public boolean hasOneof(Descriptors.OneofDescriptor var1) {
      throw new UnsupportedOperationException("hasOneof() is not implemented.");
   }

   @Override
   public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor var1) {
      throw new UnsupportedOperationException("getOneofFieldDescriptor() is not implemented.");
   }

   @Override
   public final String toString() {
      return TextFormat.printer().printToString(this);
   }

   @Override
   public void writeTo(CodedOutputStream var1) throws IOException {
      MessageReflection.writeMessageTo(this, this.getAllFields(), var1, false);
   }

   @Override
   int getMemoizedSerializedSize() {
      return this.memoizedSize;
   }

   @Override
   void setMemoizedSerializedSize(int var1) {
      this.memoizedSize = var1;
   }

   @Override
   public int getSerializedSize() {
      int var1 = this.memoizedSize;
      if (var1 != -1) {
         return var1;
      } else {
         this.memoizedSize = MessageReflection.getSerializedSize(this, this.getAllFields());
         return this.memoizedSize;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Message)) {
         return false;
      } else {
         Message var2 = (Message)var1;
         return this.getDescriptorForType() != var2.getDescriptorForType()
            ? false
            : compareFields(this.getAllFields(), var2.getAllFields()) && this.getUnknownFields().equals(var2.getUnknownFields());
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.memoizedHashCode;
      if (var1 == 0) {
         byte var2 = 41;
         int var3 = 19 * var2 + this.getDescriptorForType().hashCode();
         int var4 = hashFields(var3, this.getAllFields());
         var1 = 29 * var4 + this.getUnknownFields().hashCode();
         this.memoizedHashCode = var1;
      }

      return var1;
   }

   private static ByteString toByteString(Object var0) {
      return var0 instanceof byte[] ? ByteString.copyFrom((byte[])var0) : (ByteString)var0;
   }

   private static boolean compareBytes(Object var0, Object var1) {
      return var0 instanceof byte[] && var1 instanceof byte[] ? Arrays.equals((byte[])var0, (byte[])var1) : toByteString(var0).equals(toByteString(var1));
   }

   private static Map convertMapEntryListToMap(List var0) {
      if (var0.isEmpty()) {
         return Collections.emptyMap();
      } else {
         HashMap var1 = new HashMap();
         Iterator var2 = var0.iterator();
         Message var3 = (Message)var2.next();
         Descriptors.Descriptor var4 = var3.getDescriptorForType();
         Descriptors.FieldDescriptor var5 = var4.findFieldByName("key");
         Descriptors.FieldDescriptor var6 = var4.findFieldByName("value");
         Object var7 = var3.getField(var6);
         if (var7 instanceof Descriptors.EnumValueDescriptor) {
            var7 = ((Descriptors.EnumValueDescriptor)var7).getNumber();
         }

         var1.put(var3.getField(var5), var7);

         while (var2.hasNext()) {
            var3 = (Message)var2.next();
            var7 = var3.getField(var6);
            if (var7 instanceof Descriptors.EnumValueDescriptor) {
               var7 = ((Descriptors.EnumValueDescriptor)var7).getNumber();
            }

            var1.put(var3.getField(var5), var7);
         }

         return var1;
      }
   }

   private static boolean compareMapField(Object var0, Object var1) {
      Map var2 = convertMapEntryListToMap((List)var0);
      Map var3 = convertMapEntryListToMap((List)var1);
      return MapFieldLite.equals(var2, var3);
   }

   static boolean compareFields(Map<Descriptors.FieldDescriptor, Object> var0, Map<Descriptors.FieldDescriptor, Object> var1) {
      if (var0.size() != var1.size()) {
         return false;
      } else {
         for (Descriptors.FieldDescriptor var3 : var0.keySet()) {
            if (!var1.containsKey(var3)) {
               return false;
            }

            Object var4 = var0.get(var3);
            Object var5 = var1.get(var3);
            if (var3.getType() == Descriptors.FieldDescriptor.Type.BYTES) {
               if (var3.isRepeated()) {
                  List var6 = (List)var4;
                  List var7 = (List)var5;
                  if (var6.size() != var7.size()) {
                     return false;
                  }

                  for (int var8 = 0; var8 < var6.size(); var8++) {
                     if (!compareBytes(var6.get(var8), var7.get(var8))) {
                        return false;
                     }
                  }
               } else if (!compareBytes(var4, var5)) {
                  return false;
               }
            } else if (var3.isMapField()) {
               if (!compareMapField(var4, var5)) {
                  return false;
               }
            } else if (!var4.equals(var5)) {
               return false;
            }
         }

         return true;
      }
   }

   private static int hashMapField(Object var0) {
      return MapFieldLite.calculateHashCodeForMap(convertMapEntryListToMap((List)var0));
   }

   protected static int hashFields(int var0, Map<Descriptors.FieldDescriptor, Object> var1) {
      for (Entry var3 : var1.entrySet()) {
         Descriptors.FieldDescriptor var4 = (Descriptors.FieldDescriptor)var3.getKey();
         Object var5 = var3.getValue();
         var0 = 37 * var0 + var4.getNumber();
         if (var4.isMapField()) {
            var0 = 53 * var0 + hashMapField(var5);
         } else if (var4.getType() != Descriptors.FieldDescriptor.Type.ENUM) {
            var0 = 53 * var0 + var5.hashCode();
         } else if (var4.isRepeated()) {
            List var6 = (List)var5;
            var0 = 53 * var0 + Internal.hashEnumList(var6);
         } else {
            var0 = 53 * var0 + Internal.hashEnum((Internal.EnumLite)var5);
         }
      }

      return var0;
   }

   @Override
   UninitializedMessageException newUninitializedMessageException() {
      return AbstractMessage.Builder.newUninitializedMessageException(this);
   }

   @Deprecated
   protected static int hashLong(long var0) {
      return (int)(var0 ^ var0 >>> 32);
   }

   @Deprecated
   protected static int hashBoolean(boolean var0) {
      return var0 ? 1231 : 1237;
   }

   @Deprecated
   protected static int hashEnum(Internal.EnumLite var0) {
      return var0.getNumber();
   }

   @Deprecated
   protected static int hashEnumList(List<? extends Internal.EnumLite> var0) {
      int var1 = 1;

      for (Internal.EnumLite var3 : var0) {
         var1 = 31 * var1 + hashEnum(var3);
      }

      return var1;
   }

   public abstract static class Builder<BuilderType extends AbstractMessage.Builder<BuilderType>>
      extends AbstractMessageLite.Builder
      implements Message.Builder {
      public BuilderType clone() {
         throw new UnsupportedOperationException("clone() should be implemented in subclasses.");
      }

      @Override
      public boolean hasOneof(Descriptors.OneofDescriptor var1) {
         throw new UnsupportedOperationException("hasOneof() is not implemented.");
      }

      @Override
      public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor var1) {
         throw new UnsupportedOperationException("getOneofFieldDescriptor() is not implemented.");
      }

      public BuilderType clearOneof(Descriptors.OneofDescriptor var1) {
         throw new UnsupportedOperationException("clearOneof() is not implemented.");
      }

      public BuilderType clear() {
         for (Entry var2 : this.getAllFields().entrySet()) {
            this.clearField((Descriptors.FieldDescriptor)var2.getKey());
         }

         return (BuilderType)this;
      }

      @Override
      public List<String> findInitializationErrors() {
         return MessageReflection.findMissingFields(this);
      }

      @Override
      public String getInitializationErrorString() {
         return MessageReflection.delimitWithCommas(this.findInitializationErrors());
      }

      protected BuilderType internalMergeFrom(AbstractMessageLite var1) {
         return this.mergeFrom((Message)var1);
      }

      public BuilderType mergeFrom(Message var1) {
         return this.mergeFrom(var1, var1.getAllFields());
      }

      BuilderType mergeFrom(Message var1, Map<Descriptors.FieldDescriptor, Object> var2) {
         if (var1.getDescriptorForType() != this.getDescriptorForType()) {
            throw new IllegalArgumentException("mergeFrom(Message) can only merge messages of the same type.");
         } else {
            for (Entry var4 : var2.entrySet()) {
               Descriptors.FieldDescriptor var5 = (Descriptors.FieldDescriptor)var4.getKey();
               if (var5.isRepeated()) {
                  for (Object var7 : (List)var4.getValue()) {
                     this.addRepeatedField(var5, var7);
                  }
               } else if (var5.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                  Message var6 = (Message)this.getField(var5);
                  if (var6 == var6.getDefaultInstanceForType()) {
                     this.setField(var5, var4.getValue());
                  } else {
                     this.setField(var5, var6.newBuilderForType().mergeFrom(var6).mergeFrom((Message)var4.getValue()).build());
                  }
               } else {
                  this.setField(var5, var4.getValue());
               }
            }

            this.mergeUnknownFields(var1.getUnknownFields());
            return (BuilderType)this;
         }
      }

      public BuilderType mergeFrom(CodedInputStream var1) throws IOException {
         return this.mergeFrom(var1, ExtensionRegistry.getEmptyRegistry());
      }

      public BuilderType mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         boolean var3 = var1.shouldDiscardUnknownFields();
         UnknownFieldSet.Builder var4 = var3 ? null : UnknownFieldSet.newBuilder(this.getUnknownFields());

         int var5;
         MessageReflection.BuilderAdapter var6;
         do {
            var5 = var1.readTag();
            if (var5 == 0) {
               break;
            }

            var6 = new MessageReflection.BuilderAdapter(this);
         } while (MessageReflection.mergeFieldFrom(var1, var4, var2, this.getDescriptorForType(), var6, var5));

         if (var4 != null) {
            this.setUnknownFields(var4.build());
         }

         return (BuilderType)this;
      }

      public BuilderType mergeUnknownFields(UnknownFieldSet var1) {
         this.setUnknownFields(UnknownFieldSet.newBuilder(this.getUnknownFields()).mergeFrom(var1).build());
         return (BuilderType)this;
      }

      @Override
      public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor var1) {
         throw new UnsupportedOperationException("getFieldBuilder() called on an unsupported message type.");
      }

      @Override
      public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor var1, int var2) {
         throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on an unsupported message type.");
      }

      @Override
      public String toString() {
         return TextFormat.printer().printToString(this);
      }

      protected static UninitializedMessageException newUninitializedMessageException(Message var0) {
         return new UninitializedMessageException(MessageReflection.findMissingFields(var0));
      }

      void markClean() {
         throw new IllegalStateException("Should be overridden by subclasses.");
      }

      void dispose() {
         throw new IllegalStateException("Should be overridden by subclasses.");
      }

      public BuilderType mergeFrom(ByteString var1) throws InvalidProtocolBufferException {
         return super.mergeFrom(var1);
      }

      public BuilderType mergeFrom(ByteString var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return super.mergeFrom(var1, var2);
      }

      public BuilderType mergeFrom(byte[] var1) throws InvalidProtocolBufferException {
         return super.mergeFrom(var1);
      }

      public BuilderType mergeFrom(byte[] var1, int var2, int var3) throws InvalidProtocolBufferException {
         return super.mergeFrom(var1, var2, var3);
      }

      public BuilderType mergeFrom(byte[] var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return super.mergeFrom(var1, var2);
      }

      public BuilderType mergeFrom(byte[] var1, int var2, int var3, ExtensionRegistryLite var4) throws InvalidProtocolBufferException {
         return super.mergeFrom(var1, var2, var3, var4);
      }

      public BuilderType mergeFrom(InputStream var1) throws IOException {
         return super.mergeFrom(var1);
      }

      public BuilderType mergeFrom(InputStream var1, ExtensionRegistryLite var2) throws IOException {
         return super.mergeFrom(var1, var2);
      }

      @Override
      public boolean mergeDelimitedFrom(InputStream var1) throws IOException {
         return super.mergeDelimitedFrom(var1);
      }

      @Override
      public boolean mergeDelimitedFrom(InputStream var1, ExtensionRegistryLite var2) throws IOException {
         return super.mergeDelimitedFrom(var1, var2);
      }
   }

   protected interface BuilderParent {
      void markDirty();
   }
}
