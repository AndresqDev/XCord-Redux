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
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxExpect {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expect_Open_descriptor = getDescriptor().getMessageTypes().get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expect_Open_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Expect_Open_descriptor, new String[]{"Op", "Cond"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expect_Open_Condition_descriptor = internal_static_Mysqlx_Expect_Open_descriptor.getNestedTypes()
      .get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Expect_Open_Condition_descriptor, new String[]{"ConditionKey", "ConditionValue", "Op"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Expect_Close_descriptor = getDescriptor().getMessageTypes().get(1);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Expect_Close_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Expect_Close_descriptor, new String[0]
   );
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxExpect() {
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
         "\n\u0013mysqlx_expect.proto\u0012\rMysqlx.Expect\u001a\fmysqlx.proto\"Ö\u0003\n\u0004Open\u0012B\n\u0002op\u0018\u0001 \u0001(\u000e2 .Mysqlx.Expect.Open.CtxOperation:\u0014EXPECT_CTX_COPY_PREV\u0012+\n\u0004cond\u0018\u0002 \u0003(\u000b2\u001d.Mysqlx.Expect.Open.Condition\u001a\u0096\u0002\n\tCondition\u0012\u0015\n\rcondition_key\u0018\u0001 \u0002(\r\u0012\u0017\n\u000fcondition_value\u0018\u0002 \u0001(\f\u0012K\n\u0002op\u0018\u0003 \u0001(\u000e20.Mysqlx.Expect.Open.Condition.ConditionOperation:\rEXPECT_OP_SET\"N\n\u0003Key\u0012\u0013\n\u000fEXPECT_NO_ERROR\u0010\u0001\u0012\u0016\n\u0012EXPECT_FIELD_EXIST\u0010\u0002\u0012\u001a\n\u0016EXPECT_DOCID_GENERATED\u0010\u0003\"<\n\u0012ConditionOperation\u0012\u0011\n\rEXPECT_OP_SET\u0010\u0000\u0012\u0013\n\u000fEXPECT_OP_UNSET\u0010\u0001\">\n\fCtxOperation\u0012\u0018\n\u0014EXPECT_CTX_COPY_PREV\u0010\u0000\u0012\u0014\n\u0010EXPECT_CTX_EMPTY\u0010\u0001:\u0004\u0088ê0\u0018\"\r\n\u0005Close:\u0004\u0088ê0\u0019B\u0019\n\u0017com.mysql.cj.x.protobuf"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor()});
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.clientMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      Mysqlx.getDescriptor();
   }

   public static final class Close extends GeneratedMessageV3 implements MysqlxExpect.CloseOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxExpect.Close DEFAULT_INSTANCE = new MysqlxExpect.Close();
      @Deprecated
      public static final Parser<MysqlxExpect.Close> PARSER = new AbstractParser<MysqlxExpect.Close>() {
         public MysqlxExpect.Close parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxExpect.Close(input, extensionRegistry);
         }
      };

      private Close(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Close() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxExpect.Close();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Close(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         this();
         if (extensionRegistry == null) {
            throw new NullPointerException();
         } else {
            UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();

            try {
               boolean done = false;

               while (!done) {
                  int tag = input.readTag();
                  switch (tag) {
                     case 0:
                        done = true;
                        break;
                     default:
                        if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                           done = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var10) {
               throw var10.setUnfinishedMessage(this);
            } catch (IOException var11) {
               throw new InvalidProtocolBufferException(var11).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpect.internal_static_Mysqlx_Expect_Close_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpect.internal_static_Mysqlx_Expect_Close_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxExpect.Close.class, MysqlxExpect.Close.Builder.class);
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream output) throws IOException {
         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            int var2 = 0;
            var2 += this.unknownFields.getSerializedSize();
            this.memoizedSize = var2;
            return var2;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxExpect.Close)) {
            return super.equals(obj);
         } else {
            MysqlxExpect.Close other = (MysqlxExpect.Close)obj;
            return this.unknownFields.equals(other.unknownFields);
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxExpect.Close parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpect.Close parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpect.Close parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpect.Close parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpect.Close parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpect.Close parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpect.Close parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpect.Close parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpect.Close parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxExpect.Close parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpect.Close parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpect.Close parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxExpect.Close.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxExpect.Close.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxExpect.Close.Builder newBuilder(MysqlxExpect.Close prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxExpect.Close.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxExpect.Close.Builder() : new MysqlxExpect.Close.Builder().mergeFrom(this);
      }

      protected MysqlxExpect.Close.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxExpect.Close.Builder(parent);
      }

      public static MysqlxExpect.Close getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxExpect.Close> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxExpect.Close> getParserForType() {
         return PARSER;
      }

      public MysqlxExpect.Close getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxExpect.Close.Builder> implements MysqlxExpect.CloseOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpect.internal_static_Mysqlx_Expect_Close_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpect.internal_static_Mysqlx_Expect_Close_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxExpect.Close.class, MysqlxExpect.Close.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpect.Close.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxExpect.Close.Builder clear() {
            super.clear();
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpect.internal_static_Mysqlx_Expect_Close_descriptor;
         }

         public MysqlxExpect.Close getDefaultInstanceForType() {
            return MysqlxExpect.Close.getDefaultInstance();
         }

         public MysqlxExpect.Close build() {
            MysqlxExpect.Close result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxExpect.Close buildPartial() {
            MysqlxExpect.Close result = new MysqlxExpect.Close(this);
            this.onBuilt();
            return result;
         }

         public MysqlxExpect.Close.Builder clone() {
            return (MysqlxExpect.Close.Builder)super.clone();
         }

         public MysqlxExpect.Close.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxExpect.Close.Builder)super.setField(field, value);
         }

         public MysqlxExpect.Close.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxExpect.Close.Builder)super.clearField(field);
         }

         public MysqlxExpect.Close.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxExpect.Close.Builder)super.clearOneof(oneof);
         }

         public MysqlxExpect.Close.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxExpect.Close.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxExpect.Close.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxExpect.Close.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxExpect.Close.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxExpect.Close) {
               return this.mergeFrom((MysqlxExpect.Close)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxExpect.Close.Builder mergeFrom(MysqlxExpect.Close other) {
            if (other == MysqlxExpect.Close.getDefaultInstance()) {
               return this;
            } else {
               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return true;
         }

         public MysqlxExpect.Close.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxExpect.Close parsedMessage = null;

            try {
               parsedMessage = MysqlxExpect.Close.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxExpect.Close)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         public final MysqlxExpect.Close.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpect.Close.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxExpect.Close.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpect.Close.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface CloseOrBuilder extends MessageOrBuilder {
   }

   public static final class Open extends GeneratedMessageV3 implements MysqlxExpect.OpenOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int OP_FIELD_NUMBER = 1;
      private int op_;
      public static final int COND_FIELD_NUMBER = 2;
      private List<MysqlxExpect.Open.Condition> cond_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxExpect.Open DEFAULT_INSTANCE = new MysqlxExpect.Open();
      @Deprecated
      public static final Parser<MysqlxExpect.Open> PARSER = new AbstractParser<MysqlxExpect.Open>() {
         public MysqlxExpect.Open parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxExpect.Open(input, extensionRegistry);
         }
      };

      private Open(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Open() {
         this.op_ = 0;
         this.cond_ = Collections.emptyList();
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxExpect.Open();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Open(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        MysqlxExpect.Open.CtxOperation value = MysqlxExpect.Open.CtxOperation.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(1, rawValue);
                        } else {
                           this.bitField0_ |= 1;
                           this.op_ = rawValue;
                        }
                        break;
                     case 18:
                        if ((mutable_bitField0_ & 2) == 0) {
                           this.cond_ = new ArrayList<>();
                           mutable_bitField0_ |= 2;
                        }

                        this.cond_.add(input.readMessage(MysqlxExpect.Open.Condition.PARSER, extensionRegistry));
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
               if ((mutable_bitField0_ & 2) != 0) {
                  this.cond_ = Collections.unmodifiableList(this.cond_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxExpect.internal_static_Mysqlx_Expect_Open_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxExpect.internal_static_Mysqlx_Expect_Open_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxExpect.Open.class, MysqlxExpect.Open.Builder.class);
      }

      @Override
      public boolean hasOp() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxExpect.Open.CtxOperation getOp() {
         MysqlxExpect.Open.CtxOperation result = MysqlxExpect.Open.CtxOperation.valueOf(this.op_);
         return result == null ? MysqlxExpect.Open.CtxOperation.EXPECT_CTX_COPY_PREV : result;
      }

      @Override
      public List<MysqlxExpect.Open.Condition> getCondList() {
         return this.cond_;
      }

      @Override
      public List<? extends MysqlxExpect.Open.ConditionOrBuilder> getCondOrBuilderList() {
         return this.cond_;
      }

      @Override
      public int getCondCount() {
         return this.cond_.size();
      }

      @Override
      public MysqlxExpect.Open.Condition getCond(int index) {
         return this.cond_.get(index);
      }

      @Override
      public MysqlxExpect.Open.ConditionOrBuilder getCondOrBuilder(int index) {
         return this.cond_.get(index);
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            for (int i = 0; i < this.getCondCount(); i++) {
               if (!this.getCond(i).isInitialized()) {
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
            output.writeEnum(1, this.op_);
         }

         for (int i = 0; i < this.cond_.size(); i++) {
            output.writeMessage(2, this.cond_.get(i));
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
               size += CodedOutputStream.computeEnumSize(1, this.op_);
            }

            for (int i = 0; i < this.cond_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(2, this.cond_.get(i));
            }

            size += this.unknownFields.getSerializedSize();
            this.memoizedSize = size;
            return size;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxExpect.Open)) {
            return super.equals(obj);
         } else {
            MysqlxExpect.Open other = (MysqlxExpect.Open)obj;
            if (this.hasOp() != other.hasOp()) {
               return false;
            } else if (this.hasOp() && this.op_ != other.op_) {
               return false;
            } else {
               return !this.getCondList().equals(other.getCondList()) ? false : this.unknownFields.equals(other.unknownFields);
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
            if (this.hasOp()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.op_;
            }

            if (this.getCondCount() > 0) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getCondList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxExpect.Open parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpect.Open parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpect.Open parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpect.Open parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpect.Open parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxExpect.Open parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxExpect.Open parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpect.Open parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpect.Open parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxExpect.Open parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxExpect.Open parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxExpect.Open parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxExpect.Open.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxExpect.Open.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxExpect.Open.Builder newBuilder(MysqlxExpect.Open prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxExpect.Open.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxExpect.Open.Builder() : new MysqlxExpect.Open.Builder().mergeFrom(this);
      }

      protected MysqlxExpect.Open.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxExpect.Open.Builder(parent);
      }

      public static MysqlxExpect.Open getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxExpect.Open> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxExpect.Open> getParserForType() {
         return PARSER;
      }

      public MysqlxExpect.Open getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxExpect.Open.Builder> implements MysqlxExpect.OpenOrBuilder {
         private int bitField0_;
         private int op_ = 0;
         private List<MysqlxExpect.Open.Condition> cond_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxExpect.Open.Condition, MysqlxExpect.Open.Condition.Builder, MysqlxExpect.Open.ConditionOrBuilder> condBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxExpect.internal_static_Mysqlx_Expect_Open_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpect.internal_static_Mysqlx_Expect_Open_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxExpect.Open.class, MysqlxExpect.Open.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxExpect.Open.alwaysUseFieldBuilders) {
               this.getCondFieldBuilder();
            }
         }

         public MysqlxExpect.Open.Builder clear() {
            super.clear();
            this.op_ = 0;
            this.bitField0_ &= -2;
            if (this.condBuilder_ == null) {
               this.cond_ = Collections.emptyList();
               this.bitField0_ &= -3;
            } else {
               this.condBuilder_.clear();
            }

            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxExpect.internal_static_Mysqlx_Expect_Open_descriptor;
         }

         public MysqlxExpect.Open getDefaultInstanceForType() {
            return MysqlxExpect.Open.getDefaultInstance();
         }

         public MysqlxExpect.Open build() {
            MysqlxExpect.Open result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxExpect.Open buildPartial() {
            MysqlxExpect.Open result = new MysqlxExpect.Open(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.op_ = this.op_;
            if (this.condBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0) {
                  this.cond_ = Collections.unmodifiableList(this.cond_);
                  this.bitField0_ &= -3;
               }

               result.cond_ = this.cond_;
            } else {
               result.cond_ = this.condBuilder_.build();
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxExpect.Open.Builder clone() {
            return (MysqlxExpect.Open.Builder)super.clone();
         }

         public MysqlxExpect.Open.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxExpect.Open.Builder)super.setField(field, value);
         }

         public MysqlxExpect.Open.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxExpect.Open.Builder)super.clearField(field);
         }

         public MysqlxExpect.Open.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxExpect.Open.Builder)super.clearOneof(oneof);
         }

         public MysqlxExpect.Open.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxExpect.Open.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxExpect.Open.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxExpect.Open.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxExpect.Open.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxExpect.Open) {
               return this.mergeFrom((MysqlxExpect.Open)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxExpect.Open.Builder mergeFrom(MysqlxExpect.Open other) {
            if (other == MysqlxExpect.Open.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasOp()) {
                  this.setOp(other.getOp());
               }

               if (this.condBuilder_ == null) {
                  if (!other.cond_.isEmpty()) {
                     if (this.cond_.isEmpty()) {
                        this.cond_ = other.cond_;
                        this.bitField0_ &= -3;
                     } else {
                        this.ensureCondIsMutable();
                        this.cond_.addAll(other.cond_);
                     }

                     this.onChanged();
                  }
               } else if (!other.cond_.isEmpty()) {
                  if (this.condBuilder_.isEmpty()) {
                     this.condBuilder_.dispose();
                     this.condBuilder_ = null;
                     this.cond_ = other.cond_;
                     this.bitField0_ &= -3;
                     this.condBuilder_ = MysqlxExpect.Open.alwaysUseFieldBuilders ? this.getCondFieldBuilder() : null;
                  } else {
                     this.condBuilder_.addAllMessages(other.cond_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            for (int i = 0; i < this.getCondCount(); i++) {
               if (!this.getCond(i).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public MysqlxExpect.Open.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxExpect.Open parsedMessage = null;

            try {
               parsedMessage = MysqlxExpect.Open.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxExpect.Open)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasOp() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxExpect.Open.CtxOperation getOp() {
            MysqlxExpect.Open.CtxOperation result = MysqlxExpect.Open.CtxOperation.valueOf(this.op_);
            return result == null ? MysqlxExpect.Open.CtxOperation.EXPECT_CTX_COPY_PREV : result;
         }

         public MysqlxExpect.Open.Builder setOp(MysqlxExpect.Open.CtxOperation value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.op_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxExpect.Open.Builder clearOp() {
            this.bitField0_ &= -2;
            this.op_ = 0;
            this.onChanged();
            return this;
         }

         private void ensureCondIsMutable() {
            if ((this.bitField0_ & 2) == 0) {
               this.cond_ = new ArrayList<>(this.cond_);
               this.bitField0_ |= 2;
            }
         }

         @Override
         public List<MysqlxExpect.Open.Condition> getCondList() {
            return this.condBuilder_ == null ? Collections.unmodifiableList(this.cond_) : this.condBuilder_.getMessageList();
         }

         @Override
         public int getCondCount() {
            return this.condBuilder_ == null ? this.cond_.size() : this.condBuilder_.getCount();
         }

         @Override
         public MysqlxExpect.Open.Condition getCond(int index) {
            return this.condBuilder_ == null ? this.cond_.get(index) : this.condBuilder_.getMessage(index);
         }

         public MysqlxExpect.Open.Builder setCond(int index, MysqlxExpect.Open.Condition value) {
            if (this.condBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureCondIsMutable();
               this.cond_.set(index, value);
               this.onChanged();
            } else {
               this.condBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxExpect.Open.Builder setCond(int index, MysqlxExpect.Open.Condition.Builder builderForValue) {
            if (this.condBuilder_ == null) {
               this.ensureCondIsMutable();
               this.cond_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.condBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxExpect.Open.Builder addCond(MysqlxExpect.Open.Condition value) {
            if (this.condBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureCondIsMutable();
               this.cond_.add(value);
               this.onChanged();
            } else {
               this.condBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxExpect.Open.Builder addCond(int index, MysqlxExpect.Open.Condition value) {
            if (this.condBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureCondIsMutable();
               this.cond_.add(index, value);
               this.onChanged();
            } else {
               this.condBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxExpect.Open.Builder addCond(MysqlxExpect.Open.Condition.Builder builderForValue) {
            if (this.condBuilder_ == null) {
               this.ensureCondIsMutable();
               this.cond_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.condBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxExpect.Open.Builder addCond(int index, MysqlxExpect.Open.Condition.Builder builderForValue) {
            if (this.condBuilder_ == null) {
               this.ensureCondIsMutable();
               this.cond_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.condBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxExpect.Open.Builder addAllCond(Iterable<? extends MysqlxExpect.Open.Condition> values) {
            if (this.condBuilder_ == null) {
               this.ensureCondIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.cond_);
               this.onChanged();
            } else {
               this.condBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxExpect.Open.Builder clearCond() {
            if (this.condBuilder_ == null) {
               this.cond_ = Collections.emptyList();
               this.bitField0_ &= -3;
               this.onChanged();
            } else {
               this.condBuilder_.clear();
            }

            return this;
         }

         public MysqlxExpect.Open.Builder removeCond(int index) {
            if (this.condBuilder_ == null) {
               this.ensureCondIsMutable();
               this.cond_.remove(index);
               this.onChanged();
            } else {
               this.condBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxExpect.Open.Condition.Builder getCondBuilder(int index) {
            return this.getCondFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxExpect.Open.ConditionOrBuilder getCondOrBuilder(int index) {
            return this.condBuilder_ == null ? this.cond_.get(index) : this.condBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxExpect.Open.ConditionOrBuilder> getCondOrBuilderList() {
            return this.condBuilder_ != null ? this.condBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.cond_);
         }

         public MysqlxExpect.Open.Condition.Builder addCondBuilder() {
            return this.getCondFieldBuilder().addBuilder(MysqlxExpect.Open.Condition.getDefaultInstance());
         }

         public MysqlxExpect.Open.Condition.Builder addCondBuilder(int index) {
            return this.getCondFieldBuilder().addBuilder(index, MysqlxExpect.Open.Condition.getDefaultInstance());
         }

         public List<MysqlxExpect.Open.Condition.Builder> getCondBuilderList() {
            return this.getCondFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxExpect.Open.Condition, MysqlxExpect.Open.Condition.Builder, MysqlxExpect.Open.ConditionOrBuilder> getCondFieldBuilder() {
            if (this.condBuilder_ == null) {
               this.condBuilder_ = new RepeatedFieldBuilderV3<>(this.cond_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
               this.cond_ = null;
            }

            return this.condBuilder_;
         }

         public final MysqlxExpect.Open.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpect.Open.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxExpect.Open.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxExpect.Open.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static final class Condition extends GeneratedMessageV3 implements MysqlxExpect.Open.ConditionOrBuilder {
         private static final long serialVersionUID = 0L;
         private int bitField0_;
         public static final int CONDITION_KEY_FIELD_NUMBER = 1;
         private int conditionKey_;
         public static final int CONDITION_VALUE_FIELD_NUMBER = 2;
         private ByteString conditionValue_;
         public static final int OP_FIELD_NUMBER = 3;
         private int op_;
         private byte memoizedIsInitialized = -1;
         private static final MysqlxExpect.Open.Condition DEFAULT_INSTANCE = new MysqlxExpect.Open.Condition();
         @Deprecated
         public static final Parser<MysqlxExpect.Open.Condition> PARSER = new AbstractParser<MysqlxExpect.Open.Condition>() {
            public MysqlxExpect.Open.Condition parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
               return new MysqlxExpect.Open.Condition(input, extensionRegistry);
            }
         };

         private Condition(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
         }

         private Condition() {
            this.conditionValue_ = ByteString.EMPTY;
            this.op_ = 0;
         }

         @Override
         protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new MysqlxExpect.Open.Condition();
         }

         @Override
         public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
         }

         private Condition(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                           this.bitField0_ |= 1;
                           this.conditionKey_ = input.readUInt32();
                           break;
                        case 18:
                           this.bitField0_ |= 2;
                           this.conditionValue_ = input.readBytes();
                           break;
                        case 24:
                           int rawValue = input.readEnum();
                           MysqlxExpect.Open.Condition.ConditionOperation value = MysqlxExpect.Open.Condition.ConditionOperation.valueOf(rawValue);
                           if (value == null) {
                              unknownFields.mergeVarintField(3, rawValue);
                           } else {
                              this.bitField0_ |= 4;
                              this.op_ = rawValue;
                           }
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
            return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxExpect.Open.Condition.class, MysqlxExpect.Open.Condition.Builder.class);
         }

         @Override
         public boolean hasConditionKey() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public int getConditionKey() {
            return this.conditionKey_;
         }

         @Override
         public boolean hasConditionValue() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public ByteString getConditionValue() {
            return this.conditionValue_;
         }

         @Override
         public boolean hasOp() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public MysqlxExpect.Open.Condition.ConditionOperation getOp() {
            MysqlxExpect.Open.Condition.ConditionOperation result = MysqlxExpect.Open.Condition.ConditionOperation.valueOf(this.op_);
            return result == null ? MysqlxExpect.Open.Condition.ConditionOperation.EXPECT_OP_SET : result;
         }

         @Override
         public final boolean isInitialized() {
            byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized == 1) {
               return true;
            } else if (isInitialized == 0) {
               return false;
            } else if (!this.hasConditionKey()) {
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
               output.writeUInt32(1, this.conditionKey_);
            }

            if ((this.bitField0_ & 2) != 0) {
               output.writeBytes(2, this.conditionValue_);
            }

            if ((this.bitField0_ & 4) != 0) {
               output.writeEnum(3, this.op_);
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
                  size += CodedOutputStream.computeUInt32Size(1, this.conditionKey_);
               }

               if ((this.bitField0_ & 2) != 0) {
                  size += CodedOutputStream.computeBytesSize(2, this.conditionValue_);
               }

               if ((this.bitField0_ & 4) != 0) {
                  size += CodedOutputStream.computeEnumSize(3, this.op_);
               }

               size += this.unknownFields.getSerializedSize();
               this.memoizedSize = size;
               return size;
            }
         }

         @Override
         public boolean equals(Object obj) {
            if (obj == this) {
               return true;
            } else if (!(obj instanceof MysqlxExpect.Open.Condition)) {
               return super.equals(obj);
            } else {
               MysqlxExpect.Open.Condition other = (MysqlxExpect.Open.Condition)obj;
               if (this.hasConditionKey() != other.hasConditionKey()) {
                  return false;
               } else if (this.hasConditionKey() && this.getConditionKey() != other.getConditionKey()) {
                  return false;
               } else if (this.hasConditionValue() != other.hasConditionValue()) {
                  return false;
               } else if (this.hasConditionValue() && !this.getConditionValue().equals(other.getConditionValue())) {
                  return false;
               } else if (this.hasOp() != other.hasOp()) {
                  return false;
               } else {
                  return this.hasOp() && this.op_ != other.op_ ? false : this.unknownFields.equals(other.unknownFields);
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
               if (this.hasConditionKey()) {
                  hash = 37 * hash + 1;
                  hash = 53 * hash + this.getConditionKey();
               }

               if (this.hasConditionValue()) {
                  hash = 37 * hash + 2;
                  hash = 53 * hash + this.getConditionValue().hashCode();
               }

               if (this.hasOp()) {
                  hash = 37 * hash + 3;
                  hash = 53 * hash + this.op_;
               }

               hash = 29 * hash + this.unknownFields.hashCode();
               this.memoizedHashCode = hash;
               return hash;
            }
         }

         public static MysqlxExpect.Open.Condition parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxExpect.Open.Condition parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxExpect.Open.Condition parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxExpect.Open.Condition parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxExpect.Open.Condition parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxExpect.Open.Condition parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxExpect.Open.Condition parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static MysqlxExpect.Open.Condition parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public static MysqlxExpect.Open.Condition parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
         }

         public static MysqlxExpect.Open.Condition parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
         }

         public static MysqlxExpect.Open.Condition parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static MysqlxExpect.Open.Condition parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public MysqlxExpect.Open.Condition.Builder newBuilderForType() {
            return newBuilder();
         }

         public static MysqlxExpect.Open.Condition.Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
         }

         public static MysqlxExpect.Open.Condition.Builder newBuilder(MysqlxExpect.Open.Condition prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
         }

         public MysqlxExpect.Open.Condition.Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new MysqlxExpect.Open.Condition.Builder() : new MysqlxExpect.Open.Condition.Builder().mergeFrom(this);
         }

         protected MysqlxExpect.Open.Condition.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            return new MysqlxExpect.Open.Condition.Builder(parent);
         }

         public static MysqlxExpect.Open.Condition getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<MysqlxExpect.Open.Condition> parser() {
            return PARSER;
         }

         @Override
         public Parser<MysqlxExpect.Open.Condition> getParserForType() {
            return PARSER;
         }

         public MysqlxExpect.Open.Condition getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
         }

         public static final class Builder
            extends GeneratedMessageV3.Builder<MysqlxExpect.Open.Condition.Builder>
            implements MysqlxExpect.Open.ConditionOrBuilder {
            private int bitField0_;
            private int conditionKey_;
            private ByteString conditionValue_ = ByteString.EMPTY;
            private int op_ = 0;

            public static final Descriptors.Descriptor getDescriptor() {
               return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
               return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_fieldAccessorTable
                  .ensureFieldAccessorsInitialized(MysqlxExpect.Open.Condition.class, MysqlxExpect.Open.Condition.Builder.class);
            }

            private Builder() {
               this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
               super(parent);
               this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
               if (MysqlxExpect.Open.Condition.alwaysUseFieldBuilders) {
               }
            }

            public MysqlxExpect.Open.Condition.Builder clear() {
               super.clear();
               this.conditionKey_ = 0;
               this.bitField0_ &= -2;
               this.conditionValue_ = ByteString.EMPTY;
               this.bitField0_ &= -3;
               this.op_ = 0;
               this.bitField0_ &= -5;
               return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
               return MysqlxExpect.internal_static_Mysqlx_Expect_Open_Condition_descriptor;
            }

            public MysqlxExpect.Open.Condition getDefaultInstanceForType() {
               return MysqlxExpect.Open.Condition.getDefaultInstance();
            }

            public MysqlxExpect.Open.Condition build() {
               MysqlxExpect.Open.Condition result = this.buildPartial();
               if (!result.isInitialized()) {
                  throw newUninitializedMessageException(result);
               } else {
                  return result;
               }
            }

            public MysqlxExpect.Open.Condition buildPartial() {
               MysqlxExpect.Open.Condition result = new MysqlxExpect.Open.Condition(this);
               int from_bitField0_ = this.bitField0_;
               int to_bitField0_ = 0;
               if ((from_bitField0_ & 1) != 0) {
                  result.conditionKey_ = this.conditionKey_;
                  to_bitField0_ |= 1;
               }

               if ((from_bitField0_ & 2) != 0) {
                  to_bitField0_ |= 2;
               }

               result.conditionValue_ = this.conditionValue_;
               if ((from_bitField0_ & 4) != 0) {
                  to_bitField0_ |= 4;
               }

               result.op_ = this.op_;
               result.bitField0_ = to_bitField0_;
               this.onBuilt();
               return result;
            }

            public MysqlxExpect.Open.Condition.Builder clone() {
               return (MysqlxExpect.Open.Condition.Builder)super.clone();
            }

            public MysqlxExpect.Open.Condition.Builder setField(Descriptors.FieldDescriptor field, Object value) {
               return (MysqlxExpect.Open.Condition.Builder)super.setField(field, value);
            }

            public MysqlxExpect.Open.Condition.Builder clearField(Descriptors.FieldDescriptor field) {
               return (MysqlxExpect.Open.Condition.Builder)super.clearField(field);
            }

            public MysqlxExpect.Open.Condition.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
               return (MysqlxExpect.Open.Condition.Builder)super.clearOneof(oneof);
            }

            public MysqlxExpect.Open.Condition.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
               return (MysqlxExpect.Open.Condition.Builder)super.setRepeatedField(field, index, value);
            }

            public MysqlxExpect.Open.Condition.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
               return (MysqlxExpect.Open.Condition.Builder)super.addRepeatedField(field, value);
            }

            public MysqlxExpect.Open.Condition.Builder mergeFrom(Message other) {
               if (other instanceof MysqlxExpect.Open.Condition) {
                  return this.mergeFrom((MysqlxExpect.Open.Condition)other);
               } else {
                  super.mergeFrom(other);
                  return this;
               }
            }

            public MysqlxExpect.Open.Condition.Builder mergeFrom(MysqlxExpect.Open.Condition other) {
               if (other == MysqlxExpect.Open.Condition.getDefaultInstance()) {
                  return this;
               } else {
                  if (other.hasConditionKey()) {
                     this.setConditionKey(other.getConditionKey());
                  }

                  if (other.hasConditionValue()) {
                     this.setConditionValue(other.getConditionValue());
                  }

                  if (other.hasOp()) {
                     this.setOp(other.getOp());
                  }

                  this.mergeUnknownFields(other.unknownFields);
                  this.onChanged();
                  return this;
               }
            }

            @Override
            public final boolean isInitialized() {
               return this.hasConditionKey();
            }

            public MysqlxExpect.Open.Condition.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
               MysqlxExpect.Open.Condition parsedMessage = null;

               try {
                  parsedMessage = MysqlxExpect.Open.Condition.PARSER.parsePartialFrom(input, extensionRegistry);
               } catch (InvalidProtocolBufferException var8) {
                  parsedMessage = (MysqlxExpect.Open.Condition)var8.getUnfinishedMessage();
                  throw var8.unwrapIOException();
               } finally {
                  if (parsedMessage != null) {
                     this.mergeFrom(parsedMessage);
                  }
               }

               return this;
            }

            @Override
            public boolean hasConditionKey() {
               return (this.bitField0_ & 1) != 0;
            }

            @Override
            public int getConditionKey() {
               return this.conditionKey_;
            }

            public MysqlxExpect.Open.Condition.Builder setConditionKey(int value) {
               this.bitField0_ |= 1;
               this.conditionKey_ = value;
               this.onChanged();
               return this;
            }

            public MysqlxExpect.Open.Condition.Builder clearConditionKey() {
               this.bitField0_ &= -2;
               this.conditionKey_ = 0;
               this.onChanged();
               return this;
            }

            @Override
            public boolean hasConditionValue() {
               return (this.bitField0_ & 2) != 0;
            }

            @Override
            public ByteString getConditionValue() {
               return this.conditionValue_;
            }

            public MysqlxExpect.Open.Condition.Builder setConditionValue(ByteString value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 2;
                  this.conditionValue_ = value;
                  this.onChanged();
                  return this;
               }
            }

            public MysqlxExpect.Open.Condition.Builder clearConditionValue() {
               this.bitField0_ &= -3;
               this.conditionValue_ = MysqlxExpect.Open.Condition.getDefaultInstance().getConditionValue();
               this.onChanged();
               return this;
            }

            @Override
            public boolean hasOp() {
               return (this.bitField0_ & 4) != 0;
            }

            @Override
            public MysqlxExpect.Open.Condition.ConditionOperation getOp() {
               MysqlxExpect.Open.Condition.ConditionOperation result = MysqlxExpect.Open.Condition.ConditionOperation.valueOf(this.op_);
               return result == null ? MysqlxExpect.Open.Condition.ConditionOperation.EXPECT_OP_SET : result;
            }

            public MysqlxExpect.Open.Condition.Builder setOp(MysqlxExpect.Open.Condition.ConditionOperation value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 4;
                  this.op_ = value.getNumber();
                  this.onChanged();
                  return this;
               }
            }

            public MysqlxExpect.Open.Condition.Builder clearOp() {
               this.bitField0_ &= -5;
               this.op_ = 0;
               this.onChanged();
               return this;
            }

            public final MysqlxExpect.Open.Condition.Builder setUnknownFields(UnknownFieldSet unknownFields) {
               return (MysqlxExpect.Open.Condition.Builder)super.setUnknownFields(unknownFields);
            }

            public final MysqlxExpect.Open.Condition.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
               return (MysqlxExpect.Open.Condition.Builder)super.mergeUnknownFields(unknownFields);
            }
         }

         public static enum ConditionOperation implements ProtocolMessageEnum {
            EXPECT_OP_SET(0),
            EXPECT_OP_UNSET(1);

            public static final int EXPECT_OP_SET_VALUE = 0;
            public static final int EXPECT_OP_UNSET_VALUE = 1;
            private static final Internal.EnumLiteMap<MysqlxExpect.Open.Condition.ConditionOperation> internalValueMap = new Internal.EnumLiteMap<MysqlxExpect.Open.Condition.ConditionOperation>() {
               public MysqlxExpect.Open.Condition.ConditionOperation findValueByNumber(int number) {
                  return MysqlxExpect.Open.Condition.ConditionOperation.forNumber(number);
               }
            };
            private static final MysqlxExpect.Open.Condition.ConditionOperation[] VALUES = values();
            private final int value;

            @Override
            public final int getNumber() {
               return this.value;
            }

            @Deprecated
            public static MysqlxExpect.Open.Condition.ConditionOperation valueOf(int value) {
               return forNumber(value);
            }

            public static MysqlxExpect.Open.Condition.ConditionOperation forNumber(int value) {
               switch (value) {
                  case 0:
                     return EXPECT_OP_SET;
                  case 1:
                     return EXPECT_OP_UNSET;
                  default:
                     return null;
               }
            }

            public static Internal.EnumLiteMap<MysqlxExpect.Open.Condition.ConditionOperation> internalGetValueMap() {
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
               return MysqlxExpect.Open.Condition.getDescriptor().getEnumTypes().get(1);
            }

            public static MysqlxExpect.Open.Condition.ConditionOperation valueOf(Descriptors.EnumValueDescriptor desc) {
               if (desc.getType() != getDescriptor()) {
                  throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
               } else {
                  return VALUES[desc.getIndex()];
               }
            }

            private ConditionOperation(int value) {
               this.value = value;
            }
         }

         public static enum Key implements ProtocolMessageEnum {
            EXPECT_NO_ERROR(1),
            EXPECT_FIELD_EXIST(2),
            EXPECT_DOCID_GENERATED(3);

            public static final int EXPECT_NO_ERROR_VALUE = 1;
            public static final int EXPECT_FIELD_EXIST_VALUE = 2;
            public static final int EXPECT_DOCID_GENERATED_VALUE = 3;
            private static final Internal.EnumLiteMap<MysqlxExpect.Open.Condition.Key> internalValueMap = new Internal.EnumLiteMap<MysqlxExpect.Open.Condition.Key>() {
               public MysqlxExpect.Open.Condition.Key findValueByNumber(int number) {
                  return MysqlxExpect.Open.Condition.Key.forNumber(number);
               }
            };
            private static final MysqlxExpect.Open.Condition.Key[] VALUES = values();
            private final int value;

            @Override
            public final int getNumber() {
               return this.value;
            }

            @Deprecated
            public static MysqlxExpect.Open.Condition.Key valueOf(int value) {
               return forNumber(value);
            }

            public static MysqlxExpect.Open.Condition.Key forNumber(int value) {
               switch (value) {
                  case 1:
                     return EXPECT_NO_ERROR;
                  case 2:
                     return EXPECT_FIELD_EXIST;
                  case 3:
                     return EXPECT_DOCID_GENERATED;
                  default:
                     return null;
               }
            }

            public static Internal.EnumLiteMap<MysqlxExpect.Open.Condition.Key> internalGetValueMap() {
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
               return MysqlxExpect.Open.Condition.getDescriptor().getEnumTypes().get(0);
            }

            public static MysqlxExpect.Open.Condition.Key valueOf(Descriptors.EnumValueDescriptor desc) {
               if (desc.getType() != getDescriptor()) {
                  throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
               } else {
                  return VALUES[desc.getIndex()];
               }
            }

            private Key(int value) {
               this.value = value;
            }
         }
      }

      public interface ConditionOrBuilder extends MessageOrBuilder {
         boolean hasConditionKey();

         int getConditionKey();

         boolean hasConditionValue();

         ByteString getConditionValue();

         boolean hasOp();

         MysqlxExpect.Open.Condition.ConditionOperation getOp();
      }

      public static enum CtxOperation implements ProtocolMessageEnum {
         EXPECT_CTX_COPY_PREV(0),
         EXPECT_CTX_EMPTY(1);

         public static final int EXPECT_CTX_COPY_PREV_VALUE = 0;
         public static final int EXPECT_CTX_EMPTY_VALUE = 1;
         private static final Internal.EnumLiteMap<MysqlxExpect.Open.CtxOperation> internalValueMap = new Internal.EnumLiteMap<MysqlxExpect.Open.CtxOperation>() {
            public MysqlxExpect.Open.CtxOperation findValueByNumber(int number) {
               return MysqlxExpect.Open.CtxOperation.forNumber(number);
            }
         };
         private static final MysqlxExpect.Open.CtxOperation[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static MysqlxExpect.Open.CtxOperation valueOf(int value) {
            return forNumber(value);
         }

         public static MysqlxExpect.Open.CtxOperation forNumber(int value) {
            switch (value) {
               case 0:
                  return EXPECT_CTX_COPY_PREV;
               case 1:
                  return EXPECT_CTX_EMPTY;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<MysqlxExpect.Open.CtxOperation> internalGetValueMap() {
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
            return MysqlxExpect.Open.getDescriptor().getEnumTypes().get(0);
         }

         public static MysqlxExpect.Open.CtxOperation valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private CtxOperation(int value) {
            this.value = value;
         }
      }
   }

   public interface OpenOrBuilder extends MessageOrBuilder {
      boolean hasOp();

      MysqlxExpect.Open.CtxOperation getOp();

      List<MysqlxExpect.Open.Condition> getCondList();

      MysqlxExpect.Open.Condition getCond(int var1);

      int getCondCount();

      List<? extends MysqlxExpect.Open.ConditionOrBuilder> getCondOrBuilderList();

      MysqlxExpect.Open.ConditionOrBuilder getCondOrBuilder(int var1);
   }
}
