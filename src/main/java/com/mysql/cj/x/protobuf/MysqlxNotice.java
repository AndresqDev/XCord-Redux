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

public final class MysqlxNotice {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_Frame_descriptor = getDescriptor().getMessageTypes().get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_Frame_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Notice_Frame_descriptor, new String[]{"Type", "Scope", "Payload"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_Warning_descriptor = getDescriptor().getMessageTypes().get(1);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_Warning_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Notice_Warning_descriptor, new String[]{"Level", "Code", "Msg"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor = getDescriptor().getMessageTypes().get(2);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor, new String[]{"Param", "Value"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_SessionStateChanged_descriptor = getDescriptor().getMessageTypes().get(3);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Notice_SessionStateChanged_descriptor, new String[]{"Param", "Value"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor = getDescriptor().getMessageTypes().get(4);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor, new String[]{"Type", "ViewId"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Notice_ServerHello_descriptor = getDescriptor().getMessageTypes().get(5);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Notice_ServerHello_descriptor, new String[0]
   );
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxNotice() {
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
         "\n\u0013mysqlx_notice.proto\u0012\rMysqlx.Notice\u001a\fmysqlx.proto\u001a\u0016mysqlx_datatypes.proto\"\u0085\u0002\n\u0005Frame\u0012\f\n\u0004type\u0018\u0001 \u0002(\r\u00121\n\u0005scope\u0018\u0002 \u0001(\u000e2\u001a.Mysqlx.Notice.Frame.Scope:\u0006GLOBAL\u0012\u000f\n\u0007payload\u0018\u0003 \u0001(\f\"\u001e\n\u0005Scope\u0012\n\n\u0006GLOBAL\u0010\u0001\u0012\t\n\u0005LOCAL\u0010\u0002\"\u0083\u0001\n\u0004Type\u0012\u000b\n\u0007WARNING\u0010\u0001\u0012\u001c\n\u0018SESSION_VARIABLE_CHANGED\u0010\u0002\u0012\u0019\n\u0015SESSION_STATE_CHANGED\u0010\u0003\u0012#\n\u001fGROUP_REPLICATION_STATE_CHANGED\u0010\u0004\u0012\u0010\n\fSERVER_HELLO\u0010\u0005:\u0004\u0090ê0\u000b\"\u0085\u0001\n\u0007Warning\u00124\n\u0005level\u0018\u0001 \u0001(\u000e2\u001c.Mysqlx.Notice.Warning.Level:\u0007WARNING\u0012\f\n\u0004code\u0018\u0002 \u0002(\r\u0012\u000b\n\u0003msg\u0018\u0003 \u0002(\t\")\n\u0005Level\u0012\b\n\u0004NOTE\u0010\u0001\u0012\u000b\n\u0007WARNING\u0010\u0002\u0012\t\n\u0005ERROR\u0010\u0003\"P\n\u0016SessionVariableChanged\u0012\r\n\u0005param\u0018\u0001 \u0002(\t\u0012'\n\u0005value\u0018\u0002 \u0001(\u000b2\u0018.Mysqlx.Datatypes.Scalar\"ñ\u0002\n\u0013SessionStateChanged\u0012;\n\u0005param\u0018\u0001 \u0002(\u000e2,.Mysqlx.Notice.SessionStateChanged.Parameter\u0012'\n\u0005value\u0018\u0002 \u0003(\u000b2\u0018.Mysqlx.Datatypes.Scalar\"ó\u0001\n\tParameter\u0012\u0012\n\u000eCURRENT_SCHEMA\u0010\u0001\u0012\u0013\n\u000fACCOUNT_EXPIRED\u0010\u0002\u0012\u0017\n\u0013GENERATED_INSERT_ID\u0010\u0003\u0012\u0011\n\rROWS_AFFECTED\u0010\u0004\u0012\u000e\n\nROWS_FOUND\u0010\u0005\u0012\u0010\n\fROWS_MATCHED\u0010\u0006\u0012\u0011\n\rTRX_COMMITTED\u0010\u0007\u0012\u0012\n\u000eTRX_ROLLEDBACK\u0010\t\u0012\u0014\n\u0010PRODUCED_MESSAGE\u0010\n\u0012\u0016\n\u0012CLIENT_ID_ASSIGNED\u0010\u000b\u0012\u001a\n\u0016GENERATED_DOCUMENT_IDS\u0010\f\"®\u0001\n\u001cGroupReplicationStateChanged\u0012\f\n\u0004type\u0018\u0001 \u0002(\r\u0012\u000f\n\u0007view_id\u0018\u0002 \u0001(\t\"o\n\u0004Type\u0012\u001a\n\u0016MEMBERSHIP_QUORUM_LOSS\u0010\u0001\u0012\u001a\n\u0016MEMBERSHIP_VIEW_CHANGE\u0010\u0002\u0012\u0016\n\u0012MEMBER_ROLE_CHANGE\u0010\u0003\u0012\u0017\n\u0013MEMBER_STATE_CHANGE\u0010\u0004\"\r\n\u000bServerHelloB\u0019\n\u0017com.mysql.cj.x.protobuf"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
         descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor(), MysqlxDatatypes.getDescriptor()}
      );
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.serverMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      Mysqlx.getDescriptor();
      MysqlxDatatypes.getDescriptor();
   }

   public static final class Frame extends GeneratedMessageV3 implements MysqlxNotice.FrameOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int TYPE_FIELD_NUMBER = 1;
      private int type_;
      public static final int SCOPE_FIELD_NUMBER = 2;
      private int scope_;
      public static final int PAYLOAD_FIELD_NUMBER = 3;
      private ByteString payload_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxNotice.Frame DEFAULT_INSTANCE = new MysqlxNotice.Frame();
      @Deprecated
      public static final Parser<MysqlxNotice.Frame> PARSER = new AbstractParser<MysqlxNotice.Frame>() {
         public MysqlxNotice.Frame parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxNotice.Frame(input, extensionRegistry);
         }
      };

      private Frame(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Frame() {
         this.scope_ = 1;
         this.payload_ = ByteString.EMPTY;
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxNotice.Frame();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Frame(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.type_ = input.readUInt32();
                        break;
                     case 16:
                        int rawValue = input.readEnum();
                        MysqlxNotice.Frame.Scope value = MysqlxNotice.Frame.Scope.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(2, rawValue);
                        } else {
                           this.bitField0_ |= 2;
                           this.scope_ = rawValue;
                        }
                        break;
                     case 26:
                        this.bitField0_ |= 4;
                        this.payload_ = input.readBytes();
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
         return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxNotice.Frame.class, MysqlxNotice.Frame.Builder.class);
      }

      @Override
      public boolean hasType() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public int getType() {
         return this.type_;
      }

      @Override
      public boolean hasScope() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public MysqlxNotice.Frame.Scope getScope() {
         MysqlxNotice.Frame.Scope result = MysqlxNotice.Frame.Scope.valueOf(this.scope_);
         return result == null ? MysqlxNotice.Frame.Scope.GLOBAL : result;
      }

      @Override
      public boolean hasPayload() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public ByteString getPayload() {
         return this.payload_;
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
            output.writeUInt32(1, this.type_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(2, this.scope_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeBytes(3, this.payload_);
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
               size += CodedOutputStream.computeUInt32Size(1, this.type_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(2, this.scope_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeBytesSize(3, this.payload_);
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
         } else if (!(obj instanceof MysqlxNotice.Frame)) {
            return super.equals(obj);
         } else {
            MysqlxNotice.Frame other = (MysqlxNotice.Frame)obj;
            if (this.hasType() != other.hasType()) {
               return false;
            } else if (this.hasType() && this.getType() != other.getType()) {
               return false;
            } else if (this.hasScope() != other.hasScope()) {
               return false;
            } else if (this.hasScope() && this.scope_ != other.scope_) {
               return false;
            } else if (this.hasPayload() != other.hasPayload()) {
               return false;
            } else {
               return this.hasPayload() && !this.getPayload().equals(other.getPayload()) ? false : this.unknownFields.equals(other.unknownFields);
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
               hash = 53 * hash + this.getType();
            }

            if (this.hasScope()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.scope_;
            }

            if (this.hasPayload()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getPayload().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxNotice.Frame parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.Frame parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.Frame parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.Frame parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.Frame parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.Frame parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.Frame parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxNotice.Frame parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxNotice.Frame parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxNotice.Frame parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxNotice.Frame parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxNotice.Frame parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxNotice.Frame.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxNotice.Frame.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxNotice.Frame.Builder newBuilder(MysqlxNotice.Frame prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxNotice.Frame.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxNotice.Frame.Builder() : new MysqlxNotice.Frame.Builder().mergeFrom(this);
      }

      protected MysqlxNotice.Frame.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxNotice.Frame.Builder(parent);
      }

      public static MysqlxNotice.Frame getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxNotice.Frame> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxNotice.Frame> getParserForType() {
         return PARSER;
      }

      public MysqlxNotice.Frame getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxNotice.Frame.Builder> implements MysqlxNotice.FrameOrBuilder {
         private int bitField0_;
         private int type_;
         private int scope_ = 1;
         private ByteString payload_ = ByteString.EMPTY;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxNotice.Frame.class, MysqlxNotice.Frame.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxNotice.Frame.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxNotice.Frame.Builder clear() {
            super.clear();
            this.type_ = 0;
            this.bitField0_ &= -2;
            this.scope_ = 1;
            this.bitField0_ &= -3;
            this.payload_ = ByteString.EMPTY;
            this.bitField0_ &= -5;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_Frame_descriptor;
         }

         public MysqlxNotice.Frame getDefaultInstanceForType() {
            return MysqlxNotice.Frame.getDefaultInstance();
         }

         public MysqlxNotice.Frame build() {
            MysqlxNotice.Frame result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxNotice.Frame buildPartial() {
            MysqlxNotice.Frame result = new MysqlxNotice.Frame(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.type_ = this.type_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.scope_ = this.scope_;
            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.payload_ = this.payload_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxNotice.Frame.Builder clone() {
            return (MysqlxNotice.Frame.Builder)super.clone();
         }

         public MysqlxNotice.Frame.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxNotice.Frame.Builder)super.setField(field, value);
         }

         public MysqlxNotice.Frame.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxNotice.Frame.Builder)super.clearField(field);
         }

         public MysqlxNotice.Frame.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxNotice.Frame.Builder)super.clearOneof(oneof);
         }

         public MysqlxNotice.Frame.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxNotice.Frame.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxNotice.Frame.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxNotice.Frame.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxNotice.Frame.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxNotice.Frame) {
               return this.mergeFrom((MysqlxNotice.Frame)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxNotice.Frame.Builder mergeFrom(MysqlxNotice.Frame other) {
            if (other == MysqlxNotice.Frame.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasType()) {
                  this.setType(other.getType());
               }

               if (other.hasScope()) {
                  this.setScope(other.getScope());
               }

               if (other.hasPayload()) {
                  this.setPayload(other.getPayload());
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

         public MysqlxNotice.Frame.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxNotice.Frame parsedMessage = null;

            try {
               parsedMessage = MysqlxNotice.Frame.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxNotice.Frame)var8.getUnfinishedMessage();
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
         public int getType() {
            return this.type_;
         }

         public MysqlxNotice.Frame.Builder setType(int value) {
            this.bitField0_ |= 1;
            this.type_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxNotice.Frame.Builder clearType() {
            this.bitField0_ &= -2;
            this.type_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasScope() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxNotice.Frame.Scope getScope() {
            MysqlxNotice.Frame.Scope result = MysqlxNotice.Frame.Scope.valueOf(this.scope_);
            return result == null ? MysqlxNotice.Frame.Scope.GLOBAL : result;
         }

         public MysqlxNotice.Frame.Builder setScope(MysqlxNotice.Frame.Scope value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.scope_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxNotice.Frame.Builder clearScope() {
            this.bitField0_ &= -3;
            this.scope_ = 1;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasPayload() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public ByteString getPayload() {
            return this.payload_;
         }

         public MysqlxNotice.Frame.Builder setPayload(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.payload_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxNotice.Frame.Builder clearPayload() {
            this.bitField0_ &= -5;
            this.payload_ = MysqlxNotice.Frame.getDefaultInstance().getPayload();
            this.onChanged();
            return this;
         }

         public final MysqlxNotice.Frame.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxNotice.Frame.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxNotice.Frame.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxNotice.Frame.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static enum Scope implements ProtocolMessageEnum {
         GLOBAL(1),
         LOCAL(2);

         public static final int GLOBAL_VALUE = 1;
         public static final int LOCAL_VALUE = 2;
         private static final Internal.EnumLiteMap<MysqlxNotice.Frame.Scope> internalValueMap = new Internal.EnumLiteMap<MysqlxNotice.Frame.Scope>() {
            public MysqlxNotice.Frame.Scope findValueByNumber(int number) {
               return MysqlxNotice.Frame.Scope.forNumber(number);
            }
         };
         private static final MysqlxNotice.Frame.Scope[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static MysqlxNotice.Frame.Scope valueOf(int value) {
            return forNumber(value);
         }

         public static MysqlxNotice.Frame.Scope forNumber(int value) {
            switch (value) {
               case 1:
                  return GLOBAL;
               case 2:
                  return LOCAL;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<MysqlxNotice.Frame.Scope> internalGetValueMap() {
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
            return MysqlxNotice.Frame.getDescriptor().getEnumTypes().get(0);
         }

         public static MysqlxNotice.Frame.Scope valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Scope(int value) {
            this.value = value;
         }
      }

      public static enum Type implements ProtocolMessageEnum {
         WARNING(1),
         SESSION_VARIABLE_CHANGED(2),
         SESSION_STATE_CHANGED(3),
         GROUP_REPLICATION_STATE_CHANGED(4),
         SERVER_HELLO(5);

         public static final int WARNING_VALUE = 1;
         public static final int SESSION_VARIABLE_CHANGED_VALUE = 2;
         public static final int SESSION_STATE_CHANGED_VALUE = 3;
         public static final int GROUP_REPLICATION_STATE_CHANGED_VALUE = 4;
         public static final int SERVER_HELLO_VALUE = 5;
         private static final Internal.EnumLiteMap<MysqlxNotice.Frame.Type> internalValueMap = new Internal.EnumLiteMap<MysqlxNotice.Frame.Type>() {
            public MysqlxNotice.Frame.Type findValueByNumber(int number) {
               return MysqlxNotice.Frame.Type.forNumber(number);
            }
         };
         private static final MysqlxNotice.Frame.Type[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static MysqlxNotice.Frame.Type valueOf(int value) {
            return forNumber(value);
         }

         public static MysqlxNotice.Frame.Type forNumber(int value) {
            switch (value) {
               case 1:
                  return WARNING;
               case 2:
                  return SESSION_VARIABLE_CHANGED;
               case 3:
                  return SESSION_STATE_CHANGED;
               case 4:
                  return GROUP_REPLICATION_STATE_CHANGED;
               case 5:
                  return SERVER_HELLO;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<MysqlxNotice.Frame.Type> internalGetValueMap() {
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
            return MysqlxNotice.Frame.getDescriptor().getEnumTypes().get(1);
         }

         public static MysqlxNotice.Frame.Type valueOf(Descriptors.EnumValueDescriptor desc) {
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

   public interface FrameOrBuilder extends MessageOrBuilder {
      boolean hasType();

      int getType();

      boolean hasScope();

      MysqlxNotice.Frame.Scope getScope();

      boolean hasPayload();

      ByteString getPayload();
   }

   public static final class GroupReplicationStateChanged extends GeneratedMessageV3 implements MysqlxNotice.GroupReplicationStateChangedOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int TYPE_FIELD_NUMBER = 1;
      private int type_;
      public static final int VIEW_ID_FIELD_NUMBER = 2;
      private volatile Object viewId_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxNotice.GroupReplicationStateChanged DEFAULT_INSTANCE = new MysqlxNotice.GroupReplicationStateChanged();
      @Deprecated
      public static final Parser<MysqlxNotice.GroupReplicationStateChanged> PARSER = new AbstractParser<MysqlxNotice.GroupReplicationStateChanged>() {
         public MysqlxNotice.GroupReplicationStateChanged parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxNotice.GroupReplicationStateChanged(input, extensionRegistry);
         }
      };

      private GroupReplicationStateChanged(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private GroupReplicationStateChanged() {
         this.viewId_ = "";
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxNotice.GroupReplicationStateChanged();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private GroupReplicationStateChanged(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.type_ = input.readUInt32();
                        break;
                     case 18:
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 2;
                        this.viewId_ = bs;
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
         return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxNotice.GroupReplicationStateChanged.class, MysqlxNotice.GroupReplicationStateChanged.Builder.class);
      }

      @Override
      public boolean hasType() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public int getType() {
         return this.type_;
      }

      @Override
      public boolean hasViewId() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public String getViewId() {
         Object ref = this.viewId_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.viewId_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getViewIdBytes() {
         Object ref = this.viewId_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.viewId_ = b;
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
            output.writeUInt32(1, this.type_);
         }

         if ((this.bitField0_ & 2) != 0) {
            GeneratedMessageV3.writeString(output, 2, this.viewId_);
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
               size += CodedOutputStream.computeUInt32Size(1, this.type_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += GeneratedMessageV3.computeStringSize(2, this.viewId_);
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
         } else if (!(obj instanceof MysqlxNotice.GroupReplicationStateChanged)) {
            return super.equals(obj);
         } else {
            MysqlxNotice.GroupReplicationStateChanged other = (MysqlxNotice.GroupReplicationStateChanged)obj;
            if (this.hasType() != other.hasType()) {
               return false;
            } else if (this.hasType() && this.getType() != other.getType()) {
               return false;
            } else if (this.hasViewId() != other.hasViewId()) {
               return false;
            } else {
               return this.hasViewId() && !this.getViewId().equals(other.getViewId()) ? false : this.unknownFields.equals(other.unknownFields);
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
               hash = 53 * hash + this.getType();
            }

            if (this.hasViewId()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getViewId().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxNotice.GroupReplicationStateChanged parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.GroupReplicationStateChanged parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.GroupReplicationStateChanged parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.GroupReplicationStateChanged parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.GroupReplicationStateChanged parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.GroupReplicationStateChanged parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.GroupReplicationStateChanged parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxNotice.GroupReplicationStateChanged parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxNotice.GroupReplicationStateChanged parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxNotice.GroupReplicationStateChanged parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxNotice.GroupReplicationStateChanged parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxNotice.GroupReplicationStateChanged parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxNotice.GroupReplicationStateChanged.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxNotice.GroupReplicationStateChanged.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxNotice.GroupReplicationStateChanged.Builder newBuilder(MysqlxNotice.GroupReplicationStateChanged prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxNotice.GroupReplicationStateChanged.Builder toBuilder() {
         return this == DEFAULT_INSTANCE
            ? new MysqlxNotice.GroupReplicationStateChanged.Builder()
            : new MysqlxNotice.GroupReplicationStateChanged.Builder().mergeFrom(this);
      }

      protected MysqlxNotice.GroupReplicationStateChanged.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxNotice.GroupReplicationStateChanged.Builder(parent);
      }

      public static MysqlxNotice.GroupReplicationStateChanged getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxNotice.GroupReplicationStateChanged> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxNotice.GroupReplicationStateChanged> getParserForType() {
         return PARSER;
      }

      public MysqlxNotice.GroupReplicationStateChanged getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxNotice.GroupReplicationStateChanged.Builder>
         implements MysqlxNotice.GroupReplicationStateChangedOrBuilder {
         private int bitField0_;
         private int type_;
         private Object viewId_ = "";

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxNotice.GroupReplicationStateChanged.class, MysqlxNotice.GroupReplicationStateChanged.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxNotice.GroupReplicationStateChanged.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder clear() {
            super.clear();
            this.type_ = 0;
            this.bitField0_ &= -2;
            this.viewId_ = "";
            this.bitField0_ &= -3;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_GroupReplicationStateChanged_descriptor;
         }

         public MysqlxNotice.GroupReplicationStateChanged getDefaultInstanceForType() {
            return MysqlxNotice.GroupReplicationStateChanged.getDefaultInstance();
         }

         public MysqlxNotice.GroupReplicationStateChanged build() {
            MysqlxNotice.GroupReplicationStateChanged result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxNotice.GroupReplicationStateChanged buildPartial() {
            MysqlxNotice.GroupReplicationStateChanged result = new MysqlxNotice.GroupReplicationStateChanged(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.type_ = this.type_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.viewId_ = this.viewId_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder clone() {
            return (MysqlxNotice.GroupReplicationStateChanged.Builder)super.clone();
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxNotice.GroupReplicationStateChanged.Builder)super.setField(field, value);
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxNotice.GroupReplicationStateChanged.Builder)super.clearField(field);
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxNotice.GroupReplicationStateChanged.Builder)super.clearOneof(oneof);
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxNotice.GroupReplicationStateChanged.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxNotice.GroupReplicationStateChanged.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxNotice.GroupReplicationStateChanged) {
               return this.mergeFrom((MysqlxNotice.GroupReplicationStateChanged)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder mergeFrom(MysqlxNotice.GroupReplicationStateChanged other) {
            if (other == MysqlxNotice.GroupReplicationStateChanged.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasType()) {
                  this.setType(other.getType());
               }

               if (other.hasViewId()) {
                  this.bitField0_ |= 2;
                  this.viewId_ = other.viewId_;
                  this.onChanged();
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

         public MysqlxNotice.GroupReplicationStateChanged.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxNotice.GroupReplicationStateChanged parsedMessage = null;

            try {
               parsedMessage = MysqlxNotice.GroupReplicationStateChanged.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxNotice.GroupReplicationStateChanged)var8.getUnfinishedMessage();
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
         public int getType() {
            return this.type_;
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder setType(int value) {
            this.bitField0_ |= 1;
            this.type_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder clearType() {
            this.bitField0_ &= -2;
            this.type_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasViewId() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getViewId() {
            Object ref = this.viewId_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.viewId_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getViewIdBytes() {
            Object ref = this.viewId_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.viewId_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder setViewId(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.viewId_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder clearViewId() {
            this.bitField0_ &= -3;
            this.viewId_ = MysqlxNotice.GroupReplicationStateChanged.getDefaultInstance().getViewId();
            this.onChanged();
            return this;
         }

         public MysqlxNotice.GroupReplicationStateChanged.Builder setViewIdBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.viewId_ = value;
               this.onChanged();
               return this;
            }
         }

         public final MysqlxNotice.GroupReplicationStateChanged.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxNotice.GroupReplicationStateChanged.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxNotice.GroupReplicationStateChanged.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxNotice.GroupReplicationStateChanged.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static enum Type implements ProtocolMessageEnum {
         MEMBERSHIP_QUORUM_LOSS(1),
         MEMBERSHIP_VIEW_CHANGE(2),
         MEMBER_ROLE_CHANGE(3),
         MEMBER_STATE_CHANGE(4);

         public static final int MEMBERSHIP_QUORUM_LOSS_VALUE = 1;
         public static final int MEMBERSHIP_VIEW_CHANGE_VALUE = 2;
         public static final int MEMBER_ROLE_CHANGE_VALUE = 3;
         public static final int MEMBER_STATE_CHANGE_VALUE = 4;
         private static final Internal.EnumLiteMap<MysqlxNotice.GroupReplicationStateChanged.Type> internalValueMap = new Internal.EnumLiteMap<MysqlxNotice.GroupReplicationStateChanged.Type>() {
            public MysqlxNotice.GroupReplicationStateChanged.Type findValueByNumber(int number) {
               return MysqlxNotice.GroupReplicationStateChanged.Type.forNumber(number);
            }
         };
         private static final MysqlxNotice.GroupReplicationStateChanged.Type[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static MysqlxNotice.GroupReplicationStateChanged.Type valueOf(int value) {
            return forNumber(value);
         }

         public static MysqlxNotice.GroupReplicationStateChanged.Type forNumber(int value) {
            switch (value) {
               case 1:
                  return MEMBERSHIP_QUORUM_LOSS;
               case 2:
                  return MEMBERSHIP_VIEW_CHANGE;
               case 3:
                  return MEMBER_ROLE_CHANGE;
               case 4:
                  return MEMBER_STATE_CHANGE;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<MysqlxNotice.GroupReplicationStateChanged.Type> internalGetValueMap() {
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
            return MysqlxNotice.GroupReplicationStateChanged.getDescriptor().getEnumTypes().get(0);
         }

         public static MysqlxNotice.GroupReplicationStateChanged.Type valueOf(Descriptors.EnumValueDescriptor desc) {
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

   public interface GroupReplicationStateChangedOrBuilder extends MessageOrBuilder {
      boolean hasType();

      int getType();

      boolean hasViewId();

      String getViewId();

      ByteString getViewIdBytes();
   }

   public static final class ServerHello extends GeneratedMessageV3 implements MysqlxNotice.ServerHelloOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxNotice.ServerHello DEFAULT_INSTANCE = new MysqlxNotice.ServerHello();
      @Deprecated
      public static final Parser<MysqlxNotice.ServerHello> PARSER = new AbstractParser<MysqlxNotice.ServerHello>() {
         public MysqlxNotice.ServerHello parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxNotice.ServerHello(input, extensionRegistry);
         }
      };

      private ServerHello(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private ServerHello() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxNotice.ServerHello();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private ServerHello(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxNotice.ServerHello.class, MysqlxNotice.ServerHello.Builder.class);
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
         } else if (!(obj instanceof MysqlxNotice.ServerHello)) {
            return super.equals(obj);
         } else {
            MysqlxNotice.ServerHello other = (MysqlxNotice.ServerHello)obj;
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

      public static MysqlxNotice.ServerHello parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.ServerHello parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.ServerHello parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.ServerHello parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.ServerHello parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.ServerHello parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.ServerHello parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxNotice.ServerHello parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxNotice.ServerHello parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxNotice.ServerHello parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxNotice.ServerHello parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxNotice.ServerHello parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxNotice.ServerHello.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxNotice.ServerHello.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxNotice.ServerHello.Builder newBuilder(MysqlxNotice.ServerHello prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxNotice.ServerHello.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxNotice.ServerHello.Builder() : new MysqlxNotice.ServerHello.Builder().mergeFrom(this);
      }

      protected MysqlxNotice.ServerHello.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxNotice.ServerHello.Builder(parent);
      }

      public static MysqlxNotice.ServerHello getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxNotice.ServerHello> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxNotice.ServerHello> getParserForType() {
         return PARSER;
      }

      public MysqlxNotice.ServerHello getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxNotice.ServerHello.Builder> implements MysqlxNotice.ServerHelloOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxNotice.ServerHello.class, MysqlxNotice.ServerHello.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxNotice.ServerHello.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxNotice.ServerHello.Builder clear() {
            super.clear();
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_ServerHello_descriptor;
         }

         public MysqlxNotice.ServerHello getDefaultInstanceForType() {
            return MysqlxNotice.ServerHello.getDefaultInstance();
         }

         public MysqlxNotice.ServerHello build() {
            MysqlxNotice.ServerHello result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxNotice.ServerHello buildPartial() {
            MysqlxNotice.ServerHello result = new MysqlxNotice.ServerHello(this);
            this.onBuilt();
            return result;
         }

         public MysqlxNotice.ServerHello.Builder clone() {
            return (MysqlxNotice.ServerHello.Builder)super.clone();
         }

         public MysqlxNotice.ServerHello.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxNotice.ServerHello.Builder)super.setField(field, value);
         }

         public MysqlxNotice.ServerHello.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxNotice.ServerHello.Builder)super.clearField(field);
         }

         public MysqlxNotice.ServerHello.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxNotice.ServerHello.Builder)super.clearOneof(oneof);
         }

         public MysqlxNotice.ServerHello.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxNotice.ServerHello.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxNotice.ServerHello.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxNotice.ServerHello.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxNotice.ServerHello.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxNotice.ServerHello) {
               return this.mergeFrom((MysqlxNotice.ServerHello)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxNotice.ServerHello.Builder mergeFrom(MysqlxNotice.ServerHello other) {
            if (other == MysqlxNotice.ServerHello.getDefaultInstance()) {
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

         public MysqlxNotice.ServerHello.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxNotice.ServerHello parsedMessage = null;

            try {
               parsedMessage = MysqlxNotice.ServerHello.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxNotice.ServerHello)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         public final MysqlxNotice.ServerHello.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxNotice.ServerHello.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxNotice.ServerHello.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxNotice.ServerHello.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface ServerHelloOrBuilder extends MessageOrBuilder {
   }

   public static final class SessionStateChanged extends GeneratedMessageV3 implements MysqlxNotice.SessionStateChangedOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int PARAM_FIELD_NUMBER = 1;
      private int param_;
      public static final int VALUE_FIELD_NUMBER = 2;
      private List<MysqlxDatatypes.Scalar> value_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxNotice.SessionStateChanged DEFAULT_INSTANCE = new MysqlxNotice.SessionStateChanged();
      @Deprecated
      public static final Parser<MysqlxNotice.SessionStateChanged> PARSER = new AbstractParser<MysqlxNotice.SessionStateChanged>() {
         public MysqlxNotice.SessionStateChanged parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxNotice.SessionStateChanged(input, extensionRegistry);
         }
      };

      private SessionStateChanged(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private SessionStateChanged() {
         this.param_ = 1;
         this.value_ = Collections.emptyList();
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxNotice.SessionStateChanged();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private SessionStateChanged(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        MysqlxNotice.SessionStateChanged.Parameter value = MysqlxNotice.SessionStateChanged.Parameter.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(1, rawValue);
                        } else {
                           this.bitField0_ |= 1;
                           this.param_ = rawValue;
                        }
                        break;
                     case 18:
                        if ((mutable_bitField0_ & 2) == 0) {
                           this.value_ = new ArrayList<>();
                           mutable_bitField0_ |= 2;
                        }

                        this.value_.add(input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry));
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
                  this.value_ = Collections.unmodifiableList(this.value_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxNotice.SessionStateChanged.class, MysqlxNotice.SessionStateChanged.Builder.class);
      }

      @Override
      public boolean hasParam() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxNotice.SessionStateChanged.Parameter getParam() {
         MysqlxNotice.SessionStateChanged.Parameter result = MysqlxNotice.SessionStateChanged.Parameter.valueOf(this.param_);
         return result == null ? MysqlxNotice.SessionStateChanged.Parameter.CURRENT_SCHEMA : result;
      }

      @Override
      public List<MysqlxDatatypes.Scalar> getValueList() {
         return this.value_;
      }

      @Override
      public List<? extends MysqlxDatatypes.ScalarOrBuilder> getValueOrBuilderList() {
         return this.value_;
      }

      @Override
      public int getValueCount() {
         return this.value_.size();
      }

      @Override
      public MysqlxDatatypes.Scalar getValue(int index) {
         return this.value_.get(index);
      }

      @Override
      public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder(int index) {
         return this.value_.get(index);
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasParam()) {
            this.memoizedIsInitialized = 0;
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
         if ((this.bitField0_ & 1) != 0) {
            output.writeEnum(1, this.param_);
         }

         for (int i = 0; i < this.value_.size(); i++) {
            output.writeMessage(2, this.value_.get(i));
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
               size += CodedOutputStream.computeEnumSize(1, this.param_);
            }

            for (int i = 0; i < this.value_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(2, this.value_.get(i));
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
         } else if (!(obj instanceof MysqlxNotice.SessionStateChanged)) {
            return super.equals(obj);
         } else {
            MysqlxNotice.SessionStateChanged other = (MysqlxNotice.SessionStateChanged)obj;
            if (this.hasParam() != other.hasParam()) {
               return false;
            } else if (this.hasParam() && this.param_ != other.param_) {
               return false;
            } else {
               return !this.getValueList().equals(other.getValueList()) ? false : this.unknownFields.equals(other.unknownFields);
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
            if (this.hasParam()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.param_;
            }

            if (this.getValueCount() > 0) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getValueList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxNotice.SessionStateChanged parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.SessionStateChanged parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.SessionStateChanged parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.SessionStateChanged parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.SessionStateChanged parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.SessionStateChanged parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.SessionStateChanged parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxNotice.SessionStateChanged parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxNotice.SessionStateChanged parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxNotice.SessionStateChanged parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxNotice.SessionStateChanged parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxNotice.SessionStateChanged parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxNotice.SessionStateChanged.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxNotice.SessionStateChanged.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxNotice.SessionStateChanged.Builder newBuilder(MysqlxNotice.SessionStateChanged prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxNotice.SessionStateChanged.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxNotice.SessionStateChanged.Builder() : new MysqlxNotice.SessionStateChanged.Builder().mergeFrom(this);
      }

      protected MysqlxNotice.SessionStateChanged.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxNotice.SessionStateChanged.Builder(parent);
      }

      public static MysqlxNotice.SessionStateChanged getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxNotice.SessionStateChanged> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxNotice.SessionStateChanged> getParserForType() {
         return PARSER;
      }

      public MysqlxNotice.SessionStateChanged getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxNotice.SessionStateChanged.Builder>
         implements MysqlxNotice.SessionStateChangedOrBuilder {
         private int bitField0_;
         private int param_ = 1;
         private List<MysqlxDatatypes.Scalar> value_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> valueBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxNotice.SessionStateChanged.class, MysqlxNotice.SessionStateChanged.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxNotice.SessionStateChanged.alwaysUseFieldBuilders) {
               this.getValueFieldBuilder();
            }
         }

         public MysqlxNotice.SessionStateChanged.Builder clear() {
            super.clear();
            this.param_ = 1;
            this.bitField0_ &= -2;
            if (this.valueBuilder_ == null) {
               this.value_ = Collections.emptyList();
               this.bitField0_ &= -3;
            } else {
               this.valueBuilder_.clear();
            }

            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_SessionStateChanged_descriptor;
         }

         public MysqlxNotice.SessionStateChanged getDefaultInstanceForType() {
            return MysqlxNotice.SessionStateChanged.getDefaultInstance();
         }

         public MysqlxNotice.SessionStateChanged build() {
            MysqlxNotice.SessionStateChanged result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxNotice.SessionStateChanged buildPartial() {
            MysqlxNotice.SessionStateChanged result = new MysqlxNotice.SessionStateChanged(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.param_ = this.param_;
            if (this.valueBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0) {
                  this.value_ = Collections.unmodifiableList(this.value_);
                  this.bitField0_ &= -3;
               }

               result.value_ = this.value_;
            } else {
               result.value_ = this.valueBuilder_.build();
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxNotice.SessionStateChanged.Builder clone() {
            return (MysqlxNotice.SessionStateChanged.Builder)super.clone();
         }

         public MysqlxNotice.SessionStateChanged.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxNotice.SessionStateChanged.Builder)super.setField(field, value);
         }

         public MysqlxNotice.SessionStateChanged.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxNotice.SessionStateChanged.Builder)super.clearField(field);
         }

         public MysqlxNotice.SessionStateChanged.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxNotice.SessionStateChanged.Builder)super.clearOneof(oneof);
         }

         public MysqlxNotice.SessionStateChanged.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxNotice.SessionStateChanged.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxNotice.SessionStateChanged.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxNotice.SessionStateChanged.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxNotice.SessionStateChanged.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxNotice.SessionStateChanged) {
               return this.mergeFrom((MysqlxNotice.SessionStateChanged)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxNotice.SessionStateChanged.Builder mergeFrom(MysqlxNotice.SessionStateChanged other) {
            if (other == MysqlxNotice.SessionStateChanged.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasParam()) {
                  this.setParam(other.getParam());
               }

               if (this.valueBuilder_ == null) {
                  if (!other.value_.isEmpty()) {
                     if (this.value_.isEmpty()) {
                        this.value_ = other.value_;
                        this.bitField0_ &= -3;
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
                     this.bitField0_ &= -3;
                     this.valueBuilder_ = MysqlxNotice.SessionStateChanged.alwaysUseFieldBuilders ? this.getValueFieldBuilder() : null;
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
            if (!this.hasParam()) {
               return false;
            } else {
               for (int i = 0; i < this.getValueCount(); i++) {
                  if (!this.getValue(i).isInitialized()) {
                     return false;
                  }
               }

               return true;
            }
         }

         public MysqlxNotice.SessionStateChanged.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxNotice.SessionStateChanged parsedMessage = null;

            try {
               parsedMessage = MysqlxNotice.SessionStateChanged.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxNotice.SessionStateChanged)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasParam() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxNotice.SessionStateChanged.Parameter getParam() {
            MysqlxNotice.SessionStateChanged.Parameter result = MysqlxNotice.SessionStateChanged.Parameter.valueOf(this.param_);
            return result == null ? MysqlxNotice.SessionStateChanged.Parameter.CURRENT_SCHEMA : result;
         }

         public MysqlxNotice.SessionStateChanged.Builder setParam(MysqlxNotice.SessionStateChanged.Parameter value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.param_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxNotice.SessionStateChanged.Builder clearParam() {
            this.bitField0_ &= -2;
            this.param_ = 1;
            this.onChanged();
            return this;
         }

         private void ensureValueIsMutable() {
            if ((this.bitField0_ & 2) == 0) {
               this.value_ = new ArrayList<>(this.value_);
               this.bitField0_ |= 2;
            }
         }

         @Override
         public List<MysqlxDatatypes.Scalar> getValueList() {
            return this.valueBuilder_ == null ? Collections.unmodifiableList(this.value_) : this.valueBuilder_.getMessageList();
         }

         @Override
         public int getValueCount() {
            return this.valueBuilder_ == null ? this.value_.size() : this.valueBuilder_.getCount();
         }

         @Override
         public MysqlxDatatypes.Scalar getValue(int index) {
            return this.valueBuilder_ == null ? this.value_.get(index) : this.valueBuilder_.getMessage(index);
         }

         public MysqlxNotice.SessionStateChanged.Builder setValue(int index, MysqlxDatatypes.Scalar value) {
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

         public MysqlxNotice.SessionStateChanged.Builder setValue(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               this.value_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.valueBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxNotice.SessionStateChanged.Builder addValue(MysqlxDatatypes.Scalar value) {
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

         public MysqlxNotice.SessionStateChanged.Builder addValue(int index, MysqlxDatatypes.Scalar value) {
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

         public MysqlxNotice.SessionStateChanged.Builder addValue(MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               this.value_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.valueBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxNotice.SessionStateChanged.Builder addValue(int index, MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               this.value_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.valueBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxNotice.SessionStateChanged.Builder addAllValue(Iterable<? extends MysqlxDatatypes.Scalar> values) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.value_);
               this.onChanged();
            } else {
               this.valueBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxNotice.SessionStateChanged.Builder clearValue() {
            if (this.valueBuilder_ == null) {
               this.value_ = Collections.emptyList();
               this.bitField0_ &= -3;
               this.onChanged();
            } else {
               this.valueBuilder_.clear();
            }

            return this;
         }

         public MysqlxNotice.SessionStateChanged.Builder removeValue(int index) {
            if (this.valueBuilder_ == null) {
               this.ensureValueIsMutable();
               this.value_.remove(index);
               this.onChanged();
            } else {
               this.valueBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxDatatypes.Scalar.Builder getValueBuilder(int index) {
            return this.getValueFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder(int index) {
            return this.valueBuilder_ == null ? this.value_.get(index) : this.valueBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxDatatypes.ScalarOrBuilder> getValueOrBuilderList() {
            return this.valueBuilder_ != null ? this.valueBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.value_);
         }

         public MysqlxDatatypes.Scalar.Builder addValueBuilder() {
            return this.getValueFieldBuilder().addBuilder(MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public MysqlxDatatypes.Scalar.Builder addValueBuilder(int index) {
            return this.getValueFieldBuilder().addBuilder(index, MysqlxDatatypes.Scalar.getDefaultInstance());
         }

         public List<MysqlxDatatypes.Scalar.Builder> getValueBuilderList() {
            return this.getValueFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getValueFieldBuilder() {
            if (this.valueBuilder_ == null) {
               this.valueBuilder_ = new RepeatedFieldBuilderV3<>(this.value_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
               this.value_ = null;
            }

            return this.valueBuilder_;
         }

         public final MysqlxNotice.SessionStateChanged.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxNotice.SessionStateChanged.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxNotice.SessionStateChanged.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxNotice.SessionStateChanged.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static enum Parameter implements ProtocolMessageEnum {
         CURRENT_SCHEMA(1),
         ACCOUNT_EXPIRED(2),
         GENERATED_INSERT_ID(3),
         ROWS_AFFECTED(4),
         ROWS_FOUND(5),
         ROWS_MATCHED(6),
         TRX_COMMITTED(7),
         TRX_ROLLEDBACK(9),
         PRODUCED_MESSAGE(10),
         CLIENT_ID_ASSIGNED(11),
         GENERATED_DOCUMENT_IDS(12);

         public static final int CURRENT_SCHEMA_VALUE = 1;
         public static final int ACCOUNT_EXPIRED_VALUE = 2;
         public static final int GENERATED_INSERT_ID_VALUE = 3;
         public static final int ROWS_AFFECTED_VALUE = 4;
         public static final int ROWS_FOUND_VALUE = 5;
         public static final int ROWS_MATCHED_VALUE = 6;
         public static final int TRX_COMMITTED_VALUE = 7;
         public static final int TRX_ROLLEDBACK_VALUE = 9;
         public static final int PRODUCED_MESSAGE_VALUE = 10;
         public static final int CLIENT_ID_ASSIGNED_VALUE = 11;
         public static final int GENERATED_DOCUMENT_IDS_VALUE = 12;
         private static final Internal.EnumLiteMap<MysqlxNotice.SessionStateChanged.Parameter> internalValueMap = new Internal.EnumLiteMap<MysqlxNotice.SessionStateChanged.Parameter>() {
            public MysqlxNotice.SessionStateChanged.Parameter findValueByNumber(int number) {
               return MysqlxNotice.SessionStateChanged.Parameter.forNumber(number);
            }
         };
         private static final MysqlxNotice.SessionStateChanged.Parameter[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static MysqlxNotice.SessionStateChanged.Parameter valueOf(int value) {
            return forNumber(value);
         }

         public static MysqlxNotice.SessionStateChanged.Parameter forNumber(int value) {
            switch (value) {
               case 1:
                  return CURRENT_SCHEMA;
               case 2:
                  return ACCOUNT_EXPIRED;
               case 3:
                  return GENERATED_INSERT_ID;
               case 4:
                  return ROWS_AFFECTED;
               case 5:
                  return ROWS_FOUND;
               case 6:
                  return ROWS_MATCHED;
               case 7:
                  return TRX_COMMITTED;
               case 8:
               default:
                  return null;
               case 9:
                  return TRX_ROLLEDBACK;
               case 10:
                  return PRODUCED_MESSAGE;
               case 11:
                  return CLIENT_ID_ASSIGNED;
               case 12:
                  return GENERATED_DOCUMENT_IDS;
            }
         }

         public static Internal.EnumLiteMap<MysqlxNotice.SessionStateChanged.Parameter> internalGetValueMap() {
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
            return MysqlxNotice.SessionStateChanged.getDescriptor().getEnumTypes().get(0);
         }

         public static MysqlxNotice.SessionStateChanged.Parameter valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Parameter(int value) {
            this.value = value;
         }
      }
   }

   public interface SessionStateChangedOrBuilder extends MessageOrBuilder {
      boolean hasParam();

      MysqlxNotice.SessionStateChanged.Parameter getParam();

      List<MysqlxDatatypes.Scalar> getValueList();

      MysqlxDatatypes.Scalar getValue(int var1);

      int getValueCount();

      List<? extends MysqlxDatatypes.ScalarOrBuilder> getValueOrBuilderList();

      MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder(int var1);
   }

   public static final class SessionVariableChanged extends GeneratedMessageV3 implements MysqlxNotice.SessionVariableChangedOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int PARAM_FIELD_NUMBER = 1;
      private volatile Object param_;
      public static final int VALUE_FIELD_NUMBER = 2;
      private MysqlxDatatypes.Scalar value_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxNotice.SessionVariableChanged DEFAULT_INSTANCE = new MysqlxNotice.SessionVariableChanged();
      @Deprecated
      public static final Parser<MysqlxNotice.SessionVariableChanged> PARSER = new AbstractParser<MysqlxNotice.SessionVariableChanged>() {
         public MysqlxNotice.SessionVariableChanged parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxNotice.SessionVariableChanged(input, extensionRegistry);
         }
      };

      private SessionVariableChanged(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private SessionVariableChanged() {
         this.param_ = "";
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxNotice.SessionVariableChanged();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private SessionVariableChanged(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.param_ = bs;
                        break;
                     case 18:
                        MysqlxDatatypes.Scalar.Builder subBuilder = null;
                        if ((this.bitField0_ & 2) != 0) {
                           subBuilder = this.value_.toBuilder();
                        }

                        this.value_ = input.readMessage(MysqlxDatatypes.Scalar.PARSER, extensionRegistry);
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
         return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxNotice.SessionVariableChanged.class, MysqlxNotice.SessionVariableChanged.Builder.class);
      }

      @Override
      public boolean hasParam() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getParam() {
         Object ref = this.param_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.param_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getParamBytes() {
         Object ref = this.param_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.param_ = b;
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
      public MysqlxDatatypes.Scalar getValue() {
         return this.value_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_;
      }

      @Override
      public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder() {
         return this.value_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasParam()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (this.hasValue() && !this.getValue().isInitialized()) {
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
            GeneratedMessageV3.writeString(output, 1, this.param_);
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
               size += GeneratedMessageV3.computeStringSize(1, this.param_);
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
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxNotice.SessionVariableChanged)) {
            return super.equals(obj);
         } else {
            MysqlxNotice.SessionVariableChanged other = (MysqlxNotice.SessionVariableChanged)obj;
            if (this.hasParam() != other.hasParam()) {
               return false;
            } else if (this.hasParam() && !this.getParam().equals(other.getParam())) {
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
            if (this.hasParam()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getParam().hashCode();
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

      public static MysqlxNotice.SessionVariableChanged parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.SessionVariableChanged parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.SessionVariableChanged parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.SessionVariableChanged parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.SessionVariableChanged parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.SessionVariableChanged parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.SessionVariableChanged parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxNotice.SessionVariableChanged parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxNotice.SessionVariableChanged parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxNotice.SessionVariableChanged parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxNotice.SessionVariableChanged parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxNotice.SessionVariableChanged parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxNotice.SessionVariableChanged.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxNotice.SessionVariableChanged.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxNotice.SessionVariableChanged.Builder newBuilder(MysqlxNotice.SessionVariableChanged prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxNotice.SessionVariableChanged.Builder toBuilder() {
         return this == DEFAULT_INSTANCE
            ? new MysqlxNotice.SessionVariableChanged.Builder()
            : new MysqlxNotice.SessionVariableChanged.Builder().mergeFrom(this);
      }

      protected MysqlxNotice.SessionVariableChanged.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxNotice.SessionVariableChanged.Builder(parent);
      }

      public static MysqlxNotice.SessionVariableChanged getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxNotice.SessionVariableChanged> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxNotice.SessionVariableChanged> getParserForType() {
         return PARSER;
      }

      public MysqlxNotice.SessionVariableChanged getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxNotice.SessionVariableChanged.Builder>
         implements MysqlxNotice.SessionVariableChangedOrBuilder {
         private int bitField0_;
         private Object param_ = "";
         private MysqlxDatatypes.Scalar value_;
         private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> valueBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxNotice.SessionVariableChanged.class, MysqlxNotice.SessionVariableChanged.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxNotice.SessionVariableChanged.alwaysUseFieldBuilders) {
               this.getValueFieldBuilder();
            }
         }

         public MysqlxNotice.SessionVariableChanged.Builder clear() {
            super.clear();
            this.param_ = "";
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
            return MysqlxNotice.internal_static_Mysqlx_Notice_SessionVariableChanged_descriptor;
         }

         public MysqlxNotice.SessionVariableChanged getDefaultInstanceForType() {
            return MysqlxNotice.SessionVariableChanged.getDefaultInstance();
         }

         public MysqlxNotice.SessionVariableChanged build() {
            MysqlxNotice.SessionVariableChanged result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxNotice.SessionVariableChanged buildPartial() {
            MysqlxNotice.SessionVariableChanged result = new MysqlxNotice.SessionVariableChanged(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.param_ = this.param_;
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

         public MysqlxNotice.SessionVariableChanged.Builder clone() {
            return (MysqlxNotice.SessionVariableChanged.Builder)super.clone();
         }

         public MysqlxNotice.SessionVariableChanged.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxNotice.SessionVariableChanged.Builder)super.setField(field, value);
         }

         public MysqlxNotice.SessionVariableChanged.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxNotice.SessionVariableChanged.Builder)super.clearField(field);
         }

         public MysqlxNotice.SessionVariableChanged.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxNotice.SessionVariableChanged.Builder)super.clearOneof(oneof);
         }

         public MysqlxNotice.SessionVariableChanged.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxNotice.SessionVariableChanged.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxNotice.SessionVariableChanged.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxNotice.SessionVariableChanged.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxNotice.SessionVariableChanged.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxNotice.SessionVariableChanged) {
               return this.mergeFrom((MysqlxNotice.SessionVariableChanged)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxNotice.SessionVariableChanged.Builder mergeFrom(MysqlxNotice.SessionVariableChanged other) {
            if (other == MysqlxNotice.SessionVariableChanged.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasParam()) {
                  this.bitField0_ |= 1;
                  this.param_ = other.param_;
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
            return !this.hasParam() ? false : !this.hasValue() || this.getValue().isInitialized();
         }

         public MysqlxNotice.SessionVariableChanged.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxNotice.SessionVariableChanged parsedMessage = null;

            try {
               parsedMessage = MysqlxNotice.SessionVariableChanged.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxNotice.SessionVariableChanged)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasParam() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getParam() {
            Object ref = this.param_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.param_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getParamBytes() {
            Object ref = this.param_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.param_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxNotice.SessionVariableChanged.Builder setParam(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.param_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxNotice.SessionVariableChanged.Builder clearParam() {
            this.bitField0_ &= -2;
            this.param_ = MysqlxNotice.SessionVariableChanged.getDefaultInstance().getParam();
            this.onChanged();
            return this;
         }

         public MysqlxNotice.SessionVariableChanged.Builder setParamBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.param_ = value;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasValue() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxDatatypes.Scalar getValue() {
            if (this.valueBuilder_ == null) {
               return this.value_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_;
            } else {
               return this.valueBuilder_.getMessage();
            }
         }

         public MysqlxNotice.SessionVariableChanged.Builder setValue(MysqlxDatatypes.Scalar value) {
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

         public MysqlxNotice.SessionVariableChanged.Builder setValue(MysqlxDatatypes.Scalar.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.value_ = builderForValue.build();
               this.onChanged();
            } else {
               this.valueBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 2;
            return this;
         }

         public MysqlxNotice.SessionVariableChanged.Builder mergeValue(MysqlxDatatypes.Scalar value) {
            if (this.valueBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0 && this.value_ != null && this.value_ != MysqlxDatatypes.Scalar.getDefaultInstance()) {
                  this.value_ = MysqlxDatatypes.Scalar.newBuilder(this.value_).mergeFrom(value).buildPartial();
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

         public MysqlxNotice.SessionVariableChanged.Builder clearValue() {
            if (this.valueBuilder_ == null) {
               this.value_ = null;
               this.onChanged();
            } else {
               this.valueBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         public MysqlxDatatypes.Scalar.Builder getValueBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return this.getValueFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder() {
            if (this.valueBuilder_ != null) {
               return this.valueBuilder_.getMessageOrBuilder();
            } else {
               return this.value_ == null ? MysqlxDatatypes.Scalar.getDefaultInstance() : this.value_;
            }
         }

         private SingleFieldBuilderV3<MysqlxDatatypes.Scalar, MysqlxDatatypes.Scalar.Builder, MysqlxDatatypes.ScalarOrBuilder> getValueFieldBuilder() {
            if (this.valueBuilder_ == null) {
               this.valueBuilder_ = new SingleFieldBuilderV3<>(this.getValue(), this.getParentForChildren(), this.isClean());
               this.value_ = null;
            }

            return this.valueBuilder_;
         }

         public final MysqlxNotice.SessionVariableChanged.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxNotice.SessionVariableChanged.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxNotice.SessionVariableChanged.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxNotice.SessionVariableChanged.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface SessionVariableChangedOrBuilder extends MessageOrBuilder {
      boolean hasParam();

      String getParam();

      ByteString getParamBytes();

      boolean hasValue();

      MysqlxDatatypes.Scalar getValue();

      MysqlxDatatypes.ScalarOrBuilder getValueOrBuilder();
   }

   public static final class Warning extends GeneratedMessageV3 implements MysqlxNotice.WarningOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int LEVEL_FIELD_NUMBER = 1;
      private int level_;
      public static final int CODE_FIELD_NUMBER = 2;
      private int code_;
      public static final int MSG_FIELD_NUMBER = 3;
      private volatile Object msg_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxNotice.Warning DEFAULT_INSTANCE = new MysqlxNotice.Warning();
      @Deprecated
      public static final Parser<MysqlxNotice.Warning> PARSER = new AbstractParser<MysqlxNotice.Warning>() {
         public MysqlxNotice.Warning parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxNotice.Warning(input, extensionRegistry);
         }
      };

      private Warning(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Warning() {
         this.level_ = 2;
         this.msg_ = "";
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxNotice.Warning();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Warning(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        MysqlxNotice.Warning.Level value = MysqlxNotice.Warning.Level.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(1, rawValue);
                        } else {
                           this.bitField0_ |= 1;
                           this.level_ = rawValue;
                        }
                        break;
                     case 16:
                        this.bitField0_ |= 2;
                        this.code_ = input.readUInt32();
                        break;
                     case 26:
                        ByteString bs = input.readBytes();
                        this.bitField0_ |= 4;
                        this.msg_ = bs;
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
         return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxNotice.Warning.class, MysqlxNotice.Warning.Builder.class);
      }

      @Override
      public boolean hasLevel() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxNotice.Warning.Level getLevel() {
         MysqlxNotice.Warning.Level result = MysqlxNotice.Warning.Level.valueOf(this.level_);
         return result == null ? MysqlxNotice.Warning.Level.WARNING : result;
      }

      @Override
      public boolean hasCode() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public int getCode() {
         return this.code_;
      }

      @Override
      public boolean hasMsg() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public String getMsg() {
         Object ref = this.msg_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.msg_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getMsgBytes() {
         Object ref = this.msg_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.msg_ = b;
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
         } else if (!this.hasCode()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.hasMsg()) {
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
            output.writeEnum(1, this.level_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeUInt32(2, this.code_);
         }

         if ((this.bitField0_ & 4) != 0) {
            GeneratedMessageV3.writeString(output, 3, this.msg_);
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
               size += CodedOutputStream.computeEnumSize(1, this.level_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeUInt32Size(2, this.code_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += GeneratedMessageV3.computeStringSize(3, this.msg_);
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
         } else if (!(obj instanceof MysqlxNotice.Warning)) {
            return super.equals(obj);
         } else {
            MysqlxNotice.Warning other = (MysqlxNotice.Warning)obj;
            if (this.hasLevel() != other.hasLevel()) {
               return false;
            } else if (this.hasLevel() && this.level_ != other.level_) {
               return false;
            } else if (this.hasCode() != other.hasCode()) {
               return false;
            } else if (this.hasCode() && this.getCode() != other.getCode()) {
               return false;
            } else if (this.hasMsg() != other.hasMsg()) {
               return false;
            } else {
               return this.hasMsg() && !this.getMsg().equals(other.getMsg()) ? false : this.unknownFields.equals(other.unknownFields);
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
            if (this.hasLevel()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.level_;
            }

            if (this.hasCode()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getCode();
            }

            if (this.hasMsg()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getMsg().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxNotice.Warning parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.Warning parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.Warning parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.Warning parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.Warning parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxNotice.Warning parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxNotice.Warning parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxNotice.Warning parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxNotice.Warning parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxNotice.Warning parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxNotice.Warning parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxNotice.Warning parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxNotice.Warning.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxNotice.Warning.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxNotice.Warning.Builder newBuilder(MysqlxNotice.Warning prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxNotice.Warning.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxNotice.Warning.Builder() : new MysqlxNotice.Warning.Builder().mergeFrom(this);
      }

      protected MysqlxNotice.Warning.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxNotice.Warning.Builder(parent);
      }

      public static MysqlxNotice.Warning getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxNotice.Warning> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxNotice.Warning> getParserForType() {
         return PARSER;
      }

      public MysqlxNotice.Warning getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxNotice.Warning.Builder> implements MysqlxNotice.WarningOrBuilder {
         private int bitField0_;
         private int level_ = 2;
         private int code_;
         private Object msg_ = "";

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxNotice.Warning.class, MysqlxNotice.Warning.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxNotice.Warning.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxNotice.Warning.Builder clear() {
            super.clear();
            this.level_ = 2;
            this.bitField0_ &= -2;
            this.code_ = 0;
            this.bitField0_ &= -3;
            this.msg_ = "";
            this.bitField0_ &= -5;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxNotice.internal_static_Mysqlx_Notice_Warning_descriptor;
         }

         public MysqlxNotice.Warning getDefaultInstanceForType() {
            return MysqlxNotice.Warning.getDefaultInstance();
         }

         public MysqlxNotice.Warning build() {
            MysqlxNotice.Warning result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxNotice.Warning buildPartial() {
            MysqlxNotice.Warning result = new MysqlxNotice.Warning(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.level_ = this.level_;
            if ((from_bitField0_ & 2) != 0) {
               result.code_ = this.code_;
               to_bitField0_ |= 2;
            }

            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.msg_ = this.msg_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxNotice.Warning.Builder clone() {
            return (MysqlxNotice.Warning.Builder)super.clone();
         }

         public MysqlxNotice.Warning.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxNotice.Warning.Builder)super.setField(field, value);
         }

         public MysqlxNotice.Warning.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxNotice.Warning.Builder)super.clearField(field);
         }

         public MysqlxNotice.Warning.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxNotice.Warning.Builder)super.clearOneof(oneof);
         }

         public MysqlxNotice.Warning.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxNotice.Warning.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxNotice.Warning.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxNotice.Warning.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxNotice.Warning.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxNotice.Warning) {
               return this.mergeFrom((MysqlxNotice.Warning)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxNotice.Warning.Builder mergeFrom(MysqlxNotice.Warning other) {
            if (other == MysqlxNotice.Warning.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasLevel()) {
                  this.setLevel(other.getLevel());
               }

               if (other.hasCode()) {
                  this.setCode(other.getCode());
               }

               if (other.hasMsg()) {
                  this.bitField0_ |= 4;
                  this.msg_ = other.msg_;
                  this.onChanged();
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return !this.hasCode() ? false : this.hasMsg();
         }

         public MysqlxNotice.Warning.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxNotice.Warning parsedMessage = null;

            try {
               parsedMessage = MysqlxNotice.Warning.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxNotice.Warning)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasLevel() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxNotice.Warning.Level getLevel() {
            MysqlxNotice.Warning.Level result = MysqlxNotice.Warning.Level.valueOf(this.level_);
            return result == null ? MysqlxNotice.Warning.Level.WARNING : result;
         }

         public MysqlxNotice.Warning.Builder setLevel(MysqlxNotice.Warning.Level value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.level_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxNotice.Warning.Builder clearLevel() {
            this.bitField0_ &= -2;
            this.level_ = 2;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasCode() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public int getCode() {
            return this.code_;
         }

         public MysqlxNotice.Warning.Builder setCode(int value) {
            this.bitField0_ |= 2;
            this.code_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxNotice.Warning.Builder clearCode() {
            this.bitField0_ &= -3;
            this.code_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasMsg() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public String getMsg() {
            Object ref = this.msg_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.msg_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getMsgBytes() {
            Object ref = this.msg_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.msg_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxNotice.Warning.Builder setMsg(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.msg_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxNotice.Warning.Builder clearMsg() {
            this.bitField0_ &= -5;
            this.msg_ = MysqlxNotice.Warning.getDefaultInstance().getMsg();
            this.onChanged();
            return this;
         }

         public MysqlxNotice.Warning.Builder setMsgBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.msg_ = value;
               this.onChanged();
               return this;
            }
         }

         public final MysqlxNotice.Warning.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxNotice.Warning.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxNotice.Warning.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxNotice.Warning.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static enum Level implements ProtocolMessageEnum {
         NOTE(1),
         WARNING(2),
         ERROR(3);

         public static final int NOTE_VALUE = 1;
         public static final int WARNING_VALUE = 2;
         public static final int ERROR_VALUE = 3;
         private static final Internal.EnumLiteMap<MysqlxNotice.Warning.Level> internalValueMap = new Internal.EnumLiteMap<MysqlxNotice.Warning.Level>() {
            public MysqlxNotice.Warning.Level findValueByNumber(int number) {
               return MysqlxNotice.Warning.Level.forNumber(number);
            }
         };
         private static final MysqlxNotice.Warning.Level[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static MysqlxNotice.Warning.Level valueOf(int value) {
            return forNumber(value);
         }

         public static MysqlxNotice.Warning.Level forNumber(int value) {
            switch (value) {
               case 1:
                  return NOTE;
               case 2:
                  return WARNING;
               case 3:
                  return ERROR;
               default:
                  return null;
            }
         }

         public static Internal.EnumLiteMap<MysqlxNotice.Warning.Level> internalGetValueMap() {
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
            return MysqlxNotice.Warning.getDescriptor().getEnumTypes().get(0);
         }

         public static MysqlxNotice.Warning.Level valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private Level(int value) {
            this.value = value;
         }
      }
   }

   public interface WarningOrBuilder extends MessageOrBuilder {
      boolean hasLevel();

      MysqlxNotice.Warning.Level getLevel();

      boolean hasCode();

      int getCode();

      boolean hasMsg();

      String getMsg();

      ByteString getMsgBytes();
   }
}
