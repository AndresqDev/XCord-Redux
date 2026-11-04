package org.yaml.snakeyaml.representer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public abstract class BaseRepresenter {
   protected final Map<Class<?>, Represent> representers = new HashMap<>();
   protected Represent nullRepresenter;
   protected final Map<Class<?>, Represent> multiRepresenters = new LinkedHashMap<>();
   protected DumperOptions.ScalarStyle defaultScalarStyle = null;
   protected DumperOptions.FlowStyle defaultFlowStyle = DumperOptions.FlowStyle.AUTO;
   protected final Map<Object, Node> representedObjects = new IdentityHashMap<Object, Node>() {
      private static final long serialVersionUID = -5576159264232131854L;

      public Node put(Object var1, Node var2) {
         return super.put(var1, new AnchorNode(var2));
      }
   };
   protected Object objectToRepresent;
   private PropertyUtils propertyUtils;
   private boolean explicitPropertyUtils = false;

   public Node represent(Object var1) {
      Node var2 = this.representData(var1);
      this.representedObjects.clear();
      this.objectToRepresent = null;
      return var2;
   }

   protected final Node representData(Object var1) {
      this.objectToRepresent = var1;
      if (this.representedObjects.containsKey(this.objectToRepresent)) {
         return this.representedObjects.get(this.objectToRepresent);
      } else if (var1 == null) {
         return this.nullRepresenter.representData(null);
      } else {
         Class var3 = var1.getClass();
         Node var2;
         if (this.representers.containsKey(var3)) {
            Represent var4 = this.representers.get(var3);
            var2 = var4.representData(var1);
         } else {
            for (Class var5 : this.multiRepresenters.keySet()) {
               if (var5 != null && var5.isInstance(var1)) {
                  Represent var6 = this.multiRepresenters.get(var5);
                  return var6.representData(var1);
               }
            }

            if (this.multiRepresenters.containsKey(null)) {
               Represent var8 = this.multiRepresenters.get(null);
               var2 = var8.representData(var1);
            } else {
               Represent var9 = this.representers.get(null);
               var2 = var9.representData(var1);
            }
         }

         return var2;
      }
   }

   protected Node representScalar(Tag var1, String var2, DumperOptions.ScalarStyle var3) {
      if (var3 == null) {
         var3 = this.defaultScalarStyle;
      }

      return new ScalarNode(var1, var2, null, null, var3);
   }

   protected Node representScalar(Tag var1, String var2) {
      return this.representScalar(var1, var2, null);
   }

   protected Node representSequence(Tag var1, Iterable<?> var2, DumperOptions.FlowStyle var3) {
      int var4 = 10;
      if (var2 instanceof List) {
         var4 = ((List)var2).size();
      }

      ArrayList var5 = new ArrayList(var4);
      SequenceNode var6 = new SequenceNode(var1, var5, var3);
      this.representedObjects.put(this.objectToRepresent, var6);
      DumperOptions.FlowStyle var7 = DumperOptions.FlowStyle.FLOW;

      for (Object var9 : var2) {
         Node var10 = this.representData(var9);
         if (!(var10 instanceof ScalarNode) || !((ScalarNode)var10).isPlain()) {
            var7 = DumperOptions.FlowStyle.BLOCK;
         }

         var5.add(var10);
      }

      if (var3 == DumperOptions.FlowStyle.AUTO) {
         if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
            var6.setFlowStyle(this.defaultFlowStyle);
         } else {
            var6.setFlowStyle(var7);
         }
      }

      return var6;
   }

   protected Node representMapping(Tag var1, Map<?, ?> var2, DumperOptions.FlowStyle var3) {
      ArrayList var4 = new ArrayList(var2.size());
      MappingNode var5 = new MappingNode(var1, var4, var3);
      this.representedObjects.put(this.objectToRepresent, var5);
      DumperOptions.FlowStyle var6 = DumperOptions.FlowStyle.FLOW;

      for (Entry var8 : var2.entrySet()) {
         Node var9 = this.representData(var8.getKey());
         Node var10 = this.representData(var8.getValue());
         if (!(var9 instanceof ScalarNode) || !((ScalarNode)var9).isPlain()) {
            var6 = DumperOptions.FlowStyle.BLOCK;
         }

         if (!(var10 instanceof ScalarNode) || !((ScalarNode)var10).isPlain()) {
            var6 = DumperOptions.FlowStyle.BLOCK;
         }

         var4.add(new NodeTuple(var9, var10));
      }

      if (var3 == DumperOptions.FlowStyle.AUTO) {
         if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
            var5.setFlowStyle(this.defaultFlowStyle);
         } else {
            var5.setFlowStyle(var6);
         }
      }

      return var5;
   }

   public void setDefaultScalarStyle(DumperOptions.ScalarStyle var1) {
      this.defaultScalarStyle = var1;
   }

   public DumperOptions.ScalarStyle getDefaultScalarStyle() {
      return this.defaultScalarStyle == null ? DumperOptions.ScalarStyle.PLAIN : this.defaultScalarStyle;
   }

   public void setDefaultFlowStyle(DumperOptions.FlowStyle var1) {
      this.defaultFlowStyle = var1;
   }

   public DumperOptions.FlowStyle getDefaultFlowStyle() {
      return this.defaultFlowStyle;
   }

   public void setPropertyUtils(PropertyUtils var1) {
      this.propertyUtils = var1;
      this.explicitPropertyUtils = true;
   }

   public final PropertyUtils getPropertyUtils() {
      if (this.propertyUtils == null) {
         this.propertyUtils = new PropertyUtils();
      }

      return this.propertyUtils;
   }

   public final boolean isExplicitPropertyUtils() {
      return this.explicitPropertyUtils;
   }
}
