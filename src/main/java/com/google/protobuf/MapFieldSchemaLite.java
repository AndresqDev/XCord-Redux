package com.google.protobuf;

import java.util.Map;
import java.util.Map.Entry;

class MapFieldSchemaLite implements MapFieldSchema {
   @Override
   public Map<?, ?> forMutableMapData(Object var1) {
      return (MapFieldLite)var1;
   }

   @Override
   public MapEntryLite.Metadata<?, ?> forMapMetadata(Object var1) {
      return ((MapEntryLite)var1).getMetadata();
   }

   @Override
   public Map<?, ?> forMapData(Object var1) {
      return (MapFieldLite)var1;
   }

   @Override
   public boolean isImmutable(Object var1) {
      return !((MapFieldLite)var1).isMutable();
   }

   @Override
   public Object toImmutable(Object var1) {
      ((MapFieldLite)var1).makeImmutable();
      return var1;
   }

   @Override
   public Object newMapField(Object var1) {
      return MapFieldLite.emptyMapField().mutableCopy();
   }

   @Override
   public Object mergeFrom(Object var1, Object var2) {
      return mergeFromLite(var1, var2);
   }

   private static <K, V> MapFieldLite<K, V> mergeFromLite(Object var0, Object var1) {
      MapFieldLite var2 = (MapFieldLite)var0;
      MapFieldLite var3 = (MapFieldLite)var1;
      if (!var3.isEmpty()) {
         if (!var2.isMutable()) {
            var2 = var2.mutableCopy();
         }

         var2.mergeFrom(var3);
      }

      return var2;
   }

   @Override
   public int getSerializedSize(int var1, Object var2, Object var3) {
      return getSerializedSizeLite(var1, var2, var3);
   }

   private static <K, V> int getSerializedSizeLite(int var0, Object var1, Object var2) {
      MapFieldLite var3 = (MapFieldLite)var1;
      MapEntryLite var4 = (MapEntryLite)var2;
      if (var3.isEmpty()) {
         return 0;
      } else {
         int var5 = 0;

         for (Entry var7 : var3.entrySet()) {
            var5 += var4.computeMessageSize(var0, var7.getKey(), var7.getValue());
         }

         return var5;
      }
   }
}
