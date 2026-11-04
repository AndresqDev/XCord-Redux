package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Int64Value extends GeneratedMessageV3 implements Int64ValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUE_FIELD_NUMBER = 1;
   private long value_;
   private byte memoizedIsInitialized = -1;
   private static final Int64Value DEFAULT_INSTANCE = new Int64Value();
   private static final Parser<Int64Value> PARSER = new AbstractParser<Int64Value>() {
      public Int64Value parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new Int64Value(var1, var2);
      }
   };

   private Int64Value(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private Int64Value() {
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new Int64Value();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Int64Value(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                     this.value_ = var1.readInt64();
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
      return WrappersProto.internal_static_google_protobuf_Int64Value_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_Int64Value_fieldAccessorTable
         .ensureFieldAccessorsInitialized(Int64Value.class, Int64Value.Builder.class);
   }

   @Override
   public long getValue() {
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
      if (this.value_ != 0L) {
         var1.writeInt64(1, this.value_);
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
         if (this.value_ != 0L) {
            var1 += CodedOutputStream.computeInt64Size(1, this.value_);
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
      } else if (!(var1 instanceof Int64Value)) {
         return super.equals(var1);
      } else {
         Int64Value var2 = (Int64Value)var1;
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
         var1 = 53 * var1 + Internal.hashLong(this.getValue());
         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static Int64Value parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Int64Value parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Int64Value parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Int64Value parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Int64Value parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Int64Value parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Int64Value parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Int64Value parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static Int64Value parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static Int64Value parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static Int64Value parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Int64Value parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public Int64Value.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Int64Value.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Int64Value.Builder newBuilder(Int64Value var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public Int64Value.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Int64Value.Builder() : new Int64Value.Builder().mergeFrom(this);
   }

   protected Int64Value.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new Int64Value.Builder(var1);
   }

   public static Int64Value getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Int64Value of(long var0) {
      return newBuilder().setValue(var0).build();
   }

   public static Parser<Int64Value> parser() {
      return PARSER;
   }

   @Override
   public Parser<Int64Value> getParserForType() {
      return PARSER;
   }

   public Int64Value getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Int64Value.Builder> implements Int64ValueOrBuilder {
      private long value_;

      public static final Descriptors.Descriptor getDescriptor() {
         return WrappersProto.internal_static_google_protobuf_Int64Value_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return WrappersProto.internal_static_google_protobuf_Int64Value_fieldAccessorTable
            .ensureFieldAccessorsInitialized(Int64Value.class, Int64Value.Builder.class);
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

      public Int64Value.Builder clear() {
         super.clear();
         this.value_ = 0L;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return WrappersProto.internal_static_google_protobuf_Int64Value_descriptor;
      }

      public Int64Value getDefaultInstanceForType() {
         return Int64Value.getDefaultInstance();
      }

      public Int64Value build() {
         Int64Value var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public Int64Value buildPartial() {
         Int64Value var1 = new Int64Value(this);
         var1.value_ = this.value_;
         this.onBuilt();
         return var1;
      }

      public Int64Value.Builder clone() {
         return (Int64Value.Builder)super.clone();
      }

      public Int64Value.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Int64Value.Builder)super.setField(var1, var2);
      }

      public Int64Value.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (Int64Value.Builder)super.clearField(var1);
      }

      public Int64Value.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (Int64Value.Builder)super.clearOneof(var1);
      }

      public Int64Value.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (Int64Value.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public Int64Value.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Int64Value.Builder)super.addRepeatedField(var1, var2);
      }

      public Int64Value.Builder mergeFrom(Message var1) {
         if (var1 instanceof Int64Value) {
            return this.mergeFrom((Int64Value)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public Int64Value.Builder mergeFrom(Int64Value var1) {
         if (var1 == Int64Value.getDefaultInstance()) {
            return this;
         } else {
            if (var1.getValue() != 0L) {
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

      public Int64Value.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         Int64Value var3 = null;

         try {
            var3 = Int64Value.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (Int64Value)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      @Override
      public long getValue() {
         return this.value_;
      }

      public Int64Value.Builder setValue(long var1) {
         this.value_ = var1;
         this.onChanged();
         return this;
      }

      public Int64Value.Builder clearValue() {
         this.value_ = 0L;
         this.onChanged();
         return this;
      }

      public final Int64Value.Builder setUnknownFields(UnknownFieldSet var1) {
         return (Int64Value.Builder)super.setUnknownFields(var1);
      }

      public final Int64Value.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (Int64Value.Builder)super.mergeUnknownFields(var1);
      }
   }
}
