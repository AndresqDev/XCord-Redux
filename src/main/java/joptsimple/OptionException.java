package joptsimple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import joptsimple.internal.Messages;
import joptsimple.internal.Strings;

public abstract class OptionException extends RuntimeException {
   private static final long serialVersionUID = -1L;
   private final List<String> options = new ArrayList<>();

   protected OptionException(List<String> var1) {
      this.options.addAll(var1);
   }

   protected OptionException(Collection<? extends OptionSpec<?>> var1) {
      this.options.addAll(this.specsToStrings(var1));
   }

   protected OptionException(Collection<? extends OptionSpec<?>> var1, Throwable var2) {
      super(var2);
      this.options.addAll(this.specsToStrings(var1));
   }

   private List<String> specsToStrings(Collection<? extends OptionSpec<?>> var1) {
      ArrayList var2 = new ArrayList();

      for (OptionSpec var4 : var1) {
         var2.add(this.specToString(var4));
      }

      return var2;
   }

   private String specToString(OptionSpec<?> var1) {
      return Strings.join(new ArrayList<>(var1.options()), "/");
   }

   public List<String> options() {
      return Collections.unmodifiableList(this.options);
   }

   protected final String singleOptionString() {
      return this.singleOptionString(this.options.get(0));
   }

   protected final String singleOptionString(String var1) {
      return var1;
   }

   protected final String multipleOptionString() {
      StringBuilder var1 = new StringBuilder("[");
      LinkedHashSet var2 = new LinkedHashSet<>(this.options);
      Iterator var3 = var2.iterator();

      while (var3.hasNext()) {
         var1.append(this.singleOptionString((String)var3.next()));
         if (var3.hasNext()) {
            var1.append(", ");
         }
      }

      var1.append(']');
      return var1.toString();
   }

   static OptionException unrecognizedOption(String var0) {
      return new UnrecognizedOptionException(var0);
   }

   @Override
   public final String getMessage() {
      return this.localizedMessage(Locale.getDefault());
   }

   final String localizedMessage(Locale var1) {
      return this.formattedMessage(var1);
   }

   private String formattedMessage(Locale var1) {
      return Messages.message(var1, "joptsimple.ExceptionMessages", this.getClass(), "message", this.messageArguments());
   }

   abstract Object[] messageArguments();
}
