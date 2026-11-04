package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Locale.Category;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public class MessageFormatMessage implements Message {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final long serialVersionUID = 1L;
   private static final int HASHVAL = 31;
   private String messagePattern;
   private transient Object[] parameters;
   private String[] serializedParameters;
   private transient String formattedMessage;
   private transient Throwable throwable;
   private final Locale locale;

   public MessageFormatMessage(Locale var1, String var2, Object... var3) {
      this.locale = var1;
      this.messagePattern = var2;
      this.parameters = var3;
      int var4 = var3 == null ? 0 : var3.length;
      if (var4 > 0 && var3[var4 - 1] instanceof Throwable) {
         this.throwable = (Throwable)var3[var4 - 1];
      }
   }

   public MessageFormatMessage(String var1, Object... var2) {
      this(Locale.getDefault(Category.FORMAT), var1, var2);
   }

   @Override
   public String getFormattedMessage() {
      if (this.formattedMessage == null) {
         this.formattedMessage = this.formatMessage(this.messagePattern, this.parameters);
      }

      return this.formattedMessage;
   }

   @Override
   public String getFormat() {
      return this.messagePattern;
   }

   @Override
   public Object[] getParameters() {
      return (Object[])(this.parameters != null ? this.parameters : this.serializedParameters);
   }

   protected String formatMessage(String var1, Object... var2) {
      try {
         MessageFormat var3 = new MessageFormat(var1, this.locale);
         return var3.format(var2);
      } catch (IllegalFormatException var4) {
         LOGGER.error("Unable to format msg: " + var1, (Throwable)var4);
         return var1;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         MessageFormatMessage var2 = (MessageFormatMessage)var1;
         return (this.messagePattern != null ? this.messagePattern.equals(var2.messagePattern) : var2.messagePattern == null)
            ? Arrays.equals((Object[])this.serializedParameters, (Object[])var2.serializedParameters)
            : false;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.messagePattern != null ? this.messagePattern.hashCode() : 0;
      return 31 * var1 + (this.serializedParameters != null ? Arrays.hashCode((Object[])this.serializedParameters) : 0);
   }

   @Override
   public String toString() {
      return this.getFormattedMessage();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.getFormattedMessage();
      var1.writeUTF(this.formattedMessage);
      var1.writeUTF(this.messagePattern);
      int var2 = this.parameters == null ? 0 : this.parameters.length;
      var1.writeInt(var2);
      this.serializedParameters = new String[var2];
      if (var2 > 0) {
         for (int var3 = 0; var3 < var2; var3++) {
            this.serializedParameters[var3] = String.valueOf(this.parameters[var3]);
            var1.writeUTF(this.serializedParameters[var3]);
         }
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException {
      this.parameters = null;
      this.throwable = null;
      this.formattedMessage = var1.readUTF();
      this.messagePattern = var1.readUTF();
      int var2 = var1.readInt();
      this.serializedParameters = new String[var2];

      for (int var3 = 0; var3 < var2; var3++) {
         this.serializedParameters[var3] = var1.readUTF();
      }
   }

   @Override
   public Throwable getThrowable() {
      return this.throwable;
   }
}
