package net.md_5.bungee.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.lang.reflect.Type;
import java.util.Set;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.KeybindComponent;
import net.md_5.bungee.api.chat.ScoreComponent;
import net.md_5.bungee.api.chat.SelectorComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.EntitySerializer;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.ItemSerializer;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.chat.hover.content.TextSerializer;

public class ComponentSerializer implements JsonDeserializer<BaseComponent> {
   private static final JsonParser JSON_PARSER = new JsonParser();
   private static final Gson gson = new GsonBuilder()
      .registerTypeAdapter(BaseComponent.class, new ComponentSerializer())
      .registerTypeAdapter(TextComponent.class, new TextComponentSerializer())
      .registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer())
      .registerTypeAdapter(KeybindComponent.class, new KeybindComponentSerializer())
      .registerTypeAdapter(ScoreComponent.class, new ScoreComponentSerializer())
      .registerTypeAdapter(SelectorComponent.class, new SelectorComponentSerializer())
      .registerTypeAdapter(Entity.class, new EntitySerializer())
      .registerTypeAdapter(Text.class, new TextSerializer())
      .registerTypeAdapter(Item.class, new ItemSerializer())
      .registerTypeAdapter(ItemTag.class, new ItemTag.Serializer())
      .create();
   public static final ThreadLocal<Set<BaseComponent>> serializedComponents = new ThreadLocal<>();

   public static BaseComponent[] parse(String var0) {
      JsonElement var1 = JSON_PARSER.parse(var0);
      return var1.isJsonArray()
         ? (BaseComponent[])gson.fromJson(var1, BaseComponent[].class)
         : new BaseComponent[]{(BaseComponent)gson.fromJson(var1, BaseComponent.class)};
   }

   public static String toString(Object var0) {
      return gson.toJson(var0);
   }

   public static String toString(BaseComponent var0) {
      return gson.toJson(var0);
   }

   public static String toString(BaseComponent... var0) {
      return var0.length == 1 ? gson.toJson(var0[0]) : gson.toJson(new TextComponent(var0));
   }

   public BaseComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      if (var1.isJsonPrimitive()) {
         return new TextComponent(var1.getAsString());
      } else {
         JsonObject var4 = var1.getAsJsonObject();
         if (var4.has("translate")) {
            return (BaseComponent)var3.deserialize(var1, TranslatableComponent.class);
         } else if (var4.has("keybind")) {
            return (BaseComponent)var3.deserialize(var1, KeybindComponent.class);
         } else if (var4.has("score")) {
            return (BaseComponent)var3.deserialize(var1, ScoreComponent.class);
         } else {
            return var4.has("selector")
               ? (BaseComponent)var3.deserialize(var1, SelectorComponent.class)
               : (BaseComponent)var3.deserialize(var1, TextComponent.class);
         }
      }
   }
}
