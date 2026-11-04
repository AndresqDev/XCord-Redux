package org.jline.reader.impl.completer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedString;

public class StringsCompleter implements Completer {
   protected final Collection<Candidate> candidates = new ArrayList<>();

   public StringsCompleter() {
   }

   public StringsCompleter(String... var1) {
      this(Arrays.asList(var1));
   }

   public StringsCompleter(Iterable<String> var1) {
      assert var1 != null;

      for (String var3 : var1) {
         this.candidates.add(new Candidate(AttributedString.stripAnsi(var3), var3, null, null, null, null, true));
      }
   }

   public StringsCompleter(Candidate... var1) {
      assert var1 != null;

      this.candidates.addAll(Arrays.asList(var1));
   }

   @Override
   public void complete(LineReader var1, ParsedLine var2, List<Candidate> var3) {
      assert var2 != null;

      assert var3 != null;

      var3.addAll(this.candidates);
   }
}
