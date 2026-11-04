package gnu.trove.strategy;

public class IdentityHashingStrategy<K> implements HashingStrategy<K> {
   static final long serialVersionUID = -5188534454583764904L;
   public static final IdentityHashingStrategy<Object> INSTANCE = new IdentityHashingStrategy<>();

   @Override
   public int computeHashCode(K var1) {
      return System.identityHashCode(var1);
   }

   @Override
   public boolean equals(K var1, K var2) {
      return var1 == var2;
   }
}
