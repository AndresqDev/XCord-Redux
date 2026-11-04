package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Enum extends GeneratedMessageV3 implements EnumOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int NAME_FIELD_NUMBER = 1;
   private volatile Object name_;
   public static final int ENUMVALUE_FIELD_NUMBER = 2;
   private List<EnumValue> enumvalue_;
   public static final int OPTIONS_FIELD_NUMBER = 3;
   private List<Option> options_;
   public static final int SOURCE_CONTEXT_FIELD_NUMBER = 4;
   private SourceContext sourceContext_;
   public static final int SYNTAX_FIELD_NUMBER = 5;
   private int syntax_;
   private byte memoizedIsInitialized = -1;
   private static final Enum DEFAULT_INSTANCE = new Enum();
   private static final Parser<Enum> PARSER = new AbstractParser<Enum>() {
      public Enum parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new Enum(var1, var2);
      }
   };

   private Enum(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private Enum() {
      this.name_ = "";
      this.enumvalue_ = Collections.emptyList();
      this.options_ = Collections.emptyList();
      this.syntax_ = 0;
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new Enum();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Enum(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                     String var16 = var1.readStringRequireUtf8();
                     this.name_ = var16;
                     break;
                  case 18:
                     if ((var3 & 1) == 0) {
                        this.enumvalue_ = new ArrayList<>();
                        var3 |= 1;
                     }

                     this.enumvalue_.add(var1.readMessage(EnumValue.parser(), var2));
                     break;
                  case 26:
                     if ((var3 & 2) == 0) {
                        this.options_ = new ArrayList<>();
                        var3 |= 2;
                     }

                     this.options_.add(var1.readMessage(Option.parser(), var2));
                     break;
                  case 34:
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
                  case 40:
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
               this.enumvalue_ = Collections.unmodifiableList(this.enumvalue_);
            }

            if ((var3 & 2) != 0) {
               this.options_ = Collections.unmodifiableList(this.options_);
            }

            this.unknownFields = var4.build();
            this.makeExtensionsImmutable();
         }
      }
   }

   public static final Descriptors.Descriptor getDescriptor() {
      return TypeProto.internal_static_google_protobuf_Enum_descriptor;
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return TypeProto.internal_static_google_protobuf_Enum_fieldAccessorTable.ensureFieldAccessorsInitialized(Enum.class, Enum.Builder.class);
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
   public List<EnumValue> getEnumvalueList() {
      return this.enumvalue_;
   }

   @Override
   public List<? extends EnumValueOrBuilder> getEnumvalueOrBuilderList() {
      return this.enumvalue_;
   }

   @Override
   public int getEnumvalueCount() {
      return this.enumvalue_.size();
   }

   @Override
   public EnumValue getEnumvalue(int var1) {
      return this.enumvalue_.get(var1);
   }

   @Override
   public EnumValueOrBuilder getEnumvalueOrBuilder(int var1) {
      return this.enumvalue_.get(var1);
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

      for (int var2 = 0; var2 < this.enumvalue_.size(); var2++) {
         var1.writeMessage(2, this.enumvalue_.get(var2));
      }

      for (int var3 = 0; var3 < this.options_.size(); var3++) {
         var1.writeMessage(3, this.options_.get(var3));
      }

      if (this.sourceContext_ != null) {
         var1.writeMessage(4, this.getSourceContext());
      }

      if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
         var1.writeEnum(5, this.syntax_);
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

         for (int var2 = 0; var2 < this.enumvalue_.size(); var2++) {
            var1 += CodedOutputStream.computeMessageSize(2, this.enumvalue_.get(var2));
         }

         for (int var5 = 0; var5 < this.options_.size(); var5++) {
            var1 += CodedOutputStream.computeMessageSize(3, this.options_.get(var5));
         }

         if (this.sourceContext_ != null) {
            var1 += CodedOutputStream.computeMessageSize(4, this.getSourceContext());
         }

         if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber()) {
            var1 += CodedOutputStream.computeEnumSize(5, this.syntax_);
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
      } else if (!(var1 instanceof Enum)) {
         return super.equals(var1);
      } else {
         Enum var2 = (Enum)var1;
         if (!this.getName().equals(var2.getName())) {
            return false;
         } else if (!this.getEnumvalueList().equals(var2.getEnumvalueList())) {
            return false;
         } else if (!this.getOptionsList().equals(var2.getOptionsList())) {
            return false;
         } else if (this.hasSourceContext() != var2.hasSourceContext()) {
            return false;
         } else if (this.hasSourceContext() && !this.getSourceContext().equals(var2.getSourceContext())) {
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
         if (this.getEnumvalueCount() > 0) {
            var1 = 37 * var1 + 2;
            var1 = 53 * var1 + this.getEnumvalueList().hashCode();
         }

         if (this.getOptionsCount() > 0) {
            var1 = 37 * var1 + 3;
            var1 = 53 * var1 + this.getOptionsList().hashCode();
         }

         if (this.hasSourceContext()) {
            var1 = 37 * var1 + 4;
            var1 = 53 * var1 + this.getSourceContext().hashCode();
         }

         var1 = 37 * var1 + 5;
         var1 = 53 * var1 + this.syntax_;
         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static Enum parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Enum parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Enum parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Enum parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Enum parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Enum parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Enum parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Enum parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static Enum parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static Enum parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static Enum parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Enum parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public Enum.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Enum.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Enum.Builder newBuilder(Enum var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public Enum.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Enum.Builder() : new Enum.Builder().mergeFrom(this);
   }

   protected Enum.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new Enum.Builder(var1);
   }

   public static Enum getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Enum> parser() {
      return PARSER;
   }

   @Override
   public Parser<Enum> getParserForType() {
      return PARSER;
   }

   public Enum getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Enum.Builder> implements EnumOrBuilder {
      private int bitField0_;
      private Object name_ = "";
      private List<EnumValue> enumvalue_ = Collections.emptyList();
      private RepeatedFieldBuilderV3<EnumValue, EnumValue.Builder, EnumValueOrBuilder> enumvalueBuilder_;
      private List<Option> options_ = Collections.emptyList();
      private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
      private SourceContext sourceContext_;
      private SingleFieldBuilderV3<SourceContext, SourceContext.Builder, SourceContextOrBuilder> sourceContextBuilder_;
      private int syntax_ = 0;

      public static final Descriptors.Descriptor getDescriptor() {
         return TypeProto.internal_static_google_protobuf_Enum_descriptor;
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return TypeProto.internal_static_google_protobuf_Enum_fieldAccessorTable.ensureFieldAccessorsInitialized(Enum.class, Enum.Builder.class);
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
            this.getEnumvalueFieldBuilder();
            this.getOptionsFieldBuilder();
         }
      }

      public Enum.Builder clear() {
         super.clear();
         this.name_ = "";
         if (this.enumvalueBuilder_ == null) {
            this.enumvalue_ = Collections.emptyList();
            this.bitField0_ &= -2;
         } else {
            this.enumvalueBuilder_.clear();
         }

         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -3;
         } else {
            this.optionsBuilder_.clear();
         }

         if (this.sourceContextBuilder_ == null) {
            this.sourceContext_ = null;
         } else {
            this.sourceContext_ = null;
            this.sourceContextBuilder_ = null;
         }

         this.syntax_ = 0;
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return TypeProto.internal_static_google_protobuf_Enum_descriptor;
      }

      public Enum getDefaultInstanceForType() {
         return Enum.getDefaultInstance();
      }

      public Enum build() {
         Enum var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public Enum buildPartial() {
         Enum var1 = new Enum(this);
         int var2 = this.bitField0_;
         var1.name_ = this.name_;
         if (this.enumvalueBuilder_ == null) {
            if ((this.bitField0_ & 1) != 0) {
               this.enumvalue_ = Collections.unmodifiableList(this.enumvalue_);
               this.bitField0_ &= -2;
            }

            var1.enumvalue_ = this.enumvalue_;
         } else {
            var1.enumvalue_ = this.enumvalueBuilder_.build();
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

         if (this.sourceContextBuilder_ == null) {
            var1.sourceContext_ = this.sourceContext_;
         } else {
            var1.sourceContext_ = this.sourceContextBuilder_.build();
         }

         var1.syntax_ = this.syntax_;
         this.onBuilt();
         return var1;
      }

      public Enum.Builder clone() {
         return (Enum.Builder)super.clone();
      }

      public Enum.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Enum.Builder)super.setField(var1, var2);
      }

      public Enum.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (Enum.Builder)super.clearField(var1);
      }

      public Enum.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (Enum.Builder)super.clearOneof(var1);
      }

      public Enum.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (Enum.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public Enum.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Enum.Builder)super.addRepeatedField(var1, var2);
      }

      public Enum.Builder mergeFrom(Message var1) {
         if (var1 instanceof Enum) {
            return this.mergeFrom((Enum)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public Enum.Builder mergeFrom(Enum var1) {
         if (var1 == Enum.getDefaultInstance()) {
            return this;
         } else {
            if (!var1.getName().isEmpty()) {
               this.name_ = var1.name_;
               this.onChanged();
            }

            if (this.enumvalueBuilder_ == null) {
               if (!var1.enumvalue_.isEmpty()) {
                  if (this.enumvalue_.isEmpty()) {
                     this.enumvalue_ = var1.enumvalue_;
                     this.bitField0_ &= -2;
                  } else {
                     this.ensureEnumvalueIsMutable();
                     this.enumvalue_.addAll(var1.enumvalue_);
                  }

                  this.onChanged();
               }
            } else if (!var1.enumvalue_.isEmpty()) {
               if (this.enumvalueBuilder_.isEmpty()) {
                  this.enumvalueBuilder_.dispose();
                  this.enumvalueBuilder_ = null;
                  this.enumvalue_ = var1.enumvalue_;
                  this.bitField0_ &= -2;
                  this.enumvalueBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? this.getEnumvalueFieldBuilder() : null;
               } else {
                  this.enumvalueBuilder_.addAllMessages(var1.enumvalue_);
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

            if (var1.hasSourceContext()) {
               this.mergeSourceContext(var1.getSourceContext());
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

      public Enum.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         Enum var3 = null;

         try {
            var3 = Enum.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (Enum)var8.getUnfinishedMessage();
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

      public Enum.Builder setName(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.name_ = var1;
            this.onChanged();
            return this;
         }
      }

      public Enum.Builder clearName() {
         this.name_ = Enum.getDefaultInstance().getName();
         this.onChanged();
         return this;
      }

      public Enum.Builder setNameBytes(ByteString var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            AbstractMessageLite.checkByteStringIsUtf8(var1);
            this.name_ = var1;
            this.onChanged();
            return this;
         }
      }

      private void ensureEnumvalueIsMutable() {
         if ((this.bitField0_ & 1) == 0) {
            this.enumvalue_ = new ArrayList<>(this.enumvalue_);
            this.bitField0_ |= 1;
         }
      }

      @Override
      public List<EnumValue> getEnumvalueList() {
         return this.enumvalueBuilder_ == null ? Collections.unmodifiableList(this.enumvalue_) : this.enumvalueBuilder_.getMessageList();
      }

      @Override
      public int getEnumvalueCount() {
         return this.enumvalueBuilder_ == null ? this.enumvalue_.size() : this.enumvalueBuilder_.getCount();
      }

      @Override
      public EnumValue getEnumvalue(int var1) {
         return this.enumvalueBuilder_ == null ? this.enumvalue_.get(var1) : this.enumvalueBuilder_.getMessage(var1);
      }

      public Enum.Builder setEnumvalue(int var1, EnumValue var2) {
         if (this.enumvalueBuilder_ == null) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            this.ensureEnumvalueIsMutable();
            this.enumvalue_.set(var1, var2);
            this.onChanged();
         } else {
            this.enumvalueBuilder_.setMessage(var1, var2);
         }

         return this;
      }

      public Enum.Builder setEnumvalue(int var1, EnumValue.Builder var2) {
         if (this.enumvalueBuilder_ == null) {
            this.ensureEnumvalueIsMutable();
            this.enumvalue_.set(var1, var2.build());
            this.onChanged();
         } else {
            this.enumvalueBuilder_.setMessage(var1, var2.build());
         }

         return this;
      }

      public Enum.Builder addEnumvalue(EnumValue var1) {
         if (this.enumvalueBuilder_ == null) {
            if (var1 == null) {
               throw new NullPointerException();
            }

            this.ensureEnumvalueIsMutable();
            this.enumvalue_.add(var1);
            this.onChanged();
         } else {
            this.enumvalueBuilder_.addMessage(var1);
         }

         return this;
      }

      public Enum.Builder addEnumvalue(int var1, EnumValue var2) {
         if (this.enumvalueBuilder_ == null) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            this.ensureEnumvalueIsMutable();
            this.enumvalue_.add(var1, var2);
            this.onChanged();
         } else {
            this.enumvalueBuilder_.addMessage(var1, var2);
         }

         return this;
      }

      public Enum.Builder addEnumvalue(EnumValue.Builder var1) {
         if (this.enumvalueBuilder_ == null) {
            this.ensureEnumvalueIsMutable();
            this.enumvalue_.add(var1.build());
            this.onChanged();
         } else {
            this.enumvalueBuilder_.addMessage(var1.build());
         }

         return this;
      }

      public Enum.Builder addEnumvalue(int var1, EnumValue.Builder var2) {
         if (this.enumvalueBuilder_ == null) {
            this.ensureEnumvalueIsMutable();
            this.enumvalue_.add(var1, var2.build());
            this.onChanged();
         } else {
            this.enumvalueBuilder_.addMessage(var1, var2.build());
         }

         return this;
      }

      public Enum.Builder addAllEnumvalue(Iterable<? extends EnumValue> var1) {
         if (this.enumvalueBuilder_ == null) {
            this.ensureEnumvalueIsMutable();
            AbstractMessageLite.Builder.addAll(var1, this.enumvalue_);
            this.onChanged();
         } else {
            this.enumvalueBuilder_.addAllMessages(var1);
         }

         return this;
      }

      public Enum.Builder clearEnumvalue() {
         if (this.enumvalueBuilder_ == null) {
            this.enumvalue_ = Collections.emptyList();
            this.bitField0_ &= -2;
            this.onChanged();
         } else {
            this.enumvalueBuilder_.clear();
         }

         return this;
      }

      public Enum.Builder removeEnumvalue(int var1) {
         if (this.enumvalueBuilder_ == null) {
            this.ensureEnumvalueIsMutable();
            this.enumvalue_.remove(var1);
            this.onChanged();
         } else {
            this.enumvalueBuilder_.remove(var1);
         }

         return this;
      }

      public EnumValue.Builder getEnumvalueBuilder(int var1) {
         return this.getEnumvalueFieldBuilder().getBuilder(var1);
      }

      @Override
      public EnumValueOrBuilder getEnumvalueOrBuilder(int var1) {
         return this.enumvalueBuilder_ == null ? this.enumvalue_.get(var1) : this.enumvalueBuilder_.getMessageOrBuilder(var1);
      }

      @Override
      public List<? extends EnumValueOrBuilder> getEnumvalueOrBuilderList() {
         return this.enumvalueBuilder_ != null ? this.enumvalueBuilder_.getMessageOrBuilderList() : Collections.unmodifiableList(this.enumvalue_);
      }

      public EnumValue.Builder addEnumvalueBuilder() {
         return this.getEnumvalueFieldBuilder().addBuilder(EnumValue.getDefaultInstance());
      }

      public EnumValue.Builder addEnumvalueBuilder(int var1) {
         return this.getEnumvalueFieldBuilder().addBuilder(var1, EnumValue.getDefaultInstance());
      }

      public List<EnumValue.Builder> getEnumvalueBuilderList() {
         return this.getEnumvalueFieldBuilder().getBuilderList();
      }

      private RepeatedFieldBuilderV3<EnumValue, EnumValue.Builder, EnumValueOrBuilder> getEnumvalueFieldBuilder() {
         if (this.enumvalueBuilder_ == null) {
            this.enumvalueBuilder_ = new RepeatedFieldBuilderV3<>(this.enumvalue_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
            this.enumvalue_ = null;
         }

         return this.enumvalueBuilder_;
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

      public Enum.Builder setOptions(int var1, Option var2) {
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

      public Enum.Builder setOptions(int var1, Option.Builder var2) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.set(var1, var2.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.setMessage(var1, var2.build());
         }

         return this;
      }

      public Enum.Builder addOptions(Option var1) {
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

      public Enum.Builder addOptions(int var1, Option var2) {
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

      public Enum.Builder addOptions(Option.Builder var1) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(var1.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1.build());
         }

         return this;
      }

      public Enum.Builder addOptions(int var1, Option.Builder var2) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            this.options_.add(var1, var2.build());
            this.onChanged();
         } else {
            this.optionsBuilder_.addMessage(var1, var2.build());
         }

         return this;
      }

      public Enum.Builder addAllOptions(Iterable<? extends Option> var1) {
         if (this.optionsBuilder_ == null) {
            this.ensureOptionsIsMutable();
            AbstractMessageLite.Builder.addAll(var1, this.options_);
            this.onChanged();
         } else {
            this.optionsBuilder_.addAllMessages(var1);
         }

         return this;
      }

      public Enum.Builder clearOptions() {
         if (this.optionsBuilder_ == null) {
            this.options_ = Collections.emptyList();
            this.bitField0_ &= -3;
            this.onChanged();
         } else {
            this.optionsBuilder_.clear();
         }

         return this;
      }

      public Enum.Builder removeOptions(int var1) {
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

      public Enum.Builder setSourceContext(SourceContext var1) {
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

      public Enum.Builder setSourceContext(SourceContext.Builder var1) {
         if (this.sourceContextBuilder_ == null) {
            this.sourceContext_ = var1.build();
            this.onChanged();
         } else {
            this.sourceContextBuilder_.setMessage(var1.build());
         }

         return this;
      }

      public Enum.Builder mergeSourceContext(SourceContext var1) {
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

      public Enum.Builder clearSourceContext() {
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

      @Override
      public int getSyntaxValue() {
         return this.syntax_;
      }

      public Enum.Builder setSyntaxValue(int var1) {
         this.syntax_ = var1;
         this.onChanged();
         return this;
      }

      @Override
      public Syntax getSyntax() {
         Syntax var1 = Syntax.valueOf(this.syntax_);
         return var1 == null ? Syntax.UNRECOGNIZED : var1;
      }

      public Enum.Builder setSyntax(Syntax var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.syntax_ = var1.getNumber();
            this.onChanged();
            return this;
         }
      }

      public Enum.Builder clearSyntax() {
         this.syntax_ = 0;
         this.onChanged();
         return this;
      }

      public final Enum.Builder setUnknownFields(UnknownFieldSet var1) {
         return (Enum.Builder)super.setUnknownFields(var1);
      }

      public final Enum.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (Enum.Builder)super.mergeUnknownFields(var1);
      }
   }
}
