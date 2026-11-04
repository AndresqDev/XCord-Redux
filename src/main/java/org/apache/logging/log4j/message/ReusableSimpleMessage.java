package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
public class ReusableSimpleMessage implements CharSequence, Clearable, ParameterVisitable, ReusableMessage {
   private static final long serialVersionUID = -9199974506498249809L;
   private CharSequence charSequence;

   public void set(String var1) {
      this.charSequence = var1;
   }

   public void set(CharSequence var1) {
      this.charSequence = var1;
   }

   @Override
   public String getFormattedMessage() {
      return String.valueOf(this.charSequence);
   }

   @Override
   public String getFormat() {
      return this.charSequence instanceof String ? (String)this.charSequence : null;
   }

   @Override
   public Object[] getParameters() {
      return Constants.EMPTY_OBJECT_ARRAY;
   }

   @Override
   public Throwable getThrowable() {
      return null;
   }

   @Override
   public void formatTo(StringBuilder var1) {
      var1.append(this.charSequence);
   }

   @Override
   public Object[] swapParameters(Object[] var1) {
      return var1;
   }

   @Override
   public short getParameterCount() {
      return 0;
   }

   @Override
   public <S> void forEachParameter(ParameterConsumer<S> var1, S var2) {
   }

   @Override
   public Message memento() {
      return new SimpleMessage(this.charSequence);
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

   @Override
   public void clear() {
      this.charSequence = null;
   }
}
