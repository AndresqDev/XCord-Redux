package org.apache.logging.log4j.spi;

import java.util.Objects;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFactory2;
import org.apache.logging.log4j.message.SimpleMessage;

public class MessageFactory2Adapter implements MessageFactory2 {
   private final MessageFactory wrapped;

   public MessageFactory2Adapter(MessageFactory var1) {
      this.wrapped = Objects.requireNonNull(var1);
   }

   public MessageFactory getOriginal() {
      return this.wrapped;
   }

   @Override
   public Message newMessage(CharSequence var1) {
      return new SimpleMessage(var1);
   }

   @Override
   public Message newMessage(String var1, Object var2) {
      return this.wrapped.newMessage(var1, var2);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3) {
      return this.wrapped.newMessage(var1, var2, var3);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4) {
      return this.wrapped.newMessage(var1, var2, var3, var4);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5) {
      return this.wrapped.newMessage(var1, var2, var3, var4, var5);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      return this.wrapped.newMessage(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      return this.wrapped.newMessage(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      return this.wrapped.newMessage(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.wrapped.newMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.wrapped.newMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public Message newMessage(
      String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      return this.wrapped.newMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Override
   public Message newMessage(Object var1) {
      return this.wrapped.newMessage(var1);
   }

   @Override
   public Message newMessage(String var1) {
      return this.wrapped.newMessage(var1);
   }

   @Override
   public Message newMessage(String var1, Object... var2) {
      return this.wrapped.newMessage(var1, var2);
   }
}
