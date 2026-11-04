package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.Immutable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

@Immutable
@ElementTypesAreNonnullByDefault
abstract class AbstractCompositeHashFunction extends AbstractHashFunction {
   final HashFunction[] functions;
   private static final long serialVersionUID = 0L;

   AbstractCompositeHashFunction(HashFunction... var1) {
      for (HashFunction var5 : var1) {
         Preconditions.checkNotNull(var5);
      }

      this.functions = var1;
   }

   abstract HashCode makeHash(Hasher[] var1);

   @Override
   public Hasher newHasher() {
      Hasher[] var1 = new Hasher[this.functions.length];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = this.functions[var2].newHasher();
      }

      return this.fromHashers(var1);
   }

   @Override
   public Hasher newHasher(int var1) {
      Preconditions.checkArgument(var1 >= 0);
      Hasher[] var2 = new Hasher[this.functions.length];

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = this.functions[var3].newHasher(var1);
      }

      return this.fromHashers(var2);
   }

   private Hasher fromHashers(final Hasher[] var1) {
      return new Hasher() {
         @Override
         public Hasher putByte(byte var1x) {
            for (Hasher var5 : var1) {
               var5.putByte(var1x);
            }

            return this;
         }

         @Override
         public Hasher putBytes(byte[] var1x) {
            for (Hasher var5 : var1) {
               var5.putBytes(var1x);
            }

            return this;
         }

         @Override
         public Hasher putBytes(byte[] var1x, int var2, int var3) {
            for (Hasher var7 : var1) {
               var7.putBytes(var1x, var2, var3);
            }

            return this;
         }

         @Override
         public Hasher putBytes(ByteBuffer var1x) {
            int var2 = var1x.position();

            for (Hasher var6 : var1) {
               Java8Compatibility.position(var1x, var2);
               var6.putBytes(var1x);
            }

            return this;
         }

         @Override
         public Hasher putShort(short var1x) {
            for (Hasher var5 : var1) {
               var5.putShort(var1x);
            }

            return this;
         }

         @Override
         public Hasher putInt(int var1x) {
            for (Hasher var5 : var1) {
               var5.putInt(var1x);
            }

            return this;
         }

         @Override
         public Hasher putLong(long var1x) {
            for (Hasher var6 : var1) {
               var6.putLong(var1x);
            }

            return this;
         }

         @Override
         public Hasher putFloat(float var1x) {
            for (Hasher var5 : var1) {
               var5.putFloat(var1x);
            }

            return this;
         }

         @Override
         public Hasher putDouble(double var1x) {
            for (Hasher var6 : var1) {
               var6.putDouble(var1x);
            }

            return this;
         }

         @Override
         public Hasher putBoolean(boolean var1x) {
            for (Hasher var5 : var1) {
               var5.putBoolean(var1x);
            }

            return this;
         }

         @Override
         public Hasher putChar(char var1x) {
            for (Hasher var5 : var1) {
               var5.putChar(var1x);
            }

            return this;
         }

         @Override
         public Hasher putUnencodedChars(CharSequence var1x) {
            for (Hasher var5 : var1) {
               var5.putUnencodedChars(var1x);
            }

            return this;
         }

         @Override
         public Hasher putString(CharSequence var1x, Charset var2) {
            for (Hasher var6 : var1) {
               var6.putString(var1x, var2);
            }

            return this;
         }

         @Override
         public <T> Hasher putObject(@ParametricNullness T var1x, Funnel<? super T> var2) {
            for (Hasher var6 : var1) {
               var6.putObject(var1x, var2);
            }

            return this;
         }

         @Override
         public HashCode hash() {
            return AbstractCompositeHashFunction.this.makeHash(var1);
         }
      };
   }
}
