package org.jline.reader.impl;

import java.io.Flushable;
import java.io.IOError;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterators;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.Buffer;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.CompletingParsedLine;
import org.jline.reader.EOFError;
import org.jline.reader.EndOfFileException;
import org.jline.reader.Expander;
import org.jline.reader.Highlighter;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.Macro;
import org.jline.reader.MaskingCallback;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.Reference;
import org.jline.reader.SyntaxError;
import org.jline.reader.UserInterruptException;
import org.jline.reader.Widget;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Curses;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp;
import org.jline.utils.Levenshtein;
import org.jline.utils.Log;
import org.jline.utils.Status;
import org.jline.utils.WCWidth;

public class LineReaderImpl implements Flushable, LineReader {
   public static final char NULL_MASK = '\u0000';
   public static final int TAB_WIDTH = 4;
   public static final String DEFAULT_WORDCHARS = "*?_-.[]~=/&;!#$%^(){}<>";
   public static final String DEFAULT_REMOVE_SUFFIX_CHARS = " \t\n;&|";
   public static final String DEFAULT_COMMENT_BEGIN = "#";
   public static final String DEFAULT_SEARCH_TERMINATORS = "\u001b\n";
   public static final String DEFAULT_BELL_STYLE = "";
   public static final int DEFAULT_LIST_MAX = 100;
   public static final int DEFAULT_ERRORS = 2;
   public static final long DEFAULT_BLINK_MATCHING_PAREN = 500L;
   public static final long DEFAULT_AMBIGUOUS_BINDING = 1000L;
   public static final String DEFAULT_SECONDARY_PROMPT_PATTERN = "%M> ";
   public static final String DEFAULT_OTHERS_GROUP_NAME = "others";
   public static final String DEFAULT_ORIGINAL_GROUP_NAME = "original";
   public static final String DEFAULT_COMPLETION_STYLE_STARTING = "36";
   public static final String DEFAULT_COMPLETION_STYLE_DESCRIPTION = "90";
   public static final String DEFAULT_COMPLETION_STYLE_GROUP = "35;1";
   public static final String DEFAULT_COMPLETION_STYLE_SELECTION = "7";
   private static final int MIN_ROWS = 3;
   public static final String BRACKETED_PASTE_ON = "\u001b[?2004h";
   public static final String BRACKETED_PASTE_OFF = "\u001b[?2004l";
   public static final String BRACKETED_PASTE_BEGIN = "\u001b[200~";
   public static final String BRACKETED_PASTE_END = "\u001b[201~";
   public static final String FOCUS_IN_SEQ = "\u001b[I";
   public static final String FOCUS_OUT_SEQ = "\u001b[O";
   protected final Terminal terminal;
   protected final String appName;
   protected final Map<String, KeyMap<Binding>> keyMaps;
   protected final Map<String, Object> variables;
   protected History history = new DefaultHistory();
   protected Completer completer = null;
   protected Highlighter highlighter = new DefaultHighlighter();
   protected Parser parser = new DefaultParser();
   protected Expander expander = new DefaultExpander();
   protected final Map<LineReader.Option, Boolean> options = new HashMap<>();
   protected final Buffer buf = new BufferImpl();
   protected final Size size = new Size();
   protected AttributedString prompt = AttributedString.EMPTY;
   protected AttributedString rightPrompt = AttributedString.EMPTY;
   protected MaskingCallback maskingCallback;
   protected Map<Integer, String> modifiedHistory = new HashMap<>();
   protected Buffer historyBuffer = null;
   protected CharSequence searchBuffer;
   protected StringBuffer searchTerm = null;
   protected boolean searchFailing;
   protected boolean searchBackward;
   protected int searchIndex = -1;
   protected final BindingReader bindingReader;
   protected int findChar;
   protected int findDir;
   protected int findTailAdd;
   private int searchDir;
   private String searchString;
   protected int regionMark;
   protected LineReader.RegionType regionActive;
   private boolean forceChar;
   private boolean forceLine;
   protected String yankBuffer = "";
   protected LineReaderImpl.ViMoveMode viMoveMode = LineReaderImpl.ViMoveMode.NORMAL;
   protected KillRing killRing = new KillRing();
   protected UndoTree<Buffer> undo = new UndoTree<>(this::setBuffer);
   protected boolean isUndo;
   protected final ReentrantLock lock = new ReentrantLock();
   protected LineReaderImpl.State state = LineReaderImpl.State.DONE;
   protected final AtomicBoolean startedReading = new AtomicBoolean();
   protected boolean reading;
   protected Supplier<AttributedString> post;
   protected Map<String, Widget> builtinWidgets;
   protected Map<String, Widget> widgets;
   protected int count;
   protected int mult;
   protected int universal = 4;
   protected int repeatCount;
   protected boolean isArgDigit;
   protected ParsedLine parsedLine;
   protected boolean skipRedisplay;
   protected Display display;
   protected boolean overTyping = false;
   protected String keyMap;
   protected int smallTerminalOffset = 0;
   protected boolean nextCommandFromHistory = false;
   protected int nextHistoryId = -1;
   private static final String DESC_PREFIX = "(";
   private static final String DESC_SUFFIX = ")";
   private static final int MARGIN_BETWEEN_DISPLAY_AND_DESC = 1;
   private static final int MARGIN_BETWEEN_COLUMNS = 3;

   public LineReaderImpl(Terminal var1) throws IOException {
      this(var1, null, null);
   }

   public LineReaderImpl(Terminal var1, String var2) throws IOException {
      this(var1, var2, null);
   }

   public LineReaderImpl(Terminal var1, String var2, Map<String, Object> var3) {
      Objects.requireNonNull(var1, "terminal can not be null");
      this.terminal = var1;
      if (var2 == null) {
         var2 = "JLine";
      }

      this.appName = var2;
      if (var3 != null) {
         this.variables = var3;
      } else {
         this.variables = new HashMap<>();
      }

      this.keyMaps = this.defaultKeyMaps();
      this.builtinWidgets = this.builtinWidgets();
      this.widgets = new HashMap<>(this.builtinWidgets);
      this.bindingReader = new BindingReader(var1.reader());
      this.doDisplay();
   }

   @Override
   public Terminal getTerminal() {
      return this.terminal;
   }

   @Override
   public String getAppName() {
      return this.appName;
   }

   @Override
   public Map<String, KeyMap<Binding>> getKeyMaps() {
      return this.keyMaps;
   }

   @Override
   public KeyMap<Binding> getKeys() {
      return this.keyMaps.get(this.keyMap);
   }

   @Override
   public Map<String, Widget> getWidgets() {
      return this.widgets;
   }

   @Override
   public Map<String, Widget> getBuiltinWidgets() {
      return Collections.unmodifiableMap(this.builtinWidgets);
   }

   @Override
   public Buffer getBuffer() {
      return this.buf;
   }

   @Override
   public void runMacro(String var1) {
      this.bindingReader.runMacro(var1);
   }

   @Override
   public MouseEvent readMouseEvent() {
      return this.terminal.readMouseEvent(this.bindingReader::readCharacter);
   }

   public void setCompleter(Completer var1) {
      this.completer = var1;
   }

   public Completer getCompleter() {
      return this.completer;
   }

   public void setHistory(History var1) {
      Objects.requireNonNull(var1);
      this.history = var1;
   }

   @Override
   public History getHistory() {
      return this.history;
   }

   public void setHighlighter(Highlighter var1) {
      this.highlighter = var1;
   }

   @Override
   public Highlighter getHighlighter() {
      return this.highlighter;
   }

   @Override
   public Parser getParser() {
      return this.parser;
   }

   public void setParser(Parser var1) {
      this.parser = var1;
   }

   @Override
   public Expander getExpander() {
      return this.expander;
   }

   public void setExpander(Expander var1) {
      this.expander = var1;
   }

   @Override
   public String readLine() throws UserInterruptException, EndOfFileException {
      return this.readLine(null, null, (MaskingCallback)null, null);
   }

   @Override
   public String readLine(Character var1) throws UserInterruptException, EndOfFileException {
      return this.readLine(null, null, var1, null);
   }

   @Override
   public String readLine(String var1) throws UserInterruptException, EndOfFileException {
      return this.readLine(var1, null, (MaskingCallback)null, null);
   }

   @Override
   public String readLine(String var1, Character var2) throws UserInterruptException, EndOfFileException {
      return this.readLine(var1, null, var2, null);
   }

   @Override
   public String readLine(String var1, Character var2, String var3) throws UserInterruptException, EndOfFileException {
      return this.readLine(var1, null, var2, var3);
   }

   @Override
   public String readLine(String var1, String var2, Character var3, String var4) throws UserInterruptException, EndOfFileException {
      return this.readLine(var1, var2, var3 != null ? new SimpleMaskingCallback(var3) : null, var4);
   }

   @Override
   public String readLine(String var1, String var2, MaskingCallback var3, String var4) throws UserInterruptException, EndOfFileException {
      if (!this.startedReading.compareAndSet(false, true)) {
         throw new IllegalStateException();
      } else {
         Thread var5 = Thread.currentThread();
         Terminal.SignalHandler var6 = null;
         Terminal.SignalHandler var7 = null;
         Terminal.SignalHandler var8 = null;
         Attributes var9 = null;
         boolean var10 = this.isTerminalDumb();

         try {
            this.maskingCallback = var3;
            this.repeatCount = 0;
            this.mult = 1;
            this.regionActive = LineReader.RegionType.NONE;
            this.regionMark = -1;
            this.smallTerminalOffset = 0;
            this.state = LineReaderImpl.State.NORMAL;
            this.modifiedHistory.clear();
            this.setPrompt(var1);
            this.setRightPrompt(var2);
            this.buf.clear();
            if (var4 != null) {
               this.buf.write(var4);
            }

            if (this.nextCommandFromHistory && this.nextHistoryId > 0) {
               if (this.history.size() > this.nextHistoryId) {
                  this.history.moveTo(this.nextHistoryId);
               } else {
                  this.history.moveTo(this.history.last());
               }

               this.buf.write(this.history.current());
            } else {
               this.nextHistoryId = -1;
            }

            this.nextCommandFromHistory = false;
            this.undo.clear();
            this.parsedLine = null;
            this.keyMap = "main";
            if (this.history != null) {
               this.history.attach(this);
            }

            try {
               this.lock.lock();
               this.reading = true;
               var6 = this.terminal.handle(Terminal.Signal.INT, var1x -> var5.interrupt());
               var7 = this.terminal.handle(Terminal.Signal.WINCH, this::handleSignal);
               var8 = this.terminal.handle(Terminal.Signal.CONT, this::handleSignal);
               var9 = this.terminal.enterRawMode();
               this.doDisplay();
               if (!var10) {
                  this.terminal.puts(InfoCmp.Capability.keypad_xmit);
                  if (this.isSet(LineReader.Option.AUTO_FRESH_LINE)) {
                     this.callWidget("fresh-line");
                  }

                  if (this.isSet(LineReader.Option.MOUSE)) {
                     this.terminal.trackMouse(Terminal.MouseTracking.Normal);
                  }

                  if (this.isSet(LineReader.Option.BRACKETED_PASTE)) {
                     this.terminal.writer().write("\u001b[?2004h");
                  }
               } else {
                  Attributes var11 = new Attributes(var9);
                  var11.setInputFlag(Attributes.InputFlag.IGNCR, true);
                  this.terminal.setAttributes(var11);
               }

               this.callWidget("callback-init");
               this.undo.newState(this.buf.copy());
               this.redrawLine();
               this.redisplay();
            } finally {
               this.lock.unlock();
            }

            while (true) {
               KeyMap var53 = null;
               if (this.isInViCmdMode() && this.regionActive != LineReader.RegionType.NONE) {
                  var53 = this.keyMaps.get("visual");
               }

               Binding var12 = this.readBinding(this.getKeys(), var53);
               if (var12 == null) {
                  throw new EndOfFileException();
               }

               Log.trace("Binding: ", var12);
               if (this.buf.length() == 0 && this.getLastBinding().charAt(0) == var9.getControlChar(Attributes.ControlChar.VEOF)) {
                  throw new EndOfFileException();
               }

               this.isArgDigit = false;
               this.count = (this.repeatCount == 0 ? 1 : this.repeatCount) * this.mult;
               this.isUndo = false;
               if (this.regionActive == LineReader.RegionType.PASTE) {
                  this.regionActive = LineReader.RegionType.NONE;
               }

               try {
                  this.lock.lock();
                  Buffer var13 = this.buf.copy();
                  Widget var14 = this.getWidget(var12);
                  if (!var14.apply()) {
                     this.beep();
                  }

                  if (!this.isUndo && !var13.toString().equals(this.buf.toString())) {
                     this.undo.newState(this.buf.copy());
                  }

                  switch (this.state) {
                     case DONE:
                        return this.finishBuffer();
                     case EOF:
                        throw new EndOfFileException();
                     case INTERRUPT:
                        throw new UserInterruptException(this.buf.toString());
                     default:
                        if (!this.isArgDigit) {
                           this.repeatCount = 0;
                           this.mult = 1;
                        }

                        if (!var10) {
                           this.redisplay();
                        }
                  }
               } finally {
                  this.lock.unlock();
               }
            }
         } catch (IOError var51) {
            if (var51.getCause() instanceof InterruptedIOException) {
               throw new UserInterruptException(this.buf.toString());
            } else {
               throw var51;
            }
         } finally {
            try {
               this.lock.lock();
               this.reading = false;
               this.cleanup();
               if (var9 != null) {
                  this.terminal.setAttributes(var9);
               }

               if (var6 != null) {
                  this.terminal.handle(Terminal.Signal.INT, var6);
               }

               if (var7 != null) {
                  this.terminal.handle(Terminal.Signal.WINCH, var7);
               }

               if (var8 != null) {
                  this.terminal.handle(Terminal.Signal.CONT, var8);
               }
            } finally {
               this.lock.unlock();
            }

            this.startedReading.set(false);
         }
      }
   }

   private boolean isTerminalDumb() {
      return "dumb".equals(this.terminal.getType()) || "dumb-color".equals(this.terminal.getType());
   }

   private void doDisplay() {
      this.size.copy(this.terminal.getBufferSize());
      this.display = new Display(this.terminal, false);
      if (this.size.getRows() != 0 && this.size.getColumns() != 0) {
         this.display.resize(this.size.getRows(), this.size.getColumns());
      } else {
         this.display.resize(1, Integer.MAX_VALUE);
      }

      if (this.isSet(LineReader.Option.DELAY_LINE_WRAP)) {
         this.display.setDelayLineWrap(true);
      }
   }

   @Override
   public void printAbove(String var1) {
      try {
         this.lock.lock();
         boolean var2 = this.reading;
         if (var2) {
            this.display.update(Collections.emptyList(), 0);
         }

         if (!var1.endsWith("\n") && !var1.endsWith("\n\u001b[m") && !var1.endsWith("\n\u001b[0m")) {
            this.terminal.writer().println(var1);
         } else {
            this.terminal.writer().print(var1);
         }

         if (var2) {
            this.redisplay(false);
         }

         this.terminal.flush();
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public void printAbove(AttributedString var1) {
      this.printAbove(var1.toAnsi(this.terminal));
   }

   @Override
   public boolean isReading() {
      boolean var1;
      try {
         this.lock.lock();
         var1 = this.reading;
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   protected boolean freshLine() {
      boolean var1 = this.terminal.getBooleanCapability(InfoCmp.Capability.auto_right_margin);
      boolean var2 = var1 && this.terminal.getBooleanCapability(InfoCmp.Capability.eat_newline_glitch);
      AttributedStringBuilder var3 = new AttributedStringBuilder();
      var3.style(AttributedStyle.DEFAULT.foreground(8));
      var3.append("~");
      var3.style(AttributedStyle.DEFAULT);
      if (var1 && !var2) {
         String var6 = this.terminal.getStringCapability(InfoCmp.Capability.clr_eol);
         if (var6 != null) {
            Curses.tputs(var3, var6);
         }

         for (int var5 = 0; var5 < this.size.getColumns() - 2; var5++) {
            var3.append(" ");
         }

         var3.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
         var3.append(" ");
         var3.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
      } else {
         for (int var4 = 0; var4 < this.size.getColumns() - 1; var4++) {
            var3.append(" ");
         }

         var3.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
         var3.append(" ");
         var3.append(KeyMap.key(this.terminal, InfoCmp.Capability.carriage_return));
      }

      var3.print(this.terminal);
      return true;
   }

   @Override
   public void callWidget(String var1) {
      try {
         this.lock.lock();
         if (!this.reading) {
            throw new IllegalStateException("Widgets can only be called during a `readLine` call");
         }

         try {
            Widget var2;
            if (var1.startsWith(".")) {
               var2 = this.builtinWidgets.get(var1.substring(1));
            } else {
               var2 = this.widgets.get(var1);
            }

            if (var2 != null) {
               var2.apply();
            }
         } catch (Throwable var6) {
            Log.debug("Error executing widget '", var1, "'", var6);
         }
      } finally {
         this.lock.unlock();
      }
   }

   public boolean redrawLine() {
      this.display.reset();
      return true;
   }

   public void putString(CharSequence var1) {
      this.buf.write(var1, this.overTyping);
   }

   @Override
   public void flush() {
      this.terminal.flush();
   }

   public boolean isKeyMap(String var1) {
      return this.keyMap.equals(var1);
   }

   public int readCharacter() {
      if (this.lock.isHeldByCurrentThread()) {
         int var1;
         try {
            this.lock.unlock();
            var1 = this.bindingReader.readCharacter();
         } finally {
            this.lock.lock();
         }

         return var1;
      } else {
         return this.bindingReader.readCharacter();
      }
   }

   public int peekCharacter(long var1) {
      return this.bindingReader.peekCharacter(var1);
   }

   protected <T> T doReadBinding(KeyMap<T> var1, KeyMap<T> var2) {
      if (this.lock.isHeldByCurrentThread()) {
         Object var3;
         try {
            this.lock.unlock();
            var3 = this.bindingReader.readBinding(var1, var2);
         } finally {
            this.lock.lock();
         }

         return (T)var3;
      } else {
         return this.bindingReader.readBinding(var1, var2);
      }
   }

   public Binding readBinding(KeyMap<Binding> var1) {
      return this.readBinding(var1, null);
   }

   public Binding readBinding(KeyMap<Binding> var1, KeyMap<Binding> var2) {
      Binding var3 = this.doReadBinding(var1, var2);
      if (var3 instanceof Reference) {
         String var4 = ((Reference)var3).name();
         if (!"yank-pop".equals(var4) && !"yank".equals(var4)) {
            this.killRing.resetLastYank();
         }

         if (!"kill-line".equals(var4) && !"kill-whole-line".equals(var4) && !"backward-kill-word".equals(var4) && !"kill-word".equals(var4)) {
            this.killRing.resetLastKill();
         }
      }

      return var3;
   }

   @Override
   public ParsedLine getParsedLine() {
      return this.parsedLine;
   }

   public String getLastBinding() {
      return this.bindingReader.getLastBinding();
   }

   @Override
   public String getSearchTerm() {
      return this.searchTerm != null ? this.searchTerm.toString() : null;
   }

   @Override
   public LineReader.RegionType getRegionActive() {
      return this.regionActive;
   }

   @Override
   public int getRegionMark() {
      return this.regionMark;
   }

   @Override
   public boolean setKeyMap(String var1) {
      KeyMap var2 = this.keyMaps.get(var1);
      if (var2 == null) {
         return false;
      } else {
         this.keyMap = var1;
         if (this.reading) {
            this.callWidget("callback-keymap");
         }

         return true;
      }
   }

   @Override
   public String getKeyMap() {
      return this.keyMap;
   }

   @Override
   public LineReader variable(String var1, Object var2) {
      this.variables.put(var1, var2);
      return this;
   }

   @Override
   public Map<String, Object> getVariables() {
      return this.variables;
   }

   @Override
   public Object getVariable(String var1) {
      return this.variables.get(var1);
   }

   @Override
   public void setVariable(String var1, Object var2) {
      this.variables.put(var1, var2);
   }

   @Override
   public LineReader option(LineReader.Option var1, boolean var2) {
      this.options.put(var1, var2);
      return this;
   }

   @Override
   public boolean isSet(LineReader.Option var1) {
      Boolean var2 = this.options.get(var1);
      return var2 != null ? var2 : var1.isDef();
   }

   @Override
   public void setOpt(LineReader.Option var1) {
      this.options.put(var1, Boolean.TRUE);
   }

   @Override
   public void unsetOpt(LineReader.Option var1) {
      this.options.put(var1, Boolean.FALSE);
   }

   protected String finishBuffer() {
      String var1 = this.buf.toString();
      String var2 = var1;
      if (!this.isSet(LineReader.Option.DISABLE_EVENT_EXPANSION)) {
         StringBuilder var3 = new StringBuilder();
         boolean var4 = false;

         for (int var5 = 0; var5 < var1.length(); var5++) {
            char var6 = var1.charAt(var5);
            if (var4) {
               var4 = false;
               if (var6 != '\n') {
                  var3.append(var6);
               }
            } else if (this.parser.isEscapeChar(var6)) {
               var4 = true;
            } else {
               var3.append(var6);
            }
         }

         var1 = var3.toString();
      }

      if (this.maskingCallback != null) {
         var2 = this.maskingCallback.history(var2);
      }

      if (var2 != null && var2.length() > 0) {
         this.history.add(Instant.now(), var2);
      }

      return var1;
   }

   protected void handleSignal(Terminal.Signal var1) {
      if (var1 == Terminal.Signal.WINCH) {
         Status var2 = Status.getStatus(this.terminal, false);
         if (var2 != null) {
            var2.hardReset();
         }

         this.size.copy(this.terminal.getBufferSize());
         this.display.resize(this.size.getRows(), this.size.getColumns());
         this.redrawLine();
         this.redisplay();
      } else if (var1 == Terminal.Signal.CONT) {
         this.terminal.enterRawMode();
         this.size.copy(this.terminal.getBufferSize());
         this.display.resize(this.size.getRows(), this.size.getColumns());
         this.terminal.puts(InfoCmp.Capability.keypad_xmit);
         this.redrawLine();
         this.redisplay();
      }
   }

   protected Widget getWidget(Object var1) {
      Widget var2;
      if (var1 instanceof Widget) {
         var2 = (Widget)var1;
      } else if (var1 instanceof Macro) {
         String var3 = ((Macro)var1).getSequence();
         var2 = () -> {
            this.bindingReader.runMacro(var3);
            return true;
         };
      } else if (var1 instanceof Reference) {
         String var4 = ((Reference)var1).name();
         var2 = this.widgets.get(var4);
         if (var2 == null) {
            var2 = () -> {
               this.post = () -> new AttributedString("No such widget `" + var4 + "'");
               return false;
            };
         }
      } else {
         var2 = () -> {
            this.post = () -> new AttributedString("Unsupported widget");
            return false;
         };
      }

      return var2;
   }

   public void setPrompt(String var1) {
      this.prompt = var1 == null ? AttributedString.EMPTY : this.expandPromptPattern(var1, 0, "", 0);
   }

   public void setRightPrompt(String var1) {
      this.rightPrompt = var1 == null ? AttributedString.EMPTY : this.expandPromptPattern(var1, 0, "", 0);
   }

   protected void setBuffer(Buffer var1) {
      this.buf.copyFrom(var1);
   }

   protected void setBuffer(String var1) {
      this.buf.clear();
      this.buf.write(var1);
   }

   protected String viDeleteChangeYankToRemap(String var1) {
      switch (var1) {
         case "abort":
         case "backward-char":
         case "forward-char":
         case "end-of-line":
         case "vi-match-bracket":
         case "vi-digit-or-beginning-of-line":
         case "neg-argument":
         case "digit-argument":
         case "vi-backward-char":
         case "vi-backward-word":
         case "vi-forward-char":
         case "vi-forward-word":
         case "vi-forward-word-end":
         case "vi-first-non-blank":
         case "vi-goto-column":
         case "vi-delete":
         case "vi-yank":
         case "vi-change-to":
         case "vi-find-next-char":
         case "vi-find-next-char-skip":
         case "vi-find-prev-char":
         case "vi-find-prev-char-skip":
         case "vi-repeat-find":
         case "vi-rev-repeat-find":
            return var1;
         default:
            return "vi-cmd-mode";
      }
   }

   protected int switchCase(int var1) {
      if (Character.isUpperCase(var1)) {
         return Character.toLowerCase(var1);
      } else {
         return Character.isLowerCase(var1) ? Character.toUpperCase(var1) : var1;
      }
   }

   protected boolean isInViMoveOperation() {
      return this.viMoveMode != LineReaderImpl.ViMoveMode.NORMAL;
   }

   protected boolean isInViChangeOperation() {
      return this.viMoveMode == LineReaderImpl.ViMoveMode.CHANGE;
   }

   protected boolean isInViCmdMode() {
      return "vicmd".equals(this.keyMap);
   }

   protected boolean viForwardChar() {
      if (this.count < 0) {
         return this.callNeg(this::viBackwardChar);
      } else {
         int var1 = this.findeol();
         if (this.isInViCmdMode() && !this.isInViMoveOperation()) {
            var1--;
         }

         if (this.buf.cursor() >= var1) {
            return false;
         } else {
            while (this.count-- > 0 && this.buf.cursor() < var1) {
               this.buf.move(1);
            }

            return true;
         }
      }
   }

   protected boolean viBackwardChar() {
      if (this.count < 0) {
         return this.callNeg(this::viForwardChar);
      } else {
         int var1 = this.findbol();
         if (this.buf.cursor() == var1) {
            return false;
         } else {
            while (this.count-- > 0 && this.buf.cursor() > 0) {
               this.buf.move(-1);
               if (this.buf.currChar() == 10) {
                  this.buf.move(1);
                  break;
               }
            }

            return true;
         }
      }
   }

   protected boolean forwardWord() {
      if (this.count < 0) {
         return this.callNeg(this::backwardWord);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar())) {
               this.buf.move(1);
            }

            if (this.isInViChangeOperation() && this.count == 0) {
               break;
            }

            while (this.buf.cursor() < this.buf.length() && !this.isWord(this.buf.currChar())) {
               this.buf.move(1);
            }
         }

         return true;
      }
   }

   protected boolean viForwardWord() {
      if (this.count < 0) {
         return this.callNeg(this::backwardWord);
      } else {
         while (this.count-- > 0) {
            if (this.isViAlphaNum(this.buf.currChar())) {
               while (this.buf.cursor() < this.buf.length() && this.isViAlphaNum(this.buf.currChar())) {
                  this.buf.move(1);
               }
            } else {
               while (this.buf.cursor() < this.buf.length() && !this.isViAlphaNum(this.buf.currChar()) && !this.isWhitespace(this.buf.currChar())) {
                  this.buf.move(1);
               }
            }

            if (this.isInViChangeOperation() && this.count == 0) {
               return true;
            }

            for (int var1 = this.buf.currChar() == 10 ? 1 : 0;
               this.buf.cursor() < this.buf.length() && var1 < 2 && this.isWhitespace(this.buf.currChar());
               var1 += this.buf.currChar() == 10 ? 1 : 0
            ) {
               this.buf.move(1);
            }
         }

         return true;
      }
   }

   protected boolean viForwardBlankWord() {
      if (this.count < 0) {
         return this.callNeg(this::viBackwardBlankWord);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() < this.buf.length() && !this.isWhitespace(this.buf.currChar())) {
               this.buf.move(1);
            }

            if (this.isInViChangeOperation() && this.count == 0) {
               return true;
            }

            for (int var1 = this.buf.currChar() == 10 ? 1 : 0;
               this.buf.cursor() < this.buf.length() && var1 < 2 && this.isWhitespace(this.buf.currChar());
               var1 += this.buf.currChar() == 10 ? 1 : 0
            ) {
               this.buf.move(1);
            }
         }

         return true;
      }
   }

   protected boolean emacsForwardWord() {
      if (this.count < 0) {
         return this.callNeg(this::emacsBackwardWord);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() < this.buf.length() && !this.isWord(this.buf.currChar())) {
               this.buf.move(1);
            }

            if (this.isInViChangeOperation() && this.count == 0) {
               return true;
            }

            while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar())) {
               this.buf.move(1);
            }
         }

         return true;
      }
   }

   protected boolean viForwardBlankWordEnd() {
      if (this.count < 0) {
         return false;
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() < this.buf.length()) {
               this.buf.move(1);
               if (!this.isWhitespace(this.buf.currChar())) {
                  break;
               }
            }

            while (this.buf.cursor() < this.buf.length()) {
               this.buf.move(1);
               if (this.isWhitespace(this.buf.currChar())) {
                  break;
               }
            }
         }

         return true;
      }
   }

   protected boolean viForwardWordEnd() {
      if (this.count < 0) {
         return this.callNeg(this::backwardWord);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() < this.buf.length() && this.isWhitespace(this.buf.nextChar())) {
               this.buf.move(1);
            }

            if (this.buf.cursor() < this.buf.length()) {
               if (this.isViAlphaNum(this.buf.nextChar())) {
                  this.buf.move(1);

                  while (this.buf.cursor() < this.buf.length() && this.isViAlphaNum(this.buf.nextChar())) {
                     this.buf.move(1);
                  }
               } else {
                  this.buf.move(1);

                  while (this.buf.cursor() < this.buf.length() && !this.isViAlphaNum(this.buf.nextChar()) && !this.isWhitespace(this.buf.nextChar())) {
                     this.buf.move(1);
                  }
               }
            }
         }

         if (this.buf.cursor() < this.buf.length() && this.isInViMoveOperation()) {
            this.buf.move(1);
         }

         return true;
      }
   }

   protected boolean backwardWord() {
      if (this.count < 0) {
         return this.callNeg(this::forwardWord);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() > 0 && !this.isWord(this.buf.atChar(this.buf.cursor() - 1))) {
               this.buf.move(-1);
            }

            while (this.buf.cursor() > 0 && this.isWord(this.buf.atChar(this.buf.cursor() - 1))) {
               this.buf.move(-1);
            }
         }

         return true;
      }
   }

   protected boolean viBackwardWord() {
      if (this.count < 0) {
         return this.callNeg(this::backwardWord);
      } else {
         while (this.count-- > 0) {
            int var1 = 0;

            while (this.buf.cursor() > 0) {
               this.buf.move(-1);
               if (!this.isWhitespace(this.buf.currChar())) {
                  break;
               }

               var1 += this.buf.currChar() == 10 ? 1 : 0;
               if (var1 == 2) {
                  this.buf.move(1);
                  break;
               }
            }

            if (this.buf.cursor() > 0) {
               if (this.isViAlphaNum(this.buf.currChar())) {
                  while (this.buf.cursor() > 0 && this.isViAlphaNum(this.buf.prevChar())) {
                     this.buf.move(-1);
                  }
               } else {
                  while (this.buf.cursor() > 0 && !this.isViAlphaNum(this.buf.prevChar()) && !this.isWhitespace(this.buf.prevChar())) {
                     this.buf.move(-1);
                  }
               }
            }
         }

         return true;
      }
   }

   protected boolean viBackwardBlankWord() {
      if (this.count < 0) {
         return this.callNeg(this::viForwardBlankWord);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() > 0) {
               this.buf.move(-1);
               if (!this.isWhitespace(this.buf.currChar())) {
                  break;
               }
            }

            while (this.buf.cursor() > 0) {
               this.buf.move(-1);
               if (this.isWhitespace(this.buf.currChar())) {
                  break;
               }
            }
         }

         return true;
      }
   }

   protected boolean viBackwardWordEnd() {
      if (this.count < 0) {
         return this.callNeg(this::viForwardWordEnd);
      } else {
         while (this.count-- > 0 && this.buf.cursor() > 1) {
            byte var1;
            if (this.isViAlphaNum(this.buf.currChar())) {
               var1 = 1;
            } else if (!this.isWhitespace(this.buf.currChar())) {
               var1 = 2;
            } else {
               var1 = 0;
            }

            while (this.buf.cursor() > 0) {
               boolean var2 = var1 != 1 && this.isWhitespace(this.buf.currChar());
               if (var1 != 0) {
                  var2 |= this.isViAlphaNum(this.buf.currChar());
               }

               if (var2 == (var1 == 2)) {
                  break;
               }

               this.buf.move(-1);
            }

            while (this.buf.cursor() > 0 && this.isWhitespace(this.buf.currChar())) {
               this.buf.move(-1);
            }
         }

         return true;
      }
   }

   protected boolean viBackwardBlankWordEnd() {
      if (this.count < 0) {
         return this.callNeg(this::viForwardBlankWordEnd);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() > 0 && !this.isWhitespace(this.buf.currChar())) {
               this.buf.move(-1);
            }

            while (this.buf.cursor() > 0 && this.isWhitespace(this.buf.currChar())) {
               this.buf.move(-1);
            }
         }

         return true;
      }
   }

   protected boolean emacsBackwardWord() {
      if (this.count < 0) {
         return this.callNeg(this::emacsForwardWord);
      } else {
         while (this.count-- > 0) {
            while (this.buf.cursor() > 0) {
               this.buf.move(-1);
               if (this.isWord(this.buf.currChar())) {
                  break;
               }
            }

            while (this.buf.cursor() > 0) {
               this.buf.move(-1);
               if (!this.isWord(this.buf.currChar())) {
                  break;
               }
            }
         }

         return true;
      }
   }

   protected boolean backwardDeleteWord() {
      if (this.count < 0) {
         return this.callNeg(this::deleteWord);
      } else {
         int var1 = this.buf.cursor();

         while (this.count-- > 0) {
            while (var1 > 0 && !this.isWord(this.buf.atChar(var1 - 1))) {
               var1--;
            }

            while (var1 > 0 && this.isWord(this.buf.atChar(var1 - 1))) {
               var1--;
            }
         }

         this.buf.backspace(this.buf.cursor() - var1);
         return true;
      }
   }

   protected boolean viBackwardKillWord() {
      if (this.count < 0) {
         return false;
      } else {
         int var1 = this.findbol();
         int var2 = this.buf.cursor();

         while (this.count-- > 0) {
            while (var2 > var1 && this.isWhitespace(this.buf.atChar(var2 - 1))) {
               var2--;
            }

            if (var2 > var1) {
               if (this.isViAlphaNum(this.buf.atChar(var2 - 1))) {
                  while (var2 > var1 && this.isViAlphaNum(this.buf.atChar(var2 - 1))) {
                     var2--;
                  }
               } else {
                  while (var2 > var1 && !this.isViAlphaNum(this.buf.atChar(var2 - 1)) && !this.isWhitespace(this.buf.atChar(var2 - 1))) {
                     var2--;
                  }
               }
            }
         }

         this.killRing.addBackwards(this.buf.substring(var2, this.buf.cursor()));
         this.buf.backspace(this.buf.cursor() - var2);
         return true;
      }
   }

   protected boolean backwardKillWord() {
      if (this.count < 0) {
         return this.callNeg(this::killWord);
      } else {
         int var1 = this.buf.cursor();

         while (this.count-- > 0) {
            while (var1 > 0 && !this.isWord(this.buf.atChar(var1 - 1))) {
               var1--;
            }

            while (var1 > 0 && this.isWord(this.buf.atChar(var1 - 1))) {
               var1--;
            }
         }

         this.killRing.addBackwards(this.buf.substring(var1, this.buf.cursor()));
         this.buf.backspace(this.buf.cursor() - var1);
         return true;
      }
   }

   protected boolean copyPrevWord() {
      if (this.count <= 0) {
         return false;
      } else {
         int var2 = this.buf.cursor();

         do {
            int var1 = var2;

            while (var2 > 0 && !this.isWord(this.buf.atChar(var2 - 1))) {
               var2--;
            }

            while (var2 > 0 && this.isWord(this.buf.atChar(var2 - 1))) {
               var2--;
            }

            if (--this.count == 0) {
               this.buf.write(this.buf.substring(var2, var1));
               return true;
            }
         } while (var2 != 0);

         return false;
      }
   }

   protected boolean upCaseWord() {
      int var1 = Math.abs(this.count);
      int var2 = this.buf.cursor();

      while (var1-- > 0) {
         while (this.buf.cursor() < this.buf.length() && !this.isWord(this.buf.currChar())) {
            this.buf.move(1);
         }

         while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar())) {
            this.buf.currChar(Character.toUpperCase(this.buf.currChar()));
            this.buf.move(1);
         }
      }

      if (this.count < 0) {
         this.buf.cursor(var2);
      }

      return true;
   }

   protected boolean downCaseWord() {
      int var1 = Math.abs(this.count);
      int var2 = this.buf.cursor();

      while (var1-- > 0) {
         while (this.buf.cursor() < this.buf.length() && !this.isWord(this.buf.currChar())) {
            this.buf.move(1);
         }

         while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar())) {
            this.buf.currChar(Character.toLowerCase(this.buf.currChar()));
            this.buf.move(1);
         }
      }

      if (this.count < 0) {
         this.buf.cursor(var2);
      }

      return true;
   }

   protected boolean capitalizeWord() {
      int var1 = Math.abs(this.count);
      int var2 = this.buf.cursor();

      while (var1-- > 0) {
         boolean var3 = true;

         while (this.buf.cursor() < this.buf.length() && !this.isWord(this.buf.currChar())) {
            this.buf.move(1);
         }

         while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar()) && !this.isAlpha(this.buf.currChar())) {
            this.buf.move(1);
         }

         while (this.buf.cursor() < this.buf.length() && this.isWord(this.buf.currChar())) {
            this.buf.currChar(var3 ? Character.toUpperCase(this.buf.currChar()) : Character.toLowerCase(this.buf.currChar()));
            this.buf.move(1);
            var3 = false;
         }
      }

      if (this.count < 0) {
         this.buf.cursor(var2);
      }

      return true;
   }

   protected boolean deleteWord() {
      if (this.count < 0) {
         return this.callNeg(this::backwardDeleteWord);
      } else {
         int var1 = this.buf.cursor();

         while (this.count-- > 0) {
            while (var1 < this.buf.length() && !this.isWord(this.buf.atChar(var1))) {
               var1++;
            }

            while (var1 < this.buf.length() && this.isWord(this.buf.atChar(var1))) {
               var1++;
            }
         }

         this.buf.delete(var1 - this.buf.cursor());
         return true;
      }
   }

   protected boolean killWord() {
      if (this.count < 0) {
         return this.callNeg(this::backwardKillWord);
      } else {
         int var1 = this.buf.cursor();

         while (this.count-- > 0) {
            while (var1 < this.buf.length() && !this.isWord(this.buf.atChar(var1))) {
               var1++;
            }

            while (var1 < this.buf.length() && this.isWord(this.buf.atChar(var1))) {
               var1++;
            }
         }

         this.killRing.add(this.buf.substring(this.buf.cursor(), var1));
         this.buf.delete(var1 - this.buf.cursor());
         return true;
      }
   }

   protected boolean transposeWords() {
      int var1 = this.buf.cursor() - 1;
      int var2 = this.buf.cursor();

      while (this.buf.atChar(var1) != 0 && this.buf.atChar(var1) != 10) {
         var1--;
      }

      var1++;

      while (this.buf.atChar(var2) != 0 && this.buf.atChar(var2) != 10) {
         var2++;
      }

      if (var2 - var1 < 2) {
         return false;
      } else {
         int var3 = 0;
         boolean var4 = false;
         if (!this.isDelimiter(this.buf.atChar(var1))) {
            var3++;
            var4 = true;
         }

         for (int var5 = var1; var5 < var2; var5++) {
            if (this.isDelimiter(this.buf.atChar(var5))) {
               var4 = false;
            } else {
               if (!var4) {
                  var3++;
               }

               var4 = true;
            }
         }

         if (var3 < 2) {
            return false;
         } else {
            boolean var13 = this.count < 0;

            for (int var6 = Math.max(this.count, -this.count); var6 > 0; var6--) {
               int var7 = this.buf.cursor();

               while (var7 > var1 && !this.isDelimiter(this.buf.atChar(var7 - 1))) {
                  var7--;
               }

               int var8 = var7;

               while (var8 < var2) {
                  if (this.isDelimiter(this.buf.atChar(++var8))) {
                     break;
                  }
               }

               int var9;
               int var10;
               if (var13) {
                  var10 = var7 - 1;

                  while (var10 > var1 && this.isDelimiter(this.buf.atChar(var10 - 1))) {
                     var10--;
                  }

                  if (var10 >= var1) {
                     var9 = var10;

                     while (var9 > var1 && !this.isDelimiter(this.buf.atChar(var9 - 1))) {
                        var9--;
                     }
                  } else {
                     var9 = var8;

                     while (this.isDelimiter(this.buf.atChar(++var9))) {
                     }

                     var10 = var9;

                     while (var10 < var2) {
                        if (this.isDelimiter(this.buf.atChar(++var10))) {
                           break;
                        }
                     }
                  }
               } else {
                  var9 = var8;

                  while (var9 < var2) {
                     if (!this.isDelimiter(this.buf.atChar(++var9))) {
                        break;
                     }
                  }

                  if (var9 != var2) {
                     var10 = var9;

                     while (var10 < var2) {
                        if (this.isDelimiter(this.buf.atChar(++var10))) {
                           break;
                        }
                     }
                  } else {
                     var10 = var7;

                     while (this.isDelimiter(this.buf.atChar(var10 - 1))) {
                        var10--;
                     }

                     var9 = var10;

                     while (var9 > var1 && !this.isDelimiter(this.buf.atChar(var9 - 1))) {
                        var9--;
                     }
                  }
               }

               if (var7 < var9) {
                  String var11 = this.buf.substring(0, var7)
                     + this.buf.substring(var9, var10)
                     + this.buf.substring(var8, var9)
                     + this.buf.substring(var7, var8)
                     + this.buf.substring(var10);
                  this.buf.clear();
                  this.buf.write(var11);
                  this.buf.cursor(var13 ? var8 : var10);
               } else {
                  String var14 = this.buf.substring(0, var9)
                     + this.buf.substring(var7, var8)
                     + this.buf.substring(var10, var7)
                     + this.buf.substring(var9, var10)
                     + this.buf.substring(var8);
                  this.buf.clear();
                  this.buf.write(var14);
                  this.buf.cursor(var13 ? var10 : var8);
               }
            }

            return true;
         }
      }
   }

   private int findbol() {
      int var1 = this.buf.cursor();

      while (var1 > 0 && this.buf.atChar(var1 - 1) != 10) {
         var1--;
      }

      return var1;
   }

   private int findeol() {
      int var1 = this.buf.cursor();

      while (var1 < this.buf.length() && this.buf.atChar(var1) != 10) {
         var1++;
      }

      return var1;
   }

   protected boolean insertComment() {
      return this.doInsertComment(false);
   }

   protected boolean viInsertComment() {
      return this.doInsertComment(true);
   }

   protected boolean doInsertComment(boolean var1) {
      String var2 = this.getString("comment-begin", "#");
      this.beginningOfLine();
      this.putString(var2);
      if (var1) {
         this.setKeyMap("viins");
      }

      return this.acceptLine();
   }

   protected boolean viFindNextChar() {
      if ((this.findChar = this.vigetkey()) > 0) {
         this.findDir = 1;
         this.findTailAdd = 0;
         return this.vifindchar(false);
      } else {
         return false;
      }
   }

   protected boolean viFindPrevChar() {
      if ((this.findChar = this.vigetkey()) > 0) {
         this.findDir = -1;
         this.findTailAdd = 0;
         return this.vifindchar(false);
      } else {
         return false;
      }
   }

   protected boolean viFindNextCharSkip() {
      if ((this.findChar = this.vigetkey()) > 0) {
         this.findDir = 1;
         this.findTailAdd = -1;
         return this.vifindchar(false);
      } else {
         return false;
      }
   }

   protected boolean viFindPrevCharSkip() {
      if ((this.findChar = this.vigetkey()) > 0) {
         this.findDir = -1;
         this.findTailAdd = 1;
         return this.vifindchar(false);
      } else {
         return false;
      }
   }

   protected boolean viRepeatFind() {
      return this.vifindchar(true);
   }

   protected boolean viRevRepeatFind() {
      if (this.count < 0) {
         return this.callNeg(() -> this.vifindchar(true));
      } else {
         this.findTailAdd = -this.findTailAdd;
         this.findDir = -this.findDir;
         boolean var1 = this.vifindchar(true);
         this.findTailAdd = -this.findTailAdd;
         this.findDir = -this.findDir;
         return var1;
      }
   }

   private int vigetkey() {
      int var1 = this.readCharacter();
      KeyMap var2 = this.keyMaps.get("main");
      if (var2 != null) {
         Binding var3 = (Binding)var2.getBound(new String(Character.toChars(var1)));
         if (var3 instanceof Reference) {
            String var4 = ((Reference)var3).name();
            if ("abort".equals(var4)) {
               return -1;
            }
         }
      }

      return var1;
   }

   private boolean vifindchar(boolean var1) {
      if (this.findDir == 0) {
         return false;
      } else if (this.count < 0) {
         return this.callNeg(this::viRevRepeatFind);
      } else {
         if (var1 && this.findTailAdd != 0) {
            if (this.findDir > 0) {
               if (this.buf.cursor() < this.buf.length() && this.buf.nextChar() == this.findChar) {
                  this.buf.move(1);
               }
            } else if (this.buf.cursor() > 0 && this.buf.prevChar() == this.findChar) {
               this.buf.move(-1);
            }
         }

         int var2 = this.buf.cursor();

         while (this.count-- > 0) {
            do {
               this.buf.move(this.findDir);
            } while (this.buf.cursor() > 0 && this.buf.cursor() < this.buf.length() && this.buf.currChar() != this.findChar && this.buf.currChar() != 10);

            if (this.buf.cursor() <= 0 || this.buf.cursor() >= this.buf.length() || this.buf.currChar() == 10) {
               this.buf.cursor(var2);
               return false;
            }
         }

         if (this.findTailAdd != 0) {
            this.buf.move(this.findTailAdd);
         }

         if (this.findDir == 1 && this.isInViMoveOperation()) {
            this.buf.move(1);
         }

         return true;
      }
   }

   private boolean callNeg(Widget var1) {
      this.count = -this.count;
      boolean var2 = var1.apply();
      this.count = -this.count;
      return var2;
   }

   protected boolean viHistorySearchForward() {
      this.searchDir = 1;
      this.searchIndex = 0;
      return this.getViSearchString() && this.viRepeatSearch();
   }

   protected boolean viHistorySearchBackward() {
      this.searchDir = -1;
      this.searchIndex = this.history.size() - 1;
      return this.getViSearchString() && this.viRepeatSearch();
   }

   protected boolean viRepeatSearch() {
      if (this.searchDir == 0) {
         return false;
      } else {
         int var1 = this.searchDir < 0
            ? this.searchBackwards(this.searchString, this.searchIndex, false)
            : this.searchForwards(this.searchString, this.searchIndex, false);
         if (var1 != -1 && var1 != this.history.index()) {
            this.searchIndex = var1;
            this.buf.clear();
            this.history.moveTo(this.searchIndex);
            this.buf.write(this.history.get(this.searchIndex));
            if ("vicmd".equals(this.keyMap)) {
               this.buf.move(-1);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   protected boolean viRevRepeatSearch() {
      this.searchDir = -this.searchDir;
      boolean var1 = this.viRepeatSearch();
      this.searchDir = -this.searchDir;
      return var1;
   }

   private boolean getViSearchString() {
      if (this.searchDir == 0) {
         return false;
      } else {
         String var1 = this.searchDir < 0 ? "?" : "/";
         BufferImpl var2 = new BufferImpl();
         KeyMap var3 = this.keyMaps.get("main");
         if (var3 == null) {
            var3 = this.keyMaps.get(".safe");
         }

         while (true) {
            this.post = () -> new AttributedString(var1 + var2.toString() + "_");
            this.redisplay();
            Binding var4 = this.doReadBinding(var3, null);
            if (var4 instanceof Reference) {
               String var5 = ((Reference)var4).name();
               switch (var5) {
                  case "abort":
                     this.post = null;
                     return false;
                  case "accept-line":
                  case "vi-cmd-mode":
                     this.searchString = var2.toString();
                     this.post = null;
                     return true;
                  case "magic-space":
                     var2.write(32);
                     break;
                  case "redisplay":
                     this.redisplay();
                     break;
                  case "clear-screen":
                     this.clearScreen();
                     break;
                  case "self-insert":
                     var2.write(this.getLastBinding());
                     break;
                  case "self-insert-unmeta":
                     if (this.getLastBinding().charAt(0) == 27) {
                        String var9 = this.getLastBinding().substring(1);
                        if ("\r".equals(var9)) {
                           var9 = "\n";
                        }

                        var2.write(var9);
                     }
                     break;
                  case "backward-delete-char":
                  case "vi-backward-delete-char":
                     if (var2.length() > 0) {
                        var2.backspace();
                     }
                     break;
                  case "backward-kill-word":
                  case "vi-backward-kill-word":
                     if (var2.length() > 0 && !this.isWhitespace(var2.prevChar())) {
                        var2.backspace();
                     }

                     if (var2.length() > 0 && this.isWhitespace(var2.prevChar())) {
                        var2.backspace();
                     }
                     break;
                  case "quoted-insert":
                  case "vi-quoted-insert":
                     int var8 = this.readCharacter();
                     if (var8 >= 0) {
                        var2.write(var8);
                     } else {
                        this.beep();
                     }
                     break;
                  default:
                     this.beep();
               }
            }
         }
      }
   }

   protected boolean insertCloseCurly() {
      return this.insertClose("}");
   }

   protected boolean insertCloseParen() {
      return this.insertClose(")");
   }

   protected boolean insertCloseSquare() {
      return this.insertClose("]");
   }

   protected boolean insertClose(String var1) {
      this.putString(var1);
      long var2 = this.getLong("blink-matching-paren", 500L);
      if (var2 <= 0L) {
         return true;
      } else {
         int var4 = this.buf.cursor();
         this.buf.move(-1);
         this.doViMatchBracket();
         this.redisplay();
         this.peekCharacter(var2);
         this.buf.cursor(var4);
         return true;
      }
   }

   protected boolean viMatchBracket() {
      return this.doViMatchBracket();
   }

   protected boolean undefinedKey() {
      return false;
   }

   protected boolean doViMatchBracket() {
      int var1 = this.buf.cursor();
      if (var1 == this.buf.length()) {
         return false;
      } else {
         int var2 = this.getBracketType(this.buf.atChar(var1));
         int var3 = var2 < 0 ? -1 : 1;
         int var4 = 1;
         if (var2 == 0) {
            return false;
         } else {
            while (var4 > 0) {
               var1 += var3;
               if (var1 < 0 || var1 >= this.buf.length()) {
                  return false;
               }

               int var5 = this.getBracketType(this.buf.atChar(var1));
               if (var5 == var2) {
                  var4++;
               } else if (var5 == -var2) {
                  var4--;
               }
            }

            if (var3 > 0 && this.isInViMoveOperation()) {
               var1++;
            }

            this.buf.cursor(var1);
            return true;
         }
      }
   }

   protected int getBracketType(int var1) {
      switch (var1) {
         case 40:
            return 3;
         case 41:
            return -3;
         case 91:
            return 1;
         case 93:
            return -1;
         case 123:
            return 2;
         case 125:
            return -2;
         default:
            return 0;
      }
   }

   protected boolean transposeChars() {
      int var1 = this.buf.cursor() - 1;
      int var2 = this.buf.cursor();

      while (this.buf.atChar(var1) != 0 && this.buf.atChar(var1) != 10) {
         var1--;
      }

      var1++;

      while (this.buf.atChar(var2) != 0 && this.buf.atChar(var2) != 10) {
         var2++;
      }

      if (var2 - var1 < 2) {
         return false;
      } else {
         boolean var3 = this.count < 0;

         for (int var4 = Math.max(this.count, -this.count); var4 > 0; var4--) {
            while (this.buf.cursor() <= var1) {
               this.buf.move(1);
            }

            while (this.buf.cursor() >= var2) {
               this.buf.move(-1);
            }

            int var5 = this.buf.currChar();
            this.buf.currChar(this.buf.prevChar());
            this.buf.move(-1);
            this.buf.currChar(var5);
            this.buf.move(var3 ? 0 : 2);
         }

         return true;
      }
   }

   protected boolean undo() {
      this.isUndo = true;
      if (this.undo.canUndo()) {
         this.undo.undo();
         return true;
      } else {
         return false;
      }
   }

   protected boolean redo() {
      this.isUndo = true;
      if (this.undo.canRedo()) {
         this.undo.redo();
         return true;
      } else {
         return false;
      }
   }

   protected boolean sendBreak() {
      if (this.searchTerm == null) {
         this.buf.clear();
         this.println();
         this.redrawLine();
         return false;
      } else {
         return true;
      }
   }

   protected boolean backwardChar() {
      return this.buf.move(-this.count) != 0;
   }

   protected boolean forwardChar() {
      return this.buf.move(this.count) != 0;
   }

   protected boolean viDigitOrBeginningOfLine() {
      return this.repeatCount > 0 ? this.digitArgument() : this.beginningOfLine();
   }

   protected boolean universalArgument() {
      this.mult = this.mult * this.universal;
      this.isArgDigit = true;
      return true;
   }

   protected boolean argumentBase() {
      if (this.repeatCount > 0 && this.repeatCount < 32) {
         this.universal = this.repeatCount;
         this.isArgDigit = true;
         return true;
      } else {
         return false;
      }
   }

   protected boolean negArgument() {
      this.mult *= -1;
      this.isArgDigit = true;
      return true;
   }

   protected boolean digitArgument() {
      String var1 = this.getLastBinding();
      this.repeatCount = this.repeatCount * 10 + var1.charAt(var1.length() - 1) - 48;
      this.isArgDigit = true;
      return true;
   }

   protected boolean viDelete() {
      int var1 = this.buf.cursor();
      Binding var2 = this.readBinding(this.getKeys());
      if (var2 instanceof Reference) {
         String var3 = this.viDeleteChangeYankToRemap(((Reference)var2).name());
         if ("vi-delete".equals(var3)) {
            this.killWholeLine();
         } else {
            this.viMoveMode = LineReaderImpl.ViMoveMode.DELETE;
            Widget var4 = this.widgets.get(var3);
            if (var4 != null && !var4.apply()) {
               this.viMoveMode = LineReaderImpl.ViMoveMode.NORMAL;
               return false;
            }

            this.viMoveMode = LineReaderImpl.ViMoveMode.NORMAL;
         }

         return this.viDeleteTo(var1, this.buf.cursor());
      } else {
         this.pushBackBinding();
         return false;
      }
   }

   protected boolean viYankTo() {
      int var1 = this.buf.cursor();
      Binding var2 = this.readBinding(this.getKeys());
      if (var2 instanceof Reference) {
         String var3 = this.viDeleteChangeYankToRemap(((Reference)var2).name());
         if ("vi-yank".equals(var3)) {
            this.yankBuffer = this.buf.toString();
            return true;
         } else {
            this.viMoveMode = LineReaderImpl.ViMoveMode.YANK;
            Widget var4 = this.widgets.get(var3);
            if (var4 != null && !var4.apply()) {
               return false;
            } else {
               this.viMoveMode = LineReaderImpl.ViMoveMode.NORMAL;
               return this.viYankTo(var1, this.buf.cursor());
            }
         }
      } else {
         this.pushBackBinding();
         return false;
      }
   }

   protected boolean viYankWholeLine() {
      int var3 = this.buf.cursor();

      while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
      }

      int var1 = this.buf.cursor();

      for (int var4 = 0; var4 < this.repeatCount; var4++) {
         while (this.buf.move(1) == 1 && this.buf.prevChar() != 10) {
         }
      }

      int var2 = this.buf.cursor();
      this.yankBuffer = this.buf.substring(var1, var2);
      if (!this.yankBuffer.endsWith("\n")) {
         this.yankBuffer = this.yankBuffer + "\n";
      }

      this.buf.cursor(var3);
      return true;
   }

   protected boolean viChange() {
      int var1 = this.buf.cursor();
      Binding var2 = this.readBinding(this.getKeys());
      if (var2 instanceof Reference) {
         String var3 = this.viDeleteChangeYankToRemap(((Reference)var2).name());
         if ("vi-change-to".equals(var3)) {
            this.killWholeLine();
         } else {
            this.viMoveMode = LineReaderImpl.ViMoveMode.CHANGE;
            Widget var4 = this.widgets.get(var3);
            if (var4 != null && !var4.apply()) {
               this.viMoveMode = LineReaderImpl.ViMoveMode.NORMAL;
               return false;
            }

            this.viMoveMode = LineReaderImpl.ViMoveMode.NORMAL;
         }

         boolean var5 = this.viChange(var1, this.buf.cursor());
         this.setKeyMap("viins");
         return var5;
      } else {
         this.pushBackBinding();
         return false;
      }
   }

   protected void cleanup() {
      if (this.isSet(LineReader.Option.ERASE_LINE_ON_FINISH)) {
         Buffer var1 = this.buf.copy();
         AttributedString var2 = this.prompt;
         this.buf.clear();
         this.prompt = new AttributedString("");
         this.doCleanup(false);
         this.prompt = var2;
         this.buf.copyFrom(var1);
      } else {
         this.doCleanup(true);
      }
   }

   protected void doCleanup(boolean var1) {
      this.buf.cursor(this.buf.length());
      this.post = null;
      if (this.size.getColumns() > 0 || this.size.getRows() > 0) {
         this.redisplay(false);
         if (var1) {
            this.println();
         }

         this.terminal.puts(InfoCmp.Capability.keypad_local);
         this.terminal.trackMouse(Terminal.MouseTracking.Off);
         if (this.isSet(LineReader.Option.BRACKETED_PASTE)) {
            this.terminal.writer().write("\u001b[?2004l");
         }

         this.flush();
      }

      this.history.moveToEnd();
   }

   protected boolean historyIncrementalSearchForward() {
      return this.doSearchHistory(false);
   }

   protected boolean historyIncrementalSearchBackward() {
      return this.doSearchHistory(true);
   }

   protected boolean doSearchHistory(boolean var1) {
      if (this.history.isEmpty()) {
         return false;
      } else {
         KeyMap var2 = new KeyMap();
         this.getString("search-terminators", "\u001b\n").codePoints().forEach(var2x -> this.bind(var2, "accept-line", new String(Character.toChars(var2x))));
         Buffer var3 = this.buf.copy();
         this.searchIndex = -1;
         this.searchTerm = new StringBuffer();
         this.searchBackward = var1;
         this.searchFailing = false;
         this.post = () -> new AttributedString(
               (this.searchFailing ? "failing " : "") + (this.searchBackward ? "bck-i-search" : "fwd-i-search") + ": " + this.searchTerm + "_"
            );
         this.redisplay();

         try {
            while (true) {
               int var4 = this.searchIndex;
               Binding var18 = this.readBinding(this.getKeys(), var2);
               String var6 = var18 instanceof Reference ? ((Reference)var18).name() : "";
               boolean var7 = false;
               switch (var6) {
                  case "abort":
                     this.beep();
                     this.buf.copyFrom(var3);
                     return true;
                  case "history-incremental-search-backward":
                     this.searchBackward = true;
                     var7 = true;
                     break;
                  case "history-incremental-search-forward":
                     this.searchBackward = false;
                     var7 = true;
                     break;
                  case "backward-delete-char":
                     if (this.searchTerm.length() > 0) {
                        this.searchTerm.deleteCharAt(this.searchTerm.length() - 1);
                     }
                     break;
                  case "self-insert":
                     this.searchTerm.append(this.getLastBinding());
                     break;
                  default:
                     if (this.searchIndex != -1) {
                        this.history.moveTo(this.searchIndex);
                     }

                     this.pushBackBinding();
                     return true;
               }

               String var8 = this.doGetSearchPattern();
               if (var8.length() == 0) {
                  this.buf.copyFrom(var3);
                  this.searchFailing = false;
               } else {
                  boolean var19 = this.isSet(LineReader.Option.CASE_INSENSITIVE_SEARCH);
                  Pattern var20 = Pattern.compile(var8, var19 ? 66 : 64);
                  Object var11 = null;
                  if (this.searchBackward) {
                     boolean var12 = var7;
                     var11 = this.matches(var20, this.buf.toString(), this.searchIndex)
                        .stream()
                        .filter(var2x -> var12 ? var2x.v < this.buf.cursor() : var2x.v <= this.buf.cursor())
                        .max(Comparator.comparing(LineReaderImpl.Pair::getV))
                        .orElse(null);
                     if (var11 == null) {
                        var11 = StreamSupport.stream(
                              Spliterators.spliteratorUnknownSize(
                                 this.history.reverseIterator(this.searchIndex < 0 ? this.history.last() : this.searchIndex - 1), 16
                              ),
                              false
                           )
                           .flatMap(var2x -> this.matches(var20, var2x.line(), var2x.index()).stream())
                           .findFirst()
                           .orElse(null);
                     }
                  } else {
                     boolean var23 = var7;
                     var11 = this.matches(var20, this.buf.toString(), this.searchIndex)
                        .stream()
                        .filter(var2x -> var23 ? var2x.v > this.buf.cursor() : var2x.v >= this.buf.cursor())
                        .min(Comparator.comparing(LineReaderImpl.Pair::getV))
                        .orElse(null);
                     if (var11 == null) {
                        var11 = StreamSupport.stream(
                              Spliterators.spliteratorUnknownSize(
                                 this.history.iterator((this.searchIndex < 0 ? this.history.last() : this.searchIndex) + 1), 16
                              ),
                              false
                           )
                           .flatMap(var2x -> this.matches(var20, var2x.line(), var2x.index()).stream())
                           .findFirst()
                           .orElse(null);
                        if (var11 == null && this.searchIndex >= 0) {
                           var11 = this.matches(var20, var3.toString(), -1).stream().min(Comparator.comparing(LineReaderImpl.Pair::getV)).orElse(null);
                        }
                     }
                  }

                  if (var11 != null) {
                     this.searchIndex = (Integer)((LineReaderImpl.Pair)var11).u;
                     this.buf.clear();
                     if (this.searchIndex >= 0) {
                        this.buf.write(this.history.get(this.searchIndex));
                     } else {
                        this.buf.write(var3.toString());
                     }

                     this.buf.cursor((Integer)((LineReaderImpl.Pair)var11).v);
                     this.searchFailing = false;
                  } else {
                     this.searchFailing = true;
                     this.beep();
                  }
               }

               this.redisplay();
            }
         } catch (IOError var16) {
            if (!(var16.getCause() instanceof InterruptedException)) {
               throw var16;
            } else {
               return true;
            }
         } finally {
            this.searchTerm = null;
            this.searchIndex = -1;
            this.post = null;
         }
      }
   }

   private List<LineReaderImpl.Pair<Integer, Integer>> matches(Pattern var1, String var2, int var3) {
      ArrayList var4 = new ArrayList();
      Matcher var5 = var1.matcher(var2);

      while (var5.find()) {
         var4.add(new LineReaderImpl.Pair<>(var3, var5.start()));
      }

      return var4;
   }

   private String doGetSearchPattern() {
      StringBuilder var1 = new StringBuilder();
      boolean var2 = false;

      for (int var3 = 0; var3 < this.searchTerm.length(); var3++) {
         char var4 = this.searchTerm.charAt(var3);
         if (Character.isLowerCase(var4)) {
            if (var2) {
               var1.append("\\E");
               var2 = false;
            }

            var1.append("[").append(Character.toLowerCase(var4)).append(Character.toUpperCase(var4)).append("]");
         } else {
            if (!var2) {
               var1.append("\\Q");
               var2 = true;
            }

            var1.append(var4);
         }
      }

      if (var2) {
         var1.append("\\E");
      }

      return var1.toString();
   }

   private void pushBackBinding() {
      this.pushBackBinding(false);
   }

   private void pushBackBinding(boolean var1) {
      String var2 = this.getLastBinding();
      if (var2 != null) {
         this.bindingReader.runMacro(var2);
         this.skipRedisplay = var1;
      }
   }

   protected boolean historySearchForward() {
      if (this.historyBuffer == null || this.buf.length() == 0 || !this.buf.toString().equals(this.history.current())) {
         this.historyBuffer = this.buf.copy();
         this.searchBuffer = this.getFirstWord();
      }

      int var1 = this.history.index() + 1;
      if (var1 < this.history.last() + 1) {
         int var2 = this.searchForwards(this.searchBuffer.toString(), var1, true);
         if (var2 == -1) {
            this.history.moveToEnd();
            if (this.buf.toString().equals(this.historyBuffer.toString())) {
               return false;
            }

            this.setBuffer(this.historyBuffer.toString());
            this.historyBuffer = null;
         } else {
            if (!this.history.moveTo(var2)) {
               this.history.moveToEnd();
               this.setBuffer(this.historyBuffer.toString());
               return false;
            }

            this.setBuffer(this.history.current());
         }
      } else {
         this.history.moveToEnd();
         if (this.buf.toString().equals(this.historyBuffer.toString())) {
            return false;
         }

         this.setBuffer(this.historyBuffer.toString());
         this.historyBuffer = null;
      }

      return true;
   }

   private CharSequence getFirstWord() {
      String var1 = this.buf.toString();
      int var2 = 0;

      while (var2 < var1.length() && !Character.isWhitespace(var1.charAt(var2))) {
         var2++;
      }

      return var1.substring(0, var2);
   }

   protected boolean historySearchBackward() {
      if (this.historyBuffer == null || this.buf.length() == 0 || !this.buf.toString().equals(this.history.current())) {
         this.historyBuffer = this.buf.copy();
         this.searchBuffer = this.getFirstWord();
      }

      int var1 = this.searchBackwards(this.searchBuffer.toString(), this.history.index(), true);
      if (var1 == -1) {
         return false;
      } else if (this.history.moveTo(var1)) {
         this.setBuffer(this.history.current());
         return true;
      } else {
         return false;
      }
   }

   public int searchBackwards(String var1, int var2) {
      return this.searchBackwards(var1, var2, false);
   }

   public int searchBackwards(String var1) {
      return this.searchBackwards(var1, this.history.index(), false);
   }

   public int searchBackwards(String var1, int var2, boolean var3) {
      boolean var4 = this.isSet(LineReader.Option.CASE_INSENSITIVE_SEARCH);
      if (var4) {
         var1 = var1.toLowerCase();
      }

      ListIterator var5 = this.history.iterator(var2);

      while (var5.hasPrevious()) {
         History.Entry var6 = (History.Entry)var5.previous();
         String var7 = var6.line();
         if (var4) {
            var7 = var7.toLowerCase();
         }

         int var8 = var7.indexOf(var1);
         if (var3 && var8 == 0 || !var3 && var8 >= 0) {
            return var6.index();
         }
      }

      return -1;
   }

   public int searchForwards(String var1, int var2, boolean var3) {
      boolean var4 = this.isSet(LineReader.Option.CASE_INSENSITIVE_SEARCH);
      if (var4) {
         var1 = var1.toLowerCase();
      }

      if (var2 > this.history.last()) {
         var2 = this.history.last();
      }

      ListIterator var5 = this.history.iterator(var2);
      if (this.searchIndex != -1 && var5.hasNext()) {
         var5.next();
      }

      while (var5.hasNext()) {
         History.Entry var6 = (History.Entry)var5.next();
         String var7 = var6.line();
         if (var4) {
            var7 = var7.toLowerCase();
         }

         int var8 = var7.indexOf(var1);
         if (var3 && var8 == 0 || !var3 && var8 >= 0) {
            return var6.index();
         }
      }

      return -1;
   }

   public int searchForwards(String var1, int var2) {
      return this.searchForwards(var1, var2, false);
   }

   public int searchForwards(String var1) {
      return this.searchForwards(var1, this.history.index());
   }

   protected boolean quit() {
      this.getBuffer().clear();
      return this.acceptLine();
   }

   protected boolean acceptAndHold() {
      this.nextCommandFromHistory = false;
      this.acceptLine();
      if (!this.buf.toString().isEmpty()) {
         this.nextHistoryId = Integer.MAX_VALUE;
         this.nextCommandFromHistory = true;
      }

      return this.nextCommandFromHistory;
   }

   protected boolean acceptLineAndDownHistory() {
      this.nextCommandFromHistory = false;
      this.acceptLine();
      if (this.nextHistoryId < 0) {
         this.nextHistoryId = this.history.index();
      }

      if (this.history.size() > this.nextHistoryId + 1) {
         this.nextHistoryId++;
         this.nextCommandFromHistory = true;
      }

      return this.nextCommandFromHistory;
   }

   protected boolean acceptAndInferNextHistory() {
      this.nextCommandFromHistory = false;
      this.acceptLine();
      if (!this.buf.toString().isEmpty()) {
         this.nextHistoryId = this.searchBackwards(this.buf.toString(), this.history.last());
         if (this.nextHistoryId >= 0 && this.history.size() > this.nextHistoryId + 1) {
            this.nextHistoryId++;
            this.nextCommandFromHistory = true;
         }
      }

      return this.nextCommandFromHistory;
   }

   protected boolean acceptLine() {
      this.parsedLine = null;
      if (!this.isSet(LineReader.Option.DISABLE_EVENT_EXPANSION)) {
         try {
            String var1 = this.buf.toString();
            String var2 = this.expander.expandHistory(this.history, var1);
            if (!var2.equals(var1)) {
               this.buf.clear();
               this.buf.write(var2);
               if (this.isSet(LineReader.Option.HISTORY_VERIFY)) {
                  return true;
               }
            }
         } catch (IllegalArgumentException var5) {
         }
      }

      try {
         this.parsedLine = this.parser.parse(this.buf.toString(), this.buf.cursor(), Parser.ParseContext.ACCEPT_LINE);
      } catch (EOFError var3) {
         this.buf.write("\n");
         return true;
      } catch (SyntaxError var4) {
      }

      this.callWidget("callback-finish");
      this.state = LineReaderImpl.State.DONE;
      return true;
   }

   protected boolean selfInsert() {
      for (int var1 = this.count; var1 > 0; var1--) {
         this.putString(this.getLastBinding());
      }

      return true;
   }

   protected boolean selfInsertUnmeta() {
      if (this.getLastBinding().charAt(0) != 27) {
         return false;
      } else {
         String var1 = this.getLastBinding().substring(1);
         if ("\r".equals(var1)) {
            var1 = "\n";
         }

         for (int var2 = this.count; var2 > 0; var2--) {
            this.putString(var1);
         }

         return true;
      }
   }

   protected boolean overwriteMode() {
      this.overTyping = !this.overTyping;
      return true;
   }

   protected boolean beginningOfBufferOrHistory() {
      if (this.findbol() != 0) {
         this.buf.cursor(0);
         return true;
      } else {
         return this.beginningOfHistory();
      }
   }

   protected boolean beginningOfHistory() {
      if (this.history.moveToFirst()) {
         this.setBuffer(this.history.current());
         return true;
      } else {
         return false;
      }
   }

   protected boolean endOfBufferOrHistory() {
      if (this.findeol() != this.buf.length()) {
         this.buf.cursor(this.buf.length());
         return true;
      } else {
         return this.endOfHistory();
      }
   }

   protected boolean endOfHistory() {
      if (this.history.moveToLast()) {
         this.setBuffer(this.history.current());
         return true;
      } else {
         return false;
      }
   }

   protected boolean beginningOfLineHist() {
      if (this.count < 0) {
         return this.callNeg(this::endOfLineHist);
      } else {
         while (this.count-- > 0) {
            int var1 = this.findbol();
            if (var1 != this.buf.cursor()) {
               this.buf.cursor(var1);
            } else {
               this.moveHistory(false);
               this.buf.cursor(0);
            }
         }

         return true;
      }
   }

   protected boolean endOfLineHist() {
      if (this.count < 0) {
         return this.callNeg(this::beginningOfLineHist);
      } else {
         while (this.count-- > 0) {
            int var1 = this.findeol();
            if (var1 != this.buf.cursor()) {
               this.buf.cursor(var1);
            } else {
               this.moveHistory(true);
            }
         }

         return true;
      }
   }

   protected boolean upHistory() {
      while (this.count-- > 0) {
         if (!this.moveHistory(false)) {
            return !this.isSet(LineReader.Option.HISTORY_BEEP);
         }
      }

      return true;
   }

   protected boolean downHistory() {
      while (this.count-- > 0) {
         if (!this.moveHistory(true)) {
            return !this.isSet(LineReader.Option.HISTORY_BEEP);
         }
      }

      return true;
   }

   protected boolean viUpLineOrHistory() {
      return this.upLine() || this.upHistory() && this.viFirstNonBlank();
   }

   protected boolean viDownLineOrHistory() {
      return this.downLine() || this.downHistory() && this.viFirstNonBlank();
   }

   protected boolean upLine() {
      return this.buf.up();
   }

   protected boolean downLine() {
      return this.buf.down();
   }

   protected boolean upLineOrHistory() {
      return this.upLine() || this.upHistory();
   }

   protected boolean upLineOrSearch() {
      return this.upLine() || this.historySearchBackward();
   }

   protected boolean downLineOrHistory() {
      return this.downLine() || this.downHistory();
   }

   protected boolean downLineOrSearch() {
      return this.downLine() || this.historySearchForward();
   }

   protected boolean viCmdMode() {
      if (this.state == LineReaderImpl.State.NORMAL) {
         this.buf.move(-1);
      }

      return this.setKeyMap("vicmd");
   }

   protected boolean viInsert() {
      return this.setKeyMap("viins");
   }

   protected boolean viAddNext() {
      this.buf.move(1);
      return this.setKeyMap("viins");
   }

   protected boolean viAddEol() {
      return this.endOfLine() && this.setKeyMap("viins");
   }

   protected boolean emacsEditingMode() {
      return this.setKeyMap("emacs");
   }

   protected boolean viChangeWholeLine() {
      return this.viFirstNonBlank() && this.viChangeEol();
   }

   protected boolean viChangeEol() {
      return this.viChange(this.buf.cursor(), this.buf.length()) && this.setKeyMap("viins");
   }

   protected boolean viKillEol() {
      int var1 = this.findeol();
      if (this.buf.cursor() == var1) {
         return false;
      } else {
         this.killRing.add(this.buf.substring(this.buf.cursor(), var1));
         this.buf.delete(var1 - this.buf.cursor());
         return true;
      }
   }

   protected boolean quotedInsert() {
      int var1 = this.readCharacter();

      while (this.count-- > 0) {
         this.putString(new String(Character.toChars(var1)));
      }

      return true;
   }

   protected boolean viJoin() {
      if (!this.buf.down()) {
         return false;
      } else {
         while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
         }

         this.buf.backspace();
         this.buf.write(32);
         this.buf.move(-1);
         return true;
      }
   }

   protected boolean viKillWholeLine() {
      return this.killWholeLine() && this.setKeyMap("viins");
   }

   protected boolean viInsertBol() {
      return this.beginningOfLine() && this.setKeyMap("viins");
   }

   protected boolean backwardDeleteChar() {
      if (this.count < 0) {
         return this.callNeg(this::deleteChar);
      } else if (this.buf.cursor() == 0) {
         return false;
      } else {
         this.buf.backspace(this.count);
         return true;
      }
   }

   protected boolean viFirstNonBlank() {
      this.beginningOfLine();

      while (this.buf.cursor() < this.buf.length() && this.isWhitespace(this.buf.currChar())) {
         this.buf.move(1);
      }

      return true;
   }

   protected boolean viBeginningOfLine() {
      this.buf.cursor(this.findbol());
      return true;
   }

   protected boolean viEndOfLine() {
      if (this.count < 0) {
         return false;
      } else {
         while (this.count-- > 0) {
            this.buf.cursor(this.findeol() + 1);
         }

         this.buf.move(-1);
         return true;
      }
   }

   protected boolean beginningOfLine() {
      while (this.count-- > 0) {
         if (this.buf.move(-1) != -1 || this.buf.prevChar() == 10) {
         }
      }

      return true;
   }

   protected boolean endOfLine() {
      while (this.count-- > 0) {
         if (this.buf.move(1) != 1 || this.buf.currChar() == 10) {
         }
      }

      return true;
   }

   protected boolean deleteChar() {
      if (this.count < 0) {
         return this.callNeg(this::backwardDeleteChar);
      } else if (this.buf.cursor() == this.buf.length()) {
         return false;
      } else {
         this.buf.delete(this.count);
         return true;
      }
   }

   protected boolean viBackwardDeleteChar() {
      for (int var1 = 0; var1 < this.count; var1++) {
         if (!this.buf.backspace()) {
            return false;
         }
      }

      return true;
   }

   protected boolean viDeleteChar() {
      for (int var1 = 0; var1 < this.count; var1++) {
         if (!this.buf.delete()) {
            return false;
         }
      }

      return true;
   }

   protected boolean viSwapCase() {
      for (int var1 = 0; var1 < this.count; var1++) {
         if (this.buf.cursor() >= this.buf.length()) {
            return false;
         }

         int var2 = this.buf.atChar(this.buf.cursor());
         var2 = this.switchCase(var2);
         this.buf.currChar(var2);
         this.buf.move(1);
      }

      return true;
   }

   protected boolean viReplaceChars() {
      int var1 = this.readCharacter();
      if (var1 >= 0 && var1 != 27 && var1 != 3) {
         for (int var2 = 0; var2 < this.count; var2++) {
            if (!this.buf.currChar((char)var1)) {
               return false;
            }

            if (var2 < this.count - 1) {
               this.buf.move(1);
            }
         }

         return true;
      } else {
         return true;
      }
   }

   protected boolean viChange(int var1, int var2) {
      return this.doViDeleteOrChange(var1, var2, true);
   }

   protected boolean viDeleteTo(int var1, int var2) {
      return this.doViDeleteOrChange(var1, var2, false);
   }

   protected boolean doViDeleteOrChange(int var1, int var2, boolean var3) {
      if (var1 == var2) {
         return true;
      } else {
         if (var2 < var1) {
            int var4 = var2;
            var2 = var1;
            var1 = var4;
         }

         this.buf.cursor(var1);
         this.buf.delete(var2 - var1);
         if (!var3 && var1 > 0 && var1 == this.buf.length()) {
            this.buf.move(-1);
         }

         return true;
      }
   }

   protected boolean viYankTo(int var1, int var2) {
      int var3 = var1;
      if (var2 < var1) {
         int var4 = var2;
         var2 = var1;
         var1 = var4;
      }

      if (var1 == var2) {
         this.yankBuffer = "";
         return true;
      } else {
         this.yankBuffer = this.buf.substring(var1, var2);
         this.buf.cursor(var3);
         return true;
      }
   }

   protected boolean viOpenLineAbove() {
      while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
      }

      this.buf.write(10);
      this.buf.move(-1);
      return this.setKeyMap("viins");
   }

   protected boolean viOpenLineBelow() {
      while (this.buf.move(1) == 1 && this.buf.currChar() != 10) {
      }

      this.buf.write(10);
      return this.setKeyMap("viins");
   }

   protected boolean viPutAfter() {
      if (this.yankBuffer.indexOf(10) < 0) {
         if (this.yankBuffer.length() != 0) {
            if (this.buf.cursor() < this.buf.length()) {
               this.buf.move(1);
            }

            for (int var1 = 0; var1 < this.count; var1++) {
               this.putString(this.yankBuffer);
            }

            this.buf.move(-1);
         }
      } else {
         while (this.buf.move(1) == 1 && this.buf.currChar() != 10) {
         }

         this.buf.move(1);
         this.putString(this.yankBuffer);
         this.buf.move(-this.yankBuffer.length());
      }

      return true;
   }

   protected boolean viPutBefore() {
      if (this.yankBuffer.indexOf(10) < 0) {
         if (this.yankBuffer.length() != 0) {
            if (this.buf.cursor() > 0) {
               this.buf.move(-1);
            }

            for (int var1 = 0; var1 < this.count; var1++) {
               this.putString(this.yankBuffer);
            }

            this.buf.move(-1);
         }
      } else {
         while (this.buf.move(-1) == -1 && this.buf.prevChar() != 10) {
         }

         this.putString(this.yankBuffer);
         this.buf.move(-this.yankBuffer.length());
      }

      return true;
   }

   protected boolean doLowercaseVersion() {
      this.bindingReader.runMacro(this.getLastBinding().toLowerCase());
      return true;
   }

   protected boolean setMarkCommand() {
      if (this.count < 0) {
         this.regionActive = LineReader.RegionType.NONE;
         return true;
      } else {
         this.regionMark = this.buf.cursor();
         this.regionActive = LineReader.RegionType.CHAR;
         return true;
      }
   }

   protected boolean exchangePointAndMark() {
      if (this.count == 0) {
         this.regionActive = LineReader.RegionType.CHAR;
         return true;
      } else {
         int var1 = this.regionMark;
         this.regionMark = this.buf.cursor();
         this.buf.cursor(var1);
         if (this.buf.cursor() > this.buf.length()) {
            this.buf.cursor(this.buf.length());
         }

         if (this.count > 0) {
            this.regionActive = LineReader.RegionType.CHAR;
         }

         return true;
      }
   }

   protected boolean visualMode() {
      if (this.isInViMoveOperation()) {
         this.isArgDigit = true;
         this.forceLine = false;
         this.forceChar = true;
         return true;
      } else {
         if (this.regionActive == LineReader.RegionType.NONE) {
            this.regionMark = this.buf.cursor();
            this.regionActive = LineReader.RegionType.CHAR;
         } else if (this.regionActive == LineReader.RegionType.CHAR) {
            this.regionActive = LineReader.RegionType.NONE;
         } else if (this.regionActive == LineReader.RegionType.LINE) {
            this.regionActive = LineReader.RegionType.CHAR;
         }

         return true;
      }
   }

   protected boolean visualLineMode() {
      if (this.isInViMoveOperation()) {
         this.isArgDigit = true;
         this.forceLine = true;
         this.forceChar = false;
         return true;
      } else {
         if (this.regionActive == LineReader.RegionType.NONE) {
            this.regionMark = this.buf.cursor();
            this.regionActive = LineReader.RegionType.LINE;
         } else if (this.regionActive == LineReader.RegionType.CHAR) {
            this.regionActive = LineReader.RegionType.LINE;
         } else if (this.regionActive == LineReader.RegionType.LINE) {
            this.regionActive = LineReader.RegionType.NONE;
         }

         return true;
      }
   }

   protected boolean deactivateRegion() {
      this.regionActive = LineReader.RegionType.NONE;
      return true;
   }

   protected boolean whatCursorPosition() {
      this.post = () -> {
         AttributedStringBuilder var1 = new AttributedStringBuilder();
         if (this.buf.cursor() < this.buf.length()) {
            int var2 = this.buf.currChar();
            var1.append("Char: ");
            if (var2 == 32) {
               var1.append("SPC");
            } else if (var2 == 10) {
               var1.append("LFD");
            } else if (var2 < 32) {
               var1.append('^');
               var1.append((char)(var2 + 65 - 1));
            } else if (var2 == 127) {
               var1.append("^?");
            } else {
               var1.append((char)var2);
            }

            var1.append(" (");
            var1.append("0").append(Integer.toOctalString(var2)).append(" ");
            var1.append(Integer.toString(var2)).append(" ");
            var1.append("0x").append(Integer.toHexString(var2)).append(" ");
            var1.append(")");
         } else {
            var1.append("EOF");
         }

         var1.append("   ");
         var1.append("point ");
         var1.append(Integer.toString(this.buf.cursor() + 1));
         var1.append(" of ");
         var1.append(Integer.toString(this.buf.length() + 1));
         var1.append(" (");
         var1.append(Integer.toString(this.buf.length() == 0 ? 100 : 100 * this.buf.cursor() / this.buf.length()));
         var1.append("%)");
         var1.append("   ");
         var1.append("column ");
         var1.append(Integer.toString(this.buf.cursor() - this.findbol()));
         return var1.toAttributedString();
      };
      return true;
   }

   protected Map<String, Widget> builtinWidgets() {
      HashMap var1 = new HashMap();
      this.addBuiltinWidget(var1, "accept-and-infer-next-history", this::acceptAndInferNextHistory);
      this.addBuiltinWidget(var1, "accept-and-hold", this::acceptAndHold);
      this.addBuiltinWidget(var1, "accept-line", this::acceptLine);
      this.addBuiltinWidget(var1, "accept-line-and-down-history", this::acceptLineAndDownHistory);
      this.addBuiltinWidget(var1, "argument-base", this::argumentBase);
      this.addBuiltinWidget(var1, "backward-char", this::backwardChar);
      this.addBuiltinWidget(var1, "backward-delete-char", this::backwardDeleteChar);
      this.addBuiltinWidget(var1, "backward-delete-word", this::backwardDeleteWord);
      this.addBuiltinWidget(var1, "backward-kill-line", this::backwardKillLine);
      this.addBuiltinWidget(var1, "backward-kill-word", this::backwardKillWord);
      this.addBuiltinWidget(var1, "backward-word", this::backwardWord);
      this.addBuiltinWidget(var1, "beep", this::beep);
      this.addBuiltinWidget(var1, "beginning-of-buffer-or-history", this::beginningOfBufferOrHistory);
      this.addBuiltinWidget(var1, "beginning-of-history", this::beginningOfHistory);
      this.addBuiltinWidget(var1, "beginning-of-line", this::beginningOfLine);
      this.addBuiltinWidget(var1, "beginning-of-line-hist", this::beginningOfLineHist);
      this.addBuiltinWidget(var1, "capitalize-word", this::capitalizeWord);
      this.addBuiltinWidget(var1, "clear", this::clear);
      this.addBuiltinWidget(var1, "clear-screen", this::clearScreen);
      this.addBuiltinWidget(var1, "complete-prefix", this::completePrefix);
      this.addBuiltinWidget(var1, "complete-word", this::completeWord);
      this.addBuiltinWidget(var1, "copy-prev-word", this::copyPrevWord);
      this.addBuiltinWidget(var1, "copy-region-as-kill", this::copyRegionAsKill);
      this.addBuiltinWidget(var1, "delete-char", this::deleteChar);
      this.addBuiltinWidget(var1, "delete-char-or-list", this::deleteCharOrList);
      this.addBuiltinWidget(var1, "delete-word", this::deleteWord);
      this.addBuiltinWidget(var1, "digit-argument", this::digitArgument);
      this.addBuiltinWidget(var1, "do-lowercase-version", this::doLowercaseVersion);
      this.addBuiltinWidget(var1, "down-case-word", this::downCaseWord);
      this.addBuiltinWidget(var1, "down-line", this::downLine);
      this.addBuiltinWidget(var1, "down-line-or-history", this::downLineOrHistory);
      this.addBuiltinWidget(var1, "down-line-or-search", this::downLineOrSearch);
      this.addBuiltinWidget(var1, "down-history", this::downHistory);
      this.addBuiltinWidget(var1, "emacs-editing-mode", this::emacsEditingMode);
      this.addBuiltinWidget(var1, "emacs-backward-word", this::emacsBackwardWord);
      this.addBuiltinWidget(var1, "emacs-forward-word", this::emacsForwardWord);
      this.addBuiltinWidget(var1, "end-of-buffer-or-history", this::endOfBufferOrHistory);
      this.addBuiltinWidget(var1, "end-of-history", this::endOfHistory);
      this.addBuiltinWidget(var1, "end-of-line", this::endOfLine);
      this.addBuiltinWidget(var1, "end-of-line-hist", this::endOfLineHist);
      this.addBuiltinWidget(var1, "exchange-point-and-mark", this::exchangePointAndMark);
      this.addBuiltinWidget(var1, "expand-history", this::expandHistory);
      this.addBuiltinWidget(var1, "expand-or-complete", this::expandOrComplete);
      this.addBuiltinWidget(var1, "expand-or-complete-prefix", this::expandOrCompletePrefix);
      this.addBuiltinWidget(var1, "expand-word", this::expandWord);
      this.addBuiltinWidget(var1, "fresh-line", this::freshLine);
      this.addBuiltinWidget(var1, "forward-char", this::forwardChar);
      this.addBuiltinWidget(var1, "forward-word", this::forwardWord);
      this.addBuiltinWidget(var1, "history-incremental-search-backward", this::historyIncrementalSearchBackward);
      this.addBuiltinWidget(var1, "history-incremental-search-forward", this::historyIncrementalSearchForward);
      this.addBuiltinWidget(var1, "history-search-backward", this::historySearchBackward);
      this.addBuiltinWidget(var1, "history-search-forward", this::historySearchForward);
      this.addBuiltinWidget(var1, "insert-close-curly", this::insertCloseCurly);
      this.addBuiltinWidget(var1, "insert-close-paren", this::insertCloseParen);
      this.addBuiltinWidget(var1, "insert-close-square", this::insertCloseSquare);
      this.addBuiltinWidget(var1, "insert-comment", this::insertComment);
      this.addBuiltinWidget(var1, "kill-buffer", this::killBuffer);
      this.addBuiltinWidget(var1, "kill-line", this::killLine);
      this.addBuiltinWidget(var1, "kill-region", this::killRegion);
      this.addBuiltinWidget(var1, "kill-whole-line", this::killWholeLine);
      this.addBuiltinWidget(var1, "kill-word", this::killWord);
      this.addBuiltinWidget(var1, "list-choices", this::listChoices);
      this.addBuiltinWidget(var1, "menu-complete", this::menuComplete);
      this.addBuiltinWidget(var1, "menu-expand-or-complete", this::menuExpandOrComplete);
      this.addBuiltinWidget(var1, "neg-argument", this::negArgument);
      this.addBuiltinWidget(var1, "overwrite-mode", this::overwriteMode);
      this.addBuiltinWidget(var1, "quoted-insert", this::quotedInsert);
      this.addBuiltinWidget(var1, "redisplay", this::redisplay);
      this.addBuiltinWidget(var1, "redraw-line", this::redrawLine);
      this.addBuiltinWidget(var1, "redo", this::redo);
      this.addBuiltinWidget(var1, "self-insert", this::selfInsert);
      this.addBuiltinWidget(var1, "self-insert-unmeta", this::selfInsertUnmeta);
      this.addBuiltinWidget(var1, "abort", this::sendBreak);
      this.addBuiltinWidget(var1, "set-mark-command", this::setMarkCommand);
      this.addBuiltinWidget(var1, "transpose-chars", this::transposeChars);
      this.addBuiltinWidget(var1, "transpose-words", this::transposeWords);
      this.addBuiltinWidget(var1, "undefined-key", this::undefinedKey);
      this.addBuiltinWidget(var1, "universal-argument", this::universalArgument);
      this.addBuiltinWidget(var1, "undo", this::undo);
      this.addBuiltinWidget(var1, "up-case-word", this::upCaseWord);
      this.addBuiltinWidget(var1, "up-history", this::upHistory);
      this.addBuiltinWidget(var1, "up-line", this::upLine);
      this.addBuiltinWidget(var1, "up-line-or-history", this::upLineOrHistory);
      this.addBuiltinWidget(var1, "up-line-or-search", this::upLineOrSearch);
      this.addBuiltinWidget(var1, "vi-add-eol", this::viAddEol);
      this.addBuiltinWidget(var1, "vi-add-next", this::viAddNext);
      this.addBuiltinWidget(var1, "vi-backward-char", this::viBackwardChar);
      this.addBuiltinWidget(var1, "vi-backward-delete-char", this::viBackwardDeleteChar);
      this.addBuiltinWidget(var1, "vi-backward-blank-word", this::viBackwardBlankWord);
      this.addBuiltinWidget(var1, "vi-backward-blank-word-end", this::viBackwardBlankWordEnd);
      this.addBuiltinWidget(var1, "vi-backward-kill-word", this::viBackwardKillWord);
      this.addBuiltinWidget(var1, "vi-backward-word", this::viBackwardWord);
      this.addBuiltinWidget(var1, "vi-backward-word-end", this::viBackwardWordEnd);
      this.addBuiltinWidget(var1, "vi-beginning-of-line", this::viBeginningOfLine);
      this.addBuiltinWidget(var1, "vi-cmd-mode", this::viCmdMode);
      this.addBuiltinWidget(var1, "vi-digit-or-beginning-of-line", this::viDigitOrBeginningOfLine);
      this.addBuiltinWidget(var1, "vi-down-line-or-history", this::viDownLineOrHistory);
      this.addBuiltinWidget(var1, "vi-change-to", this::viChange);
      this.addBuiltinWidget(var1, "vi-change-eol", this::viChangeEol);
      this.addBuiltinWidget(var1, "vi-change-whole-line", this::viChangeWholeLine);
      this.addBuiltinWidget(var1, "vi-delete-char", this::viDeleteChar);
      this.addBuiltinWidget(var1, "vi-delete", this::viDelete);
      this.addBuiltinWidget(var1, "vi-end-of-line", this::viEndOfLine);
      this.addBuiltinWidget(var1, "vi-kill-eol", this::viKillEol);
      this.addBuiltinWidget(var1, "vi-first-non-blank", this::viFirstNonBlank);
      this.addBuiltinWidget(var1, "vi-find-next-char", this::viFindNextChar);
      this.addBuiltinWidget(var1, "vi-find-next-char-skip", this::viFindNextCharSkip);
      this.addBuiltinWidget(var1, "vi-find-prev-char", this::viFindPrevChar);
      this.addBuiltinWidget(var1, "vi-find-prev-char-skip", this::viFindPrevCharSkip);
      this.addBuiltinWidget(var1, "vi-forward-blank-word", this::viForwardBlankWord);
      this.addBuiltinWidget(var1, "vi-forward-blank-word-end", this::viForwardBlankWordEnd);
      this.addBuiltinWidget(var1, "vi-forward-char", this::viForwardChar);
      this.addBuiltinWidget(var1, "vi-forward-word", this::viForwardWord);
      this.addBuiltinWidget(var1, "vi-forward-word", this::viForwardWord);
      this.addBuiltinWidget(var1, "vi-forward-word-end", this::viForwardWordEnd);
      this.addBuiltinWidget(var1, "vi-history-search-backward", this::viHistorySearchBackward);
      this.addBuiltinWidget(var1, "vi-history-search-forward", this::viHistorySearchForward);
      this.addBuiltinWidget(var1, "vi-insert", this::viInsert);
      this.addBuiltinWidget(var1, "vi-insert-bol", this::viInsertBol);
      this.addBuiltinWidget(var1, "vi-insert-comment", this::viInsertComment);
      this.addBuiltinWidget(var1, "vi-join", this::viJoin);
      this.addBuiltinWidget(var1, "vi-kill-line", this::viKillWholeLine);
      this.addBuiltinWidget(var1, "vi-match-bracket", this::viMatchBracket);
      this.addBuiltinWidget(var1, "vi-open-line-above", this::viOpenLineAbove);
      this.addBuiltinWidget(var1, "vi-open-line-below", this::viOpenLineBelow);
      this.addBuiltinWidget(var1, "vi-put-after", this::viPutAfter);
      this.addBuiltinWidget(var1, "vi-put-before", this::viPutBefore);
      this.addBuiltinWidget(var1, "vi-repeat-find", this::viRepeatFind);
      this.addBuiltinWidget(var1, "vi-repeat-search", this::viRepeatSearch);
      this.addBuiltinWidget(var1, "vi-replace-chars", this::viReplaceChars);
      this.addBuiltinWidget(var1, "vi-rev-repeat-find", this::viRevRepeatFind);
      this.addBuiltinWidget(var1, "vi-rev-repeat-search", this::viRevRepeatSearch);
      this.addBuiltinWidget(var1, "vi-swap-case", this::viSwapCase);
      this.addBuiltinWidget(var1, "vi-up-line-or-history", this::viUpLineOrHistory);
      this.addBuiltinWidget(var1, "vi-yank", this::viYankTo);
      this.addBuiltinWidget(var1, "vi-yank-whole-line", this::viYankWholeLine);
      this.addBuiltinWidget(var1, "visual-line-mode", this::visualLineMode);
      this.addBuiltinWidget(var1, "visual-mode", this::visualMode);
      this.addBuiltinWidget(var1, "what-cursor-position", this::whatCursorPosition);
      this.addBuiltinWidget(var1, "yank", this::yank);
      this.addBuiltinWidget(var1, "yank-pop", this::yankPop);
      this.addBuiltinWidget(var1, "mouse", this::mouse);
      this.addBuiltinWidget(var1, "begin-paste", this::beginPaste);
      this.addBuiltinWidget(var1, "terminal-focus-in", this::focusIn);
      this.addBuiltinWidget(var1, "terminal-focus-out", this::focusOut);
      return var1;
   }

   private void addBuiltinWidget(Map<String, Widget> var1, String var2, Widget var3) {
      var1.put(var2, this.namedWidget(var2, var3));
   }

   private Widget namedWidget(final String var1, final Widget var2) {
      return new Widget() {
         @Override
         public String toString() {
            return var1;
         }

         @Override
         public boolean apply() {
            return var2.apply();
         }
      };
   }

   public boolean redisplay() {
      this.redisplay(true);
      return true;
   }

   protected void redisplay(boolean var1) {
      try {
         this.lock.lock();
         if (this.skipRedisplay) {
            this.skipRedisplay = false;
            return;
         }

         Status var2 = Status.getStatus(this.terminal, false);
         if (var2 != null) {
            var2.redraw();
         }

         if (this.size.getRows() <= 0 || this.size.getRows() >= 3) {
            ArrayList var22 = new ArrayList();
            AttributedString var23 = this.getDisplayedBufferWithPrompts(var22);
            Object var24;
            if (this.size.getColumns() <= 0) {
               var24 = new ArrayList();
               var24.add(var23);
            } else {
               var24 = var23.columnSplitLength(this.size.getColumns(), true, this.display.delayLineWrap());
            }

            Object var25;
            if (this.rightPrompt.length() != 0 && this.size.getColumns() > 0) {
               var25 = this.rightPrompt.columnSplitLength(this.size.getColumns());
            } else {
               var25 = new ArrayList();
            }

            while (var24.size() < var25.size()) {
               var24.add(new AttributedString(""));
            }

            for (int var26 = 0; var26 < var25.size(); var26++) {
               AttributedString var28 = (AttributedString)var25.get(var26);
               var24.set(var26, this.addRightPrompt(var28, (AttributedString)var24.get(var26)));
            }

            int var27 = -1;
            int var29 = -1;
            int var30 = -1;
            if (this.size.getColumns() > 0) {
               AttributedStringBuilder var31 = new AttributedStringBuilder().tabs(4);
               var31.append(this.prompt);
               String var33 = this.buf.upToCursor();
               if (this.maskingCallback != null) {
                  var33 = this.maskingCallback.display(var33);
               }

               var31.append(this.insertSecondaryPrompts(new AttributedString(var33), var22, false));
               List var12 = var31.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap());
               if (!var12.isEmpty()) {
                  var29 = var12.size() - 1;
                  var30 = ((AttributedString)var12.get(var12.size() - 1)).columnLength();
                  var27 = this.size.cursorPos(var29, var30);
               }
            }

            Object var32 = new ArrayList();
            int var34 = this.size.getRows() - (var2 != null ? var2.size() : 0);
            if (var24.size() > var34 && !this.isTerminalDumb()) {
               StringBuilder var35 = new StringBuilder(">....");

               for (int var13 = var35.toString().length(); var13 < this.size.getColumns(); var13++) {
                  var35.append(" ");
               }

               AttributedString var36 = new AttributedString(var35.toString());
               int var14 = var24.size() - var34 + 1;
               int var15 = var34;
               byte var16 = 1;
               if (var14 > var29) {
                  var14 = var29;
                  var15 = var34 - 1;
                  var16 = 0;
               } else {
                  var32.add(var36);
               }

               int var17 = 0;

               for (int var18 = var16; var18 < var15; var18++) {
                  if (var29 == var14) {
                     var17 = var18;
                  }

                  var32.add(var24.get(var14++));
               }

               if (var16 == 0) {
                  var32.add(var36);
               }

               var27 = this.size.cursorPos(var17, var30);
            } else {
               var32 = var24;
            }

            this.display.update((List<AttributedString>)var32, var27, var1);
            return;
         }

         AttributedStringBuilder var3 = new AttributedStringBuilder().tabs(4);
         var3.append(this.prompt);
         this.concat(this.getHighlightedBuffer(this.buf.toString()).columnSplitLength(Integer.MAX_VALUE), var3);
         AttributedString var4 = var3.toAttributedString();
         var3.setLength(0);
         var3.append(this.prompt);
         String var5 = this.buf.upToCursor();
         if (this.maskingCallback != null) {
            var5 = this.maskingCallback.display(var5);
         }

         this.concat(new AttributedString(var5).columnSplitLength(Integer.MAX_VALUE), var3);
         AttributedString var6 = var3.toAttributedString();
         int var7 = WCWidth.wcwidth(8230);
         int var8 = this.size.getColumns();
         int var9 = var6.columnLength();
         int var10 = var8 / 2 + 1;

         while (var9 <= this.smallTerminalOffset + var7) {
            this.smallTerminalOffset -= var10;
         }

         while (var9 >= this.smallTerminalOffset + var8 - var7) {
            this.smallTerminalOffset += var10;
         }

         if (this.smallTerminalOffset > 0) {
            var3.setLength(0);
            var3.append("…");
            var3.append(var4.columnSubSequence(this.smallTerminalOffset + var7, Integer.MAX_VALUE));
            var4 = var3.toAttributedString();
         }

         int var11 = var4.columnLength();
         if (var11 >= this.smallTerminalOffset + var8) {
            var3.setLength(0);
            var3.append(var4.columnSubSequence(0, var8 - var7));
            var3.append("…");
            var4 = var3.toAttributedString();
         }

         this.display.update(Collections.singletonList(var4), var9 - this.smallTerminalOffset, var1);
      } finally {
         this.lock.unlock();
      }
   }

   private void concat(List<AttributedString> var1, AttributedStringBuilder var2) {
      if (var1.size() > 1) {
         for (int var3 = 0; var3 < var1.size() - 1; var3++) {
            var2.append((AttributedString)var1.get(var3));
            var2.style(var2.style().inverse());
            var2.append("\\n");
            var2.style(var2.style().inverseOff());
         }
      }

      var2.append((AttributedString)var1.get(var1.size() - 1));
   }

   public AttributedString getDisplayedBufferWithPrompts(List<AttributedString> var1) {
      AttributedString var2 = this.getHighlightedBuffer(this.buf.toString());
      AttributedString var3 = this.insertSecondaryPrompts(var2, var1);
      AttributedStringBuilder var4 = new AttributedStringBuilder().tabs(4);
      var4.append(this.prompt);
      var4.append(var3);
      if (this.post != null) {
         var4.append("\n");
         var4.append(this.post.get());
      }

      return var4.toAttributedString();
   }

   private AttributedString getHighlightedBuffer(String var1) {
      if (this.maskingCallback != null) {
         var1 = this.maskingCallback.display(var1);
      }

      return this.highlighter != null && !this.isSet(LineReader.Option.DISABLE_HIGHLIGHTER)
         ? this.highlighter.highlight(this, var1)
         : new AttributedString(var1);
   }

   private AttributedString expandPromptPattern(String var1, int var2, String var3, int var4) {
      ArrayList var5 = new ArrayList();
      boolean var6 = false;
      int var7 = -1;
      StringBuilder var8 = null;
      StringBuilder var9 = new StringBuilder();
      var1 = var1 + "%{";
      int var10 = var1.length();
      int var11 = -1;
      int var12 = -1;
      int var13 = 0;
      int var14 = 0;

      label104:
      while (var14 < var10) {
         char var15 = var1.charAt(var14++);
         if (var15 == '%' && var14 < var10) {
            int var16 = 0;
            boolean var17 = false;

            while (true) {
               var15 = var1.charAt(var14++);
               switch (var15) {
                  case '%':
                     var9.append(var15);
                     continue label104;
                  case '-':
                  case '0':
                  case '1':
                  case '2':
                  case '3':
                  case '4':
                  case '5':
                  case '6':
                  case '7':
                  case '8':
                  case '9':
                     boolean var20 = false;
                     if (var15 == '-') {
                        var20 = true;
                        var15 = var1.charAt(var14++);
                     }

                     var17 = true;

                     for (var16 = 0; var15 >= '0' && var15 <= '9'; var15 = var1.charAt(var14++)) {
                        var16 = (var16 < 0 ? 0 : 10 * var16) + (var15 - '0');
                     }

                     if (var20) {
                        var16 = -var16;
                     }

                     var14--;
                     break;
                  case 'M':
                     if (var3 != null) {
                        var9.append(var3);
                     }
                     continue label104;
                  case 'N':
                     var9.append(this.getInt("line-offset", 0) + var4);
                     continue label104;
                  case 'P':
                     if (var17 && var16 >= 0) {
                        var2 = var16;
                     }

                     if (var14 < var10) {
                        var11 = var1.charAt(var14++);
                     }

                     var12 = var9.length();
                     var7 = var5.size();
                     continue label104;
                  case '{':
                  case '}':
                     String var18 = var9.toString();
                     AttributedString var19;
                     if (!var6) {
                        var19 = AttributedString.fromAnsi(var18);
                        var13 += var19.columnLength();
                     } else {
                        var19 = new AttributedString(var18, AttributedStyle.HIDDEN);
                     }

                     if (var7 == var5.size()) {
                        var8 = var9;
                        if (var14 < var10) {
                           var9 = new StringBuilder();
                        }
                     } else {
                        var9.setLength(0);
                     }

                     var5.add(var19);
                     var6 = var15 == '{';
                  default:
                     continue label104;
               }
            }
         } else {
            var9.append(var15);
         }
      }

      if (var2 > var13) {
         var14 = WCWidth.wcwidth(var11);
         int var25 = (var2 - var13) / var14;
         var9 = var8;

         while (--var25 >= 0) {
            var9.insert(var12, (char)var11);
         }

         var5.set(var7, AttributedString.fromAnsi(var9.toString()));
      }

      return AttributedString.join(null, var5);
   }

   private AttributedString insertSecondaryPrompts(AttributedString var1, List<AttributedString> var2) {
      return this.insertSecondaryPrompts(var1, var2, true);
   }

   private AttributedString insertSecondaryPrompts(AttributedString var1, List<AttributedString> var2, boolean var3) {
      Objects.requireNonNull(var2);
      List var4 = var1.columnSplitLength(Integer.MAX_VALUE);
      AttributedStringBuilder var5 = new AttributedStringBuilder();
      String var6 = this.getString("secondary-prompt-pattern", "%M> ");
      boolean var7 = var6.contains("%M");
      AttributedStringBuilder var8 = new AttributedStringBuilder();
      int var9 = 0;
      ArrayList var10 = new ArrayList();
      if (var3 && var6.contains("%P")) {
         var9 = this.prompt.columnLength();

         for (int var11 = 0; var11 < var4.size() - 1; var11++) {
            var8.append((AttributedString)var4.get(var11)).append("\n");
            String var13 = "";
            if (var7) {
               try {
                  this.parser.parse(var8.toString(), var8.length(), Parser.ParseContext.SECONDARY_PROMPT);
               } catch (EOFError var17) {
                  var13 = var17.getMissing();
               } catch (SyntaxError var18) {
               }
            }

            var10.add(var13);
            AttributedString var12 = this.expandPromptPattern(var6, 0, var13, var11 + 1);
            var9 = Math.max(var9, var12.columnLength());
         }

         var8.setLength(0);
      }

      int var19;
      for (var19 = 0; var19 < var4.size() - 1; var19++) {
         var5.append((AttributedString)var4.get(var19)).append("\n");
         var8.append((AttributedString)var4.get(var19)).append("\n");
         AttributedString var20;
         if (var3) {
            String var21 = "";
            if (var7) {
               if (var10.isEmpty()) {
                  try {
                     this.parser.parse(var8.toString(), var8.length(), Parser.ParseContext.SECONDARY_PROMPT);
                  } catch (EOFError var15) {
                     var21 = var15.getMissing();
                  } catch (SyntaxError var16) {
                  }
               } else {
                  var21 = (String)var10.get(var19);
               }
            }

            var20 = this.expandPromptPattern(var6, var9, var21, var19 + 1);
         } else {
            var20 = (AttributedString)var2.get(var19);
         }

         var2.add(var20);
         var5.append(var20);
      }

      var5.append((AttributedString)var4.get(var19));
      var8.append((AttributedString)var4.get(var19));
      return var5.toAttributedString();
   }

   private AttributedString addRightPrompt(AttributedString var1, AttributedString var2) {
      int var3 = var1.columnLength();
      boolean var4 = var2.length() > 0 && var2.charAt(var2.length() - 1) == '\n';
      int var5 = this.size.getColumns() - var3 - (var2.columnLength() + (var4 ? 1 : 0));
      if (var5 >= 3) {
         AttributedStringBuilder var6 = new AttributedStringBuilder(this.size.getColumns());
         var6.append(var2, 0, var4 ? var2.length() - 1 : var2.length());

         for (int var7 = 0; var7 < var5; var7++) {
            var6.append(' ');
         }

         var6.append(var1);
         if (var4) {
            var6.append('\n');
         }

         var2 = var6.toAttributedString();
      }

      return var2;
   }

   protected boolean insertTab() {
      return this.isSet(LineReader.Option.INSERT_TAB) && this.getLastBinding().equals("\t") && this.buf.toString().matches("(^|[\\s\\S]*\n)[\r\n\t ]*");
   }

   protected boolean expandHistory() {
      String var1 = this.buf.toString();
      String var2 = this.expander.expandHistory(this.history, var1);
      if (!var2.equals(var1)) {
         this.buf.clear();
         this.buf.write(var2);
         return true;
      } else {
         return false;
      }
   }

   protected boolean expandWord() {
      return this.insertTab() ? this.selfInsert() : this.doComplete(LineReaderImpl.CompletionType.Expand, this.isSet(LineReader.Option.MENU_COMPLETE), false);
   }

   protected boolean expandOrComplete() {
      return this.insertTab()
         ? this.selfInsert()
         : this.doComplete(LineReaderImpl.CompletionType.ExpandComplete, this.isSet(LineReader.Option.MENU_COMPLETE), false);
   }

   protected boolean expandOrCompletePrefix() {
      return this.insertTab()
         ? this.selfInsert()
         : this.doComplete(LineReaderImpl.CompletionType.ExpandComplete, this.isSet(LineReader.Option.MENU_COMPLETE), true);
   }

   protected boolean completeWord() {
      return this.insertTab() ? this.selfInsert() : this.doComplete(LineReaderImpl.CompletionType.Complete, this.isSet(LineReader.Option.MENU_COMPLETE), false);
   }

   protected boolean menuComplete() {
      return this.insertTab() ? this.selfInsert() : this.doComplete(LineReaderImpl.CompletionType.Complete, true, false);
   }

   protected boolean menuExpandOrComplete() {
      return this.insertTab() ? this.selfInsert() : this.doComplete(LineReaderImpl.CompletionType.ExpandComplete, true, false);
   }

   protected boolean completePrefix() {
      return this.insertTab() ? this.selfInsert() : this.doComplete(LineReaderImpl.CompletionType.Complete, this.isSet(LineReader.Option.MENU_COMPLETE), true);
   }

   protected boolean listChoices() {
      return this.doComplete(LineReaderImpl.CompletionType.List, this.isSet(LineReader.Option.MENU_COMPLETE), false);
   }

   protected boolean deleteCharOrList() {
      return this.buf.cursor() == this.buf.length() && this.buf.length() != 0
         ? this.doComplete(LineReaderImpl.CompletionType.List, this.isSet(LineReader.Option.MENU_COMPLETE), false)
         : this.deleteChar();
   }

   protected boolean doComplete(LineReaderImpl.CompletionType var1, boolean var2, boolean var3) {
      if (this.getBoolean("disable-completion", false)) {
         return true;
      } else {
         if (!this.isSet(LineReader.Option.DISABLE_EVENT_EXPANSION)) {
            try {
               if (this.expandHistory()) {
                  return true;
               }
            } catch (Exception var25) {
               Log.info("Error while expanding history", var25);
               return false;
            }
         }

         CompletingParsedLine var4;
         try {
            var4 = this.wrap(this.parser.parse(this.buf.toString(), this.buf.cursor(), Parser.ParseContext.COMPLETE));
         } catch (Exception var24) {
            Log.info("Error while parsing line", var24);
            return false;
         }

         ArrayList var5 = new ArrayList();

         try {
            if (this.completer != null) {
               this.completer.complete(this, var4, var5);
            }
         } catch (Exception var23) {
            Log.info("Error while finding completion candidates", var23);
            return false;
         }

         if (var1 == LineReaderImpl.CompletionType.ExpandComplete || var1 == LineReaderImpl.CompletionType.Expand) {
            String var6 = this.expander.expandVar(var4.word());
            if (!var4.word().equals(var6)) {
               if (var3) {
                  this.buf.backspace(var4.wordCursor());
               } else {
                  this.buf.move(var4.word().length() - var4.wordCursor());
                  this.buf.backspace(var4.word().length());
               }

               this.buf.write(var6);
               return true;
            }

            if (var1 == LineReaderImpl.CompletionType.Expand) {
               return false;
            }

            var1 = LineReaderImpl.CompletionType.Complete;
         }

         boolean var27 = this.isSet(LineReader.Option.CASE_INSENSITIVE);
         int var7 = this.getInt("errors", 2);
         HashMap var8 = new HashMap();

         for (Candidate var10 : var5) {
            var8.computeIfAbsent(AttributedString.fromAnsi(var10.value()).toString(), var0 -> new ArrayList<>()).add(var10);
         }

         List var28;
         Predicate var29;
         if (var3) {
            String var11 = var4.word();
            String var12 = var27 ? var11.toLowerCase() : var11;
            String var13 = var12.substring(0, var4.wordCursor());
            var28 = Arrays.asList(
               this.simpleMatcher(var2x -> (var27 ? var2x.toLowerCase() : var2x).startsWith(var13)),
               this.simpleMatcher(var2x -> (var27 ? var2x.toLowerCase() : var2x).contains(var13)),
               this.typoMatcher(var13, var7, var27)
            );
            var29 = var2x -> var27 ? var2x.equalsIgnoreCase(var13) : var2x.equals(var13);
         } else if (this.isSet(LineReader.Option.COMPLETE_IN_WORD)) {
            String var30 = var4.word();
            String var33 = var27 ? var30.toLowerCase() : var30;
            String var38 = var33.substring(0, var4.wordCursor());
            String var14 = var33.substring(var4.wordCursor());
            Pattern var15 = Pattern.compile(Pattern.quote(var38) + ".*" + Pattern.quote(var14) + ".*");
            Pattern var16 = Pattern.compile(".*" + Pattern.quote(var38) + ".*" + Pattern.quote(var14) + ".*");
            var28 = Arrays.asList(
               this.simpleMatcher(var2x -> var15.matcher(var27 ? var2x.toLowerCase() : var2x).matches()),
               this.simpleMatcher(var2x -> var16.matcher(var27 ? var2x.toLowerCase() : var2x).matches()),
               this.typoMatcher(var33, var7, var27)
            );
            var29 = var2x -> var27 ? var2x.equalsIgnoreCase(var30) : var2x.equals(var30);
         } else {
            String var31 = var4.word();
            String var34 = var27 ? var31.toLowerCase() : var31;
            var28 = Arrays.asList(
               this.simpleMatcher(var2x -> (var27 ? var2x.toLowerCase() : var2x).startsWith(var34)),
               this.simpleMatcher(var2x -> (var27 ? var2x.toLowerCase() : var2x).contains(var34)),
               this.typoMatcher(var34, var7, var27)
            );
            var29 = var2x -> var27 ? var2x.equalsIgnoreCase(var31) : var2x.equals(var31);
         }

         Map var32 = Collections.emptyMap();

         for (Function var39 : var28) {
            var32 = (Map)var39.apply(var8);
            if (!var32.isEmpty()) {
               break;
            }
         }

         if (var32.isEmpty()) {
            return false;
         } else {
            this.size.copy(this.terminal.getSize());

            boolean var41;
            try {
               if (var1 == LineReaderImpl.CompletionType.List) {
                  List var37 = var32.entrySet().stream().flatMap(var0 -> ((List)var0.getValue()).stream()).collect(Collectors.toList());
                  this.doList(var37, var4.word(), false, var4::escape);
                  return !var37.isEmpty();
               }

               Candidate var36 = null;
               if (var32.size() == 1) {
                  var36 = var32.values().stream().flatMap(Collection::stream).findFirst().orElse(null);
               } else if (this.isSet(LineReader.Option.RECOGNIZE_EXACT)) {
                  var36 = var32.values()
                     .stream()
                     .flatMap(Collection::stream)
                     .filter(Candidate::complete)
                     .filter(var1x -> var29.test(var1x.value()))
                     .findFirst()
                     .orElse(null);
               }

               if (var36 == null || var36.value().isEmpty()) {
                  List var42 = var32.entrySet().stream().flatMap(var0 -> ((List)var0.getValue()).stream()).collect(Collectors.toList());
                  if (var2) {
                     this.buf.move(var4.word().length() - var4.wordCursor());
                     this.buf.backspace(var4.word().length());
                     this.doMenu(var42, var4.word(), var4::escape);
                     return true;
                  }

                  String var45;
                  if (var3) {
                     var45 = var4.word().substring(0, var4.wordCursor());
                  } else {
                     var45 = var4.word();
                     this.buf.move(var4.rawWordLength() - var4.rawWordCursor());
                  }

                  String var48 = null;

                  for (String var17 : var32.keySet()) {
                     var48 = var48 == null ? var17 : this.getCommonStart(var48, var17, var27);
                  }

                  boolean var50 = var48.startsWith(var45) && !var48.equals(var45);
                  if (var50) {
                     this.buf.backspace(var4.rawWordLength());
                     this.buf.write(var4.escape(var48, false));
                     var45 = var48;
                     if ((
                           !this.isSet(LineReader.Option.AUTO_LIST) && this.isSet(LineReader.Option.AUTO_MENU)
                              || this.isSet(LineReader.Option.AUTO_LIST) && this.isSet(LineReader.Option.LIST_AMBIGUOUS)
                        )
                        && !this.nextBindingIsComplete()) {
                        return true;
                     }
                  }

                  if (!this.isSet(LineReader.Option.AUTO_LIST) || this.doList(var42, var45, true, var4::escape)) {
                     if (this.isSet(LineReader.Option.AUTO_MENU)) {
                        this.buf.backspace(var45.length());
                        this.doMenu(var42, var4.word(), var4::escape);
                     }

                     return true;
                  }

                  return true;
               }

               if (var3) {
                  this.buf.backspace(var4.rawWordCursor());
               } else {
                  this.buf.move(var4.rawWordLength() - var4.rawWordCursor());
                  this.buf.backspace(var4.rawWordLength());
               }

               this.buf.write(var4.escape(var36.value(), var36.complete()));
               if (var36.complete()) {
                  if (this.buf.currChar() != 32) {
                     this.buf.write(" ");
                  } else {
                     this.buf.move(1);
                  }
               }

               if (var36.suffix() != null) {
                  this.redisplay();
                  Binding var40 = this.readBinding(this.getKeys());
                  if (var40 != null) {
                     String var44 = this.getString("REMOVE_SUFFIX_CHARS", " \t\n;&|");
                     String var47 = var40 instanceof Reference ? ((Reference)var40).name() : null;
                     if ("self-insert".equals(var47) && var44.indexOf(this.getLastBinding().charAt(0)) >= 0 || "accept-line".equals(var47)) {
                        this.buf.backspace(var36.suffix().length());
                        if (this.getLastBinding().charAt(0) != ' ') {
                           this.buf.write(32);
                        }
                     }

                     this.pushBackBinding(true);
                  }
               }

               var41 = true;
            } finally {
               this.size.copy(this.terminal.getBufferSize());
            }

            return var41;
         }
      }
   }

   private CompletingParsedLine wrap(final ParsedLine var1) {
      return var1 instanceof CompletingParsedLine ? (CompletingParsedLine)var1 : new CompletingParsedLine() {
         @Override
         public String word() {
            return var1.word();
         }

         @Override
         public int wordCursor() {
            return var1.wordCursor();
         }

         @Override
         public int wordIndex() {
            return var1.wordIndex();
         }

         @Override
         public List<String> words() {
            return var1.words();
         }

         @Override
         public String line() {
            return var1.line();
         }

         @Override
         public int cursor() {
            return var1.cursor();
         }

         @Override
         public CharSequence escape(CharSequence var1x, boolean var2) {
            return var1x;
         }

         @Override
         public int rawWordCursor() {
            return this.wordCursor();
         }

         @Override
         public int rawWordLength() {
            return this.word().length();
         }
      };
   }

   protected Comparator<Candidate> getCandidateComparator(boolean var1, String var2) {
      String var3 = var1 ? var2.toLowerCase() : var2;
      ToIntFunction var4 = var3x -> this.distance(var3, var1 ? var3x.toLowerCase() : var3x);
      return Comparator.comparing(Candidate::value, Comparator.comparingInt(var4))
         .thenComparing(Candidate::value, Comparator.comparingInt(String::length))
         .thenComparing(Comparator.naturalOrder());
   }

   protected String getOthersGroupName() {
      return this.getString("OTHERS_GROUP_NAME", "others");
   }

   protected String getOriginalGroupName() {
      return this.getString("ORIGINAL_GROUP_NAME", "original");
   }

   protected Comparator<String> getGroupComparator() {
      return Comparator.<String>comparingInt(var1 -> this.getOthersGroupName().equals(var1) ? 1 : (this.getOriginalGroupName().equals(var1) ? -1 : 0))
         .thenComparing(String::toLowerCase, Comparator.naturalOrder());
   }

   private void mergeCandidates(List<Candidate> var1) {
      HashMap var2 = new HashMap();

      for (Candidate var4 : var1) {
         if (var4.key() != null) {
            List var5 = var2.computeIfAbsent(var4.key(), var0 -> new ArrayList());
            var5.add(var4);
         }
      }

      if (!var2.isEmpty()) {
         for (List var8 : var2.values()) {
            if (var8.size() >= 1) {
               var1.removeAll(var8);
               var8.sort(Comparator.comparing(Candidate::value));
               Candidate var9 = (Candidate)var8.get(0);
               String var6 = var8.stream().map(Candidate::displ).collect(Collectors.joining(" "));
               var1.add(new Candidate(var9.value(), var6, var9.group(), var9.descr(), var9.suffix(), null, var9.complete()));
            }
         }
      }
   }

   private Function<Map<String, List<Candidate>>, Map<String, List<Candidate>>> simpleMatcher(Predicate<String> var1) {
      return var1x -> var1x.entrySet().stream().filter(var1xx -> var1.test(var1xx.getKey())).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
   }

   private Function<Map<String, List<Candidate>>, Map<String, List<Candidate>>> typoMatcher(String var1, int var2, boolean var3) {
      return var4 -> {
         Map var5 = var4.entrySet()
            .stream()
            .filter(var4x -> this.distance(var1, var3 ? var4x.getKey() : var4x.getKey().toLowerCase()) < var2)
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
         if (var5.size() > 1) {
            var5.computeIfAbsent(var1, var0 -> new ArrayList<>()).add(new Candidate(var1, var1, this.getOriginalGroupName(), null, null, null, false));
         }

         return var5;
      };
   }

   private int distance(String var1, String var2) {
      if (var1.length() < var2.length()) {
         int var3 = Levenshtein.distance(var1, var2.substring(0, Math.min(var2.length(), var1.length())));
         int var4 = Levenshtein.distance(var1, var2);
         return Math.min(var3, var4);
      } else {
         return Levenshtein.distance(var1, var2);
      }
   }

   protected boolean nextBindingIsComplete() {
      this.redisplay();
      KeyMap var1 = this.keyMaps.get("menu");
      Binding var2 = this.readBinding(this.getKeys(), var1);
      if (var2 instanceof Reference && "menu-complete".equals(((Reference)var2).name())) {
         return true;
      } else {
         this.pushBackBinding();
         return false;
      }
   }

   protected boolean doMenu(List<Candidate> var1, String var2, BiFunction<CharSequence, Boolean, CharSequence> var3) {
      ArrayList var4 = new ArrayList();
      boolean var5 = this.isSet(LineReader.Option.CASE_INSENSITIVE);
      var1.sort(this.getCandidateComparator(var5, var2));
      this.mergeCandidates(var1);
      this.computePost(var1, null, var4, var2);
      LineReaderImpl.MenuSupport var6 = new LineReaderImpl.MenuSupport(var1, var2, var3);
      this.post = var6;
      this.redisplay();

      Binding var8;
      for (KeyMap var7 = this.keyMaps.get("menu"); (var8 = this.readBinding(this.getKeys(), var7)) != null; this.redisplay()) {
         String var9 = var8 instanceof Reference ? ((Reference)var8).name() : "";
         switch (var9) {
            case "menu-complete":
               var6.next();
               break;
            case "reverse-menu-complete":
               var6.previous();
               break;
            case "up-line-or-history":
            case "up-line-or-search":
               var6.up();
               break;
            case "down-line-or-history":
            case "down-line-or-search":
               var6.down();
               break;
            case "forward-char":
               var6.right();
               break;
            case "backward-char":
               var6.left();
               break;
            case "clear-screen":
               this.clearScreen();
               break;
            default:
               Candidate var12 = var6.completion();
               if (var12.suffix() != null) {
                  String var13 = this.getString("REMOVE_SUFFIX_CHARS", " \t\n;&|");
                  if ("self-insert".equals(var9) && var13.indexOf(this.getLastBinding().charAt(0)) >= 0 || "backward-delete-char".equals(var9)) {
                     this.buf.backspace(var12.suffix().length());
                  }
               }

               if (var12.complete() && this.getLastBinding().charAt(0) != ' ' && ("self-insert".equals(var9) || this.getLastBinding().charAt(0) != ' ')) {
                  this.buf.write(32);
               }

               if (!"accept-line".equals(var9) && (!"self-insert".equals(var9) || var12.suffix() == null || !var12.suffix().startsWith(this.getLastBinding()))) {
                  this.pushBackBinding(true);
               }

               this.post = null;
               return true;
         }
      }

      return false;
   }

   protected boolean doList(List<Candidate> var1, String var2, boolean var3, BiFunction<CharSequence, Boolean, CharSequence> var4) {
      this.mergeCandidates(var1);
      AttributedString var5 = this.insertSecondaryPrompts(AttributedStringBuilder.append(this.prompt, this.buf.toString()), new ArrayList<>());
      int var6 = var5.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap()).size();
      LineReaderImpl.PostResult var7 = this.computePost(var1, null, null, var2);
      int var8 = var7.lines;
      int var9 = this.getInt("list-max", 100);
      if (var9 > 0 && var1.size() >= var9 || var8 >= this.size.getRows() - var6) {
         this.post = () -> new AttributedString(this.getAppName() + ": do you wish to see all " + var1.size() + " possibilities (" + var8 + " lines)?");
         this.redisplay(true);
         int var10 = this.readCharacter();
         if (var10 != 121 && var10 != 89 && var10 != 9) {
            this.post = null;
            return false;
         }
      }

      boolean var16 = this.isSet(LineReader.Option.CASE_INSENSITIVE);
      StringBuilder var11 = new StringBuilder();

      while (true) {
         String var12 = var2 + var11.toString();
         List var13;
         if (var11.length() > 0) {
            var13 = var1.stream()
               .filter(var2x -> var16 ? var2x.value().toLowerCase().startsWith(var12.toLowerCase()) : var2x.value().startsWith(var12))
               .sorted(this.getCandidateComparator(var16, var12))
               .collect(Collectors.toList());
         } else {
            var13 = var1.stream().sorted(this.getCandidateComparator(var16, var12)).collect(Collectors.toList());
         }

         this.post = () -> {
            AttributedString var4x = this.insertSecondaryPrompts(AttributedStringBuilder.append(this.prompt, this.buf.toString()), new ArrayList<>());
            int var5x = var4x.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap()).size();
            LineReaderImpl.PostResult var6x = this.computePost(var13, null, null, var12);
            if (var6x.lines >= this.size.getRows() - var5x) {
               this.post = null;
               int var7x = this.buf.cursor();
               this.buf.cursor(this.buf.length());
               this.redisplay(false);
               this.buf.cursor(var7x);
               this.println();
               List var8x = var7.post.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap());
               Display var9x = new Display(this.terminal, false);
               var9x.resize(this.size.getRows(), this.size.getColumns());
               var9x.update(var8x, -1);
               this.redrawLine();
               return new AttributedString("");
            } else {
               return var6x.post;
            }
         };
         if (!var3) {
            return false;
         }

         this.redisplay();
         Binding var14 = this.doReadBinding(this.getKeys(), null);
         if (var14 instanceof Reference) {
            String var15 = ((Reference)var14).name();
            if (!"backward-delete-char".equals(var15) && !"vi-backward-delete-char".equals(var15)) {
               if (!"self-insert".equals(var15)) {
                  if (!"\t".equals(this.getLastBinding())) {
                     this.pushBackBinding();
                     this.post = null;
                     return false;
                  }

                  if (var13.size() == 1 || var11.length() > 0) {
                     this.post = null;
                     this.pushBackBinding();
                  } else if (this.isSet(LineReader.Option.AUTO_MENU)) {
                     this.buf.backspace(((CharSequence)var4.apply(var12, false)).length());
                     this.doMenu(var13, var12, var4);
                  }

                  return false;
               }

               var11.append(this.getLastBinding());
               this.buf.write(this.getLastBinding());
               if (var13.isEmpty()) {
                  this.post = null;
                  return false;
               }
            } else {
               if (var11.length() == 0) {
                  this.pushBackBinding();
                  this.post = null;
                  return false;
               }

               var11.setLength(var11.length() - 1);
               this.buf.backspace();
            }
         } else if (var14 == null) {
            this.post = null;
            return false;
         }
      }
   }

   protected LineReaderImpl.PostResult computePost(List<Candidate> var1, Candidate var2, List<Candidate> var3, String var4) {
      return this.computePost(
         var1,
         var2,
         var3,
         var4,
         this.display::wcwidth,
         this.size.getColumns(),
         this.isSet(LineReader.Option.AUTO_GROUP),
         this.isSet(LineReader.Option.GROUP),
         this.isSet(LineReader.Option.LIST_ROWS_FIRST)
      );
   }

   protected LineReaderImpl.PostResult computePost(
      List<Candidate> var1,
      Candidate var2,
      List<Candidate> var3,
      String var4,
      Function<String, Integer> var5,
      int var6,
      boolean var7,
      boolean var8,
      boolean var9
   ) {
      ArrayList var10 = new ArrayList();
      if (var8) {
         Comparator var11 = this.getGroupComparator();
         Object var12 = var11 != null ? new TreeMap(var11) : new LinkedHashMap();

         for (Candidate var14 : var1) {
            String var15 = var14.group();
            var12.computeIfAbsent(var15 != null ? var15 : "", var0 -> new LinkedHashMap<>()).put(var14.value(), var14);
         }

         for (Entry var20 : var12.entrySet()) {
            String var22 = (String)var20.getKey();
            if (var22.isEmpty() && var12.size() > 1) {
               var22 = this.getOthersGroupName();
            }

            if (!var22.isEmpty() && var7) {
               var10.add(var22);
            }

            var10.add(new ArrayList(((Map)var20.getValue()).values()));
            if (var3 != null) {
               var3.addAll(((Map)var20.getValue()).values());
            }
         }
      } else {
         LinkedHashSet var16 = new LinkedHashSet();
         TreeMap var17 = new TreeMap();

         for (Candidate var21 : var1) {
            String var23 = var21.group();
            if (var23 != null) {
               var16.add(var23);
            }

            var17.put(var21.value(), var21);
         }

         if (var7) {
            var10.addAll(var16);
         }

         var10.add(new ArrayList(var17.values()));
         if (var3 != null) {
            var3.addAll(var17.values());
         }
      }

      return this.toColumns(var10, var2, var4, var5, var6, var9);
   }

   protected LineReaderImpl.PostResult toColumns(List<Object> var1, Candidate var2, String var3, Function<String, Integer> var4, int var5, boolean var6) {
      int[] var7 = new int[2];
      int var8 = 0;

      for (Object var10 : var1) {
         if (var10 instanceof String) {
            int var16 = (Integer)var4.apply((String)var10);
            var8 = Math.max(var8, var16);
         } else if (var10 instanceof List) {
            for (Candidate var12 : (List)var10) {
               int var13 = (Integer)var4.apply(var12.displ());
               if (var12.descr() != null) {
                  var13 = ++var13 + "(".length();
                  var13 += var4.apply(var12.descr());
                  var13 += ")".length();
               }

               var8 = Math.max(var8, var13);
            }
         }
      }

      AttributedStringBuilder var14 = new AttributedStringBuilder();

      for (Object var17 : var1) {
         this.toColumns(var17, var5, var8, var14, var2, var3, var6, var7);
      }

      if (var14.length() > 0 && var14.charAt(var14.length() - 1) == '\n') {
         var14.setLength(var14.length() - 1);
      }

      return new LineReaderImpl.PostResult(var14.toAttributedString(), var7[0], var7[1]);
   }

   protected void toColumns(Object var1, int var2, int var3, AttributedStringBuilder var4, Candidate var5, String var6, boolean var7, int[] var8) {
      if (var3 > 0 && var2 > 0) {
         if (var1 instanceof String) {
            var4.style(this.getCompletionStyleGroup()).append((String)var1).style(AttributedStyle.DEFAULT).append("\n");
            var8[0]++;
         } else if (var1 instanceof List) {
            List var9 = (List)var1;
            var3 = Math.min(var2, var3);
            int var10 = var2 / var3;

            while (var10 > 1 && var10 * var3 + (var10 - 1) * 3 >= var2) {
               var10--;
            }

            int var11 = (var9.size() + var10 - 1) / var10;
            int var12 = (var9.size() + var11 - 1) / var11;
            IntBinaryOperator var13;
            if (var7) {
               var13 = (var1x, var2x) -> var1x * var12 + var2x;
            } else {
               var13 = (var1x, var2x) -> var2x * var11 + var1x;
            }

            for (int var14 = 0; var14 < var11; var14++) {
               for (int var15 = 0; var15 < var12; var15++) {
                  int var16 = var13.applyAsInt(var14, var15);
                  if (var16 < var9.size()) {
                     Candidate var17 = (Candidate)var9.get(var16);
                     boolean var18 = var15 < var12 - 1 && var13.applyAsInt(var14, var15 + 1) < var9.size();
                     AttributedString var19 = AttributedString.fromAnsi(var17.displ());
                     AttributedString var20 = AttributedString.fromAnsi(var17.descr());
                     int var21 = var19.columnLength();
                     int var22 = 0;
                     if (var20 != null) {
                        int var23 = var3 - (var21 + 1 + "(".length() + ")".length());
                        var22 = var20.columnLength();
                        if (var22 > var23) {
                           var20 = AttributedStringBuilder.append(var20.columnSubSequence(0, var23 - WCWidth.wcwidth(8230)), "…");
                           var22 = var20.columnLength();
                        }

                        var20 = AttributedStringBuilder.append("(", var20, ")");
                        var22 += "(".length() + ")".length();
                     }

                     if (var17 == var5) {
                        var8[1] = var14;
                        var4.style(this.getCompletionStyleSelection());
                        if (var19.toString().regionMatches(this.isSet(LineReader.Option.CASE_INSENSITIVE), 0, var6, 0, var6.length())) {
                           var4.append(var19.toString(), 0, var6.length());
                           var4.append(var19.toString(), var6.length(), var19.length());
                        } else {
                           var4.append(var19.toString());
                        }

                        for (int var26 = 0; var26 < var3 - var21 - var22; var26++) {
                           var4.append(' ');
                        }

                        if (var20 != null) {
                           var4.append(var20);
                        }

                        var4.style(AttributedStyle.DEFAULT);
                     } else {
                        if (var19.toString().regionMatches(this.isSet(LineReader.Option.CASE_INSENSITIVE), 0, var6, 0, var6.length())) {
                           var4.style(this.getCompletionStyleStarting());
                           var4.append(var19, 0, var6.length());
                           var4.style(AttributedStyle.DEFAULT);
                           var4.append(var19, var6.length(), var19.length());
                        } else {
                           var4.append(var19);
                        }

                        if (var20 != null || var18) {
                           for (int var27 = 0; var27 < var3 - var21 - var22; var27++) {
                              var4.append(' ');
                           }
                        }

                        if (var20 != null) {
                           var4.style(this.getCompletionStyleDescription());
                           var4.append(var20);
                           var4.style(AttributedStyle.DEFAULT);
                        }
                     }

                     if (var18) {
                        for (int var28 = 0; var28 < 3; var28++) {
                           var4.append(' ');
                        }
                     }
                  }
               }

               var4.append('\n');
            }

            var8[0] += var11;
         }
      }
   }

   private AttributedStyle getCompletionStyleStarting() {
      return this.getCompletionStyle("COMPLETION_STYLE_STARTING", "36");
   }

   protected AttributedStyle getCompletionStyleDescription() {
      return this.getCompletionStyle("COMPLETION_STYLE_DESCRIPTION", "90");
   }

   protected AttributedStyle getCompletionStyleGroup() {
      return this.getCompletionStyle("COMPLETION_STYLE_GROUP", "35;1");
   }

   protected AttributedStyle getCompletionStyleSelection() {
      return this.getCompletionStyle("COMPLETION_STYLE_SELECTION", "7");
   }

   protected AttributedStyle getCompletionStyle(String var1, String var2) {
      return this.buildStyle(this.getString(var1, var2));
   }

   protected AttributedStyle buildStyle(String var1) {
      return AttributedString.fromAnsi("\u001b[" + var1 + "m ").styleAt(0);
   }

   private String getCommonStart(String var1, String var2, boolean var3) {
      int[] var4 = var1.codePoints().toArray();
      int[] var5 = var2.codePoints().toArray();

      int var6;
      for (var6 = 0; var6 < Math.min(var4.length, var5.length); var6++) {
         int var7 = var4[var6];
         int var8 = var5[var6];
         if (var7 != var8 && var3) {
            var7 = Character.toUpperCase(var7);
            var8 = Character.toUpperCase(var8);
            if (var7 != var8) {
               var7 = Character.toLowerCase(var7);
               var8 = Character.toLowerCase(var8);
            }
         }

         if (var7 != var8) {
            break;
         }
      }

      return new String(var4, 0, var6);
   }

   protected boolean moveHistory(boolean var1, int var2) {
      boolean var3 = true;
      int var4 = 0;

      while (var4 < var2 && (var3 = this.moveHistory(var1))) {
         var4++;
      }

      return var3;
   }

   protected boolean moveHistory(boolean var1) {
      if (!this.buf.toString().equals(this.history.current())) {
         this.modifiedHistory.put(this.history.index(), this.buf.toString());
      }

      if (var1 && !this.history.next()) {
         return false;
      } else if (!var1 && !this.history.previous()) {
         return false;
      } else {
         this.setBuffer(this.modifiedHistory.containsKey(this.history.index()) ? this.modifiedHistory.get(this.history.index()) : this.history.current());
         return true;
      }
   }

   void print(String var1) {
      this.terminal.writer().write(var1);
   }

   void println(String var1) {
      this.print(var1);
      this.println();
   }

   void println() {
      this.terminal.puts(InfoCmp.Capability.carriage_return);
      this.print("\n");
      this.redrawLine();
   }

   protected boolean killBuffer() {
      this.killRing.add(this.buf.toString());
      this.buf.clear();
      return true;
   }

   protected boolean killWholeLine() {
      if (this.buf.length() == 0) {
         return false;
      } else {
         int var1;
         int var2;
         if (this.count < 0) {
            var2 = this.buf.cursor();

            while (this.buf.atChar(var2) != 0 && this.buf.atChar(var2) != 10) {
               var2++;
            }

            var1 = var2;

            for (int var3 = -this.count; var3 > 0; var3--) {
               while (var1 > 0 && this.buf.atChar(var1 - 1) != 10) {
                  var1--;
               }

               var1--;
            }
         } else {
            var1 = this.buf.cursor();

            while (var1 > 0 && this.buf.atChar(var1 - 1) != 10) {
               var1--;
            }

            var2 = var1;

            while (this.count-- > 0) {
               while (var2 < this.buf.length() && this.buf.atChar(var2) != 10) {
                  var2++;
               }

               if (var2 < this.buf.length()) {
                  var2++;
               }
            }
         }

         String var4 = this.buf.substring(var1, var2);
         this.buf.cursor(var1);
         this.buf.delete(var2 - var1);
         this.killRing.add(var4);
         return true;
      }
   }

   public boolean killLine() {
      if (this.count < 0) {
         return this.callNeg(this::backwardKillLine);
      } else if (this.buf.cursor() == this.buf.length()) {
         return false;
      } else {
         int var1 = this.buf.cursor();
         int var2 = var1;

         while (this.count-- > 0) {
            if (this.buf.atChar(var2) == 10) {
               var2++;
            } else {
               while (this.buf.atChar(var2) != 0 && this.buf.atChar(var2) != 10) {
                  var2++;
               }
            }
         }

         int var3 = var2 - var1;
         String var4 = this.buf.substring(var1, var1 + var3);
         this.buf.delete(var3);
         this.killRing.add(var4);
         return true;
      }
   }

   public boolean backwardKillLine() {
      if (this.count < 0) {
         return this.callNeg(this::killLine);
      } else if (this.buf.cursor() == 0) {
         return false;
      } else {
         int var1 = this.buf.cursor();
         int var2 = var1;

         while (this.count-- > 0 && var2 != 0) {
            if (this.buf.atChar(var2 - 1) == 10) {
               var2--;
            } else {
               while (var2 > 0 && this.buf.atChar(var2 - 1) != 0 && this.buf.atChar(var2 - 1) != 10) {
                  var2--;
               }
            }
         }

         int var3 = var1 - var2;
         String var4 = this.buf.substring(var1 - var2, var1);
         this.buf.cursor(var2);
         this.buf.delete(var3);
         this.killRing.add(var4);
         return true;
      }
   }

   public boolean killRegion() {
      return this.doCopyKillRegion(true);
   }

   public boolean copyRegionAsKill() {
      return this.doCopyKillRegion(false);
   }

   private boolean doCopyKillRegion(boolean var1) {
      if (this.regionMark > this.buf.length()) {
         this.regionMark = this.buf.length();
      }

      if (this.regionActive == LineReader.RegionType.LINE) {
         int var2 = this.regionMark;
         int var3 = this.buf.cursor();
         if (var2 < var3) {
            while (var2 > 0 && this.buf.atChar(var2 - 1) != 10) {
               var2--;
            }

            while (var3 < this.buf.length() - 1 && this.buf.atChar(var3 + 1) != 10) {
               var3++;
            }

            if (this.isInViCmdMode()) {
               var3++;
            }

            this.killRing.add(this.buf.substring(var2, var3));
            if (var1) {
               this.buf.backspace(var3 - var2);
            }
         } else {
            while (var3 > 0 && this.buf.atChar(var3 - 1) != 10) {
               var3--;
            }

            while (var2 < this.buf.length() && this.buf.atChar(var2) != 10) {
               var2++;
            }

            if (this.isInViCmdMode()) {
               var2++;
            }

            this.killRing.addBackwards(this.buf.substring(var3, var2));
            if (var1) {
               this.buf.cursor(var3);
               this.buf.delete(var2 - var3);
            }
         }
      } else if (this.regionMark > this.buf.cursor()) {
         if (this.isInViCmdMode()) {
            this.regionMark++;
         }

         this.killRing.add(this.buf.substring(this.buf.cursor(), this.regionMark));
         if (var1) {
            this.buf.delete(this.regionMark - this.buf.cursor());
         }
      } else {
         if (this.isInViCmdMode()) {
            this.buf.move(1);
         }

         this.killRing.add(this.buf.substring(this.regionMark, this.buf.cursor()));
         if (var1) {
            this.buf.backspace(this.buf.cursor() - this.regionMark);
         }
      }

      if (var1) {
         this.regionActive = LineReader.RegionType.NONE;
      }

      return true;
   }

   public boolean yank() {
      String var1 = this.killRing.yank();
      if (var1 == null) {
         return false;
      } else {
         this.putString(var1);
         return true;
      }
   }

   public boolean yankPop() {
      if (!this.killRing.lastYank()) {
         return false;
      } else {
         String var1 = this.killRing.yank();
         if (var1 == null) {
            return false;
         } else {
            this.buf.backspace(var1.length());
            String var2 = this.killRing.yankPop();
            if (var2 == null) {
               return false;
            } else {
               this.putString(var2);
               return true;
            }
         }
      }
   }

   public boolean mouse() {
      MouseEvent var1 = this.readMouseEvent();
      if (var1.getType() == MouseEvent.Type.Released && var1.getButton() == MouseEvent.Button.Button1) {
         StringBuilder var2 = new StringBuilder();
         Cursor var3 = this.terminal.getCursorPosition(var1x -> var2.append((char)var1x));
         this.bindingReader.runMacro(var2.toString());
         ArrayList var4 = new ArrayList();
         this.getDisplayedBufferWithPrompts(var4);
         AttributedStringBuilder var5 = new AttributedStringBuilder().tabs(4);
         var5.append(this.prompt);
         var5.append(this.insertSecondaryPrompts(new AttributedString(this.buf.upToCursor()), var4, false));
         List var6 = var5.columnSplitLength(this.size.getColumns(), false, this.display.delayLineWrap());
         int var7 = var6.size() - 1;
         int var8 = Math.max(0, Math.min(var7 + var1.getY() - var3.getY(), var4.size()));
         int var9 = var7 == 0 ? this.prompt.columnLength() : ((AttributedString)var4.get(var7 - 1)).columnLength();
         int var10 = var8 == 0 ? this.prompt.columnLength() : ((AttributedString)var4.get(var8 - 1)).columnLength();
         int var11 = var10 - var9;
         this.buf.moveXY(var1.getX() - var3.getX() - var11, var1.getY() - var3.getY());
      }

      return true;
   }

   public boolean beginPaste() {
      Object var1 = new Object();
      Object var2 = new Object();
      KeyMap var3 = new KeyMap();
      var3.setUnicode(var1);
      var3.setNomatch(var1);
      var3.setAmbiguousTimeout(0L);
      var3.bind(var2, "\u001b[201~");
      StringBuilder var4 = new StringBuilder();

      while (true) {
         Object var5 = this.doReadBinding(var3, null);
         if (var5 == var2) {
            this.regionActive = LineReader.RegionType.PASTE;
            this.regionMark = this.getBuffer().cursor();
            this.getBuffer().write(var4);
            return true;
         }

         String var6 = this.getLastBinding();
         if ("\r".equals(var6)) {
            var6 = "\n";
         }

         var4.append(var6);
      }
   }

   public boolean focusIn() {
      return false;
   }

   public boolean focusOut() {
      return false;
   }

   public boolean clear() {
      this.display.update(Collections.emptyList(), 0);
      return true;
   }

   public boolean clearScreen() {
      if (this.terminal.puts(InfoCmp.Capability.clear_screen)) {
         if ("windows-conemu".equals(this.terminal.getType()) && !Boolean.getBoolean("org.jline.terminal.conemu.disable-activate")) {
            this.terminal.writer().write("\u001b[9999E");
         }

         Status var1 = Status.getStatus(this.terminal, false);
         if (var1 != null) {
            var1.reset();
         }

         this.redrawLine();
      } else {
         this.println();
      }

      return true;
   }

   public boolean beep() {
      LineReaderImpl.BellType var1 = LineReaderImpl.BellType.AUDIBLE;
      String var2 = this.getString("bell-style", "").toLowerCase();
      switch (var2) {
         case "none":
         case "off":
            var1 = LineReaderImpl.BellType.NONE;
            break;
         case "audible":
            var1 = LineReaderImpl.BellType.AUDIBLE;
            break;
         case "visible":
            var1 = LineReaderImpl.BellType.VISIBLE;
            break;
         case "on":
            var1 = this.getBoolean("prefer-visible-bell", false) ? LineReaderImpl.BellType.VISIBLE : LineReaderImpl.BellType.AUDIBLE;
      }

      if (var1 == LineReaderImpl.BellType.VISIBLE) {
         if (this.terminal.puts(InfoCmp.Capability.flash_screen) || this.terminal.puts(InfoCmp.Capability.bell)) {
            this.flush();
         }
      } else if (var1 == LineReaderImpl.BellType.AUDIBLE && this.terminal.puts(InfoCmp.Capability.bell)) {
         this.flush();
      }

      return true;
   }

   protected boolean isDelimiter(int var1) {
      return !Character.isLetterOrDigit(var1);
   }

   protected boolean isWhitespace(int var1) {
      return Character.isWhitespace(var1);
   }

   protected boolean isViAlphaNum(int var1) {
      return var1 == 95 || Character.isLetterOrDigit(var1);
   }

   protected boolean isAlpha(int var1) {
      return Character.isLetter(var1);
   }

   protected boolean isWord(int var1) {
      String var2 = this.getString("WORDCHARS", "*?_-.[]~=/&;!#$%^(){}<>");
      return Character.isLetterOrDigit(var1) || var1 < 128 && var2.indexOf((char)var1) >= 0;
   }

   String getString(String var1, String var2) {
      return ReaderUtils.getString(this, var1, var2);
   }

   boolean getBoolean(String var1, boolean var2) {
      return ReaderUtils.getBoolean(this, var1, var2);
   }

   int getInt(String var1, int var2) {
      return ReaderUtils.getInt(this, var1, var2);
   }

   long getLong(String var1, long var2) {
      return ReaderUtils.getLong(this, var1, var2);
   }

   @Override
   public Map<String, KeyMap<Binding>> defaultKeyMaps() {
      HashMap var1 = new HashMap();
      var1.put("emacs", this.emacs());
      var1.put("vicmd", this.viCmd());
      var1.put("viins", this.viInsertion());
      var1.put("menu", this.menu());
      var1.put("viopp", this.viOpp());
      var1.put("visual", this.visual());
      var1.put(".safe", this.safe());
      if (this.getBoolean("bind-tty-special-chars", true)) {
         Attributes var2 = this.terminal.getAttributes();
         this.bindConsoleChars((KeyMap<Binding>)var1.get("emacs"), var2);
         this.bindConsoleChars((KeyMap<Binding>)var1.get("viins"), var2);
      }

      for (KeyMap var3 : var1.values()) {
         var3.setUnicode(new Reference("self-insert"));
         var3.setAmbiguousTimeout(this.getLong("ambiguous-binding", 1000L));
      }

      var1.put("main", var1.get("emacs"));
      return var1;
   }

   public KeyMap<Binding> emacs() {
      KeyMap var1 = new KeyMap();
      this.bindKeys(var1);
      this.bind(var1, "set-mark-command", KeyMap.ctrl('@'));
      this.bind(var1, "beginning-of-line", KeyMap.ctrl('A'));
      this.bind(var1, "backward-char", KeyMap.ctrl('B'));
      this.bind(var1, "delete-char-or-list", KeyMap.ctrl('D'));
      this.bind(var1, "end-of-line", KeyMap.ctrl('E'));
      this.bind(var1, "forward-char", KeyMap.ctrl('F'));
      this.bind(var1, "abort", KeyMap.ctrl('G'));
      this.bind(var1, "backward-delete-char", KeyMap.ctrl('H'));
      this.bind(var1, "expand-or-complete", KeyMap.ctrl('I'));
      this.bind(var1, "accept-line", KeyMap.ctrl('J'));
      this.bind(var1, "kill-line", KeyMap.ctrl('K'));
      this.bind(var1, "clear-screen", KeyMap.ctrl('L'));
      this.bind(var1, "accept-line", KeyMap.ctrl('M'));
      this.bind(var1, "down-line-or-history", KeyMap.ctrl('N'));
      this.bind(var1, "accept-line-and-down-history", KeyMap.ctrl('O'));
      this.bind(var1, "up-line-or-history", KeyMap.ctrl('P'));
      this.bind(var1, "history-incremental-search-backward", KeyMap.ctrl('R'));
      this.bind(var1, "history-incremental-search-forward", KeyMap.ctrl('S'));
      this.bind(var1, "transpose-chars", KeyMap.ctrl('T'));
      this.bind(var1, "kill-whole-line", KeyMap.ctrl('U'));
      this.bind(var1, "quoted-insert", KeyMap.ctrl('V'));
      this.bind(var1, "backward-kill-word", KeyMap.ctrl('W'));
      this.bind(var1, "yank", KeyMap.ctrl('Y'));
      this.bind(var1, "character-search", KeyMap.ctrl(']'));
      this.bind(var1, "undo", KeyMap.ctrl('_'));
      this.bind(var1, "self-insert", KeyMap.range(" -~"));
      this.bind(var1, "insert-close-paren", ")");
      this.bind(var1, "insert-close-square", "]");
      this.bind(var1, "insert-close-curly", "}");
      this.bind(var1, "backward-delete-char", KeyMap.del());
      this.bind(var1, "vi-match-bracket", KeyMap.translate("^X^B"));
      this.bind(var1, "abort", KeyMap.translate("^X^G"));
      this.bind(var1, "vi-find-next-char", KeyMap.translate("^X^F"));
      this.bind(var1, "vi-join", KeyMap.translate("^X^J"));
      this.bind(var1, "kill-buffer", KeyMap.translate("^X^K"));
      this.bind(var1, "infer-next-history", KeyMap.translate("^X^N"));
      this.bind(var1, "overwrite-mode", KeyMap.translate("^X^O"));
      this.bind(var1, "redo", KeyMap.translate("^X^R"));
      this.bind(var1, "undo", KeyMap.translate("^X^U"));
      this.bind(var1, "vi-cmd-mode", KeyMap.translate("^X^V"));
      this.bind(var1, "exchange-point-and-mark", KeyMap.translate("^X^X"));
      this.bind(var1, "do-lowercase-version", KeyMap.translate("^XA-^XZ"));
      this.bind(var1, "what-cursor-position", KeyMap.translate("^X="));
      this.bind(var1, "kill-line", KeyMap.translate("^X^?"));
      this.bind(var1, "abort", KeyMap.alt(KeyMap.ctrl('G')));
      this.bind(var1, "backward-kill-word", KeyMap.alt(KeyMap.ctrl('H')));
      this.bind(var1, "self-insert-unmeta", KeyMap.alt(KeyMap.ctrl('M')));
      this.bind(var1, "complete-word", KeyMap.alt(KeyMap.esc()));
      this.bind(var1, "character-search-backward", KeyMap.alt(KeyMap.ctrl(']')));
      this.bind(var1, "copy-prev-word", KeyMap.alt(KeyMap.ctrl('_')));
      this.bind(var1, "set-mark-command", KeyMap.alt(' '));
      this.bind(var1, "neg-argument", KeyMap.alt('-'));
      this.bind(var1, "digit-argument", KeyMap.range("\\E0-\\E9"));
      this.bind(var1, "beginning-of-history", KeyMap.alt('<'));
      this.bind(var1, "list-choices", KeyMap.alt('='));
      this.bind(var1, "end-of-history", KeyMap.alt('>'));
      this.bind(var1, "list-choices", KeyMap.alt('?'));
      this.bind(var1, "do-lowercase-version", KeyMap.range("^[A-^[Z"));
      this.bind(var1, "accept-and-hold", KeyMap.alt('a'));
      this.bind(var1, "backward-word", KeyMap.alt('b'));
      this.bind(var1, "capitalize-word", KeyMap.alt('c'));
      this.bind(var1, "kill-word", KeyMap.alt('d'));
      this.bind(var1, "kill-word", KeyMap.translate("^[[3;5~"));
      this.bind(var1, "forward-word", KeyMap.alt('f'));
      this.bind(var1, "down-case-word", KeyMap.alt('l'));
      this.bind(var1, "history-search-forward", KeyMap.alt('n'));
      this.bind(var1, "history-search-backward", KeyMap.alt('p'));
      this.bind(var1, "transpose-words", KeyMap.alt('t'));
      this.bind(var1, "up-case-word", KeyMap.alt('u'));
      this.bind(var1, "yank-pop", KeyMap.alt('y'));
      this.bind(var1, "backward-kill-word", KeyMap.alt(KeyMap.del()));
      this.bindArrowKeys(var1);
      this.bind(var1, "forward-word", KeyMap.translate("^[[1;5C"));
      this.bind(var1, "backward-word", KeyMap.translate("^[[1;5D"));
      this.bind(var1, "forward-word", KeyMap.alt(this.key(InfoCmp.Capability.key_right)));
      this.bind(var1, "backward-word", KeyMap.alt(this.key(InfoCmp.Capability.key_left)));
      this.bind(var1, "forward-word", KeyMap.alt(KeyMap.translate("^[[C")));
      this.bind(var1, "backward-word", KeyMap.alt(KeyMap.translate("^[[D")));
      return var1;
   }

   public KeyMap<Binding> viInsertion() {
      KeyMap var1 = new KeyMap();
      this.bindKeys(var1);
      this.bind(var1, "self-insert", KeyMap.range("^@-^_"));
      this.bind(var1, "list-choices", KeyMap.ctrl('D'));
      this.bind(var1, "abort", KeyMap.ctrl('G'));
      this.bind(var1, "backward-delete-char", KeyMap.ctrl('H'));
      this.bind(var1, "expand-or-complete", KeyMap.ctrl('I'));
      this.bind(var1, "accept-line", KeyMap.ctrl('J'));
      this.bind(var1, "clear-screen", KeyMap.ctrl('L'));
      this.bind(var1, "accept-line", KeyMap.ctrl('M'));
      this.bind(var1, "menu-complete", KeyMap.ctrl('N'));
      this.bind(var1, "reverse-menu-complete", KeyMap.ctrl('P'));
      this.bind(var1, "history-incremental-search-backward", KeyMap.ctrl('R'));
      this.bind(var1, "history-incremental-search-forward", KeyMap.ctrl('S'));
      this.bind(var1, "transpose-chars", KeyMap.ctrl('T'));
      this.bind(var1, "kill-whole-line", KeyMap.ctrl('U'));
      this.bind(var1, "quoted-insert", KeyMap.ctrl('V'));
      this.bind(var1, "backward-kill-word", KeyMap.ctrl('W'));
      this.bind(var1, "yank", KeyMap.ctrl('Y'));
      this.bind(var1, "vi-cmd-mode", KeyMap.ctrl('['));
      this.bind(var1, "undo", KeyMap.ctrl('_'));
      this.bind(var1, "history-incremental-search-backward", KeyMap.ctrl('X') + "r");
      this.bind(var1, "history-incremental-search-forward", KeyMap.ctrl('X') + "s");
      this.bind(var1, "self-insert", KeyMap.range(" -~"));
      this.bind(var1, "insert-close-paren", ")");
      this.bind(var1, "insert-close-square", "]");
      this.bind(var1, "insert-close-curly", "}");
      this.bind(var1, "backward-delete-char", KeyMap.del());
      this.bindArrowKeys(var1);
      return var1;
   }

   public KeyMap<Binding> viCmd() {
      KeyMap var1 = new KeyMap();
      this.bind(var1, "list-choices", KeyMap.ctrl('D'));
      this.bind(var1, "emacs-editing-mode", KeyMap.ctrl('E'));
      this.bind(var1, "abort", KeyMap.ctrl('G'));
      this.bind(var1, "vi-backward-char", KeyMap.ctrl('H'));
      this.bind(var1, "accept-line", KeyMap.ctrl('J'));
      this.bind(var1, "kill-line", KeyMap.ctrl('K'));
      this.bind(var1, "clear-screen", KeyMap.ctrl('L'));
      this.bind(var1, "accept-line", KeyMap.ctrl('M'));
      this.bind(var1, "vi-down-line-or-history", KeyMap.ctrl('N'));
      this.bind(var1, "vi-up-line-or-history", KeyMap.ctrl('P'));
      this.bind(var1, "quoted-insert", KeyMap.ctrl('Q'));
      this.bind(var1, "history-incremental-search-backward", KeyMap.ctrl('R'));
      this.bind(var1, "history-incremental-search-forward", KeyMap.ctrl('S'));
      this.bind(var1, "transpose-chars", KeyMap.ctrl('T'));
      this.bind(var1, "kill-whole-line", KeyMap.ctrl('U'));
      this.bind(var1, "quoted-insert", KeyMap.ctrl('V'));
      this.bind(var1, "backward-kill-word", KeyMap.ctrl('W'));
      this.bind(var1, "yank", KeyMap.ctrl('Y'));
      this.bind(var1, "history-incremental-search-backward", KeyMap.ctrl('X') + "r");
      this.bind(var1, "history-incremental-search-forward", KeyMap.ctrl('X') + "s");
      this.bind(var1, "abort", KeyMap.alt(KeyMap.ctrl('G')));
      this.bind(var1, "backward-kill-word", KeyMap.alt(KeyMap.ctrl('H')));
      this.bind(var1, "self-insert-unmeta", KeyMap.alt(KeyMap.ctrl('M')));
      this.bind(var1, "complete-word", KeyMap.alt(KeyMap.esc()));
      this.bind(var1, "character-search-backward", KeyMap.alt(KeyMap.ctrl(']')));
      this.bind(var1, "set-mark-command", KeyMap.alt(' '));
      this.bind(var1, "digit-argument", KeyMap.alt('-'));
      this.bind(var1, "beginning-of-history", KeyMap.alt('<'));
      this.bind(var1, "list-choices", KeyMap.alt('='));
      this.bind(var1, "end-of-history", KeyMap.alt('>'));
      this.bind(var1, "list-choices", KeyMap.alt('?'));
      this.bind(var1, "do-lowercase-version", KeyMap.range("^[A-^[Z"));
      this.bind(var1, "backward-word", KeyMap.alt('b'));
      this.bind(var1, "capitalize-word", KeyMap.alt('c'));
      this.bind(var1, "kill-word", KeyMap.alt('d'));
      this.bind(var1, "forward-word", KeyMap.alt('f'));
      this.bind(var1, "down-case-word", KeyMap.alt('l'));
      this.bind(var1, "history-search-forward", KeyMap.alt('n'));
      this.bind(var1, "history-search-backward", KeyMap.alt('p'));
      this.bind(var1, "transpose-words", KeyMap.alt('t'));
      this.bind(var1, "up-case-word", KeyMap.alt('u'));
      this.bind(var1, "yank-pop", KeyMap.alt('y'));
      this.bind(var1, "backward-kill-word", KeyMap.alt(KeyMap.del()));
      this.bind(var1, "forward-char", " ");
      this.bind(var1, "vi-insert-comment", "#");
      this.bind(var1, "end-of-line", "$");
      this.bind(var1, "vi-match-bracket", "%");
      this.bind(var1, "vi-down-line-or-history", "+");
      this.bind(var1, "vi-rev-repeat-find", ",");
      this.bind(var1, "vi-up-line-or-history", "-");
      this.bind(var1, "vi-repeat-change", ".");
      this.bind(var1, "vi-history-search-backward", "/");
      this.bind(var1, "vi-digit-or-beginning-of-line", "0");
      this.bind(var1, "digit-argument", KeyMap.range("1-9"));
      this.bind(var1, "vi-repeat-find", ";");
      this.bind(var1, "list-choices", "=");
      this.bind(var1, "vi-history-search-forward", "?");
      this.bind(var1, "vi-add-eol", "A");
      this.bind(var1, "vi-backward-blank-word", "B");
      this.bind(var1, "vi-change-eol", "C");
      this.bind(var1, "vi-kill-eol", "D");
      this.bind(var1, "vi-forward-blank-word-end", "E");
      this.bind(var1, "vi-find-prev-char", "F");
      this.bind(var1, "vi-fetch-history", "G");
      this.bind(var1, "vi-insert-bol", "I");
      this.bind(var1, "vi-join", "J");
      this.bind(var1, "vi-rev-repeat-search", "N");
      this.bind(var1, "vi-open-line-above", "O");
      this.bind(var1, "vi-put-before", "P");
      this.bind(var1, "vi-replace", "R");
      this.bind(var1, "vi-kill-line", "S");
      this.bind(var1, "vi-find-prev-char-skip", "T");
      this.bind(var1, "redo", "U");
      this.bind(var1, "visual-line-mode", "V");
      this.bind(var1, "vi-forward-blank-word", "W");
      this.bind(var1, "vi-backward-delete-char", "X");
      this.bind(var1, "vi-yank-whole-line", "Y");
      this.bind(var1, "vi-first-non-blank", "^");
      this.bind(var1, "vi-add-next", "a");
      this.bind(var1, "vi-backward-word", "b");
      this.bind(var1, "vi-change-to", "c");
      this.bind(var1, "vi-delete", "d");
      this.bind(var1, "vi-forward-word-end", "e");
      this.bind(var1, "vi-find-next-char", "f");
      this.bind(var1, "what-cursor-position", "ga");
      this.bind(var1, "vi-backward-blank-word-end", "gE");
      this.bind(var1, "vi-backward-word-end", "ge");
      this.bind(var1, "vi-backward-char", "h");
      this.bind(var1, "vi-insert", "i");
      this.bind(var1, "down-line-or-history", "j");
      this.bind(var1, "up-line-or-history", "k");
      this.bind(var1, "vi-forward-char", "l");
      this.bind(var1, "vi-repeat-search", "n");
      this.bind(var1, "vi-open-line-below", "o");
      this.bind(var1, "vi-put-after", "p");
      this.bind(var1, "vi-replace-chars", "r");
      this.bind(var1, "vi-substitute", "s");
      this.bind(var1, "vi-find-next-char-skip", "t");
      this.bind(var1, "undo", "u");
      this.bind(var1, "visual-mode", "v");
      this.bind(var1, "vi-forward-word", "w");
      this.bind(var1, "vi-delete-char", "x");
      this.bind(var1, "vi-yank", "y");
      this.bind(var1, "vi-goto-column", "|");
      this.bind(var1, "vi-swap-case", "~");
      this.bind(var1, "vi-backward-char", KeyMap.del());
      this.bindArrowKeys(var1);
      return var1;
   }

   public KeyMap<Binding> menu() {
      KeyMap var1 = new KeyMap();
      this.bind(var1, "menu-complete", "\t");
      this.bind(var1, "reverse-menu-complete", this.key(InfoCmp.Capability.back_tab));
      this.bind(var1, "accept-line", "\r", "\n");
      this.bindArrowKeys(var1);
      return var1;
   }

   public KeyMap<Binding> safe() {
      KeyMap var1 = new KeyMap();
      this.bind(var1, "self-insert", KeyMap.range("^@-^?"));
      this.bind(var1, "accept-line", "\r", "\n");
      this.bind(var1, "abort", KeyMap.ctrl('G'));
      return var1;
   }

   public KeyMap<Binding> visual() {
      KeyMap var1 = new KeyMap();
      this.bind(var1, "up-line", this.key(InfoCmp.Capability.key_up), "k");
      this.bind(var1, "down-line", this.key(InfoCmp.Capability.key_down), "j");
      this.bind(var1, this::deactivateRegion, KeyMap.esc());
      this.bind(var1, "exchange-point-and-mark", "o");
      this.bind(var1, "put-replace-selection", "p");
      this.bind(var1, "vi-delete", "x");
      this.bind(var1, "vi-oper-swap-case", "~");
      return var1;
   }

   public KeyMap<Binding> viOpp() {
      KeyMap var1 = new KeyMap();
      this.bind(var1, "up-line", this.key(InfoCmp.Capability.key_up), "k");
      this.bind(var1, "down-line", this.key(InfoCmp.Capability.key_down), "j");
      this.bind(var1, "vi-cmd-mode", KeyMap.esc());
      return var1;
   }

   private void bind(KeyMap<Binding> var1, String var2, Iterable<? extends CharSequence> var3) {
      var1.bind(new Reference(var2), var3);
   }

   private void bind(KeyMap<Binding> var1, String var2, CharSequence... var3) {
      var1.bind(new Reference(var2), var3);
   }

   private void bind(KeyMap<Binding> var1, Widget var2, CharSequence... var3) {
      var1.bind(var2, var3);
   }

   private String key(InfoCmp.Capability var1) {
      return KeyMap.key(this.terminal, var1);
   }

   private void bindKeys(KeyMap<Binding> var1) {
      Widget var2 = this.namedWidget("beep", this::beep);
      Stream.of(InfoCmp.Capability.values()).filter(var0 -> var0.name().startsWith("key_")).map(this::key).forEach(var3 -> this.bind(var1, var2, var3));
   }

   private void bindArrowKeys(KeyMap<Binding> var1) {
      this.bind(var1, "up-line-or-search", this.key(InfoCmp.Capability.key_up));
      this.bind(var1, "down-line-or-search", this.key(InfoCmp.Capability.key_down));
      this.bind(var1, "backward-char", this.key(InfoCmp.Capability.key_left));
      this.bind(var1, "forward-char", this.key(InfoCmp.Capability.key_right));
      this.bind(var1, "beginning-of-line", this.key(InfoCmp.Capability.key_home));
      this.bind(var1, "end-of-line", this.key(InfoCmp.Capability.key_end));
      this.bind(var1, "delete-char", this.key(InfoCmp.Capability.key_dc));
      this.bind(var1, "kill-whole-line", this.key(InfoCmp.Capability.key_dl));
      this.bind(var1, "overwrite-mode", this.key(InfoCmp.Capability.key_ic));
      this.bind(var1, "mouse", this.key(InfoCmp.Capability.key_mouse));
      this.bind(var1, "begin-paste", "\u001b[200~");
      this.bind(var1, "terminal-focus-in", "\u001b[I");
      this.bind(var1, "terminal-focus-out", "\u001b[O");
   }

   private void bindConsoleChars(KeyMap<Binding> var1, Attributes var2) {
      if (var2 != null) {
         this.rebind(var1, "backward-delete-char", KeyMap.del(), (char)var2.getControlChar(Attributes.ControlChar.VERASE));
         this.rebind(var1, "backward-kill-word", KeyMap.ctrl('W'), (char)var2.getControlChar(Attributes.ControlChar.VWERASE));
         this.rebind(var1, "kill-whole-line", KeyMap.ctrl('U'), (char)var2.getControlChar(Attributes.ControlChar.VKILL));
         this.rebind(var1, "quoted-insert", KeyMap.ctrl('V'), (char)var2.getControlChar(Attributes.ControlChar.VLNEXT));
      }
   }

   private void rebind(KeyMap<Binding> var1, String var2, String var3, char var4) {
      if (var4 > 0 && var4 < 128) {
         Reference var5 = new Reference(var2);
         this.bind(var1, "self-insert", var3);
         var1.bind(var5, Character.toString(var4));
      }
   }

   protected static enum BellType {
      NONE,
      AUDIBLE,
      VISIBLE;
   }

   protected static enum CompletionType {
      Expand,
      ExpandComplete,
      Complete,
      List;
   }

   private class MenuSupport implements Supplier<AttributedString> {
      final List<Candidate> possible = new ArrayList<>();
      final BiFunction<CharSequence, Boolean, CharSequence> escaper;
      int selection;
      int topLine;
      String word;
      AttributedString computed;
      int lines;
      int columns;
      String completed;

      public MenuSupport(List<Candidate> var2, String var3, BiFunction<CharSequence, Boolean, CharSequence> var4) {
         this.escaper = var4;
         this.selection = -1;
         this.topLine = 0;
         this.word = "";
         this.completed = var3;
         LineReaderImpl.this.computePost(var2, null, this.possible, var3);
         this.next();
      }

      public Candidate completion() {
         return this.possible.get(this.selection);
      }

      public void next() {
         this.selection = (this.selection + 1) % this.possible.size();
         this.update();
      }

      public void previous() {
         this.selection = (this.selection + this.possible.size() - 1) % this.possible.size();
         this.update();
      }

      private void major(int var1) {
         int var2 = LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST) ? this.columns : this.lines;
         int var3 = this.selection + var1 * var2;
         if (var3 < 0) {
            int var4 = (var3 + var2) % var2;
            int var5 = this.possible.size() % var2;
            var3 = this.possible.size() - var5 + var4;
            if (var3 >= this.possible.size()) {
               var3 -= var2;
            }
         } else if (var3 >= this.possible.size()) {
            var3 %= var2;
         }

         this.selection = var3;
         this.update();
      }

      private void minor(int var1) {
         int var2 = LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST) ? this.columns : this.lines;
         int var3 = this.selection % var2;
         int var4 = this.possible.size();
         if (this.selection - var3 + var2 > var4) {
            var2 = var4 % var2;
         }

         this.selection = this.selection - var3 + (var2 + var3 + var1) % var2;
         this.update();
      }

      public void up() {
         if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
            this.major(-1);
         } else {
            this.minor(-1);
         }
      }

      public void down() {
         if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
            this.major(1);
         } else {
            this.minor(1);
         }
      }

      public void left() {
         if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
            this.minor(-1);
         } else {
            this.major(-1);
         }
      }

      public void right() {
         if (LineReaderImpl.this.isSet(LineReader.Option.LIST_ROWS_FIRST)) {
            this.minor(1);
         } else {
            this.major(1);
         }
      }

      private void update() {
         LineReaderImpl.this.buf.backspace(this.word.length());
         this.word = this.escaper.apply(this.completion().value(), true).toString();
         LineReaderImpl.this.buf.write(this.word);
         LineReaderImpl.PostResult var1 = LineReaderImpl.this.computePost(this.possible, this.completion(), null, this.completed);
         AttributedString var2 = LineReaderImpl.this.insertSecondaryPrompts(
            AttributedStringBuilder.append(LineReaderImpl.this.prompt, LineReaderImpl.this.buf.toString()), new ArrayList<>()
         );
         int var3 = var2.columnSplitLength(LineReaderImpl.this.size.getColumns(), false, LineReaderImpl.this.display.delayLineWrap()).size();
         if (var1.lines > LineReaderImpl.this.size.getRows() - var3) {
            int var4 = LineReaderImpl.this.size.getRows() - var3 - 1;
            if (var1.selectedLine >= 0) {
               if (var1.selectedLine < this.topLine) {
                  this.topLine = var1.selectedLine;
               } else if (var1.selectedLine >= this.topLine + var4) {
                  this.topLine = var1.selectedLine - var4 + 1;
               }
            }

            AttributedString var5 = var1.post;
            if (var5.length() > 0 && var5.charAt(var5.length() - 1) != '\n') {
               var5 = new AttributedStringBuilder(var5.length() + 1).append(var5).append("\n").toAttributedString();
            }

            List var6 = var5.columnSplitLength(LineReaderImpl.this.size.getColumns(), true, LineReaderImpl.this.display.delayLineWrap());
            ArrayList var7 = new ArrayList(var6.subList(this.topLine, this.topLine + var4));
            var7.add(
               new AttributedStringBuilder()
                  .style(AttributedStyle.DEFAULT.foreground(6))
                  .append("rows ")
                  .append(Integer.toString(this.topLine + 1))
                  .append(" to ")
                  .append(Integer.toString(this.topLine + var4))
                  .append(" of ")
                  .append(Integer.toString(var6.size()))
                  .append("\n")
                  .style(AttributedStyle.DEFAULT)
                  .toAttributedString()
            );
            this.computed = AttributedString.join(AttributedString.EMPTY, var7);
         } else {
            this.computed = var1.post;
         }

         this.lines = var1.lines;
         this.columns = (this.possible.size() + this.lines - 1) / this.lines;
      }

      public AttributedString get() {
         return this.computed;
      }
   }

   static class Pair<U, V> {
      final U u;
      final V v;

      public Pair(U var1, V var2) {
         this.u = (U)var1;
         this.v = (V)var2;
      }

      public U getU() {
         return this.u;
      }

      public V getV() {
         return this.v;
      }
   }

   protected static class PostResult {
      final AttributedString post;
      final int lines;
      final int selectedLine;

      public PostResult(AttributedString var1, int var2, int var3) {
         this.post = var1;
         this.lines = var2;
         this.selectedLine = var3;
      }
   }

   protected static enum State {
      NORMAL,
      DONE,
      EOF,
      INTERRUPT;
   }

   protected static enum ViMoveMode {
      NORMAL,
      YANK,
      DELETE,
      CHANGE;
   }
}
