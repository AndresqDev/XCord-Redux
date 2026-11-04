package com.google.protobuf;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

final class CodedOutputStreamWriter implements Writer {
   private final CodedOutputStream output;

   public static CodedOutputStreamWriter forCodedOutput(CodedOutputStream var0) {
      return var0.wrapper != null ? var0.wrapper : new CodedOutputStreamWriter(var0);
   }

   private CodedOutputStreamWriter(CodedOutputStream var1) {
      this.output = Internal.checkNotNull(var1, "output");
      this.output.wrapper = this;
   }

   @Override
   public Writer.FieldOrder fieldOrder() {
      return Writer.FieldOrder.ASCENDING;
   }

   public int getTotalBytesWritten() {
      return this.output.getTotalBytesWritten();
   }

   @Override
   public void writeSFixed32(int var1, int var2) throws IOException {
      this.output.writeSFixed32(var1, var2);
   }

   @Override
   public void writeInt64(int var1, long var2) throws IOException {
      this.output.writeInt64(var1, var2);
   }

   @Override
   public void writeSFixed64(int var1, long var2) throws IOException {
      this.output.writeSFixed64(var1, var2);
   }

   @Override
   public void writeFloat(int var1, float var2) throws IOException {
      this.output.writeFloat(var1, var2);
   }

   @Override
   public void writeDouble(int var1, double var2) throws IOException {
      this.output.writeDouble(var1, var2);
   }

   @Override
   public void writeEnum(int var1, int var2) throws IOException {
      this.output.writeEnum(var1, var2);
   }

   @Override
   public void writeUInt64(int var1, long var2) throws IOException {
      this.output.writeUInt64(var1, var2);
   }

   @Override
   public void writeInt32(int var1, int var2) throws IOException {
      this.output.writeInt32(var1, var2);
   }

   @Override
   public void writeFixed64(int var1, long var2) throws IOException {
      this.output.writeFixed64(var1, var2);
   }

   @Override
   public void writeFixed32(int var1, int var2) throws IOException {
      this.output.writeFixed32(var1, var2);
   }

   @Override
   public void writeBool(int var1, boolean var2) throws IOException {
      this.output.writeBool(var1, var2);
   }

   @Override
   public void writeString(int var1, String var2) throws IOException {
      this.output.writeString(var1, var2);
   }

   @Override
   public void writeBytes(int var1, ByteString var2) throws IOException {
      this.output.writeBytes(var1, var2);
   }

   @Override
   public void writeUInt32(int var1, int var2) throws IOException {
      this.output.writeUInt32(var1, var2);
   }

   @Override
   public void writeSInt32(int var1, int var2) throws IOException {
      this.output.writeSInt32(var1, var2);
   }

   @Override
   public void writeSInt64(int var1, long var2) throws IOException {
      this.output.writeSInt64(var1, var2);
   }

   @Override
   public void writeMessage(int var1, Object var2) throws IOException {
      this.output.writeMessage(var1, (MessageLite)var2);
   }

   @Override
   public void writeMessage(int var1, Object var2, Schema var3) throws IOException {
      this.output.writeMessage(var1, (MessageLite)var2, var3);
   }

   @Override
   public void writeGroup(int var1, Object var2) throws IOException {
      this.output.writeGroup(var1, (MessageLite)var2);
   }

   @Override
   public void writeGroup(int var1, Object var2, Schema var3) throws IOException {
      this.output.writeGroup(var1, (MessageLite)var2, var3);
   }

   @Override
   public void writeStartGroup(int var1) throws IOException {
      this.output.writeTag(var1, 3);
   }

   @Override
   public void writeEndGroup(int var1) throws IOException {
      this.output.writeTag(var1, 4);
   }

   @Override
   public final void writeMessageSetItem(int var1, Object var2) throws IOException {
      if (var2 instanceof ByteString) {
         this.output.writeRawMessageSetExtension(var1, (ByteString)var2);
      } else {
         this.output.writeMessageSetExtension(var1, (MessageLite)var2);
      }
   }

   @Override
   public void writeInt32List(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeInt32SizeNoTag((Integer)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeInt32NoTag((Integer)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeInt32(var1, (Integer)var2.get(var6));
         }
      }
   }

   @Override
   public void writeFixed32List(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeFixed32SizeNoTag((Integer)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeFixed32NoTag((Integer)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeFixed32(var1, (Integer)var2.get(var6));
         }
      }
   }

   @Override
   public void writeInt64List(int var1, List<Long> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeInt64SizeNoTag((Long)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeInt64NoTag((Long)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeInt64(var1, (Long)var2.get(var6));
         }
      }
   }

   @Override
   public void writeUInt64List(int var1, List<Long> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeUInt64SizeNoTag((Long)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeUInt64NoTag((Long)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeUInt64(var1, (Long)var2.get(var6));
         }
      }
   }

   @Override
   public void writeFixed64List(int var1, List<Long> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeFixed64SizeNoTag((Long)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeFixed64NoTag((Long)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeFixed64(var1, (Long)var2.get(var6));
         }
      }
   }

   @Override
   public void writeFloatList(int var1, List<Float> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeFloatSizeNoTag((Float)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeFloatNoTag((Float)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeFloat(var1, (Float)var2.get(var6));
         }
      }
   }

   @Override
   public void writeDoubleList(int var1, List<Double> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeDoubleSizeNoTag((Double)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeDoubleNoTag((Double)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeDouble(var1, (Double)var2.get(var6));
         }
      }
   }

   @Override
   public void writeEnumList(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeEnumSizeNoTag((Integer)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeEnumNoTag((Integer)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeEnum(var1, (Integer)var2.get(var6));
         }
      }
   }

   @Override
   public void writeBoolList(int var1, List<Boolean> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeBoolSizeNoTag((Boolean)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeBoolNoTag((Boolean)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeBool(var1, (Boolean)var2.get(var6));
         }
      }
   }

   @Override
   public void writeStringList(int var1, List<String> var2) throws IOException {
      if (var2 instanceof LazyStringList) {
         LazyStringList var3 = (LazyStringList)var2;

         for (int var4 = 0; var4 < var2.size(); var4++) {
            this.writeLazyString(var1, var3.getRaw(var4));
         }
      } else {
         for (int var5 = 0; var5 < var2.size(); var5++) {
            this.output.writeString(var1, (String)var2.get(var5));
         }
      }
   }

   private void writeLazyString(int var1, Object var2) throws IOException {
      if (var2 instanceof String) {
         this.output.writeString(var1, (String)var2);
      } else {
         this.output.writeBytes(var1, (ByteString)var2);
      }
   }

   @Override
   public void writeBytesList(int var1, List<ByteString> var2) throws IOException {
      for (int var3 = 0; var3 < var2.size(); var3++) {
         this.output.writeBytes(var1, (ByteString)var2.get(var3));
      }
   }

   @Override
   public void writeUInt32List(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeUInt32SizeNoTag((Integer)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeUInt32NoTag((Integer)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeUInt32(var1, (Integer)var2.get(var6));
         }
      }
   }

   @Override
   public void writeSFixed32List(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeSFixed32SizeNoTag((Integer)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeSFixed32NoTag((Integer)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeSFixed32(var1, (Integer)var2.get(var6));
         }
      }
   }

   @Override
   public void writeSFixed64List(int var1, List<Long> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeSFixed64SizeNoTag((Long)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeSFixed64NoTag((Long)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeSFixed64(var1, (Long)var2.get(var6));
         }
      }
   }

   @Override
   public void writeSInt32List(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeSInt32SizeNoTag((Integer)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeSInt32NoTag((Integer)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeSInt32(var1, (Integer)var2.get(var6));
         }
      }
   }

   @Override
   public void writeSInt64List(int var1, List<Long> var2, boolean var3) throws IOException {
      if (var3) {
         this.output.writeTag(var1, 2);
         int var4 = 0;

         for (int var5 = 0; var5 < var2.size(); var5++) {
            var4 += CodedOutputStream.computeSInt64SizeNoTag((Long)var2.get(var5));
         }

         this.output.writeUInt32NoTag(var4);

         for (int var7 = 0; var7 < var2.size(); var7++) {
            this.output.writeSInt64NoTag((Long)var2.get(var7));
         }
      } else {
         for (int var6 = 0; var6 < var2.size(); var6++) {
            this.output.writeSInt64(var1, (Long)var2.get(var6));
         }
      }
   }

   @Override
   public void writeMessageList(int var1, List<?> var2) throws IOException {
      for (int var3 = 0; var3 < var2.size(); var3++) {
         this.writeMessage(var1, var2.get(var3));
      }
   }

   @Override
   public void writeMessageList(int var1, List<?> var2, Schema var3) throws IOException {
      for (int var4 = 0; var4 < var2.size(); var4++) {
         this.writeMessage(var1, var2.get(var4), var3);
      }
   }

   @Override
   public void writeGroupList(int var1, List<?> var2) throws IOException {
      for (int var3 = 0; var3 < var2.size(); var3++) {
         this.writeGroup(var1, var2.get(var3));
      }
   }

   @Override
   public void writeGroupList(int var1, List<?> var2, Schema var3) throws IOException {
      for (int var4 = 0; var4 < var2.size(); var4++) {
         this.writeGroup(var1, var2.get(var4), var3);
      }
   }

   @Override
   public <K, V> void writeMap(int var1, MapEntryLite.Metadata<K, V> var2, Map<K, V> var3) throws IOException {
      if (this.output.isSerializationDeterministic()) {
         this.writeDeterministicMap(var1, var2, var3);
      } else {
         for (Entry var5 : var3.entrySet()) {
            this.output.writeTag(var1, 2);
            this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(var2, var5.getKey(), var5.getValue()));
            MapEntryLite.writeTo(this.output, var2, var5.getKey(), var5.getValue());
         }
      }
   }

   private <K, V> void writeDeterministicMap(int var1, MapEntryLite.Metadata<K, V> var2, Map<K, V> var3) throws IOException {
      switch (var2.keyType) {
         case BOOL:
            Object var4;
            if ((var4 = var3.get(Boolean.FALSE)) != null) {
               this.writeDeterministicBooleanMapEntry(var1, false, var4, var2);
            }

            if ((var4 = var3.get(Boolean.TRUE)) != null) {
               this.writeDeterministicBooleanMapEntry(var1, true, var4, var2);
            }
            break;
         case FIXED32:
         case INT32:
         case SFIXED32:
         case SINT32:
         case UINT32:
            this.writeDeterministicIntegerMap(var1, var2, var3);
            break;
         case FIXED64:
         case INT64:
         case SFIXED64:
         case SINT64:
         case UINT64:
            this.writeDeterministicLongMap(var1, var2, var3);
            break;
         case STRING:
            this.writeDeterministicStringMap(var1, var2, var3);
            break;
         default:
            throw new IllegalArgumentException("does not support key type: " + var2.keyType);
      }
   }

   private <V> void writeDeterministicBooleanMapEntry(int var1, boolean var2, V var3, MapEntryLite.Metadata<Boolean, V> var4) throws IOException {
      this.output.writeTag(var1, 2);
      this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(var4, var2, var3));
      MapEntryLite.writeTo(this.output, var4, var2, var3);
   }

   private <V> void writeDeterministicIntegerMap(int var1, MapEntryLite.Metadata<Integer, V> var2, Map<Integer, V> var3) throws IOException {
      int[] var4 = new int[var3.size()];
      int var5 = 0;

      for (int var7 : var3.keySet()) {
         var4[var5++] = var7;
      }

      Arrays.sort(var4);

      for (int var9 : var4) {
         Object var10 = var3.get(var9);
         this.output.writeTag(var1, 2);
         this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(var2, var9, var10));
         MapEntryLite.writeTo(this.output, var2, var9, var10);
      }
   }

   private <V> void writeDeterministicLongMap(int var1, MapEntryLite.Metadata<Long, V> var2, Map<Long, V> var3) throws IOException {
      long[] var4 = new long[var3.size()];
      int var5 = 0;

      for (long var7 : var3.keySet()) {
         var4[var5++] = var7;
      }

      Arrays.sort(var4);

      for (long var9 : var4) {
         Object var11 = var3.get(var9);
         this.output.writeTag(var1, 2);
         this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(var2, var9, var11));
         MapEntryLite.writeTo(this.output, var2, var9, var11);
      }
   }

   private <V> void writeDeterministicStringMap(int var1, MapEntryLite.Metadata<String, V> var2, Map<String, V> var3) throws IOException {
      String[] var4 = new String[var3.size()];
      int var5 = 0;

      for (String var7 : var3.keySet()) {
         var4[var5++] = var7;
      }

      Arrays.sort((Object[])var4);

      for (String var9 : var4) {
         Object var10 = var3.get(var9);
         this.output.writeTag(var1, 2);
         this.output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(var2, var9, var10));
         MapEntryLite.writeTo(this.output, var2, var9, var10);
      }
   }
}
