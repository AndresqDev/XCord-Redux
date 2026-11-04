package com.google.protobuf;

import java.io.IOException;
import java.util.Arrays;

public final class UnknownFieldSetLite {
   private static final int MIN_CAPACITY = 8;
   private static final UnknownFieldSetLite DEFAULT_INSTANCE = new UnknownFieldSetLite(0, new int[0], new Object[0], false);
   private int count;
   private int[] tags;
   private Object[] objects;
   private int memoizedSerializedSize = -1;
   private boolean isMutable;

   public static UnknownFieldSetLite getDefaultInstance() {
      return DEFAULT_INSTANCE;
   }

   static UnknownFieldSetLite newInstance() {
      return new UnknownFieldSetLite();
   }

   static UnknownFieldSetLite mutableCopyOf(UnknownFieldSetLite var0, UnknownFieldSetLite var1) {
      int var2 = var0.count + var1.count;
      int[] var3 = Arrays.copyOf(var0.tags, var2);
      System.arraycopy(var1.tags, 0, var3, var0.count, var1.count);
      Object[] var4 = Arrays.copyOf(var0.objects, var2);
      System.arraycopy(var1.objects, 0, var4, var0.count, var1.count);
      return new UnknownFieldSetLite(var2, var3, var4, true);
   }

   private UnknownFieldSetLite() {
      this(0, new int[8], new Object[8], true);
   }

   private UnknownFieldSetLite(int var1, int[] var2, Object[] var3, boolean var4) {
      this.count = var1;
      this.tags = var2;
      this.objects = var3;
      this.isMutable = var4;
   }

   public void makeImmutable() {
      this.isMutable = false;
   }

   void checkMutable() {
      if (!this.isMutable) {
         throw new UnsupportedOperationException();
      }
   }

   public void writeTo(CodedOutputStream var1) throws IOException {
      for (int var2 = 0; var2 < this.count; var2++) {
         int var3 = this.tags[var2];
         int var4 = WireFormat.getTagFieldNumber(var3);
         switch (WireFormat.getTagWireType(var3)) {
            case 0:
               var1.writeUInt64(var4, (Long)this.objects[var2]);
               break;
            case 1:
               var1.writeFixed64(var4, (Long)this.objects[var2]);
               break;
            case 2:
               var1.writeBytes(var4, (ByteString)this.objects[var2]);
               break;
            case 3:
               var1.writeTag(var4, 3);
               ((UnknownFieldSetLite)this.objects[var2]).writeTo(var1);
               var1.writeTag(var4, 4);
               break;
            case 4:
            default:
               throw InvalidProtocolBufferException.invalidWireType();
            case 5:
               var1.writeFixed32(var4, (Integer)this.objects[var2]);
         }
      }
   }

   public void writeAsMessageSetTo(CodedOutputStream var1) throws IOException {
      for (int var2 = 0; var2 < this.count; var2++) {
         int var3 = WireFormat.getTagFieldNumber(this.tags[var2]);
         var1.writeRawMessageSetExtension(var3, (ByteString)this.objects[var2]);
      }
   }

   void writeAsMessageSetTo(Writer var1) throws IOException {
      if (var1.fieldOrder() == Writer.FieldOrder.DESCENDING) {
         for (int var2 = this.count - 1; var2 >= 0; var2--) {
            int var3 = WireFormat.getTagFieldNumber(this.tags[var2]);
            var1.writeMessageSetItem(var3, this.objects[var2]);
         }
      } else {
         for (int var4 = 0; var4 < this.count; var4++) {
            int var5 = WireFormat.getTagFieldNumber(this.tags[var4]);
            var1.writeMessageSetItem(var5, this.objects[var4]);
         }
      }
   }

   public void writeTo(Writer var1) throws IOException {
      if (this.count != 0) {
         if (var1.fieldOrder() == Writer.FieldOrder.ASCENDING) {
            for (int var2 = 0; var2 < this.count; var2++) {
               writeField(this.tags[var2], this.objects[var2], var1);
            }
         } else {
            for (int var3 = this.count - 1; var3 >= 0; var3--) {
               writeField(this.tags[var3], this.objects[var3], var1);
            }
         }
      }
   }

   private static void writeField(int var0, Object var1, Writer var2) throws IOException {
      int var3 = WireFormat.getTagFieldNumber(var0);
      switch (WireFormat.getTagWireType(var0)) {
         case 0:
            var2.writeInt64(var3, (Long)var1);
            break;
         case 1:
            var2.writeFixed64(var3, (Long)var1);
            break;
         case 2:
            var2.writeBytes(var3, (ByteString)var1);
            break;
         case 3:
            if (var2.fieldOrder() == Writer.FieldOrder.ASCENDING) {
               var2.writeStartGroup(var3);
               ((UnknownFieldSetLite)var1).writeTo(var2);
               var2.writeEndGroup(var3);
            } else {
               var2.writeEndGroup(var3);
               ((UnknownFieldSetLite)var1).writeTo(var2);
               var2.writeStartGroup(var3);
            }
            break;
         case 4:
         default:
            throw new RuntimeException(InvalidProtocolBufferException.invalidWireType());
         case 5:
            var2.writeFixed32(var3, (Integer)var1);
      }
   }

   public int getSerializedSizeAsMessageSet() {
      int var1 = this.memoizedSerializedSize;
      if (var1 != -1) {
         return var1;
      } else {
         var1 = 0;

         for (int var2 = 0; var2 < this.count; var2++) {
            int var3 = this.tags[var2];
            int var4 = WireFormat.getTagFieldNumber(var3);
            var1 += CodedOutputStream.computeRawMessageSetExtensionSize(var4, (ByteString)this.objects[var2]);
         }

         this.memoizedSerializedSize = var1;
         return var1;
      }
   }

   public int getSerializedSize() {
      int var1 = this.memoizedSerializedSize;
      if (var1 != -1) {
         return var1;
      } else {
         var1 = 0;

         for (int var2 = 0; var2 < this.count; var2++) {
            int var3 = this.tags[var2];
            int var4 = WireFormat.getTagFieldNumber(var3);
            switch (WireFormat.getTagWireType(var3)) {
               case 0:
                  var1 += CodedOutputStream.computeUInt64Size(var4, (Long)this.objects[var2]);
                  break;
               case 1:
                  var1 += CodedOutputStream.computeFixed64Size(var4, (Long)this.objects[var2]);
                  break;
               case 2:
                  var1 += CodedOutputStream.computeBytesSize(var4, (ByteString)this.objects[var2]);
                  break;
               case 3:
                  var1 += CodedOutputStream.computeTagSize(var4) * 2 + ((UnknownFieldSetLite)this.objects[var2]).getSerializedSize();
                  break;
               case 4:
               default:
                  throw new IllegalStateException(InvalidProtocolBufferException.invalidWireType());
               case 5:
                  var1 += CodedOutputStream.computeFixed32Size(var4, (Integer)this.objects[var2]);
            }
         }

         this.memoizedSerializedSize = var1;
         return var1;
      }
   }

   private static boolean equals(int[] var0, int[] var1, int var2) {
      for (int var3 = 0; var3 < var2; var3++) {
         if (var0[var3] != var1[var3]) {
            return false;
         }
      }

      return true;
   }

   private static boolean equals(Object[] var0, Object[] var1, int var2) {
      for (int var3 = 0; var3 < var2; var3++) {
         if (!var0[var3].equals(var1[var3])) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof UnknownFieldSetLite)) {
         return false;
      } else {
         UnknownFieldSetLite var2 = (UnknownFieldSetLite)var1;
         return this.count == var2.count && equals(this.tags, var2.tags, this.count) && equals(this.objects, var2.objects, this.count);
      }
   }

   private static int hashCode(int[] var0, int var1) {
      int var2 = 17;

      for (int var3 = 0; var3 < var1; var3++) {
         var2 = 31 * var2 + var0[var3];
      }

      return var2;
   }

   private static int hashCode(Object[] var0, int var1) {
      int var2 = 17;

      for (int var3 = 0; var3 < var1; var3++) {
         var2 = 31 * var2 + var0[var3].hashCode();
      }

      return var2;
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = 31 * var1 + this.count;
      var1 = 31 * var1 + hashCode(this.tags, this.count);
      return 31 * var1 + hashCode(this.objects, this.count);
   }

   final void printWithIndent(StringBuilder var1, int var2) {
      for (int var3 = 0; var3 < this.count; var3++) {
         int var4 = WireFormat.getTagFieldNumber(this.tags[var3]);
         MessageLiteToString.printField(var1, var2, String.valueOf(var4), this.objects[var3]);
      }
   }

   void storeField(int var1, Object var2) {
      this.checkMutable();
      this.ensureCapacity();
      this.tags[this.count] = var1;
      this.objects[this.count] = var2;
      this.count++;
   }

   private void ensureCapacity() {
      if (this.count == this.tags.length) {
         int var1 = this.count < 4 ? 8 : this.count >> 1;
         int var2 = this.count + var1;
         this.tags = Arrays.copyOf(this.tags, var2);
         this.objects = Arrays.copyOf(this.objects, var2);
      }
   }

   boolean mergeFieldFrom(int var1, CodedInputStream var2) throws IOException {
      this.checkMutable();
      int var3 = WireFormat.getTagFieldNumber(var1);
      switch (WireFormat.getTagWireType(var1)) {
         case 0:
            this.storeField(var1, var2.readInt64());
            return true;
         case 1:
            this.storeField(var1, var2.readFixed64());
            return true;
         case 2:
            this.storeField(var1, var2.readBytes());
            return true;
         case 3:
            UnknownFieldSetLite var4 = new UnknownFieldSetLite();
            var4.mergeFrom(var2);
            var2.checkLastTagWas(WireFormat.makeTag(var3, 4));
            this.storeField(var1, var4);
            return true;
         case 4:
            return false;
         case 5:
            this.storeField(var1, var2.readFixed32());
            return true;
         default:
            throw InvalidProtocolBufferException.invalidWireType();
      }
   }

   UnknownFieldSetLite mergeVarintField(int var1, int var2) {
      this.checkMutable();
      if (var1 == 0) {
         throw new IllegalArgumentException("Zero is not a valid field number.");
      } else {
         this.storeField(WireFormat.makeTag(var1, 0), (long)var2);
         return this;
      }
   }

   UnknownFieldSetLite mergeLengthDelimitedField(int var1, ByteString var2) {
      this.checkMutable();
      if (var1 == 0) {
         throw new IllegalArgumentException("Zero is not a valid field number.");
      } else {
         this.storeField(WireFormat.makeTag(var1, 2), var2);
         return this;
      }
   }

   private UnknownFieldSetLite mergeFrom(CodedInputStream var1) throws IOException {
      int var2;
      do {
         var2 = var1.readTag();
      } while (var2 != 0 && this.mergeFieldFrom(var2, var1));

      return this;
   }
}
