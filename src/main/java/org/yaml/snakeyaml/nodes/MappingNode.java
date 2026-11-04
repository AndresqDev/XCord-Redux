package org.yaml.snakeyaml.nodes;

import java.util.List;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

public class MappingNode extends CollectionNode<NodeTuple> {
   private List<NodeTuple> value;
   private boolean merged = false;

   public MappingNode(Tag var1, boolean var2, List<NodeTuple> var3, Mark var4, Mark var5, DumperOptions.FlowStyle var6) {
      super(var1, var4, var5, var6);
      if (var3 == null) {
         throw new NullPointerException("value in a Node is required.");
      } else {
         this.value = var3;
         this.resolved = var2;
      }
   }

   public MappingNode(Tag var1, List<NodeTuple> var2, DumperOptions.FlowStyle var3) {
      this(var1, true, var2, null, null, var3);
   }

   @Deprecated
   public MappingNode(Tag var1, boolean var2, List<NodeTuple> var3, Mark var4, Mark var5, Boolean var6) {
      this(var1, var2, var3, var4, var5, DumperOptions.FlowStyle.fromBoolean(var6));
   }

   @Deprecated
   public MappingNode(Tag var1, List<NodeTuple> var2, Boolean var3) {
      this(var1, var2, DumperOptions.FlowStyle.fromBoolean(var3));
   }

   @Override
   public NodeId getNodeId() {
      return NodeId.mapping;
   }

   @Override
   public List<NodeTuple> getValue() {
      return this.value;
   }

   public void setValue(List<NodeTuple> var1) {
      this.value = var1;
   }

   public void setOnlyKeyType(Class<? extends Object> var1) {
      for (NodeTuple var3 : this.value) {
         var3.getKeyNode().setType(var1);
      }
   }

   public void setTypes(Class<? extends Object> var1, Class<? extends Object> var2) {
      for (NodeTuple var4 : this.value) {
         var4.getValueNode().setType(var2);
         var4.getKeyNode().setType(var1);
      }
   }

   @Override
   public String toString() {
      StringBuilder var2 = new StringBuilder();

      for (NodeTuple var4 : this.getValue()) {
         var2.append("{ key=");
         var2.append(var4.getKeyNode());
         var2.append("; value=");
         if (var4.getValueNode() instanceof CollectionNode) {
            var2.append(System.identityHashCode(var4.getValueNode()));
         } else {
            var2.append(var4.toString());
         }

         var2.append(" }");
      }

      String var1 = var2.toString();
      return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", values=" + var1 + ")>";
   }

   public void setMerged(boolean var1) {
      this.merged = var1;
   }

   public boolean isMerged() {
      return this.merged;
   }
}
