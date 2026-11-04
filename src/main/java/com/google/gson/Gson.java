package com.google.gson;

import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.internal.bind.CollectionTypeAdapterFactory;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.google.gson.internal.bind.NumberTypeAdapter;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.internal.sql.SqlTypesSupport;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public final class Gson {
   static final boolean DEFAULT_JSON_NON_EXECUTABLE = false;
   static final boolean DEFAULT_LENIENT = false;
   static final boolean DEFAULT_PRETTY_PRINT = false;
   static final boolean DEFAULT_ESCAPE_HTML = true;
   static final boolean DEFAULT_SERIALIZE_NULLS = false;
   static final boolean DEFAULT_COMPLEX_MAP_KEYS = false;
   static final boolean DEFAULT_SPECIALIZE_FLOAT_VALUES = false;
   private static final TypeToken<?> NULL_KEY_SURROGATE = TypeToken.get(Object.class);
   private static final String JSON_NON_EXECUTABLE_PREFIX = ")]}'\n";
   private final ThreadLocal<Map<TypeToken<?>, Gson.FutureTypeAdapter<?>>> calls = new ThreadLocal<>();
   private final Map<TypeToken<?>, TypeAdapter<?>> typeTokenCache = new ConcurrentHashMap<>();
   private final ConstructorConstructor constructorConstructor;
   private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
   final List<TypeAdapterFactory> factories;
   final Excluder excluder;
   final FieldNamingStrategy fieldNamingStrategy;
   final Map<Type, InstanceCreator<?>> instanceCreators;
   final boolean serializeNulls;
   final boolean complexMapKeySerialization;
   final boolean generateNonExecutableJson;
   final boolean htmlSafe;
   final boolean prettyPrinting;
   final boolean lenient;
   final boolean serializeSpecialFloatingPointValues;
   final String datePattern;
   final int dateStyle;
   final int timeStyle;
   final LongSerializationPolicy longSerializationPolicy;
   final List<TypeAdapterFactory> builderFactories;
   final List<TypeAdapterFactory> builderHierarchyFactories;
   final ToNumberStrategy objectToNumberStrategy;
   final ToNumberStrategy numberToNumberStrategy;

   public Gson() {
      this(
         Excluder.DEFAULT,
         FieldNamingPolicy.IDENTITY,
         Collections.emptyMap(),
         false,
         false,
         false,
         true,
         false,
         false,
         false,
         LongSerializationPolicy.DEFAULT,
         null,
         2,
         2,
         Collections.emptyList(),
         Collections.emptyList(),
         Collections.emptyList(),
         ToNumberPolicy.DOUBLE,
         ToNumberPolicy.LAZILY_PARSED_NUMBER
      );
   }

   Gson(
      Excluder var1,
      FieldNamingStrategy var2,
      Map<Type, InstanceCreator<?>> var3,
      boolean var4,
      boolean var5,
      boolean var6,
      boolean var7,
      boolean var8,
      boolean var9,
      boolean var10,
      LongSerializationPolicy var11,
      String var12,
      int var13,
      int var14,
      List<TypeAdapterFactory> var15,
      List<TypeAdapterFactory> var16,
      List<TypeAdapterFactory> var17,
      ToNumberStrategy var18,
      ToNumberStrategy var19
   ) {
      this.excluder = var1;
      this.fieldNamingStrategy = var2;
      this.instanceCreators = var3;
      this.constructorConstructor = new ConstructorConstructor(var3);
      this.serializeNulls = var4;
      this.complexMapKeySerialization = var5;
      this.generateNonExecutableJson = var6;
      this.htmlSafe = var7;
      this.prettyPrinting = var8;
      this.lenient = var9;
      this.serializeSpecialFloatingPointValues = var10;
      this.longSerializationPolicy = var11;
      this.datePattern = var12;
      this.dateStyle = var13;
      this.timeStyle = var14;
      this.builderFactories = var15;
      this.builderHierarchyFactories = var16;
      this.objectToNumberStrategy = var18;
      this.numberToNumberStrategy = var19;
      ArrayList var20 = new ArrayList();
      var20.add(TypeAdapters.JSON_ELEMENT_FACTORY);
      var20.add(ObjectTypeAdapter.getFactory(var18));
      var20.add(var1);
      var20.addAll(var17);
      var20.add(TypeAdapters.STRING_FACTORY);
      var20.add(TypeAdapters.INTEGER_FACTORY);
      var20.add(TypeAdapters.BOOLEAN_FACTORY);
      var20.add(TypeAdapters.BYTE_FACTORY);
      var20.add(TypeAdapters.SHORT_FACTORY);
      TypeAdapter var21 = longAdapter(var11);
      var20.add(TypeAdapters.newFactory(long.class, Long.class, var21));
      var20.add(TypeAdapters.newFactory(double.class, Double.class, this.doubleAdapter(var10)));
      var20.add(TypeAdapters.newFactory(float.class, Float.class, this.floatAdapter(var10)));
      var20.add(NumberTypeAdapter.getFactory(var19));
      var20.add(TypeAdapters.ATOMIC_INTEGER_FACTORY);
      var20.add(TypeAdapters.ATOMIC_BOOLEAN_FACTORY);
      var20.add(TypeAdapters.newFactory(AtomicLong.class, atomicLongAdapter(var21)));
      var20.add(TypeAdapters.newFactory(AtomicLongArray.class, atomicLongArrayAdapter(var21)));
      var20.add(TypeAdapters.ATOMIC_INTEGER_ARRAY_FACTORY);
      var20.add(TypeAdapters.CHARACTER_FACTORY);
      var20.add(TypeAdapters.STRING_BUILDER_FACTORY);
      var20.add(TypeAdapters.STRING_BUFFER_FACTORY);
      var20.add(TypeAdapters.newFactory(BigDecimal.class, TypeAdapters.BIG_DECIMAL));
      var20.add(TypeAdapters.newFactory(BigInteger.class, TypeAdapters.BIG_INTEGER));
      var20.add(TypeAdapters.URL_FACTORY);
      var20.add(TypeAdapters.URI_FACTORY);
      var20.add(TypeAdapters.UUID_FACTORY);
      var20.add(TypeAdapters.CURRENCY_FACTORY);
      var20.add(TypeAdapters.LOCALE_FACTORY);
      var20.add(TypeAdapters.INET_ADDRESS_FACTORY);
      var20.add(TypeAdapters.BIT_SET_FACTORY);
      var20.add(DateTypeAdapter.FACTORY);
      var20.add(TypeAdapters.CALENDAR_FACTORY);
      if (SqlTypesSupport.SUPPORTS_SQL_TYPES) {
         var20.add(SqlTypesSupport.TIME_FACTORY);
         var20.add(SqlTypesSupport.DATE_FACTORY);
         var20.add(SqlTypesSupport.TIMESTAMP_FACTORY);
      }

      var20.add(ArrayTypeAdapter.FACTORY);
      var20.add(TypeAdapters.CLASS_FACTORY);
      var20.add(new CollectionTypeAdapterFactory(this.constructorConstructor));
      var20.add(new MapTypeAdapterFactory(this.constructorConstructor, var5));
      this.jsonAdapterFactory = new JsonAdapterAnnotationTypeAdapterFactory(this.constructorConstructor);
      var20.add(this.jsonAdapterFactory);
      var20.add(TypeAdapters.ENUM_FACTORY);
      var20.add(new ReflectiveTypeAdapterFactory(this.constructorConstructor, var2, var1, this.jsonAdapterFactory));
      this.factories = Collections.unmodifiableList(var20);
   }

   public GsonBuilder newBuilder() {
      return new GsonBuilder(this);
   }

   @Deprecated
   public Excluder excluder() {
      return this.excluder;
   }

   public FieldNamingStrategy fieldNamingStrategy() {
      return this.fieldNamingStrategy;
   }

   public boolean serializeNulls() {
      return this.serializeNulls;
   }

   public boolean htmlSafe() {
      return this.htmlSafe;
   }

   private TypeAdapter<Number> doubleAdapter(boolean var1) {
      return var1 ? TypeAdapters.DOUBLE : new TypeAdapter<Number>() {
         public Double read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return var1.nextDouble();
            }
         }

         public void write(JsonWriter var1, Number var2) throws IOException {
            if (var2 == null) {
               var1.nullValue();
            } else {
               double var3 = var2.doubleValue();
               Gson.checkValidFloatingPoint(var3);
               var1.value(var2);
            }
         }
      };
   }

   private TypeAdapter<Number> floatAdapter(boolean var1) {
      return var1 ? TypeAdapters.FLOAT : new TypeAdapter<Number>() {
         public Float read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return (float)var1.nextDouble();
            }
         }

         public void write(JsonWriter var1, Number var2) throws IOException {
            if (var2 == null) {
               var1.nullValue();
            } else {
               float var3 = var2.floatValue();
               Gson.checkValidFloatingPoint((double)var3);
               var1.value(var2);
            }
         }
      };
   }

   static void checkValidFloatingPoint(double var0) {
      if (Double.isNaN(var0) || Double.isInfinite(var0)) {
         throw new IllegalArgumentException(
            var0
               + " is not a valid double value as per JSON specification. To override this behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method."
         );
      }
   }

   private static TypeAdapter<Number> longAdapter(LongSerializationPolicy var0) {
      return var0 == LongSerializationPolicy.DEFAULT ? TypeAdapters.LONG : new TypeAdapter<Number>() {
         public Number read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return var1.nextLong();
            }
         }

         public void write(JsonWriter var1, Number var2) throws IOException {
            if (var2 == null) {
               var1.nullValue();
            } else {
               var1.value(var2.toString());
            }
         }
      };
   }

   private static TypeAdapter<AtomicLong> atomicLongAdapter(final TypeAdapter<Number> var0) {
      return (new TypeAdapter<AtomicLong>() {
         public void write(JsonWriter var1, AtomicLong var2) throws IOException {
            var0.write(var1, var2.get());
         }

         public AtomicLong read(JsonReader var1) throws IOException {
            Number var2 = (Number)var0.read(var1);
            return new AtomicLong(var2.longValue());
         }
      }).nullSafe();
   }

   private static TypeAdapter<AtomicLongArray> atomicLongArrayAdapter(final TypeAdapter<Number> var0) {
      return (new TypeAdapter<AtomicLongArray>() {
         public void write(JsonWriter var1, AtomicLongArray var2) throws IOException {
            var1.beginArray();
            int var3 = 0;

            for (int var4 = var2.length(); var3 < var4; var3++) {
               var0.write(var1, var2.get(var3));
            }

            var1.endArray();
         }

         public AtomicLongArray read(JsonReader var1) throws IOException {
            ArrayList var2 = new ArrayList();
            var1.beginArray();

            while (var1.hasNext()) {
               long var3 = ((Number)var0.read(var1)).longValue();
               var2.add(var3);
            }

            var1.endArray();
            int var6 = var2.size();
            AtomicLongArray var4 = new AtomicLongArray(var6);

            for (int var5 = 0; var5 < var6; var5++) {
               var4.set(var5, (Long)var2.get(var5));
            }

            return var4;
         }
      }).nullSafe();
   }

   public <T> TypeAdapter<T> getAdapter(TypeToken<T> var1) {
      TypeAdapter var2 = this.typeTokenCache.get(var1 == null ? NULL_KEY_SURROGATE : var1);
      if (var2 != null) {
         return var2;
      } else {
         Object var3 = this.calls.get();
         boolean var4 = false;
         if (var3 == null) {
            var3 = new HashMap();
            this.calls.set((Map<TypeToken<?>, Gson.FutureTypeAdapter<?>>)var3);
            var4 = true;
         }

         Gson.FutureTypeAdapter var5 = (Gson.FutureTypeAdapter)var3.get(var1);
         if (var5 != null) {
            return var5;
         } else {
            TypeAdapter var10;
            try {
               Gson.FutureTypeAdapter var6 = new Gson.FutureTypeAdapter();
               var3.put(var1, var6);
               Iterator var7 = this.factories.iterator();

               TypeAdapter var9;
               do {
                  if (!var7.hasNext()) {
                     throw new IllegalArgumentException("GSON (2.8.9) cannot handle " + var1);
                  }

                  TypeAdapterFactory var8 = (TypeAdapterFactory)var7.next();
                  var9 = var8.create(this, var1);
               } while (var9 == null);

               var6.setDelegate(var9);
               this.typeTokenCache.put(var1, var9);
               var10 = var9;
            } finally {
               var3.remove(var1);
               if (var4) {
                  this.calls.remove();
               }
            }

            return var10;
         }
      }
   }

   public <T> TypeAdapter<T> getDelegateAdapter(TypeAdapterFactory var1, TypeToken<T> var2) {
      if (!this.factories.contains(var1)) {
         var1 = this.jsonAdapterFactory;
      }

      boolean var3 = false;

      for (TypeAdapterFactory var5 : this.factories) {
         if (!var3) {
            if (var5 == var1) {
               var3 = true;
            }
         } else {
            TypeAdapter var6 = var5.create(this, var2);
            if (var6 != null) {
               return var6;
            }
         }
      }

      throw new IllegalArgumentException("GSON cannot serialize " + var2);
   }

   public <T> TypeAdapter<T> getAdapter(Class<T> var1) {
      return this.getAdapter(TypeToken.get(var1));
   }

   public JsonElement toJsonTree(Object var1) {
      return (JsonElement)(var1 == null ? JsonNull.INSTANCE : this.toJsonTree(var1, var1.getClass()));
   }

   public JsonElement toJsonTree(Object var1, Type var2) {
      JsonTreeWriter var3 = new JsonTreeWriter();
      this.toJson(var1, var2, var3);
      return var3.get();
   }

   public String toJson(Object var1) {
      return var1 == null ? this.toJson((JsonElement)JsonNull.INSTANCE) : this.toJson(var1, var1.getClass());
   }

   public String toJson(Object var1, Type var2) {
      StringWriter var3 = new StringWriter();
      this.toJson(var1, var2, var3);
      return var3.toString();
   }

   public void toJson(Object var1, Appendable var2) throws JsonIOException {
      if (var1 != null) {
         this.toJson(var1, var1.getClass(), var2);
      } else {
         this.toJson((JsonElement)JsonNull.INSTANCE, var2);
      }
   }

   public void toJson(Object var1, Type var2, Appendable var3) throws JsonIOException {
      try {
         JsonWriter var4 = this.newJsonWriter(Streams.writerForAppendable(var3));
         this.toJson(var1, var2, var4);
      } catch (IOException var5) {
         throw new JsonIOException(var5);
      }
   }

   public void toJson(Object var1, Type var2, JsonWriter var3) throws JsonIOException {
      TypeAdapter var4 = this.getAdapter(TypeToken.get(var2));
      boolean var5 = var3.isLenient();
      var3.setLenient(true);
      boolean var6 = var3.isHtmlSafe();
      var3.setHtmlSafe(this.htmlSafe);
      boolean var7 = var3.getSerializeNulls();
      var3.setSerializeNulls(this.serializeNulls);

      try {
         var4.write(var3, var1);
      } catch (IOException var14) {
         throw new JsonIOException(var14);
      } catch (AssertionError var15) {
         AssertionError var9 = new AssertionError("AssertionError (GSON 2.8.9): " + var15.getMessage());
         var9.initCause(var15);
         throw var9;
      } finally {
         var3.setLenient(var5);
         var3.setHtmlSafe(var6);
         var3.setSerializeNulls(var7);
      }
   }

   public String toJson(JsonElement var1) {
      StringWriter var2 = new StringWriter();
      this.toJson(var1, var2);
      return var2.toString();
   }

   public void toJson(JsonElement var1, Appendable var2) throws JsonIOException {
      try {
         JsonWriter var3 = this.newJsonWriter(Streams.writerForAppendable(var2));
         this.toJson(var1, var3);
      } catch (IOException var4) {
         throw new JsonIOException(var4);
      }
   }

   public JsonWriter newJsonWriter(Writer var1) throws IOException {
      if (this.generateNonExecutableJson) {
         var1.write(")]}'\n");
      }

      JsonWriter var2 = new JsonWriter(var1);
      if (this.prettyPrinting) {
         var2.setIndent("  ");
      }

      var2.setSerializeNulls(this.serializeNulls);
      return var2;
   }

   public JsonReader newJsonReader(Reader var1) {
      JsonReader var2 = new JsonReader(var1);
      var2.setLenient(this.lenient);
      return var2;
   }

   public void toJson(JsonElement var1, JsonWriter var2) throws JsonIOException {
      boolean var3 = var2.isLenient();
      var2.setLenient(true);
      boolean var4 = var2.isHtmlSafe();
      var2.setHtmlSafe(this.htmlSafe);
      boolean var5 = var2.getSerializeNulls();
      var2.setSerializeNulls(this.serializeNulls);

      try {
         Streams.write(var1, var2);
      } catch (IOException var12) {
         throw new JsonIOException(var12);
      } catch (AssertionError var13) {
         AssertionError var7 = new AssertionError("AssertionError (GSON 2.8.9): " + var13.getMessage());
         var7.initCause(var13);
         throw var7;
      } finally {
         var2.setLenient(var3);
         var2.setHtmlSafe(var4);
         var2.setSerializeNulls(var5);
      }
   }

   public <T> T fromJson(String var1, Class<T> var2) throws JsonSyntaxException {
      Object var3 = this.fromJson(var1, var2);
      return Primitives.<T>wrap(var2).cast(var3);
   }

   public <T> T fromJson(String var1, Type var2) throws JsonSyntaxException {
      if (var1 == null) {
         return null;
      } else {
         StringReader var3 = new StringReader(var1);
         return this.fromJson(var3, var2);
      }
   }

   public <T> T fromJson(Reader var1, Class<T> var2) throws JsonSyntaxException, JsonIOException {
      JsonReader var3 = this.newJsonReader(var1);
      Object var4 = this.fromJson(var3, var2);
      assertFullConsumption(var4, var3);
      return Primitives.<T>wrap(var2).cast(var4);
   }

   public <T> T fromJson(Reader var1, Type var2) throws JsonIOException, JsonSyntaxException {
      JsonReader var3 = this.newJsonReader(var1);
      Object var4 = this.fromJson(var3, var2);
      assertFullConsumption(var4, var3);
      return (T)var4;
   }

   private static void assertFullConsumption(Object var0, JsonReader var1) {
      try {
         if (var0 != null && var1.peek() != JsonToken.END_DOCUMENT) {
            throw new JsonIOException("JSON document was not fully consumed.");
         }
      } catch (MalformedJsonException var3) {
         throw new JsonSyntaxException(var3);
      } catch (IOException var4) {
         throw new JsonIOException(var4);
      }
   }

   public <T> T fromJson(JsonReader var1, Type var2) throws JsonIOException, JsonSyntaxException {
      boolean var3 = true;
      boolean var4 = var1.isLenient();
      var1.setLenient(true);

      AssertionError var20;
      try {
         var1.peek();
         var3 = false;
         TypeToken var5 = TypeToken.get(var2);
         var20 = this.getAdapter(var5);
         return (T)((TypeAdapter)var20).read(var1);
      } catch (EOFException var15) {
         if (!var3) {
            throw new JsonSyntaxException(var15);
         }

         var20 = null;
      } catch (IllegalStateException var16) {
         throw new JsonSyntaxException(var16);
      } catch (IOException var17) {
         throw new JsonSyntaxException(var17);
      } catch (AssertionError var18) {
         var20 = new AssertionError("AssertionError (GSON 2.8.9): " + var18.getMessage());
         var20.initCause(var18);
         throw var20;
      } finally {
         var1.setLenient(var4);
      }

      return (T)var20;
   }

   public <T> T fromJson(JsonElement var1, Class<T> var2) throws JsonSyntaxException {
      Object var3 = this.fromJson(var1, var2);
      return Primitives.<T>wrap(var2).cast(var3);
   }

   public <T> T fromJson(JsonElement var1, Type var2) throws JsonSyntaxException {
      return var1 == null ? null : this.fromJson(new JsonTreeReader(var1), var2);
   }

   @Override
   public String toString() {
      return "{serializeNulls:" + this.serializeNulls + ",factories:" + this.factories + ",instanceCreators:" + this.constructorConstructor + "}";
   }

   static class FutureTypeAdapter<T> extends TypeAdapter<T> {
      private TypeAdapter<T> delegate;

      public void setDelegate(TypeAdapter<T> var1) {
         if (this.delegate != null) {
            throw new AssertionError();
         } else {
            this.delegate = var1;
         }
      }

      @Override
      public T read(JsonReader var1) throws IOException {
         if (this.delegate == null) {
            throw new IllegalStateException();
         } else {
            return this.delegate.read(var1);
         }
      }

      @Override
      public void write(JsonWriter var1, T var2) throws IOException {
         if (this.delegate == null) {
            throw new IllegalStateException();
         } else {
            this.delegate.write(var1, (T)var2);
         }
      }
   }
}
