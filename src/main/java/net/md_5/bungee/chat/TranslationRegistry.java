package net.md_5.bungee.chat;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;

public final class TranslationRegistry {
   public static final TranslationRegistry INSTANCE = new TranslationRegistry();
   private final List<TranslationRegistry.TranslationProvider> providers = new LinkedList<>();

   private void addProvider(TranslationRegistry.TranslationProvider var1) {
      this.providers.add(var1);
   }

   public String translate(String var1) {
      for (TranslationRegistry.TranslationProvider var3 : this.providers) {
         String var4 = var3.translate(var1);
         if (var4 != null) {
            return var4;
         }
      }

      return var1;
   }

   public List<TranslationRegistry.TranslationProvider> getProviders() {
      return this.providers;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TranslationRegistry)) {
         return false;
      } else {
         TranslationRegistry var2 = (TranslationRegistry)var1;
         List var3 = this.getProviders();
         List var4 = var2.getProviders();
         return var3 == null ? var4 == null : var3.equals(var4);
      }
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      List var3 = this.getProviders();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }

   @Override
   public String toString() {
      return "TranslationRegistry(providers=" + this.getProviders() + ")";
   }

   private TranslationRegistry() {
   }

   static {
      try {
         INSTANCE.addProvider(new TranslationRegistry.JsonProvider("/assets/minecraft/lang/en_us.json"));
      } catch (Exception var3) {
      }

      try {
         INSTANCE.addProvider(new TranslationRegistry.JsonProvider("/mojang-translations/en_us.json"));
      } catch (Exception var2) {
      }

      try {
         INSTANCE.addProvider(new TranslationRegistry.ResourceBundleProvider("mojang-translations/en_US"));
      } catch (Exception var1) {
      }
   }

   private static class JsonProvider implements TranslationRegistry.TranslationProvider {
      private final Map<String, String> translations = new HashMap<>();

      public JsonProvider(String var1) throws IOException {
         InputStreamReader var2 = new InputStreamReader(TranslationRegistry.JsonProvider.class.getResourceAsStream(var1), Charsets.UTF_8);

         try {
            JsonObject var3 = (JsonObject)new Gson().fromJson(var2, JsonObject.class);

            for (Entry var5 : var3.entrySet()) {
               this.translations.put((String)var5.getKey(), ((JsonElement)var5.getValue()).getAsString());
            }
         } catch (Throwable var7) {
            try {
               var2.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         var2.close();
      }

      @Override
      public String translate(String var1) {
         return this.translations.get(var1);
      }

      public Map<String, String> getTranslations() {
         return this.translations;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof TranslationRegistry.JsonProvider)) {
            return false;
         } else {
            TranslationRegistry.JsonProvider var2 = (TranslationRegistry.JsonProvider)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else {
               Map var3 = this.getTranslations();
               Map var4 = var2.getTranslations();
               return var3 == null ? var4 == null : var3.equals(var4);
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof TranslationRegistry.JsonProvider;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         byte var2 = 1;
         Map var3 = this.getTranslations();
         return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      }

      @Override
      public String toString() {
         return "TranslationRegistry.JsonProvider()";
      }
   }

   private static class ResourceBundleProvider implements TranslationRegistry.TranslationProvider {
      private final ResourceBundle bundle;

      public ResourceBundleProvider(String var1) {
         this.bundle = ResourceBundle.getBundle(var1);
      }

      @Override
      public String translate(String var1) {
         return this.bundle.containsKey(var1) ? this.bundle.getString(var1) : null;
      }

      public ResourceBundle getBundle() {
         return this.bundle;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof TranslationRegistry.ResourceBundleProvider)) {
            return false;
         } else {
            TranslationRegistry.ResourceBundleProvider var2 = (TranslationRegistry.ResourceBundleProvider)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else {
               ResourceBundle var3 = this.getBundle();
               ResourceBundle var4 = var2.getBundle();
               return var3 == null ? var4 == null : var3.equals(var4);
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof TranslationRegistry.ResourceBundleProvider;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         byte var2 = 1;
         ResourceBundle var3 = this.getBundle();
         return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      }

      @Override
      public String toString() {
         return "TranslationRegistry.ResourceBundleProvider(bundle=" + this.getBundle() + ")";
      }
   }

   private interface TranslationProvider {
      String translate(String var1);
   }
}
