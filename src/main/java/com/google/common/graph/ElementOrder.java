package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.errorprone.annotations.Immutable;
import java.util.Comparator;
import java.util.Map;

@Immutable
@ElementTypesAreNonnullByDefault
@Beta
public final class ElementOrder<T> {
   private final ElementOrder.Type type;
   private final Comparator<T> comparator;

   private ElementOrder(ElementOrder.Type var1, Comparator<T> var2) {
      this.type = Preconditions.checkNotNull(var1);
      this.comparator = var2;
      Preconditions.checkState(var1 == ElementOrder.Type.SORTED == (var2 != null));
   }

   public static <S> ElementOrder<S> unordered() {
      return new ElementOrder(ElementOrder.Type.UNORDERED, null);
   }

   public static <S> ElementOrder<S> stable() {
      return new ElementOrder(ElementOrder.Type.STABLE, null);
   }

   public static <S> ElementOrder<S> insertion() {
      return new ElementOrder(ElementOrder.Type.INSERTION, null);
   }

   public static <S extends Comparable<? super S>> ElementOrder<S> natural() {
      return new ElementOrder<>(ElementOrder.Type.SORTED, Ordering.natural());
   }

   public static <S> ElementOrder<S> sorted(Comparator<S> var0) {
      return new ElementOrder(ElementOrder.Type.SORTED, Preconditions.checkNotNull(var0));
   }

   public ElementOrder.Type type() {
      return this.type;
   }

   public Comparator<T> comparator() {
      if (this.comparator != null) {
         return this.comparator;
      } else {
         throw new UnsupportedOperationException("This ordering does not define a comparator.");
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ElementOrder)) {
         return false;
      } else {
         ElementOrder var2 = (ElementOrder)var1;
         return this.type == var2.type && Objects.equal(this.comparator, var2.comparator);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.type, this.comparator);
   }

   @Override
   public String toString() {
      MoreObjects.ToStringHelper var1 = MoreObjects.toStringHelper(this).add("type", this.type);
      if (this.comparator != null) {
         var1.add("comparator", this.comparator);
      }

      return var1.toString();
   }

   <K extends T, V> Map<K, V> createMap(int var1) {
      switch (this.type) {
         case UNORDERED:
            return Maps.newHashMapWithExpectedSize(var1);
         case INSERTION:
         case STABLE:
            return Maps.newLinkedHashMapWithExpectedSize(var1);
         case SORTED:
            return Maps.newTreeMap(this.comparator());
         default:
            throw new AssertionError();
      }
   }

   <T1 extends T> ElementOrder<T1> cast() {
      return this;
   }

   public static enum Type {
      UNORDERED,
      STABLE,
      INSERTION,
      SORTED;
   }
}
