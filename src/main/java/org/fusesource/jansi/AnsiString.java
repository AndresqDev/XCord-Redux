package org.fusesource.jansi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AnsiString implements CharSequence {
   private final CharSequence encoded;
   private final CharSequence plain;

   public AnsiString(CharSequence var1) {
      assert var1 != null;

      this.encoded = var1;
      this.plain = this.chew(var1);
   }

   private CharSequence chew(CharSequence var1) {
      assert var1 != null;

      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      AnsiOutputStream var3 = new AnsiOutputStream(var2);

      try {
         var3.write(var1.toString().getBytes());
         var3.flush();
         var3.close();
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }

      return new String(var2.toByteArray());
   }

   public CharSequence getEncoded() {
      return this.encoded;
   }

   public CharSequence getPlain() {
      return this.plain;
   }

   @Override
   public char charAt(int var1) {
      return this.getEncoded().charAt(var1);
   }

   @Override
   public CharSequence subSequence(int var1, int var2) {
      return this.getEncoded().subSequence(var1, var2);
   }

   @Override
   public int length() {
      return this.getPlain().length();
   }

   @Override
   public boolean equals(Object var1) {
      return this.getEncoded().equals(var1);
   }

   @Override
   public int hashCode() {
      return this.getEncoded().hashCode();
   }

   @Override
   public String toString() {
      return this.getEncoded().toString();
   }
}
