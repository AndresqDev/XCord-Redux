package io.netty.channel.nio;

import java.nio.channels.SelectionKey;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class SelectedSelectionKeySet extends AbstractSet<SelectionKey> {
   SelectionKey[] keys = new SelectionKey[1024];
   int size;

   public boolean add(SelectionKey var1) {
      if (var1 == null) {
         return false;
      } else {
         this.keys[this.size++] = var1;
         if (this.size == this.keys.length) {
            this.increaseCapacity();
         }

         return true;
      }
   }

   @Override
   public boolean remove(Object var1) {
      return false;
   }

   @Override
   public boolean contains(Object var1) {
      return false;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public Iterator<SelectionKey> iterator() {
      return new Iterator<SelectionKey>() {
         private int idx;

         @Override
         public boolean hasNext() {
            return this.idx < SelectedSelectionKeySet.this.size;
         }

         public SelectionKey next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return SelectedSelectionKeySet.this.keys[this.idx++];
            }
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   void reset() {
      this.reset(0);
   }

   void reset(int var1) {
      Arrays.fill(this.keys, var1, this.size, null);
      this.size = 0;
   }

   private void increaseCapacity() {
      SelectionKey[] var1 = new SelectionKey[this.keys.length << 1];
      System.arraycopy(this.keys, 0, var1, 0, this.size);
      this.keys = var1;
   }
}
