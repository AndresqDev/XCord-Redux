package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilders;

@PerformanceSensitive({"allocation"})
public class ReusableObjectMessage implements Clearable, ParameterVisitable, ReusableMessage {
   private static final long serialVersionUID = 6922476812535519960L;
   private transient Object obj;

   public void set(Object var1) {
      this.obj = var1;
   }

   @Override
   public String getFormattedMessage() {
      return String.valueOf(this.obj);
   }

   @Override
   public void formatTo(StringBuilder var1) {
      StringBuilders.appendValue(var1, this.obj);
   }

   @Override
   public String getFormat() {
      return this.obj instanceof String ? (String)this.obj : null;
   }

   public Object getParameter() {
      return this.obj;
   }

   @Override
   public Object[] getParameters() {
      return new Object[]{this.obj};
   }

   @Override
   public String toString() {
      return this.getFormattedMessage();
   }

   @Override
   public Throwable getThrowable() {
      return this.obj instanceof Throwable ? (Throwable)this.obj : null;
   }

   @Override
   public Object[] swapParameters(Object[] var1) {
      if (var1.length == 0) {
         Object[] var2 = new Object[10];
         var2[0] = this.obj;
         return var2;
      } else {
         var1[0] = this.obj;
         return var1;
      }
   }

   @Override
   public short getParameterCount() {
      return 1;
   }

   @Override
   public <S> void forEachParameter(ParameterConsumer<S> var1, S var2) {
      var1.accept(this.obj, 0, var2);
   }

   @Override
   public Message memento() {
      return new ObjectMessage(this.obj);
   }

   @Override
   public void clear() {
      this.obj = null;
   }
}
