package org.yaml.snakeyaml.nodes;

import java.util.List;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.error.Mark;

public abstract class Node {
   private Tag tag;
   private Mark startMark;
   protected Mark endMark;
   private Class<? extends Object> type;
   private boolean twoStepsConstruction;
   private String anchor;
   private List<CommentLine> inLineComments;
   private List<CommentLine> blockComments;
   private List<CommentLine> endComments;
   protected boolean resolved;
   protected Boolean useClassConstructor;

   public Node(Tag var1, Mark var2, Mark var3) {
      this.setTag(var1);
      this.startMark = var2;
      this.endMark = var3;
      this.type = Object.class;
      this.twoStepsConstruction = false;
      this.resolved = true;
      this.useClassConstructor = null;
      this.inLineComments = null;
      this.blockComments = null;
      this.endComments = null;
   }

   public Tag getTag() {
      return this.tag;
   }

   public Mark getEndMark() {
      return this.endMark;
   }

   public abstract NodeId getNodeId();

   public Mark getStartMark() {
      return this.startMark;
   }

   public void setTag(Tag var1) {
      if (var1 == null) {
         throw new NullPointerException("tag in a Node is required.");
      } else {
         this.tag = var1;
      }
   }

   @Override
   public final boolean equals(Object var1) {
      return super.equals(var1);
   }

   public Class<? extends Object> getType() {
      return this.type;
   }

   public void setType(Class<? extends Object> var1) {
      if (!var1.isAssignableFrom(this.type)) {
         this.type = var1;
      }
   }

   public void setTwoStepsConstruction(boolean var1) {
      this.twoStepsConstruction = var1;
   }

   public boolean isTwoStepsConstruction() {
      return this.twoStepsConstruction;
   }

   @Override
   public final int hashCode() {
      return super.hashCode();
   }

   public boolean useClassConstructor() {
      if (this.useClassConstructor == null) {
         return !this.tag.isSecondary() && this.resolved && !Object.class.equals(this.type) && !this.tag.equals(Tag.NULL)
            ? true
            : this.tag.isCompatible(this.getType());
      } else {
         return this.useClassConstructor;
      }
   }

   public void setUseClassConstructor(Boolean var1) {
      this.useClassConstructor = var1;
   }

   @Deprecated
   public boolean isResolved() {
      return this.resolved;
   }

   public String getAnchor() {
      return this.anchor;
   }

   public void setAnchor(String var1) {
      this.anchor = var1;
   }

   public List<CommentLine> getInLineComments() {
      return this.inLineComments;
   }

   public void setInLineComments(List<CommentLine> var1) {
      this.inLineComments = var1;
   }

   public List<CommentLine> getBlockComments() {
      return this.blockComments;
   }

   public void setBlockComments(List<CommentLine> var1) {
      this.blockComments = var1;
   }

   public List<CommentLine> getEndComments() {
      return this.endComments;
   }

   public void setEndComments(List<CommentLine> var1) {
      this.endComments = var1;
   }
}
