package org.codehaus.plexus.interpolation.object;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import org.codehaus.plexus.interpolation.BasicInterpolator;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;

public class FieldBasedObjectInterpolator implements ObjectInterpolator {
   public static final Set<String> DEFAULT_BLACKLISTED_FIELD_NAMES;
   public static final Set<String> DEFAULT_BLACKLISTED_PACKAGE_PREFIXES;
   private static final Map<Class, Field[]> fieldsByClass = new WeakHashMap<>();
   private static final Map<Class, Boolean> fieldIsPrimitiveByClass = new WeakHashMap<>();
   private Set<String> blacklistedFieldNames;
   private Set<String> blacklistedPackagePrefixes;
   private List<ObjectInterpolationWarning> warnings = new ArrayList<>();

   public static void clearCaches() {
      fieldsByClass.clear();
      fieldIsPrimitiveByClass.clear();
   }

   public FieldBasedObjectInterpolator() {
      this.blacklistedFieldNames = DEFAULT_BLACKLISTED_FIELD_NAMES;
      this.blacklistedPackagePrefixes = DEFAULT_BLACKLISTED_PACKAGE_PREFIXES;
   }

   public FieldBasedObjectInterpolator(Set<String> var1, Set<String> var2) {
      this.blacklistedFieldNames = var1;
      this.blacklistedPackagePrefixes = var2;
   }

   @Override
   public boolean hasWarnings() {
      return this.warnings != null && !this.warnings.isEmpty();
   }

   @Override
   public List<ObjectInterpolationWarning> getWarnings() {
      return new ArrayList<>(this.warnings);
   }

   @Override
   public void interpolate(Object var1, BasicInterpolator var2) throws InterpolationException {
      this.interpolate(var1, var2, new SimpleRecursionInterceptor());
   }

   @Override
   public void interpolate(Object var1, BasicInterpolator var2, RecursionInterceptor var3) throws InterpolationException {
      this.warnings.clear();
      FieldBasedObjectInterpolator.InterpolateObjectAction var4 = new FieldBasedObjectInterpolator.InterpolateObjectAction(
         var1, var2, var3, this.blacklistedFieldNames, this.blacklistedPackagePrefixes, this.warnings
      );
      InterpolationException var5 = AccessController.doPrivileged(var4);
      if (var5 != null) {
         throw var5;
      }
   }

   static {
      HashSet var0 = new HashSet();
      var0.add("parent");
      DEFAULT_BLACKLISTED_FIELD_NAMES = Collections.unmodifiableSet(var0);
      HashSet var1 = new HashSet();
      var1.add("java");
      DEFAULT_BLACKLISTED_PACKAGE_PREFIXES = Collections.unmodifiableSet(var1);
   }

   private static final class InterpolateObjectAction implements PrivilegedAction {
      private final LinkedList<FieldBasedObjectInterpolator.InterpolationTarget> interpolationTargets;
      private final BasicInterpolator interpolator;
      private final Set blacklistedFieldNames;
      private final String[] blacklistedPackagePrefixes;
      private final List<ObjectInterpolationWarning> warningCollector;
      private final RecursionInterceptor recursionInterceptor;

      public InterpolateObjectAction(Object var1, BasicInterpolator var2, RecursionInterceptor var3, Set var4, Set var5, List<ObjectInterpolationWarning> var6) {
         this.recursionInterceptor = var3;
         this.blacklistedFieldNames = var4;
         this.warningCollector = var6;
         this.blacklistedPackagePrefixes = var5.toArray(new String[var5.size()]);
         this.interpolationTargets = new LinkedList<>();
         this.interpolationTargets.add(new FieldBasedObjectInterpolator.InterpolationTarget(var1, ""));
         this.interpolator = var2;
      }

      @Override
      public Object run() {
         while (!this.interpolationTargets.isEmpty()) {
            FieldBasedObjectInterpolator.InterpolationTarget var1 = this.interpolationTargets.removeFirst();

            try {
               this.traverseObjectWithParents(var1.value.getClass(), var1);
            } catch (InterpolationException var3) {
               return var3;
            }
         }

         return null;
      }

      private void traverseObjectWithParents(Class var1, FieldBasedObjectInterpolator.InterpolationTarget var2) throws InterpolationException {
         Object var3 = var2.value;
         String var4 = var2.path;
         if (var1 != null) {
            if (var1.isArray()) {
               this.evaluateArray(var3, var4);
            } else if (this.isQualifiedForInterpolation(var1)) {
               Field[] var5 = FieldBasedObjectInterpolator.fieldsByClass.get(var1);
               if (var5 == null) {
                  var5 = var1.getDeclaredFields();
                  FieldBasedObjectInterpolator.fieldsByClass.put(var1, var5);
               }

               for (Field var9 : var5) {
                  Class var10 = var9.getType();
                  if (this.isQualifiedForInterpolation(var9, var10)) {
                     boolean var11 = var9.isAccessible();
                     synchronized (var1) {
                        var9.setAccessible(true);

                        try {
                           if (String.class == var10) {
                              this.interpolateString(var3, var9);
                           } else if (!Collection.class.isAssignableFrom(var10)) {
                              if (Map.class.isAssignableFrom(var10)) {
                                 this.interpolateMap(var3, var4, var9);
                              } else {
                                 this.interpolateObject(var3, var4, var9);
                              }
                           } else if (!this.interpolateCollection(var3, var4, var9)) {
                           }
                        } catch (IllegalArgumentException var20) {
                           this.warningCollector
                              .add(new ObjectInterpolationWarning("Failed to interpolate field. Skipping.", var4 + "." + var9.getName(), var20));
                        } catch (IllegalAccessException var21) {
                           this.warningCollector
                              .add(new ObjectInterpolationWarning("Failed to interpolate field. Skipping.", var4 + "." + var9.getName(), var21));
                        } finally {
                           var9.setAccessible(var11);
                        }
                     }
                  }
               }

               this.traverseObjectWithParents(var1.getSuperclass(), var2);
            }
         }
      }

      private void interpolateObject(Object var1, String var2, Field var3) throws IllegalAccessException, InterpolationException {
         Object var4 = var3.get(var1);
         if (var4 != null) {
            if (var3.getType().isArray()) {
               this.evaluateArray(var4, var2 + "." + var3.getName());
            } else {
               this.interpolationTargets.add(new FieldBasedObjectInterpolator.InterpolationTarget(var4, var2 + "." + var3.getName()));
            }
         }
      }

      private void interpolateMap(Object var1, String var2, Field var3) throws IllegalAccessException, InterpolationException {
         Map var4 = (Map)var3.get(var1);
         if (var4 != null && !var4.isEmpty()) {
            for (Object var6 : var4.entrySet()) {
               Entry var7 = (Entry)var6;
               Object var8 = var7.getValue();
               if (var8 != null) {
                  if (String.class == var8.getClass()) {
                     String var9 = this.interpolator.interpolate((String)var8, this.recursionInterceptor);
                     if (!var9.equals(var8)) {
                        try {
                           var7.setValue(var9);
                        } catch (UnsupportedOperationException var11) {
                           this.warningCollector
                              .add(
                                 new ObjectInterpolationWarning(
                                    "Field is an unmodifiable collection. Skipping interpolation.", var2 + "." + var3.getName(), var11
                                 )
                              );
                        }
                     }
                  } else if (var8.getClass().isArray()) {
                     this.evaluateArray(var8, var2 + "." + var3.getName());
                  } else {
                     this.interpolationTargets.add(new FieldBasedObjectInterpolator.InterpolationTarget(var8, var2 + "." + var3.getName()));
                  }
               }
            }
         }
      }

      private boolean interpolateCollection(Object var1, String var2, Field var3) throws IllegalAccessException, InterpolationException {
         Collection var4 = (Collection)var3.get(var1);
         if (var4 != null && !var4.isEmpty()) {
            ArrayList var5 = new ArrayList(var4);

            try {
               var4.clear();
            } catch (UnsupportedOperationException var9) {
               this.warningCollector
                  .add(new ObjectInterpolationWarning("Field is an unmodifiable collection. Skipping interpolation.", var2 + "." + var3.getName(), var9));
               return true;
            }

            for (Object var7 : var5) {
               if (var7 != null) {
                  if (String.class == var7.getClass()) {
                     String var8 = this.interpolator.interpolate((String)var7, this.recursionInterceptor);
                     if (!var8.equals(var7)) {
                        var4.add(var8);
                     } else {
                        var4.add(var7);
                     }
                  } else {
                     var4.add(var7);
                     if (var7.getClass().isArray()) {
                        this.evaluateArray(var7, var2 + "." + var3.getName());
                     } else {
                        this.interpolationTargets.add(new FieldBasedObjectInterpolator.InterpolationTarget(var7, var2 + "." + var3.getName()));
                     }
                  }
               } else {
                  var4.add(var7);
               }
            }
         }

         return false;
      }

      private void interpolateString(Object var1, Field var2) throws IllegalAccessException, InterpolationException {
         String var3 = (String)var2.get(var1);
         if (var3 != null) {
            String var4 = this.interpolator.interpolate(var3, this.recursionInterceptor);
            if (!var4.equals(var3)) {
               var2.set(var1, var4);
            }
         }
      }

      private boolean isQualifiedForInterpolation(Class var1) {
         String var2 = var1.getPackage().getName();

         for (String var6 : this.blacklistedPackagePrefixes) {
            if (var2.startsWith(var6)) {
               return false;
            }
         }

         return true;
      }

      private boolean isQualifiedForInterpolation(Field var1, Class var2) {
         if (!FieldBasedObjectInterpolator.fieldIsPrimitiveByClass.containsKey(var2)) {
            FieldBasedObjectInterpolator.fieldIsPrimitiveByClass.put(var2, var2.isPrimitive());
         }

         return FieldBasedObjectInterpolator.fieldIsPrimitiveByClass.get(var2) ? false : !this.blacklistedFieldNames.contains(var1.getName());
      }

      private void evaluateArray(Object var1, String var2) throws InterpolationException {
         int var3 = Array.getLength(var1);

         for (int var4 = 0; var4 < var3; var4++) {
            Object var5 = Array.get(var1, var4);
            if (var5 != null) {
               if (String.class == var5.getClass()) {
                  String var6 = this.interpolator.interpolate((String)var5, this.recursionInterceptor);
                  if (!var6.equals(var5)) {
                     Array.set(var1, var4, var6);
                  }
               } else {
                  this.interpolationTargets.add(new FieldBasedObjectInterpolator.InterpolationTarget(var5, var2 + "[" + var4 + "]"));
               }
            }
         }
      }
   }

   private static final class InterpolationTarget {
      private Object value;
      private String path;

      private InterpolationTarget(Object var1, String var2) {
         this.value = var1;
         this.path = var2;
      }
   }
}
