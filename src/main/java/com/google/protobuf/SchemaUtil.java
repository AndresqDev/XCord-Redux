package com.google.protobuf;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

final class SchemaUtil {
   private static final Class<?> GENERATED_MESSAGE_CLASS = getGeneratedMessageClass();
   private static final UnknownFieldSchema<?, ?> PROTO2_UNKNOWN_FIELD_SET_SCHEMA = getUnknownFieldSetSchema(false);
   private static final UnknownFieldSchema<?, ?> PROTO3_UNKNOWN_FIELD_SET_SCHEMA = getUnknownFieldSetSchema(true);
   private static final UnknownFieldSchema<?, ?> UNKNOWN_FIELD_SET_LITE_SCHEMA = new UnknownFieldSetLiteSchema();
   private static final int DEFAULT_LOOK_UP_START_NUMBER = 40;

   private SchemaUtil() {
   }

   public static void requireGeneratedMessage(Class<?> var0) {
      if (!GeneratedMessageLite.class.isAssignableFrom(var0) && GENERATED_MESSAGE_CLASS != null && !GENERATED_MESSAGE_CLASS.isAssignableFrom(var0)) {
         throw new IllegalArgumentException("Message classes must extend GeneratedMessage or GeneratedMessageLite");
      }
   }

   public static void writeDouble(int var0, double var1, Writer var3) throws IOException {
      if (Double.compare(var1, 0.0) != 0) {
         var3.writeDouble(var0, var1);
      }
   }

   public static void writeFloat(int var0, float var1, Writer var2) throws IOException {
      if (Float.compare(var1, 0.0F) != 0) {
         var2.writeFloat(var0, var1);
      }
   }

   public static void writeInt64(int var0, long var1, Writer var3) throws IOException {
      if (var1 != 0L) {
         var3.writeInt64(var0, var1);
      }
   }

   public static void writeUInt64(int var0, long var1, Writer var3) throws IOException {
      if (var1 != 0L) {
         var3.writeUInt64(var0, var1);
      }
   }

   public static void writeSInt64(int var0, long var1, Writer var3) throws IOException {
      if (var1 != 0L) {
         var3.writeSInt64(var0, var1);
      }
   }

   public static void writeFixed64(int var0, long var1, Writer var3) throws IOException {
      if (var1 != 0L) {
         var3.writeFixed64(var0, var1);
      }
   }

   public static void writeSFixed64(int var0, long var1, Writer var3) throws IOException {
      if (var1 != 0L) {
         var3.writeSFixed64(var0, var1);
      }
   }

   public static void writeInt32(int var0, int var1, Writer var2) throws IOException {
      if (var1 != 0) {
         var2.writeInt32(var0, var1);
      }
   }

   public static void writeUInt32(int var0, int var1, Writer var2) throws IOException {
      if (var1 != 0) {
         var2.writeUInt32(var0, var1);
      }
   }

   public static void writeSInt32(int var0, int var1, Writer var2) throws IOException {
      if (var1 != 0) {
         var2.writeSInt32(var0, var1);
      }
   }

   public static void writeFixed32(int var0, int var1, Writer var2) throws IOException {
      if (var1 != 0) {
         var2.writeFixed32(var0, var1);
      }
   }

   public static void writeSFixed32(int var0, int var1, Writer var2) throws IOException {
      if (var1 != 0) {
         var2.writeSFixed32(var0, var1);
      }
   }

   public static void writeEnum(int var0, int var1, Writer var2) throws IOException {
      if (var1 != 0) {
         var2.writeEnum(var0, var1);
      }
   }

   public static void writeBool(int var0, boolean var1, Writer var2) throws IOException {
      if (var1) {
         var2.writeBool(var0, true);
      }
   }

   public static void writeString(int var0, Object var1, Writer var2) throws IOException {
      if (var1 instanceof String) {
         writeStringInternal(var0, (String)var1, var2);
      } else {
         writeBytes(var0, (ByteString)var1, var2);
      }
   }

   private static void writeStringInternal(int var0, String var1, Writer var2) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeString(var0, var1);
      }
   }

   public static void writeBytes(int var0, ByteString var1, Writer var2) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeBytes(var0, var1);
      }
   }

   public static void writeMessage(int var0, Object var1, Writer var2) throws IOException {
      if (var1 != null) {
         var2.writeMessage(var0, var1);
      }
   }

   public static void writeDoubleList(int var0, List<Double> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeDoubleList(var0, var1, var3);
      }
   }

   public static void writeFloatList(int var0, List<Float> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeFloatList(var0, var1, var3);
      }
   }

   public static void writeInt64List(int var0, List<Long> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeInt64List(var0, var1, var3);
      }
   }

   public static void writeUInt64List(int var0, List<Long> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeUInt64List(var0, var1, var3);
      }
   }

   public static void writeSInt64List(int var0, List<Long> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeSInt64List(var0, var1, var3);
      }
   }

   public static void writeFixed64List(int var0, List<Long> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeFixed64List(var0, var1, var3);
      }
   }

   public static void writeSFixed64List(int var0, List<Long> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeSFixed64List(var0, var1, var3);
      }
   }

   public static void writeInt32List(int var0, List<Integer> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeInt32List(var0, var1, var3);
      }
   }

   public static void writeUInt32List(int var0, List<Integer> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeUInt32List(var0, var1, var3);
      }
   }

   public static void writeSInt32List(int var0, List<Integer> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeSInt32List(var0, var1, var3);
      }
   }

   public static void writeFixed32List(int var0, List<Integer> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeFixed32List(var0, var1, var3);
      }
   }

   public static void writeSFixed32List(int var0, List<Integer> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeSFixed32List(var0, var1, var3);
      }
   }

   public static void writeEnumList(int var0, List<Integer> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeEnumList(var0, var1, var3);
      }
   }

   public static void writeBoolList(int var0, List<Boolean> var1, Writer var2, boolean var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeBoolList(var0, var1, var3);
      }
   }

   public static void writeStringList(int var0, List<String> var1, Writer var2) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeStringList(var0, var1);
      }
   }

   public static void writeBytesList(int var0, List<ByteString> var1, Writer var2) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeBytesList(var0, var1);
      }
   }

   public static void writeMessageList(int var0, List<?> var1, Writer var2) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeMessageList(var0, var1);
      }
   }

   public static void writeMessageList(int var0, List<?> var1, Writer var2, Schema var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeMessageList(var0, var1, var3);
      }
   }

   public static void writeLazyFieldList(int var0, List<?> var1, Writer var2) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         for (Object var4 : var1) {
            ((LazyFieldLite)var4).writeTo(var2, var0);
         }
      }
   }

   public static void writeGroupList(int var0, List<?> var1, Writer var2) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeGroupList(var0, var1);
      }
   }

   public static void writeGroupList(int var0, List<?> var1, Writer var2, Schema var3) throws IOException {
      if (var1 != null && !var1.isEmpty()) {
         var2.writeGroupList(var0, var1, var3);
      }
   }

   static int computeSizeInt64ListNoTag(List<Long> var0) {
      int var1 = var0.size();
      if (var1 == 0) {
         return 0;
      } else {
         int var2 = 0;
         if (var0 instanceof LongArrayList) {
            LongArrayList var3 = (LongArrayList)var0;

            for (int var4 = 0; var4 < var1; var4++) {
               var2 += CodedOutputStream.computeInt64SizeNoTag(var3.getLong(var4));
            }
         } else {
            for (int var5 = 0; var5 < var1; var5++) {
               var2 += CodedOutputStream.computeInt64SizeNoTag((Long)var0.get(var5));
            }
         }

         return var2;
      }
   }

   static int computeSizeInt64List(int var0, List<Long> var1, boolean var2) {
      int var3 = var1.size();
      if (var3 == 0) {
         return 0;
      } else {
         int var4 = computeSizeInt64ListNoTag(var1);
         return var2
            ? CodedOutputStream.computeTagSize(var0) + CodedOutputStream.computeLengthDelimitedFieldSize(var4)
            : var4 + var1.size() * CodedOutputStream.computeTagSize(var0);
      }
   }

   static int computeSizeUInt64ListNoTag(List<Long> var0) {
      int var1 = var0.size();
      if (var1 == 0) {
         return 0;
      } else {
         int var2 = 0;
         if (var0 instanceof LongArrayList) {
            LongArrayList var3 = (LongArrayList)var0;

            for (int var4 = 0; var4 < var1; var4++) {
               var2 += CodedOutputStream.computeUInt64SizeNoTag(var3.getLong(var4));
            }
         } else {
            for (int var5 = 0; var5 < var1; var5++) {
               var2 += CodedOutputStream.computeUInt64SizeNoTag((Long)var0.get(var5));
            }
         }

         return var2;
      }
   }

   static int computeSizeUInt64List(int var0, List<Long> var1, boolean var2) {
      int var3 = var1.size();
      if (var3 == 0) {
         return 0;
      } else {
         int var4 = computeSizeUInt64ListNoTag(var1);
         return var2
            ? CodedOutputStream.computeTagSize(var0) + CodedOutputStream.computeLengthDelimitedFieldSize(var4)
            : var4 + var3 * CodedOutputStream.computeTagSize(var0);
      }
   }

   static int computeSizeSInt64ListNoTag(List<Long> var0) {
      int var1 = var0.size();
      if (var1 == 0) {
         return 0;
      } else {
         int var2 = 0;
         if (var0 instanceof LongArrayList) {
            LongArrayList var3 = (LongArrayList)var0;

            for (int var4 = 0; var4 < var1; var4++) {
               var2 += CodedOutputStream.computeSInt64SizeNoTag(var3.getLong(var4));
            }
         } else {
            for (int var5 = 0; var5 < var1; var5++) {
               var2 += CodedOutputStream.computeSInt64SizeNoTag((Long)var0.get(var5));
            }
         }

         return var2;
      }
   }

   static int computeSizeSInt64List(int var0, List<Long> var1, boolean var2) {
      int var3 = var1.size();
      if (var3 == 0) {
         return 0;
      } else {
         int var4 = computeSizeSInt64ListNoTag(var1);
         return var2
            ? CodedOutputStream.computeTagSize(var0) + CodedOutputStream.computeLengthDelimitedFieldSize(var4)
            : var4 + var3 * CodedOutputStream.computeTagSize(var0);
      }
   }

   static int computeSizeEnumListNoTag(List<Integer> var0) {
      int var1 = var0.size();
      if (var1 == 0) {
         return 0;
      } else {
         int var2 = 0;
         if (var0 instanceof IntArrayList) {
            IntArrayList var3 = (IntArrayList)var0;

            for (int var4 = 0; var4 < var1; var4++) {
               var2 += CodedOutputStream.computeEnumSizeNoTag(var3.getInt(var4));
            }
         } else {
            for (int var5 = 0; var5 < var1; var5++) {
               var2 += CodedOutputStream.computeEnumSizeNoTag((Integer)var0.get(var5));
            }
         }

         return var2;
      }
   }

   static int computeSizeEnumList(int var0, List<Integer> var1, boolean var2) {
      int var3 = var1.size();
      if (var3 == 0) {
         return 0;
      } else {
         int var4 = computeSizeEnumListNoTag(var1);
         return var2
            ? CodedOutputStream.computeTagSize(var0) + CodedOutputStream.computeLengthDelimitedFieldSize(var4)
            : var4 + var3 * CodedOutputStream.computeTagSize(var0);
      }
   }

   static int computeSizeInt32ListNoTag(List<Integer> var0) {
      int var1 = var0.size();
      if (var1 == 0) {
         return 0;
      } else {
         int var2 = 0;
         if (var0 instanceof IntArrayList) {
            IntArrayList var3 = (IntArrayList)var0;

            for (int var4 = 0; var4 < var1; var4++) {
               var2 += CodedOutputStream.computeInt32SizeNoTag(var3.getInt(var4));
            }
         } else {
            for (int var5 = 0; var5 < var1; var5++) {
               var2 += CodedOutputStream.computeInt32SizeNoTag((Integer)var0.get(var5));
            }
         }

         return var2;
      }
   }

   static int computeSizeInt32List(int var0, List<Integer> var1, boolean var2) {
      int var3 = var1.size();
      if (var3 == 0) {
         return 0;
      } else {
         int var4 = computeSizeInt32ListNoTag(var1);
         return var2
            ? CodedOutputStream.computeTagSize(var0) + CodedOutputStream.computeLengthDelimitedFieldSize(var4)
            : var4 + var3 * CodedOutputStream.computeTagSize(var0);
      }
   }

   static int computeSizeUInt32ListNoTag(List<Integer> var0) {
      int var1 = var0.size();
      if (var1 == 0) {
         return 0;
      } else {
         int var2 = 0;
         if (var0 instanceof IntArrayList) {
            IntArrayList var3 = (IntArrayList)var0;

            for (int var4 = 0; var4 < var1; var4++) {
               var2 += CodedOutputStream.computeUInt32SizeNoTag(var3.getInt(var4));
            }
         } else {
            for (int var5 = 0; var5 < var1; var5++) {
               var2 += CodedOutputStream.computeUInt32SizeNoTag((Integer)var0.get(var5));
            }
         }

         return var2;
      }
   }

   static int computeSizeUInt32List(int var0, List<Integer> var1, boolean var2) {
      int var3 = var1.size();
      if (var3 == 0) {
         return 0;
      } else {
         int var4 = computeSizeUInt32ListNoTag(var1);
         return var2
            ? CodedOutputStream.computeTagSize(var0) + CodedOutputStream.computeLengthDelimitedFieldSize(var4)
            : var4 + var3 * CodedOutputStream.computeTagSize(var0);
      }
   }

   static int computeSizeSInt32ListNoTag(List<Integer> var0) {
      int var1 = var0.size();
      if (var1 == 0) {
         return 0;
      } else {
         int var2 = 0;
         if (var0 instanceof IntArrayList) {
            IntArrayList var3 = (IntArrayList)var0;

            for (int var4 = 0; var4 < var1; var4++) {
               var2 += CodedOutputStream.computeSInt32SizeNoTag(var3.getInt(var4));
            }
         } else {
            for (int var5 = 0; var5 < var1; var5++) {
               var2 += CodedOutputStream.computeSInt32SizeNoTag((Integer)var0.get(var5));
            }
         }

         return var2;
      }
   }

   static int computeSizeSInt32List(int var0, List<Integer> var1, boolean var2) {
      int var3 = var1.size();
      if (var3 == 0) {
         return 0;
      } else {
         int var4 = computeSizeSInt32ListNoTag(var1);
         return var2
            ? CodedOutputStream.computeTagSize(var0) + CodedOutputStream.computeLengthDelimitedFieldSize(var4)
            : var4 + var3 * CodedOutputStream.computeTagSize(var0);
      }
   }

   static int computeSizeFixed32ListNoTag(List<?> var0) {
      return var0.size() * 4;
   }

   static int computeSizeFixed32List(int var0, List<?> var1, boolean var2) {
      int var3 = var1.size();
      if (var3 == 0) {
         return 0;
      } else if (var2) {
         int var4 = var3 * 4;
         return CodedOutputStream.computeTagSize(var0) + CodedOutputStream.computeLengthDelimitedFieldSize(var4);
      } else {
         return var3 * CodedOutputStream.computeFixed32Size(var0, 0);
      }
   }

   static int computeSizeFixed64ListNoTag(List<?> var0) {
      return var0.size() * 8;
   }

   static int computeSizeFixed64List(int var0, List<?> var1, boolean var2) {
      int var3 = var1.size();
      if (var3 == 0) {
         return 0;
      } else if (var2) {
         int var4 = var3 * 8;
         return CodedOutputStream.computeTagSize(var0) + CodedOutputStream.computeLengthDelimitedFieldSize(var4);
      } else {
         return var3 * CodedOutputStream.computeFixed64Size(var0, 0L);
      }
   }

   static int computeSizeBoolListNoTag(List<?> var0) {
      return var0.size();
   }

   static int computeSizeBoolList(int var0, List<?> var1, boolean var2) {
      int var3 = var1.size();
      if (var3 == 0) {
         return 0;
      } else {
         return var2
            ? CodedOutputStream.computeTagSize(var0) + CodedOutputStream.computeLengthDelimitedFieldSize(var3)
            : var3 * CodedOutputStream.computeBoolSize(var0, true);
      }
   }

   static int computeSizeStringList(int var0, List<?> var1) {
      int var2 = var1.size();
      if (var2 == 0) {
         return 0;
      } else {
         int var3 = var2 * CodedOutputStream.computeTagSize(var0);
         if (var1 instanceof LazyStringList) {
            LazyStringList var4 = (LazyStringList)var1;

            for (int var5 = 0; var5 < var2; var5++) {
               Object var6 = var4.getRaw(var5);
               if (var6 instanceof ByteString) {
                  var3 += CodedOutputStream.computeBytesSizeNoTag((ByteString)var6);
               } else {
                  var3 += CodedOutputStream.computeStringSizeNoTag((String)var6);
               }
            }
         } else {
            for (int var7 = 0; var7 < var2; var7++) {
               Object var8 = var1.get(var7);
               if (var8 instanceof ByteString) {
                  var3 += CodedOutputStream.computeBytesSizeNoTag((ByteString)var8);
               } else {
                  var3 += CodedOutputStream.computeStringSizeNoTag((String)var8);
               }
            }
         }

         return var3;
      }
   }

   static int computeSizeMessage(int var0, Object var1, Schema var2) {
      return var1 instanceof LazyFieldLite
         ? CodedOutputStream.computeLazyFieldSize(var0, (LazyFieldLite)var1)
         : CodedOutputStream.computeMessageSize(var0, (MessageLite)var1, var2);
   }

   static int computeSizeMessageList(int var0, List<?> var1) {
      int var2 = var1.size();
      if (var2 == 0) {
         return 0;
      } else {
         int var3 = var2 * CodedOutputStream.computeTagSize(var0);

         for (int var4 = 0; var4 < var2; var4++) {
            Object var5 = var1.get(var4);
            if (var5 instanceof LazyFieldLite) {
               var3 += CodedOutputStream.computeLazyFieldSizeNoTag((LazyFieldLite)var5);
            } else {
               var3 += CodedOutputStream.computeMessageSizeNoTag((MessageLite)var5);
            }
         }

         return var3;
      }
   }

   static int computeSizeMessageList(int var0, List<?> var1, Schema var2) {
      int var3 = var1.size();
      if (var3 == 0) {
         return 0;
      } else {
         int var4 = var3 * CodedOutputStream.computeTagSize(var0);

         for (int var5 = 0; var5 < var3; var5++) {
            Object var6 = var1.get(var5);
            if (var6 instanceof LazyFieldLite) {
               var4 += CodedOutputStream.computeLazyFieldSizeNoTag((LazyFieldLite)var6);
            } else {
               var4 += CodedOutputStream.computeMessageSizeNoTag((MessageLite)var6, var2);
            }
         }

         return var4;
      }
   }

   static int computeSizeByteStringList(int var0, List<ByteString> var1) {
      int var2 = var1.size();
      if (var2 == 0) {
         return 0;
      } else {
         int var3 = var2 * CodedOutputStream.computeTagSize(var0);

         for (int var4 = 0; var4 < var1.size(); var4++) {
            var3 += CodedOutputStream.computeBytesSizeNoTag((ByteString)var1.get(var4));
         }

         return var3;
      }
   }

   static int computeSizeGroupList(int var0, List<MessageLite> var1) {
      int var2 = var1.size();
      if (var2 == 0) {
         return 0;
      } else {
         int var3 = 0;

         for (int var4 = 0; var4 < var2; var4++) {
            var3 += CodedOutputStream.computeGroupSize(var0, (MessageLite)var1.get(var4));
         }

         return var3;
      }
   }

   static int computeSizeGroupList(int var0, List<MessageLite> var1, Schema var2) {
      int var3 = var1.size();
      if (var3 == 0) {
         return 0;
      } else {
         int var4 = 0;

         for (int var5 = 0; var5 < var3; var5++) {
            var4 += CodedOutputStream.computeGroupSize(var0, (MessageLite)var1.get(var5), var2);
         }

         return var4;
      }
   }

   public static boolean shouldUseTableSwitch(FieldInfo[] var0) {
      if (var0.length == 0) {
         return false;
      } else {
         int var1 = var0[0].getFieldNumber();
         int var2 = var0[var0.length - 1].getFieldNumber();
         return shouldUseTableSwitch(var1, var2, var0.length);
      }
   }

   public static boolean shouldUseTableSwitch(int var0, int var1, int var2) {
      if (var1 < 40) {
         return true;
      } else {
         long var3 = (long)var1 - (long)var0 + 1L;
         long var5 = 3L;
         long var7 = 3L + 2L * (long)var2;
         long var9 = 3L + (long)var2;
         return var3 + 3L * var5 <= var7 + 3L * var9;
      }
   }

   public static UnknownFieldSchema<?, ?> proto2UnknownFieldSetSchema() {
      return PROTO2_UNKNOWN_FIELD_SET_SCHEMA;
   }

   public static UnknownFieldSchema<?, ?> proto3UnknownFieldSetSchema() {
      return PROTO3_UNKNOWN_FIELD_SET_SCHEMA;
   }

   public static UnknownFieldSchema<?, ?> unknownFieldSetLiteSchema() {
      return UNKNOWN_FIELD_SET_LITE_SCHEMA;
   }

   private static UnknownFieldSchema<?, ?> getUnknownFieldSetSchema(boolean var0) {
      try {
         Class var1 = getUnknownFieldSetSchemaClass();
         return var1 == null ? null : (UnknownFieldSchema)var1.getConstructor(boolean.class).newInstance(var0);
      } catch (Throwable var2) {
         return null;
      }
   }

   private static Class<?> getGeneratedMessageClass() {
      try {
         return Class.forName("com.google.protobuf.GeneratedMessageV3");
      } catch (Throwable var1) {
         return null;
      }
   }

   private static Class<?> getUnknownFieldSetSchemaClass() {
      try {
         return Class.forName("com.google.protobuf.UnknownFieldSetSchema");
      } catch (Throwable var1) {
         return null;
      }
   }

   static Object getMapDefaultEntry(Class<?> var0, String var1) {
      try {
         Class var2 = Class.forName(var0.getName() + "$" + toCamelCase(var1, true) + "DefaultEntryHolder");
         java.lang.reflect.Field[] var3 = var2.getDeclaredFields();
         if (var3.length != 1) {
            throw new IllegalStateException("Unable to look up map field default entry holder class for " + var1 + " in " + var0.getName());
         } else {
            return UnsafeUtil.getStaticObject(var3[0]);
         }
      } catch (Throwable var4) {
         throw new RuntimeException(var4);
      }
   }

   static String toCamelCase(String var0, boolean var1) {
      StringBuilder var2 = new StringBuilder();

      for (int var3 = 0; var3 < var0.length(); var3++) {
         char var4 = var0.charAt(var3);
         if ('a' <= var4 && var4 <= 'z') {
            if (var1) {
               var2.append((char)(var4 + -32));
            } else {
               var2.append(var4);
            }

            var1 = false;
         } else if ('A' <= var4 && var4 <= 'Z') {
            if (var3 == 0 && !var1) {
               var2.append((char)(var4 - -32));
            } else {
               var2.append(var4);
            }

            var1 = false;
         } else if ('0' <= var4 && var4 <= '9') {
            var2.append(var4);
            var1 = true;
         } else {
            var1 = true;
         }
      }

      return var2.toString();
   }

   static boolean safeEquals(Object var0, Object var1) {
      return var0 == var1 || var0 != null && var0.equals(var1);
   }

   static <T> void mergeMap(MapFieldSchema var0, T var1, T var2, long var3) {
      Object var5 = var0.mergeFrom(UnsafeUtil.getObject(var1, var3), UnsafeUtil.getObject(var2, var3));
      UnsafeUtil.putObject(var1, var3, var5);
   }

   static <T, FT extends FieldSet.FieldDescriptorLite<FT>> void mergeExtensions(ExtensionSchema<FT> var0, T var1, T var2) {
      FieldSet var3 = var0.getExtensions(var2);
      if (!var3.isEmpty()) {
         FieldSet var4 = var0.getMutableExtensions(var1);
         var4.mergeFrom(var3);
      }
   }

   static <T, UT, UB> void mergeUnknownFields(UnknownFieldSchema<UT, UB> var0, T var1, T var2) {
      Object var3 = var0.getFromMessage(var1);
      Object var4 = var0.getFromMessage(var2);
      Object var5 = var0.merge(var3, var4);
      var0.setToMessage(var1, var5);
   }

   static <UT, UB> UB filterUnknownEnumList(int var0, List<Integer> var1, Internal.EnumLiteMap<?> var2, UB var3, UnknownFieldSchema<UT, UB> var4) {
      if (var2 == null) {
         return (UB)var3;
      } else {
         if (var1 instanceof RandomAccess) {
            int var5 = 0;
            int var6 = var1.size();

            for (int var7 = 0; var7 < var6; var7++) {
               int var8 = (Integer)var1.get(var7);
               if (var2.findValueByNumber(var8) != null) {
                  if (var7 != var5) {
                     var1.set(var5, var8);
                  }

                  var5++;
               } else {
                  var3 = storeUnknownEnum(var0, var8, var3, var4);
               }
            }

            if (var5 != var6) {
               var1.subList(var5, var6).clear();
            }
         } else {
            Iterator var9 = var1.iterator();

            while (var9.hasNext()) {
               int var10 = (Integer)var9.next();
               if (var2.findValueByNumber(var10) == null) {
                  var3 = storeUnknownEnum(var0, var10, var3, var4);
                  var9.remove();
               }
            }
         }

         return (UB)var3;
      }
   }

   static <UT, UB> UB filterUnknownEnumList(int var0, List<Integer> var1, Internal.EnumVerifier var2, UB var3, UnknownFieldSchema<UT, UB> var4) {
      if (var2 == null) {
         return (UB)var3;
      } else {
         if (var1 instanceof RandomAccess) {
            int var5 = 0;
            int var6 = var1.size();

            for (int var7 = 0; var7 < var6; var7++) {
               int var8 = (Integer)var1.get(var7);
               if (var2.isInRange(var8)) {
                  if (var7 != var5) {
                     var1.set(var5, var8);
                  }

                  var5++;
               } else {
                  var3 = storeUnknownEnum(var0, var8, var3, var4);
               }
            }

            if (var5 != var6) {
               var1.subList(var5, var6).clear();
            }
         } else {
            Iterator var9 = var1.iterator();

            while (var9.hasNext()) {
               int var10 = (Integer)var9.next();
               if (!var2.isInRange(var10)) {
                  var3 = storeUnknownEnum(var0, var10, var3, var4);
                  var9.remove();
               }
            }
         }

         return (UB)var3;
      }
   }

   static <UT, UB> UB storeUnknownEnum(int var0, int var1, UB var2, UnknownFieldSchema<UT, UB> var3) {
      if (var2 == null) {
         var2 = var3.newBuilder();
      }

      var3.addVarint(var2, var0, (long)var1);
      return (UB)var2;
   }
}
