package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public final class TextComponent extends BaseComponent {
   private static final Pattern url = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
   private String text;

   public static BaseComponent[] fromLegacyText(String var0) {
      return fromLegacyText(var0, ChatColor.WHITE);
   }

   public static BaseComponent[] fromLegacyText(String var0, ChatColor var1) {
      ArrayList var2 = new ArrayList();
      StringBuilder var3 = new StringBuilder();
      TextComponent var4 = new TextComponent();
      Matcher var5 = url.matcher(var0);

      for (int var6 = 0; var6 < var0.length(); var6++) {
         char var7 = var0.charAt(var6);
         if (var7 == 167) {
            if (++var6 >= var0.length()) {
               break;
            }

            var7 = var0.charAt(var6);
            if (var7 >= 'A' && var7 <= 'Z') {
               var7 = (char)(var7 + ' ');
            }

            ChatColor var8;
            if (var7 == 'x' && var6 + 12 < var0.length()) {
               StringBuilder var9 = new StringBuilder("#");

               for (int var10 = 0; var10 < 6; var10++) {
                  var9.append(var0.charAt(var6 + 2 + var10 * 2));
               }

               try {
                  var8 = ChatColor.of(var9.toString());
               } catch (IllegalArgumentException var11) {
                  var8 = null;
               }

               var6 += 12;
            } else {
               var8 = ChatColor.getByChar(var7);
            }

            if (var8 != null) {
               if (var3.length() > 0) {
                  TextComponent var15 = var4;
                  var4 = new TextComponent(var4);
                  var15.setText(var3.toString());
                  var3 = new StringBuilder();
                  var2.add(var15);
               }

               if (var8 == ChatColor.BOLD) {
                  var4.setBold(Boolean.valueOf(true));
               } else if (var8 == ChatColor.ITALIC) {
                  var4.setItalic(Boolean.valueOf(true));
               } else if (var8 == ChatColor.UNDERLINE) {
                  var4.setUnderlined(Boolean.valueOf(true));
               } else if (var8 == ChatColor.STRIKETHROUGH) {
                  var4.setStrikethrough(Boolean.valueOf(true));
               } else if (var8 == ChatColor.MAGIC) {
                  var4.setObfuscated(Boolean.valueOf(true));
               } else {
                  if (var8 == ChatColor.RESET) {
                     var8 = var1;
                  }

                  var4 = new TextComponent();
                  var4.setColor(var8);
                  var4.setReset(true);
               }
            }
         } else {
            int var14 = var0.indexOf(32, var6);
            if (var14 == -1) {
               var14 = var0.length();
            }

            if (var5.region(var6, var14).find()) {
               if (var3.length() > 0) {
                  TextComponent var16 = var4;
                  var4 = new TextComponent(var4);
                  var16.setText(var3.toString());
                  var3 = new StringBuilder();
                  var2.add(var16);
               }

               TextComponent var12 = new TextComponent(var4);
               String var18 = var0.substring(var6, var14);
               var12.setText(var18);
               var12.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, var18.startsWith("http") ? var18 : "http://" + var18));
               var2.add(var12);
               var6 += var14 - var6 - 1;
               var4 = var4;
            } else {
               var3.append(var7);
            }
         }
      }

      var4.setText(var3.toString());
      var2.add(var4);
      return var2.toArray(new BaseComponent[0]);
   }

   public TextComponent() {
      this.text = "";
   }

   public TextComponent(TextComponent var1) {
      super(var1);
      this.setText(var1.getText());
   }

   public TextComponent(BaseComponent... var1) {
      this();
      if (var1.length != 0) {
         this.setExtra(new ArrayList<>(Arrays.asList(var1)));
      }
   }

   public TextComponent duplicate() {
      return new TextComponent(this);
   }

   @Override
   protected void toPlainText(StringBuilder var1) {
      var1.append(this.text);
      super.toPlainText(var1);
   }

   @Override
   protected void toLegacyText(StringBuilder var1) {
      this.addFormat(var1);
      var1.append(this.text);
      super.toLegacyText(var1);
   }

   @Override
   public String toString() {
      return "TextComponent{text=" + this.text + ", " + super.toString() + '}';
   }

   public String getText() {
      return this.text;
   }

   public void setText(String var1) {
      this.text = var1;
   }

   public TextComponent(String var1) {
      this.text = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TextComponent)) {
         return false;
      } else {
         TextComponent var2 = (TextComponent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else {
            String var3 = this.getText();
            String var4 = var2.getText();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   @Override
   protected boolean canEqual(Object var1) {
      return var1 instanceof TextComponent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = super.hashCode();
      String var3 = this.getText();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
