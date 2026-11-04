package org.codehaus.plexus.interpolation.multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.plexus.interpolation.InterpolationCycleException;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;
import org.codehaus.plexus.interpolation.ValueSource;

public class MultiDelimiterStringSearchInterpolator implements Interpolator {
   private static final int MAX_TRIES = 10;
   private Map existingAnswers = new HashMap();
   private List<ValueSource> valueSources = new ArrayList<>();
   private List postProcessors = new ArrayList();
   private boolean cacheAnswers = false;
   private LinkedHashSet<DelimiterSpecification> delimiters = new LinkedHashSet<>();
   private String escapeString;

   public MultiDelimiterStringSearchInterpolator() {
      this.delimiters.add(DelimiterSpecification.DEFAULT_SPEC);
   }

   public MultiDelimiterStringSearchInterpolator addDelimiterSpec(String var1) {
      if (var1 == null) {
         return this;
      } else {
         this.delimiters.add(DelimiterSpecification.parse(var1));
         return this;
      }
   }

   public boolean removeDelimiterSpec(String var1) {
      return var1 == null ? false : this.delimiters.remove(DelimiterSpecification.parse(var1));
   }

   public MultiDelimiterStringSearchInterpolator withValueSource(ValueSource var1) {
      this.addValueSource(var1);
      return this;
   }

   public MultiDelimiterStringSearchInterpolator withPostProcessor(InterpolationPostProcessor var1) {
      this.addPostProcessor(var1);
      return this;
   }

   @Override
   public void addValueSource(ValueSource var1) {
      this.valueSources.add(var1);
   }

   @Override
   public void removeValuesSource(ValueSource var1) {
      this.valueSources.remove(var1);
   }

   @Override
   public void addPostProcessor(InterpolationPostProcessor var1) {
      this.postProcessors.add(var1);
   }

   @Override
   public void removePostProcessor(InterpolationPostProcessor var1) {
      this.postProcessors.remove(var1);
   }

   @Override
   public String interpolate(String var1, String var2) throws InterpolationException {
      return this.interpolate(var1, new SimpleRecursionInterceptor());
   }

   @Override
   public String interpolate(String var1, String var2, RecursionInterceptor var3) throws InterpolationException {
      return this.interpolate(var1, var3);
   }

   @Override
   public String interpolate(String var1) throws InterpolationException {
      return this.interpolate(var1, new SimpleRecursionInterceptor());
   }

   @Override
   public String interpolate(String var1, RecursionInterceptor var2) throws InterpolationException {
      String var3;
      try {
         var3 = this.interpolate(var1, var2, new HashSet<>());
      } finally {
         if (!this.cacheAnswers) {
            this.existingAnswers.clear();
         }
      }

      return var3;
   }

   private String interpolate(String var1, RecursionInterceptor var2, Set<String> var3) throws InterpolationException {
      if (var1 == null) {
         return "";
      } else {
         StringBuilder var4 = new StringBuilder(var1.length() * 2);
         String var5 = var1;
         int var6 = 0;

         do {
            var6++;
            if (var4.length() > 0) {
               var5 = var4.toString();
               var4.setLength(0);
            }

            int var7 = -1;
            int var8 = -1;
            DelimiterSpecification var9 = null;

            while ((var9 = this.select(var1, var8)) != null) {
               String var10 = var9.getBegin();
               String var11 = var9.getEnd();
               var7 = var9.getNextStartIndex();
               var4.append(var1, var8 + 1, var7);
               var8 = var1.indexOf(var11, var7 + 1);
               if (var8 < 0) {
                  break;
               }

               String var12 = var1.substring(var7, var8 + var11.length());
               String var13 = var12.substring(var10.length(), var12.length() - var11.length());
               if (var7 >= 0 && this.escapeString != null && this.escapeString.length() > 0) {
                  int var14 = var7 == 0 ? 0 : var7 - this.escapeString.length();
                  if (var14 >= 0) {
                     String var15 = var1.substring(var14, var7);
                     if (var15 != null && this.escapeString.equals(var15)) {
                        var4.append(var12);
                        if (var14 > 0) {
                           var14--;
                        }

                        var4.replace(var14, var14 + this.escapeString.length(), "");
                        continue;
                     }
                  }
               }

               boolean var22 = false;
               if (!var3.contains(var12)) {
                  if (var13.startsWith(".")) {
                     var13 = var13.substring(1);
                  }

                  if (var2.hasRecursiveExpression(var13)) {
                     throw new InterpolationCycleException(var2, var13, var12);
                  }

                  var2.expressionResolutionStarted(var13);
                  Object var23 = this.existingAnswers.get(var13);
                  Object var16 = null;

                  for (ValueSource var18 : this.valueSources) {
                     if (var23 != null) {
                        break;
                     }

                     var23 = var18.getValue(var13);
                     if (var23 != null && var23.toString().contains(var12)) {
                        var16 = var23;
                        var23 = null;
                     }
                  }

                  if (var23 == null && var16 != null) {
                     throw new InterpolationCycleException(var2, var13, var12);
                  }

                  if (var23 == null) {
                     var3.add(var12);
                  } else {
                     var23 = this.interpolate(String.valueOf(var23), var2, var3);
                     if (this.postProcessors != null && !this.postProcessors.isEmpty()) {
                        for (Object var26 : this.postProcessors) {
                           InterpolationPostProcessor var19 = (InterpolationPostProcessor)var26;
                           Object var20 = var19.execute(var13, var23);
                           if (var20 != null) {
                              var23 = var20;
                              break;
                           }
                        }
                     }

                     var4.append(String.valueOf(var23));
                     var22 = true;
                  }

                  var2.expressionResolutionFinished(var13);
               }

               if (!var22) {
                  var4.append(var12);
               }

               if (var8 > -1) {
                  var8 += var11.length() - 1;
               }
            }

            if (var8 == -1 && var7 > -1) {
               var4.append(var1, var7, var1.length());
            } else if (var8 < var1.length()) {
               var4.append(var1, var8 + 1, var1.length());
            }
         } while (!var5.equals(var4.toString()) && var6 < 10);

         return var4.toString();
      }
   }

   private DelimiterSpecification select(String var1, int var2) {
      DelimiterSpecification var3 = null;

      for (DelimiterSpecification var5 : this.delimiters) {
         var5.clearNextStart();
         if (var3 == null) {
            int var6 = var1.indexOf(var5.getBegin(), var2 + 1);
            if (var6 > -1) {
               var5.setNextStartIndex(var6);
               var3 = var5;
            }
         }
      }

      return var3;
   }

   @Override
   public List getFeedback() {
      ArrayList var1 = new ArrayList();

      for (ValueSource var3 : this.valueSources) {
         List var4 = var3.getFeedback();
         if (var4 != null && !var4.isEmpty()) {
            var1.addAll(var4);
         }
      }

      return var1;
   }

   @Override
   public void clearFeedback() {
      for (ValueSource var2 : this.valueSources) {
         var2.clearFeedback();
      }
   }

   @Override
   public boolean isCacheAnswers() {
      return this.cacheAnswers;
   }

   @Override
   public void setCacheAnswers(boolean var1) {
      this.cacheAnswers = var1;
   }

   @Override
   public void clearAnswers() {
      this.existingAnswers.clear();
   }

   public String getEscapeString() {
      return this.escapeString;
   }

   public void setEscapeString(String var1) {
      this.escapeString = var1;
   }

   public MultiDelimiterStringSearchInterpolator escapeString(String var1) {
      this.escapeString = var1;
      return this;
   }

   public MultiDelimiterStringSearchInterpolator setDelimiterSpecs(LinkedHashSet<String> var1) {
      this.delimiters.clear();

      for (String var3 : var1) {
         if (var3 != null) {
            this.delimiters.add(DelimiterSpecification.parse(var3));
         }
      }

      return this;
   }
}
