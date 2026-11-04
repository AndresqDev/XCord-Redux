package joptsimple;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import joptsimple.internal.AbbreviationMap;
import joptsimple.internal.OptionNameMap;
import joptsimple.internal.SimpleOptionNameMap;
import joptsimple.util.KeyValuePair;

public class OptionParser implements OptionDeclarer {
   private final OptionNameMap<AbstractOptionSpec<?>> recognizedOptions;
   private final ArrayList<AbstractOptionSpec<?>> trainingOrder;
   private final Map<List<String>, Set<OptionSpec<?>>> requiredIf;
   private final Map<List<String>, Set<OptionSpec<?>>> requiredUnless;
   private final Map<List<String>, Set<OptionSpec<?>>> availableIf;
   private final Map<List<String>, Set<OptionSpec<?>>> availableUnless;
   private OptionParserState state;
   private boolean posixlyCorrect;
   private boolean allowsUnrecognizedOptions;
   private HelpFormatter helpFormatter = new BuiltinHelpFormatter();

   public OptionParser() {
      this(true);
   }

   public OptionParser(boolean var1) {
      this.trainingOrder = new ArrayList<>();
      this.requiredIf = new HashMap<>();
      this.requiredUnless = new HashMap<>();
      this.availableIf = new HashMap<>();
      this.availableUnless = new HashMap<>();
      this.state = OptionParserState.moreOptions(false);
      this.recognizedOptions = (OptionNameMap<AbstractOptionSpec<?>>)(var1 ? new AbbreviationMap<>() : new SimpleOptionNameMap<>());
      this.recognize(new NonOptionArgumentSpec());
   }

   public OptionParser(String var1) {
      this();
      new OptionSpecTokenizer(var1).configure(this);
   }

   @Override
   public OptionSpecBuilder accepts(String var1) {
      return this.acceptsAll(Collections.singletonList(var1));
   }

   @Override
   public OptionSpecBuilder accepts(String var1, String var2) {
      return this.acceptsAll(Collections.singletonList(var1), var2);
   }

   @Override
   public OptionSpecBuilder acceptsAll(List<String> var1) {
      return this.acceptsAll(var1, "");
   }

   @Override
   public OptionSpecBuilder acceptsAll(List<String> var1, String var2) {
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("need at least one option");
      } else {
         ParserRules.ensureLegalOptions(var1);
         return new OptionSpecBuilder(this, var1, var2);
      }
   }

   @Override
   public NonOptionArgumentSpec<String> nonOptions() {
      NonOptionArgumentSpec var1 = new NonOptionArgumentSpec();
      this.recognize(var1);
      return var1;
   }

   @Override
   public NonOptionArgumentSpec<String> nonOptions(String var1) {
      NonOptionArgumentSpec var2 = new NonOptionArgumentSpec(var1);
      this.recognize(var2);
      return var2;
   }

   @Override
   public void posixlyCorrect(boolean var1) {
      this.posixlyCorrect = var1;
      this.state = OptionParserState.moreOptions(var1);
   }

   boolean posixlyCorrect() {
      return this.posixlyCorrect;
   }

   @Override
   public void allowsUnrecognizedOptions() {
      this.allowsUnrecognizedOptions = true;
   }

   boolean doesAllowsUnrecognizedOptions() {
      return this.allowsUnrecognizedOptions;
   }

   @Override
   public void recognizeAlternativeLongOptions(boolean var1) {
      if (var1) {
         this.recognize(new AlternativeLongOptionSpec());
      } else {
         this.recognizedOptions.remove(String.valueOf("W"));
      }
   }

   void recognize(AbstractOptionSpec<?> var1) {
      this.recognizedOptions.putAll(var1.options(), var1);
      this.trainingOrder.add(var1);
   }

   public void printHelpOn(OutputStream var1) throws IOException {
      this.printHelpOn(new OutputStreamWriter(var1));
   }

   public void printHelpOn(Writer var1) throws IOException {
      var1.write(this.helpFormatter.format(this._recognizedOptions()));
      var1.flush();
   }

   public void formatHelpWith(HelpFormatter var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.helpFormatter = var1;
      }
   }

   public Map<String, OptionSpec<?>> recognizedOptions() {
      return new LinkedHashMap<>(this._recognizedOptions());
   }

   private Map<String, AbstractOptionSpec<?>> _recognizedOptions() {
      LinkedHashMap var1 = new LinkedHashMap();

      for (AbstractOptionSpec var3 : this.trainingOrder) {
         for (String var5 : var3.options()) {
            var1.put(var5, var3);
         }
      }

      return var1;
   }

   public OptionSet parse(String... var1) {
      ArgumentList var2 = new ArgumentList(var1);
      OptionSet var3 = new OptionSet(this.recognizedOptions.toJavaUtilMap());
      var3.add(this.recognizedOptions.get("[arguments]"));

      while (var2.hasMore()) {
         this.state.handleArgument(this, var2, var3);
      }

      this.reset();
      this.ensureRequiredOptions(var3);
      this.ensureAllowedOptions(var3);
      return var3;
   }

   public void mutuallyExclusive(OptionSpecBuilder... var1) {
      for (int var2 = 0; var2 < var1.length; var2++) {
         for (int var3 = 0; var3 < var1.length; var3++) {
            if (var2 != var3) {
               var1[var2].availableUnless(var1[var3]);
            }
         }
      }
   }

   private void ensureRequiredOptions(OptionSet var1) {
      List var2 = this.missingRequiredOptions(var1);
      boolean var3 = this.isHelpOptionPresent(var1);
      if (!var2.isEmpty() && !var3) {
         throw new MissingRequiredOptionsException(var2);
      }
   }

   private void ensureAllowedOptions(OptionSet var1) {
      List var2 = this.unavailableOptions(var1);
      boolean var3 = this.isHelpOptionPresent(var1);
      if (!var2.isEmpty() && !var3) {
         throw new UnavailableOptionException(var2);
      }
   }

   private List<AbstractOptionSpec<?>> missingRequiredOptions(OptionSet var1) {
      ArrayList var2 = new ArrayList();

      for (AbstractOptionSpec var4 : this.recognizedOptions.toJavaUtilMap().values()) {
         if (var4.isRequired() && !var1.has(var4)) {
            var2.add(var4);
         }
      }

      for (Entry var8 : this.requiredIf.entrySet()) {
         AbstractOptionSpec var5 = this.specFor((String)((List)var8.getKey()).iterator().next());
         if (this.optionsHasAnyOf(var1, (Collection<OptionSpec<?>>)var8.getValue()) && !var1.has(var5)) {
            var2.add(var5);
         }
      }

      for (Entry var9 : this.requiredUnless.entrySet()) {
         AbstractOptionSpec var10 = this.specFor((String)((List)var9.getKey()).iterator().next());
         if (!this.optionsHasAnyOf(var1, (Collection<OptionSpec<?>>)var9.getValue()) && !var1.has(var10)) {
            var2.add(var10);
         }
      }

      return var2;
   }

   private List<AbstractOptionSpec<?>> unavailableOptions(OptionSet var1) {
      ArrayList var2 = new ArrayList();

      for (Entry var4 : this.availableIf.entrySet()) {
         AbstractOptionSpec var5 = this.specFor((String)((List)var4.getKey()).iterator().next());
         if (!this.optionsHasAnyOf(var1, (Collection<OptionSpec<?>>)var4.getValue()) && var1.has(var5)) {
            var2.add(var5);
         }
      }

      for (Entry var7 : this.availableUnless.entrySet()) {
         AbstractOptionSpec var8 = this.specFor((String)((List)var7.getKey()).iterator().next());
         if (this.optionsHasAnyOf(var1, (Collection<OptionSpec<?>>)var7.getValue()) && var1.has(var8)) {
            var2.add(var8);
         }
      }

      return var2;
   }

   private boolean optionsHasAnyOf(OptionSet var1, Collection<OptionSpec<?>> var2) {
      for (OptionSpec var4 : var2) {
         if (var1.has(var4)) {
            return true;
         }
      }

      return false;
   }

   private boolean isHelpOptionPresent(OptionSet var1) {
      boolean var2 = false;

      for (AbstractOptionSpec var4 : this.recognizedOptions.toJavaUtilMap().values()) {
         if (var4.isForHelp() && var1.has(var4)) {
            var2 = true;
            break;
         }
      }

      return var2;
   }

   void handleLongOptionToken(String var1, ArgumentList var2, OptionSet var3) {
      KeyValuePair var4 = parseLongOptionWithArgument(var1);
      if (!this.isRecognized(var4.key)) {
         throw OptionException.unrecognizedOption(var4.key);
      } else {
         AbstractOptionSpec var5 = this.specFor(var4.key);
         var5.handleOption(this, var2, var3, var4.value);
      }
   }

   void handleShortOptionToken(String var1, ArgumentList var2, OptionSet var3) {
      KeyValuePair var4 = parseShortOptionWithArgument(var1);
      if (this.isRecognized(var4.key)) {
         this.specFor(var4.key).handleOption(this, var2, var3, var4.value);
      } else {
         this.handleShortOptionCluster(var1, var2, var3);
      }
   }

   private void handleShortOptionCluster(String var1, ArgumentList var2, OptionSet var3) {
      char[] var4 = extractShortOptionsFrom(var1);
      this.validateOptionCharacters(var4);

      for (int var5 = 0; var5 < var4.length; var5++) {
         AbstractOptionSpec var6 = this.specFor(var4[var5]);
         if (var6.acceptsArguments() && var4.length > var5 + 1) {
            String var7 = String.valueOf(var4, var5 + 1, var4.length - 1 - var5);
            var6.handleOption(this, var2, var3, var7);
            break;
         }

         var6.handleOption(this, var2, var3, null);
      }
   }

   void handleNonOptionArgument(String var1, ArgumentList var2, OptionSet var3) {
      this.specFor("[arguments]").handleOption(this, var2, var3, var1);
   }

   void noMoreOptions() {
      this.state = OptionParserState.noMoreOptions();
   }

   boolean looksLikeAnOption(String var1) {
      return ParserRules.isShortOptionToken(var1) || ParserRules.isLongOptionToken(var1);
   }

   boolean isRecognized(String var1) {
      return this.recognizedOptions.contains(var1);
   }

   void requiredIf(List<String> var1, String var2) {
      this.requiredIf(var1, this.specFor(var2));
   }

   void requiredIf(List<String> var1, OptionSpec<?> var2) {
      this.putDependentOption(var1, var2, this.requiredIf);
   }

   void requiredUnless(List<String> var1, String var2) {
      this.requiredUnless(var1, this.specFor(var2));
   }

   void requiredUnless(List<String> var1, OptionSpec<?> var2) {
      this.putDependentOption(var1, var2, this.requiredUnless);
   }

   void availableIf(List<String> var1, String var2) {
      this.availableIf(var1, this.specFor(var2));
   }

   void availableIf(List<String> var1, OptionSpec<?> var2) {
      this.putDependentOption(var1, var2, this.availableIf);
   }

   void availableUnless(List<String> var1, String var2) {
      this.availableUnless(var1, this.specFor(var2));
   }

   void availableUnless(List<String> var1, OptionSpec<?> var2) {
      this.putDependentOption(var1, var2, this.availableUnless);
   }

   private void putDependentOption(List<String> var1, OptionSpec<?> var2, Map<List<String>, Set<OptionSpec<?>>> var3) {
      for (String var5 : var1) {
         AbstractOptionSpec var6 = this.specFor(var5);
         if (var6 == null) {
            throw new UnconfiguredOptionException(var1);
         }
      }

      Object var7 = (Set)var3.get(var1);
      if (var7 == null) {
         var7 = new HashSet();
         var3.put(var1, var7);
      }

      var7.add(var2);
   }

   private AbstractOptionSpec<?> specFor(char var1) {
      return this.specFor(String.valueOf(var1));
   }

   private AbstractOptionSpec<?> specFor(String var1) {
      return this.recognizedOptions.get(var1);
   }

   private void reset() {
      this.state = OptionParserState.moreOptions(this.posixlyCorrect);
   }

   private static char[] extractShortOptionsFrom(String var0) {
      char[] var1 = new char[var0.length() - 1];
      var0.getChars(1, var0.length(), var1, 0);
      return var1;
   }

   private void validateOptionCharacters(char[] var1) {
      for (char var5 : var1) {
         String var6 = String.valueOf(var5);
         if (!this.isRecognized(var6)) {
            throw OptionException.unrecognizedOption(var6);
         }

         if (this.specFor(var6).acceptsArguments()) {
            return;
         }
      }
   }

   private static KeyValuePair parseLongOptionWithArgument(String var0) {
      return KeyValuePair.valueOf(var0.substring(2));
   }

   private static KeyValuePair parseShortOptionWithArgument(String var0) {
      return KeyValuePair.valueOf(var0.substring(1));
   }
}
