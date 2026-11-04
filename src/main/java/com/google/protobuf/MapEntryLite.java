package com.google.protobuf;

import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

public class MapEntryLite<K, V> {
   private static final int KEY_FIELD_NUMBER = 1;
   private static final int VALUE_FIELD_NUMBER = 2;
   private final MapEntryLite.Metadata<K, V> metadata;
   private final K key;
   private final V value;

   private MapEntryLite(WireFormat.FieldType var1, K var2, WireFormat.FieldType var3, V var4) {
      this.metadata = new MapEntryLite.Metadata<>(var1, (K)var2, var3, (V)var4);
      this.key = (K)var2;
      this.value = (V)var4;
   }

   private MapEntryLite(MapEntryLite.Metadata<K, V> var1, K var2, V var3) {
      this.metadata = var1;
      this.key = (K)var2;
      this.value = (V)var3;
   }

   public K getKey() {
      return this.key;
   }

   public V getValue() {
      return this.value;
   }

   public static <K, V> MapEntryLite<K, V> newDefaultInstance(WireFormat.FieldType var0, K var1, WireFormat.FieldType var2, V var3) {
      return new MapEntryLite<>(var0, (K)var1, var2, (V)var3);
   }

   static <K, V> void writeTo(CodedOutputStream var0, MapEntryLite.Metadata<K, V> var1, K var2, V var3) throws IOException {
      FieldSet.writeElement(var0, var1.keyType, 1, var2);
      FieldSet.writeElement(var0, var1.valueType, 2, var3);
   }

   static <K, V> int computeSerializedSize(MapEntryLite.Metadata<K, V> var0, K var1, V var2) {
      return FieldSet.computeElementSize(var0.keyType, 1, var1) + FieldSet.computeElementSize(var0.valueType, 2, var2);
   }

   static <T> T parseField(CodedInputStream var0, ExtensionRegistryLite var1, WireFormat.FieldType var2, T var3) throws IOException {
      switch (var2) {
         case MESSAGE:
            MessageLite.Builder var4 = ((MessageLite)var3).toBuilder();
            var0.readMessage(var4, var1);
            return (T)var4.buildPartial();
         case ENUM:
            return (T)var0.readEnum();
         case GROUP:
            throw new RuntimeException("Groups are not allowed in maps.");
         default:
            return (T)FieldSet.readPrimitiveField(var0, var2, true);
      }
   }

   public void serializeTo(CodedOutputStream var1, int var2, K var3, V var4) throws IOException {
      var1.writeTag(var2, 2);
      var1.writeUInt32NoTag(computeSerializedSize(this.metadata, (K)var3, (V)var4));
      writeTo(var1, this.metadata, (K)var3, (V)var4);
   }

   public int computeMessageSize(int var1, K var2, V var3) {
      return CodedOutputStream.computeTagSize(var1) + CodedOutputStream.computeLengthDelimitedFieldSize(computeSerializedSize(this.metadata, (K)var2, (V)var3));
   }

   public Entry<K, V> parseEntry(ByteString var1, ExtensionRegistryLite var2) throws IOException {
      return parseEntry(var1.newCodedInput(), this.metadata, var2);
   }

   static <K, V> Entry<K, V> parseEntry(CodedInputStream var0, MapEntryLite.Metadata<K, V> var1, ExtensionRegistryLite var2) throws IOException {
      Object var3 = var1.defaultKey;
      Object var4 = var1.defaultValue;

      while (true) {
         int var5 = var0.readTag();
         if (var5 == 0) {
            break;
         }

         if (var5 == WireFormat.makeTag(1, var1.keyType.getWireType())) {
            var3 = parseField(var0, var2, var1.keyType, var3);
         } else if (var5 == WireFormat.makeTag(2, var1.valueType.getWireType())) {
            var4 = parseField(var0, var2, var1.valueType, var4);
         } else if (!var0.skipField(var5)) {
            break;
         }
      }

      return new SimpleImmutableEntry<>((K)var3, (V)var4);
   }

   public void parseInto(MapFieldLite<K, V> var1, CodedInputStream var2, ExtensionRegistryLite var3) throws IOException {
      int var4 = var2.readRawVarint32();
      int var5 = var2.pushLimit(var4);
      Object var6 = this.metadata.defaultKey;
      Object var7 = this.metadata.defaultValue;

      while (true) {
         int var8 = var2.readTag();
         if (var8 == 0) {
            break;
         }

         if (var8 == WireFormat.makeTag(1, this.metadata.keyType.getWireType())) {
            var6 = parseField(var2, var3, this.metadata.keyType, var6);
         } else if (var8 == WireFormat.makeTag(2, this.metadata.valueType.getWireType())) {
            var7 = parseField(var2, var3, this.metadata.valueType, var7);
         } else if (!var2.skipField(var8)) {
            break;
         }
      }

      var2.checkLastTagWas(0);
      var2.popLimit(var5);
      var1.put(var6, var7);
   }

   MapEntryLite.Metadata<K, V> getMetadata() {
      return this.metadata;
   }

   static class Metadata<K, V> {
      public final WireFormat.FieldType keyType;
      public final K defaultKey;
      public final WireFormat.FieldType valueType;
      public final V defaultValue;

      public Metadata(WireFormat.FieldType var1, K var2, WireFormat.FieldType var3, V var4) {
         this.keyType = var1;
         this.defaultKey = (K)var2;
         this.valueType = var3;
         this.defaultValue = (V)var4;
      }
   }
}
