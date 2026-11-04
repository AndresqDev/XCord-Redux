package com.google.protobuf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

final class CodedInputStreamReader implements Reader {
   private static final int FIXED32_MULTIPLE_MASK = 3;
   private static final int FIXED64_MULTIPLE_MASK = 7;
   private static final int NEXT_TAG_UNSET = 0;
   private final CodedInputStream input;
   private int tag;
   private int endGroupTag;
   private int nextTag = 0;

   public static CodedInputStreamReader forCodedInput(CodedInputStream var0) {
      return var0.wrapper != null ? var0.wrapper : new CodedInputStreamReader(var0);
   }

   private CodedInputStreamReader(CodedInputStream var1) {
      this.input = Internal.checkNotNull(var1, "input");
      this.input.wrapper = this;
   }

   @Override
   public boolean shouldDiscardUnknownFields() {
      return this.input.shouldDiscardUnknownFields();
   }

   @Override
   public int getFieldNumber() throws IOException {
      if (this.nextTag != 0) {
         this.tag = this.nextTag;
         this.nextTag = 0;
      } else {
         this.tag = this.input.readTag();
      }

      return this.tag != 0 && this.tag != this.endGroupTag ? WireFormat.getTagFieldNumber(this.tag) : Integer.MAX_VALUE;
   }

   @Override
   public int getTag() {
      return this.tag;
   }

   @Override
   public boolean skipField() throws IOException {
      return !this.input.isAtEnd() && this.tag != this.endGroupTag ? this.input.skipField(this.tag) : false;
   }

   private void requireWireType(int var1) throws IOException {
      if (WireFormat.getTagWireType(this.tag) != var1) {
         throw InvalidProtocolBufferException.invalidWireType();
      }
   }

   @Override
   public double readDouble() throws IOException {
      this.requireWireType(1);
      return this.input.readDouble();
   }

   @Override
   public float readFloat() throws IOException {
      this.requireWireType(5);
      return this.input.readFloat();
   }

   @Override
   public long readUInt64() throws IOException {
      this.requireWireType(0);
      return this.input.readUInt64();
   }

   @Override
   public long readInt64() throws IOException {
      this.requireWireType(0);
      return this.input.readInt64();
   }

   @Override
   public int readInt32() throws IOException {
      this.requireWireType(0);
      return this.input.readInt32();
   }

   @Override
   public long readFixed64() throws IOException {
      this.requireWireType(1);
      return this.input.readFixed64();
   }

   @Override
   public int readFixed32() throws IOException {
      this.requireWireType(5);
      return this.input.readFixed32();
   }

   @Override
   public boolean readBool() throws IOException {
      this.requireWireType(0);
      return this.input.readBool();
   }

   @Override
   public String readString() throws IOException {
      this.requireWireType(2);
      return this.input.readString();
   }

   @Override
   public String readStringRequireUtf8() throws IOException {
      this.requireWireType(2);
      return this.input.readStringRequireUtf8();
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

   private <T> T readMessage(Schema<T> var1, ExtensionRegistryLite var2) throws IOException {
      int var3 = this.input.readUInt32();
      if (this.input.recursionDepth >= this.input.recursionLimit) {
         throw InvalidProtocolBufferException.recursionLimitExceeded();
      } else {
         int var4 = this.input.pushLimit(var3);
         Object var5 = var1.newInstance();
         this.input.recursionDepth++;
         var1.mergeFrom(var5, this, var2);
         var1.makeImmutable(var5);
         this.input.checkLastTagWas(0);
         this.input.recursionDepth--;
         this.input.popLimit(var4);
         return (T)var5;
      }
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
      return this.input.readBytes();
   }

   @Override
   public int readUInt32() throws IOException {
      this.requireWireType(0);
      return this.input.readUInt32();
   }

   @Override
   public int readEnum() throws IOException {
      this.requireWireType(0);
      return this.input.readEnum();
   }

   @Override
   public int readSFixed32() throws IOException {
      this.requireWireType(5);
      return this.input.readSFixed32();
   }

   @Override
   public long readSFixed64() throws IOException {
      this.requireWireType(1);
      return this.input.readSFixed64();
   }

   @Override
   public int readSInt32() throws IOException {
      this.requireWireType(0);
      return this.input.readSInt32();
   }

   @Override
   public long readSInt64() throws IOException {
      this.requireWireType(0);
      return this.input.readSInt64();
   }

   @Override
   public void readDoubleList(List<Double> var1) throws IOException {
      if (var1 instanceof DoubleArrayList) {
         DoubleArrayList var2 = (DoubleArrayList)var1;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 1:
               int var5;
               do {
                  var2.addDouble(this.input.readDouble());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            case 2:
               int var3 = this.input.readUInt32();
               this.verifyPackedFixed64Length(var3);
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addDouble(this.input.readDouble());
               } while (this.input.getTotalBytesRead() < var4);

               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 1:
               int var8;
               do {
                  var1.add(this.input.readDouble());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
               return;
            case 2:
               int var6 = this.input.readUInt32();
               this.verifyPackedFixed64Length(var6);
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readDouble());
               } while (this.input.getTotalBytesRead() < var7);

               return;
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
               int var3 = this.input.readUInt32();
               this.verifyPackedFixed32Length(var3);
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addFloat(this.input.readFloat());
               } while (this.input.getTotalBytesRead() < var4);

               return;
            case 5:
               int var5;
               do {
                  var2.addFloat(this.input.readFloat());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 2:
               int var6 = this.input.readUInt32();
               this.verifyPackedFixed32Length(var6);
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readFloat());
               } while (this.input.getTotalBytesRead() < var7);

               return;
            case 5:
               int var8;
               do {
                  var1.add(this.input.readFloat());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
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
               do {
                  var2.addLong(this.input.readUInt64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            case 2:
               int var3 = this.input.readUInt32();
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addLong(this.input.readUInt64());
               } while (this.input.getTotalBytesRead() < var4);

               this.requirePosition(var4);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int var8;
               do {
                  var1.add(this.input.readUInt64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
               return;
            case 2:
               int var6 = this.input.readUInt32();
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readUInt64());
               } while (this.input.getTotalBytesRead() < var7);

               this.requirePosition(var7);
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
               do {
                  var2.addLong(this.input.readInt64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            case 2:
               int var3 = this.input.readUInt32();
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addLong(this.input.readInt64());
               } while (this.input.getTotalBytesRead() < var4);

               this.requirePosition(var4);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int var8;
               do {
                  var1.add(this.input.readInt64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
               return;
            case 2:
               int var6 = this.input.readUInt32();
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readInt64());
               } while (this.input.getTotalBytesRead() < var7);

               this.requirePosition(var7);
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
               do {
                  var2.addInt(this.input.readInt32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            case 2:
               int var3 = this.input.readUInt32();
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addInt(this.input.readInt32());
               } while (this.input.getTotalBytesRead() < var4);

               this.requirePosition(var4);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int var8;
               do {
                  var1.add(this.input.readInt32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
               return;
            case 2:
               int var6 = this.input.readUInt32();
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readInt32());
               } while (this.input.getTotalBytesRead() < var7);

               this.requirePosition(var7);
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
               do {
                  var2.addLong(this.input.readFixed64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            case 2:
               int var3 = this.input.readUInt32();
               this.verifyPackedFixed64Length(var3);
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addLong(this.input.readFixed64());
               } while (this.input.getTotalBytesRead() < var4);

               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 1:
               int var8;
               do {
                  var1.add(this.input.readFixed64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
               return;
            case 2:
               int var6 = this.input.readUInt32();
               this.verifyPackedFixed64Length(var6);
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readFixed64());
               } while (this.input.getTotalBytesRead() < var7);

               return;
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
               int var3 = this.input.readUInt32();
               this.verifyPackedFixed32Length(var3);
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addInt(this.input.readFixed32());
               } while (this.input.getTotalBytesRead() < var4);

               return;
            case 5:
               int var5;
               do {
                  var2.addInt(this.input.readFixed32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 2:
               int var6 = this.input.readUInt32();
               this.verifyPackedFixed32Length(var6);
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readFixed32());
               } while (this.input.getTotalBytesRead() < var7);

               return;
            case 5:
               int var8;
               do {
                  var1.add(this.input.readFixed32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
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
               do {
                  var2.addBoolean(this.input.readBool());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            case 2:
               int var3 = this.input.readUInt32();
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addBoolean(this.input.readBool());
               } while (this.input.getTotalBytesRead() < var4);

               this.requirePosition(var4);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int var8;
               do {
                  var1.add(this.input.readBool());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
               return;
            case 2:
               int var6 = this.input.readUInt32();
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readBool());
               } while (this.input.getTotalBytesRead() < var7);

               this.requirePosition(var7);
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
         LazyStringList var5 = (LazyStringList)var1;

         int var4;
         do {
            var5.add(this.readBytes());
            if (this.input.isAtEnd()) {
               return;
            }

            var4 = this.input.readTag();
         } while (var4 == this.tag);

         this.nextTag = var4;
      } else {
         int var3;
         do {
            var1.add(var2 ? this.readStringRequireUtf8() : this.readString());
            if (this.input.isAtEnd()) {
               return;
            }

            var3 = this.input.readTag();
         } while (var3 == this.tag);

         this.nextTag = var3;
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
         do {
            var1.add(this.readMessage(var2, var3));
            if (this.input.isAtEnd() || this.nextTag != 0) {
               return;
            }

            var5 = this.input.readTag();
         } while (var5 == var4);

         this.nextTag = var5;
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
         do {
            var1.add(this.readGroup(var2, var3));
            if (this.input.isAtEnd() || this.nextTag != 0) {
               return;
            }

            var5 = this.input.readTag();
         } while (var5 == var4);

         this.nextTag = var5;
      }
   }

   @Override
   public void readBytesList(List<ByteString> var1) throws IOException {
      if (WireFormat.getTagWireType(this.tag) != 2) {
         throw InvalidProtocolBufferException.invalidWireType();
      } else {
         int var2;
         do {
            var1.add(this.readBytes());
            if (this.input.isAtEnd()) {
               return;
            }

            var2 = this.input.readTag();
         } while (var2 == this.tag);

         this.nextTag = var2;
      }
   }

   @Override
   public void readUInt32List(List<Integer> var1) throws IOException {
      if (var1 instanceof IntArrayList) {
         IntArrayList var2 = (IntArrayList)var1;
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int var5;
               do {
                  var2.addInt(this.input.readUInt32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            case 2:
               int var3 = this.input.readUInt32();
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addInt(this.input.readUInt32());
               } while (this.input.getTotalBytesRead() < var4);

               this.requirePosition(var4);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int var8;
               do {
                  var1.add(this.input.readUInt32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
               return;
            case 2:
               int var6 = this.input.readUInt32();
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readUInt32());
               } while (this.input.getTotalBytesRead() < var7);

               this.requirePosition(var7);
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
               do {
                  var2.addInt(this.input.readEnum());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            case 2:
               int var3 = this.input.readUInt32();
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addInt(this.input.readEnum());
               } while (this.input.getTotalBytesRead() < var4);

               this.requirePosition(var4);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int var8;
               do {
                  var1.add(this.input.readEnum());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
               return;
            case 2:
               int var6 = this.input.readUInt32();
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readEnum());
               } while (this.input.getTotalBytesRead() < var7);

               this.requirePosition(var7);
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
               int var3 = this.input.readUInt32();
               this.verifyPackedFixed32Length(var3);
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addInt(this.input.readSFixed32());
               } while (this.input.getTotalBytesRead() < var4);

               return;
            case 5:
               int var5;
               do {
                  var2.addInt(this.input.readSFixed32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 2:
               int var6 = this.input.readUInt32();
               this.verifyPackedFixed32Length(var6);
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readSFixed32());
               } while (this.input.getTotalBytesRead() < var7);

               return;
            case 5:
               int var8;
               do {
                  var1.add(this.input.readSFixed32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
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
               do {
                  var2.addLong(this.input.readSFixed64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            case 2:
               int var3 = this.input.readUInt32();
               this.verifyPackedFixed64Length(var3);
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addLong(this.input.readSFixed64());
               } while (this.input.getTotalBytesRead() < var4);

               return;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 1:
               int var8;
               do {
                  var1.add(this.input.readSFixed64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
               return;
            case 2:
               int var6 = this.input.readUInt32();
               this.verifyPackedFixed64Length(var6);
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readSFixed64());
               } while (this.input.getTotalBytesRead() < var7);

               return;
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
               do {
                  var2.addInt(this.input.readSInt32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            case 2:
               int var3 = this.input.readUInt32();
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addInt(this.input.readSInt32());
               } while (this.input.getTotalBytesRead() < var4);

               this.requirePosition(var4);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int var8;
               do {
                  var1.add(this.input.readSInt32());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
               return;
            case 2:
               int var6 = this.input.readUInt32();
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readSInt32());
               } while (this.input.getTotalBytesRead() < var7);

               this.requirePosition(var7);
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
               do {
                  var2.addLong(this.input.readSInt64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var5 = this.input.readTag();
               } while (var5 == this.tag);

               this.nextTag = var5;
               return;
            case 2:
               int var3 = this.input.readUInt32();
               int var4 = this.input.getTotalBytesRead() + var3;

               do {
                  var2.addLong(this.input.readSInt64());
               } while (this.input.getTotalBytesRead() < var4);

               this.requirePosition(var4);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      } else {
         switch (WireFormat.getTagWireType(this.tag)) {
            case 0:
               int var8;
               do {
                  var1.add(this.input.readSInt64());
                  if (this.input.isAtEnd()) {
                     return;
                  }

                  var8 = this.input.readTag();
               } while (var8 == this.tag);

               this.nextTag = var8;
               return;
            case 2:
               int var6 = this.input.readUInt32();
               int var7 = this.input.getTotalBytesRead() + var6;

               do {
                  var1.add(this.input.readSInt64());
               } while (this.input.getTotalBytesRead() < var7);

               this.requirePosition(var7);
               break;
            default:
               throw InvalidProtocolBufferException.invalidWireType();
         }
      }
   }

   private void verifyPackedFixed64Length(int var1) throws IOException {
      if ((var1 & 7) != 0) {
         throw InvalidProtocolBufferException.parseFailure();
      }
   }

   @Override
   public <K, V> void readMap(Map<K, V> var1, MapEntryLite.Metadata<K, V> var2, ExtensionRegistryLite var3) throws IOException {
      this.requireWireType(2);
      int var4 = this.input.readUInt32();
      int var5 = this.input.pushLimit(var4);
      Object var6 = var2.defaultKey;
      Object var7 = var2.defaultValue;

      try {
         while (true) {
            int var8 = this.getFieldNumber();
            if (var8 == Integer.MAX_VALUE || this.input.isAtEnd()) {
               var1.put(var6, var7);
               return;
            }

            try {
               switch (var8) {
                  case 1:
                     var6 = this.readField(var2.keyType, null, null);
                     break;
                  case 2:
                     var7 = this.readField(var2.valueType, var2.defaultValue.getClass(), var3);
                     break;
                  default:
                     if (!this.skipField()) {
                        throw new InvalidProtocolBufferException("Unable to parse map entry.");
                     }
               }
            } catch (InvalidProtocolBufferException.InvalidWireTypeException var13) {
               if (!this.skipField()) {
                  throw new InvalidProtocolBufferException("Unable to parse map entry.");
               }
            }
         }
      } finally {
         this.input.popLimit(var5);
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

   private void verifyPackedFixed32Length(int var1) throws IOException {
      if ((var1 & 3) != 0) {
         throw InvalidProtocolBufferException.parseFailure();
      }
   }

   private void requirePosition(int var1) throws IOException {
      if (this.input.getTotalBytesRead() != var1) {
         throw InvalidProtocolBufferException.truncatedMessage();
      }
   }
}
