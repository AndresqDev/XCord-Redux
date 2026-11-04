package org.yaml.snakeyaml.comments;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.CommentEvent;

public class CommentLine {
   private Mark startMark;
   private Mark endMark;
   private String value;
   private CommentType commentType;

   public CommentLine(CommentEvent var1) {
      this(var1.getStartMark(), var1.getEndMark(), var1.getValue(), var1.getCommentType());
   }

   public CommentLine(Mark var1, Mark var2, String var3, CommentType var4) {
      this.startMark = var1;
      this.endMark = var2;
      this.value = var3;
      this.commentType = var4;
   }

   public Mark getEndMark() {
      return this.endMark;
   }

   public Mark getStartMark() {
      return this.startMark;
   }

   public CommentType getCommentType() {
      return this.commentType;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      return "<" + this.getClass().getName() + " (type=" + this.getCommentType() + ", value=" + this.getValue() + ")>";
   }
}
