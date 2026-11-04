package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.logging.log4j.util.StringBuilderFormattable;

public class SimpleMessage implements CharSequence, Message, StringBuilderFormattable {
   private static final long serialVersionUID = -8398002534962715992L;
   private String message;
   private transient CharSequence charSequence;

   public SimpleMessage() {
      this(null);
   }

   public SimpleMessage(String var1) {
      this.message = var1;
      this.charSequence = var1;
   }

   public SimpleMessage(CharSequence var1) {
      this.charSequence = var1;
   }

   @Override
   public String getFormattedMessage() {
      return this.message = this.message == null ? String.valueOf(this.charSequence) : this.message;
   }

   @Override
   public void formatTo(StringBuilder var1) {
      var1.append((CharSequence)(this.message != null ? this.message : this.charSequence));
   }

   @Override
   public String getFormat() {
      return this.message;
   }

   @Override
   public Object[] getParameters() {
      return null;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         SimpleMessage var2 = (SimpleMessage)var1;
         return this.charSequence != null ? this.charSequence.equals(var2.charSequence) : var2.charSequence == null;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.charSequence != null ? this.charSequence.hashCode() : 0;
   }

   @Override
   public String toString() {
      return this.getFormattedMessage();
   }

   @Override
   public Throwable getThrowable() {
      return null;
   }

   @Override
   public int length() {
      return this.charSequence == null ? 0 : this.charSequence.length();
   }

   @Override
   public char charAt(int var1) {
      return this.charSequence.charAt(var1);
   }

   @Override
   public CharSequence subSequence(int var1, int var2) {
      return this.charSequence.subSequence(var1, var2);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.getFormattedMessage();
      var1.defaultWriteObject();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.charSequence = this.message;
   }
}
