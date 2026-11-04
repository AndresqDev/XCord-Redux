package com.google.gson.internal;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Excluder implements TypeAdapterFactory, Cloneable {
   private static final double IGNORE_VERSIONS = -1.0;
   public static final Excluder DEFAULT = new Excluder();
   private double version = -1.0;
   private int modifiers = 136;
   private boolean serializeInnerClasses = true;
   private boolean requireExpose;
   private List<ExclusionStrategy> serializationStrategies = Collections.emptyList();
   private List<ExclusionStrategy> deserializationStrategies = Collections.emptyList();

   protected Excluder clone() {
      try {
         return (Excluder)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError(var2);
      }
   }

   public Excluder withVersion(double var1) {
      Excluder var3 = this.clone();
      var3.version = var1;
      return var3;
   }

   public Excluder withModifiers(int... var1) {
      Excluder var2 = this.clone();
      var2.modifiers = 0;

      for (int var6 : var1) {
         var2.modifiers |= var6;
      }

      return var2;
   }

   public Excluder disableInnerClassSerialization() {
      Excluder var1 = this.clone();
      var1.serializeInnerClasses = false;
      return var1;
   }

   public Excluder excludeFieldsWithoutExposeAnnotation() {
      Excluder var1 = this.clone();
      var1.requireExpose = true;
      return var1;
   }

   public Excluder withExclusionStrategy(ExclusionStrategy var1, boolean var2, boolean var3) {
      Excluder var4 = this.clone();
      if (var2) {
         var4.serializationStrategies = new ArrayList<>(this.serializationStrategies);
         var4.serializationStrategies.add(var1);
      }

      if (var3) {
         var4.deserializationStrategies = new ArrayList<>(this.deserializationStrategies);
         var4.deserializationStrategies.add(var1);
      }

      return var4;
   }

   @Override
   public <T> TypeAdapter<T> create(final Gson var1, final TypeToken<T> var2) {
      Class var3 = var2.getRawType();
      boolean var4 = this.excludeClassChecks(var3);
      final boolean var5 = var4 || this.excludeClassInStrategy(var3, true);
      final boolean var6 = var4 || this.excludeClassInStrategy(var3, false);
      return !var5 && !var6 ? null : new TypeAdapter<T>() {
         private TypeAdapter<T> delegate;

         @Override
         public T read(JsonReader var1x) throws IOException {
            if (var6) {
               var1x.skipValue();
               return null;
            } else {
               return (T)this.delegate().read(var1x);
            }
         }

         @Override
         public void write(JsonWriter var1x, T var2x) throws IOException {
            if (var5) {
               var1x.nullValue();
            } else {
               this.delegate().write(var1x, var2x);
            }
         }

         private TypeAdapter<T> delegate() {
            TypeAdapter var1x = this.delegate;
            return var1x != null ? var1x : (this.delegate = var1.getDelegateAdapter(Excluder.this, var2));
         }
      };
   }

   public boolean excludeField(Field var1, boolean var2) {
      if ((this.modifiers & var1.getModifiers()) != 0) {
         return true;
      } else if (this.version != -1.0 && !this.isValidVersion(var1.getAnnotation(Since.class), var1.getAnnotation(Until.class))) {
         return true;
      } else if (var1.isSynthetic()) {
         return true;
      } else {
         if (this.requireExpose) {
            Expose var3 = var1.getAnnotation(Expose.class);
            if (var3 == null || (var2 ? !var3.serialize() : !var3.deserialize())) {
               return true;
            }
         }

         if (!this.serializeInnerClasses && this.isInnerClass(var1.getType())) {
            return true;
         } else if (this.isAnonymousOrNonStaticLocal(var1.getType())) {
            return true;
         } else {
            List var7 = var2 ? this.serializationStrategies : this.deserializationStrategies;
            if (!var7.isEmpty()) {
               FieldAttributes var4 = new FieldAttributes(var1);

               for (ExclusionStrategy var6 : var7) {
                  if (var6.shouldSkipField(var4)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   private boolean excludeClassChecks(Class<?> var1) {
      if (this.version != -1.0 && !this.isValidVersion(var1.getAnnotation(Since.class), var1.getAnnotation(Until.class))) {
         return true;
      } else {
         return !this.serializeInnerClasses && this.isInnerClass(var1) ? true : this.isAnonymousOrNonStaticLocal(var1);
      }
   }

   public boolean excludeClass(Class<?> var1, boolean var2) {
      return this.excludeClassChecks(var1) || this.excludeClassInStrategy(var1, var2);
   }

   private boolean excludeClassInStrategy(Class<?> var1, boolean var2) {
      for (ExclusionStrategy var5 : var2 ? this.serializationStrategies : this.deserializationStrategies) {
         if (var5.shouldSkipClass(var1)) {
            return true;
         }
      }

      return false;
   }

   private boolean isAnonymousOrNonStaticLocal(Class<?> var1) {
      return !Enum.class.isAssignableFrom(var1) && !this.isStatic(var1) && (var1.isAnonymousClass() || var1.isLocalClass());
   }

   private boolean isInnerClass(Class<?> var1) {
      return var1.isMemberClass() && !this.isStatic(var1);
   }

   private boolean isStatic(Class<?> var1) {
      return (var1.getModifiers() & 8) != 0;
   }

   private boolean isValidVersion(Since var1, Until var2) {
      return this.isValidSince(var1) && this.isValidUntil(var2);
   }

   private boolean isValidSince(Since var1) {
      if (var1 != null) {
         double var2 = var1.value();
         if (var2 > this.version) {
            return false;
         }
      }

      return true;
   }

   private boolean isValidUntil(Until var1) {
      if (var1 != null) {
         double var2 = var1.value();
         if (var2 <= this.version) {
            return false;
         }
      }

      return true;
   }
}
