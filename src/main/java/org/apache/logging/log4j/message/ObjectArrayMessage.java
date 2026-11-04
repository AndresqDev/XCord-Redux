package org.apache.logging.log4j.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import org.apache.logging.log4j.util.Constants;

public final class ObjectArrayMessage implements Message {
   private static final long serialVersionUID = -5903272448334166185L;
   private transient Object[] array;
   private transient String arrayString;

   public ObjectArrayMessage(Object... var1) {
      this.array = var1 == null ? Constants.EMPTY_OBJECT_ARRAY : var1;
   }

   private boolean equalObjectsOrStrings(Object[] var1, Object[] var2) {
      return Arrays.equals(var1, var2) || Arrays.toString(var1).equals(Arrays.toString(var2));
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ObjectArrayMessage var2 = (ObjectArrayMessage)var1;
         return this.array == null ? var2.array == null : this.equalObjectsOrStrings(this.array, var2.array);
      } else {
         return false;
      }
   }

   @Override
   public String getFormat() {
      return this.getFormattedMessage();
   }

   @Override
   public String getFormattedMessage() {
      if (this.arrayString == null) {
         this.arrayString = Arrays.toString(this.array);
      }

      return this.arrayString;
   }

   @Override
   public Object[] getParameters() {
      return this.array;
   }

   @Override
   public Throwable getThrowable() {
      return null;
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.array);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.array = (Object[])var1.readObject();
   }

   @Override
   public String toString() {
      return this.getFormattedMessage();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.array);
   }
}
