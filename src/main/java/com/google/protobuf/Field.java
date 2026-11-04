package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Field extends GeneratedMessageV3 implements FieldOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int KIND_FIELD_NUMBER = 1;
   private int kind_;
   public static final int CARDINALITY_FIELD_NUMBER = 2;
   private int cardinality_;
   public static final int NUMBER_FIELD_NUMBER = 3;
   private int number_;
   public static final int NAME_FIELD_NUMBER = 4;
   private volatile Object name_;
   public static final int TYPE_URL_FIELD_NUMBER = 6;
   private volatile Object typeUrl_;
   public static final int ONEOF_INDEX_FIELD_NUMBER = 7;
   private int oneofIndex_;
   public static final int PACKED_FIELD_NUMBER = 8;
   private boolean packed_;
   public static final int OPTIONS_FIELD_NUMBER = 9;
   private List<Option> options_;
   public static final int JSON_NAME_FIELD_NUMBER = 10;
   private volatile Object jsonName_;
   public static final int DEFAULT_VALUE_FIELD_NUMBER = 11;
   private volatile Object defaultValue_;
   private byte memoizedIsInitialized = -1;
   private static final Field DEFAULT_INSTANCE = new Field();
   private static final Parser<Field> PARSER = new AbstractParser<Field>() {
      public Field parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new Field(var1, var2);
      }
   };

   private Field(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private Field() {
      this.kind_ = 0;
      this.cardinality_ = 0;
      this.name_ = "";
      this.typeUrl_ = "";
      this.options_ = Collections.emptyList();
      this.jsonName_ = "";
      this.defaultValue_ = "";
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new Field();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Field(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                  case 8:
                     int var19 = var1.readEnum();
                     this.kind_ = var19;
                     break;
                  case 16:
                     int var18 = var1.readEnum();
                     this.cardinality_ = var18;
                     break;
                  case 24:
                     this.number_ = var1.readInt32();
                     break;
                  case 34:
                     String var17 = var1.readStringRequireUtf8();
                     this.name_ = var17;
                     break;
                  case 50:
                     String var16 = var1.readStringRequireUtf8();
                     this.typeUrl_ = var16;
                     break;
                  case 56:
                     this.oneofIndex_ = var1.readInt32();
                     break;
                  case 64:
                     this.packed_ = var1.readBool();
                     break;
                  case 74:
                     if (!(var3 & true)) {
                        this.options_ = new ArrayList<>();
                        var3 |= true;
                     }

                     this.options_.add(var1.readMessage(Option.parser(), var2));
                     break;
                  case 82:
                     String var15 = var1.readStringRequireUtf8();
                     this.jsonName_ = var15;
                     break;
                  case 90:
                     String var7 = var1.readStringRequireUtf8();
                     this.defaultValue_ = var7;
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
      return TypeProto.internal_static_google_protobuf_Field_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return TypeProto.internal_static_google_protobuf_Field_fieldAccessorTable.ensureFieldAccessorsInitialized(Field.class, Field.Builder.class);
   }

   @Override
   public int getKindValue() {
      return this.kind_;
   }

   @Override
   public Field.Kind getKind() {
      Field.Kind var1 = Field.Kind.valueOf(this.kind_);
      return var1 == null ? Field.Kind.UNRECOGNIZED : var1;
   }

   @Override
   public int getCardinalityValue() {
      return this.cardinality_;
   }

   @Override
   public Field.Cardinality getCardinality() {
      Field.Cardinality var1 = Field.Cardinality.valueOf(this.cardinality_);
      return var1 == null ? Field.Cardinality.UNRECOGNIZED : var1;
   }

   @Override
   public int getNumber() {
      return this.number_;
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
   public String getTypeUrl() {
      Object var1 = this.typeUrl_;
      if (var1 instanceof String) {
         return (String)var1;
      } else {
         ByteString var2 = (ByteString)var1;
         String var3 = var2.toStringUtf8();
         this.typeUrl_ = var3;
         return var3;
      }
   }

   @Override
   public ByteString getTypeUrlBytes() {
      Object var1 = this.typeUrl_;
      if (var1 instanceof String) {
         ByteString var2 = ByteString.copyFromUtf8((String)var1);
         this.typeUrl_ = var2;
         return var2;
      } else {
         return (ByteString)var1;
      }
   }

   @Override
   public int getOneofIndex() {
      return this.oneofIndex_;
   }

   @Override
   public boolean getPacked() {
      return this.packed_;
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
   public String getJsonName() {
      Object var1 = this.jsonName_;
      if (var1 instanceof String) {
         return (String)var1;
      } else {
         ByteString var2 = (ByteString)var1;
         String var3 = var2.toStringUtf8();
         this.jsonName_ = var3;
         return var3;
      }
   }

   @Override
   public ByteString getJsonNameBytes() {
      Object var1 = this.jsonName_;
      if (var1 instanceof String) {
         ByteString var2 = ByteString.copyFromUtf8((String)var1);
         this.jsonName_ = var2;
         return var2;
      } else {
         return (ByteString)var1;
      }
   }

   @Override
   public String getDefaultValue() {
      Object var1 = this.defaultValue_;
      if (var1 instanceof String) {
         return (String)var1;
      } else {
         ByteString var2 = (ByteString)var1;
         String var3 = var2.toStringUtf8();
         this.defaultValue_ = var3;
         return var3;
      }
   }

   @Override
   public ByteString getDefaultValueBytes() {
      Object var1 = this.defaultValue_;
      if (var1 instanceof String) {
         ByteString var2 = ByteString.copyFromUtf8((String)var1);
         this.defaultValue_ = var2;
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
      if (this.kind_ != Field.Kind.TYPE_UNKNOWN.getNumber()) {
         var1.writeEnum(1, this.kind_);
      }

      if (this.cardinality_ != Field.Cardinality.CARDINALITY_UNKNOWN.getNumber()) {
         var1.writeEnum(2, this.cardinality_);
      }

      if (this.number_ != 0) {
         var1.writeInt32(3, this.number_);
      }

      if (!this.getNameBytes().isEmpty()) {
         GeneratedMessageV3.writeString(var1, 4, this.name_);
      }

      if (!this.getTypeUrlBytes().isEmpty()) {
         GeneratedMessageV3.writeString(var1, 6, this.typeUrl_);
      }

      if (this.oneofIndex_ != 0) {
         var1.writeInt32(7, this.oneofIndex_);
      }

      if (this.packed_) {
         var1.writeBool(8, this.packed_);
      }

      for (int var2 = 0; var2 < this.options_.size(); var2++) {
         var1.writeMessage(9, this.options_.get(var2));
      }

      if (!this.getJsonNameBytes().isEmpty()) {
         GeneratedMessageV3.writeString(var1, 10, this.jsonName_);
      }

      if (!this.getDefaultValueBytes().isEmpty()) {
         GeneratedMessageV3.writeString(var1, 11, this.defaultValue_);
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
         if (this.kind_ != Field.Kind.TYPE_UNKNOWN.getNumber()) {
            var1 += CodedOutputStream.computeEnumSize(1, this.kind_);
         }

         if (this.cardinality_ != Field.Cardinality.CARDINALITY_UNKNOWN.getNumber()) {
            var1 += CodedOutputStream.computeEnumSize(2, this.cardinality_);
         }

         if (this.number_ != 0) {
            var1 += CodedOutputStream.computeInt32Size(3, this.number_);
         }

         if (!this.getNameBytes().isEmpty()) {
            var1 += GeneratedMessageV3.computeStringSize(4, this.name_);
         }

         if (!this.getTypeUrlBytes().isEmpty()) {
            var1 += GeneratedMessageV3.computeStringSize(6, this.typeUrl_);
         }

         if (this.oneofIndex_ != 0) {
            var1 += CodedOutputStream.computeInt32Size(7, this.oneofIndex_);
         }

         if (this.packed_) {
            var1 += CodedOutputStream.computeBoolSize(8, this.packed_);
         }

         for (int var2 = 0; var2 < this.options_.size(); var2++) {
            var1 += CodedOutputStream.computeMessageSize(9, this.options_.get(var2));
         }

         if (!this.getJsonNameBytes().isEmpty()) {
            var1 += GeneratedMessageV3.computeStringSize(10, this.jsonName_);
         }

         if (!this.getDefaultValueBytes().isEmpty()) {
            var1 += GeneratedMessageV3.computeStringSize(11, this.defaultValue_);
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
      } else if (!(var1 instanceof Field)) {
         return super.equals(var1);
      } else {
         Field var2 = (Field)var1;
         if (this.kind_ != var2.kind_) {
            return false;
         } else if (this.cardinality_ != var2.cardinality_) {
            return false;
         } else if (this.getNumber() != var2.getNumber()) {
            return false;
         } else if (!this.getName().equals(var2.getName())) {
            return false;
         } else if (!this.getTypeUrl().equals(var2.getTypeUrl())) {
            return false;
         } else if (this.getOneofIndex() != var2.getOneofIndex()) {
            return false;
         } else if (this.getPacked() != var2.getPacked()) {
            return false;
         } else if (!this.getOptionsList().equals(var2.getOptionsList())) {
            return false;
         } else if (!this.getJsonName().equals(var2.getJsonName())) {
            return false;
         } else {
            return !this.getDefaultValue().equals(var2.getDefaultValue()) ? false : this.unknownFields.equals(var2.unknownFields);
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
         var1 = 53 * var1 + this.kind_;
         var1 = 37 * var1 + 2;
         var1 = 53 * var1 + this.cardinality_;
         var1 = 37 * var1 + 3;
         var1 = 53 * var1 + this.getNumber();
         var1 = 37 * var1 + 4;
         var1 = 53 * var1 + this.getName().hashCode();
         var1 = 37 * var1 + 6;
         var1 = 53 * var1 + this.getTypeUrl().hashCode();
         var1 = 37 * var1 + 7;
         var1 = 53 * var1 + this.getOneofIndex();
         var1 = 37 * var1 + 8;
         var1 = 53 * var1 + Internal.hashBoolean(this.getPacked());
         if (this.getOptionsCount() > 0) {
            var1 = 37 * var1 + 9;
            var1 = 53 * var1 + this.getOptionsList().hashCode();
         }

         var1 = 37 * var1 + 10;
         var1 = 53 * var1 + this.getJsonName().hashCode();
         var1 = 37 * var1 + 11;
         var1 = 53 * var1 + this.getDefaultValue().hashCode();
         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static Field parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Field parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Field parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Field parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Field parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Field parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Field parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Field parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static Field parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static Field parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static Field parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Field parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public Field.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Field.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Field.Builder newBuilder(Field var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public Field.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Field.Builder() : new Field.Builder().mergeFrom(this);
   }

   protected Field.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new Field.Builder(var1);
   }

   public static Field getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Field> parser() {
      return PARSER;
   }

   @Override
   public Parser<Field> getParserForType() {
      return PARSER;
   }

   public Field getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Field.Builder> implements FieldOrBuilder {
      private int bitField0_;
      private int kind_ = 0;
      private int cardinality_ = 0;
      private int number_;
      private Object name_ = "";
      private Object typeUrl_ = "";
      private int oneofIndex_;
      private boolean packed_;
      private List<Option> options_ = Collections.emptyList();
      private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
      private Object jsonName_ = "";
      private Object defaultValue_ = "";

      public static final Descriptors.Descriptor getDescriptor() {
         return TypeProto.internal_static_google_protobuf_Field_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return TypeProto.internal_static_google_protobuf_Field_fieldAccessorTable.ensureFieldAccessorsInitialized(Field.class, Field.Builder.class);
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

      public Field.Builder clear() {
         super.clear();
         this.kind_ = 0;
         this.cardinality_ = 0;
         this.number_ = 0;
         this.name_ = "";
         this.typeUrl_ = "";
         this.oneofIndex_ = 0;
         this.packed_ = false;
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -2;
         } else {
            this.optionsBuilder_.clear();
         }

         this.jsonName_ = "";
         this.defaultValue_ = "";
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return TypeProto.internal_static_google_protobuf_Field_descriptor;
      }

      public Field getDefaultInstanceForType() {
         return Field.getDefaultInstance();
      }

      public Field build() {
         Field var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public Field buildPartial() {
         Field var1 = new Field(this);
         int var2 = this.bitField0_;
         var1.kind_ = this.kind_;
         var1.cardinality_ = this.cardinality_;
         var1.number_ = this.number_;
         var1.name_ = this.name_;
         var1.typeUrl_ = this.typeUrl_;
         var1.oneofIndex_ = this.oneofIndex_;
         var1.packed_ = this.packed_;
         if (this.optionsBuilder_ == null) {
            if ((this.bitField0_ & 1) != 0) {
               this.options_ = Collections.unmodifiableList(this.options_);
               this.bitField0_ &= -2;
            }

            var1.options_ = this.options_;
         } else {
            var1.options_ = this.optionsBuilder_.build();
         }

         var1.jsonName_ = this.jsonName_;
         var1.defaultValue_ = this.defaultValue_;
         this.onBuilt();
         return var1;
      }

      public Field.Builder clone() {
         return (Field.Builder)super.clone();
      }

      public Field.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Field.Builder)super.setField(var1, var2);
      }

      public Field.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (Field.Builder)super.clearField(var1);
      }

      public Field.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (Field.Builder)super.clearOneof(var1);
      }

      public Field.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (Field.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public Field.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Field.Builder)super.addRepeatedField(var1, var2);
      }

      public Field.Builder mergeFrom(Message var1) {
         if (var1 instanceof Field) {
            return this.mergeFrom((Field)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public Field.Builder mergeFrom(Field var1) {
         if (var1 == Field.getDefaultInstance()) {
            return this;
         } else {
            if (var1.kind_ != 0) {
               this.setKindValue(var1.getKindValue());
            }

            if (var1.cardinality_ != 0) {
               this.setCardinalityValue(var1.getCardinalityValue());
            }

            if (var1.getNumber() != 0) {
               this.setNumber(var1.getNumber());
            }

            if (!var1.getName().isEmpty()) {
               this.name_ = var1.name_;
               this.onChanged();
            }

            if (!var1.getTypeUrl().isEmpty()) {
               this.typeUrl_ = var1.typeUrl_;
               this.onChanged();
            }

            if (var1.getOneofIndex() != 0) {
               this.setOneofIndex(var1.getOneofIndex());
            }

            if (var1.getPacked()) {
               this.setPacked(var1.getPacked());
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

            if (!var1.getJsonName().isEmpty()) {
               this.jsonName_ = var1.jsonName_;
               this.onChanged();
            }

            if (!var1.getDefaultValue().isEmpty()) {
               this.defaultValue_ = var1.defaultValue_;
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

      public Field.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         Field var3 = null;

         try {
            var3 = Field.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (Field)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      @Override
      public int getKindValue() {
         return this.kind_;
      }

      public Field.Builder setKindValue(int var1) {
         this.kind_ = var1;
         this.onChanged();
         return this;
      }

      @Override
      public Field.Kind getKind() {
         Field.Kind var1 = Field.Kind.valueOf(this.kind_);
         return var1 == null ? Field.Kind.UNRECOGNIZED : var1;
      }

      public Field.Builder setKind(Field.Kind var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.kind_ = var1.getNumber();
            this.onChanged();
            return this;
         }
      }

      public Field.Builder clearKind() {
         this.kind_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getCardinalityValue() {
         return this.cardinality_;
      }

      public Field.Builder setCardinalityValue(int var1) {
         this.cardinality_ = var1;
         this.onChanged();
         return this;
      }

      @Override
      public Field.Cardinality getCardinality() {
         Field.Cardinality var1 = Field.Cardinality.valueOf(this.cardinality_);
         return var1 == null ? Field.Cardinality.UNRECOGNIZED : var1;
      }

      public Field.Builder setCardinality(Field.Cardinality var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.cardinality_ = var1.getNumber();
            this.onChanged();
            return this;
         }
      }

      public Field.Builder clearCardinality() {
         this.cardinality_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public int getNumber() {
         return this.number_;
      }

      public Field.Builder setNumber(int var1) {
         this.number_ = var1;
         this.onChanged();
         return this;
      }

      public Field.Builder clearNumber() {
         this.number_ = 0;
         this.onChanged();
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

      public Field.Builder setName(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.name_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Field.Builder clearName() {
         this.name_ = Field.getDefaultInstance().getName();
         this.onChanged();
         return this;
      }

      public Field.Builder setNameBytes(ByteString var1) {
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
      public String getTypeUrl() {
         Object var1 = this.typeUrl_;
         if (!(var1 instanceof String)) {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            this.typeUrl_ = var3;
            return var3;
         } else {
            return (String)var1;
         }
      }

      @Override
      public ByteString getTypeUrlBytes() {
         Object var1 = this.typeUrl_;
         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            this.typeUrl_ = var2;
            return var2;
         } else {
            return (ByteString)var1;
         }
      }

      public Field.Builder setTypeUrl(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.typeUrl_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Field.Builder clearTypeUrl() {
         this.typeUrl_ = Field.getDefaultInstance().getTypeUrl();
         this.onChanged();
         return this;
      }

      public Field.Builder setTypeUrlBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.typeUrl_ = var1;
            this.onChanged();
            return this;
         }
      }

      @Override
      public int getOneofIndex() {
         return this.oneofIndex_;
      }

      public Field.Builder setOneofIndex(int var1) {
         this.oneofIndex_ = var1;
         this.onChanged();
         return this;
      }

      public Field.Builder clearOneofIndex() {
         this.oneofIndex_ = 0;
         this.onChanged();
         return this;
      }

      @Override
      public boolean getPacked() {
         return this.packed_;
      }

      public Field.Builder setPacked(boolean var1) {
         this.packed_ = var1;
         this.onChanged();
         return this;
      }

      public Field.Builder clearPacked() {
         this.packed_ = false;
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

      public Field.Builder setOptions(int var1, Option var2) {
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

      public Field.Builder setOptions(int var1, Option.Builder var2) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.set(var1, var2.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.setMessage(var1, var2.build());
         }

         return this;
      }

      public Field.Builder addOptions(Option var1) {
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

      public Field.Builder addOptions(int var1, Option var2) {
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

      public Field.Builder addOptions(Option.Builder var1) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(var1.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1.build());
         }

         return this;
      }

      public Field.Builder addOptions(int var1, Option.Builder var2) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(var1, var2.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1, var2.build());
         }

         return this;
      }

      public Field.Builder addAllOptions(Iterable<? extends Option> var1) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            AbstractMessageLite.Builder.addAll(var1, this.options_);
            this.onChanged();
         } else {
            this.optionsBuilder_.addAllMessages(var1);
         }

         return this;
      }

      public Field.Builder clearOptions() {
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -2;
            this.onChanged();
         } else {
            this.optionsBuilder_.clear();
         }

         return this;
      }

      public Field.Builder removeOptions(int var1) {
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

      @Override
      public String getJsonName() {
         Object var1 = this.jsonName_;
         if (!(var1 instanceof String)) {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            this.jsonName_ = var3;
            return var3;
         } else {
            return (String)var1;
         }
      }

      @Override
      public ByteString getJsonNameBytes() {
         Object var1 = this.jsonName_;
         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            this.jsonName_ = var2;
            return var2;
         } else {
            return (ByteString)var1;
         }
      }

      public Field.Builder setJsonName(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.jsonName_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Field.Builder clearJsonName() {
         this.jsonName_ = Field.getDefaultInstance().getJsonName();
         this.onChanged();
         return this;
      }

      public Field.Builder setJsonNameBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.jsonName_ = var1;
            this.onChanged();
            return this;
         }
      }

      @Override
      public String getDefaultValue() {
         Object var1 = this.defaultValue_;
         if (!(var1 instanceof String)) {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            this.defaultValue_ = var3;
            return var3;
         } else {
            return (String)var1;
         }
      }

      @Override
      public ByteString getDefaultValueBytes() {
         Object var1 = this.defaultValue_;
         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            this.defaultValue_ = var2;
            return var2;
         } else {
            return (ByteString)var1;
         }
      }

      public Field.Builder setDefaultValue(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.defaultValue_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Field.Builder clearDefaultValue() {
         this.defaultValue_ = Field.getDefaultInstance().getDefaultValue();
         this.onChanged();
         return this;
      }

      public Field.Builder setDefaultValueBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.defaultValue_ = var1;
            this.onChanged();
            return this;
         }
      }

      public final Field.Builder setUnknownFields(UnknownFieldSet var1) {
         return (Field.Builder)super.setUnknownFields(var1);
      }

      public final Field.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (Field.Builder)super.mergeUnknownFields(var1);
      }
   }

   public static enum Cardinality implements ProtocolMessageEnum {
      CARDINALITY_UNKNOWN(0),
      CARDINALITY_OPTIONAL(1),
      CARDINALITY_REQUIRED(2),
      CARDINALITY_REPEATED(3),
      UNRECOGNIZED(-1);

      public static final int CARDINALITY_UNKNOWN_VALUE = 0;
      public static final int CARDINALITY_OPTIONAL_VALUE = 1;
      public static final int CARDINALITY_REQUIRED_VALUE = 2;
      public static final int CARDINALITY_REPEATED_VALUE = 3;
      private static final Internal.EnumLiteMap<Field.Cardinality> internalValueMap = new Internal.EnumLiteMap<Field.Cardinality>() {
         public Field.Cardinality findValueByNumber(int var1) {
            return Field.Cardinality.forNumber(var1);
         }
      };
      private static final Field.Cardinality[] VALUES = values();
      private final int value;

      @Override
      public final int getNumber() {
         if (this == UNRECOGNIZED) {
            throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
         } else {
            return this.value;
         }
      }

      @Deprecated
      public static Field.Cardinality valueOf(int var0) {
         return forNumber(var0);
      }

      public static Field.Cardinality forNumber(int var0) {
         switch (var0) {
            case 0:
               return CARDINALITY_UNKNOWN;
            case 1:
               return CARDINALITY_OPTIONAL;
            case 2:
               return CARDINALITY_REQUIRED;
            case 3:
               return CARDINALITY_REPEATED;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<Field.Cardinality> internalGetValueMap() {
         return internalValueMap;
      }

      @Override
      public final Descriptors.EnumValueDescriptor getValueDescriptor() {
         return getDescriptor().getValues().get(this.ordinal());
      }

      @Override
      public final Descriptors.EnumDescriptor getDescriptorForType() {
         return getDescriptor();
      }

      public static final Descriptors.EnumDescriptor getDescriptor() {
         return Field.getDescriptor().getEnumTypes().get(1);
      }

      public static Field.Cardinality valueOf(Descriptors.EnumValueDescriptor var0) {
         if (var0.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return var0.getIndex() == -1 ? UNRECOGNIZED : VALUES[var0.getIndex()];
         }
      }

      private Cardinality(int var3) {
         this.value = var3;
      }
   }

   public static enum Kind implements ProtocolMessageEnum {
      TYPE_UNKNOWN(0),
      TYPE_DOUBLE(1),
      TYPE_FLOAT(2),
      TYPE_INT64(3),
      TYPE_UINT64(4),
      TYPE_INT32(5),
      TYPE_FIXED64(6),
      TYPE_FIXED32(7),
      TYPE_BOOL(8),
      TYPE_STRING(9),
      TYPE_GROUP(10),
      TYPE_MESSAGE(11),
      TYPE_BYTES(12),
      TYPE_UINT32(13),
      TYPE_ENUM(14),
      TYPE_SFIXED32(15),
      TYPE_SFIXED64(16),
      TYPE_SINT32(17),
      TYPE_SINT64(18),
      UNRECOGNIZED(-1);

      public static final int TYPE_UNKNOWN_VALUE = 0;
      public static final int TYPE_DOUBLE_VALUE = 1;
      public static final int TYPE_FLOAT_VALUE = 2;
      public static final int TYPE_INT64_VALUE = 3;
      public static final int TYPE_UINT64_VALUE = 4;
      public static final int TYPE_INT32_VALUE = 5;
      public static final int TYPE_FIXED64_VALUE = 6;
      public static final int TYPE_FIXED32_VALUE = 7;
      public static final int TYPE_BOOL_VALUE = 8;
      public static final int TYPE_STRING_VALUE = 9;
      public static final int TYPE_GROUP_VALUE = 10;
      public static final int TYPE_MESSAGE_VALUE = 11;
      public static final int TYPE_BYTES_VALUE = 12;
      public static final int TYPE_UINT32_VALUE = 13;
      public static final int TYPE_ENUM_VALUE = 14;
      public static final int TYPE_SFIXED32_VALUE = 15;
      public static final int TYPE_SFIXED64_VALUE = 16;
      public static final int TYPE_SINT32_VALUE = 17;
      public static final int TYPE_SINT64_VALUE = 18;
      private static final Internal.EnumLiteMap<Field.Kind> internalValueMap = new Internal.EnumLiteMap<Field.Kind>() {
         public Field.Kind findValueByNumber(int var1) {
            return Field.Kind.forNumber(var1);
         }
      };
      private static final Field.Kind[] VALUES = values();
      private final int value;

      @Override
      public final int getNumber() {
         if (this == UNRECOGNIZED) {
            throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
         } else {
            return this.value;
         }
      }

      @Deprecated
      public static Field.Kind valueOf(int var0) {
         return forNumber(var0);
      }

      public static Field.Kind forNumber(int var0) {
         switch (var0) {
            case 0:
               return TYPE_UNKNOWN;
            case 1:
               return TYPE_DOUBLE;
            case 2:
               return TYPE_FLOAT;
            case 3:
               return TYPE_INT64;
            case 4:
               return TYPE_UINT64;
            case 5:
               return TYPE_INT32;
            case 6:
               return TYPE_FIXED64;
            case 7:
               return TYPE_FIXED32;
            case 8:
               return TYPE_BOOL;
            case 9:
               return TYPE_STRING;
            case 10:
               return TYPE_GROUP;
            case 11:
               return TYPE_MESSAGE;
            case 12:
               return TYPE_BYTES;
            case 13:
               return TYPE_UINT32;
            case 14:
               return TYPE_ENUM;
            case 15:
               return TYPE_SFIXED32;
            case 16:
               return TYPE_SFIXED64;
            case 17:
               return TYPE_SINT32;
            case 18:
               return TYPE_SINT64;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<Field.Kind> internalGetValueMap() {
         return internalValueMap;
      }

      @Override
      public final Descriptors.EnumValueDescriptor getValueDescriptor() {
         return getDescriptor().getValues().get(this.ordinal());
      }

      @Override
      public final Descriptors.EnumDescriptor getDescriptorForType() {
         return getDescriptor();
      }

      public static final Descriptors.EnumDescriptor getDescriptor() {
         return Field.getDescriptor().getEnumTypes().get(0);
      }

      public static Field.Kind valueOf(Descriptors.EnumValueDescriptor var0) {
         if (var0.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return var0.getIndex() == -1 ? UNRECOGNIZED : VALUES[var0.getIndex()];
         }
      }

      private Kind(int var3) {
         this.value = var3;
      }
   }
}
