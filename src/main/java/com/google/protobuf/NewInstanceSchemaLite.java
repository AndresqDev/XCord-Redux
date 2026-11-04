package com.google.protobuf;

final class NewInstanceSchemaLite implements NewInstanceSchema {
   @Override
   public Object newInstance(Object var1) {
      return ((GeneratedMessageLite)var1).dynamicMethod(GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE);
   }
}
