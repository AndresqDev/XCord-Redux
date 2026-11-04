package org.apache.maven.model.interpolation;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.building.ModelProblemCollectorRequest;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.interpolation.ValueSource;

@Deprecated
public class StringSearchModelInterpolator extends AbstractStringBasedModelInterpolator {
   private static final Map<Class<?>, StringSearchModelInterpolator.InterpolateObjectAction.CacheItem> CACHED_ENTRIES = new ConcurrentHashMap<>(80, 0.75F, 2);

   @Override
   public Model interpolateModel(Model var1, File var2, ModelBuildingRequest var3, ModelProblemCollector var4) {
      this.interpolateObject(var1, var1, var2, var3, var4);
      return var1;
   }

   void interpolateObject(Object var1, Model var2, File var3, ModelBuildingRequest var4, ModelProblemCollector var5) {
      List var6 = this.createValueSources(var2, var3, var4, var5);
      List var7 = this.createPostProcessors(var2, var3, var4);
      StringSearchModelInterpolator.InnerInterpolator var8 = this.createInterpolator(var6, var7, var5);
      StringSearchModelInterpolator.InterpolateObjectAction var9 = new StringSearchModelInterpolator.InterpolateObjectAction(var1, var8, var5);
      AccessController.doPrivileged(var9);
   }

   private StringSearchModelInterpolator.InnerInterpolator createInterpolator(
      List<? extends ValueSource> var1, List<? extends InterpolationPostProcessor> var2, final ModelProblemCollector var3
   ) {
      final HashMap var4 = new HashMap();
      final StringSearchInterpolator var5 = new StringSearchInterpolator();
      var5.setCacheAnswers(true);

      for (ValueSource var7 : var1) {
         var5.addValueSource(var7);
      }

      for (InterpolationPostProcessor var10 : var2) {
         var5.addPostProcessor(var10);
      }

      final RecursionInterceptor var9 = this.createRecursionInterceptor();
      return new StringSearchModelInterpolator.InnerInterpolator() {
         @Override
         public String interpolate(String var1) {
            if (var1 != null && var1.contains("${")) {
               String var2 = (String)var4.get(var1);
               if (var2 == null) {
                  try {
                     var2 = var5.interpolate(var1, var9);
                  } catch (InterpolationException var4x) {
                     var3.add(
                        new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                           .setMessage(var4x.getMessage())
                           .setException(var4x)
                     );
                  }

                  var4.put(var1, var2);
               }

               return var2;
            } else {
               return var1;
            }
         }
      };
   }

   private interface InnerInterpolator {
      String interpolate(String var1);
   }

   private static final class InterpolateObjectAction implements PrivilegedAction<Object> {
      private final LinkedList<Object> interpolationTargets = new LinkedList<>();
      private final StringSearchModelInterpolator.InnerInterpolator interpolator;
      private final ModelProblemCollector problems;

      InterpolateObjectAction(Object var1, StringSearchModelInterpolator.InnerInterpolator var2, ModelProblemCollector var3) {
         this.interpolationTargets.add(var1);
         this.interpolator = var2;
         this.problems = var3;
      }

      @Override
      public Object run() {
         while (!this.interpolationTargets.isEmpty()) {
            Object var1 = this.interpolationTargets.removeFirst();
            this.traverseObjectWithParents(var1.getClass(), var1);
         }

         return null;
      }

      private String interpolate(String var1) {
         return this.interpolator.interpolate(var1);
      }

      private void traverseObjectWithParents(Class<?> var1, Object var2) {
         if (var1 != null) {
            StringSearchModelInterpolator.InterpolateObjectAction.CacheItem var3 = this.getCacheEntry(var1);
            if (var3.isArray()) {
               evaluateArray(var2, this);
            } else if (var3.isQualifiedForInterpolation) {
               var3.interpolate(var2, this);
               this.traverseObjectWithParents(var1.getSuperclass(), var2);
            }
         }
      }

      private StringSearchModelInterpolator.InterpolateObjectAction.CacheItem getCacheEntry(Class<?> var1) {
         StringSearchModelInterpolator.InterpolateObjectAction.CacheItem var2 = StringSearchModelInterpolator.CACHED_ENTRIES.get(var1);
         if (var2 == null) {
            var2 = new StringSearchModelInterpolator.InterpolateObjectAction.CacheItem(var1);
            StringSearchModelInterpolator.CACHED_ENTRIES.put(var1, var2);
         }

         return var2;
      }

      private static void evaluateArray(Object var0, StringSearchModelInterpolator.InterpolateObjectAction var1) {
         int var2 = Array.getLength(var0);

         for (int var3 = 0; var3 < var2; var3++) {
            Object var4 = Array.get(var0, var3);
            if (var4 != null) {
               if (String.class == var4.getClass()) {
                  String var5 = var1.interpolate((String)var4);
                  if (!var5.equals(var4)) {
                     Array.set(var0, var3, var5);
                  }
               } else {
                  var1.interpolationTargets.add(var4);
               }
            }
         }
      }

      abstract static class CacheField {
         final Field field;

         CacheField(Field var1) {
            this.field = var1;
            var1.setAccessible(true);
         }

         void interpolate(Object var1, StringSearchModelInterpolator.InterpolateObjectAction var2) {
            try {
               this.doInterpolate(var1, var2);
            } catch (IllegalArgumentException var4) {
               var2.problems
                  .add(
                     new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                        .setMessage("Failed to interpolate field3: " + this.field + " on class: " + this.field.getType().getName())
                        .setException(var4)
                  );
            } catch (IllegalAccessException var5) {
               var2.problems
                  .add(
                     new ModelProblemCollectorRequest(ModelProblem.Severity.ERROR, ModelProblem.Version.BASE)
                        .setMessage("Failed to interpolate field4: " + this.field + " on class: " + this.field.getType().getName())
                        .setException(var5)
                  );
            }
         }

         abstract void doInterpolate(Object var1, StringSearchModelInterpolator.InterpolateObjectAction var2) throws IllegalAccessException;
      }

      private static class CacheItem {
         private final boolean isArray;
         private final boolean isQualifiedForInterpolation;
         private final StringSearchModelInterpolator.InterpolateObjectAction.CacheField[] fields;

         private boolean isQualifiedForInterpolation(Class<?> var1) {
            Package var2 = var1.getPackage();
            if (var2 == null) {
               return true;
            } else {
               String var3 = var2.getName();
               return !var3.startsWith("java.") && !var3.startsWith("javax.");
            }
         }

         private boolean isQualifiedForInterpolation(Field var1, Class<?> var2) {
            if (Map.class.equals(var2) && "locations".equals(var1.getName())) {
               return false;
            } else if (InputLocation.class.equals(var2)) {
               return false;
            } else {
               return var2.isPrimitive() ? false : !"parent".equals(var1.getName());
            }
         }

         CacheItem(Class var1) {
            this.isQualifiedForInterpolation = this.isQualifiedForInterpolation(var1);
            this.isArray = var1.isArray();
            ArrayList var2 = new ArrayList();
            if (this.isQualifiedForInterpolation) {
               for (Field var6 : var1.getDeclaredFields()) {
                  Class var7 = var6.getType();
                  if (this.isQualifiedForInterpolation(var6, var7)) {
                     if (String.class == var7) {
                        if (!Modifier.isFinal(var6.getModifiers())) {
                           var2.add(new StringSearchModelInterpolator.InterpolateObjectAction.StringField(var6));
                        }
                     } else if (List.class.isAssignableFrom(var7)) {
                        var2.add(new StringSearchModelInterpolator.InterpolateObjectAction.ListField(var6));
                     } else {
                        if (Collection.class.isAssignableFrom(var7)) {
                           throw new RuntimeException("We dont interpolate into collections, use a list instead");
                        }

                        if (Map.class.isAssignableFrom(var7)) {
                           var2.add(new StringSearchModelInterpolator.InterpolateObjectAction.MapField(var6));
                        } else {
                           var2.add(new StringSearchModelInterpolator.InterpolateObjectAction.ObjectField(var6));
                        }
                     }
                  }
               }
            }

            this.fields = var2.toArray(new StringSearchModelInterpolator.InterpolateObjectAction.CacheField[0]);
         }

         void interpolate(Object var1, StringSearchModelInterpolator.InterpolateObjectAction var2) {
            for (StringSearchModelInterpolator.InterpolateObjectAction.CacheField var6 : this.fields) {
               var6.interpolate(var1, var2);
            }
         }

         boolean isArray() {
            return this.isArray;
         }
      }

      static final class ListField extends StringSearchModelInterpolator.InterpolateObjectAction.CacheField {
         ListField(Field var1) {
            super(var1);
         }

         @Override
         void doInterpolate(Object var1, StringSearchModelInterpolator.InterpolateObjectAction var2) throws IllegalAccessException {
            List var3 = (List)this.field.get(var1);
            if (var3 != null) {
               int var4 = 0;

               for (int var5 = var3.size(); var4 < var5; var4++) {
                  Object var6 = var3.get(var4);
                  if (var6 != null) {
                     if (String.class == var6.getClass()) {
                        String var7 = var2.interpolate((String)var6);
                        if (!var7.equals(var6)) {
                           try {
                              var3.set(var4, var7);
                           } catch (UnsupportedOperationException var9) {
                              return;
                           }
                        }
                     } else if (var6.getClass().isArray()) {
                        StringSearchModelInterpolator.InterpolateObjectAction.evaluateArray(var6, var2);
                     } else {
                        var2.interpolationTargets.add(var6);
                     }
                  }
               }
            }
         }
      }

      static final class MapField extends StringSearchModelInterpolator.InterpolateObjectAction.CacheField {
         MapField(Field var1) {
            super(var1);
         }

         @Override
         void doInterpolate(Object var1, StringSearchModelInterpolator.InterpolateObjectAction var2) throws IllegalAccessException {
            Map var3 = (Map)this.field.get(var1);
            if (var3 != null && !var3.isEmpty()) {
               for (Entry var5 : var3.entrySet()) {
                  Object var6 = var5.getValue();
                  if (var6 != null) {
                     if (String.class == var6.getClass()) {
                        String var7 = var2.interpolate((String)var6);
                        if (var7 != null && !var7.equals(var6)) {
                           try {
                              var5.setValue(var7);
                           } catch (UnsupportedOperationException var9) {
                           }
                        }
                     } else if (var6.getClass().isArray()) {
                        StringSearchModelInterpolator.InterpolateObjectAction.evaluateArray(var6, var2);
                     } else {
                        var2.interpolationTargets.add(var6);
                     }
                  }
               }
            }
         }
      }

      static final class ObjectField extends StringSearchModelInterpolator.InterpolateObjectAction.CacheField {
         private final boolean isArray;

         ObjectField(Field var1) {
            super(var1);
            this.isArray = var1.getType().isArray();
         }

         @Override
         void doInterpolate(Object var1, StringSearchModelInterpolator.InterpolateObjectAction var2) throws IllegalAccessException {
            Object var3 = this.field.get(var1);
            if (var3 != null) {
               if (this.isArray) {
                  StringSearchModelInterpolator.InterpolateObjectAction.evaluateArray(var3, var2);
               } else {
                  var2.interpolationTargets.add(var3);
               }
            }
         }
      }

      static final class StringField extends StringSearchModelInterpolator.InterpolateObjectAction.CacheField {
         StringField(Field var1) {
            super(var1);
         }

         @Override
         void doInterpolate(Object var1, StringSearchModelInterpolator.InterpolateObjectAction var2) throws IllegalAccessException {
            String var3 = (String)this.field.get(var1);
            if (var3 != null) {
               String var4 = var2.interpolate(var3);
               if (var4 != null && !var4.equals(var3)) {
                  this.field.set(var1, var4);
               }
            }
         }
      }
   }
}
