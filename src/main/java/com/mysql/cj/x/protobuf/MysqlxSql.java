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
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxSql {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Sql_StmtExecute_descriptor = getDescriptor().getMessageTypes().get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Sql_StmtExecute_descriptor, new String[]{"Namespace", "Stmt", "Args", "CompactMetadata"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor = getDescriptor().getMessageTypes().get(1);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor, new String[0]
   );
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxSql() {
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
         "\n\u0010mysqlx_sql.proto\u0012\nMysqlx.Sql\u001a\fmysqlx.proto\u001a\u0016mysqlx_datatypes.proto\"\u007f\n\u000bStmtExecute\u0012\u0016\n\tnamespace\u0018\u0003 \u0001(\t:\u0003sql\u0012\f\n\u0004stmt\u0018\u0001 \u0002(\f\u0012#\n\u0004args\u0018\u0002 \u0003(\u000b2\u0015.Mysqlx.Datatypes.Any\u0012\u001f\n\u0010compact_metadata\u0018\u0004 \u0001(\b:\u0005false:\u0004\u0088ê0\f\"\u0015\n\rStmtExecuteOk:\u0004\u0090ê0\u0011B\u0019\n\u0017com.mysql.cj.x.protobuf"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
         descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor(), MysqlxDatatypes.getDescriptor()}
      );
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.clientMessageId);
      registry.add(Mysqlx.serverMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      Mysqlx.getDescriptor();
      MysqlxDatatypes.getDescriptor();
   }

   public static final class StmtExecute extends GeneratedMessageV3 implements MysqlxSql.StmtExecuteOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAMESPACE_FIELD_NUMBER = 3;
      private volatile Object namespace_;
      public static final int STMT_FIELD_NUMBER = 1;
      private ByteString stmt_;
      public static final int ARGS_FIELD_NUMBER = 2;
      private List<MysqlxDatatypes.Any> args_;
      public static final int COMPACT_METADATA_FIELD_NUMBER = 4;
      private boolean compactMetadata_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxSql.StmtExecute DEFAULT_INSTANCE = new MysqlxSql.StmtExecute();
      @Deprecated
      public static final Parser<MysqlxSql.StmtExecute> PARSER = new AbstractParser<MysqlxSql.StmtExecute>() {
         public MysqlxSql.StmtExecute parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxSql.StmtExecute(input, extensionRegistry);
         }
      };

      private StmtExecute(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private StmtExecute() {
         this.namespace_ = "sql";
         this.stmt_ = ByteString.EMPTY;
         this.args_ = Collections.emptyList();
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxSql.StmtExecute();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private StmtExecute(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.bitField0_ |= 2;
                        this.stmt_ = input.readBytes();
                        break;
                     case 18:
                        if ((mutable_bitField0_ & 4) == 0) {
                           this.args_ = new ArrayList<>();
                           mutable_bitField0_ |= 4;
                        }

                        this.args_.add(input.readMessage(MysqlxDatatypes.Any.PARSER, extensionRegistry));
                        break;
                     case 26:
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 1;
                        this.namespace_ = bs;
                        break;
                     case 32:
                        this.bitField0_ |= 4;
                        this.compactMetadata_ = input.readBool();
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
               if ((mutable_bitField0_ & 4) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxSql.StmtExecute.class, MysqlxSql.StmtExecute.Builder.class);
      }

      @Override
      public boolean hasNamespace() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getNamespace() {
         Object ref = this.namespace_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.namespace_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getNamespaceBytes() {
         Object ref = this.namespace_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.namespace_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public boolean hasStmt() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public ByteString getStmt() {
         return this.stmt_;
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
         return (this.bitField0_ & 4) != 0;
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
         } else if (!this.hasStmt()) {
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
         if ((this.bitField0_ & 2) != 0) {
            output.writeBytes(1, this.stmt_);
         }

         for (int i = 0; i < this.args_.size(); i++) {
            output.writeMessage(2, this.args_.get(i));
         }

         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(output, 3, this.namespace_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeBool(4, this.compactMetadata_);
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
            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeBytesSize(1, this.stmt_);
            }

            for (int i = 0; i < this.args_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(2, this.args_.get(i));
            }

            if ((this.bitField0_ & 1) != 0) {
               size += GeneratedMessageV3.computeStringSize(3, this.namespace_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeBoolSize(4, this.compactMetadata_);
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
         } else if (!(obj instanceof MysqlxSql.StmtExecute)) {
            return super.equals(obj);
         } else {
            MysqlxSql.StmtExecute other = (MysqlxSql.StmtExecute)obj;
            if (this.hasNamespace() != other.hasNamespace()) {
               return false;
            } else if (this.hasNamespace() && !this.getNamespace().equals(other.getNamespace())) {
               return false;
            } else if (this.hasStmt() != other.hasStmt()) {
               return false;
            } else if (this.hasStmt() && !this.getStmt().equals(other.getStmt())) {
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
            if (this.hasNamespace()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getNamespace().hashCode();
            }

            if (this.hasStmt()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getStmt().hashCode();
            }

            if (this.getArgsCount() > 0) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getArgsList().hashCode();
            }

            if (this.hasCompactMetadata()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + Internal.hashBoolean(this.getCompactMetadata());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxSql.StmtExecute parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSql.StmtExecute parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSql.StmtExecute parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSql.StmtExecute parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSql.StmtExecute parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSql.StmtExecute parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSql.StmtExecute parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSql.StmtExecute parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSql.StmtExecute parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxSql.StmtExecute parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSql.StmtExecute parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSql.StmtExecute parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxSql.StmtExecute.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxSql.StmtExecute.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxSql.StmtExecute.Builder newBuilder(MysqlxSql.StmtExecute prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxSql.StmtExecute.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxSql.StmtExecute.Builder() : new MysqlxSql.StmtExecute.Builder().mergeFrom(this);
      }

      protected MysqlxSql.StmtExecute.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxSql.StmtExecute.Builder(parent);
      }

      public static MysqlxSql.StmtExecute getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxSql.StmtExecute> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxSql.StmtExecute> getParserForType() {
         return PARSER;
      }

      public MysqlxSql.StmtExecute getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxSql.StmtExecute.Builder> implements MysqlxSql.StmtExecuteOrBuilder {
         private int bitField0_;
         private Object namespace_ = "sql";
         private ByteString stmt_ = ByteString.EMPTY;
         private List<MysqlxDatatypes.Any> args_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> argsBuilder_;
         private boolean compactMetadata_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxSql.StmtExecute.class, MysqlxSql.StmtExecute.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSql.StmtExecute.alwaysUseFieldBuilders) {
               this.getArgsFieldBuilder();
            }
         }

         public MysqlxSql.StmtExecute.Builder clear() {
            super.clear();
            this.namespace_ = "sql";
            this.bitField0_ &= -2;
            this.stmt_ = ByteString.EMPTY;
            this.bitField0_ &= -3;
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -5;
            } else {
               this.argsBuilder_.clear();
            }

            this.compactMetadata_ = false;
            this.bitField0_ &= -9;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecute_descriptor;
         }

         public MysqlxSql.StmtExecute getDefaultInstanceForType() {
            return MysqlxSql.StmtExecute.getDefaultInstance();
         }

         public MysqlxSql.StmtExecute build() {
            MysqlxSql.StmtExecute result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxSql.StmtExecute buildPartial() {
            MysqlxSql.StmtExecute result = new MysqlxSql.StmtExecute(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.namespace_ = this.namespace_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.stmt_ = this.stmt_;
            if (this.argsBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0) {
                  this.args_ = Collections.unmodifiableList(this.args_);
                  this.bitField0_ &= -5;
               }

               result.args_ = this.args_;
            } else {
               result.args_ = this.argsBuilder_.build();
            }

            if ((from_bitField0_ & 8) != 0) {
               result.compactMetadata_ = this.compactMetadata_;
               to_bitField0_ |= 4;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxSql.StmtExecute.Builder clone() {
            return (MysqlxSql.StmtExecute.Builder)super.clone();
         }

         public MysqlxSql.StmtExecute.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSql.StmtExecute.Builder)super.setField(field, value);
         }

         public MysqlxSql.StmtExecute.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxSql.StmtExecute.Builder)super.clearField(field);
         }

         public MysqlxSql.StmtExecute.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxSql.StmtExecute.Builder)super.clearOneof(oneof);
         }

         public MysqlxSql.StmtExecute.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxSql.StmtExecute.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxSql.StmtExecute.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSql.StmtExecute.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxSql.StmtExecute.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxSql.StmtExecute) {
               return this.mergeFrom((MysqlxSql.StmtExecute)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxSql.StmtExecute.Builder mergeFrom(MysqlxSql.StmtExecute other) {
            if (other == MysqlxSql.StmtExecute.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasNamespace()) {
                  this.bitField0_ |= 1;
                  this.namespace_ = other.namespace_;
                  this.onChanged();
               }

               if (other.hasStmt()) {
                  this.setStmt(other.getStmt());
               }

               if (this.argsBuilder_ == null) {
                  if (!other.args_.isEmpty()) {
                     if (this.args_.isEmpty()) {
                        this.args_ = other.args_;
                        this.bitField0_ &= -5;
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
                     this.bitField0_ &= -5;
                     this.argsBuilder_ = MysqlxSql.StmtExecute.alwaysUseFieldBuilders ? this.getArgsFieldBuilder() : null;
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
            if (!this.hasStmt()) {
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

         public MysqlxSql.StmtExecute.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxSql.StmtExecute parsedMessage = null;

            try {
               parsedMessage = MysqlxSql.StmtExecute.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxSql.StmtExecute)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasNamespace() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getNamespace() {
            Object ref = this.namespace_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.namespace_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getNamespaceBytes() {
            Object ref = this.namespace_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.namespace_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxSql.StmtExecute.Builder setNamespace(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.namespace_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxSql.StmtExecute.Builder clearNamespace() {
            this.bitField0_ &= -2;
            this.namespace_ = MysqlxSql.StmtExecute.getDefaultInstance().getNamespace();
            this.onChanged();
            return this;
         }

         public MysqlxSql.StmtExecute.Builder setNamespaceBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.namespace_ = value;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasStmt() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public ByteString getStmt() {
            return this.stmt_;
         }

         public MysqlxSql.StmtExecute.Builder setStmt(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.stmt_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxSql.StmtExecute.Builder clearStmt() {
            this.bitField0_ &= -3;
            this.stmt_ = MysqlxSql.StmtExecute.getDefaultInstance().getStmt();
            this.onChanged();
            return this;
         }

         private void ensureArgsIsMutable() {
            if ((this.bitField0_ & 4) == 0) {
               this.args_ = new ArrayList<>(this.args_);
               this.bitField0_ |= 4;
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

         public MysqlxSql.StmtExecute.Builder setArgs(int index, MysqlxDatatypes.Any value) {
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

         public MysqlxSql.StmtExecute.Builder setArgs(int index, MysqlxDatatypes.Any.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxSql.StmtExecute.Builder addArgs(MysqlxDatatypes.Any value) {
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

         public MysqlxSql.StmtExecute.Builder addArgs(int index, MysqlxDatatypes.Any value) {
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

         public MysqlxSql.StmtExecute.Builder addArgs(MysqlxDatatypes.Any.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxSql.StmtExecute.Builder addArgs(int index, MysqlxDatatypes.Any.Builder builderForValue) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               this.args_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.argsBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxSql.StmtExecute.Builder addAllArgs(Iterable<? extends MysqlxDatatypes.Any> values) {
            if (this.argsBuilder_ == null) {
               this.ensureArgsIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.args_);
               this.onChanged();
            } else {
               this.argsBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxSql.StmtExecute.Builder clearArgs() {
            if (this.argsBuilder_ == null) {
               this.args_ = Collections.emptyList();
               this.bitField0_ &= -5;
               this.onChanged();
            } else {
               this.argsBuilder_.clear();
            }

            return this;
         }

         public MysqlxSql.StmtExecute.Builder removeArgs(int index) {
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
               this.argsBuilder_ = new RepeatedFieldBuilderV3<>(this.args_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean());
               this.args_ = null;
            }

            return this.argsBuilder_;
         }

         @Override
         public boolean hasCompactMetadata() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public boolean getCompactMetadata() {
            return this.compactMetadata_;
         }

         public MysqlxSql.StmtExecute.Builder setCompactMetadata(boolean value) {
            this.bitField0_ |= 8;
            this.compactMetadata_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxSql.StmtExecute.Builder clearCompactMetadata() {
            this.bitField0_ &= -9;
            this.compactMetadata_ = false;
            this.onChanged();
            return this;
         }

         public final MysqlxSql.StmtExecute.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSql.StmtExecute.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxSql.StmtExecute.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSql.StmtExecute.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public static final class StmtExecuteOk extends GeneratedMessageV3 implements MysqlxSql.StmtExecuteOkOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxSql.StmtExecuteOk DEFAULT_INSTANCE = new MysqlxSql.StmtExecuteOk();
      @Deprecated
      public static final Parser<MysqlxSql.StmtExecuteOk> PARSER = new AbstractParser<MysqlxSql.StmtExecuteOk>() {
         public MysqlxSql.StmtExecuteOk parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxSql.StmtExecuteOk(input, extensionRegistry);
         }
      };

      private StmtExecuteOk(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private StmtExecuteOk() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxSql.StmtExecuteOk();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private StmtExecuteOk(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxSql.StmtExecuteOk.class, MysqlxSql.StmtExecuteOk.Builder.class);
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
         } else if (!(obj instanceof MysqlxSql.StmtExecuteOk)) {
            return super.equals(obj);
         } else {
            MysqlxSql.StmtExecuteOk other = (MysqlxSql.StmtExecuteOk)obj;
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

      public static MysqlxSql.StmtExecuteOk parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSql.StmtExecuteOk parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSql.StmtExecuteOk parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSql.StmtExecuteOk parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSql.StmtExecuteOk parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSql.StmtExecuteOk parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSql.StmtExecuteOk parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSql.StmtExecuteOk parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSql.StmtExecuteOk parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxSql.StmtExecuteOk parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSql.StmtExecuteOk parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSql.StmtExecuteOk parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxSql.StmtExecuteOk.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxSql.StmtExecuteOk.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxSql.StmtExecuteOk.Builder newBuilder(MysqlxSql.StmtExecuteOk prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxSql.StmtExecuteOk.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxSql.StmtExecuteOk.Builder() : new MysqlxSql.StmtExecuteOk.Builder().mergeFrom(this);
      }

      protected MysqlxSql.StmtExecuteOk.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxSql.StmtExecuteOk.Builder(parent);
      }

      public static MysqlxSql.StmtExecuteOk getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxSql.StmtExecuteOk> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxSql.StmtExecuteOk> getParserForType() {
         return PARSER;
      }

      public MysqlxSql.StmtExecuteOk getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxSql.StmtExecuteOk.Builder> implements MysqlxSql.StmtExecuteOkOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxSql.StmtExecuteOk.class, MysqlxSql.StmtExecuteOk.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSql.StmtExecuteOk.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxSql.StmtExecuteOk.Builder clear() {
            super.clear();
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSql.internal_static_Mysqlx_Sql_StmtExecuteOk_descriptor;
         }

         public MysqlxSql.StmtExecuteOk getDefaultInstanceForType() {
            return MysqlxSql.StmtExecuteOk.getDefaultInstance();
         }

         public MysqlxSql.StmtExecuteOk build() {
            MysqlxSql.StmtExecuteOk result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxSql.StmtExecuteOk buildPartial() {
            MysqlxSql.StmtExecuteOk result = new MysqlxSql.StmtExecuteOk(this);
            this.onBuilt();
            return result;
         }

         public MysqlxSql.StmtExecuteOk.Builder clone() {
            return (MysqlxSql.StmtExecuteOk.Builder)super.clone();
         }

         public MysqlxSql.StmtExecuteOk.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSql.StmtExecuteOk.Builder)super.setField(field, value);
         }

         public MysqlxSql.StmtExecuteOk.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxSql.StmtExecuteOk.Builder)super.clearField(field);
         }

         public MysqlxSql.StmtExecuteOk.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxSql.StmtExecuteOk.Builder)super.clearOneof(oneof);
         }

         public MysqlxSql.StmtExecuteOk.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxSql.StmtExecuteOk.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxSql.StmtExecuteOk.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSql.StmtExecuteOk.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxSql.StmtExecuteOk.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxSql.StmtExecuteOk) {
               return this.mergeFrom((MysqlxSql.StmtExecuteOk)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxSql.StmtExecuteOk.Builder mergeFrom(MysqlxSql.StmtExecuteOk other) {
            if (other == MysqlxSql.StmtExecuteOk.getDefaultInstance()) {
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

         public MysqlxSql.StmtExecuteOk.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxSql.StmtExecuteOk parsedMessage = null;

            try {
               parsedMessage = MysqlxSql.StmtExecuteOk.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxSql.StmtExecuteOk)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         public final MysqlxSql.StmtExecuteOk.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSql.StmtExecuteOk.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxSql.StmtExecuteOk.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSql.StmtExecuteOk.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface StmtExecuteOkOrBuilder extends MessageOrBuilder {
   }

   public interface StmtExecuteOrBuilder extends MessageOrBuilder {
      boolean hasNamespace();

      String getNamespace();

      ByteString getNamespaceBytes();

      boolean hasStmt();

      ByteString getStmt();

      List<MysqlxDatatypes.Any> getArgsList();

      MysqlxDatatypes.Any getArgs(int var1);

      int getArgsCount();

      List<? extends MysqlxDatatypes.AnyOrBuilder> getArgsOrBuilderList();

      MysqlxDatatypes.AnyOrBuilder getArgsOrBuilder(int var1);

      boolean hasCompactMetadata();

      boolean getCompactMetadata();
   }
}
