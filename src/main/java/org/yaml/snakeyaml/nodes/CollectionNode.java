package org.yaml.snakeyaml.nodes;

import java.util.List;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

public abstract class CollectionNode<T> extends Node {
   private DumperOptions.FlowStyle flowStyle;

   public CollectionNode(Tag var1, Mark var2, Mark var3, DumperOptions.FlowStyle var4) {
      super(var1, var2, var3);
      this.setFlowStyle(var4);
   }

   @Deprecated
   public CollectionNode(Tag var1, Mark var2, Mark var3, Boolean var4) {
      this(var1, var2, var3, DumperOptions.FlowStyle.fromBoolean(var4));
   }

   public abstract List<T> getValue();

   public DumperOptions.FlowStyle getFlowStyle() {
      return this.flowStyle;
   }

   public void setFlowStyle(DumperOptions.FlowStyle var1) {
      if (var1 == null) {
         throw new NullPointerException("Flow style must be provided.");
      } else {
         this.flowStyle = var1;
      }
   }

   @Deprecated
   public void setFlowStyle(Boolean var1) {
      this.setFlowStyle(DumperOptions.FlowStyle.fromBoolean(var1));
   }

   public void setEndMark(Mark var1) {
      this.endMark = var1;
   }
}
