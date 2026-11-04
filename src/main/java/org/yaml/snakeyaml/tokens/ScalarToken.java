package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

public final class ScalarToken extends Token {
   private final String value;
   private final boolean plain;
   private final DumperOptions.ScalarStyle style;

   public ScalarToken(String var1, Mark var2, Mark var3, boolean var4) {
      this(var1, var4, var2, var3, DumperOptions.ScalarStyle.PLAIN);
   }

   public ScalarToken(String var1, boolean var2, Mark var3, Mark var4, DumperOptions.ScalarStyle var5) {
      super(var3, var4);
      this.value = var1;
      this.plain = var2;
      if (var5 == null) {
         throw new NullPointerException("Style must be provided.");
      } else {
         this.style = var5;
      }
   }

   public boolean getPlain() {
      return this.plain;
   }

   public String getValue() {
      return this.value;
   }

   public DumperOptions.ScalarStyle getStyle() {
      return this.style;
   }

   @Override
   public Token.ID getTokenId() {
      return Token.ID.Scalar;
   }
}
