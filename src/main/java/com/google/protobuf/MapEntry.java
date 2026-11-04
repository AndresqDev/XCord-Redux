package com.google.protobuf;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public final class MapEntry<K, V> extends AbstractMessage {
   private final K key;
   private final V value;
   private final MapEntry.Metadata<K, V> metadata;
   private volatile int cachedSerializedSize = -1;

   private MapEntry(Descriptors.Descriptor var1, WireFormat.FieldType var2, K var3, WireFormat.FieldType var4, V var5) {
      this.key = (K)var3;
      this.value = (V)var5;
      this.metadata = new MapEntry.Metadata<>(var1, this, var2, var4);
   }

   private MapEntry(MapEntry.Metadata var1, K var2, V var3) {
      this.key = (K)var2;
      this.value = (V)var3;
      this.metadata = var1;
   }

   private MapEntry(MapEntry.Metadata<K, V> var1, CodedInputStream var2, ExtensionRegistryLite var3) throws InvalidProtocolBufferException {
      try {
         this.metadata = var1;
         Entry var4 = MapEntryLite.parseEntry(var2, var1, var3);
         this.key = (K)var4.getKey();
         this.value = (V)var4.getValue();
      } catch (InvalidProtocolBufferException var5) {
         throw var5.setUnfinishedMessage(this);
      } catch (IOException var6) {
         throw new InvalidProtocolBufferException(var6).setUnfinishedMessage(this);
      }
   }

   public static <K, V> MapEntry<K, V> newDefaultInstance(Descriptors.Descriptor var0, WireFormat.FieldType var1, K var2, WireFormat.FieldType var3, V var4) {
      return new MapEntry<>(var0, var1, (K)var2, var3, (V)var4);
   }

   public K getKey() {
      return this.key;
   }

   public V getValue() {
      return this.value;
   }

   @Override
   public int getSerializedSize() {
      if (this.cachedSerializedSize != -1) {
         return this.cachedSerializedSize;
      } else {
         int var1 = MapEntryLite.computeSerializedSize(this.metadata, this.key, this.value);
         this.cachedSerializedSize = var1;
         return var1;
      }
   }

   @Override
   public void writeTo(CodedOutputStream var1) throws IOException {
      MapEntryLite.writeTo(var1, this.metadata, this.key, this.value);
   }

   @Override
   public boolean isInitialized() {
      return isInitialized(this.metadata, this.value);
   }

   @Override
   public Parser<MapEntry<K, V>> getParserForType() {
      return this.metadata.parser;
   }

   public MapEntry.Builder<K, V> newBuilderForType() {
      return new MapEntry.Builder<>(this.metadata);
   }

   public MapEntry.Builder<K, V> toBuilder() {
      return new MapEntry.Builder<>(this.metadata, this.key, this.value, true, true);
   }

   public MapEntry<K, V> getDefaultInstanceForType() {
      return new MapEntry<>(this.metadata, this.metadata.defaultKey, this.metadata.defaultValue);
   }

   @Override
   public Descriptors.Descriptor getDescriptorForType() {
      return this.metadata.descriptor;
   }

   @Override
   public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
      TreeMap var1 = new TreeMap();

      for (Descriptors.FieldDescriptor var3 : this.metadata.descriptor.getFields()) {
         if (this.hasField(var3)) {
            var1.put(var3, this.getField(var3));
         }
      }

      return Collections.unmodifiableMap(var1);
   }

   private void checkFieldDescriptor(Descriptors.FieldDescriptor var1) {
      if (var1.getContainingType() != this.metadata.descriptor) {
         throw new RuntimeException("Wrong FieldDescriptor \"" + var1.getFullName() + "\" used in message \"" + this.metadata.descriptor.getFullName());
      }
   }

   @Override
   public boolean hasField(Descriptors.FieldDescriptor var1) {
      this.checkFieldDescriptor(var1);
      return true;
   }

   @Override
   public Object getField(Descriptors.FieldDescriptor var1) {
      this.checkFieldDescriptor(var1);
      Object var2 = var1.getNumber() == 1 ? this.getKey() : this.getValue();
      if (var1.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
         var2 = var1.getEnumType().findValueByNumberCreatingIfUnknown((Integer)var2);
      }

      return var2;
   }

   @Override
   public int getRepeatedFieldCount(Descriptors.FieldDescriptor var1) {
      throw new RuntimeException("There is no repeated field in a map entry message.");
   }

   @Override
   public Object getRepeatedField(Descriptors.FieldDescriptor var1, int var2) {
      throw new RuntimeException("There is no repeated field in a map entry message.");
   }

   @Override
   public UnknownFieldSet getUnknownFields() {
      return UnknownFieldSet.getDefaultInstance();
   }

   private static <V> boolean isInitialized(MapEntry.Metadata var0, V var1) {
      return var0.valueType.getJavaType() == WireFormat.JavaType.MESSAGE ? ((MessageLite)var1).isInitialized() : true;
   }

   final MapEntry.Metadata<K, V> getMetadata() {
      return this.metadata;
   }

   public static class Builder<K, V> extends AbstractMessage.Builder<MapEntry.Builder<K, V>> {
      private final MapEntry.Metadata<K, V> metadata;
      private K key;
      private V value;
      private boolean hasKey;
      private boolean hasValue;

      private Builder(MapEntry.Metadata<K, V> var1) {
         this(var1, var1.defaultKey, var1.defaultValue, false, false);
      }

      private Builder(MapEntry.Metadata<K, V> var1, K var2, V var3, boolean var4, boolean var5) {
         this.metadata = var1;
         this.key = (K)var2;
         this.value = (V)var3;
         this.hasKey = var4;
         this.hasValue = var5;
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public MapEntry.Builder<K, V> setKey(K var1) {
         this.key = (K)var1;
         this.hasKey = true;
         return this;
      }

      public MapEntry.Builder<K, V> clearKey() {
         this.key = this.metadata.defaultKey;
         this.hasKey = false;
         return this;
      }

      public MapEntry.Builder<K, V> setValue(V var1) {
         this.value = (V)var1;
         this.hasValue = true;
         return this;
      }

      public MapEntry.Builder<K, V> clearValue() {
         this.value = this.metadata.defaultValue;
         this.hasValue = false;
         return this;
      }

      public MapEntry<K, V> build() {
         MapEntry var1 = this.buildPartial();
         if (!var1.isInitialized()) {
            throw newUninitializedMessageException(var1);
         } else {
            return var1;
         }
      }

      public MapEntry<K, V> buildPartial() {
         return new MapEntry<>(this.metadata, this.key, this.value);
      }

      @Override
      public Descriptors.Descriptor getDescriptorForType() {
         return this.metadata.descriptor;
      }

      private void checkFieldDescriptor(Descriptors.FieldDescriptor var1) {
         if (var1.getContainingType() != this.metadata.descriptor) {
            throw new RuntimeException("Wrong FieldDescriptor \"" + var1.getFullName() + "\" used in message \"" + this.metadata.descriptor.getFullName());
         }
      }

      @Override
      public Message.Builder newBuilderForField(Descriptors.FieldDescriptor var1) {
         this.checkFieldDescriptor(var1);
         if (var1.getNumber() == 2 && var1.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            return ((Message)this.value).newBuilderForType();
         } else {
            throw new RuntimeException("\"" + var1.getFullName() + "\" is not a message value field.");
         }
      }

      public MapEntry.Builder<K, V> setField(Descriptors.FieldDescriptor var1, Object var2) {
         this.checkFieldDescriptor(var1);
         if (var1.getNumber() == 1) {
            this.setKey((K)var2);
         } else {
            if (var1.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
               var2 = ((Descriptors.EnumValueDescriptor)var2).getNumber();
            } else if (var1.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && var2 != null && !this.metadata.defaultValue.getClass().isInstance(var2)) {
               var2 = ((Message)this.metadata.defaultValue).toBuilder().mergeFrom((Message)var2).build();
            }

            this.setValue((V)var2);
         }

         return this;
      }

      public MapEntry.Builder<K, V> clearField(Descriptors.FieldDescriptor var1) {
         this.checkFieldDescriptor(var1);
         if (var1.getNumber() == 1) {
            this.clearKey();
         } else {
            this.clearValue();
         }

         return this;
      }

      public MapEntry.Builder<K, V> setRepeatedField(Descriptors.FieldDescriptor var1, int var2, Object var3) {
         throw new RuntimeException("There is no repeated field in a map entry message.");
      }

      public MapEntry.Builder<K, V> addRepeatedField(Descriptors.FieldDescriptor var1, Object var2) {
         throw new RuntimeException("There is no repeated field in a map entry message.");
      }

      public MapEntry.Builder<K, V> setUnknownFields(UnknownFieldSet var1) {
         return this;
      }

      public MapEntry<K, V> getDefaultInstanceForType() {
         return new MapEntry<>(this.metadata, this.metadata.defaultKey, this.metadata.defaultValue);
      }

      @Override
      public boolean isInitialized() {
         return MapEntry.isInitialized(this.metadata, this.value);
      }

      @Override
      public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
         TreeMap var1 = new TreeMap();

         for (Descriptors.FieldDescriptor var3 : this.metadata.descriptor.getFields()) {
            if (this.hasField(var3)) {
               var1.put(var3, this.getField(var3));
            }
         }

         return Collections.unmodifiableMap(var1);
      }

      @Override
      public boolean hasField(Descriptors.FieldDescriptor var1) {
         this.checkFieldDescriptor(var1);
         return var1.getNumber() == 1 ? this.hasKey : this.hasValue;
      }

      @Override
      public Object getField(Descriptors.FieldDescriptor var1) {
         this.checkFieldDescriptor(var1);
         Object var2 = var1.getNumber() == 1 ? this.getKey() : this.getValue();
         if (var1.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
            var2 = var1.getEnumType().findValueByNumberCreatingIfUnknown((Integer)var2);
         }

         return var2;
      }

      @Override
      public int getRepeatedFieldCount(Descriptors.FieldDescriptor var1) {
         throw new RuntimeException("There is no repeated field in a map entry message.");
      }

      @Override
      public Object getRepeatedField(Descriptors.FieldDescriptor var1, int var2) {
         throw new RuntimeException("There is no repeated field in a map entry message.");
      }

      @Override
      public UnknownFieldSet getUnknownFields() {
         return UnknownFieldSet.getDefaultInstance();
      }

      public MapEntry.Builder<K, V> clone() {
         return new MapEntry.Builder<>(this.metadata, this.key, this.value, this.hasKey, this.hasValue);
      }
   }

   private static final class Metadata<K, V> extends MapEntryLite.Metadata<K, V> {
      public final Descriptors.Descriptor descriptor;
      public final Parser<MapEntry<K, V>> parser;

      public Metadata(Descriptors.Descriptor var1, MapEntry<K, V> var2, WireFormat.FieldType var3, WireFormat.FieldType var4) {
         super(var3, var2.key, var4, var2.value);
         this.descriptor = var1;
         this.parser = new AbstractParser<MapEntry<K, V>>() {
            public MapEntry<K, V> parsePartialFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
               return new MapEntry<>(Metadata.this, var1, var2);
            }
         };
      }
   }
}
