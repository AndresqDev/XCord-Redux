package com.google.protobuf;

import java.io.IOException;

abstract class UnknownFieldSchema<T, B> {
   abstract boolean shouldDiscardUnknownFields(Reader var1);

   abstract void addVarint(B var1, int var2, long var3);

   abstract void addFixed32(B var1, int var2, int var3);

   abstract void addFixed64(B var1, int var2, long var3);

   abstract void addLengthDelimited(B var1, int var2, ByteString var3);

   abstract void addGroup(B var1, int var2, T var3);

   abstract B newBuilder();

   abstract T toImmutable(B var1);

   abstract void setToMessage(Object var1, T var2);

   abstract T getFromMessage(Object var1);

   abstract B getBuilderFromMessage(Object var1);

   abstract void setBuilderToMessage(Object var1, B var2);

   abstract void makeImmutable(Object var1);

   final boolean mergeOneFieldFrom(B var1, Reader var2) throws IOException {
      int var3 = var2.getTag();
      int var4 = WireFormat.getTagFieldNumber(var3);
      switch (WireFormat.getTagWireType(var3)) {
         case 0:
            this.addVarint((B)var1, var4, var2.readInt64());
            return true;
         case 1:
            this.addFixed64((B)var1, var4, var2.readFixed64());
            return true;
         case 2:
            this.addLengthDelimited((B)var1, var4, var2.readBytes());
            return true;
         case 3:
            Object var5 = this.newBuilder();
            int var6 = WireFormat.makeTag(var4, 4);
            this.mergeFrom((B)var5, var2);
            if (var6 != var2.getTag()) {
               throw InvalidProtocolBufferException.invalidEndTag();
            }

            this.addGroup((B)var1, var4, this.toImmutable((B)var5));
            return true;
         case 4:
            return false;
         case 5:
            this.addFixed32((B)var1, var4, var2.readFixed32());
            return true;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
      }
   }

   final void mergeFrom(B var1, Reader var2) throws IOException {
      while (var2.getFieldNumber() != Integer.MAX_VALUE && this.mergeOneFieldFrom((B)var1, var2)) {
      }
   }

   abstract void writeTo(T var1, Writer var2) throws IOException;

   abstract void writeAsMessageSetTo(T var1, Writer var2) throws IOException;

   abstract T merge(T var1, T var2);

   abstract int getSerializedSizeAsMessageSet(T var1);

   abstract int getSerializedSize(T var1);
}
