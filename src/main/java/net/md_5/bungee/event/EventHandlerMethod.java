package net.md_5.bungee.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventHandlerMethod {
   private final Object listener;
   private final Method method;

   public void invoke(Object var1) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.method.invoke(this.listener, var1);
   }

   public EventHandlerMethod(Object var1, Method var2) {
      this.listener = var1;
      this.method = var2;
   }

   public Object getListener() {
      return this.listener;
   }

   public Method getMethod() {
      return this.method;
   }
}
