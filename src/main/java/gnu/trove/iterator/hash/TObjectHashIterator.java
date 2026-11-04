package gnu.trove.iterator.hash;

import gnu.trove.impl.hash.THashIterator;
import gnu.trove.impl.hash.TObjectHash;

public class TObjectHashIterator<E> extends THashIterator<E> {
   protected final TObjectHash _objectHash;

   public TObjectHashIterator(TObjectHash<E> var1) {
      super(var1);
      this._objectHash = var1;
   }

   @Override
   protected E objectAtIndex(int var1) {
      Object var2 = this._objectHash._set[var1];
      return (E)(var2 != TObjectHash.FREE && var2 != TObjectHash.REMOVED ? var2 : null);
   }
}
