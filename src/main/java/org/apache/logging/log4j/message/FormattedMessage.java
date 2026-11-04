package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.regex.Pattern;

public class FormattedMessage implements Message {
   private static final long serialVersionUID = -665975803997290697L;
   private static final int HASHVAL = 31;
   private static final String FORMAT_SPECIFIER = "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";
   private static final Pattern MSG_PATTERN = Pattern.compile("%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])");
   private String messagePattern;
   private transient Object[] argArray;
   private String[] stringArgs;
   private transient String formattedMessage;
   private final Throwable throwable;
   private Message message;
   private final Locale locale;

   public FormattedMessage(Locale var1, String var2, Object var3) {
      this(var1, var2, new Object[]{var3}, null);
   }

   public FormattedMessage(Locale var1, String var2, Object var3, Object var4) {
      this(var1, var2, var3, var4);
   }

   public FormattedMessage(Locale var1, String var2, Object... var3) {
      this(var1, var2, var3, null);
   }

   public FormattedMessage(Locale var1, String var2, Object[] var3, Throwable var4) {
      this.locale = var1;
      this.messagePattern = var2;
      this.argArray = var3;
      this.throwable = var4;
   }

   public FormattedMessage(String var1, Object var2) {
      this(var1, new Object[]{var2}, null);
   }

   public FormattedMessage(String var1, Object var2, Object var3) {
      this(var1, var2, var3);
   }

   public FormattedMessage(String var1, Object... var2) {
      this(var1, var2, null);
   }

   public FormattedMessage(String var1, Object[] var2, Throwable var3) {
      this.locale = Locale.getDefault(Category.FORMAT);
      this.messagePattern = var1;
      this.argArray = var2;
      this.throwable = var3;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         FormattedMessage var2 = (FormattedMessage)var1;
         return (this.messagePattern != null ? this.messagePattern.equals(var2.messagePattern) : var2.messagePattern == null)
            ? Arrays.equals((Object[])this.stringArgs, (Object[])var2.stringArgs)
            : false;
      } else {
         return false;
      }
   }

   @Override
   public String getFormat() {
      return this.messagePattern;
   }

   @Override
   public String getFormattedMessage() {
      if (this.formattedMessage == null) {
         if (this.message == null) {
            this.message = this.getMessage(this.messagePattern, this.argArray, this.throwable);
         }

         this.formattedMessage = this.message.getFormattedMessage();
      }

      return this.formattedMessage;
   }

   protected Message getMessage(String var1, Object[] var2, Throwable var3) {
      try {
         MessageFormat var4 = new MessageFormat(var1);
         Format[] var5 = var4.getFormats();
         if (var5 != null && var5.length > 0) {
            return new MessageFormatMessage(this.locale, var1, var2);
         }
      } catch (Exception var7) {
      }

      try {
         if (MSG_PATTERN.matcher(var1).find()) {
            return new StringFormattedMessage(this.locale, var1, var2);
         }
      } catch (Exception var6) {
      }

      return new ParameterizedMessage(var1, var2, var3);
   }

   @Override
   public Object[] getParameters() {
      return (Object[])(this.argArray != null ? this.argArray : this.stringArgs);
   }

   @Override
   public Throwable getThrowable() {
      if (this.throwable != null) {
         return this.throwable;
      } else {
         if (this.message == null) {
            this.message = this.getMessage(this.messagePattern, this.argArray, null);
         }

         return this.message.getThrowable();
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.messagePattern != null ? this.messagePattern.hashCode() : 0;
      return 31 * var1 + (this.stringArgs != null ? Arrays.hashCode((Object[])this.stringArgs) : 0);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.formattedMessage = var1.readUTF();
      this.messagePattern = var1.readUTF();
      int var2 = var1.readInt();
      this.stringArgs = new String[var2];

      for (int var3 = 0; var3 < var2; var3++) {
         this.stringArgs[var3] = var1.readUTF();
      }
   }

   @Override
   public String toString() {
      return this.getFormattedMessage();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      this.getFormattedMessage();
      var1.writeUTF(this.formattedMessage);
      var1.writeUTF(this.messagePattern);
      var1.writeInt(this.argArray.length);
      this.stringArgs = new String[this.argArray.length];
      int var2 = 0;

      for (Object var6 : this.argArray) {
         String var7 = String.valueOf(var6);
         this.stringArgs[var2] = var7;
         var1.writeUTF(var7);
         var2++;
      }
   }
}
