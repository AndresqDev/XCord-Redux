package net.md_5.bungee.api.chat;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.chat.ComponentSerializer;

public final class HoverEvent {
   private final HoverEvent.Action action;
   private final List<Content> contents;
   private boolean legacy = false;

   public HoverEvent(HoverEvent.Action var1, Content... var2) {
      Preconditions.checkArgument(var2.length != 0, "Must contain at least one content");
      this.action = var1;
      this.contents = new ArrayList<>();

      for (Content var6 : var2) {
         this.addContent(var6);
      }
   }

   @Deprecated
   public HoverEvent(HoverEvent.Action var1, BaseComponent[] var2) {
      this.action = var1;
      this.contents = new ArrayList<>(Collections.singletonList(new Text(var2)));
      this.legacy = true;
   }

   @Deprecated
   public BaseComponent[] getValue() {
      Content var1 = this.contents.get(0);
      if (var1 instanceof Text && ((Text)var1).getValue() instanceof BaseComponent[]) {
         return (BaseComponent[])((Text)var1).getValue();
      } else {
         TextComponent var2 = new TextComponent(ComponentSerializer.toString(var1));
         return new BaseComponent[]{var2};
      }
   }

   public void addContent(Content var1) throws UnsupportedOperationException {
      Preconditions.checkArgument(!this.legacy || this.contents.size() == 0, "Legacy HoverEvent may not have more than one content");
      var1.assertAction(this.action);
      this.contents.add(var1);
   }

   public static Class<?> getClass(HoverEvent.Action var0, boolean var1) {
      Preconditions.checkArgument(var0 != null, "action");
      switch (var0) {
         case SHOW_TEXT:
            return var1 ? Text[].class : Text.class;
         case SHOW_ENTITY:
            return var1 ? Entity[].class : Entity.class;
         case SHOW_ITEM:
            return var1 ? Item[].class : Item.class;
         default:
            throw new UnsupportedOperationException("Action '" + var0.name() + " not supported");
      }
   }

   public HoverEvent.Action getAction() {
      return this.action;
   }

   public List<Content> getContents() {
      return this.contents;
   }

   public boolean isLegacy() {
      return this.legacy;
   }

   @Override
   public String toString() {
      return "HoverEvent(action=" + this.getAction() + ", contents=" + this.getContents() + ", legacy=" + this.isLegacy() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof HoverEvent)) {
         return false;
      } else {
         HoverEvent var2 = (HoverEvent)var1;
         if (this.isLegacy() != var2.isLegacy()) {
            return false;
         } else {
            HoverEvent.Action var3 = this.getAction();
            HoverEvent.Action var4 = var2.getAction();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               List var5 = this.getContents();
               List var6 = var2.getContents();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + (this.isLegacy() ? 79 : 97);
      HoverEvent.Action var3 = this.getAction();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      List var4 = this.getContents();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }

   public HoverEvent(HoverEvent.Action var1, List<Content> var2) {
      this.action = var1;
      this.contents = var2;
   }

   public void setLegacy(boolean var1) {
      this.legacy = var1;
   }

   public static enum Action {
      SHOW_TEXT,
      SHOW_ITEM,
      SHOW_ENTITY,
      @Deprecated
      SHOW_ACHIEVEMENT;
   }
}
