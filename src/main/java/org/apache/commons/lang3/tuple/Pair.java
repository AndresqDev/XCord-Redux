package org.apache.commons.lang3.tuple;

import java.io.Serializable;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.commons.lang3.builder.CompareToBuilder;

public abstract class Pair<L, R> implements Serializable, Comparable<Pair<L, R>>, Entry<L, R> {
   private static final long serialVersionUID = 4954918890077093841L;

   public static <L, R> Pair<L, R> of(L var0, R var1) {
      return new ImmutablePair<>((L)var0, (R)var1);
   }

   public abstract L getLeft();

   public abstract R getRight();

   @Override
   public final L getKey() {
      return this.getLeft();
   }

   @Override
   public R getValue() {
      return this.getRight();
   }

   public int compareTo(Pair<L, R> var1) {
      return new CompareToBuilder().append(this.getLeft(), var1.getLeft()).append(this.getRight(), var1.getRight()).toComparison();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Entry)) {
         return false;
      } else {
         Entry var2 = (Entry)var1;
         return Objects.equals(this.getKey(), var2.getKey()) && Objects.equals(this.getValue(), var2.getValue());
      }
   }

   @Override
   public int hashCode() {
      return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
   }

   @Override
   public String toString() {
      return "(" + this.getLeft() + ',' + this.getRight() + ')';
   }

   public String toString(String var1) {
      return String.format(var1, this.getLeft(), this.getRight());
   }
}
