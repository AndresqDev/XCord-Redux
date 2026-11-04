package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Mixin extends GeneratedMessageV3 implements MixinOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int NAME_FIELD_NUMBER = 1;
   private volatile Object name_;
   public static final int ROOT_FIELD_NUMBER = 2;
   private volatile Object root_;
   private byte memoizedIsInitialized = -1;
   private static final Mixin DEFAULT_INSTANCE = new Mixin();
   private static final Parser<Mixin> PARSER = new AbstractParser<Mixin>() {
      public Mixin parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new Mixin(var1, var2);
      }
   };

   private Mixin(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private Mixin() {
      this.name_ = "";
      this.root_ = "";
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new Mixin();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Mixin(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                     String var14 = var1.readStringRequireUtf8();
                     this.name_ = var14;
                     break;
                  case 18:
                     String var6 = var1.readStringRequireUtf8();
                     this.root_ = var6;
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
      return ApiProto.internal_static_google_protobuf_Mixin_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return ApiProto.internal_static_google_protobuf_Mixin_fieldAccessorTable.ensureFieldAccessorsInitialized(Mixin.class, Mixin.Builder.class);
   }

   @Override
   public String getName() {
      Object var1 = this.name_;
      if (var1 instanceof String) {
         return (String)var1;
      } else {
         ByteString var2 = (ByteString)var1;
         String var3 = var2.toStringUtf8();
         this.name_ = var3;
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
   public String getRoot() {
      Object var1 = this.root_;
      if (var1 instanceof String) {
         return (String)var1;
      } else {
         ByteString var2 = (ByteString)var1;
         String var3 = var2.toStringUtf8();
         this.root_ = var3;
         return var3;
      }
   }

   @Override
   public ByteString getRootBytes() {
      Object var1 = this.root_;
      if (var1 instanceof String) {
         ByteString var2 = ByteString.copyFromUtf8((String)var1);
         this.root_ = var2;
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
      if (!this.getNameBytes().isEmpty()) {
         GeneratedMessageV3.writeString(var1, 1, this.name_);
      }

      if (!this.getRootBytes().isEmpty()) {
         GeneratedMessageV3.writeString(var1, 2, this.root_);
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
         if (!this.getNameBytes().isEmpty()) {
            var1 += GeneratedMessageV3.computeStringSize(1, this.name_);
         }

         if (!this.getRootBytes().isEmpty()) {
            var1 += GeneratedMessageV3.computeStringSize(2, this.root_);
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
      } else if (!(var1 instanceof Mixin)) {
         return super.equals(var1);
      } else {
         Mixin var2 = (Mixin)var1;
         if (!this.getName().equals(var2.getName())) {
            return false;
         } else {
            return !this.getRoot().equals(var2.getRoot()) ? false : this.unknownFields.equals(var2.unknownFields);
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
         var1 = 37 * var1 + 1;
         var1 = 53 * var1 + this.getName().hashCode();
         var1 = 37 * var1 + 2;
         var1 = 53 * var1 + this.getRoot().hashCode();
         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static Mixin parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Mixin parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Mixin parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Mixin parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Mixin parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Mixin parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Mixin parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Mixin parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static Mixin parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static Mixin parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static Mixin parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Mixin parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public Mixin.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Mixin.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Mixin.Builder newBuilder(Mixin var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public Mixin.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Mixin.Builder() : new Mixin.Builder().mergeFrom(this);
   }

   protected Mixin.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new Mixin.Builder(var1);
   }

   public static Mixin getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Mixin> parser() {
      return PARSER;
   }

   @Override
   public Parser<Mixin> getParserForType() {
      return PARSER;
   }

   public Mixin getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Mixin.Builder> implements MixinOrBuilder {
      private Object name_ = "";
      private Object root_ = "";

      public static final Descriptors.Descriptor getDescriptor() {
         return ApiProto.internal_static_google_protobuf_Mixin_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return ApiProto.internal_static_google_protobuf_Mixin_fieldAccessorTable.ensureFieldAccessorsInitialized(Mixin.class, Mixin.Builder.class);
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

      public Mixin.Builder clear() {
         super.clear();
         this.name_ = "";
         this.root_ = "";
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return ApiProto.internal_static_google_protobuf_Mixin_descriptor;
      }

      public Mixin getDefaultInstanceForType() {
         return Mixin.getDefaultInstance();
      }

      public Mixin build() {
         Mixin var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public Mixin buildPartial() {
         Mixin var1 = new Mixin(this);
         var1.name_ = this.name_;
         var1.root_ = this.root_;
         this.onBuilt();
         return var1;
      }

      public Mixin.Builder clone() {
         return (Mixin.Builder)super.clone();
      }

      public Mixin.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Mixin.Builder)super.setField(var1, var2);
      }

      public Mixin.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (Mixin.Builder)super.clearField(var1);
      }

      public Mixin.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (Mixin.Builder)super.clearOneof(var1);
      }

      public Mixin.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (Mixin.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public Mixin.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Mixin.Builder)super.addRepeatedField(var1, var2);
      }

      public Mixin.Builder mergeFrom(Message var1) {
         if (var1 instanceof Mixin) {
            return this.mergeFrom((Mixin)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public Mixin.Builder mergeFrom(Mixin var1) {
         if (var1 == Mixin.getDefaultInstance()) {
            return this;
         } else {
            if (!var1.getName().isEmpty()) {
               this.name_ = var1.name_;
               this.onChanged();
            }

            if (!var1.getRoot().isEmpty()) {
               this.root_ = var1.root_;
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

      public Mixin.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         Mixin var3 = null;

         try {
            var3 = Mixin.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (Mixin)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      @Override
      public String getName() {
         Object var1 = this.name_;
         if (!(var1 instanceof String)) {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            this.name_ = var3;
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

      public Mixin.Builder setName(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.name_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Mixin.Builder clearName() {
         this.name_ = Mixin.getDefaultInstance().getName();
         this.onChanged();
         return this;
      }

      public Mixin.Builder setNameBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.name_ = var1;
            this.onChanged();
            return this;
         }
      }

      @Override
      public String getRoot() {
         Object var1 = this.root_;
         if (!(var1 instanceof String)) {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            this.root_ = var3;
            return var3;
         } else {
            return (String)var1;
         }
      }

      @Override
      public ByteString getRootBytes() {
         Object var1 = this.root_;
         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            this.root_ = var2;
            return var2;
         } else {
            return (ByteString)var1;
         }
      }

      public Mixin.Builder setRoot(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.root_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Mixin.Builder clearRoot() {
         this.root_ = Mixin.getDefaultInstance().getRoot();
         this.onChanged();
         return this;
      }

      public Mixin.Builder setRootBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.root_ = var1;
            this.onChanged();
            return this;
         }
      }

      public final Mixin.Builder setUnknownFields(UnknownFieldSet var1) {
         return (Mixin.Builder)super.setUnknownFields(var1);
      }

      public final Mixin.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (Mixin.Builder)super.mergeUnknownFields(var1);
      }
   }
}
