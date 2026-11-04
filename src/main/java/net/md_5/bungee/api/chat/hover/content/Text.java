package net.md_5.bungee.api.chat.hover.content;

import java.util.Arrays;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;

public class Text extends Content {
   private final Object value;

   public Text(BaseComponent[] var1) {
      this.value = var1;
   }

   public Text(String var1) {
      this.value = var1;
   }

   @Override
   public HoverEvent.Action requiredAction() {
      return HoverEvent.Action.SHOW_TEXT;
   }

   @Override
   public boolean equals(Object var1) {
      return !(this.value instanceof BaseComponent[])
         ? this.value.equals(var1)
         : var1 instanceof Text
            && ((Text)var1).value instanceof BaseComponent[]
            && Arrays.equals((Object[])((BaseComponent[])this.value), (Object[])((BaseComponent[])((Text)var1).value));
   }

   @Override
   public int hashCode() {
      return this.value instanceof BaseComponent[] ? Arrays.hashCode((Object[])((BaseComponent[])this.value)) : this.value.hashCode();
   }

   public Object getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return "Text(value=" + this.getValue() + ")";
   }
}
