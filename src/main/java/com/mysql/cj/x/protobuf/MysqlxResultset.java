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
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxResultset {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor = getDescriptor().getMessageTypes().get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor, new String[0]
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor = getDescriptor().getMessageTypes().get(1);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor, new String[0]
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchDone_descriptor = getDescriptor().getMessageTypes().get(2);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Resultset_FetchDone_descriptor, new String[0]
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_FetchSuspended_descriptor = getDescriptor().getMessageTypes().get(3);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Resultset_FetchSuspended_descriptor, new String[0]
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor = getDescriptor().getMessageTypes().get(4);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor,
      new String[]{
         "Type", "Name", "OriginalName", "Table", "OriginalTable", "Schema", "Catalog", "Collation", "FractionalDigits", "Length", "Flags", "ContentType"
      }
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Resultset_Row_descriptor = getDescriptor().getMessageTypes().get(5);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Resultset_Row_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Resultset_Row_descriptor, new String[]{"Field"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxResultset() {
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
         "\n\u0016mysqlx_resultset.proto\u0012\u0010Mysqlx.Resultset\u001a\fmysqlx.proto\"\u001e\n\u0016FetchDoneMoreOutParams:\u0004\u0090ê0\u0012\"\u001f\n\u0017FetchDoneMoreResultsets:\u0004\u0090ê0\u0010\"\u0011\n\tFetchDone:\u0004\u0090ê0\u000e\"\u0016\n\u000eFetchSuspended:\u0004\u0090ê0\u000f\"¥\u0003\n\u000eColumnMetaData\u00128\n\u0004type\u0018\u0001 \u0002(\u000e2*.Mysqlx.Resultset.ColumnMetaData.FieldType\u0012\f\n\u0004name\u0018\u0002 \u0001(\f\u0012\u0015\n\roriginal_name\u0018\u0003 \u0001(\f\u0012\r\n\u0005table\u0018\u0004 \u0001(\f\u0012\u0016\n\u000eoriginal_table\u0018\u0005 \u0001(\f\u0012\u000e\n\u0006schema\u0018\u0006 \u0001(\f\u0012\u000f\n\u0007catalog\u0018\u0007 \u0001(\f\u0012\u0011\n\tcollation\u0018\b \u0001(\u0004\u0012\u0019\n\u0011fractional_digits\u0018\t \u0001(\r\u0012\u000e\n\u0006length\u0018\n \u0001(\r\u0012\r\n\u0005flags\u0018\u000b \u0001(\r\u0012\u0014\n\fcontent_type\u0018\f \u0001(\r\"\u0082\u0001\n\tFieldType\u0012\b\n\u0004SINT\u0010\u0001\u0012\b\n\u0004UINT\u0010\u0002\u0012\n\n\u0006DOUBLE\u0010\u0005\u0012\t\n\u0005FLOAT\u0010\u0006\u0012\t\n\u0005BYTES\u0010\u0007\u0012\b\n\u0004TIME\u0010\n\u0012\f\n\bDATETIME\u0010\f\u0012\u0007\n\u0003SET\u0010\u000f\u0012\b\n\u0004ENUM\u0010\u0010\u0012\u0007\n\u0003BIT\u0010\u0011\u0012\u000b\n\u0007DECIMAL\u0010\u0012:\u0004\u0090ê0\f\"\u001a\n\u0003Row\u0012\r\n\u0005field\u0018\u0001 \u0003(\f:\u0004\u0090ê0\r*4\n\u0011ContentType_BYTES\u0012\f\n\bGEOMETRY\u0010\u0001\u0012\b\n\u0004JSON\u0010\u0002\u0012\u0007\n\u0003XML\u0010\u0003*.\n\u0014ContentType_DATETIME\u0012\b\n\u0004DATE\u0010\u0001\u0012\f\n\bDATETIME\u0010\u0002B\u0019\n\u0017com.mysql.cj.x.protobuf"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor()});
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.serverMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      Mysqlx.getDescriptor();
   }

   public static final class ColumnMetaData extends GeneratedMessageV3 implements MysqlxResultset.ColumnMetaDataOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int TYPE_FIELD_NUMBER = 1;
      private int type_;
      public static final int NAME_FIELD_NUMBER = 2;
      private ByteString name_;
      public static final int ORIGINAL_NAME_FIELD_NUMBER = 3;
      private ByteString originalName_;
      public static final int TABLE_FIELD_NUMBER = 4;
      private ByteString table_;
      public static final int ORIGINAL_TABLE_FIELD_NUMBER = 5;
      private ByteString originalTable_;
      public static final int SCHEMA_FIELD_NUMBER = 6;
      private ByteString schema_;
      public static final int CATALOG_FIELD_NUMBER = 7;
      private ByteString catalog_;
      public static final int COLLATION_FIELD_NUMBER = 8;
      private long collation_;
      public static final int FRACTIONAL_DIGITS_FIELD_NUMBER = 9;
      private int fractionalDigits_;
      public static final int LENGTH_FIELD_NUMBER = 10;
      private int length_;
      public static final int FLAGS_FIELD_NUMBER = 11;
      private int flags_;
      public static final int CONTENT_TYPE_FIELD_NUMBER = 12;
      private int contentType_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxResultset.ColumnMetaData DEFAULT_INSTANCE = new MysqlxResultset.ColumnMetaData();
      @Deprecated
      public static final Parser<MysqlxResultset.ColumnMetaData> PARSER = new AbstractParser<MysqlxResultset.ColumnMetaData>() {
         public MysqlxResultset.ColumnMetaData parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxResultset.ColumnMetaData(input, extensionRegistry);
         }
      };

      private ColumnMetaData(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private ColumnMetaData() {
         this.type_ = 1;
         this.name_ = ByteString.EMPTY;
         this.originalName_ = ByteString.EMPTY;
         this.table_ = ByteString.EMPTY;
         this.originalTable_ = ByteString.EMPTY;
         this.schema_ = ByteString.EMPTY;
         this.catalog_ = ByteString.EMPTY;
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxResultset.ColumnMetaData();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private ColumnMetaData(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        MysqlxResultset.ColumnMetaData.FieldType value = MysqlxResultset.ColumnMetaData.FieldType.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(1, rawValue);
                        } else {
                           this.bitField0_ |= 1;
                           this.type_ = rawValue;
                        }
                        break;
                     case 18:
                        this.bitField0_ |= 2;
                        this.name_ = input.readBytes();
                        break;
                     case 26:
                        this.bitField0_ |= 4;
                        this.originalName_ = input.readBytes();
                        break;
                     case 34:
                        this.bitField0_ |= 8;
                        this.table_ = input.readBytes();
                        break;
                     case 42:
                        this.bitField0_ |= 16;
                        this.originalTable_ = input.readBytes();
                        break;
                     case 50:
                        this.bitField0_ |= 32;
                        this.schema_ = input.readBytes();
                        break;
                     case 58:
                        this.bitField0_ |= 64;
                        this.catalog_ = input.readBytes();
                        break;
                     case 64:
                        this.bitField0_ |= 128;
                        this.collation_ = input.readUInt64();
                        break;
                     case 72:
                        this.bitField0_ |= 256;
                        this.fractionalDigits_ = input.readUInt32();
                        break;
                     case 80:
                        this.bitField0_ |= 512;
                        this.length_ = input.readUInt32();
                        break;
                     case 88:
                        this.bitField0_ |= 1024;
                        this.flags_ = input.readUInt32();
                        break;
                     case 96:
                        this.bitField0_ |= 2048;
                        this.contentType_ = input.readUInt32();
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
         return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxResultset.ColumnMetaData.class, MysqlxResultset.ColumnMetaData.Builder.class);
      }

      @Override
      public boolean hasType() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxResultset.ColumnMetaData.FieldType getType() {
         MysqlxResultset.ColumnMetaData.FieldType result = MysqlxResultset.ColumnMetaData.FieldType.valueOf(this.type_);
         return result == null ? MysqlxResultset.ColumnMetaData.FieldType.SINT : result;
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public ByteString getName() {
         return this.name_;
      }

      @Override
      public boolean hasOriginalName() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public ByteString getOriginalName() {
         return this.originalName_;
      }

      @Override
      public boolean hasTable() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public ByteString getTable() {
         return this.table_;
      }

      @Override
      public boolean hasOriginalTable() {
         return (this.bitField0_ & 16) != 0;
      }

      @Override
      public ByteString getOriginalTable() {
         return this.originalTable_;
      }

      @Override
      public boolean hasSchema() {
         return (this.bitField0_ & 32) != 0;
      }

      @Override
      public ByteString getSchema() {
         return this.schema_;
      }

      @Override
      public boolean hasCatalog() {
         return (this.bitField0_ & 64) != 0;
      }

      @Override
      public ByteString getCatalog() {
         return this.catalog_;
      }

      @Override
      public boolean hasCollation() {
         return (this.bitField0_ & 128) != 0;
      }

      @Override
      public long getCollation() {
         return this.collation_;
      }

      @Override
      public boolean hasFractionalDigits() {
         return (this.bitField0_ & 256) != 0;
      }

      @Override
      public int getFractionalDigits() {
         return this.fractionalDigits_;
      }

      @Override
      public boolean hasLength() {
         return (this.bitField0_ & 512) != 0;
      }

      @Override
      public int getLength() {
         return this.length_;
      }

      @Override
      public boolean hasFlags() {
         return (this.bitField0_ & 1024) != 0;
      }

      @Override
      public int getFlags() {
         return this.flags_;
      }

      @Override
      public boolean hasContentType() {
         return (this.bitField0_ & 2048) != 0;
      }

      @Override
      public int getContentType() {
         return this.contentType_;
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
            output.writeBytes(2, this.name_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeBytes(3, this.originalName_);
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeBytes(4, this.table_);
         }

         if ((this.bitField0_ & 16) != 0) {
            output.writeBytes(5, this.originalTable_);
         }

         if ((this.bitField0_ & 32) != 0) {
            output.writeBytes(6, this.schema_);
         }

         if ((this.bitField0_ & 64) != 0) {
            output.writeBytes(7, this.catalog_);
         }

         if ((this.bitField0_ & 128) != 0) {
            output.writeUInt64(8, this.collation_);
         }

         if ((this.bitField0_ & 256) != 0) {
            output.writeUInt32(9, this.fractionalDigits_);
         }

         if ((this.bitField0_ & 512) != 0) {
            output.writeUInt32(10, this.length_);
         }

         if ((this.bitField0_ & 1024) != 0) {
            output.writeUInt32(11, this.flags_);
         }

         if ((this.bitField0_ & 2048) != 0) {
            output.writeUInt32(12, this.contentType_);
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
               size += CodedOutputStream.computeBytesSize(2, this.name_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeBytesSize(3, this.originalName_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeBytesSize(4, this.table_);
            }

            if ((this.bitField0_ & 16) != 0) {
               size += CodedOutputStream.computeBytesSize(5, this.originalTable_);
            }

            if ((this.bitField0_ & 32) != 0) {
               size += CodedOutputStream.computeBytesSize(6, this.schema_);
            }

            if ((this.bitField0_ & 64) != 0) {
               size += CodedOutputStream.computeBytesSize(7, this.catalog_);
            }

            if ((this.bitField0_ & 128) != 0) {
               size += CodedOutputStream.computeUInt64Size(8, this.collation_);
            }

            if ((this.bitField0_ & 256) != 0) {
               size += CodedOutputStream.computeUInt32Size(9, this.fractionalDigits_);
            }

            if ((this.bitField0_ & 512) != 0) {
               size += CodedOutputStream.computeUInt32Size(10, this.length_);
            }

            if ((this.bitField0_ & 1024) != 0) {
               size += CodedOutputStream.computeUInt32Size(11, this.flags_);
            }

            if ((this.bitField0_ & 2048) != 0) {
               size += CodedOutputStream.computeUInt32Size(12, this.contentType_);
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
         } else if (!(obj instanceof MysqlxResultset.ColumnMetaData)) {
            return super.equals(obj);
         } else {
            MysqlxResultset.ColumnMetaData other = (MysqlxResultset.ColumnMetaData)obj;
            if (this.hasType() != other.hasType()) {
               return false;
            } else if (this.hasType() && this.type_ != other.type_) {
               return false;
            } else if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
               return false;
            } else if (this.hasOriginalName() != other.hasOriginalName()) {
               return false;
            } else if (this.hasOriginalName() && !this.getOriginalName().equals(other.getOriginalName())) {
               return false;
            } else if (this.hasTable() != other.hasTable()) {
               return false;
            } else if (this.hasTable() && !this.getTable().equals(other.getTable())) {
               return false;
            } else if (this.hasOriginalTable() != other.hasOriginalTable()) {
               return false;
            } else if (this.hasOriginalTable() && !this.getOriginalTable().equals(other.getOriginalTable())) {
               return false;
            } else if (this.hasSchema() != other.hasSchema()) {
               return false;
            } else if (this.hasSchema() && !this.getSchema().equals(other.getSchema())) {
               return false;
            } else if (this.hasCatalog() != other.hasCatalog()) {
               return false;
            } else if (this.hasCatalog() && !this.getCatalog().equals(other.getCatalog())) {
               return false;
            } else if (this.hasCollation() != other.hasCollation()) {
               return false;
            } else if (this.hasCollation() && this.getCollation() != other.getCollation()) {
               return false;
            } else if (this.hasFractionalDigits() != other.hasFractionalDigits()) {
               return false;
            } else if (this.hasFractionalDigits() && this.getFractionalDigits() != other.getFractionalDigits()) {
               return false;
            } else if (this.hasLength() != other.hasLength()) {
               return false;
            } else if (this.hasLength() && this.getLength() != other.getLength()) {
               return false;
            } else if (this.hasFlags() != other.hasFlags()) {
               return false;
            } else if (this.hasFlags() && this.getFlags() != other.getFlags()) {
               return false;
            } else if (this.hasContentType() != other.hasContentType()) {
               return false;
            } else {
               return this.hasContentType() && this.getContentType() != other.getContentType() ? false : this.unknownFields.equals(other.unknownFields);
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

            if (this.hasName()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getName().hashCode();
            }

            if (this.hasOriginalName()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getOriginalName().hashCode();
            }

            if (this.hasTable()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getTable().hashCode();
            }

            if (this.hasOriginalTable()) {
               hash = 37 * hash + 5;
               hash = 53 * hash + this.getOriginalTable().hashCode();
            }

            if (this.hasSchema()) {
               hash = 37 * hash + 6;
               hash = 53 * hash + this.getSchema().hashCode();
            }

            if (this.hasCatalog()) {
               hash = 37 * hash + 7;
               hash = 53 * hash + this.getCatalog().hashCode();
            }

            if (this.hasCollation()) {
               hash = 37 * hash + 8;
               hash = 53 * hash + Internal.hashLong(this.getCollation());
            }

            if (this.hasFractionalDigits()) {
               hash = 37 * hash + 9;
               hash = 53 * hash + this.getFractionalDigits();
            }

            if (this.hasLength()) {
               hash = 37 * hash + 10;
               hash = 53 * hash + this.getLength();
            }

            if (this.hasFlags()) {
               hash = 37 * hash + 11;
               hash = 53 * hash + this.getFlags();
            }

            if (this.hasContentType()) {
               hash = 37 * hash + 12;
               hash = 53 * hash + this.getContentType();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxResultset.ColumnMetaData parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.ColumnMetaData parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.ColumnMetaData parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.ColumnMetaData parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.ColumnMetaData parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.ColumnMetaData parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.ColumnMetaData parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxResultset.ColumnMetaData parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxResultset.ColumnMetaData parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxResultset.ColumnMetaData parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxResultset.ColumnMetaData parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxResultset.ColumnMetaData parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxResultset.ColumnMetaData.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxResultset.ColumnMetaData.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxResultset.ColumnMetaData.Builder newBuilder(MysqlxResultset.ColumnMetaData prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxResultset.ColumnMetaData.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxResultset.ColumnMetaData.Builder() : new MysqlxResultset.ColumnMetaData.Builder().mergeFrom(this);
      }

      protected MysqlxResultset.ColumnMetaData.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxResultset.ColumnMetaData.Builder(parent);
      }

      public static MysqlxResultset.ColumnMetaData getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxResultset.ColumnMetaData> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxResultset.ColumnMetaData> getParserForType() {
         return PARSER;
      }

      public MysqlxResultset.ColumnMetaData getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxResultset.ColumnMetaData.Builder>
         implements MysqlxResultset.ColumnMetaDataOrBuilder {
         private int bitField0_;
         private int type_ = 1;
         private ByteString name_ = ByteString.EMPTY;
         private ByteString originalName_ = ByteString.EMPTY;
         private ByteString table_ = ByteString.EMPTY;
         private ByteString originalTable_ = ByteString.EMPTY;
         private ByteString schema_ = ByteString.EMPTY;
         private ByteString catalog_ = ByteString.EMPTY;
         private long collation_;
         private int fractionalDigits_;
         private int length_;
         private int flags_;
         private int contentType_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxResultset.ColumnMetaData.class, MysqlxResultset.ColumnMetaData.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxResultset.ColumnMetaData.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxResultset.ColumnMetaData.Builder clear() {
            super.clear();
            this.type_ = 1;
            this.bitField0_ &= -2;
            this.name_ = ByteString.EMPTY;
            this.bitField0_ &= -3;
            this.originalName_ = ByteString.EMPTY;
            this.bitField0_ &= -5;
            this.table_ = ByteString.EMPTY;
            this.bitField0_ &= -9;
            this.originalTable_ = ByteString.EMPTY;
            this.bitField0_ &= -17;
            this.schema_ = ByteString.EMPTY;
            this.bitField0_ &= -33;
            this.catalog_ = ByteString.EMPTY;
            this.bitField0_ &= -65;
            this.collation_ = 0L;
            this.bitField0_ &= -129;
            this.fractionalDigits_ = 0;
            this.bitField0_ &= -257;
            this.length_ = 0;
            this.bitField0_ &= -513;
            this.flags_ = 0;
            this.bitField0_ &= -1025;
            this.contentType_ = 0;
            this.bitField0_ &= -2049;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_ColumnMetaData_descriptor;
         }

         public MysqlxResultset.ColumnMetaData getDefaultInstanceForType() {
            return MysqlxResultset.ColumnMetaData.getDefaultInstance();
         }

         public MysqlxResultset.ColumnMetaData build() {
            MysqlxResultset.ColumnMetaData result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxResultset.ColumnMetaData buildPartial() {
            MysqlxResultset.ColumnMetaData result = new MysqlxResultset.ColumnMetaData(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.type_ = this.type_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.name_ = this.name_;
            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.originalName_ = this.originalName_;
            if ((from_bitField0_ & 8) != 0) {
               to_bitField0_ |= 8;
            }

            result.table_ = this.table_;
            if ((from_bitField0_ & 16) != 0) {
               to_bitField0_ |= 16;
            }

            result.originalTable_ = this.originalTable_;
            if ((from_bitField0_ & 32) != 0) {
               to_bitField0_ |= 32;
            }

            result.schema_ = this.schema_;
            if ((from_bitField0_ & 64) != 0) {
               to_bitField0_ |= 64;
            }

            result.catalog_ = this.catalog_;
            if ((from_bitField0_ & 128) != 0) {
               result.collation_ = this.collation_;
               to_bitField0_ |= 128;
            }

            if ((from_bitField0_ & 256) != 0) {
               result.fractionalDigits_ = this.fractionalDigits_;
               to_bitField0_ |= 256;
            }

            if ((from_bitField0_ & 512) != 0) {
               result.length_ = this.length_;
               to_bitField0_ |= 512;
            }

            if ((from_bitField0_ & 1024) != 0) {
               result.flags_ = this.flags_;
               to_bitField0_ |= 1024;
            }

            if ((from_bitField0_ & 2048) != 0) {
               result.contentType_ = this.contentType_;
               to_bitField0_ |= 2048;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxResultset.ColumnMetaData.Builder clone() {
            return (MysqlxResultset.ColumnMetaData.Builder)super.clone();
         }

         public MysqlxResultset.ColumnMetaData.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxResultset.ColumnMetaData.Builder)super.setField(field, value);
         }

         public MysqlxResultset.ColumnMetaData.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxResultset.ColumnMetaData.Builder)super.clearField(field);
         }

         public MysqlxResultset.ColumnMetaData.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxResultset.ColumnMetaData.Builder)super.clearOneof(oneof);
         }

         public MysqlxResultset.ColumnMetaData.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxResultset.ColumnMetaData.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxResultset.ColumnMetaData.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxResultset.ColumnMetaData.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxResultset.ColumnMetaData.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxResultset.ColumnMetaData) {
               return this.mergeFrom((MysqlxResultset.ColumnMetaData)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxResultset.ColumnMetaData.Builder mergeFrom(MysqlxResultset.ColumnMetaData other) {
            if (other == MysqlxResultset.ColumnMetaData.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasType()) {
                  this.setType(other.getType());
               }

               if (other.hasName()) {
                  this.setName(other.getName());
               }

               if (other.hasOriginalName()) {
                  this.setOriginalName(other.getOriginalName());
               }

               if (other.hasTable()) {
                  this.setTable(other.getTable());
               }

               if (other.hasOriginalTable()) {
                  this.setOriginalTable(other.getOriginalTable());
               }

               if (other.hasSchema()) {
                  this.setSchema(other.getSchema());
               }

               if (other.hasCatalog()) {
                  this.setCatalog(other.getCatalog());
               }

               if (other.hasCollation()) {
                  this.setCollation(other.getCollation());
               }

               if (other.hasFractionalDigits()) {
                  this.setFractionalDigits(other.getFractionalDigits());
               }

               if (other.hasLength()) {
                  this.setLength(other.getLength());
               }

               if (other.hasFlags()) {
                  this.setFlags(other.getFlags());
               }

               if (other.hasContentType()) {
                  this.setContentType(other.getContentType());
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

         public MysqlxResultset.ColumnMetaData.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxResultset.ColumnMetaData parsedMessage = null;

            try {
               parsedMessage = MysqlxResultset.ColumnMetaData.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxResultset.ColumnMetaData)var8.getUnfinishedMessage();
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
         public MysqlxResultset.ColumnMetaData.FieldType getType() {
            MysqlxResultset.ColumnMetaData.FieldType result = MysqlxResultset.ColumnMetaData.FieldType.valueOf(this.type_);
            return result == null ? MysqlxResultset.ColumnMetaData.FieldType.SINT : result;
         }

         public MysqlxResultset.ColumnMetaData.Builder setType(MysqlxResultset.ColumnMetaData.FieldType value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.type_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxResultset.ColumnMetaData.Builder clearType() {
            this.bitField0_ &= -2;
            this.type_ = 1;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasName() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public ByteString getName() {
            return this.name_;
         }

         public MysqlxResultset.ColumnMetaData.Builder setName(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxResultset.ColumnMetaData.Builder clearName() {
            this.bitField0_ &= -3;
            this.name_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasOriginalName() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public ByteString getOriginalName() {
            return this.originalName_;
         }

         public MysqlxResultset.ColumnMetaData.Builder setOriginalName(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.originalName_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxResultset.ColumnMetaData.Builder clearOriginalName() {
            this.bitField0_ &= -5;
            this.originalName_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getOriginalName();
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasTable() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public ByteString getTable() {
            return this.table_;
         }

         public MysqlxResultset.ColumnMetaData.Builder setTable(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.table_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxResultset.ColumnMetaData.Builder clearTable() {
            this.bitField0_ &= -9;
            this.table_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getTable();
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasOriginalTable() {
            return (this.bitField0_ & 16) != 0;
         }

         @Override
         public ByteString getOriginalTable() {
            return this.originalTable_;
         }

         public MysqlxResultset.ColumnMetaData.Builder setOriginalTable(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 16;
               this.originalTable_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxResultset.ColumnMetaData.Builder clearOriginalTable() {
            this.bitField0_ &= -17;
            this.originalTable_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getOriginalTable();
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasSchema() {
            return (this.bitField0_ & 32) != 0;
         }

         @Override
         public ByteString getSchema() {
            return this.schema_;
         }

         public MysqlxResultset.ColumnMetaData.Builder setSchema(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 32;
               this.schema_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxResultset.ColumnMetaData.Builder clearSchema() {
            this.bitField0_ &= -33;
            this.schema_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getSchema();
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasCatalog() {
            return (this.bitField0_ & 64) != 0;
         }

         @Override
         public ByteString getCatalog() {
            return this.catalog_;
         }

         public MysqlxResultset.ColumnMetaData.Builder setCatalog(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 64;
               this.catalog_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxResultset.ColumnMetaData.Builder clearCatalog() {
            this.bitField0_ &= -65;
            this.catalog_ = MysqlxResultset.ColumnMetaData.getDefaultInstance().getCatalog();
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasCollation() {
            return (this.bitField0_ & 128) != 0;
         }

         @Override
         public long getCollation() {
            return this.collation_;
         }

         public MysqlxResultset.ColumnMetaData.Builder setCollation(long value) {
            this.bitField0_ |= 128;
            this.collation_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxResultset.ColumnMetaData.Builder clearCollation() {
            this.bitField0_ &= -129;
            this.collation_ = 0L;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasFractionalDigits() {
            return (this.bitField0_ & 256) != 0;
         }

         @Override
         public int getFractionalDigits() {
            return this.fractionalDigits_;
         }

         public MysqlxResultset.ColumnMetaData.Builder setFractionalDigits(int value) {
            this.bitField0_ |= 256;
            this.fractionalDigits_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxResultset.ColumnMetaData.Builder clearFractionalDigits() {
            this.bitField0_ &= -257;
            this.fractionalDigits_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasLength() {
            return (this.bitField0_ & 512) != 0;
         }

         @Override
         public int getLength() {
            return this.length_;
         }

         public MysqlxResultset.ColumnMetaData.Builder setLength(int value) {
            this.bitField0_ |= 512;
            this.length_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxResultset.ColumnMetaData.Builder clearLength() {
            this.bitField0_ &= -513;
            this.length_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasFlags() {
            return (this.bitField0_ & 1024) != 0;
         }

         @Override
         public int getFlags() {
            return this.flags_;
         }

         public MysqlxResultset.ColumnMetaData.Builder setFlags(int value) {
            this.bitField0_ |= 1024;
            this.flags_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxResultset.ColumnMetaData.Builder clearFlags() {
            this.bitField0_ &= -1025;
            this.flags_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasContentType() {
            return (this.bitField0_ & 2048) != 0;
         }

         @Override
         public int getContentType() {
            return this.contentType_;
         }

         public MysqlxResultset.ColumnMetaData.Builder setContentType(int value) {
            this.bitField0_ |= 2048;
            this.contentType_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxResultset.ColumnMetaData.Builder clearContentType() {
            this.bitField0_ &= -2049;
            this.contentType_ = 0;
            this.onChanged();
            return this;
         }

         public final MysqlxResultset.ColumnMetaData.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxResultset.ColumnMetaData.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxResultset.ColumnMetaData.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxResultset.ColumnMetaData.Builder)super.mergeUnknownFields(unknownFields);
         }
      }

      public static enum FieldType implements ProtocolMessageEnum {
         SINT(1),
         UINT(2),
         DOUBLE(5),
         FLOAT(6),
         BYTES(7),
         TIME(10),
         DATETIME(12),
         SET(15),
         ENUM(16),
         BIT(17),
         DECIMAL(18);

         public static final int SINT_VALUE = 1;
         public static final int UINT_VALUE = 2;
         public static final int DOUBLE_VALUE = 5;
         public static final int FLOAT_VALUE = 6;
         public static final int BYTES_VALUE = 7;
         public static final int TIME_VALUE = 10;
         public static final int DATETIME_VALUE = 12;
         public static final int SET_VALUE = 15;
         public static final int ENUM_VALUE = 16;
         public static final int BIT_VALUE = 17;
         public static final int DECIMAL_VALUE = 18;
         private static final Internal.EnumLiteMap<MysqlxResultset.ColumnMetaData.FieldType> internalValueMap = new Internal.EnumLiteMap<MysqlxResultset.ColumnMetaData.FieldType>() {
            public MysqlxResultset.ColumnMetaData.FieldType findValueByNumber(int number) {
               return MysqlxResultset.ColumnMetaData.FieldType.forNumber(number);
            }
         };
         private static final MysqlxResultset.ColumnMetaData.FieldType[] VALUES = values();
         private final int value;

         @Override
         public final int getNumber() {
            return this.value;
         }

         @Deprecated
         public static MysqlxResultset.ColumnMetaData.FieldType valueOf(int value) {
            return forNumber(value);
         }

         public static MysqlxResultset.ColumnMetaData.FieldType forNumber(int value) {
            switch (value) {
               case 1:
                  return SINT;
               case 2:
                  return UINT;
               case 3:
               case 4:
               case 8:
               case 9:
               case 11:
               case 13:
               case 14:
               default:
                  return null;
               case 5:
                  return DOUBLE;
               case 6:
                  return FLOAT;
               case 7:
                  return BYTES;
               case 10:
                  return TIME;
               case 12:
                  return DATETIME;
               case 15:
                  return SET;
               case 16:
                  return ENUM;
               case 17:
                  return BIT;
               case 18:
                  return DECIMAL;
            }
         }

         public static Internal.EnumLiteMap<MysqlxResultset.ColumnMetaData.FieldType> internalGetValueMap() {
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
            return MysqlxResultset.ColumnMetaData.getDescriptor().getEnumTypes().get(0);
         }

         public static MysqlxResultset.ColumnMetaData.FieldType valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != getDescriptor()) {
               throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            } else {
               return VALUES[desc.getIndex()];
            }
         }

         private FieldType(int value) {
            this.value = value;
         }
      }
   }

   public interface ColumnMetaDataOrBuilder extends MessageOrBuilder {
      boolean hasType();

      MysqlxResultset.ColumnMetaData.FieldType getType();

      boolean hasName();

      ByteString getName();

      boolean hasOriginalName();

      ByteString getOriginalName();

      boolean hasTable();

      ByteString getTable();

      boolean hasOriginalTable();

      ByteString getOriginalTable();

      boolean hasSchema();

      ByteString getSchema();

      boolean hasCatalog();

      ByteString getCatalog();

      boolean hasCollation();

      long getCollation();

      boolean hasFractionalDigits();

      int getFractionalDigits();

      boolean hasLength();

      int getLength();

      boolean hasFlags();

      int getFlags();

      boolean hasContentType();

      int getContentType();
   }

   public static enum ContentType_BYTES implements ProtocolMessageEnum {
      GEOMETRY(1),
      JSON(2),
      XML(3);

      public static final int GEOMETRY_VALUE = 1;
      public static final int JSON_VALUE = 2;
      public static final int XML_VALUE = 3;
      private static final Internal.EnumLiteMap<MysqlxResultset.ContentType_BYTES> internalValueMap = new Internal.EnumLiteMap<MysqlxResultset.ContentType_BYTES>() {
         public MysqlxResultset.ContentType_BYTES findValueByNumber(int number) {
            return MysqlxResultset.ContentType_BYTES.forNumber(number);
         }
      };
      private static final MysqlxResultset.ContentType_BYTES[] VALUES = values();
      private final int value;

      @Override
      public final int getNumber() {
         return this.value;
      }

      @Deprecated
      public static MysqlxResultset.ContentType_BYTES valueOf(int value) {
         return forNumber(value);
      }

      public static MysqlxResultset.ContentType_BYTES forNumber(int value) {
         switch (value) {
            case 1:
               return GEOMETRY;
            case 2:
               return JSON;
            case 3:
               return XML;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<MysqlxResultset.ContentType_BYTES> internalGetValueMap() {
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
         return MysqlxResultset.getDescriptor().getEnumTypes().get(0);
      }

      public static MysqlxResultset.ContentType_BYTES valueOf(Descriptors.EnumValueDescriptor desc) {
         if (desc.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return VALUES[desc.getIndex()];
         }
      }

      private ContentType_BYTES(int value) {
         this.value = value;
      }
   }

   public static enum ContentType_DATETIME implements ProtocolMessageEnum {
      DATE(1),
      DATETIME(2);

      public static final int DATE_VALUE = 1;
      public static final int DATETIME_VALUE = 2;
      private static final Internal.EnumLiteMap<MysqlxResultset.ContentType_DATETIME> internalValueMap = new Internal.EnumLiteMap<MysqlxResultset.ContentType_DATETIME>() {
         public MysqlxResultset.ContentType_DATETIME findValueByNumber(int number) {
            return MysqlxResultset.ContentType_DATETIME.forNumber(number);
         }
      };
      private static final MysqlxResultset.ContentType_DATETIME[] VALUES = values();
      private final int value;

      @Override
      public final int getNumber() {
         return this.value;
      }

      @Deprecated
      public static MysqlxResultset.ContentType_DATETIME valueOf(int value) {
         return forNumber(value);
      }

      public static MysqlxResultset.ContentType_DATETIME forNumber(int value) {
         switch (value) {
            case 1:
               return DATE;
            case 2:
               return DATETIME;
            default:
               return null;
         }
      }

      public static Internal.EnumLiteMap<MysqlxResultset.ContentType_DATETIME> internalGetValueMap() {
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
         return MysqlxResultset.getDescriptor().getEnumTypes().get(1);
      }

      public static MysqlxResultset.ContentType_DATETIME valueOf(Descriptors.EnumValueDescriptor desc) {
         if (desc.getType() != getDescriptor()) {
            throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
         } else {
            return VALUES[desc.getIndex()];
         }
      }

      private ContentType_DATETIME(int value) {
         this.value = value;
      }
   }

   public static final class FetchDone extends GeneratedMessageV3 implements MysqlxResultset.FetchDoneOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxResultset.FetchDone DEFAULT_INSTANCE = new MysqlxResultset.FetchDone();
      @Deprecated
      public static final Parser<MysqlxResultset.FetchDone> PARSER = new AbstractParser<MysqlxResultset.FetchDone>() {
         public MysqlxResultset.FetchDone parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxResultset.FetchDone(input, extensionRegistry);
         }
      };

      private FetchDone(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private FetchDone() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxResultset.FetchDone();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private FetchDone(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxResultset.FetchDone.class, MysqlxResultset.FetchDone.Builder.class);
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
         } else if (!(obj instanceof MysqlxResultset.FetchDone)) {
            return super.equals(obj);
         } else {
            MysqlxResultset.FetchDone other = (MysqlxResultset.FetchDone)obj;
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

      public static MysqlxResultset.FetchDone parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.FetchDone parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.FetchDone parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.FetchDone parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.FetchDone parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.FetchDone parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.FetchDone parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxResultset.FetchDone parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxResultset.FetchDone parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxResultset.FetchDone parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxResultset.FetchDone parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxResultset.FetchDone parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxResultset.FetchDone.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxResultset.FetchDone.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxResultset.FetchDone.Builder newBuilder(MysqlxResultset.FetchDone prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxResultset.FetchDone.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxResultset.FetchDone.Builder() : new MysqlxResultset.FetchDone.Builder().mergeFrom(this);
      }

      protected MysqlxResultset.FetchDone.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxResultset.FetchDone.Builder(parent);
      }

      public static MysqlxResultset.FetchDone getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxResultset.FetchDone> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxResultset.FetchDone> getParserForType() {
         return PARSER;
      }

      public MysqlxResultset.FetchDone getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxResultset.FetchDone.Builder> implements MysqlxResultset.FetchDoneOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxResultset.FetchDone.class, MysqlxResultset.FetchDone.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxResultset.FetchDone.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxResultset.FetchDone.Builder clear() {
            super.clear();
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDone_descriptor;
         }

         public MysqlxResultset.FetchDone getDefaultInstanceForType() {
            return MysqlxResultset.FetchDone.getDefaultInstance();
         }

         public MysqlxResultset.FetchDone build() {
            MysqlxResultset.FetchDone result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxResultset.FetchDone buildPartial() {
            MysqlxResultset.FetchDone result = new MysqlxResultset.FetchDone(this);
            this.onBuilt();
            return result;
         }

         public MysqlxResultset.FetchDone.Builder clone() {
            return (MysqlxResultset.FetchDone.Builder)super.clone();
         }

         public MysqlxResultset.FetchDone.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxResultset.FetchDone.Builder)super.setField(field, value);
         }

         public MysqlxResultset.FetchDone.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxResultset.FetchDone.Builder)super.clearField(field);
         }

         public MysqlxResultset.FetchDone.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxResultset.FetchDone.Builder)super.clearOneof(oneof);
         }

         public MysqlxResultset.FetchDone.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxResultset.FetchDone.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxResultset.FetchDone.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxResultset.FetchDone.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxResultset.FetchDone.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxResultset.FetchDone) {
               return this.mergeFrom((MysqlxResultset.FetchDone)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxResultset.FetchDone.Builder mergeFrom(MysqlxResultset.FetchDone other) {
            if (other == MysqlxResultset.FetchDone.getDefaultInstance()) {
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

         public MysqlxResultset.FetchDone.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxResultset.FetchDone parsedMessage = null;

            try {
               parsedMessage = MysqlxResultset.FetchDone.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxResultset.FetchDone)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         public final MysqlxResultset.FetchDone.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxResultset.FetchDone.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxResultset.FetchDone.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxResultset.FetchDone.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public static final class FetchDoneMoreOutParams extends GeneratedMessageV3 implements MysqlxResultset.FetchDoneMoreOutParamsOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxResultset.FetchDoneMoreOutParams DEFAULT_INSTANCE = new MysqlxResultset.FetchDoneMoreOutParams();
      @Deprecated
      public static final Parser<MysqlxResultset.FetchDoneMoreOutParams> PARSER = new AbstractParser<MysqlxResultset.FetchDoneMoreOutParams>() {
         public MysqlxResultset.FetchDoneMoreOutParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxResultset.FetchDoneMoreOutParams(input, extensionRegistry);
         }
      };

      private FetchDoneMoreOutParams(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private FetchDoneMoreOutParams() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxResultset.FetchDoneMoreOutParams();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private FetchDoneMoreOutParams(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxResultset.FetchDoneMoreOutParams.class, MysqlxResultset.FetchDoneMoreOutParams.Builder.class);
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
         } else if (!(obj instanceof MysqlxResultset.FetchDoneMoreOutParams)) {
            return super.equals(obj);
         } else {
            MysqlxResultset.FetchDoneMoreOutParams other = (MysqlxResultset.FetchDoneMoreOutParams)obj;
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

      public static MysqlxResultset.FetchDoneMoreOutParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.FetchDoneMoreOutParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.FetchDoneMoreOutParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.FetchDoneMoreOutParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.FetchDoneMoreOutParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.FetchDoneMoreOutParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.FetchDoneMoreOutParams parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxResultset.FetchDoneMoreOutParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxResultset.FetchDoneMoreOutParams parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxResultset.FetchDoneMoreOutParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxResultset.FetchDoneMoreOutParams parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxResultset.FetchDoneMoreOutParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxResultset.FetchDoneMoreOutParams.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxResultset.FetchDoneMoreOutParams.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxResultset.FetchDoneMoreOutParams.Builder newBuilder(MysqlxResultset.FetchDoneMoreOutParams prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxResultset.FetchDoneMoreOutParams.Builder toBuilder() {
         return this == DEFAULT_INSTANCE
            ? new MysqlxResultset.FetchDoneMoreOutParams.Builder()
            : new MysqlxResultset.FetchDoneMoreOutParams.Builder().mergeFrom(this);
      }

      protected MysqlxResultset.FetchDoneMoreOutParams.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxResultset.FetchDoneMoreOutParams.Builder(parent);
      }

      public static MysqlxResultset.FetchDoneMoreOutParams getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxResultset.FetchDoneMoreOutParams> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxResultset.FetchDoneMoreOutParams> getParserForType() {
         return PARSER;
      }

      public MysqlxResultset.FetchDoneMoreOutParams getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxResultset.FetchDoneMoreOutParams.Builder>
         implements MysqlxResultset.FetchDoneMoreOutParamsOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxResultset.FetchDoneMoreOutParams.class, MysqlxResultset.FetchDoneMoreOutParams.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxResultset.FetchDoneMoreOutParams.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxResultset.FetchDoneMoreOutParams.Builder clear() {
            super.clear();
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreOutParams_descriptor;
         }

         public MysqlxResultset.FetchDoneMoreOutParams getDefaultInstanceForType() {
            return MysqlxResultset.FetchDoneMoreOutParams.getDefaultInstance();
         }

         public MysqlxResultset.FetchDoneMoreOutParams build() {
            MysqlxResultset.FetchDoneMoreOutParams result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxResultset.FetchDoneMoreOutParams buildPartial() {
            MysqlxResultset.FetchDoneMoreOutParams result = new MysqlxResultset.FetchDoneMoreOutParams(this);
            this.onBuilt();
            return result;
         }

         public MysqlxResultset.FetchDoneMoreOutParams.Builder clone() {
            return (MysqlxResultset.FetchDoneMoreOutParams.Builder)super.clone();
         }

         public MysqlxResultset.FetchDoneMoreOutParams.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxResultset.FetchDoneMoreOutParams.Builder)super.setField(field, value);
         }

         public MysqlxResultset.FetchDoneMoreOutParams.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxResultset.FetchDoneMoreOutParams.Builder)super.clearField(field);
         }

         public MysqlxResultset.FetchDoneMoreOutParams.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxResultset.FetchDoneMoreOutParams.Builder)super.clearOneof(oneof);
         }

         public MysqlxResultset.FetchDoneMoreOutParams.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxResultset.FetchDoneMoreOutParams.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxResultset.FetchDoneMoreOutParams.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxResultset.FetchDoneMoreOutParams.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxResultset.FetchDoneMoreOutParams.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxResultset.FetchDoneMoreOutParams) {
               return this.mergeFrom((MysqlxResultset.FetchDoneMoreOutParams)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxResultset.FetchDoneMoreOutParams.Builder mergeFrom(MysqlxResultset.FetchDoneMoreOutParams other) {
            if (other == MysqlxResultset.FetchDoneMoreOutParams.getDefaultInstance()) {
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

         public MysqlxResultset.FetchDoneMoreOutParams.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxResultset.FetchDoneMoreOutParams parsedMessage = null;

            try {
               parsedMessage = MysqlxResultset.FetchDoneMoreOutParams.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxResultset.FetchDoneMoreOutParams)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         public final MysqlxResultset.FetchDoneMoreOutParams.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxResultset.FetchDoneMoreOutParams.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxResultset.FetchDoneMoreOutParams.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxResultset.FetchDoneMoreOutParams.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface FetchDoneMoreOutParamsOrBuilder extends MessageOrBuilder {
   }

   public static final class FetchDoneMoreResultsets extends GeneratedMessageV3 implements MysqlxResultset.FetchDoneMoreResultsetsOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxResultset.FetchDoneMoreResultsets DEFAULT_INSTANCE = new MysqlxResultset.FetchDoneMoreResultsets();
      @Deprecated
      public static final Parser<MysqlxResultset.FetchDoneMoreResultsets> PARSER = new AbstractParser<MysqlxResultset.FetchDoneMoreResultsets>() {
         public MysqlxResultset.FetchDoneMoreResultsets parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxResultset.FetchDoneMoreResultsets(input, extensionRegistry);
         }
      };

      private FetchDoneMoreResultsets(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private FetchDoneMoreResultsets() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxResultset.FetchDoneMoreResultsets();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private FetchDoneMoreResultsets(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxResultset.FetchDoneMoreResultsets.class, MysqlxResultset.FetchDoneMoreResultsets.Builder.class);
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
         } else if (!(obj instanceof MysqlxResultset.FetchDoneMoreResultsets)) {
            return super.equals(obj);
         } else {
            MysqlxResultset.FetchDoneMoreResultsets other = (MysqlxResultset.FetchDoneMoreResultsets)obj;
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

      public static MysqlxResultset.FetchDoneMoreResultsets parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.FetchDoneMoreResultsets parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.FetchDoneMoreResultsets parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.FetchDoneMoreResultsets parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.FetchDoneMoreResultsets parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.FetchDoneMoreResultsets parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.FetchDoneMoreResultsets parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxResultset.FetchDoneMoreResultsets parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxResultset.FetchDoneMoreResultsets parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxResultset.FetchDoneMoreResultsets parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxResultset.FetchDoneMoreResultsets parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxResultset.FetchDoneMoreResultsets parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxResultset.FetchDoneMoreResultsets.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxResultset.FetchDoneMoreResultsets.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxResultset.FetchDoneMoreResultsets.Builder newBuilder(MysqlxResultset.FetchDoneMoreResultsets prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxResultset.FetchDoneMoreResultsets.Builder toBuilder() {
         return this == DEFAULT_INSTANCE
            ? new MysqlxResultset.FetchDoneMoreResultsets.Builder()
            : new MysqlxResultset.FetchDoneMoreResultsets.Builder().mergeFrom(this);
      }

      protected MysqlxResultset.FetchDoneMoreResultsets.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxResultset.FetchDoneMoreResultsets.Builder(parent);
      }

      public static MysqlxResultset.FetchDoneMoreResultsets getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxResultset.FetchDoneMoreResultsets> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxResultset.FetchDoneMoreResultsets> getParserForType() {
         return PARSER;
      }

      public MysqlxResultset.FetchDoneMoreResultsets getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxResultset.FetchDoneMoreResultsets.Builder>
         implements MysqlxResultset.FetchDoneMoreResultsetsOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxResultset.FetchDoneMoreResultsets.class, MysqlxResultset.FetchDoneMoreResultsets.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxResultset.FetchDoneMoreResultsets.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxResultset.FetchDoneMoreResultsets.Builder clear() {
            super.clear();
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchDoneMoreResultsets_descriptor;
         }

         public MysqlxResultset.FetchDoneMoreResultsets getDefaultInstanceForType() {
            return MysqlxResultset.FetchDoneMoreResultsets.getDefaultInstance();
         }

         public MysqlxResultset.FetchDoneMoreResultsets build() {
            MysqlxResultset.FetchDoneMoreResultsets result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxResultset.FetchDoneMoreResultsets buildPartial() {
            MysqlxResultset.FetchDoneMoreResultsets result = new MysqlxResultset.FetchDoneMoreResultsets(this);
            this.onBuilt();
            return result;
         }

         public MysqlxResultset.FetchDoneMoreResultsets.Builder clone() {
            return (MysqlxResultset.FetchDoneMoreResultsets.Builder)super.clone();
         }

         public MysqlxResultset.FetchDoneMoreResultsets.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxResultset.FetchDoneMoreResultsets.Builder)super.setField(field, value);
         }

         public MysqlxResultset.FetchDoneMoreResultsets.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxResultset.FetchDoneMoreResultsets.Builder)super.clearField(field);
         }

         public MysqlxResultset.FetchDoneMoreResultsets.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxResultset.FetchDoneMoreResultsets.Builder)super.clearOneof(oneof);
         }

         public MysqlxResultset.FetchDoneMoreResultsets.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxResultset.FetchDoneMoreResultsets.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxResultset.FetchDoneMoreResultsets.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxResultset.FetchDoneMoreResultsets.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxResultset.FetchDoneMoreResultsets.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxResultset.FetchDoneMoreResultsets) {
               return this.mergeFrom((MysqlxResultset.FetchDoneMoreResultsets)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxResultset.FetchDoneMoreResultsets.Builder mergeFrom(MysqlxResultset.FetchDoneMoreResultsets other) {
            if (other == MysqlxResultset.FetchDoneMoreResultsets.getDefaultInstance()) {
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

         public MysqlxResultset.FetchDoneMoreResultsets.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxResultset.FetchDoneMoreResultsets parsedMessage = null;

            try {
               parsedMessage = MysqlxResultset.FetchDoneMoreResultsets.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxResultset.FetchDoneMoreResultsets)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         public final MysqlxResultset.FetchDoneMoreResultsets.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxResultset.FetchDoneMoreResultsets.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxResultset.FetchDoneMoreResultsets.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxResultset.FetchDoneMoreResultsets.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface FetchDoneMoreResultsetsOrBuilder extends MessageOrBuilder {
   }

   public interface FetchDoneOrBuilder extends MessageOrBuilder {
   }

   public static final class FetchSuspended extends GeneratedMessageV3 implements MysqlxResultset.FetchSuspendedOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxResultset.FetchSuspended DEFAULT_INSTANCE = new MysqlxResultset.FetchSuspended();
      @Deprecated
      public static final Parser<MysqlxResultset.FetchSuspended> PARSER = new AbstractParser<MysqlxResultset.FetchSuspended>() {
         public MysqlxResultset.FetchSuspended parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxResultset.FetchSuspended(input, extensionRegistry);
         }
      };

      private FetchSuspended(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private FetchSuspended() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxResultset.FetchSuspended();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private FetchSuspended(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxResultset.FetchSuspended.class, MysqlxResultset.FetchSuspended.Builder.class);
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
         } else if (!(obj instanceof MysqlxResultset.FetchSuspended)) {
            return super.equals(obj);
         } else {
            MysqlxResultset.FetchSuspended other = (MysqlxResultset.FetchSuspended)obj;
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

      public static MysqlxResultset.FetchSuspended parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.FetchSuspended parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.FetchSuspended parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.FetchSuspended parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.FetchSuspended parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.FetchSuspended parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.FetchSuspended parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxResultset.FetchSuspended parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxResultset.FetchSuspended parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxResultset.FetchSuspended parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxResultset.FetchSuspended parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxResultset.FetchSuspended parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxResultset.FetchSuspended.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxResultset.FetchSuspended.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxResultset.FetchSuspended.Builder newBuilder(MysqlxResultset.FetchSuspended prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxResultset.FetchSuspended.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxResultset.FetchSuspended.Builder() : new MysqlxResultset.FetchSuspended.Builder().mergeFrom(this);
      }

      protected MysqlxResultset.FetchSuspended.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxResultset.FetchSuspended.Builder(parent);
      }

      public static MysqlxResultset.FetchSuspended getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxResultset.FetchSuspended> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxResultset.FetchSuspended> getParserForType() {
         return PARSER;
      }

      public MysqlxResultset.FetchSuspended getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxResultset.FetchSuspended.Builder>
         implements MysqlxResultset.FetchSuspendedOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxResultset.FetchSuspended.class, MysqlxResultset.FetchSuspended.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxResultset.FetchSuspended.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxResultset.FetchSuspended.Builder clear() {
            super.clear();
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_FetchSuspended_descriptor;
         }

         public MysqlxResultset.FetchSuspended getDefaultInstanceForType() {
            return MysqlxResultset.FetchSuspended.getDefaultInstance();
         }

         public MysqlxResultset.FetchSuspended build() {
            MysqlxResultset.FetchSuspended result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxResultset.FetchSuspended buildPartial() {
            MysqlxResultset.FetchSuspended result = new MysqlxResultset.FetchSuspended(this);
            this.onBuilt();
            return result;
         }

         public MysqlxResultset.FetchSuspended.Builder clone() {
            return (MysqlxResultset.FetchSuspended.Builder)super.clone();
         }

         public MysqlxResultset.FetchSuspended.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxResultset.FetchSuspended.Builder)super.setField(field, value);
         }

         public MysqlxResultset.FetchSuspended.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxResultset.FetchSuspended.Builder)super.clearField(field);
         }

         public MysqlxResultset.FetchSuspended.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxResultset.FetchSuspended.Builder)super.clearOneof(oneof);
         }

         public MysqlxResultset.FetchSuspended.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxResultset.FetchSuspended.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxResultset.FetchSuspended.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxResultset.FetchSuspended.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxResultset.FetchSuspended.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxResultset.FetchSuspended) {
               return this.mergeFrom((MysqlxResultset.FetchSuspended)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxResultset.FetchSuspended.Builder mergeFrom(MysqlxResultset.FetchSuspended other) {
            if (other == MysqlxResultset.FetchSuspended.getDefaultInstance()) {
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

         public MysqlxResultset.FetchSuspended.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxResultset.FetchSuspended parsedMessage = null;

            try {
               parsedMessage = MysqlxResultset.FetchSuspended.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxResultset.FetchSuspended)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         public final MysqlxResultset.FetchSuspended.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxResultset.FetchSuspended.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxResultset.FetchSuspended.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxResultset.FetchSuspended.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface FetchSuspendedOrBuilder extends MessageOrBuilder {
   }

   public static final class Row extends GeneratedMessageV3 implements MysqlxResultset.RowOrBuilder {
      private static final long serialVersionUID = 0L;
      public static final int FIELD_FIELD_NUMBER = 1;
      private List<ByteString> field_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxResultset.Row DEFAULT_INSTANCE = new MysqlxResultset.Row();
      @Deprecated
      public static final Parser<MysqlxResultset.Row> PARSER = new AbstractParser<MysqlxResultset.Row>() {
         public MysqlxResultset.Row parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxResultset.Row(input, extensionRegistry);
         }
      };

      private Row(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Row() {
         this.field_ = Collections.emptyList();
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxResultset.Row();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Row(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                           this.field_ = new ArrayList<>();
                           mutable_bitField0_ |= 1;
                        }

                        this.field_.add(input.readBytes());
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
                  this.field_ = Collections.unmodifiableList(this.field_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxResultset.Row.class, MysqlxResultset.Row.Builder.class);
      }

      @Override
      public List<ByteString> getFieldList() {
         return this.field_;
      }

      @Override
      public int getFieldCount() {
         return this.field_.size();
      }

      @Override
      public ByteString getField(int index) {
         return this.field_.get(index);
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
         for (int i = 0; i < this.field_.size(); i++) {
            output.writeBytes(1, this.field_.get(i));
         }

         this.unknownFields.writeTo(output);
      }

      @Override
      public int getSerializedSize() {
         int size = this.memoizedSize;
         if (size != -1) {
            return size;
         } else {
            int var4 = 0;
            int dataSize = 0;

            for (int i = 0; i < this.field_.size(); i++) {
               dataSize += CodedOutputStream.computeBytesSizeNoTag(this.field_.get(i));
            }

            var4 += dataSize;
            var4 += 1 * this.getFieldList().size();
            var4 += this.unknownFields.getSerializedSize();
            this.memoizedSize = var4;
            return var4;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof MysqlxResultset.Row)) {
            return super.equals(obj);
         } else {
            MysqlxResultset.Row other = (MysqlxResultset.Row)obj;
            return !this.getFieldList().equals(other.getFieldList()) ? false : this.unknownFields.equals(other.unknownFields);
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.getFieldCount() > 0) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getFieldList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxResultset.Row parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.Row parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.Row parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.Row parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.Row parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxResultset.Row parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxResultset.Row parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxResultset.Row parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxResultset.Row parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxResultset.Row parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxResultset.Row parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxResultset.Row parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxResultset.Row.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxResultset.Row.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxResultset.Row.Builder newBuilder(MysqlxResultset.Row prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxResultset.Row.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxResultset.Row.Builder() : new MysqlxResultset.Row.Builder().mergeFrom(this);
      }

      protected MysqlxResultset.Row.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxResultset.Row.Builder(parent);
      }

      public static MysqlxResultset.Row getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxResultset.Row> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxResultset.Row> getParserForType() {
         return PARSER;
      }

      public MysqlxResultset.Row getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxResultset.Row.Builder> implements MysqlxResultset.RowOrBuilder {
         private int bitField0_;
         private List<ByteString> field_ = Collections.emptyList();

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxResultset.Row.class, MysqlxResultset.Row.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxResultset.Row.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxResultset.Row.Builder clear() {
            super.clear();
            this.field_ = Collections.emptyList();
            this.bitField0_ &= -2;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxResultset.internal_static_Mysqlx_Resultset_Row_descriptor;
         }

         public MysqlxResultset.Row getDefaultInstanceForType() {
            return MysqlxResultset.Row.getDefaultInstance();
         }

         public MysqlxResultset.Row build() {
            MysqlxResultset.Row result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxResultset.Row buildPartial() {
            MysqlxResultset.Row result = new MysqlxResultset.Row(this);
            int from_bitField0_ = this.bitField0_;
            if ((this.bitField0_ & 1) != 0) {
               this.field_ = Collections.unmodifiableList(this.field_);
               this.bitField0_ &= -2;
            }

            result.field_ = this.field_;
            this.onBuilt();
            return result;
         }

         public MysqlxResultset.Row.Builder clone() {
            return (MysqlxResultset.Row.Builder)super.clone();
         }

         public MysqlxResultset.Row.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxResultset.Row.Builder)super.setField(field, value);
         }

         public MysqlxResultset.Row.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxResultset.Row.Builder)super.clearField(field);
         }

         public MysqlxResultset.Row.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxResultset.Row.Builder)super.clearOneof(oneof);
         }

         public MysqlxResultset.Row.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxResultset.Row.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxResultset.Row.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxResultset.Row.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxResultset.Row.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxResultset.Row) {
               return this.mergeFrom((MysqlxResultset.Row)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxResultset.Row.Builder mergeFrom(MysqlxResultset.Row other) {
            if (other == MysqlxResultset.Row.getDefaultInstance()) {
               return this;
            } else {
               if (!other.field_.isEmpty()) {
                  if (this.field_.isEmpty()) {
                     this.field_ = other.field_;
                     this.bitField0_ &= -2;
                  } else {
                     this.ensureFieldIsMutable();
                     this.field_.addAll(other.field_);
                  }

                  this.onChanged();
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return true;
         }

         public MysqlxResultset.Row.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxResultset.Row parsedMessage = null;

            try {
               parsedMessage = MysqlxResultset.Row.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxResultset.Row)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         private void ensureFieldIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
               this.field_ = new ArrayList<>(this.field_);
               this.bitField0_ |= 1;
            }
         }

         @Override
         public List<ByteString> getFieldList() {
            return (this.bitField0_ & 1) != 0 ? Collections.unmodifiableList(this.field_) : this.field_;
         }

         @Override
         public int getFieldCount() {
            return this.field_.size();
         }

         @Override
         public ByteString getField(int index) {
            return this.field_.get(index);
         }

         public MysqlxResultset.Row.Builder setField(int index, ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureFieldIsMutable();
               this.field_.set(index, value);
               this.onChanged();
               return this;
            }
         }

         public MysqlxResultset.Row.Builder addField(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.ensureFieldIsMutable();
               this.field_.add(value);
               this.onChanged();
               return this;
            }
         }

         public MysqlxResultset.Row.Builder addAllField(Iterable<? extends ByteString> values) {
            this.ensureFieldIsMutable();
            AbstractMessageLite.Builder.addAll(values, this.field_);
            this.onChanged();
            return this;
         }

         public MysqlxResultset.Row.Builder clearField() {
            this.field_ = Collections.emptyList();
            this.bitField0_ &= -2;
            this.onChanged();
            return this;
         }

         public final MysqlxResultset.Row.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxResultset.Row.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxResultset.Row.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxResultset.Row.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface RowOrBuilder extends MessageOrBuilder {
      List<ByteString> getFieldList();

      int getFieldCount();

      ByteString getField(int var1);
   }
}
