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
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MysqlxConnection {
   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_Capability_descriptor = getDescriptor().getMessageTypes().get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_Capability_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Connection_Capability_descriptor, new String[]{"Name", "Value"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_Capabilities_descriptor = getDescriptor().getMessageTypes().get(1);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_Capabilities_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Connection_Capabilities_descriptor, new String[]{"Capabilities"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor = getDescriptor().getMessageTypes().get(2);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_CapabilitiesGet_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor, new String[0]
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor = getDescriptor().getMessageTypes().get(3);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_CapabilitiesSet_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor, new String[]{"Capabilities"}
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_Close_descriptor = getDescriptor().getMessageTypes().get(4);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_Close_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Connection_Close_descriptor, new String[0]
   );
   private static final Descriptors.Descriptor internal_static_Mysqlx_Connection_Compression_descriptor = getDescriptor().getMessageTypes().get(5);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_Mysqlx_Connection_Compression_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_Mysqlx_Connection_Compression_descriptor, new String[]{"UncompressedSize", "ServerMessages", "ClientMessages", "Payload"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private MysqlxConnection() {
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
         "\n\u0017mysqlx_connection.proto\u0012\u0011Mysqlx.Connection\u001a\u0016mysqlx_datatypes.proto\u001a\fmysqlx.proto\"@\n\nCapability\u0012\f\n\u0004name\u0018\u0001 \u0002(\t\u0012$\n\u0005value\u0018\u0002 \u0002(\u000b2\u0015.Mysqlx.Datatypes.Any\"I\n\fCapabilities\u00123\n\fcapabilities\u0018\u0001 \u0003(\u000b2\u001d.Mysqlx.Connection.Capability:\u0004\u0090ê0\u0002\"\u0017\n\u000fCapabilitiesGet:\u0004\u0088ê0\u0001\"N\n\u000fCapabilitiesSet\u00125\n\fcapabilities\u0018\u0001 \u0002(\u000b2\u001f.Mysqlx.Connection.Capabilities:\u0004\u0088ê0\u0002\"\r\n\u0005Close:\u0004\u0088ê0\u0003\"¯\u0001\n\u000bCompression\u0012\u0019\n\u0011uncompressed_size\u0018\u0001 \u0001(\u0004\u00124\n\u000fserver_messages\u0018\u0002 \u0001(\u000e2\u001b.Mysqlx.ServerMessages.Type\u00124\n\u000fclient_messages\u0018\u0003 \u0001(\u000e2\u001b.Mysqlx.ClientMessages.Type\u0012\u000f\n\u0007payload\u0018\u0004 \u0002(\f:\b\u0090ê0\u0013\u0088ê0.B\u0019\n\u0017com.mysql.cj.x.protobuf"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
         descriptorData, new Descriptors.FileDescriptor[]{MysqlxDatatypes.getDescriptor(), Mysqlx.getDescriptor()}
      );
      ExtensionRegistry registry = ExtensionRegistry.newInstance();
      registry.add(Mysqlx.clientMessageId);
      registry.add(Mysqlx.serverMessageId);
      Descriptors.FileDescriptor.internalUpdateFileDescriptor(descriptor, registry);
      MysqlxDatatypes.getDescriptor();
      Mysqlx.getDescriptor();
   }

   public static final class Capabilities extends GeneratedMessageV3 implements MysqlxConnection.CapabilitiesOrBuilder {
      private static final long serialVersionUID = 0L;
      public static final int CAPABILITIES_FIELD_NUMBER = 1;
      private List<MysqlxConnection.Capability> capabilities_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxConnection.Capabilities DEFAULT_INSTANCE = new MysqlxConnection.Capabilities();
      @Deprecated
      public static final Parser<MysqlxConnection.Capabilities> PARSER = new AbstractParser<MysqlxConnection.Capabilities>() {
         public MysqlxConnection.Capabilities parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxConnection.Capabilities(input, extensionRegistry);
         }
      };

      private Capabilities(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Capabilities() {
         this.capabilities_ = Collections.emptyList();
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxConnection.Capabilities();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Capabilities(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                           this.capabilities_ = new ArrayList<>();
                           mutable_bitField0_ |= 1;
                        }

                        this.capabilities_.add(input.readMessage(MysqlxConnection.Capability.PARSER, extensionRegistry));
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
                  this.capabilities_ = Collections.unmodifiableList(this.capabilities_);
               }

               this.unknownFields = unknownFields.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxConnection.Capabilities.class, MysqlxConnection.Capabilities.Builder.class);
      }

      @Override
      public List<MysqlxConnection.Capability> getCapabilitiesList() {
         return this.capabilities_;
      }

      @Override
      public List<? extends MysqlxConnection.CapabilityOrBuilder> getCapabilitiesOrBuilderList() {
         return this.capabilities_;
      }

      @Override
      public int getCapabilitiesCount() {
         return this.capabilities_.size();
      }

      @Override
      public MysqlxConnection.Capability getCapabilities(int index) {
         return this.capabilities_.get(index);
      }

      @Override
      public MysqlxConnection.CapabilityOrBuilder getCapabilitiesOrBuilder(int index) {
         return this.capabilities_.get(index);
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else {
            for (int i = 0; i < this.getCapabilitiesCount(); i++) {
               if (!this.getCapabilities(i).isInitialized()) {
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
         for (int i = 0; i < this.capabilities_.size(); i++) {
            output.writeMessage(1, this.capabilities_.get(i));
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

            for (int i = 0; i < this.capabilities_.size(); i++) {
               size += CodedOutputStream.computeMessageSize(1, this.capabilities_.get(i));
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
         } else if (!(obj instanceof MysqlxConnection.Capabilities)) {
            return super.equals(obj);
         } else {
            MysqlxConnection.Capabilities other = (MysqlxConnection.Capabilities)obj;
            return !this.getCapabilitiesList().equals(other.getCapabilitiesList()) ? false : this.unknownFields.equals(other.unknownFields);
         }
      }

      @Override
      public int hashCode() {
         if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
         } else {
            int hash = 41;
            hash = 19 * hash + getDescriptor().hashCode();
            if (this.getCapabilitiesCount() > 0) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCapabilitiesList().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxConnection.Capabilities parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.Capabilities parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.Capabilities parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.Capabilities parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.Capabilities parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.Capabilities parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.Capabilities parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxConnection.Capabilities parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxConnection.Capabilities parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxConnection.Capabilities parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxConnection.Capabilities parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxConnection.Capabilities parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxConnection.Capabilities.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxConnection.Capabilities.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxConnection.Capabilities.Builder newBuilder(MysqlxConnection.Capabilities prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxConnection.Capabilities.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxConnection.Capabilities.Builder() : new MysqlxConnection.Capabilities.Builder().mergeFrom(this);
      }

      protected MysqlxConnection.Capabilities.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxConnection.Capabilities.Builder(parent);
      }

      public static MysqlxConnection.Capabilities getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxConnection.Capabilities> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxConnection.Capabilities> getParserForType() {
         return PARSER;
      }

      public MysqlxConnection.Capabilities getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxConnection.Capabilities.Builder>
         implements MysqlxConnection.CapabilitiesOrBuilder {
         private int bitField0_;
         private List<MysqlxConnection.Capability> capabilities_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<MysqlxConnection.Capability, MysqlxConnection.Capability.Builder, MysqlxConnection.CapabilityOrBuilder> capabilitiesBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxConnection.Capabilities.class, MysqlxConnection.Capabilities.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxConnection.Capabilities.alwaysUseFieldBuilders) {
               this.getCapabilitiesFieldBuilder();
            }
         }

         public MysqlxConnection.Capabilities.Builder clear() {
            super.clear();
            if (this.capabilitiesBuilder_ == null) {
               this.capabilities_ = Collections.emptyList();
               this.bitField0_ &= -2;
            } else {
               this.capabilitiesBuilder_.clear();
            }

            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Capabilities_descriptor;
         }

         public MysqlxConnection.Capabilities getDefaultInstanceForType() {
            return MysqlxConnection.Capabilities.getDefaultInstance();
         }

         public MysqlxConnection.Capabilities build() {
            MysqlxConnection.Capabilities result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxConnection.Capabilities buildPartial() {
            MysqlxConnection.Capabilities result = new MysqlxConnection.Capabilities(this);
            int from_bitField0_ = this.bitField0_;
            if (this.capabilitiesBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0) {
                  this.capabilities_ = Collections.unmodifiableList(this.capabilities_);
                  this.bitField0_ &= -2;
               }

               result.capabilities_ = this.capabilities_;
            } else {
               result.capabilities_ = this.capabilitiesBuilder_.build();
            }

            this.onBuilt();
            return result;
         }

         public MysqlxConnection.Capabilities.Builder clone() {
            return (MysqlxConnection.Capabilities.Builder)super.clone();
         }

         public MysqlxConnection.Capabilities.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxConnection.Capabilities.Builder)super.setField(field, value);
         }

         public MysqlxConnection.Capabilities.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxConnection.Capabilities.Builder)super.clearField(field);
         }

         public MysqlxConnection.Capabilities.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxConnection.Capabilities.Builder)super.clearOneof(oneof);
         }

         public MysqlxConnection.Capabilities.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxConnection.Capabilities.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxConnection.Capabilities.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxConnection.Capabilities.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxConnection.Capabilities.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxConnection.Capabilities) {
               return this.mergeFrom((MysqlxConnection.Capabilities)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxConnection.Capabilities.Builder mergeFrom(MysqlxConnection.Capabilities other) {
            if (other == MysqlxConnection.Capabilities.getDefaultInstance()) {
               return this;
            } else {
               if (this.capabilitiesBuilder_ == null) {
                  if (!other.capabilities_.isEmpty()) {
                     if (this.capabilities_.isEmpty()) {
                        this.capabilities_ = other.capabilities_;
                        this.bitField0_ &= -2;
                     } else {
                        this.ensureCapabilitiesIsMutable();
                        this.capabilities_.addAll(other.capabilities_);
                     }

                     this.onChanged();
                  }
               } else if (!other.capabilities_.isEmpty()) {
                  if (this.capabilitiesBuilder_.isEmpty()) {
                     this.capabilitiesBuilder_.dispose();
                     this.capabilitiesBuilder_ = null;
                     this.capabilities_ = other.capabilities_;
                     this.bitField0_ &= -2;
                     this.capabilitiesBuilder_ = MysqlxConnection.Capabilities.alwaysUseFieldBuilders ? this.getCapabilitiesFieldBuilder() : null;
                  } else {
                     this.capabilitiesBuilder_.addAllMessages(other.capabilities_);
                  }
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            for (int i = 0; i < this.getCapabilitiesCount(); i++) {
               if (!this.getCapabilities(i).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public MysqlxConnection.Capabilities.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxConnection.Capabilities parsedMessage = null;

            try {
               parsedMessage = MysqlxConnection.Capabilities.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxConnection.Capabilities)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         private void ensureCapabilitiesIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
               this.capabilities_ = new ArrayList<>(this.capabilities_);
               this.bitField0_ |= 1;
            }
         }

         @Override
         public List<MysqlxConnection.Capability> getCapabilitiesList() {
            return this.capabilitiesBuilder_ == null ? Collections.unmodifiableList(this.capabilities_) : this.capabilitiesBuilder_.getMessageList();
         }

         @Override
         public int getCapabilitiesCount() {
            return this.capabilitiesBuilder_ == null ? this.capabilities_.size() : this.capabilitiesBuilder_.getCount();
         }

         @Override
         public MysqlxConnection.Capability getCapabilities(int index) {
            return this.capabilitiesBuilder_ == null ? this.capabilities_.get(index) : this.capabilitiesBuilder_.getMessage(index);
         }

         public MysqlxConnection.Capabilities.Builder setCapabilities(int index, MysqlxConnection.Capability value) {
            if (this.capabilitiesBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureCapabilitiesIsMutable();
               this.capabilities_.set(index, value);
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.setMessage(index, value);
            }

            return this;
         }

         public MysqlxConnection.Capabilities.Builder setCapabilities(int index, MysqlxConnection.Capability.Builder builderForValue) {
            if (this.capabilitiesBuilder_ == null) {
               this.ensureCapabilitiesIsMutable();
               this.capabilities_.set(index, builderForValue.build());
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.setMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxConnection.Capabilities.Builder addCapabilities(MysqlxConnection.Capability value) {
            if (this.capabilitiesBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureCapabilitiesIsMutable();
               this.capabilities_.add(value);
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.addMessage(value);
            }

            return this;
         }

         public MysqlxConnection.Capabilities.Builder addCapabilities(int index, MysqlxConnection.Capability value) {
            if (this.capabilitiesBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.ensureCapabilitiesIsMutable();
               this.capabilities_.add(index, value);
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.addMessage(index, value);
            }

            return this;
         }

         public MysqlxConnection.Capabilities.Builder addCapabilities(MysqlxConnection.Capability.Builder builderForValue) {
            if (this.capabilitiesBuilder_ == null) {
               this.ensureCapabilitiesIsMutable();
               this.capabilities_.add(builderForValue.build());
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.addMessage(builderForValue.build());
            }

            return this;
         }

         public MysqlxConnection.Capabilities.Builder addCapabilities(int index, MysqlxConnection.Capability.Builder builderForValue) {
            if (this.capabilitiesBuilder_ == null) {
               this.ensureCapabilitiesIsMutable();
               this.capabilities_.add(index, builderForValue.build());
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.addMessage(index, builderForValue.build());
            }

            return this;
         }

         public MysqlxConnection.Capabilities.Builder addAllCapabilities(Iterable<? extends MysqlxConnection.Capability> values) {
            if (this.capabilitiesBuilder_ == null) {
               this.ensureCapabilitiesIsMutable();
               AbstractMessageLite.Builder.addAll(values, this.capabilities_);
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.addAllMessages(values);
            }

            return this;
         }

         public MysqlxConnection.Capabilities.Builder clearCapabilities() {
            if (this.capabilitiesBuilder_ == null) {
               this.capabilities_ = Collections.emptyList();
               this.bitField0_ &= -2;
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.clear();
            }

            return this;
         }

         public MysqlxConnection.Capabilities.Builder removeCapabilities(int index) {
            if (this.capabilitiesBuilder_ == null) {
               this.ensureCapabilitiesIsMutable();
               this.capabilities_.remove(index);
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.remove(index);
            }

            return this;
         }

         public MysqlxConnection.Capability.Builder getCapabilitiesBuilder(int index) {
            return this.getCapabilitiesFieldBuilder().getBuilder(index);
         }

         @Override
         public MysqlxConnection.CapabilityOrBuilder getCapabilitiesOrBuilder(int index) {
            return this.capabilitiesBuilder_ == null ? this.capabilities_.get(index) : this.capabilitiesBuilder_.getMessageOrBuilder(index);
         }

         @Override
         public List<? extends MysqlxConnection.CapabilityOrBuilder> getCapabilitiesOrBuilderList() {
            return this.capabilitiesBuilder_ != null ? this.capabilitiesBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.capabilities_);
         }

         public MysqlxConnection.Capability.Builder addCapabilitiesBuilder() {
            return this.getCapabilitiesFieldBuilder().addBuilder(MysqlxConnection.Capability.getDefaultInstance());
         }

         public MysqlxConnection.Capability.Builder addCapabilitiesBuilder(int index) {
            return this.getCapabilitiesFieldBuilder().addBuilder(index, MysqlxConnection.Capability.getDefaultInstance());
         }

         public List<MysqlxConnection.Capability.Builder> getCapabilitiesBuilderList() {
            return this.getCapabilitiesFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<MysqlxConnection.Capability, MysqlxConnection.Capability.Builder, MysqlxConnection.CapabilityOrBuilder> getCapabilitiesFieldBuilder() {
            if (this.capabilitiesBuilder_ == null) {
               this.capabilitiesBuilder_ = new RepeatedFieldBuilderV3<>(
                  this.capabilities_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean()
               );
               this.capabilities_ = null;
            }

            return this.capabilitiesBuilder_;
         }

         public final MysqlxConnection.Capabilities.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxConnection.Capabilities.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxConnection.Capabilities.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxConnection.Capabilities.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public static final class CapabilitiesGet extends GeneratedMessageV3 implements MysqlxConnection.CapabilitiesGetOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxConnection.CapabilitiesGet DEFAULT_INSTANCE = new MysqlxConnection.CapabilitiesGet();
      @Deprecated
      public static final Parser<MysqlxConnection.CapabilitiesGet> PARSER = new AbstractParser<MysqlxConnection.CapabilitiesGet>() {
         public MysqlxConnection.CapabilitiesGet parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxConnection.CapabilitiesGet(input, extensionRegistry);
         }
      };

      private CapabilitiesGet(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private CapabilitiesGet() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxConnection.CapabilitiesGet();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private CapabilitiesGet(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxConnection.CapabilitiesGet.class, MysqlxConnection.CapabilitiesGet.Builder.class);
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
         } else if (!(obj instanceof MysqlxConnection.CapabilitiesGet)) {
            return super.equals(obj);
         } else {
            MysqlxConnection.CapabilitiesGet other = (MysqlxConnection.CapabilitiesGet)obj;
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

      public static MysqlxConnection.CapabilitiesGet parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.CapabilitiesGet parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.CapabilitiesGet parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.CapabilitiesGet parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.CapabilitiesGet parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.CapabilitiesGet parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.CapabilitiesGet parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxConnection.CapabilitiesGet parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxConnection.CapabilitiesGet parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxConnection.CapabilitiesGet parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxConnection.CapabilitiesGet parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxConnection.CapabilitiesGet parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxConnection.CapabilitiesGet.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxConnection.CapabilitiesGet.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxConnection.CapabilitiesGet.Builder newBuilder(MysqlxConnection.CapabilitiesGet prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxConnection.CapabilitiesGet.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxConnection.CapabilitiesGet.Builder() : new MysqlxConnection.CapabilitiesGet.Builder().mergeFrom(this);
      }

      protected MysqlxConnection.CapabilitiesGet.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxConnection.CapabilitiesGet.Builder(parent);
      }

      public static MysqlxConnection.CapabilitiesGet getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxConnection.CapabilitiesGet> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxConnection.CapabilitiesGet> getParserForType() {
         return PARSER;
      }

      public MysqlxConnection.CapabilitiesGet getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxConnection.CapabilitiesGet.Builder>
         implements MysqlxConnection.CapabilitiesGetOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxConnection.CapabilitiesGet.class, MysqlxConnection.CapabilitiesGet.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxConnection.CapabilitiesGet.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxConnection.CapabilitiesGet.Builder clear() {
            super.clear();
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesGet_descriptor;
         }

         public MysqlxConnection.CapabilitiesGet getDefaultInstanceForType() {
            return MysqlxConnection.CapabilitiesGet.getDefaultInstance();
         }

         public MysqlxConnection.CapabilitiesGet build() {
            MysqlxConnection.CapabilitiesGet result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxConnection.CapabilitiesGet buildPartial() {
            MysqlxConnection.CapabilitiesGet result = new MysqlxConnection.CapabilitiesGet(this);
            this.onBuilt();
            return result;
         }

         public MysqlxConnection.CapabilitiesGet.Builder clone() {
            return (MysqlxConnection.CapabilitiesGet.Builder)super.clone();
         }

         public MysqlxConnection.CapabilitiesGet.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxConnection.CapabilitiesGet.Builder)super.setField(field, value);
         }

         public MysqlxConnection.CapabilitiesGet.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxConnection.CapabilitiesGet.Builder)super.clearField(field);
         }

         public MysqlxConnection.CapabilitiesGet.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxConnection.CapabilitiesGet.Builder)super.clearOneof(oneof);
         }

         public MysqlxConnection.CapabilitiesGet.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxConnection.CapabilitiesGet.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxConnection.CapabilitiesGet.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxConnection.CapabilitiesGet.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxConnection.CapabilitiesGet.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxConnection.CapabilitiesGet) {
               return this.mergeFrom((MysqlxConnection.CapabilitiesGet)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxConnection.CapabilitiesGet.Builder mergeFrom(MysqlxConnection.CapabilitiesGet other) {
            if (other == MysqlxConnection.CapabilitiesGet.getDefaultInstance()) {
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

         public MysqlxConnection.CapabilitiesGet.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxConnection.CapabilitiesGet parsedMessage = null;

            try {
               parsedMessage = MysqlxConnection.CapabilitiesGet.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxConnection.CapabilitiesGet)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         public final MysqlxConnection.CapabilitiesGet.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxConnection.CapabilitiesGet.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxConnection.CapabilitiesGet.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxConnection.CapabilitiesGet.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface CapabilitiesGetOrBuilder extends MessageOrBuilder {
   }

   public interface CapabilitiesOrBuilder extends MessageOrBuilder {
      List<MysqlxConnection.Capability> getCapabilitiesList();

      MysqlxConnection.Capability getCapabilities(int var1);

      int getCapabilitiesCount();

      List<? extends MysqlxConnection.CapabilityOrBuilder> getCapabilitiesOrBuilderList();

      MysqlxConnection.CapabilityOrBuilder getCapabilitiesOrBuilder(int var1);
   }

   public static final class CapabilitiesSet extends GeneratedMessageV3 implements MysqlxConnection.CapabilitiesSetOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int CAPABILITIES_FIELD_NUMBER = 1;
      private MysqlxConnection.Capabilities capabilities_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxConnection.CapabilitiesSet DEFAULT_INSTANCE = new MysqlxConnection.CapabilitiesSet();
      @Deprecated
      public static final Parser<MysqlxConnection.CapabilitiesSet> PARSER = new AbstractParser<MysqlxConnection.CapabilitiesSet>() {
         public MysqlxConnection.CapabilitiesSet parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxConnection.CapabilitiesSet(input, extensionRegistry);
         }
      };

      private CapabilitiesSet(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private CapabilitiesSet() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxConnection.CapabilitiesSet();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private CapabilitiesSet(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        MysqlxConnection.Capabilities.Builder subBuilder = null;
                        if ((this.bitField0_ & 1) != 0) {
                           subBuilder = this.capabilities_.toBuilder();
                        }

                        this.capabilities_ = input.readMessage(MysqlxConnection.Capabilities.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                           subBuilder.mergeFrom(this.capabilities_);
                           this.capabilities_ = subBuilder.buildPartial();
                        }

                        this.bitField0_ |= 1;
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
         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxConnection.CapabilitiesSet.class, MysqlxConnection.CapabilitiesSet.Builder.class);
      }

      @Override
      public boolean hasCapabilities() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public MysqlxConnection.Capabilities getCapabilities() {
         return this.capabilities_ == null ? MysqlxConnection.Capabilities.getDefaultInstance() : this.capabilities_;
      }

      @Override
      public MysqlxConnection.CapabilitiesOrBuilder getCapabilitiesOrBuilder() {
         return this.capabilities_ == null ? MysqlxConnection.Capabilities.getDefaultInstance() : this.capabilities_;
      }

      @Override
      public final boolean isInitialized() {
         byte isInitialized = this.memoizedIsInitialized;
         if (isInitialized == 1) {
            return true;
         } else if (isInitialized == 0) {
            return false;
         } else if (!this.hasCapabilities()) {
            this.memoizedIsInitialized = 0;
            return false;
         } else if (!this.getCapabilities().isInitialized()) {
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
            output.writeMessage(1, this.getCapabilities());
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
               size += CodedOutputStream.computeMessageSize(1, this.getCapabilities());
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
         } else if (!(obj instanceof MysqlxConnection.CapabilitiesSet)) {
            return super.equals(obj);
         } else {
            MysqlxConnection.CapabilitiesSet other = (MysqlxConnection.CapabilitiesSet)obj;
            if (this.hasCapabilities() != other.hasCapabilities()) {
               return false;
            } else {
               return this.hasCapabilities() && !this.getCapabilities().equals(other.getCapabilities())
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
            if (this.hasCapabilities()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getCapabilities().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxConnection.CapabilitiesSet parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.CapabilitiesSet parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.CapabilitiesSet parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.CapabilitiesSet parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.CapabilitiesSet parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.CapabilitiesSet parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.CapabilitiesSet parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxConnection.CapabilitiesSet parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxConnection.CapabilitiesSet parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxConnection.CapabilitiesSet parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxConnection.CapabilitiesSet parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxConnection.CapabilitiesSet parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxConnection.CapabilitiesSet.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxConnection.CapabilitiesSet.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxConnection.CapabilitiesSet.Builder newBuilder(MysqlxConnection.CapabilitiesSet prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxConnection.CapabilitiesSet.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxConnection.CapabilitiesSet.Builder() : new MysqlxConnection.CapabilitiesSet.Builder().mergeFrom(this);
      }

      protected MysqlxConnection.CapabilitiesSet.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxConnection.CapabilitiesSet.Builder(parent);
      }

      public static MysqlxConnection.CapabilitiesSet getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxConnection.CapabilitiesSet> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxConnection.CapabilitiesSet> getParserForType() {
         return PARSER;
      }

      public MysqlxConnection.CapabilitiesSet getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxConnection.CapabilitiesSet.Builder>
         implements MysqlxConnection.CapabilitiesSetOrBuilder {
         private int bitField0_;
         private MysqlxConnection.Capabilities capabilities_;
         private SingleFieldBuilderV3<MysqlxConnection.Capabilities, MysqlxConnection.Capabilities.Builder, MysqlxConnection.CapabilitiesOrBuilder> capabilitiesBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxConnection.CapabilitiesSet.class, MysqlxConnection.CapabilitiesSet.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxConnection.CapabilitiesSet.alwaysUseFieldBuilders) {
               this.getCapabilitiesFieldBuilder();
            }
         }

         public MysqlxConnection.CapabilitiesSet.Builder clear() {
            super.clear();
            if (this.capabilitiesBuilder_ == null) {
               this.capabilities_ = null;
            } else {
               this.capabilitiesBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_CapabilitiesSet_descriptor;
         }

         public MysqlxConnection.CapabilitiesSet getDefaultInstanceForType() {
            return MysqlxConnection.CapabilitiesSet.getDefaultInstance();
         }

         public MysqlxConnection.CapabilitiesSet build() {
            MysqlxConnection.CapabilitiesSet result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxConnection.CapabilitiesSet buildPartial() {
            MysqlxConnection.CapabilitiesSet result = new MysqlxConnection.CapabilitiesSet(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               if (this.capabilitiesBuilder_ == null) {
                  result.capabilities_ = this.capabilities_;
               } else {
                  result.capabilities_ = this.capabilitiesBuilder_.build();
               }

               to_bitField0_ |= 1;
            }

            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxConnection.CapabilitiesSet.Builder clone() {
            return (MysqlxConnection.CapabilitiesSet.Builder)super.clone();
         }

         public MysqlxConnection.CapabilitiesSet.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxConnection.CapabilitiesSet.Builder)super.setField(field, value);
         }

         public MysqlxConnection.CapabilitiesSet.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxConnection.CapabilitiesSet.Builder)super.clearField(field);
         }

         public MysqlxConnection.CapabilitiesSet.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxConnection.CapabilitiesSet.Builder)super.clearOneof(oneof);
         }

         public MysqlxConnection.CapabilitiesSet.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxConnection.CapabilitiesSet.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxConnection.CapabilitiesSet.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxConnection.CapabilitiesSet.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxConnection.CapabilitiesSet.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxConnection.CapabilitiesSet) {
               return this.mergeFrom((MysqlxConnection.CapabilitiesSet)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxConnection.CapabilitiesSet.Builder mergeFrom(MysqlxConnection.CapabilitiesSet other) {
            if (other == MysqlxConnection.CapabilitiesSet.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasCapabilities()) {
                  this.mergeCapabilities(other.getCapabilities());
               }

               this.mergeUnknownFields(other.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            return !this.hasCapabilities() ? false : this.getCapabilities().isInitialized();
         }

         public MysqlxConnection.CapabilitiesSet.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxConnection.CapabilitiesSet parsedMessage = null;

            try {
               parsedMessage = MysqlxConnection.CapabilitiesSet.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxConnection.CapabilitiesSet)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasCapabilities() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public MysqlxConnection.Capabilities getCapabilities() {
            if (this.capabilitiesBuilder_ == null) {
               return this.capabilities_ == null ? MysqlxConnection.Capabilities.getDefaultInstance() : this.capabilities_;
            } else {
               return this.capabilitiesBuilder_.getMessage();
            }
         }

         public MysqlxConnection.CapabilitiesSet.Builder setCapabilities(MysqlxConnection.Capabilities value) {
            if (this.capabilitiesBuilder_ == null) {
               if (value == null) {
                  throw new NullPointerException();
               }

               this.capabilities_ = value;
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.setMessage(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxConnection.CapabilitiesSet.Builder setCapabilities(MysqlxConnection.Capabilities.Builder builderForValue) {
            if (this.capabilitiesBuilder_ == null) {
               this.capabilities_ = builderForValue.build();
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxConnection.CapabilitiesSet.Builder mergeCapabilities(MysqlxConnection.Capabilities value) {
            if (this.capabilitiesBuilder_ == null) {
               if ((this.bitField0_ & 1) != 0 && this.capabilities_ != null && this.capabilities_ != MysqlxConnection.Capabilities.getDefaultInstance()) {
                  this.capabilities_ = MysqlxConnection.Capabilities.newBuilder(this.capabilities_).mergeFrom(value).buildPartial();
               } else {
                  this.capabilities_ = value;
               }

               this.onChanged();
            } else {
               this.capabilitiesBuilder_.mergeFrom(value);
            }

            this.bitField0_ |= 1;
            return this;
         }

         public MysqlxConnection.CapabilitiesSet.Builder clearCapabilities() {
            if (this.capabilitiesBuilder_ == null) {
               this.capabilities_ = null;
               this.onChanged();
            } else {
               this.capabilitiesBuilder_.clear();
            }

            this.bitField0_ &= -2;
            return this;
         }

         public MysqlxConnection.Capabilities.Builder getCapabilitiesBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.getCapabilitiesFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxConnection.CapabilitiesOrBuilder getCapabilitiesOrBuilder() {
            if (this.capabilitiesBuilder_ != null) {
               return this.capabilitiesBuilder_.getMessageOrBuilder();
            } else {
               return this.capabilities_ == null ? MysqlxConnection.Capabilities.getDefaultInstance() : this.capabilities_;
            }
         }

         private SingleFieldBuilderV3<MysqlxConnection.Capabilities, MysqlxConnection.Capabilities.Builder, MysqlxConnection.CapabilitiesOrBuilder> getCapabilitiesFieldBuilder() {
            if (this.capabilitiesBuilder_ == null) {
               this.capabilitiesBuilder_ = new SingleFieldBuilderV3<>(this.getCapabilities(), this.getParentForChildren(), this.isClean());
               this.capabilities_ = null;
            }

            return this.capabilitiesBuilder_;
         }

         public final MysqlxConnection.CapabilitiesSet.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxConnection.CapabilitiesSet.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxConnection.CapabilitiesSet.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxConnection.CapabilitiesSet.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface CapabilitiesSetOrBuilder extends MessageOrBuilder {
      boolean hasCapabilities();

      MysqlxConnection.Capabilities getCapabilities();

      MysqlxConnection.CapabilitiesOrBuilder getCapabilitiesOrBuilder();
   }

   public static final class Capability extends GeneratedMessageV3 implements MysqlxConnection.CapabilityOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int NAME_FIELD_NUMBER = 1;
      private volatile Object name_;
      public static final int VALUE_FIELD_NUMBER = 2;
      private MysqlxDatatypes.Any value_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxConnection.Capability DEFAULT_INSTANCE = new MysqlxConnection.Capability();
      @Deprecated
      public static final Parser<MysqlxConnection.Capability> PARSER = new AbstractParser<MysqlxConnection.Capability>() {
         public MysqlxConnection.Capability parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxConnection.Capability(input, extensionRegistry);
         }
      };

      private Capability(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Capability() {
         this.name_ = "";
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxConnection.Capability();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Capability(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        MysqlxDatatypes.Any.Builder subBuilder = null;
                        if ((this.bitField0_ & 2) != 0) {
                           subBuilder = this.value_.toBuilder();
                        }

                        this.value_ = input.readMessage(MysqlxDatatypes.Any.PARSER, extensionRegistry);
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
         return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxConnection.Capability.class, MysqlxConnection.Capability.Builder.class);
      }

      @Override
      public boolean hasName() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getName() {
         Object ref = this.name_;
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
         Object ref = this.name_;
         if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.name_ = b;
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
      public MysqlxDatatypes.Any getValue() {
         return this.value_ == null ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_;
      }

      @Override
      public MysqlxDatatypes.AnyOrBuilder getValueOrBuilder() {
         return this.value_ == null ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_;
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
            GeneratedMessageV3.writeString(output, 1, this.name_);
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
               size += GeneratedMessageV3.computeStringSize(1, this.name_);
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
         } else if (!(obj instanceof MysqlxConnection.Capability)) {
            return super.equals(obj);
         } else {
            MysqlxConnection.Capability other = (MysqlxConnection.Capability)obj;
            if (this.hasName() != other.hasName()) {
               return false;
            } else if (this.hasName() && !this.getName().equals(other.getName())) {
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
            if (this.hasName()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + this.getName().hashCode();
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

      public static MysqlxConnection.Capability parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.Capability parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.Capability parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.Capability parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.Capability parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.Capability parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.Capability parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxConnection.Capability parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxConnection.Capability parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxConnection.Capability parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxConnection.Capability parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxConnection.Capability parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxConnection.Capability.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxConnection.Capability.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxConnection.Capability.Builder newBuilder(MysqlxConnection.Capability prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxConnection.Capability.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxConnection.Capability.Builder() : new MysqlxConnection.Capability.Builder().mergeFrom(this);
      }

      protected MysqlxConnection.Capability.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxConnection.Capability.Builder(parent);
      }

      public static MysqlxConnection.Capability getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxConnection.Capability> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxConnection.Capability> getParserForType() {
         return PARSER;
      }

      public MysqlxConnection.Capability getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxConnection.Capability.Builder> implements MysqlxConnection.CapabilityOrBuilder {
         private int bitField0_;
         private Object name_ = "";
         private MysqlxDatatypes.Any value_;
         private SingleFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> valueBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxConnection.Capability.class, MysqlxConnection.Capability.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxConnection.Capability.alwaysUseFieldBuilders) {
               this.getValueFieldBuilder();
            }
         }

         public MysqlxConnection.Capability.Builder clear() {
            super.clear();
            this.name_ = "";
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
            return MysqlxConnection.internal_static_Mysqlx_Connection_Capability_descriptor;
         }

         public MysqlxConnection.Capability getDefaultInstanceForType() {
            return MysqlxConnection.Capability.getDefaultInstance();
         }

         public MysqlxConnection.Capability build() {
            MysqlxConnection.Capability result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxConnection.Capability buildPartial() {
            MysqlxConnection.Capability result = new MysqlxConnection.Capability(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               to_bitField0_ |= 1;
            }

            result.name_ = this.name_;
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

         public MysqlxConnection.Capability.Builder clone() {
            return (MysqlxConnection.Capability.Builder)super.clone();
         }

         public MysqlxConnection.Capability.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxConnection.Capability.Builder)super.setField(field, value);
         }

         public MysqlxConnection.Capability.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxConnection.Capability.Builder)super.clearField(field);
         }

         public MysqlxConnection.Capability.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxConnection.Capability.Builder)super.clearOneof(oneof);
         }

         public MysqlxConnection.Capability.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxConnection.Capability.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxConnection.Capability.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxConnection.Capability.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxConnection.Capability.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxConnection.Capability) {
               return this.mergeFrom((MysqlxConnection.Capability)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxConnection.Capability.Builder mergeFrom(MysqlxConnection.Capability other) {
            if (other == MysqlxConnection.Capability.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasName()) {
                  this.bitField0_ |= 1;
                  this.name_ = other.name_;
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
            if (!this.hasName()) {
               return false;
            } else {
               return !this.hasValue() ? false : this.getValue().isInitialized();
            }
         }

         public MysqlxConnection.Capability.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxConnection.Capability parsedMessage = null;

            try {
               parsedMessage = MysqlxConnection.Capability.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxConnection.Capability)var8.getUnfinishedMessage();
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
            Object ref = this.name_;
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
            Object ref = this.name_;
            if (ref instanceof String) {
               ByteString b = ByteString.copyFromUtf8((String)ref);
               this.name_ = b;
               return b;
            } else {
               return (ByteString)ref;
            }
         }

         public MysqlxConnection.Capability.Builder setName(String value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.name_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxConnection.Capability.Builder clearName() {
            this.bitField0_ &= -2;
            this.name_ = MysqlxConnection.Capability.getDefaultInstance().getName();
            this.onChanged();
            return this;
         }

         public MysqlxConnection.Capability.Builder setNameBytes(ByteString value) {
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
         public boolean hasValue() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public MysqlxDatatypes.Any getValue() {
            if (this.valueBuilder_ == null) {
               return this.value_ == null ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_;
            } else {
               return this.valueBuilder_.getMessage();
            }
         }

         public MysqlxConnection.Capability.Builder setValue(MysqlxDatatypes.Any value) {
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

         public MysqlxConnection.Capability.Builder setValue(MysqlxDatatypes.Any.Builder builderForValue) {
            if (this.valueBuilder_ == null) {
               this.value_ = builderForValue.build();
               this.onChanged();
            } else {
               this.valueBuilder_.setMessage(builderForValue.build());
            }

            this.bitField0_ |= 2;
            return this;
         }

         public MysqlxConnection.Capability.Builder mergeValue(MysqlxDatatypes.Any value) {
            if (this.valueBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0 && this.value_ != null && this.value_ != MysqlxDatatypes.Any.getDefaultInstance()) {
                  this.value_ = MysqlxDatatypes.Any.newBuilder(this.value_).mergeFrom(value).buildPartial();
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

         public MysqlxConnection.Capability.Builder clearValue() {
            if (this.valueBuilder_ == null) {
               this.value_ = null;
               this.onChanged();
            } else {
               this.valueBuilder_.clear();
            }

            this.bitField0_ &= -3;
            return this;
         }

         public MysqlxDatatypes.Any.Builder getValueBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return this.getValueFieldBuilder().getBuilder();
         }

         @Override
         public MysqlxDatatypes.AnyOrBuilder getValueOrBuilder() {
            if (this.valueBuilder_ != null) {
               return this.valueBuilder_.getMessageOrBuilder();
            } else {
               return this.value_ == null ? MysqlxDatatypes.Any.getDefaultInstance() : this.value_;
            }
         }

         private SingleFieldBuilderV3<MysqlxDatatypes.Any, MysqlxDatatypes.Any.Builder, MysqlxDatatypes.AnyOrBuilder> getValueFieldBuilder() {
            if (this.valueBuilder_ == null) {
               this.valueBuilder_ = new SingleFieldBuilderV3<>(this.getValue(), this.getParentForChildren(), this.isClean());
               this.value_ = null;
            }

            return this.valueBuilder_;
         }

         public final MysqlxConnection.Capability.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxConnection.Capability.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxConnection.Capability.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxConnection.Capability.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface CapabilityOrBuilder extends MessageOrBuilder {
      boolean hasName();

      String getName();

      ByteString getNameBytes();

      boolean hasValue();

      MysqlxDatatypes.Any getValue();

      MysqlxDatatypes.AnyOrBuilder getValueOrBuilder();
   }

   public static final class Close extends GeneratedMessageV3 implements MysqlxConnection.CloseOrBuilder {
      private static final long serialVersionUID = 0L;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxConnection.Close DEFAULT_INSTANCE = new MysqlxConnection.Close();
      @Deprecated
      public static final Parser<MysqlxConnection.Close> PARSER = new AbstractParser<MysqlxConnection.Close>() {
         public MysqlxConnection.Close parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxConnection.Close(input, extensionRegistry);
         }
      };

      private Close(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Close() {
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxConnection.Close();
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
         return MysqlxConnection.internal_static_Mysqlx_Connection_Close_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_Close_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxConnection.Close.class, MysqlxConnection.Close.Builder.class);
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
         } else if (!(obj instanceof MysqlxConnection.Close)) {
            return super.equals(obj);
         } else {
            MysqlxConnection.Close other = (MysqlxConnection.Close)obj;
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

      public static MysqlxConnection.Close parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.Close parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.Close parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.Close parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.Close parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.Close parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.Close parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxConnection.Close parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxConnection.Close parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxConnection.Close parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxConnection.Close parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxConnection.Close parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxConnection.Close.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxConnection.Close.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxConnection.Close.Builder newBuilder(MysqlxConnection.Close prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxConnection.Close.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxConnection.Close.Builder() : new MysqlxConnection.Close.Builder().mergeFrom(this);
      }

      protected MysqlxConnection.Close.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxConnection.Close.Builder(parent);
      }

      public static MysqlxConnection.Close getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxConnection.Close> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxConnection.Close> getParserForType() {
         return PARSER;
      }

      public MysqlxConnection.Close getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<MysqlxConnection.Close.Builder> implements MysqlxConnection.CloseOrBuilder {
         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Close_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Close_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxConnection.Close.class, MysqlxConnection.Close.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxConnection.Close.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxConnection.Close.Builder clear() {
            super.clear();
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Close_descriptor;
         }

         public MysqlxConnection.Close getDefaultInstanceForType() {
            return MysqlxConnection.Close.getDefaultInstance();
         }

         public MysqlxConnection.Close build() {
            MysqlxConnection.Close result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxConnection.Close buildPartial() {
            MysqlxConnection.Close result = new MysqlxConnection.Close(this);
            this.onBuilt();
            return result;
         }

         public MysqlxConnection.Close.Builder clone() {
            return (MysqlxConnection.Close.Builder)super.clone();
         }

         public MysqlxConnection.Close.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxConnection.Close.Builder)super.setField(field, value);
         }

         public MysqlxConnection.Close.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxConnection.Close.Builder)super.clearField(field);
         }

         public MysqlxConnection.Close.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxConnection.Close.Builder)super.clearOneof(oneof);
         }

         public MysqlxConnection.Close.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxConnection.Close.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxConnection.Close.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxConnection.Close.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxConnection.Close.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxConnection.Close) {
               return this.mergeFrom((MysqlxConnection.Close)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxConnection.Close.Builder mergeFrom(MysqlxConnection.Close other) {
            if (other == MysqlxConnection.Close.getDefaultInstance()) {
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

         public MysqlxConnection.Close.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxConnection.Close parsedMessage = null;

            try {
               parsedMessage = MysqlxConnection.Close.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxConnection.Close)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         public final MysqlxConnection.Close.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxConnection.Close.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxConnection.Close.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxConnection.Close.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface CloseOrBuilder extends MessageOrBuilder {
   }

   public static final class Compression extends GeneratedMessageV3 implements MysqlxConnection.CompressionOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int UNCOMPRESSED_SIZE_FIELD_NUMBER = 1;
      private long uncompressedSize_;
      public static final int SERVER_MESSAGES_FIELD_NUMBER = 2;
      private int serverMessages_;
      public static final int CLIENT_MESSAGES_FIELD_NUMBER = 3;
      private int clientMessages_;
      public static final int PAYLOAD_FIELD_NUMBER = 4;
      private ByteString payload_;
      private byte memoizedIsInitialized = -1;
      private static final MysqlxConnection.Compression DEFAULT_INSTANCE = new MysqlxConnection.Compression();
      @Deprecated
      public static final Parser<MysqlxConnection.Compression> PARSER = new AbstractParser<MysqlxConnection.Compression>() {
         public MysqlxConnection.Compression parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return new MysqlxConnection.Compression(input, extensionRegistry);
         }
      };

      private Compression(GeneratedMessageV3.Builder<?> builder) {
         super(builder);
      }

      private Compression() {
         this.serverMessages_ = 0;
         this.clientMessages_ = 1;
         this.payload_ = ByteString.EMPTY;
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
         return new MysqlxConnection.Compression();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Compression(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                        this.uncompressedSize_ = input.readUInt64();
                        break;
                     case 16:
                        int rawValuex = input.readEnum();
                        Mysqlx.ServerMessages.Type valuex = Mysqlx.ServerMessages.Type.valueOf(rawValuex);
                        if (valuex == null) {
                           unknownFields.mergeVarintField(2, rawValuex);
                        } else {
                           this.bitField0_ |= 2;
                           this.serverMessages_ = rawValuex;
                        }
                        break;
                     case 24:
                        int rawValue = input.readEnum();
                        Mysqlx.ClientMessages.Type value = Mysqlx.ClientMessages.Type.valueOf(rawValue);
                        if (value == null) {
                           unknownFields.mergeVarintField(3, rawValue);
                        } else {
                           this.bitField0_ |= 4;
                           this.clientMessages_ = rawValue;
                        }
                        break;
                     case 34:
                        this.bitField0_ |= 8;
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
         return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_fieldAccessorTable
            .ensureFieldAccessorsInitialized(MysqlxConnection.Compression.class, MysqlxConnection.Compression.Builder.class);
      }

      @Override
      public boolean hasUncompressedSize() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public long getUncompressedSize() {
         return this.uncompressedSize_;
      }

      @Override
      public boolean hasServerMessages() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public Mysqlx.ServerMessages.Type getServerMessages() {
         Mysqlx.ServerMessages.Type result = Mysqlx.ServerMessages.Type.valueOf(this.serverMessages_);
         return result == null ? Mysqlx.ServerMessages.Type.OK : result;
      }

      @Override
      public boolean hasClientMessages() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public Mysqlx.ClientMessages.Type getClientMessages() {
         Mysqlx.ClientMessages.Type result = Mysqlx.ClientMessages.Type.valueOf(this.clientMessages_);
         return result == null ? Mysqlx.ClientMessages.Type.CON_CAPABILITIES_GET : result;
      }

      @Override
      public boolean hasPayload() {
         return (this.bitField0_ & 8) != 0;
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
         } else if (!this.hasPayload()) {
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
            output.writeUInt64(1, this.uncompressedSize_);
         }

         if ((this.bitField0_ & 2) != 0) {
            output.writeEnum(2, this.serverMessages_);
         }

         if ((this.bitField0_ & 4) != 0) {
            output.writeEnum(3, this.clientMessages_);
         }

         if ((this.bitField0_ & 8) != 0) {
            output.writeBytes(4, this.payload_);
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
               size += CodedOutputStream.computeUInt64Size(1, this.uncompressedSize_);
            }

            if ((this.bitField0_ & 2) != 0) {
               size += CodedOutputStream.computeEnumSize(2, this.serverMessages_);
            }

            if ((this.bitField0_ & 4) != 0) {
               size += CodedOutputStream.computeEnumSize(3, this.clientMessages_);
            }

            if ((this.bitField0_ & 8) != 0) {
               size += CodedOutputStream.computeBytesSize(4, this.payload_);
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
         } else if (!(obj instanceof MysqlxConnection.Compression)) {
            return super.equals(obj);
         } else {
            MysqlxConnection.Compression other = (MysqlxConnection.Compression)obj;
            if (this.hasUncompressedSize() != other.hasUncompressedSize()) {
               return false;
            } else if (this.hasUncompressedSize() && this.getUncompressedSize() != other.getUncompressedSize()) {
               return false;
            } else if (this.hasServerMessages() != other.hasServerMessages()) {
               return false;
            } else if (this.hasServerMessages() && this.serverMessages_ != other.serverMessages_) {
               return false;
            } else if (this.hasClientMessages() != other.hasClientMessages()) {
               return false;
            } else if (this.hasClientMessages() && this.clientMessages_ != other.clientMessages_) {
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
            if (this.hasUncompressedSize()) {
               hash = 37 * hash + 1;
               hash = 53 * hash + Internal.hashLong(this.getUncompressedSize());
            }

            if (this.hasServerMessages()) {
               hash = 37 * hash + 2;
               hash = 53 * hash + this.serverMessages_;
            }

            if (this.hasClientMessages()) {
               hash = 37 * hash + 3;
               hash = 53 * hash + this.clientMessages_;
            }

            if (this.hasPayload()) {
               hash = 37 * hash + 4;
               hash = 53 * hash + this.getPayload().hashCode();
            }

            hash = 29 * hash + this.unknownFields.hashCode();
            this.memoizedHashCode = hash;
            return hash;
         }
      }

      public static MysqlxConnection.Compression parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.Compression parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.Compression parseFrom(ByteString data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.Compression parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.Compression parseFrom(byte[] data) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data);
      }

      public static MysqlxConnection.Compression parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(data, extensionRegistry);
      }

      public static MysqlxConnection.Compression parseFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxConnection.Compression parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxConnection.Compression parseDelimitedFrom(InputStream input) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
      }

      public static MysqlxConnection.Compression parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }

      public static MysqlxConnection.Compression parseFrom(CodedInputStream input) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input);
      }

      public static MysqlxConnection.Compression parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
      }

      public MysqlxConnection.Compression.Builder newBuilderForType() {
         return newBuilder();
      }

      public static MysqlxConnection.Compression.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static MysqlxConnection.Compression.Builder newBuilder(MysqlxConnection.Compression prototype) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
      }

      public MysqlxConnection.Compression.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new MysqlxConnection.Compression.Builder() : new MysqlxConnection.Compression.Builder().mergeFrom(this);
      }

      protected MysqlxConnection.Compression.Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
         return new MysqlxConnection.Compression.Builder(parent);
      }

      public static MysqlxConnection.Compression getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<MysqlxConnection.Compression> parser() {
         return PARSER;
      }

      @Override
      public Parser<MysqlxConnection.Compression> getParserForType() {
         return PARSER;
      }

      public MysqlxConnection.Compression getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<MysqlxConnection.Compression.Builder>
         implements MysqlxConnection.CompressionOrBuilder {
         private int bitField0_;
         private long uncompressedSize_;
         private int serverMessages_ = 0;
         private int clientMessages_ = 1;
         private ByteString payload_ = ByteString.EMPTY;

         public static final Descriptors.Descriptor getDescriptor() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_fieldAccessorTable
               .ensureFieldAccessorsInitialized(MysqlxConnection.Compression.class, MysqlxConnection.Compression.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (MysqlxConnection.Compression.alwaysUseFieldBuilders) {
            }
         }

         public MysqlxConnection.Compression.Builder clear() {
            super.clear();
            this.uncompressedSize_ = 0L;
            this.bitField0_ &= -2;
            this.serverMessages_ = 0;
            this.bitField0_ &= -3;
            this.clientMessages_ = 1;
            this.bitField0_ &= -5;
            this.payload_ = ByteString.EMPTY;
            this.bitField0_ &= -9;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return MysqlxConnection.internal_static_Mysqlx_Connection_Compression_descriptor;
         }

         public MysqlxConnection.Compression getDefaultInstanceForType() {
            return MysqlxConnection.Compression.getDefaultInstance();
         }

         public MysqlxConnection.Compression build() {
            MysqlxConnection.Compression result = this.buildPartial();
            if (!result.isInitialized()) {
               throw newUninitializedMessageException(result);
            } else {
               return result;
            }
         }

         public MysqlxConnection.Compression buildPartial() {
            MysqlxConnection.Compression result = new MysqlxConnection.Compression(this);
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
               result.uncompressedSize_ = this.uncompressedSize_;
               to_bitField0_ |= 1;
            }

            if ((from_bitField0_ & 2) != 0) {
               to_bitField0_ |= 2;
            }

            result.serverMessages_ = this.serverMessages_;
            if ((from_bitField0_ & 4) != 0) {
               to_bitField0_ |= 4;
            }

            result.clientMessages_ = this.clientMessages_;
            if ((from_bitField0_ & 8) != 0) {
               to_bitField0_ |= 8;
            }

            result.payload_ = this.payload_;
            result.bitField0_ = to_bitField0_;
            this.onBuilt();
            return result;
         }

         public MysqlxConnection.Compression.Builder clone() {
            return (MysqlxConnection.Compression.Builder)super.clone();
         }

         public MysqlxConnection.Compression.Builder setField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxConnection.Compression.Builder)super.setField(field, value);
         }

         public MysqlxConnection.Compression.Builder clearField(Descriptors.FieldDescriptor field) {
            return (MysqlxConnection.Compression.Builder)super.clearField(field);
         }

         public MysqlxConnection.Compression.Builder clearOneof(Descriptors.OneofDescriptor oneof) {
            return (MysqlxConnection.Compression.Builder)super.clearOneof(oneof);
         }

         public MysqlxConnection.Compression.Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            return (MysqlxConnection.Compression.Builder)super.setRepeatedField(field, index, value);
         }

         public MysqlxConnection.Compression.Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            return (MysqlxConnection.Compression.Builder)super.addRepeatedField(field, value);
         }

         public MysqlxConnection.Compression.Builder mergeFrom(Message other) {
            if (other instanceof MysqlxConnection.Compression) {
               return this.mergeFrom((MysqlxConnection.Compression)other);
            } else {
               super.mergeFrom(other);
               return this;
            }
         }

         public MysqlxConnection.Compression.Builder mergeFrom(MysqlxConnection.Compression other) {
            if (other == MysqlxConnection.Compression.getDefaultInstance()) {
               return this;
            } else {
               if (other.hasUncompressedSize()) {
                  this.setUncompressedSize(other.getUncompressedSize());
               }

               if (other.hasServerMessages()) {
                  this.setServerMessages(other.getServerMessages());
               }

               if (other.hasClientMessages()) {
                  this.setClientMessages(other.getClientMessages());
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
            return this.hasPayload();
         }

         public MysqlxConnection.Compression.Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            MysqlxConnection.Compression parsedMessage = null;

            try {
               parsedMessage = MysqlxConnection.Compression.PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException var8) {
               parsedMessage = (MysqlxConnection.Compression)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (parsedMessage != null) {
                  this.mergeFrom(parsedMessage);
               }
            }

            return this;
         }

         @Override
         public boolean hasUncompressedSize() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public long getUncompressedSize() {
            return this.uncompressedSize_;
         }

         public MysqlxConnection.Compression.Builder setUncompressedSize(long value) {
            this.bitField0_ |= 1;
            this.uncompressedSize_ = value;
            this.onChanged();
            return this;
         }

         public MysqlxConnection.Compression.Builder clearUncompressedSize() {
            this.bitField0_ &= -2;
            this.uncompressedSize_ = 0L;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasServerMessages() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public Mysqlx.ServerMessages.Type getServerMessages() {
            Mysqlx.ServerMessages.Type result = Mysqlx.ServerMessages.Type.valueOf(this.serverMessages_);
            return result == null ? Mysqlx.ServerMessages.Type.OK : result;
         }

         public MysqlxConnection.Compression.Builder setServerMessages(Mysqlx.ServerMessages.Type value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.serverMessages_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxConnection.Compression.Builder clearServerMessages() {
            this.bitField0_ &= -3;
            this.serverMessages_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasClientMessages() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public Mysqlx.ClientMessages.Type getClientMessages() {
            Mysqlx.ClientMessages.Type result = Mysqlx.ClientMessages.Type.valueOf(this.clientMessages_);
            return result == null ? Mysqlx.ClientMessages.Type.CON_CAPABILITIES_GET : result;
         }

         public MysqlxConnection.Compression.Builder setClientMessages(Mysqlx.ClientMessages.Type value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 4;
               this.clientMessages_ = value.getNumber();
               this.onChanged();
               return this;
            }
         }

         public MysqlxConnection.Compression.Builder clearClientMessages() {
            this.bitField0_ &= -5;
            this.clientMessages_ = 1;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasPayload() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public ByteString getPayload() {
            return this.payload_;
         }

         public MysqlxConnection.Compression.Builder setPayload(ByteString value) {
            if (value == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.payload_ = value;
               this.onChanged();
               return this;
            }
         }

         public MysqlxConnection.Compression.Builder clearPayload() {
            this.bitField0_ &= -9;
            this.payload_ = MysqlxConnection.Compression.getDefaultInstance().getPayload();
            this.onChanged();
            return this;
         }

         public final MysqlxConnection.Compression.Builder setUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxConnection.Compression.Builder)super.setUnknownFields(unknownFields);
         }

         public final MysqlxConnection.Compression.Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
            return (MysqlxConnection.Compression.Builder)super.mergeUnknownFields(unknownFields);
         }
      }
   }

   public interface CompressionOrBuilder extends MessageOrBuilder {
      boolean hasUncompressedSize();

      long getUncompressedSize();

      boolean hasServerMessages();

      Mysqlx.ServerMessages.Type getServerMessages();

      boolean hasClientMessages();

      Mysqlx.ClientMessages.Type getClientMessages();

      boolean hasPayload();

      ByteString getPayload();
   }
}
