package org.apache.logging.log4j.core.layout;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.message.Message;

@Plugin(
   name = "MessageLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public class MessageLayout extends AbstractLayout<Message> {
   public MessageLayout() {
      super(null, null, null);
   }

   public MessageLayout(Configuration var1, byte[] var2, byte[] var3) {
      super(var1, var2, var3);
   }

   @Override
   public byte[] toByteArray(LogEvent var1) {
      return null;
   }

   public Message toSerializable(LogEvent var1) {
      return var1.getMessage();
   }

   @Override
   public String getContentType() {
      return null;
   }

   @PluginFactory
   public static Layout<?> createLayout() {
      return new MessageLayout();
   }
}
