package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EnumValue extends GeneratedMessageV3 implements EnumValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int NAME_FIELD_NUMBER = 1;
   private volatile Object name_;
   public static final int NUMBER_FIELD_NUMBER = 2;
   private int number_;
   public static final int OPTIONS_FIELD_NUMBER = 3;
   private List<Option> options_;
   private byte memoizedIsInitialized = -1;
   private static final EnumValue DEFAULT_INSTANCE = new EnumValue();
   private static final Parser<EnumValue> PARSER = new AbstractParser<EnumValue>() {
      public EnumValue parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new EnumValue(var1, var2);
      }
   };

   private EnumValue(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private EnumValue() {
      this.name_ = "";
      this.options_ = Collections.emptyList();
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new EnumValue();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private EnumValue(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      this();
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         boolean var3 = false;
         UnknownFieldSet.Builder var4 = UnknownFieldSet.newBuilder();

         try {
            boolean var5 = false;

            while (!var5) {
               int var6 = var1.readTag();
               switch (var6) {
                  case 0:
                     var5 = true;
                     break;
                  case 10:
                     String var7 = var1.readStringRequireUtf8();
                     this.name_ = var7;
                     break;
                  case 16:
                     this.number_ = var1.readInt32();
                     break;
                  case 26:
                     if (!(var3 & true)) {
                        this.options_ = new ArrayList<>();
                        var3 |= true;
                     }

                     this.options_.add(var1.readMessage(Option.parser(), var2));
                     break;
                  default:
                     if (!this.parseUnknownField(var1, var4, var2, var6)) {
                        var5 = true;
                     }
               }
            }
         } catch (InvalidProtocolBufferException var12) {
            throw var12.setUnfinishedMessage(this);
         } catch (IOException var13) {
            throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
         } finally {
            if (var3 & true) {
               this.options_ = Collections.unmodifiableList(this.options_);
            }

            this.unknownFields = var4.build();
            this.makeExtensionsImmutable();
         }
      }
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return TypeProto.internal_static_google_protobuf_EnumValue_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return TypeProto.internal_static_google_protobuf_EnumValue_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumValue.class, EnumValue.Builder.class);
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
   public int getNumber() {
      return this.number_;
   }

   @Override
   public List<Option> getOptionsList() {
      return this.options_;
   }

   @Override
   public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
      return this.options_;
   }

   @Override
   public int getOptionsCount() {
      return this.options_.size();
   }

   @Override
   public Option getOptions(int var1) {
      return this.options_.get(var1);
   }

   @Override
   public OptionOrBuilder getOptionsOrBuilder(int var1) {
      return this.options_.get(var1);
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

      if (this.number_ != 0) {
         var1.writeInt32(2, this.number_);
      }

      for (int var2 = 0; var2 < this.options_.size(); var2++) {
         var1.writeMessage(3, this.options_.get(var2));
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

         if (this.number_ != 0) {
            var1 += CodedOutputStream.computeInt32Size(2, this.number_);
         }

         for (int var2 = 0; var2 < this.options_.size(); var2++) {
            var1 += CodedOutputStream.computeMessageSize(3, this.options_.get(var2));
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
      } else if (!(var1 instanceof EnumValue)) {
         return super.equals(var1);
      } else {
         EnumValue var2 = (EnumValue)var1;
         if (!this.getName().equals(var2.getName())) {
            return false;
         } else if (this.getNumber() != var2.getNumber()) {
            return false;
         } else {
            return !this.getOptionsList().equals(var2.getOptionsList()) ? false : this.unknownFields.equals(var2.unknownFields);
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
         var1 = 37 * var1 + 2;
         var1 = 53 * var1 + this.getNumber();
         if (this.getOptionsCount() > 0) {
            var1 = 37 * var1 + 3;
            var1 = 53 * var1 + this.getOptionsList().hashCode();
         }

         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static EnumValue parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static EnumValue parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static EnumValue parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static EnumValue parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static EnumValue parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static EnumValue parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static EnumValue parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static EnumValue parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static EnumValue parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static EnumValue parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static EnumValue parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static EnumValue parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public EnumValue.Builder newBuilderForType() {
      return newBuilder();
   }

   public static EnumValue.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static EnumValue.Builder newBuilder(EnumValue var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public EnumValue.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new EnumValue.Builder() : new EnumValue.Builder().mergeFrom(this);
   }

   protected EnumValue.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new EnumValue.Builder(var1);
   }

   public static EnumValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<EnumValue> parser() {
      return PARSER;
   }

   @Override
   public Parser<EnumValue> getParserForType() {
      return PARSER;
   }

   public EnumValue getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<EnumValue.Builder> implements EnumValueOrBuilder {
      private int bitField0_;
      private Object name_ = "";
      private int number_;
      private List<Option> options_ = Collections.emptyList();
      private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return TypeProto.internal_static_google_protobuf_EnumValue_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return TypeProto.internal_static_google_protobuf_EnumValue_fieldAccessorTable
            .ensureFieldAccessorsInitialized(EnumValue.class, EnumValue.Builder.class);
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
            this.getOptionsFieldBuilder();
         }
      }

      public EnumValue.Builder clear() {
         super.clear();
         this.name_ = "";
         this.number_ = 0;
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -2;
         } else {
            this.optionsBuilder_.clear();
         }

         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return TypeProto.internal_static_google_protobuf_EnumValue_descriptor;
      }

      public EnumValue getDefaultInstanceForType() {
         return EnumValue.getDefaultInstance();
      }

      public EnumValue build() {
         EnumValue var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public EnumValue buildPartial() {
         EnumValue var1 = new EnumValue(this);
         int var2 = this.bitField0_;
         var1.name_ = this.name_;
         var1.number_ = this.number_;
         if (this.optionsBuilder_ == null) {
            if ((this.bitField0_ & 1) != 0) {
               this.options_ = Collections.unmodifiableList(this.options_);
               this.bitField0_ &= -2;
            }

            var1.options_ = this.options_;
         } else {
            var1.options_ = this.optionsBuilder_.build();
         }

         this.onBuilt();
         return var1;
      }

      public EnumValue.Builder clone() {
         return (EnumValue.Builder)super.clone();
      }

      public EnumValue.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (EnumValue.Builder)super.setField(var1, var2);
      }

      public EnumValue.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (EnumValue.Builder)super.clearField(var1);
      }

      public EnumValue.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (EnumValue.Builder)super.clearOneof(var1);
      }

      public EnumValue.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (EnumValue.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public EnumValue.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (EnumValue.Builder)super.addRepeatedField(var1, var2);
      }

      public EnumValue.Builder mergeFrom(Message var1) {
         if (var1 instanceof EnumValue) {
            return this.mergeFrom((EnumValue)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public EnumValue.Builder mergeFrom(EnumValue var1) {
         if (var1 == EnumValue.getDefaultInstance()) {
            return this;
         } else {
            if (!var1.getName().isEmpty()) {
               this.name_ = var1.name_;
               this.onChanged();
            }

            if (var1.getNumber() != 0) {
               this.setNumber(var1.getNumber());
            }

            if (this.optionsBuilder_ == null) {
               if (!var1.options_.isEmpty()) {
                  if (this.options_.isEmpty()) {
                     this.options_ = var1.options_;
                     this.bitField0_ &= -2;
                  } else {
                     this.ensureOptionsIsMutable();
                     this.options_.addAll(var1.options_);
                  }

                  this.onChanged();
               }
            } else if (!var1.options_.isEmpty()) {
               if (this.optionsBuilder_.isEmpty()) {
                  this.optionsBuilder_.dispose();
                  this.optionsBuilder_ = null;
                  this.options_ = var1.options_;
                  this.bitField0_ &= -2;
                  this.optionsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? this.getOptionsFieldBuilder() : null;
               } else {
                  this.optionsBuilder_.addAllMessages(var1.options_);
               }
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

      public EnumValue.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         EnumValue var3 = null;

         try {
            var3 = EnumValue.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (EnumValue)var8.getUnfinishedMessage();
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

      public EnumValue.Builder setName(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.name_ = var1;
            this.onChanged();
            return this;
         }
      }

      public EnumValue.Builder clearName() {
         this.name_ = EnumValue.getDefaultInstance().getName();
         this.onChanged();
         return this;
      }

      public EnumValue.Builder setNameBytes(ByteString var1) {
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
      public int getNumber() {
         return this.number_;
      }

      public EnumValue.Builder setNumber(int var1) {
         this.number_ = var1;
         this.onChanged();
         return this;
      }

      public EnumValue.Builder clearNumber() {
         this.number_ = 0;
         this.onChanged();
         return this;
      }

      private void ensureOptionsIsMutable() {
         if ((this.bitField0_ & 1) == 0) {
            this.options_ = new ArrayList<>(this.options_);
            this.bitField0_ |= 1;
         }
      }

      @Override
      public List<Option> getOptionsList() {
         return this.optionsBuilder_ == null ? Collections.unmodifiableList(this.options_) : this.optionsBuilder_.getMessageList();
      }

      @Override
      public int getOptionsCount() {
         return this.optionsBuilder_ == null ? this.options_.size() : this.optionsBuilder_.getCount();
      }

      @Override
      public Option getOptions(int var1) {
         return this.optionsBuilder_ == null ? this.options_.get(var1) : this.optionsBuilder_.getMessage(var1);
      }

      public EnumValue.Builder setOptions(int var1, Option var2) {
         if (this.optionsBuilder_ == null) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.set(var1, var2);
            this.onChanged();
         } else {
            this.optionsBuilder_.setMessage(var1, var2);
         }

         return this;
      }

      public EnumValue.Builder setOptions(int var1, Option.Builder var2) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.set(var1, var2.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.setMessage(var1, var2.build());
         }

         return this;
      }

      public EnumValue.Builder addOptions(Option var1) {
         if (this.optionsBuilder_ == null) {
            if (var1 == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.add(var1);
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1);
         }

         return this;
      }

      public EnumValue.Builder addOptions(int var1, Option var2) {
         if (this.optionsBuilder_ == null) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.add(var1, var2);
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1, var2);
         }

         return this;
      }

      public EnumValue.Builder addOptions(Option.Builder var1) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(var1.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1.build());
         }

         return this;
      }

      public EnumValue.Builder addOptions(int var1, Option.Builder var2) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(var1, var2.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1, var2.build());
         }

         return this;
      }

      public EnumValue.Builder addAllOptions(Iterable<? extends Option> var1) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            AbstractMessageLite.Builder.addAll(var1, this.options_);
            this.onChanged();
         } else {
            this.optionsBuilder_.addAllMessages(var1);
         }

         return this;
      }

      public EnumValue.Builder clearOptions() {
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -2;
            this.onChanged();
         } else {
            this.optionsBuilder_.clear();
         }

         return this;
      }

      public EnumValue.Builder removeOptions(int var1) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.remove(var1);
            this.onChanged();
         } else {
            this.optionsBuilder_.remove(var1);
         }

         return this;
      }

      public Option.Builder getOptionsBuilder(int var1) {
         return this.getOptionsFieldBuilder().getBuilder(var1);
      }

      @Override
      public OptionOrBuilder getOptionsOrBuilder(int var1) {
         return this.optionsBuilder_ == null ? this.options_.get(var1) : this.optionsBuilder_.getMessageOrBuilder(var1);
      }

      @Override
      public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
         return this.optionsBuilder_ != null ? this.optionsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.options_);
      }

      public Option.Builder addOptionsBuilder() {
         return this.getOptionsFieldBuilder().addBuilder(Option.getDefaultInstance());
      }

      public Option.Builder addOptionsBuilder(int var1) {
         return this.getOptionsFieldBuilder().addBuilder(var1, Option.getDefaultInstance());
      }

      public List<Option.Builder> getOptionsBuilderList() {
         return this.getOptionsFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> getOptionsFieldBuilder() {
         if (this.optionsBuilder_ == null) {
            this.optionsBuilder_ = new RepeatedFieldBuilderV3<>(this.options_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
            this.options_ = null;
         }

         return this.optionsBuilder_;
      }

      public final EnumValue.Builder setUnknownFields(UnknownFieldSet var1) {
         return (EnumValue.Builder)super.setUnknownFields(var1);
      }

      public final EnumValue.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (EnumValue.Builder)super.mergeUnknownFields(var1);
      }
   }
}
