package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
public final class HashMultimap<K, V> extends HashMultimapGwtSerializationDependencies<K, V> {
   private static final int DEFAULT_VALUES_PER_KEY = 2;
   @VisibleForTesting
   transient int expectedValuesPerKey = 2;
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   public static <K, V> HashMultimap<K, V> create() {
      return new HashMultimap<>();
   }

   public static <K, V> HashMultimap<K, V> create(int var0, int var1) {
      return new HashMultimap<>(var0, var1);
   }

   public static <K, V> HashMultimap<K, V> create(Multimap<? extends K, ? extends V> var0) {
      return new HashMultimap<>(var0);
   }

   private HashMultimap() {
      this(12, 2);
   }

   private HashMultimap(int var1, int var2) {
      super(Platform.newHashMapWithExpectedSize(var1));
      Preconditions.checkArgument(var2 >= 0);
      this.expectedValuesPerKey = var2;
   }

   private HashMultimap(Multimap<? extends K, ? extends V> var1) {
      super(Platform.newHashMapWithExpectedSize(var1.keySet().size()));
      this.putAll(var1);
   }

   @Override
   Set<V> createCollection() {
      return Platform.newHashSetWithExpectedSize(this.expectedValuesPerKey);
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      Serialization.writeMultimap(this, var1);
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.expectedValuesPerKey = 2;
      int var2 = Serialization.readCount(var1);
      Map var3 = Platform.newHashMapWithExpectedSize(12);
      this.setMap(var3);
      Serialization.populateMultimap(this, var1, var2);
   }
}
