package com.sun.jna;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

class ELFAnalyser {
   private static final byte[] ELF_MAGIC = new byte[]{127, 69, 76, 70};
   private static final int EF_ARM_ABI_FLOAT_HARD = 1024;
   private static final int EF_ARM_ABI_FLOAT_SOFT = 512;
   private static final int EI_DATA_BIG_ENDIAN = 2;
   private static final int E_MACHINE_ARM = 40;
   private static final int EI_CLASS_64BIT = 2;
   private final String filename;
   private boolean ELF = false;
   private boolean _64Bit = false;
   private boolean bigEndian = false;
   private boolean armHardFloat = false;
   private boolean armSoftFloat = false;
   private boolean arm = false;

   public static ELFAnalyser analyse(String var0) throws IOException {
      ELFAnalyser var1 = new ELFAnalyser(var0);
      var1.runDetection();
      return var1;
   }

   public boolean isELF() {
      return this.ELF;
   }

   public boolean is64Bit() {
      return this._64Bit;
   }

   public boolean isBigEndian() {
      return this.bigEndian;
   }

   public String getFilename() {
      return this.filename;
   }

   public boolean isArmHardFloat() {
      return this.armHardFloat;
   }

   public boolean isArmSoftFloat() {
      return this.armSoftFloat;
   }

   public boolean isArm() {
      return this.arm;
   }

   private ELFAnalyser(String var1) {
      this.filename = var1;
   }

   private void runDetection() throws IOException {
      RandomAccessFile var1 = new RandomAccessFile(this.filename, "r");
      if (var1.length() > 4L) {
         byte[] var2 = new byte[4];
         var1.seek(0L);
         var1.read(var2);
         if (Arrays.equals(var2, ELF_MAGIC)) {
            this.ELF = true;
         }
      }

      if (this.ELF) {
         var1.seek(4L);
         byte var5 = var1.readByte();
         this._64Bit = var5 == 2;
         var1.seek(0L);
         ByteBuffer var3 = ByteBuffer.allocate(this._64Bit ? 64 : 52);
         var1.getChannel().read(var3, 0L);
         this.bigEndian = var3.get(5) == 2;
         var3.order(this.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
         this.arm = var3.get(18) == 40;
         if (this.arm) {
            int var4 = var3.getInt(this._64Bit ? 48 : 36);
            this.armSoftFloat = (var4 & 512) == 512;
            this.armHardFloat = (var4 & 1024) == 1024;
         }
      }
   }
}
