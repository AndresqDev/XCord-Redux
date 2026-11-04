package com.google.protobuf.compiler;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.LazyStringArrayList;
import com.google.protobuf.LazyStringList;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolStringList;
import com.google.protobuf.RepeatedFieldBuilderV3;
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PluginProtos {
   private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_Version_descriptor = getDescriptor().getMessageTypes().get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_compiler_Version_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_google_protobuf_compiler_Version_descriptor, new String[]{"Major", "Minor", "Patch", "Suffix"}
   );
   private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor = getDescriptor()
      .getMessageTypes()
      .get(1);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor, new String[]{"FileToGenerate", "Parameter", "ProtoFile", "CompilerVersion"}
   );
   private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor = getDescriptor()
      .getMessageTypes()
      .get(2);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor, new String[]{"Error", "File"}
   );
   private static final Descriptors.Descriptor internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor = internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor.getNestedTypes()
      .get(0);
   private static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor, new String[]{"Name", "InsertionPoint", "Content"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private PluginProtos() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite var0) {
   }

   public static void registerAllExtensions(ExtensionRegistry var0) {
      registerAllExtensions((ExtensionRegistryLite)var0);
   }

   public static Descriptors.FileDescriptor getDescriptor() {
      return descriptor;
   }

   static {
      String[] var0 = new String[]{
         "\n%google/protobuf/compiler/plugin.proto\u0012\u0018google.protobuf.compiler\u001a google/protobuf/descriptor.proto\"F\n\u0007Version\u0012\r\n\u0005major\u0018\u0001 \u0001(\u0005\u0012\r\n\u0005minor\u0018\u0002 \u0001(\u0005\u0012\r\n\u0005patch\u0018\u0003 \u0001(\u0005\u0012\u000e\n\u0006suffix\u0018\u0004 \u0001(\t\"º\u0001\n\u0014CodeGeneratorRequest\u0012\u0018\n\u0010file_to_generate\u0018\u0001 \u0003(\t\u0012\u0011\n\tparameter\u0018\u0002 \u0001(\t\u00128\n\nproto_file\u0018\u000f \u0003(\u000b2$.google.protobuf.FileDescriptorProto\u0012;\n\u0010compiler_version\u0018\u0003 \u0001(\u000b2!.google.protobuf.compiler.Version\"ª\u0001\n\u0015CodeGeneratorResponse\u0012\r\n\u0005error\u0018\u0001 \u0001(\t\u0012B\n\u0004file\u0018\u000f \u0003(\u000b24.google.protobuf.compiler.CodeGeneratorResponse.File\u001a>\n\u0004File\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u0012\u0017\n\u000finsertion_point\u0018\u0002 \u0001(\t\u0012\u000f\n\u0007content\u0018\u000f \u0001(\tBg\n\u001ccom.google.protobuf.compilerB\fPluginProtosZ9github.com/golang/protobuf/protoc-gen-go/plugin;plugin_go"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(var0, new Descriptors.FileDescriptor[]{DescriptorProtos.getDescriptor()});
      DescriptorProtos.getDescriptor();
   }

   public static final class CodeGeneratorRequest extends GeneratedMessageV3 implements PluginProtos.CodeGeneratorRequestOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int FILE_TO_GENERATE_FIELD_NUMBER = 1;
      private LazyStringList fileToGenerate_;
      public static final int PARAMETER_FIELD_NUMBER = 2;
      private volatile Object parameter_;
      public static final int PROTO_FILE_FIELD_NUMBER = 15;
      private List<DescriptorProtos.FileDescriptorProto> protoFile_;
      public static final int COMPILER_VERSION_FIELD_NUMBER = 3;
      private PluginProtos.Version compilerVersion_;
      private byte memoizedIsInitialized = -1;
      private static final PluginProtos.CodeGeneratorRequest DEFAULT_INSTANCE = new PluginProtos.CodeGeneratorRequest();
      @Deprecated
      public static final Parser<PluginProtos.CodeGeneratorRequest> PARSER = new AbstractParser<PluginProtos.CodeGeneratorRequest>() {
         public PluginProtos.CodeGeneratorRequest parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
            return new PluginProtos.CodeGeneratorRequest(var1, var2);
         }
      };

      private CodeGeneratorRequest(GeneratedMessageV3.Builder<?> var1) {
         super(var1);
      }

      private CodeGeneratorRequest() {
         this.fileToGenerate_ = LazyStringArrayList.EMPTY;
         this.parameter_ = "";
         this.protoFile_ = Collections.emptyList();
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
         return new PluginProtos.CodeGeneratorRequest();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private CodeGeneratorRequest(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         this();
         if (var2 == null) {
            throw new NullPointerException();
         } else {
            byte var3 = 0;
            UnknownFieldSet.Builder var4 = UnknownFieldSet.newBuilder();

            try {
               boolean var5 = false;

               while (!var5) {
                  int var6 = var1.readTag();
                  switch (var6) {
                     case 0:
                        var5 = true;
                        break;
                     case 10:
                        ByteString var16 = var1.readBytes();
                        if ((var3 & 1) == 0) {
                           this.fileToGenerate_ = new LazyStringArrayList();
                           var3 |= 1;
                        }

                        this.fileToGenerate_.add(var16);
                        break;
                     case 18:
                        ByteString var15 = var1.readBytes();
                        this.bitField0_ |= 1;
                        this.parameter_ = var15;
                        break;
                     case 26:
                        PluginProtos.Version.Builder var7 = null;
                        if ((this.bitField0_ & 2) != 0) {
                           var7 = this.compilerVersion_.toBuilder();
                        }

                        this.compilerVersion_ = var1.readMessage(PluginProtos.Version.PARSER, var2);
                        if (var7 != null) {
                           var7.mergeFrom(this.compilerVersion_);
                           this.compilerVersion_ = var7.buildPartial();
                        }

                        this.bitField0_ |= 2;
                        break;
                     case 122:
                        if ((var3 & 4) == 0) {
                           this.protoFile_ = new ArrayList<>();
                           var3 |= 4;
                        }

                        this.protoFile_.add(var1.readMessage(DescriptorProtos.FileDescriptorProto.PARSER, var2));
                        break;
                     default:
                        if (!this.parseUnknownField(var1, var4, var2, var6)) {
                           var5 = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
            } finally {
               if ((var3 & 1) != 0) {
                  this.fileToGenerate_ = this.fileToGenerate_.getUnmodifiableView();
               }

               if ((var3 & 4) != 0) {
                  this.protoFile_ = Collections.unmodifiableList(this.protoFile_);
               }

               this.unknownFields = var4.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable
            .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorRequest.class, PluginProtos.CodeGeneratorRequest.Builder.class);
      }

      public ProtocolStringList getFileToGenerateList() {
         return this.fileToGenerate_;
      }

      @Override
      public int getFileToGenerateCount() {
         return this.fileToGenerate_.size();
      }

      @Override
      public String getFileToGenerate(int var1) {
         return this.fileToGenerate_.get(var1);
      }

      @Override
      public ByteString getFileToGenerateBytes(int var1) {
         return this.fileToGenerate_.getByteString(var1);
      }

      @Override
      public boolean hasParameter() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getParameter() {
         Object var1 = this.parameter_;
         if (var1 instanceof String) {
            return (String)var1;
         } else {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            if (var2.isValidUtf8()) {
               this.parameter_ = var3;
            }

            return var3;
         }
      }

      @Override
      public ByteString getParameterBytes() {
         Object var1 = this.parameter_;
         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            this.parameter_ = var2;
            return var2;
         } else {
            return (ByteString)var1;
         }
      }

      @Override
      public List<DescriptorProtos.FileDescriptorProto> getProtoFileList() {
         return this.protoFile_;
      }

      @Override
      public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList() {
         return this.protoFile_;
      }

      @Override
      public int getProtoFileCount() {
         return this.protoFile_.size();
      }

      @Override
      public DescriptorProtos.FileDescriptorProto getProtoFile(int var1) {
         return this.protoFile_.get(var1);
      }

      @Override
      public DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int var1) {
         return this.protoFile_.get(var1);
      }

      @Override
      public boolean hasCompilerVersion() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public PluginProtos.Version getCompilerVersion() {
         return this.compilerVersion_ == null ? PluginProtos.Version.getDefaultInstance() : this.compilerVersion_;
      }

      @Override
      public PluginProtos.VersionOrBuilder getCompilerVersionOrBuilder() {
         return this.compilerVersion_ == null ? PluginProtos.Version.getDefaultInstance() : this.compilerVersion_;
      }

      @Override
      public final boolean isInitialized() {
         byte var1 = this.memoizedIsInitialized;
         if (var1 == 1) {
            return true;
         } else if (var1 == 0) {
            return false;
         } else {
            for (int var2 = 0; var2 < this.getProtoFileCount(); var2++) {
               if (!this.getProtoFile(var2).isInitialized()) {
                  this.memoizedIsInitialized = 0;
                  return false;
               }
            }

            this.memoizedIsInitialized = 1;
            return true;
         }
      }

      @Override
      public void writeTo(CodedOutputStream var1) throws IOException {
         for (int var2 = 0; var2 < this.fileToGenerate_.size(); var2++) {
            GeneratedMessageV3.writeString(var1, 1, this.fileToGenerate_.getRaw(var2));
         }

         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(var1, 2, this.parameter_);
         }

         if ((this.bitField0_ & 2) != 0) {
            var1.writeMessage(3, this.getCompilerVersion());
         }

         for (int var3 = 0; var3 < this.protoFile_.size(); var3++) {
            var1.writeMessage(15, this.protoFile_.get(var3));
         }

         this.unknownFields.writeTo(var1);
      }

      @Override
      public int getSerializedSize() {
         int var1 = this.memoizedSize;
         if (var1 != -1) {
            return var1;
         } else {
            int var4 = 0;
            int var2 = 0;

            for (int var3 = 0; var3 < this.fileToGenerate_.size(); var3++) {
               var2 += computeStringSizeNoTag(this.fileToGenerate_.getRaw(var3));
            }

            var4 += var2;
            var4 += 1 * this.getFileToGenerateList().size();
            if ((this.bitField0_ & 1) != 0) {
               var4 += GeneratedMessageV3.computeStringSize(2, this.parameter_);
            }

            if ((this.bitField0_ & 2) != 0) {
               var4 += CodedOutputStream.computeMessageSize(3, this.getCompilerVersion());
            }

            for (int var8 = 0; var8 < this.protoFile_.size(); var8++) {
               var4 += CodedOutputStream.computeMessageSize(15, this.protoFile_.get(var8));
            }

            var4 += this.unknownFields.getSerializedSize();
            this.memoizedSize = var4;
            return var4;
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof PluginProtos.CodeGeneratorRequest)) {
            return super.equals(var1);
         } else {
            PluginProtos.CodeGeneratorRequest var2 = (PluginProtos.CodeGeneratorRequest)var1;
            if (!this.getFileToGenerateList().equals(var2.getFileToGenerateList())) {
               return false;
            } else if (this.hasParameter() != var2.hasParameter()) {
               return false;
            } else if (this.hasParameter() && !this.getParameter().equals(var2.getParameter())) {
               return false;
            } else if (!this.getProtoFileList().equals(var2.getProtoFileList())) {
               return false;
            } else if (this.hasCompilerVersion() != var2.hasCompilerVersion()) {
               return false;
            } else {
               return this.hasCompilerVersion() && !this.getCompilerVersion().equals(var2.getCompilerVersion())
                  ? false
                  : this.unknownFields.equals(var2.unknownFields);
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
            if (this.getFileToGenerateCount() > 0) {
               var1 = 37 * var1 + 1;
               var1 = 53 * var1 + this.getFileToGenerateList().hashCode();
            }

            if (this.hasParameter()) {
               var1 = 37 * var1 + 2;
               var1 = 53 * var1 + this.getParameter().hashCode();
            }

            if (this.getProtoFileCount() > 0) {
               var1 = 37 * var1 + 15;
               var1 = 53 * var1 + this.getProtoFileList().hashCode();
            }

            if (this.hasCompilerVersion()) {
               var1 = 37 * var1 + 3;
               var1 = 53 * var1 + this.getCompilerVersion().hashCode();
            }

            var1 = 29 * var1 + this.unknownFields.hashCode();
            this.memoizedHashCode = var1;
            return var1;
         }
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0, var1);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(ByteString var0) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0, var1);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(byte[] var0) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0, var1);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(InputStream var0) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, var0);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
      }

      public static PluginProtos.CodeGeneratorRequest parseDelimitedFrom(InputStream var0) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
      }

      public static PluginProtos.CodeGeneratorRequest parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(CodedInputStream var0) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, var0);
      }

      public static PluginProtos.CodeGeneratorRequest parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
      }

      public PluginProtos.CodeGeneratorRequest.Builder newBuilderForType() {
         return newBuilder();
      }

      public static PluginProtos.CodeGeneratorRequest.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static PluginProtos.CodeGeneratorRequest.Builder newBuilder(PluginProtos.CodeGeneratorRequest var0) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
      }

      public PluginProtos.CodeGeneratorRequest.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new PluginProtos.CodeGeneratorRequest.Builder() : new PluginProtos.CodeGeneratorRequest.Builder().mergeFrom(this);
      }

      protected PluginProtos.CodeGeneratorRequest.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
         return new PluginProtos.CodeGeneratorRequest.Builder(var1);
      }

      public static PluginProtos.CodeGeneratorRequest getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<PluginProtos.CodeGeneratorRequest> parser() {
         return PARSER;
      }

      @Override
      public Parser<PluginProtos.CodeGeneratorRequest> getParserForType() {
         return PARSER;
      }

      public PluginProtos.CodeGeneratorRequest getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<PluginProtos.CodeGeneratorRequest.Builder>
         implements PluginProtos.CodeGeneratorRequestOrBuilder {
         private int bitField0_;
         private LazyStringList fileToGenerate_ = LazyStringArrayList.EMPTY;
         private Object parameter_ = "";
         private List<DescriptorProtos.FileDescriptorProto> protoFile_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> protoFileBuilder_;
         private PluginProtos.Version compilerVersion_;
         private SingleFieldBuilderV3<PluginProtos.Version, PluginProtos.Version.Builder, PluginProtos.VersionOrBuilder> compilerVersionBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_fieldAccessorTable
               .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorRequest.class, PluginProtos.CodeGeneratorRequest.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent var1) {
            super(var1);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (PluginProtos.CodeGeneratorRequest.alwaysUseFieldBuilders) {
               this.getProtoFileFieldBuilder();
               this.getCompilerVersionFieldBuilder();
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder clear() {
            super.clear();
            this.fileToGenerate_ = LazyStringArrayList.EMPTY;
            this.bitField0_ &= -2;
            this.parameter_ = "";
            this.bitField0_ &= -3;
            if (this.protoFileBuilder_ == null) {
               this.protoFile_ = Collections.emptyList();
               this.bitField0_ &= -5;
            } else {
               this.protoFileBuilder_.clear();
            }

            if (this.compilerVersionBuilder_ == null) {
               this.compilerVersion_ = null;
            } else {
               this.compilerVersionBuilder_.clear();
            }

            this.bitField0_ &= -9;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorRequest_descriptor;
         }

         public PluginProtos.CodeGeneratorRequest getDefaultInstanceForType() {
            return PluginProtos.CodeGeneratorRequest.getDefaultInstance();
         }

         public PluginProtos.CodeGeneratorRequest build() {
            PluginProtos.CodeGeneratorRequest var1 = this.buildPartial();
            if (!var1.isInitialized()) {
               throw newUninitializedMessageException(var1);
            } else {
               return var1;
            }
         }

         public PluginProtos.CodeGeneratorRequest buildPartial() {
            PluginProtos.CodeGeneratorRequest var1 = new PluginProtos.CodeGeneratorRequest(this);
            int var2 = this.bitField0_;
            byte var3 = 0;
            if ((this.bitField0_ & 1) != 0) {
               this.fileToGenerate_ = this.fileToGenerate_.getUnmodifiableView();
               this.bitField0_ &= -2;
            }

            var1.fileToGenerate_ = this.fileToGenerate_;
            if ((var2 & 2) != 0) {
               var3 |= 1;
            }

            var1.parameter_ = this.parameter_;
            if (this.protoFileBuilder_ == null) {
               if ((this.bitField0_ & 4) != 0) {
                  this.protoFile_ = Collections.unmodifiableList(this.protoFile_);
                  this.bitField0_ &= -5;
               }

               var1.protoFile_ = this.protoFile_;
            } else {
               var1.protoFile_ = this.protoFileBuilder_.build();
            }

            if ((var2 & 8) != 0) {
               if (this.compilerVersionBuilder_ == null) {
                  var1.compilerVersion_ = this.compilerVersion_;
               } else {
                  var1.compilerVersion_ = this.compilerVersionBuilder_.build();
               }

               var3 |= 2;
            }

            var1.bitField0_ = var3;
            this.onBuilt();
            return var1;
         }

         public PluginProtos.CodeGeneratorRequest.Builder clone() {
            return (PluginProtos.CodeGeneratorRequest.Builder)super.clone();
         }

         public PluginProtos.CodeGeneratorRequest.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
            return (PluginProtos.CodeGeneratorRequest.Builder)super.setField(var1, var2);
         }

         public PluginProtos.CodeGeneratorRequest.Builder clearField(Descriptors.FieldDescriptor var1) {
            return (PluginProtos.CodeGeneratorRequest.Builder)super.clearField(var1);
         }

         public PluginProtos.CodeGeneratorRequest.Builder clearOneof(Descriptors.OneofDescriptor var1) {
            return (PluginProtos.CodeGeneratorRequest.Builder)super.clearOneof(var1);
         }

         public PluginProtos.CodeGeneratorRequest.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
            return (PluginProtos.CodeGeneratorRequest.Builder)super.setRepeatedField(var1, var2, var3);
         }

         public PluginProtos.CodeGeneratorRequest.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
            return (PluginProtos.CodeGeneratorRequest.Builder)super.addRepeatedField(var1, var2);
         }

         public PluginProtos.CodeGeneratorRequest.Builder mergeFrom(Message var1) {
            if (var1 instanceof PluginProtos.CodeGeneratorRequest) {
               return this.mergeFrom((PluginProtos.CodeGeneratorRequest)var1);
            } else {
               super.mergeFrom(var1);
               return this;
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder mergeFrom(PluginProtos.CodeGeneratorRequest var1) {
            if (var1 == PluginProtos.CodeGeneratorRequest.getDefaultInstance()) {
               return this;
            } else {
               if (!var1.fileToGenerate_.isEmpty()) {
                  if (this.fileToGenerate_.isEmpty()) {
                     this.fileToGenerate_ = var1.fileToGenerate_;
                     this.bitField0_ &= -2;
                  } else {
                     this.ensureFileToGenerateIsMutable();
                     this.fileToGenerate_.addAll(var1.fileToGenerate_);
                  }

                  this.onChanged();
               }

               if (var1.hasParameter()) {
                  this.bitField0_ |= 2;
                  this.parameter_ = var1.parameter_;
                  this.onChanged();
               }

               if (this.protoFileBuilder_ == null) {
                  if (!var1.protoFile_.isEmpty()) {
                     if (this.protoFile_.isEmpty()) {
                        this.protoFile_ = var1.protoFile_;
                        this.bitField0_ &= -5;
                     } else {
                        this.ensureProtoFileIsMutable();
                        this.protoFile_.addAll(var1.protoFile_);
                     }

                     this.onChanged();
                  }
               } else if (!var1.protoFile_.isEmpty()) {
                  if (this.protoFileBuilder_.isEmpty()) {
                     this.protoFileBuilder_.dispose();
                     this.protoFileBuilder_ = null;
                     this.protoFile_ = var1.protoFile_;
                     this.bitField0_ &= -5;
                     this.protoFileBuilder_ = PluginProtos.CodeGeneratorRequest.alwaysUseFieldBuilders ? this.getProtoFileFieldBuilder() : null;
                  } else {
                     this.protoFileBuilder_.addAllMessages(var1.protoFile_);
                  }
               }

               if (var1.hasCompilerVersion()) {
                  this.mergeCompilerVersion(var1.getCompilerVersion());
               }

               this.mergeUnknownFields(var1.unknownFields);
               this.onChanged();
               return this;
            }
         }

         @Override
         public final boolean isInitialized() {
            for (int var1 = 0; var1 < this.getProtoFileCount(); var1++) {
               if (!this.getProtoFile(var1).isInitialized()) {
                  return false;
               }
            }

            return true;
         }

         public PluginProtos.CodeGeneratorRequest.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
            PluginProtos.CodeGeneratorRequest var3 = null;

            try {
               var3 = PluginProtos.CodeGeneratorRequest.PARSER.parsePartialFrom(var1, var2);
            } catch (InvalidProtocolBufferException var8) {
               var3 = (PluginProtos.CodeGeneratorRequest)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (var3 != null) {
                  this.mergeFrom(var3);
               }
            }

            return this;
         }

         private void ensureFileToGenerateIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
               this.fileToGenerate_ = new LazyStringArrayList(this.fileToGenerate_);
               this.bitField0_ |= 1;
            }
         }

         public ProtocolStringList getFileToGenerateList() {
            return this.fileToGenerate_.getUnmodifiableView();
         }

         @Override
         public int getFileToGenerateCount() {
            return this.fileToGenerate_.size();
         }

         @Override
         public String getFileToGenerate(int var1) {
            return this.fileToGenerate_.get(var1);
         }

         @Override
         public ByteString getFileToGenerateBytes(int var1) {
            return this.fileToGenerate_.getByteString(var1);
         }

         public PluginProtos.CodeGeneratorRequest.Builder setFileToGenerate(int var1, String var2) {
            if (var2 == null) {
               throw new NullPointerException();
            } else {
               this.ensureFileToGenerateIsMutable();
               this.fileToGenerate_.set(var1, var2);
               this.onChanged();
               return this;
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder addFileToGenerate(String var1) {
            if (var1 == null) {
               throw new NullPointerException();
            } else {
               this.ensureFileToGenerateIsMutable();
               this.fileToGenerate_.add(var1);
               this.onChanged();
               return this;
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder addAllFileToGenerate(Iterable<String> var1) {
            this.ensureFileToGenerateIsMutable();
            AbstractMessageLite.Builder.addAll(var1, this.fileToGenerate_);
            this.onChanged();
            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder clearFileToGenerate() {
            this.fileToGenerate_ = LazyStringArrayList.EMPTY;
            this.bitField0_ &= -2;
            this.onChanged();
            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addFileToGenerateBytes(ByteString var1) {
            if (var1 == null) {
               throw new NullPointerException();
            } else {
               this.ensureFileToGenerateIsMutable();
               this.fileToGenerate_.add(var1);
               this.onChanged();
               return this;
            }
         }

         @Override
         public boolean hasParameter() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getParameter() {
            Object var1 = this.parameter_;
            if (!(var1 instanceof String)) {
               ByteString var2 = (ByteString)var1;
               String var3 = var2.toStringUtf8();
               if (var2.isValidUtf8()) {
                  this.parameter_ = var3;
               }

               return var3;
            } else {
               return (String)var1;
            }
         }

         @Override
         public ByteString getParameterBytes() {
            Object var1 = this.parameter_;
            if (var1 instanceof String) {
               ByteString var2 = ByteString.copyFromUtf8((String)var1);
               this.parameter_ = var2;
               return var2;
            } else {
               return (ByteString)var1;
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder setParameter(String var1) {
            if (var1 == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.parameter_ = var1;
               this.onChanged();
               return this;
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder clearParameter() {
            this.bitField0_ &= -3;
            this.parameter_ = PluginProtos.CodeGeneratorRequest.getDefaultInstance().getParameter();
            this.onChanged();
            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder setParameterBytes(ByteString var1) {
            if (var1 == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 2;
               this.parameter_ = var1;
               this.onChanged();
               return this;
            }
         }

         private void ensureProtoFileIsMutable() {
            if ((this.bitField0_ & 4) == 0) {
               this.protoFile_ = new ArrayList<>(this.protoFile_);
               this.bitField0_ |= 4;
            }
         }

         @Override
         public List<DescriptorProtos.FileDescriptorProto> getProtoFileList() {
            return this.protoFileBuilder_ == null ? Collections.unmodifiableList(this.protoFile_) : this.protoFileBuilder_.getMessageList();
         }

         @Override
         public int getProtoFileCount() {
            return this.protoFileBuilder_ == null ? this.protoFile_.size() : this.protoFileBuilder_.getCount();
         }

         @Override
         public DescriptorProtos.FileDescriptorProto getProtoFile(int var1) {
            return this.protoFileBuilder_ == null ? this.protoFile_.get(var1) : this.protoFileBuilder_.getMessage(var1);
         }

         public PluginProtos.CodeGeneratorRequest.Builder setProtoFile(int var1, DescriptorProtos.FileDescriptorProto var2) {
            if (this.protoFileBuilder_ == null) {
               if (var2 == null) {
                  throw new NullPointerException();
               }

               this.ensureProtoFileIsMutable();
               this.protoFile_.set(var1, var2);
               this.onChanged();
            } else {
               this.protoFileBuilder_.setMessage(var1, var2);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder setProtoFile(int var1, DescriptorProtos.FileDescriptorProto.Builder var2) {
            if (this.protoFileBuilder_ == null) {
               this.ensureProtoFileIsMutable();
               this.protoFile_.set(var1, var2.build());
               this.onChanged();
            } else {
               this.protoFileBuilder_.setMessage(var1, var2.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addProtoFile(DescriptorProtos.FileDescriptorProto var1) {
            if (this.protoFileBuilder_ == null) {
               if (var1 == null) {
                  throw new NullPointerException();
               }

               this.ensureProtoFileIsMutable();
               this.protoFile_.add(var1);
               this.onChanged();
            } else {
               this.protoFileBuilder_.addMessage(var1);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addProtoFile(int var1, DescriptorProtos.FileDescriptorProto var2) {
            if (this.protoFileBuilder_ == null) {
               if (var2 == null) {
                  throw new NullPointerException();
               }

               this.ensureProtoFileIsMutable();
               this.protoFile_.add(var1, var2);
               this.onChanged();
            } else {
               this.protoFileBuilder_.addMessage(var1, var2);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addProtoFile(DescriptorProtos.FileDescriptorProto.Builder var1) {
            if (this.protoFileBuilder_ == null) {
               this.ensureProtoFileIsMutable();
               this.protoFile_.add(var1.build());
               this.onChanged();
            } else {
               this.protoFileBuilder_.addMessage(var1.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addProtoFile(int var1, DescriptorProtos.FileDescriptorProto.Builder var2) {
            if (this.protoFileBuilder_ == null) {
               this.ensureProtoFileIsMutable();
               this.protoFile_.add(var1, var2.build());
               this.onChanged();
            } else {
               this.protoFileBuilder_.addMessage(var1, var2.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder addAllProtoFile(Iterable<? extends DescriptorProtos.FileDescriptorProto> var1) {
            if (this.protoFileBuilder_ == null) {
               this.ensureProtoFileIsMutable();
               AbstractMessageLite.Builder.addAll(var1, this.protoFile_);
               this.onChanged();
            } else {
               this.protoFileBuilder_.addAllMessages(var1);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder clearProtoFile() {
            if (this.protoFileBuilder_ == null) {
               this.protoFile_ = Collections.emptyList();
               this.bitField0_ &= -5;
               this.onChanged();
            } else {
               this.protoFileBuilder_.clear();
            }

            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder removeProtoFile(int var1) {
            if (this.protoFileBuilder_ == null) {
               this.ensureProtoFileIsMutable();
               this.protoFile_.remove(var1);
               this.onChanged();
            } else {
               this.protoFileBuilder_.remove(var1);
            }

            return this;
         }

         public DescriptorProtos.FileDescriptorProto.Builder getProtoFileBuilder(int var1) {
            return this.getProtoFileFieldBuilder().getBuilder(var1);
         }

         @Override
         public DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int var1) {
            return this.protoFileBuilder_ == null ? this.protoFile_.get(var1) : this.protoFileBuilder_.getMessageOrBuilder(var1);
         }

         @Override
         public List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList() {
            return this.protoFileBuilder_ != null ? this.protoFileBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.protoFile_);
         }

         public DescriptorProtos.FileDescriptorProto.Builder addProtoFileBuilder() {
            return this.getProtoFileFieldBuilder().addBuilder(DescriptorProtos.FileDescriptorProto.getDefaultInstance());
         }

         public DescriptorProtos.FileDescriptorProto.Builder addProtoFileBuilder(int var1) {
            return this.getProtoFileFieldBuilder().addBuilder(var1, DescriptorProtos.FileDescriptorProto.getDefaultInstance());
         }

         public List<DescriptorProtos.FileDescriptorProto.Builder> getProtoFileBuilderList() {
            return this.getProtoFileFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<DescriptorProtos.FileDescriptorProto, DescriptorProtos.FileDescriptorProto.Builder, DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileFieldBuilder() {
            if (this.protoFileBuilder_ == null) {
               this.protoFileBuilder_ = new RepeatedFieldBuilderV3<>(this.protoFile_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean());
               this.protoFile_ = null;
            }

            return this.protoFileBuilder_;
         }

         @Override
         public boolean hasCompilerVersion() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public PluginProtos.Version getCompilerVersion() {
            if (this.compilerVersionBuilder_ == null) {
               return this.compilerVersion_ == null ? PluginProtos.Version.getDefaultInstance() : this.compilerVersion_;
            } else {
               return this.compilerVersionBuilder_.getMessage();
            }
         }

         public PluginProtos.CodeGeneratorRequest.Builder setCompilerVersion(PluginProtos.Version var1) {
            if (this.compilerVersionBuilder_ == null) {
               if (var1 == null) {
                  throw new NullPointerException();
               }

               this.compilerVersion_ = var1;
               this.onChanged();
            } else {
               this.compilerVersionBuilder_.setMessage(var1);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder setCompilerVersion(PluginProtos.Version.Builder var1) {
            if (this.compilerVersionBuilder_ == null) {
               this.compilerVersion_ = var1.build();
               this.onChanged();
            } else {
               this.compilerVersionBuilder_.setMessage(var1.build());
            }

            this.bitField0_ |= 8;
            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder mergeCompilerVersion(PluginProtos.Version var1) {
            if (this.compilerVersionBuilder_ == null) {
               if ((this.bitField0_ & 8) != 0 && this.compilerVersion_ != null && this.compilerVersion_ != PluginProtos.Version.getDefaultInstance()) {
                  this.compilerVersion_ = PluginProtos.Version.newBuilder(this.compilerVersion_).mergeFrom(var1).buildPartial();
               } else {
                  this.compilerVersion_ = var1;
               }

               this.onChanged();
            } else {
               this.compilerVersionBuilder_.mergeFrom(var1);
            }

            this.bitField0_ |= 8;
            return this;
         }

         public PluginProtos.CodeGeneratorRequest.Builder clearCompilerVersion() {
            if (this.compilerVersionBuilder_ == null) {
               this.compilerVersion_ = null;
               this.onChanged();
            } else {
               this.compilerVersionBuilder_.clear();
            }

            this.bitField0_ &= -9;
            return this;
         }

         public PluginProtos.Version.Builder getCompilerVersionBuilder() {
            this.bitField0_ |= 8;
            this.onChanged();
            return this.getCompilerVersionFieldBuilder().getBuilder();
         }

         @Override
         public PluginProtos.VersionOrBuilder getCompilerVersionOrBuilder() {
            if (this.compilerVersionBuilder_ != null) {
               return this.compilerVersionBuilder_.getMessageOrBuilder();
            } else {
               return this.compilerVersion_ == null ? PluginProtos.Version.getDefaultInstance() : this.compilerVersion_;
            }
         }

         private SingleFieldBuilderV3<PluginProtos.Version, PluginProtos.Version.Builder, PluginProtos.VersionOrBuilder> getCompilerVersionFieldBuilder() {
            if (this.compilerVersionBuilder_ == null) {
               this.compilerVersionBuilder_ = new SingleFieldBuilderV3<>(this.getCompilerVersion(), this.getParentForChildren(), this.isClean());
               this.compilerVersion_ = null;
            }

            return this.compilerVersionBuilder_;
         }

         public final PluginProtos.CodeGeneratorRequest.Builder setUnknownFields(UnknownFieldSet var1) {
            return (PluginProtos.CodeGeneratorRequest.Builder)super.setUnknownFields(var1);
         }

         public final PluginProtos.CodeGeneratorRequest.Builder mergeUnknownFields(UnknownFieldSet var1) {
            return (PluginProtos.CodeGeneratorRequest.Builder)super.mergeUnknownFields(var1);
         }
      }
   }

   public interface CodeGeneratorRequestOrBuilder extends MessageOrBuilder {
      List<String> getFileToGenerateList();

      int getFileToGenerateCount();

      String getFileToGenerate(int var1);

      ByteString getFileToGenerateBytes(int var1);

      boolean hasParameter();

      String getParameter();

      ByteString getParameterBytes();

      List<DescriptorProtos.FileDescriptorProto> getProtoFileList();

      DescriptorProtos.FileDescriptorProto getProtoFile(int var1);

      int getProtoFileCount();

      List<? extends DescriptorProtos.FileDescriptorProtoOrBuilder> getProtoFileOrBuilderList();

      DescriptorProtos.FileDescriptorProtoOrBuilder getProtoFileOrBuilder(int var1);

      boolean hasCompilerVersion();

      PluginProtos.Version getCompilerVersion();

      PluginProtos.VersionOrBuilder getCompilerVersionOrBuilder();
   }

   public static final class CodeGeneratorResponse extends GeneratedMessageV3 implements PluginProtos.CodeGeneratorResponseOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int ERROR_FIELD_NUMBER = 1;
      private volatile Object error_;
      public static final int FILE_FIELD_NUMBER = 15;
      private List<PluginProtos.CodeGeneratorResponse.File> file_;
      private byte memoizedIsInitialized = -1;
      private static final PluginProtos.CodeGeneratorResponse DEFAULT_INSTANCE = new PluginProtos.CodeGeneratorResponse();
      @Deprecated
      public static final Parser<PluginProtos.CodeGeneratorResponse> PARSER = new AbstractParser<PluginProtos.CodeGeneratorResponse>() {
         public PluginProtos.CodeGeneratorResponse parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
            return new PluginProtos.CodeGeneratorResponse(var1, var2);
         }
      };

      private CodeGeneratorResponse(GeneratedMessageV3.Builder<?> var1) {
         super(var1);
      }

      private CodeGeneratorResponse() {
         this.error_ = "";
         this.file_ = Collections.emptyList();
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
         return new PluginProtos.CodeGeneratorResponse();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private CodeGeneratorResponse(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         this();
         if (var2 == null) {
            throw new NullPointerException();
         } else {
            byte var3 = 0;
            UnknownFieldSet.Builder var4 = UnknownFieldSet.newBuilder();

            try {
               boolean var5 = false;

               while (!var5) {
                  int var6 = var1.readTag();
                  switch (var6) {
                     case 0:
                        var5 = true;
                        break;
                     case 10:
                        ByteString var7 = var1.readBytes();
                        this.bitField0_ |= 1;
                        this.error_ = var7;
                        break;
                     case 122:
                        if ((var3 & 2) == 0) {
                           this.file_ = new ArrayList<>();
                           var3 |= 2;
                        }

                        this.file_.add(var1.readMessage(PluginProtos.CodeGeneratorResponse.File.PARSER, var2));
                        break;
                     default:
                        if (!this.parseUnknownField(var1, var4, var2, var6)) {
                           var5 = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
            } finally {
               if ((var3 & 2) != 0) {
                  this.file_ = Collections.unmodifiableList(this.file_);
               }

               this.unknownFields = var4.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable
            .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorResponse.class, PluginProtos.CodeGeneratorResponse.Builder.class);
      }

      @Override
      public boolean hasError() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public String getError() {
         Object var1 = this.error_;
         if (var1 instanceof String) {
            return (String)var1;
         } else {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            if (var2.isValidUtf8()) {
               this.error_ = var3;
            }

            return var3;
         }
      }

      @Override
      public ByteString getErrorBytes() {
         Object var1 = this.error_;
         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            this.error_ = var2;
            return var2;
         } else {
            return (ByteString)var1;
         }
      }

      @Override
      public List<PluginProtos.CodeGeneratorResponse.File> getFileList() {
         return this.file_;
      }

      @Override
      public List<? extends PluginProtos.CodeGeneratorResponse.FileOrBuilder> getFileOrBuilderList() {
         return this.file_;
      }

      @Override
      public int getFileCount() {
         return this.file_.size();
      }

      @Override
      public PluginProtos.CodeGeneratorResponse.File getFile(int var1) {
         return this.file_.get(var1);
      }

      @Override
      public PluginProtos.CodeGeneratorResponse.FileOrBuilder getFileOrBuilder(int var1) {
         return this.file_.get(var1);
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
         if ((this.bitField0_ & 1) != 0) {
            GeneratedMessageV3.writeString(var1, 1, this.error_);
         }

         for (int var2 = 0; var2 < this.file_.size(); var2++) {
            var1.writeMessage(15, this.file_.get(var2));
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
            if ((this.bitField0_ & 1) != 0) {
               var1 += GeneratedMessageV3.computeStringSize(1, this.error_);
            }

            for (int var2 = 0; var2 < this.file_.size(); var2++) {
               var1 += CodedOutputStream.computeMessageSize(15, this.file_.get(var2));
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
         } else if (!(var1 instanceof PluginProtos.CodeGeneratorResponse)) {
            return super.equals(var1);
         } else {
            PluginProtos.CodeGeneratorResponse var2 = (PluginProtos.CodeGeneratorResponse)var1;
            if (this.hasError() != var2.hasError()) {
               return false;
            } else if (this.hasError() && !this.getError().equals(var2.getError())) {
               return false;
            } else {
               return !this.getFileList().equals(var2.getFileList()) ? false : this.unknownFields.equals(var2.unknownFields);
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
            if (this.hasError()) {
               var1 = 37 * var1 + 1;
               var1 = 53 * var1 + this.getError().hashCode();
            }

            if (this.getFileCount() > 0) {
               var1 = 37 * var1 + 15;
               var1 = 53 * var1 + this.getFileList().hashCode();
            }

            var1 = 29 * var1 + this.unknownFields.hashCode();
            this.memoizedHashCode = var1;
            return var1;
         }
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0, var1);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(ByteString var0) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0, var1);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(byte[] var0) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0, var1);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(InputStream var0) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, var0);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
      }

      public static PluginProtos.CodeGeneratorResponse parseDelimitedFrom(InputStream var0) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
      }

      public static PluginProtos.CodeGeneratorResponse parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(CodedInputStream var0) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, var0);
      }

      public static PluginProtos.CodeGeneratorResponse parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
      }

      public PluginProtos.CodeGeneratorResponse.Builder newBuilderForType() {
         return newBuilder();
      }

      public static PluginProtos.CodeGeneratorResponse.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static PluginProtos.CodeGeneratorResponse.Builder newBuilder(PluginProtos.CodeGeneratorResponse var0) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
      }

      public PluginProtos.CodeGeneratorResponse.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new PluginProtos.CodeGeneratorResponse.Builder() : new PluginProtos.CodeGeneratorResponse.Builder().mergeFrom(this);
      }

      protected PluginProtos.CodeGeneratorResponse.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
         return new PluginProtos.CodeGeneratorResponse.Builder(var1);
      }

      public static PluginProtos.CodeGeneratorResponse getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<PluginProtos.CodeGeneratorResponse> parser() {
         return PARSER;
      }

      @Override
      public Parser<PluginProtos.CodeGeneratorResponse> getParserForType() {
         return PARSER;
      }

      public PluginProtos.CodeGeneratorResponse getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder
         extends GeneratedMessageV3.Builder<PluginProtos.CodeGeneratorResponse.Builder>
         implements PluginProtos.CodeGeneratorResponseOrBuilder {
         private int bitField0_;
         private Object error_ = "";
         private List<PluginProtos.CodeGeneratorResponse.File> file_ = Collections.emptyList();
         private RepeatedFieldBuilderV3<PluginProtos.CodeGeneratorResponse.File, PluginProtos.CodeGeneratorResponse.File.Builder, PluginProtos.CodeGeneratorResponse.FileOrBuilder> fileBuilder_;

         public static final Descriptors.Descriptor getDescriptor() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_fieldAccessorTable
               .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorResponse.class, PluginProtos.CodeGeneratorResponse.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent var1) {
            super(var1);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (PluginProtos.CodeGeneratorResponse.alwaysUseFieldBuilders) {
               this.getFileFieldBuilder();
            }
         }

         public PluginProtos.CodeGeneratorResponse.Builder clear() {
            super.clear();
            this.error_ = "";
            this.bitField0_ &= -2;
            if (this.fileBuilder_ == null) {
               this.file_ = Collections.emptyList();
               this.bitField0_ &= -3;
            } else {
               this.fileBuilder_.clear();
            }

            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_descriptor;
         }

         public PluginProtos.CodeGeneratorResponse getDefaultInstanceForType() {
            return PluginProtos.CodeGeneratorResponse.getDefaultInstance();
         }

         public PluginProtos.CodeGeneratorResponse build() {
            PluginProtos.CodeGeneratorResponse var1 = this.buildPartial();
            if (!var1.isInitialized()) {
               throw newUninitializedMessageException(var1);
            } else {
               return var1;
            }
         }

         public PluginProtos.CodeGeneratorResponse buildPartial() {
            PluginProtos.CodeGeneratorResponse var1 = new PluginProtos.CodeGeneratorResponse(this);
            int var2 = this.bitField0_;
            byte var3 = 0;
            if ((var2 & 1) != 0) {
               var3 |= 1;
            }

            var1.error_ = this.error_;
            if (this.fileBuilder_ == null) {
               if ((this.bitField0_ & 2) != 0) {
                  this.file_ = Collections.unmodifiableList(this.file_);
                  this.bitField0_ &= -3;
               }

               var1.file_ = this.file_;
            } else {
               var1.file_ = this.fileBuilder_.build();
            }

            var1.bitField0_ = var3;
            this.onBuilt();
            return var1;
         }

         public PluginProtos.CodeGeneratorResponse.Builder clone() {
            return (PluginProtos.CodeGeneratorResponse.Builder)super.clone();
         }

         public PluginProtos.CodeGeneratorResponse.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
            return (PluginProtos.CodeGeneratorResponse.Builder)super.setField(var1, var2);
         }

         public PluginProtos.CodeGeneratorResponse.Builder clearField(Descriptors.FieldDescriptor var1) {
            return (PluginProtos.CodeGeneratorResponse.Builder)super.clearField(var1);
         }

         public PluginProtos.CodeGeneratorResponse.Builder clearOneof(Descriptors.OneofDescriptor var1) {
            return (PluginProtos.CodeGeneratorResponse.Builder)super.clearOneof(var1);
         }

         public PluginProtos.CodeGeneratorResponse.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
            return (PluginProtos.CodeGeneratorResponse.Builder)super.setRepeatedField(var1, var2, var3);
         }

         public PluginProtos.CodeGeneratorResponse.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
            return (PluginProtos.CodeGeneratorResponse.Builder)super.addRepeatedField(var1, var2);
         }

         public PluginProtos.CodeGeneratorResponse.Builder mergeFrom(Message var1) {
            if (var1 instanceof PluginProtos.CodeGeneratorResponse) {
               return this.mergeFrom((PluginProtos.CodeGeneratorResponse)var1);
            } else {
               super.mergeFrom(var1);
               return this;
            }
         }

         public PluginProtos.CodeGeneratorResponse.Builder mergeFrom(PluginProtos.CodeGeneratorResponse var1) {
            if (var1 == PluginProtos.CodeGeneratorResponse.getDefaultInstance()) {
               return this;
            } else {
               if (var1.hasError()) {
                  this.bitField0_ |= 1;
                  this.error_ = var1.error_;
                  this.onChanged();
               }

               if (this.fileBuilder_ == null) {
                  if (!var1.file_.isEmpty()) {
                     if (this.file_.isEmpty()) {
                        this.file_ = var1.file_;
                        this.bitField0_ &= -3;
                     } else {
                        this.ensureFileIsMutable();
                        this.file_.addAll(var1.file_);
                     }

                     this.onChanged();
                  }
               } else if (!var1.file_.isEmpty()) {
                  if (this.fileBuilder_.isEmpty()) {
                     this.fileBuilder_.dispose();
                     this.fileBuilder_ = null;
                     this.file_ = var1.file_;
                     this.bitField0_ &= -3;
                     this.fileBuilder_ = PluginProtos.CodeGeneratorResponse.alwaysUseFieldBuilders ? this.getFileFieldBuilder() : null;
                  } else {
                     this.fileBuilder_.addAllMessages(var1.file_);
                  }
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

         public PluginProtos.CodeGeneratorResponse.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
            PluginProtos.CodeGeneratorResponse var3 = null;

            try {
               var3 = PluginProtos.CodeGeneratorResponse.PARSER.parsePartialFrom(var1, var2);
            } catch (InvalidProtocolBufferException var8) {
               var3 = (PluginProtos.CodeGeneratorResponse)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (var3 != null) {
                  this.mergeFrom(var3);
               }
            }

            return this;
         }

         @Override
         public boolean hasError() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getError() {
            Object var1 = this.error_;
            if (!(var1 instanceof String)) {
               ByteString var2 = (ByteString)var1;
               String var3 = var2.toStringUtf8();
               if (var2.isValidUtf8()) {
                  this.error_ = var3;
               }

               return var3;
            } else {
               return (String)var1;
            }
         }

         @Override
         public ByteString getErrorBytes() {
            Object var1 = this.error_;
            if (var1 instanceof String) {
               ByteString var2 = ByteString.copyFromUtf8((String)var1);
               this.error_ = var2;
               return var2;
            } else {
               return (ByteString)var1;
            }
         }

         public PluginProtos.CodeGeneratorResponse.Builder setError(String var1) {
            if (var1 == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.error_ = var1;
               this.onChanged();
               return this;
            }
         }

         public PluginProtos.CodeGeneratorResponse.Builder clearError() {
            this.bitField0_ &= -2;
            this.error_ = PluginProtos.CodeGeneratorResponse.getDefaultInstance().getError();
            this.onChanged();
            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder setErrorBytes(ByteString var1) {
            if (var1 == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 1;
               this.error_ = var1;
               this.onChanged();
               return this;
            }
         }

         private void ensureFileIsMutable() {
            if ((this.bitField0_ & 2) == 0) {
               this.file_ = new ArrayList<>(this.file_);
               this.bitField0_ |= 2;
            }
         }

         @Override
         public List<PluginProtos.CodeGeneratorResponse.File> getFileList() {
            return this.fileBuilder_ == null ? Collections.unmodifiableList(this.file_) : this.fileBuilder_.getMessageList();
         }

         @Override
         public int getFileCount() {
            return this.fileBuilder_ == null ? this.file_.size() : this.fileBuilder_.getCount();
         }

         @Override
         public PluginProtos.CodeGeneratorResponse.File getFile(int var1) {
            return this.fileBuilder_ == null ? this.file_.get(var1) : this.fileBuilder_.getMessage(var1);
         }

         public PluginProtos.CodeGeneratorResponse.Builder setFile(int var1, PluginProtos.CodeGeneratorResponse.File var2) {
            if (this.fileBuilder_ == null) {
               if (var2 == null) {
                  throw new NullPointerException();
               }

               this.ensureFileIsMutable();
               this.file_.set(var1, var2);
               this.onChanged();
            } else {
               this.fileBuilder_.setMessage(var1, var2);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder setFile(int var1, PluginProtos.CodeGeneratorResponse.File.Builder var2) {
            if (this.fileBuilder_ == null) {
               this.ensureFileIsMutable();
               this.file_.set(var1, var2.build());
               this.onChanged();
            } else {
               this.fileBuilder_.setMessage(var1, var2.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder addFile(PluginProtos.CodeGeneratorResponse.File var1) {
            if (this.fileBuilder_ == null) {
               if (var1 == null) {
                  throw new NullPointerException();
               }

               this.ensureFileIsMutable();
               this.file_.add(var1);
               this.onChanged();
            } else {
               this.fileBuilder_.addMessage(var1);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder addFile(int var1, PluginProtos.CodeGeneratorResponse.File var2) {
            if (this.fileBuilder_ == null) {
               if (var2 == null) {
                  throw new NullPointerException();
               }

               this.ensureFileIsMutable();
               this.file_.add(var1, var2);
               this.onChanged();
            } else {
               this.fileBuilder_.addMessage(var1, var2);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder addFile(PluginProtos.CodeGeneratorResponse.File.Builder var1) {
            if (this.fileBuilder_ == null) {
               this.ensureFileIsMutable();
               this.file_.add(var1.build());
               this.onChanged();
            } else {
               this.fileBuilder_.addMessage(var1.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder addFile(int var1, PluginProtos.CodeGeneratorResponse.File.Builder var2) {
            if (this.fileBuilder_ == null) {
               this.ensureFileIsMutable();
               this.file_.add(var1, var2.build());
               this.onChanged();
            } else {
               this.fileBuilder_.addMessage(var1, var2.build());
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder addAllFile(Iterable<? extends PluginProtos.CodeGeneratorResponse.File> var1) {
            if (this.fileBuilder_ == null) {
               this.ensureFileIsMutable();
               AbstractMessageLite.Builder.addAll(var1, this.file_);
               this.onChanged();
            } else {
               this.fileBuilder_.addAllMessages(var1);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder clearFile() {
            if (this.fileBuilder_ == null) {
               this.file_ = Collections.emptyList();
               this.bitField0_ &= -3;
               this.onChanged();
            } else {
               this.fileBuilder_.clear();
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.Builder removeFile(int var1) {
            if (this.fileBuilder_ == null) {
               this.ensureFileIsMutable();
               this.file_.remove(var1);
               this.onChanged();
            } else {
               this.fileBuilder_.remove(var1);
            }

            return this;
         }

         public PluginProtos.CodeGeneratorResponse.File.Builder getFileBuilder(int var1) {
            return this.getFileFieldBuilder().getBuilder(var1);
         }

         @Override
         public PluginProtos.CodeGeneratorResponse.FileOrBuilder getFileOrBuilder(int var1) {
            return this.fileBuilder_ == null ? this.file_.get(var1) : this.fileBuilder_.getMessageOrBuilder(var1);
         }

         @Override
         public List<? extends PluginProtos.CodeGeneratorResponse.FileOrBuilder> getFileOrBuilderList() {
            return this.fileBuilder_ != null ? this.fileBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.file_);
         }

         public PluginProtos.CodeGeneratorResponse.File.Builder addFileBuilder() {
            return this.getFileFieldBuilder().addBuilder(PluginProtos.CodeGeneratorResponse.File.getDefaultInstance());
         }

         public PluginProtos.CodeGeneratorResponse.File.Builder addFileBuilder(int var1) {
            return this.getFileFieldBuilder().addBuilder(var1, PluginProtos.CodeGeneratorResponse.File.getDefaultInstance());
         }

         public List<PluginProtos.CodeGeneratorResponse.File.Builder> getFileBuilderList() {
            return this.getFileFieldBuilder().getBuilderList();
         }

         private RepeatedFieldBuilderV3<PluginProtos.CodeGeneratorResponse.File, PluginProtos.CodeGeneratorResponse.File.Builder, PluginProtos.CodeGeneratorResponse.FileOrBuilder> getFileFieldBuilder() {
            if (this.fileBuilder_ == null) {
               this.fileBuilder_ = new RepeatedFieldBuilderV3<>(this.file_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
               this.file_ = null;
            }

            return this.fileBuilder_;
         }

         public final PluginProtos.CodeGeneratorResponse.Builder setUnknownFields(UnknownFieldSet var1) {
            return (PluginProtos.CodeGeneratorResponse.Builder)super.setUnknownFields(var1);
         }

         public final PluginProtos.CodeGeneratorResponse.Builder mergeUnknownFields(UnknownFieldSet var1) {
            return (PluginProtos.CodeGeneratorResponse.Builder)super.mergeUnknownFields(var1);
         }
      }

      public static final class File extends GeneratedMessageV3 implements PluginProtos.CodeGeneratorResponse.FileOrBuilder {
         private static final long serialVersionUID = 0L;
         private int bitField0_;
         public static final int NAME_FIELD_NUMBER = 1;
         private volatile Object name_;
         public static final int INSERTION_POINT_FIELD_NUMBER = 2;
         private volatile Object insertionPoint_;
         public static final int CONTENT_FIELD_NUMBER = 15;
         private volatile Object content_;
         private byte memoizedIsInitialized = -1;
         private static final PluginProtos.CodeGeneratorResponse.File DEFAULT_INSTANCE = new PluginProtos.CodeGeneratorResponse.File();
         @Deprecated
         public static final Parser<PluginProtos.CodeGeneratorResponse.File> PARSER = new AbstractParser<PluginProtos.CodeGeneratorResponse.File>() {
            public PluginProtos.CodeGeneratorResponse.File parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
               return new PluginProtos.CodeGeneratorResponse.File(var1, var2);
            }
         };

         private File(GeneratedMessageV3.Builder<?> var1) {
            super(var1);
         }

         private File() {
            this.name_ = "";
            this.insertionPoint_ = "";
            this.content_ = "";
         }

         @Override
         protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
            return new PluginProtos.CodeGeneratorResponse.File();
         }

         @Override
         public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
         }

         private File(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
            this();
            if (var2 == null) {
               throw new NullPointerException();
            } else {
               boolean var3 = false;
               UnknownFieldSet.Builder var4 = UnknownFieldSet.newBuilder();

               try {
                  boolean var5 = false;

                  while (!var5) {
                     int var6 = var1.readTag();
                     switch (var6) {
                        case 0:
                           var5 = true;
                           break;
                        case 10:
                           ByteString var16 = var1.readBytes();
                           this.bitField0_ |= 1;
                           this.name_ = var16;
                           break;
                        case 18:
                           ByteString var15 = var1.readBytes();
                           this.bitField0_ |= 2;
                           this.insertionPoint_ = var15;
                           break;
                        case 122:
                           ByteString var7 = var1.readBytes();
                           this.bitField0_ |= 4;
                           this.content_ = var7;
                           break;
                        default:
                           if (!this.parseUnknownField(var1, var4, var2, var6)) {
                              var5 = true;
                           }
                     }
                  }
               } catch (InvalidProtocolBufferException var12) {
                  throw var12.setUnfinishedMessage(this);
               } catch (IOException var13) {
                  throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
               } finally {
                  this.unknownFields = var4.build();
                  this.makeExtensionsImmutable();
               }
            }
         }

         public static final Descriptors.Descriptor getDescriptor() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable
               .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorResponse.File.class, PluginProtos.CodeGeneratorResponse.File.Builder.class);
         }

         @Override
         public boolean hasName() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public String getName() {
            Object var1 = this.name_;
            if (var1 instanceof String) {
               return (String)var1;
            } else {
               ByteString var2 = (ByteString)var1;
               String var3 = var2.toStringUtf8();
               if (var2.isValidUtf8()) {
                  this.name_ = var3;
               }

               return var3;
            }
         }

         @Override
         public ByteString getNameBytes() {
            Object var1 = this.name_;
            if (var1 instanceof String) {
               ByteString var2 = ByteString.copyFromUtf8((String)var1);
               this.name_ = var2;
               return var2;
            } else {
               return (ByteString)var1;
            }
         }

         @Override
         public boolean hasInsertionPoint() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public String getInsertionPoint() {
            Object var1 = this.insertionPoint_;
            if (var1 instanceof String) {
               return (String)var1;
            } else {
               ByteString var2 = (ByteString)var1;
               String var3 = var2.toStringUtf8();
               if (var2.isValidUtf8()) {
                  this.insertionPoint_ = var3;
               }

               return var3;
            }
         }

         @Override
         public ByteString getInsertionPointBytes() {
            Object var1 = this.insertionPoint_;
            if (var1 instanceof String) {
               ByteString var2 = ByteString.copyFromUtf8((String)var1);
               this.insertionPoint_ = var2;
               return var2;
            } else {
               return (ByteString)var1;
            }
         }

         @Override
         public boolean hasContent() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public String getContent() {
            Object var1 = this.content_;
            if (var1 instanceof String) {
               return (String)var1;
            } else {
               ByteString var2 = (ByteString)var1;
               String var3 = var2.toStringUtf8();
               if (var2.isValidUtf8()) {
                  this.content_ = var3;
               }

               return var3;
            }
         }

         @Override
         public ByteString getContentBytes() {
            Object var1 = this.content_;
            if (var1 instanceof String) {
               ByteString var2 = ByteString.copyFromUtf8((String)var1);
               this.content_ = var2;
               return var2;
            } else {
               return (ByteString)var1;
            }
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
            if ((this.bitField0_ & 1) != 0) {
               GeneratedMessageV3.writeString(var1, 1, this.name_);
            }

            if ((this.bitField0_ & 2) != 0) {
               GeneratedMessageV3.writeString(var1, 2, this.insertionPoint_);
            }

            if ((this.bitField0_ & 4) != 0) {
               GeneratedMessageV3.writeString(var1, 15, this.content_);
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
               if ((this.bitField0_ & 1) != 0) {
                  var1 += GeneratedMessageV3.computeStringSize(1, this.name_);
               }

               if ((this.bitField0_ & 2) != 0) {
                  var1 += GeneratedMessageV3.computeStringSize(2, this.insertionPoint_);
               }

               if ((this.bitField0_ & 4) != 0) {
                  var1 += GeneratedMessageV3.computeStringSize(15, this.content_);
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
            } else if (!(var1 instanceof PluginProtos.CodeGeneratorResponse.File)) {
               return super.equals(var1);
            } else {
               PluginProtos.CodeGeneratorResponse.File var2 = (PluginProtos.CodeGeneratorResponse.File)var1;
               if (this.hasName() != var2.hasName()) {
                  return false;
               } else if (this.hasName() && !this.getName().equals(var2.getName())) {
                  return false;
               } else if (this.hasInsertionPoint() != var2.hasInsertionPoint()) {
                  return false;
               } else if (this.hasInsertionPoint() && !this.getInsertionPoint().equals(var2.getInsertionPoint())) {
                  return false;
               } else if (this.hasContent() != var2.hasContent()) {
                  return false;
               } else {
                  return this.hasContent() && !this.getContent().equals(var2.getContent()) ? false : this.unknownFields.equals(var2.unknownFields);
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
               if (this.hasName()) {
                  var1 = 37 * var1 + 1;
                  var1 = 53 * var1 + this.getName().hashCode();
               }

               if (this.hasInsertionPoint()) {
                  var1 = 37 * var1 + 2;
                  var1 = 53 * var1 + this.getInsertionPoint().hashCode();
               }

               if (this.hasContent()) {
                  var1 = 37 * var1 + 15;
                  var1 = 53 * var1 + this.getContent().hashCode();
               }

               var1 = 29 * var1 + this.unknownFields.hashCode();
               this.memoizedHashCode = var1;
               return var1;
            }
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(var0);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(var0, var1);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(ByteString var0) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(var0);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(var0, var1);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(byte[] var0) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(var0);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(var0, var1);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(InputStream var0) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, var0);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseDelimitedFrom(InputStream var0) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
            return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(CodedInputStream var0) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, var0);
         }

         public static PluginProtos.CodeGeneratorResponse.File parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
            return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
         }

         public PluginProtos.CodeGeneratorResponse.File.Builder newBuilderForType() {
            return newBuilder();
         }

         public static PluginProtos.CodeGeneratorResponse.File.Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
         }

         public static PluginProtos.CodeGeneratorResponse.File.Builder newBuilder(PluginProtos.CodeGeneratorResponse.File var0) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
         }

         public PluginProtos.CodeGeneratorResponse.File.Builder toBuilder() {
            return this == DEFAULT_INSTANCE
               ? new PluginProtos.CodeGeneratorResponse.File.Builder()
               : new PluginProtos.CodeGeneratorResponse.File.Builder().mergeFrom(this);
         }

         protected PluginProtos.CodeGeneratorResponse.File.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
            return new PluginProtos.CodeGeneratorResponse.File.Builder(var1);
         }

         public static PluginProtos.CodeGeneratorResponse.File getDefaultInstance() {
            return DEFAULT_INSTANCE;
         }

         public static Parser<PluginProtos.CodeGeneratorResponse.File> parser() {
            return PARSER;
         }

         @Override
         public Parser<PluginProtos.CodeGeneratorResponse.File> getParserForType() {
            return PARSER;
         }

         public PluginProtos.CodeGeneratorResponse.File getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
         }

         public static final class Builder
            extends GeneratedMessageV3.Builder<PluginProtos.CodeGeneratorResponse.File.Builder>
            implements PluginProtos.CodeGeneratorResponse.FileOrBuilder {
            private int bitField0_;
            private Object name_ = "";
            private Object insertionPoint_ = "";
            private Object content_ = "";

            public static final Descriptors.Descriptor getDescriptor() {
               return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
            }

            @Override
            protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
               return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_fieldAccessorTable
                  .ensureFieldAccessorsInitialized(PluginProtos.CodeGeneratorResponse.File.class, PluginProtos.CodeGeneratorResponse.File.Builder.class);
            }

            private Builder() {
               this.maybeForceBuilderInitialization();
            }

            private Builder(GeneratedMessageV3.BuilderParent var1) {
               super(var1);
               this.maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
               if (PluginProtos.CodeGeneratorResponse.File.alwaysUseFieldBuilders) {
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder clear() {
               super.clear();
               this.name_ = "";
               this.bitField0_ &= -2;
               this.insertionPoint_ = "";
               this.bitField0_ &= -3;
               this.content_ = "";
               this.bitField0_ &= -5;
               return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
               return PluginProtos.internal_static_google_protobuf_compiler_CodeGeneratorResponse_File_descriptor;
            }

            public PluginProtos.CodeGeneratorResponse.File getDefaultInstanceForType() {
               return PluginProtos.CodeGeneratorResponse.File.getDefaultInstance();
            }

            public PluginProtos.CodeGeneratorResponse.File build() {
               PluginProtos.CodeGeneratorResponse.File var1 = this.buildPartial();
               if (!var1.isInitialized()) {
                  throw newUninitializedMessageException(var1);
               } else {
                  return var1;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File buildPartial() {
               PluginProtos.CodeGeneratorResponse.File var1 = new PluginProtos.CodeGeneratorResponse.File(this);
               int var2 = this.bitField0_;
               byte var3 = 0;
               if ((var2 & 1) != 0) {
                  var3 |= 1;
               }

               var1.name_ = this.name_;
               if ((var2 & 2) != 0) {
                  var3 |= 2;
               }

               var1.insertionPoint_ = this.insertionPoint_;
               if ((var2 & 4) != 0) {
                  var3 |= 4;
               }

               var1.content_ = this.content_;
               var1.bitField0_ = var3;
               this.onBuilt();
               return var1;
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder clone() {
               return (PluginProtos.CodeGeneratorResponse.File.Builder)super.clone();
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
               return (PluginProtos.CodeGeneratorResponse.File.Builder)super.setField(var1, var2);
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder clearField(Descriptors.FieldDescriptor var1) {
               return (PluginProtos.CodeGeneratorResponse.File.Builder)super.clearField(var1);
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder clearOneof(Descriptors.OneofDescriptor var1) {
               return (PluginProtos.CodeGeneratorResponse.File.Builder)super.clearOneof(var1);
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
               return (PluginProtos.CodeGeneratorResponse.File.Builder)super.setRepeatedField(var1, var2, var3);
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
               return (PluginProtos.CodeGeneratorResponse.File.Builder)super.addRepeatedField(var1, var2);
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder mergeFrom(Message var1) {
               if (var1 instanceof PluginProtos.CodeGeneratorResponse.File) {
                  return this.mergeFrom((PluginProtos.CodeGeneratorResponse.File)var1);
               } else {
                  super.mergeFrom(var1);
                  return this;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder mergeFrom(PluginProtos.CodeGeneratorResponse.File var1) {
               if (var1 == PluginProtos.CodeGeneratorResponse.File.getDefaultInstance()) {
                  return this;
               } else {
                  if (var1.hasName()) {
                     this.bitField0_ |= 1;
                     this.name_ = var1.name_;
                     this.onChanged();
                  }

                  if (var1.hasInsertionPoint()) {
                     this.bitField0_ |= 2;
                     this.insertionPoint_ = var1.insertionPoint_;
                     this.onChanged();
                  }

                  if (var1.hasContent()) {
                     this.bitField0_ |= 4;
                     this.content_ = var1.content_;
                     this.onChanged();
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

            public PluginProtos.CodeGeneratorResponse.File.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
               PluginProtos.CodeGeneratorResponse.File var3 = null;

               try {
                  var3 = PluginProtos.CodeGeneratorResponse.File.PARSER.parsePartialFrom(var1, var2);
               } catch (InvalidProtocolBufferException var8) {
                  var3 = (PluginProtos.CodeGeneratorResponse.File)var8.getUnfinishedMessage();
                  throw var8.unwrapIOException();
               } finally {
                  if (var3 != null) {
                     this.mergeFrom(var3);
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
               Object var1 = this.name_;
               if (!(var1 instanceof String)) {
                  ByteString var2 = (ByteString)var1;
                  String var3 = var2.toStringUtf8();
                  if (var2.isValidUtf8()) {
                     this.name_ = var3;
                  }

                  return var3;
               } else {
                  return (String)var1;
               }
            }

            @Override
            public ByteString getNameBytes() {
               Object var1 = this.name_;
               if (var1 instanceof String) {
                  ByteString var2 = ByteString.copyFromUtf8((String)var1);
                  this.name_ = var2;
                  return var2;
               } else {
                  return (ByteString)var1;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setName(String var1) {
               if (var1 == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 1;
                  this.name_ = var1;
                  this.onChanged();
                  return this;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder clearName() {
               this.bitField0_ &= -2;
               this.name_ = PluginProtos.CodeGeneratorResponse.File.getDefaultInstance().getName();
               this.onChanged();
               return this;
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setNameBytes(ByteString var1) {
               if (var1 == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 1;
                  this.name_ = var1;
                  this.onChanged();
                  return this;
               }
            }

            @Override
            public boolean hasInsertionPoint() {
               return (this.bitField0_ & 2) != 0;
            }

            @Override
            public String getInsertionPoint() {
               Object var1 = this.insertionPoint_;
               if (!(var1 instanceof String)) {
                  ByteString var2 = (ByteString)var1;
                  String var3 = var2.toStringUtf8();
                  if (var2.isValidUtf8()) {
                     this.insertionPoint_ = var3;
                  }

                  return var3;
               } else {
                  return (String)var1;
               }
            }

            @Override
            public ByteString getInsertionPointBytes() {
               Object var1 = this.insertionPoint_;
               if (var1 instanceof String) {
                  ByteString var2 = ByteString.copyFromUtf8((String)var1);
                  this.insertionPoint_ = var2;
                  return var2;
               } else {
                  return (ByteString)var1;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setInsertionPoint(String var1) {
               if (var1 == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 2;
                  this.insertionPoint_ = var1;
                  this.onChanged();
                  return this;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder clearInsertionPoint() {
               this.bitField0_ &= -3;
               this.insertionPoint_ = PluginProtos.CodeGeneratorResponse.File.getDefaultInstance().getInsertionPoint();
               this.onChanged();
               return this;
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setInsertionPointBytes(ByteString var1) {
               if (var1 == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 2;
                  this.insertionPoint_ = var1;
                  this.onChanged();
                  return this;
               }
            }

            @Override
            public boolean hasContent() {
               return (this.bitField0_ & 4) != 0;
            }

            @Override
            public String getContent() {
               Object var1 = this.content_;
               if (!(var1 instanceof String)) {
                  ByteString var2 = (ByteString)var1;
                  String var3 = var2.toStringUtf8();
                  if (var2.isValidUtf8()) {
                     this.content_ = var3;
                  }

                  return var3;
               } else {
                  return (String)var1;
               }
            }

            @Override
            public ByteString getContentBytes() {
               Object var1 = this.content_;
               if (var1 instanceof String) {
                  ByteString var2 = ByteString.copyFromUtf8((String)var1);
                  this.content_ = var2;
                  return var2;
               } else {
                  return (ByteString)var1;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setContent(String var1) {
               if (var1 == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 4;
                  this.content_ = var1;
                  this.onChanged();
                  return this;
               }
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder clearContent() {
               this.bitField0_ &= -5;
               this.content_ = PluginProtos.CodeGeneratorResponse.File.getDefaultInstance().getContent();
               this.onChanged();
               return this;
            }

            public PluginProtos.CodeGeneratorResponse.File.Builder setContentBytes(ByteString var1) {
               if (var1 == null) {
                  throw new NullPointerException();
               } else {
                  this.bitField0_ |= 4;
                  this.content_ = var1;
                  this.onChanged();
                  return this;
               }
            }

            public final PluginProtos.CodeGeneratorResponse.File.Builder setUnknownFields(UnknownFieldSet var1) {
               return (PluginProtos.CodeGeneratorResponse.File.Builder)super.setUnknownFields(var1);
            }

            public final PluginProtos.CodeGeneratorResponse.File.Builder mergeUnknownFields(UnknownFieldSet var1) {
               return (PluginProtos.CodeGeneratorResponse.File.Builder)super.mergeUnknownFields(var1);
            }
         }
      }

      public interface FileOrBuilder extends MessageOrBuilder {
         boolean hasName();

         String getName();

         ByteString getNameBytes();

         boolean hasInsertionPoint();

         String getInsertionPoint();

         ByteString getInsertionPointBytes();

         boolean hasContent();

         String getContent();

         ByteString getContentBytes();
      }
   }

   public interface CodeGeneratorResponseOrBuilder extends MessageOrBuilder {
      boolean hasError();

      String getError();

      ByteString getErrorBytes();

      List<PluginProtos.CodeGeneratorResponse.File> getFileList();

      PluginProtos.CodeGeneratorResponse.File getFile(int var1);

      int getFileCount();

      List<? extends PluginProtos.CodeGeneratorResponse.FileOrBuilder> getFileOrBuilderList();

      PluginProtos.CodeGeneratorResponse.FileOrBuilder getFileOrBuilder(int var1);
   }

   public static final class Version extends GeneratedMessageV3 implements PluginProtos.VersionOrBuilder {
      private static final long serialVersionUID = 0L;
      private int bitField0_;
      public static final int MAJOR_FIELD_NUMBER = 1;
      private int major_;
      public static final int MINOR_FIELD_NUMBER = 2;
      private int minor_;
      public static final int PATCH_FIELD_NUMBER = 3;
      private int patch_;
      public static final int SUFFIX_FIELD_NUMBER = 4;
      private volatile Object suffix_;
      private byte memoizedIsInitialized = -1;
      private static final PluginProtos.Version DEFAULT_INSTANCE = new PluginProtos.Version();
      @Deprecated
      public static final Parser<PluginProtos.Version> PARSER = new AbstractParser<PluginProtos.Version>() {
         public PluginProtos.Version parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
            return new PluginProtos.Version(var1, var2);
         }
      };

      private Version(GeneratedMessageV3.Builder<?> var1) {
         super(var1);
      }

      private Version() {
         this.suffix_ = "";
      }

      @Override
      protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
         return new PluginProtos.Version();
      }

      @Override
      public final UnknownFieldSet getUnknownFields() {
         return this.unknownFields;
      }

      private Version(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         this();
         if (var2 == null) {
            throw new NullPointerException();
         } else {
            boolean var3 = false;
            UnknownFieldSet.Builder var4 = UnknownFieldSet.newBuilder();

            try {
               boolean var5 = false;

               while (!var5) {
                  int var6 = var1.readTag();
                  switch (var6) {
                     case 0:
                        var5 = true;
                        break;
                     case 8:
                        this.bitField0_ |= 1;
                        this.major_ = var1.readInt32();
                        break;
                     case 16:
                        this.bitField0_ |= 2;
                        this.minor_ = var1.readInt32();
                        break;
                     case 24:
                        this.bitField0_ |= 4;
                        this.patch_ = var1.readInt32();
                        break;
                     case 34:
                        ByteString var7 = var1.readBytes();
                        this.bitField0_ |= 8;
                        this.suffix_ = var7;
                        break;
                     default:
                        if (!this.parseUnknownField(var1, var4, var2, var6)) {
                           var5 = true;
                        }
                  }
               }
            } catch (InvalidProtocolBufferException var12) {
               throw var12.setUnfinishedMessage(this);
            } catch (IOException var13) {
               throw new InvalidProtocolBufferException(var13).setUnfinishedMessage(this);
            } finally {
               this.unknownFields = var4.build();
               this.makeExtensionsImmutable();
            }
         }
      }

      public static final Descriptors.Descriptor getDescriptor() {
         return PluginProtos.internal_static_google_protobuf_compiler_Version_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return PluginProtos.internal_static_google_protobuf_compiler_Version_fieldAccessorTable
            .ensureFieldAccessorsInitialized(PluginProtos.Version.class, PluginProtos.Version.Builder.class);
      }

      @Override
      public boolean hasMajor() {
         return (this.bitField0_ & 1) != 0;
      }

      @Override
      public int getMajor() {
         return this.major_;
      }

      @Override
      public boolean hasMinor() {
         return (this.bitField0_ & 2) != 0;
      }

      @Override
      public int getMinor() {
         return this.minor_;
      }

      @Override
      public boolean hasPatch() {
         return (this.bitField0_ & 4) != 0;
      }

      @Override
      public int getPatch() {
         return this.patch_;
      }

      @Override
      public boolean hasSuffix() {
         return (this.bitField0_ & 8) != 0;
      }

      @Override
      public String getSuffix() {
         Object var1 = this.suffix_;
         if (var1 instanceof String) {
            return (String)var1;
         } else {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            if (var2.isValidUtf8()) {
               this.suffix_ = var3;
            }

            return var3;
         }
      }

      @Override
      public ByteString getSuffixBytes() {
         Object var1 = this.suffix_;
         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            this.suffix_ = var2;
            return var2;
         } else {
            return (ByteString)var1;
         }
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
         if ((this.bitField0_ & 1) != 0) {
            var1.writeInt32(1, this.major_);
         }

         if ((this.bitField0_ & 2) != 0) {
            var1.writeInt32(2, this.minor_);
         }

         if ((this.bitField0_ & 4) != 0) {
            var1.writeInt32(3, this.patch_);
         }

         if ((this.bitField0_ & 8) != 0) {
            GeneratedMessageV3.writeString(var1, 4, this.suffix_);
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
            if ((this.bitField0_ & 1) != 0) {
               var1 += CodedOutputStream.computeInt32Size(1, this.major_);
            }

            if ((this.bitField0_ & 2) != 0) {
               var1 += CodedOutputStream.computeInt32Size(2, this.minor_);
            }

            if ((this.bitField0_ & 4) != 0) {
               var1 += CodedOutputStream.computeInt32Size(3, this.patch_);
            }

            if ((this.bitField0_ & 8) != 0) {
               var1 += GeneratedMessageV3.computeStringSize(4, this.suffix_);
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
         } else if (!(var1 instanceof PluginProtos.Version)) {
            return super.equals(var1);
         } else {
            PluginProtos.Version var2 = (PluginProtos.Version)var1;
            if (this.hasMajor() != var2.hasMajor()) {
               return false;
            } else if (this.hasMajor() && this.getMajor() != var2.getMajor()) {
               return false;
            } else if (this.hasMinor() != var2.hasMinor()) {
               return false;
            } else if (this.hasMinor() && this.getMinor() != var2.getMinor()) {
               return false;
            } else if (this.hasPatch() != var2.hasPatch()) {
               return false;
            } else if (this.hasPatch() && this.getPatch() != var2.getPatch()) {
               return false;
            } else if (this.hasSuffix() != var2.hasSuffix()) {
               return false;
            } else {
               return this.hasSuffix() && !this.getSuffix().equals(var2.getSuffix()) ? false : this.unknownFields.equals(var2.unknownFields);
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
            if (this.hasMajor()) {
               var1 = 37 * var1 + 1;
               var1 = 53 * var1 + this.getMajor();
            }

            if (this.hasMinor()) {
               var1 = 37 * var1 + 2;
               var1 = 53 * var1 + this.getMinor();
            }

            if (this.hasPatch()) {
               var1 = 37 * var1 + 3;
               var1 = 53 * var1 + this.getPatch();
            }

            if (this.hasSuffix()) {
               var1 = 37 * var1 + 4;
               var1 = 53 * var1 + this.getSuffix().hashCode();
            }

            var1 = 29 * var1 + this.unknownFields.hashCode();
            this.memoizedHashCode = var1;
            return var1;
         }
      }

      public static PluginProtos.Version parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0);
      }

      public static PluginProtos.Version parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0, var1);
      }

      public static PluginProtos.Version parseFrom(ByteString var0) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0);
      }

      public static PluginProtos.Version parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0, var1);
      }

      public static PluginProtos.Version parseFrom(byte[] var0) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0);
      }

      public static PluginProtos.Version parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
         return PARSER.parseFrom(var0, var1);
      }

      public static PluginProtos.Version parseFrom(InputStream var0) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, var0);
      }

      public static PluginProtos.Version parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
      }

      public static PluginProtos.Version parseDelimitedFrom(InputStream var0) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
      }

      public static PluginProtos.Version parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
         return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
      }

      public static PluginProtos.Version parseFrom(CodedInputStream var0) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, var0);
      }

      public static PluginProtos.Version parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
         return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
      }

      public PluginProtos.Version.Builder newBuilderForType() {
         return newBuilder();
      }

      public static PluginProtos.Version.Builder newBuilder() {
         return DEFAULT_INSTANCE.toBuilder();
      }

      public static PluginProtos.Version.Builder newBuilder(PluginProtos.Version var0) {
         return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
      }

      public PluginProtos.Version.Builder toBuilder() {
         return this == DEFAULT_INSTANCE ? new PluginProtos.Version.Builder() : new PluginProtos.Version.Builder().mergeFrom(this);
      }

      protected PluginProtos.Version.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
         return new PluginProtos.Version.Builder(var1);
      }

      public static PluginProtos.Version getDefaultInstance() {
         return DEFAULT_INSTANCE;
      }

      public static Parser<PluginProtos.Version> parser() {
         return PARSER;
      }

      @Override
      public Parser<PluginProtos.Version> getParserForType() {
         return PARSER;
      }

      public PluginProtos.Version getDefaultInstanceForType() {
         return DEFAULT_INSTANCE;
      }

      public static final class Builder extends GeneratedMessageV3.Builder<PluginProtos.Version.Builder> implements PluginProtos.VersionOrBuilder {
         private int bitField0_;
         private int major_;
         private int minor_;
         private int patch_;
         private Object suffix_ = "";

         public static final Descriptors.Descriptor getDescriptor() {
            return PluginProtos.internal_static_google_protobuf_compiler_Version_descriptor;
         }

         @Override
         protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return PluginProtos.internal_static_google_protobuf_compiler_Version_fieldAccessorTable
               .ensureFieldAccessorsInitialized(PluginProtos.Version.class, PluginProtos.Version.Builder.class);
         }

         private Builder() {
            this.maybeForceBuilderInitialization();
         }

         private Builder(GeneratedMessageV3.BuilderParent var1) {
            super(var1);
            this.maybeForceBuilderInitialization();
         }

         private void maybeForceBuilderInitialization() {
            if (PluginProtos.Version.alwaysUseFieldBuilders) {
            }
         }

         public PluginProtos.Version.Builder clear() {
            super.clear();
            this.major_ = 0;
            this.bitField0_ &= -2;
            this.minor_ = 0;
            this.bitField0_ &= -3;
            this.patch_ = 0;
            this.bitField0_ &= -5;
            this.suffix_ = "";
            this.bitField0_ &= -9;
            return this;
         }

         @Override
         public Descriptors.Descriptor getDescriptorForType() {
            return PluginProtos.internal_static_google_protobuf_compiler_Version_descriptor;
         }

         public PluginProtos.Version getDefaultInstanceForType() {
            return PluginProtos.Version.getDefaultInstance();
         }

         public PluginProtos.Version build() {
            PluginProtos.Version var1 = this.buildPartial();
            if (!var1.isInitialized()) {
               throw newUninitializedMessageException(var1);
            } else {
               return var1;
            }
         }

         public PluginProtos.Version buildPartial() {
            PluginProtos.Version var1 = new PluginProtos.Version(this);
            int var2 = this.bitField0_;
            byte var3 = 0;
            if ((var2 & 1) != 0) {
               var1.major_ = this.major_;
               var3 |= 1;
            }

            if ((var2 & 2) != 0) {
               var1.minor_ = this.minor_;
               var3 |= 2;
            }

            if ((var2 & 4) != 0) {
               var1.patch_ = this.patch_;
               var3 |= 4;
            }

            if ((var2 & 8) != 0) {
               var3 |= 8;
            }

            var1.suffix_ = this.suffix_;
            var1.bitField0_ = var3;
            this.onBuilt();
            return var1;
         }

         public PluginProtos.Version.Builder clone() {
            return (PluginProtos.Version.Builder)super.clone();
         }

         public PluginProtos.Version.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
            return (PluginProtos.Version.Builder)super.setField(var1, var2);
         }

         public PluginProtos.Version.Builder clearField(Descriptors.FieldDescriptor var1) {
            return (PluginProtos.Version.Builder)super.clearField(var1);
         }

         public PluginProtos.Version.Builder clearOneof(Descriptors.OneofDescriptor var1) {
            return (PluginProtos.Version.Builder)super.clearOneof(var1);
         }

         public PluginProtos.Version.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
            return (PluginProtos.Version.Builder)super.setRepeatedField(var1, var2, var3);
         }

         public PluginProtos.Version.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
            return (PluginProtos.Version.Builder)super.addRepeatedField(var1, var2);
         }

         public PluginProtos.Version.Builder mergeFrom(Message var1) {
            if (var1 instanceof PluginProtos.Version) {
               return this.mergeFrom((PluginProtos.Version)var1);
            } else {
               super.mergeFrom(var1);
               return this;
            }
         }

         public PluginProtos.Version.Builder mergeFrom(PluginProtos.Version var1) {
            if (var1 == PluginProtos.Version.getDefaultInstance()) {
               return this;
            } else {
               if (var1.hasMajor()) {
                  this.setMajor(var1.getMajor());
               }

               if (var1.hasMinor()) {
                  this.setMinor(var1.getMinor());
               }

               if (var1.hasPatch()) {
                  this.setPatch(var1.getPatch());
               }

               if (var1.hasSuffix()) {
                  this.bitField0_ |= 8;
                  this.suffix_ = var1.suffix_;
                  this.onChanged();
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

         public PluginProtos.Version.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
            PluginProtos.Version var3 = null;

            try {
               var3 = PluginProtos.Version.PARSER.parsePartialFrom(var1, var2);
            } catch (InvalidProtocolBufferException var8) {
               var3 = (PluginProtos.Version)var8.getUnfinishedMessage();
               throw var8.unwrapIOException();
            } finally {
               if (var3 != null) {
                  this.mergeFrom(var3);
               }
            }

            return this;
         }

         @Override
         public boolean hasMajor() {
            return (this.bitField0_ & 1) != 0;
         }

         @Override
         public int getMajor() {
            return this.major_;
         }

         public PluginProtos.Version.Builder setMajor(int var1) {
            this.bitField0_ |= 1;
            this.major_ = var1;
            this.onChanged();
            return this;
         }

         public PluginProtos.Version.Builder clearMajor() {
            this.bitField0_ &= -2;
            this.major_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasMinor() {
            return (this.bitField0_ & 2) != 0;
         }

         @Override
         public int getMinor() {
            return this.minor_;
         }

         public PluginProtos.Version.Builder setMinor(int var1) {
            this.bitField0_ |= 2;
            this.minor_ = var1;
            this.onChanged();
            return this;
         }

         public PluginProtos.Version.Builder clearMinor() {
            this.bitField0_ &= -3;
            this.minor_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasPatch() {
            return (this.bitField0_ & 4) != 0;
         }

         @Override
         public int getPatch() {
            return this.patch_;
         }

         public PluginProtos.Version.Builder setPatch(int var1) {
            this.bitField0_ |= 4;
            this.patch_ = var1;
            this.onChanged();
            return this;
         }

         public PluginProtos.Version.Builder clearPatch() {
            this.bitField0_ &= -5;
            this.patch_ = 0;
            this.onChanged();
            return this;
         }

         @Override
         public boolean hasSuffix() {
            return (this.bitField0_ & 8) != 0;
         }

         @Override
         public String getSuffix() {
            Object var1 = this.suffix_;
            if (!(var1 instanceof String)) {
               ByteString var2 = (ByteString)var1;
               String var3 = var2.toStringUtf8();
               if (var2.isValidUtf8()) {
                  this.suffix_ = var3;
               }

               return var3;
            } else {
               return (String)var1;
            }
         }

         @Override
         public ByteString getSuffixBytes() {
            Object var1 = this.suffix_;
            if (var1 instanceof String) {
               ByteString var2 = ByteString.copyFromUtf8((String)var1);
               this.suffix_ = var2;
               return var2;
            } else {
               return (ByteString)var1;
            }
         }

         public PluginProtos.Version.Builder setSuffix(String var1) {
            if (var1 == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.suffix_ = var1;
               this.onChanged();
               return this;
            }
         }

         public PluginProtos.Version.Builder clearSuffix() {
            this.bitField0_ &= -9;
            this.suffix_ = PluginProtos.Version.getDefaultInstance().getSuffix();
            this.onChanged();
            return this;
         }

         public PluginProtos.Version.Builder setSuffixBytes(ByteString var1) {
            if (var1 == null) {
               throw new NullPointerException();
            } else {
               this.bitField0_ |= 8;
               this.suffix_ = var1;
               this.onChanged();
               return this;
            }
         }

         public final PluginProtos.Version.Builder setUnknownFields(UnknownFieldSet var1) {
            return (PluginProtos.Version.Builder)super.setUnknownFields(var1);
         }

         public final PluginProtos.Version.Builder mergeUnknownFields(UnknownFieldSet var1) {
            return (PluginProtos.Version.Builder)super.mergeUnknownFields(var1);
         }
      }
   }

   public interface VersionOrBuilder extends MessageOrBuilder {
      boolean hasMajor();

      int getMajor();

      boolean hasMinor();

      int getMinor();

      boolean hasPatch();

      int getPatch();

      boolean hasSuffix();

      String getSuffix();

      ByteString getSuffixBytes();
   }
}
