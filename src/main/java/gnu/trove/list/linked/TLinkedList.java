package gnu.trove.list.linked;

import gnu.trove.list.TLinkable;
import gnu.trove.procedure.TObjectProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSequentialList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class TLinkedList<T extends TLinkable<T>> extends AbstractSequentialList<T> implements Externalizable {
   static final long serialVersionUID = 1L;
   protected T _head;
   protected T _tail;
   protected int _size = 0;

   @Override
   public ListIterator<T> listIterator(int var1) {
      return new TLinkedList.IteratorImpl(var1);
   }

   @Override
   public int size() {
      return this._size;
   }

   public void add(int var1, T var2) {
      if (var1 >= 0 && var1 <= this.size()) {
         this.insert(var1, (T)var2);
      } else {
         throw new IndexOutOfBoundsException("index:" + var1);
      }
   }

   public boolean add(T var1) {
      this.insert(this._size, (T)var1);
      return true;
   }

   public void addFirst(T var1) {
      this.insert(0, (T)var1);
   }

   public void addLast(T var1) {
      this.insert(this.size(), (T)var1);
   }

   @Override
   public void clear() {
      if (null != this._head) {
         for (TLinkable var1 = this._head.getNext(); var1 != null; var1 = var1.getNext()) {
            TLinkable var2 = var1.getPrevious();
            var2.setNext(null);
            var1.setPrevious(null);
         }

         this._head = this._tail = null;
      }

      this._size = 0;
   }

   @Override
   public Object[] toArray() {
      Object[] var1 = new Object[this._size];
      int var2 = 0;

      for (TLinkable var3 = this._head; var3 != null; var3 = var3.getNext()) {
         var1[var2++] = var3;
      }

      return var1;
   }

   public Object[] toUnlinkedArray() {
      Object[] var1 = new Object[this._size];
      int var2 = 0;

      for (TLinkable var3 = this._head; var3 != null; var2++) {
         var1[var2] = var3;
         TLinkable var4 = var3;
         var3 = var3.getNext();
         var4.setNext(null);
         var4.setPrevious(null);
      }

      this._size = 0;
      this._head = this._tail = null;
      return var1;
   }

   public T[] toUnlinkedArray(T[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = (TLinkable[])Array.newInstance(var1.getClass().getComponentType(), var2);
      }

      int var3 = 0;

      for (TLinkable var4 = this._head; var4 != null; var3++) {
         var1[var3] = var4;
         TLinkable var5 = var4;
         var4 = var4.getNext();
         var5.setNext(null);
         var5.setPrevious(null);
      }

      this._size = 0;
      this._head = this._tail = null;
      return (T[])var1;
   }

   @Override
   public boolean contains(Object var1) {
      for (TLinkable var2 = this._head; var2 != null; var2 = var2.getNext()) {
         if (var1.equals(var2)) {
            return true;
         }
      }

      return false;
   }

   public T get(int var1) {
      if (var1 < 0 || var1 >= this._size) {
         throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: " + this._size);
      } else if (var1 > this._size >> 1) {
         int var4 = this._size - 1;

         TLinkable var5;
         for (var5 = this._tail; var4 > var1; var4--) {
            var5 = var5.getPrevious();
         }

         return (T)var5;
      } else {
         int var2 = 0;

         TLinkable var3;
         for (var3 = this._head; var2 < var1; var2++) {
            var3 = var3.getNext();
         }

         return (T)var3;
      }
   }

   public T getFirst() {
      return this._head;
   }

   public T getLast() {
      return this._tail;
   }

   public T getNext(T var1) {
      return (T)var1.getNext();
   }

   public T getPrevious(T var1) {
      return (T)var1.getPrevious();
   }

   public T removeFirst() {
      TLinkable var1 = this._head;
      if (var1 == null) {
         return null;
      } else {
         TLinkable var2 = var1.getNext();
         var1.setNext(null);
         if (null != var2) {
            var2.setPrevious(null);
         }

         this._head = (T)var2;
         if (--this._size == 0) {
            this._tail = null;
         }

         return (T)var1;
      }
   }

   public T removeLast() {
      TLinkable var1 = this._tail;
      if (var1 == null) {
         return null;
      } else {
         TLinkable var2 = var1.getPrevious();
         var1.setPrevious(null);
         if (null != var2) {
            var2.setNext(null);
         }

         this._tail = (T)var2;
         if (--this._size == 0) {
            this._head = null;
         }

         return (T)var1;
      }
   }

   protected void insert(int var1, T var2) {
      if (this._size == 0) {
         this._head = this._tail = (T)var2;
      } else if (var1 == 0) {
         var2.setNext(this._head);
         this._head.setPrevious((T)var2);
         this._head = (T)var2;
      } else if (var1 == this._size) {
         this._tail.setNext((T)var2);
         var2.setPrevious(this._tail);
         this._tail = (T)var2;
      } else {
         TLinkable var3 = this.get(var1);
         TLinkable var4 = var3.getPrevious();
         if (var4 != null) {
            var4.setNext(var2);
         }

         var2.setPrevious(var4);
         var2.setNext(var3);
         var3.setPrevious(var2);
      }

      this._size++;
   }

   @Override
   public boolean remove(Object var1) {
      if (!(var1 instanceof TLinkable)) {
         return false;
      } else {
         TLinkable var4 = (TLinkable)var1;
         TLinkable var2 = var4.getPrevious();
         TLinkable var3 = var4.getNext();
         if (var3 == null && var2 == null) {
            if (var1 != this._head) {
               return false;
            }

            this._head = this._tail = null;
         } else if (var3 == null) {
            var4.setPrevious(null);
            var2.setNext(null);
            this._tail = (T)var2;
         } else if (var2 == null) {
            var4.setNext(null);
            var3.setPrevious(null);
            this._head = (T)var3;
         } else {
            var2.setNext(var3);
            var3.setPrevious(var2);
            var4.setNext(null);
            var4.setPrevious(null);
         }

         this._size--;
         return true;
      }
   }

   public void addBefore(T var1, T var2) {
      if (var1 == this._head) {
         this.addFirst((T)var2);
      } else if (var1 == null) {
         this.addLast((T)var2);
      } else {
         TLinkable var3 = var1.getPrevious();
         var2.setNext(var1);
         var3.setNext(var2);
         var2.setPrevious(var3);
         var1.setPrevious(var2);
         this._size++;
      }
   }

   public void addAfter(T var1, T var2) {
      if (var1 == this._tail) {
         this.addLast((T)var2);
      } else if (var1 == null) {
         this.addFirst((T)var2);
      } else {
         TLinkable var3 = var1.getNext();
         var2.setPrevious(var1);
         var2.setNext(var3);
         var1.setNext(var2);
         var3.setPrevious(var2);
         this._size++;
      }
   }

   public boolean forEachValue(TObjectProcedure<T> var1) {
      for (TLinkable var2 = this._head; var2 != null; var2 = var2.getNext()) {
         boolean var3 = var1.execute(var2);
         if (!var3) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeInt(this._size);
      var1.writeObject(this._head);
      var1.writeObject(this._tail);
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      this._size = var1.readInt();
      this._head = (T)var1.readObject();
      this._tail = (T)var1.readObject();
   }

   protected final class IteratorImpl implements ListIterator<T> {
      private int _nextIndex = 0;
      private T _next;
      private T _lastReturned;

      IteratorImpl(int var2) {
         if (var2 >= 0 && var2 <= TLinkedList.this._size) {
            this._nextIndex = var2;
            if (var2 == 0) {
               this._next = TLinkedList.this._head;
            } else if (var2 == TLinkedList.this._size) {
               this._next = null;
            } else if (var2 < TLinkedList.this._size >> 1) {
               int var3 = 0;

               for (this._next = TLinkedList.this._head; var3 < var2; var3++) {
                  this._next = this._next.getNext();
               }
            } else {
               int var4 = TLinkedList.this._size - 1;

               for (this._next = TLinkedList.this._tail; var4 > var2; var4--) {
                  this._next = this._next.getPrevious();
               }
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public final void add(T var1) {
         this._lastReturned = null;
         this._nextIndex++;
         if (TLinkedList.this._size == 0) {
            TLinkedList.this.add((T)var1);
         } else {
            TLinkedList.this.addBefore(this._next, (T)var1);
         }
      }

      @Override
      public final boolean hasNext() {
         return this._nextIndex != TLinkedList.this._size;
      }

      @Override
      public final boolean hasPrevious() {
         return this._nextIndex != 0;
      }

      public final T next() {
         if (this._nextIndex == TLinkedList.this._size) {
            throw new NoSuchElementException();
         } else {
            this._lastReturned = this._next;
            this._next = this._next.getNext();
            this._nextIndex++;
            return this._lastReturned;
         }
      }

      @Override
      public final int nextIndex() {
         return this._nextIndex;
      }

      public final T previous() {
         if (this._nextIndex == 0) {
            throw new NoSuchElementException();
         } else {
            if (this._nextIndex == TLinkedList.this._size) {
               this._lastReturned = this._next = TLinkedList.this._tail;
            } else {
               this._lastReturned = this._next = this._next.getPrevious();
            }

            this._nextIndex--;
            return this._lastReturned;
         }
      }

      @Override
      public final int previousIndex() {
         return this._nextIndex - 1;
      }

      @Override
      public final void remove() {
         if (this._lastReturned == null) {
            throw new IllegalStateException("must invoke next or previous before invoking remove");
         } else {
            if (this._lastReturned != this._next) {
               this._nextIndex--;
            }

            this._next = this._lastReturned.getNext();
            TLinkedList.this.remove(this._lastReturned);
            this._lastReturned = null;
         }
      }

      public final void set(T var1) {
         if (this._lastReturned == null) {
            throw new IllegalStateException();
         } else {
            this.swap(this._lastReturned, (T)var1);
            this._lastReturned = (T)var1;
         }
      }

      private void swap(T var1, T var2) {
         TLinkable var3 = var1.getPrevious();
         TLinkable var4 = var1.getNext();
         TLinkable var5 = var2.getPrevious();
         TLinkable var6 = var2.getNext();
         if (var4 == var2) {
            if (var3 != null) {
               var3.setNext(var2);
            }

            var2.setPrevious(var3);
            var2.setNext(var1);
            var1.setPrevious(var2);
            var1.setNext(var6);
            if (var6 != null) {
               var6.setPrevious(var1);
            }
         } else if (var6 == var1) {
            if (var5 != null) {
               var5.setNext(var2);
            }

            var2.setPrevious(var1);
            var2.setNext(var4);
            var1.setPrevious(var5);
            var1.setNext(var2);
            if (var4 != null) {
               var4.setPrevious(var2);
            }
         } else {
            var1.setNext(var6);
            var1.setPrevious(var5);
            if (var5 != null) {
               var5.setNext(var1);
            }

            if (var6 != null) {
               var6.setPrevious(var1);
            }

            var2.setNext(var4);
            var2.setPrevious(var3);
            if (var3 != null) {
               var3.setNext(var2);
            }

            if (var4 != null) {
               var4.setPrevious(var2);
            }
         }

         if (TLinkedList.this._head == var1) {
            TLinkedList.this._head = (T)var2;
         } else if (TLinkedList.this._head == var2) {
            TLinkedList.this._head = (T)var1;
         }

         if (TLinkedList.this._tail == var1) {
            TLinkedList.this._tail = (T)var2;
         } else if (TLinkedList.this._tail == var2) {
            TLinkedList.this._tail = (T)var1;
         }

         if (this._lastReturned == var1) {
            this._lastReturned = (T)var2;
         } else if (this._lastReturned == var2) {
            this._lastReturned = (T)var1;
         }

         if (this._next == var1) {
            this._next = (T)var2;
         } else if (this._next == var2) {
            this._next = (T)var1;
         }
      }
   }
}
