package net.md_5.bungee.chat;

import com.google.common.base.Preconditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Locale;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Content;

public class BaseComponentSerializer {
   protected void deserialize(JsonObject var1, BaseComponent var2, JsonDeserializationContext var3) {
      if (var1.has("bold")) {
         var2.setBold(var1.get("bold").getAsBoolean());
      }

      if (var1.has("italic")) {
         var2.setItalic(var1.get("italic").getAsBoolean());
      }

      if (var1.has("underlined")) {
         var2.setUnderlined(var1.get("underlined").getAsBoolean());
      }

      if (var1.has("strikethrough")) {
         var2.setStrikethrough(var1.get("strikethrough").getAsBoolean());
      }

      if (var1.has("obfuscated")) {
         var2.setObfuscated(var1.get("obfuscated").getAsBoolean());
      }

      if (var1.has("color")) {
         var2.setColor(ChatColor.of(var1.get("color").getAsString()));
      }

      if (var1.has("insertion")) {
         var2.setInsertion(var1.get("insertion").getAsString());
      }

      if (var1.has("clickEvent")) {
         JsonObject var4 = var1.getAsJsonObject("clickEvent");
         var2.setClickEvent(
            new ClickEvent(
               ClickEvent.Action.valueOf(var4.get("action").getAsString().toUpperCase(Locale.ROOT)), var4.has("value") ? var4.get("value").getAsString() : ""
            )
         );
      }

      if (var1.has("hoverEvent")) {
         JsonObject var13 = var1.getAsJsonObject("hoverEvent");
         HoverEvent var5 = null;
         HoverEvent.Action var6 = HoverEvent.Action.valueOf(var13.get("action").getAsString().toUpperCase(Locale.ROOT));

         for (String var8 : Arrays.asList("value", "contents")) {
            if (var13.has(var8)) {
               JsonElement var9 = var13.get(var8);

               try {
                  BaseComponent[] var10;
                  if (var9.isJsonArray()) {
                     var10 = (BaseComponent[])var3.deserialize(var9, BaseComponent[].class);
                  } else {
                     var10 = new BaseComponent[]{(BaseComponent)var3.deserialize(var9, BaseComponent.class)};
                  }

                  var5 = new HoverEvent(var6, var10);
               } catch (JsonParseException var12) {
                  Content[] var11;
                  if (var9.isJsonArray()) {
                     var11 = (Content[])var3.deserialize(var9, HoverEvent.getClass(var6, true));
                  } else {
                     var11 = new Content[]{(Content)var3.deserialize(var9, HoverEvent.getClass(var6, false))};
                  }

                  var5 = new HoverEvent(var6, new ArrayList<>(Arrays.asList(var11)));
               }
               break;
            }
         }

         if (var5 != null) {
            var2.setHoverEvent(var5);
         }
      }

      if (var1.has("font")) {
         var2.setFont(var1.get("font").getAsString());
      }

      if (var1.has("extra")) {
         var2.setExtra(Arrays.asList((BaseComponent[])var3.deserialize(var1.get("extra"), BaseComponent[].class)));
      }
   }

   protected void serialize(JsonObject var1, BaseComponent var2, JsonSerializationContext var3) {
      boolean var4 = false;
      if (ComponentSerializer.serializedComponents.get() == null) {
         var4 = true;
         ComponentSerializer.serializedComponents.set(Collections.newSetFromMap(new IdentityHashMap<>()));
      }

      try {
         Preconditions.checkArgument(!ComponentSerializer.serializedComponents.get().contains(var2), "Component loop");
         ComponentSerializer.serializedComponents.get().add(var2);
         if (var2.isBoldRaw() != null) {
            var1.addProperty("bold", var2.isBoldRaw());
         }

         if (var2.isItalicRaw() != null) {
            var1.addProperty("italic", var2.isItalicRaw());
         }

         if (var2.isUnderlinedRaw() != null) {
            var1.addProperty("underlined", var2.isUnderlinedRaw());
         }

         if (var2.isStrikethroughRaw() != null) {
            var1.addProperty("strikethrough", var2.isStrikethroughRaw());
         }

         if (var2.isObfuscatedRaw() != null) {
            var1.addProperty("obfuscated", var2.isObfuscatedRaw());
         }

         if (var2.getColorRaw() != null) {
            var1.addProperty("color", var2.getColorRaw().getName());
         }

         if (var2.getInsertion() != null) {
            var1.addProperty("insertion", var2.getInsertion());
         }

         if (var2.getClickEvent() != null) {
            JsonObject var5 = new JsonObject();
            var5.addProperty("action", var2.getClickEvent().getAction().toString().toLowerCase(Locale.ROOT));
            var5.addProperty("value", var2.getClickEvent().getValue());
            var1.add("clickEvent", var5);
         }

         if (var2.getHoverEvent() != null) {
            JsonObject var9 = new JsonObject();
            var9.addProperty("action", var2.getHoverEvent().getAction().toString().toLowerCase(Locale.ROOT));
            if (var2.getHoverEvent().isLegacy()) {
               var9.add("value", var3.serialize(var2.getHoverEvent().getContents().get(0)));
            } else {
               var9.add(
                  "contents",
                  var3.serialize(
                     var2.getHoverEvent().getContents().size() == 1 ? var2.getHoverEvent().getContents().get(0) : var2.getHoverEvent().getContents()
                  )
               );
            }

            var1.add("hoverEvent", var9);
         }

         if (var2.getFontRaw() != null) {
            var1.addProperty("font", var2.getFontRaw());
         }

         if (var2.getExtra() != null) {
            var1.add("extra", var3.serialize(var2.getExtra()));
         }
      } finally {
         ComponentSerializer.serializedComponents.get().remove(var2);
         if (var4) {
            ComponentSerializer.serializedComponents.set(null);
         }
      }
   }
}
