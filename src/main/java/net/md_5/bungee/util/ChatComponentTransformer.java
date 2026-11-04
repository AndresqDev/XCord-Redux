package net.md_5.bungee.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.score.Score;

public final class ChatComponentTransformer {
   private static final ChatComponentTransformer INSTANCE = new ChatComponentTransformer();
   private static final Pattern SELECTOR_PATTERN = Pattern.compile("^@([pares])(?:\\[([^ ]*)\\])?$");

   public BaseComponent[] legacyHoverTransform(ProxiedPlayer var1, BaseComponent... var2) {
      if (var1.getPendingConnection().getVersion() < 735) {
         for (int var3 = 0; var3 < var2.length; var3++) {
            BaseComponent var4 = var2[var3];
            if (var4.getHoverEvent() != null && !var4.getHoverEvent().isLegacy()) {
               var4 = var4.duplicate();
               var4.getHoverEvent().setLegacy(true);
               if (var4.getHoverEvent().getContents().size() > 1) {
                  Content var5 = var4.getHoverEvent().getContents().get(0);
                  var4.getHoverEvent().getContents().clear();
                  var4.getHoverEvent().getContents().add(var5);
               }

               var2[var3] = var4;
            }
         }
      }

      return var2;
   }

   public static ChatComponentTransformer getInstance() {
      return INSTANCE;
   }

   public BaseComponent[] transform(ProxiedPlayer var1, BaseComponent... var2) {
      return this.transform(var1, false, var2);
   }

   public BaseComponent[] transform(ProxiedPlayer var1, boolean var2, BaseComponent... var3) {
      if (var3 != null && var3.length >= 1 && (var3.length != 1 || var3[0] != null)) {
         if (var2) {
            var3 = this.legacyHoverTransform(var1, var3);
         }

         for (BaseComponent var7 : var3) {
            if (var7.getExtra() != null && !var7.getExtra().isEmpty()) {
               ArrayList var8 = Lists.newArrayList(this.transform(var1, var2, var7.getExtra().toArray(new BaseComponent[0])));
               var7.setExtra(var8);
            }

            if (var7 instanceof ScoreComponent) {
               this.transformScoreComponent(var1, (ScoreComponent)var7);
            }
         }

         return var3;
      } else {
         return new BaseComponent[]{new TextComponent("")};
      }
   }

   private void transformScoreComponent(ProxiedPlayer var1, ScoreComponent var2) {
      Preconditions.checkArgument(!this.isSelectorPattern(var2.getName()), "Cannot transform entity selector patterns");
      if (var2.getValue() == null || var2.getValue().isEmpty()) {
         if (var2.getName().equals("*")) {
            var2.setName(var1.getName());
         }

         if (var1.getScoreboard().getObjective(var2.getObjective()) != null) {
            Score var3 = var1.getScoreboard().getScore(var2.getName());
            if (var3 != null) {
               var2.setValue(Integer.toString(var3.getValue()));
            }
         }
      }
   }

   public boolean isSelectorPattern(String var1) {
      return SELECTOR_PATTERN.matcher(var1).matches();
   }

   private ChatComponentTransformer() {
   }
}
