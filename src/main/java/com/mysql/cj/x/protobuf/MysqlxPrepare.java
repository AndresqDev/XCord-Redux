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

public final class MysqlxPrepare {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Prepare_Prepare_descriptor = getDescriptor().getMessageTypes().get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Prepare_Prepare_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Prepare_Prepare_descriptor, new String[]{"StmtId", "Stmt"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor = internal_static_Mysqlx_Prepare_Prepare_descriptor.getNestedTypes()
      .get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor, new String[]{"Type", "Find", "Insert", "Update", "Delete", "StmtExecute"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Prepare_Execute_descriptor = getDescriptor().getMessageTypes().get(1);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Prepare_Execute_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Prepare_Execute_descriptor, new String[]{"StmtId", "Args", "CompactMetadata"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Prepare_Deallocate_descriptor = getDescriptor().getMessageTypes().get(2);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Prepare_Deallocate_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Prepare_Deallocate_descriptor, new String[]{"StmtId"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxPrepare() {
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
         "\n\u0014mysqlx_prepare.proto\u0012\u000eMysqlx.Prepare\u001a\fmysqlx.proto\u001a\u0010mysqlx_sql.proto\u001a\u0011mysqlx_crud.proto\u001a\u0016mysqlx_datatypes.proto\"\u009d\u0003\n\u0007Prepare\u0012\u000f\n\u0007stmt_id\u0018\u0001 \u0002(\r\u00122\n\u0004stmt\u0018\u0002 \u0002(\u000b2$.Mysqlx.Prepare.Prepare.OneOfMessage\u001aÆ\u0002\n\fOneOfMessage\u00127\n\u0004type\u0018\u0001 \u0002(\u000e2).Mysqlx.Prepare.Prepare.OneOfMessage.Type\u0012\u001f\n\u0004find\u0018\u0002 \u0001(\u000b2\u0011.Mysqlx.Crud.Find\u0012#\n\u0006insert\u0018\u0003 \u0001(\u000b2\u0013.Mysqlx.Crud.Insert\u0012#\n\u0006update\u0018\u0004 \u0001(\u000b2\u0013.Mysqlx.Crud.Update\u0012#\n\u0006delete\u0018\u0005 \u0001(\u000b2\u0013.Mysqlx.Crud.Delete\u0012-\n\fstmt_execute\u0018\u0006 \u0001(\u000b2\u0017.Mysqlx.Sql.StmtExecute\">\n\u0004Type\u0012\b\n\u0004FIND\u0010\u0000\u0012\n\n\u0006INSERT\u0010\u0001\u0012\n\n\u0006UPDATE\u0010\u0002\u0012\n\n\u0006DELETE\u0010\u0004\u0012\b\n\u0004STMT\u0010\u0005:\u0004\u0088ê0(\"f\n\u0007Execute\u0012\u000f\n\u0007stmt_id\u0018\u0001 \u0002(\r\u0012#\n\u0004args\u0018\u0002 \u0003(\u000b2\u0015.Mysqlx.Datatypes.Any\u0012\u001f\n\u0010compact_metadata\u0018\u0003 \u0001(\b:\u0005false:\u0004\u0088ê0)\"#\n\nDeallocate\u0012\u000f\n\u0007stmt_id\u0018\u0001 \u0002(\r:\u0004\u0088ê0*B\u0019\n\u0017com.mysql.cj.x.protobuf"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
         descriptorData,
         new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor(), MysqlxSql.getDescriptor(), MysqlxCrud.getDescriptor(), MysqlxDatatypes.getDescriptor()}
      );
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.clientMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      Mysqlx.getDescriptor();
      MysqlxSql.getDescriptor();
      MysqlxCrud.getDescriptor();
      MysqlxDatatypes.getDescriptor();
   }

   public static final class Deallocate extends GeneratedMessageV3 implements MysqlxPrepare.DeallocateOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int STMT_ID_FIELD_NUMBER = 1;
      private int stmtId_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxPrepare.Deallocate DEFAULT_INSTANCE = new MysqlxPrepare.Deallocate();
      @Deprecated
      public static final Parser<MysqlxPrepare.Deallocate> PARSER = new AbstractParser<MysqlxPrepare.Deallocate>() {
         public MysqlxPrepare.Deallocate parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxPrepare.Deallocate(input, extensionRegistry);
         }
      };

      private Deallocate(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Deallocate() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxPrepare.Deallocate();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Deallocate(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.stmtId_ = input.readUInt32();
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
               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxPrepare.Deallocate.class, MysqlxPrepare.Deallocate.Builder.class);
      }

      @Override
      public boolean hasStmtId() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public int getStmtId() {
         return this.stmtId_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasStmtId()) {
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
            output.writeUInt32(1, this.stmtId_);
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
               size += CodedOutputStream.computeUInt32Size(1, this.stmtId_);
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
         } else if (!(obj instanceof MysqlxPrepare.Deallocate)) {
            return super.equals(obj);
         } else {
            MysqlxPrepare.Deallocate other = (MysqlxPrepare.Deallocate)obj;
            if (this.hasStmtId() != other.hasStmtId()) {
               return false;
            } else {
               return this.hasStmtId() && this.getStmtId() != other.getStmtId() ? false : this.unknownFields.equals(other.unknownFields);
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
            if (this.hasStmtId()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getStmtId();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxPrepare.Deallocate parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxPrepare.Deallocate parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxPrepare.Deallocate parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxPrepare.Deallocate parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxPrepare.Deallocate parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxPrepare.Deallocate parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxPrepare.Deallocate parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxPrepare.Deallocate parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxPrepare.Deallocate parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxPrepare.Deallocate parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxPrepare.Deallocate parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxPrepare.Deallocate parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxPrepare.Deallocate.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxPrepare.Deallocate.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxPrepare.Deallocate.Builder newBuilder(MysqlxPrepare.Deallocate prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxPrepare.Deallocate.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxPrepare.Deallocate.Builder() : new MysqlxPrepare.Deallocate.Builder().mergeFrom(this);
      }

      protected MysqlxPrepare.Deallocate.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxPrepare.Deallocate.Builder(parent);
      }

      public static MysqlxPrepare.Deallocate getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxPrepare.Deallocate> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxPrepare.Deallocate> getParserForType() {
         return PARSER;
      }

      public MysqlxPrepare.Deallocate getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxPrepare.Deallocate.Builder> implements MysqlxPrepare.DeallocateOrBuilder {
         private int bitField0_;
         private int stmtId_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxPrepare.Deallocate.class, MysqlxPrepare.Deallocate.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxPrepare.Deallocate.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxPrepare.Deallocate.Builder clear() {
            super.clear();
            this.stmtId_ = 0;
            this.bitField0_ &= -2;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxPrepare.internal_static_Mysqlx_Prepare_Deallocate_descriptor;
         }

         public MysqlxPrepare.Deallocate getDefaultInstanceForType() {
            return MysqlxPrepare.Deallocate.getDefaultInstance();
         }

         public MysqlxPrepare.Deallocate build() {
            MysqlxPrepare.Deallocate result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxPrepare.Deallocate buildPartial() {
            MysqlxPrepare.Deallocate result = new MysqlxPrepare.Deallocate(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.stmtId_ = this.stmtId_;
               to_bitField0_ |= 1;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxPrepare.Deallocate.Builder clone() {
            return (MysqlxPrepare.Deallocate.Builder)super.clone();
         }

         public MysqlxPrepare.Deallocate.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxPrepare.Deallocate.Builder)super.setField(field, value);
         }

         public MysqlxPrepare.Deallocate.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxPrepare.Deallocate.Builder)super.clearField(field);
         }

         public MysqlxPrepare.Deallocate.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxPrepare.Deallocate.Builder)super.clearOneof(oneof);
         }

         public MysqlxPrepare.Deallocate.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxPrepare.Deallocate.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxPrepare.Deallocate.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxPrepare.Deallocate.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxPrepare.Deallocate.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxPrepare.Deallocate) {
               return this.mergeFrom((MysqlxPrepare.Deallocate)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxPrepare.Deallocate.Builder mergeFrom(MysqlxPrepare.Deallocate other) {
            if (other == MysqlxPrepare.Deallocate.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasStmtId()) {
                  this.setStmtId(other.getStmtId());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return this.hasStmtId();
         }

         public MysqlxPrepare.Deallocate.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxPrepare.Deallocate parsedMessage = null;

            try {
               parsedMessage = MysqlxPrepare.Deallocate.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxPrepare.Deallocate)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasStmtId() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public int getStmtId() {
            return this.stmtId_;
         }

         public MysqlxPrepare.Deallocate.Builder setStmtId(int value) {
            this.bitField0_ |= 1;
            this.stmtId_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxPrepare.Deallocate.Builder clearStmtId() {
            this.bitField0_ &= -2;
            this.stmtId_ = 0;
            this.onChanged();
            return this;
         }

         public final MysqlxPrepare.Deallocate.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxPrepare.Deallocate.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxPrepare.Deallocate.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxPrepare.Deallocate.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface DeallocateOrBuilder extends MessageOrBuilder {
      boolean hasStmtId();

      int getStmtId();
   }

   public static final class Execute extends GeneratedMessageV3 implements MysqlxPrepare.ExecuteOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int STMT_ID_FIELD_NUMBER = 1;
      private int stmtId_;
      public static final int ARGS_FIELD_NUMBER = 2;
      private List<MysqlxDatatypes.Any> args_;
      public static final int COMPACT_METADATA_FIELD_NUMBER = 3;
      private boolean compactMetadata_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxPrepare.Execute DEFAULT_INSTANCE = new MysqlxPrepare.Execute();
      @Deprecated
      public static final Parser<MysqlxPrepare.Execute> PARSER = new AbstractParser<MysqlxPrepare.Execute>() {
         public MysqlxPrepare.Execute parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxPrepare.Execute(input, extensionRegistry);
         }
      };

      private Execute(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Execute() {
         this.args_ = Collections.emptyList();
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxPrepare.Execute();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Execute(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.stmtId_ = input.readUInt32();
                        break;
                     case 18:
                        if ((mutable_bitField0_ & 2) == 0) {
                           this.args_ = new ArrayList<>();
                           mutable_bitField0_ |= 2;
                        }

                        this.args_.add(input.readMessage(MysqlxDatatypes.Any.PARSER, extensionRegistry));
                        break;
                     case 24:
                        this.bitField0_ |= 2;
                        this.compactMetadata_ = input.readBool();
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
               if ((mutable_bitField0_ & 2) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxPrepare.Execute.class, MysqlxPrepare.Execute.Builder.class);
      }

      @Override
      public boolean hasStmtId() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public int getStmtId() {
         return this.stmtId_;
      }

      @Override
      public List<MysqlxDatatypes.Any> getArgsList() {
         return this.args_;
      }

      @Override
      public List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList() {
         return this.args_;
      }

      @Override
      public int getArgsCount() {
         return this.args_.size();
      }

      @Override
      public MysqlxDatatypes.Any getArgs(int index) {
         return this.args_.get(index);
      }

      @Override
      public MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int index) {
         return this.args_.get(index);
      }

      @Override
      public boolean hasCompactMetadata() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public boolean getCompactMetadata() {
         return this.compactMetadata_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasStmtId()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else {
            for (int i = 0; i < this.getArgsCount(); i++) {
               if (!this.getArgs(i).isInitialized()) {
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
            output.writeUInt32(1, this.stmtId_);
         }

         for (int i = 0; i < this.args_.size(); i++) {
            output.writeMessage(2, this.args_.get(i));
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeBool(3, this.compactMetadata_);
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
               size += CodedOutputStream.computeUInt32Size(1, this.stmtId_);
            }

            for (int i = 0; i < this.args_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(2, this.args_.get(i));
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeBoolSize(3, this.compactMetadata_);
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
         } else if (!(obj instanceof MysqlxPrepare.Execute)) {
            return super.equals(obj);
         } else {
            MysqlxPrepare.Execute other = (MysqlxPrepare.Execute)obj;
            if (this.hasStmtId() != other.hasStmtId()) {
               return false;
            } else if (this.hasStmtId() && this.getStmtId() != other.getStmtId()) {
               return false;
            } else if (!this.getArgsList().equals(other.getArgsList())) {
               return false;
            } else if (this.hasCompactMetadata() != other.hasCompactMetadata()) {
               return false;
            } else {
               return this.hasCompactMetadata() && this.getCompactMetadata() != other.getCompactMetadata()
                  ? false
                  : this.unknownFields.equals(other.unknownFields);
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
            if (this.hasStmtId()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getStmtId();
            }

            if (this.getArgsCount() > 0) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getArgsList().hashCode();
            }

            if (this.hasCompactMetadata()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + Internal.hashBoolean(this.getCompactMetadata());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxPrepare.Execute parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxPrepare.Execute parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxPrepare.Execute parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxPrepare.Execute parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxPrepare.Execute parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxPrepare.Execute parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxPrepare.Execute parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxPrepare.Execute parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxPrepare.Execute parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxPrepare.Execute parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxPrepare.Execute parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxPrepare.Execute parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxPrepare.Execute.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxPrepare.Execute.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxPrepare.Execute.Builder newBuilder(MysqlxPrepare.Execute prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxPrepare.Execute.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxPrepare.Execute.Builder() : new MysqlxPrepare.Execute.Builder().mergeFrom(this);
      }

      protected MysqlxPrepare.Execute.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxPrepare.Execute.Builder(parent);
      }

      public static MysqlxPrepare.Execute getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxPrepare.Execute> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxPrepare.Execute> getParserForType() {
         return PARSER;
      }

      public MysqlxPrepare.Execute getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxPrepare.Execute.Builder> implements MysqlxPrepare.ExecuteOrBuilder {
         private int bitField0_;
         private int stmtId_;
         private List<MysqlxDatatypes.Any> args_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> argsBuilder_;
         private boolean compactMetadata_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxPrepare.Execute.class, MysqlxPrepare.Execute.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxPrepare.Execute.alwaysUseFieldBuilders) {
               this.getArgsFieldBuilder();
            }
         }

         public MysqlxPrepare.Execute.Builder clear() {
            super.clear();
            this.stmtId_ = 0;
            this.bitField0_ &= -2;
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -3;
            } else {
               this.argsBuilder_.clear();
            }

            this.compactMetadata_ = false;
            this.bitField0_ &= -5;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxPrepare.internal_static_Mysqlx_Prepare_Execute_descriptor;
         }

         public MysqlxPrepare.Execute getDefaultInstanceForType() {
            return MysqlxPrepare.Execute.getDefaultInstance();
         }

         public MysqlxPrepare.Execute build() {
            MysqlxPrepare.Execute result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxPrepare.Execute buildPartial() {
            MysqlxPrepare.Execute result = new MysqlxPrepare.Execute(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.stmtId_ = this.stmtId_;
               to_bitField0_ |= 1;
            }

            if (this.argsBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
                  this.bitField0_ &= -3;
               }

               result.args_ = this.args_;
            } else {
               result.args_ = this.argsBuilder_.build();
            }

            if ((from_bitField0_ & 4) != 0) {
               result.compactMetadata_ = this.compactMetadata_;
               to_bitField0_ |= 2;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxPrepare.Execute.Builder clone() {
            return (MysqlxPrepare.Execute.Builder)super.clone();
         }

         public MysqlxPrepare.Execute.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxPrepare.Execute.Builder)super.setField(field, value);
         }

         public MysqlxPrepare.Execute.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxPrepare.Execute.Builder)super.clearField(field);
         }

         public MysqlxPrepare.Execute.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxPrepare.Execute.Builder)super.clearOneof(oneof);
         }

         public MysqlxPrepare.Execute.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxPrepare.Execute.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxPrepare.Execute.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxPrepare.Execute.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxPrepare.Execute.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxPrepare.Execute) {
               return this.mergeFrom((MysqlxPrepare.Execute)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxPrepare.Execute.Builder mergeFrom(MysqlxPrepare.Execute other) {
            if (other == MysqlxPrepare.Execute.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasStmtId()) {
                  this.setStmtId(other.getStmtId());
               }

               if (this.argsBuilder_ == null) {
                  if (!other.args_.isEmpty()) {
                     if (this.args_.isEmpty()) {
                        this.args_ = other.args_;
                        this.bitField0_ &= -3;
                     } else {
                        this.ensureArgsIsMutable();
                        this.args_.addAll(other.args_);
                     }

                     this.onChanged();
                  }
               } else if (!other.args_.isEmpty()) {
                  if (this.argsBuilder_.isEmpty()) {
                     this.argsBuilder_.dispose();
                     this.argsBuilder_ = null;
                     this.args_ = other.args_;
                     this.bitField0_ &= -3;
                     this.argsBuilder_ = MysqlxPrepare.Execute.alwaysUseFieldBuilders ? this.getArgsFieldBuilder() : null;
                  } else {
                     this.argsBuilder_.addAllMessages(other.args_);
                  }
               }

               if (other.hasCompactMetadata()) {
                  this.setCompactMetadata(other.getCompactMetadata());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasStmtId()) {
               return false;
            } else {
               for (int i = 0; i < this.getArgsCount(); i++) {
                  if (!this.getArgs(i).isInitialized()) {
                     return false;
                  }
               }

               return true;
            }
         }

         public MysqlxPrepare.Execute.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxPrepare.Execute parsedMessage = null;

            try {
               parsedMessage = MysqlxPrepare.Execute.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxPrepare.Execute)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasStmtId() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public int getStmtId() {
            return this.stmtId_;
         }

         public MysqlxPrepare.Execute.Builder setStmtId(int value) {
            this.bitField0_ |= 1;
            this.stmtId_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxPrepare.Execute.Builder clearStmtId() {
            this.bitField0_ &= -2;
            this.stmtId_ = 0;
            this.onChanged();
            return this;
         }

         private void ensureArgsIsMutable() {
            if ((this.bitField0_ & 2) == 0) {
               this.args_ = new ArrayList<>(this.args_);
               this.bitField0_ |= 2;
            }
         }

         @Override
         public List<MysqlxDatatypes.Any> getArgsList() {
            return this.argsBuilder_ == null ? Collections.unmodifiableList(this.args_) : this.argsBuilder_.getMessageList();
         }

         @Override
         public int getArgsCount() {
            return this.argsBuilder_ == null ? this.args_.size() : this.argsBuilder_.getCount();
         }

         @Override
         public MysqlxDatatypes.Any getArgs(int index) {
            return this.argsBuilder_ == null ? this.args_.get(index) : this.argsBuilder_.getMessage(index);
         }

         public MysqlxPrepare.Execute.Builder setArgs(int index, MysqlxDatatypes.Any value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.set(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxPrepare.Execute.Builder setArgs(int index, MysqlxDatatypes.Any.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxPrepare.Execute.Builder addArgs(MysqlxDatatypes.Any value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxPrepare.Execute.Builder addArgs(int index, MysqlxDatatypes.Any value) {
            if (this.argsBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureArgsIsMutable();
               this.args_.add(index, value);
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxPrepare.Execute.Builder addArgs(MysqlxDatatypes.Any.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxPrepare.Execute.Builder addArgs(int index, MysqlxDatatypes.Any.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxPrepare.Execute.Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Any> values) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.args_);
               this.onChanged();
            } else {
               this.argsBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxPrepare.Execute.Builder clearArgs() {
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -3;
               this.onChanged();
            } else {
               this.argsBuilder_.clear();
            }

            return this;
         }

         public MysqlxPrepare.Execute.Builder removeArgs(int index) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.remove(index);
               this.onChanged();
            } else {
               this.argsBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxDatatypes.Any.Builder getArgsBuilder(int index) {
            return this.getArgsFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int index) {
            return this.argsBuilder_ == null ? this.args_.get(index) : this.argsBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList() {
            return this.argsBuilder_ != null ? this.argsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.args_);
         }

         public MysqlxDatatypes.Any.Builder addArgsBuilder() {
            return this.getArgsFieldBuilder().addBuilder(MysqlxDatatypes.Any.getDefaultInstance());
         }

         public MysqlxDatatypes.Any.Builder addArgsBuilder(int index) {
            return this.getArgsFieldBuilder().addBuilder(index, MysqlxDatatypes.Any.getDefaultInstance());
         }

         public List<MysqlxDatatypes.Any.Builder> getArgsBuilderList() {
            return this.getArgsFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> getArgsFieldBuilder() {
            if (this.argsBuilder_ == null) {
               this.argsBuilder_ = new RepeatedFieldBuilderV3<>(this.args_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
               this.args_ = null;
            }

            return this.argsBuilder_;
         }

         @Override
         public boolean hasCompactMetadata() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public boolean getCompactMetadata() {
            return this.compactMetadata_;
         }

         public MysqlxPrepare.Execute.Builder setCompactMetadata(boolean value) {
            this.bitField0_ |= 4;
            this.compactMetadata_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxPrepare.Execute.Builder clearCompactMetadata() {
            this.bitField0_ &= -5;
            this.compactMetadata_ = false;
            this.onChanged();
            return this;
         }

         public final MysqlxPrepare.Execute.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxPrepare.Execute.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxPrepare.Execute.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxPrepare.Execute.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface ExecuteOrBuilder extends MessageOrBuilder {
      boolean hasStmtId();

      int getStmtId();

      List<MysqlxDatatypes.Any> getArgsList();

      MysqlxDatatypes.Any getArgs(int var1);

      int getArgsCount();

      List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList();

      MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int var1);

      boolean hasCompactMetadata();

      boolean getCompactMetadata();
   }

   public static final class Prepare extends GeneratedMessageV3 implements MysqlxPrepare.PrepareOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int STMT_ID_FIELD_NUMBER = 1;
      private int stmtId_;
      public static final int STMT_FIELD_NUMBER = 2;
      private MysqlxPrepare.Prepare.OneOfMessage stmt_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxPrepare.Prepare DEFAULT_INSTANCE = new MysqlxPrepare.Prepare();
      @Deprecated
      public static final Parser<MysqlxPrepare.Prepare> PARSER = new AbstractParser<MysqlxPrepare.Prepare>() {
         public MysqlxPrepare.Prepare parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxPrepare.Prepare(input, extensionRegistry);
         }
      };

      private Prepare(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Prepare() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxPrepare.Prepare();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Prepare(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.stmtId_ = input.readUInt32();
                        break;
                     case 18:
                        MysqlxPrepare.Prepare.OneOfMessage.Builder subBuilder = null;
                        if ((this.bitField0_ & 2) != 0) {
                           subBuilder = this.stmt_.toBuilder();
                        }

                        this.stmt_ = input.readMessage(MysqlxPrepare.Prepare.OneOfMessage.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.stmt_);
                           this.stmt_ = subBuilder.buildPartial();
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
         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxPrepare.Prepare.class, MysqlxPrepare.Prepare.Builder.class);
      }

      @Override
      public boolean hasStmtId() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public int getStmtId() {
         return this.stmtId_;
      }

      @Override
      public boolean hasStmt() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public MysqlxPrepare.Prepare.OneOfMessage getStmt() {
         return this.stmt_ == null ? MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance() : this.stmt_;
      }

      @Override
      public MysqlxPrepare.Prepare.OneOfMessageOrBuilder getStmtOrBuilder() {
         return this.stmt_ == null ? MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance() : this.stmt_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasStmtId()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.hasStmt()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getStmt().isInitialized()) {
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
            output.writeUInt32(1, this.stmtId_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeMessage(2, this.getStmt());
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
               size += CodedOutputStream.computeUInt32Size(1, this.stmtId_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeMessageSize(2, this.getStmt());
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
         } else if (!(obj instanceof MysqlxPrepare.Prepare)) {
            return super.equals(obj);
         } else {
            MysqlxPrepare.Prepare other = (MysqlxPrepare.Prepare)obj;
            if (this.hasStmtId() != other.hasStmtId()) {
               return false;
            } else if (this.hasStmtId() && this.getStmtId() != other.getStmtId()) {
               return false;
            } else if (this.hasStmt() != other.hasStmt()) {
               return false;
            } else {
               return this.hasStmt() && !this.getStmt().equals(other.getStmt()) ? false : this.unknownFields.equals(other.unknownFields);
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
            if (this.hasStmtId()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getStmtId();
            }

            if (this.hasStmt()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getStmt().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxPrepare.Prepare parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxPrepare.Prepare parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxPrepare.Prepare parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxPrepare.Prepare parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxPrepare.Prepare parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxPrepare.Prepare parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxPrepare.Prepare parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxPrepare.Prepare parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxPrepare.Prepare parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxPrepare.Prepare parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxPrepare.Prepare parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxPrepare.Prepare parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxPrepare.Prepare.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxPrepare.Prepare.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxPrepare.Prepare.Builder newBuilder(MysqlxPrepare.Prepare prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxPrepare.Prepare.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxPrepare.Prepare.Builder() : new MysqlxPrepare.Prepare.Builder().mergeFrom(this);
      }

      protected MysqlxPrepare.Prepare.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxPrepare.Prepare.Builder(parent);
      }

      public static MysqlxPrepare.Prepare getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxPrepare.Prepare> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxPrepare.Prepare> getParserForType() {
         return PARSER;
      }

      public MysqlxPrepare.Prepare getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxPrepare.Prepare.Builder> implements MysqlxPrepare.PrepareOrBuilder {
         private int bitField0_;
         private int stmtId_;
         private MysqlxPrepare.Prepare.OneOfMessage stmt_;
         private SingleFieldBuilderV3<MysqlxPrepare.Prepare.OneOfMessage, MysqlxPrepare.Prepare.OneOfMessage.Builder, MysqlxPrepare.Prepare.OneOfMessageOrBuilder> stmtBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxPrepare.Prepare.class, MysqlxPrepare.Prepare.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxPrepare.Prepare.alwaysUseFieldBuilders) {
               this.getStmtFieldBuilder();
            }
         }

         public MysqlxPrepare.Prepare.Builder clear() {
            super.clear();
            this.stmtId_ = 0;
            this.bitField0_ &= -2;
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_descriptor;
         }

         public MysqlxPrepare.Prepare getDefaultInstanceForType() {
            return MysqlxPrepare.Prepare.getDefaultInstance();
         }

         public MysqlxPrepare.Prepare build() {
            MysqlxPrepare.Prepare result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxPrepare.Prepare buildPartial() {
            MysqlxPrepare.Prepare result = new MysqlxPrepare.Prepare(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.stmtId_ = this.stmtId_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               if (this.stmtBuilder_ == null) {
                  result.stmt_ = this.stmt_;
               } else {
                  result.stmt_ = this.stmtBuilder_.build();
               }

               to_bitField0_ |= 2;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxPrepare.Prepare.Builder clone() {
            return (MysqlxPrepare.Prepare.Builder)super.clone();
         }

         public MysqlxPrepare.Prepare.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxPrepare.Prepare.Builder)super.setField(field, value);
         }

         public MysqlxPrepare.Prepare.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxPrepare.Prepare.Builder)super.clearField(field);
         }

         public MysqlxPrepare.Prepare.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxPrepare.Prepare.Builder)super.clearOneof(oneof);
         }

         public MysqlxPrepare.Prepare.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxPrepare.Prepare.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxPrepare.Prepare.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxPrepare.Prepare.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxPrepare.Prepare.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxPrepare.Prepare) {
               return this.mergeFrom((MysqlxPrepare.Prepare)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxPrepare.Prepare.Builder mergeFrom(MysqlxPrepare.Prepare other) {
            if (other == MysqlxPrepare.Prepare.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasStmtId()) {
                  this.setStmtId(other.getStmtId());
               }

               if (other.hasStmt()) {
                  this.mergeStmt(other.getStmt());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasStmtId()) {
               return false;
            } else {
               return !this.hasStmt() ? false : this.getStmt().isInitialized();
            }
         }

         public MysqlxPrepare.Prepare.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxPrepare.Prepare parsedMessage = null;

            try {
               parsedMessage = MysqlxPrepare.Prepare.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxPrepare.Prepare)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasStmtId() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public int getStmtId() {
            return this.stmtId_;
         }

         public MysqlxPrepare.Prepare.Builder setStmtId(int value) {
            this.bitField0_ |= 1;
            this.stmtId_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxPrepare.Prepare.Builder clearStmtId() {
            this.bitField0_ &= -2;
            this.stmtId_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasStmt() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxPrepare.Prepare.OneOfMessage getStmt() {
            if (this.stmtBuilder_ == null) {
               return this.stmt_ == null ? MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance() : this.stmt_;
            } else {
               return this.stmtBuilder_.getMessage();
            }
         }

         public MysqlxPrepare.Prepare.Builder setStmt(MysqlxPrepare.Prepare.OneOfMessage value) {
            if (this.stmtBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.stmt_ = value;
               this.onChanged();
            } else {
               this.stmtBuilder_.setMessage(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public MysqlxPrepare.Prepare.Builder setStmt(MysqlxPrepare.Prepare.OneOfMessage.Builder builderForValue) {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = builderForValue.build();
               this.onChanged();
            } else {
               this.stmtBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 2;
            return this;
         }

         public MysqlxPrepare.Prepare.Builder mergeStmt(MysqlxPrepare.Prepare.OneOfMessage value) {
            if (this.stmtBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0 && this.stmt_ != null && this.stmt_ != MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance()) {
                  this.stmt_ = MysqlxPrepare.Prepare.OneOfMessage.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
               } else {
                  this.stmt_ = value;
               }

               this.onChanged();
            } else {
               this.stmtBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 2;
            return this;
         }

         public MysqlxPrepare.Prepare.Builder clearStmt() {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
               this.onChanged();
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         public MysqlxPrepare.Prepare.OneOfMessage.Builder getStmtBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return this.getStmtFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxPrepare.Prepare.OneOfMessageOrBuilder getStmtOrBuilder() {
            if (this.stmtBuilder_ != null) {
               return this.stmtBuilder_.getMessageOrBuilder();
            } else {
               return this.stmt_ == null ? MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance() : this.stmt_;
            }
         }

         private SingleFieldBuilderV3<MysqlxPrepare.Prepare.OneOfMessage, MysqlxPrepare.Prepare.OneOfMessage.Builder, MysqlxPrepare.Prepare.OneOfMessageOrBuilder> getStmtFieldBuilder() {
            if (this.stmtBuilder_ == null) {
               this.stmtBuilder_ = new SingleFieldBuilderV3<>(this.getStmt(), this.getParentForChildren(), this.isClean());
               this.stmt_ = null;
            }

            return this.stmtBuilder_;
         }

         public final MysqlxPrepare.Prepare.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxPrepare.Prepare.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxPrepare.Prepare.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxPrepare.Prepare.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static final class OneOfMessage extends GeneratedMessageV3 implements MysqlxPrepare.Prepare.OneOfMessageOrBuilder {
         private static final long serialVersionUID = 0L;
         private int bitField0_;
         public static final int TYPE_FIELD_NUMBER = 1;
         private int type_;
         public static final int FIND_FIELD_NUMBER = 2;
         private MysqlxCrud.Find find_;
         public static final int INSERT_FIELD_NUMBER = 3;
         private MysqlxCrud.Insert insert_;
         public static final int UPDATE_FIELD_NUMBER = 4;
         private MysqlxCrud.Update update_;
         public static final int DELETE_FIELD_NUMBER = 5;
         private MysqlxCrud.Delete delete_;
         public static final int STMT_EXECUTE_FIELD_NUMBER = 6;
         private MysqlxSql.StmtExecute stmtExecute_;
         private byte memoizedIsInitialized = -1;
         private static final MysqlxPrepare.Prepare.OneOfMessage DEFAULT_INSTANCE = new MysqlxPrepare.Prepare.OneOfMessage();
         @Deprecated
         public static final Parser<MysqlxPrepare.Prepare.OneOfMessage> PARSER = new AbstractParser<MysqlxPrepare.Prepare.OneOfMessage>() {
            public MysqlxPrepare.Prepare.OneOfMessage parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
               return new MysqlxPrepare.Prepare.OneOfMessage(input, extensionRegistry);
            }
         };

         private OneOfMessage(GeneratedMessageV3.Builder<?> builder) {
            super(builder);
         }

         private OneOfMessage() {
            this.type_ = 0;
         }

         @Override
         protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
            return new MysqlxPrepare.Prepare.OneOfMessage();
         }

         @Override
         public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
         }

         private OneOfMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                           MysqlxPrepare.Prepare.OneOfMessage.Type value = MysqlxPrepare.Prepare.OneOfMessage.Type.valueOf(rawValue);
                           if (value == null) {
                              unknownFields.mergeVarintField(1, rawValue);
                           } else {
                              this.bitField0_ |= 1;
                              this.type_ = rawValue;
                           }
                           break;
                        case 18:
                           MysqlxCrud.Find.Builder subBuilderxxxx = null;
                           if ((this.bitField0_ & 2) != 0) {
                              subBuilderxxxx = this.find_.toBuilder();
                           }

                           this.find_ = input.readMessage(MysqlxCrud.Find.PARSER, extensionRegistry);
                           if (subBuilderxxxx != null) {
                              subBuilderxxxx.mergeFrom(this.find_);
                              this.find_ = subBuilderxxxx.buildPartial();
                           }

                           this.bitField0_ |= 2;
                           break;
                        case 26:
                           MysqlxCrud.Insert.Builder subBuilderxxx = null;
                           if ((this.bitField0_ & 4) != 0) {
                              subBuilderxxx = this.insert_.toBuilder();
                           }

                           this.insert_ = input.readMessage(MysqlxCrud.Insert.PARSER, extensionRegistry);
                           if (subBuilderxxx != null) {
                              subBuilderxxx.mergeFrom(this.insert_);
                              this.insert_ = subBuilderxxx.buildPartial();
                           }

                           this.bitField0_ |= 4;
                           break;
                        case 34:
                           MysqlxCrud.Update.Builder subBuilderxx = null;
                           if ((this.bitField0_ & 8) != 0) {
                              subBuilderxx = this.update_.toBuilder();
                           }

                           this.update_ = input.readMessage(MysqlxCrud.Update.PARSER, extensionRegistry);
                           if (subBuilderxx != null) {
                              subBuilderxx.mergeFrom(this.update_);
                              this.update_ = subBuilderxx.buildPartial();
                           }

                           this.bitField0_ |= 8;
                           break;
                        case 42:
                           MysqlxCrud.Delete.Builder subBuilderx = null;
                           if ((this.bitField0_ & 16) != 0) {
                              subBuilderx = this.delete_.toBuilder();
                           }

                           this.delete_ = input.readMessage(MysqlxCrud.Delete.PARSER, extensionRegistry);
                           if (subBuilderx != null) {
                              subBuilderx.mergeFrom(this.delete_);
                              this.delete_ = subBuilderx.buildPartial();
                           }

                           this.bitField0_ |= 16;
                           break;
                        case 50:
                           MysqlxSql.StmtExecute.Builder subBuilder = null;
                           if ((this.bitField0_ & 32) != 0) {
                              subBuilder = this.stmtExecute_.toBuilder();
                           }

                           this.stmtExecute_ = input.readMessage(MysqlxSql.StmtExecute.PARSER, extensionRegistry);
                           if (subBuilder != null) {
                              subBuilder.mergeFrom(this.stmtExecute_);
                              this.stmtExecute_ = subBuilder.buildPartial();
                           }

                           this.bitField0_ |= 32;
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
            return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxPrepare.Prepare.OneOfMessage.class, MysqlxPrepare.Prepare.OneOfMessage.Builder.class);
         }

         @Override
         public boolean hasType() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxPrepare.Prepare.OneOfMessage.Type getType() {
            MysqlxPrepare.Prepare.OneOfMessage.Type result = MysqlxPrepare.Prepare.OneOfMessage.Type.valueOf(this.type_);
            return result == null ? MysqlxPrepare.Prepare.OneOfMessage.Type.FIND : result;
         }

         @Override
         public boolean hasFind() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxCrud.Find getFind() {
            return this.find_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.find_;
         }

         @Override
         public MysqlxCrud.FindOrBuilder getFindOrBuilder() {
            return this.find_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.find_;
         }

         @Override
         public boolean hasInsert() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public MysqlxCrud.Insert getInsert() {
            return this.insert_ == null ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_;
         }

         @Override
         public MysqlxCrud.InsertOrBuilder getInsertOrBuilder() {
            return this.insert_ == null ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_;
         }

         @Override
         public boolean hasUpdate() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public MysqlxCrud.Update getUpdate() {
            return this.update_ == null ? MysqlxCrud.Update.getDefaultInstance() : this.update_;
         }

         @Override
         public MysqlxCrud.UpdateOrBuilder getUpdateOrBuilder() {
            return this.update_ == null ? MysqlxCrud.Update.getDefaultInstance() : this.update_;
         }

         @Override
         public boolean hasDelete() {
            return (this.bitField0_ & 16) != 0;
         }

         @Override
         public MysqlxCrud.Delete getDelete() {
            return this.delete_ == null ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_;
         }

         @Override
         public MysqlxCrud.DeleteOrBuilder getDeleteOrBuilder() {
            return this.delete_ == null ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_;
         }

         @Override
         public boolean hasStmtExecute() {
            return (this.bitField0_ & 32) != 0;
         }

         @Override
         public MysqlxSql.StmtExecute getStmtExecute() {
            return this.stmtExecute_ == null ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_;
         }

         @Override
         public MysqlxSql.StmtExecuteOrBuilder getStmtExecuteOrBuilder() {
            return this.stmtExecute_ == null ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_;
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
            } else if (this.hasFind() && !this.getFind().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else if (this.hasInsert() && !this.getInsert().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else if (this.hasUpdate() && !this.getUpdate().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else if (this.hasDelete() && !this.getDelete().isInitialized()) {
               this.memoizedIsInitialized = 0;
               return false;
            } else if (this.hasStmtExecute() && !this.getStmtExecute().isInitialized()) {
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
               output.writeMessage(2, this.getFind());
            }

            if ((this.bitField0_ & 4) != 0) {
               output.writeMessage(3, this.getInsert());
            }

            if ((this.bitField0_ & 8) != 0) {
               output.writeMessage(4, this.getUpdate());
            }

            if ((this.bitField0_ & 16) != 0) {
               output.writeMessage(5, this.getDelete());
            }

            if ((this.bitField0_ & 32) != 0) {
               output.writeMessage(6, this.getStmtExecute());
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
                  size += CodedOutputStream.computeMessageSize(2, this.getFind());
               }

               if ((this.bitField0_ & 4) != 0) {
                  size += CodedOutputStream.computeMessageSize(3, this.getInsert());
               }

               if ((this.bitField0_ & 8) != 0) {
                  size += CodedOutputStream.computeMessageSize(4, this.getUpdate());
               }

               if ((this.bitField0_ & 16) != 0) {
                  size += CodedOutputStream.computeMessageSize(5, this.getDelete());
               }

               if ((this.bitField0_ & 32) != 0) {
                  size += CodedOutputStream.computeMessageSize(6, this.getStmtExecute());
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
            } else if (!(obj instanceof MysqlxPrepare.Prepare.OneOfMessage)) {
               return super.equals(obj);
            } else {
               MysqlxPrepare.Prepare.OneOfMessage other = (MysqlxPrepare.Prepare.OneOfMessage)obj;
               if (this.hasType() != other.hasType()) {
                  return false;
               } else if (this.hasType() && this.type_ != other.type_) {
                  return false;
               } else if (this.hasFind() != other.hasFind()) {
                  return false;
               } else if (this.hasFind() && !this.getFind().equals(other.getFind())) {
                  return false;
               } else if (this.hasInsert() != other.hasInsert()) {
                  return false;
               } else if (this.hasInsert() && !this.getInsert().equals(other.getInsert())) {
                  return false;
               } else if (this.hasUpdate() != other.hasUpdate()) {
                  return false;
               } else if (this.hasUpdate() && !this.getUpdate().equals(other.getUpdate())) {
                  return false;
               } else if (this.hasDelete() != other.hasDelete()) {
                  return false;
               } else if (this.hasDelete() && !this.getDelete().equals(other.getDelete())) {
                  return false;
               } else if (this.hasStmtExecute() != other.hasStmtExecute()) {
                  return false;
               } else {
                  return this.hasStmtExecute() && !this.getStmtExecute().equals(other.getStmtExecute())
                     ? false
                     : this.unknownFields.equals(other.unknownFields);
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

               if (this.hasFind()) {
                  hash = 37 * hash + 2;
                  hash = 53 * hash + this.getFind().hashCode();
               }

               if (this.hasInsert()) {
                  hash = 37 * hash + 3;
                  hash = 53 * hash + this.getInsert().hashCode();
               }

               if (this.hasUpdate()) {
                  hash = 37 * hash + 4;
                  hash = 53 * hash + this.getUpdate().hashCode();
               }

               if (this.hasDelete()) {
                  hash = 37 * hash + 5;
                  hash = 53 * hash + this.getDelete().hashCode();
               }

               if (this.hasStmtExecute()) {
                  hash = 37 * hash + 6;
                  hash = 53 * hash + this.getStmtExecute().hashCode();
               }

               hash = 29 * hash + this.unknownFields.hashCode();
               this.memoizedHashCode = hash;
               return hash;
            }
         }

         public static MysqlxPrepare.Prepare.OneOfMessage parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxPrepare.Prepare.OneOfMessage parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxPrepare.Prepare.OneOfMessage parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxPrepare.Prepare.OneOfMessage parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxPrepare.Prepare.OneOfMessage parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxPrepare.Prepare.OneOfMessage parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxPrepare.Prepare.OneOfMessage parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static MysqlxPrepare.Prepare.OneOfMessage parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public static MysqlxPrepare.Prepare.OneOfMessage parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
         }

         public static MysqlxPrepare.Prepare.OneOfMessage parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
         }

         public static MysqlxPrepare.Prepare.OneOfMessage parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static MysqlxPrepare.Prepare.OneOfMessage parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public MysqlxPrepare.Prepare.OneOfMessage.Builder newBuilderForType() {
            return newBuilder();
         }

         public static MysqlxPrepare.Prepare.OneOfMessage.Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
         }

         public static MysqlxPrepare.Prepare.OneOfMessage.Builder newBuilder(MysqlxPrepare.Prepare.OneOfMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
         }

         public MysqlxPrepare.Prepare.OneOfMessage.Builder toBuilder() {
            return this == DEFAULT_INSTANCE
               ? new MysqlxPrepare.Prepare.OneOfMessage.Builder()
               : new MysqlxPrepare.Prepare.OneOfMessage.Builder().mergeFrom(this);
         }

         protected MysqlxPrepare.Prepare.OneOfMessage.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            return new MysqlxPrepare.Prepare.OneOfMessage.Builder(parent);
         }

         public static MysqlxPrepare.Prepare.OneOfMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<MysqlxPrepare.Prepare.OneOfMessage> parser() {
            return PARSER;
         }

         @Override
         public Parser<MysqlxPrepare.Prepare.OneOfMessage> getParserForType() {
            return PARSER;
         }

         public MysqlxPrepare.Prepare.OneOfMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
         }

         public static final class Builder
            extends GeneratedMessageV3.Builder<MysqlxPrepare.Prepare.OneOfMessage.Builder>
            implements MysqlxPrepare.Prepare.OneOfMessageOrBuilder {
            private int bitField0_;
            private int type_ = 0;
            private MysqlxCrud.Find find_;
            private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> findBuilder_;
            private MysqlxCrud.Insert insert_;
            private SingleFieldBuilderV3<MysqlxCrud.Insert, MysqlxCrud.Insert.Builder, MysqlxCrud.InsertOrBuilder> insertBuilder_;
            private MysqlxCrud.Update update_;
            private SingleFieldBuilderV3<MysqlxCrud.Update, MysqlxCrud.Update.Builder, MysqlxCrud.UpdateOrBuilder> updateBuilder_;
            private MysqlxCrud.Delete delete_;
            private SingleFieldBuilderV3<MysqlxCrud.Delete, MysqlxCrud.Delete.Builder, MysqlxCrud.DeleteOrBuilder> deleteBuilder_;
            private MysqlxSql.StmtExecute stmtExecute_;
            private SingleFieldBuilderV3<MysqlxSql.StmtExecute, MysqlxSql.StmtExecute.Builder, MysqlxSql.StmtExecuteOrBuilder> stmtExecuteBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
               return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
               return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_fieldAccessorTable
                  .ensureFieldAccessorsInitialized(MysqlxPrepare.Prepare.OneOfMessage.class, MysqlxPrepare.Prepare.OneOfMessage.Builder.class);
            }

            private Builder() {
               this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
               super(parent);
               this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
               if (MysqlxPrepare.Prepare.OneOfMessage.alwaysUseFieldBuilders) {
                  this.getFindFieldBuilder();
                  this.getInsertFieldBuilder();
                  this.getUpdateFieldBuilder();
                  this.getDeleteFieldBuilder();
                  this.getStmtExecuteFieldBuilder();
               }
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder clear() {
               super.clear();
               this.type_ = 0;
               this.bitField0_ &= -2;
               if (this.findBuilder_ == null) {
                  this.find_ = null;
               } else {
                  this.findBuilder_.clear();
               }

               this.bitField0_ &= -3;
               if (this.insertBuilder_ == null) {
                  this.insert_ = null;
               } else {
                  this.insertBuilder_.clear();
               }

               this.bitField0_ &= -5;
               if (this.updateBuilder_ == null) {
                  this.update_ = null;
               } else {
                  this.updateBuilder_.clear();
               }

               this.bitField0_ &= -9;
               if (this.deleteBuilder_ == null) {
                  this.delete_ = null;
               } else {
                  this.deleteBuilder_.clear();
               }

               this.bitField0_ &= -17;
               if (this.stmtExecuteBuilder_ == null) {
                  this.stmtExecute_ = null;
               } else {
                  this.stmtExecuteBuilder_.clear();
               }

               this.bitField0_ &= -33;
               return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
               return MysqlxPrepare.internal_static_Mysqlx_Prepare_Prepare_OneOfMessage_descriptor;
            }

            public MysqlxPrepare.Prepare.OneOfMessage getDefaultInstanceForType() {
               return MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance();
            }

            public MysqlxPrepare.Prepare.OneOfMessage build() {
               MysqlxPrepare.Prepare.OneOfMessage result = this.buildPartial();
               if (!result.isInitialized()) {
                  throw newUninitializedMessageException(result);
               } else {
                  return result;
               }
            }

            public MysqlxPrepare.Prepare.OneOfMessage buildPartial() {
               MysqlxPrepare.Prepare.OneOfMessage result = new MysqlxPrepare.Prepare.OneOfMessage(this);
               int from_bitField0_ = this.bitField0_;
               int to_bitField0_ = 0;
               if ((from_bitField0_ & 1) != 0) {
                  to_bitField0_ |= 1;
               }

               result.type_ = this.type_;
               if ((from_bitField0_ & 2) != 0) {
                  if (this.findBuilder_ == null) {
                     result.find_ = this.find_;
                  } else {
                     result.find_ = this.findBuilder_.build();
                  }

                  to_bitField0_ |= 2;
               }

               if ((from_bitField0_ & 4) != 0) {
                  if (this.insertBuilder_ == null) {
                     result.insert_ = this.insert_;
                  } else {
                     result.insert_ = this.insertBuilder_.build();
                  }

                  to_bitField0_ |= 4;
               }

               if ((from_bitField0_ & 8) != 0) {
                  if (this.updateBuilder_ == null) {
                     result.update_ = this.update_;
                  } else {
                     result.update_ = this.updateBuilder_.build();
                  }

                  to_bitField0_ |= 8;
               }

               if ((from_bitField0_ & 16) != 0) {
                  if (this.deleteBuilder_ == null) {
                     result.delete_ = this.delete_;
                  } else {
                     result.delete_ = this.deleteBuilder_.build();
                  }

                  to_bitField0_ |= 16;
               }

               if ((from_bitField0_ & 32) != 0) {
                  if (this.stmtExecuteBuilder_ == null) {
                     result.stmtExecute_ = this.stmtExecute_;
                  } else {
                     result.stmtExecute_ = this.stmtExecuteBuilder_.build();
                  }

                  to_bitField0_ |= 32;
               }

               result.bitField0_ = to_bitField0_;
               this.onBuilt();
               return result;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder clone() {
               return (MysqlxPrepare.Prepare.OneOfMessage.Builder)super.clone();
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder setField(Descriptors.FieldDescriptor field, Object value) {
               return (MysqlxPrepare.Prepare.OneOfMessage.Builder)super.setField(field, value);
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder clearField(Descriptors.FieldDescriptor field) {
               return (MysqlxPrepare.Prepare.OneOfMessage.Builder)super.clearField(field);
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
               return (MysqlxPrepare.Prepare.OneOfMessage.Builder)super.clearOneof(oneof);
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
               return (MysqlxPrepare.Prepare.OneOfMessage.Builder)super.setRepeatedField(field, index, value);
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
               return (MysqlxPrepare.Prepare.OneOfMessage.Builder)super.addRepeatedField(field, value);
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder mergeFrom(Message other) {
               if (other instanceof MysqlxPrepare.Prepare.OneOfMessage) {
                  return this.mergeFrom((MysqlxPrepare.Prepare.OneOfMessage)other);
               } else {
                  super.mergeFrom(other);
                  return this;
               }
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder mergeFrom(MysqlxPrepare.Prepare.OneOfMessage other) {
               if (other == MysqlxPrepare.Prepare.OneOfMessage.getDefaultInstance()) {
                  return this;
               } else {
                  if (other.hasType()) {
                     this.setType(other.getType());
                  }

                  if (other.hasFind()) {
                     this.mergeFind(other.getFind());
                  }

                  if (other.hasInsert()) {
                     this.mergeInsert(other.getInsert());
                  }

                  if (other.hasUpdate()) {
                     this.mergeUpdate(other.getUpdate());
                  }

                  if (other.hasDelete()) {
                     this.mergeDelete(other.getDelete());
                  }

                  if (other.hasStmtExecute()) {
                     this.mergeStmtExecute(other.getStmtExecute());
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
               } else if (this.hasFind() && !this.getFind().isInitialized()) {
                  return false;
               } else if (this.hasInsert() && !this.getInsert().isInitialized()) {
                  return false;
               } else if (this.hasUpdate() && !this.getUpdate().isInitialized()) {
                  return false;
               } else {
                  return this.hasDelete() && !this.getDelete().isInitialized() ? false : !this.hasStmtExecute() || this.getStmtExecute().isInitialized();
               }
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
               MysqlxPrepare.Prepare.OneOfMessage parsedMessage = null;

               try {
                  parsedMessage = MysqlxPrepare.Prepare.OneOfMessage.PARSER.parsePartialFrom(input, extensionRegistry);
               } catch (InvalidProtocolBufferException var8) {
                  parsedMessage = (MysqlxPrepare.Prepare.OneOfMessage)var8.getUnfinishedMessage();
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
            public MysqlxPrepare.Prepare.OneOfMessage.Type getType() {
               MysqlxPrepare.Prepare.OneOfMessage.Type result = MysqlxPrepare.Prepare.OneOfMessage.Type.valueOf(this.type_);
               return result == null ? MysqlxPrepare.Prepare.OneOfMessage.Type.FIND : result;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder setType(MysqlxPrepare.Prepare.OneOfMessage.Type value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 1;
                  this.type_ = value.getNumber();
                  this.onChanged();
                  return this;
               }
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder clearType() {
               this.bitField0_ &= -2;
               this.type_ = 0;
               this.onChanged();
               return this;
            }

            @Override
            public boolean hasFind() {
               return (this.bitField0_ & 2) != 0;
            }

            @Override
            public MysqlxCrud.Find getFind() {
               if (this.findBuilder_ == null) {
                  return this.find_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.find_;
               } else {
                  return this.findBuilder_.getMessage();
               }
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder setFind(MysqlxCrud.Find value) {
               if (this.findBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.find_ = value;
                  this.onChanged();
               } else {
                  this.findBuilder_.setMessage(value);
               }

               this.bitField0_ |= 2;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder setFind(MysqlxCrud.Find.Builder builderForValue) {
               if (this.findBuilder_ == null) {
                  this.find_ = builderForValue.build();
                  this.onChanged();
               } else {
                  this.findBuilder_.setMessage(builderForValue.build());
               }

               this.bitField0_ |= 2;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder mergeFind(MysqlxCrud.Find value) {
               if (this.findBuilder_ == null) {
                  if ((this.bitField0_ & 2) != 0 && this.find_ != null && this.find_ != MysqlxCrud.Find.getDefaultInstance()) {
                     this.find_ = MysqlxCrud.Find.newBuilder(this.find_).mergeFrom(value).buildPartial();
                  } else {
                     this.find_ = value;
                  }

                  this.onChanged();
               } else {
                  this.findBuilder_.mergeFrom(value);
               }

               this.bitField0_ |= 2;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder clearFind() {
               if (this.findBuilder_ == null) {
                  this.find_ = null;
                  this.onChanged();
               } else {
                  this.findBuilder_.clear();
               }

               this.bitField0_ &= -3;
               return this;
            }

            public MysqlxCrud.Find.Builder getFindBuilder() {
               this.bitField0_ |= 2;
               this.onChanged();
               return this.getFindFieldBuilder().getBuilder();
            }

            @Override
            public MysqlxCrud.FindOrBuilder getFindOrBuilder() {
               if (this.findBuilder_ != null) {
                  return this.findBuilder_.getMessageOrBuilder();
               } else {
                  return this.find_ == null ? MysqlxCrud.Find.getDefaultInstance() : this.find_;
               }
            }

            private SingleFieldBuilderV3<MysqlxCrud.Find, MysqlxCrud.Find.Builder, MysqlxCrud.FindOrBuilder> getFindFieldBuilder() {
               if (this.findBuilder_ == null) {
                  this.findBuilder_ = new SingleFieldBuilderV3<>(this.getFind(), this.getParentForChildren(), this.isClean());
                  this.find_ = null;
               }

               return this.findBuilder_;
            }

            @Override
            public boolean hasInsert() {
               return (this.bitField0_ & 4) != 0;
            }

            @Override
            public MysqlxCrud.Insert getInsert() {
               if (this.insertBuilder_ == null) {
                  return this.insert_ == null ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_;
               } else {
                  return this.insertBuilder_.getMessage();
               }
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder setInsert(MysqlxCrud.Insert value) {
               if (this.insertBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.insert_ = value;
                  this.onChanged();
               } else {
                  this.insertBuilder_.setMessage(value);
               }

               this.bitField0_ |= 4;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder setInsert(MysqlxCrud.Insert.Builder builderForValue) {
               if (this.insertBuilder_ == null) {
                  this.insert_ = builderForValue.build();
                  this.onChanged();
               } else {
                  this.insertBuilder_.setMessage(builderForValue.build());
               }

               this.bitField0_ |= 4;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder mergeInsert(MysqlxCrud.Insert value) {
               if (this.insertBuilder_ == null) {
                  if ((this.bitField0_ & 4) != 0 && this.insert_ != null && this.insert_ != MysqlxCrud.Insert.getDefaultInstance()) {
                     this.insert_ = MysqlxCrud.Insert.newBuilder(this.insert_).mergeFrom(value).buildPartial();
                  } else {
                     this.insert_ = value;
                  }

                  this.onChanged();
               } else {
                  this.insertBuilder_.mergeFrom(value);
               }

               this.bitField0_ |= 4;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder clearInsert() {
               if (this.insertBuilder_ == null) {
                  this.insert_ = null;
                  this.onChanged();
               } else {
                  this.insertBuilder_.clear();
               }

               this.bitField0_ &= -5;
               return this;
            }

            public MysqlxCrud.Insert.Builder getInsertBuilder() {
               this.bitField0_ |= 4;
               this.onChanged();
               return this.getInsertFieldBuilder().getBuilder();
            }

            @Override
            public MysqlxCrud.InsertOrBuilder getInsertOrBuilder() {
               if (this.insertBuilder_ != null) {
                  return this.insertBuilder_.getMessageOrBuilder();
               } else {
                  return this.insert_ == null ? MysqlxCrud.Insert.getDefaultInstance() : this.insert_;
               }
            }

            private SingleFieldBuilderV3<MysqlxCrud.Insert, MysqlxCrud.Insert.Builder, MysqlxCrud.InsertOrBuilder> getInsertFieldBuilder() {
               if (this.insertBuilder_ == null) {
                  this.insertBuilder_ = new SingleFieldBuilderV3<>(this.getInsert(), this.getParentForChildren(), this.isClean());
                  this.insert_ = null;
               }

               return this.insertBuilder_;
            }

            @Override
            public boolean hasUpdate() {
               return (this.bitField0_ & 8) != 0;
            }

            @Override
            public MysqlxCrud.Update getUpdate() {
               if (this.updateBuilder_ == null) {
                  return this.update_ == null ? MysqlxCrud.Update.getDefaultInstance() : this.update_;
               } else {
                  return this.updateBuilder_.getMessage();
               }
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder setUpdate(MysqlxCrud.Update value) {
               if (this.updateBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.update_ = value;
                  this.onChanged();
               } else {
                  this.updateBuilder_.setMessage(value);
               }

               this.bitField0_ |= 8;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder setUpdate(MysqlxCrud.Update.Builder builderForValue) {
               if (this.updateBuilder_ == null) {
                  this.update_ = builderForValue.build();
                  this.onChanged();
               } else {
                  this.updateBuilder_.setMessage(builderForValue.build());
               }

               this.bitField0_ |= 8;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder mergeUpdate(MysqlxCrud.Update value) {
               if (this.updateBuilder_ == null) {
                  if ((this.bitField0_ & 8) != 0 && this.update_ != null && this.update_ != MysqlxCrud.Update.getDefaultInstance()) {
                     this.update_ = MysqlxCrud.Update.newBuilder(this.update_).mergeFrom(value).buildPartial();
                  } else {
                     this.update_ = value;
                  }

                  this.onChanged();
               } else {
                  this.updateBuilder_.mergeFrom(value);
               }

               this.bitField0_ |= 8;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder clearUpdate() {
               if (this.updateBuilder_ == null) {
                  this.update_ = null;
                  this.onChanged();
               } else {
                  this.updateBuilder_.clear();
               }

               this.bitField0_ &= -9;
               return this;
            }

            public MysqlxCrud.Update.Builder getUpdateBuilder() {
               this.bitField0_ |= 8;
               this.onChanged();
               return this.getUpdateFieldBuilder().getBuilder();
            }

            @Override
            public MysqlxCrud.UpdateOrBuilder getUpdateOrBuilder() {
               if (this.updateBuilder_ != null) {
                  return this.updateBuilder_.getMessageOrBuilder();
               } else {
                  return this.update_ == null ? MysqlxCrud.Update.getDefaultInstance() : this.update_;
               }
            }

            private SingleFieldBuilderV3<MysqlxCrud.Update, MysqlxCrud.Update.Builder, MysqlxCrud.UpdateOrBuilder> getUpdateFieldBuilder() {
               if (this.updateBuilder_ == null) {
                  this.updateBuilder_ = new SingleFieldBuilderV3<>(this.getUpdate(), this.getParentForChildren(), this.isClean());
                  this.update_ = null;
               }

               return this.updateBuilder_;
            }

            @Override
            public boolean hasDelete() {
               return (this.bitField0_ & 16) != 0;
            }

            @Override
            public MysqlxCrud.Delete getDelete() {
               if (this.deleteBuilder_ == null) {
                  return this.delete_ == null ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_;
               } else {
                  return this.deleteBuilder_.getMessage();
               }
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder setDelete(MysqlxCrud.Delete value) {
               if (this.deleteBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.delete_ = value;
                  this.onChanged();
               } else {
                  this.deleteBuilder_.setMessage(value);
               }

               this.bitField0_ |= 16;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder setDelete(MysqlxCrud.Delete.Builder builderForValue) {
               if (this.deleteBuilder_ == null) {
                  this.delete_ = builderForValue.build();
                  this.onChanged();
               } else {
                  this.deleteBuilder_.setMessage(builderForValue.build());
               }

               this.bitField0_ |= 16;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder mergeDelete(MysqlxCrud.Delete value) {
               if (this.deleteBuilder_ == null) {
                  if ((this.bitField0_ & 16) != 0 && this.delete_ != null && this.delete_ != MysqlxCrud.Delete.getDefaultInstance()) {
                     this.delete_ = MysqlxCrud.Delete.newBuilder(this.delete_).mergeFrom(value).buildPartial();
                  } else {
                     this.delete_ = value;
                  }

                  this.onChanged();
               } else {
                  this.deleteBuilder_.mergeFrom(value);
               }

               this.bitField0_ |= 16;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder clearDelete() {
               if (this.deleteBuilder_ == null) {
                  this.delete_ = null;
                  this.onChanged();
               } else {
                  this.deleteBuilder_.clear();
               }

               this.bitField0_ &= -17;
               return this;
            }

            public MysqlxCrud.Delete.Builder getDeleteBuilder() {
               this.bitField0_ |= 16;
               this.onChanged();
               return this.getDeleteFieldBuilder().getBuilder();
            }

            @Override
            public MysqlxCrud.DeleteOrBuilder getDeleteOrBuilder() {
               if (this.deleteBuilder_ != null) {
                  return this.deleteBuilder_.getMessageOrBuilder();
               } else {
                  return this.delete_ == null ? MysqlxCrud.Delete.getDefaultInstance() : this.delete_;
               }
            }

            private SingleFieldBuilderV3<MysqlxCrud.Delete, MysqlxCrud.Delete.Builder, MysqlxCrud.DeleteOrBuilder> getDeleteFieldBuilder() {
               if (this.deleteBuilder_ == null) {
                  this.deleteBuilder_ = new SingleFieldBuilderV3<>(this.getDelete(), this.getParentForChildren(), this.isClean());
                  this.delete_ = null;
               }

               return this.deleteBuilder_;
            }

            @Override
            public boolean hasStmtExecute() {
               return (this.bitField0_ & 32) != 0;
            }

            @Override
            public MysqlxSql.StmtExecute getStmtExecute() {
               if (this.stmtExecuteBuilder_ == null) {
                  return this.stmtExecute_ == null ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_;
               } else {
                  return this.stmtExecuteBuilder_.getMessage();
               }
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder setStmtExecute(MysqlxSql.StmtExecute value) {
               if (this.stmtExecuteBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.stmtExecute_ = value;
                  this.onChanged();
               } else {
                  this.stmtExecuteBuilder_.setMessage(value);
               }

               this.bitField0_ |= 32;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder setStmtExecute(MysqlxSql.StmtExecute.Builder builderForValue) {
               if (this.stmtExecuteBuilder_ == null) {
                  this.stmtExecute_ = builderForValue.build();
                  this.onChanged();
               } else {
                  this.stmtExecuteBuilder_.setMessage(builderForValue.build());
               }

               this.bitField0_ |= 32;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder mergeStmtExecute(MysqlxSql.StmtExecute value) {
               if (this.stmtExecuteBuilder_ == null) {
                  if ((this.bitField0_ & 32) != 0 && this.stmtExecute_ != null && this.stmtExecute_ != MysqlxSql.StmtExecute.getDefaultInstance()) {
                     this.stmtExecute_ = MysqlxSql.StmtExecute.newBuilder(this.stmtExecute_).mergeFrom(value).buildPartial();
                  } else {
                     this.stmtExecute_ = value;
                  }

                  this.onChanged();
               } else {
                  this.stmtExecuteBuilder_.mergeFrom(value);
               }

               this.bitField0_ |= 32;
               return this;
            }

            public MysqlxPrepare.Prepare.OneOfMessage.Builder clearStmtExecute() {
               if (this.stmtExecuteBuilder_ == null) {
                  this.stmtExecute_ = null;
                  this.onChanged();
               } else {
                  this.stmtExecuteBuilder_.clear();
               }

               this.bitField0_ &= -33;
               return this;
            }

            public MysqlxSql.StmtExecute.Builder getStmtExecuteBuilder() {
               this.bitField0_ |= 32;
               this.onChanged();
               return this.getStmtExecuteFieldBuilder().getBuilder();
            }

            @Override
            public MysqlxSql.StmtExecuteOrBuilder getStmtExecuteOrBuilder() {
               if (this.stmtExecuteBuilder_ != null) {
                  return this.stmtExecuteBuilder_.getMessageOrBuilder();
               } else {
                  return this.stmtExecute_ == null ? MysqlxSql.StmtExecute.getDefaultInstance() : this.stmtExecute_;
               }
            }

            private SingleFieldBuilderV3<MysqlxSql.StmtExecute, MysqlxSql.StmtExecute.Builder, MysqlxSql.StmtExecuteOrBuilder> getStmtExecuteFieldBuilder() {
               if (this.stmtExecuteBuilder_ == null) {
                  this.stmtExecuteBuilder_ = new SingleFieldBuilderV3<>(this.getStmtExecute(), this.getParentForChildren(), this.isClean());
                  this.stmtExecute_ = null;
               }

               return this.stmtExecuteBuilder_;
            }

            public final MysqlxPrepare.Prepare.OneOfMessage.Builder setUnknownFields(UnknownFieldSet unknownFields) {
               return (MysqlxPrepare.Prepare.OneOfMessage.Builder)super.setUnknownFields(unknownFields);
            }

            public final MysqlxPrepare.Prepare.OneOfMessage.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
               return (MysqlxPrepare.Prepare.OneOfMessage.Builder)super.mergeUnknownFields(unknownFields);
            }
         }

         public static enum Type implements ProtocolMessageEnum {
            FIND(0),
            INSERT(1),
            UPDATE(2),
            DELETE(4),
            STMT(5);

            public static final int FIND_VALUE = 0;
            public static final int INSERT_VALUE = 1;
            public static final int UPDATE_VALUE = 2;
            public static final int DELETE_VALUE = 4;
            public static final int STMT_VALUE = 5;
            private static final Internal.EnumLiteMap<MysqlxPrepare.Prepare.OneOfMessage.Type> internalValueMap = new Internal.EnumLiteMap<MysqlxPrepare.Prepare.OneOfMessage.Type>() {
               public MysqlxPrepare.Prepare.OneOfMessage.Type findValueByNumber(int number) {
                  return MysqlxPrepare.Prepare.OneOfMessage.Type.forNumber(number);
               }
            };
            private static final MysqlxPrepare.Prepare.OneOfMessage.Type[] VALUES = values();
            private final int value;

            @Override
            public final int getNumber() {
               return this.value;
            }

            @Deprecated
            public static MysqlxPrepare.Prepare.OneOfMessage.Type valueOf(int value) {
               return forNumber(value);
            }

            public static MysqlxPrepare.Prepare.OneOfMessage.Type forNumber(int value) {
               switch (value) {
                  case 0:
                     return FIND;
                  case 1:
                     return INSERT;
                  case 2:
                     return UPDATE;
                  case 3:
                  default:
                     return null;
                  case 4:
                     return DELETE;
                  case 5:
                     return STMT;
               }
            }

            public static Internal.EnumLiteMap<MysqlxPrepare.Prepare.OneOfMessage.Type> internalGetValueMap() {
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
               return MysqlxPrepare.Prepare.OneOfMessage.getDescriptor().getEnumTypes().get(0);
            }

            public static MysqlxPrepare.Prepare.OneOfMessage.Type valueOf(Descriptors.EnumValueDescriptor desc) {
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

      public interface OneOfMessageOrBuilder extends MessageOrBuilder {
         boolean hasType();

         MysqlxPrepare.Prepare.OneOfMessage.Type getType();

         boolean hasFind();

         MysqlxCrud.Find getFind();

         MysqlxCrud.FindOrBuilder getFindOrBuilder();

         boolean hasInsert();

         MysqlxCrud.Insert getInsert();

         MysqlxCrud.InsertOrBuilder getInsertOrBuilder();

         boolean hasUpdate();

         MysqlxCrud.Update getUpdate();

         MysqlxCrud.UpdateOrBuilder getUpdateOrBuilder();

         boolean hasDelete();

         MysqlxCrud.Delete getDelete();

         MysqlxCrud.DeleteOrBuilder getDeleteOrBuilder();

         boolean hasStmtExecute();

         MysqlxSql.StmtExecute getStmtExecute();

         MysqlxSql.StmtExecuteOrBuilder getStmtExecuteOrBuilder();
      }
   }

   public interface PrepareOrBuilder extends MessageOrBuilder {
      boolean hasStmtId();

      int getStmtId();

      boolean hasStmt();

      MysqlxPrepare.Prepare.OneOfMessage getStmt();

      MysqlxPrepare.Prepare.OneOfMessageOrBuilder getStmtOrBuilder();
   }
}
