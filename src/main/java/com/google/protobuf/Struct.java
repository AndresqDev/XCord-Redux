package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;

public final class Struct extends GeneratedMessageV3 implements StructOrBuilder {
   private static final long serialVersionUID = 0L;
   public static final int FIELDS_FIELD_NUMBER = 1;
   private MapField<String, Value> fields_;
   private byte memoizedIsInitialized = -1;
   private static final Struct DEFAULT_INSTANCE = new Struct();
   private static final Parser<Struct> PARSER = new AbstractParser<Struct>() {
      public Struct parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return new Struct(var1, var2);
      }
   };

   private Struct(GeneratedMessageV3.Builder<?> var1) {
      super(var1);
   }

   private Struct() {
   }

   @Override
   protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter var1) {
      return new Struct();
   }

   @Override
   public final UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
   }

   private Struct(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
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
                        this.fields_ = MapField.newMapField(Struct.FieldsDefaultEntryHolder.defaultEntry);
                        var3 |= true;
                     }

                     MapEntry var7 = var1.readMessage(Struct.FieldsDefaultEntryHolder.defaultEntry.getParserForType(), var2);
                     this.fields_.getMutableMap().put((String)var7.getKey(), (Value)var7.getValue());
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
      return StructProto.internal_static_google_protobuf_Struct_descriptor;
   }

   @Override
   protected MapField internalGetMapField(int var1) {
      switch (var1) {
         case 1:
            return this.internalGetFields();
         default:
            throw new RuntimeException("Invalid map field number: " + var1);
      }
   }

   @Override
   protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return StructProto.internal_static_google_protobuf_Struct_fieldAccessorTable.ensureFieldAccessorsInitialized(Struct.class, Struct.Builder.class);
   }

   private MapField<String, Value> internalGetFields() {
      return this.fields_ == null ? MapField.emptyMapField(Struct.FieldsDefaultEntryHolder.defaultEntry) : this.fields_;
   }

   @Override
   public int getFieldsCount() {
      return this.internalGetFields().getMap().size();
   }

   @Override
   public boolean containsFields(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.internalGetFields().getMap().containsKey(var1);
      }
   }

   @Deprecated
   @Override
   public Map<String, Value> getFields() {
      return this.getFieldsMap();
   }

   @Override
   public Map<String, Value> getFieldsMap() {
      return this.internalGetFields().getMap();
   }

   @Override
   public Value getFieldsOrDefault(String var1, Value var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Map var3 = this.internalGetFields().getMap();
         return var3.containsKey(var1) ? (Value)var3.get(var1) : var2;
      }
   }

   @Override
   public Value getFieldsOrThrow(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Map var2 = this.internalGetFields().getMap();
         if (!var2.containsKey(var1)) {
            throw new IllegalArgumentException();
         } else {
            return (Value)var2.get(var1);
         }
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
      GeneratedMessageV3.serializeStringMapTo(var1, this.internalGetFields(), Struct.FieldsDefaultEntryHolder.defaultEntry, 1);
      this.unknownFields.writeTo(var1);
   }

   @Override
   public int getSerializedSize() {
      int var1 = this.memoizedSize;
      if (var1 != -1) {
         return var1;
      } else {
         var1 = 0;

         for (Entry var3 : this.internalGetFields().getMap().entrySet()) {
            MapEntry var4 = Struct.FieldsDefaultEntryHolder.defaultEntry
               .newBuilderForType()
               .setKey((String)var3.getKey())
               .setValue((Value)var3.getValue())
               .build();
            var1 += CodedOutputStream.computeMessageSize(1, var4);
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
      } else if (!(var1 instanceof Struct)) {
         return super.equals(var1);
      } else {
         Struct var2 = (Struct)var1;
         return !this.internalGetFields().equals(var2.internalGetFields()) ? false : this.unknownFields.equals(var2.unknownFields);
      }
   }

   @Override
   public int hashCode() {
      if (this.memoizedHashCode != 0) {
         return this.memoizedHashCode;
      } else {
         int var1 = 41;
         var1 = 19 * var1 + getDescriptor().hashCode();
         if (!this.internalGetFields().getMap().isEmpty()) {
            var1 = 37 * var1 + 1;
            var1 = 53 * var1 + this.internalGetFields().hashCode();
         }

         var1 = 29 * var1 + this.unknownFields.hashCode();
         this.memoizedHashCode = var1;
         return var1;
      }
   }

   public static Struct parseFrom(ByteBuffer var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Struct parseFrom(ByteBuffer var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Struct parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Struct parseFrom(ByteString var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Struct parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0);
   }

   public static Struct parseFrom(byte[] var0, ExtensionRegistryLite var1) throws InvalidProtocolBufferException {
      return PARSER.parseFrom(var0, var1);
   }

   public static Struct parseFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Struct parseFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public static Struct parseDelimitedFrom(InputStream var0) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0);
   }

   public static Struct parseDelimitedFrom(InputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, var0, var1);
   }

   public static Struct parseFrom(CodedInputStream var0) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0);
   }

   public static Struct parseFrom(CodedInputStream var0, ExtensionRegistryLite var1) throws IOException {
      return GeneratedMessageV3.parseWithIOException(PARSER, var0, var1);
   }

   public Struct.Builder newBuilderForType() {
      return newBuilder();
   }

   public static Struct.Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
   }

   public static Struct.Builder newBuilder(Struct var0) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(var0);
   }

   public Struct.Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Struct.Builder() : new Struct.Builder().mergeFrom(this);
   }

   protected Struct.Builder newBuilderForType(GeneratedMessageV3.BuilderParent var1) {
      return new Struct.Builder(var1);
   }

   public static Struct getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   public static Parser<Struct> parser() {
      return PARSER;
   }

   @Override
   public Parser<Struct> getParserForType() {
      return PARSER;
   }

   public Struct getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
   }

   public static final class Builder extends GeneratedMessageV3.Builder<Struct.Builder> implements StructOrBuilder {
      private int bitField0_;
      private MapField<String, Value> fields_;

      public static final Descriptors.Descriptor getDescriptor() {
         return StructProto.internal_static_google_protobuf_Struct_descriptor;
      }

      @Override
      protected MapField internalGetMapField(int var1) {
         switch (var1) {
            case 1:
               return this.internalGetFields();
            default:
               throw new RuntimeException("Invalid map field number: " + var1);
         }
      }

      @Override
      protected MapField internalGetMutableMapField(int var1) {
         switch (var1) {
            case 1:
               return this.internalGetMutableFields();
            default:
               throw new RuntimeException("Invalid map field number: " + var1);
         }
      }

      @Override
      protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
         return StructProto.internal_static_google_protobuf_Struct_fieldAccessorTable.ensureFieldAccessorsInitialized(Struct.class, Struct.Builder.class);
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

      public Struct.Builder clear() {
         super.clear();
         this.internalGetMutableFields().clear();
         return this;
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return StructProto.internal_static_google_protobuf_Struct_descriptor;
      }

      public Struct getDefaultInstanceForType() {
         return Struct.getDefaultInstance();
      }

      public Struct build() {
         Struct var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public Struct buildPartial() {
         Struct var1 = new Struct(this);
         int var2 = this.bitField0_;
         var1.fields_ = this.internalGetFields();
         var1.fields_.makeImmutable();
         this.onBuilt();
         return var1;
      }

      public Struct.Builder clone() {
         return (Struct.Builder)super.clone();
      }

      public Struct.Builder setField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Struct.Builder)super.setField(var1, var2);
      }

      public Struct.Builder clearField(Descriptors.FieldDescriptor var1) {
         return (Struct.Builder)super.clearField(var1);
      }

      public Struct.Builder clearOneof(Descriptors.OneofDescriptor var1) {
         return (Struct.Builder)super.clearOneof(var1);
      }

      public Struct.Builder setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         return (Struct.Builder)super.setRepeatedField(var1, var2, var3);
      }

      public Struct.Builder addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         return (Struct.Builder)super.addRepeatedField(var1, var2);
      }

      public Struct.Builder mergeFrom(Message var1) {
         if (var1 instanceof Struct) {
            return this.mergeFrom((Struct)var1);
         } else {
            super.mergeFrom(var1);
            return this;
         }
      }

      public Struct.Builder mergeFrom(Struct var1) {
         if (var1 == Struct.getDefaultInstance()) {
            return this;
         } else {
            this.internalGetMutableFields().mergeFrom(var1.internalGetFields());
            this.mergeUnknownFields(var1.unknownFields);
            this.onChanged();
            return this;
         }
      }

      @Override
      public final boolean isInitialized() {
         return true;
      }

      public Struct.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         Struct var3 = null;

         try {
            var3 = Struct.PARSER.parsePartialFrom(var1, var2);
         } catch (InvalidProtocolBufferException var8) {
            var3 = (Struct)var8.getUnfinishedMessage();
            throw var8.unwrapIOException();
         } finally {
            if (var3 != null) {
               this.mergeFrom(var3);
            }
         }

         return this;
      }

      private MapField<String, Value> internalGetFields() {
         return this.fields_ == null ? MapField.emptyMapField(Struct.FieldsDefaultEntryHolder.defaultEntry) : this.fields_;
      }

      private MapField<String, Value> internalGetMutableFields() {
         this.onChanged();
         if (this.fields_ == null) {
            this.fields_ = MapField.newMapField(Struct.FieldsDefaultEntryHolder.defaultEntry);
         }

         if (!this.fields_.isMutable()) {
            this.fields_ = this.fields_.copy();
         }

         return this.fields_;
      }

      @Override
      public int getFieldsCount() {
         return this.internalGetFields().getMap().size();
      }

      @Override
      public boolean containsFields(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            return this.internalGetFields().getMap().containsKey(var1);
         }
      }

      @Deprecated
      @Override
      public Map<String, Value> getFields() {
         return this.getFieldsMap();
      }

      @Override
      public Map<String, Value> getFieldsMap() {
         return this.internalGetFields().getMap();
      }

      @Override
      public Value getFieldsOrDefault(String var1, Value var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Map var3 = this.internalGetFields().getMap();
            return var3.containsKey(var1) ? (Value)var3.get(var1) : var2;
         }
      }

      @Override
      public Value getFieldsOrThrow(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Map var2 = this.internalGetFields().getMap();
            if (!var2.containsKey(var1)) {
               throw new IllegalArgumentException();
            } else {
               return (Value)var2.get(var1);
            }
         }
      }

      public Struct.Builder clearFields() {
         this.internalGetMutableFields().getMutableMap().clear();
         return this;
      }

      public Struct.Builder removeFields(String var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.internalGetMutableFields().getMutableMap().remove(var1);
            return this;
         }
      }

      @Deprecated
      public Map<String, Value> getMutableFields() {
         return this.internalGetMutableFields().getMutableMap();
      }

      public Struct.Builder putFields(String var1, Value var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (var2 == null) {
            throw new NullPointerException();
         } else {
            this.internalGetMutableFields().getMutableMap().put(var1, var2);
            return this;
         }
      }

      public Struct.Builder putAllFields(Map<String, Value> var1) {
         this.internalGetMutableFields().getMutableMap().putAll(var1);
         return this;
      }

      public final Struct.Builder setUnknownFields(UnknownFieldSet var1) {
         return (Struct.Builder)super.setUnknownFields(var1);
      }

      public final Struct.Builder mergeUnknownFields(UnknownFieldSet var1) {
         return (Struct.Builder)super.mergeUnknownFields(var1);
      }
   }

   private static final class FieldsDefaultEntryHolder {
      static final MapEntry<String, Value> defaultEntry = MapEntry.newDefaultInstance(
         StructProto.internal_static_google_protobuf_Struct_FieldsEntry_descriptor,
         WireFormat.FieldType.STRING,
         "",
         WireFormat.FieldType.MESSAGE,
         Value.getDefaultInstance()
      );
   }
}
