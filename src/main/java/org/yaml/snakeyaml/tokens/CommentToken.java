package org.yaml.snakeyaml.tokens;

import java.util.Objects;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;

public final class CommentToken extends Token {
   private final CommentType type;
   private final String value;

   public CommentToken(CommentType var1, String var2, Mark var3, Mark var4) {
      super(var3, var4);
      Objects.requireNonNull(var1);
      this.type = var1;
      Objects.requireNonNull(var2);
      this.value = var2;
   }

   public CommentType getCommentType() {
      return this.type;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public Token.ID getTokenId() {
      return Token.ID.Comment;
   }
}
