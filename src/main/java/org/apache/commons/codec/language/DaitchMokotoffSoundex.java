package org.apache.commons.codec.language;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

public class DaitchMokotoffSoundex implements StringEncoder {
   private static final String COMMENT = "//";
   private static final String DOUBLE_QUOTE = "\"";
   private static final String MULTILINE_COMMENT_END = "*/";
   private static final String MULTILINE_COMMENT_START = "/*";
   private static final String RESOURCE_FILE = "org/apache/commons/codec/language/dmrules.txt";
   private static final int MAX_LENGTH = 6;
   private static final Map<Character, List<DaitchMokotoffSoundex.Rule>> RULES = new HashMap<>();
   private static final Map<Character, Character> FOLDINGS = new HashMap<>();
   private final boolean folding;

   private static void parseRules(Scanner var0, String var1, Map<Character, List<DaitchMokotoffSoundex.Rule>> var2, Map<Character, Character> var3) {
      int var4 = 0;
      boolean var5 = false;

      while (var0.hasNextLine()) {
         var4++;
         String var6 = var0.nextLine();
         String var7 = var6;
         if (!var5) {
            if (!var6.startsWith("/*")) {
               int var8 = var6.indexOf("//");
               if (var8 >= 0) {
                  var7 = var6.substring(0, var8);
               }

               var7 = var7.trim();
               if (var7.length() != 0) {
                  if (var7.contains("=")) {
                     String[] var19 = var7.split("=");
                     if (var19.length != 2) {
                        throw new IllegalArgumentException("Malformed folding statement split into " + var19.length + " parts: " + var6 + " in " + var1);
                     }

                     String var20 = var19[0];
                     String var21 = var19[1];
                     if (var20.length() != 1 || var21.length() != 1) {
                        throw new IllegalArgumentException("Malformed folding statement - patterns are not single characters: " + var6 + " in " + var1);
                     }

                     var3.put(var20.charAt(0), var21.charAt(0));
                  } else {
                     String[] var9 = var7.split("\\s+");
                     if (var9.length != 4) {
                        throw new IllegalArgumentException("Malformed rule statement split into " + var9.length + " parts: " + var6 + " in " + var1);
                     }

                     try {
                        String var10 = stripQuotes(var9[0]);
                        String var11 = stripQuotes(var9[1]);
                        String var12 = stripQuotes(var9[2]);
                        String var13 = stripQuotes(var9[3]);
                        DaitchMokotoffSoundex.Rule var14 = new DaitchMokotoffSoundex.Rule(var10, var11, var12, var13);
                        char var15 = var14.pattern.charAt(0);
                        Object var16 = (List)var2.get(var15);
                        if (var16 == null) {
                           var16 = new ArrayList();
                           var2.put(var15, var16);
                        }

                        var16.add(var14);
                     } catch (IllegalArgumentException var17) {
                        throw new IllegalStateException("Problem parsing line '" + var4 + "' in " + var1, var17);
                     }
                  }
               }
            } else {
               var5 = true;
            }
         } else if (var6.endsWith("*/")) {
            var5 = false;
         }
      }
   }

   private static String stripQuotes(String var0) {
      if (var0.startsWith("\"")) {
         var0 = var0.substring(1);
      }

      if (var0.endsWith("\"")) {
         var0 = var0.substring(0, var0.length() - 1);
      }

      return var0;
   }

   public DaitchMokotoffSoundex() {
      this(true);
   }

   public DaitchMokotoffSoundex(boolean var1) {
      this.folding = var1;
   }

   private String cleanup(String var1) {
      StringBuilder var2 = new StringBuilder();

      for (char var6 : var1.toCharArray()) {
         if (!Character.isWhitespace(var6)) {
            var6 = Character.toLowerCase(var6);
            if (this.folding && FOLDINGS.containsKey(var6)) {
               var6 = FOLDINGS.get(var6);
            }

            var2.append(var6);
         }
      }

      return var2.toString();
   }

   @Override
   public Object encode(Object var1) throws EncoderException {
      if (!(var1 instanceof String)) {
         throw new EncoderException("Parameter supplied to DaitchMokotoffSoundex encode is not of type java.lang.String");
      } else {
         return this.encode((String)var1);
      }
   }

   @Override
   public String encode(String var1) {
      return var1 == null ? null : this.soundex(var1, false)[0];
   }

   public String soundex(String var1) {
      String[] var2 = this.soundex(var1, true);
      StringBuilder var3 = new StringBuilder();
      int var4 = 0;

      for (String var8 : var2) {
         var3.append(var8);
         if (++var4 < var2.length) {
            var3.append('|');
         }
      }

      return var3.toString();
   }

   private String[] soundex(String var1, boolean var2) {
      if (var1 == null) {
         return null;
      } else {
         String var3 = this.cleanup(var1);
         LinkedHashSet var4 = new LinkedHashSet();
         var4.add(new DaitchMokotoffSoundex.Branch());
         char var5 = 0;

         for (int var6 = 0; var6 < var3.length(); var6++) {
            char var7 = var3.charAt(var6);
            if (!Character.isWhitespace(var7)) {
               String var8 = var3.substring(var6);
               List var9 = RULES.get(var7);
               if (var9 != null) {
                  Object var10 = var2 ? new ArrayList() : Collections.EMPTY_LIST;

                  for (DaitchMokotoffSoundex.Rule var12 : var9) {
                     if (var12.matches(var8)) {
                        if (var2) {
                           var10.clear();
                        }

                        String[] var13 = var12.getReplacements(var8, var5 == 0);
                        boolean var14 = var13.length > 1 && var2;

                        for (DaitchMokotoffSoundex.Branch var16 : var4) {
                           for (String var20 : var13) {
                              DaitchMokotoffSoundex.Branch var21 = var14 ? var16.createBranch() : var16;
                              boolean var22 = var5 == 'm' && var7 == 'n' || var5 == 'n' && var7 == 'm';
                              var21.processNextReplacement(var20, var22);
                              if (!var2) {
                                 break;
                              }

                              var10.add(var21);
                           }
                        }

                        if (var2) {
                           var4.clear();
                           var4.addAll((Collection)var10);
                        }

                        var6 += var12.getPatternLength() - 1;
                        break;
                     }
                  }

                  var5 = var7;
               }
            }
         }

         String[] var23 = new String[var4.size()];
         int var24 = 0;

         for (DaitchMokotoffSoundex.Branch var26 : var4) {
            var26.finish();
            var23[var24++] = var26.toString();
         }

         return var23;
      }
   }

   static {
      InputStream var0 = DaitchMokotoffSoundex.class.getClassLoader().getResourceAsStream("org/apache/commons/codec/language/dmrules.txt");
      if (var0 == null) {
         throw new IllegalArgumentException("Unable to load resource: org/apache/commons/codec/language/dmrules.txt");
      } else {
         Scanner var1 = new Scanner(var0, "UTF-8");

         try {
            parseRules(var1, "org/apache/commons/codec/language/dmrules.txt", RULES, FOLDINGS);
         } finally {
            var1.close();
         }

         for (Entry var3 : RULES.entrySet()) {
            List var4 = (List)var3.getValue();
            Collections.sort(var4, new Comparator<DaitchMokotoffSoundex.Rule>() {
               public int compare(DaitchMokotoffSoundex.Rule var1, DaitchMokotoffSoundex.Rule var2) {
                  return var2.getPatternLength() - var1.getPatternLength();
               }
            });
         }
      }
   }

   private static final class Branch {
      private final StringBuilder builder = new StringBuilder();
      private String cachedString;
      private String lastReplacement = null;

      private Branch() {
         this.cachedString = null;
      }

      public DaitchMokotoffSoundex.Branch createBranch() {
         DaitchMokotoffSoundex.Branch var1 = new DaitchMokotoffSoundex.Branch();
         var1.builder.append(this.toString());
         var1.lastReplacement = this.lastReplacement;
         return var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return !(var1 instanceof DaitchMokotoffSoundex.Branch) ? false : this.toString().equals(((DaitchMokotoffSoundex.Branch)var1).toString());
         }
      }

      public void finish() {
         while (this.builder.length() < 6) {
            this.builder.append('0');
            this.cachedString = null;
         }
      }

      @Override
      public int hashCode() {
         return this.toString().hashCode();
      }

      public void processNextReplacement(String var1, boolean var2) {
         boolean var3 = this.lastReplacement == null || !this.lastReplacement.endsWith(var1) || var2;
         if (var3 && this.builder.length() < 6) {
            this.builder.append(var1);
            if (this.builder.length() > 6) {
               this.builder.delete(6, this.builder.length());
            }

            this.cachedString = null;
         }

         this.lastReplacement = var1;
      }

      @Override
      public String toString() {
         if (this.cachedString == null) {
            this.cachedString = this.builder.toString();
         }

         return this.cachedString;
      }
   }

   private static final class Rule {
      private final String pattern;
      private final String[] replacementAtStart;
      private final String[] replacementBeforeVowel;
      private final String[] replacementDefault;

      protected Rule(String var1, String var2, String var3, String var4) {
         this.pattern = var1;
         this.replacementAtStart = var2.split("\\|");
         this.replacementBeforeVowel = var3.split("\\|");
         this.replacementDefault = var4.split("\\|");
      }

      public int getPatternLength() {
         return this.pattern.length();
      }

      public String[] getReplacements(String var1, boolean var2) {
         if (var2) {
            return this.replacementAtStart;
         } else {
            int var3 = this.getPatternLength();
            boolean var4 = var3 < var1.length() ? this.isVowel(var1.charAt(var3)) : false;
            return var4 ? this.replacementBeforeVowel : this.replacementDefault;
         }
      }

      private boolean isVowel(char var1) {
         return var1 == 'a' || var1 == 'e' || var1 == 'i' || var1 == 'o' || var1 == 'u';
      }

      public boolean matches(String var1) {
         return var1.startsWith(this.pattern);
      }

      @Override
      public String toString() {
         return String.format(
            "%s=(%s,%s,%s)",
            this.pattern,
            Arrays.asList(this.replacementAtStart),
            Arrays.asList(this.replacementBeforeVowel),
            Arrays.asList(this.replacementDefault)
         );
      }
   }
}
