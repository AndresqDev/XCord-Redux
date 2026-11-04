package gnu.trove.impl.hash;

public abstract class TPrimitiveHash extends THash {
   static final long serialVersionUID = 1L;
   public transient byte[] _states;
   public static final byte FREE = 0;
   public static final byte FULL = 1;
   public static final byte REMOVED = 2;

   public TPrimitiveHash() {
   }

   public TPrimitiveHash(int var1) {
      super(var1, 0.5F);
   }

   public TPrimitiveHash(int var1, float var2) {
      super(var1, var2);
   }

   @Override
   public int capacity() {
      return this._states.length;
   }

   @Override
   protected void removeAt(int var1) {
      this._states[var1] = 2;
      super.removeAt(var1);
   }

   @Override
   protected int setUp(int var1) {
      int var2 = super.setUp(var1);
      this._states = new byte[var2];
      return var2;
   }
}
