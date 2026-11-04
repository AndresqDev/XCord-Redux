package org.apache.logging.log4j.core.lookup;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationAware;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public class StrSubstitutor implements ConfigurationAware {
   public static final char DEFAULT_ESCAPE = '$';
   public static final StrMatcher DEFAULT_PREFIX = StrMatcher.stringMatcher("${");
   public static final StrMatcher DEFAULT_SUFFIX = StrMatcher.stringMatcher("}");
   public static final String DEFAULT_VALUE_DELIMITER_STRING = ":-";
   public static final StrMatcher DEFAULT_VALUE_DELIMITER = StrMatcher.stringMatcher(":-");
   public static final String ESCAPE_DELIMITER_STRING = ":\\-";
   public static final StrMatcher DEFAULT_VALUE_ESCAPE_DELIMITER = StrMatcher.stringMatcher(":\\-");
   private static final int BUF_SIZE = 256;
   private char escapeChar;
   private StrMatcher prefixMatcher;
   private StrMatcher suffixMatcher;
   private String valueDelimiterString;
   private StrMatcher valueDelimiterMatcher;
   private StrMatcher valueEscapeDelimiterMatcher;
   private StrLookup variableResolver;
   private boolean enableSubstitutionInVariables = true;
   private Configuration configuration;
   private boolean recursiveEvaluationAllowed;

   public StrSubstitutor() {
      this(null, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
   }

   public StrSubstitutor(Map<String, String> var1) {
      this(new PropertiesLookup(var1), DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
   }

   public StrSubstitutor(Map<String, String> var1, String var2, String var3) {
      this(new PropertiesLookup(var1), var2, var3, '$');
   }

   public StrSubstitutor(Map<String, String> var1, String var2, String var3, char var4) {
      this(new PropertiesLookup(var1), var2, var3, var4);
   }

   public StrSubstitutor(Map<String, String> var1, String var2, String var3, char var4, String var5) {
      this(new PropertiesLookup(var1), var2, var3, var4, var5);
   }

   public StrSubstitutor(Properties var1) {
      this(toTypeSafeMap(var1));
   }

   public StrSubstitutor(StrLookup var1) {
      this(var1, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
   }

   public StrSubstitutor(StrLookup var1, String var2, String var3, char var4) {
      this.setVariableResolver(var1);
      this.setVariablePrefix(var2);
      this.setVariableSuffix(var3);
      this.setEscapeChar(var4);
   }

   public StrSubstitutor(StrLookup var1, String var2, String var3, char var4, String var5) {
      this.setVariableResolver(var1);
      this.setVariablePrefix(var2);
      this.setVariableSuffix(var3);
      this.setEscapeChar(var4);
      this.setValueDelimiter(var5);
   }

   public StrSubstitutor(StrLookup var1, StrMatcher var2, StrMatcher var3, char var4) {
      this(var1, var2, var3, var4, DEFAULT_VALUE_DELIMITER, DEFAULT_VALUE_ESCAPE_DELIMITER);
      this.valueDelimiterString = ":-";
   }

   public StrSubstitutor(StrLookup var1, StrMatcher var2, StrMatcher var3, char var4, StrMatcher var5) {
      this.setVariableResolver(var1);
      this.setVariablePrefixMatcher(var2);
      this.setVariableSuffixMatcher(var3);
      this.setEscapeChar(var4);
      this.setValueDelimiterMatcher(var5);
   }

   public StrSubstitutor(StrLookup var1, StrMatcher var2, StrMatcher var3, char var4, StrMatcher var5, StrMatcher var6) {
      this.setVariableResolver(var1);
      this.setVariablePrefixMatcher(var2);
      this.setVariableSuffixMatcher(var3);
      this.setEscapeChar(var4);
      this.setValueDelimiterMatcher(var5);
      this.valueEscapeDelimiterMatcher = var6;
   }

   StrSubstitutor(StrSubstitutor var1) {
      Objects.requireNonNull(var1, "other");
      this.setVariableResolver(var1.getVariableResolver());
      this.setVariablePrefixMatcher(var1.getVariablePrefixMatcher());
      this.setVariableSuffixMatcher(var1.getVariableSuffixMatcher());
      this.setEscapeChar(var1.getEscapeChar());
      this.setValueDelimiterMatcher(var1.valueDelimiterMatcher);
      this.valueEscapeDelimiterMatcher = var1.valueEscapeDelimiterMatcher;
      this.configuration = var1.configuration;
      this.recursiveEvaluationAllowed = var1.isRecursiveEvaluationAllowed();
      this.enableSubstitutionInVariables = var1.isEnableSubstitutionInVariables();
      this.valueDelimiterString = var1.valueDelimiterString;
   }

   public static String replace(Object var0, Map<String, String> var1) {
      return new StrSubstitutor(var1).replace(var0);
   }

   public static String replace(Object var0, Map<String, String> var1, String var2, String var3) {
      return new StrSubstitutor(var1, var2, var3).replace(var0);
   }

   public static String replace(Object var0, Properties var1) {
      if (var1 == null) {
         return Objects.toString(var0, null);
      } else {
         HashMap var2 = new HashMap();
         Enumeration var3 = var1.propertyNames();

         while (var3.hasMoreElements()) {
            String var4 = (String)var3.nextElement();
            String var5 = var1.getProperty(var4);
            var2.put(var4, var5);
         }

         return replace(var0, var2);
      }
   }

   private static Map<String, String> toTypeSafeMap(Properties var0) {
      HashMap var1 = new HashMap(var0.size());

      for (String var3 : var0.stringPropertyNames()) {
         var1.put(var3, var0.getProperty(var3));
      }

      return var1;
   }

   private static String handleFailedReplacement(String var0, Throwable var1) {
      StatusLogger.getLogger().error("Replacement failed on {}", var0, var1);
      return var0;
   }

   public String replace(String var1) {
      return this.replace(null, var1);
   }

   public String replace(LogEvent var1, String var2) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var3 = new StringBuilder(var2);

         try {
            if (!this.substitute(var1, var3, 0, var2.length())) {
               return var2;
            }
         } catch (Throwable var5) {
            return handleFailedReplacement(var2, var5);
         }

         return var3.toString();
      }
   }

   public String replace(String var1, int var2, int var3) {
      return this.replace(null, var1, var2, var3);
   }

   public String replace(LogEvent var1, String var2, int var3, int var4) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var5 = new StringBuilder(var4).append(var2, var3, var4);

         try {
            if (!this.substitute(var1, var5, 0, var4)) {
               return var2.substring(var3, var3 + var4);
            }
         } catch (Throwable var7) {
            return handleFailedReplacement(var2, var7);
         }

         return var5.toString();
      }
   }

   public String replace(char[] var1) {
      return this.replace(null, var1);
   }

   public String replace(LogEvent var1, char[] var2) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var3 = new StringBuilder(var2.length).append(var2);

         try {
            this.substitute(var1, var3, 0, var2.length);
         } catch (Throwable var5) {
            return handleFailedReplacement(new String(var2), var5);
         }

         return var3.toString();
      }
   }

   public String replace(char[] var1, int var2, int var3) {
      return this.replace(null, var1, var2, var3);
   }

   public String replace(LogEvent var1, char[] var2, int var3, int var4) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var5 = new StringBuilder(var4).append(var2, var3, var4);

         try {
            this.substitute(var1, var5, 0, var4);
         } catch (Throwable var7) {
            return handleFailedReplacement(new String(var2, var3, var4), var7);
         }

         return var5.toString();
      }
   }

   public String replace(StringBuffer var1) {
      return this.replace(null, var1);
   }

   public String replace(LogEvent var1, StringBuffer var2) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var3 = new StringBuilder(var2.length()).append(var2);

         try {
            this.substitute(var1, var3, 0, var3.length());
         } catch (Throwable var5) {
            return handleFailedReplacement(var2.toString(), var5);
         }

         return var3.toString();
      }
   }

   public String replace(StringBuffer var1, int var2, int var3) {
      return this.replace(null, var1, var2, var3);
   }

   public String replace(LogEvent var1, StringBuffer var2, int var3, int var4) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var5 = new StringBuilder(var4).append(var2, var3, var4);

         try {
            this.substitute(var1, var5, 0, var4);
         } catch (Throwable var7) {
            return handleFailedReplacement(var2.substring(var3, var3 + var4), var7);
         }

         return var5.toString();
      }
   }

   public String replace(StringBuilder var1) {
      return this.replace(null, var1);
   }

   public String replace(LogEvent var1, StringBuilder var2) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var3 = new StringBuilder(var2.length()).append((CharSequence)var2);

         try {
            this.substitute(var1, var3, 0, var3.length());
         } catch (Throwable var5) {
            return handleFailedReplacement(var2.toString(), var5);
         }

         return var3.toString();
      }
   }

   public String replace(StringBuilder var1, int var2, int var3) {
      return this.replace(null, var1, var2, var3);
   }

   public String replace(LogEvent var1, StringBuilder var2, int var3, int var4) {
      if (var2 == null) {
         return null;
      } else {
         StringBuilder var5 = new StringBuilder(var4).append(var2, var3, var4);

         try {
            this.substitute(var1, var5, 0, var4);
         } catch (Throwable var7) {
            return handleFailedReplacement(var2.substring(var3, var3 + var4), var7);
         }

         return var5.toString();
      }
   }

   public String replace(Object var1) {
      return this.replace(null, var1);
   }

   public String replace(LogEvent var1, Object var2) {
      if (var2 == null) {
         return null;
      } else {
         String var3 = String.valueOf(var2);
         StringBuilder var4 = new StringBuilder(var3.length()).append(var3);

         try {
            this.substitute(var1, var4, 0, var4.length());
         } catch (Throwable var6) {
            return handleFailedReplacement(var3, var6);
         }

         return var4.toString();
      }
   }

   public boolean replaceIn(StringBuffer var1) {
      return var1 == null ? false : this.replaceIn(var1, 0, var1.length());
   }

   public boolean replaceIn(StringBuffer var1, int var2, int var3) {
      return this.replaceIn(null, var1, var2, var3);
   }

   public boolean replaceIn(LogEvent var1, StringBuffer var2, int var3, int var4) {
      if (var2 == null) {
         return false;
      } else {
         StringBuilder var5 = new StringBuilder(var4).append(var2, var3, var4);

         try {
            if (!this.substitute(var1, var5, 0, var4)) {
               return false;
            }
         } catch (Throwable var7) {
            StatusLogger.getLogger().error("Replacement failed on {}", var2, var7);
            return false;
         }

         var2.replace(var3, var3 + var4, var5.toString());
         return true;
      }
   }

   public boolean replaceIn(StringBuilder var1) {
      return this.replaceIn(null, var1);
   }

   public boolean replaceIn(LogEvent var1, StringBuilder var2) {
      return var2 == null ? false : this.substitute(var1, var2, 0, var2.length());
   }

   public boolean replaceIn(StringBuilder var1, int var2, int var3) {
      return this.replaceIn(null, var1, var2, var3);
   }

   public boolean replaceIn(LogEvent var1, StringBuilder var2, int var3, int var4) {
      return var2 == null ? false : this.substitute(var1, var2, var3, var4);
   }

   protected boolean substitute(LogEvent var1, StringBuilder var2, int var3, int var4) {
      return this.substitute(var1, var2, var3, var4, null) > 0;
   }

   private int substitute(LogEvent var1, StringBuilder var2, int var3, int var4, List<String> var5) {
      StrMatcher var6 = this.getVariablePrefixMatcher();
      StrMatcher var7 = this.getVariableSuffixMatcher();
      char var8 = this.getEscapeChar();
      StrMatcher var9 = this.getValueDelimiterMatcher();
      boolean var10 = this.isEnableSubstitutionInVariables();
      boolean var11 = var5 == null;
      boolean var12 = false;
      int var13 = 0;
      char[] var14 = this.getChars(var2);
      int var15 = var3 + var4;
      int var16 = var3;

      while (var16 < var15) {
         int var17 = var6.isMatch(var14, var16, var3, var15);
         if (var17 == 0) {
            var16++;
         } else if (var16 > var3 && var14[var16 - 1] == var8) {
            var2.deleteCharAt(var16 - 1);
            var14 = this.getChars(var2);
            var13--;
            var12 = true;
            var15--;
         } else {
            int var18 = var16;
            var16 += var17;
            int var19 = 0;
            int var20 = 0;

            while (var16 < var15) {
               if (var10 && (var19 = var6.isMatch(var14, var16, var3, var15)) != 0) {
                  var20++;
                  var16 += var19;
               } else {
                  var19 = var7.isMatch(var14, var16, var3, var15);
                  if (var19 == 0) {
                     var16++;
                  } else {
                     if (var20 == 0) {
                        String var21 = new String(var14, var18 + var17, var16 - var18 - var17);
                        if (var10) {
                           if (var5 == null) {
                              var5 = new ArrayList();
                           }

                           StringBuilder var22 = new StringBuilder(var21);
                           this.substitute(var1, var22, 0, var22.length(), (List<String>)var5);
                           var21 = var22.toString();
                        }

                        var16 += var19;
                        String var23 = var21;
                        String var24 = null;
                        if (var9 != null) {
                           char[] var25 = var21.toCharArray();
                           int var26 = 0;

                           label116:
                           for (int var27 = 0; var27 < var25.length && (var10 || var6.isMatch(var25, var27, var27, var25.length) == 0); var27++) {
                              if (this.valueEscapeDelimiterMatcher != null) {
                                 int var28 = this.valueEscapeDelimiterMatcher.isMatch(var25, var27);
                                 if (var28 != 0) {
                                    String var29 = var21.substring(0, var27) + ':';
                                    var23 = var29 + var21.substring(var27 + var28 - 1);

                                    for (int var30 = var27 + var28; var30 < var25.length; var30++) {
                                       if ((var26 = var9.isMatch(var25, var30)) != 0) {
                                          var23 = var29 + var21.substring(var27 + var28, var30);
                                          var24 = var21.substring(var30 + var26);
                                          break label116;
                                       }
                                    }
                                    break;
                                 }

                                 if ((var26 = var9.isMatch(var25, var27)) != 0) {
                                    var23 = var21.substring(0, var27);
                                    var24 = var21.substring(var27 + var26);
                                    break;
                                 }
                              } else if ((var26 = var9.isMatch(var25, var27)) != 0) {
                                 var23 = var21.substring(0, var27);
                                 var24 = var21.substring(var27 + var26);
                                 break;
                              }
                           }
                        }

                        if (var5 == null) {
                           var5 = new ArrayList();
                           var5.add(new String(var14, var3, var4 + var13));
                        }

                        boolean var33 = this.isCyclicSubstitution(var23, (List<String>)var5);
                        String var37 = var33 ? null : this.resolveVariable(var1, var23, var2, var18, var16);
                        if (var37 == null) {
                           var37 = var24;
                        }

                        if (var37 != null) {
                           int var38 = var37.length();
                           var2.replace(var18, var16, var37);
                           var12 = true;
                           int var39 = this.isRecursiveEvaluationAllowed() ? this.substitute(var1, var2, var18, var38, (List<String>)var5) : 0;
                           var39 += var38 - (var16 - var18);
                           var16 += var39;
                           var15 += var39;
                           var13 += var39;
                           var14 = this.getChars(var2);
                        }

                        if (!var33) {
                           var5.remove(var5.size() - 1);
                        }
                        break;
                     }

                     var20--;
                     var16 += var19;
                  }
               }
            }
         }
      }

      if (var11) {
         return var12 ? 1 : 0;
      } else {
         return var13;
      }
   }

   private boolean isCyclicSubstitution(String var1, List<String> var2) {
      if (!var2.contains(var1)) {
         var2.add(var1);
         return false;
      } else {
         StringBuilder var3 = new StringBuilder(256);
         var3.append("Infinite loop in property interpolation of ");
         this.appendWithSeparators(var3, var2, "->");
         StatusLogger.getLogger().warn(var3);
         return true;
      }
   }

   protected String resolveVariable(LogEvent var1, String var2, StringBuilder var3, int var4, int var5) {
      StrLookup var6 = this.getVariableResolver();
      if (var6 == null) {
         return null;
      } else {
         try {
            return var6.lookup(var1, var2);
         } catch (Throwable var8) {
            StatusLogger.getLogger().error("Resolver failed to lookup {}", var2, var8);
            return null;
         }
      }
   }

   public char getEscapeChar() {
      return this.escapeChar;
   }

   public void setEscapeChar(char var1) {
      this.escapeChar = var1;
   }

   public StrMatcher getVariablePrefixMatcher() {
      return this.prefixMatcher;
   }

   public StrSubstitutor setVariablePrefixMatcher(StrMatcher var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Variable prefix matcher must not be null!");
      } else {
         this.prefixMatcher = var1;
         return this;
      }
   }

   public StrSubstitutor setVariablePrefix(char var1) {
      return this.setVariablePrefixMatcher(StrMatcher.charMatcher(var1));
   }

   public StrSubstitutor setVariablePrefix(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Variable prefix must not be null!");
      } else {
         return this.setVariablePrefixMatcher(StrMatcher.stringMatcher(var1));
      }
   }

   public StrMatcher getVariableSuffixMatcher() {
      return this.suffixMatcher;
   }

   public StrSubstitutor setVariableSuffixMatcher(StrMatcher var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Variable suffix matcher must not be null!");
      } else {
         this.suffixMatcher = var1;
         return this;
      }
   }

   public StrSubstitutor setVariableSuffix(char var1) {
      return this.setVariableSuffixMatcher(StrMatcher.charMatcher(var1));
   }

   public StrSubstitutor setVariableSuffix(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Variable suffix must not be null!");
      } else {
         return this.setVariableSuffixMatcher(StrMatcher.stringMatcher(var1));
      }
   }

   public StrMatcher getValueDelimiterMatcher() {
      return this.valueDelimiterMatcher;
   }

   public StrSubstitutor setValueDelimiterMatcher(StrMatcher var1) {
      this.valueDelimiterMatcher = var1;
      return this;
   }

   public StrSubstitutor setValueDelimiter(char var1) {
      return this.setValueDelimiterMatcher(StrMatcher.charMatcher(var1));
   }

   public StrSubstitutor setValueDelimiter(String var1) {
      if (Strings.isEmpty(var1)) {
         this.setValueDelimiterMatcher(null);
         return this;
      } else {
         String var2 = var1.substring(0, var1.length() - 1) + "\\" + var1.substring(var1.length() - 1);
         this.valueEscapeDelimiterMatcher = StrMatcher.stringMatcher(var2);
         return this.setValueDelimiterMatcher(StrMatcher.stringMatcher(var1));
      }
   }

   public StrLookup getVariableResolver() {
      return this.variableResolver;
   }

   public void setVariableResolver(StrLookup var1) {
      if (var1 instanceof ConfigurationAware && this.configuration != null) {
         ((ConfigurationAware)var1).setConfiguration(this.configuration);
      }

      this.variableResolver = var1;
   }

   public boolean isEnableSubstitutionInVariables() {
      return this.enableSubstitutionInVariables;
   }

   public void setEnableSubstitutionInVariables(boolean var1) {
      this.enableSubstitutionInVariables = var1;
   }

   boolean isRecursiveEvaluationAllowed() {
      return this.recursiveEvaluationAllowed;
   }

   void setRecursiveEvaluationAllowed(boolean var1) {
      this.recursiveEvaluationAllowed = var1;
   }

   private char[] getChars(StringBuilder var1) {
      char[] var2 = new char[var1.length()];
      var1.getChars(0, var1.length(), var2, 0);
      return var2;
   }

   public void appendWithSeparators(StringBuilder var1, Iterable<?> var2, String var3) {
      if (var2 != null) {
         var3 = var3 == null ? "" : var3;
         Iterator var4 = var2.iterator();

         while (var4.hasNext()) {
            var1.append(var4.next());
            if (var4.hasNext()) {
               var1.append(var3);
            }
         }
      }
   }

   @Override
   public String toString() {
      return "StrSubstitutor(" + this.variableResolver.toString() + ')';
   }

   @Override
   public void setConfiguration(Configuration var1) {
      this.configuration = var1;
      if (this.variableResolver instanceof ConfigurationAware) {
         ((ConfigurationAware)this.variableResolver).setConfiguration(this.configuration);
      }
   }
}
