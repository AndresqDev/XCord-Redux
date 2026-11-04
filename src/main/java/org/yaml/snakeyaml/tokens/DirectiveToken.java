package org.yaml.snakeyaml.tokens;

import java.util.List;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;

public final class DirectiveToken<T> extends Token {
   private final String name;
   private final List<T> value;

   public DirectiveToken(String var1, List<T> var2, Mark var3, Mark var4) {
      super(var3, var4);
      this.name = var1;
      if (var2 != null && var2.size() != 2) {
         throw new YAMLException("Two strings must be provided instead of " + String.valueOf(var2.size()));
      } else {
         this.value = var2;
      }
   }

   public String getName() {
      return this.name;
   }

   public List<T> getValue() {
      return this.value;
   }

   @Override
   public Token.ID getTokenId() {
      return Token.ID.Directive;
   }
}
