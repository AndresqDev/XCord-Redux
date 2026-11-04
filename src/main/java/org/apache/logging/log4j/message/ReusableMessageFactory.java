package org.apache.logging.log4j.message;

import java.io.Serializable;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
public final class ReusableMessageFactory implements Serializable, MessageFactory2 {
   public static final ReusableMessageFactory INSTANCE = new ReusableMessageFactory();
   private static final long serialVersionUID = -8970940216592525651L;
   private static ThreadLocal<ReusableParameterizedMessage> threadLocalParameterized = new ThreadLocal<>();
   private static ThreadLocal<ReusableSimpleMessage> threadLocalSimpleMessage = new ThreadLocal<>();
   private static ThreadLocal<ReusableObjectMessage> threadLocalObjectMessage = new ThreadLocal<>();

   private static ReusableParameterizedMessage getParameterized() {
      ReusableParameterizedMessage var0 = threadLocalParameterized.get();
      if (var0 == null) {
         var0 = new ReusableParameterizedMessage();
         threadLocalParameterized.set(var0);
      }

      return var0.reserved ? new ReusableParameterizedMessage().reserve() : var0.reserve();
   }

   private static ReusableSimpleMessage getSimple() {
      ReusableSimpleMessage var0 = threadLocalSimpleMessage.get();
      if (var0 == null) {
         var0 = new ReusableSimpleMessage();
         threadLocalSimpleMessage.set(var0);
      }

      return var0;
   }

   private static ReusableObjectMessage getObject() {
      ReusableObjectMessage var0 = threadLocalObjectMessage.get();
      if (var0 == null) {
         var0 = new ReusableObjectMessage();
         threadLocalObjectMessage.set(var0);
      }

      return var0;
   }

   public static void release(Message var0) {
      if (var0 instanceof Clearable) {
         ((Clearable)var0).clear();
      }
   }

   @Override
   public Message newMessage(CharSequence var1) {
      ReusableSimpleMessage var2 = getSimple();
      var2.set(var1);
      return var2;
   }

   @Override
   public Message newMessage(String var1, Object... var2) {
      return getParameterized().set(var1, var2);
   }

   @Override
   public Message newMessage(String var1, Object var2) {
      return getParameterized().set(var1, var2);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3) {
      return getParameterized().set(var1, var2, var3);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4) {
      return getParameterized().set(var1, var2, var3, var4);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5) {
      return getParameterized().set(var1, var2, var3, var4, var5);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      return getParameterized().set(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      return getParameterized().set(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      return getParameterized().set(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return getParameterized().set(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return getParameterized().set(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public Message newMessage(
      String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      return getParameterized().set(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Override
   public Message newMessage(String var1) {
      ReusableSimpleMessage var2 = getSimple();
      var2.set(var1);
      return var2;
   }

   @Override
   public Message newMessage(Object var1) {
      ReusableObjectMessage var2 = getObject();
      var2.set(var1);
      return var2;
   }
}
