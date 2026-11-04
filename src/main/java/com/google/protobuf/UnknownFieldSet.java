package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public final class UnknownFieldSet implements MessageLite {
   private static final UnknownFieldSet defaultInstance = new UnknownFieldSet(Collections.emptyMap(), Collections.emptyMap());
   private final Map<Integer, UnknownFieldSet.Field> fields;
   private final Map<Integer, UnknownFieldSet.Field> fieldsDescending;
   private static final UnknownFieldSet.Parser PARSER = new UnknownFieldSet.Parser();

   private UnknownFieldSet() {
      this.fields = null;
      this.fieldsDescending = null;
   }

   public static UnknownFieldSet.Builder newBuilder() {
      return UnknownFieldSet.Builder.create();
   }

   public static UnknownFieldSet.Builder newBuilder(UnknownFieldSet var0) {
      return newBuilder().mergeFrom(var0);
   }

   public static UnknownFieldSet getDefaultInstance() {
      return defaultInstance;
   }

   public UnknownFieldSet getDefaultInstanceForType() {
      return defaultInstance;
   }

   UnknownFieldSet(Map<Integer, UnknownFieldSet.Field> var1, Map<Integer, UnknownFieldSet.Field> var2) {
      this.fields = var1;
      this.fieldsDescending = var2;
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 ? true : var1 instanceof UnknownFieldSet && this.fields.equals(((UnknownFieldSet)var1).fields);
   }

   @Override
   public int hashCode() {
      return this.fields.hashCode();
   }

   public Map<Integer, UnknownFieldSet.Field> asMap() {
      return this.fields;
   }

   public boolean hasField(int var1) {
      return this.fields.containsKey(var1);
   }

   public UnknownFieldSet.Field getField(int var1) {
      UnknownFieldSet.Field var2 = this.fields.get(var1);
      return var2 == null ? UnknownFieldSet.Field.getDefaultInstance() : var2;
   }

   @Override
   public void writeTo(CodedOutputStream var1) throws IOException {
      for (Entry var3 : this.fields.entrySet()) {
         UnknownFieldSet.Field var4 = (UnknownFieldSet.Field)var3.getValue();
         var4.writeTo((Integer)var3.getKey(), var1);
      }
   }

   @Override
   public String toString() {
      return TextFormat.printer().printToString(this);
   }

   @Override
   public ByteString toByteString() {
      try {
         ByteString.CodedBuilder var1 = ByteString.newCodedBuilder(this.getSerializedSize());
         this.writeTo(var1.getCodedOutput());
         return var1.build();
      } catch (IOException var2) {
         throw new RuntimeException("Serializing to a ByteString threw an IOException (should never happen).", var2);
      }
   }

   @Override
   public byte[] toByteArray() {
      try {
         byte[] var1 = new byte[this.getSerializedSize()];
         CodedOutputStream var2 = CodedOutputStream.newInstance(var1);
         this.writeTo(var2);
         var2.checkNoSpaceLeft();
         return var1;
      } catch (IOException var3) {
         throw new RuntimeException("Serializing to a byte array threw an IOException (should never happen).", var3);
      }
   }

   @Override
   public void writeTo(OutputStream var1) throws IOException {
      CodedOutputStream var2 = CodedOutputStream.newInstance(var1);
      this.writeTo(var2);
      var2.flush();
   }

   @Override
   public void writeDelimitedTo(OutputStream var1) throws IOException {
      CodedOutputStream var2 = CodedOutputStream.newInstance(var1);
      var2.writeRawVarint32(this.getSerializedSize());
      this.writeTo(var2);
      var2.flush();
   }

   @Override
   public int getSerializedSize() {
      int var1 = 0;

      for (Entry var3 : this.fields.entrySet()) {
         var1 += ((UnknownFieldSet.Field)var3.getValue()).getSerializedSize((Integer)var3.getKey());
      }

      return var1;
   }

   public void writeAsMessageSetTo(CodedOutputStream var1) throws IOException {
      for (Entry var3 : this.fields.entrySet()) {
         ((UnknownFieldSet.Field)var3.getValue()).writeAsMessageSetExtensionTo((Integer)var3.getKey(), var1);
      }
   }

   void writeTo(Writer var1) throws IOException {
      if (var1.fieldOrder() == Writer.FieldOrder.DESCENDING) {
         for (Entry var3 : this.fieldsDescending.entrySet()) {
            ((UnknownFieldSet.Field)var3.getValue()).writeTo((Integer)var3.getKey(), var1);
         }
      } else {
         for (Entry var5 : this.fields.entrySet()) {
            ((UnknownFieldSet.Field)var5.getValue()).writeTo((Integer)var5.getKey(), var1);
         }
      }
   }

   void writeAsMessageSetTo(Writer var1) throws IOException {
      if (var1.fieldOrder() == Writer.FieldOrder.DESCENDING) {
         for (Entry var3 : this.fieldsDescending.entrySet()) {
            ((UnknownFieldSet.Field)var3.getValue()).writeAsMessageSetExtensionTo((Integer)var3.getKey(), var1);
         }
      } else {
         for (Entry var5 : this.fields.entrySet()) {
            ((UnknownFieldSet.Field)var5.getValue()).writeAsMessageSetExtensionTo((Integer)var5.getKey(), var1);
         }
      }
   }

   public int getSerializedSizeAsMessageSet() {
      int var1 = 0;

      for (Entry var3 : this.fields.entrySet()) {
         var1 += ((UnknownFieldSet.Field)var3.getValue()).getSerializedSizeAsMessageSetExtension((Integer)var3.getKey());
      }

      return var1;
   }

   @Override
   public boolean isInitialized() {
      return true;
   }

   public static UnknownFieldSet parseFrom(CodedInputStream var0) throws IOException {
      return newBuilder().mergeFrom(var0).build();
   }

   public static UnknownFieldSet parseFrom(ByteString var0) throws InvalidProtocolBufferException {
      return newBuilder().mergeFrom(var0).build();
   }

   public static UnknownFieldSet parseFrom(byte[] var0) throws InvalidProtocolBufferException {
      return newBuilder().mergeFrom(var0).build();
   }

   public static UnknownFieldSet parseFrom(InputStream var0) throws IOException {
      return newBuilder().mergeFrom(var0).build();
   }

   public UnknownFieldSet.Builder newBuilderForType() {
      return newBuilder();
   }

   public UnknownFieldSet.Builder toBuilder() {
      return newBuilder().mergeFrom(this);
   }

   public final UnknownFieldSet.Parser getParserForType() {
      return PARSER;
   }

   public static final class Builder implements MessageLite.Builder {
      private Map<Integer, UnknownFieldSet.Field> fields;
      private int lastFieldNumber;
      private UnknownFieldSet.Field.Builder lastField;

      private Builder() {
      }

      private static UnknownFieldSet.Builder create() {
         UnknownFieldSet.Builder var0 = new UnknownFieldSet.Builder();
         var0.reinitialize();
         return var0;
      }

      private UnknownFieldSet.Field.Builder getFieldBuilder(int var1) {
         if (this.lastField != null) {
            if (var1 == this.lastFieldNumber) {
               return this.lastField;
            }

            this.addField(this.lastFieldNumber, this.lastField.build());
         }

         if (var1 == 0) {
            return null;
         } else {
            UnknownFieldSet.Field var2 = this.fields.get(var1);
            this.lastFieldNumber = var1;
            this.lastField = UnknownFieldSet.Field.newBuilder();
            if (var2 != null) {
               this.lastField.mergeFrom(var2);
            }

            return this.lastField;
         }
      }

      public UnknownFieldSet build() {
         this.getFieldBuilder(0);
         UnknownFieldSet var1;
         if (this.fields.isEmpty()) {
            var1 = UnknownFieldSet.getDefaultInstance();
         } else {
            Object var2 = null;
            var2 = Collections.unmodifiableMap(((TreeMap)this.fields).descendingMap());
            var1 = new UnknownFieldSet(Collections.unmodifiableMap(this.fields), (Map<Integer, UnknownFieldSet.Field>)var2);
         }

         this.fields = null;
         return var1;
      }

      public UnknownFieldSet buildPartial() {
         return this.build();
      }

      public UnknownFieldSet.Builder clone() {
         this.getFieldBuilder(0);
         Object var1 = null;
         var1 = Collections.unmodifiableMap(((TreeMap)this.fields).descendingMap());
         return UnknownFieldSet.newBuilder().mergeFrom(new UnknownFieldSet(this.fields, (Map<Integer, UnknownFieldSet.Field>)var1));
      }

      public UnknownFieldSet getDefaultInstanceForType() {
         return UnknownFieldSet.getDefaultInstance();
      }

      private void reinitialize() {
         this.fields = Collections.emptyMap();
         this.lastFieldNumber = 0;
         this.lastField = null;
      }

      public UnknownFieldSet.Builder clear() {
         this.reinitialize();
         return this;
      }

      public UnknownFieldSet.Builder clearField(int var1) {
         if (var1 == 0) {
            throw new IllegalArgumentException("Zero is not a valid field number.");
         } else {
            if (this.lastField != null && this.lastFieldNumber == var1) {
               this.lastField = null;
               this.lastFieldNumber = 0;
            }

            if (this.fields.containsKey(var1)) {
               this.fields.remove(var1);
            }

            return this;
         }
      }

      public UnknownFieldSet.Builder mergeFrom(UnknownFieldSet var1) {
         if (var1 != UnknownFieldSet.getDefaultInstance()) {
            for (Entry var3 : var1.fields.entrySet()) {
               this.mergeField((Integer)var3.getKey(), (UnknownFieldSet.Field)var3.getValue());
            }
         }

         return this;
      }

      public UnknownFieldSet.Builder mergeField(int var1, UnknownFieldSet.Field var2) {
         if (var1 == 0) {
            throw new IllegalArgumentException("Zero is not a valid field number.");
         } else {
            if (this.hasField(var1)) {
               this.getFieldBuilder(var1).mergeFrom(var2);
            } else {
               this.addField(var1, var2);
            }

            return this;
         }
      }

      public UnknownFieldSet.Builder mergeVarintField(int var1, int var2) {
         if (var1 == 0) {
            throw new IllegalArgumentException("Zero is not a valid field number.");
         } else {
            this.getFieldBuilder(var1).addVarint((long)var2);
            return this;
         }
      }

      public UnknownFieldSet.Builder mergeLengthDelimitedField(int var1, ByteString var2) {
         if (var1 == 0) {
            throw new IllegalArgumentException("Zero is not a valid field number.");
         } else {
            this.getFieldBuilder(var1).addLengthDelimited(var2);
            return this;
         }
      }

      public boolean hasField(int var1) {
         if (var1 == 0) {
            throw new IllegalArgumentException("Zero is not a valid field number.");
         } else {
            return var1 == this.lastFieldNumber || this.fields.containsKey(var1);
         }
      }

      public UnknownFieldSet.Builder addField(int var1, UnknownFieldSet.Field var2) {
         if (var1 == 0) {
            throw new IllegalArgumentException("Zero is not a valid field number.");
         } else {
            if (this.lastField != null && this.lastFieldNumber == var1) {
               this.lastField = null;
               this.lastFieldNumber = 0;
            }

            if (this.fields.isEmpty()) {
               this.fields = new TreeMap<>();
            }

            this.fields.put(var1, var2);
            return this;
         }
      }

      public Map<Integer, UnknownFieldSet.Field> asMap() {
         this.getFieldBuilder(0);
         return Collections.unmodifiableMap(this.fields);
      }

      public UnknownFieldSet.Builder mergeFrom(CodedInputStream var1) throws IOException {
         int var2;
         do {
            var2 = var1.readTag();
         } while (var2 != 0 && this.mergeFieldFrom(var2, var1));

         return this;
      }

      public boolean mergeFieldFrom(int var1, CodedInputStream var2) throws IOException {
         int var3 = WireFormat.getTagFieldNumber(var1);
         switch (WireFormat.getTagWireType(var1)) {
            case 0:
               this.getFieldBuilder(var3).addVarint(var2.readInt64());
               return true;
            case 1:
               this.getFieldBuilder(var3).addFixed64(var2.readFixed64());
               return true;
            case 2:
               this.getFieldBuilder(var3).addLengthDelimited(var2.readBytes());
               return true;
            case 3:
               UnknownFieldSet.Builder var4 = UnknownFieldSet.newBuilder();
               var2.readGroup(var3, var4, ExtensionRegistry.getEmptyRegistry());
               this.getFieldBuilder(var3).addGroup(var4.build());
               return true;
            case 4:
               return false;
            case 5:
               this.getFieldBuilder(var3).addFixed32(var2.readFixed32());
               return true;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      public UnknownFieldSet.Builder mergeFrom(ByteString var1) throws InvalidProtocolBufferException {
         try {
            CodedInputStream var2 = var1.newCodedInput();
            this.mergeFrom(var2);
            var2.checkLastTagWas(0);
            return this;
         } catch (InvalidProtocolBufferException var3) {
            throw var3;
         } catch (IOException var4) {
            throw new RuntimeException("Reading from a ByteString threw an IOException (should never happen).", var4);
         }
      }

      public UnknownFieldSet.Builder mergeFrom(byte[] var1) throws InvalidProtocolBufferException {
         try {
            CodedInputStream var2 = CodedInputStream.newInstance(var1);
            this.mergeFrom(var2);
            var2.checkLastTagWas(0);
            return this;
         } catch (InvalidProtocolBufferException var3) {
            throw var3;
         } catch (IOException var4) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", var4);
         }
      }

      public UnknownFieldSet.Builder mergeFrom(InputStream var1) throws IOException {
         CodedInputStream var2 = CodedInputStream.newInstance(var1);
         this.mergeFrom(var2);
         var2.checkLastTagWas(0);
         return this;
      }

      @Override
      public boolean mergeDelimitedFrom(InputStream var1) throws IOException {
         int var2 = var1.read();
         if (var2 == -1) {
            return false;
         } else {
            int var3 = CodedInputStream.readRawVarint32(var2, var1);
            AbstractMessageLite.Builder.LimitedInputStream var4 = new AbstractMessageLite.Builder.LimitedInputStream(var1, var3);
            this.mergeFrom(var4);
            return true;
         }
      }

      @Override
      public boolean mergeDelimitedFrom(InputStream var1, ExtensionRegistryLite var2) throws IOException {
         return this.mergeDelimitedFrom(var1);
      }

      public UnknownFieldSet.Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
         return this.mergeFrom(var1);
      }

      public UnknownFieldSet.Builder mergeFrom(ByteString var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return this.mergeFrom(var1);
      }

      public UnknownFieldSet.Builder mergeFrom(byte[] var1, int var2, int var3) throws InvalidProtocolBufferException {
         try {
            CodedInputStream var4 = CodedInputStream.newInstance(var1, var2, var3);
            this.mergeFrom(var4);
            var4.checkLastTagWas(0);
            return this;
         } catch (InvalidProtocolBufferException var5) {
            throw var5;
         } catch (IOException var6) {
            throw new RuntimeException("Reading from a byte array threw an IOException (should never happen).", var6);
         }
      }

      public UnknownFieldSet.Builder mergeFrom(byte[] var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         return this.mergeFrom(var1);
      }

      public UnknownFieldSet.Builder mergeFrom(byte[] var1, int var2, int var3, ExtensionRegistryLite var4) throws InvalidProtocolBufferException {
         return this.mergeFrom(var1, var2, var3);
      }

      public UnknownFieldSet.Builder mergeFrom(InputStream var1, ExtensionRegistryLite var2) throws IOException {
         return this.mergeFrom(var1);
      }

      public UnknownFieldSet.Builder mergeFrom(MessageLite var1) {
         if (var1 instanceof UnknownFieldSet) {
            return this.mergeFrom((UnknownFieldSet)var1);
         } else {
            throw new IllegalArgumentException("mergeFrom(MessageLite) can only merge messages of the same type.");
         }
      }

      @Override
      public boolean isInitialized() {
         return true;
      }
   }

   public static final class Field {
      private static final UnknownFieldSet.Field fieldDefaultInstance = newBuilder().build();
      private List<Long> varint;
      private List<Integer> fixed32;
      private List<Long> fixed64;
      private List<ByteString> lengthDelimited;
      private List<UnknownFieldSet> group;

      private Field() {
      }

      public static UnknownFieldSet.Field.Builder newBuilder() {
         return UnknownFieldSet.Field.Builder.create();
      }

      public static UnknownFieldSet.Field.Builder newBuilder(UnknownFieldSet.Field var0) {
         return newBuilder().mergeFrom(var0);
      }

      public static UnknownFieldSet.Field getDefaultInstance() {
         return fieldDefaultInstance;
      }

      public List<Long> getVarintList() {
         return this.varint;
      }

      public List<Integer> getFixed32List() {
         return this.fixed32;
      }

      public List<Long> getFixed64List() {
         return this.fixed64;
      }

      public List<ByteString> getLengthDelimitedList() {
         return this.lengthDelimited;
      }

      public List<UnknownFieldSet> getGroupList() {
         return this.group;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return !(var1 instanceof UnknownFieldSet.Field) ? false : Arrays.equals(this.getIdentityArray(), ((UnknownFieldSet.Field)var1).getIdentityArray());
         }
      }

      @Override
      public int hashCode() {
         return Arrays.hashCode(this.getIdentityArray());
      }

      private Object[] getIdentityArray() {
         return new Object[]{this.varint, this.fixed32, this.fixed64, this.lengthDelimited, this.group};
      }

      public ByteString toByteString(int var1) {
         try {
            ByteString.CodedBuilder var2 = ByteString.newCodedBuilder(this.getSerializedSize(var1));
            this.writeTo(var1, var2.getCodedOutput());
            return var2.build();
         } catch (IOException var3) {
            throw new RuntimeException("Serializing to a ByteString should never fail with an IOException", var3);
         }
      }

      public void writeTo(int var1, CodedOutputStream var2) throws IOException {
         for (long var4 : this.varint) {
            var2.writeUInt64(var1, var4);
         }

         for (int var10 : this.fixed32) {
            var2.writeFixed32(var1, var10);
         }

         for (long var11 : this.fixed64) {
            var2.writeFixed64(var1, var11);
         }

         for (ByteString var12 : this.lengthDelimited) {
            var2.writeBytes(var1, var12);
         }

         for (UnknownFieldSet var13 : this.group) {
            var2.writeGroup(var1, var13);
         }
      }

      public int getSerializedSize(int var1) {
         int var2 = 0;

         for (long var4 : this.varint) {
            var2 += CodedOutputStream.computeUInt64Size(var1, var4);
         }

         for (int var10 : this.fixed32) {
            var2 += CodedOutputStream.computeFixed32Size(var1, var10);
         }

         for (long var11 : this.fixed64) {
            var2 += CodedOutputStream.computeFixed64Size(var1, var11);
         }

         for (ByteString var12 : this.lengthDelimited) {
            var2 += CodedOutputStream.computeBytesSize(var1, var12);
         }

         for (UnknownFieldSet var13 : this.group) {
            var2 += CodedOutputStream.computeGroupSize(var1, var13);
         }

         return var2;
      }

      public void writeAsMessageSetExtensionTo(int var1, CodedOutputStream var2) throws IOException {
         for (ByteString var4 : this.lengthDelimited) {
            var2.writeRawMessageSetExtension(var1, var4);
         }
      }

      void writeTo(int var1, Writer var2) throws IOException {
         var2.writeInt64List(var1, this.varint, false);
         var2.writeFixed32List(var1, this.fixed32, false);
         var2.writeFixed64List(var1, this.fixed64, false);
         var2.writeBytesList(var1, this.lengthDelimited);
         if (var2.fieldOrder() == Writer.FieldOrder.ASCENDING) {
            for (int var3 = 0; var3 < this.group.size(); var3++) {
               var2.writeStartGroup(var1);
               this.group.get(var3).writeTo(var2);
               var2.writeEndGroup(var1);
            }
         } else {
            for (int var4 = this.group.size() - 1; var4 >= 0; var4--) {
               var2.writeEndGroup(var1);
               this.group.get(var4).writeTo(var2);
               var2.writeStartGroup(var1);
            }
         }
      }

      private void writeAsMessageSetExtensionTo(int var1, Writer var2) throws IOException {
         if (var2.fieldOrder() == Writer.FieldOrder.DESCENDING) {
            ListIterator var3 = this.lengthDelimited.listIterator(this.lengthDelimited.size());

            while (var3.hasPrevious()) {
               var2.writeMessageSetItem(var1, var3.previous());
            }
         } else {
            for (ByteString var4 : this.lengthDelimited) {
               var2.writeMessageSetItem(var1, var4);
            }
         }
      }

      public int getSerializedSizeAsMessageSetExtension(int var1) {
         int var2 = 0;

         for (ByteString var4 : this.lengthDelimited) {
            var2 += CodedOutputStream.computeRawMessageSetExtensionSize(var1, var4);
         }

         return var2;
      }

      public static final class Builder {
         private UnknownFieldSet.Field result;

         private Builder() {
         }

         private static UnknownFieldSet.Field.Builder create() {
            UnknownFieldSet.Field.Builder var0 = new UnknownFieldSet.Field.Builder();
            var0.result = new UnknownFieldSet.Field();
            return var0;
         }

         public UnknownFieldSet.Field build() {
            if (this.result.varint == null) {
               this.result.varint = Collections.emptyList();
            } else {
               this.result.varint = Collections.unmodifiableList(this.result.varint);
            }

            if (this.result.fixed32 == null) {
               this.result.fixed32 = Collections.emptyList();
            } else {
               this.result.fixed32 = Collections.unmodifiableList(this.result.fixed32);
            }

            if (this.result.fixed64 == null) {
               this.result.fixed64 = Collections.emptyList();
            } else {
               this.result.fixed64 = Collections.unmodifiableList(this.result.fixed64);
            }

            if (this.result.lengthDelimited == null) {
               this.result.lengthDelimited = Collections.emptyList();
            } else {
               this.result.lengthDelimited = Collections.unmodifiableList(this.result.lengthDelimited);
            }

            if (this.result.group == null) {
               this.result.group = Collections.emptyList();
            } else {
               this.result.group = Collections.unmodifiableList(this.result.group);
            }

            UnknownFieldSet.Field var1 = this.result;
            this.result = null;
            return var1;
         }

         public UnknownFieldSet.Field.Builder clear() {
            this.result = new UnknownFieldSet.Field();
            return this;
         }

         public UnknownFieldSet.Field.Builder mergeFrom(UnknownFieldSet.Field var1) {
            if (!var1.varint.isEmpty()) {
               if (this.result.varint == null) {
                  this.result.varint = new ArrayList<>();
               }

               this.result.varint.addAll(var1.varint);
            }

            if (!var1.fixed32.isEmpty()) {
               if (this.result.fixed32 == null) {
                  this.result.fixed32 = new ArrayList<>();
               }

               this.result.fixed32.addAll(var1.fixed32);
            }

            if (!var1.fixed64.isEmpty()) {
               if (this.result.fixed64 == null) {
                  this.result.fixed64 = new ArrayList<>();
               }

               this.result.fixed64.addAll(var1.fixed64);
            }

            if (!var1.lengthDelimited.isEmpty()) {
               if (this.result.lengthDelimited == null) {
                  this.result.lengthDelimited = new ArrayList<>();
               }

               this.result.lengthDelimited.addAll(var1.lengthDelimited);
            }

            if (!var1.group.isEmpty()) {
               if (this.result.group == null) {
                  this.result.group = new ArrayList<>();
               }

               this.result.group.addAll(var1.group);
            }

            return this;
         }

         public UnknownFieldSet.Field.Builder addVarint(long var1) {
            if (this.result.varint == null) {
               this.result.varint = new ArrayList<>();
            }

            this.result.varint.add(var1);
            return this;
         }

         public UnknownFieldSet.Field.Builder addFixed32(int var1) {
            if (this.result.fixed32 == null) {
               this.result.fixed32 = new ArrayList<>();
            }

            this.result.fixed32.add(var1);
            return this;
         }

         public UnknownFieldSet.Field.Builder addFixed64(long var1) {
            if (this.result.fixed64 == null) {
               this.result.fixed64 = new ArrayList<>();
            }

            this.result.fixed64.add(var1);
            return this;
         }

         public UnknownFieldSet.Field.Builder addLengthDelimited(ByteString var1) {
            if (this.result.lengthDelimited == null) {
               this.result.lengthDelimited = new ArrayList<>();
            }

            this.result.lengthDelimited.add(var1);
            return this;
         }

         public UnknownFieldSet.Field.Builder addGroup(UnknownFieldSet var1) {
            if (this.result.group == null) {
               this.result.group = new ArrayList<>();
            }

            this.result.group.add(var1);
            return this;
         }
      }
   }

   public static final class Parser extends AbstractParser<UnknownFieldSet> {
      public UnknownFieldSet parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
         UnknownFieldSet.Builder var3 = UnknownFieldSet.newBuilder();

         try {
            var3.mergeFrom(var1);
         } catch (InvalidProtocolBufferException var5) {
            throw var5.setUnfinishedMessage(var3.buildPartial());
         } catch (IOException var6) {
            throw new InvalidProtocolBufferException(var6).setUnfinishedMessage(var3.buildPartial());
         }

         return var3.buildPartial();
      }
   }
}
