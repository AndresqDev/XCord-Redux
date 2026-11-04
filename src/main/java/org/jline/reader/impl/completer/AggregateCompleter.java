package org.jline.reader.impl.completer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public class AggregateCompleter implements Completer {
   private final Collection<Completer> completers;

   public AggregateCompleter(Completer... var1) {
      this(Arrays.asList(var1));
   }

   public AggregateCompleter(Collection<Completer> var1) {
      assert var1 != null;

      this.completers = var1;
   }

   public Collection<Completer> getCompleters() {
      return this.completers;
   }

   @Override
   public void complete(LineReader var1, ParsedLine var2, List<Candidate> var3) {
      Objects.requireNonNull(var2);
      Objects.requireNonNull(var3);
      this.completers.forEach(var3x -> var3x.complete(var1, var2, var3));
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + "{completers=" + this.completers + '}';
   }
}
