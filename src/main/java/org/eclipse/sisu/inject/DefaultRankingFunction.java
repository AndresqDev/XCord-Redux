package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import org.eclipse.sisu.Priority;
import xcord.Inject;

public final class DefaultRankingFunction implements RankingFunction {
   private final int primaryRank;

   public DefaultRankingFunction(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Primary rank must be zero or more");
      } else {
         this.primaryRank = var1;
      }
   }

   @Inject
   public DefaultRankingFunction() {
      this(0);
   }

   @Override
   public int maxRank() {
      return Integer.MAX_VALUE;
   }

   @Override
   public <T> int rank(Binding<T> var1) {
      Priority var2 = Sources.getAnnotation(var1, Priority.class);
      if (var2 != null) {
         return var2.value();
      } else {
         return QualifyingStrategy.DEFAULT_QUALIFIER.equals(QualifyingStrategy.qualify(var1.getKey()))
            ? this.primaryRank
            : this.primaryRank + Integer.MIN_VALUE;
      }
   }
}
