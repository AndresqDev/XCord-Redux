package org.apache.http.config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.http.util.Args;

public final class RegistryBuilder<I> {
   private final Map<String, I> items = new HashMap<>();

   public static <I> RegistryBuilder<I> create() {
      return new RegistryBuilder<>();
   }

   RegistryBuilder() {
   }

   public RegistryBuilder<I> register(String var1, I var2) {
      Args.notEmpty(var1, "ID");
      Args.notNull(var2, "Item");
      this.items.put(var1.toLowerCase(Locale.ROOT), (I)var2);
      return this;
   }

   public Registry<I> build() {
      return new Registry<>(this.items);
   }

   @Override
   public String toString() {
      return this.items.toString();
   }
}
