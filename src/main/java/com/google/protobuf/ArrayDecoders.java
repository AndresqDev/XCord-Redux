package com.google.protobuf;

import java.io.IOException;

final class ArrayDecoders {
   static int decodeVarint32(byte[] var0, int var1, ArrayDecoders.Registers var2) {
      byte var3 = var0[var1++];
      if (var3 >= 0) {
         var2.int1 = var3;
         return var1;
      } else {
         return decodeVarint32(var3, var0, var1, var2);
      }
   }

   static int decodeVarint32(int var0, byte[] var1, int var2, ArrayDecoders.Registers var3) {
      int var4 = var0 & 127;
      byte var5 = var1[var2++];
      if (var5 >= 0) {
         var3.int1 = var4 | var5 << 7;
         return var2;
      } else {
         var4 |= (var5 & 127) << 7;
         byte var6 = var1[var2++];
         if (var6 >= 0) {
            var3.int1 = var4 | var6 << 14;
            return var2;
         } else {
            var4 |= (var6 & 127) << 14;
            byte var7 = var1[var2++];
            if (var7 >= 0) {
               var3.int1 = var4 | var7 << 21;
               return var2;
            } else {
               var4 |= (var7 & 127) << 21;
               byte var8 = var1[var2++];
               if (var8 >= 0) {
                  var3.int1 = var4 | var8 << 28;
                  return var2;
               } else {
                  var4 |= (var8 & 127) << 28;

                  while (var1[var2++] < 0) {
                  }

                  var3.int1 = var4;
                  return var2;
               }
            }
         }
      }
   }

   static int decodeVarint64(byte[] var0, int var1, ArrayDecoders.Registers var2) {
      long var3 = (long)var0[var1++];
      if (var3 >= 0L) {
         var2.long1 = var3;
         return var1;
      } else {
         return decodeVarint64(var3, var0, var1, var2);
      }
   }

   static int decodeVarint64(long var0, byte[] var2, int var3, ArrayDecoders.Registers var4) {
      long var5 = var0 & 127L;
      byte var7 = var2[var3++];
      byte var8 = 7;

      for (var5 |= (long)(var7 & 127) << 7; var7 < 0; var5 |= (long)(var7 & 127) << var8) {
         var7 = var2[var3++];
         var8 += 7;
      }

      var4.long1 = var5;
      return var3;
   }

   static int decodeFixed32(byte[] var0, int var1) {
      return var0[var1] & 0xFF | (var0[var1 + 1] & 0xFF) << 8 | (var0[var1 + 2] & 0xFF) << 16 | (var0[var1 + 3] & 0xFF) << 24;
   }

   static long decodeFixed64(byte[] var0, int var1) {
      return (long)var0[var1] & 255L
         | ((long)var0[var1 + 1] & 255L) << 8
         | ((long)var0[var1 + 2] & 255L) << 16
         | ((long)var0[var1 + 3] & 255L) << 24
         | ((long)var0[var1 + 4] & 255L) << 32
         | ((long)var0[var1 + 5] & 255L) << 40
         | ((long)var0[var1 + 6] & 255L) << 48
         | ((long)var0[var1 + 7] & 255L) << 56;
   }

   static double decodeDouble(byte[] var0, int var1) {
      return Double.longBitsToDouble(decodeFixed64(var0, var1));
   }

   static float decodeFloat(byte[] var0, int var1) {
      return Float.intBitsToFloat(decodeFixed32(var0, var1));
   }

   static int decodeString(byte[] var0, int var1, ArrayDecoders.Registers var2) throws InvalidProtocolBufferException {
      var1 = decodeVarint32(var0, var1, var2);
      int var3 = var2.int1;
      if (var3 < 0) {
         throw InvalidProtocolBufferException.negativeSize();
      } else if (var3 == 0) {
         var2.object1 = "";
         return var1;
      } else {
         var2.object1 = new String(var0, var1, var3, Internal.UTF_8);
         return var1 + var3;
      }
   }

   static int decodeStringRequireUtf8(byte[] var0, int var1, ArrayDecoders.Registers var2) throws InvalidProtocolBufferException {
      var1 = decodeVarint32(var0, var1, var2);
      int var3 = var2.int1;
      if (var3 < 0) {
         throw InvalidProtocolBufferException.negativeSize();
      } else if (var3 == 0) {
         var2.object1 = "";
         return var1;
      } else {
         var2.object1 = Utf8.decodeUtf8(var0, var1, var3);
         return var1 + var3;
      }
   }

   static int decodeBytes(byte[] var0, int var1, ArrayDecoders.Registers var2) throws InvalidProtocolBufferException {
      var1 = decodeVarint32(var0, var1, var2);
      int var3 = var2.int1;
      if (var3 < 0) {
         throw InvalidProtocolBufferException.negativeSize();
      } else if (var3 > var0.length - var1) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else if (var3 == 0) {
         var2.object1 = ByteString.EMPTY;
         return var1;
      } else {
         var2.object1 = ByteString.copyFrom(var0, var1, var3);
         return var1 + var3;
      }
   }

   static int decodeMessageField(Schema var0, byte[] var1, int var2, int var3, ArrayDecoders.Registers var4) throws IOException {
      int var5 = var1[var2++];
      if (var5 < 0) {
         var2 = decodeVarint32(var5, var1, var2, var4);
         var5 = var4.int1;
      }

      if (var5 >= 0 && var5 <= var3 - var2) {
         Object var6 = var0.newInstance();
         var0.mergeFrom(var6, var1, var2, var2 + var5, var4);
         var0.makeImmutable(var6);
         var4.object1 = var6;
         return var2 + var5;
      } else {
         throw InvalidProtocolBufferException.truncatedMessage();
      }
   }

   static int decodeGroupField(Schema var0, byte[] var1, int var2, int var3, int var4, ArrayDecoders.Registers var5) throws IOException {
      MessageSchema var6 = (MessageSchema)var0;
      Object var7 = var6.newInstance();
      int var8 = var6.parseProto2Message(var7, var1, var2, var3, var4, var5);
      var6.makeImmutable(var7);
      var5.object1 = var7;
      return var8;
   }

   static int decodeVarint32List(int var0, byte[] var1, int var2, int var3, Internal.ProtobufList<?> var4, ArrayDecoders.Registers var5) {
      IntArrayList var6 = (IntArrayList)var4;
      var2 = decodeVarint32(var1, var2, var5);
      var6.addInt(var5.int1);

      while (var2 < var3) {
         int var7 = decodeVarint32(var1, var2, var5);
         if (var0 != var5.int1) {
            break;
         }

         var2 = decodeVarint32(var1, var7, var5);
         var6.addInt(var5.int1);
      }

      return var2;
   }

   static int decodeVarint64List(int var0, byte[] var1, int var2, int var3, Internal.ProtobufList<?> var4, ArrayDecoders.Registers var5) {
      LongArrayList var6 = (LongArrayList)var4;
      var2 = decodeVarint64(var1, var2, var5);
      var6.addLong(var5.long1);

      while (var2 < var3) {
         int var7 = decodeVarint32(var1, var2, var5);
         if (var0 != var5.int1) {
            break;
         }

         var2 = decodeVarint64(var1, var7, var5);
         var6.addLong(var5.long1);
      }

      return var2;
   }

   static int decodeFixed32List(int var0, byte[] var1, int var2, int var3, Internal.ProtobufList<?> var4, ArrayDecoders.Registers var5) {
      IntArrayList var6 = (IntArrayList)var4;
      var6.addInt(decodeFixed32(var1, var2));
      var2 += 4;

      while (var2 < var3) {
         int var7 = decodeVarint32(var1, var2, var5);
         if (var0 != var5.int1) {
            break;
         }

         var6.addInt(decodeFixed32(var1, var7));
         var2 = var7 + 4;
      }

      return var2;
   }

   static int decodeFixed64List(int var0, byte[] var1, int var2, int var3, Internal.ProtobufList<?> var4, ArrayDecoders.Registers var5) {
      LongArrayList var6 = (LongArrayList)var4;
      var6.addLong(decodeFixed64(var1, var2));
      var2 += 8;

      while (var2 < var3) {
         int var7 = decodeVarint32(var1, var2, var5);
         if (var0 != var5.int1) {
            break;
         }

         var6.addLong(decodeFixed64(var1, var7));
         var2 = var7 + 8;
      }

      return var2;
   }

   static int decodeFloatList(int var0, byte[] var1, int var2, int var3, Internal.ProtobufList<?> var4, ArrayDecoders.Registers var5) {
      FloatArrayList var6 = (FloatArrayList)var4;
      var6.addFloat(decodeFloat(var1, var2));
      var2 += 4;

      while (var2 < var3) {
         int var7 = decodeVarint32(var1, var2, var5);
         if (var0 != var5.int1) {
            break;
         }

         var6.addFloat(decodeFloat(var1, var7));
         var2 = var7 + 4;
      }

      return var2;
   }

   static int decodeDoubleList(int var0, byte[] var1, int var2, int var3, Internal.ProtobufList<?> var4, ArrayDecoders.Registers var5) {
      DoubleArrayList var6 = (DoubleArrayList)var4;
      var6.addDouble(decodeDouble(var1, var2));
      var2 += 8;

      while (var2 < var3) {
         int var7 = decodeVarint32(var1, var2, var5);
         if (var0 != var5.int1) {
            break;
         }

         var6.addDouble(decodeDouble(var1, var7));
         var2 = var7 + 8;
      }

      return var2;
   }

   static int decodeBoolList(int var0, byte[] var1, int var2, int var3, Internal.ProtobufList<?> var4, ArrayDecoders.Registers var5) {
      BooleanArrayList var6 = (BooleanArrayList)var4;
      var2 = decodeVarint64(var1, var2, var5);
      var6.addBoolean(var5.long1 != 0L);

      while (var2 < var3) {
         int var7 = decodeVarint32(var1, var2, var5);
         if (var0 != var5.int1) {
            break;
         }

         var2 = decodeVarint64(var1, var7, var5);
         var6.addBoolean(var5.long1 != 0L);
      }

      return var2;
   }

   static int decodeSInt32List(int var0, byte[] var1, int var2, int var3, Internal.ProtobufList<?> var4, ArrayDecoders.Registers var5) {
      IntArrayList var6 = (IntArrayList)var4;
      var2 = decodeVarint32(var1, var2, var5);
      var6.addInt(CodedInputStream.decodeZigZag32(var5.int1));

      while (var2 < var3) {
         int var7 = decodeVarint32(var1, var2, var5);
         if (var0 != var5.int1) {
            break;
         }

         var2 = decodeVarint32(var1, var7, var5);
         var6.addInt(CodedInputStream.decodeZigZag32(var5.int1));
      }

      return var2;
   }

   static int decodeSInt64List(int var0, byte[] var1, int var2, int var3, Internal.ProtobufList<?> var4, ArrayDecoders.Registers var5) {
      LongArrayList var6 = (LongArrayList)var4;
      var2 = decodeVarint64(var1, var2, var5);
      var6.addLong(CodedInputStream.decodeZigZag64(var5.long1));

      while (var2 < var3) {
         int var7 = decodeVarint32(var1, var2, var5);
         if (var0 != var5.int1) {
            break;
         }

         var2 = decodeVarint64(var1, var7, var5);
         var6.addLong(CodedInputStream.decodeZigZag64(var5.long1));
      }

      return var2;
   }

   static int decodePackedVarint32List(byte[] var0, int var1, Internal.ProtobufList<?> var2, ArrayDecoders.Registers var3) throws IOException {
      IntArrayList var4 = (IntArrayList)var2;
      var1 = decodeVarint32(var0, var1, var3);
      int var5 = var1 + var3.int1;

      while (var1 < var5) {
         var1 = decodeVarint32(var0, var1, var3);
         var4.addInt(var3.int1);
      }

      if (var1 != var5) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return var1;
      }
   }

   static int decodePackedVarint64List(byte[] var0, int var1, Internal.ProtobufList<?> var2, ArrayDecoders.Registers var3) throws IOException {
      LongArrayList var4 = (LongArrayList)var2;
      var1 = decodeVarint32(var0, var1, var3);
      int var5 = var1 + var3.int1;

      while (var1 < var5) {
         var1 = decodeVarint64(var0, var1, var3);
         var4.addLong(var3.long1);
      }

      if (var1 != var5) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return var1;
      }
   }

   static int decodePackedFixed32List(byte[] var0, int var1, Internal.ProtobufList<?> var2, ArrayDecoders.Registers var3) throws IOException {
      IntArrayList var4 = (IntArrayList)var2;
      var1 = decodeVarint32(var0, var1, var3);

      int var5;
      for (var5 = var1 + var3.int1; var1 < var5; var1 += 4) {
         var4.addInt(decodeFixed32(var0, var1));
      }

      if (var1 != var5) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return var1;
      }
   }

   static int decodePackedFixed64List(byte[] var0, int var1, Internal.ProtobufList<?> var2, ArrayDecoders.Registers var3) throws IOException {
      LongArrayList var4 = (LongArrayList)var2;
      var1 = decodeVarint32(var0, var1, var3);

      int var5;
      for (var5 = var1 + var3.int1; var1 < var5; var1 += 8) {
         var4.addLong(decodeFixed64(var0, var1));
      }

      if (var1 != var5) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return var1;
      }
   }

   static int decodePackedFloatList(byte[] var0, int var1, Internal.ProtobufList<?> var2, ArrayDecoders.Registers var3) throws IOException {
      FloatArrayList var4 = (FloatArrayList)var2;
      var1 = decodeVarint32(var0, var1, var3);

      int var5;
      for (var5 = var1 + var3.int1; var1 < var5; var1 += 4) {
         var4.addFloat(decodeFloat(var0, var1));
      }

      if (var1 != var5) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return var1;
      }
   }

   static int decodePackedDoubleList(byte[] var0, int var1, Internal.ProtobufList<?> var2, ArrayDecoders.Registers var3) throws IOException {
      DoubleArrayList var4 = (DoubleArrayList)var2;
      var1 = decodeVarint32(var0, var1, var3);

      int var5;
      for (var5 = var1 + var3.int1; var1 < var5; var1 += 8) {
         var4.addDouble(decodeDouble(var0, var1));
      }

      if (var1 != var5) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return var1;
      }
   }

   static int decodePackedBoolList(byte[] var0, int var1, Internal.ProtobufList<?> var2, ArrayDecoders.Registers var3) throws IOException {
      BooleanArrayList var4 = (BooleanArrayList)var2;
      var1 = decodeVarint32(var0, var1, var3);
      int var5 = var1 + var3.int1;

      while (var1 < var5) {
         var1 = decodeVarint64(var0, var1, var3);
         var4.addBoolean(var3.long1 != 0L);
      }

      if (var1 != var5) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return var1;
      }
   }

   static int decodePackedSInt32List(byte[] var0, int var1, Internal.ProtobufList<?> var2, ArrayDecoders.Registers var3) throws IOException {
      IntArrayList var4 = (IntArrayList)var2;
      var1 = decodeVarint32(var0, var1, var3);
      int var5 = var1 + var3.int1;

      while (var1 < var5) {
         var1 = decodeVarint32(var0, var1, var3);
         var4.addInt(CodedInputStream.decodeZigZag32(var3.int1));
      }

      if (var1 != var5) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return var1;
      }
   }

   static int decodePackedSInt64List(byte[] var0, int var1, Internal.ProtobufList<?> var2, ArrayDecoders.Registers var3) throws IOException {
      LongArrayList var4 = (LongArrayList)var2;
      var1 = decodeVarint32(var0, var1, var3);
      int var5 = var1 + var3.int1;

      while (var1 < var5) {
         var1 = decodeVarint64(var0, var1, var3);
         var4.addLong(CodedInputStream.decodeZigZag64(var3.long1));
      }

      if (var1 != var5) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         return var1;
      }
   }

   static int decodeStringList(int var0, byte[] var1, int var2, int var3, Internal.ProtobufList<?> var4, ArrayDecoders.Registers var5) throws InvalidProtocolBufferException {
      Internal.ProtobufList var6 = var4;
      var2 = decodeVarint32(var1, var2, var5);
      int var7 = var5.int1;
      if (var7 < 0) {
         throw InvalidProtocolBufferException.negativeSize();
      } else {
         if (var7 == 0) {
            var4.add("");
         } else {
            String var8 = new String(var1, var2, var7, Internal.UTF_8);
            var4.add(var8);
            var2 += var7;
         }

         while (var2 < var3) {
            int var12 = decodeVarint32(var1, var2, var5);
            if (var0 != var5.int1) {
               break;
            }

            var2 = decodeVarint32(var1, var12, var5);
            int var9 = var5.int1;
            if (var9 < 0) {
               throw InvalidProtocolBufferException.negativeSize();
            }

            if (var9 == 0) {
               var6.add("");
            } else {
               String var10 = new String(var1, var2, var9, Internal.UTF_8);
               var6.add(var10);
               var2 += var9;
            }
         }

         return var2;
      }
   }

   static int decodeStringListRequireUtf8(int var0, byte[] var1, int var2, int var3, Internal.ProtobufList<?> var4, ArrayDecoders.Registers var5) throws InvalidProtocolBufferException {
      Internal.ProtobufList var6 = var4;
      var2 = decodeVarint32(var1, var2, var5);
      int var7 = var5.int1;
      if (var7 < 0) {
         throw InvalidProtocolBufferException.negativeSize();
      } else {
         if (var7 == 0) {
            var4.add("");
         } else {
            if (!Utf8.isValidUtf8(var1, var2, var2 + var7)) {
               throw InvalidProtocolBufferException.invalidUtf8();
            }

            String var8 = new String(var1, var2, var7, Internal.UTF_8);
            var4.add(var8);
            var2 += var7;
         }

         while (var2 < var3) {
            int var12 = decodeVarint32(var1, var2, var5);
            if (var0 != var5.int1) {
               break;
            }

            var2 = decodeVarint32(var1, var12, var5);
            int var9 = var5.int1;
            if (var9 < 0) {
               throw InvalidProtocolBufferException.negativeSize();
            }

            if (var9 == 0) {
               var6.add("");
            } else {
               if (!Utf8.isValidUtf8(var1, var2, var2 + var9)) {
                  throw InvalidProtocolBufferException.invalidUtf8();
               }

               String var10 = new String(var1, var2, var9, Internal.UTF_8);
               var6.add(var10);
               var2 += var9;
            }
         }

         return var2;
      }
   }

   static int decodeBytesList(int var0, byte[] var1, int var2, int var3, Internal.ProtobufList<?> var4, ArrayDecoders.Registers var5) throws InvalidProtocolBufferException {
      Internal.ProtobufList var6 = var4;
      var2 = decodeVarint32(var1, var2, var5);
      int var7 = var5.int1;
      if (var7 < 0) {
         throw InvalidProtocolBufferException.negativeSize();
      } else if (var7 > var1.length - var2) {
         throw InvalidProtocolBufferException.truncatedMessage();
      } else {
         if (var7 == 0) {
            var4.add(ByteString.EMPTY);
         } else {
            var4.add(ByteString.copyFrom(var1, var2, var7));
            var2 += var7;
         }

         while (var2 < var3) {
            int var8 = decodeVarint32(var1, var2, var5);
            if (var0 != var5.int1) {
               break;
            }

            var2 = decodeVarint32(var1, var8, var5);
            int var9 = var5.int1;
            if (var9 < 0) {
               throw InvalidProtocolBufferException.negativeSize();
            }

            if (var9 > var1.length - var2) {
               throw InvalidProtocolBufferException.truncatedMessage();
            }

            if (var9 == 0) {
               var6.add(ByteString.EMPTY);
            } else {
               var6.add(ByteString.copyFrom(var1, var2, var9));
               var2 += var9;
            }
         }

         return var2;
      }
   }

   static int decodeMessageList(Schema<?> var0, int var1, byte[] var2, int var3, int var4, Internal.ProtobufList<?> var5, ArrayDecoders.Registers var6) throws IOException {
      Internal.ProtobufList var7 = var5;
      var3 = decodeMessageField(var0, var2, var3, var4, var6);
      var5.add(var6.object1);

      while (var3 < var4) {
         int var8 = decodeVarint32(var2, var3, var6);
         if (var1 != var6.int1) {
            break;
         }

         var3 = decodeMessageField(var0, var2, var8, var4, var6);
         var7.add(var6.object1);
      }

      return var3;
   }

   static int decodeGroupList(Schema var0, int var1, byte[] var2, int var3, int var4, Internal.ProtobufList<?> var5, ArrayDecoders.Registers var6) throws IOException {
      Internal.ProtobufList var7 = var5;
      int var8 = var1 & -8 | 4;
      var3 = decodeGroupField(var0, var2, var3, var4, var8, var6);
      var5.add(var6.object1);

      while (var3 < var4) {
         int var9 = decodeVarint32(var2, var3, var6);
         if (var1 != var6.int1) {
            break;
         }

         var3 = decodeGroupField(var0, var2, var9, var4, var8, var6);
         var7.add(var6.object1);
      }

      return var3;
   }

   static int decodeExtensionOrUnknownField(
      int var0,
      byte[] var1,
      int var2,
      int var3,
      Object var4,
      MessageLite var5,
      UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> var6,
      ArrayDecoders.Registers var7
   ) throws IOException {
      int var8 = var0 >>> 3;
      GeneratedMessageLite.GeneratedExtension var9 = var7.extensionRegistry.findLiteExtensionByNumber(var5, var8);
      if (var9 == null) {
         return decodeUnknownField(var0, var1, var2, var3, MessageSchema.getMutableUnknownFields(var4), var7);
      } else {
         ((GeneratedMessageLite.ExtendableMessage)var4).ensureExtensionsAreMutable();
         return decodeExtension(var0, var1, var2, var3, (GeneratedMessageLite.ExtendableMessage<?, ?>)var4, var9, var6, var7);
      }
   }

   static int decodeExtension(
      int var0,
      byte[] var1,
      int var2,
      int var3,
      GeneratedMessageLite.ExtendableMessage<?, ?> var4,
      GeneratedMessageLite.GeneratedExtension<?, ?> var5,
      UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> var6,
      ArrayDecoders.Registers var7
   ) throws IOException {
      FieldSet var8 = var4.extensions;
      int var9 = var0 >>> 3;
      if (var5.descriptor.isRepeated() && var5.descriptor.isPacked()) {
         switch (var5.getLiteType()) {
            case DOUBLE:
               DoubleArrayList var22 = new DoubleArrayList();
               var2 = decodePackedDoubleList(var1, var2, var22, var7);
               var8.setField(var5.descriptor, var22);
               break;
            case FLOAT:
               FloatArrayList var21 = new FloatArrayList();
               var2 = decodePackedFloatList(var1, var2, var21, var7);
               var8.setField(var5.descriptor, var21);
               break;
            case INT64:
            case UINT64:
               LongArrayList var20 = new LongArrayList();
               var2 = decodePackedVarint64List(var1, var2, var20, var7);
               var8.setField(var5.descriptor, var20);
               break;
            case INT32:
            case UINT32:
               IntArrayList var19 = new IntArrayList();
               var2 = decodePackedVarint32List(var1, var2, var19, var7);
               var8.setField(var5.descriptor, var19);
               break;
            case FIXED64:
            case SFIXED64:
               LongArrayList var18 = new LongArrayList();
               var2 = decodePackedFixed64List(var1, var2, var18, var7);
               var8.setField(var5.descriptor, var18);
               break;
            case FIXED32:
            case SFIXED32:
               IntArrayList var17 = new IntArrayList();
               var2 = decodePackedFixed32List(var1, var2, var17, var7);
               var8.setField(var5.descriptor, var17);
               break;
            case BOOL:
               BooleanArrayList var16 = new BooleanArrayList();
               var2 = decodePackedBoolList(var1, var2, var16, var7);
               var8.setField(var5.descriptor, var16);
               break;
            case SINT32:
               IntArrayList var15 = new IntArrayList();
               var2 = decodePackedSInt32List(var1, var2, var15, var7);
               var8.setField(var5.descriptor, var15);
               break;
            case SINT64:
               LongArrayList var14 = new LongArrayList();
               var2 = decodePackedSInt64List(var1, var2, var14, var7);
               var8.setField(var5.descriptor, var14);
               break;
            case ENUM:
               IntArrayList var13 = new IntArrayList();
               var2 = decodePackedVarint32List(var1, var2, var13, var7);
               UnknownFieldSetLite var25 = var4.unknownFields;
               if (var25 == UnknownFieldSetLite.getDefaultInstance()) {
                  var25 = null;
               }

               var25 = SchemaUtil.filterUnknownEnumList(var9, var13, var5.descriptor.getEnumType(), var25, var6);
               if (var25 != null) {
                  var4.unknownFields = var25;
               }

               var8.setField(var5.descriptor, var13);
               break;
            default:
               throw new IllegalStateException("Type cannot be packed: " + var5.descriptor.getLiteType());
         }
      } else {
         Object var10 = null;
         if (var5.getLiteType() == WireFormat.FieldType.ENUM) {
            var2 = decodeVarint32(var1, var2, var7);
            Internal.EnumLite var11 = var5.descriptor.getEnumType().findValueByNumber(var7.int1);
            if (var11 == null) {
               UnknownFieldSetLite var12 = var4.unknownFields;
               if (var12 == UnknownFieldSetLite.getDefaultInstance()) {
                  var12 = UnknownFieldSetLite.newInstance();
                  var4.unknownFields = var12;
               }

               SchemaUtil.storeUnknownEnum(var9, var7.int1, var12, var6);
               return var2;
            }

            var10 = var7.int1;
         } else {
            switch (var5.getLiteType()) {
               case DOUBLE:
                  var10 = decodeDouble(var1, var2);
                  var2 += 8;
                  break;
               case FLOAT:
                  var10 = decodeFloat(var1, var2);
                  var2 += 4;
                  break;
               case INT64:
               case UINT64:
                  var2 = decodeVarint64(var1, var2, var7);
                  var10 = var7.long1;
                  break;
               case INT32:
               case UINT32:
                  var2 = decodeVarint32(var1, var2, var7);
                  var10 = var7.int1;
                  break;
               case FIXED64:
               case SFIXED64:
                  var10 = decodeFixed64(var1, var2);
                  var2 += 8;
                  break;
               case FIXED32:
               case SFIXED32:
                  var10 = decodeFixed32(var1, var2);
                  var2 += 4;
                  break;
               case BOOL:
                  var2 = decodeVarint64(var1, var2, var7);
                  var10 = var7.long1 != 0L;
                  break;
               case SINT32:
                  var2 = decodeVarint32(var1, var2, var7);
                  var10 = CodedInputStream.decodeZigZag32(var7.int1);
                  break;
               case SINT64:
                  var2 = decodeVarint64(var1, var2, var7);
                  var10 = CodedInputStream.decodeZigZag64(var7.long1);
                  break;
               case ENUM:
                  throw new IllegalStateException("Shouldn't reach here.");
               case BYTES:
                  var2 = decodeBytes(var1, var2, var7);
                  var10 = var7.object1;
                  break;
               case STRING:
                  var2 = decodeString(var1, var2, var7);
                  var10 = var7.object1;
                  break;
               case GROUP:
                  int var23 = var9 << 3 | 4;
                  var2 = decodeGroupField(Protobuf.getInstance().schemaFor(var5.getMessageDefaultInstance().getClass()), var1, var2, var3, var23, var7);
                  var10 = var7.object1;
                  break;
               case MESSAGE:
                  var2 = decodeMessageField(Protobuf.getInstance().schemaFor(var5.getMessageDefaultInstance().getClass()), var1, var2, var3, var7);
                  var10 = var7.object1;
            }
         }

         if (var5.isRepeated()) {
            var8.addRepeatedField(var5.descriptor, var10);
         } else {
            switch (var5.getLiteType()) {
               case GROUP:
               case MESSAGE:
                  Object var24 = var8.getField(var5.descriptor);
                  if (var24 != null) {
                     var10 = Internal.mergeMessage(var24, var10);
                  }
               default:
                  var8.setField(var5.descriptor, var10);
            }
         }
      }

      return var2;
   }

   static int decodeUnknownField(int var0, byte[] var1, int var2, int var3, UnknownFieldSetLite var4, ArrayDecoders.Registers var5) throws InvalidProtocolBufferException {
      if (WireFormat.getTagFieldNumber(var0) == 0) {
         throw InvalidProtocolBufferException.invalidTag();
      } else {
         switch (WireFormat.getTagWireType(var0)) {
            case 0:
               var2 = decodeVarint64(var1, var2, var5);
               var4.storeField(var0, var5.long1);
               return var2;
            case 1:
               var4.storeField(var0, decodeFixed64(var1, var2));
               return var2 + 8;
            case 2:
               var2 = decodeVarint32(var1, var2, var5);
               int var6 = var5.int1;
               if (var6 < 0) {
                  throw InvalidProtocolBufferException.negativeSize();
               } else {
                  if (var6 > var1.length - var2) {
                     throw InvalidProtocolBufferException.truncatedMessage();
                  }

                  if (var6 == 0) {
                     var4.storeField(var0, ByteString.EMPTY);
                  } else {
                     var4.storeField(var0, ByteString.copyFrom(var1, var2, var6));
                  }

                  return var2 + var6;
               }
            case 3:
               UnknownFieldSetLite var7 = UnknownFieldSetLite.newInstance();
               int var8 = var0 & -8 | 4;
               int var9 = 0;

               while (var2 < var3) {
                  var2 = decodeVarint32(var1, var2, var5);
                  var9 = var5.int1;
                  if (var9 == var8) {
                     break;
                  }

                  var2 = decodeUnknownField(var9, var1, var2, var3, var7, var5);
               }

               if (var2 <= var3 && var9 == var8) {
                  var4.storeField(var0, var7);
                  return var2;
               } else {
                  throw InvalidProtocolBufferException.parseFailure();
               }
            case 4:
            default:
               throw InvalidProtocolBufferException.invalidTag();
            case 5:
               var4.storeField(var0, decodeFixed32(var1, var2));
               return var2 + 4;
         }
      }
   }

   static int skipField(int var0, byte[] var1, int var2, int var3, ArrayDecoders.Registers var4) throws InvalidProtocolBufferException {
      if (WireFormat.getTagFieldNumber(var0) == 0) {
         throw InvalidProtocolBufferException.invalidTag();
      } else {
         switch (WireFormat.getTagWireType(var0)) {
            case 0:
               return decodeVarint64(var1, var2, var4);
            case 1:
               return var2 + 8;
            case 2:
               var2 = decodeVarint32(var1, var2, var4);
               return var2 + var4.int1;
            case 3:
               int var5 = var0 & -8 | 4;
               int var6 = 0;

               while (var2 < var3) {
                  var2 = decodeVarint32(var1, var2, var4);
                  var6 = var4.int1;
                  if (var6 == var5) {
                     break;
                  }

                  var2 = skipField(var6, var1, var2, var3, var4);
               }

               if (var2 <= var3 && var6 == var5) {
                  return var2;
               } else {
                  throw InvalidProtocolBufferException.parseFailure();
               }
            case 4:
            default:
               throw InvalidProtocolBufferException.invalidTag();
            case 5:
               return var2 + 4;
         }
      }
   }

   static final class Registers {
      public int int1;
      public long long1;
      public Object object1;
      public final ExtensionRegistryLite extensionRegistry;

      Registers() {
         this.extensionRegistry = ExtensionRegistryLite.getEmptyRegistry();
      }

      Registers(ExtensionRegistryLite var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.extensionRegistry = var1;
         }
      }
   }
}
