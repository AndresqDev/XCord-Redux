package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.inject.BeanLocator;
import org.eclipse.sisu.inject.TypeArguments;

final class BeanProviders {
   final Provider<BeanLocator> locator;

   BeanProviders(Binder var1) {
      this.locator = var1.getProvider(BeanLocator.class);
   }

   public <K extends Annotation, V> Provider<Iterable<? extends BeanEntry<K, V>>> beanEntriesOf(final Key<V> var1) {
      return new Provider<Iterable<? extends BeanEntry<K, V>>>() {
         public Iterable<? extends BeanEntry<K, V>> get() {
            return ((BeanLocator)BeanProviders.this.locator.get()).locate(var1);
         }
      };
   }

   public <K extends Annotation, V> Provider<Iterable<Entry<K, V>>> entriesOf(Key var1) {
      TypeLiteral var2 = var1.getTypeLiteral();
      Class var3 = var2.getRawType();
      if (xcord.Provider.class != var3 && Provider.class != var3) {
         return (Provider<Iterable<Entry<K, V>>>)this.beanEntriesOf(var1);
      } else {
         final Provider var4 = this.beanEntriesOf(var1.ofType(TypeArguments.get(var2, 0)));
         return new Provider<Iterable<Entry<K, V>>>() {
            public Iterable<Entry<K, V>> get() {
               return new ProviderIterableAdapter<>((Iterable<BeanEntry<K, V>>)var4.get());
            }
         };
      }
   }

   public <K extends Annotation, V> Provider<List<V>> listOf(Key<V> var1) {
      final Provider var2 = this.entriesOf(var1);
      return new Provider<List<V>>() {
         public List<V> get() {
            return new EntryListAdapter<>((Iterable<? extends Entry<?, V>>)var2.get());
         }
      };
   }

   public <K extends Annotation, V> Provider<Set<V>> setOf(Key<V> var1) {
      final Provider var2 = this.entriesOf(var1);
      return new Provider<Set<V>>() {
         public Set<V> get() {
            return new EntrySetAdapter<>((Iterable<? extends Entry<?, V>>)var2.get());
         }
      };
   }

   public <K extends Annotation, V> Provider<Map<K, V>> mapOf(Key<V> var1) {
      final Provider var2 = this.entriesOf(var1);
      return new Provider<Map<K, V>>() {
         public Map<K, V> get() {
            return new EntryMapAdapter<>((Iterable<? extends Entry<K, V>>)var2.get());
         }
      };
   }

   public <V> Provider<Map<String, V>> stringMapOf(TypeLiteral<V> var1) {
      final Provider var2 = this.entriesOf(Key.get(var1, Named.class));
      return new Provider<Map<String, V>>() {
         public Map<String, V> get() {
            return new EntryMapAdapter<>(new NamedIterableAdapter<>((Iterable<Entry<Named, V>>)var2.get()));
         }
      };
   }

   <V> Provider<V> firstOf(Key<V> var1) {
      final Provider var2 = this.beanEntriesOf(var1);
      return new Provider<V>() {
         public V get() {
            return BeanProviders.firstOf((Iterable<? extends Entry<?, V>>)var2.get());
         }
      };
   }

   public <V> Provider<V> placeholderOf(Key<V> var1) {
      return new PlaceholderBeanProvider<>(this.locator, var1);
   }

   public static <V> V firstOf(Iterable<? extends Entry<?, V>> var0) {
      Iterator var1 = var0.iterator();
      return (V)(var1.hasNext() ? ((Entry)var1.next()).getValue() : null);
   }
}
