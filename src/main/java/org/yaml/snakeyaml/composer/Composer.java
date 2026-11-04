package org.yaml.snakeyaml.composer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.comments.CommentEventsCollector;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.resolver.Resolver;

public class Composer {
   protected final Parser parser;
   private final Resolver resolver;
   private final Map<String, Node> anchors;
   private final Set<Node> recursiveNodes;
   private int nonScalarAliasesCount = 0;
   private final LoaderOptions loadingConfig;
   private final CommentEventsCollector blockCommentsCollector;
   private final CommentEventsCollector inlineCommentsCollector;

   public Composer(Parser var1, Resolver var2) {
      this(var1, var2, new LoaderOptions());
   }

   public Composer(Parser var1, Resolver var2, LoaderOptions var3) {
      this.parser = var1;
      this.resolver = var2;
      this.anchors = new HashMap<>();
      this.recursiveNodes = new HashSet<>();
      this.loadingConfig = var3;
      this.blockCommentsCollector = new CommentEventsCollector(var1, CommentType.BLANK_LINE, CommentType.BLOCK);
      this.inlineCommentsCollector = new CommentEventsCollector(var1, CommentType.IN_LINE);
   }

   public boolean checkNode() {
      if (this.parser.checkEvent(Event.ID.StreamStart)) {
         this.parser.getEvent();
      }

      return !this.parser.checkEvent(Event.ID.StreamEnd);
   }

   public Node getNode() {
      this.blockCommentsCollector.collectEvents();
      if (this.parser.checkEvent(Event.ID.StreamEnd)) {
         List var5 = this.blockCommentsCollector.consume();
         Mark var2 = ((CommentLine)var5.get(0)).getStartMark();
         List var3 = Collections.emptyList();
         MappingNode var4 = new MappingNode(Tag.COMMENT, false, var3, var2, null, DumperOptions.FlowStyle.BLOCK);
         var4.setBlockComments(var5);
         return var4;
      } else {
         this.parser.getEvent();
         Node var1 = this.composeNode(null);
         this.blockCommentsCollector.collectEvents();
         if (!this.blockCommentsCollector.isEmpty()) {
            var1.setEndComments(this.blockCommentsCollector.consume());
         }

         this.parser.getEvent();
         this.anchors.clear();
         this.recursiveNodes.clear();
         return var1;
      }
   }

   public Node getSingleNode() {
      this.parser.getEvent();
      Node var1 = null;
      if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
         var1 = this.getNode();
      }

      if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
         Event var2 = this.parser.getEvent();
         Mark var3 = var1 != null ? var1.getStartMark() : null;
         throw new ComposerException("expected a single document in the stream", var3, "but found another document", var2.getStartMark());
      } else {
         this.parser.getEvent();
         return var1;
      }
   }

   private Node composeNode(Node var1) {
      this.blockCommentsCollector.collectEvents();
      if (var1 != null) {
         this.recursiveNodes.add(var1);
      }

      Node var2;
      if (this.parser.checkEvent(Event.ID.Alias)) {
         AliasEvent var3 = (AliasEvent)this.parser.getEvent();
         String var4 = var3.getAnchor();
         if (!this.anchors.containsKey(var4)) {
            throw new ComposerException(null, null, "found undefined alias " + var4, var3.getStartMark());
         }

         var2 = this.anchors.get(var4);
         if (!(var2 instanceof ScalarNode)) {
            this.nonScalarAliasesCount++;
            if (this.nonScalarAliasesCount > this.loadingConfig.getMaxAliasesForCollections()) {
               throw new YAMLException("Number of aliases for non-scalar nodes exceeds the specified max=" + this.loadingConfig.getMaxAliasesForCollections());
            }
         }

         if (this.recursiveNodes.remove(var2)) {
            var2.setTwoStepsConstruction(true);
         }

         var2.setBlockComments(this.blockCommentsCollector.consume());
      } else {
         NodeEvent var5 = (NodeEvent)this.parser.peekEvent();
         String var6 = var5.getAnchor();
         if (this.parser.checkEvent(Event.ID.Scalar)) {
            var2 = this.composeScalarNode(var6, this.blockCommentsCollector.consume());
         } else if (this.parser.checkEvent(Event.ID.SequenceStart)) {
            var2 = this.composeSequenceNode(var6);
         } else {
            var2 = this.composeMappingNode(var6);
         }
      }

      this.recursiveNodes.remove(var1);
      return var2;
   }

   protected Node composeScalarNode(String var1, List<CommentLine> var2) {
      ScalarEvent var3 = (ScalarEvent)this.parser.getEvent();
      String var4 = var3.getTag();
      boolean var5 = false;
      Tag var6;
      if (var4 != null && !var4.equals("!")) {
         var6 = new Tag(var4);
      } else {
         var6 = this.resolver.resolve(NodeId.scalar, var3.getValue(), var3.getImplicit().canOmitTagInPlainScalar());
         var5 = true;
      }

      ScalarNode var7 = new ScalarNode(var6, var5, var3.getValue(), var3.getStartMark(), var3.getEndMark(), var3.getScalarStyle());
      if (var1 != null) {
         var7.setAnchor(var1);
         this.anchors.put(var1, var7);
      }

      var7.setBlockComments(var2);
      var7.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
      return var7;
   }

   protected Node composeSequenceNode(String var1) {
      SequenceStartEvent var2 = (SequenceStartEvent)this.parser.getEvent();
      String var3 = var2.getTag();
      boolean var5 = false;
      Tag var4;
      if (var3 != null && !var3.equals("!")) {
         var4 = new Tag(var3);
      } else {
         var4 = this.resolver.resolve(NodeId.sequence, null, var2.getImplicit());
         var5 = true;
      }

      ArrayList var6 = new ArrayList();
      SequenceNode var7 = new SequenceNode(var4, var5, var6, var2.getStartMark(), null, var2.getFlowStyle());
      if (var2.isFlow()) {
         var7.setBlockComments(this.blockCommentsCollector.consume());
      }

      if (var1 != null) {
         var7.setAnchor(var1);
         this.anchors.put(var1, var7);
      }

      while (!this.parser.checkEvent(Event.ID.SequenceEnd)) {
         this.blockCommentsCollector.collectEvents();
         if (this.parser.checkEvent(Event.ID.SequenceEnd)) {
            break;
         }

         var6.add(this.composeNode(var7));
      }

      if (var2.isFlow()) {
         var7.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
      }

      Event var8 = this.parser.getEvent();
      var7.setEndMark(var8.getEndMark());
      this.inlineCommentsCollector.collectEvents();
      if (!this.inlineCommentsCollector.isEmpty()) {
         var7.setInLineComments(this.inlineCommentsCollector.consume());
      }

      return var7;
   }

   protected Node composeMappingNode(String var1) {
      MappingStartEvent var2 = (MappingStartEvent)this.parser.getEvent();
      String var3 = var2.getTag();
      boolean var5 = false;
      Tag var4;
      if (var3 != null && !var3.equals("!")) {
         var4 = new Tag(var3);
      } else {
         var4 = this.resolver.resolve(NodeId.mapping, null, var2.getImplicit());
         var5 = true;
      }

      ArrayList var6 = new ArrayList();
      MappingNode var7 = new MappingNode(var4, var5, var6, var2.getStartMark(), null, var2.getFlowStyle());
      if (var2.isFlow()) {
         var7.setBlockComments(this.blockCommentsCollector.consume());
      }

      if (var1 != null) {
         var7.setAnchor(var1);
         this.anchors.put(var1, var7);
      }

      while (!this.parser.checkEvent(Event.ID.MappingEnd)) {
         this.blockCommentsCollector.collectEvents();
         if (this.parser.checkEvent(Event.ID.MappingEnd)) {
            break;
         }

         this.composeMappingChildren(var6, var7);
      }

      if (var2.isFlow()) {
         var7.setInLineComments(this.inlineCommentsCollector.collectEvents().consume());
      }

      Event var8 = this.parser.getEvent();
      var7.setEndMark(var8.getEndMark());
      this.inlineCommentsCollector.collectEvents();
      if (!this.inlineCommentsCollector.isEmpty()) {
         var7.setInLineComments(this.inlineCommentsCollector.consume());
      }

      return var7;
   }

   protected void composeMappingChildren(List<NodeTuple> var1, MappingNode var2) {
      Node var3 = this.composeKeyNode(var2);
      if (var3.getTag().equals(Tag.MERGE)) {
         var2.setMerged(true);
      }

      Node var4 = this.composeValueNode(var2);
      var1.add(new NodeTuple(var3, var4));
   }

   protected Node composeKeyNode(MappingNode var1) {
      return this.composeNode(var1);
   }

   protected Node composeValueNode(MappingNode var1) {
      return this.composeNode(var1);
   }
}
