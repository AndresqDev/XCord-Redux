package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Method extends GeneratedMessageV3 implements MethodOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int NAME_FIELD_NUMBER = 1;
   private volatile Object name_;
   public static final int REQUEST_TYPE_URL_FIELD_NUMBER = 2;
   private volatile Object requestTypeUrl_;
   public static final int REQUEST_STREAMING_FIELD_NUMBER = 3;
   private boolean requestStreaming_;
   public static final int RESPONSE_TYPE_URL_FIELD_NUMBER = 4;
   private volatile Object responseTypeUrl_;
   public static final int RESPONSE_STREAMING_FIELD_NUMBER = 5;
   private boolean responseStreaming_;
   public static final int OPTIONS_FIELD_NUMBER = 6;
   private List<Option> options_;
   public static final int SYNTAX_FIELD_NUMBER = 7;
   private int syntax_;
   private byte memoizedIsInitialized = -1;
   private static final Method DEFAULT_INSTANCE = new Method();
   private static final Parser<Method> PARSER = new AbstractParser<Method>() {
      public Method parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new Method(var1, var2);
      }
   };

   private Method(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private Method() {
      this.name_ = "";
      this.requestTypeUrl_ = "";
      this.responseTypeUrl_ = "";
      this.options_ = Collections.emptyList();
      this.syntax_ = 0;
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new Method();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Method(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                     String var17 = var1.readStringRequireUtf8();
                     this.name_ = var17;
                     break;
                  case 18:
                     String var16 = var1.readStringRequireUtf8();
                     this.requestTypeUrl_ = var16;
                     break;
                  case 24:
                     this.requestStreaming_ = var1.readBool();
                     break;
                  case 34:
                     String var15 = var1.readStringRequireUtf8();
                     this.responseTypeUrl_ = var15;
                     break;
                  case 40:
                     this.responseStreaming_ = var1.readBool();
                     break;
                  case 50:
                     if (!(var3 & true)) {
                        this.options_ = new ArrayList<>();
                        var3 |= true;
                     }

                     this.options_.add(var1.readMessage(Option.parser(), var2));
                     break;
                  case 56:
                     int var7 = var1.readEnum();
                     this.syntax_ = var7;
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
               this.options_ = Collections.unmodifiableList(this.options_);
            }

            this.unknownFields = var4.build();
            this.makeExtensionsImmutable();
         }
      }
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return ApiProto.internal_static_google_protobuf_Method_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return ApiProto.internal_static_google_protobuf_Method_fieldAccessorTable.ensureFieldAccessorsInitialized(Method.class, Method.Builder.class);
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
   public String getRequestTypeUrl() {
      Object var1 = this.requestTypeUrl_;
      if (var1 instanceof String) {
         return (String)var1;
      } else {
         ByteString var2 = (ByteString)var1;
         String var3 = var2.toStringUtf8();
         this.requestTypeUrl_ = var3;
         return var3;
      }
   }

   @Override
   public ByteString getRequestTypeUrlBytes() {
      Object var1 = this.requestTypeUrl_;
      if (var1 instanceof String) {
         ByteString var2 = ByteString.copyFromUtf8((String)var1);
         this.requestTypeUrl_ = var2;
         return var2;
      } else {
         return (ByteString)var1;
      }
   }

   @Override
   public boolean getRequestStreaming() {
      return this.requestStreaming_;
   }

   @Override
   public String getResponseTypeUrl() {
      Object var1 = this.responseTypeUrl_;
      if (var1 instanceof String) {
         return (String)var1;
      } else {
         ByteString var2 = (ByteString)var1;
         String var3 = var2.toStringUtf8();
         this.responseTypeUrl_ = var3;
         return var3;
      }
   }

   @Override
   public ByteString getResponseTypeUrlBytes() {
      Object var1 = this.responseTypeUrl_;
      if (var1 instanceof String) {
         ByteString var2 = ByteString.copyFromUtf8((String)var1);
         this.responseTypeUrl_ = var2;
         return var2;
      } else {
         return (ByteString)var1;
      }
   }

   @Override
   public boolean getResponseStreaming() {
      return this.responseStreaming_;
   }

   @Override
   public List<Option> getOptionsList() {
      return this.options_;
   }

   @Override
   public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
      return this.options_;
   }

   @Override
   public int getOptionsCount() {
      return this.options_.size();
   }

   @Override
   public Option getOptions(int var1) {
      return this.options_.get(var1);
   }

   @Override
   public OptionOrBuilder getOptionsOrBuilder(int var1) {
      return this.options_.get(var1);
   }

   @Override
   public int getSyntaxValue() {
      return this.syntax_;
   }

   @Override
   public Syntax getSyntax() {
      Syntax var1 = Syntax.valueOf(this.syntax_);
      return var1 == null ? Syntax.UNRECOGNIZED : var1;
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

      if (!this.getRequestTypeUrlBytes().isEmpty()) {
         GeneratedMessageV3.writeString(var1, 2, this.requestTypeUrl_);
      }

      if (this.requestStreaming_) {
         var1.writeBool(3, this.requestStreaming_);
      }

      if (!this.getResponseTypeUrlBytes().isEmpty()) {
         GeneratedMessageV3.writeString(var1, 4, this.responseTypeUrl_);
      }

      if (this.responseStreaming_) {
         var1.writeBool(5, this.responseStreaming_);
      }

      for (int var2 = 0; var2 < this.options_.size(); var2++) {
         var1.writeMessage(6, this.options_.get(var2));
      }

      if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
         var1.writeEnum(7, this.syntax_);
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

         if (!this.getRequestTypeUrlBytes().isEmpty()) {
            var1 += GeneratedMessageV3.computeStringSize(2, this.requestTypeUrl_);
         }

         if (this.requestStreaming_) {
            var1 += CodedOutputStream.computeBoolSize(3, this.requestStreaming_);
         }

         if (!this.getResponseTypeUrlBytes().isEmpty()) {
            var1 += GeneratedMessageV3.computeStringSize(4, this.responseTypeUrl_);
         }

         if (this.responseStreaming_) {
            var1 += CodedOutputStream.computeBoolSize(5, this.responseStreaming_);
         }

         for (int var2 = 0; var2 < this.options_.size(); var2++) {
            var1 += CodedOutputStream.computeMessageSize(6, this.options_.get(var2));
         }

         if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
            var1 += CodedOutputStream.computeEnumSize(7, this.syntax_);
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
      } else if (!(var1 instanceof Method)) {
         return super.equals(var1);
      } else {
         Method var2 = (Method)var1;
         if (!this.getName().equals(var2.getName())) {
            return false;
         } else if (!this.getRequestTypeUrl().equals(var2.getRequestTypeUrl())) {
            return false;
         } else if (this.getRequestStreaming() != var2.getRequestStreaming()) {
            return false;
         } else if (!this.getResponseTypeUrl().equals(var2.getResponseTypeUrl())) {
            return false;
         } else if (this.getResponseStreaming() != var2.getResponseStreaming()) {
            return false;
         } else if (!this.getOptionsList().equals(var2.getOptionsList())) {
            return false;
         } else {
            return this.syntax_ != var2.syntax_ ? false : this.unknownFields.equals(var2.unknownFields);
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
         var1 = 53 * var1 + this.getRequestTypeUrl().hashCode();
         var1 = 37 * var1 + 3;
         var1 = 53 * var1 + Internal.hashBoolean(this.getRequestStreaming());
         var1 = 37 * var1 + 4;
         var1 = 53 * var1 + this.getResponseTypeUrl().hashCode();
         var1 = 37 * var1 + 5;
         var1 = 53 * var1 + Internal.hashBoolean(this.getResponseStreaming());
         if (this.getOptionsCount() > 0) {
            var1 = 37 * var1 + 6;
            var1 = 53 * var1 + this.getOptionsList().hashCode();
         }

         var1 = 37 * var1 + 7;
         var1 = 53 * var1 + this.syntax_;
         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static Method parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Method parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Method parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Method parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Method parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Method parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Method parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Method parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static Method parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static Method parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static Method parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Method parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public Method.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Method.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Method.Builder newBuilder(Method var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public Method.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Method.Builder() : new Method.Builder().mergeFrom(this);
   }

   protected Method.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new Method.Builder(var1);
   }

   public static Method getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Method> parser() {
      return PARSER;
   }

   @Override
   public Parser<Method> getParserForType() {
      return PARSER;
   }

   public Method getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Method.Builder> implements MethodOrBuilder {
      private int bitField0_;
      private Object name_ = "";
      private Object requestTypeUrl_ = "";
      private boolean requestStreaming_;
      private Object responseTypeUrl_ = "";
      private boolean responseStreaming_;
      private List<Option> options_ = Collections.emptyList();
      private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
      private int syntax_ = 0;

      public static final Descriptors.Descriptor getDescriptor() {
         return ApiProto.internal_static_google_protobuf_Method_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return ApiProto.internal_static_google_protobuf_Method_fieldAccessorTable.ensureFieldAccessorsInitialized(Method.class, Method.Builder.class);
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
            this.getOptionsFieldBuilder();
         }
      }

      public Method.Builder clear() {
         super.clear();
         this.name_ = "";
         this.requestTypeUrl_ = "";
         this.requestStreaming_ = false;
         this.responseTypeUrl_ = "";
         this.responseStreaming_ = false;
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -2;
         } else {
            this.optionsBuilder_.clear();
         }

         this.syntax_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return ApiProto.internal_static_google_protobuf_Method_descriptor;
      }

      public Method getDefaultInstanceForType() {
         return Method.getDefaultInstance();
      }

      public Method build() {
         Method var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public Method buildPartial() {
         Method var1 = new Method(this);
         int var2 = this.bitField0_;
         var1.name_ = this.name_;
         var1.requestTypeUrl_ = this.requestTypeUrl_;
         var1.requestStreaming_ = this.requestStreaming_;
         var1.responseTypeUrl_ = this.responseTypeUrl_;
         var1.responseStreaming_ = this.responseStreaming_;
         if (this.optionsBuilder_ == null) {
            if ((this.bitField0_ & 1) != 0) {
               this.options_ = Collections.unmodifiableList(this.options_);
               this.bitField0_ &= -2;
            }

            var1.options_ = this.options_;
         } else {
            var1.options_ = this.optionsBuilder_.build();
         }

         var1.syntax_ = this.syntax_;
         this.onBuilt();
         return var1;
      }

      public Method.Builder clone() {
         return (Method.Builder)super.clone();
      }

      public Method.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Method.Builder)super.setField(var1, var2);
      }

      public Method.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (Method.Builder)super.clearField(var1);
      }

      public Method.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (Method.Builder)super.clearOneof(var1);
      }

      public Method.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (Method.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public Method.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Method.Builder)super.addRepeatedField(var1, var2);
      }

      public Method.Builder mergeFrom(Message var1) {
         if (var1 instanceof Method) {
            return this.mergeFrom((Method)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public Method.Builder mergeFrom(Method var1) {
         if (var1 == Method.getDefaultInstance()) {
            return this;
         } else {
            if (!var1.getName().isEmpty()) {
               this.name_ = var1.name_;
               this.onChanged();
            }

            if (!var1.getRequestTypeUrl().isEmpty()) {
               this.requestTypeUrl_ = var1.requestTypeUrl_;
               this.onChanged();
            }

            if (var1.getRequestStreaming()) {
               this.setRequestStreaming(var1.getRequestStreaming());
            }

            if (!var1.getResponseTypeUrl().isEmpty()) {
               this.responseTypeUrl_ = var1.responseTypeUrl_;
               this.onChanged();
            }

            if (var1.getResponseStreaming()) {
               this.setResponseStreaming(var1.getResponseStreaming());
            }

            if (this.optionsBuilder_ == null) {
               if (!var1.options_.isEmpty()) {
                  if (this.options_.isEmpty()) {
                     this.options_ = var1.options_;
                     this.bitField0_ &= -2;
                  } else {
                     this.ensureOptionsIsMutable();
                     this.options_.addAll(var1.options_);
                  }

                  this.onChanged();
               }
            } else if (!var1.options_.isEmpty()) {
               if (this.optionsBuilder_.isEmpty()) {
                  this.optionsBuilder_.dispose();
                  this.optionsBuilder_ = null;
                  this.options_ = var1.options_;
                  this.bitField0_ &= -2;
                  this.optionsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? this.getOptionsFieldBuilder() : null;
               } else {
                  this.optionsBuilder_.addAllMessages(var1.options_);
               }
            }

            if (var1.syntax_ != 0) {
               this.setSyntaxValue(var1.getSyntaxValue());
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

      public Method.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         Method var3 = null;

         try {
            var3 = Method.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (Method)var8.getUnfinishedMessage();
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

      public Method.Builder setName(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.name_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Method.Builder clearName() {
         this.name_ = Method.getDefaultInstance().getName();
         this.onChanged();
         return this;
      }

      public Method.Builder setNameBytes(ByteString var1) {
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
      public String getRequestTypeUrl() {
         Object var1 = this.requestTypeUrl_;
         if (!(var1 instanceof String)) {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            this.requestTypeUrl_ = var3;
            return var3;
         } else {
            return (String)var1;
         }
      }

      @Override
      public ByteString getRequestTypeUrlBytes() {
         Object var1 = this.requestTypeUrl_;
         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            this.requestTypeUrl_ = var2;
            return var2;
         } else {
            return (ByteString)var1;
         }
      }

      public Method.Builder setRequestTypeUrl(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.requestTypeUrl_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Method.Builder clearRequestTypeUrl() {
         this.requestTypeUrl_ = Method.getDefaultInstance().getRequestTypeUrl();
         this.onChanged();
         return this;
      }

      public Method.Builder setRequestTypeUrlBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.requestTypeUrl_ = var1;
            this.onChanged();
            return this;
         }
      }

      @Override
      public boolean getRequestStreaming() {
         return this.requestStreaming_;
      }

      public Method.Builder setRequestStreaming(boolean var1) {
         this.requestStreaming_ = var1;
         this.onChanged();
         return this;
      }

      public Method.Builder clearRequestStreaming() {
         this.requestStreaming_ = false;
         this.onChanged();
         return this;
      }

      @Override
      public String getResponseTypeUrl() {
         Object var1 = this.responseTypeUrl_;
         if (!(var1 instanceof String)) {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            this.responseTypeUrl_ = var3;
            return var3;
         } else {
            return (String)var1;
         }
      }

      @Override
      public ByteString getResponseTypeUrlBytes() {
         Object var1 = this.responseTypeUrl_;
         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            this.responseTypeUrl_ = var2;
            return var2;
         } else {
            return (ByteString)var1;
         }
      }

      public Method.Builder setResponseTypeUrl(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.responseTypeUrl_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Method.Builder clearResponseTypeUrl() {
         this.responseTypeUrl_ = Method.getDefaultInstance().getResponseTypeUrl();
         this.onChanged();
         return this;
      }

      public Method.Builder setResponseTypeUrlBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.responseTypeUrl_ = var1;
            this.onChanged();
            return this;
         }
      }

      @Override
      public boolean getResponseStreaming() {
         return this.responseStreaming_;
      }

      public Method.Builder setResponseStreaming(boolean var1) {
         this.responseStreaming_ = var1;
         this.onChanged();
         return this;
      }

      public Method.Builder clearResponseStreaming() {
         this.responseStreaming_ = false;
         this.onChanged();
         return this;
      }

      private void ensureOptionsIsMutable() {
         if ((this.bitField0_ & 1) == 0) {
            this.options_ = new ArrayList<>(this.options_);
            this.bitField0_ |= 1;
         }
      }

      @Override
      public List<Option> getOptionsList() {
         return this.optionsBuilder_ == null ? Collections.unmodifiableList(this.options_) : this.optionsBuilder_.getMessageList();
      }

      @Override
      public int getOptionsCount() {
         return this.optionsBuilder_ == null ? this.options_.size() : this.optionsBuilder_.getCount();
      }

      @Override
      public Option getOptions(int var1) {
         return this.optionsBuilder_ == null ? this.options_.get(var1) : this.optionsBuilder_.getMessage(var1);
      }

      public Method.Builder setOptions(int var1, Option var2) {
         if (this.optionsBuilder_ == null) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.set(var1, var2);
            this.onChanged();
         } else {
            this.optionsBuilder_.setMessage(var1, var2);
         }

         return this;
      }

      public Method.Builder setOptions(int var1, Option.Builder var2) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.set(var1, var2.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.setMessage(var1, var2.build());
         }

         return this;
      }

      public Method.Builder addOptions(Option var1) {
         if (this.optionsBuilder_ == null) {
            if (var1 == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.add(var1);
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1);
         }

         return this;
      }

      public Method.Builder addOptions(int var1, Option var2) {
         if (this.optionsBuilder_ == null) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            this.ensureOptionsIsMutable();
            this.options_.add(var1, var2);
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1, var2);
         }

         return this;
      }

      public Method.Builder addOptions(Option.Builder var1) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(var1.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1.build());
         }

         return this;
      }

      public Method.Builder addOptions(int var1, Option.Builder var2) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(var1, var2.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1, var2.build());
         }

         return this;
      }

      public Method.Builder addAllOptions(Iterable<? extends Option> var1) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            AbstractMessageLite.Builder.addAll(var1, this.options_);
            this.onChanged();
         } else {
            this.optionsBuilder_.addAllMessages(var1);
         }

         return this;
      }

      public Method.Builder clearOptions() {
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -2;
            this.onChanged();
         } else {
            this.optionsBuilder_.clear();
         }

         return this;
      }

      public Method.Builder removeOptions(int var1) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.remove(var1);
            this.onChanged();
         } else {
            this.optionsBuilder_.remove(var1);
         }

         return this;
      }

      public Option.Builder getOptionsBuilder(int var1) {
         return this.getOptionsFieldBuilder().getBuilder(var1);
      }

      @Override
      public OptionOrBuilder getOptionsOrBuilder(int var1) {
         return this.optionsBuilder_ == null ? this.options_.get(var1) : this.optionsBuilder_.getMessageOrBuilder(var1);
      }

      @Override
      public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
         return this.optionsBuilder_ != null ? this.optionsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.options_);
      }

      public Option.Builder addOptionsBuilder() {
         return this.getOptionsFieldBuilder().addBuilder(Option.getDefaultInstance());
      }

      public Option.Builder addOptionsBuilder(int var1) {
         return this.getOptionsFieldBuilder().addBuilder(var1, Option.getDefaultInstance());
      }

      public List<Option.Builder> getOptionsBuilderList() {
         return this.getOptionsFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> getOptionsFieldBuilder() {
         if (this.optionsBuilder_ == null) {
            this.optionsBuilder_ = new RepeatedFieldBuilderV3<>(this.options_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
            this.options_ = null;
         }

         return this.optionsBuilder_;
      }

      @Override
      public int getSyntaxValue() {
         return this.syntax_;
      }

      public Method.Builder setSyntaxValue(int var1) {
         this.syntax_ = var1;
         this.onChanged();
         return this;
      }

      @Override
      public Syntax getSyntax() {
         Syntax var1 = Syntax.valueOf(this.syntax_);
         return var1 == null ? Syntax.UNRECOGNIZED : var1;
      }

      public Method.Builder setSyntax(Syntax var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.syntax_ = var1.getNumber();
            this.onChanged();
            return this;
         }
      }

      public Method.Builder clearSyntax() {
         this.syntax_ = 0;
         this.onChanged();
         return this;
      }

      public final Method.Builder setUnknownFields(UnknownFieldSet var1) {
         return (Method.Builder)super.setUnknownFields(var1);
      }

      public final Method.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (Method.Builder)super.mergeUnknownFields(var1);
      }
   }
}
