package io.netty.util.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

public final class RecyclableArrayList extends ArrayList<Object> {
   private static final long serialVersionUID = -8605125654176467947L;
   private static final int DEFAULT_INITIAL_CAPACITY = 8;
   private static final ObjectPool<RecyclableArrayList> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<RecyclableArrayList>() {
      public RecyclableArrayList newObject(ObjectPool.Handle<RecyclableArrayList> var1) {
         return new RecyclableArrayList(var1);
      }
   });
   private boolean insertSinceRecycled;
   private final ObjectPool.Handle<RecyclableArrayList> handle;

   public static RecyclableArrayList newInstance() {
      return newInstance(8);
   }

   public static RecyclableArrayList newInstance(int var0) {
      RecyclableArrayList var1 = RECYCLER.get();
      var1.ensureCapacity(var0);
      return var1;
   }

   private RecyclableArrayList(ObjectPool.Handle<RecyclableArrayList> var1) {
      this(var1, 8);
   }

   private RecyclableArrayList(ObjectPool.Handle<RecyclableArrayList> var1, int var2) {
      super(var2);
      this.handle = var1;
   }

   @Override
   public boolean addAll(Collection<?> var1) {
      checkNullElements(var1);
      if (super.addAll(var1)) {
         this.insertSinceRecycled = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean addAll(int var1, Collection<?> var2) {
      checkNullElements(var2);
      if (super.addAll(var1, var2)) {
         this.insertSinceRecycled = true;
         return true;
      } else {
         return false;
      }
   }

   private static void checkNullElements(Collection<?> var0) {
      if (var0 instanceof RandomAccess && var0 instanceof List) {
         List var4 = (List)var0;
         int var5 = var4.size();

         for (int var3 = 0; var3 < var5; var3++) {
            if (var4.get(var3) == null) {
               throw new IllegalArgumentException("c contains null values");
            }
         }
      } else {
         for (Object var2 : var0) {
            if (var2 == null) {
               throw new IllegalArgumentException("c contains null values");
            }
         }
      }
   }

   @Override
   public boolean add(Object var1) {
      if (super.add(ObjectUtil.checkNotNull(var1, "element"))) {
         this.insertSinceRecycled = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void add(int var1, Object var2) {
      super.add(var1, ObjectUtil.checkNotNull(var2, "element"));
      this.insertSinceRecycled = true;
   }

   @Override
   public Object set(int var1, Object var2) {
      Object var3 = super.set(var1, ObjectUtil.checkNotNull(var2, "element"));
      this.insertSinceRecycled = true;
      return var3;
   }

   public boolean insertSinceRecycled() {
      return this.insertSinceRecycled;
   }

   public boolean recycle() {
      this.clear();
      this.insertSinceRecycled = false;
      this.handle.recycle(this);
      return true;
   }
}
