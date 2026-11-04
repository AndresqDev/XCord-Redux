package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public enum BoundType {
   OPEN(false),
   CLOSED(true);

   final boolean inclusive;

   private BoundType(boolean var3) {
      this.inclusive = var3;
   }

   static BoundType forBoolean(boolean var0) {
      return var0 ? CLOSED : OPEN;
   }
}
