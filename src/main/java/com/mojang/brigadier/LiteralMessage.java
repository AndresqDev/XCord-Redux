package com.mojang.brigadier;

public class LiteralMessage implements Message {
   private final String string;

   public LiteralMessage(String var1) {
      this.string = var1;
   }

   @Override
   public String getString() {
      return this.string;
   }

   @Override
   public String toString() {
      return this.string;
   }
}
