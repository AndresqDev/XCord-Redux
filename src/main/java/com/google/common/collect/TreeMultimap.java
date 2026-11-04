package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
public class TreeMultimap<K, V> extends AbstractSortedKeySortedSetMultimap<K, V> {
   private transient Comparator<? super K> keyComparator;
   private transient Comparator<? super V> valueComparator;
   @GwtIncompatible
   private static final long serialVersionUID = 0L;

   public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create() {
      return new TreeMultimap<>(Ordering.natural(), Ordering.natural());
   }

   public static <K, V> TreeMultimap<K, V> create(Comparator<? super K> var0, Comparator<? super V> var1) {
      return new TreeMultimap<>(Preconditions.checkNotNull(var0), Preconditions.checkNotNull(var1));
   }

   public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create(Multimap<? extends K, ? extends V> var0) {
      return new TreeMultimap<>(Ordering.natural(), Ordering.natural(), var0);
   }

   TreeMultimap(Comparator<? super K> var1, Comparator<? super V> var2) {
      super(new TreeMap<>(var1));
      this.keyComparator = var1;
      this.valueComparator = var2;
   }

   private TreeMultimap(Comparator<? super K> var1, Comparator<? super V> var2, Multimap<? extends K, ? extends V> var3) {
      this(var1, var2);
      this.putAll(var3);
   }

   @Override
   Map<K, Collection<V>> createAsMap() {
      return this.createMaybeNavigableAsMap();
   }

   @Override
   SortedSet<V> createCollection() {
      return new TreeSet<>(this.valueComparator);
   }

   @Override
   Collection<V> createCollection(@ParametricNullness K var1) {
      if (var1 == null) {
         this.keyComparator().compare((K)var1, (K)var1);
      }

      return super.createCollection((K)var1);
   }

   @Deprecated
   public Comparator<? super K> keyComparator() {
      return this.keyComparator;
   }

   @Override
   public Comparator<? super V> valueComparator() {
      return this.valueComparator;
   }

   @GwtIncompatible
   public NavigableSet<V> get(@ParametricNullness K var1) {
      return (NavigableSet<V>)super.get((K)var1);
   }

   public NavigableSet<K> keySet() {
      return (NavigableSet<K>)super.keySet();
   }

   public NavigableMap<K, Collection<V>> asMap() {
      return (NavigableMap<K, Collection<V>>)super.asMap();
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.keyComparator());
      var1.writeObject(this.valueComparator());
      Serialization.writeMultimap(this, var1);
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.keyComparator = Preconditions.checkNotNull((Comparator<? super K>)var1.readObject());
      this.valueComparator = Preconditions.checkNotNull((Comparator<? super V>)var1.readObject());
      this.setMap(new TreeMap<>(this.keyComparator));
      Serialization.populateMultimap(this, var1);
   }
}
