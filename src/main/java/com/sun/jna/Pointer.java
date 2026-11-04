package com.sun.jna;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Pointer {
   public static final int SIZE;
   public static final Pointer NULL;
   protected long peer;

   public static final Pointer createConstant(long var0) {
      return new Pointer.Opaque(var0);
   }

   public static final Pointer createConstant(int var0) {
      return new Pointer.Opaque((long)var0 & -1L);
   }

   Pointer() {
   }

   public Pointer(long var1) {
      this.peer = var1;
   }

   public Pointer share(long var1) {
      return this.share(var1, 0L);
   }

   public Pointer share(long var1, long var3) {
      return var1 == 0L ? this : new Pointer(this.peer + var1);
   }

   public void clear(long var1) {
      this.setMemory(0L, var1, (byte)0);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         return var1 == null ? false : var1 instanceof Pointer && ((Pointer)var1).peer == this.peer;
      }
   }

   @Override
   public int hashCode() {
      return (int)((this.peer >>> 32) + (this.peer & -1L));
   }

   public long indexOf(long var1, byte var3) {
      return Native.indexOf(this, this.peer, var1, var3);
   }

   public void read(long var1, byte[] var3, int var4, int var5) {
      Native.read(this, this.peer, var1, var3, var4, var5);
   }

   public void read(long var1, short[] var3, int var4, int var5) {
      Native.read(this, this.peer, var1, var3, var4, var5);
   }

   public void read(long var1, char[] var3, int var4, int var5) {
      Native.read(this, this.peer, var1, var3, var4, var5);
   }

   public void read(long var1, int[] var3, int var4, int var5) {
      Native.read(this, this.peer, var1, var3, var4, var5);
   }

   public void read(long var1, long[] var3, int var4, int var5) {
      Native.read(this, this.peer, var1, var3, var4, var5);
   }

   public void read(long var1, float[] var3, int var4, int var5) {
      Native.read(this, this.peer, var1, var3, var4, var5);
   }

   public void read(long var1, double[] var3, int var4, int var5) {
      Native.read(this, this.peer, var1, var3, var4, var5);
   }

   public void read(long var1, Pointer[] var3, int var4, int var5) {
      for (int var6 = 0; var6 < var5; var6++) {
         Pointer var7 = this.getPointer(var1 + (long)(var6 * SIZE));
         Pointer var8 = var3[var6 + var4];
         if (var8 == null || var7 == null || var7.peer != var8.peer) {
            var3[var6 + var4] = var7;
         }
      }
   }

   public void write(long var1, byte[] var3, int var4, int var5) {
      Native.write(this, this.peer, var1, var3, var4, var5);
   }

   public void write(long var1, short[] var3, int var4, int var5) {
      Native.write(this, this.peer, var1, var3, var4, var5);
   }

   public void write(long var1, char[] var3, int var4, int var5) {
      Native.write(this, this.peer, var1, var3, var4, var5);
   }

   public void write(long var1, int[] var3, int var4, int var5) {
      Native.write(this, this.peer, var1, var3, var4, var5);
   }

   public void write(long var1, long[] var3, int var4, int var5) {
      Native.write(this, this.peer, var1, var3, var4, var5);
   }

   public void write(long var1, float[] var3, int var4, int var5) {
      Native.write(this, this.peer, var1, var3, var4, var5);
   }

   public void write(long var1, double[] var3, int var4, int var5) {
      Native.write(this, this.peer, var1, var3, var4, var5);
   }

   public void write(long var1, Pointer[] var3, int var4, int var5) {
      for (int var6 = 0; var6 < var5; var6++) {
         this.setPointer(var1 + (long)(var6 * SIZE), var3[var4 + var6]);
      }
   }

   Object getValue(long var1, Class<?> var3, Object var4) {
      Object var5 = null;
      if (Structure.class.isAssignableFrom(var3)) {
         Structure var14 = (Structure)var4;
         if (Structure.ByReference.class.isAssignableFrom(var3)) {
            var14 = Structure.updateStructureByReference(var3, var14, this.getPointer(var1));
         } else {
            var14.useMemory(this, (int)var1, true);
            var14.read();
         }

         var5 = var14;
      } else if (var3 == boolean.class || var3 == Boolean.class) {
         var5 = Function.valueOf(this.getInt(var1) != 0);
      } else if (var3 == byte.class || var3 == Byte.class) {
         var5 = this.getByte(var1);
      } else if (var3 == short.class || var3 == Short.class) {
         var5 = this.getShort(var1);
      } else if (var3 == char.class || var3 == Character.class) {
         var5 = this.getChar(var1);
      } else if (var3 == int.class || var3 == Integer.class) {
         var5 = this.getInt(var1);
      } else if (var3 == long.class || var3 == Long.class) {
         var5 = this.getLong(var1);
      } else if (var3 == float.class || var3 == Float.class) {
         var5 = this.getFloat(var1);
      } else if (var3 == double.class || var3 == Double.class) {
         var5 = this.getDouble(var1);
      } else if (Pointer.class.isAssignableFrom(var3)) {
         Pointer var13 = this.getPointer(var1);
         if (var13 != null) {
            Pointer var18 = var4 instanceof Pointer ? (Pointer)var4 : null;
            if (var18 != null && var13.peer == var18.peer) {
               var5 = var18;
            } else {
               var5 = var13;
            }
         }
      } else if (var3 == String.class) {
         Pointer var12 = this.getPointer(var1);
         var5 = var12 != null ? var12.getString(0L) : null;
      } else if (var3 == WString.class) {
         Pointer var11 = this.getPointer(var1);
         var5 = var11 != null ? new WString(var11.getWideString(0L)) : null;
      } else if (Callback.class.isAssignableFrom(var3)) {
         Pointer var10 = this.getPointer(var1);
         if (var10 == null) {
            var5 = null;
         } else {
            Callback var17 = (Callback)var4;
            Pointer var19 = CallbackReference.getFunctionPointer(var17);
            if (!var10.equals(var19)) {
               var17 = CallbackReference.getCallback(var3, var10);
            }

            var5 = var17;
         }
      } else if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(var3)) {
         Pointer var9 = this.getPointer(var1);
         if (var9 == null) {
            var5 = null;
         } else {
            Pointer var16 = var4 == null ? null : Native.getDirectBufferPointer((Buffer)var4);
            if (var16 == null || !var16.equals(var9)) {
               throw new IllegalStateException("Can't autogenerate a direct buffer on memory read");
            }

            var5 = var4;
         }
      } else if (NativeMapped.class.isAssignableFrom(var3)) {
         NativeMapped var6 = (NativeMapped)var4;
         if (var6 != null) {
            Object var7 = this.getValue(var1, var6.nativeType(), null);
            var5 = var6.fromNative(var7, new FromNativeContext(var3));
            if (var6.equals(var5)) {
               var5 = var6;
            }
         } else {
            NativeMappedConverter var15 = NativeMappedConverter.getInstance(var3);
            Object var8 = this.getValue(var1, var15.nativeType(), null);
            var5 = var15.fromNative(var8, new FromNativeContext(var3));
         }
      } else {
         if (!var3.isArray()) {
            throw new IllegalArgumentException("Reading \"" + var3 + "\" from memory is not supported");
         }

         var5 = var4;
         if (var4 == null) {
            throw new IllegalStateException("Need an initialized array");
         }

         this.readArray(var1, var4, var3.getComponentType());
      }

      return var5;
   }

   private void readArray(long var1, Object var3, Class<?> var4) {
      int var5 = 0;
      var5 = Array.getLength(var3);
      if (var4 == byte.class) {
         this.read(var1, (byte[])var3, 0, var5);
      } else if (var4 == short.class) {
         this.read(var1, (short[])var3, 0, var5);
      } else if (var4 == char.class) {
         this.read(var1, (char[])var3, 0, var5);
      } else if (var4 == int.class) {
         this.read(var1, (int[])var3, 0, var5);
      } else if (var4 == long.class) {
         this.read(var1, (long[])var3, 0, var5);
      } else if (var4 == float.class) {
         this.read(var1, (float[])var3, 0, var5);
      } else if (var4 == double.class) {
         this.read(var1, (double[])var3, 0, var5);
      } else if (Pointer.class.isAssignableFrom(var4)) {
         this.read(var1, (Pointer[])var3, 0, var5);
      } else if (Structure.class.isAssignableFrom(var4)) {
         Structure[] var7 = (Structure[])var3;
         if (Structure.ByReference.class.isAssignableFrom(var4)) {
            Pointer[] var8 = this.getPointerArray(var1, var7.length);

            for (int var9 = 0; var9 < var7.length; var9++) {
               var7[var9] = Structure.updateStructureByReference(var4, var7[var9], var8[var9]);
            }
         } else {
            Structure var14 = var7[0];
            if (var14 == null) {
               var14 = Structure.newInstance(var4, this.share(var1));
               var14.conditionalAutoRead();
               var7[0] = var14;
            } else {
               var14.useMemory(this, (int)var1, true);
               var14.read();
            }

            Structure[] var16 = var14.toArray(var7.length);

            for (int var10 = 1; var10 < var7.length; var10++) {
               if (var7[var10] == null) {
                  var7[var10] = var16[var10];
               } else {
                  var7[var10].useMemory(this, (int)(var1 + (long)(var10 * var7[var10].size())), true);
                  var7[var10].read();
               }
            }
         }
      } else {
         if (!NativeMapped.class.isAssignableFrom(var4)) {
            throw new IllegalArgumentException("Reading array of " + var4 + " from memory not supported");
         }

         NativeMapped[] var13 = (NativeMapped[])var3;
         NativeMappedConverter var15 = NativeMappedConverter.getInstance(var4);
         int var17 = Native.getNativeSize(var3.getClass(), var3) / var13.length;

         for (int var18 = 0; var18 < var13.length; var18++) {
            Object var11 = this.getValue(var1 + (long)(var17 * var18), var15.nativeType(), var13[var18]);
            var13[var18] = (NativeMapped)var15.fromNative(var11, new FromNativeContext(var4));
         }
      }
   }

   public byte getByte(long var1) {
      return Native.getByte(this, this.peer, var1);
   }

   public char getChar(long var1) {
      return Native.getChar(this, this.peer, var1);
   }

   public short getShort(long var1) {
      return Native.getShort(this, this.peer, var1);
   }

   public int getInt(long var1) {
      return Native.getInt(this, this.peer, var1);
   }

   public long getLong(long var1) {
      return Native.getLong(this, this.peer, var1);
   }

   public NativeLong getNativeLong(long var1) {
      return new NativeLong(NativeLong.SIZE == 8 ? this.getLong(var1) : (long)this.getInt(var1));
   }

   public float getFloat(long var1) {
      return Native.getFloat(this, this.peer, var1);
   }

   public double getDouble(long var1) {
      return Native.getDouble(this, this.peer, var1);
   }

   public Pointer getPointer(long var1) {
      return Native.getPointer(this.peer + var1);
   }

   public ByteBuffer getByteBuffer(long var1, long var3) {
      return Native.getDirectByteBuffer(this, this.peer, var1, var3).order(ByteOrder.nativeOrder());
   }

   @Deprecated
   public String getString(long var1, boolean var3) {
      return var3 ? this.getWideString(var1) : this.getString(var1);
   }

   public String getWideString(long var1) {
      return Native.getWideString(this, this.peer, var1);
   }

   public String getString(long var1) {
      return this.getString(var1, Native.getDefaultStringEncoding());
   }

   public String getString(long var1, String var3) {
      return Native.getString(this, var1, var3);
   }

   public byte[] getByteArray(long var1, int var3) {
      byte[] var4 = new byte[var3];
      this.read(var1, var4, 0, var3);
      return var4;
   }

   public char[] getCharArray(long var1, int var3) {
      char[] var4 = new char[var3];
      this.read(var1, var4, 0, var3);
      return var4;
   }

   public short[] getShortArray(long var1, int var3) {
      short[] var4 = new short[var3];
      this.read(var1, var4, 0, var3);
      return var4;
   }

   public int[] getIntArray(long var1, int var3) {
      int[] var4 = new int[var3];
      this.read(var1, var4, 0, var3);
      return var4;
   }

   public long[] getLongArray(long var1, int var3) {
      long[] var4 = new long[var3];
      this.read(var1, var4, 0, var3);
      return var4;
   }

   public float[] getFloatArray(long var1, int var3) {
      float[] var4 = new float[var3];
      this.read(var1, var4, 0, var3);
      return var4;
   }

   public double[] getDoubleArray(long var1, int var3) {
      double[] var4 = new double[var3];
      this.read(var1, var4, 0, var3);
      return var4;
   }

   public Pointer[] getPointerArray(long var1) {
      ArrayList var3 = new ArrayList();
      int var4 = 0;

      for (Pointer var5 = this.getPointer(var1); var5 != null; var5 = this.getPointer(var1 + (long)var4)) {
         var3.add(var5);
         var4 += SIZE;
      }

      return var3.toArray(new Pointer[var3.size()]);
   }

   public Pointer[] getPointerArray(long var1, int var3) {
      Pointer[] var4 = new Pointer[var3];
      this.read(var1, var4, 0, var3);
      return var4;
   }

   public String[] getStringArray(long var1) {
      return this.getStringArray(var1, -1, Native.getDefaultStringEncoding());
   }

   public String[] getStringArray(long var1, String var3) {
      return this.getStringArray(var1, -1, var3);
   }

   public String[] getStringArray(long var1, int var3) {
      return this.getStringArray(var1, var3, Native.getDefaultStringEncoding());
   }

   @Deprecated
   public String[] getStringArray(long var1, boolean var3) {
      return this.getStringArray(var1, -1, var3);
   }

   public String[] getWideStringArray(long var1) {
      return this.getWideStringArray(var1, -1);
   }

   public String[] getWideStringArray(long var1, int var3) {
      return this.getStringArray(var1, var3, "--WIDE-STRING--");
   }

   @Deprecated
   public String[] getStringArray(long var1, int var3, boolean var4) {
      return this.getStringArray(var1, var3, var4 ? "--WIDE-STRING--" : Native.getDefaultStringEncoding());
   }

   public String[] getStringArray(long var1, int var3, String var4) {
      ArrayList var5 = new ArrayList();
      int var7 = 0;
      Pointer var6;
      if (var3 != -1) {
         var6 = this.getPointer(var1 + (long)var7);
         int var11 = 0;

         while (var11++ < var3) {
            String var9 = var6 == null ? null : ("--WIDE-STRING--".equals(var4) ? var6.getWideString(0L) : var6.getString(0L, var4));
            var5.add(var9);
            if (var11 < var3) {
               var7 += SIZE;
               var6 = this.getPointer(var1 + (long)var7);
            }
         }
      } else {
         while ((var6 = this.getPointer(var1 + (long)var7)) != null) {
            String var8 = var6 == null ? null : ("--WIDE-STRING--".equals(var4) ? var6.getWideString(0L) : var6.getString(0L, var4));
            var5.add(var8);
            var7 += SIZE;
         }
      }

      return var5.toArray(new String[var5.size()]);
   }

   void setValue(long var1, Object var3, Class<?> var4) {
      if (var4 == boolean.class || var4 == Boolean.class) {
         this.setInt(var1, Boolean.TRUE.equals(var3) ? -1 : 0);
      } else if (var4 != byte.class && var4 != Byte.class) {
         if (var4 != short.class && var4 != Short.class) {
            if (var4 != char.class && var4 != Character.class) {
               if (var4 != int.class && var4 != Integer.class) {
                  if (var4 != long.class && var4 != Long.class) {
                     if (var4 != float.class && var4 != Float.class) {
                        if (var4 != double.class && var4 != Double.class) {
                           if (var4 == Pointer.class) {
                              this.setPointer(var1, (Pointer)var3);
                           } else if (var4 == String.class) {
                              this.setPointer(var1, (Pointer)var3);
                           } else if (var4 == WString.class) {
                              this.setPointer(var1, (Pointer)var3);
                           } else if (Structure.class.isAssignableFrom(var4)) {
                              Structure var5 = (Structure)var3;
                              if (Structure.ByReference.class.isAssignableFrom(var4)) {
                                 this.setPointer(var1, var5 == null ? null : var5.getPointer());
                                 if (var5 != null) {
                                    var5.autoWrite();
                                 }
                              } else {
                                 var5.useMemory(this, (int)var1, true);
                                 var5.write();
                              }
                           } else if (Callback.class.isAssignableFrom(var4)) {
                              this.setPointer(var1, CallbackReference.getFunctionPointer((Callback)var3));
                           } else if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(var4)) {
                              Pointer var8 = var3 == null ? null : Native.getDirectBufferPointer((Buffer)var3);
                              this.setPointer(var1, var8);
                           } else if (NativeMapped.class.isAssignableFrom(var4)) {
                              NativeMappedConverter var7 = NativeMappedConverter.getInstance(var4);
                              Class var6 = var7.nativeType();
                              this.setValue(var1, var7.toNative(var3, new ToNativeContext()), var6);
                           } else {
                              if (!var4.isArray()) {
                                 throw new IllegalArgumentException("Writing " + var4 + " to memory is not supported");
                              }

                              this.writeArray(var1, var3, var4.getComponentType());
                           }
                        } else {
                           this.setDouble(var1, var3 == null ? 0.0 : (Double)var3);
                        }
                     } else {
                        this.setFloat(var1, var3 == null ? 0.0F : (Float)var3);
                     }
                  } else {
                     this.setLong(var1, var3 == null ? 0L : (Long)var3);
                  }
               } else {
                  this.setInt(var1, var3 == null ? 0 : (Integer)var3);
               }
            } else {
               this.setChar(var1, var3 == null ? '\u0000' : (Character)var3);
            }
         } else {
            this.setShort(var1, var3 == null ? 0 : (Short)var3);
         }
      } else {
         this.setByte(var1, var3 == null ? 0 : (Byte)var3);
      }
   }

   private void writeArray(long var1, Object var3, Class<?> var4) {
      if (var4 == byte.class) {
         byte[] var5 = (byte[])var3;
         this.write(var1, var5, 0, var5.length);
      } else if (var4 == short.class) {
         short[] var11 = (short[])var3;
         this.write(var1, var11, 0, var11.length);
      } else if (var4 == char.class) {
         char[] var12 = (char[])var3;
         this.write(var1, var12, 0, var12.length);
      } else if (var4 == int.class) {
         int[] var13 = (int[])var3;
         this.write(var1, var13, 0, var13.length);
      } else if (var4 == long.class) {
         long[] var14 = (long[])var3;
         this.write(var1, var14, 0, var14.length);
      } else if (var4 == float.class) {
         float[] var15 = (float[])var3;
         this.write(var1, var15, 0, var15.length);
      } else if (var4 == double.class) {
         double[] var16 = (double[])var3;
         this.write(var1, var16, 0, var16.length);
      } else if (Pointer.class.isAssignableFrom(var4)) {
         Pointer[] var17 = (Pointer[])var3;
         this.write(var1, var17, 0, var17.length);
      } else if (Structure.class.isAssignableFrom(var4)) {
         Structure[] var18 = (Structure[])var3;
         if (Structure.ByReference.class.isAssignableFrom(var4)) {
            Pointer[] var6 = new Pointer[var18.length];

            for (int var7 = 0; var7 < var18.length; var7++) {
               if (var18[var7] == null) {
                  var6[var7] = null;
               } else {
                  var6[var7] = var18[var7].getPointer();
                  var18[var7].write();
               }
            }

            this.write(var1, var6, 0, var6.length);
         } else {
            Structure var20 = var18[0];
            if (var20 == null) {
               var20 = Structure.newInstance(var4, this.share(var1));
               var18[0] = var20;
            } else {
               var20.useMemory(this, (int)var1, true);
            }

            var20.write();
            Structure[] var22 = var20.toArray(var18.length);

            for (int var8 = 1; var8 < var18.length; var8++) {
               if (var18[var8] == null) {
                  var18[var8] = var22[var8];
               } else {
                  var18[var8].useMemory(this, (int)(var1 + (long)(var8 * var18[var8].size())), true);
               }

               var18[var8].write();
            }
         }
      } else {
         if (!NativeMapped.class.isAssignableFrom(var4)) {
            throw new IllegalArgumentException("Writing array of " + var4 + " to memory not supported");
         }

         NativeMapped[] var19 = (NativeMapped[])var3;
         NativeMappedConverter var21 = NativeMappedConverter.getInstance(var4);
         Class var23 = var21.nativeType();
         int var24 = Native.getNativeSize(var3.getClass(), var3) / var19.length;

         for (int var9 = 0; var9 < var19.length; var9++) {
            Object var10 = var21.toNative(var19[var9], new ToNativeContext());
            this.setValue(var1 + (long)(var9 * var24), var10, var23);
         }
      }
   }

   public void setMemory(long var1, long var3, byte var5) {
      Native.setMemory(this, this.peer, var1, var3, var5);
   }

   public void setByte(long var1, byte var3) {
      Native.setByte(this, this.peer, var1, var3);
   }

   public void setShort(long var1, short var3) {
      Native.setShort(this, this.peer, var1, var3);
   }

   public void setChar(long var1, char var3) {
      Native.setChar(this, this.peer, var1, var3);
   }

   public void setInt(long var1, int var3) {
      Native.setInt(this, this.peer, var1, var3);
   }

   public void setLong(long var1, long var3) {
      Native.setLong(this, this.peer, var1, var3);
   }

   public void setNativeLong(long var1, NativeLong var3) {
      if (NativeLong.SIZE == 8) {
         this.setLong(var1, var3.longValue());
      } else {
         this.setInt(var1, var3.intValue());
      }
   }

   public void setFloat(long var1, float var3) {
      Native.setFloat(this, this.peer, var1, var3);
   }

   public void setDouble(long var1, double var3) {
      Native.setDouble(this, this.peer, var1, var3);
   }

   public void setPointer(long var1, Pointer var3) {
      Native.setPointer(this, this.peer, var1, var3 != null ? var3.peer : 0L);
   }

   @Deprecated
   public void setString(long var1, String var3, boolean var4) {
      if (var4) {
         this.setWideString(var1, var3);
      } else {
         this.setString(var1, var3);
      }
   }

   public void setWideString(long var1, String var3) {
      Native.setWideString(this, this.peer, var1, var3);
   }

   public void setString(long var1, WString var3) {
      this.setWideString(var1, var3 == null ? null : var3.toString());
   }

   public void setString(long var1, String var3) {
      this.setString(var1, var3, Native.getDefaultStringEncoding());
   }

   public void setString(long var1, String var3, String var4) {
      byte[] var5 = Native.getBytes(var3, var4);
      this.write(var1, var5, 0, var5.length);
      this.setByte(var1 + (long)var5.length, (byte)0);
   }

   public String dump(long var1, int var3) {
      byte var4 = 4;
      String var5 = "memory dump";
      StringWriter var6 = new StringWriter("memory dump".length() + 2 + var3 * 2 + var3 / 4 * 4);
      PrintWriter var7 = new PrintWriter(var6);
      var7.println("memory dump");

      for (int var8 = 0; var8 < var3; var8++) {
         byte var9 = this.getByte(var1 + (long)var8);
         if (var8 % 4 == 0) {
            var7.print("[");
         }

         if (var9 >= 0 && var9 < 16) {
            var7.print("0");
         }

         var7.print(Integer.toHexString(var9 & 255));
         if (var8 % 4 == 3 && var8 < var3 - 1) {
            var7.println("]");
         }
      }

      if (var6.getBuffer().charAt(var6.getBuffer().length() - 2) != ']') {
         var7.println("]");
      }

      return var6.toString();
   }

   @Override
   public String toString() {
      return "native@0x" + Long.toHexString(this.peer);
   }

   public static long nativeValue(Pointer var0) {
      return var0 == null ? 0L : var0.peer;
   }

   public static void nativeValue(Pointer var0, long var1) {
      var0.peer = var1;
   }

   static {
      if ((SIZE = Native.POINTER_SIZE) == 0) {
         throw new Error("Native library not initialized");
      } else {
         NULL = null;
      }
   }

   private static class Opaque extends Pointer {
      private final String MSG = "This pointer is opaque: " + this;

      private Opaque(long var1) {
         super(var1);
      }

      @Override
      public Pointer share(long var1, long var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void clear(long var1) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public long indexOf(long var1, byte var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void read(long var1, byte[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void read(long var1, char[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void read(long var1, short[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void read(long var1, int[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void read(long var1, long[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void read(long var1, float[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void read(long var1, double[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void read(long var1, Pointer[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void write(long var1, byte[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void write(long var1, char[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void write(long var1, short[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void write(long var1, int[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void write(long var1, long[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void write(long var1, float[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void write(long var1, double[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void write(long var1, Pointer[] var3, int var4, int var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public ByteBuffer getByteBuffer(long var1, long var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public byte getByte(long var1) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public char getChar(long var1) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public short getShort(long var1) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public int getInt(long var1) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public long getLong(long var1) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public float getFloat(long var1) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public double getDouble(long var1) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public Pointer getPointer(long var1) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public String getString(long var1, String var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public String getWideString(long var1) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void setByte(long var1, byte var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void setChar(long var1, char var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void setShort(long var1, short var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void setInt(long var1, int var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void setLong(long var1, long var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void setFloat(long var1, float var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void setDouble(long var1, double var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void setPointer(long var1, Pointer var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void setString(long var1, String var3, String var4) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void setWideString(long var1, String var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public void setMemory(long var1, long var3, byte var5) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public String dump(long var1, int var3) {
         throw new UnsupportedOperationException(this.MSG);
      }

      @Override
      public String toString() {
         return "const@0x" + Long.toHexString(this.peer);
      }
   }
}
