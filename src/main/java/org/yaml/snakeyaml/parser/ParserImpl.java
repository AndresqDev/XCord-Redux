package org.yaml.snakeyaml.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.CommentEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.tokens.CommentToken;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.tokens.StreamStartToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.util.ArrayStack;

public class ParserImpl implements Parser {
   private static final Map<String, String> DEFAULT_TAGS = new HashMap<>();
   protected final Scanner scanner;
   private Event currentEvent;
   private final ArrayStack<Production> states;
   private final ArrayStack<Mark> marks;
   private Production state;
   private VersionTagsTuple directives;

   public ParserImpl(StreamReader var1) {
      this(new ScannerImpl(var1));
   }

   public ParserImpl(StreamReader var1, boolean var2) {
      this(new ScannerImpl(var1).setParseComments(var2));
   }

   public ParserImpl(Scanner var1) {
      this.scanner = var1;
      this.currentEvent = null;
      this.directives = new VersionTagsTuple(null, new HashMap<>(DEFAULT_TAGS));
      this.states = new ArrayStack<>(100);
      this.marks = new ArrayStack<>(10);
      this.state = new ParserImpl.ParseStreamStart();
   }

   @Override
   public boolean checkEvent(Event.ID var1) {
      this.peekEvent();
      return this.currentEvent != null && this.currentEvent.is(var1);
   }

   @Override
   public Event peekEvent() {
      if (this.currentEvent == null && this.state != null) {
         this.currentEvent = this.state.produce();
      }

      return this.currentEvent;
   }

   @Override
   public Event getEvent() {
      this.peekEvent();
      Event var1 = this.currentEvent;
      this.currentEvent = null;
      return var1;
   }

   private CommentEvent produceCommentEvent(CommentToken var1) {
      Mark var2 = var1.getStartMark();
      Mark var3 = var1.getEndMark();
      String var4 = var1.getValue();
      CommentType var5 = var1.getCommentType();
      return new CommentEvent(var5, var4, var2, var3);
   }

   private VersionTagsTuple processDirectives() {
      DumperOptions.Version var1 = null;
      HashMap var2 = new HashMap();

      while (this.scanner.checkToken(Token.ID.Directive)) {
         DirectiveToken var3 = (DirectiveToken)this.scanner.getToken();
         if (var3.getName().equals("YAML")) {
            if (var1 != null) {
               throw new ParserException(null, null, "found duplicate YAML directive", var3.getStartMark());
            }

            List var4 = var3.getValue();
            Integer var5 = (Integer)var4.get(0);
            if (var5 != 1) {
               throw new ParserException(null, null, "found incompatible YAML document (version 1.* is required)", var3.getStartMark());
            }

            Integer var6 = (Integer)var4.get(1);
            switch (var6) {
               case 0:
                  var1 = DumperOptions.Version.V1_0;
                  break;
               default:
                  var1 = DumperOptions.Version.V1_1;
            }
         } else if (var3.getName().equals("TAG")) {
            List var8 = var3.getValue();
            String var10 = (String)var8.get(0);
            String var11 = (String)var8.get(1);
            if (var2.containsKey(var10)) {
               throw new ParserException(null, null, "duplicate tag handle " + var10, var3.getStartMark());
            }

            var2.put(var10, var11);
         }
      }

      if (var1 != null || !var2.isEmpty()) {
         for (String var9 : DEFAULT_TAGS.keySet()) {
            if (!var2.containsKey(var9)) {
               var2.put(var9, DEFAULT_TAGS.get(var9));
            }
         }

         this.directives = new VersionTagsTuple(var1, var2);
      }

      return this.directives;
   }

   private Event parseFlowNode() {
      return this.parseNode(false, false);
   }

   private Event parseBlockNodeOrIndentlessSequence() {
      return this.parseNode(true, true);
   }

   private Event parseNode(boolean var1, boolean var2) {
      Mark var4 = null;
      Mark var5 = null;
      Mark var6 = null;
      Object var3;
      if (this.scanner.checkToken(Token.ID.Alias)) {
         AliasToken var7 = (AliasToken)this.scanner.getToken();
         var3 = new AliasEvent(var7.getValue(), var7.getStartMark(), var7.getEndMark());
         this.state = this.states.pop();
      } else {
         String var20 = null;
         TagTuple var8 = null;
         if (this.scanner.checkToken(Token.ID.Anchor)) {
            AnchorToken var9 = (AnchorToken)this.scanner.getToken();
            var4 = var9.getStartMark();
            var5 = var9.getEndMark();
            var20 = var9.getValue();
            if (this.scanner.checkToken(Token.ID.Tag)) {
               TagToken var10 = (TagToken)this.scanner.getToken();
               var6 = var10.getStartMark();
               var5 = var10.getEndMark();
               var8 = var10.getValue();
            }
         } else if (this.scanner.checkToken(Token.ID.Tag)) {
            TagToken var21 = (TagToken)this.scanner.getToken();
            var4 = var21.getStartMark();
            var6 = var4;
            var5 = var21.getEndMark();
            var8 = var21.getValue();
            if (this.scanner.checkToken(Token.ID.Anchor)) {
               AnchorToken var23 = (AnchorToken)this.scanner.getToken();
               var5 = var23.getEndMark();
               var20 = var23.getValue();
            }
         }

         String var22 = null;
         if (var8 != null) {
            String var24 = var8.getHandle();
            String var11 = var8.getSuffix();
            if (var24 != null) {
               if (!this.directives.getTags().containsKey(var24)) {
                  throw new ParserException("while parsing a node", var4, "found undefined tag handle " + var24, var6);
               }

               var22 = this.directives.getTags().get(var24) + var11;
            } else {
               var22 = var11;
            }
         }

         if (var4 == null) {
            var4 = this.scanner.peekToken().getStartMark();
            var5 = var4;
         }

         var3 = null;
         boolean var25 = var22 == null || var22.equals("!");
         if (var2 && this.scanner.checkToken(Token.ID.BlockEntry)) {
            var5 = this.scanner.peekToken().getEndMark();
            var3 = new SequenceStartEvent(var20, var22, var25, var4, var5, DumperOptions.FlowStyle.BLOCK);
            this.state = new ParserImpl.ParseIndentlessSequenceEntryKey();
         } else if (this.scanner.checkToken(Token.ID.Scalar)) {
            ScalarToken var26 = (ScalarToken)this.scanner.getToken();
            var5 = var26.getEndMark();
            ImplicitTuple var12;
            if ((!var26.getPlain() || var22 != null) && !"!".equals(var22)) {
               if (var22 == null) {
                  var12 = new ImplicitTuple(false, true);
               } else {
                  var12 = new ImplicitTuple(false, false);
               }
            } else {
               var12 = new ImplicitTuple(true, false);
            }

            var3 = new ScalarEvent(var20, var22, var12, var26.getValue(), var4, var5, var26.getStyle());
            this.state = this.states.pop();
         } else if (this.scanner.checkToken(Token.ID.FlowSequenceStart)) {
            var5 = this.scanner.peekToken().getEndMark();
            var3 = new SequenceStartEvent(var20, var22, var25, var4, var5, DumperOptions.FlowStyle.FLOW);
            this.state = new ParserImpl.ParseFlowSequenceFirstEntry();
         } else if (this.scanner.checkToken(Token.ID.FlowMappingStart)) {
            var5 = this.scanner.peekToken().getEndMark();
            var3 = new MappingStartEvent(var20, var22, var25, var4, var5, DumperOptions.FlowStyle.FLOW);
            this.state = new ParserImpl.ParseFlowMappingFirstKey();
         } else if (var1 && this.scanner.checkToken(Token.ID.BlockSequenceStart)) {
            var5 = this.scanner.peekToken().getStartMark();
            var3 = new SequenceStartEvent(var20, var22, var25, var4, var5, DumperOptions.FlowStyle.BLOCK);
            this.state = new ParserImpl.ParseBlockSequenceFirstEntry();
         } else if (var1 && this.scanner.checkToken(Token.ID.BlockMappingStart)) {
            var5 = this.scanner.peekToken().getStartMark();
            var3 = new MappingStartEvent(var20, var22, var25, var4, var5, DumperOptions.FlowStyle.BLOCK);
            this.state = new ParserImpl.ParseBlockMappingFirstKey();
         } else {
            if (var20 == null && var22 == null) {
               Token var27 = this.scanner.peekToken();
               throw new ParserException(
                  "while parsing a " + (var1 ? "block" : "flow") + " node",
                  var4,
                  "expected the node content, but found '" + var27.getTokenId() + "'",
                  var27.getStartMark()
               );
            }

            var3 = new ScalarEvent(var20, var22, new ImplicitTuple(var25, false), "", var4, var5, DumperOptions.ScalarStyle.PLAIN);
            this.state = this.states.pop();
         }
      }

      return (Event)var3;
   }

   private Event processEmptyScalar(Mark var1) {
      return new ScalarEvent(null, null, new ImplicitTuple(true, false), "", var1, var1, DumperOptions.ScalarStyle.PLAIN);
   }

   static {
      DEFAULT_TAGS.put("!", "!");
      DEFAULT_TAGS.put("!!", "tag:yaml.org,2002:");
   }

   private class ParseBlockMappingFirstKey implements Production {
      private ParseBlockMappingFirstKey() {
      }

      @Override
      public Event produce() {
         Token var1 = ParserImpl.this.scanner.getToken();
         ParserImpl.this.marks.push(var1.getStartMark());
         return ParserImpl.this.new ParseBlockMappingKey().produce();
      }
   }

   private class ParseBlockMappingKey implements Production {
      private ParseBlockMappingKey() {
      }

      @Override
      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingKey();
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
            Token var4 = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingValue());
               return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingValue();
               return ParserImpl.this.processEmptyScalar(var4.getEndMark());
            }
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
            Token var3 = ParserImpl.this.scanner.peekToken();
            throw new ParserException(
               "while parsing a block mapping", ParserImpl.this.marks.pop(), "expected <block end>, but found '" + var3.getTokenId() + "'", var3.getStartMark()
            );
         } else {
            Token var1 = ParserImpl.this.scanner.getToken();
            MappingEndEvent var2 = new MappingEndEvent(var1.getStartMark(), var1.getEndMark());
            ParserImpl.this.state = ParserImpl.this.states.pop();
            ParserImpl.this.marks.pop();
            return var2;
         }
      }
   }

   private class ParseBlockMappingValue implements Production {
      private ParseBlockMappingValue() {
      }

      @Override
      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
            Token var2 = ParserImpl.this.scanner.getToken();
            if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
               ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingValueComment();
               return ParserImpl.this.state.produce();
            } else if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingKey());
               return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingKey();
               return ParserImpl.this.processEmptyScalar(var2.getEndMark());
            }
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.Scalar)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingKey());
            return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingKey();
            Token var1 = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(var1.getStartMark());
         }
      }
   }

   private class ParseBlockMappingValueComment implements Production {
      List<CommentToken> tokens = new LinkedList<>();

      private ParseBlockMappingValueComment() {
      }

      @Override
      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            this.tokens.add((CommentToken)ParserImpl.this.scanner.getToken());
            return this.produce();
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
            if (!this.tokens.isEmpty()) {
               return ParserImpl.this.produceCommentEvent(this.tokens.remove(0));
            } else {
               ParserImpl.this.states.push(ParserImpl.this.new ParseBlockMappingKey());
               return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
            }
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseBlockMappingValueCommentList(this.tokens);
            return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
         }
      }
   }

   private class ParseBlockMappingValueCommentList implements Production {
      List<CommentToken> tokens;

      public ParseBlockMappingValueCommentList(List<CommentToken> var2) {
         this.tokens = var2;
      }

      @Override
      public Event produce() {
         return (Event)(!this.tokens.isEmpty()
            ? ParserImpl.this.produceCommentEvent(this.tokens.remove(0))
            : ParserImpl.this.new ParseBlockMappingKey().produce());
      }
   }

   private class ParseBlockNode implements Production {
      private ParseBlockNode() {
      }

      @Override
      public Event produce() {
         return ParserImpl.this.parseNode(true, false);
      }
   }

   private class ParseBlockSequenceEntryKey implements Production {
      private ParseBlockSequenceEntryKey() {
      }

      @Override
      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = ParserImpl.this.new ParseBlockSequenceEntryKey();
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
            BlockEntryToken var4 = (BlockEntryToken)ParserImpl.this.scanner.getToken();
            return ParserImpl.this.new ParseBlockSequenceEntryValue(var4).produce();
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
            Token var3 = ParserImpl.this.scanner.peekToken();
            throw new ParserException(
               "while parsing a block collection",
               ParserImpl.this.marks.pop(),
               "expected <block end>, but found '" + var3.getTokenId() + "'",
               var3.getStartMark()
            );
         } else {
            Token var1 = ParserImpl.this.scanner.getToken();
            SequenceEndEvent var2 = new SequenceEndEvent(var1.getStartMark(), var1.getEndMark());
            ParserImpl.this.state = ParserImpl.this.states.pop();
            ParserImpl.this.marks.pop();
            return var2;
         }
      }
   }

   private class ParseBlockSequenceEntryValue implements Production {
      BlockEntryToken token;

      public ParseBlockSequenceEntryValue(BlockEntryToken var2) {
         this.token = var2;
      }

      @Override
      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = ParserImpl.this.new ParseBlockSequenceEntryValue(this.token);
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.BlockEnd)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseBlockSequenceEntryKey());
            return ParserImpl.this.new ParseBlockNode().produce();
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseBlockSequenceEntryKey();
            return ParserImpl.this.processEmptyScalar(this.token.getEndMark());
         }
      }
   }

   private class ParseBlockSequenceFirstEntry implements Production {
      private ParseBlockSequenceFirstEntry() {
      }

      @Override
      public Event produce() {
         Token var1 = ParserImpl.this.scanner.getToken();
         ParserImpl.this.marks.push(var1.getStartMark());
         return ParserImpl.this.new ParseBlockSequenceEntryKey().produce();
      }
   }

   private class ParseDocumentContent implements Production {
      private ParseDocumentContent() {
      }

      @Override
      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = ParserImpl.this.new ParseDocumentContent();
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.DocumentEnd, Token.ID.StreamEnd)) {
            Event var1 = ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
            ParserImpl.this.state = ParserImpl.this.states.pop();
            return var1;
         } else {
            return ParserImpl.this.new ParseBlockNode().produce();
         }
      }
   }

   private class ParseDocumentEnd implements Production {
      private ParseDocumentEnd() {
      }

      @Override
      public Event produce() {
         Token var1 = ParserImpl.this.scanner.peekToken();
         Mark var2 = var1.getStartMark();
         Mark var3 = var2;
         boolean var4 = false;
         if (ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
            var1 = ParserImpl.this.scanner.getToken();
            var3 = var1.getEndMark();
            var4 = true;
         }

         DocumentEndEvent var5 = new DocumentEndEvent(var2, var3, var4);
         ParserImpl.this.state = ParserImpl.this.new ParseDocumentStart();
         return var5;
      }
   }

   private class ParseDocumentStart implements Production {
      private ParseDocumentStart() {
      }

      @Override
      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = ParserImpl.this.new ParseDocumentStart();
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else {
            while (ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
               ParserImpl.this.scanner.getToken();
            }

            if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
               ParserImpl.this.state = ParserImpl.this.new ParseDocumentStart();
               return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
            } else {
               if (!ParserImpl.this.scanner.checkToken(Token.ID.StreamEnd)) {
                  Token var2 = ParserImpl.this.scanner.peekToken();
                  Mark var3 = var2.getStartMark();
                  VersionTagsTuple var4 = ParserImpl.this.processDirectives();

                  while (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
                     ParserImpl.this.scanner.getToken();
                  }

                  if (!ParserImpl.this.scanner.checkToken(Token.ID.StreamEnd)) {
                     if (!ParserImpl.this.scanner.checkToken(Token.ID.DocumentStart)) {
                        throw new ParserException(
                           null,
                           null,
                           "expected '<document start>', but found '" + ParserImpl.this.scanner.peekToken().getTokenId() + "'",
                           ParserImpl.this.scanner.peekToken().getStartMark()
                        );
                     }

                     var2 = ParserImpl.this.scanner.getToken();
                     Mark var5 = var2.getEndMark();
                     DocumentStartEvent var6 = new DocumentStartEvent(var3, var5, true, var4.getVersion(), var4.getTags());
                     ParserImpl.this.states.push(ParserImpl.this.new ParseDocumentEnd());
                     ParserImpl.this.state = ParserImpl.this.new ParseDocumentContent();
                     return var6;
                  }
               }

               StreamEndToken var7 = (StreamEndToken)ParserImpl.this.scanner.getToken();
               StreamEndEvent var1 = new StreamEndEvent(var7.getStartMark(), var7.getEndMark());
               if (!ParserImpl.this.states.isEmpty()) {
                  throw new YAMLException("Unexpected end of stream. States left: " + ParserImpl.this.states);
               } else if (!ParserImpl.this.marks.isEmpty()) {
                  throw new YAMLException("Unexpected end of stream. Marks left: " + ParserImpl.this.marks);
               } else {
                  ParserImpl.this.state = null;
                  return var1;
               }
            }
         }
      }
   }

   private class ParseFlowEndComment implements Production {
      private ParseFlowEndComment() {
      }

      @Override
      public Event produce() {
         CommentEvent var1 = ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         if (!ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = ParserImpl.this.states.pop();
         }

         return var1;
      }
   }

   private class ParseFlowMappingEmptyValue implements Production {
      private ParseFlowMappingEmptyValue() {
      }

      @Override
      public Event produce() {
         ParserImpl.this.state = ParserImpl.this.new ParseFlowMappingKey(false);
         return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
      }
   }

   private class ParseFlowMappingFirstKey implements Production {
      private ParseFlowMappingFirstKey() {
      }

      @Override
      public Event produce() {
         Token var1 = ParserImpl.this.scanner.getToken();
         ParserImpl.this.marks.push(var1.getStartMark());
         return ParserImpl.this.new ParseFlowMappingKey(true).produce();
      }
   }

   private class ParseFlowMappingKey implements Production {
      private boolean first = false;

      public ParseFlowMappingKey(boolean var2) {
         this.first = var2;
      }

      @Override
      public Event produce() {
         if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
            if (!this.first) {
               if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                  Token var4 = ParserImpl.this.scanner.peekToken();
                  throw new ParserException(
                     "while parsing a flow mapping", ParserImpl.this.marks.pop(), "expected ',' or '}', but got " + var4.getTokenId(), var4.getStartMark()
                  );
               }

               ParserImpl.this.scanner.getToken();
            }

            if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
               Token var3 = ParserImpl.this.scanner.getToken();
               if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
                  ParserImpl.this.states.push(ParserImpl.this.new ParseFlowMappingValue());
                  return ParserImpl.this.parseFlowNode();
               }

               ParserImpl.this.state = ParserImpl.this.new ParseFlowMappingValue();
               return ParserImpl.this.processEmptyScalar(var3.getEndMark());
            }

            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowMappingEmptyValue());
               return ParserImpl.this.parseFlowNode();
            }
         }

         Token var1 = ParserImpl.this.scanner.getToken();
         MappingEndEvent var2 = new MappingEndEvent(var1.getStartMark(), var1.getEndMark());
         ParserImpl.this.marks.pop();
         if (!ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = ParserImpl.this.states.pop();
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseFlowEndComment();
         }

         return var2;
      }
   }

   private class ParseFlowMappingValue implements Production {
      private ParseFlowMappingValue() {
      }

      @Override
      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
            Token var2 = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowMappingKey(false));
               return ParserImpl.this.parseFlowNode();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseFlowMappingKey(false);
               return ParserImpl.this.processEmptyScalar(var2.getEndMark());
            }
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseFlowMappingKey(false);
            Token var1 = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(var1.getStartMark());
         }
      }
   }

   private class ParseFlowSequenceEntry implements Production {
      private final boolean first;

      public ParseFlowSequenceEntry(boolean var2) {
         this.first = var2;
      }

      @Override
      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntry(this.first);
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
               if (!this.first) {
                  if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                     Token var4 = ParserImpl.this.scanner.peekToken();
                     throw new ParserException(
                        "while parsing a flow sequence", ParserImpl.this.marks.pop(), "expected ',' or ']', but got " + var4.getTokenId(), var4.getStartMark()
                     );
                  }

                  ParserImpl.this.scanner.getToken();
                  if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
                     ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntry(true);
                     return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
                  }
               }

               if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
                  Token var3 = ParserImpl.this.scanner.peekToken();
                  MappingStartEvent var5 = new MappingStartEvent(null, null, true, var3.getStartMark(), var3.getEndMark(), DumperOptions.FlowStyle.FLOW);
                  ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntryMappingKey();
                  return var5;
               }

               if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
                  ParserImpl.this.states.push(ParserImpl.this.new ParseFlowSequenceEntry(false));
                  return ParserImpl.this.parseFlowNode();
               }
            }

            Token var1 = ParserImpl.this.scanner.getToken();
            SequenceEndEvent var2 = new SequenceEndEvent(var1.getStartMark(), var1.getEndMark());
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
               ParserImpl.this.state = ParserImpl.this.states.pop();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseFlowEndComment();
            }

            ParserImpl.this.marks.pop();
            return var2;
         }
      }
   }

   private class ParseFlowSequenceEntryMappingEnd implements Production {
      private ParseFlowSequenceEntryMappingEnd() {
      }

      @Override
      public Event produce() {
         ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntry(false);
         Token var1 = ParserImpl.this.scanner.peekToken();
         return new MappingEndEvent(var1.getStartMark(), var1.getEndMark());
      }
   }

   private class ParseFlowSequenceEntryMappingKey implements Production {
      private ParseFlowSequenceEntryMappingKey() {
      }

      @Override
      public Event produce() {
         Token var1 = ParserImpl.this.scanner.getToken();
         if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseFlowSequenceEntryMappingValue());
            return ParserImpl.this.parseFlowNode();
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntryMappingValue();
            return ParserImpl.this.processEmptyScalar(var1.getEndMark());
         }
      }
   }

   private class ParseFlowSequenceEntryMappingValue implements Production {
      private ParseFlowSequenceEntryMappingValue() {
      }

      @Override
      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
            Token var2 = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
               ParserImpl.this.states.push(ParserImpl.this.new ParseFlowSequenceEntryMappingEnd());
               return ParserImpl.this.parseFlowNode();
            } else {
               ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntryMappingEnd();
               return ParserImpl.this.processEmptyScalar(var2.getEndMark());
            }
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseFlowSequenceEntryMappingEnd();
            Token var1 = ParserImpl.this.scanner.peekToken();
            return ParserImpl.this.processEmptyScalar(var1.getStartMark());
         }
      }
   }

   private class ParseFlowSequenceFirstEntry implements Production {
      private ParseFlowSequenceFirstEntry() {
      }

      @Override
      public Event produce() {
         Token var1 = ParserImpl.this.scanner.getToken();
         ParserImpl.this.marks.push(var1.getStartMark());
         return ParserImpl.this.new ParseFlowSequenceEntry(true).produce();
      }
   }

   private class ParseImplicitDocumentStart implements Production {
      private ParseImplicitDocumentStart() {
      }

      @Override
      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = ParserImpl.this.new ParseImplicitDocumentStart();
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.StreamEnd)) {
            ParserImpl.this.directives = new VersionTagsTuple(null, ParserImpl.DEFAULT_TAGS);
            Token var1 = ParserImpl.this.scanner.peekToken();
            Mark var2 = var1.getStartMark();
            DocumentStartEvent var4 = new DocumentStartEvent(var2, var2, false, null, null);
            ParserImpl.this.states.push(ParserImpl.this.new ParseDocumentEnd());
            ParserImpl.this.state = ParserImpl.this.new ParseBlockNode();
            return var4;
         } else {
            return ParserImpl.this.new ParseDocumentStart().produce();
         }
      }
   }

   private class ParseIndentlessSequenceEntryKey implements Production {
      private ParseIndentlessSequenceEntryKey() {
      }

      @Override
      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = ParserImpl.this.new ParseIndentlessSequenceEntryKey();
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else if (ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
            BlockEntryToken var3 = (BlockEntryToken)ParserImpl.this.scanner.getToken();
            return ParserImpl.this.new ParseIndentlessSequenceEntryValue(var3).produce();
         } else {
            Token var1 = ParserImpl.this.scanner.peekToken();
            SequenceEndEvent var2 = new SequenceEndEvent(var1.getStartMark(), var1.getEndMark());
            ParserImpl.this.state = ParserImpl.this.states.pop();
            return var2;
         }
      }
   }

   private class ParseIndentlessSequenceEntryValue implements Production {
      BlockEntryToken token;

      public ParseIndentlessSequenceEntryValue(BlockEntryToken var2) {
         this.token = var2;
      }

      @Override
      public Event produce() {
         if (ParserImpl.this.scanner.checkToken(Token.ID.Comment)) {
            ParserImpl.this.state = ParserImpl.this.new ParseIndentlessSequenceEntryValue(this.token);
            return ParserImpl.this.produceCommentEvent((CommentToken)ParserImpl.this.scanner.getToken());
         } else if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
            ParserImpl.this.states.push(ParserImpl.this.new ParseIndentlessSequenceEntryKey());
            return ParserImpl.this.new ParseBlockNode().produce();
         } else {
            ParserImpl.this.state = ParserImpl.this.new ParseIndentlessSequenceEntryKey();
            return ParserImpl.this.processEmptyScalar(this.token.getEndMark());
         }
      }
   }

   private class ParseStreamStart implements Production {
      private ParseStreamStart() {
      }

      @Override
      public Event produce() {
         StreamStartToken var1 = (StreamStartToken)ParserImpl.this.scanner.getToken();
         StreamStartEvent var2 = new StreamStartEvent(var1.getStartMark(), var1.getEndMark());
         ParserImpl.this.state = ParserImpl.this.new ParseImplicitDocumentStart();
         return var2;
      }
   }
}
