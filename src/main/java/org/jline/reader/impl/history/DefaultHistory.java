package org.jline.reader.impl.history;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.impl.ReaderUtils;
import org.jline.utils.Log;

public class DefaultHistory implements History {
   public static final int DEFAULT_HISTORY_SIZE = 500;
   public static final int DEFAULT_HISTORY_FILE_SIZE = 10000;
   private final LinkedList<History.Entry> items = new LinkedList<>();
   private LineReader reader;
   private Map<String, DefaultHistory.HistoryFileData> historyFiles = new HashMap<>();
   private int offset = 0;
   private int index = 0;

   public DefaultHistory() {
   }

   public DefaultHistory(LineReader var1) {
      this.attach(var1);
   }

   private Path getPath() {
      Object var1 = this.reader != null ? this.reader.getVariables().get("history-file") : null;
      if (var1 instanceof Path) {
         return (Path)var1;
      } else if (var1 instanceof File) {
         return ((File)var1).toPath();
      } else {
         return var1 != null ? Paths.get(var1.toString()) : null;
      }
   }

   @Override
   public void attach(LineReader var1) {
      if (this.reader != var1) {
         this.reader = var1;

         try {
            this.load();
         } catch (IOException | IllegalArgumentException var3) {
            Log.warn("Failed to load history", var3);
         }
      }
   }

   @Override
   public void load() throws IOException {
      Path var1 = this.getPath();
      if (var1 != null) {
         try {
            if (Files.exists(var1)) {
               Log.trace("Loading history from: ", var1);

               try (BufferedReader var2 = Files.newBufferedReader(var1)) {
                  this.internalClear();
                  var2.lines().forEach(var2x -> this.addHistoryLine(var1, var2x));
                  this.setHistoryFileData(var1, new DefaultHistory.HistoryFileData(this.items.size(), this.items.size()));
                  this.maybeResize();
               }
            }
         } catch (IOException | IllegalArgumentException var15) {
            Log.debug("Failed to load history; clearing", var15);
            this.internalClear();
            throw var15;
         }
      }
   }

   @Override
   public void read(Path var1, boolean var2) throws IOException {
      Path var3 = var1 != null ? var1 : this.getPath();
      if (var3 != null) {
         try {
            if (Files.exists(var3)) {
               Log.trace("Reading history from: ", var3);

               try (BufferedReader var4 = Files.newBufferedReader(var3)) {
                  var4.lines().forEach(var3x -> this.addHistoryLine(var3, var3x, var2));
                  this.setHistoryFileData(var3, new DefaultHistory.HistoryFileData(this.items.size(), this.items.size()));
                  this.maybeResize();
               }
            }
         } catch (IOException | IllegalArgumentException var17) {
            Log.debug("Failed to read history; clearing", var17);
            this.internalClear();
            throw var17;
         }
      }
   }

   private String doHistoryFileDataKey(Path var1) {
      return var1 != null ? var1.toAbsolutePath().toString() : null;
   }

   private DefaultHistory.HistoryFileData getHistoryFileData(Path var1) {
      String var2 = this.doHistoryFileDataKey(var1);
      if (!this.historyFiles.containsKey(var2)) {
         this.historyFiles.put(var2, new DefaultHistory.HistoryFileData());
      }

      return this.historyFiles.get(var2);
   }

   private void setHistoryFileData(Path var1, DefaultHistory.HistoryFileData var2) {
      this.historyFiles.put(this.doHistoryFileDataKey(var1), var2);
   }

   private boolean isLineReaderHistory(Path var1) throws IOException {
      Path var2 = this.getPath();
      return var2 == null ? var1 == null : Files.isSameFile(var2, var1);
   }

   private void setLastLoaded(Path var1, int var2) {
      this.getHistoryFileData(var1).setLastLoaded(var2);
   }

   private void setEntriesInFile(Path var1, int var2) {
      this.getHistoryFileData(var1).setEntriesInFile(var2);
   }

   private void incEntriesInFile(Path var1, int var2) {
      this.getHistoryFileData(var1).incEntriesInFile(var2);
   }

   private int getLastLoaded(Path var1) {
      return this.getHistoryFileData(var1).getLastLoaded();
   }

   private int getEntriesInFile(Path var1) {
      return this.getHistoryFileData(var1).getEntriesInFile();
   }

   protected void addHistoryLine(Path var1, String var2) {
      this.addHistoryLine(var1, var2, false);
   }

   protected void addHistoryLine(Path var1, String var2, boolean var3) {
      if (this.reader.isSet(LineReader.Option.HISTORY_TIMESTAMPED)) {
         int var4 = var2.indexOf(58);
         String var5 = "Bad history file syntax! The history file `" + var1 + "` may be an older history: please remove it or use a different history file.";
         if (var4 < 0) {
            throw new IllegalArgumentException(var5);
         }

         Instant var6;
         try {
            var6 = Instant.ofEpochMilli(Long.parseLong(var2.substring(0, var4)));
         } catch (NumberFormatException | DateTimeException var8) {
            throw new IllegalArgumentException(var5);
         }

         String var7 = unescape(var2.substring(var4 + 1));
         this.internalAdd(var6, var7, var3);
      } else {
         this.internalAdd(Instant.now(), unescape(var2), var3);
      }
   }

   @Override
   public void purge() throws IOException {
      this.internalClear();
      Path var1 = this.getPath();
      if (var1 != null) {
         Log.trace("Purging history from: ", var1);
         Files.deleteIfExists(var1);
      }
   }

   @Override
   public void write(Path var1, boolean var2) throws IOException {
      Path var3 = var1 != null ? var1 : this.getPath();
      if (var3 != null && Files.exists(var3)) {
         var3.toFile().delete();
      }

      this.internalWrite(var3, var2 ? this.getLastLoaded(var3) : 0);
   }

   @Override
   public void append(Path var1, boolean var2) throws IOException {
      this.internalWrite(var1 != null ? var1 : this.getPath(), var2 ? this.getLastLoaded(var1) : 0);
   }

   @Override
   public void save() throws IOException {
      this.internalWrite(this.getPath(), this.getLastLoaded(this.getPath()));
   }

   private void internalWrite(Path var1, int var2) throws IOException {
      if (var1 != null) {
         Log.trace("Saving history to: ", var1);
         Files.createDirectories(var1.toAbsolutePath().getParent());

         try (BufferedWriter var3 = Files.newBufferedWriter(
               var1.toAbsolutePath(), StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE
            )) {
            for (History.Entry var6 : this.items.subList(var2, this.items.size())) {
               if (this.isPersistable(var6)) {
                  var3.append(this.format(var6));
               }
            }
         }

         this.incEntriesInFile(var1, this.items.size() - var2);
         int var16 = ReaderUtils.getInt(this.reader, "history-file-size", 10000);
         if (this.getEntriesInFile(var1) > var16 + var16 / 4) {
            this.trimHistory(var1, var16);
         }
      }

      this.setLastLoaded(var1, this.items.size());
   }

   protected void trimHistory(Path var1, int var2) throws IOException {
      Log.trace("Trimming history path: ", var1);
      LinkedList var3 = new LinkedList();

      try (BufferedReader var4 = Files.newBufferedReader(var1)) {
         var4.lines().forEach(var2x -> {
            int var3x = var2x.indexOf(58);
            Instant var4x = Instant.ofEpochMilli(Long.parseLong(var2x.substring(0, var3x)));
            String var5 = unescape(var2x.substring(var3x + 1));
            var3.add(this.createEntry(var3.size(), var4x, var5));
         });
      }

      doTrimHistory(var3, var2);
      Path var32 = Files.createTempFile(var1.toAbsolutePath().getParent(), var1.getFileName().toString(), ".tmp");

      try (BufferedWriter var33 = Files.newBufferedWriter(var32, StandardOpenOption.WRITE)) {
         for (History.Entry var8 : var3) {
            var33.append(this.format(var8));
         }
      }

      Files.move(var32, var1, StandardCopyOption.REPLACE_EXISTING);
      if (this.isLineReaderHistory(var1)) {
         this.internalClear();
         this.offset = ((History.Entry)var3.get(0)).index();
         this.items.addAll(var3);
         this.setHistoryFileData(var1, new DefaultHistory.HistoryFileData(this.items.size(), this.items.size()));
      } else {
         this.setEntriesInFile(var1, var3.size());
      }

      this.maybeResize();
   }

   protected DefaultHistory.EntryImpl createEntry(int var1, Instant var2, String var3) {
      return new DefaultHistory.EntryImpl(var1, var2, var3);
   }

   private void internalClear() {
      this.offset = 0;
      this.index = 0;
      this.historyFiles = new HashMap<>();
      this.items.clear();
   }

   static void doTrimHistory(List<History.Entry> var0, int var1) {
      for (int var2 = 0; var2 < var0.size(); var2++) {
         int var3 = var0.size() - var2 - 1;
         String var4 = ((History.Entry)var0.get(var3)).line().trim();
         ListIterator var5 = var0.listIterator(var3);

         while (var5.hasPrevious()) {
            String var6 = ((History.Entry)var5.previous()).line();
            if (var4.equals(var6.trim())) {
               var5.remove();
            }
         }
      }

      while (var0.size() > var1) {
         var0.remove(0);
      }
   }

   @Override
   public int size() {
      return this.items.size();
   }

   @Override
   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   @Override
   public int index() {
      return this.offset + this.index;
   }

   @Override
   public int first() {
      return this.offset;
   }

   @Override
   public int last() {
      return this.offset + this.items.size() - 1;
   }

   private String format(History.Entry var1) {
      return this.reader.isSet(LineReader.Option.HISTORY_TIMESTAMPED)
         ? Long.toString(var1.time().toEpochMilli()) + ":" + escape(var1.line()) + "\n"
         : escape(var1.line()) + "\n";
   }

   @Override
   public String get(int var1) {
      return this.items.get(var1 - this.offset).line();
   }

   @Override
   public void add(Instant var1, String var2) {
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      if (!ReaderUtils.getBoolean(this.reader, "disable-history", false)) {
         if (!ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_IGNORE_SPACE) || !var2.startsWith(" ")) {
            if (ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_REDUCE_BLANKS)) {
               var2 = var2.trim();
            }

            if (!ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_IGNORE_DUPS) || this.items.isEmpty() || !var2.equals(this.items.getLast().line())) {
               if (!this.matchPatterns(ReaderUtils.getString(this.reader, "history-ignore", ""), var2)) {
                  this.internalAdd(var1, var2);
                  if (ReaderUtils.isSet(this.reader, LineReader.Option.HISTORY_INCREMENTAL)) {
                     try {
                        this.save();
                     } catch (IOException var4) {
                        Log.warn("Failed to save history", var4);
                     }
                  }
               }
            }
         }
      }
   }

   protected boolean matchPatterns(String var1, String var2) {
      if (var1 != null && !var1.isEmpty()) {
         StringBuilder var3 = new StringBuilder();

         for (int var4 = 0; var4 < var1.length(); var4++) {
            char var5 = var1.charAt(var4);
            if (var5 == '\\') {
               var5 = var1.charAt(++var4);
               var3.append(var5);
            } else if (var5 == ':') {
               var3.append('|');
            } else if (var5 == '*') {
               var3.append('.').append('*');
            }
         }

         return var2.matches(var3.toString());
      } else {
         return false;
      }
   }

   protected void internalAdd(Instant var1, String var2) {
      this.internalAdd(var1, var2, false);
   }

   protected void internalAdd(Instant var1, String var2, boolean var3) {
      DefaultHistory.EntryImpl var4 = new DefaultHistory.EntryImpl(this.offset + this.items.size(), var1, var2);
      if (var3) {
         for (History.Entry var6 : this.items) {
            if (var6.line().trim().equals(var2.trim())) {
               return;
            }
         }
      }

      this.items.add(var4);
      this.maybeResize();
   }

   private void maybeResize() {
      while (this.size() > ReaderUtils.getInt(this.reader, "history-size", 500)) {
         this.items.removeFirst();

         for (DefaultHistory.HistoryFileData var2 : this.historyFiles.values()) {
            var2.decLastLoaded();
         }

         this.offset++;
      }

      this.index = this.size();
   }

   @Override
   public ListIterator<History.Entry> iterator(int var1) {
      return this.items.listIterator(var1 - this.offset);
   }

   @Override
   public Spliterator<History.Entry> spliterator() {
      return this.items.spliterator();
   }

   @Override
   public boolean moveToLast() {
      int var1 = this.size() - 1;
      if (var1 >= 0 && var1 != this.index) {
         this.index = this.size() - 1;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean moveTo(int var1) {
      var1 -= this.offset;
      if (var1 >= 0 && var1 < this.size()) {
         this.index = var1;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean moveToFirst() {
      if (this.size() > 0 && this.index != 0) {
         this.index = 0;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void moveToEnd() {
      this.index = this.size();
   }

   @Override
   public String current() {
      return this.index >= this.size() ? "" : this.items.get(this.index).line();
   }

   @Override
   public boolean previous() {
      if (this.index <= 0) {
         return false;
      } else {
         this.index--;
         return true;
      }
   }

   @Override
   public boolean next() {
      if (this.index >= this.size()) {
         return false;
      } else {
         this.index++;
         return true;
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();

      for (History.Entry var3 : this) {
         var1.append(var3.toString()).append("\n");
      }

      return var1.toString();
   }

   private static String escape(String var0) {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < var0.length(); var2++) {
         char var3 = var0.charAt(var2);
         switch (var3) {
            case '\n':
               var1.append('\\');
               var1.append('n');
               break;
            case '\r':
               var1.append('\\');
               var1.append('r');
               break;
            case '\\':
               var1.append('\\');
               var1.append('\\');
               break;
            default:
               var1.append(var3);
         }
      }

      return var1.toString();
   }

   static String unescape(String var0) {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < var0.length(); var2++) {
         char var3 = var0.charAt(var2);
         switch (var3) {
            case '\\':
               var3 = var0.charAt(++var2);
               if (var3 == 'n') {
                  var1.append('\n');
               } else if (var3 == 'r') {
                  var1.append('\r');
               } else {
                  var1.append(var3);
               }
               break;
            default:
               var1.append(var3);
         }
      }

      return var1.toString();
   }

   protected static class EntryImpl implements History.Entry {
      private final int index;
      private final Instant time;
      private final String line;

      public EntryImpl(int var1, Instant var2, String var3) {
         this.index = var1;
         this.time = var2;
         this.line = var3;
      }

      @Override
      public int index() {
         return this.index;
      }

      @Override
      public Instant time() {
         return this.time;
      }

      @Override
      public String line() {
         return this.line;
      }

      @Override
      public String toString() {
         return String.format("%d: %s", this.index, this.line);
      }
   }

   private class HistoryFileData {
      private int lastLoaded = 0;
      private int entriesInFile = 0;

      public HistoryFileData() {
      }

      public HistoryFileData(int var2, int var3) {
         this.lastLoaded = var2;
         this.entriesInFile = var3;
      }

      public int getLastLoaded() {
         return this.lastLoaded;
      }

      public void setLastLoaded(int var1) {
         this.lastLoaded = var1;
      }

      public void decLastLoaded() {
         this.lastLoaded--;
         if (this.lastLoaded < 0) {
            this.lastLoaded = 0;
         }
      }

      public int getEntriesInFile() {
         return this.entriesInFile;
      }

      public void setEntriesInFile(int var1) {
         this.entriesInFile = var1;
      }

      public void incEntriesInFile(int var1) {
         this.entriesInFile += var1;
      }
   }
}
