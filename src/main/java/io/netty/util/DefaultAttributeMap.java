package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultAttributeMap implements AttributeMap {
   private static final AtomicReferenceFieldUpdater<DefaultAttributeMap, DefaultAttributeMap.DefaultAttribute[]> ATTRIBUTES_UPDATER = AtomicReferenceFieldUpdater.newUpdater(
      DefaultAttributeMap.class, DefaultAttributeMap.DefaultAttribute[].class, "attributes"
   );
   private static final DefaultAttributeMap.DefaultAttribute[] EMPTY_ATTRIBUTES = new DefaultAttributeMap.DefaultAttribute[0];
   private volatile DefaultAttributeMap.DefaultAttribute[] attributes = EMPTY_ATTRIBUTES;

   private static int searchAttributeByKey(DefaultAttributeMap.DefaultAttribute[] var0, AttributeKey<?> var1) {
      int var2 = 0;
      int var3 = var0.length - 1;

      while (var2 <= var3) {
         int var4 = var2 + var3 >>> 1;
         DefaultAttributeMap.DefaultAttribute var5 = var0[var4];
         AttributeKey var6 = var5.key;
         if (var6 == var1) {
            return var4;
         }

         int var7 = var6.id();
         int var8 = var1.id();

         assert var7 != var8;

         boolean var9 = var7 < var8;
         if (var9) {
            var2 = var4 + 1;
         } else {
            var3 = var4 - 1;
         }
      }

      return -(var2 + 1);
   }

   private static void orderedCopyOnInsert(
      DefaultAttributeMap.DefaultAttribute[] var0, int var1, DefaultAttributeMap.DefaultAttribute[] var2, DefaultAttributeMap.DefaultAttribute var3
   ) {
      int var4 = var3.key.id();

      int var5;
      for (var5 = var1 - 1; var5 >= 0; var5--) {
         DefaultAttributeMap.DefaultAttribute var6 = var0[var5];

         assert var6.key.id() != var4;

         if (var6.key.id() < var4) {
            break;
         }

         var2[var5 + 1] = var0[var5];
      }

      var2[var5 + 1] = var3;
      int var7 = var5 + 1;
      if (var7 > 0) {
         System.arraycopy(var0, 0, var2, 0, var7);
      }
   }

   @Override
   public <T> Attribute<T> attr(AttributeKey<T> var1) {
      ObjectUtil.checkNotNull(var1, "key");
      DefaultAttributeMap.DefaultAttribute var2 = null;

      DefaultAttributeMap.DefaultAttribute[] var3;
      DefaultAttributeMap.DefaultAttribute[] var5;
      do {
         var3 = this.attributes;
         int var4 = searchAttributeByKey(var3, var1);
         if (var4 >= 0) {
            DefaultAttributeMap.DefaultAttribute var6 = var3[var4];

            assert var6.key() == var1;

            if (!var6.isRemoved()) {
               return var6;
            }

            if (var2 == null) {
               var2 = new DefaultAttributeMap.DefaultAttribute(this, var1);
            }

            int var7 = var3.length;
            var5 = Arrays.copyOf(var3, var7);
            var5[var4] = var2;
         } else {
            if (var2 == null) {
               var2 = new DefaultAttributeMap.DefaultAttribute(this, var1);
            }

            int var8 = var3.length;
            var5 = new DefaultAttributeMap.DefaultAttribute[var8 + 1];
            orderedCopyOnInsert(var3, var8, var5, var2);
         }
      } while (!ATTRIBUTES_UPDATER.compareAndSet(this, var3, var5));

      return var2;
   }

   @Override
   public <T> boolean hasAttr(AttributeKey<T> var1) {
      ObjectUtil.checkNotNull(var1, "key");
      return searchAttributeByKey(this.attributes, var1) >= 0;
   }

   private <T> void removeAttributeIfMatch(AttributeKey<T> var1, DefaultAttributeMap.DefaultAttribute<T> var2) {
      DefaultAttributeMap.DefaultAttribute[] var3;
      DefaultAttributeMap.DefaultAttribute[] var8;
      do {
         var3 = this.attributes;
         int var4 = searchAttributeByKey(var3, var1);
         if (var4 < 0) {
            return;
         }

         DefaultAttributeMap.DefaultAttribute var5 = var3[var4];

         assert var5.key() == var1;

         if (var5 != var2) {
            return;
         }

         int var6 = var3.length;
         int var7 = var6 - 1;
         var8 = var7 == 0 ? EMPTY_ATTRIBUTES : new DefaultAttributeMap.DefaultAttribute[var7];
         System.arraycopy(var3, 0, var8, 0, var4);
         int var9 = var6 - var4 - 1;
         if (var9 > 0) {
            System.arraycopy(var3, var4 + 1, var8, var4, var9);
         }
      } while (!ATTRIBUTES_UPDATER.compareAndSet(this, var3, var8));
   }

   private static final class DefaultAttribute<T> extends AtomicReference<T> implements Attribute<T> {
      private static final AtomicReferenceFieldUpdater<DefaultAttributeMap.DefaultAttribute, DefaultAttributeMap> MAP_UPDATER = AtomicReferenceFieldUpdater.newUpdater(
         DefaultAttributeMap.DefaultAttribute.class, DefaultAttributeMap.class, "attributeMap"
      );
      private static final long serialVersionUID = -2661411462200283011L;
      private volatile DefaultAttributeMap attributeMap;
      private final AttributeKey<T> key;

      DefaultAttribute(DefaultAttributeMap var1, AttributeKey<T> var2) {
         this.attributeMap = var1;
         this.key = var2;
      }

      @Override
      public AttributeKey<T> key() {
         return this.key;
      }

      private boolean isRemoved() {
         return this.attributeMap == null;
      }

      @Override
      public T setIfAbsent(T var1) {
         while (!this.compareAndSet(null, (T)var1)) {
            Object var2 = this.get();
            if (var2 != null) {
               return (T)var2;
            }
         }

         return null;
      }

      @Override
      public T getAndRemove() {
         DefaultAttributeMap var1 = this.attributeMap;
         boolean var2 = var1 != null && MAP_UPDATER.compareAndSet(this, var1, null);
         Object var3 = this.getAndSet(null);
         if (var2) {
            var1.removeAttributeIfMatch(this.key, this);
         }

         return (T)var3;
      }

      @Override
      public void remove() {
         DefaultAttributeMap var1 = this.attributeMap;
         boolean var2 = var1 != null && MAP_UPDATER.compareAndSet(this, var1, null);
         this.set(null);
         if (var2) {
            var1.removeAttributeIfMatch(this.key, this);
         }
      }
   }
}
