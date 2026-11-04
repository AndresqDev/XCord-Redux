package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class FieldMask extends GeneratedMessageV3 implements FieldMaskOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int PATHS_FIELD_NUMBER = 1;
   private LazyStringList paths_;
   private byte memoizedIsInitialized = -1;
   private static final FieldMask DEFAULT_INSTANCE = new FieldMask();
   private static final Parser<FieldMask> PARSER = new AbstractParser<FieldMask>() {
      public FieldMask parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new FieldMask(var1, var2);
      }
   };

   private FieldMask(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private FieldMask() {
      this.paths_ = LazyStringArrayList.EMPTY;
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new FieldMask();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private FieldMask(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                     String var7 = var1.readStringRequireUtf8();
                     if (!(var3 & true)) {
                        this.paths_ = new LazyStringArrayList();
                        var3 |= true;
                     }

                     this.paths_.add(var7);
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
            if (var3 & true) {
               this.paths_ = this.paths_.getUnmodifiableView();
            }

            this.unknownFields = var4.build();
            this.makeExtensionsImmutable();
         }
      }
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return FieldMaskProto.internal_static_google_protobuf_FieldMask_fieldAccessorTable
         .ensureFieldAccessorsInitialized(FieldMask.class, FieldMask.Builder.class);
   }

   public ProtocolStringList getPathsList() {
      return this.paths_;
   }

   @Override
   public int getPathsCount() {
      return this.paths_.size();
   }

   @Override
   public String getPaths(int var1) {
      return this.paths_.get(var1);
   }

   @Override
   public ByteString getPathsBytes(int var1) {
      return this.paths_.getByteString(var1);
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
      for (int var2 = 0; var2 < this.paths_.size(); var2++) {
         GeneratedMessageV3.writeString(var1, 1, this.paths_.getRaw(var2));
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

         for (int var3 = 0; var3 < this.paths_.size(); var3++) {
            var2 += computeStringSizeNoTag(this.paths_.getRaw(var3));
         }

         var4 += var2;
         var4 += 1 * this.getPathsList().size();
         var4 += this.unknownFields.getSerializedSize();
         this.memoizedSize = var4;
         return var4;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof FieldMask)) {
         return super.equals(var1);
      } else {
         FieldMask var2 = (FieldMask)var1;
         return !this.getPathsList().equals(var2.getPathsList()) ? false : this.unknownFields.equals(var2.unknownFields);
      }
   }

   @Override
   public int hashCode() {
      if (this.memoizedHashCode != 0) {
         return this.memoizedHashCode;
      } else {
         int var1 = 41;
         var1 = 19 * var1 + getDescriptor().hashCode();
         if (this.getPathsCount() > 0) {
            var1 = 37 * var1 + 1;
            var1 = 53 * var1 + this.getPathsList().hashCode();
         }

         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static FieldMask parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static FieldMask parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static FieldMask parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static FieldMask parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static FieldMask parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static FieldMask parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static FieldMask parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static FieldMask parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static FieldMask parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static FieldMask parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static FieldMask parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static FieldMask parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public FieldMask.Builder newBuilderForType() {
      return newBuilder();
   }

   public static FieldMask.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static FieldMask.Builder newBuilder(FieldMask var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public FieldMask.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new FieldMask.Builder() : new FieldMask.Builder().mergeFrom(this);
   }

   protected FieldMask.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new FieldMask.Builder(var1);
   }

   public static FieldMask getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<FieldMask> parser() {
      return PARSER;
   }

   @Override
   public Parser<FieldMask> getParserForType() {
      return PARSER;
   }

   public FieldMask getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<FieldMask.Builder> implements FieldMaskOrBuilder {
      private int bitField0_;
      private LazyStringList paths_ = LazyStringArrayList.EMPTY;

      public static final Descriptors.Descriptor getDescriptor() {
         return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return FieldMaskProto.internal_static_google_protobuf_FieldMask_fieldAccessorTable
            .ensureFieldAccessorsInitialized(FieldMask.class, FieldMask.Builder.class);
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

      public FieldMask.Builder clear() {
         super.clear();
         this.paths_ = LazyStringArrayList.EMPTY;
         this.bitField0_ &= -2;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor;
      }

      public FieldMask getDefaultInstanceForType() {
         return FieldMask.getDefaultInstance();
      }

      public FieldMask build() {
         FieldMask var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public FieldMask buildPartial() {
         FieldMask var1 = new FieldMask(this);
         int var2 = this.bitField0_;
         if ((this.bitField0_ & 1) != 0) {
            this.paths_ = this.paths_.getUnmodifiableView();
            this.bitField0_ &= -2;
         }

         var1.paths_ = this.paths_;
         this.onBuilt();
         return var1;
      }

      public FieldMask.Builder clone() {
         return (FieldMask.Builder)super.clone();
      }

      public FieldMask.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (FieldMask.Builder)super.setField(var1, var2);
      }

      public FieldMask.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (FieldMask.Builder)super.clearField(var1);
      }

      public FieldMask.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (FieldMask.Builder)super.clearOneof(var1);
      }

      public FieldMask.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (FieldMask.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public FieldMask.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (FieldMask.Builder)super.addRepeatedField(var1, var2);
      }

      public FieldMask.Builder mergeFrom(Message var1) {
         if (var1 instanceof FieldMask) {
            return this.mergeFrom((FieldMask)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public FieldMask.Builder mergeFrom(FieldMask var1) {
         if (var1 == FieldMask.getDefaultInstance()) {
            return this;
         } else {
            if (!var1.paths_.isEmpty()) {
               if (this.paths_.isEmpty()) {
                  this.paths_ = var1.paths_;
                  this.bitField0_ &= -2;
               } else {
                  this.ensurePathsIsMutable();
                  this.paths_.addAll(var1.paths_);
               }

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

      public FieldMask.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         FieldMask var3 = null;

         try {
            var3 = FieldMask.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (FieldMask)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      private void ensurePathsIsMutable() {
         if ((this.bitField0_ & 1) == 0) {
            this.paths_ = new LazyStringArrayList(this.paths_);
            this.bitField0_ |= 1;
         }
      }

      public ProtocolStringList getPathsList() {
         return this.paths_.getUnmodifiableView();
      }

      @Override
      public int getPathsCount() {
         return this.paths_.size();
      }

      @Override
      public String getPaths(int var1) {
         return this.paths_.get(var1);
      }

      @Override
      public ByteString getPathsBytes(int var1) {
         return this.paths_.getByteString(var1);
      }

      public FieldMask.Builder setPaths(int var1, String var2) {
         if (var2 == null) {
            throw new NullPointerException();
         } else {
            this.ensurePathsIsMutable();
            this.paths_.set(var1, var2);
            this.onChanged();
            return this;
         }
      }

      public FieldMask.Builder addPaths(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.ensurePathsIsMutable();
            this.paths_.add(var1);
            this.onChanged();
            return this;
         }
      }

      public FieldMask.Builder addAllPaths(Iterable<String> var1) {
         this.ensurePathsIsMutable();
         AbstractMessageLite.Builder.addAll(var1, this.paths_);
         this.onChanged();
         return this;
      }

      public FieldMask.Builder clearPaths() {
         this.paths_ = LazyStringArrayList.EMPTY;
         this.bitField0_ &= -2;
         this.onChanged();
         return this;
      }

      public FieldMask.Builder addPathsBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.ensurePathsIsMutable();
            this.paths_.add(var1);
            this.onChanged();
            return this;
         }
      }

      public final FieldMask.Builder setUnknownFields(UnknownFieldSet var1) {
         return (FieldMask.Builder)super.setUnknownFields(var1);
      }

      public final FieldMask.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (FieldMask.Builder)super.mergeUnknownFields(var1);
      }
   }
}
