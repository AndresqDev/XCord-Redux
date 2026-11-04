package org.yaml.snakeyaml.emitter;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentEventsCollector;
import org.yaml.snakeyaml.comments.CommentLine;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.CollectionEndEvent;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.CommentEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.scanner.Constant;
import org.yaml.snakeyaml.util.ArrayStack;

public final class Emitter implements Emitable {
   public static final int MIN_INDENT = 1;
   public static final int MAX_INDENT = 10;
   private static final char[] SPACE = new char[]{' '};
   private static final Pattern SPACES_PATTERN = Pattern.compile("\\s");
   private static final Set<Character> INVALID_ANCHOR = new HashSet<>();
   private static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<>();
   private static final Map<String, String> DEFAULT_TAG_PREFIXES = new LinkedHashMap<>();
   private final Writer stream;
   private final ArrayStack<EmitterState> states;
   private EmitterState state;
   private final Queue<Event> events;
   private Event event;
   private final ArrayStack<Integer> indents;
   private Integer indent;
   private int flowLevel;
   private boolean rootContext;
   private boolean mappingContext;
   private boolean simpleKeyContext;
   private int column;
   private boolean whitespace;
   private boolean indention;
   private boolean openEnded;
   private final Boolean canonical;
   private final Boolean prettyFlow;
   private final boolean allowUnicode;
   private int bestIndent;
   private final int indicatorIndent;
   private final boolean indentWithIndicator;
   private int bestWidth;
   private final char[] bestLineBreak;
   private final boolean splitLines;
   private final int maxSimpleKeyLength;
   private final boolean emitComments;
   private Map<String, String> tagPrefixes;
   private String preparedAnchor;
   private String preparedTag;
   private ScalarAnalysis analysis;
   private DumperOptions.ScalarStyle style;
   private final CommentEventsCollector blockCommentsCollector;
   private final CommentEventsCollector inlineCommentsCollector;
   private static final Pattern HANDLE_FORMAT = Pattern.compile("^![-_\\w]*!$");

   public Emitter(Writer var1, DumperOptions var2) {
      this.stream = var1;
      this.states = new ArrayStack<>(100);
      this.state = new Emitter.ExpectStreamStart();
      this.events = new ArrayBlockingQueue<>(100);
      this.event = null;
      this.indents = new ArrayStack<>(10);
      this.indent = null;
      this.flowLevel = 0;
      this.mappingContext = false;
      this.simpleKeyContext = false;
      this.column = 0;
      this.whitespace = true;
      this.indention = true;
      this.openEnded = false;
      this.canonical = var2.isCanonical();
      this.prettyFlow = var2.isPrettyFlow();
      this.allowUnicode = var2.isAllowUnicode();
      this.bestIndent = 2;
      if (var2.getIndent() > 1 && var2.getIndent() < 10) {
         this.bestIndent = var2.getIndent();
      }

      this.indicatorIndent = var2.getIndicatorIndent();
      this.indentWithIndicator = var2.getIndentWithIndicator();
      this.bestWidth = 80;
      if (var2.getWidth() > this.bestIndent * 2) {
         this.bestWidth = var2.getWidth();
      }

      this.bestLineBreak = var2.getLineBreak().getString().toCharArray();
      this.splitLines = var2.getSplitLines();
      this.maxSimpleKeyLength = var2.getMaxSimpleKeyLength();
      this.emitComments = var2.isProcessComments();
      this.tagPrefixes = new LinkedHashMap<>();
      this.preparedAnchor = null;
      this.preparedTag = null;
      this.analysis = null;
      this.style = null;
      this.blockCommentsCollector = new CommentEventsCollector(this.events, CommentType.BLANK_LINE, CommentType.BLOCK);
      this.inlineCommentsCollector = new CommentEventsCollector(this.events, CommentType.IN_LINE);
   }

   @Override
   public void emit(Event var1) throws IOException {
      this.events.add(var1);

      while (!this.needMoreEvents()) {
         this.event = this.events.poll();
         this.state.expect();
         this.event = null;
      }
   }

   private boolean needMoreEvents() {
      if (this.events.isEmpty()) {
         return true;
      } else {
         Iterator var1 = this.events.iterator();

         Event var2;
         for (var2 = (Event)var1.next(); var2 instanceof CommentEvent; var2 = (Event)var1.next()) {
            if (!var1.hasNext()) {
               return true;
            }
         }

         if (var2 instanceof DocumentStartEvent) {
            return this.needEvents(var1, 1);
         } else if (var2 instanceof SequenceStartEvent) {
            return this.needEvents(var1, 2);
         } else if (var2 instanceof MappingStartEvent) {
            return this.needEvents(var1, 3);
         } else if (var2 instanceof StreamStartEvent) {
            return this.needEvents(var1, 2);
         } else if (var2 instanceof StreamEndEvent) {
            return false;
         } else {
            return this.emitComments ? this.needEvents(var1, 1) : false;
         }
      }
   }

   private boolean needEvents(Iterator<Event> var1, int var2) {
      int var3 = 0;
      int var4 = 0;

      while (var1.hasNext()) {
         Event var5 = (Event)var1.next();
         if (!(var5 instanceof CommentEvent)) {
            var4++;
            if (var5 instanceof DocumentStartEvent || var5 instanceof CollectionStartEvent) {
               var3++;
            } else if (var5 instanceof DocumentEndEvent || var5 instanceof CollectionEndEvent) {
               var3--;
            } else if (var5 instanceof StreamEndEvent) {
               var3 = -1;
            }

            if (var3 < 0) {
               return false;
            }
         }
      }

      return var4 < var2;
   }

   private void increaseIndent(boolean var1, boolean var2) {
      this.indents.push(this.indent);
      if (this.indent == null) {
         if (var1) {
            this.indent = this.bestIndent;
         } else {
            this.indent = 0;
         }
      } else if (!var2) {
         this.indent = this.indent + this.bestIndent;
      }
   }

   private void expectNode(boolean var1, boolean var2, boolean var3) throws IOException {
      this.rootContext = var1;
      this.mappingContext = var2;
      this.simpleKeyContext = var3;
      if (this.event instanceof AliasEvent) {
         this.expectAlias();
      } else {
         if (!(this.event instanceof ScalarEvent) && !(this.event instanceof CollectionStartEvent)) {
            throw new EmitterException("expected NodeEvent, but got " + this.event);
         }

         this.processAnchor("&");
         this.processTag();
         if (this.event instanceof ScalarEvent) {
            this.expectScalar();
         } else if (this.event instanceof SequenceStartEvent) {
            if (this.flowLevel == 0 && !this.canonical && !((SequenceStartEvent)this.event).isFlow() && !this.checkEmptySequence()) {
               this.expectBlockSequence();
            } else {
               this.expectFlowSequence();
            }
         } else if (this.flowLevel == 0 && !this.canonical && !((MappingStartEvent)this.event).isFlow() && !this.checkEmptyMapping()) {
            this.expectBlockMapping();
         } else {
            this.expectFlowMapping();
         }
      }
   }

   private void expectAlias() throws IOException {
      if (!(this.event instanceof AliasEvent)) {
         throw new EmitterException("Alias must be provided");
      } else {
         this.processAnchor("*");
         this.state = this.states.pop();
      }
   }

   private void expectScalar() throws IOException {
      this.increaseIndent(true, false);
      this.processScalar();
      this.indent = this.indents.pop();
      this.state = this.states.pop();
   }

   private void expectFlowSequence() throws IOException {
      this.writeIndicator("[", true, true, false);
      this.flowLevel++;
      this.increaseIndent(true, false);
      if (this.prettyFlow) {
         this.writeIndent();
      }

      this.state = new Emitter.ExpectFirstFlowSequenceItem();
   }

   private void expectFlowMapping() throws IOException {
      this.writeIndicator("{", true, true, false);
      this.flowLevel++;
      this.increaseIndent(true, false);
      if (this.prettyFlow) {
         this.writeIndent();
      }

      this.state = new Emitter.ExpectFirstFlowMappingKey();
   }

   private void expectBlockSequence() throws IOException {
      boolean var1 = this.mappingContext && !this.indention;
      this.increaseIndent(false, var1);
      this.state = new Emitter.ExpectFirstBlockSequenceItem();
   }

   private void expectBlockMapping() throws IOException {
      this.increaseIndent(false, false);
      this.state = new Emitter.ExpectFirstBlockMappingKey();
   }

   private boolean isFoldedOrLiteral(Event var1) {
      if (!var1.is(Event.ID.Scalar)) {
         return false;
      } else {
         ScalarEvent var2 = (ScalarEvent)var1;
         DumperOptions.ScalarStyle var3 = var2.getScalarStyle();
         return var3 == DumperOptions.ScalarStyle.FOLDED || var3 == DumperOptions.ScalarStyle.LITERAL;
      }
   }

   private boolean checkEmptySequence() {
      return this.event instanceof SequenceStartEvent && !this.events.isEmpty() && this.events.peek() instanceof SequenceEndEvent;
   }

   private boolean checkEmptyMapping() {
      return this.event instanceof MappingStartEvent && !this.events.isEmpty() && this.events.peek() instanceof MappingEndEvent;
   }

   private boolean checkEmptyDocument() {
      if (this.event instanceof DocumentStartEvent && !this.events.isEmpty()) {
         Event var1 = this.events.peek();
         if (!(var1 instanceof ScalarEvent)) {
            return false;
         } else {
            ScalarEvent var2 = (ScalarEvent)var1;
            return var2.getAnchor() == null && var2.getTag() == null && var2.getImplicit() != null && var2.getValue().length() == 0;
         }
      } else {
         return false;
      }
   }

   private boolean checkSimpleKey() {
      int var1 = 0;
      if (this.event instanceof NodeEvent && ((NodeEvent)this.event).getAnchor() != null) {
         if (this.preparedAnchor == null) {
            this.preparedAnchor = prepareAnchor(((NodeEvent)this.event).getAnchor());
         }

         var1 += this.preparedAnchor.length();
      }

      String var2 = null;
      if (this.event instanceof ScalarEvent) {
         var2 = ((ScalarEvent)this.event).getTag();
      } else if (this.event instanceof CollectionStartEvent) {
         var2 = ((CollectionStartEvent)this.event).getTag();
      }

      if (var2 != null) {
         if (this.preparedTag == null) {
            this.preparedTag = this.prepareTag(var2);
         }

         var1 += this.preparedTag.length();
      }

      if (this.event instanceof ScalarEvent) {
         if (this.analysis == null) {
            this.analysis = this.analyzeScalar(((ScalarEvent)this.event).getValue());
         }

         var1 += this.analysis.getScalar().length();
      }

      return var1 < this.maxSimpleKeyLength
         && (
            this.event instanceof AliasEvent
               || this.event instanceof ScalarEvent && !this.analysis.isEmpty() && !this.analysis.isMultiline()
               || this.checkEmptySequence()
               || this.checkEmptyMapping()
         );
   }

   private void processAnchor(String var1) throws IOException {
      NodeEvent var2 = (NodeEvent)this.event;
      if (var2.getAnchor() == null) {
         this.preparedAnchor = null;
      } else {
         if (this.preparedAnchor == null) {
            this.preparedAnchor = prepareAnchor(var2.getAnchor());
         }

         this.writeIndicator(var1 + this.preparedAnchor, true, false, false);
         this.preparedAnchor = null;
      }
   }

   private void processTag() throws IOException {
      Object var1 = null;
      if (this.event instanceof ScalarEvent) {
         ScalarEvent var4 = (ScalarEvent)this.event;
         var1 = var4.getTag();
         if (this.style == null) {
            this.style = this.chooseScalarStyle();
         }

         if ((!this.canonical || var1 == null)
            && (this.style == null && var4.getImplicit().canOmitTagInPlainScalar() || this.style != null && var4.getImplicit().canOmitTagInNonPlainScalar())) {
            this.preparedTag = null;
            return;
         }

         if (var4.getImplicit().canOmitTagInPlainScalar() && var1 == null) {
            var1 = "!";
            this.preparedTag = null;
         }
      } else {
         CollectionStartEvent var2 = (CollectionStartEvent)this.event;
         var1 = var2.getTag();
         if ((!this.canonical || var1 == null) && var2.getImplicit()) {
            this.preparedTag = null;
            return;
         }
      }

      if (var1 == null) {
         throw new EmitterException("tag is not specified");
      } else {
         if (this.preparedTag == null) {
            this.preparedTag = this.prepareTag((String)var1);
         }

         this.writeIndicator(this.preparedTag, true, false, false);
         this.preparedTag = null;
      }
   }

   private DumperOptions.ScalarStyle chooseScalarStyle() {
      ScalarEvent var1 = (ScalarEvent)this.event;
      if (this.analysis == null) {
         this.analysis = this.analyzeScalar(var1.getValue());
      }

      if ((var1.isPlain() || var1.getScalarStyle() != DumperOptions.ScalarStyle.DOUBLE_QUOTED) && !this.canonical) {
         if (!var1.isPlain()
            || !var1.getImplicit().canOmitTagInPlainScalar()
            || this.simpleKeyContext && (this.analysis.isEmpty() || this.analysis.isMultiline())
            || (this.flowLevel == 0 || !this.analysis.isAllowFlowPlain()) && (this.flowLevel != 0 || !this.analysis.isAllowBlockPlain())) {
            if (!var1.isPlain()
               && (var1.getScalarStyle() == DumperOptions.ScalarStyle.LITERAL || var1.getScalarStyle() == DumperOptions.ScalarStyle.FOLDED)
               && this.flowLevel == 0
               && !this.simpleKeyContext
               && this.analysis.isAllowBlock()) {
               return var1.getScalarStyle();
            } else {
               return !var1.isPlain() && var1.getScalarStyle() != DumperOptions.ScalarStyle.SINGLE_QUOTED
                     || !this.analysis.isAllowSingleQuoted()
                     || this.simpleKeyContext && this.analysis.isMultiline()
                  ? DumperOptions.ScalarStyle.DOUBLE_QUOTED
                  : DumperOptions.ScalarStyle.SINGLE_QUOTED;
            }
         } else {
            return null;
         }
      } else {
         return DumperOptions.ScalarStyle.DOUBLE_QUOTED;
      }
   }

   private void processScalar() throws IOException {
      ScalarEvent var1 = (ScalarEvent)this.event;
      if (this.analysis == null) {
         this.analysis = this.analyzeScalar(var1.getValue());
      }

      if (this.style == null) {
         this.style = this.chooseScalarStyle();
      }

      boolean var2 = !this.simpleKeyContext && this.splitLines;
      if (this.style == null) {
         this.writePlain(this.analysis.getScalar(), var2);
      } else {
         switch (this.style) {
            case DOUBLE_QUOTED:
               this.writeDoubleQuoted(this.analysis.getScalar(), var2);
               break;
            case SINGLE_QUOTED:
               this.writeSingleQuoted(this.analysis.getScalar(), var2);
               break;
            case FOLDED:
               this.writeFolded(this.analysis.getScalar(), var2);
               break;
            case LITERAL:
               this.writeLiteral(this.analysis.getScalar());
               break;
            default:
               throw new YAMLException("Unexpected style: " + this.style);
         }
      }

      this.analysis = null;
      this.style = null;
   }

   private String prepareVersion(DumperOptions.Version var1) {
      if (var1.major() != 1) {
         throw new EmitterException("unsupported YAML version: " + var1);
      } else {
         return var1.getRepresentation();
      }
   }

   private String prepareTagHandle(String var1) {
      if (var1.length() == 0) {
         throw new EmitterException("tag handle must not be empty");
      } else if (var1.charAt(0) != '!' || var1.charAt(var1.length() - 1) != '!') {
         throw new EmitterException("tag handle must start and end with '!': " + var1);
      } else if (!"!".equals(var1) && !HANDLE_FORMAT.matcher(var1).matches()) {
         throw new EmitterException("invalid character in the tag handle: " + var1);
      } else {
         return var1;
      }
   }

   private String prepareTagPrefix(String var1) {
      if (var1.length() == 0) {
         throw new EmitterException("tag prefix must not be empty");
      } else {
         StringBuilder var2 = new StringBuilder();
         byte var3 = 0;
         int var4 = 0;
         if (var1.charAt(0) == '!') {
            var4 = 1;
         }

         while (var4 < var1.length()) {
            var4++;
         }

         if (var3 < var4) {
            var2.append(var1, var3, var4);
         }

         return var2.toString();
      }
   }

   private String prepareTag(String var1) {
      if (var1.length() == 0) {
         throw new EmitterException("tag must not be empty");
      } else if ("!".equals(var1)) {
         return var1;
      } else {
         String var2 = null;
         String var3 = var1;

         for (String var5 : this.tagPrefixes.keySet()) {
            if (var1.startsWith(var5) && ("!".equals(var5) || var5.length() < var1.length())) {
               var2 = var5;
            }
         }

         if (var2 != null) {
            var3 = var1.substring(var2.length());
            var2 = this.tagPrefixes.get(var2);
         }

         int var6 = var3.length();
         String var7 = var6 > 0 ? var3.substring(0, var6) : "";
         return var2 != null ? var2 + var7 : "!<" + var7 + ">";
      }
   }

   static String prepareAnchor(String var0) {
      if (var0.length() == 0) {
         throw new EmitterException("anchor must not be empty");
      } else {
         for (Character var2 : INVALID_ANCHOR) {
            if (var0.indexOf(var2) > -1) {
               throw new EmitterException("Invalid character '" + var2 + "' in the anchor: " + var0);
            }
         }

         Matcher var3 = SPACES_PATTERN.matcher(var0);
         if (var3.find()) {
            throw new EmitterException("Anchor may not contain spaces: " + var0);
         } else {
            return var0;
         }
      }
   }

   private ScalarAnalysis analyzeScalar(String var1) {
      if (var1.length() == 0) {
         return new ScalarAnalysis(var1, true, false, false, true, true, false);
      } else {
         boolean var2 = false;
         boolean var3 = false;
         boolean var4 = false;
         boolean var5 = false;
         boolean var6 = false;
         boolean var7 = false;
         boolean var8 = false;
         boolean var9 = false;
         boolean var10 = false;
         boolean var11 = false;
         if (var1.startsWith("---") || var1.startsWith("...")) {
            var2 = true;
            var3 = true;
         }

         boolean var12 = true;
         boolean var13 = var1.length() == 1 || Constant.NULL_BL_T_LINEBR.has(var1.codePointAt(1));
         boolean var14 = false;
         boolean var15 = false;
         int var16 = 0;

         while (var16 < var1.length()) {
            int var17 = var1.codePointAt(var16);
            if (var16 == 0) {
               if ("#,[]{}&*!|>'\"%@`".indexOf(var17) != -1) {
                  var3 = true;
                  var2 = true;
               }

               if (var17 == 63 || var17 == 58) {
                  var3 = true;
                  if (var13) {
                     var2 = true;
                  }
               }

               if (var17 == 45 && var13) {
                  var3 = true;
                  var2 = true;
               }
            } else {
               if (",?[]{}".indexOf(var17) != -1) {
                  var3 = true;
               }

               if (var17 == 58) {
                  var3 = true;
                  if (var13) {
                     var2 = true;
                  }
               }

               if (var17 == 35 && var12) {
                  var3 = true;
                  var2 = true;
               }
            }

            boolean var18 = Constant.LINEBR.has(var17);
            if (var18) {
               var4 = true;
            }

            if (var17 != 10 && (32 > var17 || var17 > 126)) {
               if (var17 == 133 || var17 >= 160 && var17 <= 55295 || var17 >= 57344 && var17 <= 65533 || var17 >= 65536 && var17 <= 1114111) {
                  if (!this.allowUnicode) {
                     var5 = true;
                  }
               } else {
                  var5 = true;
               }
            }

            if (var17 == 32) {
               if (var16 == 0) {
                  var6 = true;
               }

               if (var16 == var1.length() - 1) {
                  var8 = true;
               }

               if (var15) {
                  var10 = true;
               }

               var14 = true;
               var15 = false;
            } else if (var18) {
               if (var16 == 0) {
                  var7 = true;
               }

               if (var16 == var1.length() - 1) {
                  var9 = true;
               }

               if (var14) {
                  var11 = true;
               }

               var14 = false;
               var15 = true;
            } else {
               var14 = false;
               var15 = false;
            }

            var16 += Character.charCount(var17);
            var12 = Constant.NULL_BL_T.has(var17) || var18;
            var13 = true;
            if (var16 + 1 < var1.length()) {
               int var19 = var16 + Character.charCount(var1.codePointAt(var16));
               if (var19 < var1.length()) {
                  var13 = Constant.NULL_BL_T.has(var1.codePointAt(var19)) || var18;
               }
            }
         }

         boolean var21 = true;
         boolean var22 = true;
         boolean var23 = true;
         boolean var20 = true;
         if (var6 || var7 || var8 || var9) {
            var22 = false;
            var21 = false;
         }

         if (var8) {
            var20 = false;
         }

         if (var10) {
            var23 = false;
            var22 = false;
            var21 = false;
         }

         if (var11 || var5) {
            var20 = false;
            var23 = false;
            var22 = false;
            var21 = false;
         }

         if (var4) {
            var21 = false;
         }

         if (var3) {
            var21 = false;
         }

         if (var2) {
            var22 = false;
         }

         return new ScalarAnalysis(var1, false, var4, var21, var22, var23, var20);
      }
   }

   void flushStream() throws IOException {
      this.stream.flush();
   }

   void writeStreamStart() {
   }

   void writeStreamEnd() throws IOException {
      this.flushStream();
   }

   void writeIndicator(String var1, boolean var2, boolean var3, boolean var4) throws IOException {
      if (!this.whitespace && var2) {
         this.column++;
         this.stream.write(SPACE);
      }

      this.whitespace = var3;
      this.indention = this.indention && var4;
      this.column = this.column + var1.length();
      this.openEnded = false;
      this.stream.write(var1);
   }

   void writeIndent() throws IOException {
      int var1;
      if (this.indent != null) {
         var1 = this.indent;
      } else {
         var1 = 0;
      }

      if (!this.indention || this.column > var1 || this.column == var1 && !this.whitespace) {
         this.writeLineBreak(null);
      }

      this.writeWhitespace(var1 - this.column);
   }

   private void writeWhitespace(int var1) throws IOException {
      if (var1 > 0) {
         this.whitespace = true;
         char[] var2 = new char[var1];

         for (int var3 = 0; var3 < var2.length; var3++) {
            var2[var3] = ' ';
         }

         this.column += var1;
         this.stream.write(var2);
      }
   }

   private void writeLineBreak(String var1) throws IOException {
      this.whitespace = true;
      this.indention = true;
      this.column = 0;
      if (var1 == null) {
         this.stream.write(this.bestLineBreak);
      } else {
         this.stream.write(var1);
      }
   }

   void writeVersionDirective(String var1) throws IOException {
      this.stream.write("%YAML ");
      this.stream.write(var1);
      this.writeLineBreak(null);
   }

   void writeTagDirective(String var1, String var2) throws IOException {
      this.stream.write("%TAG ");
      this.stream.write(var1);
      this.stream.write(SPACE);
      this.stream.write(var2);
      this.writeLineBreak(null);
   }

   private void writeSingleQuoted(String var1, boolean var2) throws IOException {
      this.writeIndicator("'", true, false, false);
      boolean var3 = false;
      boolean var4 = false;
      int var5 = 0;

      for (int var6 = 0; var6 <= var1.length(); var6++) {
         char var7 = 0;
         if (var6 < var1.length()) {
            var7 = var1.charAt(var6);
         }

         if (var3) {
            if (var7 == 0 || var7 != ' ') {
               if (var5 + 1 == var6 && this.column > this.bestWidth && var2 && var5 != 0 && var6 != var1.length()) {
                  this.writeIndent();
               } else {
                  int var14 = var6 - var5;
                  this.column += var14;
                  this.stream.write(var1, var5, var14);
               }

               var5 = var6;
            }
         } else if (!var4) {
            if (Constant.LINEBR.has(var7, "\u0000 '") && var5 < var6) {
               int var13 = var6 - var5;
               this.column += var13;
               this.stream.write(var1, var5, var13);
               var5 = var6;
            }
         } else if (var7 == 0 || Constant.LINEBR.hasNo(var7)) {
            if (var1.charAt(var5) == '\n') {
               this.writeLineBreak(null);
            }

            String var8 = var1.substring(var5, var6);

            for (char var12 : var8.toCharArray()) {
               if (var12 == '\n') {
                  this.writeLineBreak(null);
               } else {
                  this.writeLineBreak(String.valueOf(var12));
               }
            }

            this.writeIndent();
            var5 = var6;
         }

         if (var7 == '\'') {
            this.column += 2;
            this.stream.write("''");
            var5 = var6 + 1;
         }

         if (var7 != 0) {
            var3 = var7 == ' ';
            var4 = Constant.LINEBR.has(var7);
         }
      }

      this.writeIndicator("'", false, false, false);
   }

   private void writeDoubleQuoted(String var1, boolean var2) throws IOException {
      this.writeIndicator("\"", true, false, false);
      int var3 = 0;

      for (int var4 = 0; var4 <= var1.length(); var4++) {
         Character var5 = null;
         if (var4 < var1.length()) {
            var5 = var1.charAt(var4);
         }

         if (var5 == null || "\"\\\u0085\u2028\u2029\ufeff".indexOf(var5) != -1 || ' ' > var5 || var5 > '~') {
            if (var3 < var4) {
               int var6 = var4 - var3;
               this.column += var6;
               this.stream.write(var1, var3, var6);
               var3 = var4;
            }

            if (var5 != null) {
               String var9;
               if (ESCAPE_REPLACEMENTS.containsKey(var5)) {
                  var9 = "\\" + ESCAPE_REPLACEMENTS.get(var5);
               } else if (this.allowUnicode && StreamReader.isPrintable(var5)) {
                  var9 = String.valueOf(var5);
               } else if (var5 <= 255) {
                  String var14 = "0" + Integer.toString(var5, 16);
                  var9 = "\\x" + var14.substring(var14.length() - 2);
               } else if (var5 < '\ud800' || var5 > '\udbff') {
                  String var13 = "000" + Integer.toString(var5, 16);
                  var9 = "\\u" + var13.substring(var13.length() - 4);
               } else if (var4 + 1 < var1.length()) {
                  Character var7 = var1.charAt(++var4);
                  String var8 = "000" + Long.toHexString((long)Character.toCodePoint(var5, var7));
                  var9 = "\\U" + var8.substring(var8.length() - 8);
               } else {
                  String var12 = "000" + Integer.toString(var5, 16);
                  var9 = "\\u" + var12.substring(var12.length() - 4);
               }

               this.column = this.column + var9.length();
               this.stream.write(var9);
               var3 = var4 + 1;
            }
         }

         if (0 < var4 && var4 < var1.length() - 1 && (var5 == ' ' || var3 >= var4) && this.column + (var4 - var3) > this.bestWidth && var2) {
            String var10;
            if (var3 >= var4) {
               var10 = "\\";
            } else {
               var10 = var1.substring(var3, var4) + "\\";
            }

            if (var3 < var4) {
               var3 = var4;
            }

            this.column = this.column + var10.length();
            this.stream.write(var10);
            this.writeIndent();
            this.whitespace = false;
            this.indention = false;
            if (var1.charAt(var3) == ' ') {
               var10 = "\\";
               this.column = this.column + var10.length();
               this.stream.write(var10);
            }
         }
      }

      this.writeIndicator("\"", false, false, false);
   }

   private boolean writeCommentLines(List<CommentLine> var1) throws IOException {
      boolean var2 = false;
      if (this.emitComments) {
         int var3 = 0;
         boolean var4 = true;

         for (CommentLine var6 : var1) {
            if (var6.getCommentType() != CommentType.BLANK_LINE) {
               if (var4) {
                  var4 = false;
                  this.writeIndicator("#", var6.getCommentType() == CommentType.IN_LINE, false, false);
                  var3 = this.column > 0 ? this.column - 1 : 0;
               } else {
                  this.writeWhitespace(var3);
                  this.writeIndicator("#", false, false, false);
               }

               this.stream.write(var6.getValue());
               this.writeLineBreak(null);
            } else {
               this.writeLineBreak(null);
               this.writeIndent();
            }

            var2 = true;
         }
      }

      return var2;
   }

   private void writeBlockComment() throws IOException {
      if (!this.blockCommentsCollector.isEmpty()) {
         this.writeIndent();
         this.writeCommentLines(this.blockCommentsCollector.consume());
      }
   }

   private boolean writeInlineComments() throws IOException {
      return this.writeCommentLines(this.inlineCommentsCollector.consume());
   }

   private String determineBlockHints(String var1) {
      StringBuilder var2 = new StringBuilder();
      if (Constant.LINEBR.has(var1.charAt(0), " ")) {
         var2.append(this.bestIndent);
      }

      char var3 = var1.charAt(var1.length() - 1);
      if (Constant.LINEBR.hasNo(var3)) {
         var2.append("-");
      } else if (var1.length() == 1 || Constant.LINEBR.has(var1.charAt(var1.length() - 2))) {
         var2.append("+");
      }

      return var2.toString();
   }

   void writeFolded(String var1, boolean var2) throws IOException {
      String var3 = this.determineBlockHints(var1);
      this.writeIndicator(">" + var3, true, false, false);
      if (var3.length() > 0 && var3.charAt(var3.length() - 1) == '+') {
         this.openEnded = true;
      }

      if (!this.writeInlineComments()) {
         this.writeLineBreak(null);
      }

      boolean var4 = true;
      boolean var5 = false;
      boolean var6 = true;
      int var7 = 0;

      for (int var8 = 0; var8 <= var1.length(); var8++) {
         char var9 = 0;
         if (var8 < var1.length()) {
            var9 = var1.charAt(var8);
         }

         if (var6) {
            if (var9 == 0 || Constant.LINEBR.hasNo(var9)) {
               if (!var4 && var9 != 0 && var9 != ' ' && var1.charAt(var7) == '\n') {
                  this.writeLineBreak(null);
               }

               var4 = var9 == ' ';
               String var16 = var1.substring(var7, var8);

               for (char var14 : var16.toCharArray()) {
                  if (var14 == '\n') {
                     this.writeLineBreak(null);
                  } else {
                     this.writeLineBreak(String.valueOf(var14));
                  }
               }

               if (var9 != 0) {
                  this.writeIndent();
               }

               var7 = var8;
            }
         } else if (var5) {
            if (var9 != ' ') {
               if (var7 + 1 == var8 && this.column > this.bestWidth && var2) {
                  this.writeIndent();
               } else {
                  int var15 = var8 - var7;
                  this.column += var15;
                  this.stream.write(var1, var7, var15);
               }

               var7 = var8;
            }
         } else if (Constant.LINEBR.has(var9, "\u0000 ")) {
            int var10 = var8 - var7;
            this.column += var10;
            this.stream.write(var1, var7, var10);
            if (var9 == 0) {
               this.writeLineBreak(null);
            }

            var7 = var8;
         }

         if (var9 != 0) {
            var6 = Constant.LINEBR.has(var9);
            var5 = var9 == ' ';
         }
      }
   }

   void writeLiteral(String var1) throws IOException {
      String var2 = this.determineBlockHints(var1);
      this.writeIndicator("|" + var2, true, false, false);
      if (var2.length() > 0 && var2.charAt(var2.length() - 1) == '+') {
         this.openEnded = true;
      }

      if (!this.writeInlineComments()) {
         this.writeLineBreak(null);
      }

      boolean var3 = true;
      int var4 = 0;

      for (int var5 = 0; var5 <= var1.length(); var5++) {
         char var6 = 0;
         if (var5 < var1.length()) {
            var6 = var1.charAt(var5);
         }

         if (!var3) {
            if (var6 == 0 || Constant.LINEBR.has(var6)) {
               this.stream.write(var1, var4, var5 - var4);
               if (var6 == 0) {
                  this.writeLineBreak(null);
               }

               var4 = var5;
            }
         } else if (var6 == 0 || Constant.LINEBR.hasNo(var6)) {
            String var7 = var1.substring(var4, var5);

            for (char var11 : var7.toCharArray()) {
               if (var11 == '\n') {
                  this.writeLineBreak(null);
               } else {
                  this.writeLineBreak(String.valueOf(var11));
               }
            }

            if (var6 != 0) {
               this.writeIndent();
            }

            var4 = var5;
         }

         if (var6 != 0) {
            var3 = Constant.LINEBR.has(var6);
         }
      }
   }

   void writePlain(String var1, boolean var2) throws IOException {
      if (this.rootContext) {
         this.openEnded = true;
      }

      if (var1.length() != 0) {
         if (!this.whitespace) {
            this.column++;
            this.stream.write(SPACE);
         }

         this.whitespace = false;
         this.indention = false;
         boolean var3 = false;
         boolean var4 = false;
         int var5 = 0;

         for (int var6 = 0; var6 <= var1.length(); var6++) {
            char var7 = 0;
            if (var6 < var1.length()) {
               var7 = var1.charAt(var6);
            }

            if (var3) {
               if (var7 != ' ') {
                  if (var5 + 1 == var6 && this.column > this.bestWidth && var2) {
                     this.writeIndent();
                     this.whitespace = false;
                     this.indention = false;
                  } else {
                     int var14 = var6 - var5;
                     this.column += var14;
                     this.stream.write(var1, var5, var14);
                  }

                  var5 = var6;
               }
            } else if (!var4) {
               if (Constant.LINEBR.has(var7, "\u0000 ")) {
                  int var13 = var6 - var5;
                  this.column += var13;
                  this.stream.write(var1, var5, var13);
                  var5 = var6;
               }
            } else if (Constant.LINEBR.hasNo(var7)) {
               if (var1.charAt(var5) == '\n') {
                  this.writeLineBreak(null);
               }

               String var8 = var1.substring(var5, var6);

               for (char var12 : var8.toCharArray()) {
                  if (var12 == '\n') {
                     this.writeLineBreak(null);
                  } else {
                     this.writeLineBreak(String.valueOf(var12));
                  }
               }

               this.writeIndent();
               this.whitespace = false;
               this.indention = false;
               var5 = var6;
            }

            if (var7 != 0) {
               var3 = var7 == ' ';
               var4 = Constant.LINEBR.has(var7);
            }
         }
      }
   }

   static {
      INVALID_ANCHOR.add('[');
      INVALID_ANCHOR.add(']');
      INVALID_ANCHOR.add('{');
      INVALID_ANCHOR.add('}');
      INVALID_ANCHOR.add(',');
      INVALID_ANCHOR.add('*');
      INVALID_ANCHOR.add('&');
      ESCAPE_REPLACEMENTS.put('\u0000', "0");
      ESCAPE_REPLACEMENTS.put('\u0007', "a");
      ESCAPE_REPLACEMENTS.put('\b', "b");
      ESCAPE_REPLACEMENTS.put('\t', "t");
      ESCAPE_REPLACEMENTS.put('\n', "n");
      ESCAPE_REPLACEMENTS.put('\u000b', "v");
      ESCAPE_REPLACEMENTS.put('\f', "f");
      ESCAPE_REPLACEMENTS.put('\r', "r");
      ESCAPE_REPLACEMENTS.put('\u001b', "e");
      ESCAPE_REPLACEMENTS.put('"', "\"");
      ESCAPE_REPLACEMENTS.put('\\', "\\");
      ESCAPE_REPLACEMENTS.put('\u0085', "N");
      ESCAPE_REPLACEMENTS.put(' ', "_");
      ESCAPE_REPLACEMENTS.put('\u2028', "L");
      ESCAPE_REPLACEMENTS.put('\u2029', "P");
      DEFAULT_TAG_PREFIXES.put("!", "!");
      DEFAULT_TAG_PREFIXES.put("tag:yaml.org,2002:", "!!");
   }

   private class ExpectBlockMappingKey implements EmitterState {
      private final boolean first;

      public ExpectBlockMappingKey(boolean var2) {
         this.first = var2;
      }

      @Override
      public void expect() throws IOException {
         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeBlockComment();
         if (!this.first && Emitter.this.event instanceof MappingEndEvent) {
            Emitter.this.indent = Emitter.this.indents.pop();
            Emitter.this.state = Emitter.this.states.pop();
         } else {
            Emitter.this.writeIndent();
            if (Emitter.this.checkSimpleKey()) {
               Emitter.this.states.push(Emitter.this.new ExpectBlockMappingSimpleValue());
               Emitter.this.expectNode(false, true, true);
            } else {
               Emitter.this.writeIndicator("?", true, false, true);
               Emitter.this.states.push(Emitter.this.new ExpectBlockMappingValue());
               Emitter.this.expectNode(false, true, false);
            }
         }
      }
   }

   private class ExpectBlockMappingSimpleValue implements EmitterState {
      private ExpectBlockMappingSimpleValue() {
      }

      @Override
      public void expect() throws IOException {
         Emitter.this.writeIndicator(":", false, false, false);
         Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         if (!Emitter.this.isFoldedOrLiteral(Emitter.this.event) && Emitter.this.writeInlineComments()) {
            Emitter.this.increaseIndent(true, false);
            Emitter.this.writeIndent();
            Emitter.this.indent = Emitter.this.indents.pop();
         }

         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         if (!Emitter.this.blockCommentsCollector.isEmpty()) {
            Emitter.this.increaseIndent(true, false);
            Emitter.this.writeBlockComment();
            Emitter.this.writeIndent();
            Emitter.this.indent = Emitter.this.indents.pop();
         }

         Emitter.this.states.push(Emitter.this.new ExpectBlockMappingKey(false));
         Emitter.this.expectNode(false, true, false);
         Emitter.this.inlineCommentsCollector.collectEvents();
         Emitter.this.writeInlineComments();
      }
   }

   private class ExpectBlockMappingValue implements EmitterState {
      private ExpectBlockMappingValue() {
      }

      @Override
      public void expect() throws IOException {
         Emitter.this.writeIndent();
         Emitter.this.writeIndicator(":", true, false, true);
         Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeInlineComments();
         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeBlockComment();
         Emitter.this.states.push(Emitter.this.new ExpectBlockMappingKey(false));
         Emitter.this.expectNode(false, true, false);
         Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
         Emitter.this.writeInlineComments();
      }
   }

   private class ExpectBlockSequenceItem implements EmitterState {
      private final boolean first;

      public ExpectBlockSequenceItem(boolean var2) {
         this.first = var2;
      }

      @Override
      public void expect() throws IOException {
         if (!this.first && Emitter.this.event instanceof SequenceEndEvent) {
            Emitter.this.indent = Emitter.this.indents.pop();
            Emitter.this.state = Emitter.this.states.pop();
         } else if (Emitter.this.event instanceof CommentEvent) {
            Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
         } else {
            Emitter.this.writeIndent();
            if (!Emitter.this.indentWithIndicator || this.first) {
               Emitter.this.writeWhitespace(Emitter.this.indicatorIndent);
            }

            Emitter.this.writeIndicator("-", true, false, true);
            if (Emitter.this.indentWithIndicator && this.first) {
               Emitter.this.indent = Emitter.this.indent + Emitter.this.indicatorIndent;
            }

            if (!Emitter.this.blockCommentsCollector.isEmpty()) {
               Emitter.this.increaseIndent(false, false);
               Emitter.this.writeBlockComment();
               if (Emitter.this.event instanceof ScalarEvent) {
                  Emitter.this.analysis = Emitter.this.analyzeScalar(((ScalarEvent)Emitter.this.event).getValue());
                  if (!Emitter.this.analysis.isEmpty()) {
                     Emitter.this.writeIndent();
                  }
               }

               Emitter.this.indent = Emitter.this.indents.pop();
            }

            Emitter.this.states.push(Emitter.this.new ExpectBlockSequenceItem(false));
            Emitter.this.expectNode(false, false, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
         }
      }
   }

   private class ExpectDocumentEnd implements EmitterState {
      private ExpectDocumentEnd() {
      }

      @Override
      public void expect() throws IOException {
         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeBlockComment();
         if (Emitter.this.event instanceof DocumentEndEvent) {
            Emitter.this.writeIndent();
            if (((DocumentEndEvent)Emitter.this.event).getExplicit()) {
               Emitter.this.writeIndicator("...", true, false, false);
               Emitter.this.writeIndent();
            }

            Emitter.this.flushStream();
            Emitter.this.state = Emitter.this.new ExpectDocumentStart(false);
         } else {
            throw new EmitterException("expected DocumentEndEvent, but got " + Emitter.this.event);
         }
      }
   }

   private class ExpectDocumentRoot implements EmitterState {
      private ExpectDocumentRoot() {
      }

      @Override
      public void expect() throws IOException {
         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         if (!Emitter.this.blockCommentsCollector.isEmpty()) {
            Emitter.this.writeBlockComment();
            if (Emitter.this.event instanceof DocumentEndEvent) {
               Emitter.this.new ExpectDocumentEnd().expect();
               return;
            }
         }

         Emitter.this.states.push(Emitter.this.new ExpectDocumentEnd());
         Emitter.this.expectNode(true, false, false);
      }
   }

   private class ExpectDocumentStart implements EmitterState {
      private final boolean first;

      public ExpectDocumentStart(boolean var2) {
         this.first = var2;
      }

      @Override
      public void expect() throws IOException {
         if (Emitter.this.event instanceof DocumentStartEvent) {
            DocumentStartEvent var1 = (DocumentStartEvent)Emitter.this.event;
            if ((var1.getVersion() != null || var1.getTags() != null) && Emitter.this.openEnded) {
               Emitter.this.writeIndicator("...", true, false, false);
               Emitter.this.writeIndent();
            }

            if (var1.getVersion() != null) {
               String var2 = Emitter.this.prepareVersion(var1.getVersion());
               Emitter.this.writeVersionDirective(var2);
            }

            Emitter.this.tagPrefixes = new LinkedHashMap<>(Emitter.DEFAULT_TAG_PREFIXES);
            if (var1.getTags() != null) {
               for (String var4 : new TreeSet<>(var1.getTags().keySet())) {
                  String var5 = var1.getTags().get(var4);
                  Emitter.this.tagPrefixes.put(var5, var4);
                  String var6 = Emitter.this.prepareTagHandle(var4);
                  String var7 = Emitter.this.prepareTagPrefix(var5);
                  Emitter.this.writeTagDirective(var6, var7);
               }
            }

            boolean var9 = this.first
               && !var1.getExplicit()
               && !Emitter.this.canonical
               && var1.getVersion() == null
               && (var1.getTags() == null || var1.getTags().isEmpty())
               && !Emitter.this.checkEmptyDocument();
            if (!var9) {
               Emitter.this.writeIndent();
               Emitter.this.writeIndicator("---", true, false, false);
               if (Emitter.this.canonical) {
                  Emitter.this.writeIndent();
               }
            }

            Emitter.this.state = Emitter.this.new ExpectDocumentRoot();
         } else if (Emitter.this.event instanceof StreamEndEvent) {
            Emitter.this.writeStreamEnd();
            Emitter.this.state = Emitter.this.new ExpectNothing();
         } else {
            if (!(Emitter.this.event instanceof CommentEvent)) {
               throw new EmitterException("expected DocumentStartEvent, but got " + Emitter.this.event);
            }

            Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeBlockComment();
         }
      }
   }

   private class ExpectFirstBlockMappingKey implements EmitterState {
      private ExpectFirstBlockMappingKey() {
      }

      @Override
      public void expect() throws IOException {
         Emitter.this.new ExpectBlockMappingKey(true).expect();
      }
   }

   private class ExpectFirstBlockSequenceItem implements EmitterState {
      private ExpectFirstBlockSequenceItem() {
      }

      @Override
      public void expect() throws IOException {
         Emitter.this.new ExpectBlockSequenceItem(true).expect();
      }
   }

   private class ExpectFirstDocumentStart implements EmitterState {
      private ExpectFirstDocumentStart() {
      }

      @Override
      public void expect() throws IOException {
         Emitter.this.new ExpectDocumentStart(true).expect();
      }
   }

   private class ExpectFirstFlowMappingKey implements EmitterState {
      private ExpectFirstFlowMappingKey() {
      }

      @Override
      public void expect() throws IOException {
         Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeBlockComment();
         if (Emitter.this.event instanceof MappingEndEvent) {
            Emitter.this.indent = Emitter.this.indents.pop();
            Emitter.this.flowLevel--;
            Emitter.this.writeIndicator("}", false, false, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
            Emitter.this.state = Emitter.this.states.pop();
         } else {
            if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.prettyFlow) {
               Emitter.this.writeIndent();
            }

            if (!Emitter.this.canonical && Emitter.this.checkSimpleKey()) {
               Emitter.this.states.push(Emitter.this.new ExpectFlowMappingSimpleValue());
               Emitter.this.expectNode(false, true, true);
            } else {
               Emitter.this.writeIndicator("?", true, false, false);
               Emitter.this.states.push(Emitter.this.new ExpectFlowMappingValue());
               Emitter.this.expectNode(false, true, false);
            }
         }
      }
   }

   private class ExpectFirstFlowSequenceItem implements EmitterState {
      private ExpectFirstFlowSequenceItem() {
      }

      @Override
      public void expect() throws IOException {
         if (Emitter.this.event instanceof SequenceEndEvent) {
            Emitter.this.indent = Emitter.this.indents.pop();
            Emitter.this.flowLevel--;
            Emitter.this.writeIndicator("]", false, false, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
            Emitter.this.state = Emitter.this.states.pop();
         } else if (Emitter.this.event instanceof CommentEvent) {
            Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeBlockComment();
         } else {
            if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.prettyFlow) {
               Emitter.this.writeIndent();
            }

            Emitter.this.states.push(Emitter.this.new ExpectFlowSequenceItem());
            Emitter.this.expectNode(false, false, false);
            Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeInlineComments();
         }
      }
   }

   private class ExpectFlowMappingKey implements EmitterState {
      private ExpectFlowMappingKey() {
      }

      @Override
      public void expect() throws IOException {
         if (Emitter.this.event instanceof MappingEndEvent) {
            Emitter.this.indent = Emitter.this.indents.pop();
            Emitter.this.flowLevel--;
            if (Emitter.this.canonical) {
               Emitter.this.writeIndicator(",", false, false, false);
               Emitter.this.writeIndent();
            }

            if (Emitter.this.prettyFlow) {
               Emitter.this.writeIndent();
            }

            Emitter.this.writeIndicator("}", false, false, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
            Emitter.this.state = Emitter.this.states.pop();
         } else {
            Emitter.this.writeIndicator(",", false, false, false);
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEventsAndPoll(Emitter.this.event);
            Emitter.this.writeBlockComment();
            if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.prettyFlow) {
               Emitter.this.writeIndent();
            }

            if (!Emitter.this.canonical && Emitter.this.checkSimpleKey()) {
               Emitter.this.states.push(Emitter.this.new ExpectFlowMappingSimpleValue());
               Emitter.this.expectNode(false, true, true);
            } else {
               Emitter.this.writeIndicator("?", true, false, false);
               Emitter.this.states.push(Emitter.this.new ExpectFlowMappingValue());
               Emitter.this.expectNode(false, true, false);
            }
         }
      }
   }

   private class ExpectFlowMappingSimpleValue implements EmitterState {
      private ExpectFlowMappingSimpleValue() {
      }

      @Override
      public void expect() throws IOException {
         Emitter.this.writeIndicator(":", false, false, false);
         Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeInlineComments();
         Emitter.this.states.push(Emitter.this.new ExpectFlowMappingKey());
         Emitter.this.expectNode(false, true, false);
         Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
         Emitter.this.writeInlineComments();
      }
   }

   private class ExpectFlowMappingValue implements EmitterState {
      private ExpectFlowMappingValue() {
      }

      @Override
      public void expect() throws IOException {
         if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.prettyFlow) {
            Emitter.this.writeIndent();
         }

         Emitter.this.writeIndicator(":", true, false, false);
         Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEventsAndPoll(Emitter.this.event);
         Emitter.this.writeInlineComments();
         Emitter.this.states.push(Emitter.this.new ExpectFlowMappingKey());
         Emitter.this.expectNode(false, true, false);
         Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
         Emitter.this.writeInlineComments();
      }
   }

   private class ExpectFlowSequenceItem implements EmitterState {
      private ExpectFlowSequenceItem() {
      }

      @Override
      public void expect() throws IOException {
         if (Emitter.this.event instanceof SequenceEndEvent) {
            Emitter.this.indent = Emitter.this.indents.pop();
            Emitter.this.flowLevel--;
            if (Emitter.this.canonical) {
               Emitter.this.writeIndicator(",", false, false, false);
               Emitter.this.writeIndent();
            } else if (Emitter.this.prettyFlow) {
               Emitter.this.writeIndent();
            }

            Emitter.this.writeIndicator("]", false, false, false);
            Emitter.this.inlineCommentsCollector.collectEvents();
            Emitter.this.writeInlineComments();
            if (Emitter.this.prettyFlow) {
               Emitter.this.writeIndent();
            }

            Emitter.this.state = Emitter.this.states.pop();
         } else if (Emitter.this.event instanceof CommentEvent) {
            Emitter.this.event = Emitter.this.blockCommentsCollector.collectEvents(Emitter.this.event);
         } else {
            Emitter.this.writeIndicator(",", false, false, false);
            Emitter.this.writeBlockComment();
            if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth && Emitter.this.splitLines || Emitter.this.prettyFlow) {
               Emitter.this.writeIndent();
            }

            Emitter.this.states.push(Emitter.this.new ExpectFlowSequenceItem());
            Emitter.this.expectNode(false, false, false);
            Emitter.this.event = Emitter.this.inlineCommentsCollector.collectEvents(Emitter.this.event);
            Emitter.this.writeInlineComments();
         }
      }
   }

   private class ExpectNothing implements EmitterState {
      private ExpectNothing() {
      }

      @Override
      public void expect() throws IOException {
         throw new EmitterException("expecting nothing, but got " + Emitter.this.event);
      }
   }

   private class ExpectStreamStart implements EmitterState {
      private ExpectStreamStart() {
      }

      @Override
      public void expect() throws IOException {
         if (Emitter.this.event instanceof StreamStartEvent) {
            Emitter.this.writeStreamStart();
            Emitter.this.state = Emitter.this.new ExpectFirstDocumentStart();
         } else {
            throw new EmitterException("expected StreamStartEvent, but got " + Emitter.this.event);
         }
      }
   }
}
