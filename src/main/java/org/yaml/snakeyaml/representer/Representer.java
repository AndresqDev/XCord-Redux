package org.yaml.snakeyaml.representer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class Representer extends SafeRepresenter {
   protected Map<Class<? extends Object>, TypeDescription> typeDefinitions = Collections.emptyMap();

   public Representer() {
      this.representers.put(null, new Representer.RepresentJavaBean());
   }

   public Representer(DumperOptions var1) {
      super(var1);
      this.representers.put(null, new Representer.RepresentJavaBean());
   }

   public TypeDescription addTypeDescription(TypeDescription var1) {
      if (Collections.EMPTY_MAP == this.typeDefinitions) {
         this.typeDefinitions = new HashMap<>();
      }

      if (var1.getTag() != null) {
         this.addClassTag(var1.getType(), var1.getTag());
      }

      var1.setPropertyUtils(this.getPropertyUtils());
      return this.typeDefinitions.put(var1.getType(), var1);
   }

   @Override
   public void setPropertyUtils(PropertyUtils var1) {
      super.setPropertyUtils(var1);

      for (TypeDescription var4 : this.typeDefinitions.values()) {
         var4.setPropertyUtils(var1);
      }
   }

   protected MappingNode representJavaBean(Set<Property> var1, Object var2) {
      ArrayList var3 = new ArrayList(var1.size());
      Tag var5 = this.classTags.get(var2.getClass());
      Tag var4 = var5 != null ? var5 : new Tag((Class<? extends Object>)var2.getClass());
      MappingNode var6 = new MappingNode(var4, var3, DumperOptions.FlowStyle.AUTO);
      this.representedObjects.put(var2, var6);
      DumperOptions.FlowStyle var7 = DumperOptions.FlowStyle.FLOW;

      for (Property var9 : var1) {
         Object var10 = var9.get(var2);
         Tag var11 = var10 == null ? null : this.classTags.get(var10.getClass());
         NodeTuple var12 = this.representJavaBeanProperty(var2, var9, var10, var11);
         if (var12 != null) {
            if (!((ScalarNode)var12.getKeyNode()).isPlain()) {
               var7 = DumperOptions.FlowStyle.BLOCK;
            }

            Node var13 = var12.getValueNode();
            if (!(var13 instanceof ScalarNode) || !((ScalarNode)var13).isPlain()) {
               var7 = DumperOptions.FlowStyle.BLOCK;
            }

            var3.add(var12);
         }
      }

      if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
         var6.setFlowStyle(this.defaultFlowStyle);
      } else {
         var6.setFlowStyle(var7);
      }

      return var6;
   }

   protected NodeTuple representJavaBeanProperty(Object var1, Property var2, Object var3, Tag var4) {
      ScalarNode var5 = (ScalarNode)this.representData(var2.getName());
      boolean var6 = this.representedObjects.containsKey(var3);
      Node var7 = this.representData(var3);
      if (var3 != null && !var6) {
         NodeId var8 = var7.getNodeId();
         if (var4 == null) {
            if (var8 == NodeId.scalar) {
               if (var2.getType() != Enum.class && var3 instanceof Enum) {
                  var7.setTag(Tag.STR);
               }
            } else {
               if (var8 == NodeId.mapping && var2.getType() == var3.getClass() && !(var3 instanceof Map) && !var7.getTag().equals(Tag.SET)) {
                  var7.setTag(Tag.MAP);
               }

               this.checkGlobalTag(var2, var7, var3);
            }
         }
      }

      return new NodeTuple(var5, var7);
   }

   protected void checkGlobalTag(Property var1, Node var2, Object var3) {
      if (!var3.getClass().isArray() || !var3.getClass().getComponentType().isPrimitive()) {
         Class[] var4 = var1.getActualTypeArguments();
         if (var4 != null) {
            if (var2.getNodeId() == NodeId.sequence) {
               Class var5 = var4[0];
               SequenceNode var6 = (SequenceNode)var2;
               Object var7 = Collections.EMPTY_LIST;
               if (var3.getClass().isArray()) {
                  var7 = Arrays.asList((Object[])var3);
               } else if (var3 instanceof Iterable) {
                  var7 = (Iterable)var3;
               }

               Iterator var8 = var7.iterator();
               if (var8.hasNext()) {
                  for (Node var10 : var6.getValue()) {
                     Object var11 = var8.next();
                     if (var11 != null && var5.equals(var11.getClass()) && var10.getNodeId() == NodeId.mapping) {
                        var10.setTag(Tag.MAP);
                     }
                  }
               }
            } else if (var3 instanceof Set) {
               Class var13 = var4[0];
               MappingNode var15 = (MappingNode)var2;
               Iterator var17 = var15.getValue().iterator();

               for (Object var23 : (Set)var3) {
                  NodeTuple var24 = (NodeTuple)var17.next();
                  Node var12 = var24.getKeyNode();
                  if (var13.equals(var23.getClass()) && var12.getNodeId() == NodeId.mapping) {
                     var12.setTag(Tag.MAP);
                  }
               }
            } else if (var3 instanceof Map) {
               Class var14 = var4[0];
               Class var16 = var4[1];
               MappingNode var18 = (MappingNode)var2;

               for (NodeTuple var22 : var18.getValue()) {
                  this.resetTag(var14, var22.getKeyNode());
                  this.resetTag(var16, var22.getValueNode());
               }
            }
         }
      }
   }

   private void resetTag(Class<? extends Object> var1, Node var2) {
      Tag var3 = var2.getTag();
      if (var3.matches(var1)) {
         if (Enum.class.isAssignableFrom(var1)) {
            var2.setTag(Tag.STR);
         } else {
            var2.setTag(Tag.MAP);
         }
      }
   }

   protected Set<Property> getProperties(Class<? extends Object> var1) {
      return this.typeDefinitions.containsKey(var1) ? this.typeDefinitions.get(var1).getProperties() : this.getPropertyUtils().getProperties(var1);
   }

   protected class RepresentJavaBean implements Represent {
      @Override
      public Node representData(Object var1) {
         return Representer.this.representJavaBean(Representer.this.getProperties((Class<? extends Object>)var1.getClass()), var1);
      }
   }
}
