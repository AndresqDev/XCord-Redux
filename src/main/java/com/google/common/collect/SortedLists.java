package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

@ElementTypesAreNonnullByDefault
@GwtCompatible
@Beta
final class SortedLists {
   private SortedLists() {
   }

   public static <E extends Comparable> int binarySearch(
      List<? extends E> var0, E var1, SortedLists.KeyPresentBehavior var2, SortedLists.KeyAbsentBehavior var3
   ) {
      Preconditions.checkNotNull(var1);
      return binarySearch(var0, var1, Ordering.natural(), var2, var3);
   }

   public static <E, K extends Comparable> int binarySearch(
      List<E> var0, Function<? super E, K> var1, K var2, SortedLists.KeyPresentBehavior var3, SortedLists.KeyAbsentBehavior var4
   ) {
      Preconditions.checkNotNull(var2);
      return binarySearch(var0, var1, var2, Ordering.natural(), var3, var4);
   }

   public static <E, K> int binarySearch(
      List<E> var0,
      Function<? super E, K> var1,
      @ParametricNullness K var2,
      Comparator<? super K> var3,
      SortedLists.KeyPresentBehavior var4,
      SortedLists.KeyAbsentBehavior var5
   ) {
      return binarySearch(Lists.transform(var0, var1), var2, var3, var4, var5);
   }

   public static <E> int binarySearch(
      List<? extends E> var0, @ParametricNullness E var1, Comparator<? super E> var2, SortedLists.KeyPresentBehavior var3, SortedLists.KeyAbsentBehavior var4
   ) {
      Preconditions.checkNotNull(var2);
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var3);
      Preconditions.checkNotNull(var4);
      if (!(var0 instanceof RandomAccess)) {
         var0 = Lists.newArrayList((Iterable<? extends E>)var0);
      }

      int var5 = 0;
      int var6 = var0.size() - 1;

      while (var5 <= var6) {
         int var7 = var5 + var6 >>> 1;
         int var8 = var2.compare(var1, var0.get(var7));
         if (var8 < 0) {
            var6 = var7 - 1;
         } else {
            if (var8 <= 0) {
               return var5 + var3.resultIndex(var2, var1, var0.subList(var5, var6 + 1), var7 - var5);
            }

            var5 = var7 + 1;
         }
      }

      return var4.resultIndex(var5);
   }

   static enum KeyAbsentBehavior {
      NEXT_LOWER {
         @Override
         int resultIndex(int var1) {
            return var1 - 1;
         }
      },
      NEXT_HIGHER {
         @Override
         public int resultIndex(int var1) {
            return var1;
         }
      },
      INVERTED_INSERTION_INDEX {
         @Override
         public int resultIndex(int var1) {
            return ~var1;
         }
      };

      private KeyAbsentBehavior() {
      }

      abstract int resultIndex(int var1);
   }

   static enum KeyPresentBehavior {
      ANY_PRESENT {
         @Override
         <E> int resultIndex(Comparator<? super E> var1, @ParametricNullness E var2, List<? extends E> var3, int var4) {
            return var4;
         }
      },
      LAST_PRESENT {
         @Override
         <E> int resultIndex(Comparator<? super E> var1, @ParametricNullness E var2, List<? extends E> var3, int var4) {
            int var5 = var4;
            int var6 = var3.size() - 1;

            while (var5 < var6) {
               int var7 = var5 + var6 + 1 >>> 1;
               int var8 = var1.compare(var3.get(var7), var2);
               if (var8 > 0) {
                  var6 = var7 - 1;
               } else {
                  var5 = var7;
               }
            }

            return var5;
         }
      },
      FIRST_PRESENT {
         @Override
         <E> int resultIndex(Comparator<? super E> var1, @ParametricNullness E var2, List<? extends E> var3, int var4) {
            int var5 = 0;
            int var6 = var4;

            while (var5 < var6) {
               int var7 = var5 + var6 >>> 1;
               int var8 = var1.compare(var3.get(var7), var2);
               if (var8 < 0) {
                  var5 = var7 + 1;
               } else {
                  var6 = var7;
               }
            }

            return var5;
         }
      },
      FIRST_AFTER {
         @Override
         public <E> int resultIndex(Comparator<? super E> var1, @ParametricNullness E var2, List<? extends E> var3, int var4) {
            return LAST_PRESENT.resultIndex(var1, var2, var3, var4) + 1;
         }
      },
      LAST_BEFORE {
         @Override
         public <E> int resultIndex(Comparator<? super E> var1, @ParametricNullness E var2, List<? extends E> var3, int var4) {
            return FIRST_PRESENT.resultIndex(var1, var2, var3, var4) - 1;
         }
      };

      private KeyPresentBehavior() {
      }

      abstract <E> int resultIndex(Comparator<? super E> var1, @ParametricNullness E var2, List<? extends E> var3, int var4);
   }
}
