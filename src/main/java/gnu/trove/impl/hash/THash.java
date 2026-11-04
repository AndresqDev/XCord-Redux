package gnu.trove.impl.hash;

import gnu.trove.impl.PrimeFinder;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class THash implements Externalizable {
   static final long serialVersionUID = -1792948471915530295L;
   protected static final float DEFAULT_LOAD_FACTOR = 0.5F;
   protected static final int DEFAULT_CAPACITY = 10;
   protected transient int _size;
   protected transient int _free;
   protected float _loadFactor;
   protected int _maxSize;
   protected int _autoCompactRemovesRemaining;
   protected float _autoCompactionFactor;
   protected transient boolean _autoCompactTemporaryDisable = false;

   public THash() {
      this(10, 0.5F);
   }

   public THash(int var1) {
      this(var1, 0.5F);
   }

   public THash(int var1, float var2) {
      if (var1 < 0) {
         throw new IllegalArgumentException("negative capacity: " + var1);
      } else if (0.0F >= var2) {
         throw new IllegalArgumentException("load factor out of range: " + var2);
      } else {
         this._loadFactor = var2;
         this._autoCompactionFactor = var2;
         this.setUp(saturatedCast(fastCeil((double)var1 / (double)var2)));
      }
   }

   protected static long fastCeil(double var0) {
      long var2 = (long)var0;
      if (var0 - (double)var2 > 0.0) {
         var2++;
      }

      return var2;
   }

   protected static int saturatedCast(long var0) {
      int var2 = (int)(var0 & 2147483647L);
      return (long)var2 != var0 ? Integer.MAX_VALUE : var2;
   }

   public boolean isEmpty() {
      return 0 == this._size;
   }

   public int size() {
      return this._size;
   }

   public abstract int capacity();

   public void ensureCapacity(int var1) {
      if (var1 > this._maxSize - this.size()) {
         this.rehash(PrimeFinder.nextPrime(Math.max(this._size + 1, saturatedCast(fastCeil((double)(var1 + this._size) / (double)this._loadFactor) + 1L))));
         if (this.capacity() >= PrimeFinder.largestPrime) {
            this._loadFactor = 1.0F;
         }

         this.computeMaxSize(this.capacity());
      }
   }

   public void compact() {
      this.rehash(PrimeFinder.nextPrime(Math.max(this._size + 1, saturatedCast(fastCeil((double)this._size / (double)this._loadFactor) + 1L))));
      this.computeMaxSize(this.capacity());
      if (this._autoCompactionFactor != 0.0F) {
         this.computeNextAutoCompactionAmount(this.size());
      }
   }

   public void setAutoCompactionFactor(float var1) {
      if (var1 < 0.0F) {
         throw new IllegalArgumentException("Factor must be >= 0: " + var1);
      } else {
         this._autoCompactionFactor = var1;
      }
   }

   public float getAutoCompactionFactor() {
      return this._autoCompactionFactor;
   }

   public final void trimToSize() {
      this.compact();
   }

   protected void removeAt(int var1) {
      this._size--;
      if (this._autoCompactionFactor != 0.0F) {
         this._autoCompactRemovesRemaining--;
         if (!this._autoCompactTemporaryDisable && this._autoCompactRemovesRemaining <= 0) {
            this.compact();
         }
      }
   }

   public void clear() {
      this._size = 0;
      this._free = this.capacity();
   }

   protected int setUp(int var1) {
      int var2 = PrimeFinder.nextPrime(var1);
      if (var2 >= PrimeFinder.largestPrime) {
         this._loadFactor = 1.0F;
      }

      this.computeMaxSize(var2);
      this.computeNextAutoCompactionAmount(var1);
      return var2;
   }

   protected abstract void rehash(int var1);

   public void tempDisableAutoCompaction() {
      this._autoCompactTemporaryDisable = true;
   }

   public void reenableAutoCompaction(boolean var1) {
      this._autoCompactTemporaryDisable = false;
      if (var1 && this._autoCompactRemovesRemaining <= 0 && this._autoCompactionFactor != 0.0F) {
         this.compact();
      }
   }

   protected void computeMaxSize(int var1) {
      this._maxSize = Math.min(var1 - 1, (int)((float)var1 * this._loadFactor));
      this._free = var1 - this._size;
   }

   protected void computeNextAutoCompactionAmount(int var1) {
      if (this._autoCompactionFactor != 0.0F) {
         this._autoCompactRemovesRemaining = (int)((float)var1 * this._autoCompactionFactor + 0.5F);
      }
   }

   protected final void postInsertHook(boolean var1) {
      if (var1) {
         this._free--;
      }

      if (++this._size > this._maxSize || this._free == 0) {
         int var2 = this._size > this._maxSize ? PrimeFinder.nextPrime(this.capacity() << 1) : this.capacity();
         this.rehash(var2);
         this.computeMaxSize(this.capacity());
      }
   }

   protected int calculateGrownCapacity() {
      return this.capacity() << 1;
   }

   @Override
   public void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeByte(0);
      var1.writeFloat(this._loadFactor);
      var1.writeFloat(this._autoCompactionFactor);
   }

   @Override
   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      var1.readByte();
      float var2 = this._loadFactor;
      this._loadFactor = Math.abs(var1.readFloat());
      this._autoCompactionFactor = var1.readFloat();
      if (var2 != this._loadFactor) {
         this.setUp(saturatedCast((long)Math.ceil(10.0 / (double)this._loadFactor)));
      }
   }
}
