package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Empty extends GeneratedMessageV3 implements EmptyOrBuilder {
   private static final long serialVersionUID = 0L;
   private byte memoizedIsInitialized = -1;
   private static final Empty DEFAULT_INSTANCE = new Empty();
   private static final Parser<Empty> PARSER = new AbstractParser<Empty>() {
      public Empty parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new Empty(var1, var2);
      }
   };

   private Empty(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private Empty() {
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new Empty();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Empty(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      this();
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         UnknownFieldSet.Builder var3 = UnknownFieldSet.newBuilder();

         try {
            boolean var4 = false;

            while (!var4) {
               int var5 = var1.readTag();
               switch (var5) {
                  case 0:
                     var4 = true;
                     break;
                  default:
                     if (!this.parseUnknownField(var1, var3, var2, var5)) {
                        var4 = true;
                     }
               }
            }
         } catch (InvalidProtocolBufferException var10) {
            throw var10.setUnfinishedMessage(this);
         } catch (IOException var11) {
            throw new InvalidProtocolBufferException(var11).setUnfinishedMessage(this);
         } finally {
            this.unknownFields = var3.build();
            this.makeExtensionsImmutable();
         }
      }
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return EmptyProto.internal_static_google_protobuf_Empty_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return EmptyProto.internal_static_google_protobuf_Empty_fieldAccessorTable.ensureFieldAccessorsInitialized(Empty.class, Empty.Builder.class);
   }

   @Override
   public final boolean isInitialized() {
      byte var1 = this.memoizedIsInitialized;
      if (var1 == 1) {
         return true;
      } else if (var1 == 0) {
         return false;
      } else {
         this.memoizedIsInitialized = 1;
         return true;
      }
   }

   @Override
   public void writeTo(CodedOutputStream var1) throws IOException {
      this.unknownFields.writeTo(var1);
   }

   @Override
   public int getSerializedSize() {
      int var1 = this.memoizedSize;
      if (var1 != -1) {
         return var1;
      } else {
         int var2 = 0;
         var2 += this.unknownFields.getSerializedSize();
         this.memoizedSize = var2;
         return var2;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Empty)) {
         return super.equals(var1);
      } else {
         Empty var2 = (Empty)var1;
         return this.unknownFields.equals(var2.unknownFields);
      }
   }

   @Override
   public int hashCode() {
      if (this.memoizedHashCode != 0) {
         return this.memoizedHashCode;
      } else {
         int var1 = 41;
         var1 = 19 * var1 + getDescriptor().hashCode();
         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static Empty parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Empty parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Empty parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Empty parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Empty parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Empty parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Empty parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Empty parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static Empty parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static Empty parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static Empty parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Empty parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public Empty.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Empty.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Empty.Builder newBuilder(Empty var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public Empty.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Empty.Builder() : new Empty.Builder().mergeFrom(this);
   }

   protected Empty.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new Empty.Builder(var1);
   }

   public static Empty getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Empty> parser() {
      return PARSER;
   }

   @Override
   public Parser<Empty> getParserForType() {
      return PARSER;
   }

   public Empty getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Empty.Builder> implements EmptyOrBuilder {
      public static final Descriptors.Descriptor getDescriptor() {
         return EmptyProto.internal_static_google_protobuf_Empty_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return EmptyProto.internal_static_google_protobuf_Empty_fieldAccessorTable.ensureFieldAccessorsInitialized(Empty.class, Empty.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(GeneratedMessageV3.BuilderParent var1) {
         super(var1);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (GeneratedMessageV3.alwaysUseFieldBuilders) {
         }
      }

      public Empty.Builder clear() {
         super.clear();
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return EmptyProto.internal_static_google_protobuf_Empty_descriptor;
      }

      public Empty getDefaultInstanceForType() {
         return Empty.getDefaultInstance();
      }

      public Empty build() {
         Empty var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public Empty buildPartial() {
         Empty var1 = new Empty(this);
         this.onBuilt();
         return var1;
      }

      public Empty.Builder clone() {
         return (Empty.Builder)super.clone();
      }

      public Empty.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Empty.Builder)super.setField(var1, var2);
      }

      public Empty.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (Empty.Builder)super.clearField(var1);
      }

      public Empty.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (Empty.Builder)super.clearOneof(var1);
      }

      public Empty.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (Empty.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public Empty.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Empty.Builder)super.addRepeatedField(var1, var2);
      }

      public Empty.Builder mergeFrom(Message var1) {
         if (var1 instanceof Empty) {
            return this.mergeFrom((Empty)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public Empty.Builder mergeFrom(Empty var1) {
         if (var1 == Empty.getDefaultInstance()) {
            return this;
         } else {
            this.mergeUnknownFields(var1.unknownFields);
            this.onChanged();
            return this;
         }
      }

      @Override
      public final boolean isInitialized() {
         return true;
      }

      public Empty.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         Empty var3 = null;

         try {
            var3 = Empty.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (Empty)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      public final Empty.Builder setUnknownFields(UnknownFieldSet var1) {
         return (Empty.Builder)super.setUnknownFields(var1);
      }

      public final Empty.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (Empty.Builder)super.mergeUnknownFields(var1);
      }
   }
}
