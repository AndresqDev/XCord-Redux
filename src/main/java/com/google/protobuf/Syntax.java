package com.google.protobuf;

public enum Syntax implements ProtocolMessageEnum {
   SYNTAX_PROTO2(0),
   SYNTAX_PROTO3(1),
   UNRECOGNIZED(-1);

   public static final int SYNTAX_PROTO2_VALUE = 0;
   public static final int SYNTAX_PROTO3_VALUE = 1;
   private static final Internal.EnumLiteMap<Syntax> internalValueMap = new Internal.EnumLiteMap<Syntax>() {
      public Syntax findValueByNumber(int var1) {
         return Syntax.forNumber(var1);
      }
   };
   private static final Syntax[] VALUES = values();
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
   public static Syntax valueOf(int var0) {
      return forNumber(var0);
   }

   public static Syntax forNumber(int var0) {
      switch (var0) {
         case 0:
            return SYNTAX_PROTO2;
         case 1:
            return SYNTAX_PROTO3;
         default:
            return null;
      }
   }

   public static Internal.EnumLiteMap<Syntax> internalGetValueMap() {
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
      return TypeProto.getDescriptor().getEnumTypes().get(0);
   }

   public static Syntax valueOf(Descriptors.EnumValueDescriptor var0) {
      if (var0.getType() != getDescriptor()) {
         throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
      } else {
         return var0.getIndex() == -1 ? UNRECOGNIZED : VALUES[var0.getIndex()];
      }
   }

   private Syntax(int var3) {
      this.value = var3;
   }
}
