package org.apache.commons.codec.language.bm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PhoneticEngine {
   private static final Map<NameType, Set<String>> NAME_PREFIXES = new EnumMap<>(NameType.class);
   private static final int DEFAULT_MAX_PHONEMES = 20;
   private final Lang lang;
   private final NameType nameType;
   private final RuleType ruleType;
   private final boolean concat;
   private final int maxPhonemes;

   private static String join(Iterable<String> var0, String var1) {
      StringBuilder var2 = new StringBuilder();
      Iterator var3 = var0.iterator();
      if (var3.hasNext()) {
         var2.append((String)var3.next());
      }

      while (var3.hasNext()) {
         var2.append(var1).append((String)var3.next());
      }

      return var2.toString();
   }

   public PhoneticEngine(NameType var1, RuleType var2, boolean var3) {
      this(var1, var2, var3, 20);
   }

   public PhoneticEngine(NameType var1, RuleType var2, boolean var3, int var4) {
      if (var2 == RuleType.RULES) {
         throw new IllegalArgumentException("ruleType must not be " + RuleType.RULES);
      } else {
         this.nameType = var1;
         this.ruleType = var2;
         this.concat = var3;
         this.lang = Lang.instance(var1);
         this.maxPhonemes = var4;
      }
   }

   private PhoneticEngine.PhonemeBuilder applyFinalRules(PhoneticEngine.PhonemeBuilder var1, Map<String, List<Rule>> var2) {
      if (var2 == null) {
         throw new NullPointerException("finalRules can not be null");
      } else if (var2.isEmpty()) {
         return var1;
      } else {
         TreeMap var3 = new TreeMap<>(Rule.Phoneme.COMPARATOR);

         for (Rule.Phoneme var5 : var1.getPhonemes()) {
            PhoneticEngine.PhonemeBuilder var6 = PhoneticEngine.PhonemeBuilder.empty(var5.getLanguages());
            String var7 = var5.getPhonemeText().toString();
            int var8 = 0;

            while (var8 < var7.length()) {
               PhoneticEngine.RulesApplication var9 = new PhoneticEngine.RulesApplication(var2, var7, var6, var8, this.maxPhonemes).invoke();
               boolean var10 = var9.isFound();
               var6 = var9.getPhonemeBuilder();
               if (!var10) {
                  var6.append(var7.subSequence(var8, var8 + 1));
               }

               var8 = var9.getI();
            }

            for (Rule.Phoneme var13 : var6.getPhonemes()) {
               if (var3.containsKey(var13)) {
                  Rule.Phoneme var14 = (Rule.Phoneme)var3.remove(var13);
                  Rule.Phoneme var11 = var14.mergeWithLanguage(var13.getLanguages());
                  var3.put(var11, var11);
               } else {
                  var3.put(var13, var13);
               }
            }
         }

         return new PhoneticEngine.PhonemeBuilder(var3.keySet());
      }
   }

   public String encode(String var1) {
      Languages.LanguageSet var2 = this.lang.guessLanguages(var1);
      return this.encode(var1, var2);
   }

   public String encode(String var1, Languages.LanguageSet var2) {
      Map var3 = Rule.getInstanceMap(this.nameType, RuleType.RULES, var2);
      Map var4 = Rule.getInstanceMap(this.nameType, this.ruleType, "common");
      Map var5 = Rule.getInstanceMap(this.nameType, this.ruleType, var2);
      var1 = var1.toLowerCase(Locale.ENGLISH).replace('-', ' ').trim();
      if (this.nameType == NameType.GENERIC) {
         if (var1.length() >= 2 && var1.substring(0, 2).equals("d'")) {
            String var15 = var1.substring(2);
            String var17 = "d" + var15;
            return "(" + this.encode(var15) + ")-(" + this.encode(var17) + ")";
         }

         for (String var7 : NAME_PREFIXES.get(this.nameType)) {
            if (var1.startsWith(var7 + " ")) {
               String var8 = var1.substring(var7.length() + 1);
               String var9 = var7 + var8;
               return "(" + this.encode(var8) + ")-(" + this.encode(var9) + ")";
            }
         }
      }

      List var14 = Arrays.asList(var1.split("\\s+"));
      ArrayList var16 = new ArrayList();
      switch (this.nameType) {
         case SEPHARDIC:
            for (String var23 : var14) {
               String[] var10 = var23.split("'");
               String var11 = var10[var10.length - 1];
               var16.add(var11);
            }

            var16.removeAll(NAME_PREFIXES.get(this.nameType));
            break;
         case ASHKENAZI:
            var16.addAll(var14);
            var16.removeAll(NAME_PREFIXES.get(this.nameType));
            break;
         case GENERIC:
            var16.addAll(var14);
            break;
         default:
            throw new IllegalStateException("Unreachable case: " + this.nameType);
      }

      if (this.concat) {
         var1 = join(var16, " ");
      } else {
         if (var16.size() != 1) {
            StringBuilder var22 = new StringBuilder();

            for (String var27 : var16) {
               var22.append("-").append(this.encode(var27));
            }

            return var22.substring(1);
         }

         var1 = (String)var14.iterator().next();
      }

      PhoneticEngine.PhonemeBuilder var19 = PhoneticEngine.PhonemeBuilder.empty(var2);
      int var24 = 0;

      while (var24 < var1.length()) {
         PhoneticEngine.RulesApplication var26 = new PhoneticEngine.RulesApplication(var3, var1, var19, var24, this.maxPhonemes).invoke();
         var24 = var26.getI();
         var19 = var26.getPhonemeBuilder();
      }

      var19 = this.applyFinalRules(var19, var4);
      var19 = this.applyFinalRules(var19, var5);
      return var19.makeString();
   }

   public Lang getLang() {
      return this.lang;
   }

   public NameType getNameType() {
      return this.nameType;
   }

   public RuleType getRuleType() {
      return this.ruleType;
   }

   public boolean isConcat() {
      return this.concat;
   }

   public int getMaxPhonemes() {
      return this.maxPhonemes;
   }

   static {
      NAME_PREFIXES.put(NameType.ASHKENAZI, Collections.unmodifiableSet(new HashSet<>(Arrays.asList("bar", "ben", "da", "de", "van", "von"))));
      NAME_PREFIXES.put(
         NameType.SEPHARDIC,
         Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("al", "el", "da", "dal", "de", "del", "dela", "de la", "della", "des", "di", "do", "dos", "du", "van", "von"))
         )
      );
      NAME_PREFIXES.put(
         NameType.GENERIC,
         Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("da", "dal", "de", "del", "dela", "de la", "della", "des", "di", "do", "dos", "du", "van", "von"))
         )
      );
   }

   static final class PhonemeBuilder {
      private final Set<Rule.Phoneme> phonemes;

      public static PhoneticEngine.PhonemeBuilder empty(Languages.LanguageSet var0) {
         return new PhoneticEngine.PhonemeBuilder(new Rule.Phoneme("", var0));
      }

      private PhonemeBuilder(Rule.Phoneme var1) {
         this.phonemes = new LinkedHashSet<>();
         this.phonemes.add(var1);
      }

      private PhonemeBuilder(Set<Rule.Phoneme> var1) {
         this.phonemes = var1;
      }

      public void append(CharSequence var1) {
         for (Rule.Phoneme var3 : this.phonemes) {
            var3.append(var1);
         }
      }

      public void apply(Rule.PhonemeExpr var1, int var2) {
         LinkedHashSet var3 = new LinkedHashSet(var2);

         label25:
         for (Rule.Phoneme var5 : this.phonemes) {
            for (Rule.Phoneme var7 : var1.getPhonemes()) {
               Languages.LanguageSet var8 = var5.getLanguages().restrictTo(var7.getLanguages());
               if (!var8.isEmpty()) {
                  Rule.Phoneme var9 = new Rule.Phoneme(var5, var7, var8);
                  if (var3.size() < var2) {
                     var3.add(var9);
                     if (var3.size() >= var2) {
                        break label25;
                     }
                  }
               }
            }
         }

         this.phonemes.clear();
         this.phonemes.addAll(var3);
      }

      public Set<Rule.Phoneme> getPhonemes() {
         return this.phonemes;
      }

      public String makeString() {
         StringBuilder var1 = new StringBuilder();

         for (Rule.Phoneme var3 : this.phonemes) {
            if (var1.length() > 0) {
               var1.append("|");
            }

            var1.append(var3.getPhonemeText());
         }

         return var1.toString();
      }
   }

   private static final class RulesApplication {
      private final Map<String, List<Rule>> finalRules;
      private final CharSequence input;
      private final PhoneticEngine.PhonemeBuilder phonemeBuilder;
      private int i;
      private final int maxPhonemes;
      private boolean found;

      public RulesApplication(Map<String, List<Rule>> var1, CharSequence var2, PhoneticEngine.PhonemeBuilder var3, int var4, int var5) {
         if (var1 == null) {
            throw new NullPointerException("The finalRules argument must not be null");
         } else {
            this.finalRules = var1;
            this.phonemeBuilder = var3;
            this.input = var2;
            this.i = var4;
            this.maxPhonemes = var5;
         }
      }

      public int getI() {
         return this.i;
      }

      public PhoneticEngine.PhonemeBuilder getPhonemeBuilder() {
         return this.phonemeBuilder;
      }

      public PhoneticEngine.RulesApplication invoke() {
         this.found = false;
         int var1 = 1;
         List var2 = this.finalRules.get(this.input.subSequence(this.i, this.i + var1));
         if (var2 != null) {
            for (Rule var4 : var2) {
               String var5 = var4.getPattern();
               var1 = var5.length();
               if (var4.patternAndContextMatches(this.input, this.i)) {
                  this.phonemeBuilder.apply(var4.getPhoneme(), this.maxPhonemes);
                  this.found = true;
                  break;
               }
            }
         }

         if (!this.found) {
            var1 = 1;
         }

         this.i += var1;
         return this;
      }

      public boolean isFound() {
         return this.found;
      }
   }
}
