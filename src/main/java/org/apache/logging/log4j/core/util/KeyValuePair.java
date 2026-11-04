package org.apache.logging.log4j.core.util;

import java.util.Objects;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(
   name = "KeyValuePair",
   category = "Core",
   printObject = true
)
public final class KeyValuePair {
   public static final KeyValuePair[] EMPTY_ARRAY = new KeyValuePair[0];
   private final String key;
   private final String value;

   public KeyValuePair(String var1, String var2) {
      this.key = var1;
      this.value = var2;
   }

   public String getKey() {
      return this.key;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return this.key + '=' + this.value;
   }

   @PluginBuilderFactory
   public static KeyValuePair.Builder newBuilder() {
      return new KeyValuePair.Builder();
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.key, this.value);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         KeyValuePair var2 = (KeyValuePair)var1;
         return !Objects.equals(this.key, var2.key) ? false : Objects.equals(this.value, var2.value);
      }
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<KeyValuePair> {
      @PluginBuilderAttribute
      private String key;
      @PluginBuilderAttribute
      private String value;

      public KeyValuePair.Builder setKey(String var1) {
         this.key = var1;
         return this;
      }

      public KeyValuePair.Builder setValue(String var1) {
         this.value = var1;
         return this;
      }

      public KeyValuePair build() {
         return new KeyValuePair(this.key, this.value);
      }
   }
}
