package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ConstantPool<T extends Constant<T>> {
   private final ConcurrentMap<String, T> constants = PlatformDependent.newConcurrentHashMap();
   private final AtomicInteger nextId = new AtomicInteger(1);

   public T valueOf(Class<?> var1, String var2) {
      return this.valueOf(ObjectUtil.checkNotNull(var1, "firstNameComponent").getName() + '#' + ObjectUtil.checkNotNull(var2, "secondNameComponent"));
   }

   public T valueOf(String var1) {
      return this.getOrCreate(ObjectUtil.checkNonEmpty(var1, "name"));
   }

   private T getOrCreate(String var1) {
      Constant var2 = this.constants.get(var1);
      if (var2 == null) {
         Constant var3 = this.newConstant(this.nextId(), var1);
         var2 = this.constants.putIfAbsent(var1, (T)var3);
         if (var2 == null) {
            return (T)var3;
         }
      }

      return (T)var2;
   }

   public boolean exists(String var1) {
      return this.constants.containsKey(ObjectUtil.checkNonEmpty(var1, "name"));
   }

   public T newInstance(String var1) {
      return this.createOrThrow(ObjectUtil.checkNonEmpty(var1, "name"));
   }

   private T createOrThrow(String var1) {
      Constant var2 = this.constants.get(var1);
      if (var2 == null) {
         Constant var3 = this.newConstant(this.nextId(), var1);
         var2 = this.constants.putIfAbsent(var1, (T)var3);
         if (var2 == null) {
            return (T)var3;
         }
      }

      throw new IllegalArgumentException(String.format("'%s' is already in use", var1));
   }

   protected abstract T newConstant(int var1, String var2);

   @Deprecated
   public final int nextId() {
      return this.nextId.getAndIncrement();
   }
}
