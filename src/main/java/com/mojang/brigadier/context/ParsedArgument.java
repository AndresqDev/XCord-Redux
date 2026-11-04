package com.mojang.brigadier.context;

import java.util.Objects;

public class ParsedArgument<S, T> {
   private final StringRange range;
   private final T result;

   public ParsedArgument(int var1, int var2, T var3) {
      this.range = StringRange.between(var1, var2);
      this.result = (T)var3;
   }

   public StringRange getRange() {
      return this.range;
   }

   public T getResult() {
      return this.result;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ParsedArgument)) {
         return false;
      } else {
         ParsedArgument var2 = (ParsedArgument)var1;
         return Objects.equals(this.range, var2.range) && Objects.equals(this.result, var2.result);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.range, this.result);
   }
}
