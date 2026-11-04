package com.google.protobuf;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;

abstract class BinaryWriter extends ByteOutput implements Writer {
   public static final int DEFAULT_CHUNK_SIZE = 4096;
   private final BufferAllocator alloc;
   private final int chunkSize;
   final ArrayDeque<AllocatedBuffer> buffers = new ArrayDeque<>(4);
   int totalDoneBytes;
   private static final int MAP_KEY_NUMBER = 1;
   private static final int MAP_VALUE_NUMBER = 2;

   public static BinaryWriter newHeapInstance(BufferAllocator var0) {
      return newHeapInstance(var0, 4096);
   }

   public static BinaryWriter newHeapInstance(BufferAllocator var0, int var1) {
      return isUnsafeHeapSupported() ? newUnsafeHeapInstance(var0, var1) : newSafeHeapInstance(var0, var1);
   }

   public static BinaryWriter newDirectInstance(BufferAllocator var0) {
      return newDirectInstance(var0, 4096);
   }

   public static BinaryWriter newDirectInstance(BufferAllocator var0, int var1) {
      return isUnsafeDirectSupported() ? newUnsafeDirectInstance(var0, var1) : newSafeDirectInstance(var0, var1);
   }

   static boolean isUnsafeHeapSupported() {
      return BinaryWriter.UnsafeHeapWriter.isSupported();
   }

   static boolean isUnsafeDirectSupported() {
      return BinaryWriter.UnsafeDirectWriter.isSupported();
   }

   static BinaryWriter newSafeHeapInstance(BufferAllocator var0, int var1) {
      return new BinaryWriter.SafeHeapWriter(var0, var1);
   }

   static BinaryWriter newUnsafeHeapInstance(BufferAllocator var0, int var1) {
      if (!isUnsafeHeapSupported()) {
         throw new UnsupportedOperationException("Unsafe operations not supported");
      } else {
         return new BinaryWriter.UnsafeHeapWriter(var0, var1);
      }
   }

   static BinaryWriter newSafeDirectInstance(BufferAllocator var0, int var1) {
      return new BinaryWriter.SafeDirectWriter(var0, var1);
   }

   static BinaryWriter newUnsafeDirectInstance(BufferAllocator var0, int var1) {
      if (!isUnsafeDirectSupported()) {
         throw new UnsupportedOperationException("Unsafe operations not supported");
      } else {
         return new BinaryWriter.UnsafeDirectWriter(var0, var1);
      }
   }

   private BinaryWriter(BufferAllocator var1, int var2) {
      if (var2 <= 0) {
         throw new IllegalArgumentException("chunkSize must be > 0");
      } else {
         this.alloc = Internal.checkNotNull(var1, "alloc");
         this.chunkSize = var2;
      }
   }

   @Override
   public final Writer.FieldOrder fieldOrder() {
      return Writer.FieldOrder.DESCENDING;
   }

   public final Queue<AllocatedBuffer> complete() {
      this.finishCurrentBuffer();
      return this.buffers;
   }

   @Override
   public final void writeSFixed32(int var1, int var2) throws IOException {
      this.writeFixed32(var1, var2);
   }

   @Override
   public final void writeInt64(int var1, long var2) throws IOException {
      this.writeUInt64(var1, var2);
   }

   @Override
   public final void writeSFixed64(int var1, long var2) throws IOException {
      this.writeFixed64(var1, var2);
   }

   @Override
   public final void writeFloat(int var1, float var2) throws IOException {
      this.writeFixed32(var1, Float.floatToRawIntBits(var2));
   }

   @Override
   public final void writeDouble(int var1, double var2) throws IOException {
      this.writeFixed64(var1, Double.doubleToRawLongBits(var2));
   }

   @Override
   public final void writeEnum(int var1, int var2) throws IOException {
      this.writeInt32(var1, var2);
   }

   @Override
   public final void writeInt32List(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var2 instanceof IntArrayList) {
         this.writeInt32List_Internal(var1, (IntArrayList)var2, var3);
      } else {
         this.writeInt32List_Internal(var1, var2, var3);
      }
   }

   private final void writeInt32List_Internal(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 10);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeInt32((Integer)var2.get(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeInt32(var1, (Integer)var2.get(var6));
         }
      }
   }

   private final void writeInt32List_Internal(int var1, IntArrayList var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 10);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeInt32(var2.getInt(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeInt32(var1, var2.getInt(var6));
         }
      }
   }

   @Override
   public final void writeFixed32List(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var2 instanceof IntArrayList) {
         this.writeFixed32List_Internal(var1, (IntArrayList)var2, var3);
      } else {
         this.writeFixed32List_Internal(var1, var2, var3);
      }
   }

   private final void writeFixed32List_Internal(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 4);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeFixed32((Integer)var2.get(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeFixed32(var1, (Integer)var2.get(var6));
         }
      }
   }

   private final void writeFixed32List_Internal(int var1, IntArrayList var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 4);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeFixed32(var2.getInt(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeFixed32(var1, var2.getInt(var6));
         }
      }
   }

   @Override
   public final void writeInt64List(int var1, List<Long> var2, boolean var3) throws IOException {
      this.writeUInt64List(var1, var2, var3);
   }

   @Override
   public final void writeUInt64List(int var1, List<Long> var2, boolean var3) throws IOException {
      if (var2 instanceof LongArrayList) {
         this.writeUInt64List_Internal(var1, (LongArrayList)var2, var3);
      } else {
         this.writeUInt64List_Internal(var1, var2, var3);
      }
   }

   private final void writeUInt64List_Internal(int var1, List<Long> var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 10);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeVarint64((Long)var2.get(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeUInt64(var1, (Long)var2.get(var6));
         }
      }
   }

   private final void writeUInt64List_Internal(int var1, LongArrayList var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 10);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeVarint64(var2.getLong(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeUInt64(var1, var2.getLong(var6));
         }
      }
   }

   @Override
   public final void writeFixed64List(int var1, List<Long> var2, boolean var3) throws IOException {
      if (var2 instanceof LongArrayList) {
         this.writeFixed64List_Internal(var1, (LongArrayList)var2, var3);
      } else {
         this.writeFixed64List_Internal(var1, var2, var3);
      }
   }

   private final void writeFixed64List_Internal(int var1, List<Long> var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 8);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeFixed64((Long)var2.get(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeFixed64(var1, (Long)var2.get(var6));
         }
      }
   }

   private final void writeFixed64List_Internal(int var1, LongArrayList var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 8);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeFixed64(var2.getLong(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeFixed64(var1, var2.getLong(var6));
         }
      }
   }

   @Override
   public final void writeFloatList(int var1, List<Float> var2, boolean var3) throws IOException {
      if (var2 instanceof FloatArrayList) {
         this.writeFloatList_Internal(var1, (FloatArrayList)var2, var3);
      } else {
         this.writeFloatList_Internal(var1, var2, var3);
      }
   }

   private final void writeFloatList_Internal(int var1, List<Float> var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 4);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeFixed32(Float.floatToRawIntBits((Float)var2.get(var5)));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeFloat(var1, (Float)var2.get(var6));
         }
      }
   }

   private final void writeFloatList_Internal(int var1, FloatArrayList var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 4);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeFixed32(Float.floatToRawIntBits(var2.getFloat(var5)));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeFloat(var1, var2.getFloat(var6));
         }
      }
   }

   @Override
   public final void writeDoubleList(int var1, List<Double> var2, boolean var3) throws IOException {
      if (var2 instanceof DoubleArrayList) {
         this.writeDoubleList_Internal(var1, (DoubleArrayList)var2, var3);
      } else {
         this.writeDoubleList_Internal(var1, var2, var3);
      }
   }

   private final void writeDoubleList_Internal(int var1, List<Double> var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 8);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeFixed64(Double.doubleToRawLongBits((Double)var2.get(var5)));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeDouble(var1, (Double)var2.get(var6));
         }
      }
   }

   private final void writeDoubleList_Internal(int var1, DoubleArrayList var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 8);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeFixed64(Double.doubleToRawLongBits(var2.getDouble(var5)));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeDouble(var1, var2.getDouble(var6));
         }
      }
   }

   @Override
   public final void writeEnumList(int var1, List<Integer> var2, boolean var3) throws IOException {
      this.writeInt32List(var1, var2, var3);
   }

   @Override
   public final void writeBoolList(int var1, List<Boolean> var2, boolean var3) throws IOException {
      if (var2 instanceof BooleanArrayList) {
         this.writeBoolList_Internal(var1, (BooleanArrayList)var2, var3);
      } else {
         this.writeBoolList_Internal(var1, var2, var3);
      }
   }

   private final void writeBoolList_Internal(int var1, List<Boolean> var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size());
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeBool((Boolean)var2.get(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeBool(var1, (Boolean)var2.get(var6));
         }
      }
   }

   private final void writeBoolList_Internal(int var1, BooleanArrayList var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size());
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeBool(var2.getBoolean(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeBool(var1, var2.getBoolean(var6));
         }
      }
   }

   @Override
   public final void writeStringList(int var1, List<String> var2) throws IOException {
      if (var2 instanceof LazyStringList) {
         LazyStringList var3 = (LazyStringList)var2;

         for (int var4 = var2.size() - 1; var4 >= 0; var4--) {
            this.writeLazyString(var1, var3.getRaw(var4));
         }
      } else {
         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeString(var1, (String)var2.get(var5));
         }
      }
   }

   private void writeLazyString(int var1, Object var2) throws IOException {
      if (var2 instanceof String) {
         this.writeString(var1, (String)var2);
      } else {
         this.writeBytes(var1, (ByteString)var2);
      }
   }

   @Override
   public final void writeBytesList(int var1, List<ByteString> var2) throws IOException {
      for (int var3 = var2.size() - 1; var3 >= 0; var3--) {
         this.writeBytes(var1, (ByteString)var2.get(var3));
      }
   }

   @Override
   public final void writeUInt32List(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var2 instanceof IntArrayList) {
         this.writeUInt32List_Internal(var1, (IntArrayList)var2, var3);
      } else {
         this.writeUInt32List_Internal(var1, var2, var3);
      }
   }

   private final void writeUInt32List_Internal(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 5);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeVarint32((Integer)var2.get(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeUInt32(var1, (Integer)var2.get(var6));
         }
      }
   }

   private final void writeUInt32List_Internal(int var1, IntArrayList var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 5);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeVarint32(var2.getInt(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeUInt32(var1, var2.getInt(var6));
         }
      }
   }

   @Override
   public final void writeSFixed32List(int var1, List<Integer> var2, boolean var3) throws IOException {
      this.writeFixed32List(var1, var2, var3);
   }

   @Override
   public final void writeSFixed64List(int var1, List<Long> var2, boolean var3) throws IOException {
      this.writeFixed64List(var1, var2, var3);
   }

   @Override
   public final void writeSInt32List(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var2 instanceof IntArrayList) {
         this.writeSInt32List_Internal(var1, (IntArrayList)var2, var3);
      } else {
         this.writeSInt32List_Internal(var1, var2, var3);
      }
   }

   private final void writeSInt32List_Internal(int var1, List<Integer> var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 5);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeSInt32((Integer)var2.get(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeSInt32(var1, (Integer)var2.get(var6));
         }
      }
   }

   private final void writeSInt32List_Internal(int var1, IntArrayList var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 5);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeSInt32(var2.getInt(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeSInt32(var1, var2.getInt(var6));
         }
      }
   }

   @Override
   public final void writeSInt64List(int var1, List<Long> var2, boolean var3) throws IOException {
      if (var2 instanceof LongArrayList) {
         this.writeSInt64List_Internal(var1, (LongArrayList)var2, var3);
      } else {
         this.writeSInt64List_Internal(var1, var2, var3);
      }
   }

   @Override
   public <K, V> void writeMap(int var1, MapEntryLite.Metadata<K, V> var2, Map<K, V> var3) throws IOException {
      for (Entry var5 : var3.entrySet()) {
         int var6 = this.getTotalBytesWritten();
         writeMapEntryField(this, 2, var2.valueType, var5.getValue());
         writeMapEntryField(this, 1, var2.keyType, var5.getKey());
         int var7 = this.getTotalBytesWritten() - var6;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      }
   }

   static final void writeMapEntryField(Writer var0, int var1, WireFormat.FieldType var2, Object var3) throws IOException {
      switch (var2) {
         case BOOL:
            var0.writeBool(var1, (Boolean)var3);
            break;
         case FIXED32:
            var0.writeFixed32(var1, (Integer)var3);
            break;
         case FIXED64:
            var0.writeFixed64(var1, (Long)var3);
            break;
         case INT32:
            var0.writeInt32(var1, (Integer)var3);
            break;
         case INT64:
            var0.writeInt64(var1, (Long)var3);
            break;
         case SFIXED32:
            var0.writeSFixed32(var1, (Integer)var3);
            break;
         case SFIXED64:
            var0.writeSFixed64(var1, (Long)var3);
            break;
         case SINT32:
            var0.writeSInt32(var1, (Integer)var3);
            break;
         case SINT64:
            var0.writeSInt64(var1, (Long)var3);
            break;
         case STRING:
            var0.writeString(var1, (String)var3);
            break;
         case UINT32:
            var0.writeUInt32(var1, (Integer)var3);
            break;
         case UINT64:
            var0.writeUInt64(var1, (Long)var3);
            break;
         case FLOAT:
            var0.writeFloat(var1, (Float)var3);
            break;
         case DOUBLE:
            var0.writeDouble(var1, (Double)var3);
            break;
         case MESSAGE:
            var0.writeMessage(var1, var3);
            break;
         case BYTES:
            var0.writeBytes(var1, (ByteString)var3);
            break;
         case ENUM:
            if (var3 instanceof Internal.EnumLite) {
               var0.writeEnum(var1, ((Internal.EnumLite)var3).getNumber());
            } else {
               if (!(var3 instanceof Integer)) {
                  throw new IllegalArgumentException("Unexpected type for enum in map.");
               }

               var0.writeEnum(var1, (Integer)var3);
            }
            break;
         default:
            throw new IllegalArgumentException("Unsupported map value type for: " + var2);
      }
   }

   private final void writeSInt64List_Internal(int var1, List<Long> var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 10);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeSInt64((Long)var2.get(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeSInt64(var1, (Long)var2.get(var6));
         }
      }
   }

   private final void writeSInt64List_Internal(int var1, LongArrayList var2, boolean var3) throws IOException {
      if (var3) {
         this.requireSpace(10 + var2.size() * 10);
         int var4 = this.getTotalBytesWritten();

         for (int var5 = var2.size() - 1; var5 >= 0; var5--) {
            this.writeSInt64(var2.getLong(var5));
         }

         int var7 = this.getTotalBytesWritten() - var4;
         this.writeVarint32(var7);
         this.writeTag(var1, 2);
      } else {
         for (int var6 = var2.size() - 1; var6 >= 0; var6--) {
            this.writeSInt64(var1, var2.getLong(var6));
         }
      }
   }

   @Override
   public final void writeMessageList(int var1, List<?> var2) throws IOException {
      for (int var3 = var2.size() - 1; var3 >= 0; var3--) {
         this.writeMessage(var1, var2.get(var3));
      }
   }

   @Override
   public final void writeMessageList(int var1, List<?> var2, Schema var3) throws IOException {
      for (int var4 = var2.size() - 1; var4 >= 0; var4--) {
         this.writeMessage(var1, var2.get(var4), var3);
      }
   }

   @Override
   public final void writeGroupList(int var1, List<?> var2) throws IOException {
      for (int var3 = var2.size() - 1; var3 >= 0; var3--) {
         this.writeGroup(var1, var2.get(var3));
      }
   }

   @Override
   public final void writeGroupList(int var1, List<?> var2, Schema var3) throws IOException {
      for (int var4 = var2.size() - 1; var4 >= 0; var4--) {
         this.writeGroup(var1, var2.get(var4), var3);
      }
   }

   @Override
   public final void writeMessageSetItem(int var1, Object var2) throws IOException {
      this.writeTag(1, 4);
      if (var2 instanceof ByteString) {
         this.writeBytes(3, (ByteString)var2);
      } else {
         this.writeMessage(3, var2);
      }

      this.writeUInt32(2, var1);
      this.writeTag(1, 3);
   }

   final AllocatedBuffer newHeapBuffer() {
      return this.alloc.allocateHeapBuffer(this.chunkSize);
   }

   final AllocatedBuffer newHeapBuffer(int var1) {
      return this.alloc.allocateHeapBuffer(Math.max(var1, this.chunkSize));
   }

   final AllocatedBuffer newDirectBuffer() {
      return this.alloc.allocateDirectBuffer(this.chunkSize);
   }

   final AllocatedBuffer newDirectBuffer(int var1) {
      return this.alloc.allocateDirectBuffer(Math.max(var1, this.chunkSize));
   }

   public abstract int getTotalBytesWritten();

   abstract void requireSpace(int var1);

   abstract void finishCurrentBuffer();

   abstract void writeTag(int var1, int var2);

   abstract void writeVarint32(int var1);

   abstract void writeInt32(int var1);

   abstract void writeSInt32(int var1);

   abstract void writeFixed32(int var1);

   abstract void writeVarint64(long var1);

   abstract void writeSInt64(long var1);

   abstract void writeFixed64(long var1);

   abstract void writeBool(boolean var1);

   abstract void writeString(String var1);

   private static byte computeUInt64SizeNoTag(long var0) {
      if ((var0 & -128L) == 0L) {
         return 1;
      } else if (var0 < 0L) {
         return 10;
      } else {
         byte var2 = 2;
         if ((var0 & -34359738368L) != 0L) {
            var2 = (byte)(var2 + 4);
            var0 >>>= 28;
         }

         if ((var0 & -2097152L) != 0L) {
            var2 = (byte)(var2 + 2);
            var0 >>>= 14;
         }

         if ((var0 & -16384L) != 0L) {
            var2++;
         }

         return var2;
      }
   }

   private static final class SafeDirectWriter extends BinaryWriter {
      private ByteBuffer buffer;
      private int limitMinusOne;
      private int pos;

      SafeDirectWriter(BufferAllocator var1, int var2) {
         super(var1, var2);
         this.nextBuffer();
      }

      private void nextBuffer() {
         this.nextBuffer(this.newDirectBuffer());
      }

      private void nextBuffer(int var1) {
         this.nextBuffer(this.newDirectBuffer(var1));
      }

      private void nextBuffer(AllocatedBuffer var1) {
         if (!var1.hasNioBuffer()) {
            throw new RuntimeException("Allocated buffer does not have NIO buffer");
         } else {
            ByteBuffer var2 = var1.nioBuffer();
            if (!var2.isDirect()) {
               throw new RuntimeException("Allocator returned non-direct buffer");
            } else {
               this.finishCurrentBuffer();
               this.buffers.addFirst(var1);
               this.buffer = var2;
               ((Buffer)this.buffer).limit(this.buffer.capacity());
               ((Buffer)this.buffer).position(0);
               this.buffer.order(ByteOrder.LITTLE_ENDIAN);
               this.limitMinusOne = this.buffer.limit() - 1;
               this.pos = this.limitMinusOne;
            }
         }
      }

      @Override
      public int getTotalBytesWritten() {
         return this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
      }

      private int bytesWrittenToCurrentBuffer() {
         return this.limitMinusOne - this.pos;
      }

      private int spaceLeft() {
         return this.pos + 1;
      }

      @Override
      void finishCurrentBuffer() {
         if (this.buffer != null) {
            this.totalDoneBytes = this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
            ((Buffer)this.buffer).position(this.pos + 1);
            this.buffer = null;
            this.pos = 0;
            this.limitMinusOne = 0;
         }
      }

      @Override
      public void writeUInt32(int var1, int var2) {
         this.requireSpace(10);
         this.writeVarint32(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeInt32(int var1, int var2) {
         this.requireSpace(15);
         this.writeInt32(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeSInt32(int var1, int var2) {
         this.requireSpace(10);
         this.writeSInt32(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeFixed32(int var1, int var2) {
         this.requireSpace(9);
         this.writeFixed32(var2);
         this.writeTag(var1, 5);
      }

      @Override
      public void writeUInt64(int var1, long var2) {
         this.requireSpace(15);
         this.writeVarint64(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeSInt64(int var1, long var2) {
         this.requireSpace(15);
         this.writeSInt64(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeFixed64(int var1, long var2) {
         this.requireSpace(13);
         this.writeFixed64(var2);
         this.writeTag(var1, 1);
      }

      @Override
      public void writeBool(int var1, boolean var2) {
         this.requireSpace(6);
         this.write((byte)(var2 ? 1 : 0));
         this.writeTag(var1, 0);
      }

      @Override
      public void writeString(int var1, String var2) {
         int var3 = this.getTotalBytesWritten();
         this.writeString(var2);
         int var4 = this.getTotalBytesWritten() - var3;
         this.requireSpace(10);
         this.writeVarint32(var4);
         this.writeTag(var1, 2);
      }

      @Override
      public void writeBytes(int var1, ByteString var2) {
         try {
            var2.writeToReverse(this);
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }

         this.requireSpace(10);
         this.writeVarint32(var2.size());
         this.writeTag(var1, 2);
      }

      @Override
      public void writeMessage(int var1, Object var2) throws IOException {
         int var3 = this.getTotalBytesWritten();
         Protobuf.getInstance().writeTo(var2, this);
         int var4 = this.getTotalBytesWritten() - var3;
         this.requireSpace(10);
         this.writeVarint32(var4);
         this.writeTag(var1, 2);
      }

      @Override
      public void writeMessage(int var1, Object var2, Schema var3) throws IOException {
         int var4 = this.getTotalBytesWritten();
         var3.writeTo(var2, this);
         int var5 = this.getTotalBytesWritten() - var4;
         this.requireSpace(10);
         this.writeVarint32(var5);
         this.writeTag(var1, 2);
      }

      @Override
      public void writeGroup(int var1, Object var2) throws IOException {
         this.writeTag(var1, 4);
         Protobuf.getInstance().writeTo(var2, this);
         this.writeTag(var1, 3);
      }

      @Override
      public void writeGroup(int var1, Object var2, Schema var3) throws IOException {
         this.writeTag(var1, 4);
         var3.writeTo(var2, this);
         this.writeTag(var1, 3);
      }

      @Override
      public void writeStartGroup(int var1) {
         this.writeTag(var1, 3);
      }

      @Override
      public void writeEndGroup(int var1) {
         this.writeTag(var1, 4);
      }

      @Override
      void writeInt32(int var1) {
         if (var1 >= 0) {
            this.writeVarint32(var1);
         } else {
            this.writeVarint64((long)var1);
         }
      }

      @Override
      void writeSInt32(int var1) {
         this.writeVarint32(CodedOutputStream.encodeZigZag32(var1));
      }

      @Override
      void writeSInt64(long var1) {
         this.writeVarint64(CodedOutputStream.encodeZigZag64(var1));
      }

      @Override
      void writeBool(boolean var1) {
         this.write((byte)(var1 ? 1 : 0));
      }

      @Override
      void writeTag(int var1, int var2) {
         this.writeVarint32(WireFormat.makeTag(var1, var2));
      }

      @Override
      void writeVarint32(int var1) {
         if ((var1 & -128) == 0) {
            this.writeVarint32OneByte(var1);
         } else if ((var1 & -16384) == 0) {
            this.writeVarint32TwoBytes(var1);
         } else if ((var1 & -2097152) == 0) {
            this.writeVarint32ThreeBytes(var1);
         } else if ((var1 & -268435456) == 0) {
            this.writeVarint32FourBytes(var1);
         } else {
            this.writeVarint32FiveBytes(var1);
         }
      }

      private void writeVarint32OneByte(int var1) {
         this.buffer.put(this.pos--, (byte)var1);
      }

      private void writeVarint32TwoBytes(int var1) {
         this.pos -= 2;
         this.buffer.putShort(this.pos + 1, (short)((var1 & 16256) << 1 | var1 & 127 | 128));
      }

      private void writeVarint32ThreeBytes(int var1) {
         this.pos -= 3;
         this.buffer.putInt(this.pos, (var1 & 2080768) << 10 | (var1 & 16256 | 16384) << 9 | (var1 & 127 | 128) << 8);
      }

      private void writeVarint32FourBytes(int var1) {
         this.pos -= 4;
         this.buffer.putInt(this.pos + 1, (var1 & 266338304) << 3 | (var1 & 2080768 | 2097152) << 2 | (var1 & 16256 | 16384) << 1 | var1 & 127 | 128);
      }

      private void writeVarint32FiveBytes(int var1) {
         this.buffer.put(this.pos--, (byte)(var1 >>> 28));
         this.pos -= 4;
         this.buffer.putInt(this.pos + 1, (var1 >>> 21 & 127 | 128) << 24 | (var1 >>> 14 & 127 | 128) << 16 | (var1 >>> 7 & 127 | 128) << 8 | var1 & 127 | 128);
      }

      @Override
      void writeVarint64(long var1) {
         switch (BinaryWriter.computeUInt64SizeNoTag(var1)) {
            case 1:
               this.writeVarint64OneByte(var1);
               break;
            case 2:
               this.writeVarint64TwoBytes(var1);
               break;
            case 3:
               this.writeVarint64ThreeBytes(var1);
               break;
            case 4:
               this.writeVarint64FourBytes(var1);
               break;
            case 5:
               this.writeVarint64FiveBytes(var1);
               break;
            case 6:
               this.writeVarint64SixBytes(var1);
               break;
            case 7:
               this.writeVarint64SevenBytes(var1);
               break;
            case 8:
               this.writeVarint64EightBytes(var1);
               break;
            case 9:
               this.writeVarint64NineBytes(var1);
               break;
            case 10:
               this.writeVarint64TenBytes(var1);
         }
      }

      private void writeVarint64OneByte(long var1) {
         this.writeVarint32OneByte((int)var1);
      }

      private void writeVarint64TwoBytes(long var1) {
         this.writeVarint32TwoBytes((int)var1);
      }

      private void writeVarint64ThreeBytes(long var1) {
         this.writeVarint32ThreeBytes((int)var1);
      }

      private void writeVarint64FourBytes(long var1) {
         this.writeVarint32FourBytes((int)var1);
      }

      private void writeVarint64FiveBytes(long var1) {
         this.pos -= 5;
         this.buffer
            .putLong(
               this.pos - 2,
               (var1 & 34091302912L) << 28
                  | (var1 & 266338304L | 268435456L) << 27
                  | (var1 & 2080768L | 2097152L) << 26
                  | (var1 & 16256L | 16384L) << 25
                  | (var1 & 127L | 128L) << 24
            );
      }

      private void writeVarint64SixBytes(long var1) {
         this.pos -= 6;
         this.buffer
            .putLong(
               this.pos - 1,
               (var1 & 4363686772736L) << 21
                  | (var1 & 34091302912L | 34359738368L) << 20
                  | (var1 & 266338304L | 268435456L) << 19
                  | (var1 & 2080768L | 2097152L) << 18
                  | (var1 & 16256L | 16384L) << 17
                  | (var1 & 127L | 128L) << 16
            );
      }

      private void writeVarint64SevenBytes(long var1) {
         this.pos -= 7;
         this.buffer
            .putLong(
               this.pos,
               (var1 & 558551906910208L) << 14
                  | (var1 & 4363686772736L | 4398046511104L) << 13
                  | (var1 & 34091302912L | 34359738368L) << 12
                  | (var1 & 266338304L | 268435456L) << 11
                  | (var1 & 2080768L | 2097152L) << 10
                  | (var1 & 16256L | 16384L) << 9
                  | (var1 & 127L | 128L) << 8
            );
      }

      private void writeVarint64EightBytes(long var1) {
         this.pos -= 8;
         this.buffer
            .putLong(
               this.pos + 1,
               (var1 & 71494644084506624L) << 7
                  | (var1 & 558551906910208L | 562949953421312L) << 6
                  | (var1 & 4363686772736L | 4398046511104L) << 5
                  | (var1 & 34091302912L | 34359738368L) << 4
                  | (var1 & 266338304L | 268435456L) << 3
                  | (var1 & 2080768L | 2097152L) << 2
                  | (var1 & 16256L | 16384L) << 1
                  | var1 & 127L
                  | 128L
            );
      }

      private void writeVarint64EightBytesWithSign(long var1) {
         this.pos -= 8;
         this.buffer
            .putLong(
               this.pos + 1,
               (var1 & 71494644084506624L | 72057594037927936L) << 7
                  | (var1 & 558551906910208L | 562949953421312L) << 6
                  | (var1 & 4363686772736L | 4398046511104L) << 5
                  | (var1 & 34091302912L | 34359738368L) << 4
                  | (var1 & 266338304L | 268435456L) << 3
                  | (var1 & 2080768L | 2097152L) << 2
                  | (var1 & 16256L | 16384L) << 1
                  | var1 & 127L
                  | 128L
            );
      }

      private void writeVarint64NineBytes(long var1) {
         this.buffer.put(this.pos--, (byte)((int)(var1 >>> 56)));
         this.writeVarint64EightBytesWithSign(var1 & 72057594037927935L);
      }

      private void writeVarint64TenBytes(long var1) {
         this.buffer.put(this.pos--, (byte)((int)(var1 >>> 63)));
         this.buffer.put(this.pos--, (byte)((int)(var1 >>> 56 & 127L | 128L)));
         this.writeVarint64EightBytesWithSign(var1 & 72057594037927935L);
      }

      @Override
      void writeFixed32(int var1) {
         this.pos -= 4;
         this.buffer.putInt(this.pos + 1, var1);
      }

      @Override
      void writeFixed64(long var1) {
         this.pos -= 8;
         this.buffer.putLong(this.pos + 1, var1);
      }

      @Override
      void writeString(String var1) {
         this.requireSpace(var1.length());
         int var2 = var1.length() - 1;

         char var3;
         for (this.pos -= var2; var2 >= 0 && (var3 = var1.charAt(var2)) < 128; var2--) {
            this.buffer.put(this.pos + var2, (byte)var3);
         }

         if (var2 == -1) {
            this.pos--;
         } else {
            for (this.pos += var2; var2 >= 0; var2--) {
               var3 = var1.charAt(var2);
               if (var3 < 128 && this.pos >= 0) {
                  this.buffer.put(this.pos--, (byte)var3);
               } else if (var3 < 2048 && this.pos > 0) {
                  this.buffer.put(this.pos--, (byte)(128 | '?' & var3));
                  this.buffer.put(this.pos--, (byte)(960 | var3 >>> 6));
               } else if ((var3 < '\ud800' || '\udfff' < var3) && this.pos > 1) {
                  this.buffer.put(this.pos--, (byte)(128 | '?' & var3));
                  this.buffer.put(this.pos--, (byte)(128 | 63 & var3 >>> 6));
                  this.buffer.put(this.pos--, (byte)(480 | var3 >>> '\f'));
               } else if (this.pos > 2) {
                  char var4 = '\u0000';
                  if (var2 == 0 || !Character.isSurrogatePair(var4 = var1.charAt(var2 - 1), var3)) {
                     throw new Utf8.UnpairedSurrogateException(var2 - 1, var2);
                  }

                  var2--;
                  int var5 = Character.toCodePoint(var4, var3);
                  this.buffer.put(this.pos--, (byte)(128 | 63 & var5));
                  this.buffer.put(this.pos--, (byte)(128 | 63 & var5 >>> 6));
                  this.buffer.put(this.pos--, (byte)(128 | 63 & var5 >>> 12));
                  this.buffer.put(this.pos--, (byte)(240 | var5 >>> 18));
               } else {
                  this.requireSpace(var2);
                  var2++;
               }
            }
         }
      }

      @Override
      public void write(byte var1) {
         this.buffer.put(this.pos--, var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) {
         if (this.spaceLeft() < var3) {
            this.nextBuffer(var3);
         }

         this.pos -= var3;
         ((Buffer)this.buffer).position(this.pos + 1);
         this.buffer.put(var1, var2, var3);
      }

      @Override
      public void writeLazy(byte[] var1, int var2, int var3) {
         if (this.spaceLeft() < var3) {
            this.totalDoneBytes += var3;
            this.buffers.addFirst(AllocatedBuffer.wrap(var1, var2, var3));
            this.nextBuffer();
         } else {
            this.pos -= var3;
            ((Buffer)this.buffer).position(this.pos + 1);
            this.buffer.put(var1, var2, var3);
         }
      }

      @Override
      public void write(ByteBuffer var1) {
         int var2 = var1.remaining();
         if (this.spaceLeft() < var2) {
            this.nextBuffer(var2);
         }

         this.pos -= var2;
         ((Buffer)this.buffer).position(this.pos + 1);
         this.buffer.put(var1);
      }

      @Override
      public void writeLazy(ByteBuffer var1) {
         int var2 = var1.remaining();
         if (this.spaceLeft() < var2) {
            this.totalDoneBytes += var2;
            this.buffers.addFirst(AllocatedBuffer.wrap(var1));
            this.nextBuffer();
         } else {
            this.pos -= var2;
            ((Buffer)this.buffer).position(this.pos + 1);
            this.buffer.put(var1);
         }
      }

      @Override
      void requireSpace(int var1) {
         if (this.spaceLeft() < var1) {
            this.nextBuffer(var1);
         }
      }
   }

   private static final class SafeHeapWriter extends BinaryWriter {
      private AllocatedBuffer allocatedBuffer;
      private byte[] buffer;
      private int offset;
      private int limit;
      private int offsetMinusOne;
      private int limitMinusOne;
      private int pos;

      SafeHeapWriter(BufferAllocator var1, int var2) {
         super(var1, var2);
         this.nextBuffer();
      }

      @Override
      void finishCurrentBuffer() {
         if (this.allocatedBuffer != null) {
            this.totalDoneBytes = this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
            this.allocatedBuffer.position(this.pos - this.allocatedBuffer.arrayOffset() + 1);
            this.allocatedBuffer = null;
            this.pos = 0;
            this.limitMinusOne = 0;
         }
      }

      private void nextBuffer() {
         this.nextBuffer(this.newHeapBuffer());
      }

      private void nextBuffer(int var1) {
         this.nextBuffer(this.newHeapBuffer(var1));
      }

      private void nextBuffer(AllocatedBuffer var1) {
         if (!var1.hasArray()) {
            throw new RuntimeException("Allocator returned non-heap buffer");
         } else {
            this.finishCurrentBuffer();
            this.buffers.addFirst(var1);
            this.allocatedBuffer = var1;
            this.buffer = var1.array();
            int var2 = var1.arrayOffset();
            this.limit = var2 + var1.limit();
            this.offset = var2 + var1.position();
            this.offsetMinusOne = this.offset - 1;
            this.limitMinusOne = this.limit - 1;
            this.pos = this.limitMinusOne;
         }
      }

      @Override
      public int getTotalBytesWritten() {
         return this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
      }

      int bytesWrittenToCurrentBuffer() {
         return this.limitMinusOne - this.pos;
      }

      int spaceLeft() {
         return this.pos - this.offsetMinusOne;
      }

      @Override
      public void writeUInt32(int var1, int var2) throws IOException {
         this.requireSpace(10);
         this.writeVarint32(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeInt32(int var1, int var2) throws IOException {
         this.requireSpace(15);
         this.writeInt32(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeSInt32(int var1, int var2) throws IOException {
         this.requireSpace(10);
         this.writeSInt32(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeFixed32(int var1, int var2) throws IOException {
         this.requireSpace(9);
         this.writeFixed32(var2);
         this.writeTag(var1, 5);
      }

      @Override
      public void writeUInt64(int var1, long var2) throws IOException {
         this.requireSpace(15);
         this.writeVarint64(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeSInt64(int var1, long var2) throws IOException {
         this.requireSpace(15);
         this.writeSInt64(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeFixed64(int var1, long var2) throws IOException {
         this.requireSpace(13);
         this.writeFixed64(var2);
         this.writeTag(var1, 1);
      }

      @Override
      public void writeBool(int var1, boolean var2) throws IOException {
         this.requireSpace(6);
         this.write((byte)(var2 ? 1 : 0));
         this.writeTag(var1, 0);
      }

      @Override
      public void writeString(int var1, String var2) throws IOException {
         int var3 = this.getTotalBytesWritten();
         this.writeString(var2);
         int var4 = this.getTotalBytesWritten() - var3;
         this.requireSpace(10);
         this.writeVarint32(var4);
         this.writeTag(var1, 2);
      }

      @Override
      public void writeBytes(int var1, ByteString var2) throws IOException {
         try {
            var2.writeToReverse(this);
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }

         this.requireSpace(10);
         this.writeVarint32(var2.size());
         this.writeTag(var1, 2);
      }

      @Override
      public void writeMessage(int var1, Object var2) throws IOException {
         int var3 = this.getTotalBytesWritten();
         Protobuf.getInstance().writeTo(var2, this);
         int var4 = this.getTotalBytesWritten() - var3;
         this.requireSpace(10);
         this.writeVarint32(var4);
         this.writeTag(var1, 2);
      }

      @Override
      public void writeMessage(int var1, Object var2, Schema var3) throws IOException {
         int var4 = this.getTotalBytesWritten();
         var3.writeTo(var2, this);
         int var5 = this.getTotalBytesWritten() - var4;
         this.requireSpace(10);
         this.writeVarint32(var5);
         this.writeTag(var1, 2);
      }

      @Override
      public void writeGroup(int var1, Object var2) throws IOException {
         this.writeTag(var1, 4);
         Protobuf.getInstance().writeTo(var2, this);
         this.writeTag(var1, 3);
      }

      @Override
      public void writeGroup(int var1, Object var2, Schema var3) throws IOException {
         this.writeTag(var1, 4);
         var3.writeTo(var2, this);
         this.writeTag(var1, 3);
      }

      @Override
      public void writeStartGroup(int var1) {
         this.writeTag(var1, 3);
      }

      @Override
      public void writeEndGroup(int var1) {
         this.writeTag(var1, 4);
      }

      @Override
      void writeInt32(int var1) {
         if (var1 >= 0) {
            this.writeVarint32(var1);
         } else {
            this.writeVarint64((long)var1);
         }
      }

      @Override
      void writeSInt32(int var1) {
         this.writeVarint32(CodedOutputStream.encodeZigZag32(var1));
      }

      @Override
      void writeSInt64(long var1) {
         this.writeVarint64(CodedOutputStream.encodeZigZag64(var1));
      }

      @Override
      void writeBool(boolean var1) {
         this.write((byte)(var1 ? 1 : 0));
      }

      @Override
      void writeTag(int var1, int var2) {
         this.writeVarint32(WireFormat.makeTag(var1, var2));
      }

      @Override
      void writeVarint32(int var1) {
         if ((var1 & -128) == 0) {
            this.writeVarint32OneByte(var1);
         } else if ((var1 & -16384) == 0) {
            this.writeVarint32TwoBytes(var1);
         } else if ((var1 & -2097152) == 0) {
            this.writeVarint32ThreeBytes(var1);
         } else if ((var1 & -268435456) == 0) {
            this.writeVarint32FourBytes(var1);
         } else {
            this.writeVarint32FiveBytes(var1);
         }
      }

      private void writeVarint32OneByte(int var1) {
         this.buffer[this.pos--] = (byte)var1;
      }

      private void writeVarint32TwoBytes(int var1) {
         this.buffer[this.pos--] = (byte)(var1 >>> 7);
         this.buffer[this.pos--] = (byte)(var1 & 127 | 128);
      }

      private void writeVarint32ThreeBytes(int var1) {
         this.buffer[this.pos--] = (byte)(var1 >>> 14);
         this.buffer[this.pos--] = (byte)(var1 >>> 7 & 127 | 128);
         this.buffer[this.pos--] = (byte)(var1 & 127 | 128);
      }

      private void writeVarint32FourBytes(int var1) {
         this.buffer[this.pos--] = (byte)(var1 >>> 21);
         this.buffer[this.pos--] = (byte)(var1 >>> 14 & 127 | 128);
         this.buffer[this.pos--] = (byte)(var1 >>> 7 & 127 | 128);
         this.buffer[this.pos--] = (byte)(var1 & 127 | 128);
      }

      private void writeVarint32FiveBytes(int var1) {
         this.buffer[this.pos--] = (byte)(var1 >>> 28);
         this.buffer[this.pos--] = (byte)(var1 >>> 21 & 127 | 128);
         this.buffer[this.pos--] = (byte)(var1 >>> 14 & 127 | 128);
         this.buffer[this.pos--] = (byte)(var1 >>> 7 & 127 | 128);
         this.buffer[this.pos--] = (byte)(var1 & 127 | 128);
      }

      @Override
      void writeVarint64(long var1) {
         switch (BinaryWriter.computeUInt64SizeNoTag(var1)) {
            case 1:
               this.writeVarint64OneByte(var1);
               break;
            case 2:
               this.writeVarint64TwoBytes(var1);
               break;
            case 3:
               this.writeVarint64ThreeBytes(var1);
               break;
            case 4:
               this.writeVarint64FourBytes(var1);
               break;
            case 5:
               this.writeVarint64FiveBytes(var1);
               break;
            case 6:
               this.writeVarint64SixBytes(var1);
               break;
            case 7:
               this.writeVarint64SevenBytes(var1);
               break;
            case 8:
               this.writeVarint64EightBytes(var1);
               break;
            case 9:
               this.writeVarint64NineBytes(var1);
               break;
            case 10:
               this.writeVarint64TenBytes(var1);
         }
      }

      private void writeVarint64OneByte(long var1) {
         this.buffer[this.pos--] = (byte)((int)var1);
      }

      private void writeVarint64TwoBytes(long var1) {
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 7));
         this.buffer[this.pos--] = (byte)((int)var1 & 127 | 128);
      }

      private void writeVarint64ThreeBytes(long var1) {
         this.buffer[this.pos--] = (byte)((int)var1 >>> 14);
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 & 127L | 128L));
      }

      private void writeVarint64FourBytes(long var1) {
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 21));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 & 127L | 128L));
      }

      private void writeVarint64FiveBytes(long var1) {
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 28));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 21 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 & 127L | 128L));
      }

      private void writeVarint64SixBytes(long var1) {
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 35));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 28 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 21 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 & 127L | 128L));
      }

      private void writeVarint64SevenBytes(long var1) {
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 42));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 35 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 28 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 21 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 & 127L | 128L));
      }

      private void writeVarint64EightBytes(long var1) {
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 49));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 42 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 35 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 28 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 21 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 & 127L | 128L));
      }

      private void writeVarint64NineBytes(long var1) {
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 56));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 49 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 42 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 35 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 28 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 21 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 & 127L | 128L));
      }

      private void writeVarint64TenBytes(long var1) {
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 63));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 56 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 49 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 42 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 35 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 28 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 21 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 14 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 >>> 7 & 127L | 128L));
         this.buffer[this.pos--] = (byte)((int)(var1 & 127L | 128L));
      }

      @Override
      void writeFixed32(int var1) {
         this.buffer[this.pos--] = (byte)(var1 >> 24 & 0xFF);
         this.buffer[this.pos--] = (byte)(var1 >> 16 & 0xFF);
         this.buffer[this.pos--] = (byte)(var1 >> 8 & 0xFF);
         this.buffer[this.pos--] = (byte)(var1 & 0xFF);
      }

      @Override
      void writeFixed64(long var1) {
         this.buffer[this.pos--] = (byte)((int)(var1 >> 56) & 0xFF);
         this.buffer[this.pos--] = (byte)((int)(var1 >> 48) & 0xFF);
         this.buffer[this.pos--] = (byte)((int)(var1 >> 40) & 0xFF);
         this.buffer[this.pos--] = (byte)((int)(var1 >> 32) & 0xFF);
         this.buffer[this.pos--] = (byte)((int)(var1 >> 24) & 0xFF);
         this.buffer[this.pos--] = (byte)((int)(var1 >> 16) & 0xFF);
         this.buffer[this.pos--] = (byte)((int)(var1 >> 8) & 0xFF);
         this.buffer[this.pos--] = (byte)((int)var1 & 0xFF);
      }

      @Override
      void writeString(String var1) {
         this.requireSpace(var1.length());
         int var2 = var1.length() - 1;

         char var3;
         for (this.pos -= var2; var2 >= 0 && (var3 = var1.charAt(var2)) < 128; var2--) {
            this.buffer[this.pos + var2] = (byte)var3;
         }

         if (var2 == -1) {
            this.pos--;
         } else {
            for (this.pos += var2; var2 >= 0; var2--) {
               var3 = var1.charAt(var2);
               if (var3 < 128 && this.pos > this.offsetMinusOne) {
                  this.buffer[this.pos--] = (byte)var3;
               } else if (var3 < 2048 && this.pos > this.offset) {
                  this.buffer[this.pos--] = (byte)(128 | '?' & var3);
                  this.buffer[this.pos--] = (byte)(960 | var3 >>> 6);
               } else if ((var3 < '\ud800' || '\udfff' < var3) && this.pos > this.offset + 1) {
                  this.buffer[this.pos--] = (byte)(128 | '?' & var3);
                  this.buffer[this.pos--] = (byte)(128 | 63 & var3 >>> 6);
                  this.buffer[this.pos--] = (byte)(480 | var3 >>> '\f');
               } else if (this.pos > this.offset + 2) {
                  char var4 = '\u0000';
                  if (var2 == 0 || !Character.isSurrogatePair(var4 = var1.charAt(var2 - 1), var3)) {
                     throw new Utf8.UnpairedSurrogateException(var2 - 1, var2);
                  }

                  var2--;
                  int var5 = Character.toCodePoint(var4, var3);
                  this.buffer[this.pos--] = (byte)(128 | 63 & var5);
                  this.buffer[this.pos--] = (byte)(128 | 63 & var5 >>> 6);
                  this.buffer[this.pos--] = (byte)(128 | 63 & var5 >>> 12);
                  this.buffer[this.pos--] = (byte)(240 | var5 >>> 18);
               } else {
                  this.requireSpace(var2);
                  var2++;
               }
            }
         }
      }

      @Override
      public void write(byte var1) {
         this.buffer[this.pos--] = var1;
      }

      @Override
      public void write(byte[] var1, int var2, int var3) {
         if (this.spaceLeft() < var3) {
            this.nextBuffer(var3);
         }

         this.pos -= var3;
         System.arraycopy(var1, var2, this.buffer, this.pos + 1, var3);
      }

      @Override
      public void writeLazy(byte[] var1, int var2, int var3) {
         if (this.spaceLeft() < var3) {
            this.totalDoneBytes += var3;
            this.buffers.addFirst(AllocatedBuffer.wrap(var1, var2, var3));
            this.nextBuffer();
         } else {
            this.pos -= var3;
            System.arraycopy(var1, var2, this.buffer, this.pos + 1, var3);
         }
      }

      @Override
      public void write(ByteBuffer var1) {
         int var2 = var1.remaining();
         if (this.spaceLeft() < var2) {
            this.nextBuffer(var2);
         }

         this.pos -= var2;
         var1.get(this.buffer, this.pos + 1, var2);
      }

      @Override
      public void writeLazy(ByteBuffer var1) {
         int var2 = var1.remaining();
         if (this.spaceLeft() < var2) {
            this.totalDoneBytes += var2;
            this.buffers.addFirst(AllocatedBuffer.wrap(var1));
            this.nextBuffer();
         }

         this.pos -= var2;
         var1.get(this.buffer, this.pos + 1, var2);
      }

      @Override
      void requireSpace(int var1) {
         if (this.spaceLeft() < var1) {
            this.nextBuffer(var1);
         }
      }
   }

   private static final class UnsafeDirectWriter extends BinaryWriter {
      private ByteBuffer buffer;
      private long bufferOffset;
      private long limitMinusOne;
      private long pos;

      UnsafeDirectWriter(BufferAllocator var1, int var2) {
         super(var1, var2);
         this.nextBuffer();
      }

      private static boolean isSupported() {
         return UnsafeUtil.hasUnsafeByteBufferOperations();
      }

      private void nextBuffer() {
         this.nextBuffer(this.newDirectBuffer());
      }

      private void nextBuffer(int var1) {
         this.nextBuffer(this.newDirectBuffer(var1));
      }

      private void nextBuffer(AllocatedBuffer var1) {
         if (!var1.hasNioBuffer()) {
            throw new RuntimeException("Allocated buffer does not have NIO buffer");
         } else {
            ByteBuffer var2 = var1.nioBuffer();
            if (!var2.isDirect()) {
               throw new RuntimeException("Allocator returned non-direct buffer");
            } else {
               this.finishCurrentBuffer();
               this.buffers.addFirst(var1);
               this.buffer = var2;
               ((Buffer)this.buffer).limit(this.buffer.capacity());
               ((Buffer)this.buffer).position(0);
               this.bufferOffset = UnsafeUtil.addressOffset(this.buffer);
               this.limitMinusOne = this.bufferOffset + (long)(this.buffer.limit() - 1);
               this.pos = this.limitMinusOne;
            }
         }
      }

      @Override
      public int getTotalBytesWritten() {
         return this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
      }

      private int bytesWrittenToCurrentBuffer() {
         return (int)(this.limitMinusOne - this.pos);
      }

      private int spaceLeft() {
         return this.bufferPos() + 1;
      }

      @Override
      void finishCurrentBuffer() {
         if (this.buffer != null) {
            this.totalDoneBytes = this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
            ((Buffer)this.buffer).position(this.bufferPos() + 1);
            this.buffer = null;
            this.pos = 0L;
            this.limitMinusOne = 0L;
         }
      }

      private int bufferPos() {
         return (int)(this.pos - this.bufferOffset);
      }

      @Override
      public void writeUInt32(int var1, int var2) {
         this.requireSpace(10);
         this.writeVarint32(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeInt32(int var1, int var2) {
         this.requireSpace(15);
         this.writeInt32(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeSInt32(int var1, int var2) {
         this.requireSpace(10);
         this.writeSInt32(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeFixed32(int var1, int var2) {
         this.requireSpace(9);
         this.writeFixed32(var2);
         this.writeTag(var1, 5);
      }

      @Override
      public void writeUInt64(int var1, long var2) {
         this.requireSpace(15);
         this.writeVarint64(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeSInt64(int var1, long var2) {
         this.requireSpace(15);
         this.writeSInt64(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeFixed64(int var1, long var2) {
         this.requireSpace(13);
         this.writeFixed64(var2);
         this.writeTag(var1, 1);
      }

      @Override
      public void writeBool(int var1, boolean var2) {
         this.requireSpace(6);
         this.write((byte)(var2 ? 1 : 0));
         this.writeTag(var1, 0);
      }

      @Override
      public void writeString(int var1, String var2) {
         int var3 = this.getTotalBytesWritten();
         this.writeString(var2);
         int var4 = this.getTotalBytesWritten() - var3;
         this.requireSpace(10);
         this.writeVarint32(var4);
         this.writeTag(var1, 2);
      }

      @Override
      public void writeBytes(int var1, ByteString var2) {
         try {
            var2.writeToReverse(this);
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }

         this.requireSpace(10);
         this.writeVarint32(var2.size());
         this.writeTag(var1, 2);
      }

      @Override
      public void writeMessage(int var1, Object var2) throws IOException {
         int var3 = this.getTotalBytesWritten();
         Protobuf.getInstance().writeTo(var2, this);
         int var4 = this.getTotalBytesWritten() - var3;
         this.requireSpace(10);
         this.writeVarint32(var4);
         this.writeTag(var1, 2);
      }

      @Override
      public void writeMessage(int var1, Object var2, Schema var3) throws IOException {
         int var4 = this.getTotalBytesWritten();
         var3.writeTo(var2, this);
         int var5 = this.getTotalBytesWritten() - var4;
         this.requireSpace(10);
         this.writeVarint32(var5);
         this.writeTag(var1, 2);
      }

      @Override
      public void writeGroup(int var1, Object var2) throws IOException {
         this.writeTag(var1, 4);
         Protobuf.getInstance().writeTo(var2, this);
         this.writeTag(var1, 3);
      }

      @Override
      public void writeGroup(int var1, Object var2, Schema var3) throws IOException {
         this.writeTag(var1, 4);
         var3.writeTo(var2, this);
         this.writeTag(var1, 3);
      }

      @Override
      public void writeStartGroup(int var1) {
         this.writeTag(var1, 3);
      }

      @Override
      public void writeEndGroup(int var1) {
         this.writeTag(var1, 4);
      }

      @Override
      void writeInt32(int var1) {
         if (var1 >= 0) {
            this.writeVarint32(var1);
         } else {
            this.writeVarint64((long)var1);
         }
      }

      @Override
      void writeSInt32(int var1) {
         this.writeVarint32(CodedOutputStream.encodeZigZag32(var1));
      }

      @Override
      void writeSInt64(long var1) {
         this.writeVarint64(CodedOutputStream.encodeZigZag64(var1));
      }

      @Override
      void writeBool(boolean var1) {
         this.write((byte)(var1 ? 1 : 0));
      }

      @Override
      void writeTag(int var1, int var2) {
         this.writeVarint32(WireFormat.makeTag(var1, var2));
      }

      @Override
      void writeVarint32(int var1) {
         if ((var1 & -128) == 0) {
            this.writeVarint32OneByte(var1);
         } else if ((var1 & -16384) == 0) {
            this.writeVarint32TwoBytes(var1);
         } else if ((var1 & -2097152) == 0) {
            this.writeVarint32ThreeBytes(var1);
         } else if ((var1 & -268435456) == 0) {
            this.writeVarint32FourBytes(var1);
         } else {
            this.writeVarint32FiveBytes(var1);
         }
      }

      private void writeVarint32OneByte(int var1) {
         UnsafeUtil.putByte(this.pos--, (byte)var1);
      }

      private void writeVarint32TwoBytes(int var1) {
         UnsafeUtil.putByte(this.pos--, (byte)(var1 >>> 7));
         UnsafeUtil.putByte(this.pos--, (byte)(var1 & 127 | 128));
      }

      private void writeVarint32ThreeBytes(int var1) {
         UnsafeUtil.putByte(this.pos--, (byte)(var1 >>> 14));
         UnsafeUtil.putByte(this.pos--, (byte)(var1 >>> 7 & 127 | 128));
         UnsafeUtil.putByte(this.pos--, (byte)(var1 & 127 | 128));
      }

      private void writeVarint32FourBytes(int var1) {
         UnsafeUtil.putByte(this.pos--, (byte)(var1 >>> 21));
         UnsafeUtil.putByte(this.pos--, (byte)(var1 >>> 14 & 127 | 128));
         UnsafeUtil.putByte(this.pos--, (byte)(var1 >>> 7 & 127 | 128));
         UnsafeUtil.putByte(this.pos--, (byte)(var1 & 127 | 128));
      }

      private void writeVarint32FiveBytes(int var1) {
         UnsafeUtil.putByte(this.pos--, (byte)(var1 >>> 28));
         UnsafeUtil.putByte(this.pos--, (byte)(var1 >>> 21 & 127 | 128));
         UnsafeUtil.putByte(this.pos--, (byte)(var1 >>> 14 & 127 | 128));
         UnsafeUtil.putByte(this.pos--, (byte)(var1 >>> 7 & 127 | 128));
         UnsafeUtil.putByte(this.pos--, (byte)(var1 & 127 | 128));
      }

      @Override
      void writeVarint64(long var1) {
         switch (BinaryWriter.computeUInt64SizeNoTag(var1)) {
            case 1:
               this.writeVarint64OneByte(var1);
               break;
            case 2:
               this.writeVarint64TwoBytes(var1);
               break;
            case 3:
               this.writeVarint64ThreeBytes(var1);
               break;
            case 4:
               this.writeVarint64FourBytes(var1);
               break;
            case 5:
               this.writeVarint64FiveBytes(var1);
               break;
            case 6:
               this.writeVarint64SixBytes(var1);
               break;
            case 7:
               this.writeVarint64SevenBytes(var1);
               break;
            case 8:
               this.writeVarint64EightBytes(var1);
               break;
            case 9:
               this.writeVarint64NineBytes(var1);
               break;
            case 10:
               this.writeVarint64TenBytes(var1);
         }
      }

      private void writeVarint64OneByte(long var1) {
         UnsafeUtil.putByte(this.pos--, (byte)((int)var1));
      }

      private void writeVarint64TwoBytes(long var1) {
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 7)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)var1 & 127 | 128));
      }

      private void writeVarint64ThreeBytes(long var1) {
         UnsafeUtil.putByte(this.pos--, (byte)((int)var1 >>> 14));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64FourBytes(long var1) {
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 21)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64FiveBytes(long var1) {
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 28)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64SixBytes(long var1) {
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 35)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64SevenBytes(long var1) {
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 42)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64EightBytes(long var1) {
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 49)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 42 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64NineBytes(long var1) {
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 56)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 49 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 42 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64TenBytes(long var1) {
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 63)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 56 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 49 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 42 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      @Override
      void writeFixed32(int var1) {
         UnsafeUtil.putByte(this.pos--, (byte)(var1 >> 24 & 0xFF));
         UnsafeUtil.putByte(this.pos--, (byte)(var1 >> 16 & 0xFF));
         UnsafeUtil.putByte(this.pos--, (byte)(var1 >> 8 & 0xFF));
         UnsafeUtil.putByte(this.pos--, (byte)(var1 & 0xFF));
      }

      @Override
      void writeFixed64(long var1) {
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >> 56) & 0xFF));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >> 48) & 0xFF));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >> 40) & 0xFF));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >> 32) & 0xFF));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >> 24) & 0xFF));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >> 16) & 0xFF));
         UnsafeUtil.putByte(this.pos--, (byte)((int)(var1 >> 8) & 0xFF));
         UnsafeUtil.putByte(this.pos--, (byte)((int)var1 & 0xFF));
      }

      @Override
      void writeString(String var1) {
         this.requireSpace(var1.length());

         int var2;
         char var3;
         for (var2 = var1.length() - 1; var2 >= 0 && (var3 = var1.charAt(var2)) < 128; var2--) {
            UnsafeUtil.putByte(this.pos--, (byte)var3);
         }

         if (var2 != -1) {
            for (; var2 >= 0; var2--) {
               var3 = var1.charAt(var2);
               if (var3 < 128 && this.pos >= this.bufferOffset) {
                  UnsafeUtil.putByte(this.pos--, (byte)var3);
               } else if (var3 < 2048 && this.pos > this.bufferOffset) {
                  UnsafeUtil.putByte(this.pos--, (byte)(128 | '?' & var3));
                  UnsafeUtil.putByte(this.pos--, (byte)(960 | var3 >>> 6));
               } else if ((var3 < '\ud800' || '\udfff' < var3) && this.pos > this.bufferOffset + 1L) {
                  UnsafeUtil.putByte(this.pos--, (byte)(128 | '?' & var3));
                  UnsafeUtil.putByte(this.pos--, (byte)(128 | 63 & var3 >>> 6));
                  UnsafeUtil.putByte(this.pos--, (byte)(480 | var3 >>> '\f'));
               } else if (this.pos > this.bufferOffset + 2L) {
                  char var4;
                  if (var2 == 0 || !Character.isSurrogatePair(var4 = var1.charAt(var2 - 1), var3)) {
                     throw new Utf8.UnpairedSurrogateException(var2 - 1, var2);
                  }

                  var2--;
                  int var5 = Character.toCodePoint(var4, var3);
                  UnsafeUtil.putByte(this.pos--, (byte)(128 | 63 & var5));
                  UnsafeUtil.putByte(this.pos--, (byte)(128 | 63 & var5 >>> 6));
                  UnsafeUtil.putByte(this.pos--, (byte)(128 | 63 & var5 >>> 12));
                  UnsafeUtil.putByte(this.pos--, (byte)(240 | var5 >>> 18));
               } else {
                  this.requireSpace(var2);
                  var2++;
               }
            }
         }
      }

      @Override
      public void write(byte var1) {
         UnsafeUtil.putByte(this.pos--, var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) {
         if (this.spaceLeft() < var3) {
            this.nextBuffer(var3);
         }

         this.pos -= (long)var3;
         ((Buffer)this.buffer).position(this.bufferPos() + 1);
         this.buffer.put(var1, var2, var3);
      }

      @Override
      public void writeLazy(byte[] var1, int var2, int var3) {
         if (this.spaceLeft() < var3) {
            this.totalDoneBytes += var3;
            this.buffers.addFirst(AllocatedBuffer.wrap(var1, var2, var3));
            this.nextBuffer();
         } else {
            this.pos -= (long)var3;
            ((Buffer)this.buffer).position(this.bufferPos() + 1);
            this.buffer.put(var1, var2, var3);
         }
      }

      @Override
      public void write(ByteBuffer var1) {
         int var2 = var1.remaining();
         if (this.spaceLeft() < var2) {
            this.nextBuffer(var2);
         }

         this.pos -= (long)var2;
         ((Buffer)this.buffer).position(this.bufferPos() + 1);
         this.buffer.put(var1);
      }

      @Override
      public void writeLazy(ByteBuffer var1) {
         int var2 = var1.remaining();
         if (this.spaceLeft() < var2) {
            this.totalDoneBytes += var2;
            this.buffers.addFirst(AllocatedBuffer.wrap(var1));
            this.nextBuffer();
         } else {
            this.pos -= (long)var2;
            ((Buffer)this.buffer).position(this.bufferPos() + 1);
            this.buffer.put(var1);
         }
      }

      @Override
      void requireSpace(int var1) {
         if (this.spaceLeft() < var1) {
            this.nextBuffer(var1);
         }
      }
   }

   private static final class UnsafeHeapWriter extends BinaryWriter {
      private AllocatedBuffer allocatedBuffer;
      private byte[] buffer;
      private long offset;
      private long limit;
      private long offsetMinusOne;
      private long limitMinusOne;
      private long pos;

      UnsafeHeapWriter(BufferAllocator var1, int var2) {
         super(var1, var2);
         this.nextBuffer();
      }

      static boolean isSupported() {
         return UnsafeUtil.hasUnsafeArrayOperations();
      }

      @Override
      void finishCurrentBuffer() {
         if (this.allocatedBuffer != null) {
            this.totalDoneBytes = this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
            this.allocatedBuffer.position(this.arrayPos() - this.allocatedBuffer.arrayOffset() + 1);
            this.allocatedBuffer = null;
            this.pos = 0L;
            this.limitMinusOne = 0L;
         }
      }

      private int arrayPos() {
         return (int)this.pos;
      }

      private void nextBuffer() {
         this.nextBuffer(this.newHeapBuffer());
      }

      private void nextBuffer(int var1) {
         this.nextBuffer(this.newHeapBuffer(var1));
      }

      private void nextBuffer(AllocatedBuffer var1) {
         if (!var1.hasArray()) {
            throw new RuntimeException("Allocator returned non-heap buffer");
         } else {
            this.finishCurrentBuffer();
            this.buffers.addFirst(var1);
            this.allocatedBuffer = var1;
            this.buffer = var1.array();
            int var2 = var1.arrayOffset();
            this.limit = (long)(var2 + var1.limit());
            this.offset = (long)(var2 + var1.position());
            this.offsetMinusOne = this.offset - 1L;
            this.limitMinusOne = this.limit - 1L;
            this.pos = this.limitMinusOne;
         }
      }

      @Override
      public int getTotalBytesWritten() {
         return this.totalDoneBytes + this.bytesWrittenToCurrentBuffer();
      }

      int bytesWrittenToCurrentBuffer() {
         return (int)(this.limitMinusOne - this.pos);
      }

      int spaceLeft() {
         return (int)(this.pos - this.offsetMinusOne);
      }

      @Override
      public void writeUInt32(int var1, int var2) {
         this.requireSpace(10);
         this.writeVarint32(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeInt32(int var1, int var2) {
         this.requireSpace(15);
         this.writeInt32(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeSInt32(int var1, int var2) {
         this.requireSpace(10);
         this.writeSInt32(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeFixed32(int var1, int var2) {
         this.requireSpace(9);
         this.writeFixed32(var2);
         this.writeTag(var1, 5);
      }

      @Override
      public void writeUInt64(int var1, long var2) {
         this.requireSpace(15);
         this.writeVarint64(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeSInt64(int var1, long var2) {
         this.requireSpace(15);
         this.writeSInt64(var2);
         this.writeTag(var1, 0);
      }

      @Override
      public void writeFixed64(int var1, long var2) {
         this.requireSpace(13);
         this.writeFixed64(var2);
         this.writeTag(var1, 1);
      }

      @Override
      public void writeBool(int var1, boolean var2) {
         this.requireSpace(6);
         this.write((byte)(var2 ? 1 : 0));
         this.writeTag(var1, 0);
      }

      @Override
      public void writeString(int var1, String var2) {
         int var3 = this.getTotalBytesWritten();
         this.writeString(var2);
         int var4 = this.getTotalBytesWritten() - var3;
         this.requireSpace(10);
         this.writeVarint32(var4);
         this.writeTag(var1, 2);
      }

      @Override
      public void writeBytes(int var1, ByteString var2) {
         try {
            var2.writeToReverse(this);
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }

         this.requireSpace(10);
         this.writeVarint32(var2.size());
         this.writeTag(var1, 2);
      }

      @Override
      public void writeMessage(int var1, Object var2) throws IOException {
         int var3 = this.getTotalBytesWritten();
         Protobuf.getInstance().writeTo(var2, this);
         int var4 = this.getTotalBytesWritten() - var3;
         this.requireSpace(10);
         this.writeVarint32(var4);
         this.writeTag(var1, 2);
      }

      @Override
      public void writeMessage(int var1, Object var2, Schema var3) throws IOException {
         int var4 = this.getTotalBytesWritten();
         var3.writeTo(var2, this);
         int var5 = this.getTotalBytesWritten() - var4;
         this.requireSpace(10);
         this.writeVarint32(var5);
         this.writeTag(var1, 2);
      }

      @Override
      public void writeGroup(int var1, Object var2) throws IOException {
         this.writeTag(var1, 4);
         Protobuf.getInstance().writeTo(var2, this);
         this.writeTag(var1, 3);
      }

      @Override
      public void writeGroup(int var1, Object var2, Schema var3) throws IOException {
         this.writeTag(var1, 4);
         var3.writeTo(var2, this);
         this.writeTag(var1, 3);
      }

      @Override
      public void writeStartGroup(int var1) {
         this.writeTag(var1, 3);
      }

      @Override
      public void writeEndGroup(int var1) {
         this.writeTag(var1, 4);
      }

      @Override
      void writeInt32(int var1) {
         if (var1 >= 0) {
            this.writeVarint32(var1);
         } else {
            this.writeVarint64((long)var1);
         }
      }

      @Override
      void writeSInt32(int var1) {
         this.writeVarint32(CodedOutputStream.encodeZigZag32(var1));
      }

      @Override
      void writeSInt64(long var1) {
         this.writeVarint64(CodedOutputStream.encodeZigZag64(var1));
      }

      @Override
      void writeBool(boolean var1) {
         this.write((byte)(var1 ? 1 : 0));
      }

      @Override
      void writeTag(int var1, int var2) {
         this.writeVarint32(WireFormat.makeTag(var1, var2));
      }

      @Override
      void writeVarint32(int var1) {
         if ((var1 & -128) == 0) {
            this.writeVarint32OneByte(var1);
         } else if ((var1 & -16384) == 0) {
            this.writeVarint32TwoBytes(var1);
         } else if ((var1 & -2097152) == 0) {
            this.writeVarint32ThreeBytes(var1);
         } else if ((var1 & -268435456) == 0) {
            this.writeVarint32FourBytes(var1);
         } else {
            this.writeVarint32FiveBytes(var1);
         }
      }

      private void writeVarint32OneByte(int var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)var1);
      }

      private void writeVarint32TwoBytes(int var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 >>> 7));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 & 127 | 128));
      }

      private void writeVarint32ThreeBytes(int var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 >>> 14));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 >>> 7 & 127 | 128));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 & 127 | 128));
      }

      private void writeVarint32FourBytes(int var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 >>> 21));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 >>> 14 & 127 | 128));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 >>> 7 & 127 | 128));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 & 127 | 128));
      }

      private void writeVarint32FiveBytes(int var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 >>> 28));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 >>> 21 & 127 | 128));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 >>> 14 & 127 | 128));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 >>> 7 & 127 | 128));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 & 127 | 128));
      }

      @Override
      void writeVarint64(long var1) {
         switch (BinaryWriter.computeUInt64SizeNoTag(var1)) {
            case 1:
               this.writeVarint64OneByte(var1);
               break;
            case 2:
               this.writeVarint64TwoBytes(var1);
               break;
            case 3:
               this.writeVarint64ThreeBytes(var1);
               break;
            case 4:
               this.writeVarint64FourBytes(var1);
               break;
            case 5:
               this.writeVarint64FiveBytes(var1);
               break;
            case 6:
               this.writeVarint64SixBytes(var1);
               break;
            case 7:
               this.writeVarint64SevenBytes(var1);
               break;
            case 8:
               this.writeVarint64EightBytes(var1);
               break;
            case 9:
               this.writeVarint64NineBytes(var1);
               break;
            case 10:
               this.writeVarint64TenBytes(var1);
         }
      }

      private void writeVarint64OneByte(long var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)var1));
      }

      private void writeVarint64TwoBytes(long var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 7)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)var1 & 127 | 128));
      }

      private void writeVarint64ThreeBytes(long var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)var1 >>> 14));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64FourBytes(long var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 21)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64FiveBytes(long var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 28)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64SixBytes(long var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 35)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64SevenBytes(long var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 42)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64EightBytes(long var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 49)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 42 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64NineBytes(long var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 56)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 49 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 42 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      private void writeVarint64TenBytes(long var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 63)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 56 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 49 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 42 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 35 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 28 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 21 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 14 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >>> 7 & 127L | 128L)));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 & 127L | 128L)));
      }

      @Override
      void writeFixed32(int var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 >> 24 & 0xFF));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 >> 16 & 0xFF));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 >> 8 & 0xFF));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(var1 & 0xFF));
      }

      @Override
      void writeFixed64(long var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >> 56) & 0xFF));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >> 48) & 0xFF));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >> 40) & 0xFF));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >> 32) & 0xFF));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >> 24) & 0xFF));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >> 16) & 0xFF));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)(var1 >> 8) & 0xFF));
         UnsafeUtil.putByte(this.buffer, this.pos--, (byte)((int)var1 & 0xFF));
      }

      @Override
      void writeString(String var1) {
         this.requireSpace(var1.length());

         int var2;
         char var3;
         for (var2 = var1.length() - 1; var2 >= 0 && (var3 = var1.charAt(var2)) < 128; var2--) {
            UnsafeUtil.putByte(this.buffer, this.pos--, (byte)var3);
         }

         if (var2 != -1) {
            for (; var2 >= 0; var2--) {
               var3 = var1.charAt(var2);
               if (var3 < 128 && this.pos > this.offsetMinusOne) {
                  UnsafeUtil.putByte(this.buffer, this.pos--, (byte)var3);
               } else if (var3 < 2048 && this.pos > this.offset) {
                  UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(128 | '?' & var3));
                  UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(960 | var3 >>> 6));
               } else if ((var3 < '\ud800' || '\udfff' < var3) && this.pos > this.offset + 1L) {
                  UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(128 | '?' & var3));
                  UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(128 | 63 & var3 >>> 6));
                  UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(480 | var3 >>> '\f'));
               } else if (this.pos > this.offset + 2L) {
                  char var4;
                  if (var2 == 0 || !Character.isSurrogatePair(var4 = var1.charAt(var2 - 1), var3)) {
                     throw new Utf8.UnpairedSurrogateException(var2 - 1, var2);
                  }

                  var2--;
                  int var5 = Character.toCodePoint(var4, var3);
                  UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(128 | 63 & var5));
                  UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(128 | 63 & var5 >>> 6));
                  UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(128 | 63 & var5 >>> 12));
                  UnsafeUtil.putByte(this.buffer, this.pos--, (byte)(240 | var5 >>> 18));
               } else {
                  this.requireSpace(var2);
                  var2++;
               }
            }
         }
      }

      @Override
      public void write(byte var1) {
         UnsafeUtil.putByte(this.buffer, this.pos--, var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) {
         if (var2 >= 0 && var2 + var3 <= var1.length) {
            this.requireSpace(var3);
            this.pos -= (long)var3;
            System.arraycopy(var1, var2, this.buffer, this.arrayPos() + 1, var3);
         } else {
            throw new ArrayIndexOutOfBoundsException(String.format("value.length=%d, offset=%d, length=%d", var1.length, var2, var3));
         }
      }

      @Override
      public void writeLazy(byte[] var1, int var2, int var3) {
         if (var2 < 0 || var2 + var3 > var1.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("value.length=%d, offset=%d, length=%d", var1.length, var2, var3));
         } else if (this.spaceLeft() < var3) {
            this.totalDoneBytes += var3;
            this.buffers.addFirst(AllocatedBuffer.wrap(var1, var2, var3));
            this.nextBuffer();
         } else {
            this.pos -= (long)var3;
            System.arraycopy(var1, var2, this.buffer, this.arrayPos() + 1, var3);
         }
      }

      @Override
      public void write(ByteBuffer var1) {
         int var2 = var1.remaining();
         this.requireSpace(var2);
         this.pos -= (long)var2;
         var1.get(this.buffer, this.arrayPos() + 1, var2);
      }

      @Override
      public void writeLazy(ByteBuffer var1) {
         int var2 = var1.remaining();
         if (this.spaceLeft() < var2) {
            this.totalDoneBytes += var2;
            this.buffers.addFirst(AllocatedBuffer.wrap(var1));
            this.nextBuffer();
         }

         this.pos -= (long)var2;
         var1.get(this.buffer, this.arrayPos() + 1, var2);
      }

      @Override
      void requireSpace(int var1) {
         if (this.spaceLeft() < var1) {
            this.nextBuffer(var1);
         }
      }
   }
}
