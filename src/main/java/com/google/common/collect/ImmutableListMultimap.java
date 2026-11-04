package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
public class ImmutableListMultimap<K, V> extends ImmutableMultimap<K, V> implements ListMultimap<K, V> {
   @LazyInit
   @RetainedWith
   private transient ImmutableListMultimap<V, K> inverse;
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   public static <T, K, V> Collector<T, ?, ImmutableListMultimap<K, V>> toImmutableListMultimap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends V> var1
   ) {
      return CollectCollectors.toImmutableListMultimap(var0, var1);
   }

   public static <T, K, V> Collector<T, ?, ImmutableListMultimap<K, V>> flatteningToImmutableListMultimap(
      Function<? super T, ? extends K> var0, Function<? super T, ? extends Stream<? extends V>> var1
   ) {
      return CollectCollectors.flatteningToImmutableListMultimap(var0, var1);
   }

   public static <K, V> ImmutableListMultimap<K, V> of() {
      return EmptyImmutableListMultimap.INSTANCE;
   }

   public static <K, V> ImmutableListMultimap<K, V> of(K var0, V var1) {
      ImmutableListMultimap.Builder var2 = builder();
      var2.put(var0, var1);
      return var2.build();
   }

   public static <K, V> ImmutableListMultimap<K, V> of(K var0, V var1, K var2, V var3) {
      ImmutableListMultimap.Builder var4 = builder();
      var4.put(var0, var1);
      var4.put(var2, var3);
      return var4.build();
   }

   public static <K, V> ImmutableListMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5) {
      ImmutableListMultimap.Builder var6 = builder();
      var6.put(var0, var1);
      var6.put(var2, var3);
      var6.put(var4, var5);
      return var6.build();
   }

   public static <K, V> ImmutableListMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7) {
      ImmutableListMultimap.Builder var8 = builder();
      var8.put(var0, var1);
      var8.put(var2, var3);
      var8.put(var4, var5);
      var8.put(var6, var7);
      return var8.build();
   }

   public static <K, V> ImmutableListMultimap<K, V> of(K var0, V var1, K var2, V var3, K var4, V var5, K var6, V var7, K var8, V var9) {
      ImmutableListMultimap.Builder var10 = builder();
      var10.put(var0, var1);
      var10.put(var2, var3);
      var10.put(var4, var5);
      var10.put(var6, var7);
      var10.put(var8, var9);
      return var10.build();
   }

   public static <K, V> ImmutableListMultimap.Builder<K, V> builder() {
      return new ImmutableListMultimap.Builder<>();
   }

   public static <K, V> ImmutableListMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> var0) {
      if (var0.isEmpty()) {
         return of();
      } else {
         if (var0 instanceof ImmutableListMultimap) {
            ImmutableListMultimap var1 = (ImmutableListMultimap)var0;
            if (!var1.isPartialView()) {
               return var1;
            }
         }

         return fromMapEntries(var0.asMap().entrySet(), null);
      }
   }

   @Beta
   public static <K, V> ImmutableListMultimap<K, V> copyOf(Iterable<? extends Entry<? extends K, ? extends V>> var0) {
      return new ImmutableListMultimap.Builder<K, V>().putAll(var0).build();
   }

   static <K, V> ImmutableListMultimap<K, V> fromMapEntries(
      Collection<? extends Entry<? extends K, ? extends Collection<? extends V>>> var0, @Nullable Comparator<? super V> var1
   ) {
      if (var0.isEmpty()) {
         return of();
      } else {
         ImmutableMap.Builder var2 = new ImmutableMap.Builder(var0.size());
         int var3 = 0;

         for (Entry var5 : var0) {
            Object var6 = var5.getKey();
            Collection var7 = (Collection)var5.getValue();
            ImmutableList var8 = var1 == null ? ImmutableList.copyOf(var7) : ImmutableList.sortedCopyOf(var1, var7);
            if (!var8.isEmpty()) {
               var2.put(var6, var8);
               var3 += var8.size();
            }
         }

         return new ImmutableListMultimap<>(var2.build(), var3);
      }
   }

   ImmutableListMultimap(ImmutableMap<K, ImmutableList<V>> var1, int var2) {
      super(var1, var2);
   }

   public ImmutableList<V> get(K var1) {
      ImmutableList var2 = (ImmutableList)this.map.get(var1);
      return var2 == null ? ImmutableList.of() : var2;
   }

   public ImmutableListMultimap<V, K> inverse() {
      ImmutableListMultimap var1 = this.inverse;
      return var1 == null ? (this.inverse = this.invert()) : var1;
   }

   private ImmutableListMultimap<V, K> invert() {
      ImmutableListMultimap.Builder var1 = builder();

      for (Entry var3 : this.entries()) {
         var1.put(var3.getValue(), var3.getKey());
      }

      ImmutableListMultimap var4 = var1.build();
      var4.inverse = (ImmutableListMultimap<V, K>)this;
      return var4;
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   public final ImmutableList<V> removeAll(Object var1) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   public final ImmutableList<V> replaceValues(K var1, Iterable<? extends V> var2) {
      throw new UnsupportedOperationException();
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      Serialization.writeMultimap(this, var1);
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = var1.readInt();
      if (var2 < 0) {
         throw new InvalidObjectException(new StringBuilder(29).append("Invalid key count ").append(var2).toString());
      } else {
         ImmutableMap.Builder var3 = ImmutableMap.builder();
         int var4 = 0;

         for (int var5 = 0; var5 < var2; var5++) {
            Object var6 = var1.readObject();
            int var7 = var1.readInt();
            if (var7 <= 0) {
               throw new InvalidObjectException(new StringBuilder(31).append("Invalid value count ").append(var7).toString());
            }

            ImmutableList.Builder var8 = ImmutableList.builder();

            for (int var9 = 0; var9 < var7; var9++) {
               var8.add(var1.readObject());
            }

            var3.put(var6, var8.build());
            var4 += var7;
         }

         ImmutableMap var11;
         try {
            var11 = var3.build();
         } catch (IllegalArgumentException var10) {
            throw (InvalidObjectException)new InvalidObjectException(var10.getMessage()).initCause(var10);
         }

         ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set(this, var11);
         ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set(this, var4);
      }
   }

   public static final class Builder<K, V> extends ImmutableMultimap.Builder<K, V> {
      @CanIgnoreReturnValue
      public ImmutableListMultimap.Builder<K, V> put(K var1, V var2) {
         super.put((K)var1, (V)var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableListMultimap.Builder<K, V> put(Entry<? extends K, ? extends V> var1) {
         super.put(var1);
         return this;
      }

      @CanIgnoreReturnValue
      @Beta
      public ImmutableListMultimap.Builder<K, V> putAll(Iterable<? extends Entry<? extends K, ? extends V>> var1) {
         super.putAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableListMultimap.Builder<K, V> putAll(K var1, Iterable<? extends V> var2) {
         super.putAll((K)var1, var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableListMultimap.Builder<K, V> putAll(K var1, V... var2) {
         super.putAll((K)var1, (V[])var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableListMultimap.Builder<K, V> putAll(Multimap<? extends K, ? extends V> var1) {
         super.putAll(var1);
         return this;
      }

      @CanIgnoreReturnValue
      ImmutableListMultimap.Builder<K, V> combine(ImmutableMultimap.Builder<K, V> var1) {
         super.combine(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableListMultimap.Builder<K, V> orderKeysBy(Comparator<? super K> var1) {
         super.orderKeysBy(var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableListMultimap.Builder<K, V> orderValuesBy(Comparator<? super V> var1) {
         super.orderValuesBy(var1);
         return this;
      }

      public ImmutableListMultimap<K, V> build() {
         return (ImmutableListMultimap<K, V>)super.build();
      }
   }
}
