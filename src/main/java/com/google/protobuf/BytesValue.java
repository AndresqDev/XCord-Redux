package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class BytesValue extends GeneratedMessageV3 implements BytesValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUE_FIELD_NUMBER = 1;
   private ByteString value_;
   private byte memoizedIsInitialized = -1;
   private static final BytesValue DEFAULT_INSTANCE = new BytesValue();
   private static final Parser<BytesValue> PARSER = new AbstractParser<BytesValue>() {
      public BytesValue parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new BytesValue(var1, var2);
      }
   };

   private BytesValue(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private BytesValue() {
      this.value_ = ByteString.EMPTY;
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new BytesValue();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private BytesValue(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                  case 10:
                     this.value_ = var1.readBytes();
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
      return WrappersProto.internal_static_google_protobuf_BytesValue_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_BytesValue_fieldAccessorTable
         .ensureFieldAccessorsInitialized(BytesValue.class, BytesValue.Builder.class);
   }

   @Override
   public ByteString getValue() {
      return this.value_;
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
      if (!this.value_.isEmpty()) {
         var1.writeBytes(1, this.value_);
      }

      this.unknownFields.writeTo(var1);
   }

   @Override
   public int getSerializedSize() {
      int var1 = this.memoizedSize;
      if (var1 != -1) {
         return var1;
      } else {
         var1 = 0;
         if (!this.value_.isEmpty()) {
            var1 += CodedOutputStream.computeBytesSize(1, this.value_);
         }

         var1 += this.unknownFields.getSerializedSize();
         this.memoizedSize = var1;
         return var1;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof BytesValue)) {
         return super.equals(var1);
      } else {
         BytesValue var2 = (BytesValue)var1;
         return !this.getValue().equals(var2.getValue()) ? false : this.unknownFields.equals(var2.unknownFields);
      }
   }

   @Override
   public int hashCode() {
      if (this.memoizedHashCode != 0) {
         return this.memoizedHashCode;
      } else {
         int var1 = 41;
         var1 = 19 * var1 + getDescriptor().hashCode();
         var1 = 37 * var1 + 1;
         var1 = 53 * var1 + this.getValue().hashCode();
         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static BytesValue parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static BytesValue parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static BytesValue parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static BytesValue parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static BytesValue parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static BytesValue parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static BytesValue parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static BytesValue parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static BytesValue parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static BytesValue parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static BytesValue parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static BytesValue parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public BytesValue.Builder newBuilderForType() {
      return newBuilder();
   }

   public static BytesValue.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static BytesValue.Builder newBuilder(BytesValue var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public BytesValue.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new BytesValue.Builder() : new BytesValue.Builder().mergeFrom(this);
   }

   protected BytesValue.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new BytesValue.Builder(var1);
   }

   public static BytesValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static BytesValue of(ByteString var0) {
      return newBuilder().setValue(var0).build();
   }

   public static Parser<BytesValue> parser() {
      return PARSER;
   }

   @Override
   public Parser<BytesValue> getParserForType() {
      return PARSER;
   }

   public BytesValue getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<BytesValue.Builder> implements BytesValueOrBuilder {
      private ByteString value_ = ByteString.EMPTY;

      public static final Descriptors.Descriptor getDescriptor() {
         return WrappersProto.internal_static_google_protobuf_BytesValue_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return WrappersProto.internal_static_google_protobuf_BytesValue_fieldAccessorTable
            .ensureFieldAccessorsInitialized(BytesValue.class, BytesValue.Builder.class);
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

      public BytesValue.Builder clear() {
         super.clear();
         this.value_ = ByteString.EMPTY;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return WrappersProto.internal_static_google_protobuf_BytesValue_descriptor;
      }

      public BytesValue getDefaultInstanceForType() {
         return BytesValue.getDefaultInstance();
      }

      public BytesValue build() {
         BytesValue var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public BytesValue buildPartial() {
         BytesValue var1 = new BytesValue(this);
         var1.value_ = this.value_;
         this.onBuilt();
         return var1;
      }

      public BytesValue.Builder clone() {
         return (BytesValue.Builder)super.clone();
      }

      public BytesValue.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (BytesValue.Builder)super.setField(var1, var2);
      }

      public BytesValue.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (BytesValue.Builder)super.clearField(var1);
      }

      public BytesValue.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (BytesValue.Builder)super.clearOneof(var1);
      }

      public BytesValue.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (BytesValue.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public BytesValue.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (BytesValue.Builder)super.addRepeatedField(var1, var2);
      }

      public BytesValue.Builder mergeFrom(Message var1) {
         if (var1 instanceof BytesValue) {
            return this.mergeFrom((BytesValue)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public BytesValue.Builder mergeFrom(BytesValue var1) {
         if (var1 == BytesValue.getDefaultInstance()) {
            return this;
         } else {
            if (var1.getValue() != ByteString.EMPTY) {
               this.setValue(var1.getValue());
            }

            this.mergeUnknownFields(var1.unknownFields);
            this.onChanged();
            return this;
         }
      }

      @Override
      public final boolean isInitialized() {
         return true;
      }

      public BytesValue.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         BytesValue var3 = null;

         try {
            var3 = BytesValue.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (BytesValue)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      @Override
      public ByteString getValue() {
         return this.value_;
      }

      public BytesValue.Builder setValue(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.value_ = var1;
            this.onChanged();
            return this;
         }
      }

      public BytesValue.Builder clearValue() {
         this.value_ = BytesValue.getDefaultInstance().getValue();
         this.onChanged();
         return this;
      }

      public final BytesValue.Builder setUnknownFields(UnknownFieldSet var1) {
         return (BytesValue.Builder)super.setUnknownFields(var1);
      }

      public final BytesValue.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (BytesValue.Builder)super.mergeUnknownFields(var1);
      }
   }
}
