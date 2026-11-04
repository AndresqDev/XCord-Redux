package com.google.gson.internal.reflect;

import java.lang.reflect.AccessibleObject;

final class PreJava9ReflectionAccessor extends ReflectionAccessor {
   @Override
   public void makeAccessible(AccessibleObject var1) {
      var1.setAccessible(true);
   }
}
