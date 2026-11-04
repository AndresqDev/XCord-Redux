package org.jline.reader.impl.completer;

import java.util.Objects;
import org.jline.reader.Candidate;

public class EnumCompleter extends StringsCompleter {
   public EnumCompleter(Class<? extends Enum<?>> var1) {
      Objects.requireNonNull(var1);

      for (Enum var5 : (Enum[])var1.getEnumConstants()) {
         this.candidates.add(new Candidate(var5.name().toLowerCase()));
      }
   }
}
