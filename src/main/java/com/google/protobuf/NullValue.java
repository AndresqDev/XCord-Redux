package com.google.protobuf;

public enum NullValue implements ProtocolMessageEnum {
   NULL_VALUE(0),
   UNRECOGNIZED(-1);

   public static final int NULL_VALUE_VALUE = 0;
   private static final Internal.EnumLiteMap<NullValue> internalValueMap = new Internal.EnumLiteMap<NullValue>() {
      public NullValue findValueByNumber(int var1) {
         return NullValue.forNumber(var1);
      }
   };
   private static final NullValue[] VALUES = values();
   private final int value;

   @Override
   public final int getNumber() {
      if (this == UNRECOGNIZED) {
         throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
      } else {
         return this.value;
      }
   }

   @Deprecated
   public static NullValue valueOf(int var0) {
      return forNumber(var0);
   }

   public static NullValue forNumber(int var0) {
      switch (var0) {
         case 0:
            return NULL_VALUE;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<NullValue> internalGetValueMap() {
      return internalValueMap;
   }

   @Override
   public final Descriptors.EnumValueDescriptor getValueDescriptor() {
      return getDescriptor().getValues().get(this.ordinal());
   }

   @Override
   public final Descriptors.EnumDescriptor getDescriptorForType() {
      return getDescriptor();
   }

   public static final Descriptors.EnumDescriptor getDescriptor() {
      return StructProto.getDescriptor().getEnumTypes().get(0);
   }

   public static NullValue valueOf(Descriptors.EnumValueDescriptor var0) {
      if (var0.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return var0.getIndex() == -1 ? UNRECOGNIZED : VALUES[var0.getIndex()];
      }
   }

   private NullValue(int var3) {
      this.value = var3;
   }
}
