package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListValue extends GeneratedMessageV3 implements ListValueOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int VALUES_FIELD_NUMBER = 1;
   private List<Value> values_;
   private byte memoizedIsInitialized = -1;
   private static final ListValue DEFAULT_INSTANCE = new ListValue();
   private static final Parser<ListValue> PARSER = new AbstractParser<ListValue>() {
      public ListValue parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new ListValue(var1, var2);
      }
   };

   private ListValue(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private ListValue() {
      this.values_ = Collections.emptyList();
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new ListValue();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private ListValue(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                     if (!(var3 & true)) {
                        this.values_ = new ArrayList<>();
                        var3 |= true;
                     }

                     this.values_.add(var1.readMessage(Value.parser(), var2));
                     break;
                  default:
                     if (!this.parseUnknownField(var1, var4, var2, var6)) {
                        var5 = true;
                     }
               }
            }
         } catch (InvalidProtocolBufferException var11) {
            throw var11.setUnfinishedMessage(this);
         } catch (IOException var12) {
            throw new InvalidProtocolBufferException(var12).setUnfinishedMessage(this);
         } finally {
            if (var3 & true) {
               this.values_ = Collections.unmodifiableList(this.values_);
            }

            this.unknownFields = var4.build();
            this.makeExtensionsImmutable();
         }
      }
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return StructProto.internal_static_google_protobuf_ListValue_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return StructProto.internal_static_google_protobuf_ListValue_fieldAccessorTable.ensureFieldAccessorsInitialized(ListValue.class, ListValue.Builder.class);
   }

   @Override
   public List<Value> getValuesList() {
      return this.values_;
   }

   @Override
   public List<? extends ValueOrBuilder> getValuesOrBuilderList() {
      return this.values_;
   }

   @Override
   public int getValuesCount() {
      return this.values_.size();
   }

   @Override
   public Value getValues(int var1) {
      return this.values_.get(var1);
   }

   @Override
   public ValueOrBuilder getValuesOrBuilder(int var1) {
      return this.values_.get(var1);
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
      for (int var2 = 0; var2 < this.values_.size(); var2++) {
         var1.writeMessage(1, this.values_.get(var2));
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

         for (int var2 = 0; var2 < this.values_.size(); var2++) {
            var1 += CodedOutputStream.computeMessageSize(1, this.values_.get(var2));
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
      } else if (!(var1 instanceof ListValue)) {
         return super.equals(var1);
      } else {
         ListValue var2 = (ListValue)var1;
         return !this.getValuesList().equals(var2.getValuesList()) ? false : this.unknownFields.equals(var2.unknownFields);
      }
   }

   @Override
   public int hashCode() {
      if (this.memoizedHashCode != 0) {
         return this.memoizedHashCode;
      } else {
         int var1 = 41;
         var1 = 19 * var1 + getDescriptor().hashCode();
         if (this.getValuesCount() > 0) {
            var1 = 37 * var1 + 1;
            var1 = 53 * var1 + this.getValuesList().hashCode();
         }

         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static ListValue parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static ListValue parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static ListValue parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static ListValue parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static ListValue parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static ListValue parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static ListValue parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static ListValue parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static ListValue parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static ListValue parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static ListValue parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static ListValue parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public ListValue.Builder newBuilderForType() {
      return newBuilder();
   }

   public static ListValue.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static ListValue.Builder newBuilder(ListValue var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public ListValue.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new ListValue.Builder() : new ListValue.Builder().mergeFrom(this);
   }

   protected ListValue.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new ListValue.Builder(var1);
   }

   public static ListValue getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<ListValue> parser() {
      return PARSER;
   }

   @Override
   public Parser<ListValue> getParserForType() {
      return PARSER;
   }

   public ListValue getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<ListValue.Builder> implements ListValueOrBuilder {
      private int bitField0_;
      private List<Value> values_ = Collections.emptyList();
      private RepeatedFieldBuilderV3<Value, Value.Builder, ValueOrBuilder> valuesBuilder_;

      public static final Descriptors.Descriptor getDescriptor() {
         return StructProto.internal_static_google_protobuf_ListValue_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return StructProto.internal_static_google_protobuf_ListValue_fieldAccessorTable
            .ensureFieldAccessorsInitialized(ListValue.class, ListValue.Builder.class);
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
            this.getValuesFieldBuilder();
         }
      }

      public ListValue.Builder clear() {
         super.clear();
         if (this.valuesBuilder_ == null) {
            this.values_ = Collections.emptyList();
            this.bitField0_ &= -2;
         } else {
            this.valuesBuilder_.clear();
         }

         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return StructProto.internal_static_google_protobuf_ListValue_descriptor;
      }

      public ListValue getDefaultInstanceForType() {
         return ListValue.getDefaultInstance();
      }

      public ListValue build() {
         ListValue var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public ListValue buildPartial() {
         ListValue var1 = new ListValue(this);
         int var2 = this.bitField0_;
         if (this.valuesBuilder_ == null) {
            if ((this.bitField0_ & 1) != 0) {
               this.values_ = Collections.unmodifiableList(this.values_);
               this.bitField0_ &= -2;
            }

            var1.values_ = this.values_;
         } else {
            var1.values_ = this.valuesBuilder_.build();
         }

         this.onBuilt();
         return var1;
      }

      public ListValue.Builder clone() {
         return (ListValue.Builder)super.clone();
      }

      public ListValue.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (ListValue.Builder)super.setField(var1, var2);
      }

      public ListValue.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (ListValue.Builder)super.clearField(var1);
      }

      public ListValue.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (ListValue.Builder)super.clearOneof(var1);
      }

      public ListValue.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (ListValue.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public ListValue.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (ListValue.Builder)super.addRepeatedField(var1, var2);
      }

      public ListValue.Builder mergeFrom(Message var1) {
         if (var1 instanceof ListValue) {
            return this.mergeFrom((ListValue)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public ListValue.Builder mergeFrom(ListValue var1) {
         if (var1 == ListValue.getDefaultInstance()) {
            return this;
         } else {
            if (this.valuesBuilder_ == null) {
               if (!var1.values_.isEmpty()) {
                  if (this.values_.isEmpty()) {
                     this.values_ = var1.values_;
                     this.bitField0_ &= -2;
                  } else {
                     this.ensureValuesIsMutable();
                     this.values_.addAll(var1.values_);
                  }

                  this.onChanged();
               }
            } else if (!var1.values_.isEmpty()) {
               if (this.valuesBuilder_.isEmpty()) {
                  this.valuesBuilder_.dispose();
                  this.valuesBuilder_ = null;
                  this.values_ = var1.values_;
                  this.bitField0_ &= -2;
                  this.valuesBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? this.getValuesFieldBuilder() : null;
               } else {
                  this.valuesBuilder_.addAllMessages(var1.values_);
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

      public ListValue.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         ListValue var3 = null;

         try {
            var3 = ListValue.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (ListValue)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      private void ensureValuesIsMutable() {
         if ((this.bitField0_ & 1) == 0) {
            this.values_ = new ArrayList<>(this.values_);
            this.bitField0_ |= 1;
         }
      }

      @Override
      public List<Value> getValuesList() {
         return this.valuesBuilder_ == null ? Collections.unmodifiableList(this.values_) : this.valuesBuilder_.getMessageList();
      }

      @Override
      public int getValuesCount() {
         return this.valuesBuilder_ == null ? this.values_.size() : this.valuesBuilder_.getCount();
      }

      @Override
      public Value getValues(int var1) {
         return this.valuesBuilder_ == null ? this.values_.get(var1) : this.valuesBuilder_.getMessage(var1);
      }

      public ListValue.Builder setValues(int var1, Value var2) {
         if (this.valuesBuilder_ == null) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            this.ensureValuesIsMutable();
            this.values_.set(var1, var2);
            this.onChanged();
         } else {
            this.valuesBuilder_.setMessage(var1, var2);
         }

         return this;
      }

      public ListValue.Builder setValues(int var1, Value.Builder var2) {
         if (this.valuesBuilder_ == null) {
            this.ensureValuesIsMutable();
            this.values_.set(var1, var2.build());
            this.onChanged();
         } else {
            this.valuesBuilder_.setMessage(var1, var2.build());
         }

         return this;
      }

      public ListValue.Builder addValues(Value var1) {
         if (this.valuesBuilder_ == null) {
            if (var1 == null) {
               throw new NullPointerException();
            }

            this.ensureValuesIsMutable();
            this.values_.add(var1);
            this.onChanged();
         } else {
            this.valuesBuilder_.addMessage(var1);
         }

         return this;
      }

      public ListValue.Builder addValues(int var1, Value var2) {
         if (this.valuesBuilder_ == null) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            this.ensureValuesIsMutable();
            this.values_.add(var1, var2);
            this.onChanged();
         } else {
            this.valuesBuilder_.addMessage(var1, var2);
         }

         return this;
      }

      public ListValue.Builder addValues(Value.Builder var1) {
         if (this.valuesBuilder_ == null) {
            this.ensureValuesIsMutable();
            this.values_.add(var1.build());
            this.onChanged();
         } else {
            this.valuesBuilder_.addMessage(var1.build());
         }

         return this;
      }

      public ListValue.Builder addValues(int var1, Value.Builder var2) {
         if (this.valuesBuilder_ == null) {
            this.ensureValuesIsMutable();
            this.values_.add(var1, var2.build());
            this.onChanged();
         } else {
            this.valuesBuilder_.addMessage(var1, var2.build());
         }

         return this;
      }

      public ListValue.Builder addAllValues(Iterable<? extends Value> var1) {
         if (this.valuesBuilder_ == null) {
            this.ensureValuesIsMutable();
            AbstractMessageLite.Builder.addAll(var1, this.values_);
            this.onChanged();
         } else {
            this.valuesBuilder_.addAllMessages(var1);
         }

         return this;
      }

      public ListValue.Builder clearValues() {
         if (this.valuesBuilder_ == null) {
            this.values_ = Collections.emptyList();
            this.bitField0_ &= -2;
            this.onChanged();
         } else {
            this.valuesBuilder_.clear();
         }

         return this;
      }

      public ListValue.Builder removeValues(int var1) {
         if (this.valuesBuilder_ == null) {
            this.ensureValuesIsMutable();
            this.values_.remove(var1);
            this.onChanged();
         } else {
            this.valuesBuilder_.remove(var1);
         }

         return this;
      }

      public Value.Builder getValuesBuilder(int var1) {
         return this.getValuesFieldBuilder().getBuilder(var1);
      }

      @Override
      public ValueOrBuilder getValuesOrBuilder(int var1) {
         return this.valuesBuilder_ == null ? this.values_.get(var1) : this.valuesBuilder_.getMessageOrBuilder(var1);
      }

      @Override
      public List<? extends ValueOrBuilder> getValuesOrBuilderList() {
         return this.valuesBuilder_ != null ? this.valuesBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.values_);
      }

      public Value.Builder addValuesBuilder() {
         return this.getValuesFieldBuilder().addBuilder(Value.getDefaultInstance());
      }

      public Value.Builder addValuesBuilder(int var1) {
         return this.getValuesFieldBuilder().addBuilder(var1, Value.getDefaultInstance());
      }

      public List<Value.Builder> getValuesBuilderList() {
         return this.getValuesFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilderV3<Value, Value.Builder, ValueOrBuilder> getValuesFieldBuilder() {
         if (this.valuesBuilder_ == null) {
            this.valuesBuilder_ = new RepeatedFieldBuilderV3<>(this.values_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
            this.values_ = null;
         }

         return this.valuesBuilder_;
      }

      public final ListValue.Builder setUnknownFields(UnknownFieldSet var1) {
         return (ListValue.Builder)super.setUnknownFields(var1);
      }

      public final ListValue.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (ListValue.Builder)super.mergeUnknownFields(var1);
      }
   }
}
