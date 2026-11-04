package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public enum RemovalCause {
   EXPLICIT {
      @Override
      boolean wasEvicted() {
         return false;
      }
   },
   REPLACED {
      @Override
      boolean wasEvicted() {
         return false;
      }
   },
   COLLECTED {
      @Override
      boolean wasEvicted() {
         return true;
      }
   },
   EXPIRED {
      @Override
      boolean wasEvicted() {
         return true;
      }
   },
   SIZE {
      @Override
      boolean wasEvicted() {
         return true;
      }
   };

   private RemovalCause() {
   }

   abstract boolean wasEvicted();
}
