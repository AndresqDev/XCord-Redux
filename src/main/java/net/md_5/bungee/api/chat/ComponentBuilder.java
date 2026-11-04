package net.md_5.bungee.api.chat;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;

public final class ComponentBuilder {
   private int cursor = -1;
   private final List<BaseComponent> parts = new ArrayList<>();
   private BaseComponent dummy;

   private ComponentBuilder(BaseComponent[] var1) {
      for (BaseComponent var5 : var1) {
         this.parts.add(var5.duplicate());
      }

      this.resetCursor();
   }

   public ComponentBuilder(ComponentBuilder var1) {
      this(var1.parts.toArray(new BaseComponent[var1.parts.size()]));
   }

   public ComponentBuilder(String var1) {
      this(new TextComponent(var1));
   }

   public ComponentBuilder(BaseComponent var1) {
      this(new BaseComponent[]{var1});
   }

   private BaseComponent getDummy() {
      if (this.dummy == null) {
         this.dummy = new BaseComponent() {
            @Override
            public BaseComponent duplicate() {
               return this;
            }
         };
      }

      return this.dummy;
   }

   public ComponentBuilder resetCursor() {
      this.cursor = this.parts.size() - 1;
      return this;
   }

   public ComponentBuilder setCursor(int var1) throws IndexOutOfBoundsException {
      if (this.cursor == var1 || var1 >= 0 && var1 < this.parts.size()) {
         this.cursor = var1;
         return this;
      } else {
         throw new IndexOutOfBoundsException("Cursor out of bounds (expected between 0 + " + (this.parts.size() - 1) + ")");
      }
   }

   public ComponentBuilder append(BaseComponent var1) {
      return this.append(var1, ComponentBuilder.FormatRetention.ALL);
   }

   public ComponentBuilder append(BaseComponent var1, ComponentBuilder.FormatRetention var2) {
      BaseComponent var3 = this.parts.isEmpty() ? null : this.parts.get(this.parts.size() - 1);
      if (var3 == null) {
         var3 = this.dummy;
         this.dummy = null;
      }

      if (var3 != null && !var1.isReset()) {
         var1.copyFormatting(var3, var2, false);
      }

      this.parts.add(var1);
      this.resetCursor();
      return this;
   }

   public ComponentBuilder append(BaseComponent[] var1) {
      return this.append(var1, ComponentBuilder.FormatRetention.ALL);
   }

   public ComponentBuilder append(BaseComponent[] var1, ComponentBuilder.FormatRetention var2) {
      Preconditions.checkArgument(var1.length != 0, "No components to append");

      for (BaseComponent var6 : var1) {
         this.append(var6, var2);
      }

      return this;
   }

   public ComponentBuilder append(String var1) {
      return this.append(var1, ComponentBuilder.FormatRetention.ALL);
   }

   public ComponentBuilder appendLegacy(String var1) {
      return this.append(TextComponent.fromLegacyText(var1));
   }

   public ComponentBuilder append(String var1, ComponentBuilder.FormatRetention var2) {
      return this.append(new TextComponent(var1), var2);
   }

   public ComponentBuilder append(ComponentBuilder.Joiner var1) {
      return var1.join(this, ComponentBuilder.FormatRetention.ALL);
   }

   public ComponentBuilder append(ComponentBuilder.Joiner var1, ComponentBuilder.FormatRetention var2) {
      return var1.join(this, var2);
   }

   public void removeComponent(int var1) throws IndexOutOfBoundsException {
      if (this.parts.remove(var1) != null) {
         this.resetCursor();
      }
   }

   public BaseComponent getComponent(int var1) throws IndexOutOfBoundsException {
      return this.parts.get(var1);
   }

   public BaseComponent getCurrentComponent() {
      return this.cursor == -1 ? this.getDummy() : this.parts.get(this.cursor);
   }

   public ComponentBuilder color(ChatColor var1) {
      this.getCurrentComponent().setColor(var1);
      return this;
   }

   public ComponentBuilder font(String var1) {
      this.getCurrentComponent().setFont(var1);
      return this;
   }

   public ComponentBuilder bold(boolean var1) {
      this.getCurrentComponent().setBold(var1);
      return this;
   }

   public ComponentBuilder italic(boolean var1) {
      this.getCurrentComponent().setItalic(var1);
      return this;
   }

   public ComponentBuilder underlined(boolean var1) {
      this.getCurrentComponent().setUnderlined(var1);
      return this;
   }

   public ComponentBuilder strikethrough(boolean var1) {
      this.getCurrentComponent().setStrikethrough(var1);
      return this;
   }

   public ComponentBuilder obfuscated(boolean var1) {
      this.getCurrentComponent().setObfuscated(var1);
      return this;
   }

   public ComponentBuilder insertion(String var1) {
      this.getCurrentComponent().setInsertion(var1);
      return this;
   }

   public ComponentBuilder event(ClickEvent var1) {
      this.getCurrentComponent().setClickEvent(var1);
      return this;
   }

   public ComponentBuilder event(HoverEvent var1) {
      this.getCurrentComponent().setHoverEvent(var1);
      return this;
   }

   public ComponentBuilder reset() {
      return this.retain(ComponentBuilder.FormatRetention.NONE);
   }

   public ComponentBuilder retain(ComponentBuilder.FormatRetention var1) {
      this.getCurrentComponent().retain(var1);
      return this;
   }

   public BaseComponent[] create() {
      BaseComponent[] var1 = new BaseComponent[this.parts.size()];
      int var2 = 0;

      for (BaseComponent var4 : this.parts) {
         var1[var2++] = var4.duplicate();
      }

      return var1;
   }

   public ComponentBuilder() {
   }

   public int getCursor() {
      return this.cursor;
   }

   public List<BaseComponent> getParts() {
      return this.parts;
   }

   public static enum FormatRetention {
      NONE,
      FORMATTING,
      EVENTS,
      ALL;
   }

   public interface Joiner {
      ComponentBuilder join(ComponentBuilder var1, ComponentBuilder.FormatRetention var2);
   }
}
