package org.codehaus.plexus.interpolation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.plexus.interpolation.util.StringUtils;

public class RegexBasedInterpolator implements Interpolator {
   private String startRegex;
   private String endRegex;
   private Map existingAnswers = new HashMap();
   private List<ValueSource> valueSources = new ArrayList<>();
   private List<InterpolationPostProcessor> postProcessors = new ArrayList<>();
   private boolean reusePatterns = false;
   private boolean cacheAnswers = false;
   public static final String DEFAULT_REGEXP = "\\$\\{(.+?)\\}";
   private Map<String, Pattern> compiledPatterns = new WeakHashMap<>();

   public RegexBasedInterpolator() {
      this.compiledPatterns.put("\\$\\{(.+?)\\}", Pattern.compile("\\$\\{(.+?)\\}"));
   }

   public RegexBasedInterpolator(boolean var1) {
      this();
      this.reusePatterns = var1;
   }

   public RegexBasedInterpolator(String var1, String var2) {
      this();
      this.startRegex = var1;
      this.endRegex = var2;
   }

   public RegexBasedInterpolator(List var1) {
      this();
      this.valueSources.addAll(var1);
   }

   public RegexBasedInterpolator(String var1, String var2, List var3) {
      this();
      this.startRegex = var1;
      this.endRegex = var2;
      this.valueSources.addAll(var3);
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
   public String interpolate(String var1, String var2, RecursionInterceptor var3) throws InterpolationException {
      if (var1 == null) {
         return "";
      } else {
         if (var3 == null) {
            var3 = new SimpleRecursionInterceptor();
         }

         if (var2 != null && var2.length() == 0) {
            var2 = null;
         }

         byte var4 = 2;
         Pattern var5;
         if (this.startRegex == null && this.endRegex == null) {
            if (var2 != null) {
               var5 = this.getPattern("\\$\\{(" + var2 + ")?(.+?)\\}");
            } else {
               var5 = this.getPattern("\\$\\{(.+?)\\}");
               var4 = 1;
            }
         } else if (var2 == null) {
            var5 = this.getPattern(this.startRegex + this.endRegex);
            var4 = 1;
         } else {
            var5 = this.getPattern(this.startRegex + var2 + this.endRegex);
         }

         String var6;
         try {
            var6 = this.interpolate(var1, (RecursionInterceptor)var3, var5, var4);
         } finally {
            if (!this.cacheAnswers) {
               this.clearAnswers();
            }
         }

         return var6;
      }
   }

   private Pattern getPattern(String var1) {
      if (!this.reusePatterns) {
         return Pattern.compile(var1);
      } else {
         synchronized (this) {
            Pattern var2 = this.compiledPatterns.get(var1);
            if (var2 != null) {
               return var2;
            } else {
               var2 = Pattern.compile(var1);
               this.compiledPatterns.put(var1, var2);
               return var2;
            }
         }
      }
   }

   private String interpolate(String var1, RecursionInterceptor var2, Pattern var3, int var4) throws InterpolationException {
      if (var1 == null) {
         return "";
      } else {
         String var5 = var1;
         Matcher var6 = var3.matcher(var1);

         while (var6.find()) {
            String var7 = var6.group(0);
            String var8 = var6.group(var4);
            if (var8.startsWith(".")) {
               var8 = var8.substring(1);
            }

            if (var2.hasRecursiveExpression(var8)) {
               throw new InterpolationCycleException(var2, var8, var7);
            }

            var2.expressionResolutionStarted(var8);

            try {
               Object var9 = this.existingAnswers.get(var8);

               for (ValueSource var11 : this.valueSources) {
                  if (var9 != null) {
                     break;
                  }

                  var9 = var11.getValue(var8);
               }

               if (var9 != null) {
                  var9 = this.interpolate(String.valueOf(var9), var2, var3, var4);
                  if (this.postProcessors != null && !this.postProcessors.isEmpty()) {
                     for (InterpolationPostProcessor var18 : this.postProcessors) {
                        Object var12 = var18.execute(var8, var9);
                        if (var12 != null) {
                           var9 = var12;
                           break;
                        }
                     }
                  }

                  var5 = StringUtils.replace(var5, var7, String.valueOf(var9));
                  var6.reset(var5);
               }
            } finally {
               var2.expressionResolutionFinished(var8);
            }
         }

         return var5;
      }
   }

   @Override
   public List getFeedback() {
      ArrayList var1 = new ArrayList();

      for (Object var3 : this.valueSources) {
         ValueSource var4 = (ValueSource)var3;
         List var5 = var4.getFeedback();
         if (var5 != null && !var5.isEmpty()) {
            var1.addAll(var5);
         }
      }

      return var1;
   }

   @Override
   public void clearFeedback() {
      for (Object var2 : this.valueSources) {
         ValueSource var3 = (ValueSource)var2;
         var3.clearFeedback();
      }
   }

   @Override
   public String interpolate(String var1, String var2) throws InterpolationException {
      return this.interpolate(var1, var2, null);
   }

   @Override
   public String interpolate(String var1) throws InterpolationException {
      return this.interpolate(var1, null, null);
   }

   @Override
   public String interpolate(String var1, RecursionInterceptor var2) throws InterpolationException {
      return this.interpolate(var1, null, var2);
   }

   public boolean isReusePatterns() {
      return this.reusePatterns;
   }

   public void setReusePatterns(boolean var1) {
      this.reusePatterns = var1;
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
}
