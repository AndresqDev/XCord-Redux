package org.yaml.snakeyaml.scanner;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.AnchorToken;
import org.yaml.snakeyaml.tokens.BlockEndToken;
import org.yaml.snakeyaml.tokens.BlockEntryToken;
import org.yaml.snakeyaml.tokens.BlockMappingStartToken;
import org.yaml.snakeyaml.tokens.BlockSequenceStartToken;
import org.yaml.snakeyaml.tokens.CommentToken;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.tokens.DocumentEndToken;
import org.yaml.snakeyaml.tokens.DocumentStartToken;
import org.yaml.snakeyaml.tokens.FlowEntryToken;
import org.yaml.snakeyaml.tokens.FlowMappingEndToken;
import org.yaml.snakeyaml.tokens.FlowMappingStartToken;
import org.yaml.snakeyaml.tokens.FlowSequenceEndToken;
import org.yaml.snakeyaml.tokens.FlowSequenceStartToken;
import org.yaml.snakeyaml.tokens.KeyToken;
import org.yaml.snakeyaml.tokens.ScalarToken;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import org.yaml.snakeyaml.tokens.StreamStartToken;
import org.yaml.snakeyaml.tokens.TagToken;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.tokens.ValueToken;
import org.yaml.snakeyaml.util.ArrayStack;
import org.yaml.snakeyaml.util.UriEncoder;

public final class ScannerImpl implements Scanner {
   private static final Pattern NOT_HEXA = Pattern.compile("[^0-9A-Fa-f]");
   public static final Map<Character, String> ESCAPE_REPLACEMENTS = new HashMap<>();
   public static final Map<Character, Integer> ESCAPE_CODES = new HashMap<>();
   private final StreamReader reader;
   private boolean done = false;
   private int flowLevel = 0;
   private List<Token> tokens;
   private Token lastToken;
   private int tokensTaken = 0;
   private int indent = -1;
   private ArrayStack<Integer> indents;
   private boolean parseComments;
   private boolean allowSimpleKey = true;
   private Map<Integer, SimpleKey> possibleSimpleKeys;

   public ScannerImpl(StreamReader var1) {
      this.parseComments = false;
      this.reader = var1;
      this.tokens = new ArrayList<>(100);
      this.indents = new ArrayStack<>(10);
      this.possibleSimpleKeys = new LinkedHashMap<>();
      this.fetchStreamStart();
   }

   public ScannerImpl setParseComments(boolean var1) {
      this.parseComments = var1;
      return this;
   }

   public boolean isParseComments() {
      return this.parseComments;
   }

   @Override
   public boolean checkToken(Token.ID... var1) {
      while (this.needMoreTokens()) {
         this.fetchMoreTokens();
      }

      if (!this.tokens.isEmpty()) {
         if (var1.length == 0) {
            return true;
         }

         Token.ID var2 = this.tokens.get(0).getTokenId();

         for (int var3 = 0; var3 < var1.length; var3++) {
            if (var2 == var1[var3]) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public Token peekToken() {
      while (this.needMoreTokens()) {
         this.fetchMoreTokens();
      }

      return this.tokens.get(0);
   }

   @Override
   public Token getToken() {
      this.tokensTaken++;
      return this.tokens.remove(0);
   }

   private void addToken(Token var1) {
      this.lastToken = var1;
      this.tokens.add(var1);
   }

   private void addToken(int var1, Token var2) {
      if (var1 == this.tokens.size()) {
         this.lastToken = var2;
      }

      this.tokens.add(var1, var2);
   }

   private void addAllTokens(List<Token> var1) {
      this.lastToken = (Token)var1.get(var1.size() - 1);
      this.tokens.addAll(var1);
   }

   private boolean needMoreTokens() {
      if (this.done) {
         return false;
      } else if (this.tokens.isEmpty()) {
         return true;
      } else {
         this.stalePossibleSimpleKeys();
         return this.nextPossibleSimpleKey() == this.tokensTaken;
      }
   }

   private void fetchMoreTokens() {
      this.scanToNextToken();
      this.stalePossibleSimpleKeys();
      this.unwindIndent(this.reader.getColumn());
      int var1 = this.reader.peek();
      switch (var1) {
         case 0:
            this.fetchStreamEnd();
            return;
         case 33:
            this.fetchTag();
            return;
         case 34:
            this.fetchDouble();
            return;
         case 37:
            if (this.checkDirective()) {
               this.fetchDirective();
               return;
            }
            break;
         case 38:
            this.fetchAnchor();
            return;
         case 39:
            this.fetchSingle();
            return;
         case 42:
            this.fetchAlias();
            return;
         case 44:
            this.fetchFlowEntry();
            return;
         case 45:
            if (this.checkDocumentStart()) {
               this.fetchDocumentStart();
               return;
            }

            if (this.checkBlockEntry()) {
               this.fetchBlockEntry();
               return;
            }
            break;
         case 46:
            if (this.checkDocumentEnd()) {
               this.fetchDocumentEnd();
               return;
            }
            break;
         case 58:
            if (this.checkValue()) {
               this.fetchValue();
               return;
            }
            break;
         case 62:
            if (this.flowLevel == 0) {
               this.fetchFolded();
               return;
            }
            break;
         case 63:
            if (this.checkKey()) {
               this.fetchKey();
               return;
            }
            break;
         case 91:
            this.fetchFlowSequenceStart();
            return;
         case 93:
            this.fetchFlowSequenceEnd();
            return;
         case 123:
            this.fetchFlowMappingStart();
            return;
         case 124:
            if (this.flowLevel == 0) {
               this.fetchLiteral();
               return;
            }
            break;
         case 125:
            this.fetchFlowMappingEnd();
            return;
      }

      if (this.checkPlain()) {
         this.fetchPlain();
      } else {
         String var2 = String.valueOf(Character.toChars(var1));

         for (Character var4 : ESCAPE_REPLACEMENTS.keySet()) {
            String var5 = ESCAPE_REPLACEMENTS.get(var4);
            if (var5.equals(var2)) {
               var2 = "\\" + var4;
               break;
            }
         }

         if (var1 == 9) {
            var2 = var2 + "(TAB)";
         }

         String var6 = String.format("found character '%s' that cannot start any token. (Do not use %s for indentation)", var2, var2);
         throw new ScannerException("while scanning for the next token", null, var6, this.reader.getMark());
      }
   }

   private int nextPossibleSimpleKey() {
      return !this.possibleSimpleKeys.isEmpty() ? this.possibleSimpleKeys.values().iterator().next().getTokenNumber() : -1;
   }

   private void stalePossibleSimpleKeys() {
      if (!this.possibleSimpleKeys.isEmpty()) {
         Iterator var1 = this.possibleSimpleKeys.values().iterator();

         while (var1.hasNext()) {
            SimpleKey var2 = (SimpleKey)var1.next();
            if (var2.getLine() != this.reader.getLine() || this.reader.getIndex() - var2.getIndex() > 1024) {
               if (var2.isRequired()) {
                  throw new ScannerException("while scanning a simple key", var2.getMark(), "could not find expected ':'", this.reader.getMark());
               }

               var1.remove();
            }
         }
      }
   }

   private void savePossibleSimpleKey() {
      boolean var1 = this.flowLevel == 0 && this.indent == this.reader.getColumn();
      if (!this.allowSimpleKey && var1) {
         throw new YAMLException("A simple key is required only if it is the first token in the current line");
      } else {
         if (this.allowSimpleKey) {
            this.removePossibleSimpleKey();
            int var2 = this.tokensTaken + this.tokens.size();
            SimpleKey var3 = new SimpleKey(var2, var1, this.reader.getIndex(), this.reader.getLine(), this.reader.getColumn(), this.reader.getMark());
            this.possibleSimpleKeys.put(this.flowLevel, var3);
         }
      }
   }

   private void removePossibleSimpleKey() {
      SimpleKey var1 = this.possibleSimpleKeys.remove(this.flowLevel);
      if (var1 != null && var1.isRequired()) {
         throw new ScannerException("while scanning a simple key", var1.getMark(), "could not find expected ':'", this.reader.getMark());
      }
   }

   private void unwindIndent(int var1) {
      if (this.flowLevel == 0) {
         while (this.indent > var1) {
            Mark var2 = this.reader.getMark();
            this.indent = this.indents.pop();
            this.addToken(new BlockEndToken(var2, var2));
         }
      }
   }

   private boolean addIndent(int var1) {
      if (this.indent < var1) {
         this.indents.push(this.indent);
         this.indent = var1;
         return true;
      } else {
         return false;
      }
   }

   private void fetchStreamStart() {
      Mark var1 = this.reader.getMark();
      StreamStartToken var2 = new StreamStartToken(var1, var1);
      this.addToken(var2);
   }

   private void fetchStreamEnd() {
      this.unwindIndent(-1);
      this.removePossibleSimpleKey();
      this.allowSimpleKey = false;
      this.possibleSimpleKeys.clear();
      Mark var1 = this.reader.getMark();
      StreamEndToken var2 = new StreamEndToken(var1, var1);
      this.addToken(var2);
      this.done = true;
   }

   private void fetchDirective() {
      this.unwindIndent(-1);
      this.removePossibleSimpleKey();
      this.allowSimpleKey = false;
      List var1 = this.scanDirective();
      this.addAllTokens(var1);
   }

   private void fetchDocumentStart() {
      this.fetchDocumentIndicator(true);
   }

   private void fetchDocumentEnd() {
      this.fetchDocumentIndicator(false);
   }

   private void fetchDocumentIndicator(boolean var1) {
      this.unwindIndent(-1);
      this.removePossibleSimpleKey();
      this.allowSimpleKey = false;
      Mark var2 = this.reader.getMark();
      this.reader.forward(3);
      Mark var3 = this.reader.getMark();
      Object var4;
      if (var1) {
         var4 = new DocumentStartToken(var2, var3);
      } else {
         var4 = new DocumentEndToken(var2, var3);
      }

      this.addToken((Token)var4);
   }

   private void fetchFlowSequenceStart() {
      this.fetchFlowCollectionStart(false);
   }

   private void fetchFlowMappingStart() {
      this.fetchFlowCollectionStart(true);
   }

   private void fetchFlowCollectionStart(boolean var1) {
      this.savePossibleSimpleKey();
      this.flowLevel++;
      this.allowSimpleKey = true;
      Mark var2 = this.reader.getMark();
      this.reader.forward(1);
      Mark var3 = this.reader.getMark();
      Object var4;
      if (var1) {
         var4 = new FlowMappingStartToken(var2, var3);
      } else {
         var4 = new FlowSequenceStartToken(var2, var3);
      }

      this.addToken((Token)var4);
   }

   private void fetchFlowSequenceEnd() {
      this.fetchFlowCollectionEnd(false);
   }

   private void fetchFlowMappingEnd() {
      this.fetchFlowCollectionEnd(true);
   }

   private void fetchFlowCollectionEnd(boolean var1) {
      this.removePossibleSimpleKey();
      this.flowLevel--;
      this.allowSimpleKey = false;
      Mark var2 = this.reader.getMark();
      this.reader.forward();
      Mark var3 = this.reader.getMark();
      Object var4;
      if (var1) {
         var4 = new FlowMappingEndToken(var2, var3);
      } else {
         var4 = new FlowSequenceEndToken(var2, var3);
      }

      this.addToken((Token)var4);
   }

   private void fetchFlowEntry() {
      this.allowSimpleKey = true;
      this.removePossibleSimpleKey();
      Mark var1 = this.reader.getMark();
      this.reader.forward();
      Mark var2 = this.reader.getMark();
      FlowEntryToken var3 = new FlowEntryToken(var1, var2);
      this.addToken(var3);
   }

   private void fetchBlockEntry() {
      if (this.flowLevel == 0) {
         if (!this.allowSimpleKey) {
            throw new ScannerException(null, null, "sequence entries are not allowed here", this.reader.getMark());
         }

         if (this.addIndent(this.reader.getColumn())) {
            Mark var1 = this.reader.getMark();
            this.addToken(new BlockSequenceStartToken(var1, var1));
         }
      }

      this.allowSimpleKey = true;
      this.removePossibleSimpleKey();
      Mark var4 = this.reader.getMark();
      this.reader.forward();
      Mark var2 = this.reader.getMark();
      BlockEntryToken var3 = new BlockEntryToken(var4, var2);
      this.addToken(var3);
   }

   private void fetchKey() {
      if (this.flowLevel == 0) {
         if (!this.allowSimpleKey) {
            throw new ScannerException(null, null, "mapping keys are not allowed here", this.reader.getMark());
         }

         if (this.addIndent(this.reader.getColumn())) {
            Mark var1 = this.reader.getMark();
            this.addToken(new BlockMappingStartToken(var1, var1));
         }
      }

      this.allowSimpleKey = this.flowLevel == 0;
      this.removePossibleSimpleKey();
      Mark var4 = this.reader.getMark();
      this.reader.forward();
      Mark var2 = this.reader.getMark();
      KeyToken var3 = new KeyToken(var4, var2);
      this.addToken(var3);
   }

   private void fetchValue() {
      SimpleKey var1 = this.possibleSimpleKeys.remove(this.flowLevel);
      if (var1 != null) {
         this.addToken(var1.getTokenNumber() - this.tokensTaken, new KeyToken(var1.getMark(), var1.getMark()));
         if (this.flowLevel == 0 && this.addIndent(var1.getColumn())) {
            this.addToken(var1.getTokenNumber() - this.tokensTaken, new BlockMappingStartToken(var1.getMark(), var1.getMark()));
         }

         this.allowSimpleKey = false;
      } else {
         if (this.flowLevel == 0 && !this.allowSimpleKey) {
            throw new ScannerException(null, null, "mapping values are not allowed here", this.reader.getMark());
         }

         if (this.flowLevel == 0 && this.addIndent(this.reader.getColumn())) {
            Mark var2 = this.reader.getMark();
            this.addToken(new BlockMappingStartToken(var2, var2));
         }

         this.allowSimpleKey = this.flowLevel == 0;
         this.removePossibleSimpleKey();
      }

      Mark var5 = this.reader.getMark();
      this.reader.forward();
      Mark var3 = this.reader.getMark();
      ValueToken var4 = new ValueToken(var5, var3);
      this.addToken(var4);
   }

   private void fetchAlias() {
      this.savePossibleSimpleKey();
      this.allowSimpleKey = false;
      Token var1 = this.scanAnchor(false);
      this.addToken(var1);
   }

   private void fetchAnchor() {
      this.savePossibleSimpleKey();
      this.allowSimpleKey = false;
      Token var1 = this.scanAnchor(true);
      this.addToken(var1);
   }

   private void fetchTag() {
      this.savePossibleSimpleKey();
      this.allowSimpleKey = false;
      Token var1 = this.scanTag();
      this.addToken(var1);
   }

   private void fetchLiteral() {
      this.fetchBlockScalar('|');
   }

   private void fetchFolded() {
      this.fetchBlockScalar('>');
   }

   private void fetchBlockScalar(char var1) {
      this.allowSimpleKey = true;
      this.removePossibleSimpleKey();
      List var2 = this.scanBlockScalar(var1);
      this.addAllTokens(var2);
   }

   private void fetchSingle() {
      this.fetchFlowScalar('\'');
   }

   private void fetchDouble() {
      this.fetchFlowScalar('"');
   }

   private void fetchFlowScalar(char var1) {
      this.savePossibleSimpleKey();
      this.allowSimpleKey = false;
      Token var2 = this.scanFlowScalar(var1);
      this.addToken(var2);
   }

   private void fetchPlain() {
      this.savePossibleSimpleKey();
      this.allowSimpleKey = false;
      Token var1 = this.scanPlain();
      this.addToken(var1);
   }

   private boolean checkDirective() {
      return this.reader.getColumn() == 0;
   }

   private boolean checkDocumentStart() {
      return this.reader.getColumn() == 0 && "---".equals(this.reader.prefix(3)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3));
   }

   private boolean checkDocumentEnd() {
      return this.reader.getColumn() == 0 && "...".equals(this.reader.prefix(3)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3));
   }

   private boolean checkBlockEntry() {
      return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
   }

   private boolean checkKey() {
      return this.flowLevel != 0 ? true : Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
   }

   private boolean checkValue() {
      return this.flowLevel != 0 ? true : Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
   }

   private boolean checkPlain() {
      int var1 = this.reader.peek();
      return Constant.NULL_BL_T_LINEBR.hasNo(var1, "-?:,[]{}#&*!|>'\"%@`")
         || Constant.NULL_BL_T_LINEBR.hasNo(this.reader.peek(1)) && (var1 == 45 || this.flowLevel == 0 && "?:".indexOf(var1) != -1);
   }

   private void scanToNextToken() {
      if (this.reader.getIndex() == 0 && this.reader.peek() == 65279) {
         this.reader.forward();
      }

      boolean var1 = false;
      int var2 = -1;

      while (!var1) {
         Mark var3 = this.reader.getMark();
         int var4 = this.reader.getColumn();
         boolean var5 = false;
         int var6 = 0;

         while (this.reader.peek(var6) == 32) {
            var6++;
         }

         if (var6 > 0) {
            this.reader.forward(var6);
         }

         if (this.reader.peek() == 35) {
            var5 = true;
            CommentType var7;
            if (var4 == 0 || this.lastToken != null && this.lastToken.getTokenId() == Token.ID.BlockEntry) {
               if (var2 == this.reader.getColumn()) {
                  var7 = CommentType.IN_LINE;
               } else {
                  var2 = -1;
                  var7 = CommentType.BLOCK;
               }
            } else {
               var7 = CommentType.IN_LINE;
               var2 = this.reader.getColumn();
            }

            CommentToken var8 = this.scanComment(var7);
            if (this.parseComments) {
               this.addToken(var8);
            }
         }

         String var9 = this.scanLineBreak();
         if (var9.length() != 0) {
            if (this.parseComments && !var5 && var4 == 0) {
               Mark var10 = this.reader.getMark();
               this.addToken(new CommentToken(CommentType.BLANK_LINE, var9, var3, var10));
            }

            if (this.flowLevel == 0) {
               this.allowSimpleKey = true;
            }
         } else {
            var1 = true;
         }
      }
   }

   private CommentToken scanComment(CommentType var1) {
      Mark var2 = this.reader.getMark();
      this.reader.forward();
      int var3 = 0;

      while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(var3))) {
         var3++;
      }

      String var4 = this.reader.prefixForward(var3);
      Mark var5 = this.reader.getMark();
      return new CommentToken(var1, var4, var2, var5);
   }

   private List<Token> scanDirective() {
      Mark var1 = this.reader.getMark();
      this.reader.forward();
      String var3 = this.scanDirectiveName(var1);
      List var4 = null;
      Mark var2;
      if ("YAML".equals(var3)) {
         var4 = this.scanYamlDirectiveValue(var1);
         var2 = this.reader.getMark();
      } else if ("TAG".equals(var3)) {
         var4 = this.scanTagDirectiveValue(var1);
         var2 = this.reader.getMark();
      } else {
         var2 = this.reader.getMark();
         int var5 = 0;

         while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(var5))) {
            var5++;
         }

         if (var5 > 0) {
            this.reader.forward(var5);
         }
      }

      CommentToken var7 = this.scanDirectiveIgnoredLine(var1);
      DirectiveToken var6 = new DirectiveToken(var3, var4, var1, var2);
      return this.makeTokenList(var6, var7);
   }

   private String scanDirectiveName(Mark var1) {
      int var2 = 0;
      int var3 = this.reader.peek(var2);

      while (Constant.ALPHA.has(var3)) {
         var3 = this.reader.peek(++var2);
      }

      if (var2 == 0) {
         String var7 = String.valueOf(Character.toChars(var3));
         throw new ScannerException(
            "while scanning a directive", var1, "expected alphabetic or numeric character, but found " + var7 + "(" + var3 + ")", this.reader.getMark()
         );
      } else {
         String var4 = this.reader.prefixForward(var2);
         var3 = this.reader.peek();
         if (Constant.NULL_BL_LINEBR.hasNo(var3)) {
            String var5 = String.valueOf(Character.toChars(var3));
            throw new ScannerException(
               "while scanning a directive", var1, "expected alphabetic or numeric character, but found " + var5 + "(" + var3 + ")", this.reader.getMark()
            );
         } else {
            return var4;
         }
      }
   }

   private List<Integer> scanYamlDirectiveValue(Mark var1) {
      while (this.reader.peek() == 32) {
         this.reader.forward();
      }

      Integer var2 = this.scanYamlDirectiveNumber(var1);
      int var3 = this.reader.peek();
      if (var3 != 46) {
         String var7 = String.valueOf(Character.toChars(var3));
         throw new ScannerException("while scanning a directive", var1, "expected a digit or '.', but found " + var7 + "(" + var3 + ")", this.reader.getMark());
      } else {
         this.reader.forward();
         Integer var4 = this.scanYamlDirectiveNumber(var1);
         var3 = this.reader.peek();
         if (Constant.NULL_BL_LINEBR.hasNo(var3)) {
            String var8 = String.valueOf(Character.toChars(var3));
            throw new ScannerException(
               "while scanning a directive", var1, "expected a digit or ' ', but found " + var8 + "(" + var3 + ")", this.reader.getMark()
            );
         } else {
            ArrayList var5 = new ArrayList(2);
            var5.add(var2);
            var5.add(var4);
            return var5;
         }
      }
   }

   private Integer scanYamlDirectiveNumber(Mark var1) {
      int var2 = this.reader.peek();
      if (!Character.isDigit(var2)) {
         String var5 = String.valueOf(Character.toChars(var2));
         throw new ScannerException("while scanning a directive", var1, "expected a digit, but found " + var5 + "(" + var2 + ")", this.reader.getMark());
      } else {
         int var3 = 0;

         while (Character.isDigit(this.reader.peek(var3))) {
            var3++;
         }

         return Integer.parseInt(this.reader.prefixForward(var3));
      }
   }

   private List<String> scanTagDirectiveValue(Mark var1) {
      while (this.reader.peek() == 32) {
         this.reader.forward();
      }

      String var2 = this.scanTagDirectiveHandle(var1);

      while (this.reader.peek() == 32) {
         this.reader.forward();
      }

      String var3 = this.scanTagDirectivePrefix(var1);
      ArrayList var4 = new ArrayList(2);
      var4.add(var2);
      var4.add(var3);
      return var4;
   }

   private String scanTagDirectiveHandle(Mark var1) {
      String var2 = this.scanTagHandle("directive", var1);
      int var3 = this.reader.peek();
      if (var3 != 32) {
         String var4 = String.valueOf(Character.toChars(var3));
         throw new ScannerException("while scanning a directive", var1, "expected ' ', but found " + var4 + "(" + var3 + ")", this.reader.getMark());
      } else {
         return var2;
      }
   }

   private String scanTagDirectivePrefix(Mark var1) {
      String var2 = this.scanTagUri("directive", var1);
      int var3 = this.reader.peek();
      if (Constant.NULL_BL_LINEBR.hasNo(var3)) {
         String var4 = String.valueOf(Character.toChars(var3));
         throw new ScannerException("while scanning a directive", var1, "expected ' ', but found " + var4 + "(" + var3 + ")", this.reader.getMark());
      } else {
         return var2;
      }
   }

   private CommentToken scanDirectiveIgnoredLine(Mark var1) {
      while (this.reader.peek() == 32) {
         this.reader.forward();
      }

      CommentToken var2 = null;
      if (this.reader.peek() == 35) {
         CommentToken var3 = this.scanComment(CommentType.IN_LINE);
         if (this.parseComments) {
            var2 = var3;
         }
      }

      int var6 = this.reader.peek();
      String var4 = this.scanLineBreak();
      if (var4.length() == 0 && var6 != 0) {
         String var5 = String.valueOf(Character.toChars(var6));
         throw new ScannerException(
            "while scanning a directive", var1, "expected a comment or a line break, but found " + var5 + "(" + var6 + ")", this.reader.getMark()
         );
      } else {
         return var2;
      }
   }

   private Token scanAnchor(boolean var1) {
      Mark var2 = this.reader.getMark();
      int var3 = this.reader.peek();
      String var4 = var3 == 42 ? "alias" : "anchor";
      this.reader.forward();
      int var5 = 0;
      int var6 = this.reader.peek(var5);

      while (Constant.NULL_BL_T_LINEBR.hasNo(var6, ":,[]{}/.*&")) {
         var6 = this.reader.peek(++var5);
      }

      if (var5 == 0) {
         String var11 = String.valueOf(Character.toChars(var6));
         throw new ScannerException("while scanning an " + var4, var2, "unexpected character found " + var11 + "(" + var6 + ")", this.reader.getMark());
      } else {
         String var7 = this.reader.prefixForward(var5);
         var6 = this.reader.peek();
         if (Constant.NULL_BL_T_LINEBR.hasNo(var6, "?:,]}%@`")) {
            String var12 = String.valueOf(Character.toChars(var6));
            throw new ScannerException("while scanning an " + var4, var2, "unexpected character found " + var12 + "(" + var6 + ")", this.reader.getMark());
         } else {
            Mark var8 = this.reader.getMark();
            Object var9;
            if (var1) {
               var9 = new AnchorToken(var7, var2, var8);
            } else {
               var9 = new AliasToken(var7, var2, var8);
            }

            return (Token)var9;
         }
      }
   }

   private Token scanTag() {
      Mark var1 = this.reader.getMark();
      int var2 = this.reader.peek(1);
      String var3 = null;
      Object var4 = null;
      if (var2 == 60) {
         this.reader.forward(2);
         var4 = this.scanTagUri("tag", var1);
         var2 = this.reader.peek();
         if (var2 != 62) {
            String var5 = String.valueOf(Character.toChars(var2));
            throw new ScannerException("while scanning a tag", var1, "expected '>', but found '" + var5 + "' (" + var2 + ")", this.reader.getMark());
         }

         this.reader.forward();
      } else if (Constant.NULL_BL_T_LINEBR.has(var2)) {
         var4 = "!";
         this.reader.forward();
      } else {
         int var10 = 1;

         boolean var6;
         for (var6 = false; Constant.NULL_BL_LINEBR.hasNo(var2); var2 = this.reader.peek(++var10)) {
            if (var2 == 33) {
               var6 = true;
               break;
            }
         }

         if (var6) {
            var3 = this.scanTagHandle("tag", var1);
         } else {
            var3 = "!";
            this.reader.forward();
         }

         var4 = this.scanTagUri("tag", var1);
      }

      var2 = this.reader.peek();
      if (Constant.NULL_BL_LINEBR.hasNo(var2)) {
         String var12 = String.valueOf(Character.toChars(var2));
         throw new ScannerException("while scanning a tag", var1, "expected ' ', but found '" + var12 + "' (" + var2 + ")", this.reader.getMark());
      } else {
         TagTuple var11 = new TagTuple(var3, (String)var4);
         Mark var13 = this.reader.getMark();
         return new TagToken(var11, var1, var13);
      }
   }

   private List<Token> scanBlockScalar(char var1) {
      boolean var2;
      if (var1 == '>') {
         var2 = true;
      } else {
         var2 = false;
      }

      StringBuilder var3 = new StringBuilder();
      Mark var4 = this.reader.getMark();
      this.reader.forward();
      ScannerImpl.Chomping var5 = this.scanBlockScalarIndicators(var4);
      int var6 = var5.getIncrement();
      CommentToken var7 = this.scanBlockScalarIgnoredLine(var4);
      int var8 = this.indent + 1;
      if (var8 < 1) {
         var8 = 1;
      }

      String var9;
      int var11;
      Mark var12;
      if (var6 == -1) {
         Object[] var13 = this.scanBlockScalarIndentation();
         var9 = (String)var13[0];
         int var10 = (Integer)var13[1];
         var12 = (Mark)var13[2];
         var11 = Math.max(var8, var10);
      } else {
         var11 = var8 + var6 - 1;
         Object[] var17 = this.scanBlockScalarBreaks(var11);
         var9 = (String)var17[0];
         var12 = (Mark)var17[1];
      }

      String var18 = "";

      while (this.reader.getColumn() == var11 && this.reader.peek() != 0) {
         var3.append(var9);
         boolean var14 = " \t".indexOf(this.reader.peek()) == -1;
         int var15 = 0;

         while (Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(var15))) {
            var15++;
         }

         var3.append(this.reader.prefixForward(var15));
         var18 = this.scanLineBreak();
         Object[] var16 = this.scanBlockScalarBreaks(var11);
         var9 = (String)var16[0];
         var12 = (Mark)var16[1];
         if (this.reader.getColumn() != var11 || this.reader.peek() == 0) {
            break;
         }

         if (!var2 || !"\n".equals(var18) || !var14 || " \t".indexOf(this.reader.peek()) != -1) {
            var3.append(var18);
         } else if (var9.length() == 0) {
            var3.append(" ");
         }
      }

      if (var5.chompTailIsNotFalse()) {
         var3.append(var18);
      }

      CommentToken var19 = null;
      if (var5.chompTailIsTrue()) {
         if (this.parseComments) {
            var19 = new CommentToken(CommentType.BLANK_LINE, var9, var4, var12);
         }

         var3.append(var9);
      }

      ScalarToken var20 = new ScalarToken(var3.toString(), false, var4, var12, DumperOptions.ScalarStyle.createStyle(var1));
      return this.makeTokenList(var7, var20, var19);
   }

   private ScannerImpl.Chomping scanBlockScalarIndicators(Mark var1) {
      Boolean var2 = null;
      int var3 = -1;
      int var4 = this.reader.peek();
      if (var4 != 45 && var4 != 43) {
         if (Character.isDigit(var4)) {
            String var9 = String.valueOf(Character.toChars(var4));
            var3 = Integer.parseInt(var9);
            if (var3 == 0) {
               throw new ScannerException(
                  "while scanning a block scalar", var1, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark()
               );
            }

            this.reader.forward();
            var4 = this.reader.peek();
            if (var4 == 45 || var4 == 43) {
               if (var4 == 43) {
                  var2 = Boolean.TRUE;
               } else {
                  var2 = Boolean.FALSE;
               }

               this.reader.forward();
            }
         }
      } else {
         if (var4 == 43) {
            var2 = Boolean.TRUE;
         } else {
            var2 = Boolean.FALSE;
         }

         this.reader.forward();
         var4 = this.reader.peek();
         if (Character.isDigit(var4)) {
            String var5 = String.valueOf(Character.toChars(var4));
            var3 = Integer.parseInt(var5);
            if (var3 == 0) {
               throw new ScannerException(
                  "while scanning a block scalar", var1, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark()
               );
            }

            this.reader.forward();
         }
      }

      var4 = this.reader.peek();
      if (Constant.NULL_BL_LINEBR.hasNo(var4)) {
         String var10 = String.valueOf(Character.toChars(var4));
         throw new ScannerException(
            "while scanning a block scalar", var1, "expected chomping or indentation indicators, but found " + var10 + "(" + var4 + ")", this.reader.getMark()
         );
      } else {
         return new ScannerImpl.Chomping(var2, var3);
      }
   }

   private CommentToken scanBlockScalarIgnoredLine(Mark var1) {
      while (this.reader.peek() == 32) {
         this.reader.forward();
      }

      CommentToken var2 = null;
      if (this.reader.peek() == 35) {
         var2 = this.scanComment(CommentType.IN_LINE);
      }

      int var3 = this.reader.peek();
      String var4 = this.scanLineBreak();
      if (var4.length() == 0 && var3 != 0) {
         String var5 = String.valueOf(Character.toChars(var3));
         throw new ScannerException(
            "while scanning a block scalar", var1, "expected a comment or a line break, but found " + var5 + "(" + var3 + ")", this.reader.getMark()
         );
      } else {
         return var2;
      }
   }

   private Object[] scanBlockScalarIndentation() {
      StringBuilder var1 = new StringBuilder();
      int var2 = 0;
      Mark var3 = this.reader.getMark();

      while (Constant.LINEBR.has(this.reader.peek(), " \r")) {
         if (this.reader.peek() != 32) {
            var1.append(this.scanLineBreak());
            var3 = this.reader.getMark();
         } else {
            this.reader.forward();
            if (this.reader.getColumn() > var2) {
               var2 = this.reader.getColumn();
            }
         }
      }

      return new Object[]{var1.toString(), var2, var3};
   }

   private Object[] scanBlockScalarBreaks(int var1) {
      StringBuilder var2 = new StringBuilder();
      Mark var3 = this.reader.getMark();

      for (int var4 = this.reader.getColumn(); var4 < var1 && this.reader.peek() == 32; var4++) {
         this.reader.forward();
      }

      Object var5 = null;

      while ((var5 = this.scanLineBreak()).length() != 0) {
         var2.append((String)var5);
         var3 = this.reader.getMark();

         for (int var6 = this.reader.getColumn(); var6 < var1 && this.reader.peek() == 32; var6++) {
            this.reader.forward();
         }
      }

      return new Object[]{var2.toString(), var3};
   }

   private Token scanFlowScalar(char var1) {
      boolean var2;
      if (var1 == '"') {
         var2 = true;
      } else {
         var2 = false;
      }

      StringBuilder var3 = new StringBuilder();
      Mark var4 = this.reader.getMark();
      int var5 = this.reader.peek();
      this.reader.forward();
      var3.append(this.scanFlowScalarNonSpaces(var2, var4));

      while (this.reader.peek() != var5) {
         var3.append(this.scanFlowScalarSpaces(var4));
         var3.append(this.scanFlowScalarNonSpaces(var2, var4));
      }

      this.reader.forward();
      Mark var6 = this.reader.getMark();
      return new ScalarToken(var3.toString(), false, var4, var6, DumperOptions.ScalarStyle.createStyle(var1));
   }

   private String scanFlowScalarNonSpaces(boolean var1, Mark var2) {
      StringBuilder var3 = new StringBuilder();

      while (true) {
         int var4 = 0;

         while (Constant.NULL_BL_T_LINEBR.hasNo(this.reader.peek(var4), "'\"\\")) {
            var4++;
         }

         if (var4 != 0) {
            var3.append(this.reader.prefixForward(var4));
         }

         int var5 = this.reader.peek();
         if (!var1 && var5 == 39 && this.reader.peek(1) == 39) {
            var3.append("'");
            this.reader.forward(2);
         } else if ((!var1 || var5 != 39) && (var1 || "\"\\".indexOf(var5) == -1)) {
            if (!var1 || var5 != 92) {
               return var3.toString();
            }

            this.reader.forward();
            var5 = this.reader.peek();
            if (!Character.isSupplementaryCodePoint(var5) && ESCAPE_REPLACEMENTS.containsKey((char)var5)) {
               var3.append(ESCAPE_REPLACEMENTS.get((char)var5));
               this.reader.forward();
            } else if (!Character.isSupplementaryCodePoint(var5) && ESCAPE_CODES.containsKey((char)var5)) {
               var4 = ESCAPE_CODES.get((char)var5);
               this.reader.forward();
               String var11 = this.reader.prefix(var4);
               if (NOT_HEXA.matcher(var11).find()) {
                  throw new ScannerException(
                     "while scanning a double-quoted scalar",
                     var2,
                     "expected escape sequence of " + var4 + " hexadecimal numbers, but found: " + var11,
                     this.reader.getMark()
                  );
               }

               int var7 = Integer.parseInt(var11, 16);
               String var8 = new String(Character.toChars(var7));
               var3.append(var8);
               this.reader.forward(var4);
            } else {
               if (this.scanLineBreak().length() == 0) {
                  String var6 = String.valueOf(Character.toChars(var5));
                  throw new ScannerException(
                     "while scanning a double-quoted scalar", var2, "found unknown escape character " + var6 + "(" + var5 + ")", this.reader.getMark()
                  );
               }

               var3.append(this.scanFlowScalarBreaks(var2));
            }
         } else {
            var3.appendCodePoint(var5);
            this.reader.forward();
         }
      }
   }

   private String scanFlowScalarSpaces(Mark var1) {
      StringBuilder var2 = new StringBuilder();
      int var3 = 0;

      while (" \t".indexOf(this.reader.peek(var3)) != -1) {
         var3++;
      }

      String var4 = this.reader.prefixForward(var3);
      int var5 = this.reader.peek();
      if (var5 == 0) {
         throw new ScannerException("while scanning a quoted scalar", var1, "found unexpected end of stream", this.reader.getMark());
      } else {
         String var6 = this.scanLineBreak();
         if (var6.length() != 0) {
            String var7 = this.scanFlowScalarBreaks(var1);
            if (!"\n".equals(var6)) {
               var2.append(var6);
            } else if (var7.length() == 0) {
               var2.append(" ");
            }

            var2.append(var7);
         } else {
            var2.append(var4);
         }

         return var2.toString();
      }
   }

   private String scanFlowScalarBreaks(Mark var1) {
      StringBuilder var2 = new StringBuilder();

      while (true) {
         String var3 = this.reader.prefix(3);
         if (("---".equals(var3) || "...".equals(var3)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3))) {
            throw new ScannerException("while scanning a quoted scalar", var1, "found unexpected document separator", this.reader.getMark());
         }

         while (" \t".indexOf(this.reader.peek()) != -1) {
            this.reader.forward();
         }

         String var4 = this.scanLineBreak();
         if (var4.length() == 0) {
            return var2.toString();
         }

         var2.append(var4);
      }
   }

   private Token scanPlain() {
      StringBuilder var1 = new StringBuilder();
      Mark var2 = this.reader.getMark();
      Mark var3 = var2;
      int var4 = this.indent + 1;
      String var5 = "";

      while (true) {
         int var7 = 0;
         if (this.reader.peek() == 35) {
            return new ScalarToken(var1.toString(), var2, var3, true);
         }

         while (true) {
            int var6 = this.reader.peek(var7);
            if (Constant.NULL_BL_T_LINEBR.has(var6)
               || var6 == 58 && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(var7 + 1), this.flowLevel != 0 ? ",[]{}" : "")
               || this.flowLevel != 0 && ",?[]{}".indexOf(var6) != -1) {
               if (var7 == 0) {
                  return new ScalarToken(var1.toString(), var2, var3, true);
               }

               this.allowSimpleKey = false;
               var1.append(var5);
               var1.append(this.reader.prefixForward(var7));
               var3 = this.reader.getMark();
               var5 = this.scanPlainSpaces();
               if (var5.length() == 0 || this.reader.peek() == 35 || this.flowLevel == 0 && this.reader.getColumn() < var4) {
                  return new ScalarToken(var1.toString(), var2, var3, true);
               }
               break;
            }

            var7++;
         }
      }
   }

   private boolean atEndOfPlain() {
      int var1 = 0;
      int var2 = this.reader.getColumn();

      int var3;
      while ((var3 = this.reader.peek(var1)) != 0 && Constant.NULL_BL_T_LINEBR.has(var3)) {
         var1++;
         if (!Constant.LINEBR.has(var3) && (var3 != 13 || this.reader.peek(var1 + 1) != 10) && var3 != 65279) {
            var2++;
         } else {
            var2 = 0;
         }
      }

      if (this.reader.peek(var1) != 35 && this.reader.peek(var1 + 1) != 0 && (this.flowLevel != 0 || var2 >= this.indent)) {
         if (this.flowLevel == 0) {
            for (int var4 = 1; (var3 = this.reader.peek(var1 + var4)) != 0 && !Constant.NULL_BL_T_LINEBR.has(var3); var4++) {
               if (var3 == 58 && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(var1 + var4 + 1))) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private String scanPlainSpaces() {
      int var1 = 0;

      while (this.reader.peek(var1) == 32 || this.reader.peek(var1) == 9) {
         var1++;
      }

      String var2 = this.reader.prefixForward(var1);
      Object var3 = this.scanLineBreak();
      if (var3.length() == 0) {
         return var2;
      } else {
         this.allowSimpleKey = true;
         String var4 = this.reader.prefix(3);
         if ("---".equals(var4) || "...".equals(var4) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3))) {
            return "";
         } else if (this.parseComments && this.atEndOfPlain()) {
            return "";
         } else {
            StringBuilder var5 = new StringBuilder();

            do {
               while (this.reader.peek() == 32) {
                  this.reader.forward();
               }

               String var6 = this.scanLineBreak();
               if (var6.length() == 0) {
                  if (!"\n".equals(var3)) {
                     return var3 + var5;
                  }

                  if (var5.length() == 0) {
                     return " ";
                  }

                  return var5.toString();
               }

               var5.append(var6);
               var4 = this.reader.prefix(3);
            } while (!"---".equals(var4) && (!"...".equals(var4) || !Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3))));

            return "";
         }
      }
   }

   private String scanTagHandle(String var1, Mark var2) {
      int var3 = this.reader.peek();
      if (var3 != 33) {
         String var7 = String.valueOf(Character.toChars(var3));
         throw new ScannerException("while scanning a " + var1, var2, "expected '!', but found " + var7 + "(" + var3 + ")", this.reader.getMark());
      } else {
         int var4 = 1;
         var3 = this.reader.peek(var4);
         if (var3 != 32) {
            while (Constant.ALPHA.has(var3)) {
               var3 = this.reader.peek(++var4);
            }

            if (var3 != 33) {
               this.reader.forward(var4);
               String var5 = String.valueOf(Character.toChars(var3));
               throw new ScannerException("while scanning a " + var1, var2, "expected '!', but found " + var5 + "(" + var3 + ")", this.reader.getMark());
            }

            var4++;
         }

         return this.reader.prefixForward(var4);
      }
   }

   private String scanTagUri(String var1, Mark var2) {
      StringBuilder var3 = new StringBuilder();
      int var4 = 0;

      int var5;
      for (var5 = this.reader.peek(var4); Constant.URI_CHARS.has(var5); var5 = this.reader.peek(var4)) {
         if (var5 == 37) {
            var3.append(this.reader.prefixForward(var4));
            var4 = 0;
            var3.append(this.scanUriEscapes(var1, var2));
         } else {
            var4++;
         }
      }

      if (var4 != 0) {
         var3.append(this.reader.prefixForward(var4));
      }

      if (var3.length() == 0) {
         String var6 = String.valueOf(Character.toChars(var5));
         throw new ScannerException("while scanning a " + var1, var2, "expected URI, but found " + var6 + "(" + var5 + ")", this.reader.getMark());
      } else {
         return var3.toString();
      }
   }

   private String scanUriEscapes(String var1, Mark var2) {
      int var3 = 1;

      while (this.reader.peek(var3 * 3) == 37) {
         var3++;
      }

      Mark var4 = this.reader.getMark();

      ByteBuffer var5;
      for (var5 = ByteBuffer.allocate(var3); this.reader.peek() == 37; this.reader.forward(2)) {
         this.reader.forward();

         try {
            byte var6 = (byte)Integer.parseInt(this.reader.prefix(2), 16);
            var5.put(var6);
         } catch (NumberFormatException var12) {
            int var7 = this.reader.peek();
            String var8 = String.valueOf(Character.toChars(var7));
            int var9 = this.reader.peek(1);
            String var10 = String.valueOf(Character.toChars(var9));
            throw new ScannerException(
               "while scanning a " + var1,
               var2,
               "expected URI escape sequence of 2 hexadecimal numbers, but found " + var8 + "(" + var7 + ") and " + var10 + "(" + var9 + ")",
               this.reader.getMark()
            );
         }
      }

      ((Buffer)var5).flip();

      try {
         return UriEncoder.decode(var5);
      } catch (CharacterCodingException var11) {
         throw new ScannerException("while scanning a " + var1, var2, "expected URI in UTF-8: " + var11.getMessage(), var4);
      }
   }

   private String scanLineBreak() {
      int var1 = this.reader.peek();
      if (var1 != 13 && var1 != 10 && var1 != 133) {
         if (var1 != 8232 && var1 != 8233) {
            return "";
         } else {
            this.reader.forward();
            return String.valueOf(Character.toChars(var1));
         }
      } else {
         if (var1 == 13 && 10 == this.reader.peek(1)) {
            this.reader.forward(2);
         } else {
            this.reader.forward();
         }

         return "\n";
      }
   }

   private List<Token> makeTokenList(Token... var1) {
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < var1.length; var3++) {
         if (var1[var3] != null && (this.parseComments || !(var1[var3] instanceof CommentToken))) {
            var2.add(var1[var3]);
         }
      }

      return var2;
   }

   static {
      ESCAPE_REPLACEMENTS.put('0', "\u0000");
      ESCAPE_REPLACEMENTS.put('a', "\u0007");
      ESCAPE_REPLACEMENTS.put('b', "\b");
      ESCAPE_REPLACEMENTS.put('t', "\t");
      ESCAPE_REPLACEMENTS.put('n', "\n");
      ESCAPE_REPLACEMENTS.put('v', "\u000b");
      ESCAPE_REPLACEMENTS.put('f', "\f");
      ESCAPE_REPLACEMENTS.put('r', "\r");
      ESCAPE_REPLACEMENTS.put('e', "\u001b");
      ESCAPE_REPLACEMENTS.put(' ', " ");
      ESCAPE_REPLACEMENTS.put('"', "\"");
      ESCAPE_REPLACEMENTS.put('\\', "\\");
      ESCAPE_REPLACEMENTS.put('N', "\u0085");
      ESCAPE_REPLACEMENTS.put('_', " ");
      ESCAPE_REPLACEMENTS.put('L', "\u2028");
      ESCAPE_REPLACEMENTS.put('P', "\u2029");
      ESCAPE_CODES.put('x', 2);
      ESCAPE_CODES.put('u', 4);
      ESCAPE_CODES.put('U', 8);
   }

   private static class Chomping {
      private final Boolean value;
      private final int increment;

      public Chomping(Boolean var1, int var2) {
         this.value = var1;
         this.increment = var2;
      }

      public boolean chompTailIsNotFalse() {
         return this.value == null || this.value;
      }

      public boolean chompTailIsTrue() {
         return this.value != null && this.value;
      }

      public int getIncrement() {
         return this.increment;
      }
   }
}
