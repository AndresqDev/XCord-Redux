package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.chat.TranslationRegistry;

public final class TranslatableComponent extends BaseComponent {
   private final Pattern format = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
   private String translate;
   private List<BaseComponent> with;

   public TranslatableComponent(TranslatableComponent var1) {
      super(var1);
      this.setTranslate(var1.getTranslate());
      if (var1.getWith() != null) {
         ArrayList var2 = new ArrayList();

         for (BaseComponent var4 : var1.getWith()) {
            var2.add(var4.duplicate());
         }

         this.setWith(var2);
      }
   }

   public TranslatableComponent(String var1, Object... var2) {
      this.setTranslate(var1);
      if (var2 != null && var2.length != 0) {
         ArrayList var3 = new ArrayList();

         for (Object var7 : var2) {
            if (var7 instanceof BaseComponent) {
               var3.add((BaseComponent)var7);
            } else {
               var3.add(new TextComponent(String.valueOf(var7)));
            }
         }

         this.setWith(var3);
      }
   }

   public TranslatableComponent duplicate() {
      return new TranslatableComponent(this);
   }

   public void setWith(List<BaseComponent> var1) {
      for (BaseComponent var3 : var1) {
         var3.parent = this;
      }

      this.with = var1;
   }

   public void addWith(String var1) {
      this.addWith(new TextComponent(var1));
   }

   public void addWith(BaseComponent var1) {
      if (this.with == null) {
         this.with = new ArrayList<>();
      }

      var1.parent = this;
      this.with.add(var1);
   }

   @Override
   protected void toPlainText(StringBuilder var1) {
      this.convert(var1, false);
      super.toPlainText(var1);
   }

   @Override
   protected void toLegacyText(StringBuilder var1) {
      this.convert(var1, true);
      super.toLegacyText(var1);
   }

   private void convert(StringBuilder var1, boolean var2) {
      String var3 = TranslationRegistry.INSTANCE.translate(this.translate);
      Matcher var4 = this.format.matcher(var3);
      int var5 = 0;
      int var6 = 0;

      while (var4.find(var5)) {
         int var7 = var4.start();
         if (var7 != var5) {
            if (var2) {
               this.addFormat(var1);
            }

            var1.append(var3.substring(var5, var7));
         }

         var5 = var4.end();
         String var8 = var4.group(2);
         switch (var8.charAt(0)) {
            case '%':
               if (var2) {
                  this.addFormat(var1);
               }

               var1.append('%');
               break;
            case 'd':
            case 's':
               String var9 = var4.group(1);
               BaseComponent var10 = this.with.get(var9 != null ? Integer.parseInt(var9) - 1 : var6++);
               if (var2) {
                  var10.toLegacyText(var1);
               } else {
                  var10.toPlainText(var1);
               }
         }
      }

      if (var3.length() != var5) {
         if (var2) {
            this.addFormat(var1);
         }

         var1.append(var3.substring(var5, var3.length()));
      }
   }

   public Pattern getFormat() {
      return this.format;
   }

   public String getTranslate() {
      return this.translate;
   }

   public List<BaseComponent> getWith() {
      return this.with;
   }

   public void setTranslate(String var1) {
      this.translate = var1;
   }

   @Override
   public String toString() {
      return "TranslatableComponent(format=" + this.getFormat() + ", translate=" + this.getTranslate() + ", with=" + this.getWith() + ")";
   }

   public TranslatableComponent() {
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TranslatableComponent)) {
         return false;
      } else {
         TranslatableComponent var2 = (TranslatableComponent)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (!super.equals(var1)) {
            return false;
         } else {
            Pattern var3 = this.getFormat();
            Pattern var4 = var2.getFormat();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getTranslate();
               String var6 = var2.getTranslate();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  List var7 = this.getWith();
                  List var8 = var2.getWith();
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
      return var1 instanceof TranslatableComponent;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = super.hashCode();
      Pattern var3 = this.getFormat();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getTranslate();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      List var5 = this.getWith();
      return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
   }
}
