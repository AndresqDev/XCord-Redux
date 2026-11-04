package com.mojang.brigadier.context;

import com.mojang.brigadier.tree.CommandNode;
import java.util.Objects;

public class ParsedCommandNode<S> {
   private final CommandNode<S> node;
   private final StringRange range;

   public ParsedCommandNode(CommandNode<S> var1, StringRange var2) {
      this.node = var1;
      this.range = var2;
   }

   public CommandNode<S> getNode() {
      return this.node;
   }

   public StringRange getRange() {
      return this.range;
   }

   @Override
   public String toString() {
      return this.node + "@" + this.range;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ParsedCommandNode var2 = (ParsedCommandNode)var1;
         return Objects.equals(this.node, var2.node) && Objects.equals(this.range, var2.range);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.node, this.range);
   }
}
