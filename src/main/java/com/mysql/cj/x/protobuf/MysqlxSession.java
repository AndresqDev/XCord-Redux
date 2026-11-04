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
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class MysqlxSession {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_AuthenticateStart_descriptor = getDescriptor().getMessageTypes().get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_AuthenticateStart_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Session_AuthenticateStart_descriptor, new String[]{"MechName", "AuthData", "InitialResponse"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_AuthenticateContinue_descriptor = getDescriptor().getMessageTypes().get(1);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_AuthenticateContinue_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Session_AuthenticateContinue_descriptor, new String[]{"AuthData"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_AuthenticateOk_descriptor = getDescriptor().getMessageTypes().get(2);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_AuthenticateOk_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Session_AuthenticateOk_descriptor, new String[]{"AuthData"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_Reset_descriptor = getDescriptor().getMessageTypes().get(3);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_Reset_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Session_Reset_descriptor, new String[]{"KeepOpen"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Session_Close_descriptor = getDescriptor().getMessageTypes().get(4);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Session_Close_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Session_Close_descriptor, new String[0]
   );
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxSession() {
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
         "\n\u0014mysqlx_session.proto\u0012\u000eMysqlx.Session\u001a\fmysqlx.proto\"Y\n\u0011AuthenticateStart\u0012\u0011\n\tmech_name\u0018\u0001 \u0002(\t\u0012\u0011\n\tauth_data\u0018\u0002 \u0001(\f\u0012\u0018\n\u0010initial_response\u0018\u0003 \u0001(\f:\u0004\u0088ê0\u0004\"3\n\u0014AuthenticateContinue\u0012\u0011\n\tauth_data\u0018\u0001 \u0002(\f:\b\u0090ê0\u0003\u0088ê0\u0005\")\n\u000eAuthenticateOk\u0012\u0011\n\tauth_data\u0018\u0001 \u0001(\f:\u0004\u0090ê0\u0004\"'\n\u0005Reset\u0012\u0018\n\tkeep_open\u0018\u0001 \u0001(\b:\u0005false:\u0004\u0088ê0\u0006\"\r\n\u0005Close:\u0004\u0088ê0\u0007B\u0019\n\u0017com.mysql.cj.x.protobuf"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[]{Mysqlx.getDescriptor()});
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.clientMessageId);
      registry.add(Mysqlx.serverMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      Mysqlx.getDescriptor();
   }

   public static final class AuthenticateContinue extends GeneratedMessageV3 implements MysqlxSession.AuthenticateContinueOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int AUTH_DATA_FIELD_NUMBER = 1;
      private ByteString authData_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxSession.AuthenticateContinue DEFAULT_INSTANCE = new MysqlxSession.AuthenticateContinue();
      @Deprecated
      public static final Parser<MysqlxSession.AuthenticateContinue> PARSER = new AbstractParser<MysqlxSession.AuthenticateContinue>() {
         public MysqlxSession.AuthenticateContinue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxSession.AuthenticateContinue(input, extensionRegistry);
         }
      };

      private AuthenticateContinue(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private AuthenticateContinue() {
         this.authData_ = ByteString.EMPTY;
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxSession.AuthenticateContinue();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private AuthenticateContinue(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.bitField0_ |= 1;
                        this.authData_ = input.readBytes();
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
         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxSession.AuthenticateContinue.class, MysqlxSession.AuthenticateContinue.Builder.class);
      }

      @Override
      public boolean hasAuthData() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public ByteString getAuthData() {
         return this.authData_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasAuthData()) {
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
            output.writeBytes(1, this.authData_);
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
               size += CodedOutputStream.computeBytesSize(1, this.authData_);
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
         } else if (!(obj instanceof MysqlxSession.AuthenticateContinue)) {
            return super.equals(obj);
         } else {
            MysqlxSession.AuthenticateContinue other = (MysqlxSession.AuthenticateContinue)obj;
            if (this.hasAuthData() != other.hasAuthData()) {
               return false;
            } else {
               return this.hasAuthData() && !this.getAuthData().equals(other.getAuthData()) ? false : this.unknownFields.equals(other.unknownFields);
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
            if (this.hasAuthData()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getAuthData().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxSession.AuthenticateContinue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.AuthenticateContinue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateContinue parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.AuthenticateContinue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateContinue parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.AuthenticateContinue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateContinue parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSession.AuthenticateContinue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateContinue parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxSession.AuthenticateContinue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateContinue parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSession.AuthenticateContinue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxSession.AuthenticateContinue.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxSession.AuthenticateContinue.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxSession.AuthenticateContinue.Builder newBuilder(MysqlxSession.AuthenticateContinue prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxSession.AuthenticateContinue.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxSession.AuthenticateContinue.Builder() : new MysqlxSession.AuthenticateContinue.Builder().mergeFrom(this);
      }

      protected MysqlxSession.AuthenticateContinue.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxSession.AuthenticateContinue.Builder(parent);
      }

      public static MysqlxSession.AuthenticateContinue getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxSession.AuthenticateContinue> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxSession.AuthenticateContinue> getParserForType() {
         return PARSER;
      }

      public MysqlxSession.AuthenticateContinue getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxSession.AuthenticateContinue.Builder>
         implements MysqlxSession.AuthenticateContinueOrBuilder {
         private int bitField0_;
         private ByteString authData_ = ByteString.EMPTY;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxSession.AuthenticateContinue.class, MysqlxSession.AuthenticateContinue.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSession.AuthenticateContinue.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxSession.AuthenticateContinue.Builder clear() {
            super.clear();
            this.authData_ = ByteString.EMPTY;
            this.bitField0_ &= -2;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateContinue_descriptor;
         }

         public MysqlxSession.AuthenticateContinue getDefaultInstanceForType() {
            return MysqlxSession.AuthenticateContinue.getDefaultInstance();
         }

         public MysqlxSession.AuthenticateContinue build() {
            MysqlxSession.AuthenticateContinue result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxSession.AuthenticateContinue buildPartial() {
            MysqlxSession.AuthenticateContinue result = new MysqlxSession.AuthenticateContinue(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.authData_ = this.authData_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxSession.AuthenticateContinue.Builder clone() {
            return (MysqlxSession.AuthenticateContinue.Builder)super.clone();
         }

         public MysqlxSession.AuthenticateContinue.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSession.AuthenticateContinue.Builder)super.setField(field, value);
         }

         public MysqlxSession.AuthenticateContinue.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxSession.AuthenticateContinue.Builder)super.clearField(field);
         }

         public MysqlxSession.AuthenticateContinue.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxSession.AuthenticateContinue.Builder)super.clearOneof(oneof);
         }

         public MysqlxSession.AuthenticateContinue.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxSession.AuthenticateContinue.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxSession.AuthenticateContinue.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSession.AuthenticateContinue.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxSession.AuthenticateContinue.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxSession.AuthenticateContinue) {
               return this.mergeFrom((MysqlxSession.AuthenticateContinue)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxSession.AuthenticateContinue.Builder mergeFrom(MysqlxSession.AuthenticateContinue other) {
            if (other == MysqlxSession.AuthenticateContinue.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasAuthData()) {
                  this.setAuthData(other.getAuthData());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return this.hasAuthData();
         }

         public MysqlxSession.AuthenticateContinue.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxSession.AuthenticateContinue parsedMessage = null;

            try {
               parsedMessage = MysqlxSession.AuthenticateContinue.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxSession.AuthenticateContinue)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasAuthData() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public ByteString getAuthData() {
            return this.authData_;
         }

         public MysqlxSession.AuthenticateContinue.Builder setAuthData(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.authData_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxSession.AuthenticateContinue.Builder clearAuthData() {
            this.bitField0_ &= -2;
            this.authData_ = MysqlxSession.AuthenticateContinue.getDefaultInstance().getAuthData();
            this.onChanged();
            return this;
         }

         public final MysqlxSession.AuthenticateContinue.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSession.AuthenticateContinue.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxSession.AuthenticateContinue.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSession.AuthenticateContinue.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface AuthenticateContinueOrBuilder extends MessageOrBuilder {
      boolean hasAuthData();

      ByteString getAuthData();
   }

   public static final class AuthenticateOk extends GeneratedMessageV3 implements MysqlxSession.AuthenticateOkOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int AUTH_DATA_FIELD_NUMBER = 1;
      private ByteString authData_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxSession.AuthenticateOk DEFAULT_INSTANCE = new MysqlxSession.AuthenticateOk();
      @Deprecated
      public static final Parser<MysqlxSession.AuthenticateOk> PARSER = new AbstractParser<MysqlxSession.AuthenticateOk>() {
         public MysqlxSession.AuthenticateOk parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxSession.AuthenticateOk(input, extensionRegistry);
         }
      };

      private AuthenticateOk(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private AuthenticateOk() {
         this.authData_ = ByteString.EMPTY;
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxSession.AuthenticateOk();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private AuthenticateOk(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.bitField0_ |= 1;
                        this.authData_ = input.readBytes();
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
         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxSession.AuthenticateOk.class, MysqlxSession.AuthenticateOk.Builder.class);
      }

      @Override
      public boolean hasAuthData() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public ByteString getAuthData() {
         return this.authData_;
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
         if ((this.bitField0_ & 1) != 0) {
            output.writeBytes(1, this.authData_);
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
               size += CodedOutputStream.computeBytesSize(1, this.authData_);
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
         } else if (!(obj instanceof MysqlxSession.AuthenticateOk)) {
            return super.equals(obj);
         } else {
            MysqlxSession.AuthenticateOk other = (MysqlxSession.AuthenticateOk)obj;
            if (this.hasAuthData() != other.hasAuthData()) {
               return false;
            } else {
               return this.hasAuthData() && !this.getAuthData().equals(other.getAuthData()) ? false : this.unknownFields.equals(other.unknownFields);
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
            if (this.hasAuthData()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getAuthData().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxSession.AuthenticateOk parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.AuthenticateOk parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateOk parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.AuthenticateOk parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateOk parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.AuthenticateOk parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateOk parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSession.AuthenticateOk parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateOk parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxSession.AuthenticateOk parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateOk parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSession.AuthenticateOk parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxSession.AuthenticateOk.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxSession.AuthenticateOk.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxSession.AuthenticateOk.Builder newBuilder(MysqlxSession.AuthenticateOk prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxSession.AuthenticateOk.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxSession.AuthenticateOk.Builder() : new MysqlxSession.AuthenticateOk.Builder().mergeFrom(this);
      }

      protected MysqlxSession.AuthenticateOk.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxSession.AuthenticateOk.Builder(parent);
      }

      public static MysqlxSession.AuthenticateOk getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxSession.AuthenticateOk> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxSession.AuthenticateOk> getParserForType() {
         return PARSER;
      }

      public MysqlxSession.AuthenticateOk getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxSession.AuthenticateOk.Builder>
         implements MysqlxSession.AuthenticateOkOrBuilder {
         private int bitField0_;
         private ByteString authData_ = ByteString.EMPTY;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxSession.AuthenticateOk.class, MysqlxSession.AuthenticateOk.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSession.AuthenticateOk.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxSession.AuthenticateOk.Builder clear() {
            super.clear();
            this.authData_ = ByteString.EMPTY;
            this.bitField0_ &= -2;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateOk_descriptor;
         }

         public MysqlxSession.AuthenticateOk getDefaultInstanceForType() {
            return MysqlxSession.AuthenticateOk.getDefaultInstance();
         }

         public MysqlxSession.AuthenticateOk build() {
            MysqlxSession.AuthenticateOk result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxSession.AuthenticateOk buildPartial() {
            MysqlxSession.AuthenticateOk result = new MysqlxSession.AuthenticateOk(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.authData_ = this.authData_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxSession.AuthenticateOk.Builder clone() {
            return (MysqlxSession.AuthenticateOk.Builder)super.clone();
         }

         public MysqlxSession.AuthenticateOk.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSession.AuthenticateOk.Builder)super.setField(field, value);
         }

         public MysqlxSession.AuthenticateOk.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxSession.AuthenticateOk.Builder)super.clearField(field);
         }

         public MysqlxSession.AuthenticateOk.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxSession.AuthenticateOk.Builder)super.clearOneof(oneof);
         }

         public MysqlxSession.AuthenticateOk.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxSession.AuthenticateOk.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxSession.AuthenticateOk.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSession.AuthenticateOk.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxSession.AuthenticateOk.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxSession.AuthenticateOk) {
               return this.mergeFrom((MysqlxSession.AuthenticateOk)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxSession.AuthenticateOk.Builder mergeFrom(MysqlxSession.AuthenticateOk other) {
            if (other == MysqlxSession.AuthenticateOk.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasAuthData()) {
                  this.setAuthData(other.getAuthData());
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

         public MysqlxSession.AuthenticateOk.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxSession.AuthenticateOk parsedMessage = null;

            try {
               parsedMessage = MysqlxSession.AuthenticateOk.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxSession.AuthenticateOk)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasAuthData() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public ByteString getAuthData() {
            return this.authData_;
         }

         public MysqlxSession.AuthenticateOk.Builder setAuthData(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.authData_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxSession.AuthenticateOk.Builder clearAuthData() {
            this.bitField0_ &= -2;
            this.authData_ = MysqlxSession.AuthenticateOk.getDefaultInstance().getAuthData();
            this.onChanged();
            return this;
         }

         public final MysqlxSession.AuthenticateOk.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSession.AuthenticateOk.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxSession.AuthenticateOk.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSession.AuthenticateOk.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface AuthenticateOkOrBuilder extends MessageOrBuilder {
      boolean hasAuthData();

      ByteString getAuthData();
   }

   public static final class AuthenticateStart extends GeneratedMessageV3 implements MysqlxSession.AuthenticateStartOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int MECH_NAME_FIELD_NUMBER = 1;
      private volatile Object mechName_;
      public static final int AUTH_DATA_FIELD_NUMBER = 2;
      private ByteString authData_;
      public static final int INITIAL_RESPONSE_FIELD_NUMBER = 3;
      private ByteString initialResponse_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxSession.AuthenticateStart DEFAULT_INSTANCE = new MysqlxSession.AuthenticateStart();
      @Deprecated
      public static final Parser<MysqlxSession.AuthenticateStart> PARSER = new AbstractParser<MysqlxSession.AuthenticateStart>() {
         public MysqlxSession.AuthenticateStart parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxSession.AuthenticateStart(input, extensionRegistry);
         }
      };

      private AuthenticateStart(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private AuthenticateStart() {
         this.mechName_ = "";
         this.authData_ = ByteString.EMPTY;
         this.initialResponse_ = ByteString.EMPTY;
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxSession.AuthenticateStart();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private AuthenticateStart(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.mechName_ = bs;
                        break;
                     case 18:
                        this.bitField0_ |= 2;
                        this.authData_ = input.readBytes();
                        break;
                     case 26:
                        this.bitField0_ |= 4;
                        this.initialResponse_ = input.readBytes();
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
         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxSession.AuthenticateStart.class, MysqlxSession.AuthenticateStart.Builder.class);
      }

      @Override
      public boolean hasMechName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getMechName() {
         Object ref = this.mechName_;
         if (ref instanceof String) {
            return (String)ref;
         } else {
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
               this.mechName_ = s;
            }

            return s;
         }
      }

      @Override
      public ByteString getMechNameBytes() {
         Object ref = this.mechName_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.mechName_ = b;
            return b;
         } else {
            return (ByteString)ref;
         }
      }

      @Override
      public boolean hasAuthData() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public ByteString getAuthData() {
         return this.authData_;
      }

      @Override
      public boolean hasInitialResponse() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public ByteString getInitialResponse() {
         return this.initialResponse_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasMechName()) {
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
            GeneratedMessageV3.writeString(output, 1, this.mechName_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeBytes(2, this.authData_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeBytes(3, this.initialResponse_);
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
               size += GeneratedMessageV3.computeStringSize(1, this.mechName_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeBytesSize(2, this.authData_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeBytesSize(3, this.initialResponse_);
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
         } else if (!(obj instanceof MysqlxSession.AuthenticateStart)) {
            return super.equals(obj);
         } else {
            MysqlxSession.AuthenticateStart other = (MysqlxSession.AuthenticateStart)obj;
            if (this.hasMechName() != other.hasMechName()) {
               return false;
            } else if (this.hasMechName() && !this.getMechName().equals(other.getMechName())) {
               return false;
            } else if (this.hasAuthData() != other.hasAuthData()) {
               return false;
            } else if (this.hasAuthData() && !this.getAuthData().equals(other.getAuthData())) {
               return false;
            } else if (this.hasInitialResponse() != other.hasInitialResponse()) {
               return false;
            } else {
               return this.hasInitialResponse() && !this.getInitialResponse().equals(other.getInitialResponse())
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
            if (this.hasMechName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getMechName().hashCode();
            }

            if (this.hasAuthData()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.getAuthData().hashCode();
            }

            if (this.hasInitialResponse()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.getInitialResponse().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxSession.AuthenticateStart parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.AuthenticateStart parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateStart parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.AuthenticateStart parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateStart parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.AuthenticateStart parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateStart parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSession.AuthenticateStart parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateStart parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxSession.AuthenticateStart parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSession.AuthenticateStart parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSession.AuthenticateStart parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxSession.AuthenticateStart.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxSession.AuthenticateStart.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxSession.AuthenticateStart.Builder newBuilder(MysqlxSession.AuthenticateStart prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxSession.AuthenticateStart.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxSession.AuthenticateStart.Builder() : new MysqlxSession.AuthenticateStart.Builder().mergeFrom(this);
      }

      protected MysqlxSession.AuthenticateStart.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxSession.AuthenticateStart.Builder(parent);
      }

      public static MysqlxSession.AuthenticateStart getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxSession.AuthenticateStart> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxSession.AuthenticateStart> getParserForType() {
         return PARSER;
      }

      public MysqlxSession.AuthenticateStart getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxSession.AuthenticateStart.Builder>
         implements MysqlxSession.AuthenticateStartOrBuilder {
         private int bitField0_;
         private Object mechName_ = "";
         private ByteString authData_ = ByteString.EMPTY;
         private ByteString initialResponse_ = ByteString.EMPTY;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxSession.AuthenticateStart.class, MysqlxSession.AuthenticateStart.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSession.AuthenticateStart.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxSession.AuthenticateStart.Builder clear() {
            super.clear();
            this.mechName_ = "";
            this.bitField0_ &= -2;
            this.authData_ = ByteString.EMPTY;
            this.bitField0_ &= -3;
            this.initialResponse_ = ByteString.EMPTY;
            this.bitField0_ &= -5;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSession.internal_static_Mysqlx_Session_AuthenticateStart_descriptor;
         }

         public MysqlxSession.AuthenticateStart getDefaultInstanceForType() {
            return MysqlxSession.AuthenticateStart.getDefaultInstance();
         }

         public MysqlxSession.AuthenticateStart build() {
            MysqlxSession.AuthenticateStart result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxSession.AuthenticateStart buildPartial() {
            MysqlxSession.AuthenticateStart result = new MysqlxSession.AuthenticateStart(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.mechName_ = this.mechName_;
            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.authData_ = this.authData_;
            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.initialResponse_ = this.initialResponse_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxSession.AuthenticateStart.Builder clone() {
            return (MysqlxSession.AuthenticateStart.Builder)super.clone();
         }

         public MysqlxSession.AuthenticateStart.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSession.AuthenticateStart.Builder)super.setField(field, value);
         }

         public MysqlxSession.AuthenticateStart.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxSession.AuthenticateStart.Builder)super.clearField(field);
         }

         public MysqlxSession.AuthenticateStart.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxSession.AuthenticateStart.Builder)super.clearOneof(oneof);
         }

         public MysqlxSession.AuthenticateStart.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxSession.AuthenticateStart.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxSession.AuthenticateStart.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSession.AuthenticateStart.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxSession.AuthenticateStart.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxSession.AuthenticateStart) {
               return this.mergeFrom((MysqlxSession.AuthenticateStart)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxSession.AuthenticateStart.Builder mergeFrom(MysqlxSession.AuthenticateStart other) {
            if (other == MysqlxSession.AuthenticateStart.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasMechName()) {
                  this.bitField0_ |= 1;
                  this.mechName_ = other.mechName_;
                  this.onChanged();
               }

               if (other.hasAuthData()) {
                  this.setAuthData(other.getAuthData());
               }

               if (other.hasInitialResponse()) {
                  this.setInitialResponse(other.getInitialResponse());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return this.hasMechName();
         }

         public MysqlxSession.AuthenticateStart.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxSession.AuthenticateStart parsedMessage = null;

            try {
               parsedMessage = MysqlxSession.AuthenticateStart.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxSession.AuthenticateStart)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasMechName() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getMechName() {
            Object ref = this.mechName_;
            if (!(ref instanceof String)) {
               ByteString bs = (ByteString)ref;
               String s = bs.toStringUtf8();
               if (bs.isValidUtf8()) {
                  this.mechName_ = s;
               }

               return s;
            } else {
               return (String)ref;
            }
         }

         @Override
         public ByteString getMechNameBytes() {
            Object ref = this.mechName_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.mechName_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxSession.AuthenticateStart.Builder setMechName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.mechName_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxSession.AuthenticateStart.Builder clearMechName() {
            this.bitField0_ &= -2;
            this.mechName_ = MysqlxSession.AuthenticateStart.getDefaultInstance().getMechName();
            this.onChanged();
            return this;
         }

         public MysqlxSession.AuthenticateStart.Builder setMechNameBytes(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.mechName_ = value;
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasAuthData() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public ByteString getAuthData() {
            return this.authData_;
         }

         public MysqlxSession.AuthenticateStart.Builder setAuthData(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.authData_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxSession.AuthenticateStart.Builder clearAuthData() {
            this.bitField0_ &= -3;
            this.authData_ = MysqlxSession.AuthenticateStart.getDefaultInstance().getAuthData();
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasInitialResponse() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public ByteString getInitialResponse() {
            return this.initialResponse_;
         }

         public MysqlxSession.AuthenticateStart.Builder setInitialResponse(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.initialResponse_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxSession.AuthenticateStart.Builder clearInitialResponse() {
            this.bitField0_ &= -5;
            this.initialResponse_ = MysqlxSession.AuthenticateStart.getDefaultInstance().getInitialResponse();
            this.onChanged();
            return this;
         }

         public final MysqlxSession.AuthenticateStart.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSession.AuthenticateStart.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxSession.AuthenticateStart.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSession.AuthenticateStart.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface AuthenticateStartOrBuilder extends MessageOrBuilder {
      boolean hasMechName();

      String getMechName();

      ByteString getMechNameBytes();

      boolean hasAuthData();

      ByteString getAuthData();

      boolean hasInitialResponse();

      ByteString getInitialResponse();
   }

   public static final class Close extends GeneratedMessageV3 implements MysqlxSession.CloseOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxSession.Close DEFAULT_INSTANCE = new MysqlxSession.Close();
      @Deprecated
      public static final Parser<MysqlxSession.Close> PARSER = new AbstractParser<MysqlxSession.Close>() {
         public MysqlxSession.Close parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxSession.Close(input, extensionRegistry);
         }
      };

      private Close(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Close() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxSession.Close();
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
         return MysqlxSession.internal_static_Mysqlx_Session_Close_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSession.internal_static_Mysqlx_Session_Close_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxSession.Close.class, MysqlxSession.Close.Builder.class);
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
         } else if (!(obj instanceof MysqlxSession.Close)) {
            return super.equals(obj);
         } else {
            MysqlxSession.Close other = (MysqlxSession.Close)obj;
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

      public static MysqlxSession.Close parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.Close parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.Close parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.Close parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.Close parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.Close parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.Close parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSession.Close parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSession.Close parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxSession.Close parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSession.Close parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSession.Close parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxSession.Close.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxSession.Close.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxSession.Close.Builder newBuilder(MysqlxSession.Close prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxSession.Close.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxSession.Close.Builder() : new MysqlxSession.Close.Builder().mergeFrom(this);
      }

      protected MysqlxSession.Close.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxSession.Close.Builder(parent);
      }

      public static MysqlxSession.Close getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxSession.Close> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxSession.Close> getParserForType() {
         return PARSER;
      }

      public MysqlxSession.Close getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxSession.Close.Builder> implements MysqlxSession.CloseOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSession.internal_static_Mysqlx_Session_Close_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSession.internal_static_Mysqlx_Session_Close_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxSession.Close.class, MysqlxSession.Close.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSession.Close.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxSession.Close.Builder clear() {
            super.clear();
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSession.internal_static_Mysqlx_Session_Close_descriptor;
         }

         public MysqlxSession.Close getDefaultInstanceForType() {
            return MysqlxSession.Close.getDefaultInstance();
         }

         public MysqlxSession.Close build() {
            MysqlxSession.Close result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxSession.Close buildPartial() {
            MysqlxSession.Close result = new MysqlxSession.Close(this);
            this.onBuilt();
            return result;
         }

         public MysqlxSession.Close.Builder clone() {
            return (MysqlxSession.Close.Builder)super.clone();
         }

         public MysqlxSession.Close.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSession.Close.Builder)super.setField(field, value);
         }

         public MysqlxSession.Close.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxSession.Close.Builder)super.clearField(field);
         }

         public MysqlxSession.Close.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxSession.Close.Builder)super.clearOneof(oneof);
         }

         public MysqlxSession.Close.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxSession.Close.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxSession.Close.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSession.Close.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxSession.Close.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxSession.Close) {
               return this.mergeFrom((MysqlxSession.Close)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxSession.Close.Builder mergeFrom(MysqlxSession.Close other) {
            if (other == MysqlxSession.Close.getDefaultInstance()) {
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

         public MysqlxSession.Close.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxSession.Close parsedMessage = null;

            try {
               parsedMessage = MysqlxSession.Close.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxSession.Close)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         public final MysqlxSession.Close.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSession.Close.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxSession.Close.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSession.Close.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface CloseOrBuilder extends MessageOrBuilder {
   }

   public static final class Reset extends GeneratedMessageV3 implements MysqlxSession.ResetOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int KEEP_OPEN_FIELD_NUMBER = 1;
      private boolean keepOpen_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxSession.Reset DEFAULT_INSTANCE = new MysqlxSession.Reset();
      @Deprecated
      public static final Parser<MysqlxSession.Reset> PARSER = new AbstractParser<MysqlxSession.Reset>() {
         public MysqlxSession.Reset parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxSession.Reset(input, extensionRegistry);
         }
      };

      private Reset(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Reset() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxSession.Reset();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Reset(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.keepOpen_ = input.readBool();
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
         return MysqlxSession.internal_static_Mysqlx_Session_Reset_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxSession.internal_static_Mysqlx_Session_Reset_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxSession.Reset.class, MysqlxSession.Reset.Builder.class);
      }

      @Override
      public boolean hasKeepOpen() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public boolean getKeepOpen() {
         return this.keepOpen_;
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
         if ((this.bitField0_ & 1) != 0) {
            output.writeBool(1, this.keepOpen_);
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
               size += CodedOutputStream.computeBoolSize(1, this.keepOpen_);
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
         } else if (!(obj instanceof MysqlxSession.Reset)) {
            return super.equals(obj);
         } else {
            MysqlxSession.Reset other = (MysqlxSession.Reset)obj;
            if (this.hasKeepOpen() != other.hasKeepOpen()) {
               return false;
            } else {
               return this.hasKeepOpen() && this.getKeepOpen() != other.getKeepOpen() ? false : this.unknownFields.equals(other.unknownFields);
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
            if (this.hasKeepOpen()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + Internal.hashBoolean(this.getKeepOpen());
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxSession.Reset parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.Reset parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.Reset parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.Reset parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.Reset parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxSession.Reset parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxSession.Reset parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSession.Reset parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSession.Reset parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxSession.Reset parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxSession.Reset parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxSession.Reset parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxSession.Reset.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxSession.Reset.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxSession.Reset.Builder newBuilder(MysqlxSession.Reset prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxSession.Reset.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxSession.Reset.Builder() : new MysqlxSession.Reset.Builder().mergeFrom(this);
      }

      protected MysqlxSession.Reset.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxSession.Reset.Builder(parent);
      }

      public static MysqlxSession.Reset getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxSession.Reset> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxSession.Reset> getParserForType() {
         return PARSER;
      }

      public MysqlxSession.Reset getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxSession.Reset.Builder> implements MysqlxSession.ResetOrBuilder {
         private int bitField0_;
         private boolean keepOpen_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxSession.internal_static_Mysqlx_Session_Reset_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxSession.internal_static_Mysqlx_Session_Reset_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxSession.Reset.class, MysqlxSession.Reset.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxSession.Reset.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxSession.Reset.Builder clear() {
            super.clear();
            this.keepOpen_ = false;
            this.bitField0_ &= -2;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxSession.internal_static_Mysqlx_Session_Reset_descriptor;
         }

         public MysqlxSession.Reset getDefaultInstanceForType() {
            return MysqlxSession.Reset.getDefaultInstance();
         }

         public MysqlxSession.Reset build() {
            MysqlxSession.Reset result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxSession.Reset buildPartial() {
            MysqlxSession.Reset result = new MysqlxSession.Reset(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.keepOpen_ = this.keepOpen_;
               to_bitField0_ |= 1;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxSession.Reset.Builder clone() {
            return (MysqlxSession.Reset.Builder)super.clone();
         }

         public MysqlxSession.Reset.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSession.Reset.Builder)super.setField(field, value);
         }

         public MysqlxSession.Reset.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxSession.Reset.Builder)super.clearField(field);
         }

         public MysqlxSession.Reset.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxSession.Reset.Builder)super.clearOneof(oneof);
         }

         public MysqlxSession.Reset.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxSession.Reset.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxSession.Reset.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxSession.Reset.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxSession.Reset.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxSession.Reset) {
               return this.mergeFrom((MysqlxSession.Reset)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxSession.Reset.Builder mergeFrom(MysqlxSession.Reset other) {
            if (other == MysqlxSession.Reset.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasKeepOpen()) {
                  this.setKeepOpen(other.getKeepOpen());
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

         public MysqlxSession.Reset.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxSession.Reset parsedMessage = null;

            try {
               parsedMessage = MysqlxSession.Reset.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxSession.Reset)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasKeepOpen() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public boolean getKeepOpen() {
            return this.keepOpen_;
         }

         public MysqlxSession.Reset.Builder setKeepOpen(boolean value) {
            this.bitField0_ |= 1;
            this.keepOpen_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxSession.Reset.Builder clearKeepOpen() {
            this.bitField0_ &= -2;
            this.keepOpen_ = false;
            this.onChanged();
            return this;
         }

         public final MysqlxSession.Reset.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSession.Reset.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxSession.Reset.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxSession.Reset.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface ResetOrBuilder extends MessageOrBuilder {
      boolean hasKeepOpen();

      boolean getKeepOpen();
   }
}
