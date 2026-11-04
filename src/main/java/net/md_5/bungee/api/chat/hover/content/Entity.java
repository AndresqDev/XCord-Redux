package net.md_5.bungee.api.chat.hover.content;

import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;

public class Entity extends Content {
   private String type;
   @NonNull
   private String id;
   private BaseComponent name;

   @Override
   public HoverEvent.Action requiredAction() {
      return HoverEvent.Action.SHOW_ENTITY;
   }

   public String getType() {
      return this.type;
   }

   @NonNull
   public String getId() {
      return this.id;
   }

   public BaseComponent getName() {
      return this.name;
   }

   public void setType(String var1) {
      this.type = var1;
   }

   public void setId(@NonNull String var1) {
      if (var1 == null) {
         throw new NullPointerException("id is marked non-null but is null");
      } else {
         this.id = var1;
      }
   }

   public void setName(BaseComponent var1) {
      this.name = var1;
   }

   public Entity(String var1, @NonNull String var2, BaseComponent var3) {
      if (var2 == null) {
         throw new NullPointerException("id is marked non-null but is null");
      } else {
         this.type = var1;
         this.id = var2;
         this.name = var3;
      }
   }

   @Override
   public String toString() {
      return "Entity(type=" + this.getType() + ", id=" + this.getId() + ", name=" + this.getName() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Entity)) {
         return false;
      } else {
         Entity var2 = (Entity)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else {
            String var3 = this.getType();
            String var4 = var2.getType();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getId();
               String var6 = var2.getId();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  BaseComponent var7 = this.getName();
                  BaseComponent var8 = var2.getName();
                  return var7 == null ? var8 == null : var7.equals(var8);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof Entity;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = super.hashCode();
      String var3 = this.getType();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getId();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      BaseComponent var5 = this.getName();
      return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
   }
}
