package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Api extends GeneratedMessageV3 implements ApiOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int NAME_FIELD_NUMBER = 1;
   private volatile Object name_;
   public static final int METHODS_FIELD_NUMBER = 2;
   private List<Method> methods_;
   public static final int OPTIONS_FIELD_NUMBER = 3;
   private List<Option> options_;
   public static final int VERSION_FIELD_NUMBER = 4;
   private volatile Object version_;
   public static final int SOURCE_CONTEXT_FIELD_NUMBER = 5;
   private SourceContext sourceContext_;
   public static final int MIXINS_FIELD_NUMBER = 6;
   private List<Mixin> mixins_;
   public static final int SYNTAX_FIELD_NUMBER = 7;
   private int syntax_;
   private byte memoizedIsInitialized = -1;
   private static final Api DEFAULT_INSTANCE = new Api();
   private static final Parser<Api> PARSER = new AbstractParser<Api>() {
      public Api parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new Api(var1, var2);
      }
   };

   private Api(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private Api() {
      this.name_ = "";
      this.methods_ = Collections.emptyList();
      this.options_ = Collections.emptyList();
      this.version_ = "";
      this.mixins_ = Collections.emptyList();
      this.syntax_ = 0;
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new Api();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Api(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                     String var17 = var1.readStringRequireUtf8();
                     this.name_ = var17;
                     break;
                  case 18:
                     if ((var3 & 1) == 0) {
                        this.methods_ = new ArrayList<>();
                        var3 |= 1;
                     }

                     this.methods_.add(var1.readMessage(Method.parser(), var2));
                     break;
                  case 26:
                     if ((var3 & 2) == 0) {
                        this.options_ = new ArrayList<>();
                        var3 |= 2;
                     }

                     this.options_.add(var1.readMessage(Option.parser(), var2));
                     break;
                  case 34:
                     String var16 = var1.readStringRequireUtf8();
                     this.version_ = var16;
                     break;
                  case 42:
                     SourceContext.Builder var15 = null;
                     if (this.sourceContext_ != null) {
                        var15 = this.sourceContext_.toBuilder();
                     }

                     this.sourceContext_ = var1.readMessage(SourceContext.parser(), var2);
                     if (var15 != null) {
                        var15.mergeFrom(this.sourceContext_);
                        this.sourceContext_ = var15.buildPartial();
                     }
                     break;
                  case 50:
                     if ((var3 & 4) == 0) {
                        this.mixins_ = new ArrayList<>();
                        var3 |= 4;
                     }

                     this.mixins_.add(var1.readMessage(Mixin.parser(), var2));
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
            if ((var3 & 1) != 0) {
               this.methods_ = Collections.unmodifiableList(this.methods_);
            }

            if ((var3 & 2) != 0) {
               this.options_ = Collections.unmodifiableList(this.options_);
            }

            if ((var3 & 4) != 0) {
               this.mixins_ = Collections.unmodifiableList(this.mixins_);
            }

            this.unknownFields = var4.build();
            this.makeExtensionsImmutable();
         }
      }
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return ApiProto.internal_static_google_protobuf_Api_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return ApiProto.internal_static_google_protobuf_Api_fieldAccessorTable.ensureFieldAccessorsInitialized(Api.class, Api.Builder.class);
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
   public List<Method> getMethodsList() {
      return this.methods_;
   }

   @Override
   public List<? extends MethodOrBuilder> getMethodsOrBuilderList() {
      return this.methods_;
   }

   @Override
   public int getMethodsCount() {
      return this.methods_.size();
   }

   @Override
   public Method getMethods(int var1) {
      return this.methods_.get(var1);
   }

   @Override
   public MethodOrBuilder getMethodsOrBuilder(int var1) {
      return this.methods_.get(var1);
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
   public String getVersion() {
      Object var1 = this.version_;
      if (var1 instanceof String) {
         return (String)var1;
      } else {
         ByteString var2 = (ByteString)var1;
         String var3 = var2.toStringUtf8();
         this.version_ = var3;
         return var3;
      }
   }

   @Override
   public ByteString getVersionBytes() {
      Object var1 = this.version_;
      if (var1 instanceof String) {
         ByteString var2 = ByteString.copyFromUtf8((String)var1);
         this.version_ = var2;
         return var2;
      } else {
         return (ByteString)var1;
      }
   }

   @Override
   public boolean hasSourceContext() {
      return this.sourceContext_ != null;
   }

   @Override
   public SourceContext getSourceContext() {
      return this.sourceContext_ == null ? SourceContext.getDefaultInstance() : this.sourceContext_;
   }

   @Override
   public SourceContextOrBuilder getSourceContextOrBuilder() {
      return this.getSourceContext();
   }

   @Override
   public List<Mixin> getMixinsList() {
      return this.mixins_;
   }

   @Override
   public List<? extends MixinOrBuilder> getMixinsOrBuilderList() {
      return this.mixins_;
   }

   @Override
   public int getMixinsCount() {
      return this.mixins_.size();
   }

   @Override
   public Mixin getMixins(int var1) {
      return this.mixins_.get(var1);
   }

   @Override
   public MixinOrBuilder getMixinsOrBuilder(int var1) {
      return this.mixins_.get(var1);
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

      for (int var2 = 0; var2 < this.methods_.size(); var2++) {
         var1.writeMessage(2, this.methods_.get(var2));
      }

      for (int var3 = 0; var3 < this.options_.size(); var3++) {
         var1.writeMessage(3, this.options_.get(var3));
      }

      if (!this.getVersionBytes().isEmpty()) {
         GeneratedMessageV3.writeString(var1, 4, this.version_);
      }

      if (this.sourceContext_ != null) {
         var1.writeMessage(5, this.getSourceContext());
      }

      for (int var4 = 0; var4 < this.mixins_.size(); var4++) {
         var1.writeMessage(6, this.mixins_.get(var4));
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

         for (int var2 = 0; var2 < this.methods_.size(); var2++) {
            var1 += CodedOutputStream.computeMessageSize(2, this.methods_.get(var2));
         }

         for (int var5 = 0; var5 < this.options_.size(); var5++) {
            var1 += CodedOutputStream.computeMessageSize(3, this.options_.get(var5));
         }

         if (!this.getVersionBytes().isEmpty()) {
            var1 += GeneratedMessageV3.computeStringSize(4, this.version_);
         }

         if (this.sourceContext_ != null) {
            var1 += CodedOutputStream.computeMessageSize(5, this.getSourceContext());
         }

         for (int var6 = 0; var6 < this.mixins_.size(); var6++) {
            var1 += CodedOutputStream.computeMessageSize(6, this.mixins_.get(var6));
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
      } else if (!(var1 instanceof Api)) {
         return super.equals(var1);
      } else {
         Api var2 = (Api)var1;
         if (!this.getName().equals(var2.getName())) {
            return false;
         } else if (!this.getMethodsList().equals(var2.getMethodsList())) {
            return false;
         } else if (!this.getOptionsList().equals(var2.getOptionsList())) {
            return false;
         } else if (!this.getVersion().equals(var2.getVersion())) {
            return false;
         } else if (this.hasSourceContext() != var2.hasSourceContext()) {
            return false;
         } else if (this.hasSourceContext() && !this.getSourceContext().equals(var2.getSourceContext())) {
            return false;
         } else if (!this.getMixinsList().equals(var2.getMixinsList())) {
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
         if (this.getMethodsCount() > 0) {
            var1 = 37 * var1 + 2;
            var1 = 53 * var1 + this.getMethodsList().hashCode();
         }

         if (this.getOptionsCount() > 0) {
            var1 = 37 * var1 + 3;
            var1 = 53 * var1 + this.getOptionsList().hashCode();
         }

         var1 = 37 * var1 + 4;
         var1 = 53 * var1 + this.getVersion().hashCode();
         if (this.hasSourceContext()) {
            var1 = 37 * var1 + 5;
            var1 = 53 * var1 + this.getSourceContext().hashCode();
         }

         if (this.getMixinsCount() > 0) {
            var1 = 37 * var1 + 6;
            var1 = 53 * var1 + this.getMixinsList().hashCode();
         }

         var1 = 37 * var1 + 7;
         var1 = 53 * var1 + this.syntax_;
         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static Api parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Api parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Api parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Api parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Api parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Api parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Api parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Api parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static Api parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static Api parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static Api parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Api parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public Api.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Api.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Api.Builder newBuilder(Api var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public Api.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Api.Builder() : new Api.Builder().mergeFrom(this);
   }

   protected Api.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new Api.Builder(var1);
   }

   public static Api getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Api> parser() {
      return PARSER;
   }

   @Override
   public Parser<Api> getParserForType() {
      return PARSER;
   }

   public Api getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Api.Builder> implements ApiOrBuilder {
      private int bitField0_;
      private Object name_ = "";
      private List<Method> methods_ = Collections.emptyList();
      private RepeatedFieldBuilderV3<Method, Method.Builder, MethodOrBuilder> methodsBuilder_;
      private List<Option> options_ = Collections.emptyList();
      private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
      private Object version_ = "";
      private SourceContext sourceContext_;
      private SingleFieldBuilderV3<SourceContext, SourceContext.Builder, SourceContextOrBuilder> sourceContextBuilder_;
      private List<Mixin> mixins_ = Collections.emptyList();
      private RepeatedFieldBuilderV3<Mixin, Mixin.Builder, MixinOrBuilder> mixinsBuilder_;
      private int syntax_ = 0;

      public static final Descriptors.Descriptor getDescriptor() {
         return ApiProto.internal_static_google_protobuf_Api_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return ApiProto.internal_static_google_protobuf_Api_fieldAccessorTable.ensureFieldAccessorsInitialized(Api.class, Api.Builder.class);
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
            this.getMethodsFieldBuilder();
            this.getOptionsFieldBuilder();
            this.getMixinsFieldBuilder();
         }
      }

      public Api.Builder clear() {
         super.clear();
         this.name_ = "";
         if (this.methodsBuilder_ == null) {
            this.methods_ = Collections.emptyList();
            this.bitField0_ &= -2;
         } else {
            this.methodsBuilder_.clear();
         }

         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -3;
         } else {
            this.optionsBuilder_.clear();
         }

         this.version_ = "";
         if (this.sourceContextBuilder_ == null) {
            this.sourceContext_ = null;
         } else {
            this.sourceContext_ = null;
            this.sourceContextBuilder_ = null;
         }

         if (this.mixinsBuilder_ == null) {
            this.mixins_ = Collections.emptyList();
            this.bitField0_ &= -5;
         } else {
            this.mixinsBuilder_.clear();
         }

         this.syntax_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return ApiProto.internal_static_google_protobuf_Api_descriptor;
      }

      public Api getDefaultInstanceForType() {
         return Api.getDefaultInstance();
      }

      public Api build() {
         Api var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public Api buildPartial() {
         Api var1 = new Api(this);
         int var2 = this.bitField0_;
         var1.name_ = this.name_;
         if (this.methodsBuilder_ == null) {
            if ((this.bitField0_ & 1) != 0) {
               this.methods_ = Collections.unmodifiableList(this.methods_);
               this.bitField0_ &= -2;
            }

            var1.methods_ = this.methods_;
         } else {
            var1.methods_ = this.methodsBuilder_.build();
         }

         if (this.optionsBuilder_ == null) {
            if ((this.bitField0_ & 2) != 0) {
               this.options_ = Collections.unmodifiableList(this.options_);
               this.bitField0_ &= -3;
            }

            var1.options_ = this.options_;
         } else {
            var1.options_ = this.optionsBuilder_.build();
         }

         var1.version_ = this.version_;
         if (this.sourceContextBuilder_ == null) {
            var1.sourceContext_ = this.sourceContext_;
         } else {
            var1.sourceContext_ = this.sourceContextBuilder_.build();
         }

         if (this.mixinsBuilder_ == null) {
            if ((this.bitField0_ & 4) != 0) {
               this.mixins_ = Collections.unmodifiableList(this.mixins_);
               this.bitField0_ &= -5;
            }

            var1.mixins_ = this.mixins_;
         } else {
            var1.mixins_ = this.mixinsBuilder_.build();
         }

         var1.syntax_ = this.syntax_;
         this.onBuilt();
         return var1;
      }

      public Api.Builder clone() {
         return (Api.Builder)super.clone();
      }

      public Api.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Api.Builder)super.setField(var1, var2);
      }

      public Api.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (Api.Builder)super.clearField(var1);
      }

      public Api.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (Api.Builder)super.clearOneof(var1);
      }

      public Api.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (Api.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public Api.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Api.Builder)super.addRepeatedField(var1, var2);
      }

      public Api.Builder mergeFrom(Message var1) {
         if (var1 instanceof Api) {
            return this.mergeFrom((Api)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public Api.Builder mergeFrom(Api var1) {
         if (var1 == Api.getDefaultInstance()) {
            return this;
         } else {
            if (!var1.getName().isEmpty()) {
               this.name_ = var1.name_;
               this.onChanged();
            }

            if (this.methodsBuilder_ == null) {
               if (!var1.methods_.isEmpty()) {
                  if (this.methods_.isEmpty()) {
                     this.methods_ = var1.methods_;
                     this.bitField0_ &= -2;
                  } else {
                     this.ensureMethodsIsMutable();
                     this.methods_.addAll(var1.methods_);
                  }

                  this.onChanged();
               }
            } else if (!var1.methods_.isEmpty()) {
               if (this.methodsBuilder_.isEmpty()) {
                  this.methodsBuilder_.dispose();
                  this.methodsBuilder_ = null;
                  this.methods_ = var1.methods_;
                  this.bitField0_ &= -2;
                  this.methodsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? this.getMethodsFieldBuilder() : null;
               } else {
                  this.methodsBuilder_.addAllMessages(var1.methods_);
               }
            }

            if (this.optionsBuilder_ == null) {
               if (!var1.options_.isEmpty()) {
                  if (this.options_.isEmpty()) {
                     this.options_ = var1.options_;
                     this.bitField0_ &= -3;
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
                  this.bitField0_ &= -3;
                  this.optionsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? this.getOptionsFieldBuilder() : null;
               } else {
                  this.optionsBuilder_.addAllMessages(var1.options_);
               }
            }

            if (!var1.getVersion().isEmpty()) {
               this.version_ = var1.version_;
               this.onChanged();
            }

            if (var1.hasSourceContext()) {
               this.mergeSourceContext(var1.getSourceContext());
            }

            if (this.mixinsBuilder_ == null) {
               if (!var1.mixins_.isEmpty()) {
                  if (this.mixins_.isEmpty()) {
                     this.mixins_ = var1.mixins_;
                     this.bitField0_ &= -5;
                  } else {
                     this.ensureMixinsIsMutable();
                     this.mixins_.addAll(var1.mixins_);
                  }

                  this.onChanged();
               }
            } else if (!var1.mixins_.isEmpty()) {
               if (this.mixinsBuilder_.isEmpty()) {
                  this.mixinsBuilder_.dispose();
                  this.mixinsBuilder_ = null;
                  this.mixins_ = var1.mixins_;
                  this.bitField0_ &= -5;
                  this.mixinsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? this.getMixinsFieldBuilder() : null;
               } else {
                  this.mixinsBuilder_.addAllMessages(var1.mixins_);
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

      public Api.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         Api var3 = null;

         try {
            var3 = Api.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (Api)var8.getUnfinishedMessage();
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

      public Api.Builder setName(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.name_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Api.Builder clearName() {
         this.name_ = Api.getDefaultInstance().getName();
         this.onChanged();
         return this;
      }

      public Api.Builder setNameBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.name_ = var1;
            this.onChanged();
            return this;
         }
      }

      private void ensureMethodsIsMutable() {
         if ((this.bitField0_ & 1) == 0) {
            this.methods_ = new ArrayList<>(this.methods_);
            this.bitField0_ |= 1;
         }
      }

      @Override
      public List<Method> getMethodsList() {
         return this.methodsBuilder_ == null ? Collections.unmodifiableList(this.methods_) : this.methodsBuilder_.getMessageList();
      }

      @Override
      public int getMethodsCount() {
         return this.methodsBuilder_ == null ? this.methods_.size() : this.methodsBuilder_.getCount();
      }

      @Override
      public Method getMethods(int var1) {
         return this.methodsBuilder_ == null ? this.methods_.get(var1) : this.methodsBuilder_.getMessage(var1);
      }

      public Api.Builder setMethods(int var1, Method var2) {
         if (this.methodsBuilder_ == null) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            this.ensureMethodsIsMutable();
            this.methods_.set(var1, var2);
            this.onChanged();
         } else {
            this.methodsBuilder_.setMessage(var1, var2);
         }

         return this;
      }

      public Api.Builder setMethods(int var1, Method.Builder var2) {
         if (this.methodsBuilder_ == null) {
            this.ensureMethodsIsMutable();
            this.methods_.set(var1, var2.build());
            this.onChanged();
         } else {
            this.methodsBuilder_.setMessage(var1, var2.build());
         }

         return this;
      }

      public Api.Builder addMethods(Method var1) {
         if (this.methodsBuilder_ == null) {
            if (var1 == null) {
               throw new NullPointerException();
            }

            this.ensureMethodsIsMutable();
            this.methods_.add(var1);
            this.onChanged();
         } else {
            this.methodsBuilder_.addMessage(var1);
         }

         return this;
      }

      public Api.Builder addMethods(int var1, Method var2) {
         if (this.methodsBuilder_ == null) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            this.ensureMethodsIsMutable();
            this.methods_.add(var1, var2);
            this.onChanged();
         } else {
            this.methodsBuilder_.addMessage(var1, var2);
         }

         return this;
      }

      public Api.Builder addMethods(Method.Builder var1) {
         if (this.methodsBuilder_ == null) {
            this.ensureMethodsIsMutable();
            this.methods_.add(var1.build());
            this.onChanged();
         } else {
            this.methodsBuilder_.addMessage(var1.build());
         }

         return this;
      }

      public Api.Builder addMethods(int var1, Method.Builder var2) {
         if (this.methodsBuilder_ == null) {
            this.ensureMethodsIsMutable();
            this.methods_.add(var1, var2.build());
            this.onChanged();
         } else {
            this.methodsBuilder_.addMessage(var1, var2.build());
         }

         return this;
      }

      public Api.Builder addAllMethods(Iterable<? extends Method> var1) {
         if (this.methodsBuilder_ == null) {
            this.ensureMethodsIsMutable();
            AbstractMessageLite.Builder.addAll(var1, this.methods_);
            this.onChanged();
         } else {
            this.methodsBuilder_.addAllMessages(var1);
         }

         return this;
      }

      public Api.Builder clearMethods() {
         if (this.methodsBuilder_ == null) {
            this.methods_ = Collections.emptyList();
            this.bitField0_ &= -2;
            this.onChanged();
         } else {
            this.methodsBuilder_.clear();
         }

         return this;
      }

      public Api.Builder removeMethods(int var1) {
         if (this.methodsBuilder_ == null) {
            this.ensureMethodsIsMutable();
            this.methods_.remove(var1);
            this.onChanged();
         } else {
            this.methodsBuilder_.remove(var1);
         }

         return this;
      }

      public Method.Builder getMethodsBuilder(int var1) {
         return this.getMethodsFieldBuilder().getBuilder(var1);
      }

      @Override
      public MethodOrBuilder getMethodsOrBuilder(int var1) {
         return this.methodsBuilder_ == null ? this.methods_.get(var1) : this.methodsBuilder_.getMessageOrBuilder(var1);
      }

      @Override
      public List<? extends MethodOrBuilder> getMethodsOrBuilderList() {
         return this.methodsBuilder_ != null ? this.methodsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.methods_);
      }

      public Method.Builder addMethodsBuilder() {
         return this.getMethodsFieldBuilder().addBuilder(Method.getDefaultInstance());
      }

      public Method.Builder addMethodsBuilder(int var1) {
         return this.getMethodsFieldBuilder().addBuilder(var1, Method.getDefaultInstance());
      }

      public List<Method.Builder> getMethodsBuilderList() {
         return this.getMethodsFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilderV3<Method, Method.Builder, MethodOrBuilder> getMethodsFieldBuilder() {
         if (this.methodsBuilder_ == null) {
            this.methodsBuilder_ = new RepeatedFieldBuilderV3<>(this.methods_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
            this.methods_ = null;
         }

         return this.methodsBuilder_;
      }

      private void ensureOptionsIsMutable() {
         if ((this.bitField0_ & 2) == 0) {
            this.options_ = new ArrayList<>(this.options_);
            this.bitField0_ |= 2;
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

      public Api.Builder setOptions(int var1, Option var2) {
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

      public Api.Builder setOptions(int var1, Option.Builder var2) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.set(var1, var2.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.setMessage(var1, var2.build());
         }

         return this;
      }

      public Api.Builder addOptions(Option var1) {
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

      public Api.Builder addOptions(int var1, Option var2) {
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

      public Api.Builder addOptions(Option.Builder var1) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(var1.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1.build());
         }

         return this;
      }

      public Api.Builder addOptions(int var1, Option.Builder var2) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(var1, var2.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1, var2.build());
         }

         return this;
      }

      public Api.Builder addAllOptions(Iterable<? extends Option> var1) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            AbstractMessageLite.Builder.addAll(var1, this.options_);
            this.onChanged();
         } else {
            this.optionsBuilder_.addAllMessages(var1);
         }

         return this;
      }

      public Api.Builder clearOptions() {
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -3;
            this.onChanged();
         } else {
            this.optionsBuilder_.clear();
         }

         return this;
      }

      public Api.Builder removeOptions(int var1) {
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
            this.optionsBuilder_ = new RepeatedFieldBuilderV3<>(this.options_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
            this.options_ = null;
         }

         return this.optionsBuilder_;
      }

      @Override
      public String getVersion() {
         Object var1 = this.version_;
         if (!(var1 instanceof String)) {
            ByteString var2 = (ByteString)var1;
            String var3 = var2.toStringUtf8();
            this.version_ = var3;
            return var3;
         } else {
            return (String)var1;
         }
      }

      @Override
      public ByteString getVersionBytes() {
         Object var1 = this.version_;
         if (var1 instanceof String) {
            ByteString var2 = ByteString.copyFromUtf8((String)var1);
            this.version_ = var2;
            return var2;
         } else {
            return (ByteString)var1;
         }
      }

      public Api.Builder setVersion(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.version_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Api.Builder clearVersion() {
         this.version_ = Api.getDefaultInstance().getVersion();
         this.onChanged();
         return this;
      }

      public Api.Builder setVersionBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.version_ = var1;
            this.onChanged();
            return this;
         }
      }

      @Override
      public boolean hasSourceContext() {
         return this.sourceContextBuilder_ != null || this.sourceContext_ != null;
      }

      @Override
      public SourceContext getSourceContext() {
         if (this.sourceContextBuilder_ == null) {
            return this.sourceContext_ == null ? SourceContext.getDefaultInstance() : this.sourceContext_;
         } else {
            return this.sourceContextBuilder_.getMessage();
         }
      }

      public Api.Builder setSourceContext(SourceContext var1) {
         if (this.sourceContextBuilder_ == null) {
            if (var1 == null) {
               throw new NullPointerException();
            }

            this.sourceContext_ = var1;
            this.onChanged();
         } else {
            this.sourceContextBuilder_.setMessage(var1);
         }

         return this;
      }

      public Api.Builder setSourceContext(SourceContext.Builder var1) {
         if (this.sourceContextBuilder_ == null) {
            this.sourceContext_ = var1.build();
            this.onChanged();
         } else {
            this.sourceContextBuilder_.setMessage(var1.build());
         }

         return this;
      }

      public Api.Builder mergeSourceContext(SourceContext var1) {
         if (this.sourceContextBuilder_ == null) {
            if (this.sourceContext_ != null) {
               this.sourceContext_ = SourceContext.newBuilder(this.sourceContext_).mergeFrom(var1).buildPartial();
            } else {
               this.sourceContext_ = var1;
            }

            this.onChanged();
         } else {
            this.sourceContextBuilder_.mergeFrom(var1);
         }

         return this;
      }

      public Api.Builder clearSourceContext() {
         if (this.sourceContextBuilder_ == null) {
            this.sourceContext_ = null;
            this.onChanged();
         } else {
            this.sourceContext_ = null;
            this.sourceContextBuilder_ = null;
         }

         return this;
      }

      public SourceContext.Builder getSourceContextBuilder() {
         this.onChanged();
         return this.getSourceContextFieldBuilder().getBuilder();
      }

      @Override
      public SourceContextOrBuilder getSourceContextOrBuilder() {
         if (this.sourceContextBuilder_ != null) {
            return this.sourceContextBuilder_.getMessageOrBuilder();
         } else {
            return this.sourceContext_ == null ? SourceContext.getDefaultInstance() : this.sourceContext_;
         }
      }

      private SingleFieldBuilderV3<SourceContext, SourceContext.Builder, SourceContextOrBuilder> getSourceContextFieldBuilder() {
         if (this.sourceContextBuilder_ == null) {
            this.sourceContextBuilder_ = new SingleFieldBuilderV3<>(this.getSourceContext(), this.getParentForChildren(), this.isClean());
            this.sourceContext_ = null;
         }

         return this.sourceContextBuilder_;
      }

      private void ensureMixinsIsMutable() {
         if ((this.bitField0_ & 4) == 0) {
            this.mixins_ = new ArrayList<>(this.mixins_);
            this.bitField0_ |= 4;
         }
      }

      @Override
      public List<Mixin> getMixinsList() {
         return this.mixinsBuilder_ == null ? Collections.unmodifiableList(this.mixins_) : this.mixinsBuilder_.getMessageList();
      }

      @Override
      public int getMixinsCount() {
         return this.mixinsBuilder_ == null ? this.mixins_.size() : this.mixinsBuilder_.getCount();
      }

      @Override
      public Mixin getMixins(int var1) {
         return this.mixinsBuilder_ == null ? this.mixins_.get(var1) : this.mixinsBuilder_.getMessage(var1);
      }

      public Api.Builder setMixins(int var1, Mixin var2) {
         if (this.mixinsBuilder_ == null) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            this.ensureMixinsIsMutable();
            this.mixins_.set(var1, var2);
            this.onChanged();
         } else {
            this.mixinsBuilder_.setMessage(var1, var2);
         }

         return this;
      }

      public Api.Builder setMixins(int var1, Mixin.Builder var2) {
         if (this.mixinsBuilder_ == null) {
            this.ensureMixinsIsMutable();
            this.mixins_.set(var1, var2.build());
            this.onChanged();
         } else {
            this.mixinsBuilder_.setMessage(var1, var2.build());
         }

         return this;
      }

      public Api.Builder addMixins(Mixin var1) {
         if (this.mixinsBuilder_ == null) {
            if (var1 == null) {
               throw new NullPointerException();
            }

            this.ensureMixinsIsMutable();
            this.mixins_.add(var1);
            this.onChanged();
         } else {
            this.mixinsBuilder_.addMessage(var1);
         }

         return this;
      }

      public Api.Builder addMixins(int var1, Mixin var2) {
         if (this.mixinsBuilder_ == null) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            this.ensureMixinsIsMutable();
            this.mixins_.add(var1, var2);
            this.onChanged();
         } else {
            this.mixinsBuilder_.addMessage(var1, var2);
         }

         return this;
      }

      public Api.Builder addMixins(Mixin.Builder var1) {
         if (this.mixinsBuilder_ == null) {
            this.ensureMixinsIsMutable();
            this.mixins_.add(var1.build());
            this.onChanged();
         } else {
            this.mixinsBuilder_.addMessage(var1.build());
         }

         return this;
      }

      public Api.Builder addMixins(int var1, Mixin.Builder var2) {
         if (this.mixinsBuilder_ == null) {
            this.ensureMixinsIsMutable();
            this.mixins_.add(var1, var2.build());
            this.onChanged();
         } else {
            this.mixinsBuilder_.addMessage(var1, var2.build());
         }

         return this;
      }

      public Api.Builder addAllMixins(Iterable<? extends Mixin> var1) {
         if (this.mixinsBuilder_ == null) {
            this.ensureMixinsIsMutable();
            AbstractMessageLite.Builder.addAll(var1, this.mixins_);
            this.onChanged();
         } else {
            this.mixinsBuilder_.addAllMessages(var1);
         }

         return this;
      }

      public Api.Builder clearMixins() {
         if (this.mixinsBuilder_ == null) {
            this.mixins_ = Collections.emptyList();
            this.bitField0_ &= -5;
            this.onChanged();
         } else {
            this.mixinsBuilder_.clear();
         }

         return this;
      }

      public Api.Builder removeMixins(int var1) {
         if (this.mixinsBuilder_ == null) {
            this.ensureMixinsIsMutable();
            this.mixins_.remove(var1);
            this.onChanged();
         } else {
            this.mixinsBuilder_.remove(var1);
         }

         return this;
      }

      public Mixin.Builder getMixinsBuilder(int var1) {
         return this.getMixinsFieldBuilder().getBuilder(var1);
      }

      @Override
      public MixinOrBuilder getMixinsOrBuilder(int var1) {
         return this.mixinsBuilder_ == null ? this.mixins_.get(var1) : this.mixinsBuilder_.getMessageOrBuilder(var1);
      }

      @Override
      public List<? extends MixinOrBuilder> getMixinsOrBuilderList() {
         return this.mixinsBuilder_ != null ? this.mixinsBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.mixins_);
      }

      public Mixin.Builder addMixinsBuilder() {
         return this.getMixinsFieldBuilder().addBuilder(Mixin.getDefaultInstance());
      }

      public Mixin.Builder addMixinsBuilder(int var1) {
         return this.getMixinsFieldBuilder().addBuilder(var1, Mixin.getDefaultInstance());
      }

      public List<Mixin.Builder> getMixinsBuilderList() {
         return this.getMixinsFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilderV3<Mixin, Mixin.Builder, MixinOrBuilder> getMixinsFieldBuilder() {
         if (this.mixinsBuilder_ == null) {
            this.mixinsBuilder_ = new RepeatedFieldBuilderV3<>(this.mixins_, (this.bitField0_ & 4) != 0, this.getParentForChildren(), this.isClean());
            this.mixins_ = null;
         }

         return this.mixinsBuilder_;
      }

      @Override
      public int getSyntaxValue() {
         return this.syntax_;
      }

      public Api.Builder setSyntaxValue(int var1) {
         this.syntax_ = var1;
         this.onChanged();
         return this;
      }

      @Override
      public Syntax getSyntax() {
         Syntax var1 = Syntax.valueOf(this.syntax_);
         return var1 == null ? Syntax.UNRECOGNIZED : var1;
      }

      public Api.Builder setSyntax(Syntax var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.syntax_ = var1.getNumber();
            this.onChanged();
            return this;
         }
      }

      public Api.Builder clearSyntax() {
         this.syntax_ = 0;
         this.onChanged();
         return this;
      }

      public final Api.Builder setUnknownFields(UnknownFieldSet var1) {
         return (Api.Builder)super.setUnknownFields(var1);
      }

      public final Api.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (Api.Builder)super.mergeUnknownFields(var1);
      }
   }
}
