package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import java.util.Arrays;

final class OpenSslSessionId {
   private final byte[] id;
   private final int hashCode;
   static final OpenSslSessionId NULL_ID = new OpenSslSessionId(EmptyArrays.EMPTY_BYTES);

   OpenSslSessionId(byte[] var1) {
      this.id = var1;
      this.hashCode = Arrays.hashCode(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof OpenSslSessionId) ? false : Arrays.equals(this.id, ((OpenSslSessionId)var1).id);
      }
   }

   @Override
   public String toString() {
      return "OpenSslSessionId{id=" + Arrays.toString(this.id) + '}';
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   byte[] cloneBytes() {
      return (byte[])this.id.clone();
   }
}
