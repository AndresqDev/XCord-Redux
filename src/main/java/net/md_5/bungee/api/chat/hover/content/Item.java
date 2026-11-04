package net.md_5.bungee.api.chat.hover.content;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;

public class Item extends Content {
   private String id;
   private int count = -1;
   private ItemTag tag;

   @Override
   public HoverEvent.Action requiredAction() {
      return HoverEvent.Action.SHOW_ITEM;
   }

   public String getId() {
      return this.id;
   }

   public int getCount() {
      return this.count;
   }

   public ItemTag getTag() {
      return this.tag;
   }

   public void setId(String var1) {
      this.id = var1;
   }

   public void setCount(int var1) {
      this.count = var1;
   }

   public void setTag(ItemTag var1) {
      this.tag = var1;
   }

   public Item(String var1, int var2, ItemTag var3) {
      this.id = var1;
      this.count = var2;
      this.tag = var3;
   }

   @Override
   public String toString() {
      return "Item(id=" + this.getId() + ", count=" + this.getCount() + ", tag=" + this.getTag() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Item)) {
         return false;
      } else {
         Item var2 = (Item)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getCount() != var2.getCount()) {
            return false;
         } else {
            String var3 = this.getId();
            String var4 = var2.getId();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               ItemTag var5 = this.getTag();
               ItemTag var6 = var2.getTag();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof Item;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getCount();
      String var3 = this.getId();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      ItemTag var4 = this.getTag();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
