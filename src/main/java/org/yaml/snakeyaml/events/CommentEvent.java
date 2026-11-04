package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;

public final class CommentEvent extends Event {
   private final CommentType type;
   private final String value;

   public CommentEvent(CommentType var1, String var2, Mark var3, Mark var4) {
      super(var3, var4);
      if (var1 == null) {
         throw new NullPointerException("Event Type must be provided.");
      } else {
         this.type = var1;
         if (var2 == null) {
            throw new NullPointerException("Value must be provided.");
         } else {
            this.value = var2;
         }
      }
   }

   public String getValue() {
      return this.value;
   }

   public CommentType getCommentType() {
      return this.type;
   }

   @Override
   protected String getArguments() {
      return super.getArguments() + "type=" + this.type + ", value=" + this.value;
   }

   @Override
   public Event.ID getEventId() {
      return Event.ID.Comment;
   }
}
