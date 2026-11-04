package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class SourceContext extends GeneratedMessageV3 implements SourceContextOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int FILE_NAME_FIELD_NUMBER = 1;
   private volatile Object fileName_;
   private byte memoizedIsInitialized = -1;
   private static final SourceContext DEFAULT_INSTANCE = new SourceContext();
   private static final Parser<SourceContext> PARSER = new AbstractParser<SourceContext>() {
      public SourceContext parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new SourceContext(var1, var2);
      }
   };

   private SourceContext(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private SourceContext() {
      this.fileName_ = "";
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new SourceContext();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private SourceContext(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      this();
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         UnknownFieldSet.Builder var3 = UnknownFieldSet.newBuilder();

         try {
            boolean var4 = false;

            while (!var4) {
               int var5 = var1.readTag();
               switch (var5) {
                  case 0:
                     var4 = true;
                     break;
                  case 10:
                     String var6 = var1.readStringRequireUtf8();
                     this.fileName_ = var6;
                     break;
                  default:
                     if (!this.parseUnknownField(var1, var3, var2, var5)) {
                        var4 = true;
                     }
               }
            }
         } catch (InvalidProtocolBufferException var11) {
            throw var11.setUnfinishedMessage(this);
         } catch (IOException var12) {
            throw new InvalidProtocolBufferException(var12).setUnfinishedMessage(this);
         } finally {
            this.unknownFields = var3.build();
            this.makeExtensionsImmutable();
         }
      }
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return SourceContextProto.internal_static_google_protobuf_SourceContext_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return SourceContextProto.internal_static_google_protobuf_SourceContext_fieldAccessorTable
         .ensureFieldAccessorsInitialized(SourceContext.class, SourceContext.Builder.class);
   }

   @Override
   public String getFileName() {
      Object var1 = this.fileName_;
      if (var1 instanceof String) {
         return (String)var1;
      } else {
         ByteString var2 = (ByteString)var1;
         String var3 = var2.toStringUtf8();
         this.fileName_ = var3;
         return var3;
      }
   }

   @Override
   public ByteString getFileNameBytes() {
      Object var1 = this.fileName_;
      if (var1 instanceof String) {
         ByteString var2 = ByteString.copyFromUtf8((String)var1);
         this.fileName_ = var2;
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
      if (!this.getFileNameBytes().isEmpty()) {
         GeneratedMessageV3.writeString(var1, 1, this.fileName_);
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
         if (!this.getFileNameBytes().isEmpty()) {
            var1 += GeneratedMessageV3.computeStringSize(1, this.fileName_);
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
      } else if (!(var1 instanceof SourceContext)) {
         return super.equals(var1);
      } else {
         SourceContext var2 = (SourceContext)var1;
         return !this.getFileName().equals(var2.getFileName()) ? false : this.unknownFields.equals(var2.unknownFields);
      }
   }

   @Override
   public int hashCode() {
      if (this.memoizedHashCode != 0) {
         return this.memoizedHashCode;
      } else {
         int var1 = 41;
         var1 = 19 * var1 + getDescriptor().hashCode();
         var1 = 37 * var1 + 1;
         var1 = 53 * var1 + this.getFileName().hashCode();
         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static SourceContext parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static SourceContext parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static SourceContext parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static SourceContext parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static SourceContext parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static SourceContext parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static SourceContext parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static SourceContext parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static SourceContext parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static SourceContext parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static SourceContext parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static SourceContext parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public SourceContext.Builder newBuilderForType() {
      return newBuilder();
   }

   public static SourceContext.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static SourceContext.Builder newBuilder(SourceContext var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public SourceContext.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new SourceContext.Builder() : new SourceContext.Builder().mergeFrom(this);
   }

   protected SourceContext.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new SourceContext.Builder(var1);
   }

   public static SourceContext getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<SourceContext> parser() {
      return PARSER;
   }

   @Override
   public Parser<SourceContext> getParserForType() {
      return PARSER;
   }

   public SourceContext getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<SourceContext.Builder> implements SourceContextOrBuilder {
      private Object fileName_ = "";

      public static final Descriptors.Descriptor getDescriptor() {
         return SourceContextProto.internal_static_google_protobuf_SourceContext_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return SourceContextProto.internal_static_google_protobuf_SourceContext_fieldAccessorTable
            .ensureFieldAccessorsInitialized(SourceContext.class, SourceContext.Builder.class);
      }

      private Builder() {
         this.maybeForceBuilderInitialization();
      }

      private Builder(GeneratedMessageV3.BuilderParent var1) {
         super(var1);
         this.maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
         if (GeneratedMessageV3.alwaysUseFieldBuilders) {
         }
      }

      public SourceContext.Builder clear() {
         super.clear();
         this.fileName_ = "";
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return SourceContextProto.internal_static_google_protobuf_SourceContext_descriptor;
      }

      public SourceContext getDefaultInstanceForType() {
         return SourceContext.getDefaultInstance();
      }

      public SourceContext build() {
         SourceContext var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public SourceContext buildPartial() {
         SourceContext var1 = new SourceContext(this);
         var1.fileName_ = this.fileName_;
         this.onBuilt();
         return var1;
      }

      public SourceContext.Builder clone() {
         return (SourceContext.Builder)super.clone();
      }

      public SourceContext.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (SourceContext.Builder)super.setField(var1, var2);
      }

      public SourceContext.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (SourceContext.Builder)super.clearField(var1);
      }

      public SourceContext.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (SourceContext.Builder)super.clearOneof(var1);
      }

      public SourceContext.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (SourceContext.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public SourceContext.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (SourceContext.Builder)super.addRepeatedField(var1, var2);
      }

      public SourceContext.Builder mergeFrom(Message var1) {
         if (var1 instanceof SourceContext) {
            return this.mergeFrom((SourceContext)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public SourceContext.Builder mergeFrom(SourceContext var1) {
         if (var1 == SourceContext.getDefaultInstance()) {
            return this;
         } else {
            if (!var1.getFileName().isEmpty()) {
               this.fileName_ = var1.fileName_;
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

      public SourceContext.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         SourceContext var3 = null;

         try {
            var3 = SourceContext.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (SourceContext)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      @Override
      public String getFileName() {
         Object var1 = this.fileName_;
         if (!(var1 instanceof String)) {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            this.fileName_ = var3;
            return var3;
         } else {
            return (String)var1;
         }
      }

      @Override
      public ByteString getFileNameBytes() {
         Object var1 = this.fileName_;
         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            this.fileName_ = var2;
            return var2;
         } else {
            return (ByteString)var1;
         }
      }

      public SourceContext.Builder setFileName(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.fileName_ = var1;
            this.onChanged();
            return this;
         }
      }

      public SourceContext.Builder clearFileName() {
         this.fileName_ = SourceContext.getDefaultInstance().getFileName();
         this.onChanged();
         return this;
      }

      public SourceContext.Builder setFileNameBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.fileName_ = var1;
            this.onChanged();
            return this;
         }
      }

      public final SourceContext.Builder setUnknownFields(UnknownFieldSet var1) {
         return (SourceContext.Builder)super.setUnknownFields(var1);
      }

      public final SourceContext.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (SourceContext.Builder)super.mergeUnknownFields(var1);
      }
   }
}
