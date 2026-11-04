package org.jline.reader.impl.completer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public class ArgumentCompleter implements Completer {
   private final List<Completer> completers = new ArrayList<>();
   private boolean strict = true;

   public ArgumentCompleter(Collection<Completer> var1) {
      Objects.requireNonNull(var1);
      this.completers.addAll(var1);
   }

   public ArgumentCompleter(Completer... var1) {
      this(Arrays.asList(var1));
   }

   public void setStrict(boolean var1) {
      this.strict = var1;
   }

   public boolean isStrict() {
      return this.strict;
   }

   public List<Completer> getCompleters() {
      return this.completers;
   }

   @Override
   public void complete(LineReader var1, ParsedLine var2, List<Candidate> var3) {
      Objects.requireNonNull(var2);
      Objects.requireNonNull(var3);
      if (var2.wordIndex() >= 0) {
         List var4 = this.getCompleters();
         Completer var5;
         if (var2.wordIndex() >= var4.size()) {
            var5 = (Completer)var4.get(var4.size() - 1);
         } else {
            var5 = (Completer)var4.get(var2.wordIndex());
         }

         for (int var6 = 0; this.isStrict() && var6 < var2.wordIndex(); var6++) {
            Completer var7 = (Completer)var4.get(var6 >= var4.size() ? var4.size() - 1 : var6);
            List var8 = var2.words();
            String var9 = var8 != null && var6 < var8.size() ? ((CharSequence)var8.get(var6)).toString() : "";
            LinkedList var10 = new LinkedList();
            var7.complete(var1, new ArgumentCompleter.ArgumentLine(var9, var9.length()), var10);
            boolean var11 = false;
            Iterator var12 = var10.iterator();

            while (true) {
               if (var12.hasNext()) {
                  Candidate var13 = (Candidate)var12.next();
                  if (!var13.value().equals(var9)) {
                     continue;
                  }

                  var11 = true;
               }

               if (!var11) {
                  return;
               }
               break;
            }
         }

         var5.complete(var1, var2, var3);
      }
   }

   public static class ArgumentLine implements ParsedLine {
      private final String word;
      private final int cursor;

      public ArgumentLine(String var1, int var2) {
         this.word = var1;
         this.cursor = var2;
      }

      @Override
      public String word() {
         return this.word;
      }

      @Override
      public int wordCursor() {
         return this.cursor;
      }

      @Override
      public int wordIndex() {
         return 0;
      }

      @Override
      public List<String> words() {
         return Collections.singletonList(this.word);
      }

      @Override
      public String line() {
         return this.word;
      }

      @Override
      public int cursor() {
         return this.cursor;
      }
   }
}
