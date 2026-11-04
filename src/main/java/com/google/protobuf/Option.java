package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Option extends GeneratedMessageV3 implements OptionOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int NAME_FIELD_NUMBER = 1;
   private volatile Object name_;
   public static final int VALUE_FIELD_NUMBER = 2;
   private Any value_;
   private byte memoizedIsInitialized = -1;
   private static final Option DEFAULT_INSTANCE = new Option();
   private static final Parser<Option> PARSER = new AbstractParser<Option>() {
      public Option parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new Option(var1, var2);
      }
   };

   private Option(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private Option() {
      this.name_ = "";
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new Option();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Option(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                     String var14 = var1.readStringRequireUtf8();
                     this.name_ = var14;
                     break;
                  case 18:
                     Any.Builder var6 = null;
                     if (this.value_ != null) {
                        var6 = this.value_.toBuilder();
                     }

                     this.value_ = var1.readMessage(Any.parser(), var2);
                     if (var6 != null) {
                        var6.mergeFrom(this.value_);
                        this.value_ = var6.buildPartial();
                     }
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
      return TypeProto.internal_static_google_protobuf_Option_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return TypeProto.internal_static_google_protobuf_Option_fieldAccessorTable.ensureFieldAccessorsInitialized(Option.class, Option.Builder.class);
   }

   @Override
   public String getName() {
      Object var1 = this.name_;
      if (var1 instanceof String) {
         return (String)var1;
      } else {
         ByteString var2 = (ByteString)var1;
         String var3 = var2.toStringUtf8();
         this.name_ = var3;
         return var3;
      }
   }

   @Override
   public ByteString getNameBytes() {
      Object var1 = this.name_;
      if (var1 instanceof String) {
         ByteString var2 = ByteString.copyFromUtf8((String)var1);
         this.name_ = var2;
         return var2;
      } else {
         return (ByteString)var1;
      }
   }

   @Override
   public boolean hasValue() {
      return this.value_ != null;
   }

   @Override
   public Any getValue() {
      return this.value_ == null ? Any.getDefaultInstance() : this.value_;
   }

   @Override
   public AnyOrBuilder getValueOrBuilder() {
      return this.getValue();
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
      if (!this.getNameBytes().isEmpty()) {
         GeneratedMessageV3.writeString(var1, 1, this.name_);
      }

      if (this.value_ != null) {
         var1.writeMessage(2, this.getValue());
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
         if (!this.getNameBytes().isEmpty()) {
            var1 += GeneratedMessageV3.computeStringSize(1, this.name_);
         }

         if (this.value_ != null) {
            var1 += CodedOutputStream.computeMessageSize(2, this.getValue());
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
      } else if (!(var1 instanceof Option)) {
         return super.equals(var1);
      } else {
         Option var2 = (Option)var1;
         if (!this.getName().equals(var2.getName())) {
            return false;
         } else if (this.hasValue() != var2.hasValue()) {
            return false;
         } else {
            return this.hasValue() && !this.getValue().equals(var2.getValue()) ? false : this.unknownFields.equals(var2.unknownFields);
         }
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
         var1 = 53 * var1 + this.getName().hashCode();
         if (this.hasValue()) {
            var1 = 37 * var1 + 2;
            var1 = 53 * var1 + this.getValue().hashCode();
         }

         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static Option parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Option parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Option parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Option parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Option parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Option parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Option parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Option parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static Option parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static Option parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static Option parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Option parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public Option.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Option.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Option.Builder newBuilder(Option var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public Option.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Option.Builder() : new Option.Builder().mergeFrom(this);
   }

   protected Option.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new Option.Builder(var1);
   }

   public static Option getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Option> parser() {
      return PARSER;
   }

   @Override
   public Parser<Option> getParserForType() {
      return PARSER;
   }

   public Option getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Option.Builder> implements OptionOrBuilder {
      private Object name_ = "";
      private Any value_;
      private SingleFieldBuilderV3<Any, Any.Builder, AnyOrBuilder> valueBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return TypeProto.internal_static_google_protobuf_Option_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return TypeProto.internal_static_google_protobuf_Option_fieldAccessorTable.ensureFieldAccessorsInitialized(Option.class, Option.Builder.class);
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

      public Option.Builder clear() {
         super.clear();
         this.name_ = "";
         if (this.valueBuilder_ == null) {
            this.value_ = null;
         } else {
            this.value_ = null;
            this.valueBuilder_ = null;
         }

         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return TypeProto.internal_static_google_protobuf_Option_descriptor;
      }

      public Option getDefaultInstanceForType() {
         return Option.getDefaultInstance();
      }

      public Option build() {
         Option var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public Option buildPartial() {
         Option var1 = new Option(this);
         var1.name_ = this.name_;
         if (this.valueBuilder_ == null) {
            var1.value_ = this.value_;
         } else {
            var1.value_ = this.valueBuilder_.build();
         }

         this.onBuilt();
         return var1;
      }

      public Option.Builder clone() {
         return (Option.Builder)super.clone();
      }

      public Option.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Option.Builder)super.setField(var1, var2);
      }

      public Option.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (Option.Builder)super.clearField(var1);
      }

      public Option.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (Option.Builder)super.clearOneof(var1);
      }

      public Option.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (Option.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public Option.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Option.Builder)super.addRepeatedField(var1, var2);
      }

      public Option.Builder mergeFrom(Message var1) {
         if (var1 instanceof Option) {
            return this.mergeFrom((Option)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public Option.Builder mergeFrom(Option var1) {
         if (var1 == Option.getDefaultInstance()) {
            return this;
         } else {
            if (!var1.getName().isEmpty()) {
               this.name_ = var1.name_;
               this.onChanged();
            }

            if (var1.hasValue()) {
               this.mergeValue(var1.getValue());
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

      public Option.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         Option var3 = null;

         try {
            var3 = Option.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (Option)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      @Override
      public String getName() {
         Object var1 = this.name_;
         if (!(var1 instanceof String)) {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            this.name_ = var3;
            return var3;
         } else {
            return (String)var1;
         }
      }

      @Override
      public ByteString getNameBytes() {
         Object var1 = this.name_;
         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            this.name_ = var2;
            return var2;
         } else {
            return (ByteString)var1;
         }
      }

      public Option.Builder setName(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.name_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Option.Builder clearName() {
         this.name_ = Option.getDefaultInstance().getName();
         this.onChanged();
         return this;
      }

      public Option.Builder setNameBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.name_ = var1;
            this.onChanged();
            return this;
         }
      }

      @Override
      public boolean hasValue() {
         return this.valueBuilder_ != null || this.value_ != null;
      }

      @Override
      public Any getValue() {
         if (this.valueBuilder_ == null) {
            return this.value_ == null ? Any.getDefaultInstance() : this.value_;
         } else {
            return this.valueBuilder_.getMessage();
         }
      }

      public Option.Builder setValue(Any var1) {
         if (this.valueBuilder_ == null) {
            if (var1 == null) {
               throw new NullPointerException();
            }

            this.value_ = var1;
            this.onChanged();
         } else {
            this.valueBuilder_.setMessage(var1);
         }

         return this;
      }

      public Option.Builder setValue(Any.Builder var1) {
         if (this.valueBuilder_ == null) {
            this.value_ = var1.build();
            this.onChanged();
         } else {
            this.valueBuilder_.setMessage(var1.build());
         }

         return this;
      }

      public Option.Builder mergeValue(Any var1) {
         if (this.valueBuilder_ == null) {
            if (this.value_ != null) {
               this.value_ = Any.newBuilder(this.value_).mergeFrom(var1).buildPartial();
            } else {
               this.value_ = var1;
            }

            this.onChanged();
         } else {
            this.valueBuilder_.mergeFrom(var1);
         }

         return this;
      }

      public Option.Builder clearValue() {
         if (this.valueBuilder_ == null) {
            this.value_ = null;
            this.onChanged();
         } else {
            this.value_ = null;
            this.valueBuilder_ = null;
         }

         return this;
      }

      public Any.Builder getValueBuilder() {
         this.onChanged();
         return this.getValueFieldBuilder().getBuilder();
      }

      @Override
      public AnyOrBuilder getValueOrBuilder() {
         if (this.valueBuilder_ != null) {
            return this.valueBuilder_.getMessageOrBuilder();
         } else {
            return this.value_ == null ? Any.getDefaultInstance() : this.value_;
         }
      }

      private SingleFieldBuilderV3<Any, Any.Builder, AnyOrBuilder> getValueFieldBuilder() {
         if (this.valueBuilder_ == null) {
            this.valueBuilder_ = new SingleFieldBuilderV3<>(this.getValue(), this.getParentForChildren(), this.isClean());
            this.value_ = null;
         }

         return this.valueBuilder_;
      }

      public final Option.Builder setUnknownFields(UnknownFieldSet var1) {
         return (Option.Builder)super.setUnknownFields(var1);
      }

      public final Option.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (Option.Builder)super.mergeUnknownFields(var1);
      }
   }
}
