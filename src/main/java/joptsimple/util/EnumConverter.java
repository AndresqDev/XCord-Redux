package joptsimple.util;

import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public abstract class EnumConverter<E extends Enum<E>> implements ValueConverter<E> {
   private final Class<E> clazz;
   private String delimiters = "[,]";

   protected EnumConverter(Class<E> var1) {
      this.clazz = var1;
   }

   public E convert(String var1) {
      for (Enum var5 : (Enum[])this.valueType().getEnumConstants()) {
         if (var5.name().equalsIgnoreCase(var1)) {
            return (E)var5;
         }
      }

      throw new ValueConversionException(this.message(var1));
   }

   @Override
   public Class<E> valueType() {
      return this.clazz;
   }

   public void setDelimiters(String var1) {
      this.delimiters = var1;
   }

   @Override
   public String valuePattern() {
      EnumSet var1 = EnumSet.allOf(this.valueType());
      StringBuilder var2 = new StringBuilder();
      var2.append(this.delimiters.charAt(0));
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         var2.append(((Enum)var3.next()).toString());
         if (var3.hasNext()) {
            var2.append(this.delimiters.charAt(1));
         }
      }

      var2.append(this.delimiters.charAt(2));
      return var2.toString();
   }

   private String message(String var1) {
      ResourceBundle var2 = ResourceBundle.getBundle("joptsimple.ExceptionMessages");
      Object[] var3 = new Object[]{var1, this.valuePattern()};
      String var4 = var2.getString(EnumConverter.class.getName() + ".message");
      return new MessageFormat(var4).format(var3);
   }
}
