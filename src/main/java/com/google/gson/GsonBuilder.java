package com.google.gson;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.DefaultDateTypeAdapter;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.internal.sql.SqlTypesSupport;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GsonBuilder {
   private Excluder excluder = Excluder.DEFAULT;
   private LongSerializationPolicy longSerializationPolicy = LongSerializationPolicy.DEFAULT;
   private FieldNamingStrategy fieldNamingPolicy = FieldNamingPolicy.IDENTITY;
   private final Map<Type, InstanceCreator<?>> instanceCreators = new HashMap<>();
   private final List<TypeAdapterFactory> factories = new ArrayList<>();
   private final List<TypeAdapterFactory> hierarchyFactories = new ArrayList<>();
   private boolean serializeNulls = false;
   private String datePattern;
   private int dateStyle = 2;
   private int timeStyle = 2;
   private boolean complexMapKeySerialization = false;
   private boolean serializeSpecialFloatingPointValues = false;
   private boolean escapeHtmlChars = true;
   private boolean prettyPrinting = false;
   private boolean generateNonExecutableJson = false;
   private boolean lenient = false;
   private ToNumberStrategy objectToNumberStrategy = ToNumberPolicy.DOUBLE;
   private ToNumberStrategy numberToNumberStrategy = ToNumberPolicy.LAZILY_PARSED_NUMBER;

   public GsonBuilder() {
   }

   GsonBuilder(Gson var1) {
      this.excluder = var1.excluder;
      this.fieldNamingPolicy = var1.fieldNamingStrategy;
      this.instanceCreators.putAll(var1.instanceCreators);
      this.serializeNulls = var1.serializeNulls;
      this.complexMapKeySerialization = var1.complexMapKeySerialization;
      this.generateNonExecutableJson = var1.generateNonExecutableJson;
      this.escapeHtmlChars = var1.htmlSafe;
      this.prettyPrinting = var1.prettyPrinting;
      this.lenient = var1.lenient;
      this.serializeSpecialFloatingPointValues = var1.serializeSpecialFloatingPointValues;
      this.longSerializationPolicy = var1.longSerializationPolicy;
      this.datePattern = var1.datePattern;
      this.dateStyle = var1.dateStyle;
      this.timeStyle = var1.timeStyle;
      this.factories.addAll(var1.builderFactories);
      this.hierarchyFactories.addAll(var1.builderHierarchyFactories);
      this.objectToNumberStrategy = var1.objectToNumberStrategy;
      this.numberToNumberStrategy = var1.numberToNumberStrategy;
   }

   public GsonBuilder setVersion(double var1) {
      this.excluder = this.excluder.withVersion(var1);
      return this;
   }

   public GsonBuilder excludeFieldsWithModifiers(int... var1) {
      this.excluder = this.excluder.withModifiers(var1);
      return this;
   }

   public GsonBuilder generateNonExecutableJson() {
      this.generateNonExecutableJson = true;
      return this;
   }

   public GsonBuilder excludeFieldsWithoutExposeAnnotation() {
      this.excluder = this.excluder.excludeFieldsWithoutExposeAnnotation();
      return this;
   }

   public GsonBuilder serializeNulls() {
      this.serializeNulls = true;
      return this;
   }

   public GsonBuilder enableComplexMapKeySerialization() {
      this.complexMapKeySerialization = true;
      return this;
   }

   public GsonBuilder disableInnerClassSerialization() {
      this.excluder = this.excluder.disableInnerClassSerialization();
      return this;
   }

   public GsonBuilder setLongSerializationPolicy(LongSerializationPolicy var1) {
      this.longSerializationPolicy = var1;
      return this;
   }

   public GsonBuilder setFieldNamingPolicy(FieldNamingPolicy var1) {
      this.fieldNamingPolicy = var1;
      return this;
   }

   public GsonBuilder setFieldNamingStrategy(FieldNamingStrategy var1) {
      this.fieldNamingPolicy = var1;
      return this;
   }

   public GsonBuilder setObjectToNumberStrategy(ToNumberStrategy var1) {
      this.objectToNumberStrategy = var1;
      return this;
   }

   public GsonBuilder setNumberToNumberStrategy(ToNumberStrategy var1) {
      this.numberToNumberStrategy = var1;
      return this;
   }

   public GsonBuilder setExclusionStrategies(ExclusionStrategy... var1) {
      for (ExclusionStrategy var5 : var1) {
         this.excluder = this.excluder.withExclusionStrategy(var5, true, true);
      }

      return this;
   }

   public GsonBuilder addSerializationExclusionStrategy(ExclusionStrategy var1) {
      this.excluder = this.excluder.withExclusionStrategy(var1, true, false);
      return this;
   }

   public GsonBuilder addDeserializationExclusionStrategy(ExclusionStrategy var1) {
      this.excluder = this.excluder.withExclusionStrategy(var1, false, true);
      return this;
   }

   public GsonBuilder setPrettyPrinting() {
      this.prettyPrinting = true;
      return this;
   }

   public GsonBuilder setLenient() {
      this.lenient = true;
      return this;
   }

   public GsonBuilder disableHtmlEscaping() {
      this.escapeHtmlChars = false;
      return this;
   }

   public GsonBuilder setDateFormat(String var1) {
      this.datePattern = var1;
      return this;
   }

   public GsonBuilder setDateFormat(int var1) {
      this.dateStyle = var1;
      this.datePattern = null;
      return this;
   }

   public GsonBuilder setDateFormat(int var1, int var2) {
      this.dateStyle = var1;
      this.timeStyle = var2;
      this.datePattern = null;
      return this;
   }

   public GsonBuilder registerTypeAdapter(Type var1, Object var2) {
      $Gson$Preconditions.checkArgument(
         var2 instanceof JsonSerializer || var2 instanceof JsonDeserializer || var2 instanceof InstanceCreator || var2 instanceof TypeAdapter
      );
      if (var2 instanceof InstanceCreator) {
         this.instanceCreators.put(var1, (InstanceCreator<?>)var2);
      }

      if (var2 instanceof JsonSerializer || var2 instanceof JsonDeserializer) {
         TypeToken var3 = TypeToken.get(var1);
         this.factories.add(TreeTypeAdapter.newFactoryWithMatchRawType(var3, var2));
      }

      if (var2 instanceof TypeAdapter) {
         this.factories.add(TypeAdapters.newFactory(TypeToken.get(var1), (TypeAdapter<?>)var2));
      }

      return this;
   }

   public GsonBuilder registerTypeAdapterFactory(TypeAdapterFactory var1) {
      this.factories.add(var1);
      return this;
   }

   public GsonBuilder registerTypeHierarchyAdapter(Class<?> var1, Object var2) {
      $Gson$Preconditions.checkArgument(var2 instanceof JsonSerializer || var2 instanceof JsonDeserializer || var2 instanceof TypeAdapter);
      if (var2 instanceof JsonDeserializer || var2 instanceof JsonSerializer) {
         this.hierarchyFactories.add(TreeTypeAdapter.newTypeHierarchyFactory(var1, var2));
      }

      if (var2 instanceof TypeAdapter) {
         this.factories.add(TypeAdapters.newTypeHierarchyFactory(var1, (TypeAdapter)var2));
      }

      return this;
   }

   public GsonBuilder serializeSpecialFloatingPointValues() {
      this.serializeSpecialFloatingPointValues = true;
      return this;
   }

   public Gson create() {
      ArrayList var1 = new ArrayList(this.factories.size() + this.hierarchyFactories.size() + 3);
      var1.addAll(this.factories);
      Collections.reverse(var1);
      ArrayList var2 = new ArrayList<>(this.hierarchyFactories);
      Collections.reverse(var2);
      var1.addAll(var2);
      this.addTypeAdaptersForDate(this.datePattern, this.dateStyle, this.timeStyle, var1);
      return new Gson(
         this.excluder,
         this.fieldNamingPolicy,
         this.instanceCreators,
         this.serializeNulls,
         this.complexMapKeySerialization,
         this.generateNonExecutableJson,
         this.escapeHtmlChars,
         this.prettyPrinting,
         this.lenient,
         this.serializeSpecialFloatingPointValues,
         this.longSerializationPolicy,
         this.datePattern,
         this.dateStyle,
         this.timeStyle,
         this.factories,
         this.hierarchyFactories,
         var1,
         this.objectToNumberStrategy,
         this.numberToNumberStrategy
      );
   }

   private void addTypeAdaptersForDate(String var1, int var2, int var3, List<TypeAdapterFactory> var4) {
      boolean var6 = SqlTypesSupport.SUPPORTS_SQL_TYPES;
      TypeAdapterFactory var7 = null;
      TypeAdapterFactory var8 = null;
      TypeAdapterFactory var5;
      if (var1 != null && !var1.trim().isEmpty()) {
         var5 = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(var1);
         if (var6) {
            var7 = SqlTypesSupport.TIMESTAMP_DATE_TYPE.createAdapterFactory(var1);
            var8 = SqlTypesSupport.DATE_DATE_TYPE.createAdapterFactory(var1);
         }
      } else {
         if (var2 == 2 || var3 == 2) {
            return;
         }

         var5 = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(var2, var3);
         if (var6) {
            var7 = SqlTypesSupport.TIMESTAMP_DATE_TYPE.createAdapterFactory(var2, var3);
            var8 = SqlTypesSupport.DATE_DATE_TYPE.createAdapterFactory(var2, var3);
         }
      }

      var4.add(var5);
      if (var6) {
         var4.add(var7);
         var4.add(var8);
      }
   }
}
