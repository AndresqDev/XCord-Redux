package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.Spliterator;
import java.util.Map.Entry;
import java.util.function.Consumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
abstract class ImmutableMapEntrySet<K, V> extends ImmutableSet.CachingAsList<Entry<K, V>> {
   abstract ImmutableMap<K, V> map();

   @Override
   public int size() {
      return this.map().size();
   }

   @Override
   public boolean contains(Object var1) {
      if (!(var1 instanceof Entry)) {
         return false;
      } else {
         Entry var2 = (Entry)var1;
         Object var3 = this.map().get(var2.getKey());
         return var3 != null && var3.equals(var2.getValue());
      }
   }

   @Override
   boolean isPartialView() {
      return this.map().isPartialView();
   }

   @GwtIncompatible
   @Override
   boolean isHashCodeFast() {
      return this.map().isHashCodeFast();
   }

   @Override
   public int hashCode() {
      return this.map().hashCode();
   }

   @GwtIncompatible
   @Override
   Object writeReplace() {
      return new ImmutableMapEntrySet.EntrySetSerializedForm<>(this.map());
   }

   @GwtIncompatible
   private static class EntrySetSerializedForm<K, V> implements Serializable {
      final ImmutableMap<K, V> map;
      private static final long serialVersionUID = 0L;

      EntrySetSerializedForm(ImmutableMap<K, V> var1) {
         this.map = var1;
      }

      Object readResolve() {
         return this.map.entrySet();
      }
   }

   static final class RegularEntrySet<K, V> extends ImmutableMapEntrySet<K, V> {
      private final transient ImmutableMap<K, V> map;
      private final transient ImmutableList<Entry<K, V>> entries;

      RegularEntrySet(ImmutableMap<K, V> var1, Entry<K, V>[] var2) {
         this(var1, ImmutableList.asImmutableList(var2));
      }

      RegularEntrySet(ImmutableMap<K, V> var1, ImmutableList<Entry<K, V>> var2) {
         this.map = var1;
         this.entries = var2;
      }

      @Override
      ImmutableMap<K, V> map() {
         return this.map;
      }

      @GwtIncompatible("not used in GWT")
      @Override
      int copyIntoArray(Object[] var1, int var2) {
         return this.entries.copyIntoArray(var1, var2);
      }

      @Override
      public UnmodifiableIterator<Entry<K, V>> iterator() {
         return this.entries.iterator();
      }

      @Override
      public Spliterator<Entry<K, V>> spliterator() {
         return this.entries.spliterator();
      }

      @Override
      public void forEach(Consumer<? super Entry<K, V>> var1) {
         this.entries.forEach(var1);
      }

      @Override
      ImmutableList<Entry<K, V>> createAsList() {
         return new RegularImmutableAsList<>(this, this.entries);
      }
   }
}
