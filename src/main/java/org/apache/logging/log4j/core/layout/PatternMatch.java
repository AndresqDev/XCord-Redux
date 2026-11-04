package org.apache.logging.log4j.core.layout;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Objects;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(
   name = "PatternMatch",
   category = "Core",
   printObject = true
)
public final class PatternMatch {
   private final String key;
   private final String pattern;

   public PatternMatch(String var1, String var2) {
      this.key = var1;
      this.pattern = var2;
   }

   public String getKey() {
      return this.key;
   }

   public String getPattern() {
      return this.pattern;
   }

   @Override
   public String toString() {
      return this.key + '=' + this.pattern;
   }

   @PluginBuilderFactory
   public static PatternMatch.Builder newBuilder() {
      return new PatternMatch.Builder();
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.key, this.pattern);
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
         PatternMatch var2 = (PatternMatch)var1;
         return !Objects.equals(this.key, var2.key) ? false : Objects.equals(this.pattern, var2.pattern);
      }
   }

   public static class Builder implements Serializable, org.apache.logging.log4j.core.util.Builder<PatternMatch> {
      private static final long serialVersionUID = 1L;
      @PluginBuilderAttribute
      private String key;
      @PluginBuilderAttribute
      private String pattern;

      public PatternMatch.Builder setKey(String var1) {
         this.key = var1;
         return this;
      }

      public PatternMatch.Builder setPattern(String var1) {
         this.pattern = var1;
         return this;
      }

      public PatternMatch build() {
         return new PatternMatch(this.key, this.pattern);
      }

      protected Object readResolve() throws ObjectStreamException {
         return new PatternMatch(this.key, this.pattern);
      }
   }
}
