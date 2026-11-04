package org.codehaus.plexus.interpolation.fixed;

import java.util.ArrayList;
import org.codehaus.plexus.interpolation.BasicInterpolator;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.RecursionInterceptor;

public class FixedStringSearchInterpolator implements FixedValueSource {
   private final FixedValueSource[] valueSources;
   private final InterpolationPostProcessor postProcessor;
   public static final String DEFAULT_START_EXPR = "${";
   public static final String DEFAULT_END_EXPR = "}";
   private final String startExpr;
   private final String endExpr;
   private final String escapeString;

   private FixedStringSearchInterpolator(String var1, String var2, String var3, InterpolationPostProcessor var4, FixedValueSource... var5) {
      this.startExpr = var1;
      this.endExpr = var2;
      this.escapeString = var3;
      if (var5 == null) {
         throw new IllegalArgumentException("valueSources cannot be null");
      } else {
         for (int var6 = 0; var6 < var5.length; var6++) {
            if (var5[var6] == null) {
               throw new IllegalArgumentException("valueSources[" + var6 + "] is null");
            }
         }

         this.valueSources = var5;
         this.postProcessor = var4;
      }
   }

   public static FixedStringSearchInterpolator create(String var0, String var1, FixedValueSource... var2) {
      return new FixedStringSearchInterpolator(var0, var1, null, null, var2);
   }

   public static FixedStringSearchInterpolator create(FixedValueSource... var0) {
      return new FixedStringSearchInterpolator("${", "}", null, null, var0);
   }

   public static FixedStringSearchInterpolator createWithPermittedNulls(FixedValueSource... var0) {
      ArrayList var1 = new ArrayList();

      for (FixedValueSource var5 : var0) {
         if (var5 != null) {
            var1.add(var5);
         }
      }

      return new FixedStringSearchInterpolator("${", "}", null, null, var1.toArray(new FixedValueSource[var1.size()]));
   }

   public FixedStringSearchInterpolator withExpressionMarkers(String var1, String var2) {
      return new FixedStringSearchInterpolator(var1, var2, this.escapeString, this.postProcessor, this.valueSources);
   }

   public FixedStringSearchInterpolator withPostProcessor(InterpolationPostProcessor var1) {
      return new FixedStringSearchInterpolator(this.startExpr, this.endExpr, this.escapeString, var1, this.valueSources);
   }

   public FixedStringSearchInterpolator withEscapeString(String var1) {
      return new FixedStringSearchInterpolator(this.startExpr, this.endExpr, var1, this.postProcessor, this.valueSources);
   }

   public String interpolate(String var1) throws InterpolationCycleException {
      return this.interpolate(var1, new InterpolationState());
   }

   public static FixedStringSearchInterpolator empty() {
      return create();
   }

   @Override
   public Object getValue(String var1, InterpolationState var2) {
      var2.recursionInterceptor.expressionResolutionStarted(var1);

      String var11;
      try {
         Object var3 = null;

         for (FixedValueSource var7 : this.valueSources) {
            var3 = var7.getValue(var1, var2);
            if (var3 != null) {
               break;
            }
         }

         if (var3 == null) {
            return null;
         }

         if (var2.root != null) {
            var3 = var2.root.interpolate(String.valueOf(var3), var2);
         }

         var11 = String.valueOf(var3);
      } finally {
         var2.recursionInterceptor.expressionResolutionFinished(var1);
      }

      return var11;
   }

   public BasicInterpolator asBasicInterpolator() {
      final InterpolationState var1 = new InterpolationState();
      return new BasicInterpolator() {
         @Override
         public String interpolate(String var1x) throws InterpolationException {
            return FixedStringSearchInterpolator.this.interpolate(var1x, var1);
         }

         @Override
         public String interpolate(String var1x, RecursionInterceptor var2) throws InterpolationException {
            var1.setRecursionInterceptor(var2);
            return FixedStringSearchInterpolator.this.interpolate(var1x, var1);
         }
      };
   }

   public String interpolate(String var1, InterpolationState var2) throws InterpolationCycleException {
      if (var2.root == null) {
         var2.root = this;
      }

      if (var1 == null) {
         return "";
      } else {
         StringBuilder var3 = new StringBuilder(var1.length() * 2);
         int var5 = -1;

         int var4;
         while ((var4 = var1.indexOf(this.startExpr, var5 + 1)) > -1) {
            var3.append(var1, var5 + 1, var4);
            var5 = var1.indexOf(this.endExpr, var4 + 1);
            if (var5 < 0) {
               break;
            }

            String var6 = var1.substring(var4, var5 + this.endExpr.length());
            String var7 = var6.substring(this.startExpr.length(), var6.length() - this.endExpr.length());
            if (var4 >= 0 && this.escapeString != null && this.escapeString.length() > 0) {
               int var8 = var4 == 0 ? 0 : var4 - this.escapeString.length();
               if (var8 >= 0) {
                  String var9 = var1.substring(var8, var4);
                  if (this.escapeString.equals(var9)) {
                     var3.append(var6);
                     var3.replace(var8, var8 + this.escapeString.length(), "");
                     continue;
                  }
               }
            }

            boolean var11 = false;
            if (!var2.unresolvable.contains(var6)) {
               if (var7.startsWith(".")) {
                  var7 = var7.substring(1);
               }

               if (var2.recursionInterceptor.hasRecursiveExpression(var7)) {
                  throw new InterpolationCycleException(var2.recursionInterceptor, var7, var6);
               }

               Object var12 = this.getValue(var7, var2);
               if (var12 != null) {
                  var12 = this.interpolate(String.valueOf(var12), var2);
                  if (this.postProcessor != null) {
                     Object var10 = this.postProcessor.execute(var7, var12);
                     if (var10 != null) {
                        var12 = var10;
                     }
                  }

                  var3.append(String.valueOf(var12));
                  var11 = true;
               } else {
                  var2.unresolvable.add(var6);
               }
            }

            if (!var11) {
               var3.append(var6);
            }

            if (var5 > -1) {
               var5 += this.endExpr.length() - 1;
            }
         }

         if (var5 == -1 && var4 > -1) {
            var3.append(var1, var4, var1.length());
         } else if (var5 < var1.length()) {
            var3.append(var1, var5 + 1, var1.length());
         }

         return var3.toString();
      }
   }
}
