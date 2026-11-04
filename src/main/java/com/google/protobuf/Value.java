package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Value extends GeneratedMessageV3 implements ValueOrBuilder {
   private static final long serialVersionUID = 0L;
   private int kindCase_ = 0;
   private Object kind_;
   public static final int NULL_VALUE_FIELD_NUMBER = 1;
   public static final int NUMBER_VALUE_FIELD_NUMBER = 2;
   public static final int STRING_VALUE_FIELD_NUMBER = 3;
   public static final int BOOL_VALUE_FIELD_NUMBER = 4;
   public static final int STRUCT_VALUE_FIELD_NUMBER = 5;
   public static final int LIST_VALUE_FIELD_NUMBER = 6;
   private byte memoizedIsInitialized = -1;
   private static final Value DEFAULT_INSTANCE = new Value();
   private static final Parser<Value> PARSER = new AbstractParser<Value>() {
      public Value parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new Value(var1, var2);
      }
   };

   private Value(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private Value() {
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new Value();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Value(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                     int var16 = var1.readEnum();
                     this.kindCase_ = 1;
                     this.kind_ = var16;
                     break;
                  case 17:
                     this.kindCase_ = 2;
                     this.kind_ = var1.readDouble();
                     break;
                  case 26:
                     String var15 = var1.readStringRequireUtf8();
                     this.kindCase_ = 3;
                     this.kind_ = var15;
                     break;
                  case 32:
                     this.kindCase_ = 4;
                     this.kind_ = var1.readBool();
                     break;
                  case 42:
                     Struct.Builder var14 = null;
                     if (this.kindCase_ == 5) {
                        var14 = ((Struct)this.kind_).toBuilder();
                     }

                     this.kind_ = var1.readMessage(Struct.parser(), var2);
                     if (var14 != null) {
                        var14.mergeFrom((Struct)this.kind_);
                        this.kind_ = var14.buildPartial();
                     }

                     this.kindCase_ = 5;
                     break;
                  case 50:
                     ListValue.Builder var6 = null;
                     if (this.kindCase_ == 6) {
                        var6 = ((ListValue)this.kind_).toBuilder();
                     }

                     this.kind_ = var1.readMessage(ListValue.parser(), var2);
                     if (var6 != null) {
                        var6.mergeFrom((ListValue)this.kind_);
                        this.kind_ = var6.buildPartial();
                     }

                     this.kindCase_ = 6;
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
      return StructProto.internal_static_google_protobuf_Value_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return StructProto.internal_static_google_protobuf_Value_fieldAccessorTable.ensureFieldAccessorsInitialized(Value.class, Value.Builder.class);
   }

   @Override
   public Value.KindCase getKindCase() {
      return Value.KindCase.forNumber(this.kindCase_);
   }

   @Override
   public int getNullValueValue() {
      return this.kindCase_ == 1 ? (Integer)this.kind_ : 0;
   }

   @Override
   public NullValue getNullValue() {
      if (this.kindCase_ == 1) {
         NullValue var1 = NullValue.valueOf((Integer)this.kind_);
         return var1 == null ? NullValue.UNRECOGNIZED : var1;
      } else {
         return NullValue.NULL_VALUE;
      }
   }

   @Override
   public double getNumberValue() {
      return this.kindCase_ == 2 ? (Double)this.kind_ : 0.0;
   }

   @Override
   public String getStringValue() {
      Object var1 = "";
      if (this.kindCase_ == 3) {
         var1 = this.kind_;
      }

      if (var1 instanceof String) {
         return (String)var1;
      } else {
         ByteString var2 = (ByteString)var1;
         String var3 = var2.toStringUtf8();
         if (this.kindCase_ == 3) {
            this.kind_ = var3;
         }

         return var3;
      }
   }

   @Override
   public ByteString getStringValueBytes() {
      Object var1 = "";
      if (this.kindCase_ == 3) {
         var1 = this.kind_;
      }

      if (var1 instanceof String) {
         ByteString var2 = ByteString.copyFromUtf8((String)var1);
         if (this.kindCase_ == 3) {
            this.kind_ = var2;
         }

         return var2;
      } else {
         return (ByteString)var1;
      }
   }

   @Override
   public boolean getBoolValue() {
      return this.kindCase_ == 4 ? (Boolean)this.kind_ : false;
   }

   @Override
   public boolean hasStructValue() {
      return this.kindCase_ == 5;
   }

   @Override
   public Struct getStructValue() {
      return this.kindCase_ == 5 ? (Struct)this.kind_ : Struct.getDefaultInstance();
   }

   @Override
   public StructOrBuilder getStructValueOrBuilder() {
      return this.kindCase_ == 5 ? (Struct)this.kind_ : Struct.getDefaultInstance();
   }

   @Override
   public boolean hasListValue() {
      return this.kindCase_ == 6;
   }

   @Override
   public ListValue getListValue() {
      return this.kindCase_ == 6 ? (ListValue)this.kind_ : ListValue.getDefaultInstance();
   }

   @Override
   public ListValueOrBuilder getListValueOrBuilder() {
      return this.kindCase_ == 6 ? (ListValue)this.kind_ : ListValue.getDefaultInstance();
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
      if (this.kindCase_ == 1) {
         var1.writeEnum(1, (Integer)this.kind_);
      }

      if (this.kindCase_ == 2) {
         var1.writeDouble(2, (Double)this.kind_);
      }

      if (this.kindCase_ == 3) {
         GeneratedMessageV3.writeString(var1, 3, this.kind_);
      }

      if (this.kindCase_ == 4) {
         var1.writeBool(4, (Boolean)this.kind_);
      }

      if (this.kindCase_ == 5) {
         var1.writeMessage(5, (Struct)this.kind_);
      }

      if (this.kindCase_ == 6) {
         var1.writeMessage(6, (ListValue)this.kind_);
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
         if (this.kindCase_ == 1) {
            var1 += CodedOutputStream.computeEnumSize(1, (Integer)this.kind_);
         }

         if (this.kindCase_ == 2) {
            var1 += CodedOutputStream.computeDoubleSize(2, (Double)this.kind_);
         }

         if (this.kindCase_ == 3) {
            var1 += GeneratedMessageV3.computeStringSize(3, this.kind_);
         }

         if (this.kindCase_ == 4) {
            var1 += CodedOutputStream.computeBoolSize(4, (Boolean)this.kind_);
         }

         if (this.kindCase_ == 5) {
            var1 += CodedOutputStream.computeMessageSize(5, (Struct)this.kind_);
         }

         if (this.kindCase_ == 6) {
            var1 += CodedOutputStream.computeMessageSize(6, (ListValue)this.kind_);
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
      } else if (!(var1 instanceof Value)) {
         return super.equals(var1);
      } else {
         Value var2 = (Value)var1;
         if (!this.getKindCase().equals(var2.getKindCase())) {
            return false;
         } else {
            switch (this.kindCase_) {
               case 0:
               default:
                  break;
               case 1:
                  if (this.getNullValueValue() != var2.getNullValueValue()) {
                     return false;
                  }
                  break;
               case 2:
                  if (Double.doubleToLongBits(this.getNumberValue()) != Double.doubleToLongBits(var2.getNumberValue())) {
                     return false;
                  }
                  break;
               case 3:
                  if (!this.getStringValue().equals(var2.getStringValue())) {
                     return false;
                  }
                  break;
               case 4:
                  if (this.getBoolValue() != var2.getBoolValue()) {
                     return false;
                  }
                  break;
               case 5:
                  if (!this.getStructValue().equals(var2.getStructValue())) {
                     return false;
                  }
                  break;
               case 6:
                  if (!this.getListValue().equals(var2.getListValue())) {
                     return false;
                  }
            }

            return this.unknownFields.equals(var2.unknownFields);
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
         switch (this.kindCase_) {
            case 0:
            default:
               break;
            case 1:
               var1 = 37 * var1 + 1;
               var1 = 53 * var1 + this.getNullValueValue();
               break;
            case 2:
               var1 = 37 * var1 + 2;
               var1 = 53 * var1 + Internal.hashLong(Double.doubleToLongBits(this.getNumberValue()));
               break;
            case 3:
               var1 = 37 * var1 + 3;
               var1 = 53 * var1 + this.getStringValue().hashCode();
               break;
            case 4:
               var1 = 37 * var1 + 4;
               var1 = 53 * var1 + Internal.hashBoolean(this.getBoolValue());
               break;
            case 5:
               var1 = 37 * var1 + 5;
               var1 = 53 * var1 + this.getStructValue().hashCode();
               break;
            case 6:
               var1 = 37 * var1 + 6;
               var1 = 53 * var1 + this.getListValue().hashCode();
         }

         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static Value parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Value parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Value parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Value parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Value parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Value parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Value parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Value parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static Value parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static Value parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static Value parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Value parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public Value.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Value.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Value.Builder newBuilder(Value var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public Value.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Value.Builder() : new Value.Builder().mergeFrom(this);
   }

   protected Value.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new Value.Builder(var1);
   }

   public static Value getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Value> parser() {
      return PARSER;
   }

   @Override
   public Parser<Value> getParserForType() {
      return PARSER;
   }

   public Value getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Value.Builder> implements ValueOrBuilder {
      private int kindCase_ = 0;
      private Object kind_;
      private SingleFieldBuilderV3<Struct, Struct.Builder, StructOrBuilder> structValueBuilder_;
      private SingleFieldBuilderV3<ListValue, ListValue.Builder, ListValueOrBuilder> listValueBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return StructProto.internal_static_google_protobuf_Value_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return StructProto.internal_static_google_protobuf_Value_fieldAccessorTable.ensureFieldAccessorsInitialized(Value.class, Value.Builder.class);
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

      public Value.Builder clear() {
         super.clear();
         this.kindCase_ = 0;
         this.kind_ = null;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return StructProto.internal_static_google_protobuf_Value_descriptor;
      }

      public Value getDefaultInstanceForType() {
         return Value.getDefaultInstance();
      }

      public Value build() {
         Value var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public Value buildPartial() {
         Value var1 = new Value(this);
         if (this.kindCase_ == 1) {
            var1.kind_ = this.kind_;
         }

         if (this.kindCase_ == 2) {
            var1.kind_ = this.kind_;
         }

         if (this.kindCase_ == 3) {
            var1.kind_ = this.kind_;
         }

         if (this.kindCase_ == 4) {
            var1.kind_ = this.kind_;
         }

         if (this.kindCase_ == 5) {
            if (this.structValueBuilder_ == null) {
               var1.kind_ = this.kind_;
            } else {
               var1.kind_ = this.structValueBuilder_.build();
            }
         }

         if (this.kindCase_ == 6) {
            if (this.listValueBuilder_ == null) {
               var1.kind_ = this.kind_;
            } else {
               var1.kind_ = this.listValueBuilder_.build();
            }
         }

         var1.kindCase_ = this.kindCase_;
         this.onBuilt();
         return var1;
      }

      public Value.Builder clone() {
         return (Value.Builder)super.clone();
      }

      public Value.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Value.Builder)super.setField(var1, var2);
      }

      public Value.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (Value.Builder)super.clearField(var1);
      }

      public Value.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (Value.Builder)super.clearOneof(var1);
      }

      public Value.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (Value.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public Value.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Value.Builder)super.addRepeatedField(var1, var2);
      }

      public Value.Builder mergeFrom(Message var1) {
         if (var1 instanceof Value) {
            return this.mergeFrom((Value)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public Value.Builder mergeFrom(Value var1) {
         if (var1 == Value.getDefaultInstance()) {
            return this;
         } else {
            switch (var1.getKindCase()) {
               case NULL_VALUE:
                  this.setNullValueValue(var1.getNullValueValue());
                  break;
               case NUMBER_VALUE:
                  this.setNumberValue(var1.getNumberValue());
                  break;
               case STRING_VALUE:
                  this.kindCase_ = 3;
                  this.kind_ = var1.kind_;
                  this.onChanged();
                  break;
               case BOOL_VALUE:
                  this.setBoolValue(var1.getBoolValue());
                  break;
               case STRUCT_VALUE:
                  this.mergeStructValue(var1.getStructValue());
                  break;
               case LIST_VALUE:
                  this.mergeListValue(var1.getListValue());
               case KIND_NOT_SET:
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

      public Value.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         Value var3 = null;

         try {
            var3 = Value.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (Value)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      @Override
      public Value.KindCase getKindCase() {
         return Value.KindCase.forNumber(this.kindCase_);
      }

      public Value.Builder clearKind() {
         this.kindCase_ = 0;
         this.kind_ = null;
         this.onChanged();
         return this;
      }

      @Override
      public int getNullValueValue() {
         return this.kindCase_ == 1 ? (Integer)this.kind_ : 0;
      }

      public Value.Builder setNullValueValue(int var1) {
         this.kindCase_ = 1;
         this.kind_ = var1;
         this.onChanged();
         return this;
      }

      @Override
      public NullValue getNullValue() {
         if (this.kindCase_ == 1) {
            NullValue var1 = NullValue.valueOf((Integer)this.kind_);
            return var1 == null ? NullValue.UNRECOGNIZED : var1;
         } else {
            return NullValue.NULL_VALUE;
         }
      }

      public Value.Builder setNullValue(NullValue var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.kindCase_ = 1;
            this.kind_ = var1.getNumber();
            this.onChanged();
            return this;
         }
      }

      public Value.Builder clearNullValue() {
         if (this.kindCase_ == 1) {
            this.kindCase_ = 0;
            this.kind_ = null;
            this.onChanged();
         }

         return this;
      }

      @Override
      public double getNumberValue() {
         return this.kindCase_ == 2 ? (Double)this.kind_ : 0.0;
      }

      public Value.Builder setNumberValue(double var1) {
         this.kindCase_ = 2;
         this.kind_ = var1;
         this.onChanged();
         return this;
      }

      public Value.Builder clearNumberValue() {
         if (this.kindCase_ == 2) {
            this.kindCase_ = 0;
            this.kind_ = null;
            this.onChanged();
         }

         return this;
      }

      @Override
      public String getStringValue() {
         Object var1 = "";
         if (this.kindCase_ == 3) {
            var1 = this.kind_;
         }

         if (!(var1 instanceof String)) {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            if (this.kindCase_ == 3) {
               this.kind_ = var3;
            }

            return var3;
         } else {
            return (String)var1;
         }
      }

      @Override
      public ByteString getStringValueBytes() {
         Object var1 = "";
         if (this.kindCase_ == 3) {
            var1 = this.kind_;
         }

         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            if (this.kindCase_ == 3) {
               this.kind_ = var2;
            }

            return var2;
         } else {
            return (ByteString)var1;
         }
      }

      public Value.Builder setStringValue(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.kindCase_ = 3;
            this.kind_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Value.Builder clearStringValue() {
         if (this.kindCase_ == 3) {
            this.kindCase_ = 0;
            this.kind_ = null;
            this.onChanged();
         }

         return this;
      }

      public Value.Builder setStringValueBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.kindCase_ = 3;
            this.kind_ = var1;
            this.onChanged();
            return this;
         }
      }

      @Override
      public boolean getBoolValue() {
         return this.kindCase_ == 4 ? (Boolean)this.kind_ : false;
      }

      public Value.Builder setBoolValue(boolean var1) {
         this.kindCase_ = 4;
         this.kind_ = var1;
         this.onChanged();
         return this;
      }

      public Value.Builder clearBoolValue() {
         if (this.kindCase_ == 4) {
            this.kindCase_ = 0;
            this.kind_ = null;
            this.onChanged();
         }

         return this;
      }

      @Override
      public boolean hasStructValue() {
         return this.kindCase_ == 5;
      }

      @Override
      public Struct getStructValue() {
         if (this.structValueBuilder_ == null) {
            return this.kindCase_ == 5 ? (Struct)this.kind_ : Struct.getDefaultInstance();
         } else {
            return this.kindCase_ == 5 ? this.structValueBuilder_.getMessage() : Struct.getDefaultInstance();
         }
      }

      public Value.Builder setStructValue(Struct var1) {
         if (this.structValueBuilder_ == null) {
            if (var1 == null) {
               throw new NullPointerException();
            }

            this.kind_ = var1;
            this.onChanged();
         } else {
            this.structValueBuilder_.setMessage(var1);
         }

         this.kindCase_ = 5;
         return this;
      }

      public Value.Builder setStructValue(Struct.Builder var1) {
         if (this.structValueBuilder_ == null) {
            this.kind_ = var1.build();
            this.onChanged();
         } else {
            this.structValueBuilder_.setMessage(var1.build());
         }

         this.kindCase_ = 5;
         return this;
      }

      public Value.Builder mergeStructValue(Struct var1) {
         if (this.structValueBuilder_ == null) {
            if (this.kindCase_ == 5 && this.kind_ != Struct.getDefaultInstance()) {
               this.kind_ = Struct.newBuilder((Struct)this.kind_).mergeFrom(var1).buildPartial();
            } else {
               this.kind_ = var1;
            }

            this.onChanged();
         } else {
            if (this.kindCase_ == 5) {
               this.structValueBuilder_.mergeFrom(var1);
            }

            this.structValueBuilder_.setMessage(var1);
         }

         this.kindCase_ = 5;
         return this;
      }

      public Value.Builder clearStructValue() {
         if (this.structValueBuilder_ == null) {
            if (this.kindCase_ == 5) {
               this.kindCase_ = 0;
               this.kind_ = null;
               this.onChanged();
            }
         } else {
            if (this.kindCase_ == 5) {
               this.kindCase_ = 0;
               this.kind_ = null;
            }

            this.structValueBuilder_.clear();
         }

         return this;
      }

      public Struct.Builder getStructValueBuilder() {
         return this.getStructValueFieldBuilder().getBuilder();
      }

      @Override
      public StructOrBuilder getStructValueOrBuilder() {
         if (this.kindCase_ == 5 && this.structValueBuilder_ != null) {
            return this.structValueBuilder_.getMessageOrBuilder();
         } else {
            return this.kindCase_ == 5 ? (Struct)this.kind_ : Struct.getDefaultInstance();
         }
      }

      private SingleFieldBuilderV3<Struct, Struct.Builder, StructOrBuilder> getStructValueFieldBuilder() {
         if (this.structValueBuilder_ == null) {
            if (this.kindCase_ != 5) {
               this.kind_ = Struct.getDefaultInstance();
            }

            this.structValueBuilder_ = new SingleFieldBuilderV3<>((Struct)this.kind_, this.getParentForChildren(), this.isClean());
            this.kind_ = null;
         }

         this.kindCase_ = 5;
         this.onChanged();
         return this.structValueBuilder_;
      }

      @Override
      public boolean hasListValue() {
         return this.kindCase_ == 6;
      }

      @Override
      public ListValue getListValue() {
         if (this.listValueBuilder_ == null) {
            return this.kindCase_ == 6 ? (ListValue)this.kind_ : ListValue.getDefaultInstance();
         } else {
            return this.kindCase_ == 6 ? this.listValueBuilder_.getMessage() : ListValue.getDefaultInstance();
         }
      }

      public Value.Builder setListValue(ListValue var1) {
         if (this.listValueBuilder_ == null) {
            if (var1 == null) {
               throw new NullPointerException();
            }

            this.kind_ = var1;
            this.onChanged();
         } else {
            this.listValueBuilder_.setMessage(var1);
         }

         this.kindCase_ = 6;
         return this;
      }

      public Value.Builder setListValue(ListValue.Builder var1) {
         if (this.listValueBuilder_ == null) {
            this.kind_ = var1.build();
            this.onChanged();
         } else {
            this.listValueBuilder_.setMessage(var1.build());
         }

         this.kindCase_ = 6;
         return this;
      }

      public Value.Builder mergeListValue(ListValue var1) {
         if (this.listValueBuilder_ == null) {
            if (this.kindCase_ == 6 && this.kind_ != ListValue.getDefaultInstance()) {
               this.kind_ = ListValue.newBuilder((ListValue)this.kind_).mergeFrom(var1).buildPartial();
            } else {
               this.kind_ = var1;
            }

            this.onChanged();
         } else {
            if (this.kindCase_ == 6) {
               this.listValueBuilder_.mergeFrom(var1);
            }

            this.listValueBuilder_.setMessage(var1);
         }

         this.kindCase_ = 6;
         return this;
      }

      public Value.Builder clearListValue() {
         if (this.listValueBuilder_ == null) {
            if (this.kindCase_ == 6) {
               this.kindCase_ = 0;
               this.kind_ = null;
               this.onChanged();
            }
         } else {
            if (this.kindCase_ == 6) {
               this.kindCase_ = 0;
               this.kind_ = null;
            }

            this.listValueBuilder_.clear();
         }

         return this;
      }

      public ListValue.Builder getListValueBuilder() {
         return this.getListValueFieldBuilder().getBuilder();
      }

      @Override
      public ListValueOrBuilder getListValueOrBuilder() {
         if (this.kindCase_ == 6 && this.listValueBuilder_ != null) {
            return this.listValueBuilder_.getMessageOrBuilder();
         } else {
            return this.kindCase_ == 6 ? (ListValue)this.kind_ : ListValue.getDefaultInstance();
         }
      }

      private SingleFieldBuilderV3<ListValue, ListValue.Builder, ListValueOrBuilder> getListValueFieldBuilder() {
         if (this.listValueBuilder_ == null) {
            if (this.kindCase_ != 6) {
               this.kind_ = ListValue.getDefaultInstance();
            }

            this.listValueBuilder_ = new SingleFieldBuilderV3<>((ListValue)this.kind_, this.getParentForChildren(), this.isClean());
            this.kind_ = null;
         }

         this.kindCase_ = 6;
         this.onChanged();
         return this.listValueBuilder_;
      }

      public final Value.Builder setUnknownFields(UnknownFieldSet var1) {
         return (Value.Builder)super.setUnknownFields(var1);
      }

      public final Value.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (Value.Builder)super.mergeUnknownFields(var1);
      }
   }

   public static enum KindCase implements AbstractMessageLite.InternalOneOfEnum, Internal.EnumLite {
      NULL_VALUE(1),
      NUMBER_VALUE(2),
      STRING_VALUE(3),
      BOOL_VALUE(4),
      STRUCT_VALUE(5),
      LIST_VALUE(6),
      KIND_NOT_SET(0);

      private final int value;

      private KindCase(int var3) {
         this.value = var3;
      }

      @Deprecated
      public static Value.KindCase valueOf(int var0) {
         return forNumber(var0);
      }

      public static Value.KindCase forNumber(int var0) {
         switch (var0) {
            case 0:
               return KIND_NOT_SET;
            case 1:
               return NULL_VALUE;
            case 2:
               return NUMBER_VALUE;
            case 3:
               return STRING_VALUE;
            case 4:
               return BOOL_VALUE;
            case 5:
               return STRUCT_VALUE;
            case 6:
               return LIST_VALUE;
            default:
               return null;
         }
      }

      @Override
      public int getNumber() {
         return this.value;
      }
   }
}
