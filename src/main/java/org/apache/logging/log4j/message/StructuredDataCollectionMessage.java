package org.apache.logging.log4j.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.util.StringBuilderFormattable;

public class StructuredDataCollectionMessage implements MessageCollectionMessage<StructuredDataMessage>, StringBuilderFormattable {
   private static final long serialVersionUID = 5725337076388822924L;
   private final List<StructuredDataMessage> structuredDataMessageList;

   public StructuredDataCollectionMessage(List<StructuredDataMessage> var1) {
      this.structuredDataMessageList = var1;
   }

   @Override
   public Iterator<StructuredDataMessage> iterator() {
      return this.structuredDataMessageList.iterator();
   }

   @Override
   public String getFormattedMessage() {
      StringBuilder var1 = new StringBuilder();
      this.formatTo(var1);
      return var1.toString();
   }

   @Override
   public String getFormat() {
      StringBuilder var1 = new StringBuilder();

      for (StructuredDataMessage var3 : this.structuredDataMessageList) {
         if (var3.getFormat() != null) {
            if (var1.length() > 0) {
               var1.append(", ");
            }

            var1.append(var3.getFormat());
         }
      }

      return var1.toString();
   }

   @Override
   public void formatTo(StringBuilder var1) {
      for (StructuredDataMessage var3 : this.structuredDataMessageList) {
         var3.formatTo(var1);
      }
   }

   @Override
   public Object[] getParameters() {
      ArrayList var1 = new ArrayList();
      int var2 = 0;

      for (StructuredDataMessage var4 : this.structuredDataMessageList) {
         Object[] var5 = var4.getParameters();
         if (var5 != null) {
            var1.add(var5);
            var2 += var5.length;
         }
      }

      Object[] var11 = new Object[var2];
      int var12 = 0;

      for (Object[] var6 : var1) {
         for (Object var10 : var6) {
            var11[var12++] = var10;
         }
      }

      return var11;
   }

   @Override
   public Throwable getThrowable() {
      for (StructuredDataMessage var2 : this.structuredDataMessageList) {
         Throwable var3 = var2.getThrowable();
         if (var3 != null) {
            return var3;
         }
      }

      return null;
   }
}
