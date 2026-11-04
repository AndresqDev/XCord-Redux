package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Duration extends GeneratedMessageV3 implements DurationOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int SECONDS_FIELD_NUMBER = 1;
   private long seconds_;
   public static final int NANOS_FIELD_NUMBER = 2;
   private int nanos_;
   private byte memoizedIsInitialized = -1;
   private static final Duration DEFAULT_INSTANCE = new Duration();
   private static final Parser<Duration> PARSER = new AbstractParser<Duration>() {
      public Duration parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new Duration(var1, var2);
      }
   };

   private Duration(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private Duration() {
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new Duration();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Duration(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                  case 8:
                     this.seconds_ = var1.readInt64();
                     break;
                  case 16:
                     this.nanos_ = var1.readInt32();
                     break;
                  default:
                     if (!this.parseUnknownField(var1, var3, var2, var5)) {
                        var4 = true;
                     }
               }
            }
         } catch (InvalidProtocolBufferException var10) {
            throw var10.setUnfinishedMessage(this);
         } catch (IOException var11) {
            throw new InvalidProtocolBufferException(var11).setUnfinishedMessage(this);
         } finally {
            this.unknownFields = var3.build();
            this.makeExtensionsImmutable();
         }
      }
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return DurationProto.internal_static_google_protobuf_Duration_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return DurationProto.internal_static_google_protobuf_Duration_fieldAccessorTable.ensureFieldAccessorsInitialized(Duration.class, Duration.Builder.class);
   }

   @Override
   public long getSeconds() {
      return this.seconds_;
   }

   @Override
   public int getNanos() {
      return this.nanos_;
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
      if (this.seconds_ != 0L) {
         var1.writeInt64(1, this.seconds_);
      }

      if (this.nanos_ != 0) {
         var1.writeInt32(2, this.nanos_);
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
         if (this.seconds_ != 0L) {
            var1 += CodedOutputStream.computeInt64Size(1, this.seconds_);
         }

         if (this.nanos_ != 0) {
            var1 += CodedOutputStream.computeInt32Size(2, this.nanos_);
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
      } else if (!(var1 instanceof Duration)) {
         return super.equals(var1);
      } else {
         Duration var2 = (Duration)var1;
         if (this.getSeconds() != var2.getSeconds()) {
            return false;
         } else {
            return this.getNanos() != var2.getNanos() ? false : this.unknownFields.equals(var2.unknownFields);
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
         var1 = 53 * var1 + Internal.hashLong(this.getSeconds());
         var1 = 37 * var1 + 2;
         var1 = 53 * var1 + this.getNanos();
         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static Duration parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Duration parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Duration parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Duration parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Duration parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Duration parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Duration parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Duration parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static Duration parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static Duration parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static Duration parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Duration parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public Duration.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Duration.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Duration.Builder newBuilder(Duration var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public Duration.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Duration.Builder() : new Duration.Builder().mergeFrom(this);
   }

   protected Duration.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new Duration.Builder(var1);
   }

   public static Duration getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Duration> parser() {
      return PARSER;
   }

   @Override
   public Parser<Duration> getParserForType() {
      return PARSER;
   }

   public Duration getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Duration.Builder> implements DurationOrBuilder {
      private long seconds_;
      private int nanos_;

      public static final Descriptors.Descriptor getDescriptor() {
         return DurationProto.internal_static_google_protobuf_Duration_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return DurationProto.internal_static_google_protobuf_Duration_fieldAccessorTable
            .ensureFieldAccessorsInitialized(Duration.class, Duration.Builder.class);
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

      public Duration.Builder clear() {
         super.clear();
         this.seconds_ = 0L;
         this.nanos_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return DurationProto.internal_static_google_protobuf_Duration_descriptor;
      }

      public Duration getDefaultInstanceForType() {
         return Duration.getDefaultInstance();
      }

      public Duration build() {
         Duration var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public Duration buildPartial() {
         Duration var1 = new Duration(this);
         var1.seconds_ = this.seconds_;
         var1.nanos_ = this.nanos_;
         this.onBuilt();
         return var1;
      }

      public Duration.Builder clone() {
         return (Duration.Builder)super.clone();
      }

      public Duration.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Duration.Builder)super.setField(var1, var2);
      }

      public Duration.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (Duration.Builder)super.clearField(var1);
      }

      public Duration.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (Duration.Builder)super.clearOneof(var1);
      }

      public Duration.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (Duration.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public Duration.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Duration.Builder)super.addRepeatedField(var1, var2);
      }

      public Duration.Builder mergeFrom(Message var1) {
         if (var1 instanceof Duration) {
            return this.mergeFrom((Duration)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public Duration.Builder mergeFrom(Duration var1) {
         if (var1 == Duration.getDefaultInstance()) {
            return this;
         } else {
            if (var1.getSeconds() != 0L) {
               this.setSeconds(var1.getSeconds());
            }

            if (var1.getNanos() != 0) {
               this.setNanos(var1.getNanos());
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

      public Duration.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         Duration var3 = null;

         try {
            var3 = Duration.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (Duration)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      @Override
      public long getSeconds() {
         return this.seconds_;
      }

      public Duration.Builder setSeconds(long var1) {
         this.seconds_ = var1;
         this.onChanged();
         return this;
      }

      public Duration.Builder clearSeconds() {
         this.seconds_ = 0L;
         this.onChanged();
         return this;
      }

      @Override
      public int getNanos() {
         return this.nanos_;
      }

      public Duration.Builder setNanos(int var1) {
         this.nanos_ = var1;
         this.onChanged();
         return this;
      }

      public Duration.Builder clearNanos() {
         this.nanos_ = 0;
         this.onChanged();
         return this;
      }

      public final Duration.Builder setUnknownFields(UnknownFieldSet var1) {
         return (Duration.Builder)super.setUnknownFields(var1);
      }

      public final Duration.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (Duration.Builder)super.mergeUnknownFields(var1);
      }
   }
}
