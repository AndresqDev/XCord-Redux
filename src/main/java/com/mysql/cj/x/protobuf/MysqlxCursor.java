package com.mysql.cj.x.protobuf;

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
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class MysqlxCursor {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Cursor_Open_descriptor = getDescriptor().getMessageTypes().get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Cursor_Open_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Cursor_Open_descriptor, new String[]{"CursorId", "Stmt", "FetchRows"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor = internal_static_Mysqlx_Cursor_Open_descriptor.getNestedTypes()
      .get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Cursor_Open_OneOfMessage_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor, new String[]{"Type", "PrepareExecute"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Cursor_Fetch_descriptor = getDescriptor().getMessageTypes().get(1);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Cursor_Fetch_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Cursor_Fetch_descriptor, new String[]{"CursorId", "FetchRows"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Cursor_Close_descriptor = getDescriptor().getMessageTypes().get(2);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Cursor_Close_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Cursor_Close_descriptor, new String[]{"CursorId"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxCursor() {
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
         "\n\u0013mysqlx_cursor.proto\u0012\rMysqlx.Cursor\u001a\fmysqlx.proto\u001a\u0014mysqlx_prepare.proto\"ø\u0001\n\u0004Open\u0012\u0011\n\tcursor_id\u0018\u0001 \u0002(\r\u0012.\n\u0004stmt\u0018\u0004 \u0002(\u000b2 .Mysqlx.Cursor.Open.OneOfMessage\u0012\u0012\n\nfetch_rows\u0018\u0005 \u0001(\u0004\u001a\u0092\u0001\n\fOneOfMessage\u00123\n\u0004type\u0018\u0001 \u0002(\u000e2%.Mysqlx.Cursor.Open.OneOfMessage.Type\u00120\n\u000fprepare_execute\u0018\u0002 \u0001(\u000b2\u0017.Mysqlx.Prepare.Execute\"\u001b\n\u0004Type\u0012\u0013\n\u000fPREPARE_EXECUTE\u0010\u0000:\u0004\u0088ê0+\"4\n\u0005Fetch\u0012\u0011\n\tcursor_id\u0018\u0001 \u0002(\r\u0012\u0012\n\nfetch_rows\u0018\u0005 \u0001(\u0004:\u0004\u0088ê0-\" \n\u0005Close\u0012\u0011\n\tcursor_id\u0018\u0001 \u0002(\r:\u0004\u0088ê0,B\u0019\n\u0017com.mysql.cj.x.protobuf"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
         descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor(), MysqlxPrepare.getDescriptor()}
      );
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.clientMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      Mysqlx.getDescriptor();
      MysqlxPrepare.getDescriptor();
   }

   public static final class Close extends GeneratedMessageV3 implements MysqlxCursor.CloseOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int CURSOR_ID_FIELD_NUMBER = 1;
      private int cursorId_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCursor.Close DEFAULT_INSTANCE = new MysqlxCursor.Close();
      @Deprecated
      public static final Parser<MysqlxCursor.Close> PARSER = new AbstractParser<MysqlxCursor.Close>() {
         public MysqlxCursor.Close parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCursor.Close(input, extensionRegistry);
         }
      };

      private Close(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Close() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCursor.Close();
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
                        this.cursorId_ = input.readUInt32();
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
         return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCursor.Close.class, MysqlxCursor.Close.Builder.class);
      }

      @Override
      public boolean hasCursorId() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public int getCursorId() {
         return this.cursorId_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCursorId()) {
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
            output.writeUInt32(1, this.cursorId_);
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
               size += CodedOutputStream.computeUInt32Size(1, this.cursorId_);
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
         } else if (!(obj instanceof MysqlxCursor.Close)) {
            return super.equals(obj);
         } else {
            MysqlxCursor.Close other = (MysqlxCursor.Close)obj;
            if (this.hasCursorId() != other.hasCursorId()) {
               return false;
            } else {
               return this.hasCursorId() && this.getCursorId() != other.getCursorId() ? false : this.unknownFields.equals(other.unknownFields);
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
            if (this.hasCursorId()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCursorId();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCursor.Close parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCursor.Close parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCursor.Close parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCursor.Close parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCursor.Close parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCursor.Close parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCursor.Close parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCursor.Close parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCursor.Close parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCursor.Close parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCursor.Close parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCursor.Close parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCursor.Close.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCursor.Close.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCursor.Close.Builder newBuilder(MysqlxCursor.Close prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCursor.Close.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCursor.Close.Builder() : new MysqlxCursor.Close.Builder().mergeFrom(this);
      }

      protected MysqlxCursor.Close.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCursor.Close.Builder(parent);
      }

      public static MysqlxCursor.Close getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCursor.Close> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCursor.Close> getParserForType() {
         return PARSER;
      }

      public MysqlxCursor.Close getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCursor.Close.Builder> implements MysqlxCursor.CloseOrBuilder {
         private int bitField0_;
         private int cursorId_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCursor.Close.class, MysqlxCursor.Close.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCursor.Close.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxCursor.Close.Builder clear() {
            super.clear();
            this.cursorId_ = 0;
            this.bitField0_ &= -2;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Close_descriptor;
         }

         public MysqlxCursor.Close getDefaultInstanceForType() {
            return MysqlxCursor.Close.getDefaultInstance();
         }

         public MysqlxCursor.Close build() {
            MysqlxCursor.Close result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCursor.Close buildPartial() {
            MysqlxCursor.Close result = new MysqlxCursor.Close(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.cursorId_ = this.cursorId_;
               to_bitField0_ |= 1;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCursor.Close.Builder clone() {
            return (MysqlxCursor.Close.Builder)super.clone();
         }

         public MysqlxCursor.Close.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCursor.Close.Builder)super.setField(field, value);
         }

         public MysqlxCursor.Close.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCursor.Close.Builder)super.clearField(field);
         }

         public MysqlxCursor.Close.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCursor.Close.Builder)super.clearOneof(oneof);
         }

         public MysqlxCursor.Close.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCursor.Close.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCursor.Close.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCursor.Close.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCursor.Close.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCursor.Close) {
               return this.mergeFrom((MysqlxCursor.Close)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCursor.Close.Builder mergeFrom(MysqlxCursor.Close other) {
            if (other == MysqlxCursor.Close.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCursorId()) {
                  this.setCursorId(other.getCursorId());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return this.hasCursorId();
         }

         public MysqlxCursor.Close.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCursor.Close parsedMessage = null;

            try {
               parsedMessage = MysqlxCursor.Close.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCursor.Close)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasCursorId() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public int getCursorId() {
            return this.cursorId_;
         }

         public MysqlxCursor.Close.Builder setCursorId(int value) {
            this.bitField0_ |= 1;
            this.cursorId_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxCursor.Close.Builder clearCursorId() {
            this.bitField0_ &= -2;
            this.cursorId_ = 0;
            this.onChanged();
            return this;
         }

         public final MysqlxCursor.Close.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCursor.Close.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCursor.Close.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCursor.Close.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface CloseOrBuilder extends MessageOrBuilder {
      boolean hasCursorId();

      int getCursorId();
   }

   public static final class Fetch extends GeneratedMessageV3 implements MysqlxCursor.FetchOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int CURSOR_ID_FIELD_NUMBER = 1;
      private int cursorId_;
      public static final int FETCH_ROWS_FIELD_NUMBER = 5;
      private long fetchRows_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCursor.Fetch DEFAULT_INSTANCE = new MysqlxCursor.Fetch();
      @Deprecated
      public static final Parser<MysqlxCursor.Fetch> PARSER = new AbstractParser<MysqlxCursor.Fetch>() {
         public MysqlxCursor.Fetch parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCursor.Fetch(input, extensionRegistry);
         }
      };

      private Fetch(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Fetch() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCursor.Fetch();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Fetch(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.cursorId_ = input.readUInt32();
                        break;
                     case 40:
                        this.bitField0_ |= 2;
                        this.fetchRows_ = input.readUInt64();
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
         return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCursor.Fetch.class, MysqlxCursor.Fetch.Builder.class);
      }

      @Override
      public boolean hasCursorId() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public int getCursorId() {
         return this.cursorId_;
      }

      @Override
      public boolean hasFetchRows() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public long getFetchRows() {
         return this.fetchRows_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCursorId()) {
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
            output.writeUInt32(1, this.cursorId_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeUInt64(5, this.fetchRows_);
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
               size += CodedOutputStream.computeUInt32Size(1, this.cursorId_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeUInt64Size(5, this.fetchRows_);
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
         } else if (!(obj instanceof MysqlxCursor.Fetch)) {
            return super.equals(obj);
         } else {
            MysqlxCursor.Fetch other = (MysqlxCursor.Fetch)obj;
            if (this.hasCursorId() != other.hasCursorId()) {
               return false;
            } else if (this.hasCursorId() && this.getCursorId() != other.getCursorId()) {
               return false;
            } else if (this.hasFetchRows() != other.hasFetchRows()) {
               return false;
            } else {
               return this.hasFetchRows() && this.getFetchRows() != other.getFetchRows() ? false : this.unknownFields.equals(other.unknownFields);
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
            if (this.hasCursorId()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCursorId();
            }

            if (this.hasFetchRows()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + Internal.hashLong(this.getFetchRows());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCursor.Fetch parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCursor.Fetch parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCursor.Fetch parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCursor.Fetch parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCursor.Fetch parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCursor.Fetch parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCursor.Fetch parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCursor.Fetch parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCursor.Fetch parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCursor.Fetch parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCursor.Fetch parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCursor.Fetch parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCursor.Fetch.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCursor.Fetch.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCursor.Fetch.Builder newBuilder(MysqlxCursor.Fetch prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCursor.Fetch.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCursor.Fetch.Builder() : new MysqlxCursor.Fetch.Builder().mergeFrom(this);
      }

      protected MysqlxCursor.Fetch.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCursor.Fetch.Builder(parent);
      }

      public static MysqlxCursor.Fetch getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCursor.Fetch> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCursor.Fetch> getParserForType() {
         return PARSER;
      }

      public MysqlxCursor.Fetch getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCursor.Fetch.Builder> implements MysqlxCursor.FetchOrBuilder {
         private int bitField0_;
         private int cursorId_;
         private long fetchRows_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCursor.Fetch.class, MysqlxCursor.Fetch.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCursor.Fetch.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxCursor.Fetch.Builder clear() {
            super.clear();
            this.cursorId_ = 0;
            this.bitField0_ &= -2;
            this.fetchRows_ = 0L;
            this.bitField0_ &= -3;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Fetch_descriptor;
         }

         public MysqlxCursor.Fetch getDefaultInstanceForType() {
            return MysqlxCursor.Fetch.getDefaultInstance();
         }

         public MysqlxCursor.Fetch build() {
            MysqlxCursor.Fetch result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCursor.Fetch buildPartial() {
            MysqlxCursor.Fetch result = new MysqlxCursor.Fetch(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.cursorId_ = this.cursorId_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               result.fetchRows_ = this.fetchRows_;
               to_bitField0_ |= 2;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCursor.Fetch.Builder clone() {
            return (MysqlxCursor.Fetch.Builder)super.clone();
         }

         public MysqlxCursor.Fetch.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCursor.Fetch.Builder)super.setField(field, value);
         }

         public MysqlxCursor.Fetch.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCursor.Fetch.Builder)super.clearField(field);
         }

         public MysqlxCursor.Fetch.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCursor.Fetch.Builder)super.clearOneof(oneof);
         }

         public MysqlxCursor.Fetch.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCursor.Fetch.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCursor.Fetch.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCursor.Fetch.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCursor.Fetch.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCursor.Fetch) {
               return this.mergeFrom((MysqlxCursor.Fetch)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCursor.Fetch.Builder mergeFrom(MysqlxCursor.Fetch other) {
            if (other == MysqlxCursor.Fetch.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCursorId()) {
                  this.setCursorId(other.getCursorId());
               }

               if (other.hasFetchRows()) {
                  this.setFetchRows(other.getFetchRows());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return this.hasCursorId();
         }

         public MysqlxCursor.Fetch.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCursor.Fetch parsedMessage = null;

            try {
               parsedMessage = MysqlxCursor.Fetch.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCursor.Fetch)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasCursorId() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public int getCursorId() {
            return this.cursorId_;
         }

         public MysqlxCursor.Fetch.Builder setCursorId(int value) {
            this.bitField0_ |= 1;
            this.cursorId_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxCursor.Fetch.Builder clearCursorId() {
            this.bitField0_ &= -2;
            this.cursorId_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasFetchRows() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public long getFetchRows() {
            return this.fetchRows_;
         }

         public MysqlxCursor.Fetch.Builder setFetchRows(long value) {
            this.bitField0_ |= 2;
            this.fetchRows_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxCursor.Fetch.Builder clearFetchRows() {
            this.bitField0_ &= -3;
            this.fetchRows_ = 0L;
            this.onChanged();
            return this;
         }

         public final MysqlxCursor.Fetch.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCursor.Fetch.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCursor.Fetch.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCursor.Fetch.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface FetchOrBuilder extends MessageOrBuilder {
      boolean hasCursorId();

      int getCursorId();

      boolean hasFetchRows();

      long getFetchRows();
   }

   public static final class Open extends GeneratedMessageV3 implements MysqlxCursor.OpenOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int CURSOR_ID_FIELD_NUMBER = 1;
      private int cursorId_;
      public static final int STMT_FIELD_NUMBER = 4;
      private MysqlxCursor.Open.OneOfMessage stmt_;
      public static final int FETCH_ROWS_FIELD_NUMBER = 5;
      private long fetchRows_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxCursor.Open DEFAULT_INSTANCE = new MysqlxCursor.Open();
      @Deprecated
      public static final Parser<MysqlxCursor.Open> PARSER = new AbstractParser<MysqlxCursor.Open>() {
         public MysqlxCursor.Open parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxCursor.Open(input, extensionRegistry);
         }
      };

      private Open(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Open() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxCursor.Open();
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
                        this.bitField0_ |= 1;
                        this.cursorId_ = input.readUInt32();
                        break;
                     case 34:
                        MysqlxCursor.Open.OneOfMessage.Builder subBuilder = null;
                        if ((this.bitField0_ & 2) != 0) {
                           subBuilder = this.stmt_.toBuilder();
                        }

                        this.stmt_ = input.readMessage(MysqlxCursor.Open.OneOfMessage.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.stmt_);
                           this.stmt_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 2;
                        break;
                     case 40:
                        this.bitField0_ |= 4;
                        this.fetchRows_ = input.readUInt64();
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
         return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxCursor.Open.class, MysqlxCursor.Open.Builder.class);
      }

      @Override
      public boolean hasCursorId() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public int getCursorId() {
         return this.cursorId_;
      }

      @Override
      public boolean hasStmt() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public MysqlxCursor.Open.OneOfMessage getStmt() {
         return this.stmt_ == null ? MysqlxCursor.Open.OneOfMessage.getDefaultInstance() : this.stmt_;
      }

      @Override
      public MysqlxCursor.Open.OneOfMessageOrBuilder getStmtOrBuilder() {
         return this.stmt_ == null ? MysqlxCursor.Open.OneOfMessage.getDefaultInstance() : this.stmt_;
      }

      @Override
      public boolean hasFetchRows() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public long getFetchRows() {
         return this.fetchRows_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCursorId()) {
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
            output.writeUInt32(1, this.cursorId_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeMessage(4, this.getStmt());
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeUInt64(5, this.fetchRows_);
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
               size += CodedOutputStream.computeUInt32Size(1, this.cursorId_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeMessageSize(4, this.getStmt());
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeUInt64Size(5, this.fetchRows_);
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
         } else if (!(obj instanceof MysqlxCursor.Open)) {
            return super.equals(obj);
         } else {
            MysqlxCursor.Open other = (MysqlxCursor.Open)obj;
            if (this.hasCursorId() != other.hasCursorId()) {
               return false;
            } else if (this.hasCursorId() && this.getCursorId() != other.getCursorId()) {
               return false;
            } else if (this.hasStmt() != other.hasStmt()) {
               return false;
            } else if (this.hasStmt() && !this.getStmt().equals(other.getStmt())) {
               return false;
            } else if (this.hasFetchRows() != other.hasFetchRows()) {
               return false;
            } else {
               return this.hasFetchRows() && this.getFetchRows() != other.getFetchRows() ? false : this.unknownFields.equals(other.unknownFields);
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
            if (this.hasCursorId()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCursorId();
            }

            if (this.hasStmt()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getStmt().hashCode();
            }

            if (this.hasFetchRows()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + Internal.hashLong(this.getFetchRows());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxCursor.Open parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCursor.Open parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCursor.Open parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCursor.Open parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCursor.Open parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxCursor.Open parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxCursor.Open parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCursor.Open parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCursor.Open parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxCursor.Open parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxCursor.Open parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxCursor.Open parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxCursor.Open.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxCursor.Open.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxCursor.Open.Builder newBuilder(MysqlxCursor.Open prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxCursor.Open.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxCursor.Open.Builder() : new MysqlxCursor.Open.Builder().mergeFrom(this);
      }

      protected MysqlxCursor.Open.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxCursor.Open.Builder(parent);
      }

      public static MysqlxCursor.Open getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxCursor.Open> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxCursor.Open> getParserForType() {
         return PARSER;
      }

      public MysqlxCursor.Open getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxCursor.Open.Builder> implements MysqlxCursor.OpenOrBuilder {
         private int bitField0_;
         private int cursorId_;
         private MysqlxCursor.Open.OneOfMessage stmt_;
         private SingleFieldBuilderV3<MysqlxCursor.Open.OneOfMessage, MysqlxCursor.Open.OneOfMessage.Builder, MysqlxCursor.Open.OneOfMessageOrBuilder> stmtBuilder_;
         private long fetchRows_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCursor.Open.class, MysqlxCursor.Open.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxCursor.Open.alwaysUseFieldBuilders) {
               this.getStmtFieldBuilder();
            }
         }

         public MysqlxCursor.Open.Builder clear() {
            super.clear();
            this.cursorId_ = 0;
            this.bitField0_ &= -2;
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -3;
            this.fetchRows_ = 0L;
            this.bitField0_ &= -5;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_descriptor;
         }

         public MysqlxCursor.Open getDefaultInstanceForType() {
            return MysqlxCursor.Open.getDefaultInstance();
         }

         public MysqlxCursor.Open build() {
            MysqlxCursor.Open result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxCursor.Open buildPartial() {
            MysqlxCursor.Open result = new MysqlxCursor.Open(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.cursorId_ = this.cursorId_;
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

            if ((from_bitField0_ & 4) != 0) {
               result.fetchRows_ = this.fetchRows_;
               to_bitField0_ |= 4;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxCursor.Open.Builder clone() {
            return (MysqlxCursor.Open.Builder)super.clone();
         }

         public MysqlxCursor.Open.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCursor.Open.Builder)super.setField(field, value);
         }

         public MysqlxCursor.Open.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxCursor.Open.Builder)super.clearField(field);
         }

         public MysqlxCursor.Open.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxCursor.Open.Builder)super.clearOneof(oneof);
         }

         public MysqlxCursor.Open.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxCursor.Open.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxCursor.Open.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxCursor.Open.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxCursor.Open.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxCursor.Open) {
               return this.mergeFrom((MysqlxCursor.Open)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxCursor.Open.Builder mergeFrom(MysqlxCursor.Open other) {
            if (other == MysqlxCursor.Open.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCursorId()) {
                  this.setCursorId(other.getCursorId());
               }

               if (other.hasStmt()) {
                  this.mergeStmt(other.getStmt());
               }

               if (other.hasFetchRows()) {
                  this.setFetchRows(other.getFetchRows());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            if (!this.hasCursorId()) {
               return false;
            } else {
               return !this.hasStmt() ? false : this.getStmt().isInitialized();
            }
         }

         public MysqlxCursor.Open.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxCursor.Open parsedMessage = null;

            try {
               parsedMessage = MysqlxCursor.Open.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxCursor.Open)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasCursorId() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public int getCursorId() {
            return this.cursorId_;
         }

         public MysqlxCursor.Open.Builder setCursorId(int value) {
            this.bitField0_ |= 1;
            this.cursorId_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxCursor.Open.Builder clearCursorId() {
            this.bitField0_ &= -2;
            this.cursorId_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasStmt() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxCursor.Open.OneOfMessage getStmt() {
            if (this.stmtBuilder_ == null) {
               return this.stmt_ == null ? MysqlxCursor.Open.OneOfMessage.getDefaultInstance() : this.stmt_;
            } else {
               return this.stmtBuilder_.getMessage();
            }
         }

         public MysqlxCursor.Open.Builder setStmt(MysqlxCursor.Open.OneOfMessage value) {
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

         public MysqlxCursor.Open.Builder setStmt(MysqlxCursor.Open.OneOfMessage.Builder builderForValue) {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = builderForValue.build();
               this.onChanged();
            } else {
               this.stmtBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 2;
            return this;
         }

         public MysqlxCursor.Open.Builder mergeStmt(MysqlxCursor.Open.OneOfMessage value) {
            if (this.stmtBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0 && this.stmt_ != null && this.stmt_ != MysqlxCursor.Open.OneOfMessage.getDefaultInstance()) {
                  this.stmt_ = MysqlxCursor.Open.OneOfMessage.newBuilder(this.stmt_).mergeFrom(value).buildPartial();
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

         public MysqlxCursor.Open.Builder clearStmt() {
            if (this.stmtBuilder_ == null) {
               this.stmt_ = null;
               this.onChanged();
            } else {
               this.stmtBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         public MysqlxCursor.Open.OneOfMessage.Builder getStmtBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return this.getStmtFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxCursor.Open.OneOfMessageOrBuilder getStmtOrBuilder() {
            if (this.stmtBuilder_ != null) {
               return this.stmtBuilder_.getMessageOrBuilder();
            } else {
               return this.stmt_ == null ? MysqlxCursor.Open.OneOfMessage.getDefaultInstance() : this.stmt_;
            }
         }

         private SingleFieldBuilderV3<MysqlxCursor.Open.OneOfMessage, MysqlxCursor.Open.OneOfMessage.Builder, MysqlxCursor.Open.OneOfMessageOrBuilder> getStmtFieldBuilder() {
            if (this.stmtBuilder_ == null) {
               this.stmtBuilder_ = new SingleFieldBuilderV3<>(this.getStmt(), this.getParentForChildren(), this.isClean());
               this.stmt_ = null;
            }

            return this.stmtBuilder_;
         }

         @Override
         public boolean hasFetchRows() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public long getFetchRows() {
            return this.fetchRows_;
         }

         public MysqlxCursor.Open.Builder setFetchRows(long value) {
            this.bitField0_ |= 4;
            this.fetchRows_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxCursor.Open.Builder clearFetchRows() {
            this.bitField0_ &= -5;
            this.fetchRows_ = 0L;
            this.onChanged();
            return this;
         }

         public final MysqlxCursor.Open.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCursor.Open.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxCursor.Open.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxCursor.Open.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static final class OneOfMessage extends GeneratedMessageV3 implements MysqlxCursor.Open.OneOfMessageOrBuilder {
         private static final long serialVersionUID = 0L;
         private int bitField0_;
         public static final int TYPE_FIELD_NUMBER = 1;
         private int type_;
         public static final int PREPARE_EXECUTE_FIELD_NUMBER = 2;
         private MysqlxPrepare.Execute prepareExecute_;
         private byte memoizedIsInitialized = -1;
         private static final MysqlxCursor.Open.OneOfMessage DEFAULT_INSTANCE = new MysqlxCursor.Open.OneOfMessage();
         @Deprecated
         public static final Parser<MysqlxCursor.Open.OneOfMessage> PARSER = new AbstractParser<MysqlxCursor.Open.OneOfMessage>() {
            public MysqlxCursor.Open.OneOfMessage parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
               return new MysqlxCursor.Open.OneOfMessage(input, extensionRegistry);
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
            return new MysqlxCursor.Open.OneOfMessage();
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
                           MysqlxCursor.Open.OneOfMessage.Type value = MysqlxCursor.Open.OneOfMessage.Type.valueOf(rawValue);
                           if (value == null) {
                              unknownFields.mergeVarintField(1, rawValue);
                           } else {
                              this.bitField0_ |= 1;
                              this.type_ = rawValue;
                           }
                           break;
                        case 18:
                           MysqlxPrepare.Execute.Builder subBuilder = null;
                           if ((this.bitField0_ & 2) != 0) {
                              subBuilder = this.prepareExecute_.toBuilder();
                           }

                           this.prepareExecute_ = input.readMessage(MysqlxPrepare.Execute.PARSER, extensionRegistry);
                           if (subBuilder != null) {
                              subBuilder.mergeFrom(this.prepareExecute_);
                              this.prepareExecute_ = subBuilder.buildPartial();
                           }

                           this.bitField0_ |= 2;
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
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxCursor.Open.OneOfMessage.class, MysqlxCursor.Open.OneOfMessage.Builder.class);
         }

         @Override
         public boolean hasType() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxCursor.Open.OneOfMessage.Type getType() {
            MysqlxCursor.Open.OneOfMessage.Type result = MysqlxCursor.Open.OneOfMessage.Type.valueOf(this.type_);
            return result == null ? MysqlxCursor.Open.OneOfMessage.Type.PREPARE_EXECUTE : result;
         }

         @Override
         public boolean hasPrepareExecute() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxPrepare.Execute getPrepareExecute() {
            return this.prepareExecute_ == null ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_;
         }

         @Override
         public MysqlxPrepare.ExecuteOrBuilder getPrepareExecuteOrBuilder() {
            return this.prepareExecute_ == null ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_;
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
            } else if (this.hasPrepareExecute() && !this.getPrepareExecute().isInitialized()) {
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
               output.writeMessage(2, this.getPrepareExecute());
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
                  size += CodedOutputStream.computeMessageSize(2, this.getPrepareExecute());
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
            } else if (!(obj instanceof MysqlxCursor.Open.OneOfMessage)) {
               return super.equals(obj);
            } else {
               MysqlxCursor.Open.OneOfMessage other = (MysqlxCursor.Open.OneOfMessage)obj;
               if (this.hasType() != other.hasType()) {
                  return false;
               } else if (this.hasType() && this.type_ != other.type_) {
                  return false;
               } else if (this.hasPrepareExecute() != other.hasPrepareExecute()) {
                  return false;
               } else {
                  return this.hasPrepareExecute() && !this.getPrepareExecute().equals(other.getPrepareExecute())
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

               if (this.hasPrepareExecute()) {
                  hash = 37 * hash + 2;
                  hash = 53 * hash + this.getPrepareExecute().hashCode();
               }

               hash = 29 * hash + this.unknownFields.hashCode();
               this.memoizedHashCode = hash;
               return hash;
            }
         }

         public static MysqlxCursor.Open.OneOfMessage parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxCursor.Open.OneOfMessage parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxCursor.Open.OneOfMessage parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxCursor.Open.OneOfMessage parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxCursor.Open.OneOfMessage parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
         }

         public static MysqlxCursor.Open.OneOfMessage parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
         }

         public static MysqlxCursor.Open.OneOfMessage parseFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static MysqlxCursor.Open.OneOfMessage parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public static MysqlxCursor.Open.OneOfMessage parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
         }

         public static MysqlxCursor.Open.OneOfMessage parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
         }

         public static MysqlxCursor.Open.OneOfMessage parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input);
         }

         public static MysqlxCursor.Open.OneOfMessage parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
         }

         public MysqlxCursor.Open.OneOfMessage.Builder newBuilderForType() {
            return newBuilder();
         }

         public static MysqlxCursor.Open.OneOfMessage.Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
         }

         public static MysqlxCursor.Open.OneOfMessage.Builder newBuilder(MysqlxCursor.Open.OneOfMessage prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
         }

         public MysqlxCursor.Open.OneOfMessage.Builder toBuilder() {
            return this == DEFAULT_INSTANCE ? new MysqlxCursor.Open.OneOfMessage.Builder() : new MysqlxCursor.Open.OneOfMessage.Builder().mergeFrom(this);
         }

         protected MysqlxCursor.Open.OneOfMessage.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
            return new MysqlxCursor.Open.OneOfMessage.Builder(parent);
         }

         public static MysqlxCursor.Open.OneOfMessage getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<MysqlxCursor.Open.OneOfMessage> parser() {
            return PARSER;
         }

         @Override
         public Parser<MysqlxCursor.Open.OneOfMessage> getParserForType() {
            return PARSER;
         }

         public MysqlxCursor.Open.OneOfMessage getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
         }

         public static final class Builder
            extends GeneratedMessageV3.Builder<MysqlxCursor.Open.OneOfMessage.Builder>
            implements MysqlxCursor.Open.OneOfMessageOrBuilder {
            private int bitField0_;
            private int type_ = 0;
            private MysqlxPrepare.Execute prepareExecute_;
            private SingleFieldBuilderV3<MysqlxPrepare.Execute, MysqlxPrepare.Execute.Builder, MysqlxPrepare.ExecuteOrBuilder> prepareExecuteBuilder_;

            public static final Descriptors.Descriptor getDescriptor() {
               return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
               return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_fieldAccessorTable
                  .ensureFieldAccessorsInitialized(MysqlxCursor.Open.OneOfMessage.class, MysqlxCursor.Open.OneOfMessage.Builder.class);
            }

            private Builder() {
               this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent parent) {
               super(parent);
               this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
               if (MysqlxCursor.Open.OneOfMessage.alwaysUseFieldBuilders) {
                  this.getPrepareExecuteFieldBuilder();
               }
            }

            public MysqlxCursor.Open.OneOfMessage.Builder clear() {
               super.clear();
               this.type_ = 0;
               this.bitField0_ &= -2;
               if (this.prepareExecuteBuilder_ == null) {
                  this.prepareExecute_ = null;
               } else {
                  this.prepareExecuteBuilder_.clear();
               }

               this.bitField0_ &= -3;
               return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
               return MysqlxCursor.internal_static_Mysqlx_Cursor_Open_OneOfMessage_descriptor;
            }

            public MysqlxCursor.Open.OneOfMessage getDefaultInstanceForType() {
               return MysqlxCursor.Open.OneOfMessage.getDefaultInstance();
            }

            public MysqlxCursor.Open.OneOfMessage build() {
               MysqlxCursor.Open.OneOfMessage result = this.buildPartial();
               if (!result.isInitialized()) {
                  throw newUninitializedMessageException(result);
               } else {
                  return result;
               }
            }

            public MysqlxCursor.Open.OneOfMessage buildPartial() {
               MysqlxCursor.Open.OneOfMessage result = new MysqlxCursor.Open.OneOfMessage(this);
               int from_bitField0_ = this.bitField0_;
               int to_bitField0_ = 0;
               if ((from_bitField0_ & 1) != 0) {
                  to_bitField0_ |= 1;
               }

               result.type_ = this.type_;
               if ((from_bitField0_ & 2) != 0) {
                  if (this.prepareExecuteBuilder_ == null) {
                     result.prepareExecute_ = this.prepareExecute_;
                  } else {
                     result.prepareExecute_ = this.prepareExecuteBuilder_.build();
                  }

                  to_bitField0_ |= 2;
               }

               result.bitField0_ = to_bitField0_;
               this.onBuilt();
               return result;
            }

            public MysqlxCursor.Open.OneOfMessage.Builder clone() {
               return (MysqlxCursor.Open.OneOfMessage.Builder)super.clone();
            }

            public MysqlxCursor.Open.OneOfMessage.Builder setField(Descriptors.FieldDescriptor field, Object value) {
               return (MysqlxCursor.Open.OneOfMessage.Builder)super.setField(field, value);
            }

            public MysqlxCursor.Open.OneOfMessage.Builder clearField(Descriptors.FieldDescriptor field) {
               return (MysqlxCursor.Open.OneOfMessage.Builder)super.clearField(field);
            }

            public MysqlxCursor.Open.OneOfMessage.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
               return (MysqlxCursor.Open.OneOfMessage.Builder)super.clearOneof(oneof);
            }

            public MysqlxCursor.Open.OneOfMessage.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
               return (MysqlxCursor.Open.OneOfMessage.Builder)super.setRepeatedField(field, index, value);
            }

            public MysqlxCursor.Open.OneOfMessage.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
               return (MysqlxCursor.Open.OneOfMessage.Builder)super.addRepeatedField(field, value);
            }

            public MysqlxCursor.Open.OneOfMessage.Builder mergeFrom(Message other) {
               if (other instanceof MysqlxCursor.Open.OneOfMessage) {
                  return this.mergeFrom((MysqlxCursor.Open.OneOfMessage)other);
               } else {
                  super.mergeFrom(other);
                  return this;
               }
            }

            public MysqlxCursor.Open.OneOfMessage.Builder mergeFrom(MysqlxCursor.Open.OneOfMessage other) {
               if (other == MysqlxCursor.Open.OneOfMessage.getDefaultInstance()) {
                  return this;
               } else {
                  if (other.hasType()) {
                     this.setType(other.getType());
                  }

                  if (other.hasPrepareExecute()) {
                     this.mergePrepareExecute(other.getPrepareExecute());
                  }

                  this.mergeUnknownFields(other.unknownFields);
                  this.onChanged();
                  return this;
               }
            }

            @Override
            public final boolean isInitialized() {
               return !this.hasType() ? false : !this.hasPrepareExecute() || this.getPrepareExecute().isInitialized();
            }

            public MysqlxCursor.Open.OneOfMessage.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
               MysqlxCursor.Open.OneOfMessage parsedMessage = null;

               try {
                  parsedMessage = MysqlxCursor.Open.OneOfMessage.PARSER.parsePartialFrom(input, extensionRegistry);
               } catch (InvalidProtocolBufferException var8) {
                  parsedMessage = (MysqlxCursor.Open.OneOfMessage)var8.getUnfinishedMessage();
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
            public MysqlxCursor.Open.OneOfMessage.Type getType() {
               MysqlxCursor.Open.OneOfMessage.Type result = MysqlxCursor.Open.OneOfMessage.Type.valueOf(this.type_);
               return result == null ? MysqlxCursor.Open.OneOfMessage.Type.PREPARE_EXECUTE : result;
            }

            public MysqlxCursor.Open.OneOfMessage.Builder setType(MysqlxCursor.Open.OneOfMessage.Type value) {
               if (value == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 1;
                  this.type_ = value.getNumber();
                  this.onChanged();
                  return this;
               }
            }

            public MysqlxCursor.Open.OneOfMessage.Builder clearType() {
               this.bitField0_ &= -2;
               this.type_ = 0;
               this.onChanged();
               return this;
            }

            @Override
            public boolean hasPrepareExecute() {
               return (this.bitField0_ & 2) != 0;
            }

            @Override
            public MysqlxPrepare.Execute getPrepareExecute() {
               if (this.prepareExecuteBuilder_ == null) {
                  return this.prepareExecute_ == null ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_;
               } else {
                  return this.prepareExecuteBuilder_.getMessage();
               }
            }

            public MysqlxCursor.Open.OneOfMessage.Builder setPrepareExecute(MysqlxPrepare.Execute value) {
               if (this.prepareExecuteBuilder_ == null) {
                  if (value == null) {
                     throw new NullPointerException();
                  }

                  this.prepareExecute_ = value;
                  this.onChanged();
               } else {
                  this.prepareExecuteBuilder_.setMessage(value);
               }

               this.bitField0_ |= 2;
               return this;
            }

            public MysqlxCursor.Open.OneOfMessage.Builder setPrepareExecute(MysqlxPrepare.Execute.Builder builderForValue) {
               if (this.prepareExecuteBuilder_ == null) {
                  this.prepareExecute_ = builderForValue.build();
                  this.onChanged();
               } else {
                  this.prepareExecuteBuilder_.setMessage(builderForValue.build());
               }

               this.bitField0_ |= 2;
               return this;
            }

            public MysqlxCursor.Open.OneOfMessage.Builder mergePrepareExecute(MysqlxPrepare.Execute value) {
               if (this.prepareExecuteBuilder_ == null) {
                  if ((this.bitField0_ & 2) != 0 && this.prepareExecute_ != null && this.prepareExecute_ != MysqlxPrepare.Execute.getDefaultInstance()) {
                     this.prepareExecute_ = MysqlxPrepare.Execute.newBuilder(this.prepareExecute_).mergeFrom(value).buildPartial();
                  } else {
                     this.prepareExecute_ = value;
                  }

                  this.onChanged();
               } else {
                  this.prepareExecuteBuilder_.mergeFrom(value);
               }

               this.bitField0_ |= 2;
               return this;
            }

            public MysqlxCursor.Open.OneOfMessage.Builder clearPrepareExecute() {
               if (this.prepareExecuteBuilder_ == null) {
                  this.prepareExecute_ = null;
                  this.onChanged();
               } else {
                  this.prepareExecuteBuilder_.clear();
               }

               this.bitField0_ &= -3;
               return this;
            }

            public MysqlxPrepare.Execute.Builder getPrepareExecuteBuilder() {
               this.bitField0_ |= 2;
               this.onChanged();
               return this.getPrepareExecuteFieldBuilder().getBuilder();
            }

            @Override
            public MysqlxPrepare.ExecuteOrBuilder getPrepareExecuteOrBuilder() {
               if (this.prepareExecuteBuilder_ != null) {
                  return this.prepareExecuteBuilder_.getMessageOrBuilder();
               } else {
                  return this.prepareExecute_ == null ? MysqlxPrepare.Execute.getDefaultInstance() : this.prepareExecute_;
               }
            }

            private SingleFieldBuilderV3<MysqlxPrepare.Execute, MysqlxPrepare.Execute.Builder, MysqlxPrepare.ExecuteOrBuilder> getPrepareExecuteFieldBuilder() {
               if (this.prepareExecuteBuilder_ == null) {
                  this.prepareExecuteBuilder_ = new SingleFieldBuilderV3<>(this.getPrepareExecute(), this.getParentForChildren(), this.isClean());
                  this.prepareExecute_ = null;
               }

               return this.prepareExecuteBuilder_;
            }

            public final MysqlxCursor.Open.OneOfMessage.Builder setUnknownFields(UnknownFieldSet unknownFields) {
               return (MysqlxCursor.Open.OneOfMessage.Builder)super.setUnknownFields(unknownFields);
            }

            public final MysqlxCursor.Open.OneOfMessage.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
               return (MysqlxCursor.Open.OneOfMessage.Builder)super.mergeUnknownFields(unknownFields);
            }
         }

         public static enum Type implements ProtocolMessageEnum {
            PREPARE_EXECUTE(0);

            public static final int PREPARE_EXECUTE_VALUE = 0;
            private static final Internal.EnumLiteMap<MysqlxCursor.Open.OneOfMessage.Type> internalValueMap = new Internal.EnumLiteMap<MysqlxCursor.Open.OneOfMessage.Type>() {
               public MysqlxCursor.Open.OneOfMessage.Type findValueByNumber(int number) {
                  return MysqlxCursor.Open.OneOfMessage.Type.forNumber(number);
               }
            };
            private static final MysqlxCursor.Open.OneOfMessage.Type[] VALUES = values();
            private final int value;

            @Override
            public final int getNumber() {
               return this.value;
            }

            @Deprecated
            public static MysqlxCursor.Open.OneOfMessage.Type valueOf(int value) {
               return forNumber(value);
            }

            public static MysqlxCursor.Open.OneOfMessage.Type forNumber(int value) {
               switch (value) {
                  case 0:
                     return PREPARE_EXECUTE;
                  default:
                     return null;
               }
            }

            public static Internal.EnumLiteMap<MysqlxCursor.Open.OneOfMessage.Type> internalGetValueMap() {
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
               return MysqlxCursor.Open.OneOfMessage.getDescriptor().getEnumTypes().get(0);
            }

            public static MysqlxCursor.Open.OneOfMessage.Type valueOf(Descriptors.EnumValueDescriptor desc) {
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

         MysqlxCursor.Open.OneOfMessage.Type getType();

         boolean hasPrepareExecute();

         MysqlxPrepare.Execute getPrepareExecute();

         MysqlxPrepare.ExecuteOrBuilder getPrepareExecuteOrBuilder();
      }
   }

   public interface OpenOrBuilder extends MessageOrBuilder {
      boolean hasCursorId();

      int getCursorId();

      boolean hasStmt();

      MysqlxCursor.Open.OneOfMessage getStmt();

      MysqlxCursor.Open.OneOfMessageOrBuilder getStmtOrBuilder();

      boolean hasFetchRows();

      long getFetchRows();
   }
}
