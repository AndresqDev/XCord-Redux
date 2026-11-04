package com.google.protobuf;

import java.util.Map;
import java.util.Map.Entry;

class MapFieldSchemaFull implements MapFieldSchema {
   @Override
   public Map<?, ?> forMutableMapData(Object var1) {
      return ((MapField)var1).getMutableMap();
   }

   @Override
   public Map<?, ?> forMapData(Object var1) {
      return ((MapField)var1).getMap();
   }

   @Override
   public boolean isImmutable(Object var1) {
      return !((MapField)var1).isMutable();
   }

   @Override
   public Object toImmutable(Object var1) {
      ((MapField)var1).makeImmutable();
      return var1;
   }

   @Override
   public Object newMapField(Object var1) {
      return MapField.newMapField((MapEntry)var1);
   }

   @Override
   public MapEntryLite.Metadata<?, ?> forMapMetadata(Object var1) {
      return ((MapEntry)var1).getMetadata();
   }

   @Override
   public Object mergeFrom(Object var1, Object var2) {
      return mergeFromFull(var1, var2);
   }

   private static <K, V> Object mergeFromFull(Object var0, Object var1) {
      MapField var2 = (MapField)var0;
      MapField var3 = (MapField)var1;
      if (!var2.isMutable()) {
         var2.copy();
      }

      var2.mergeFrom(var3);
      return var2;
   }

   @Override
   public int getSerializedSize(int var1, Object var2, Object var3) {
      return getSerializedSizeFull(var1, var2, var3);
   }

   private static <K, V> int getSerializedSizeFull(int var0, Object var1, Object var2) {
      if (var1 == null) {
         return 0;
      } else {
         Map var3 = ((MapField)var1).getMap();
         MapEntry var4 = (MapEntry)var2;
         if (var3.isEmpty()) {
            return 0;
         } else {
            int var5 = 0;

            for (Entry var7 : var3.entrySet()) {
               var5 += CodedOutputStream.computeTagSize(var0)
                  + CodedOutputStream.computeLengthDelimitedFieldSize(
                     MapEntryLite.computeSerializedSize(var4.getMetadata(), (K)var7.getKey(), (V)var7.getValue())
                  );
            }

            return var5;
         }
      }
   }
}
