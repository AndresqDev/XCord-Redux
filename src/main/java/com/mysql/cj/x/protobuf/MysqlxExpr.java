package com.mysql.cj.x.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxExpr {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Expr_descriptor = getDescriptor().getMessageTypes().get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Expr_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Expr_Expr_descriptor,
      new String[]{"Type", "Identifier", "Variable", "Literal", "FunctionCall", "Operator", "Position", "Object", "Array"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Identifier_descriptor = getDescriptor().getMessageTypes().get(1);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Expr_Identifier_descriptor, new String[]{"Name", "SchemaName"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_DocumentPathItem_descriptor = getDescriptor().getMessageTypes().get(2);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Expr_DocumentPathItem_descriptor, new String[]{"Type", "Value", "Index"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor = getDescriptor().getMessageTypes().get(3);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor, new String[]{"DocumentPath", "Name", "TableName", "SchemaName"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_FunctionCall_descriptor = getDescriptor().getMessageTypes().get(4);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Expr_FunctionCall_descriptor, new String[]{"Name", "Param"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Operator_descriptor = getDescriptor().getMessageTypes().get(5);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Operator_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Expr_Operator_descriptor, new String[]{"Name", "Param"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Object_descriptor = getDescriptor().getMessageTypes().get(6);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Object_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Expr_Object_descriptor, new String[]{"Fld"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Object_ObjectField_descriptor = internal_static_Mysqlx_Expr_Object_descriptor.getNestedTypes()
      .get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Expr_Object_ObjectField_descriptor, new String[]{"Key", "Value"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expr_Array_descriptor = getDescriptor().getMessageTypes().get(7);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expr_Array_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Expr_Array_descriptor, new String[]{"Value"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxExpr() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
   }

   public static void registerAllExtensions(ExtensionRegistry registry) {
      registerAllExtensions((ExtensionRegistryLite)registry);
   }

   public static Descriptors.FileDescriptor getDescriptor() {
      return descriptor;
   }

   static {
      String[] descriptorData = new String[]{
         "\n\u0011mysqlx_expr.proto\u0012\u000bMysqlx.Expr\u001a\u0016mysqlx_datatypes.proto\"Ä\u0003\n\u0004Expr\u0012$\n\u0004type\u0018\u0001 \u0002(\u000e2\u0016.Mysqlx.Expr.Expr.Type\u00121\n\nidentifier\u0018\u0002 \u0001(\u000b2\u001d.Mysqlx.Expr.ColumnIdentifier\u0012\u0010\n\bvariable\u0018\u0003 \u0001(\t\u0012)\n\u0007literal\u0018\u0004 \u0001(\u000b2\u0018.Mysqlx.Datatypes.Scalar\u00120\n\rfunction_call\u0018\u0005 \u0001(\u000b2\u0019.Mysqlx.Expr.FunctionCall\u0012'\n\boperator\u0018\u0006 \u0001(\u000b2\u0015.Mysqlx.Expr.Operator\u0012\u0010\n\bposition\u0018\u0007 \u0001(\r\u0012#\n\u0006object\u0018\b \u0001(\u000b2\u0013.Mysqlx.Expr.Object\u0012!\n\u0005array\u0018\t \u0001(\u000b2\u0012.Mysqlx.Expr.Array\"q\n\u0004Type\u0012\t\n\u0005IDENT\u0010\u0001\u0012\u000b\n\u0007LITERAL\u0010\u0002\u0012\f\n\bVARIABLE\u0010\u0003\u0012\r\n\tFUNC_CALL\u0010\u0004\u0012\f\n\bOPERATOR\u0010\u0005\u0012\u000f\n\u000bPLACEHOLDER\u0010\u0006\u0012\n\n\u0006OBJECT\u0010\u0007\u0012\t\n\u0005ARRAY\u0010\b\"/\n\nIdentifier\u0012\f\n\u0004name\u0018\u0001 \u0002(\t\u0012\u0013\n\u000bschema_name\u0018\u0002 \u0001(\t\"Ë\u0001\n\u0010DocumentPathItem\u00120\n\u0004type\u0018\u0001 \u0002(\u000e2\".Mysqlx.Expr.DocumentPathItem.Type\u0012\r\n\u0005value\u0018\u0002 \u0001(\t\u0012\r\n\u0005index\u0018\u0003 \u0001(\r\"g\n\u0004Type\u0012\n\n\u0006MEMBER\u0010\u0001\u0012\u0013\n\u000fMEMBER_ASTERISK\u0010\u0002\u0012\u000f\n\u000bARRAY_INDEX\u0010\u0003\u0012\u0018\n\u0014ARRAY_INDEX_ASTERISK\u0010\u0004\u0012\u0013\n\u000fDOUBLE_ASTERISK\u0010\u0005\"\u007f\n\u0010ColumnIdentifier\u00124\n\rdocument_path\u0018\u0001 \u0003(\u000b2\u001d.Mysqlx.Expr.DocumentPathItem\u0012\f\n\u0004name\u0018\u0002 \u0001(\t\u0012\u0012\n\ntable_name\u0018\u0003 \u0001(\t\u0012\u0013\n\u000bschema_name\u0018\u0004 \u0001(\t\"W\n\fFunctionCall\u0012%\n\u0004name\u0018\u0001 \u0002(\u000b2\u0017.Mysqlx.Expr.Identifier\u0012 \n\u0005param\u0018\u0002 \u0003(\u000b2\u0011.Mysqlx.Expr.Expr\":\n\bOperator\u0012\f\n\u0004name\u0018\u0001 \u0002(\t\u0012 \n\u0005param\u0018\u0002 \u0003(\u000b2\u0011.Mysqlx.Expr.Expr\"t\n\u0006Object\u0012,\n\u0003fld\u0018\u0001 \u0003(\u000b2\u001f.Mysqlx.Expr.Object.ObjectField\u001a<\n\u000bObjectField\u0012\u000b\n\u0003key\u0018\u0001 \u0002(\t\u0012 \n\u0005value\u0018\u0002 \u0002(\u000b2\u0011.Mysqlx.Expr.Expr\")\n\u0005Array\u0012 \n\u0005value\u0018\u0001 \u0003(\u000b2\u0011.Mysqlx.Expr.ExprB\u0019\n\u0017com.mysql.cj.x.protobuf"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{MysqlxDatatypes.getDescriptor()});
      MysqlxDatatypes.getDescriptor();
   }

   public static final class Array extends GeneratedMessageV3 implements MysqlxExpr.ArrayOrBuilder {
      private static final long serialVersionUID = 0L;
      public static final int VALUE_FIELD_NUMBER = 1;
      private List<MysqlxExpr.Expr> value_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxExpr.Array DEFAULT_INSTANCE = new MysqlxExpr.Array();
      @Deprecated
      public static final Parser<MysqlxExpr.Array> PARSER = new AbstractParser<MysqlxExpr.Array>() {
         public MysqlxExpr.Array parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxExpr.Array(input, extensionRegistry);
         }
      };

      private Array(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Array() {
         this.value_ = Collections.emptyList();
      }

      @Override
      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxExpr.Array();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Array(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        if ((mutable_bitField0_ & 1) == 0) {
                           this.value_ = new ArrayList<>();
                           mutable_bitField0_ |= 1;
                        }

                        this.value_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry));
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var11) {
               throw var11.setUnfinishedMessage(this);
            } catch (IOException var12) {
               throw new InvalidProtocolBufferException(var12).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 1) != 0) {
                  this.value_ = Collections.unmodifiableList(this.value_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Array_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Array_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxExpr.Array.class, MysqlxExpr.Array.Builder.class);
      }

      @Override
      public List<MysqlxExpr.Expr> getValueList() {
         return this.value_;
      }

      @Override
      public List<? extends MysqlxExpr.ExprOrBuilder> getValueOrBuilderList() {
         return this.value_;
      }

      @Override
      public int getValueCount() {
         return this.value_.size();
      }

      @Override
      public MysqlxExpr.Expr getValue(int index) {
         return this.value_.get(index);
      }

      @Override
      public MysqlxExpr.ExprOrBuilder getValueOrBuilder(int index) {
         return this.value_.get(index);
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            for (int i = 0; i < this.getValueCount(); i++) {
               if (!this.getValue(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         for (int i = 0; i < this.value_.size(); i++) {
            output.writeMessage(1, this.value_.get(i));
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;

            for (int i = 0; i < this.value_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(1, this.value_.get(i));
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxExpr.Array)) {
            return super.equals(obj);
         } else {
            MysqlxExpr.Array other = (MysqlxExpr.Array)obj;
            return !this.getValueList().equals(other.getValueList()) ? false : this.unknownFields.equals(other.unknownFields);
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.getValueCount() > 0) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getValueList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxExpr.Array parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Array parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Array parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Array parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Array parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Array parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Array parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Array parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.Array parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Array parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.Array parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Array parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxExpr.Array.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxExpr.Array.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxExpr.Array.Builder newBuilder(MysqlxExpr.Array prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxExpr.Array.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxExpr.Array.Builder() : new MysqlxExpr.Array.Builder().mergeFrom(this);
      }

      protected MysqlxExpr.Array.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxExpr.Array.Builder(parent);
      }

      public static MysqlxExpr.Array getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxExpr.Array> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxExpr.Array> getParserForType() {
         return PARSER;
      }

      public MysqlxExpr.Array getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxExpr.Array.Builder> implements MysqlxExpr.ArrayOrBuilder {
         private int bitField0_;
         private List<MysqlxExpr.Expr> value_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> valueBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Array_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Array_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxExpr.Array.class, MysqlxExpr.Array.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.Array.alwaysUseFieldBuilders) {
               this.getValueFieldBuilder();
            }
         }

         public MysqlxExpr.Array.Builder clear() {
            super.clear();
            if (this.valueBuilder_ == null) {
               this.value_ = Collections.emptyList();
               this.bitField0_ &= -2;
            } else {
               this.valueBuilder_.clear();
            }

            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Array_descriptor;
         }

         public MysqlxExpr.Array getDefaultInstanceForType() {
            return MysqlxExpr.Array.getDefaultInstance();
         }

         public MysqlxExpr.Array build() {
            MysqlxExpr.Array result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxExpr.Array buildPartial() {
            MysqlxExpr.Array result = new MysqlxExpr.Array(this);
            int from_bitField0_ = this.bitField0_;
            if (this.valueBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0) {
                  this.value_ = Collections.unmodifiableList(this.value_);
                  this.bitField0_ &= -2;
               }

               result.value_ = this.value_;
            } else {
               result.value_ = this.valueBuilder_.build();
            }

            this.onBuilt();
            return result;
         }

         public MysqlxExpr.Array.Builder clone() {
            return (MysqlxExpr.Array.Builder)super.clone();
         }

         public MysqlxExpr.Array.Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.Array.Builder)super.setField(field, value);
         }

         public MysqlxExpr.Array.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxExpr.Array.Builder)super.clearField(field);
         }

         public MysqlxExpr.Array.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxExpr.Array.Builder)super.clearOneof(oneof);
         }

         public MysqlxExpr.Array.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (MysqlxExpr.Array.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxExpr.Array.Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.Array.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxExpr.Array.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxExpr.Array) {
               return this.mergeFrom((MysqlxExpr.Array)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxExpr.Array.Builder mergeFrom(MysqlxExpr.Array other) {
            if (other == MysqlxExpr.Array.getDefaultInstance()) {
               return this;
            } else {
               if (this.valueBuilder_ == null) {
                  if (!other.value_.isEmpty()) {
                     if (this.value_.isEmpty()) {
                        this.value_ = other.value_;
                        this.bitField0_ &= -2;
                     } else {
                        this.ensureValueIsMutable();
                        this.value_.addAll(other.value_);
                     }

                     this.onChanged();
                  }
               } else if (!other.value_.isEmpty()) {
                  if (this.valueBuilder_.isEmpty()) {
                     this.valueBuilder_.dispose();
                     this.valueBuilder_ = null;
                     this.value_ = other.value_;
                     this.bitField0_ &= -2;
                     this.valueBuilder_ = MysqlxExpr.Array.alwaysUseFieldBuilders ? this.getValueFieldBuilder() : null;
                  } else {
                     this.valueBuilder_.addAllMessages(other.value_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            for (int i = 0; i < this.getValueCount(); i++) {
               if (!this.getValue(i).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public MysqlxExpr.Array.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxExpr.Array parsedMessage = null;

            try {
               parsedMessage = MysqlxExpr.Array.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxExpr.Array)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         private void ensureValueIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
               this.value_ = new ArrayList<>(this.value_);
               this.bitField0_ |= 1;
            }
         }

         @Override
         public List<MysqlxExpr.Expr> getValueList() {
            return this.valueBuilder_ == null ? Collections.unmodifiableList(this.value_) : this.valueBuilder_.getMessageList();
         }

         @Override
         public int getValueCount() {
            return this.valueBuilder_ == null ? this.value_.size() : this.valueBuilder_.getCount();
         }

         @Override
         public MysqlxExpr.Expr getValue(int index) {
            return this.valueBuilder_ == null ? this.value_.get(index) : this.valueBuilder_.getMessage(index);
         }

         public MysqlxExpr.Array.Builder setValue(int index, MysqlxExpr.Expr value) {
            if (this.valueBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureValueIsMutable();
               this.value_.set(index, value);
               this.onChanged();
            } else {
               this.valueBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxExpr.Array.Builder setValue(int index, MysqlxExpr.Expr.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               this.value_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.valueBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.Array.Builder addValue(MysqlxExpr.Expr value) {
            if (this.valueBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureValueIsMutable();
               this.value_.add(value);
               this.onChanged();
            } else {
               this.valueBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxExpr.Array.Builder addValue(int index, MysqlxExpr.Expr value) {
            if (this.valueBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureValueIsMutable();
               this.value_.add(index, value);
               this.onChanged();
            } else {
               this.valueBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxExpr.Array.Builder addValue(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               this.value_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.valueBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.Array.Builder addValue(int index, MysqlxExpr.Expr.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               this.value_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.valueBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.Array.Builder addAllValue(Iterable<? extends MysqlxExpr.Expr> values) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.value_);
               this.onChanged();
            } else {
               this.valueBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxExpr.Array.Builder clearValue() {
            if (this.valueBuilder_ == null) {
               this.value_ = Collections.emptyList();
               this.bitField0_ &= -2;
               this.onChanged();
            } else {
               this.valueBuilder_.clear();
            }

            return this;
         }

         public MysqlxExpr.Array.Builder removeValue(int index) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               this.value_.remove(index);
               this.onChanged();
            } else {
               this.valueBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxExpr.Expr.Builder getValueBuilder(int index) {
            return this.getValueFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getValueOrBuilder(int index) {
            return this.valueBuilder_ == null ? this.value_.get(index) : this.valueBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxExpr.ExprOrBuilder> getValueOrBuilderList() {
            return this.valueBuilder_ != null ? this.valueBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.value_);
         }

         public MysqlxExpr.Expr.Builder addValueBuilder() {
            return this.getValueFieldBuilder().addBuilder(MysqlxExpr.Expr.getDefaultInstance());
         }

         public MysqlxExpr.Expr.Builder addValueBuilder(int index) {
            return this.getValueFieldBuilder().addBuilder(index, MysqlxExpr.Expr.getDefaultInstance());
         }

         public List<MysqlxExpr.Expr.Builder> getValueBuilderList() {
            return this.getValueFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getValueFieldBuilder() {
            if (this.valueBuilder_ == null) {
               this.valueBuilder_ = new RepeatedFieldBuilderV3<>(this.value_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
               this.value_ = null;
            }

            return this.valueBuilder_;
         }

         public final MysqlxExpr.Array.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.Array.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxExpr.Array.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.Array.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface ArrayOrBuilder extends MessageOrBuilder {
      List<MysqlxExpr.Expr> getValueList();

      MysqlxExpr.Expr getValue(int var1);

      int getValueCount();

      List<? extends MysqlxExpr.ExprOrBuilder> getValueOrBuilderList();

      MysqlxExpr.ExprOrBuilder getValueOrBuilder(int var1);
   }

   public static final class ColumnIdentifier extends GeneratedMessageV3 implements MysqlxExpr.ColumnIdentifierOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int DOCUMENT_PATH_FIELD_NUMBER = 1;
      private List<MysqlxExpr.DocumentPathItem> documentPath_;
      public static final int NAME_FIELD_NUMBER = 2;
      private volatile java.lang.Object name_;
      public static final int TABLE_NAME_FIELD_NUMBER = 3;
      private volatile java.lang.Object tableName_;
      public static final int SCHEMA_NAME_FIELD_NUMBER = 4;
      private volatile java.lang.Object schemaName_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxExpr.ColumnIdentifier DEFAULT_INSTANCE = new MysqlxExpr.ColumnIdentifier();
      @Deprecated
      public static final Parser<MysqlxExpr.ColumnIdentifier> PARSER = new AbstractParser<MysqlxExpr.ColumnIdentifier>() {
         public MysqlxExpr.ColumnIdentifier parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxExpr.ColumnIdentifier(input, extensionRegistry);
         }
      };

      private ColumnIdentifier(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private ColumnIdentifier() {
         this.documentPath_ = Collections.emptyList();
         this.name_ = "";
         this.tableName_ = "";
         this.schemaName_ = "";
      }

      @Override
      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxExpr.ColumnIdentifier();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private ColumnIdentifier(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        if ((mutable_bitField0_ & 1) == 0) {
                           this.documentPath_ = new ArrayList<>();
                           mutable_bitField0_ |= 1;
                        }

                        this.documentPath_.add(input.readMessage(MysqlxExpr.DocumentPathItem.PARSER, extensionRegistry));
                        break;
                     case 18: {
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 1;
                        this.name_ = bs;
                        break;
                     }
                     case 26: {
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.tableName_ = bs;
                        break;
                     }
                     case 34: {
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 4;
                        this.schemaName_ = bs;
                        break;
                     }
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 1) != 0) {
                  this.documentPath_ = Collections.unmodifiableList(this.documentPath_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxExpr.ColumnIdentifier.class, MysqlxExpr.ColumnIdentifier.Builder.class);
      }

      @Override
      public List<MysqlxExpr.DocumentPathItem> getDocumentPathList() {
         return this.documentPath_;
      }

      @Override
      public List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() {
         return this.documentPath_;
      }

      @Override
      public int getDocumentPathCount() {
         return this.documentPath_.size();
      }

      @Override
      public MysqlxExpr.DocumentPathItem getDocumentPath(int index) {
         return this.documentPath_.get(index);
      }

      @Override
      public MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) {
         return this.documentPath_.get(index);
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         java.lang.Object ref = this.name_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.name_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getNameBytes() {
         java.lang.Object ref = this.name_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public boolean hasTableName() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public String getTableName() {
         java.lang.Object ref = this.tableName_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.tableName_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getTableNameBytes() {
         java.lang.Object ref = this.tableName_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.tableName_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public boolean hasSchemaName() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public String getSchemaName() {
         java.lang.Object ref = this.schemaName_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.schemaName_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getSchemaNameBytes() {
         java.lang.Object ref = this.schemaName_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.schemaName_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            for (int i = 0; i < this.getDocumentPathCount(); i++) {
               if (!this.getDocumentPath(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         for (int i = 0; i < this.documentPath_.size(); i++) {
            output.writeMessage(1, this.documentPath_.get(i));
         }

         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.name_);
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 3, this.tableName_);
         }

         if ((this.bitField0_ & 4) != 0) {
            GeneratedMessageV3.writeString(output, 4, this.schemaName_);
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;

            for (int i = 0; i < this.documentPath_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(1, this.documentPath_.get(i));
            }

            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.name_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(3, this.tableName_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += GeneratedMessageV3.computeStringSize(4, this.schemaName_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxExpr.ColumnIdentifier)) {
            return super.equals(obj);
         } else {
            MysqlxExpr.ColumnIdentifier other = (MysqlxExpr.ColumnIdentifier)obj;
            if (!this.getDocumentPathList().equals(other.getDocumentPathList())) {
               return false;
            } else if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else if (this.hasTableName() != other.hasTableName()) {
               return false;
            } else if (this.hasTableName() && !this.getTableName().equals(other.getTableName())) {
               return false;
            } else if (this.hasSchemaName() != other.hasSchemaName()) {
               return false;
            } else {
               return this.hasSchemaName() && !this.getSchemaName().equals(other.getSchemaName()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.getDocumentPathCount() > 0) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getDocumentPathList().hashCode();
            }

            if (this.hasName()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.hasTableName()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getTableName().hashCode();
            }

            if (this.hasSchemaName()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getSchemaName().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxExpr.ColumnIdentifier parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.ColumnIdentifier parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.ColumnIdentifier parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.ColumnIdentifier parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.ColumnIdentifier parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.ColumnIdentifier parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.ColumnIdentifier parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.ColumnIdentifier parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.ColumnIdentifier parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxExpr.ColumnIdentifier parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.ColumnIdentifier parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.ColumnIdentifier parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxExpr.ColumnIdentifier.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxExpr.ColumnIdentifier.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxExpr.ColumnIdentifier.Builder newBuilder(MysqlxExpr.ColumnIdentifier prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxExpr.ColumnIdentifier.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxExpr.ColumnIdentifier.Builder() : new MysqlxExpr.ColumnIdentifier.Builder().mergeFrom(this);
      }

      protected MysqlxExpr.ColumnIdentifier.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxExpr.ColumnIdentifier.Builder(parent);
      }

      public static MysqlxExpr.ColumnIdentifier getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxExpr.ColumnIdentifier> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxExpr.ColumnIdentifier> getParserForType() {
         return PARSER;
      }

      public MysqlxExpr.ColumnIdentifier getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxExpr.ColumnIdentifier.Builder> implements MysqlxExpr.ColumnIdentifierOrBuilder {
         private int bitField0_;
         private List<MysqlxExpr.DocumentPathItem> documentPath_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> documentPathBuilder_;
         private java.lang.Object name_ = "";
         private java.lang.Object tableName_ = "";
         private java.lang.Object schemaName_ = "";

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxExpr.ColumnIdentifier.class, MysqlxExpr.ColumnIdentifier.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.ColumnIdentifier.alwaysUseFieldBuilders) {
               this.getDocumentPathFieldBuilder();
            }
         }

         public MysqlxExpr.ColumnIdentifier.Builder clear() {
            super.clear();
            if (this.documentPathBuilder_ == null) {
               this.documentPath_ = Collections.emptyList();
               this.bitField0_ &= -2;
            } else {
               this.documentPathBuilder_.clear();
            }

            this.name_ = "";
            this.bitField0_ &= -3;
            this.tableName_ = "";
            this.bitField0_ &= -5;
            this.schemaName_ = "";
            this.bitField0_ &= -9;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_ColumnIdentifier_descriptor;
         }

         public MysqlxExpr.ColumnIdentifier getDefaultInstanceForType() {
            return MysqlxExpr.ColumnIdentifier.getDefaultInstance();
         }

         public MysqlxExpr.ColumnIdentifier build() {
            MysqlxExpr.ColumnIdentifier result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxExpr.ColumnIdentifier buildPartial() {
            MysqlxExpr.ColumnIdentifier result = new MysqlxExpr.ColumnIdentifier(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if (this.documentPathBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0) {
                  this.documentPath_ = Collections.unmodifiableList(this.documentPath_);
                  this.bitField0_ &= -2;
               }

               result.documentPath_ = this.documentPath_;
            } else {
               result.documentPath_ = this.documentPathBuilder_.build();
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 1;
            }

            result.name_ = this.name_;
            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 2;
            }

            result.tableName_ = this.tableName_;
            if ((from_bitField0_ & 8) != 0) {
               to_bitField0_ |= 4;
            }

            result.schemaName_ = this.schemaName_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxExpr.ColumnIdentifier.Builder clone() {
            return (MysqlxExpr.ColumnIdentifier.Builder)super.clone();
         }

         public MysqlxExpr.ColumnIdentifier.Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.ColumnIdentifier.Builder)super.setField(field, value);
         }

         public MysqlxExpr.ColumnIdentifier.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxExpr.ColumnIdentifier.Builder)super.clearField(field);
         }

         public MysqlxExpr.ColumnIdentifier.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxExpr.ColumnIdentifier.Builder)super.clearOneof(oneof);
         }

         public MysqlxExpr.ColumnIdentifier.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (MysqlxExpr.ColumnIdentifier.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxExpr.ColumnIdentifier.Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.ColumnIdentifier.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxExpr.ColumnIdentifier.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxExpr.ColumnIdentifier) {
               return this.mergeFrom((MysqlxExpr.ColumnIdentifier)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxExpr.ColumnIdentifier.Builder mergeFrom(MysqlxExpr.ColumnIdentifier other) {
            if (other == MysqlxExpr.ColumnIdentifier.getDefaultInstance()) {
               return this;
            } else {
               if (this.documentPathBuilder_ == null) {
                  if (!other.documentPath_.isEmpty()) {
                     if (this.documentPath_.isEmpty()) {
                        this.documentPath_ = other.documentPath_;
                        this.bitField0_ &= -2;
                     } else {
                        this.ensureDocumentPathIsMutable();
                        this.documentPath_.addAll(other.documentPath_);
                     }

                     this.onChanged();
                  }
               } else if (!other.documentPath_.isEmpty()) {
                  if (this.documentPathBuilder_.isEmpty()) {
                     this.documentPathBuilder_.dispose();
                     this.documentPathBuilder_ = null;
                     this.documentPath_ = other.documentPath_;
                     this.bitField0_ &= -2;
                     this.documentPathBuilder_ = MysqlxExpr.ColumnIdentifier.alwaysUseFieldBuilders ? this.getDocumentPathFieldBuilder() : null;
                  } else {
                     this.documentPathBuilder_.addAllMessages(other.documentPath_);
                  }
               }

               if (other.hasName()) {
                  this.bitField0_ |= 2;
                  this.name_ = other.name_;
                  this.onChanged();
               }

               if (other.hasTableName()) {
                  this.bitField0_ |= 4;
                  this.tableName_ = other.tableName_;
                  this.onChanged();
               }

               if (other.hasSchemaName()) {
                  this.bitField0_ |= 8;
                  this.schemaName_ = other.schemaName_;
                  this.onChanged();
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            for (int i = 0; i < this.getDocumentPathCount(); i++) {
               if (!this.getDocumentPath(i).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public MysqlxExpr.ColumnIdentifier.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxExpr.ColumnIdentifier parsedMessage = null;

            try {
               parsedMessage = MysqlxExpr.ColumnIdentifier.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxExpr.ColumnIdentifier)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         private void ensureDocumentPathIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
               this.documentPath_ = new ArrayList<>(this.documentPath_);
               this.bitField0_ |= 1;
            }
         }

         @Override
         public List<MysqlxExpr.DocumentPathItem> getDocumentPathList() {
            return this.documentPathBuilder_ == null ? Collections.unmodifiableList(this.documentPath_) : this.documentPathBuilder_.getMessageList();
         }

         @Override
         public int getDocumentPathCount() {
            return this.documentPathBuilder_ == null ? this.documentPath_.size() : this.documentPathBuilder_.getCount();
         }

         @Override
         public MysqlxExpr.DocumentPathItem getDocumentPath(int index) {
            return this.documentPathBuilder_ == null ? this.documentPath_.get(index) : this.documentPathBuilder_.getMessage(index);
         }

         public MysqlxExpr.ColumnIdentifier.Builder setDocumentPath(int index, MysqlxExpr.DocumentPathItem value) {
            if (this.documentPathBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureDocumentPathIsMutable();
               this.documentPath_.set(index, value);
               this.onChanged();
            } else {
               this.documentPathBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder setDocumentPath(int index, MysqlxExpr.DocumentPathItem.Builder builderForValue) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.documentPathBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder addDocumentPath(MysqlxExpr.DocumentPathItem value) {
            if (this.documentPathBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(value);
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder addDocumentPath(int index, MysqlxExpr.DocumentPathItem value) {
            if (this.documentPathBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(index, value);
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder addDocumentPath(MysqlxExpr.DocumentPathItem.Builder builderForValue) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder addDocumentPath(int index, MysqlxExpr.DocumentPathItem.Builder builderForValue) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.documentPathBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder addAllDocumentPath(Iterable<? extends MysqlxExpr.DocumentPathItem> values) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.documentPath_);
               this.onChanged();
            } else {
               this.documentPathBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder clearDocumentPath() {
            if (this.documentPathBuilder_ == null) {
               this.documentPath_ = Collections.emptyList();
               this.bitField0_ &= -2;
               this.onChanged();
            } else {
               this.documentPathBuilder_.clear();
            }

            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder removeDocumentPath(int index) {
            if (this.documentPathBuilder_ == null) {
               this.ensureDocumentPathIsMutable();
               this.documentPath_.remove(index);
               this.onChanged();
            } else {
               this.documentPathBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxExpr.DocumentPathItem.Builder getDocumentPathBuilder(int index) {
            return this.getDocumentPathFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int index) {
            return this.documentPathBuilder_ == null ? this.documentPath_.get(index) : this.documentPathBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList() {
            return this.documentPathBuilder_ != null ? this.documentPathBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.documentPath_);
         }

         public MysqlxExpr.DocumentPathItem.Builder addDocumentPathBuilder() {
            return this.getDocumentPathFieldBuilder().addBuilder(MysqlxExpr.DocumentPathItem.getDefaultInstance());
         }

         public MysqlxExpr.DocumentPathItem.Builder addDocumentPathBuilder(int index) {
            return this.getDocumentPathFieldBuilder().addBuilder(index, MysqlxExpr.DocumentPathItem.getDefaultInstance());
         }

         public List<MysqlxExpr.DocumentPathItem.Builder> getDocumentPathBuilderList() {
            return this.getDocumentPathFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxExpr.DocumentPathItem, MysqlxExpr.DocumentPathItem.Builder, MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathFieldBuilder() {
            if (this.documentPathBuilder_ == null) {
               this.documentPathBuilder_ = new RepeatedFieldBuilderV3<>(
                  this.documentPath_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean()
               );
               this.documentPath_ = null;
            }

            return this.documentPathBuilder_;
         }

         @Override
         public boolean hasName() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getName() {
            java.lang.Object ref = this.name_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.name_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getNameBytes() {
            java.lang.Object ref = this.name_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.name_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxExpr.ColumnIdentifier.Builder setName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxExpr.ColumnIdentifier.Builder clearName() {
            this.bitField0_ &= -3;
            this.name_ = MysqlxExpr.ColumnIdentifier.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder setNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasTableName() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public String getTableName() {
            java.lang.Object ref = this.tableName_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.tableName_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getTableNameBytes() {
            java.lang.Object ref = this.tableName_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.tableName_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxExpr.ColumnIdentifier.Builder setTableName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.tableName_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxExpr.ColumnIdentifier.Builder clearTableName() {
            this.bitField0_ &= -5;
            this.tableName_ = MysqlxExpr.ColumnIdentifier.getDefaultInstance().getTableName();
            this.onChanged();
            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder setTableNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.tableName_ = value;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasSchemaName() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public String getSchemaName() {
            java.lang.Object ref = this.schemaName_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.schemaName_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getSchemaNameBytes() {
            java.lang.Object ref = this.schemaName_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.schemaName_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxExpr.ColumnIdentifier.Builder setSchemaName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.schemaName_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxExpr.ColumnIdentifier.Builder clearSchemaName() {
            this.bitField0_ &= -9;
            this.schemaName_ = MysqlxExpr.ColumnIdentifier.getDefaultInstance().getSchemaName();
            this.onChanged();
            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder setSchemaNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.schemaName_ = value;
               this.onChanged();
               return this;
            }
         }

         public final MysqlxExpr.ColumnIdentifier.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.ColumnIdentifier.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxExpr.ColumnIdentifier.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.ColumnIdentifier.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface ColumnIdentifierOrBuilder extends MessageOrBuilder {
      List<MysqlxExpr.DocumentPathItem> getDocumentPathList();

      MysqlxExpr.DocumentPathItem getDocumentPath(int var1);

      int getDocumentPathCount();

      List<? extends MysqlxExpr.DocumentPathItemOrBuilder> getDocumentPathOrBuilderList();

      MysqlxExpr.DocumentPathItemOrBuilder getDocumentPathOrBuilder(int var1);

      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasTableName();

      String getTableName();

      ByteString getTableNameBytes();

      boolean hasSchemaName();

      String getSchemaName();

      ByteString getSchemaNameBytes();
   }

   public static final class DocumentPathItem extends GeneratedMessageV3 implements MysqlxExpr.DocumentPathItemOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int TYPE_FIELD_NUMBER = 1;
      private int type_;
      public static final int VALUE_FIELD_NUMBER = 2;
      private volatile java.lang.Object value_;
      public static final int INDEX_FIELD_NUMBER = 3;
      private int index_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxExpr.DocumentPathItem DEFAULT_INSTANCE = new MysqlxExpr.DocumentPathItem();
      @Deprecated
      public static final Parser<MysqlxExpr.DocumentPathItem> PARSER = new AbstractParser<MysqlxExpr.DocumentPathItem>() {
         public MysqlxExpr.DocumentPathItem parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxExpr.DocumentPathItem(input, extensionRegistry);
         }
      };

      private DocumentPathItem(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private DocumentPathItem() {
         this.type_ = 1;
         this.value_ = "";
      }

      @Override
      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxExpr.DocumentPathItem();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private DocumentPathItem(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 8:
                        int rawValue = input.readEnum();
                        MysqlxExpr.DocumentPathItem.Type value = MysqlxExpr.DocumentPathItem.Type.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(1, rawValue);
                        } else {
                           this.bitField0_ |= 1;
                           this.type_ = rawValue;
                        }
                        break;
                     case 18:
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.value_ = bs;
                        break;
                     case 24:
                        this.bitField0_ |= 4;
                        this.index_ = input.readUInt32();
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw new InvalidProtocolBufferException(var14).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxExpr.DocumentPathItem.class, MysqlxExpr.DocumentPathItem.Builder.class);
      }

      @Override
      public boolean hasType() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxExpr.DocumentPathItem.Type getType() {
         MysqlxExpr.DocumentPathItem.Type result = MysqlxExpr.DocumentPathItem.Type.valueOf(this.type_);
         return result == null ? MysqlxExpr.DocumentPathItem.Type.MEMBER : result;
      }

      @Override
      public boolean hasValue() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public String getValue() {
         java.lang.Object ref = this.value_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.value_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getValueBytes() {
         java.lang.Object ref = this.value_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.value_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public boolean hasIndex() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public int getIndex() {
         return this.index_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasType()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeEnum(1, this.type_);
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.value_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeUInt32(3, this.index_);
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeEnumSize(1, this.type_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.value_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeUInt32Size(3, this.index_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxExpr.DocumentPathItem)) {
            return super.equals(obj);
         } else {
            MysqlxExpr.DocumentPathItem other = (MysqlxExpr.DocumentPathItem)obj;
            if (this.hasType() != other.hasType()) {
               return false;
            } else if (this.hasType() && this.type_ != other.type_) {
               return false;
            } else if (this.hasValue() != other.hasValue()) {
               return false;
            } else if (this.hasValue() && !this.getValue().equals(other.getValue())) {
               return false;
            } else if (this.hasIndex() != other.hasIndex()) {
               return false;
            } else {
               return this.hasIndex() && this.getIndex() != other.getIndex() ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasType()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.type_;
            }

            if (this.hasValue()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getValue().hashCode();
            }

            if (this.hasIndex()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getIndex();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxExpr.DocumentPathItem parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.DocumentPathItem parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.DocumentPathItem parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.DocumentPathItem parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.DocumentPathItem parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.DocumentPathItem parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.DocumentPathItem parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.DocumentPathItem parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.DocumentPathItem parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxExpr.DocumentPathItem parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.DocumentPathItem parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.DocumentPathItem parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxExpr.DocumentPathItem.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxExpr.DocumentPathItem.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxExpr.DocumentPathItem.Builder newBuilder(MysqlxExpr.DocumentPathItem prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxExpr.DocumentPathItem.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxExpr.DocumentPathItem.Builder() : new MysqlxExpr.DocumentPathItem.Builder().mergeFrom(this);
      }

      protected MysqlxExpr.DocumentPathItem.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxExpr.DocumentPathItem.Builder(parent);
      }

      public static MysqlxExpr.DocumentPathItem getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxExpr.DocumentPathItem> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxExpr.DocumentPathItem> getParserForType() {
         return PARSER;
      }

      public MysqlxExpr.DocumentPathItem getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxExpr.DocumentPathItem.Builder> implements MysqlxExpr.DocumentPathItemOrBuilder {
         private int bitField0_;
         private int type_ = 1;
         private java.lang.Object value_ = "";
         private int index_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxExpr.DocumentPathItem.class, MysqlxExpr.DocumentPathItem.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.DocumentPathItem.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxExpr.DocumentPathItem.Builder clear() {
            super.clear();
            this.type_ = 1;
            this.bitField0_ &= -2;
            this.value_ = "";
            this.bitField0_ &= -3;
            this.index_ = 0;
            this.bitField0_ &= -5;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_DocumentPathItem_descriptor;
         }

         public MysqlxExpr.DocumentPathItem getDefaultInstanceForType() {
            return MysqlxExpr.DocumentPathItem.getDefaultInstance();
         }

         public MysqlxExpr.DocumentPathItem build() {
            MysqlxExpr.DocumentPathItem result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxExpr.DocumentPathItem buildPartial() {
            MysqlxExpr.DocumentPathItem result = new MysqlxExpr.DocumentPathItem(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.type_ = this.type_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.value_ = this.value_;
            if ((from_bitField0_ & 4) != 0) {
               result.index_ = this.index_;
               to_bitField0_ |= 4;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxExpr.DocumentPathItem.Builder clone() {
            return (MysqlxExpr.DocumentPathItem.Builder)super.clone();
         }

         public MysqlxExpr.DocumentPathItem.Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.DocumentPathItem.Builder)super.setField(field, value);
         }

         public MysqlxExpr.DocumentPathItem.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxExpr.DocumentPathItem.Builder)super.clearField(field);
         }

         public MysqlxExpr.DocumentPathItem.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxExpr.DocumentPathItem.Builder)super.clearOneof(oneof);
         }

         public MysqlxExpr.DocumentPathItem.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (MysqlxExpr.DocumentPathItem.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxExpr.DocumentPathItem.Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.DocumentPathItem.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxExpr.DocumentPathItem.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxExpr.DocumentPathItem) {
               return this.mergeFrom((MysqlxExpr.DocumentPathItem)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxExpr.DocumentPathItem.Builder mergeFrom(MysqlxExpr.DocumentPathItem other) {
            if (other == MysqlxExpr.DocumentPathItem.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasType()) {
                  this.setType(other.getType());
               }

               if (other.hasValue()) {
                  this.bitField0_ |= 2;
                  this.value_ = other.value_;
                  this.onChanged();
               }

               if (other.hasIndex()) {
                  this.setIndex(other.getIndex());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return this.hasType();
         }

         public MysqlxExpr.DocumentPathItem.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxExpr.DocumentPathItem parsedMessage = null;

            try {
               parsedMessage = MysqlxExpr.DocumentPathItem.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxExpr.DocumentPathItem)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasType() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxExpr.DocumentPathItem.Type getType() {
            MysqlxExpr.DocumentPathItem.Type result = MysqlxExpr.DocumentPathItem.Type.valueOf(this.type_);
            return result == null ? MysqlxExpr.DocumentPathItem.Type.MEMBER : result;
         }

         public MysqlxExpr.DocumentPathItem.Builder setType(MysqlxExpr.DocumentPathItem.Type value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.type_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxExpr.DocumentPathItem.Builder clearType() {
            this.bitField0_ &= -2;
            this.type_ = 1;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasValue() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getValue() {
            java.lang.Object ref = this.value_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.value_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getValueBytes() {
            java.lang.Object ref = this.value_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.value_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxExpr.DocumentPathItem.Builder setValue(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.value_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxExpr.DocumentPathItem.Builder clearValue() {
            this.bitField0_ &= -3;
            this.value_ = MysqlxExpr.DocumentPathItem.getDefaultInstance().getValue();
            this.onChanged();
            return this;
         }

         public MysqlxExpr.DocumentPathItem.Builder setValueBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.value_ = value;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasIndex() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public int getIndex() {
            return this.index_;
         }

         public MysqlxExpr.DocumentPathItem.Builder setIndex(int value) {
            this.bitField0_ |= 4;
            this.index_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxExpr.DocumentPathItem.Builder clearIndex() {
            this.bitField0_ &= -5;
            this.index_ = 0;
            this.onChanged();
            return this;
         }

         public final MysqlxExpr.DocumentPathItem.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.DocumentPathItem.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxExpr.DocumentPathItem.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.DocumentPathItem.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static enum Type implements ProtocolMessageEnum {
         MEMBER(1),
         MEMBER_ASTERISK(2),
         ARRAY_INDEX(3),
         ARRAY_INDEX_ASTERISK(4),
         DOUBLE_ASTERISK(5);

         public static final int MEMBER_VALUE = 1;
         public static final int MEMBER_ASTERISK_VALUE = 2;
         public static final int ARRAY_INDEX_VALUE = 3;
         public static final int ARRAY_INDEX_ASTERISK_VALUE = 4;
         public static final int DOUBLE_ASTERISK_VALUE = 5;
         private static final Internal.EnumLiteMap<MysqlxExpr.DocumentPathItem.Type> internalValueMap = new Internal.EnumLiteMap<MysqlxExpr.DocumentPathItem.Type>() {
            public MysqlxExpr.DocumentPathItem.Type findValueByNumber(int number) {
               return MysqlxExpr.DocumentPathItem.Type.forNumber(number);
            }
         };
         private static final MysqlxExpr.DocumentPathItem.Type[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static MysqlxExpr.DocumentPathItem.Type valueOf(int value) {
            return forNumber(value);
         }

         public static MysqlxExpr.DocumentPathItem.Type forNumber(int value) {
            switch (value) {
               case 1:
                  return MEMBER;
               case 2:
                  return MEMBER_ASTERISK;
               case 3:
                  return ARRAY_INDEX;
               case 4:
                  return ARRAY_INDEX_ASTERISK;
               case 5:
                  return DOUBLE_ASTERISK;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<MysqlxExpr.DocumentPathItem.Type> internalGetValueMap() {
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
            return MysqlxExpr.DocumentPathItem.getDescriptor().getEnumTypes().get(0);
         }

         public static MysqlxExpr.DocumentPathItem.Type valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Type(int value) {
            this.value = value;
         }
      }
   }

   public interface DocumentPathItemOrBuilder extends MessageOrBuilder {
      boolean hasType();

      MysqlxExpr.DocumentPathItem.Type getType();

      boolean hasValue();

      String getValue();

      ByteString getValueBytes();

      boolean hasIndex();

      int getIndex();
   }

   public static final class Expr extends GeneratedMessageV3 implements MysqlxExpr.ExprOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int TYPE_FIELD_NUMBER = 1;
      private int type_;
      public static final int IDENTIFIER_FIELD_NUMBER = 2;
      private MysqlxExpr.ColumnIdentifier identifier_;
      public static final int VARIABLE_FIELD_NUMBER = 3;
      private volatile java.lang.Object variable_;
      public static final int LITERAL_FIELD_NUMBER = 4;
      private MysqlxDatatypes.Scalar literal_;
      public static final int FUNCTION_CALL_FIELD_NUMBER = 5;
      private MysqlxExpr.FunctionCall functionCall_;
      public static final int OPERATOR_FIELD_NUMBER = 6;
      private MysqlxExpr.Operator operator_;
      public static final int POSITION_FIELD_NUMBER = 7;
      private int position_;
      public static final int OBJECT_FIELD_NUMBER = 8;
      private MysqlxExpr.Object object_;
      public static final int ARRAY_FIELD_NUMBER = 9;
      private MysqlxExpr.Array array_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxExpr.Expr DEFAULT_INSTANCE = new MysqlxExpr.Expr();
      @Deprecated
      public static final Parser<MysqlxExpr.Expr> PARSER = new AbstractParser<MysqlxExpr.Expr>() {
         public MysqlxExpr.Expr parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxExpr.Expr(input, extensionRegistry);
         }
      };

      private Expr(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Expr() {
         this.type_ = 1;
         this.variable_ = "";
      }

      @Override
      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxExpr.Expr();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Expr(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 8:
                        int rawValue = input.readEnum();
                        MysqlxExpr.Expr.Type value = MysqlxExpr.Expr.Type.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(1, rawValue);
                        } else {
                           this.bitField0_ |= 1;
                           this.type_ = rawValue;
                        }
                        break;
                     case 18:
                        MysqlxExpr.ColumnIdentifier.Builder subBuilderxxxxx = null;
                        if ((this.bitField0_ & 2) != 0) {
                           subBuilderxxxxx = this.identifier_.toBuilder();
                        }

                        this.identifier_ = input.readMessage(MysqlxExpr.ColumnIdentifier.PARSER, extensionRegistry);
                        if (subBuilderxxxxx != null) {
                           subBuilderxxxxx.mergeFrom(this.identifier_);
                           this.identifier_ = subBuilderxxxxx.buildPartial();
                        }

                        this.bitField0_ |= 2;
                        break;
                     case 26:
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 4;
                        this.variable_ = bs;
                        break;
                     case 34:
                        MysqlxDatatypes.Scalar.Builder subBuilderxxxx = null;
                        if ((this.bitField0_ & 8) != 0) {
                           subBuilderxxxx = this.literal_.toBuilder();
                        }

                        this.literal_ = input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry);
                        if (subBuilderxxxx != null) {
                           subBuilderxxxx.mergeFrom(this.literal_);
                           this.literal_ = subBuilderxxxx.buildPartial();
                        }

                        this.bitField0_ |= 8;
                        break;
                     case 42:
                        MysqlxExpr.FunctionCall.Builder subBuilderxxx = null;
                        if ((this.bitField0_ & 16) != 0) {
                           subBuilderxxx = this.functionCall_.toBuilder();
                        }

                        this.functionCall_ = input.readMessage(MysqlxExpr.FunctionCall.PARSER, extensionRegistry);
                        if (subBuilderxxx != null) {
                           subBuilderxxx.mergeFrom(this.functionCall_);
                           this.functionCall_ = subBuilderxxx.buildPartial();
                        }

                        this.bitField0_ |= 16;
                        break;
                     case 50:
                        MysqlxExpr.Operator.Builder subBuilderxx = null;
                        if ((this.bitField0_ & 32) != 0) {
                           subBuilderxx = this.operator_.toBuilder();
                        }

                        this.operator_ = input.readMessage(MysqlxExpr.Operator.PARSER, extensionRegistry);
                        if (subBuilderxx != null) {
                           subBuilderxx.mergeFrom(this.operator_);
                           this.operator_ = subBuilderxx.buildPartial();
                        }

                        this.bitField0_ |= 32;
                        break;
                     case 56:
                        this.bitField0_ |= 64;
                        this.position_ = input.readUInt32();
                        break;
                     case 66:
                        MysqlxExpr.Object.Builder subBuilderx = null;
                        if ((this.bitField0_ & 128) != 0) {
                           subBuilderx = this.object_.toBuilder();
                        }

                        this.object_ = input.readMessage(MysqlxExpr.Object.PARSER, extensionRegistry);
                        if (subBuilderx != null) {
                           subBuilderx.mergeFrom(this.object_);
                           this.object_ = subBuilderx.buildPartial();
                        }

                        this.bitField0_ |= 128;
                        break;
                     case 74:
                        MysqlxExpr.Array.Builder subBuilder = null;
                        if ((this.bitField0_ & 256) != 0) {
                           subBuilder = this.array_.toBuilder();
                        }

                        this.array_ = input.readMessage(MysqlxExpr.Array.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.array_);
                           this.array_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 256;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var13) {
               throw var13.setUnfinishedMessage(this);
            } catch (IOException var14) {
               throw new InvalidProtocolBufferException(var14).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxExpr.Expr.class, MysqlxExpr.Expr.Builder.class);
      }

      @Override
      public boolean hasType() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxExpr.Expr.Type getType() {
         MysqlxExpr.Expr.Type result = MysqlxExpr.Expr.Type.valueOf(this.type_);
         return result == null ? MysqlxExpr.Expr.Type.IDENT : result;
      }

      @Override
      public boolean hasIdentifier() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public MysqlxExpr.ColumnIdentifier getIdentifier() {
         return this.identifier_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_;
      }

      @Override
      public MysqlxExpr.ColumnIdentifierOrBuilder getIdentifierOrBuilder() {
         return this.identifier_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_;
      }

      @Override
      public boolean hasVariable() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public String getVariable() {
         java.lang.Object ref = this.variable_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.variable_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getVariableBytes() {
         java.lang.Object ref = this.variable_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.variable_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public boolean hasLiteral() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public MysqlxDatatypes.Scalar getLiteral() {
         return this.literal_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_;
      }

      @Override
      public MysqlxDatatypes.ScalarOrBuilder getLiteralOrBuilder() {
         return this.literal_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_;
      }

      @Override
      public boolean hasFunctionCall() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public MysqlxExpr.FunctionCall getFunctionCall() {
         return this.functionCall_ == null ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_;
      }

      @Override
      public MysqlxExpr.FunctionCallOrBuilder getFunctionCallOrBuilder() {
         return this.functionCall_ == null ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_;
      }

      @Override
      public boolean hasOperator() {
         return (this.bitField0_ & 32) != 0;
      }

      @Override
      public MysqlxExpr.Operator getOperator() {
         return this.operator_ == null ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_;
      }

      @Override
      public MysqlxExpr.OperatorOrBuilder getOperatorOrBuilder() {
         return this.operator_ == null ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_;
      }

      @Override
      public boolean hasPosition() {
         return (this.bitField0_ & 64) != 0;
      }

      @Override
      public int getPosition() {
         return this.position_;
      }

      @Override
      public boolean hasObject() {
         return (this.bitField0_ & 128) != 0;
      }

      @Override
      public MysqlxExpr.Object getObject() {
         return this.object_ == null ? MysqlxExpr.Object.getDefaultInstance() : this.object_;
      }

      @Override
      public MysqlxExpr.ObjectOrBuilder getObjectOrBuilder() {
         return this.object_ == null ? MysqlxExpr.Object.getDefaultInstance() : this.object_;
      }

      @Override
      public boolean hasArray() {
         return (this.bitField0_ & 256) != 0;
      }

      @Override
      public MysqlxExpr.Array getArray() {
         return this.array_ == null ? MysqlxExpr.Array.getDefaultInstance() : this.array_;
      }

      @Override
      public MysqlxExpr.ArrayOrBuilder getArrayOrBuilder() {
         return this.array_ == null ? MysqlxExpr.Array.getDefaultInstance() : this.array_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasType()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasIdentifier() && !this.getIdentifier().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasLiteral() && !this.getLiteral().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasFunctionCall() && !this.getFunctionCall().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasOperator() && !this.getOperator().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasObject() && !this.getObject().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasArray() && !this.getArray().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeEnum(1, this.type_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeMessage(2, this.getIdentifier());
         }

         if ((this.bitField0_ & 4) != 0) {
            GeneratedMessageV3.writeString(output, 3, this.variable_);
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeMessage(4, this.getLiteral());
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeMessage(5, this.getFunctionCall());
         }

         if ((this.bitField0_ & 32) != 0) {
            output.writeMessage(6, this.getOperator());
         }

         if ((this.bitField0_ & 64) != 0) {
            output.writeUInt32(7, this.position_);
         }

         if ((this.bitField0_ & 128) != 0) {
            output.writeMessage(8, this.getObject());
         }

         if ((this.bitField0_ & 256) != 0) {
            output.writeMessage(9, this.getArray());
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeEnumSize(1, this.type_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeMessageSize(2, this.getIdentifier());
            }

            if ((this.bitField0_ & 4) != 0) {
               size += GeneratedMessageV3.computeStringSize(3, this.variable_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeMessageSize(4, this.getLiteral());
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeMessageSize(5, this.getFunctionCall());
            }

            if ((this.bitField0_ & 32) != 0) {
               size += CodedOutputStream.computeMessageSize(6, this.getOperator());
            }

            if ((this.bitField0_ & 64) != 0) {
               size += CodedOutputStream.computeUInt32Size(7, this.position_);
            }

            if ((this.bitField0_ & 128) != 0) {
               size += CodedOutputStream.computeMessageSize(8, this.getObject());
            }

            if ((this.bitField0_ & 256) != 0) {
               size += CodedOutputStream.computeMessageSize(9, this.getArray());
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxExpr.Expr)) {
            return super.equals(obj);
         } else {
            MysqlxExpr.Expr other = (MysqlxExpr.Expr)obj;
            if (this.hasType() != other.hasType()) {
               return false;
            } else if (this.hasType() && this.type_ != other.type_) {
               return false;
            } else if (this.hasIdentifier() != other.hasIdentifier()) {
               return false;
            } else if (this.hasIdentifier() && !this.getIdentifier().equals(other.getIdentifier())) {
               return false;
            } else if (this.hasVariable() != other.hasVariable()) {
               return false;
            } else if (this.hasVariable() && !this.getVariable().equals(other.getVariable())) {
               return false;
            } else if (this.hasLiteral() != other.hasLiteral()) {
               return false;
            } else if (this.hasLiteral() && !this.getLiteral().equals(other.getLiteral())) {
               return false;
            } else if (this.hasFunctionCall() != other.hasFunctionCall()) {
               return false;
            } else if (this.hasFunctionCall() && !this.getFunctionCall().equals(other.getFunctionCall())) {
               return false;
            } else if (this.hasOperator() != other.hasOperator()) {
               return false;
            } else if (this.hasOperator() && !this.getOperator().equals(other.getOperator())) {
               return false;
            } else if (this.hasPosition() != other.hasPosition()) {
               return false;
            } else if (this.hasPosition() && this.getPosition() != other.getPosition()) {
               return false;
            } else if (this.hasObject() != other.hasObject()) {
               return false;
            } else if (this.hasObject() && !this.getObject().equals(other.getObject())) {
               return false;
            } else if (this.hasArray() != other.hasArray()) {
               return false;
            } else {
               return this.hasArray() && !this.getArray().equals(other.getArray()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasType()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.type_;
            }

            if (this.hasIdentifier()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getIdentifier().hashCode();
            }

            if (this.hasVariable()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getVariable().hashCode();
            }

            if (this.hasLiteral()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getLiteral().hashCode();
            }

            if (this.hasFunctionCall()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.getFunctionCall().hashCode();
            }

            if (this.hasOperator()) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getOperator().hashCode();
            }

            if (this.hasPosition()) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getPosition();
            }

            if (this.hasObject()) {
               hash = 37 * hash + 8;
               hash = 53 * hash + this.getObject().hashCode();
            }

            if (this.hasArray()) {
               hash = 37 * hash + 9;
               hash = 53 * hash + this.getArray().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxExpr.Expr parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Expr parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Expr parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Expr parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Expr parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Expr parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Expr parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Expr parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.Expr parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Expr parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.Expr parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Expr parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxExpr.Expr.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxExpr.Expr.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxExpr.Expr.Builder newBuilder(MysqlxExpr.Expr prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxExpr.Expr.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxExpr.Expr.Builder() : new MysqlxExpr.Expr.Builder().mergeFrom(this);
      }

      protected MysqlxExpr.Expr.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxExpr.Expr.Builder(parent);
      }

      public static MysqlxExpr.Expr getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxExpr.Expr> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxExpr.Expr> getParserForType() {
         return PARSER;
      }

      public MysqlxExpr.Expr getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxExpr.Expr.Builder> implements MysqlxExpr.ExprOrBuilder {
         private int bitField0_;
         private int type_ = 1;
         private MysqlxExpr.ColumnIdentifier identifier_;
         private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> identifierBuilder_;
         private java.lang.Object variable_ = "";
         private MysqlxDatatypes.Scalar literal_;
         private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> literalBuilder_;
         private MysqlxExpr.FunctionCall functionCall_;
         private SingleFieldBuilderV3<MysqlxExpr.FunctionCall, MysqlxExpr.FunctionCall.Builder, MysqlxExpr.FunctionCallOrBuilder> functionCallBuilder_;
         private MysqlxExpr.Operator operator_;
         private SingleFieldBuilderV3<MysqlxExpr.Operator, MysqlxExpr.Operator.Builder, MysqlxExpr.OperatorOrBuilder> operatorBuilder_;
         private int position_;
         private MysqlxExpr.Object object_;
         private SingleFieldBuilderV3<MysqlxExpr.Object, MysqlxExpr.Object.Builder, MysqlxExpr.ObjectOrBuilder> objectBuilder_;
         private MysqlxExpr.Array array_;
         private SingleFieldBuilderV3<MysqlxExpr.Array, MysqlxExpr.Array.Builder, MysqlxExpr.ArrayOrBuilder> arrayBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxExpr.Expr.class, MysqlxExpr.Expr.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.Expr.alwaysUseFieldBuilders) {
               this.getIdentifierFieldBuilder();
               this.getLiteralFieldBuilder();
               this.getFunctionCallFieldBuilder();
               this.getOperatorFieldBuilder();
               this.getObjectFieldBuilder();
               this.getArrayFieldBuilder();
            }
         }

         public MysqlxExpr.Expr.Builder clear() {
            super.clear();
            this.type_ = 1;
            this.bitField0_ &= -2;
            if (this.identifierBuilder_ == null) {
               this.identifier_ = null;
            } else {
               this.identifierBuilder_.clear();
            }

            this.bitField0_ &= -3;
            this.variable_ = "";
            this.bitField0_ &= -5;
            if (this.literalBuilder_ == null) {
               this.literal_ = null;
            } else {
               this.literalBuilder_.clear();
            }

            this.bitField0_ &= -9;
            if (this.functionCallBuilder_ == null) {
               this.functionCall_ = null;
            } else {
               this.functionCallBuilder_.clear();
            }

            this.bitField0_ &= -17;
            if (this.operatorBuilder_ == null) {
               this.operator_ = null;
            } else {
               this.operatorBuilder_.clear();
            }

            this.bitField0_ &= -33;
            this.position_ = 0;
            this.bitField0_ &= -65;
            if (this.objectBuilder_ == null) {
               this.object_ = null;
            } else {
               this.objectBuilder_.clear();
            }

            this.bitField0_ &= -129;
            if (this.arrayBuilder_ == null) {
               this.array_ = null;
            } else {
               this.arrayBuilder_.clear();
            }

            this.bitField0_ &= -257;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Expr_descriptor;
         }

         public MysqlxExpr.Expr getDefaultInstanceForType() {
            return MysqlxExpr.Expr.getDefaultInstance();
         }

         public MysqlxExpr.Expr build() {
            MysqlxExpr.Expr result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxExpr.Expr buildPartial() {
            MysqlxExpr.Expr result = new MysqlxExpr.Expr(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.type_ = this.type_;
            if ((from_bitField0_ & 2) != 0) {
               if (this.identifierBuilder_ == null) {
                  result.identifier_ = this.identifier_;
               } else {
                  result.identifier_ = this.identifierBuilder_.build();
               }

               to_bitField0_ |= 2;
            }

            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.variable_ = this.variable_;
            if ((from_bitField0_ & 8) != 0) {
               if (this.literalBuilder_ == null) {
                  result.literal_ = this.literal_;
               } else {
                  result.literal_ = this.literalBuilder_.build();
               }

               to_bitField0_ |= 8;
            }

            if ((from_bitField0_ & 16) != 0) {
               if (this.functionCallBuilder_ == null) {
                  result.functionCall_ = this.functionCall_;
               } else {
                  result.functionCall_ = this.functionCallBuilder_.build();
               }

               to_bitField0_ |= 16;
            }

            if ((from_bitField0_ & 32) != 0) {
               if (this.operatorBuilder_ == null) {
                  result.operator_ = this.operator_;
               } else {
                  result.operator_ = this.operatorBuilder_.build();
               }

               to_bitField0_ |= 32;
            }

            if ((from_bitField0_ & 64) != 0) {
               result.position_ = this.position_;
               to_bitField0_ |= 64;
            }

            if ((from_bitField0_ & 128) != 0) {
               if (this.objectBuilder_ == null) {
                  result.object_ = this.object_;
               } else {
                  result.object_ = this.objectBuilder_.build();
               }

               to_bitField0_ |= 128;
            }

            if ((from_bitField0_ & 256) != 0) {
               if (this.arrayBuilder_ == null) {
                  result.array_ = this.array_;
               } else {
                  result.array_ = this.arrayBuilder_.build();
               }

               to_bitField0_ |= 256;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxExpr.Expr.Builder clone() {
            return (MysqlxExpr.Expr.Builder)super.clone();
         }

         public MysqlxExpr.Expr.Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.Expr.Builder)super.setField(field, value);
         }

         public MysqlxExpr.Expr.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxExpr.Expr.Builder)super.clearField(field);
         }

         public MysqlxExpr.Expr.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxExpr.Expr.Builder)super.clearOneof(oneof);
         }

         public MysqlxExpr.Expr.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (MysqlxExpr.Expr.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxExpr.Expr.Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.Expr.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxExpr.Expr.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxExpr.Expr) {
               return this.mergeFrom((MysqlxExpr.Expr)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxExpr.Expr.Builder mergeFrom(MysqlxExpr.Expr other) {
            if (other == MysqlxExpr.Expr.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasType()) {
                  this.setType(other.getType());
               }

               if (other.hasIdentifier()) {
                  this.mergeIdentifier(other.getIdentifier());
               }

               if (other.hasVariable()) {
                  this.bitField0_ |= 4;
                  this.variable_ = other.variable_;
                  this.onChanged();
               }

               if (other.hasLiteral()) {
                  this.mergeLiteral(other.getLiteral());
               }

               if (other.hasFunctionCall()) {
                  this.mergeFunctionCall(other.getFunctionCall());
               }

               if (other.hasOperator()) {
                  this.mergeOperator(other.getOperator());
               }

               if (other.hasPosition()) {
                  this.setPosition(other.getPosition());
               }

               if (other.hasObject()) {
                  this.mergeObject(other.getObject());
               }

               if (other.hasArray()) {
                  this.mergeArray(other.getArray());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasType()) {
               return false;
            } else if (this.hasIdentifier() && !this.getIdentifier().isInitialized()) {
               return false;
            } else if (this.hasLiteral() && !this.getLiteral().isInitialized()) {
               return false;
            } else if (this.hasFunctionCall() && !this.getFunctionCall().isInitialized()) {
               return false;
            } else if (this.hasOperator() && !this.getOperator().isInitialized()) {
               return false;
            } else {
               return this.hasObject() && !this.getObject().isInitialized() ? false : !this.hasArray() || this.getArray().isInitialized();
            }
         }

         public MysqlxExpr.Expr.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxExpr.Expr parsedMessage = null;

            try {
               parsedMessage = MysqlxExpr.Expr.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxExpr.Expr)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasType() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxExpr.Expr.Type getType() {
            MysqlxExpr.Expr.Type result = MysqlxExpr.Expr.Type.valueOf(this.type_);
            return result == null ? MysqlxExpr.Expr.Type.IDENT : result;
         }

         public MysqlxExpr.Expr.Builder setType(MysqlxExpr.Expr.Type value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.type_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxExpr.Expr.Builder clearType() {
            this.bitField0_ &= -2;
            this.type_ = 1;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasIdentifier() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxExpr.ColumnIdentifier getIdentifier() {
            if (this.identifierBuilder_ == null) {
               return this.identifier_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_;
            } else {
               return this.identifierBuilder_.getMessage();
            }
         }

         public MysqlxExpr.Expr.Builder setIdentifier(MysqlxExpr.ColumnIdentifier value) {
            if (this.identifierBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.identifier_ = value;
               this.onChanged();
            } else {
               this.identifierBuilder_.setMessage(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public MysqlxExpr.Expr.Builder setIdentifier(MysqlxExpr.ColumnIdentifier.Builder builderForValue) {
            if (this.identifierBuilder_ == null) {
               this.identifier_ = builderForValue.build();
               this.onChanged();
            } else {
               this.identifierBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 2;
            return this;
         }

         public MysqlxExpr.Expr.Builder mergeIdentifier(MysqlxExpr.ColumnIdentifier value) {
            if (this.identifierBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0 && this.identifier_ != null && this.identifier_ != MysqlxExpr.ColumnIdentifier.getDefaultInstance()) {
                  this.identifier_ = MysqlxExpr.ColumnIdentifier.newBuilder(this.identifier_).mergeFrom(value).buildPartial();
               } else {
                  this.identifier_ = value;
               }

               this.onChanged();
            } else {
               this.identifierBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public MysqlxExpr.Expr.Builder clearIdentifier() {
            if (this.identifierBuilder_ == null) {
               this.identifier_ = null;
               this.onChanged();
            } else {
               this.identifierBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         public MysqlxExpr.ColumnIdentifier.Builder getIdentifierBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return this.getIdentifierFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.ColumnIdentifierOrBuilder getIdentifierOrBuilder() {
            if (this.identifierBuilder_ != null) {
               return this.identifierBuilder_.getMessageOrBuilder();
            } else {
               return this.identifier_ == null ? MysqlxExpr.ColumnIdentifier.getDefaultInstance() : this.identifier_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.ColumnIdentifier, MysqlxExpr.ColumnIdentifier.Builder, MysqlxExpr.ColumnIdentifierOrBuilder> getIdentifierFieldBuilder() {
            if (this.identifierBuilder_ == null) {
               this.identifierBuilder_ = new SingleFieldBuilderV3<>(this.getIdentifier(), this.getParentForChildren(), this.isClean());
               this.identifier_ = null;
            }

            return this.identifierBuilder_;
         }

         @Override
         public boolean hasVariable() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public String getVariable() {
            java.lang.Object ref = this.variable_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.variable_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getVariableBytes() {
            java.lang.Object ref = this.variable_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.variable_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxExpr.Expr.Builder setVariable(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.variable_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxExpr.Expr.Builder clearVariable() {
            this.bitField0_ &= -5;
            this.variable_ = MysqlxExpr.Expr.getDefaultInstance().getVariable();
            this.onChanged();
            return this;
         }

         public MysqlxExpr.Expr.Builder setVariableBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.variable_ = value;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasLiteral() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public MysqlxDatatypes.Scalar getLiteral() {
            if (this.literalBuilder_ == null) {
               return this.literal_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_;
            } else {
               return this.literalBuilder_.getMessage();
            }
         }

         public MysqlxExpr.Expr.Builder setLiteral(MysqlxDatatypes.Scalar value) {
            if (this.literalBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.literal_ = value;
               this.onChanged();
            } else {
               this.literalBuilder_.setMessage(value);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public MysqlxExpr.Expr.Builder setLiteral(MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.literalBuilder_ == null) {
               this.literal_ = builderForValue.build();
               this.onChanged();
            } else {
               this.literalBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 8;
            return this;
         }

         public MysqlxExpr.Expr.Builder mergeLiteral(MysqlxDatatypes.Scalar value) {
            if (this.literalBuilder_ == null) {
               if ((this.bitField0_ & 8) != 0 && this.literal_ != null && this.literal_ != MysqlxDatatypes.Scalar.getDefaultInstance()) {
                  this.literal_ = MysqlxDatatypes.Scalar.newBuilder(this.literal_).mergeFrom(value).buildPartial();
               } else {
                  this.literal_ = value;
               }

               this.onChanged();
            } else {
               this.literalBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public MysqlxExpr.Expr.Builder clearLiteral() {
            if (this.literalBuilder_ == null) {
               this.literal_ = null;
               this.onChanged();
            } else {
               this.literalBuilder_.clear();
            }

            this.bitField0_ &= -9;
            return this;
         }

         public MysqlxDatatypes.Scalar.Builder getLiteralBuilder() {
            this.bitField0_ |= 8;
            this.onChanged();
            return this.getLiteralFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxDatatypes.ScalarOrBuilder getLiteralOrBuilder() {
            if (this.literalBuilder_ != null) {
               return this.literalBuilder_.getMessageOrBuilder();
            } else {
               return this.literal_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.literal_;
            }
         }

         private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getLiteralFieldBuilder() {
            if (this.literalBuilder_ == null) {
               this.literalBuilder_ = new SingleFieldBuilderV3<>(this.getLiteral(), this.getParentForChildren(), this.isClean());
               this.literal_ = null;
            }

            return this.literalBuilder_;
         }

         @Override
         public boolean hasFunctionCall() {
            return (this.bitField0_ & 16) != 0;
         }

         @Override
         public MysqlxExpr.FunctionCall getFunctionCall() {
            if (this.functionCallBuilder_ == null) {
               return this.functionCall_ == null ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_;
            } else {
               return this.functionCallBuilder_.getMessage();
            }
         }

         public MysqlxExpr.Expr.Builder setFunctionCall(MysqlxExpr.FunctionCall value) {
            if (this.functionCallBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.functionCall_ = value;
               this.onChanged();
            } else {
               this.functionCallBuilder_.setMessage(value);
            }

            this.bitField0_ |= 16;
            return this;
         }

         public MysqlxExpr.Expr.Builder setFunctionCall(MysqlxExpr.FunctionCall.Builder builderForValue) {
            if (this.functionCallBuilder_ == null) {
               this.functionCall_ = builderForValue.build();
               this.onChanged();
            } else {
               this.functionCallBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 16;
            return this;
         }

         public MysqlxExpr.Expr.Builder mergeFunctionCall(MysqlxExpr.FunctionCall value) {
            if (this.functionCallBuilder_ == null) {
               if ((this.bitField0_ & 16) != 0 && this.functionCall_ != null && this.functionCall_ != MysqlxExpr.FunctionCall.getDefaultInstance()) {
                  this.functionCall_ = MysqlxExpr.FunctionCall.newBuilder(this.functionCall_).mergeFrom(value).buildPartial();
               } else {
                  this.functionCall_ = value;
               }

               this.onChanged();
            } else {
               this.functionCallBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 16;
            return this;
         }

         public MysqlxExpr.Expr.Builder clearFunctionCall() {
            if (this.functionCallBuilder_ == null) {
               this.functionCall_ = null;
               this.onChanged();
            } else {
               this.functionCallBuilder_.clear();
            }

            this.bitField0_ &= -17;
            return this;
         }

         public MysqlxExpr.FunctionCall.Builder getFunctionCallBuilder() {
            this.bitField0_ |= 16;
            this.onChanged();
            return this.getFunctionCallFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.FunctionCallOrBuilder getFunctionCallOrBuilder() {
            if (this.functionCallBuilder_ != null) {
               return this.functionCallBuilder_.getMessageOrBuilder();
            } else {
               return this.functionCall_ == null ? MysqlxExpr.FunctionCall.getDefaultInstance() : this.functionCall_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.FunctionCall, MysqlxExpr.FunctionCall.Builder, MysqlxExpr.FunctionCallOrBuilder> getFunctionCallFieldBuilder() {
            if (this.functionCallBuilder_ == null) {
               this.functionCallBuilder_ = new SingleFieldBuilderV3<>(this.getFunctionCall(), this.getParentForChildren(), this.isClean());
               this.functionCall_ = null;
            }

            return this.functionCallBuilder_;
         }

         @Override
         public boolean hasOperator() {
            return (this.bitField0_ & 32) != 0;
         }

         @Override
         public MysqlxExpr.Operator getOperator() {
            if (this.operatorBuilder_ == null) {
               return this.operator_ == null ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_;
            } else {
               return this.operatorBuilder_.getMessage();
            }
         }

         public MysqlxExpr.Expr.Builder setOperator(MysqlxExpr.Operator value) {
            if (this.operatorBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.operator_ = value;
               this.onChanged();
            } else {
               this.operatorBuilder_.setMessage(value);
            }

            this.bitField0_ |= 32;
            return this;
         }

         public MysqlxExpr.Expr.Builder setOperator(MysqlxExpr.Operator.Builder builderForValue) {
            if (this.operatorBuilder_ == null) {
               this.operator_ = builderForValue.build();
               this.onChanged();
            } else {
               this.operatorBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 32;
            return this;
         }

         public MysqlxExpr.Expr.Builder mergeOperator(MysqlxExpr.Operator value) {
            if (this.operatorBuilder_ == null) {
               if ((this.bitField0_ & 32) != 0 && this.operator_ != null && this.operator_ != MysqlxExpr.Operator.getDefaultInstance()) {
                  this.operator_ = MysqlxExpr.Operator.newBuilder(this.operator_).mergeFrom(value).buildPartial();
               } else {
                  this.operator_ = value;
               }

               this.onChanged();
            } else {
               this.operatorBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 32;
            return this;
         }

         public MysqlxExpr.Expr.Builder clearOperator() {
            if (this.operatorBuilder_ == null) {
               this.operator_ = null;
               this.onChanged();
            } else {
               this.operatorBuilder_.clear();
            }

            this.bitField0_ &= -33;
            return this;
         }

         public MysqlxExpr.Operator.Builder getOperatorBuilder() {
            this.bitField0_ |= 32;
            this.onChanged();
            return this.getOperatorFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.OperatorOrBuilder getOperatorOrBuilder() {
            if (this.operatorBuilder_ != null) {
               return this.operatorBuilder_.getMessageOrBuilder();
            } else {
               return this.operator_ == null ? MysqlxExpr.Operator.getDefaultInstance() : this.operator_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Operator, MysqlxExpr.Operator.Builder, MysqlxExpr.OperatorOrBuilder> getOperatorFieldBuilder() {
            if (this.operatorBuilder_ == null) {
               this.operatorBuilder_ = new SingleFieldBuilderV3<>(this.getOperator(), this.getParentForChildren(), this.isClean());
               this.operator_ = null;
            }

            return this.operatorBuilder_;
         }

         @Override
         public boolean hasPosition() {
            return (this.bitField0_ & 64) != 0;
         }

         @Override
         public int getPosition() {
            return this.position_;
         }

         public MysqlxExpr.Expr.Builder setPosition(int value) {
            this.bitField0_ |= 64;
            this.position_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxExpr.Expr.Builder clearPosition() {
            this.bitField0_ &= -65;
            this.position_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasObject() {
            return (this.bitField0_ & 128) != 0;
         }

         @Override
         public MysqlxExpr.Object getObject() {
            if (this.objectBuilder_ == null) {
               return this.object_ == null ? MysqlxExpr.Object.getDefaultInstance() : this.object_;
            } else {
               return this.objectBuilder_.getMessage();
            }
         }

         public MysqlxExpr.Expr.Builder setObject(MysqlxExpr.Object value) {
            if (this.objectBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.object_ = value;
               this.onChanged();
            } else {
               this.objectBuilder_.setMessage(value);
            }

            this.bitField0_ |= 128;
            return this;
         }

         public MysqlxExpr.Expr.Builder setObject(MysqlxExpr.Object.Builder builderForValue) {
            if (this.objectBuilder_ == null) {
               this.object_ = builderForValue.build();
               this.onChanged();
            } else {
               this.objectBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 128;
            return this;
         }

         public MysqlxExpr.Expr.Builder mergeObject(MysqlxExpr.Object value) {
            if (this.objectBuilder_ == null) {
               if ((this.bitField0_ & 128) != 0 && this.object_ != null && this.object_ != MysqlxExpr.Object.getDefaultInstance()) {
                  this.object_ = MysqlxExpr.Object.newBuilder(this.object_).mergeFrom(value).buildPartial();
               } else {
                  this.object_ = value;
               }

               this.onChanged();
            } else {
               this.objectBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 128;
            return this;
         }

         public MysqlxExpr.Expr.Builder clearObject() {
            if (this.objectBuilder_ == null) {
               this.object_ = null;
               this.onChanged();
            } else {
               this.objectBuilder_.clear();
            }

            this.bitField0_ &= -129;
            return this;
         }

         public MysqlxExpr.Object.Builder getObjectBuilder() {
            this.bitField0_ |= 128;
            this.onChanged();
            return this.getObjectFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.ObjectOrBuilder getObjectOrBuilder() {
            if (this.objectBuilder_ != null) {
               return this.objectBuilder_.getMessageOrBuilder();
            } else {
               return this.object_ == null ? MysqlxExpr.Object.getDefaultInstance() : this.object_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Object, MysqlxExpr.Object.Builder, MysqlxExpr.ObjectOrBuilder> getObjectFieldBuilder() {
            if (this.objectBuilder_ == null) {
               this.objectBuilder_ = new SingleFieldBuilderV3<>(this.getObject(), this.getParentForChildren(), this.isClean());
               this.object_ = null;
            }

            return this.objectBuilder_;
         }

         @Override
         public boolean hasArray() {
            return (this.bitField0_ & 256) != 0;
         }

         @Override
         public MysqlxExpr.Array getArray() {
            if (this.arrayBuilder_ == null) {
               return this.array_ == null ? MysqlxExpr.Array.getDefaultInstance() : this.array_;
            } else {
               return this.arrayBuilder_.getMessage();
            }
         }

         public MysqlxExpr.Expr.Builder setArray(MysqlxExpr.Array value) {
            if (this.arrayBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.array_ = value;
               this.onChanged();
            } else {
               this.arrayBuilder_.setMessage(value);
            }

            this.bitField0_ |= 256;
            return this;
         }

         public MysqlxExpr.Expr.Builder setArray(MysqlxExpr.Array.Builder builderForValue) {
            if (this.arrayBuilder_ == null) {
               this.array_ = builderForValue.build();
               this.onChanged();
            } else {
               this.arrayBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 256;
            return this;
         }

         public MysqlxExpr.Expr.Builder mergeArray(MysqlxExpr.Array value) {
            if (this.arrayBuilder_ == null) {
               if ((this.bitField0_ & 256) != 0 && this.array_ != null && this.array_ != MysqlxExpr.Array.getDefaultInstance()) {
                  this.array_ = MysqlxExpr.Array.newBuilder(this.array_).mergeFrom(value).buildPartial();
               } else {
                  this.array_ = value;
               }

               this.onChanged();
            } else {
               this.arrayBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 256;
            return this;
         }

         public MysqlxExpr.Expr.Builder clearArray() {
            if (this.arrayBuilder_ == null) {
               this.array_ = null;
               this.onChanged();
            } else {
               this.arrayBuilder_.clear();
            }

            this.bitField0_ &= -257;
            return this;
         }

         public MysqlxExpr.Array.Builder getArrayBuilder() {
            this.bitField0_ |= 256;
            this.onChanged();
            return this.getArrayFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.ArrayOrBuilder getArrayOrBuilder() {
            if (this.arrayBuilder_ != null) {
               return this.arrayBuilder_.getMessageOrBuilder();
            } else {
               return this.array_ == null ? MysqlxExpr.Array.getDefaultInstance() : this.array_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Array, MysqlxExpr.Array.Builder, MysqlxExpr.ArrayOrBuilder> getArrayFieldBuilder() {
            if (this.arrayBuilder_ == null) {
               this.arrayBuilder_ = new SingleFieldBuilderV3<>(this.getArray(), this.getParentForChildren(), this.isClean());
               this.array_ = null;
            }

            return this.arrayBuilder_;
         }

         public final MysqlxExpr.Expr.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.Expr.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxExpr.Expr.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.Expr.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static enum Type implements ProtocolMessageEnum {
         IDENT(1),
         LITERAL(2),
         VARIABLE(3),
         FUNC_CALL(4),
         OPERATOR(5),
         PLACEHOLDER(6),
         OBJECT(7),
         ARRAY(8);

         public static final int IDENT_VALUE = 1;
         public static final int LITERAL_VALUE = 2;
         public static final int VARIABLE_VALUE = 3;
         public static final int FUNC_CALL_VALUE = 4;
         public static final int OPERATOR_VALUE = 5;
         public static final int PLACEHOLDER_VALUE = 6;
         public static final int OBJECT_VALUE = 7;
         public static final int ARRAY_VALUE = 8;
         private static final Internal.EnumLiteMap<MysqlxExpr.Expr.Type> internalValueMap = new Internal.EnumLiteMap<MysqlxExpr.Expr.Type>() {
            public MysqlxExpr.Expr.Type findValueByNumber(int number) {
               return MysqlxExpr.Expr.Type.forNumber(number);
            }
         };
         private static final MysqlxExpr.Expr.Type[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static MysqlxExpr.Expr.Type valueOf(int value) {
            return forNumber(value);
         }

         public static MysqlxExpr.Expr.Type forNumber(int value) {
            switch (value) {
               case 1:
                  return IDENT;
               case 2:
                  return LITERAL;
               case 3:
                  return VARIABLE;
               case 4:
                  return FUNC_CALL;
               case 5:
                  return OPERATOR;
               case 6:
                  return PLACEHOLDER;
               case 7:
                  return OBJECT;
               case 8:
                  return ARRAY;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<MysqlxExpr.Expr.Type> internalGetValueMap() {
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
            return MysqlxExpr.Expr.getDescriptor().getEnumTypes().get(0);
         }

         public static MysqlxExpr.Expr.Type valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Type(int value) {
            this.value = value;
         }
      }
   }

   public interface ExprOrBuilder extends MessageOrBuilder {
      boolean hasType();

      MysqlxExpr.Expr.Type getType();

      boolean hasIdentifier();

      MysqlxExpr.ColumnIdentifier getIdentifier();

      MysqlxExpr.ColumnIdentifierOrBuilder getIdentifierOrBuilder();

      boolean hasVariable();

      String getVariable();

      ByteString getVariableBytes();

      boolean hasLiteral();

      MysqlxDatatypes.Scalar getLiteral();

      MysqlxDatatypes.ScalarOrBuilder getLiteralOrBuilder();

      boolean hasFunctionCall();

      MysqlxExpr.FunctionCall getFunctionCall();

      MysqlxExpr.FunctionCallOrBuilder getFunctionCallOrBuilder();

      boolean hasOperator();

      MysqlxExpr.Operator getOperator();

      MysqlxExpr.OperatorOrBuilder getOperatorOrBuilder();

      boolean hasPosition();

      int getPosition();

      boolean hasObject();

      MysqlxExpr.Object getObject();

      MysqlxExpr.ObjectOrBuilder getObjectOrBuilder();

      boolean hasArray();

      MysqlxExpr.Array getArray();

      MysqlxExpr.ArrayOrBuilder getArrayOrBuilder();
   }

   public static final class FunctionCall extends GeneratedMessageV3 implements MysqlxExpr.FunctionCallOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private MysqlxExpr.Identifier name_;
      public static final int PARAM_FIELD_NUMBER = 2;
      private List<MysqlxExpr.Expr> param_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxExpr.FunctionCall DEFAULT_INSTANCE = new MysqlxExpr.FunctionCall();
      @Deprecated
      public static final Parser<MysqlxExpr.FunctionCall> PARSER = new AbstractParser<MysqlxExpr.FunctionCall>() {
         public MysqlxExpr.FunctionCall parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxExpr.FunctionCall(input, extensionRegistry);
         }
      };

      private FunctionCall(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private FunctionCall() {
         this.param_ = Collections.emptyList();
      }

      @Override
      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxExpr.FunctionCall();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private FunctionCall(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        MysqlxExpr.Identifier.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.name_.toBuilder();
                        }

                        this.name_ = input.readMessage(MysqlxExpr.Identifier.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.name_);
                           this.name_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
                        break;
                     case 18:
                        if ((mutable_bitField0_ & 2) == 0) {
                           this.param_ = new ArrayList<>();
                           mutable_bitField0_ |= 2;
                        }

                        this.param_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry));
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 2) != 0) {
                  this.param_ = Collections.unmodifiableList(this.param_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxExpr.FunctionCall.class, MysqlxExpr.FunctionCall.Builder.class);
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxExpr.Identifier getName() {
         return this.name_ == null ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_;
      }

      @Override
      public MysqlxExpr.IdentifierOrBuilder getNameOrBuilder() {
         return this.name_ == null ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_;
      }

      @Override
      public List<MysqlxExpr.Expr> getParamList() {
         return this.param_;
      }

      @Override
      public List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList() {
         return this.param_;
      }

      @Override
      public int getParamCount() {
         return this.param_.size();
      }

      @Override
      public MysqlxExpr.Expr getParam(int index) {
         return this.param_.get(index);
      }

      @Override
      public MysqlxExpr.ExprOrBuilder getParamOrBuilder(int index) {
         return this.param_.get(index);
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasName()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getName().isInitialized()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            for (int i = 0; i < this.getParamCount(); i++) {
               if (!this.getParam(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getName());
         }

         for (int i = 0; i < this.param_.size(); i++) {
            output.writeMessage(2, this.param_.get(i));
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += CodedOutputStream.computeMessageSize(1, this.getName());
            }

            for (int i = 0; i < this.param_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(2, this.param_.get(i));
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxExpr.FunctionCall)) {
            return super.equals(obj);
         } else {
            MysqlxExpr.FunctionCall other = (MysqlxExpr.FunctionCall)obj;
            if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else {
               return !this.getParamList().equals(other.getParamList()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.getParamCount() > 0) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getParamList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxExpr.FunctionCall parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.FunctionCall parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.FunctionCall parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.FunctionCall parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.FunctionCall parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.FunctionCall parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.FunctionCall parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.FunctionCall parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.FunctionCall parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxExpr.FunctionCall parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.FunctionCall parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.FunctionCall parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxExpr.FunctionCall.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxExpr.FunctionCall.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxExpr.FunctionCall.Builder newBuilder(MysqlxExpr.FunctionCall prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxExpr.FunctionCall.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxExpr.FunctionCall.Builder() : new MysqlxExpr.FunctionCall.Builder().mergeFrom(this);
      }

      protected MysqlxExpr.FunctionCall.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxExpr.FunctionCall.Builder(parent);
      }

      public static MysqlxExpr.FunctionCall getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxExpr.FunctionCall> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxExpr.FunctionCall> getParserForType() {
         return PARSER;
      }

      public MysqlxExpr.FunctionCall getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxExpr.FunctionCall.Builder> implements MysqlxExpr.FunctionCallOrBuilder {
         private int bitField0_;
         private MysqlxExpr.Identifier name_;
         private SingleFieldBuilderV3<MysqlxExpr.Identifier, MysqlxExpr.Identifier.Builder, MysqlxExpr.IdentifierOrBuilder> nameBuilder_;
         private List<MysqlxExpr.Expr> param_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> paramBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxExpr.FunctionCall.class, MysqlxExpr.FunctionCall.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.FunctionCall.alwaysUseFieldBuilders) {
               this.getNameFieldBuilder();
               this.getParamFieldBuilder();
            }
         }

         public MysqlxExpr.FunctionCall.Builder clear() {
            super.clear();
            if (this.nameBuilder_ == null) {
               this.name_ = null;
            } else {
               this.nameBuilder_.clear();
            }

            this.bitField0_ &= -2;
            if (this.paramBuilder_ == null) {
               this.param_ = Collections.emptyList();
               this.bitField0_ &= -3;
            } else {
               this.paramBuilder_.clear();
            }

            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_FunctionCall_descriptor;
         }

         public MysqlxExpr.FunctionCall getDefaultInstanceForType() {
            return MysqlxExpr.FunctionCall.getDefaultInstance();
         }

         public MysqlxExpr.FunctionCall build() {
            MysqlxExpr.FunctionCall result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxExpr.FunctionCall buildPartial() {
            MysqlxExpr.FunctionCall result = new MysqlxExpr.FunctionCall(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.nameBuilder_ == null) {
                  result.name_ = this.name_;
               } else {
                  result.name_ = this.nameBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            if (this.paramBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0) {
                  this.param_ = Collections.unmodifiableList(this.param_);
                  this.bitField0_ &= -3;
               }

               result.param_ = this.param_;
            } else {
               result.param_ = this.paramBuilder_.build();
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxExpr.FunctionCall.Builder clone() {
            return (MysqlxExpr.FunctionCall.Builder)super.clone();
         }

         public MysqlxExpr.FunctionCall.Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.FunctionCall.Builder)super.setField(field, value);
         }

         public MysqlxExpr.FunctionCall.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxExpr.FunctionCall.Builder)super.clearField(field);
         }

         public MysqlxExpr.FunctionCall.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxExpr.FunctionCall.Builder)super.clearOneof(oneof);
         }

         public MysqlxExpr.FunctionCall.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (MysqlxExpr.FunctionCall.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxExpr.FunctionCall.Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.FunctionCall.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxExpr.FunctionCall.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxExpr.FunctionCall) {
               return this.mergeFrom((MysqlxExpr.FunctionCall)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxExpr.FunctionCall.Builder mergeFrom(MysqlxExpr.FunctionCall other) {
            if (other == MysqlxExpr.FunctionCall.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasName()) {
                  this.mergeName(other.getName());
               }

               if (this.paramBuilder_ == null) {
                  if (!other.param_.isEmpty()) {
                     if (this.param_.isEmpty()) {
                        this.param_ = other.param_;
                        this.bitField0_ &= -3;
                     } else {
                        this.ensureParamIsMutable();
                        this.param_.addAll(other.param_);
                     }

                     this.onChanged();
                  }
               } else if (!other.param_.isEmpty()) {
                  if (this.paramBuilder_.isEmpty()) {
                     this.paramBuilder_.dispose();
                     this.paramBuilder_ = null;
                     this.param_ = other.param_;
                     this.bitField0_ &= -3;
                     this.paramBuilder_ = MysqlxExpr.FunctionCall.alwaysUseFieldBuilders ? this.getParamFieldBuilder() : null;
                  } else {
                     this.paramBuilder_.addAllMessages(other.param_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasName()) {
               return false;
            } else if (!this.getName().isInitialized()) {
               return false;
            } else {
               for (int i = 0; i < this.getParamCount(); i++) {
                  if (!this.getParam(i).isInitialized()) {
                     return false;
                  }
               }

               return true;
            }
         }

         public MysqlxExpr.FunctionCall.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxExpr.FunctionCall parsedMessage = null;

            try {
               parsedMessage = MysqlxExpr.FunctionCall.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxExpr.FunctionCall)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasName() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxExpr.Identifier getName() {
            if (this.nameBuilder_ == null) {
               return this.name_ == null ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_;
            } else {
               return this.nameBuilder_.getMessage();
            }
         }

         public MysqlxExpr.FunctionCall.Builder setName(MysqlxExpr.Identifier value) {
            if (this.nameBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.name_ = value;
               this.onChanged();
            } else {
               this.nameBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxExpr.FunctionCall.Builder setName(MysqlxExpr.Identifier.Builder builderForValue) {
            if (this.nameBuilder_ == null) {
               this.name_ = builderForValue.build();
               this.onChanged();
            } else {
               this.nameBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxExpr.FunctionCall.Builder mergeName(MysqlxExpr.Identifier value) {
            if (this.nameBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.name_ != null && this.name_ != MysqlxExpr.Identifier.getDefaultInstance()) {
                  this.name_ = MysqlxExpr.Identifier.newBuilder(this.name_).mergeFrom(value).buildPartial();
               } else {
                  this.name_ = value;
               }

               this.onChanged();
            } else {
               this.nameBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxExpr.FunctionCall.Builder clearName() {
            if (this.nameBuilder_ == null) {
               this.name_ = null;
               this.onChanged();
            } else {
               this.nameBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxExpr.Identifier.Builder getNameBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.getNameFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxExpr.IdentifierOrBuilder getNameOrBuilder() {
            if (this.nameBuilder_ != null) {
               return this.nameBuilder_.getMessageOrBuilder();
            } else {
               return this.name_ == null ? MysqlxExpr.Identifier.getDefaultInstance() : this.name_;
            }
         }

         private SingleFieldBuilderV3<MysqlxExpr.Identifier, MysqlxExpr.Identifier.Builder, MysqlxExpr.IdentifierOrBuilder> getNameFieldBuilder() {
            if (this.nameBuilder_ == null) {
               this.nameBuilder_ = new SingleFieldBuilderV3<>(this.getName(), this.getParentForChildren(), this.isClean());
               this.name_ = null;
            }

            return this.nameBuilder_;
         }

         private void ensureParamIsMutable() {
            if ((this.bitField0_ & 2) == 0) {
               this.param_ = new ArrayList<>(this.param_);
               this.bitField0_ |= 2;
            }
         }

         @Override
         public List<MysqlxExpr.Expr> getParamList() {
            return this.paramBuilder_ == null ? Collections.unmodifiableList(this.param_) : this.paramBuilder_.getMessageList();
         }

         @Override
         public int getParamCount() {
            return this.paramBuilder_ == null ? this.param_.size() : this.paramBuilder_.getCount();
         }

         @Override
         public MysqlxExpr.Expr getParam(int index) {
            return this.paramBuilder_ == null ? this.param_.get(index) : this.paramBuilder_.getMessage(index);
         }

         public MysqlxExpr.FunctionCall.Builder setParam(int index, MysqlxExpr.Expr value) {
            if (this.paramBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureParamIsMutable();
               this.param_.set(index, value);
               this.onChanged();
            } else {
               this.paramBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxExpr.FunctionCall.Builder setParam(int index, MysqlxExpr.Expr.Builder builderForValue) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.paramBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.FunctionCall.Builder addParam(MysqlxExpr.Expr value) {
            if (this.paramBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureParamIsMutable();
               this.param_.add(value);
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxExpr.FunctionCall.Builder addParam(int index, MysqlxExpr.Expr value) {
            if (this.paramBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureParamIsMutable();
               this.param_.add(index, value);
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxExpr.FunctionCall.Builder addParam(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.FunctionCall.Builder addParam(int index, MysqlxExpr.Expr.Builder builderForValue) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.FunctionCall.Builder addAllParam(Iterable<? extends MysqlxExpr.Expr> values) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.param_);
               this.onChanged();
            } else {
               this.paramBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxExpr.FunctionCall.Builder clearParam() {
            if (this.paramBuilder_ == null) {
               this.param_ = Collections.emptyList();
               this.bitField0_ &= -3;
               this.onChanged();
            } else {
               this.paramBuilder_.clear();
            }

            return this;
         }

         public MysqlxExpr.FunctionCall.Builder removeParam(int index) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.remove(index);
               this.onChanged();
            } else {
               this.paramBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxExpr.Expr.Builder getParamBuilder(int index) {
            return this.getParamFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getParamOrBuilder(int index) {
            return this.paramBuilder_ == null ? this.param_.get(index) : this.paramBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList() {
            return this.paramBuilder_ != null ? this.paramBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.param_);
         }

         public MysqlxExpr.Expr.Builder addParamBuilder() {
            return this.getParamFieldBuilder().addBuilder(MysqlxExpr.Expr.getDefaultInstance());
         }

         public MysqlxExpr.Expr.Builder addParamBuilder(int index) {
            return this.getParamFieldBuilder().addBuilder(index, MysqlxExpr.Expr.getDefaultInstance());
         }

         public List<MysqlxExpr.Expr.Builder> getParamBuilderList() {
            return this.getParamFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getParamFieldBuilder() {
            if (this.paramBuilder_ == null) {
               this.paramBuilder_ = new RepeatedFieldBuilderV3<>(this.param_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
               this.param_ = null;
            }

            return this.paramBuilder_;
         }

         public final MysqlxExpr.FunctionCall.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.FunctionCall.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxExpr.FunctionCall.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.FunctionCall.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface FunctionCallOrBuilder extends MessageOrBuilder {
      boolean hasName();

      MysqlxExpr.Identifier getName();

      MysqlxExpr.IdentifierOrBuilder getNameOrBuilder();

      List<MysqlxExpr.Expr> getParamList();

      MysqlxExpr.Expr getParam(int var1);

      int getParamCount();

      List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList();

      MysqlxExpr.ExprOrBuilder getParamOrBuilder(int var1);
   }

   public static final class Identifier extends GeneratedMessageV3 implements MysqlxExpr.IdentifierOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private volatile java.lang.Object name_;
      public static final int SCHEMA_NAME_FIELD_NUMBER = 2;
      private volatile java.lang.Object schemaName_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxExpr.Identifier DEFAULT_INSTANCE = new MysqlxExpr.Identifier();
      @Deprecated
      public static final Parser<MysqlxExpr.Identifier> PARSER = new AbstractParser<MysqlxExpr.Identifier>() {
         public MysqlxExpr.Identifier parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxExpr.Identifier(input, extensionRegistry);
         }
      };

      private Identifier(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Identifier() {
         this.name_ = "";
         this.schemaName_ = "";
      }

      @Override
      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxExpr.Identifier();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Identifier(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10: {
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 1;
                        this.name_ = bs;
                        break;
                     }
                     case 18: {
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.schemaName_ = bs;
                        break;
                     }
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxExpr.Identifier.class, MysqlxExpr.Identifier.Builder.class);
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         java.lang.Object ref = this.name_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.name_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getNameBytes() {
         java.lang.Object ref = this.name_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public boolean hasSchemaName() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public String getSchemaName() {
         java.lang.Object ref = this.schemaName_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.schemaName_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getSchemaNameBytes() {
         java.lang.Object ref = this.schemaName_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.schemaName_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasName()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 1, this.name_);
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.schemaName_);
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(1, this.name_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.schemaName_);
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxExpr.Identifier)) {
            return super.equals(obj);
         } else {
            MysqlxExpr.Identifier other = (MysqlxExpr.Identifier)obj;
            if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else if (this.hasSchemaName() != other.hasSchemaName()) {
               return false;
            } else {
               return this.hasSchemaName() && !this.getSchemaName().equals(other.getSchemaName()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.hasSchemaName()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getSchemaName().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxExpr.Identifier parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Identifier parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Identifier parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Identifier parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Identifier parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Identifier parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Identifier parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Identifier parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.Identifier parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Identifier parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.Identifier parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Identifier parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxExpr.Identifier.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxExpr.Identifier.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxExpr.Identifier.Builder newBuilder(MysqlxExpr.Identifier prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxExpr.Identifier.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxExpr.Identifier.Builder() : new MysqlxExpr.Identifier.Builder().mergeFrom(this);
      }

      protected MysqlxExpr.Identifier.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxExpr.Identifier.Builder(parent);
      }

      public static MysqlxExpr.Identifier getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxExpr.Identifier> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxExpr.Identifier> getParserForType() {
         return PARSER;
      }

      public MysqlxExpr.Identifier getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxExpr.Identifier.Builder> implements MysqlxExpr.IdentifierOrBuilder {
         private int bitField0_;
         private java.lang.Object name_ = "";
         private java.lang.Object schemaName_ = "";

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxExpr.Identifier.class, MysqlxExpr.Identifier.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.Identifier.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxExpr.Identifier.Builder clear() {
            super.clear();
            this.name_ = "";
            this.bitField0_ &= -2;
            this.schemaName_ = "";
            this.bitField0_ &= -3;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Identifier_descriptor;
         }

         public MysqlxExpr.Identifier getDefaultInstanceForType() {
            return MysqlxExpr.Identifier.getDefaultInstance();
         }

         public MysqlxExpr.Identifier build() {
            MysqlxExpr.Identifier result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxExpr.Identifier buildPartial() {
            MysqlxExpr.Identifier result = new MysqlxExpr.Identifier(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.name_ = this.name_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.schemaName_ = this.schemaName_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxExpr.Identifier.Builder clone() {
            return (MysqlxExpr.Identifier.Builder)super.clone();
         }

         public MysqlxExpr.Identifier.Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.Identifier.Builder)super.setField(field, value);
         }

         public MysqlxExpr.Identifier.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxExpr.Identifier.Builder)super.clearField(field);
         }

         public MysqlxExpr.Identifier.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxExpr.Identifier.Builder)super.clearOneof(oneof);
         }

         public MysqlxExpr.Identifier.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (MysqlxExpr.Identifier.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxExpr.Identifier.Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.Identifier.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxExpr.Identifier.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxExpr.Identifier) {
               return this.mergeFrom((MysqlxExpr.Identifier)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxExpr.Identifier.Builder mergeFrom(MysqlxExpr.Identifier other) {
            if (other == MysqlxExpr.Identifier.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasName()) {
                  this.bitField0_ |= 1;
                  this.name_ = other.name_;
                  this.onChanged();
               }

               if (other.hasSchemaName()) {
                  this.bitField0_ |= 2;
                  this.schemaName_ = other.schemaName_;
                  this.onChanged();
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return this.hasName();
         }

         public MysqlxExpr.Identifier.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxExpr.Identifier parsedMessage = null;

            try {
               parsedMessage = MysqlxExpr.Identifier.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxExpr.Identifier)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasName() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getName() {
            java.lang.Object ref = this.name_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.name_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getNameBytes() {
            java.lang.Object ref = this.name_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.name_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxExpr.Identifier.Builder setName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxExpr.Identifier.Builder clearName() {
            this.bitField0_ &= -2;
            this.name_ = MysqlxExpr.Identifier.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         public MysqlxExpr.Identifier.Builder setNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasSchemaName() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getSchemaName() {
            java.lang.Object ref = this.schemaName_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.schemaName_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getSchemaNameBytes() {
            java.lang.Object ref = this.schemaName_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.schemaName_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxExpr.Identifier.Builder setSchemaName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.schemaName_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxExpr.Identifier.Builder clearSchemaName() {
            this.bitField0_ &= -3;
            this.schemaName_ = MysqlxExpr.Identifier.getDefaultInstance().getSchemaName();
            this.onChanged();
            return this;
         }

         public MysqlxExpr.Identifier.Builder setSchemaNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.schemaName_ = value;
               this.onChanged();
               return this;
            }
         }

         public final MysqlxExpr.Identifier.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.Identifier.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxExpr.Identifier.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.Identifier.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface IdentifierOrBuilder extends MessageOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasSchemaName();

      String getSchemaName();

      ByteString getSchemaNameBytes();
   }

   public static final class Object extends GeneratedMessageV3 implements MysqlxExpr.ObjectOrBuilder {
      private static final long serialVersionUID = 0L;
      public static final int FLD_FIELD_NUMBER = 1;
      private List<MysqlxExpr.Object.ObjectField> fld_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxExpr.Object DEFAULT_INSTANCE = new MysqlxExpr.Object();
      @Deprecated
      public static final Parser<MysqlxExpr.Object> PARSER = new AbstractParser<MysqlxExpr.Object>() {
         public MysqlxExpr.Object parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxExpr.Object(input, extensionRegistry);
         }
      };

      private Object(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Object() {
         this.fld_ = Collections.emptyList();
      }

      @Override
      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxExpr.Object();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Object(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        if ((mutable_bitField0_ & 1) == 0) {
                           this.fld_ = new ArrayList<>();
                           mutable_bitField0_ |= 1;
                        }

                        this.fld_.add(input.readMessage(MysqlxExpr.Object.ObjectField.PARSER, extensionRegistry));
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var11) {
               throw var11.setUnfinishedMessage(this);
            } catch (IOException var12) {
               throw new InvalidProtocolBufferException(var12).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 1) != 0) {
                  this.fld_ = Collections.unmodifiableList(this.fld_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Object_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Object_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxExpr.Object.class, MysqlxExpr.Object.Builder.class);
      }

      @Override
      public List<MysqlxExpr.Object.ObjectField> getFldList() {
         return this.fld_;
      }

      @Override
      public List<? extends MysqlxExpr.Object.ObjectFieldOrBuilder> getFldOrBuilderList() {
         return this.fld_;
      }

      @Override
      public int getFldCount() {
         return this.fld_.size();
      }

      @Override
      public MysqlxExpr.Object.ObjectField getFld(int index) {
         return this.fld_.get(index);
      }

      @Override
      public MysqlxExpr.Object.ObjectFieldOrBuilder getFldOrBuilder(int index) {
         return this.fld_.get(index);
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            for (int i = 0; i < this.getFldCount(); i++) {
               if (!this.getFld(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         for (int i = 0; i < this.fld_.size(); i++) {
            output.writeMessage(1, this.fld_.get(i));
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;

            for (int i = 0; i < this.fld_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(1, this.fld_.get(i));
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxExpr.Object)) {
            return super.equals(obj);
         } else {
            MysqlxExpr.Object other = (MysqlxExpr.Object)obj;
            return !this.getFldList().equals(other.getFldList()) ? false : this.unknownFields.equals(other.unknownFields);
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.getFldCount() > 0) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getFldList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxExpr.Object parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Object parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Object parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Object parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Object parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Object parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Object parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Object parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.Object parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Object parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.Object parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Object parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxExpr.Object.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxExpr.Object.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxExpr.Object.Builder newBuilder(MysqlxExpr.Object prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxExpr.Object.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxExpr.Object.Builder() : new MysqlxExpr.Object.Builder().mergeFrom(this);
      }

      protected MysqlxExpr.Object.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxExpr.Object.Builder(parent);
      }

      public static MysqlxExpr.Object getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxExpr.Object> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxExpr.Object> getParserForType() {
         return PARSER;
      }

      public MysqlxExpr.Object getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxExpr.Object.Builder> implements MysqlxExpr.ObjectOrBuilder {
         private int bitField0_;
         private List<MysqlxExpr.Object.ObjectField> fld_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxExpr.Object.ObjectField, MysqlxExpr.Object.ObjectField.Builder, MysqlxExpr.Object.ObjectFieldOrBuilder> fldBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Object_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Object_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxExpr.Object.class, MysqlxExpr.Object.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.Object.alwaysUseFieldBuilders) {
               this.getFldFieldBuilder();
            }
         }

         public MysqlxExpr.Object.Builder clear() {
            super.clear();
            if (this.fldBuilder_ == null) {
               this.fld_ = Collections.emptyList();
               this.bitField0_ &= -2;
            } else {
               this.fldBuilder_.clear();
            }

            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Object_descriptor;
         }

         public MysqlxExpr.Object getDefaultInstanceForType() {
            return MysqlxExpr.Object.getDefaultInstance();
         }

         public MysqlxExpr.Object build() {
            MysqlxExpr.Object result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxExpr.Object buildPartial() {
            MysqlxExpr.Object result = new MysqlxExpr.Object(this);
            int from_bitField0_ = this.bitField0_;
            if (this.fldBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0) {
                  this.fld_ = Collections.unmodifiableList(this.fld_);
                  this.bitField0_ &= -2;
               }

               result.fld_ = this.fld_;
            } else {
               result.fld_ = this.fldBuilder_.build();
            }

            this.onBuilt();
            return result;
         }

         public MysqlxExpr.Object.Builder clone() {
            return (MysqlxExpr.Object.Builder)super.clone();
         }

         public MysqlxExpr.Object.Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.Object.Builder)super.setField(field, value);
         }

         public MysqlxExpr.Object.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxExpr.Object.Builder)super.clearField(field);
         }

         public MysqlxExpr.Object.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxExpr.Object.Builder)super.clearOneof(oneof);
         }

         public MysqlxExpr.Object.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (MysqlxExpr.Object.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxExpr.Object.Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.Object.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxExpr.Object.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxExpr.Object) {
               return this.mergeFrom((MysqlxExpr.Object)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxExpr.Object.Builder mergeFrom(MysqlxExpr.Object other) {
            if (other == MysqlxExpr.Object.getDefaultInstance()) {
               return this;
            } else {
               if (this.fldBuilder_ == null) {
                  if (!other.fld_.isEmpty()) {
                     if (this.fld_.isEmpty()) {
                        this.fld_ = other.fld_;
                        this.bitField0_ &= -2;
                     } else {
                        this.ensureFldIsMutable();
                        this.fld_.addAll(other.fld_);
                     }

                     this.onChanged();
                  }
               } else if (!other.fld_.isEmpty()) {
                  if (this.fldBuilder_.isEmpty()) {
                     this.fldBuilder_.dispose();
                     this.fldBuilder_ = null;
                     this.fld_ = other.fld_;
                     this.bitField0_ &= -2;
                     this.fldBuilder_ = MysqlxExpr.Object.alwaysUseFieldBuilders ? this.getFldFieldBuilder() : null;
                  } else {
                     this.fldBuilder_.addAllMessages(other.fld_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            for (int i = 0; i < this.getFldCount(); i++) {
               if (!this.getFld(i).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public MysqlxExpr.Object.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxExpr.Object parsedMessage = null;

            try {
               parsedMessage = MysqlxExpr.Object.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxExpr.Object)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         private void ensureFldIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
               this.fld_ = new ArrayList<>(this.fld_);
               this.bitField0_ |= 1;
            }
         }

         @Override
         public List<MysqlxExpr.Object.ObjectField> getFldList() {
            return this.fldBuilder_ == null ? Collections.unmodifiableList(this.fld_) : this.fldBuilder_.getMessageList();
         }

         @Override
         public int getFldCount() {
            return this.fldBuilder_ == null ? this.fld_.size() : this.fldBuilder_.getCount();
         }

         @Override
         public MysqlxExpr.Object.ObjectField getFld(int index) {
            return this.fldBuilder_ == null ? this.fld_.get(index) : this.fldBuilder_.getMessage(index);
         }

         public MysqlxExpr.Object.Builder setFld(int index, MysqlxExpr.Object.ObjectField value) {
            if (this.fldBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureFldIsMutable();
               this.fld_.set(index, value);
               this.onChanged();
            } else {
               this.fldBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxExpr.Object.Builder setFld(int index, MysqlxExpr.Object.ObjectField.Builder builderForValue) {
            if (this.fldBuilder_ == null) {
               this.ensureFldIsMutable();
               this.fld_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.fldBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.Object.Builder addFld(MysqlxExpr.Object.ObjectField value) {
            if (this.fldBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureFldIsMutable();
               this.fld_.add(value);
               this.onChanged();
            } else {
               this.fldBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxExpr.Object.Builder addFld(int index, MysqlxExpr.Object.ObjectField value) {
            if (this.fldBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureFldIsMutable();
               this.fld_.add(index, value);
               this.onChanged();
            } else {
               this.fldBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxExpr.Object.Builder addFld(MysqlxExpr.Object.ObjectField.Builder builderForValue) {
            if (this.fldBuilder_ == null) {
               this.ensureFldIsMutable();
               this.fld_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.fldBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.Object.Builder addFld(int index, MysqlxExpr.Object.ObjectField.Builder builderForValue) {
            if (this.fldBuilder_ == null) {
               this.ensureFldIsMutable();
               this.fld_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.fldBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.Object.Builder addAllFld(Iterable<? extends MysqlxExpr.Object.ObjectField> values) {
            if (this.fldBuilder_ == null) {
               this.ensureFldIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.fld_);
               this.onChanged();
            } else {
               this.fldBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxExpr.Object.Builder clearFld() {
            if (this.fldBuilder_ == null) {
               this.fld_ = Collections.emptyList();
               this.bitField0_ &= -2;
               this.onChanged();
            } else {
               this.fldBuilder_.clear();
            }

            return this;
         }

         public MysqlxExpr.Object.Builder removeFld(int index) {
            if (this.fldBuilder_ == null) {
               this.ensureFldIsMutable();
               this.fld_.remove(index);
               this.onChanged();
            } else {
               this.fldBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxExpr.Object.ObjectField.Builder getFldBuilder(int index) {
            return this.getFldFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxExpr.Object.ObjectFieldOrBuilder getFldOrBuilder(int index) {
            return this.fldBuilder_ == null ? this.fld_.get(index) : this.fldBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxExpr.Object.ObjectFieldOrBuilder> getFldOrBuilderList() {
            return this.fldBuilder_ != null ? this.fldBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.fld_);
         }

         public MysqlxExpr.Object.ObjectField.Builder addFldBuilder() {
            return this.getFldFieldBuilder().addBuilder(MysqlxExpr.Object.ObjectField.getDefaultInstance());
         }

         public MysqlxExpr.Object.ObjectField.Builder addFldBuilder(int index) {
            return this.getFldFieldBuilder().addBuilder(index, MysqlxExpr.Object.ObjectField.getDefaultInstance());
         }

         public List<MysqlxExpr.Object.ObjectField.Builder> getFldBuilderList() {
            return this.getFldFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxExpr.Object.ObjectField, MysqlxExpr.Object.ObjectField.Builder, MysqlxExpr.Object.ObjectFieldOrBuilder> getFldFieldBuilder() {
            if (this.fldBuilder_ == null) {
               this.fldBuilder_ = new RepeatedFieldBuilderV3<>(this.fld_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
               this.fld_ = null;
            }

            return this.fldBuilder_;
         }

         public final MysqlxExpr.Object.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.Object.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxExpr.Object.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.Object.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static final class ObjectField extends GeneratedMessageV3 implements MysqlxExpr.Object.ObjectFieldOrBuilder {
         private static final long serialVersionUID = 0L;
         private int bitField0_;
         public static final int KEY_FIELD_NUMBER = 1;
         private volatile java.lang.Object key_;
         public static final int VALUE_FIELD_NUMBER = 2;
         private MysqlxExpr.Expr value_;
         private byte memoizedIsInitialized = -1;
         private static final MysqlxExpr.Object.ObjectField DEFAULT_INSTANCE = new MysqlxExpr.Object.ObjectField();
         @Deprecated
         public static final Parser<MysqlxExpr.Object.ObjectField> PARSER = new AbstractParser<MysqlxExpr.Object.ObjectField>() {
            public MysqlxExpr.Object.ObjectField parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
               return new MysqlxExpr.Object.ObjectField(input, extensionRegistry);
            }
         };

         private ObjectField(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
         }

         private ObjectField() {
            this.key_ = "";
         }

         @Override
         protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new MysqlxExpr.Object.ObjectField();
         }

         @Override
         public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
         }

         private ObjectField(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
               throw new NullPointerException();
            } else {
               int mutable_bitField0_ = 0;
               UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

               try {
                  boolean done = false;

                  while (!done) {
                     int tag = input.readTag();
                     switch (tag) {
                        case 0:
                           done = true;
                           break;
                        case 10:
                           ByteString bs = input.readBytes();
                           this.bitField0_ |= 1;
                           this.key_ = bs;
                           break;
                        case 18:
                           MysqlxExpr.Expr.Builder subBuilder = null;
                           if ((this.bitField0_ & 2) != 0) {
                              subBuilder = this.value_.toBuilder();
                           }

                           this.value_ = input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry);
                           if (subBuilder != null) {
                              subBuilder.mergeFrom(this.value_);
                              this.value_ = subBuilder.buildPartial();
                           }

                           this.bitField0_ |= 2;
                           break;
                        default:
                           if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                              done = true;
                           }
                     }
                  }
               } catch (InvalidProtocolBufferException var12) {
                  throw var12.setUnfinishedMessage(this);
               } catch (IOException var13) {
                  throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
               } finally {
                  this.unknownFields = unknownFields.build();
                  this.makeExtensionsImmutable();
               }
            }
         }

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxExpr.Object.ObjectField.class, MysqlxExpr.Object.ObjectField.Builder.class);
         }

         @Override
         public boolean hasKey() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getKey() {
            java.lang.Object ref = this.key_;
            if (ref instanceof String) {
               return (String)ref;
            } else {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.key_ = s;
               }

               return s;
            }
         }

         @Override
         public ByteString getKeyBytes() {
            java.lang.Object ref = this.key_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.key_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         @Override
         public boolean hasValue() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxExpr.Expr getValue() {
            return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getValueOrBuilder() {
            return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
         }

         @Override
         public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
               return true;
            } else if (isInitialized == 0) {
               return false;
            } else if (!this.hasKey()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else if (!this.hasValue()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else if (!this.getValue().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else {
               this.memoizedIsInitialized = 1;
               return true;
            }
         }

         @Override
         public void writeTo(CodedOutputStream output) throws IOException {
            if ((this.bitField0_ & 1) != 0) {
               GeneratedMessageV3.writeString(output, 1, this.key_);
            }

            if ((this.bitField0_ & 2) != 0) {
               output.writeMessage(2, this.getValue());
            }

            this.unknownFields.writeTo(output);
         }

         @Override
         public int getSerializedSize() {
            int size = this.memoizedSize;
            if (size != -1) {
               return size;
            } else {
               size = 0;
               if ((this.bitField0_ & 1) != 0) {
                  size += GeneratedMessageV3.computeStringSize(1, this.key_);
               }

               if ((this.bitField0_ & 2) != 0) {
                  size += CodedOutputStream.computeMessageSize(2, this.getValue());
               }

               size += this.unknownFields.getSerializedSize();
               this.memoizedSize = size;
               return size;
            }
         }

         @Override
         public boolean equals(java.lang.Object obj) {
            if (obj == this) {
               return true;
            } else if (!(obj instanceof MysqlxExpr.Object.ObjectField)) {
               return super.equals(obj);
            } else {
               MysqlxExpr.Object.ObjectField other = (MysqlxExpr.Object.ObjectField)obj;
               if (this.hasKey() != other.hasKey()) {
                  return false;
               } else if (this.hasKey() && !this.getKey().equals(other.getKey())) {
                  return false;
               } else if (this.hasValue() != other.hasValue()) {
                  return false;
               } else {
                  return this.hasValue() && !this.getValue().equals(other.getValue()) ? false : this.unknownFields.equals(other.unknownFields);
               }
            }
         }

         @Override
         public int hashCode() {
            if (this.memoizedHashCode != 0) {
               return this.memoizedHashCode;
            } else {
               int hash = 41;
               hash = 19 * hash + getDescriptor().hashCode();
               if (this.hasKey()) {
                  hash = 37 * hash + 1;
                  hash = 53 * hash + this.getKey().hashCode();
               }

               if (this.hasValue()) {
                  hash = 37 * hash + 2;
                  hash = 53 * hash + this.getValue().hashCode();
               }

               hash = 29 * hash + this.unknownFields.hashCode();
               this.memoizedHashCode = hash;
               return hash;
            }
         }

         public static MysqlxExpr.Object.ObjectField parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxExpr.Object.ObjectField parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxExpr.Object.ObjectField parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxExpr.Object.ObjectField parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxExpr.Object.ObjectField parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxExpr.Object.ObjectField parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxExpr.Object.ObjectField parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static MysqlxExpr.Object.ObjectField parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public static MysqlxExpr.Object.ObjectField parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
         }

         public static MysqlxExpr.Object.ObjectField parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
         }

         public static MysqlxExpr.Object.ObjectField parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static MysqlxExpr.Object.ObjectField parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public MysqlxExpr.Object.ObjectField.Builder newBuilderForType() {
            return newBuilder();
         }

         public static MysqlxExpr.Object.ObjectField.Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
         }

         public static MysqlxExpr.Object.ObjectField.Builder newBuilder(MysqlxExpr.Object.ObjectField prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
         }

         public MysqlxExpr.Object.ObjectField.Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new MysqlxExpr.Object.ObjectField.Builder() : new MysqlxExpr.Object.ObjectField.Builder().mergeFrom(this);
         }

         protected MysqlxExpr.Object.ObjectField.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            return new MysqlxExpr.Object.ObjectField.Builder(parent);
         }

         public static MysqlxExpr.Object.ObjectField getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<MysqlxExpr.Object.ObjectField> parser() {
            return PARSER;
         }

         @Override
         public Parser<MysqlxExpr.Object.ObjectField> getParserForType() {
            return PARSER;
         }

         public MysqlxExpr.Object.ObjectField getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
         }

         public static final class Builder
            extends GeneratedMessageV3.Builder<MysqlxExpr.Object.ObjectField.Builder>
            implements MysqlxExpr.Object.ObjectFieldOrBuilder {
            private int bitField0_;
            private java.lang.Object key_ = "";
            private MysqlxExpr.Expr value_;
            private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> valueBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
               return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
               return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_fieldAccessorTable
                  .ensureFieldAccessorsInitialized(MysqlxExpr.Object.ObjectField.class, MysqlxExpr.Object.ObjectField.Builder.class);
            }

            private Builder() {
               this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
               super(parent);
               this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
               if (MysqlxExpr.Object.ObjectField.alwaysUseFieldBuilders) {
                  this.getValueFieldBuilder();
               }
            }

            public MysqlxExpr.Object.ObjectField.Builder clear() {
               super.clear();
               this.key_ = "";
               this.bitField0_ &= -2;
               if (this.valueBuilder_ == null) {
                  this.value_ = null;
               } else {
                  this.valueBuilder_.clear();
               }

               this.bitField0_ &= -3;
               return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
               return MysqlxExpr.internal_static_Mysqlx_Expr_Object_ObjectField_descriptor;
            }

            public MysqlxExpr.Object.ObjectField getDefaultInstanceForType() {
               return MysqlxExpr.Object.ObjectField.getDefaultInstance();
            }

            public MysqlxExpr.Object.ObjectField build() {
               MysqlxExpr.Object.ObjectField result = this.buildPartial();
               if (!result.isInitialized()) {
                  throw newUninitializedMessageException(result);
               } else {
                  return result;
               }
            }

            public MysqlxExpr.Object.ObjectField buildPartial() {
               MysqlxExpr.Object.ObjectField result = new MysqlxExpr.Object.ObjectField(this);
               int from_bitField0_ = this.bitField0_;
               int to_bitField0_ = 0;
               if ((from_bitField0_ & 1) != 0) {
                  to_bitField0_ |= 1;
               }

               result.key_ = this.key_;
               if ((from_bitField0_ & 2) != 0) {
                  if (this.valueBuilder_ == null) {
                     result.value_ = this.value_;
                  } else {
                     result.value_ = this.valueBuilder_.build();
                  }

                  to_bitField0_ |= 2;
               }

               result.bitField0_ = to_bitField0_;
               this.onBuilt();
               return result;
            }

            public MysqlxExpr.Object.ObjectField.Builder clone() {
               return (MysqlxExpr.Object.ObjectField.Builder)super.clone();
            }

            public MysqlxExpr.Object.ObjectField.Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
               return (MysqlxExpr.Object.ObjectField.Builder)super.setField(field, value);
            }

            public MysqlxExpr.Object.ObjectField.Builder clearField(Descriptors.FieldDescriptor field) {
               return (MysqlxExpr.Object.ObjectField.Builder)super.clearField(field);
            }

            public MysqlxExpr.Object.ObjectField.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
               return (MysqlxExpr.Object.ObjectField.Builder)super.clearOneof(oneof);
            }

            public MysqlxExpr.Object.ObjectField.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
               return (MysqlxExpr.Object.ObjectField.Builder)super.setRepeatedField(field, index, value);
            }

            public MysqlxExpr.Object.ObjectField.Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
               return (MysqlxExpr.Object.ObjectField.Builder)super.addRepeatedField(field, value);
            }

            public MysqlxExpr.Object.ObjectField.Builder mergeFrom(Message other) {
               if (other instanceof MysqlxExpr.Object.ObjectField) {
                  return this.mergeFrom((MysqlxExpr.Object.ObjectField)other);
               } else {
                  super.mergeFrom(other);
                  return this;
               }
            }

            public MysqlxExpr.Object.ObjectField.Builder mergeFrom(MysqlxExpr.Object.ObjectField other) {
               if (other == MysqlxExpr.Object.ObjectField.getDefaultInstance()) {
                  return this;
               } else {
                  if (other.hasKey()) {
                     this.bitField0_ |= 1;
                     this.key_ = other.key_;
                     this.onChanged();
                  }

                  if (other.hasValue()) {
                     this.mergeValue(other.getValue());
                  }

                  this.mergeUnknownFields(other.unknownFields);
                  this.onChanged();
                  return this;
               }
            }

            @Override
            public final boolean isInitialized() {
               if (!this.hasKey()) {
                  return false;
               } else {
                  return !this.hasValue() ? false : this.getValue().isInitialized();
               }
            }

            public MysqlxExpr.Object.ObjectField.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
               MysqlxExpr.Object.ObjectField parsedMessage = null;

               try {
                  parsedMessage = MysqlxExpr.Object.ObjectField.PARSER.parsePartialFrom(input, extensionRegistry);
               } catch (InvalidProtocolBufferException var8) {
                  parsedMessage = (MysqlxExpr.Object.ObjectField)var8.getUnfinishedMessage();
                  throw var8.unwrapIOException();
               } finally {
                  if (parsedMessage != null) {
                     this.mergeFrom(parsedMessage);
                  }
               }

               return this;
            }

            @Override
            public boolean hasKey() {
               return (this.bitField0_ & 1) != 0;
            }

            @Override
            public String getKey() {
               java.lang.Object ref = this.key_;
               if (!(ref instanceof String)) {
                  ByteString bs = (ByteString)ref;
                  String s = bs.toStringUtf8();
                  if (bs.isValidUtf8()) {
                     this.key_ = s;
                  }

                  return s;
               } else {
                  return (String)ref;
               }
            }

            @Override
            public ByteString getKeyBytes() {
               java.lang.Object ref = this.key_;
               if (ref instanceof String) {
                  ByteString b = ByteString.copyFromUtf8((String)ref);
                  this.key_ = b;
                  return b;
               } else {
                  return (ByteString)ref;
               }
            }

            public MysqlxExpr.Object.ObjectField.Builder setKey(String value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 1;
                  this.key_ = value;
                  this.onChanged();
                  return this;
               }
            }

            public MysqlxExpr.Object.ObjectField.Builder clearKey() {
               this.bitField0_ &= -2;
               this.key_ = MysqlxExpr.Object.ObjectField.getDefaultInstance().getKey();
               this.onChanged();
               return this;
            }

            public MysqlxExpr.Object.ObjectField.Builder setKeyBytes(ByteString value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 1;
                  this.key_ = value;
                  this.onChanged();
                  return this;
               }
            }

            @Override
            public boolean hasValue() {
               return (this.bitField0_ & 2) != 0;
            }

            @Override
            public MysqlxExpr.Expr getValue() {
               if (this.valueBuilder_ == null) {
                  return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
               } else {
                  return this.valueBuilder_.getMessage();
               }
            }

            public MysqlxExpr.Object.ObjectField.Builder setValue(MysqlxExpr.Expr value) {
               if (this.valueBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.value_ = value;
                  this.onChanged();
               } else {
                  this.valueBuilder_.setMessage(value);
               }

               this.bitField0_ |= 2;
               return this;
            }

            public MysqlxExpr.Object.ObjectField.Builder setValue(MysqlxExpr.Expr.Builder builderForValue) {
               if (this.valueBuilder_ == null) {
                  this.value_ = builderForValue.build();
                  this.onChanged();
               } else {
                  this.valueBuilder_.setMessage(builderForValue.build());
               }

               this.bitField0_ |= 2;
               return this;
            }

            public MysqlxExpr.Object.ObjectField.Builder mergeValue(MysqlxExpr.Expr value) {
               if (this.valueBuilder_ == null) {
                  if ((this.bitField0_ & 2) != 0 && this.value_ != null && this.value_ != MysqlxExpr.Expr.getDefaultInstance()) {
                     this.value_ = MysqlxExpr.Expr.newBuilder(this.value_).mergeFrom(value).buildPartial();
                  } else {
                     this.value_ = value;
                  }

                  this.onChanged();
               } else {
                  this.valueBuilder_.mergeFrom(value);
               }

               this.bitField0_ |= 2;
               return this;
            }

            public MysqlxExpr.Object.ObjectField.Builder clearValue() {
               if (this.valueBuilder_ == null) {
                  this.value_ = null;
                  this.onChanged();
               } else {
                  this.valueBuilder_.clear();
               }

               this.bitField0_ &= -3;
               return this;
            }

            public MysqlxExpr.Expr.Builder getValueBuilder() {
               this.bitField0_ |= 2;
               this.onChanged();
               return this.getValueFieldBuilder().getBuilder();
            }

            @Override
            public MysqlxExpr.ExprOrBuilder getValueOrBuilder() {
               if (this.valueBuilder_ != null) {
                  return this.valueBuilder_.getMessageOrBuilder();
               } else {
                  return this.value_ == null ? MysqlxExpr.Expr.getDefaultInstance() : this.value_;
               }
            }

            private SingleFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getValueFieldBuilder() {
               if (this.valueBuilder_ == null) {
                  this.valueBuilder_ = new SingleFieldBuilderV3<>(this.getValue(), this.getParentForChildren(), this.isClean());
                  this.value_ = null;
               }

               return this.valueBuilder_;
            }

            public final MysqlxExpr.Object.ObjectField.Builder setUnknownFields(UnknownFieldSet unknownFields) {
               return (MysqlxExpr.Object.ObjectField.Builder)super.setUnknownFields(unknownFields);
            }

            public final MysqlxExpr.Object.ObjectField.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
               return (MysqlxExpr.Object.ObjectField.Builder)super.mergeUnknownFields(unknownFields);
            }
         }
      }

      public interface ObjectFieldOrBuilder extends MessageOrBuilder {
         boolean hasKey();

         String getKey();

         ByteString getKeyBytes();

         boolean hasValue();

         MysqlxExpr.Expr getValue();

         MysqlxExpr.ExprOrBuilder getValueOrBuilder();
      }
   }

   public interface ObjectOrBuilder extends MessageOrBuilder {
      List<MysqlxExpr.Object.ObjectField> getFldList();

      MysqlxExpr.Object.ObjectField getFld(int var1);

      int getFldCount();

      List<? extends MysqlxExpr.Object.ObjectFieldOrBuilder> getFldOrBuilderList();

      MysqlxExpr.Object.ObjectFieldOrBuilder getFldOrBuilder(int var1);
   }

   public static final class Operator extends GeneratedMessageV3 implements MysqlxExpr.OperatorOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private volatile java.lang.Object name_;
      public static final int PARAM_FIELD_NUMBER = 2;
      private List<MysqlxExpr.Expr> param_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxExpr.Operator DEFAULT_INSTANCE = new MysqlxExpr.Operator();
      @Deprecated
      public static final Parser<MysqlxExpr.Operator> PARSER = new AbstractParser<MysqlxExpr.Operator>() {
         public MysqlxExpr.Operator parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxExpr.Operator(input, extensionRegistry);
         }
      };

      private Operator(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Operator() {
         this.name_ = "";
         this.param_ = Collections.emptyList();
      }

      @Override
      protected java.lang.Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxExpr.Operator();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Operator(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            int mutable_bitField0_ = 0;
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     case 10:
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 1;
                        this.name_ = bs;
                        break;
                     case 18:
                        if ((mutable_bitField0_ & 2) == 0) {
                           this.param_ = new ArrayList<>();
                           mutable_bitField0_ |= 2;
                        }

                        this.param_.add(input.readMessage(MysqlxExpr.Expr.PARSER, extensionRegistry));
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
            } finally {
               if ((mutable_bitField0_ & 2) != 0) {
                  this.param_ = Collections.unmodifiableList(this.param_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxExpr.Operator.class, MysqlxExpr.Operator.Builder.class);
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         java.lang.Object ref = this.name_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.name_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getNameBytes() {
         java.lang.Object ref = this.name_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public List<MysqlxExpr.Expr> getParamList() {
         return this.param_;
      }

      @Override
      public List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList() {
         return this.param_;
      }

      @Override
      public int getParamCount() {
         return this.param_.size();
      }

      @Override
      public MysqlxExpr.Expr getParam(int index) {
         return this.param_.get(index);
      }

      @Override
      public MysqlxExpr.ExprOrBuilder getParamOrBuilder(int index) {
         return this.param_.get(index);
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasName()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            for (int i = 0; i < this.getParamCount(); i++) {
               if (!this.getParam(i).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 1, this.name_);
         }

         for (int i = 0; i < this.param_.size(); i++) {
            output.writeMessage(2, this.param_.get(i));
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            size = 0;
            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(1, this.name_);
            }

            for (int i = 0; i < this.param_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(2, this.param_.get(i));
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(java.lang.Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxExpr.Operator)) {
            return super.equals(obj);
         } else {
            MysqlxExpr.Operator other = (MysqlxExpr.Operator)obj;
            if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else {
               return !this.getParamList().equals(other.getParamList()) ? false : this.unknownFields.equals(other.unknownFields);
            }
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.hasName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.getParamCount() > 0) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getParamList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxExpr.Operator parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Operator parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Operator parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Operator parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Operator parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpr.Operator parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpr.Operator parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Operator parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.Operator parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Operator parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpr.Operator parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpr.Operator parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxExpr.Operator.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxExpr.Operator.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxExpr.Operator.Builder newBuilder(MysqlxExpr.Operator prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxExpr.Operator.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxExpr.Operator.Builder() : new MysqlxExpr.Operator.Builder().mergeFrom(this);
      }

      protected MysqlxExpr.Operator.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxExpr.Operator.Builder(parent);
      }

      public static MysqlxExpr.Operator getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxExpr.Operator> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxExpr.Operator> getParserForType() {
         return PARSER;
      }

      public MysqlxExpr.Operator getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxExpr.Operator.Builder> implements MysqlxExpr.OperatorOrBuilder {
         private int bitField0_;
         private java.lang.Object name_ = "";
         private List<MysqlxExpr.Expr> param_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> paramBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxExpr.Operator.class, MysqlxExpr.Operator.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpr.Operator.alwaysUseFieldBuilders) {
               this.getParamFieldBuilder();
            }
         }

         public MysqlxExpr.Operator.Builder clear() {
            super.clear();
            this.name_ = "";
            this.bitField0_ &= -2;
            if (this.paramBuilder_ == null) {
               this.param_ = Collections.emptyList();
               this.bitField0_ &= -3;
            } else {
               this.paramBuilder_.clear();
            }

            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpr.internal_static_Mysqlx_Expr_Operator_descriptor;
         }

         public MysqlxExpr.Operator getDefaultInstanceForType() {
            return MysqlxExpr.Operator.getDefaultInstance();
         }

         public MysqlxExpr.Operator build() {
            MysqlxExpr.Operator result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxExpr.Operator buildPartial() {
            MysqlxExpr.Operator result = new MysqlxExpr.Operator(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.name_ = this.name_;
            if (this.paramBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0) {
                  this.param_ = Collections.unmodifiableList(this.param_);
                  this.bitField0_ &= -3;
               }

               result.param_ = this.param_;
            } else {
               result.param_ = this.paramBuilder_.build();
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxExpr.Operator.Builder clone() {
            return (MysqlxExpr.Operator.Builder)super.clone();
         }

         public MysqlxExpr.Operator.Builder setField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.Operator.Builder)super.setField(field, value);
         }

         public MysqlxExpr.Operator.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxExpr.Operator.Builder)super.clearField(field);
         }

         public MysqlxExpr.Operator.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxExpr.Operator.Builder)super.clearOneof(oneof);
         }

         public MysqlxExpr.Operator.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
            return (MysqlxExpr.Operator.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxExpr.Operator.Builder addRepeatedField(Descriptors.FieldDescriptor field, java.lang.Object value) {
            return (MysqlxExpr.Operator.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxExpr.Operator.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxExpr.Operator) {
               return this.mergeFrom((MysqlxExpr.Operator)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxExpr.Operator.Builder mergeFrom(MysqlxExpr.Operator other) {
            if (other == MysqlxExpr.Operator.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasName()) {
                  this.bitField0_ |= 1;
                  this.name_ = other.name_;
                  this.onChanged();
               }

               if (this.paramBuilder_ == null) {
                  if (!other.param_.isEmpty()) {
                     if (this.param_.isEmpty()) {
                        this.param_ = other.param_;
                        this.bitField0_ &= -3;
                     } else {
                        this.ensureParamIsMutable();
                        this.param_.addAll(other.param_);
                     }

                     this.onChanged();
                  }
               } else if (!other.param_.isEmpty()) {
                  if (this.paramBuilder_.isEmpty()) {
                     this.paramBuilder_.dispose();
                     this.paramBuilder_ = null;
                     this.param_ = other.param_;
                     this.bitField0_ &= -3;
                     this.paramBuilder_ = MysqlxExpr.Operator.alwaysUseFieldBuilders ? this.getParamFieldBuilder() : null;
                  } else {
                     this.paramBuilder_.addAllMessages(other.param_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasName()) {
               return false;
            } else {
               for (int i = 0; i < this.getParamCount(); i++) {
                  if (!this.getParam(i).isInitialized()) {
                     return false;
                  }
               }

               return true;
            }
         }

         public MysqlxExpr.Operator.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxExpr.Operator parsedMessage = null;

            try {
               parsedMessage = MysqlxExpr.Operator.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxExpr.Operator)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasName() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getName() {
            java.lang.Object ref = this.name_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.name_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getNameBytes() {
            java.lang.Object ref = this.name_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.name_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxExpr.Operator.Builder setName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxExpr.Operator.Builder clearName() {
            this.bitField0_ &= -2;
            this.name_ = MysqlxExpr.Operator.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         public MysqlxExpr.Operator.Builder setNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         private void ensureParamIsMutable() {
            if ((this.bitField0_ & 2) == 0) {
               this.param_ = new ArrayList<>(this.param_);
               this.bitField0_ |= 2;
            }
         }

         @Override
         public List<MysqlxExpr.Expr> getParamList() {
            return this.paramBuilder_ == null ? Collections.unmodifiableList(this.param_) : this.paramBuilder_.getMessageList();
         }

         @Override
         public int getParamCount() {
            return this.paramBuilder_ == null ? this.param_.size() : this.paramBuilder_.getCount();
         }

         @Override
         public MysqlxExpr.Expr getParam(int index) {
            return this.paramBuilder_ == null ? this.param_.get(index) : this.paramBuilder_.getMessage(index);
         }

         public MysqlxExpr.Operator.Builder setParam(int index, MysqlxExpr.Expr value) {
            if (this.paramBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureParamIsMutable();
               this.param_.set(index, value);
               this.onChanged();
            } else {
               this.paramBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxExpr.Operator.Builder setParam(int index, MysqlxExpr.Expr.Builder builderForValue) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.paramBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.Operator.Builder addParam(MysqlxExpr.Expr value) {
            if (this.paramBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureParamIsMutable();
               this.param_.add(value);
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxExpr.Operator.Builder addParam(int index, MysqlxExpr.Expr value) {
            if (this.paramBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureParamIsMutable();
               this.param_.add(index, value);
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxExpr.Operator.Builder addParam(MysqlxExpr.Expr.Builder builderForValue) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.Operator.Builder addParam(int index, MysqlxExpr.Expr.Builder builderForValue) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.paramBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxExpr.Operator.Builder addAllParam(Iterable<? extends MysqlxExpr.Expr> values) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.param_);
               this.onChanged();
            } else {
               this.paramBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxExpr.Operator.Builder clearParam() {
            if (this.paramBuilder_ == null) {
               this.param_ = Collections.emptyList();
               this.bitField0_ &= -3;
               this.onChanged();
            } else {
               this.paramBuilder_.clear();
            }

            return this;
         }

         public MysqlxExpr.Operator.Builder removeParam(int index) {
            if (this.paramBuilder_ == null) {
               this.ensureParamIsMutable();
               this.param_.remove(index);
               this.onChanged();
            } else {
               this.paramBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxExpr.Expr.Builder getParamBuilder(int index) {
            return this.getParamFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxExpr.ExprOrBuilder getParamOrBuilder(int index) {
            return this.paramBuilder_ == null ? this.param_.get(index) : this.paramBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList() {
            return this.paramBuilder_ != null ? this.paramBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.param_);
         }

         public MysqlxExpr.Expr.Builder addParamBuilder() {
            return this.getParamFieldBuilder().addBuilder(MysqlxExpr.Expr.getDefaultInstance());
         }

         public MysqlxExpr.Expr.Builder addParamBuilder(int index) {
            return this.getParamFieldBuilder().addBuilder(index, MysqlxExpr.Expr.getDefaultInstance());
         }

         public List<MysqlxExpr.Expr.Builder> getParamBuilderList() {
            return this.getParamFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxExpr.Expr, MysqlxExpr.Expr.Builder, MysqlxExpr.ExprOrBuilder> getParamFieldBuilder() {
            if (this.paramBuilder_ == null) {
               this.paramBuilder_ = new RepeatedFieldBuilderV3<>(this.param_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
               this.param_ = null;
            }

            return this.paramBuilder_;
         }

         public final MysqlxExpr.Operator.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.Operator.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxExpr.Operator.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpr.Operator.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface OperatorOrBuilder extends MessageOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      List<MysqlxExpr.Expr> getParamList();

      MysqlxExpr.Expr getParam(int var1);

      int getParamCount();

      List<? extends MysqlxExpr.ExprOrBuilder> getParamOrBuilderList();

      MysqlxExpr.ExprOrBuilder getParamOrBuilder(int var1);
   }
}
