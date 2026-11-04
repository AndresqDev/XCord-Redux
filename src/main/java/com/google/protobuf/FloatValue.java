package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class FloatValue extends GeneratedMessageV3 implements FloatValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUE_FIELD_NUMBER = 1;
   private float value_;
   private byte memoizedIsInitialized = -1;
   private static final FloatValue DEFAULT_INSTANCE = new FloatValue();
   private static final Parser<FloatValue> PARSER = new AbstractParser<FloatValue>() {
      public FloatValue parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new FloatValue(var1, var2);
      }
   };

   private FloatValue(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private FloatValue() {
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new FloatValue();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private FloatValue(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                  case 13:
                     this.value_ = var1.readFloat();
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
      return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_FloatValue_fieldAccessorTable
         .ensureFieldAccessorsInitialized(FloatValue.class, FloatValue.Builder.class);
   }

   @Override
   public float getValue() {
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
      if (this.value_ != 0.0F) {
         var1.writeFloat(1, this.value_);
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
         if (this.value_ != 0.0F) {
            var1 += CodedOutputStream.computeFloatSize(1, this.value_);
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
      } else if (!(var1 instanceof FloatValue)) {
         return super.equals(var1);
      } else {
         FloatValue var2 = (FloatValue)var1;
         return Float.floatToIntBits(this.getValue()) != Float.floatToIntBits(var2.getValue()) ? false : this.unknownFields.equals(var2.unknownFields);
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
         var1 = 53 * var1 + Float.floatToIntBits(this.getValue());
         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static FloatValue parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static FloatValue parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static FloatValue parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static FloatValue parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static FloatValue parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static FloatValue parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static FloatValue parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static FloatValue parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static FloatValue parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static FloatValue parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static FloatValue parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static FloatValue parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public FloatValue.Builder newBuilderForType() {
      return newBuilder();
   }

   public static FloatValue.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static FloatValue.Builder newBuilder(FloatValue var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public FloatValue.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new FloatValue.Builder() : new FloatValue.Builder().mergeFrom(this);
   }

   protected FloatValue.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new FloatValue.Builder(var1);
   }

   public static FloatValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static FloatValue of(float var0) {
      return newBuilder().setValue(var0).build();
   }

   public static Parser<FloatValue> parser() {
      return PARSER;
   }

   @Override
   public Parser<FloatValue> getParserForType() {
      return PARSER;
   }

   public FloatValue getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<FloatValue.Builder> implements FloatValueOrBuilder {
      private float value_;

      public static final Descriptors.Descriptor getDescriptor() {
         return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return WrappersProto.internal_static_google_protobuf_FloatValue_fieldAccessorTable
            .ensureFieldAccessorsInitialized(FloatValue.class, FloatValue.Builder.class);
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

      public FloatValue.Builder clear() {
         super.clear();
         this.value_ = 0.0F;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
      }

      public FloatValue getDefaultInstanceForType() {
         return FloatValue.getDefaultInstance();
      }

      public FloatValue build() {
         FloatValue var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public FloatValue buildPartial() {
         FloatValue var1 = new FloatValue(this);
         var1.value_ = this.value_;
         this.onBuilt();
         return var1;
      }

      public FloatValue.Builder clone() {
         return (FloatValue.Builder)super.clone();
      }

      public FloatValue.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (FloatValue.Builder)super.setField(var1, var2);
      }

      public FloatValue.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (FloatValue.Builder)super.clearField(var1);
      }

      public FloatValue.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (FloatValue.Builder)super.clearOneof(var1);
      }

      public FloatValue.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (FloatValue.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public FloatValue.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (FloatValue.Builder)super.addRepeatedField(var1, var2);
      }

      public FloatValue.Builder mergeFrom(Message var1) {
         if (var1 instanceof FloatValue) {
            return this.mergeFrom((FloatValue)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public FloatValue.Builder mergeFrom(FloatValue var1) {
         if (var1 == FloatValue.getDefaultInstance()) {
            return this;
         } else {
            if (var1.getValue() != 0.0F) {
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

      public FloatValue.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         FloatValue var3 = null;

         try {
            var3 = FloatValue.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (FloatValue)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      @Override
      public float getValue() {
         return this.value_;
      }

      public FloatValue.Builder setValue(float var1) {
         this.value_ = var1;
         this.onChanged();
         return this;
      }

      public FloatValue.Builder clearValue() {
         this.value_ = 0.0F;
         this.onChanged();
         return this;
      }

      public final FloatValue.Builder setUnknownFields(UnknownFieldSet var1) {
         return (FloatValue.Builder)super.setUnknownFields(var1);
      }

      public final FloatValue.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (FloatValue.Builder)super.mergeUnknownFields(var1);
      }
   }
}
