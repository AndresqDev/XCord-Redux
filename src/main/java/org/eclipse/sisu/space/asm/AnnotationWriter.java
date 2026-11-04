package org.eclipse.sisu.space.asm;

final class AnnotationWriter extends AnnotationVisitor {
   private final ClassWriter cw;
   private int size;
   private final boolean named;
   private final ByteVector bv;
   private final ByteVector parent;
   private final int offset;
   AnnotationWriter next;
   AnnotationWriter prev;

   AnnotationWriter(ClassWriter var1, boolean var2, ByteVector var3, ByteVector var4, int var5) {
      super(327680);
      this.cw = var1;
      this.named = var2;
      this.bv = var3;
      this.parent = var4;
      this.offset = var5;
   }

   @Override
   public void visit(String var1, Object var2) {
      this.size++;
      if (this.named) {
         this.bv.putShort(this.cw.newUTF8(var1));
      }

      if (var2 instanceof String) {
         this.bv.put12(115, this.cw.newUTF8((String)var2));
      } else if (var2 instanceof Byte) {
         this.bv.put12(66, this.cw.newInteger((Byte)var2).index);
      } else if (var2 instanceof Boolean) {
         int var3 = (Boolean)var2 ? 1 : 0;
         this.bv.put12(90, this.cw.newInteger(var3).index);
      } else if (var2 instanceof Character) {
         this.bv.put12(67, this.cw.newInteger((Character)var2).index);
      } else if (var2 instanceof Short) {
         this.bv.put12(83, this.cw.newInteger((Short)var2).index);
      } else if (var2 instanceof Type) {
         this.bv.put12(99, this.cw.newUTF8(((Type)var2).getDescriptor()));
      } else if (var2 instanceof byte[]) {
         byte[] var5 = (byte[])var2;
         this.bv.put12(91, var5.length);

         for (int var4 = 0; var4 < var5.length; var4++) {
            this.bv.put12(66, this.cw.newInteger(var5[var4]).index);
         }
      } else if (var2 instanceof boolean[]) {
         boolean[] var6 = (boolean[])var2;
         this.bv.put12(91, var6.length);

         for (int var14 = 0; var14 < var6.length; var14++) {
            this.bv.put12(90, this.cw.newInteger(var6[var14] ? 1 : 0).index);
         }
      } else if (var2 instanceof short[]) {
         short[] var7 = (short[])var2;
         this.bv.put12(91, var7.length);

         for (int var15 = 0; var15 < var7.length; var15++) {
            this.bv.put12(83, this.cw.newInteger(var7[var15]).index);
         }
      } else if (var2 instanceof char[]) {
         char[] var8 = (char[])var2;
         this.bv.put12(91, var8.length);

         for (int var16 = 0; var16 < var8.length; var16++) {
            this.bv.put12(67, this.cw.newInteger(var8[var16]).index);
         }
      } else if (var2 instanceof int[]) {
         int[] var9 = (int[])var2;
         this.bv.put12(91, var9.length);

         for (int var17 = 0; var17 < var9.length; var17++) {
            this.bv.put12(73, this.cw.newInteger(var9[var17]).index);
         }
      } else if (var2 instanceof long[]) {
         long[] var10 = (long[])var2;
         this.bv.put12(91, var10.length);

         for (int var18 = 0; var18 < var10.length; var18++) {
            this.bv.put12(74, this.cw.newLong(var10[var18]).index);
         }
      } else if (var2 instanceof float[]) {
         float[] var11 = (float[])var2;
         this.bv.put12(91, var11.length);

         for (int var19 = 0; var19 < var11.length; var19++) {
            this.bv.put12(70, this.cw.newFloat(var11[var19]).index);
         }
      } else if (var2 instanceof double[]) {
         double[] var12 = (double[])var2;
         this.bv.put12(91, var12.length);

         for (int var20 = 0; var20 < var12.length; var20++) {
            this.bv.put12(68, this.cw.newDouble(var12[var20]).index);
         }
      } else {
         Item var13 = this.cw.newConstItem(var2);
         this.bv.put12(".s.IFJDCS".charAt(var13.type), var13.index);
      }
   }

   @Override
   public void visitEnum(String var1, String var2, String var3) {
      this.size++;
      if (this.named) {
         this.bv.putShort(this.cw.newUTF8(var1));
      }

      this.bv.put12(101, this.cw.newUTF8(var2)).putShort(this.cw.newUTF8(var3));
   }

   @Override
   public AnnotationVisitor visitAnnotation(String var1, String var2) {
      this.size++;
      if (this.named) {
         this.bv.putShort(this.cw.newUTF8(var1));
      }

      this.bv.put12(64, this.cw.newUTF8(var2)).putShort(0);
      return new AnnotationWriter(this.cw, true, this.bv, this.bv, this.bv.length - 2);
   }

   @Override
   public AnnotationVisitor visitArray(String var1) {
      this.size++;
      if (this.named) {
         this.bv.putShort(this.cw.newUTF8(var1));
      }

      this.bv.put12(91, 0);
      return new AnnotationWriter(this.cw, false, this.bv, this.bv, this.bv.length - 2);
   }

   @Override
   public void visitEnd() {
      if (this.parent != null) {
         byte[] var1 = this.parent.data;
         var1[this.offset] = (byte)(this.size >>> 8);
         var1[this.offset + 1] = (byte)this.size;
      }
   }

   int getSize() {
      int var1 = 0;

      for (AnnotationWriter var2 = this; var2 != null; var2 = var2.next) {
         var1 += var2.bv.length;
      }

      return var1;
   }

   void put(ByteVector var1) {
      int var2 = 0;
      int var3 = 2;
      AnnotationWriter var4 = this;

      AnnotationWriter var5;
      for (var5 = null; var4 != null; var4 = var4.next) {
         var2++;
         var3 += var4.bv.length;
         var4.visitEnd();
         var4.prev = var5;
         var5 = var4;
      }

      var1.putInt(var3);
      var1.putShort(var2);

      for (AnnotationWriter var6 = var5; var6 != null; var6 = var6.prev) {
         var1.putByteArray(var6.bv.data, 0, var6.bv.length);
      }
   }

   static void put(AnnotationWriter[] var0, int var1, ByteVector var2) {
      int var3 = 1 + 2 * (var0.length - var1);

      for (int var4 = var1; var4 < var0.length; var4++) {
         var3 += var0[var4] == null ? 0 : var0[var4].getSize();
      }

      var2.putInt(var3).putByte(var0.length - var1);

      for (int var8 = var1; var8 < var0.length; var8++) {
         AnnotationWriter var5 = var0[var8];
         AnnotationWriter var6 = null;

         int var7;
         for (var7 = 0; var5 != null; var5 = var5.next) {
            var7++;
            var5.visitEnd();
            var5.prev = var6;
            var6 = var5;
         }

         var2.putShort(var7);

         for (AnnotationWriter var9 = var6; var9 != null; var9 = var9.prev) {
            var2.putByteArray(var9.bv.data, 0, var9.bv.length);
         }
      }
   }

   static void putTarget(int var0, TypePath var1, ByteVector var2) {
      switch (var0 >>> 24) {
         case 0:
         case 1:
         case 22:
            var2.putShort(var0 >>> 16);
            break;
         case 19:
         case 20:
         case 21:
            var2.putByte(var0 >>> 24);
            break;
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
            var2.putInt(var0);
            break;
         default:
            var2.put12(var0 >>> 24, (var0 & 16776960) >> 8);
      }

      if (var1 == null) {
         var2.putByte(0);
      } else {
         int var3 = var1.b[var1.offset] * 2 + 1;
         var2.putByteArray(var1.b, var1.offset, var3);
      }
   }
}
