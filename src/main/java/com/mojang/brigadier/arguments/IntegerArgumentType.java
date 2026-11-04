package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class IntegerArgumentType implements ArgumentType<Integer> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123");
   private final int minimum;
   private final int maximum;

   private IntegerArgumentType(int var1, int var2) {
      this.minimum = var1;
      this.maximum = var2;
   }

   public static IntegerArgumentType integer() {
      return integer(Integer.MIN_VALUE);
   }

   public static IntegerArgumentType integer(int var0) {
      return integer(var0, Integer.MAX_VALUE);
   }

   public static IntegerArgumentType integer(int var0, int var1) {
      return new IntegerArgumentType(var0, var1);
   }

   public static int getInteger(CommandContext<?> var0, String var1) {
      return var0.getArgument(var1, int.class);
   }

   public int getMinimum() {
      return this.minimum;
   }

   public int getMaximum() {
      return this.maximum;
   }

   public Integer parse(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();
      int var3 = var1.readInt();
      if (var3 < this.minimum) {
         var1.setCursor(var2);
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(var1, var3, this.minimum);
      } else if (var3 > this.maximum) {
         var1.setCursor(var2);
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().createWithContext(var1, var3, this.maximum);
      } else {
         return var3;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntegerArgumentType)) {
         return false;
      } else {
         IntegerArgumentType var2 = (IntegerArgumentType)var1;
         return this.maximum == var2.maximum && this.minimum == var2.minimum;
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.minimum + this.maximum;
   }

   @Override
   public String toString() {
      if (this.minimum == Integer.MIN_VALUE && this.maximum == Integer.MAX_VALUE) {
         return "integer()";
      } else {
         return this.maximum == Integer.MAX_VALUE ? "integer(" + this.minimum + ")" : "integer(" + this.minimum + ", " + this.maximum + ")";
      }
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
