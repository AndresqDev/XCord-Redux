package org.yaml.snakeyaml.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.emitter.Emitable;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.CommentEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.resolver.Resolver;

public final class Serializer {
   private final Emitable emitter;
   private final Resolver resolver;
   private boolean explicitStart;
   private boolean explicitEnd;
   private DumperOptions.Version useVersion;
   private Map<String, String> useTags;
   private Set<Node> serializedNodes;
   private Map<Node, String> anchors;
   private AnchorGenerator anchorGenerator;
   private Boolean closed;
   private Tag explicitRoot;

   public Serializer(Emitable var1, Resolver var2, DumperOptions var3, Tag var4) {
      this.emitter = var1;
      this.resolver = var2;
      this.explicitStart = var3.isExplicitStart();
      this.explicitEnd = var3.isExplicitEnd();
      if (var3.getVersion() != null) {
         this.useVersion = var3.getVersion();
      }

      this.useTags = var3.getTags();
      this.serializedNodes = new HashSet<>();
      this.anchors = new HashMap<>();
      this.anchorGenerator = var3.getAnchorGenerator();
      this.closed = null;
      this.explicitRoot = var4;
   }

   public void open() throws IOException {
      if (this.closed == null) {
         this.emitter.emit(new StreamStartEvent(null, null));
         this.closed = Boolean.FALSE;
      } else if (Boolean.TRUE.equals(this.closed)) {
         throw new SerializerException("serializer is closed");
      } else {
         throw new SerializerException("serializer is already opened");
      }
   }

   public void close() throws IOException {
      if (this.closed == null) {
         throw new SerializerException("serializer is not opened");
      } else {
         if (!Boolean.TRUE.equals(this.closed)) {
            this.emitter.emit(new StreamEndEvent(null, null));
            this.closed = Boolean.TRUE;
            this.serializedNodes.clear();
            this.anchors.clear();
         }
      }
   }

   public void serialize(Node var1) throws IOException {
      if (this.closed == null) {
         throw new SerializerException("serializer is not opened");
      } else if (this.closed) {
         throw new SerializerException("serializer is closed");
      } else {
         this.emitter.emit(new DocumentStartEvent(null, null, this.explicitStart, this.useVersion, this.useTags));
         this.anchorNode(var1);
         if (this.explicitRoot != null) {
            var1.setTag(this.explicitRoot);
         }

         this.serializeNode(var1, null);
         this.emitter.emit(new DocumentEndEvent(null, null, this.explicitEnd));
         this.serializedNodes.clear();
         this.anchors.clear();
      }
   }

   private void anchorNode(Node var1) {
      if (var1.getNodeId() == NodeId.anchor) {
         var1 = ((AnchorNode)var1).getRealNode();
      }

      if (this.anchors.containsKey(var1)) {
         String var2 = this.anchors.get(var1);
         if (null == var2) {
            var2 = this.anchorGenerator.nextAnchor(var1);
            this.anchors.put(var1, var2);
         }
      } else {
         this.anchors.put(var1, var1.getAnchor() != null ? this.anchorGenerator.nextAnchor(var1) : null);
         switch (var1.getNodeId()) {
            case sequence:
               SequenceNode var11 = (SequenceNode)var1;

               for (Node var13 : var11.getValue()) {
                  this.anchorNode(var13);
               }
               break;
            case mapping:
               MappingNode var4 = (MappingNode)var1;

               for (NodeTuple var7 : var4.getValue()) {
                  Node var8 = var7.getKeyNode();
                  Node var9 = var7.getValueNode();
                  this.anchorNode(var8);
                  this.anchorNode(var9);
               }
         }
      }
   }

   private void serializeNode(Node var1, Node var2) throws IOException {
      if (var1.getNodeId() == NodeId.anchor) {
         var1 = ((AnchorNode)var1).getRealNode();
      }

      String var3 = this.anchors.get(var1);
      if (this.serializedNodes.contains(var1)) {
         this.emitter.emit(new AliasEvent(var3, null, null));
      } else {
         this.serializedNodes.add(var1);
         switch (var1.getNodeId()) {
            case sequence:
               SequenceNode var9 = (SequenceNode)var1;
               this.serializeComments(var1.getBlockComments());
               boolean var10 = var1.getTag().equals(this.resolver.resolve(NodeId.sequence, null, true));
               this.emitter.emit(new SequenceStartEvent(var3, var1.getTag().getValue(), var10, null, null, var9.getFlowStyle()));

               for (Node var21 : var9.getValue()) {
                  this.serializeNode(var21, var1);
               }

               this.emitter.emit(new SequenceEndEvent(null, null));
               this.serializeComments(var1.getInLineComments());
               this.serializeComments(var1.getEndComments());
               break;
            case scalar:
               ScalarNode var4 = (ScalarNode)var1;
               this.serializeComments(var1.getBlockComments());
               Tag var5 = this.resolver.resolve(NodeId.scalar, var4.getValue(), true);
               Tag var6 = this.resolver.resolve(NodeId.scalar, var4.getValue(), false);
               ImplicitTuple var7 = new ImplicitTuple(var1.getTag().equals(var5), var1.getTag().equals(var6));
               ScalarEvent var8 = new ScalarEvent(var3, var1.getTag().getValue(), var7, var4.getValue(), null, null, var4.getScalarStyle());
               this.emitter.emit(var8);
               this.serializeComments(var1.getInLineComments());
               this.serializeComments(var1.getEndComments());
               break;
            default:
               this.serializeComments(var1.getBlockComments());
               Tag var12 = this.resolver.resolve(NodeId.mapping, null, true);
               boolean var13 = var1.getTag().equals(var12);
               MappingNode var14 = (MappingNode)var1;
               List var15 = var14.getValue();
               if (var14.getTag() != Tag.COMMENT) {
                  this.emitter.emit(new MappingStartEvent(var3, var14.getTag().getValue(), var13, null, null, var14.getFlowStyle()));

                  for (NodeTuple var17 : var15) {
                     Node var18 = var17.getKeyNode();
                     Node var19 = var17.getValueNode();
                     this.serializeNode(var18, var14);
                     this.serializeNode(var19, var14);
                  }

                  this.emitter.emit(new MappingEndEvent(null, null));
                  this.serializeComments(var1.getInLineComments());
                  this.serializeComments(var1.getEndComments());
               }
         }
      }
   }

   private void serializeComments(List<CommentLine> var1) throws IOException {
      if (var1 != null) {
         for (CommentLine var3 : var1) {
            CommentEvent var4 = new CommentEvent(var3.getCommentType(), var3.getValue(), var3.getStartMark(), var3.getEndMark());
            this.emitter.emit(var4);
         }
      }
   }
}
