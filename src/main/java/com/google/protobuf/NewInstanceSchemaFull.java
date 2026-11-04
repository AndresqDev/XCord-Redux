package com.google.protobuf;

final class NewInstanceSchemaFull implements NewInstanceSchema {
   @Override
   public Object newInstance(Object var1) {
      return ((GeneratedMessageV3)var1).newInstance(GeneratedMessageV3.UnusedPrivateParameter.INSTANCE);
   }
}
