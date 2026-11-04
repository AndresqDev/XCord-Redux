package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;

public abstract class BaseComponent {
   BaseComponent parent;
   private ChatColor color;
   private String font;
   private Boolean bold;
   private Boolean italic;
   private Boolean underlined;
   private Boolean strikethrough;
   private Boolean obfuscated;
   private String insertion;
   private List<BaseComponent> extra;
   private ClickEvent clickEvent;
   private HoverEvent hoverEvent;
   private transient boolean reset;

   @Deprecated
   public BaseComponent() {
   }

   BaseComponent(BaseComponent var1) {
      this.copyFormatting(var1, ComponentBuilder.FormatRetention.ALL, true);
      if (var1.getExtra() != null) {
         for (BaseComponent var3 : var1.getExtra()) {
            this.addExtra(var3.duplicate());
         }
      }
   }

   public void copyFormatting(BaseComponent var1) {
      this.copyFormatting(var1, ComponentBuilder.FormatRetention.ALL, true);
   }

   public void copyFormatting(BaseComponent var1, boolean var2) {
      this.copyFormatting(var1, ComponentBuilder.FormatRetention.ALL, var2);
   }

   public void copyFormatting(BaseComponent var1, ComponentBuilder.FormatRetention var2, boolean var3) {
      if (var2 == ComponentBuilder.FormatRetention.EVENTS || var2 == ComponentBuilder.FormatRetention.ALL) {
         if (var3 || this.clickEvent == null) {
            this.setClickEvent(var1.getClickEvent());
         }

         if (var3 || this.hoverEvent == null) {
            this.setHoverEvent(var1.getHoverEvent());
         }
      }

      if (var2 == ComponentBuilder.FormatRetention.FORMATTING || var2 == ComponentBuilder.FormatRetention.ALL) {
         if (var3 || this.color == null) {
            this.setColor(var1.getColorRaw());
         }

         if (var3 || this.font == null) {
            this.setFont(var1.getFontRaw());
         }

         if (var3 || this.bold == null) {
            this.setBold(var1.isBoldRaw());
         }

         if (var3 || this.italic == null) {
            this.setItalic(var1.isItalicRaw());
         }

         if (var3 || this.underlined == null) {
            this.setUnderlined(var1.isUnderlinedRaw());
         }

         if (var3 || this.strikethrough == null) {
            this.setStrikethrough(var1.isStrikethroughRaw());
         }

         if (var3 || this.obfuscated == null) {
            this.setObfuscated(var1.isObfuscatedRaw());
         }

         if (var3 || this.insertion == null) {
            this.setInsertion(var1.getInsertion());
         }
      }
   }

   public void retain(ComponentBuilder.FormatRetention var1) {
      if (var1 == ComponentBuilder.FormatRetention.FORMATTING || var1 == ComponentBuilder.FormatRetention.NONE) {
         this.setClickEvent(null);
         this.setHoverEvent(null);
      }

      if (var1 == ComponentBuilder.FormatRetention.EVENTS || var1 == ComponentBuilder.FormatRetention.NONE) {
         this.setColor(null);
         this.setBold(null);
         this.setItalic(null);
         this.setUnderlined(null);
         this.setStrikethrough(null);
         this.setObfuscated(null);
         this.setInsertion(null);
      }
   }

   public abstract BaseComponent duplicate();

   @Deprecated
   public BaseComponent duplicateWithoutFormatting() {
      BaseComponent var1 = this.duplicate();
      var1.retain(ComponentBuilder.FormatRetention.NONE);
      return var1;
   }

   public static String toLegacyText(BaseComponent... var0) {
      StringBuilder var1 = new StringBuilder();

      for (BaseComponent var5 : var0) {
         var1.append(var5.toLegacyText());
      }

      return var1.toString();
   }

   public static String toPlainText(BaseComponent... var0) {
      StringBuilder var1 = new StringBuilder();

      for (BaseComponent var5 : var0) {
         var1.append(var5.toPlainText());
      }

      return var1.toString();
   }

   public ChatColor getColor() {
      if (this.color == null) {
         return this.parent == null ? ChatColor.WHITE : this.parent.getColor();
      } else {
         return this.color;
      }
   }

   public ChatColor getColorRaw() {
      return this.color;
   }

   public String getFont() {
      if (this.font == null) {
         return this.parent == null ? null : this.parent.getFont();
      } else {
         return this.font;
      }
   }

   public String getFontRaw() {
      return this.font;
   }

   public boolean isBold() {
      return this.bold != null ? this.bold : this.parent != null && this.parent.isBold();
   }

   public Boolean isBoldRaw() {
      return this.bold;
   }

   public boolean isItalic() {
      return this.italic != null ? this.italic : this.parent != null && this.parent.isItalic();
   }

   public Boolean isItalicRaw() {
      return this.italic;
   }

   public boolean isUnderlined() {
      return this.underlined != null ? this.underlined : this.parent != null && this.parent.isUnderlined();
   }

   public Boolean isUnderlinedRaw() {
      return this.underlined;
   }

   public boolean isStrikethrough() {
      return this.strikethrough != null ? this.strikethrough : this.parent != null && this.parent.isStrikethrough();
   }

   public Boolean isStrikethroughRaw() {
      return this.strikethrough;
   }

   public boolean isObfuscated() {
      return this.obfuscated != null ? this.obfuscated : this.parent != null && this.parent.isObfuscated();
   }

   public Boolean isObfuscatedRaw() {
      return this.obfuscated;
   }

   public void setExtra(List<BaseComponent> var1) {
      for (BaseComponent var3 : var1) {
         var3.parent = this;
      }

      this.extra = var1;
   }

   public void addExtra(String var1) {
      this.addExtra(new TextComponent(var1));
   }

   public void addExtra(BaseComponent var1) {
      if (this.extra == null) {
         this.extra = new ArrayList<>();
      }

      var1.parent = this;
      this.extra.add(var1);
   }

   public boolean hasFormatting() {
      return this.color != null
         || this.font != null
         || this.bold != null
         || this.italic != null
         || this.underlined != null
         || this.strikethrough != null
         || this.obfuscated != null
         || this.insertion != null
         || this.hoverEvent != null
         || this.clickEvent != null;
   }

   public String toPlainText() {
      StringBuilder var1 = new StringBuilder();
      this.toPlainText(var1);
      return var1.toString();
   }

   void toPlainText(StringBuilder var1) {
      if (this.extra != null) {
         for (BaseComponent var3 : this.extra) {
            var3.toPlainText(var1);
         }
      }
   }

   public String toLegacyText() {
      StringBuilder var1 = new StringBuilder();
      this.toLegacyText(var1);
      return var1.toString();
   }

   void toLegacyText(StringBuilder var1) {
      if (this.extra != null) {
         for (BaseComponent var3 : this.extra) {
            var3.toLegacyText(var1);
         }
      }
   }

   void addFormat(StringBuilder var1) {
      var1.append(this.getColor());
      if (this.isBold()) {
         var1.append(ChatColor.BOLD);
      }

      if (this.isItalic()) {
         var1.append(ChatColor.ITALIC);
      }

      if (this.isUnderlined()) {
         var1.append(ChatColor.UNDERLINE);
      }

      if (this.isStrikethrough()) {
         var1.append(ChatColor.STRIKETHROUGH);
      }

      if (this.isObfuscated()) {
         var1.append(ChatColor.MAGIC);
      }
   }

   public void setColor(ChatColor var1) {
      this.color = var1;
   }

   public void setFont(String var1) {
      this.font = var1;
   }

   public void setBold(Boolean var1) {
      this.bold = var1;
   }

   public void setItalic(Boolean var1) {
      this.italic = var1;
   }

   public void setUnderlined(Boolean var1) {
      this.underlined = var1;
   }

   public void setStrikethrough(Boolean var1) {
      this.strikethrough = var1;
   }

   public void setObfuscated(Boolean var1) {
      this.obfuscated = var1;
   }

   public void setInsertion(String var1) {
      this.insertion = var1;
   }

   public void setClickEvent(ClickEvent var1) {
      this.clickEvent = var1;
   }

   public void setHoverEvent(HoverEvent var1) {
      this.hoverEvent = var1;
   }

   public void setReset(boolean var1) {
      this.reset = var1;
   }

   @Override
   public String toString() {
      return "BaseComponent(color="
         + this.getColor()
         + ", font="
         + this.getFont()
         + ", bold="
         + this.bold
         + ", italic="
         + this.italic
         + ", underlined="
         + this.underlined
         + ", strikethrough="
         + this.strikethrough
         + ", obfuscated="
         + this.obfuscated
         + ", insertion="
         + this.getInsertion()
         + ", extra="
         + this.getExtra()
         + ", clickEvent="
         + this.getClickEvent()
         + ", hoverEvent="
         + this.getHoverEvent()
         + ", reset="
         + this.isReset()
         + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof BaseComponent)) {
         return false;
      } else {
         BaseComponent var2 = (BaseComponent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            Boolean var3 = this.bold;
            Boolean var4 = var2.bold;
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               Boolean var5 = this.italic;
               Boolean var6 = var2.italic;
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  Boolean var7 = this.underlined;
                  Boolean var8 = var2.underlined;
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     Boolean var9 = this.strikethrough;
                     Boolean var10 = var2.strikethrough;
                     if (var9 == null ? var10 == null : var9.equals(var10)) {
                        Boolean var11 = this.obfuscated;
                        Boolean var12 = var2.obfuscated;
                        if (var11 == null ? var12 == null : var11.equals(var12)) {
                           ChatColor var13 = this.getColor();
                           ChatColor var14 = var2.getColor();
                           if (var13 == null ? var14 == null : var13.equals(var14)) {
                              String var15 = this.getFont();
                              String var16 = var2.getFont();
                              if (var15 == null ? var16 == null : var15.equals(var16)) {
                                 String var17 = this.getInsertion();
                                 String var18 = var2.getInsertion();
                                 if (var17 == null ? var18 == null : var17.equals(var18)) {
                                    List var19 = this.getExtra();
                                    List var20 = var2.getExtra();
                                    if (var19 == null ? var20 == null : var19.equals(var20)) {
                                       ClickEvent var21 = this.getClickEvent();
                                       ClickEvent var22 = var2.getClickEvent();
                                       if (var21 == null ? var22 == null : var21.equals(var22)) {
                                          HoverEvent var23 = this.getHoverEvent();
                                          HoverEvent var24 = var2.getHoverEvent();
                                          return var23 == null ? var24 == null : var23.equals(var24);
                                       } else {
                                          return false;
                                       }
                                    } else {
                                       return false;
                                    }
                                 } else {
                                    return false;
                                 }
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof BaseComponent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      Boolean var3 = this.bold;
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      Boolean var4 = this.italic;
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      Boolean var5 = this.underlined;
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      Boolean var6 = this.strikethrough;
      var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
      Boolean var7 = this.obfuscated;
      var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
      ChatColor var8 = this.getColor();
      var2 = var2 * 59 + (var8 == null ? 43 : var8.hashCode());
      String var9 = this.getFont();
      var2 = var2 * 59 + (var9 == null ? 43 : var9.hashCode());
      String var10 = this.getInsertion();
      var2 = var2 * 59 + (var10 == null ? 43 : var10.hashCode());
      List var11 = this.getExtra();
      var2 = var2 * 59 + (var11 == null ? 43 : var11.hashCode());
      ClickEvent var12 = this.getClickEvent();
      var2 = var2 * 59 + (var12 == null ? 43 : var12.hashCode());
      HoverEvent var13 = this.getHoverEvent();
      return var2 * 59 + (var13 == null ? 43 : var13.hashCode());
   }

   public String getInsertion() {
      return this.insertion;
   }

   public List<BaseComponent> getExtra() {
      return this.extra;
   }

   public ClickEvent getClickEvent() {
      return this.clickEvent;
   }

   public HoverEvent getHoverEvent() {
      return this.hoverEvent;
   }

   public boolean isReset() {
      return this.reset;
   }
}
