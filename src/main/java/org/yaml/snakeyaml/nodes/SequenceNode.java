package org.yaml.snakeyaml.nodes;

import java.util.List;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

public class SequenceNode extends CollectionNode<Node> {
   private final List<Node> value;

   public SequenceNode(Tag var1, boolean var2, List<Node> var3, Mark var4, Mark var5, DumperOptions.FlowStyle var6) {
      super(var1, var4, var5, var6);
      if (var3 == null) {
         throw new NullPointerException("value in a Node is required.");
      } else {
         this.value = var3;
         this.resolved = var2;
      }
   }

   public SequenceNode(Tag var1, List<Node> var2, DumperOptions.FlowStyle var3) {
      this(var1, true, var2, null, null, var3);
   }

   @Deprecated
   public SequenceNode(Tag var1, List<Node> var2, Boolean var3) {
      this(var1, var2, DumperOptions.FlowStyle.fromBoolean(var3));
   }

   @Deprecated
   public SequenceNode(Tag var1, boolean var2, List<Node> var3, Mark var4, Mark var5, Boolean var6) {
      this(var1, var2, var3, var4, var5, DumperOptions.FlowStyle.fromBoolean(var6));
   }

   @Override
   public NodeId getNodeId() {
      return NodeId.sequence;
   }

   @Override
   public List<Node> getValue() {
      return this.value;
   }

   public void setListType(Class<? extends Object> var1) {
      for (Node var3 : this.value) {
         var3.setType(var1);
      }
   }

   @Override
   public String toString() {
      return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", value=" + this.getValue() + ")>";
   }
}
