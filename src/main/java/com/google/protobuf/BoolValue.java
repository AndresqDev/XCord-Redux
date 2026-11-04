package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class BoolValue extends GeneratedMessageV3 implements BoolValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUE_FIELD_NUMBER = 1;
   private boolean value_;
   private byte memoizedIsInitialized = -1;
   private static final BoolValue DEFAULT_INSTANCE = new BoolValue();
   private static final Parser<BoolValue> PARSER = new AbstractParser<BoolValue>() {
      public BoolValue parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new BoolValue(var1, var2);
      }
   };

   private BoolValue(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private BoolValue() {
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new BoolValue();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private BoolValue(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                  case 8:
                     this.value_ = var1.readBool();
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
      return WrappersProto.internal_static_google_protobuf_BoolValue_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_BoolValue_fieldAccessorTable
         .ensureFieldAccessorsInitialized(BoolValue.class, BoolValue.Builder.class);
   }

   @Override
   public boolean getValue() {
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
      if (this.value_) {
         var1.writeBool(1, this.value_);
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
         if (this.value_) {
            var1 += CodedOutputStream.computeBoolSize(1, this.value_);
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
      } else if (!(var1 instanceof BoolValue)) {
         return super.equals(var1);
      } else {
         BoolValue var2 = (BoolValue)var1;
         return this.getValue() != var2.getValue() ? false : this.unknownFields.equals(var2.unknownFields);
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
         var1 = 53 * var1 + Internal.hashBoolean(this.getValue());
         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static BoolValue parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static BoolValue parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static BoolValue parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static BoolValue parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static BoolValue parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static BoolValue parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static BoolValue parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static BoolValue parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static BoolValue parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static BoolValue parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static BoolValue parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static BoolValue parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public BoolValue.Builder newBuilderForType() {
      return newBuilder();
   }

   public static BoolValue.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static BoolValue.Builder newBuilder(BoolValue var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public BoolValue.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new BoolValue.Builder() : new BoolValue.Builder().mergeFrom(this);
   }

   protected BoolValue.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new BoolValue.Builder(var1);
   }

   public static BoolValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static BoolValue of(boolean var0) {
      return newBuilder().setValue(var0).build();
   }

   public static Parser<BoolValue> parser() {
      return PARSER;
   }

   @Override
   public Parser<BoolValue> getParserForType() {
      return PARSER;
   }

   public BoolValue getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<BoolValue.Builder> implements BoolValueOrBuilder {
      private boolean value_;

      public static final Descriptors.Descriptor getDescriptor() {
         return WrappersProto.internal_static_google_protobuf_BoolValue_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return WrappersProto.internal_static_google_protobuf_BoolValue_fieldAccessorTable
            .ensureFieldAccessorsInitialized(BoolValue.class, BoolValue.Builder.class);
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

      public BoolValue.Builder clear() {
         super.clear();
         this.value_ = false;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return WrappersProto.internal_static_google_protobuf_BoolValue_descriptor;
      }

      public BoolValue getDefaultInstanceForType() {
         return BoolValue.getDefaultInstance();
      }

      public BoolValue build() {
         BoolValue var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public BoolValue buildPartial() {
         BoolValue var1 = new BoolValue(this);
         var1.value_ = this.value_;
         this.onBuilt();
         return var1;
      }

      public BoolValue.Builder clone() {
         return (BoolValue.Builder)super.clone();
      }

      public BoolValue.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (BoolValue.Builder)super.setField(var1, var2);
      }

      public BoolValue.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (BoolValue.Builder)super.clearField(var1);
      }

      public BoolValue.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (BoolValue.Builder)super.clearOneof(var1);
      }

      public BoolValue.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (BoolValue.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public BoolValue.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (BoolValue.Builder)super.addRepeatedField(var1, var2);
      }

      public BoolValue.Builder mergeFrom(Message var1) {
         if (var1 instanceof BoolValue) {
            return this.mergeFrom((BoolValue)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public BoolValue.Builder mergeFrom(BoolValue var1) {
         if (var1 == BoolValue.getDefaultInstance()) {
            return this;
         } else {
            if (var1.getValue()) {
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

      public BoolValue.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         BoolValue var3 = null;

         try {
            var3 = BoolValue.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (BoolValue)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      @Override
      public boolean getValue() {
         return this.value_;
      }

      public BoolValue.Builder setValue(boolean var1) {
         this.value_ = var1;
         this.onChanged();
         return this;
      }

      public BoolValue.Builder clearValue() {
         this.value_ = false;
         this.onChanged();
         return this;
      }

      public final BoolValue.Builder setUnknownFields(UnknownFieldSet var1) {
         return (BoolValue.Builder)super.setUnknownFields(var1);
      }

      public final BoolValue.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (BoolValue.Builder)super.mergeUnknownFields(var1);
      }
   }
}
