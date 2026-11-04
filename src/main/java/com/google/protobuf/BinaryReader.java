package com.google.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

abstract class BinaryReader implements Reader {
   private static final int FIXED32_MULTIPLE_MASK = 3;
   private static final int FIXED64_MULTIPLE_MASK = 7;

   public static BinaryReader newInstance(ByteBuffer var0, boolean var1) {
      if (var0.hasArray()) {
         return new BinaryReader.SafeHeapReader(var0, var1);
      } else {
         throw new IllegalArgumentException("Direct buffers not yet supported");
      }
   }

   private BinaryReader() {
   }

   public abstract int getTotalBytesRead();

   @Override
   public boolean shouldDiscardUnknownFields() {
      return false;
   }

   private static final class SafeHeapReader extends BinaryReader {
      private final boolean bufferIsImmutable;
      private final byte[] buffer;
      private int pos;
      private final int initialPos;
      private int limit;
      private int tag;
      private int endGroupTag;

      public SafeHeapReader(ByteBuffer var1, boolean var2) {
         this.bufferIsImmutable = var2;
         this.buffer = var1.array();
         this.initialPos = this.pos = var1.arrayOffset() + var1.position();
         this.limit = var1.arrayOffset() + var1.limit();
      }

      private boolean isAtEnd() {
         return this.pos == this.limit;
      }

      @Override
      public int getTotalBytesRead() {
         return this.pos - this.initialPos;
      }

      @Override
      public int getFieldNumber() throws IOException {
         if (this.isAtEnd()) {
            return Integer.MAX_VALUE;
         } else {
            this.tag = this.readVarint32();
            return this.tag == this.endGroupTag ? Integer.MAX_VALUE : WireFormat.getTagFieldNumber(this.tag);
         }
      }

      @Override
      public int getTag() {
         return this.tag;
      }

      @Override
      public boolean skipField() throws IOException {
         if (!this.isAtEnd() && this.tag != this.endGroupTag) {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  this.skipVarint();
                  return true;
               case 1:
                  this.skipBytes(8);
                  return true;
               case 2:
                  this.skipBytes(this.readVarint32());
                  return true;
               case 3:
                  this.skipGroup();
                  return true;
               case 4:
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
               case 5:
                  this.skipBytes(4);
                  return true;
            }
         } else {
            return false;
         }
      }

      @Override
      public double readDouble() throws IOException {
         this.requireWireType(1);
         return Double.longBitsToDouble(this.readLittleEndian64());
      }

      @Override
      public float readFloat() throws IOException {
         this.requireWireType(5);
         return Float.intBitsToFloat(this.readLittleEndian32());
      }

      @Override
      public long readUInt64() throws IOException {
         this.requireWireType(0);
         return this.readVarint64();
      }

      @Override
      public long readInt64() throws IOException {
         this.requireWireType(0);
         return this.readVarint64();
      }

      @Override
      public int readInt32() throws IOException {
         this.requireWireType(0);
         return this.readVarint32();
      }

      @Override
      public long readFixed64() throws IOException {
         this.requireWireType(1);
         return this.readLittleEndian64();
      }

      @Override
      public int readFixed32() throws IOException {
         this.requireWireType(5);
         return this.readLittleEndian32();
      }

      @Override
      public boolean readBool() throws IOException {
         this.requireWireType(0);
         return this.readVarint32() != 0;
      }

      @Override
      public String readString() throws IOException {
         return this.readStringInternal(false);
      }

      @Override
      public String readStringRequireUtf8() throws IOException {
         return this.readStringInternal(true);
      }

      public String readStringInternal(boolean var1) throws IOException {
         this.requireWireType(2);
         int var2 = this.readVarint32();
         if (var2 == 0) {
            return "";
         } else {
            this.requireBytes(var2);
            if (var1 && !Utf8.isValidUtf8(this.buffer, this.pos, this.pos + var2)) {
               throw InvalidProtocolBufferException.invalidUtf8();
            } else {
               String var3 = new String(this.buffer, this.pos, var2, Internal.UTF_8);
               this.pos += var2;
               return var3;
            }
         }
      }

      @Override
      public <T> T readMessage(Class<T> var1, ExtensionRegistryLite var2) throws IOException {
         this.requireWireType(2);
         return this.readMessage(Protobuf.getInstance().schemaFor(var1), var2);
      }

      @Override
      public <T> T readMessageBySchemaWithCheck(Schema<T> var1, ExtensionRegistryLite var2) throws IOException {
         this.requireWireType(2);
         return this.readMessage(var1, var2);
      }

      private <T> T readMessage(Schema<T> var1, ExtensionRegistryLite var2) throws IOException {
         int var3 = this.readVarint32();
         this.requireBytes(var3);
         int var4 = this.limit;
         int var5 = this.pos + var3;
         this.limit = var5;

         Object var7;
         try {
            Object var6 = var1.newInstance();
            var1.mergeFrom(var6, this, var2);
            var1.makeImmutable(var6);
            if (this.pos != var5) {
               throw InvalidProtocolBufferException.parseFailure();
            }

            var7 = var6;
         } finally {
            this.limit = var4;
         }

         return (T)var7;
      }

      @Override
      public <T> T readGroup(Class<T> var1, ExtensionRegistryLite var2) throws IOException {
         this.requireWireType(3);
         return this.readGroup(Protobuf.getInstance().schemaFor(var1), var2);
      }

      @Override
      public <T> T readGroupBySchemaWithCheck(Schema<T> var1, ExtensionRegistryLite var2) throws IOException {
         this.requireWireType(3);
         return this.readGroup(var1, var2);
      }

      private <T> T readGroup(Schema<T> var1, ExtensionRegistryLite var2) throws IOException {
         int var3 = this.endGroupTag;
         this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4);

         Object var5;
         try {
            Object var4 = var1.newInstance();
            var1.mergeFrom(var4, this, var2);
            var1.makeImmutable(var4);
            if (this.tag != this.endGroupTag) {
               throw InvalidProtocolBufferException.parseFailure();
            }

            var5 = var4;
         } finally {
            this.endGroupTag = var3;
         }

         return (T)var5;
      }

      @Override
      public ByteString readBytes() throws IOException {
         this.requireWireType(2);
         int var1 = this.readVarint32();
         if (var1 == 0) {
            return ByteString.EMPTY;
         } else {
            this.requireBytes(var1);
            ByteString var2 = this.bufferIsImmutable ? ByteString.wrap(this.buffer, this.pos, var1) : ByteString.copyFrom(this.buffer, this.pos, var1);
            this.pos += var1;
            return var2;
         }
      }

      @Override
      public int readUInt32() throws IOException {
         this.requireWireType(0);
         return this.readVarint32();
      }

      @Override
      public int readEnum() throws IOException {
         this.requireWireType(0);
         return this.readVarint32();
      }

      @Override
      public int readSFixed32() throws IOException {
         this.requireWireType(5);
         return this.readLittleEndian32();
      }

      @Override
      public long readSFixed64() throws IOException {
         this.requireWireType(1);
         return this.readLittleEndian64();
      }

      @Override
      public int readSInt32() throws IOException {
         this.requireWireType(0);
         return CodedInputStream.decodeZigZag32(this.readVarint32());
      }

      @Override
      public long readSInt64() throws IOException {
         this.requireWireType(0);
         return CodedInputStream.decodeZigZag64(this.readVarint64());
      }

      @Override
      public void readDoubleList(List<Double> var1) throws IOException {
         if (var1 instanceof DoubleArrayList) {
            DoubleArrayList var2 = (DoubleArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 1:
                  int var5;
                  int var6;
                  do {
                     var2.addDouble(this.readDouble());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               case 2:
                  int var3 = this.readVarint32();
                  this.verifyPackedFixed64Length(var3);
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addDouble(Double.longBitsToDouble(this.readLittleEndian64_NoCheck()));
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 1:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readDouble());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               case 2:
                  int var7 = this.readVarint32();
                  this.verifyPackedFixed64Length(var7);
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(Double.longBitsToDouble(this.readLittleEndian64_NoCheck()));
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public void readFloatList(List<Float> var1) throws IOException {
         if (var1 instanceof FloatArrayList) {
            FloatArrayList var2 = (FloatArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 2:
                  int var3 = this.readVarint32();
                  this.verifyPackedFixed32Length(var3);
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addFloat(Float.intBitsToFloat(this.readLittleEndian32_NoCheck()));
                  }
                  break;
               case 5:
                  int var5;
                  int var6;
                  do {
                     var2.addFloat(this.readFloat());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 2:
                  int var7 = this.readVarint32();
                  this.verifyPackedFixed32Length(var7);
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(Float.intBitsToFloat(this.readLittleEndian32_NoCheck()));
                  }
                  break;
               case 5:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readFloat());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public void readUInt64List(List<Long> var1) throws IOException {
         if (var1 instanceof LongArrayList) {
            LongArrayList var2 = (LongArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var5;
                  int var6;
                  do {
                     var2.addLong(this.readUInt64());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               case 2:
                  int var3 = this.readVarint32();
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addLong(this.readVarint64());
                  }

                  this.requirePosition(var4);
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readUInt64());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               case 2:
                  int var7 = this.readVarint32();
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(this.readVarint64());
                  }

                  this.requirePosition(var8);
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public void readInt64List(List<Long> var1) throws IOException {
         if (var1 instanceof LongArrayList) {
            LongArrayList var2 = (LongArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var5;
                  int var6;
                  do {
                     var2.addLong(this.readInt64());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               case 2:
                  int var3 = this.readVarint32();
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addLong(this.readVarint64());
                  }

                  this.requirePosition(var4);
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readInt64());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               case 2:
                  int var7 = this.readVarint32();
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(this.readVarint64());
                  }

                  this.requirePosition(var8);
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public void readInt32List(List<Integer> var1) throws IOException {
         if (var1 instanceof IntArrayList) {
            IntArrayList var2 = (IntArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var5;
                  int var6;
                  do {
                     var2.addInt(this.readInt32());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               case 2:
                  int var3 = this.readVarint32();
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addInt(this.readVarint32());
                  }

                  this.requirePosition(var4);
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readInt32());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               case 2:
                  int var7 = this.readVarint32();
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(this.readVarint32());
                  }

                  this.requirePosition(var8);
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public void readFixed64List(List<Long> var1) throws IOException {
         if (var1 instanceof LongArrayList) {
            LongArrayList var2 = (LongArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 1:
                  int var5;
                  int var6;
                  do {
                     var2.addLong(this.readFixed64());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               case 2:
                  int var3 = this.readVarint32();
                  this.verifyPackedFixed64Length(var3);
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addLong(this.readLittleEndian64_NoCheck());
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 1:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readFixed64());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               case 2:
                  int var7 = this.readVarint32();
                  this.verifyPackedFixed64Length(var7);
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(this.readLittleEndian64_NoCheck());
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public void readFixed32List(List<Integer> var1) throws IOException {
         if (var1 instanceof IntArrayList) {
            IntArrayList var2 = (IntArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 2:
                  int var3 = this.readVarint32();
                  this.verifyPackedFixed32Length(var3);
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addInt(this.readLittleEndian32_NoCheck());
                  }
                  break;
               case 5:
                  int var5;
                  int var6;
                  do {
                     var2.addInt(this.readFixed32());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 2:
                  int var7 = this.readVarint32();
                  this.verifyPackedFixed32Length(var7);
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(this.readLittleEndian32_NoCheck());
                  }
                  break;
               case 5:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readFixed32());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public void readBoolList(List<Boolean> var1) throws IOException {
         if (var1 instanceof BooleanArrayList) {
            BooleanArrayList var2 = (BooleanArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var5;
                  int var6;
                  do {
                     var2.addBoolean(this.readBool());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               case 2:
                  int var3 = this.readVarint32();
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addBoolean(this.readVarint32() != 0);
                  }

                  this.requirePosition(var4);
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readBool());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               case 2:
                  int var7 = this.readVarint32();
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(this.readVarint32() != 0);
                  }

                  this.requirePosition(var8);
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public void readStringList(List<String> var1) throws IOException {
         this.readStringListInternal(var1, false);
      }

      @Override
      public void readStringListRequireUtf8(List<String> var1) throws IOException {
         this.readStringListInternal(var1, true);
      }

      public void readStringListInternal(List<String> var1, boolean var2) throws IOException {
         if (WireFormat.getTagWireType(this.tag) != 2) {
            throw InvalidProtocolBufferException.invalidWireType();
         } else if (var1 instanceof LazyStringList && !var2) {
            LazyStringList var6 = (LazyStringList)var1;

            int var5;
            int var7;
            do {
               var6.add(this.readBytes());
               if (this.isAtEnd()) {
                  return;
               }

               var7 = this.pos;
               var5 = this.readVarint32();
            } while (var5 == this.tag);

            this.pos = var7;
         } else {
            int var3;
            int var4;
            do {
               var1.add(this.readStringInternal(var2));
               if (this.isAtEnd()) {
                  return;
               }

               var3 = this.pos;
               var4 = this.readVarint32();
            } while (var4 == this.tag);

            this.pos = var3;
         }
      }

      @Override
      public <T> void readMessageList(List<T> var1, Class<T> var2, ExtensionRegistryLite var3) throws IOException {
         Schema var4 = Protobuf.getInstance().schemaFor(var2);
         this.readMessageList(var1, var4, var3);
      }

      @Override
      public <T> void readMessageList(List<T> var1, Schema<T> var2, ExtensionRegistryLite var3) throws IOException {
         if (WireFormat.getTagWireType(this.tag) != 2) {
            throw InvalidProtocolBufferException.invalidWireType();
         } else {
            int var4 = this.tag;

            int var5;
            int var6;
            do {
               var1.add(this.readMessage(var2, var3));
               if (this.isAtEnd()) {
                  return;
               }

               var5 = this.pos;
               var6 = this.readVarint32();
            } while (var6 == var4);

            this.pos = var5;
         }
      }

      @Override
      public <T> void readGroupList(List<T> var1, Class<T> var2, ExtensionRegistryLite var3) throws IOException {
         Schema var4 = Protobuf.getInstance().schemaFor(var2);
         this.readGroupList(var1, var4, var3);
      }

      @Override
      public <T> void readGroupList(List<T> var1, Schema<T> var2, ExtensionRegistryLite var3) throws IOException {
         if (WireFormat.getTagWireType(this.tag) != 3) {
            throw InvalidProtocolBufferException.invalidWireType();
         } else {
            int var4 = this.tag;

            int var5;
            int var6;
            do {
               var1.add(this.readGroup(var2, var3));
               if (this.isAtEnd()) {
                  return;
               }

               var5 = this.pos;
               var6 = this.readVarint32();
            } while (var6 == var4);

            this.pos = var5;
         }
      }

      @Override
      public void readBytesList(List<ByteString> var1) throws IOException {
         if (WireFormat.getTagWireType(this.tag) != 2) {
            throw InvalidProtocolBufferException.invalidWireType();
         } else {
            int var2;
            int var3;
            do {
               var1.add(this.readBytes());
               if (this.isAtEnd()) {
                  return;
               }

               var2 = this.pos;
               var3 = this.readVarint32();
            } while (var3 == this.tag);

            this.pos = var2;
         }
      }

      @Override
      public void readUInt32List(List<Integer> var1) throws IOException {
         if (var1 instanceof IntArrayList) {
            IntArrayList var2 = (IntArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var5;
                  int var6;
                  do {
                     var2.addInt(this.readUInt32());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               case 2:
                  int var3 = this.readVarint32();
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addInt(this.readVarint32());
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readUInt32());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               case 2:
                  int var7 = this.readVarint32();
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(this.readVarint32());
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public void readEnumList(List<Integer> var1) throws IOException {
         if (var1 instanceof IntArrayList) {
            IntArrayList var2 = (IntArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var5;
                  int var6;
                  do {
                     var2.addInt(this.readEnum());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               case 2:
                  int var3 = this.readVarint32();
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addInt(this.readVarint32());
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readEnum());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               case 2:
                  int var7 = this.readVarint32();
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(this.readVarint32());
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public void readSFixed32List(List<Integer> var1) throws IOException {
         if (var1 instanceof IntArrayList) {
            IntArrayList var2 = (IntArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 2:
                  int var3 = this.readVarint32();
                  this.verifyPackedFixed32Length(var3);
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addInt(this.readLittleEndian32_NoCheck());
                  }
                  break;
               case 5:
                  int var5;
                  int var6;
                  do {
                     var2.addInt(this.readSFixed32());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 2:
                  int var7 = this.readVarint32();
                  this.verifyPackedFixed32Length(var7);
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(this.readLittleEndian32_NoCheck());
                  }
                  break;
               case 5:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readSFixed32());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public void readSFixed64List(List<Long> var1) throws IOException {
         if (var1 instanceof LongArrayList) {
            LongArrayList var2 = (LongArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 1:
                  int var5;
                  int var6;
                  do {
                     var2.addLong(this.readSFixed64());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               case 2:
                  int var3 = this.readVarint32();
                  this.verifyPackedFixed64Length(var3);
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addLong(this.readLittleEndian64_NoCheck());
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 1:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readSFixed64());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               case 2:
                  int var7 = this.readVarint32();
                  this.verifyPackedFixed64Length(var7);
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(this.readLittleEndian64_NoCheck());
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public void readSInt32List(List<Integer> var1) throws IOException {
         if (var1 instanceof IntArrayList) {
            IntArrayList var2 = (IntArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var5;
                  int var6;
                  do {
                     var2.addInt(this.readSInt32());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               case 2:
                  int var3 = this.readVarint32();
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addInt(CodedInputStream.decodeZigZag32(this.readVarint32()));
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readSInt32());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               case 2:
                  int var7 = this.readVarint32();
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(CodedInputStream.decodeZigZag32(this.readVarint32()));
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public void readSInt64List(List<Long> var1) throws IOException {
         if (var1 instanceof LongArrayList) {
            LongArrayList var2 = (LongArrayList)var1;
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var5;
                  int var6;
                  do {
                     var2.addLong(this.readSInt64());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var5 = this.pos;
                     var6 = this.readVarint32();
                  } while (var6 == this.tag);

                  this.pos = var5;
                  return;
               case 2:
                  int var3 = this.readVarint32();
                  int var4 = this.pos + var3;

                  while (this.pos < var4) {
                     var2.addLong(CodedInputStream.decodeZigZag64(this.readVarint64()));
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         } else {
            switch (WireFormat.getTagWireType(this.tag)) {
               case 0:
                  int var9;
                  int var10;
                  do {
                     var1.add(this.readSInt64());
                     if (this.isAtEnd()) {
                        return;
                     }

                     var9 = this.pos;
                     var10 = this.readVarint32();
                  } while (var10 == this.tag);

                  this.pos = var9;
                  return;
               case 2:
                  int var7 = this.readVarint32();
                  int var8 = this.pos + var7;

                  while (this.pos < var8) {
                     var1.add(CodedInputStream.decodeZigZag64(this.readVarint64()));
                  }
                  break;
               default:
                  throw InvalidProtocolBufferException.invalidWireType();
            }
         }
      }

      @Override
      public <K, V> void readMap(Map<K, V> var1, MapEntryLite.Metadata<K, V> var2, ExtensionRegistryLite var3) throws IOException {
         this.requireWireType(2);
         int var4 = this.readVarint32();
         this.requireBytes(var4);
         int var5 = this.limit;
         int var6 = this.pos + var4;
         this.limit = var6;

         try {
            Object var7 = var2.defaultKey;
            Object var8 = var2.defaultValue;

            while (true) {
               int var9 = this.getFieldNumber();
               if (var9 == Integer.MAX_VALUE) {
                  var1.put(var7, var8);
                  return;
               }

               try {
                  switch (var9) {
                     case 1:
                        var7 = this.readField(var2.keyType, null, null);
                        break;
                     case 2:
                        var8 = this.readField(var2.valueType, var2.defaultValue.getClass(), var3);
                        break;
                     default:
                        if (!this.skipField()) {
                           throw new InvalidProtocolBufferException("Unable to parse map entry.");
                        }
                  }
               } catch (InvalidProtocolBufferException.InvalidWireTypeException var14) {
                  if (!this.skipField()) {
                     throw new InvalidProtocolBufferException("Unable to parse map entry.");
                  }
               }
            }
         } finally {
            this.limit = var5;
         }
      }

      private Object readField(WireFormat.FieldType var1, Class<?> var2, ExtensionRegistryLite var3) throws IOException {
         switch (var1) {
            case BOOL:
               return this.readBool();
            case BYTES:
               return this.readBytes();
            case DOUBLE:
               return this.readDouble();
            case ENUM:
               return this.readEnum();
            case FIXED32:
               return this.readFixed32();
            case FIXED64:
               return this.readFixed64();
            case FLOAT:
               return this.readFloat();
            case INT32:
               return this.readInt32();
            case INT64:
               return this.readInt64();
            case MESSAGE:
               return this.readMessage(var2, var3);
            case SFIXED32:
               return this.readSFixed32();
            case SFIXED64:
               return this.readSFixed64();
            case SINT32:
               return this.readSInt32();
            case SINT64:
               return this.readSInt64();
            case STRING:
               return this.readStringRequireUtf8();
            case UINT32:
               return this.readUInt32();
            case UINT64:
               return this.readUInt64();
            default:
               throw new RuntimeException("unsupported field type.");
         }
      }

      private int readVarint32() throws IOException {
         int var1 = this.pos;
         if (this.limit == this.pos) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            byte var2;
            if ((var2 = this.buffer[var1++]) >= 0) {
               this.pos = var1;
               return var2;
            } else if (this.limit - var1 < 9) {
               return (int)this.readVarint64SlowPath();
            } else {
               if ((var2 = var2 ^ this.buffer[var1++] << 7) < 0) {
                  var2 ^= -128;
               } else if ((var2 = var2 ^ this.buffer[var1++] << 14) >= 0) {
                  var2 ^= 16256;
               } else if ((var2 = var2 ^ this.buffer[var1++] << 21) < 0) {
                  var2 ^= -2080896;
               } else {
                  byte var3 = this.buffer[var1++];
                  var2 ^= var3 << 28;
                  var2 ^= 266354560;
                  if (var3 < 0
                     && this.buffer[var1++] < 0
                     && this.buffer[var1++] < 0
                     && this.buffer[var1++] < 0
                     && this.buffer[var1++] < 0
                     && this.buffer[var1++] < 0) {
                     throw InvalidProtocolBufferException.malformedVarint();
                  }
               }

               this.pos = var1;
               return var2;
            }
         }
      }

      public long readVarint64() throws IOException {
         int var1 = this.pos;
         if (this.limit == var1) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            byte[] var2 = this.buffer;
            byte var5;
            if ((var5 = var2[var1++]) >= 0) {
               this.pos = var1;
               return (long)var5;
            } else if (this.limit - var1 < 9) {
               return this.readVarint64SlowPath();
            } else {
               long var3;
               if ((var5 = var5 ^ var2[var1++] << 7) < 0) {
                  var3 = (long)(var5 ^ -128);
               } else if ((var5 = var5 ^ var2[var1++] << 14) >= 0) {
                  var3 = (long)(var5 ^ 16256);
               } else if ((var5 = var5 ^ var2[var1++] << 21) < 0) {
                  var3 = (long)(var5 ^ -2080896);
               } else if ((var3 = (long)var5 ^ (long)var2[var1++] << 28) >= 0L) {
                  var3 ^= 266354560L;
               } else if ((var3 = var3 ^ (long)var2[var1++] << 35) < 0L) {
                  var3 ^= -34093383808L;
               } else if ((var3 = var3 ^ (long)var2[var1++] << 42) >= 0L) {
                  var3 ^= 4363953127296L;
               } else if ((var3 = var3 ^ (long)var2[var1++] << 49) < 0L) {
                  var3 ^= -558586000294016L;
               } else {
                  var3 ^= (long)var2[var1++] << 56;
                  var3 ^= 71499008037633920L;
                  if (var3 < 0L && (long)var2[var1++] < 0L) {
                     throw InvalidProtocolBufferException.malformedVarint();
                  }
               }

               this.pos = var1;
               return var3;
            }
         }
      }

      private long readVarint64SlowPath() throws IOException {
         long var1 = 0L;

         for (byte var3 = 0; var3 < 64; var3 += 7) {
            byte var4 = this.readByte();
            var1 |= (long)(var4 & 127) << var3;
            if ((var4 & 128) == 0) {
               return var1;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private byte readByte() throws IOException {
         if (this.pos == this.limit) {
            throw InvalidProtocolBufferException.truncatedMessage();
         } else {
            return this.buffer[this.pos++];
         }
      }

      private int readLittleEndian32() throws IOException {
         this.requireBytes(4);
         return this.readLittleEndian32_NoCheck();
      }

      private long readLittleEndian64() throws IOException {
         this.requireBytes(8);
         return this.readLittleEndian64_NoCheck();
      }

      private int readLittleEndian32_NoCheck() {
         int var1 = this.pos;
         byte[] var2 = this.buffer;
         this.pos = var1 + 4;
         return var2[var1] & 0xFF | (var2[var1 + 1] & 0xFF) << 8 | (var2[var1 + 2] & 0xFF) << 16 | (var2[var1 + 3] & 0xFF) << 24;
      }

      private long readLittleEndian64_NoCheck() {
         int var1 = this.pos;
         byte[] var2 = this.buffer;
         this.pos = var1 + 8;
         return (long)var2[var1] & 255L
            | ((long)var2[var1 + 1] & 255L) << 8
            | ((long)var2[var1 + 2] & 255L) << 16
            | ((long)var2[var1 + 3] & 255L) << 24
            | ((long)var2[var1 + 4] & 255L) << 32
            | ((long)var2[var1 + 5] & 255L) << 40
            | ((long)var2[var1 + 6] & 255L) << 48
            | ((long)var2[var1 + 7] & 255L) << 56;
      }

      private void skipVarint() throws IOException {
         if (this.limit - this.pos >= 10) {
            byte[] var1 = this.buffer;
            int var2 = this.pos;

            for (int var3 = 0; var3 < 10; var3++) {
               if (var1[var2++] >= 0) {
                  this.pos = var2;
                  return;
               }
            }
         }

         this.skipVarintSlowPath();
      }

      private void skipVarintSlowPath() throws IOException {
         for (int var1 = 0; var1 < 10; var1++) {
            if (this.readByte() >= 0) {
               return;
            }
         }

         throw InvalidProtocolBufferException.malformedVarint();
      }

      private void skipBytes(int var1) throws IOException {
         this.requireBytes(var1);
         this.pos += var1;
      }

      private void skipGroup() throws IOException {
         int var1 = this.endGroupTag;
         this.endGroupTag = WireFormat.makeTag(WireFormat.getTagFieldNumber(this.tag), 4);

         while (this.getFieldNumber() != Integer.MAX_VALUE && this.skipField()) {
         }

         if (this.tag != this.endGroupTag) {
            throw InvalidProtocolBufferException.parseFailure();
         } else {
            this.endGroupTag = var1;
         }
      }

      private void requireBytes(int var1) throws IOException {
         if (var1 < 0 || var1 > this.limit - this.pos) {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }

      private void requireWireType(int var1) throws IOException {
         if (WireFormat.getTagWireType(this.tag) != var1) {
            throw InvalidProtocolBufferException.invalidWireType();
         }
      }

      private void verifyPackedFixed64Length(int var1) throws IOException {
         this.requireBytes(var1);
         if ((var1 & 7) != 0) {
            throw InvalidProtocolBufferException.parseFailure();
         }
      }

      private void verifyPackedFixed32Length(int var1) throws IOException {
         this.requireBytes(var1);
         if ((var1 & 3) != 0) {
            throw InvalidProtocolBufferException.parseFailure();
         }
      }

      private void requirePosition(int var1) throws IOException {
         if (this.pos != var1) {
            throw InvalidProtocolBufferException.truncatedMessage();
         }
      }
   }
}
