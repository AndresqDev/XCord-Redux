package org.eclipse.aether.util.graph.transformer;

import java.util.Collection;
import org.eclipse.aether.RepositoryException;

public final class SimpleOptionalitySelector extends ConflictResolver.OptionalitySelector {
   @Override
   public void selectOptionality(ConflictResolver.ConflictContext var1) throws RepositoryException {
      boolean var2 = this.chooseEffectiveOptionality(var1.getItems());
      var1.setOptional(var2);
   }

   private boolean chooseEffectiveOptionality(Collection<ConflictResolver.ConflictItem> var1) {
      boolean var2 = true;

      for (ConflictResolver.ConflictItem var4 : var1) {
         if (var4.getDepth() <= 1) {
            return var4.getDependency().isOptional();
         }

         if ((var4.getOptionalities() & 1) != 0) {
            var2 = false;
         }
      }

      return var2;
   }
}
