package joptsimple.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public class PathConverter implements ValueConverter<Path> {
   private final PathProperties[] pathProperties;

   public PathConverter(PathProperties... var1) {
      this.pathProperties = var1;
   }

   public Path convert(String var1) {
      Path var2 = Paths.get(var1);
      if (this.pathProperties != null) {
         for (PathProperties var6 : this.pathProperties) {
            if (!var6.accept(var2)) {
               throw new ValueConversionException(this.message(var6.getMessageKey(), var2.toString()));
            }
         }
      }

      return var2;
   }

   @Override
   public Class<Path> valueType() {
      return Path.class;
   }

   @Override
   public String valuePattern() {
      return null;
   }

   private String message(String var1, String var2) {
      ResourceBundle var3 = ResourceBundle.getBundle("joptsimple.ExceptionMessages");
      Object[] var4 = new Object[]{var2, this.valuePattern()};
      String var5 = var3.getString(PathConverter.class.getName() + "." + var1 + ".message");
      return new MessageFormat(var5).format(var4);
   }
}
