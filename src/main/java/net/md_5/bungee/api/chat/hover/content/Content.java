package net.md_5.bungee.api.chat.hover.content;

import net.md_5.bungee.api.chat.HoverEvent;

public abstract class Content {
   public abstract HoverEvent.Action requiredAction();

   public void assertAction(HoverEvent.Action var1) throws UnsupportedOperationException {
      if (var1 != this.requiredAction()) {
         throw new UnsupportedOperationException("Action " + var1 + " not compatible! Expected " + this.requiredAction());
      }
   }

   @Override
   public String toString() {
      return "Content()";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Content)) {
         return false;
      } else {
         Content var2 = (Content)var1;
         return var2.canEqual(this);
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Content;
   }

   @Override
   public int hashCode() {
      boolean var1 = true;
      return 1;
   }
}
