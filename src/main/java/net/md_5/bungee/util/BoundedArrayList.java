package net.md_5.bungee.util;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;

public class BoundedArrayList<E> extends ArrayList<E> {
   private final int maxSize;

   public BoundedArrayList(int var1) {
      this.maxSize = var1;
   }

   private void checkSize(int var1) {
      Preconditions.checkState(this.size() + var1 <= this.maxSize, "Adding %s elements would exceed capacity of %s", var1, this.maxSize);
   }

   @Override
   public boolean add(E var1) {
      this.checkSize(1);
      return super.add((E)var1);
   }

   @Override
   public void add(int var1, E var2) {
      this.checkSize(1);
      super.add(var1, (E)var2);
   }

   @Override
   public boolean addAll(Collection<? extends E> var1) {
      this.checkSize(var1.size());
      return super.addAll(var1);
   }

   @Override
   public boolean addAll(int var1, Collection<? extends E> var2) {
      this.checkSize(var2.size());
      return super.addAll(var1, var2);
   }
}
