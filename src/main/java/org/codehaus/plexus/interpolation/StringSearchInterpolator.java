package org.codehaus.plexus.interpolation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringSearchInterpolator implements Interpolator {
   private Map<String, Object> existingAnswers = new HashMap<>();
   private List<ValueSource> valueSources = new ArrayList<>();
   private List<InterpolationPostProcessor> postProcessors = new ArrayList<>();
   private boolean cacheAnswers = false;
   public static final String DEFAULT_START_EXPR = "${";
   public static final String DEFAULT_END_EXPR = "}";
   private String startExpr;
   private String endExpr;
   private String escapeString;

   public StringSearchInterpolator() {
      this.startExpr = "${";
      this.endExpr = "}";
   }

   public StringSearchInterpolator(String var1, String var2) {
      this.startExpr = var1;
      this.endExpr = var2;
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
         int var5 = -1;
         int var4;
         if ((var4 = var1.indexOf(this.startExpr, var5 + 1)) <= -1) {
            return var1;
         } else {
            StringBuilder var6 = new StringBuilder(var1.length() * 2);

            do {
               var6.append(var1, var5 + 1, var4);
               var5 = var1.indexOf(this.endExpr, var4 + 1);
               if (var5 < 0) {
                  break;
               }

               String var7 = var1.substring(var4, var5 + this.endExpr.length());
               String var8 = var7.substring(this.startExpr.length(), var7.length() - this.endExpr.length());
               if (var4 >= 0 && this.escapeString != null && this.escapeString.length() > 0) {
                  int var9 = var4 == 0 ? 0 : var4 - this.escapeString.length();
                  if (var9 >= 0) {
                     String var10 = var1.substring(var9, var4);
                     if (this.escapeString.equals(var10)) {
                        var6.append(var7);
                        var6.replace(var9, var9 + this.escapeString.length(), "");
                        continue;
                     }
                  }
               }

               boolean var18 = false;
               if (!var3.contains(var7)) {
                  if (var8.startsWith(".")) {
                     var8 = var8.substring(1);
                  }

                  if (var2.hasRecursiveExpression(var8)) {
                     throw new InterpolationCycleException(var2, var8, var7);
                  }

                  var2.expressionResolutionStarted(var8);

                  try {
                     Object var19 = this.existingAnswers.get(var8);
                     Object var11 = null;

                     for (ValueSource var13 : this.valueSources) {
                        if (var19 != null) {
                           break;
                        }

                        var19 = var13.getValue(var8);
                        if (var19 != null && var19.toString().contains(var7)) {
                           var11 = var19;
                           var19 = null;
                        }
                     }

                     if (var19 == null && var11 != null) {
                        throw new InterpolationCycleException(var2, var8, var7);
                     }

                     if (var19 == null) {
                        var3.add(var7);
                     } else {
                        var19 = this.interpolate(String.valueOf(var19), var2, var3);
                        if (this.postProcessors != null && !this.postProcessors.isEmpty()) {
                           for (InterpolationPostProcessor var22 : this.postProcessors) {
                              Object var14 = var22.execute(var8, var19);
                              if (var14 != null) {
                                 var19 = var14;
                                 break;
                              }
                           }
                        }

                        var6.append(String.valueOf(var19));
                        var18 = true;
                     }
                  } finally {
                     var2.expressionResolutionFinished(var8);
                  }
               }

               if (!var18) {
                  var6.append(var7);
               }

               if (var5 > -1) {
                  var5 += this.endExpr.length() - 1;
               }
            } while ((var4 = var1.indexOf(this.startExpr, var5 + 1)) > -1);

            if (var5 == -1 && var4 > -1) {
               var6.append(var1, var4, var1.length());
            } else if (var5 < var1.length()) {
               var6.append(var1, var5 + 1, var1.length());
            }

            return var6.toString();
         }
      }
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
}
