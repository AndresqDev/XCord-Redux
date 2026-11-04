package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class StringValue extends GeneratedMessageV3 implements StringValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUE_FIELD_NUMBER = 1;
   private volatile Object value_;
   private byte memoizedIsInitialized = -1;
   private static final StringValue DEFAULT_INSTANCE = new StringValue();
   private static final Parser<StringValue> PARSER = new AbstractParser<StringValue>() {
      public StringValue parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new StringValue(var1, var2);
      }
   };

   private StringValue(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private StringValue() {
      this.value_ = "";
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new StringValue();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private StringValue(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                     String var6 = var1.readStringRequireUtf8();
                     this.value_ = var6;
                     break;
                  default:
                     if (!this.parseUnknownField(var1, var3, var2, var5)) {
                        var4 = true;
                     }
               }
            }
         } catch (InvalidProtocolBufferException var11) {
            throw var11.setUnfinishedMessage(this);
         } catch (IOException var12) {
            throw new InvalidProtocolBufferException(var12).setUnfinishedMessage(this);
         } finally {
            this.unknownFields = var3.build();
            this.makeExtensionsImmutable();
         }
      }
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return WrappersProto.internal_static_google_protobuf_StringValue_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_StringValue_fieldAccessorTable
         .ensureFieldAccessorsInitialized(StringValue.class, StringValue.Builder.class);
   }

   @Override
   public String getValue() {
      Object var1 = this.value_;
      if (var1 instanceof String) {
         return (String)var1;
      } else {
         ByteString var2 = (ByteString)var1;
         String var3 = var2.toStringUtf8();
         this.value_ = var3;
         return var3;
      }
   }

   @Override
   public ByteString getValueBytes() {
      Object var1 = this.value_;
      if (var1 instanceof String) {
         ByteString var2 = ByteString.copyFromUtf8((String)var1);
         this.value_ = var2;
         return var2;
      } else {
         return (ByteString)var1;
      }
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
      if (!this.getValueBytes().isEmpty()) {
         GeneratedMessageV3.writeString(var1, 1, this.value_);
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
         if (!this.getValueBytes().isEmpty()) {
            var1 += GeneratedMessageV3.computeStringSize(1, this.value_);
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
      } else if (!(var1 instanceof StringValue)) {
         return super.equals(var1);
      } else {
         StringValue var2 = (StringValue)var1;
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

   public static StringValue parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static StringValue parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static StringValue parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static StringValue parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static StringValue parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static StringValue parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static StringValue parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static StringValue parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static StringValue parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static StringValue parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static StringValue parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static StringValue parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public StringValue.Builder newBuilderForType() {
      return newBuilder();
   }

   public static StringValue.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static StringValue.Builder newBuilder(StringValue var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public StringValue.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new StringValue.Builder() : new StringValue.Builder().mergeFrom(this);
   }

   protected StringValue.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new StringValue.Builder(var1);
   }

   public static StringValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static StringValue of(String var0) {
      return newBuilder().setValue(var0).build();
   }

   public static Parser<StringValue> parser() {
      return PARSER;
   }

   @Override
   public Parser<StringValue> getParserForType() {
      return PARSER;
   }

   public StringValue getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<StringValue.Builder> implements StringValueOrBuilder {
      private Object value_ = "";

      public static final Descriptors.Descriptor getDescriptor() {
         return WrappersProto.internal_static_google_protobuf_StringValue_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return WrappersProto.internal_static_google_protobuf_StringValue_fieldAccessorTable
            .ensureFieldAccessorsInitialized(StringValue.class, StringValue.Builder.class);
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

      public StringValue.Builder clear() {
         super.clear();
         this.value_ = "";
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return WrappersProto.internal_static_google_protobuf_StringValue_descriptor;
      }

      public StringValue getDefaultInstanceForType() {
         return StringValue.getDefaultInstance();
      }

      public StringValue build() {
         StringValue var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public StringValue buildPartial() {
         StringValue var1 = new StringValue(this);
         var1.value_ = this.value_;
         this.onBuilt();
         return var1;
      }

      public StringValue.Builder clone() {
         return (StringValue.Builder)super.clone();
      }

      public StringValue.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (StringValue.Builder)super.setField(var1, var2);
      }

      public StringValue.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (StringValue.Builder)super.clearField(var1);
      }

      public StringValue.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (StringValue.Builder)super.clearOneof(var1);
      }

      public StringValue.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (StringValue.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public StringValue.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (StringValue.Builder)super.addRepeatedField(var1, var2);
      }

      public StringValue.Builder mergeFrom(Message var1) {
         if (var1 instanceof StringValue) {
            return this.mergeFrom((StringValue)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public StringValue.Builder mergeFrom(StringValue var1) {
         if (var1 == StringValue.getDefaultInstance()) {
            return this;
         } else {
            if (!var1.getValue().isEmpty()) {
               this.value_ = var1.value_;
               this.onChanged();
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

      public StringValue.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         StringValue var3 = null;

         try {
            var3 = StringValue.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (StringValue)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      @Override
      public String getValue() {
         Object var1 = this.value_;
         if (!(var1 instanceof String)) {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            this.value_ = var3;
            return var3;
         } else {
            return (String)var1;
         }
      }

      @Override
      public ByteString getValueBytes() {
         Object var1 = this.value_;
         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            this.value_ = var2;
            return var2;
         } else {
            return (ByteString)var1;
         }
      }

      public StringValue.Builder setValue(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.value_ = var1;
            this.onChanged();
            return this;
         }
      }

      public StringValue.Builder clearValue() {
         this.value_ = StringValue.getDefaultInstance().getValue();
         this.onChanged();
         return this;
      }

      public StringValue.Builder setValueBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.value_ = var1;
            this.onChanged();
            return this;
         }
      }

      public final StringValue.Builder setUnknownFields(UnknownFieldSet var1) {
         return (StringValue.Builder)super.setUnknownFields(var1);
      }

      public final StringValue.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (StringValue.Builder)super.mergeUnknownFields(var1);
      }
   }
}
