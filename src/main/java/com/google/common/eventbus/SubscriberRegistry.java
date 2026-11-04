package com.google.common.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.j2objc.annotations.Weak;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ElementTypesAreNonnullByDefault
final class SubscriberRegistry {
   private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers = Maps.newConcurrentMap();
   @Weak
   private final EventBus bus;
   private static final LoadingCache<Class<?>, ImmutableList<Method>> subscriberMethodsCache = CacheBuilder.newBuilder()
      .weakKeys()
      .build(new CacheLoader<Class<?>, ImmutableList<Method>>() {
         public ImmutableList<Method> load(Class<?> var1) throws Exception {
            return SubscriberRegistry.getAnnotatedMethodsNotCached(var1);
         }
      });
   private static final LoadingCache<Class<?>, ImmutableSet<Class<?>>> flattenHierarchyCache = CacheBuilder.newBuilder()
      .weakKeys()
      .build(new CacheLoader<Class<?>, ImmutableSet<Class<?>>>() {
         public ImmutableSet<Class<?>> load(Class<?> var1) {
            return ImmutableSet.copyOf(TypeToken.of(var1).getTypes().rawTypes());
         }
      });

   SubscriberRegistry(EventBus var1) {
      this.bus = Preconditions.checkNotNull(var1);
   }

   void register(Object var1) {
      Multimap var2 = this.findAllSubscribers(var1);

      for (Entry var4 : var2.asMap().entrySet()) {
         Class var5 = (Class)var4.getKey();
         Collection var6 = (Collection)var4.getValue();
         CopyOnWriteArraySet var7 = this.subscribers.get(var5);
         if (var7 == null) {
            CopyOnWriteArraySet var8 = new CopyOnWriteArraySet();
            var7 = MoreObjects.firstNonNull(this.subscribers.putIfAbsent(var5, var8), var8);
         }

         var7.addAll(var6);
      }
   }

   void unregister(Object var1) {
      Multimap var2 = this.findAllSubscribers(var1);

      for (Entry var4 : var2.asMap().entrySet()) {
         Class var5 = (Class)var4.getKey();
         Collection var6 = (Collection)var4.getValue();
         CopyOnWriteArraySet var7 = this.subscribers.get(var5);
         if (var7 == null || !var7.removeAll(var6)) {
            String var8 = String.valueOf(var1);
            throw new IllegalArgumentException(
               new StringBuilder(65 + String.valueOf(var8).length())
                  .append("missing event subscriber for an annotated method. Is ")
                  .append(var8)
                  .append(" registered?")
                  .toString()
            );
         }
      }
   }

   @VisibleForTesting
   Set<Subscriber> getSubscribersForTesting(Class<?> var1) {
      return MoreObjects.firstNonNull(this.subscribers.get(var1), ImmutableSet.<Subscriber>of());
   }

   Iterator<Subscriber> getSubscribers(Object var1) {
      ImmutableSet var2 = flattenHierarchy(var1.getClass());
      ArrayList var3 = Lists.newArrayListWithCapacity(var2.size());

      for (Class var5 : var2) {
         CopyOnWriteArraySet var6 = this.subscribers.get(var5);
         if (var6 != null) {
            var3.add(var6.iterator());
         }
      }

      return Iterators.concat(var3.iterator());
   }

   private Multimap<Class<?>, Subscriber> findAllSubscribers(Object var1) {
      HashMultimap var2 = HashMultimap.create();
      Class var3 = var1.getClass();

      for (Method var5 : getAnnotatedMethods(var3)) {
         Class[] var6 = var5.getParameterTypes();
         Class var7 = var6[0];
         var2.put(var7, Subscriber.create(this.bus, var1, var5));
      }

      return var2;
   }

   private static ImmutableList<Method> getAnnotatedMethods(Class<?> var0) {
      try {
         return subscriberMethodsCache.getUnchecked(var0);
      } catch (UncheckedExecutionException var2) {
         Throwables.throwIfUnchecked(var2.getCause());
         throw var2;
      }
   }

   private static ImmutableList<Method> getAnnotatedMethodsNotCached(Class<?> var0) {
      Set var1 = TypeToken.of(var0).getTypes().rawTypes();
      HashMap var2 = Maps.newHashMap();

      for (Class var4 : var1) {
         for (Method var8 : var4.getDeclaredMethods()) {
            if (var8.isAnnotationPresent(Subscribe.class) && !var8.isSynthetic()) {
               Class[] var9 = var8.getParameterTypes();
               Preconditions.checkArgument(
                  var9.length == 1,
                  "Method %s has @Subscribe annotation but has %s parameters. Subscriber methods must have exactly 1 parameter.",
                  var8,
                  var9.length
               );
               Preconditions.checkArgument(
                  !var9[0].isPrimitive(),
                  "@Subscribe method %s's parameter is %s. Subscriber methods cannot accept primitives. Consider changing the parameter to %s.",
                  var8,
                  var9[0].getName(),
                  Primitives.wrap(var9[0]).getSimpleName()
               );
               SubscriberRegistry.MethodIdentifier var10 = new SubscriberRegistry.MethodIdentifier(var8);
               if (!var2.containsKey(var10)) {
                  var2.put(var10, var8);
               }
            }
         }
      }

      return ImmutableList.copyOf(var2.values());
   }

   @VisibleForTesting
   static ImmutableSet<Class<?>> flattenHierarchy(Class<?> var0) {
      try {
         return flattenHierarchyCache.getUnchecked(var0);
      } catch (UncheckedExecutionException var2) {
         throw Throwables.propagate(var2.getCause());
      }
   }

   private static final class MethodIdentifier {
      private final String name;
      private final List<Class<?>> parameterTypes;

      MethodIdentifier(Method var1) {
         this.name = var1.getName();
         this.parameterTypes = Arrays.asList(var1.getParameterTypes());
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.name, this.parameterTypes);
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof SubscriberRegistry.MethodIdentifier)) {
            return false;
         } else {
            SubscriberRegistry.MethodIdentifier var2 = (SubscriberRegistry.MethodIdentifier)var1;
            return this.name.equals(var2.name) && this.parameterTypes.equals(var2.parameterTypes);
         }
      }
   }
}
