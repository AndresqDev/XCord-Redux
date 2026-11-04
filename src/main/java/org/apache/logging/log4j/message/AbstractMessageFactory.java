package org.apache.logging.log4j.message;

import java.io.Serializable;

public abstract class AbstractMessageFactory implements Serializable, MessageFactory2 {
   private static final long serialVersionUID = -1307891137684031187L;

   @Override
   public Message newMessage(CharSequence var1) {
      return new SimpleMessage(var1);
   }

   @Override
   public Message newMessage(Object var1) {
      return new ObjectMessage(var1);
   }

   @Override
   public Message newMessage(String var1) {
      return new SimpleMessage(var1);
   }

   @Override
   public Message newMessage(String var1, Object var2) {
      return this.newMessage(var1, new Object[]{var2});
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3) {
      return this.newMessage(var1, new Object[]{var2, var3});
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4) {
      return this.newMessage(var1, new Object[]{var2, var3, var4});
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5) {
      return this.newMessage(var1, new Object[]{var2, var3, var4, var5});
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      return this.newMessage(var1, new Object[]{var2, var3, var4, var5, var6});
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      return this.newMessage(var1, new Object[]{var2, var3, var4, var5, var6, var7});
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      return this.newMessage(var1, new Object[]{var2, var3, var4, var5, var6, var7, var8});
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.newMessage(var1, new Object[]{var2, var3, var4, var5, var6, var7, var8, var9});
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.newMessage(var1, new Object[]{var2, var3, var4, var5, var6, var7, var8, var9, var10});
   }

   @Override
   public Message newMessage(
      String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      return this.newMessage(var1, new Object[]{var2, var3, var4, var5, var6, var7, var8, var9, var10, var11});
   }
}
