package org.eclipse.sisu.wire;

import com.google.inject.TypeLiteral;
import java.io.File;

final class FileTypeConverter extends AbstractTypeConverter<File> {
   public Object convert(String var1, TypeLiteral<?> var2) {
      return new File(var1);
   }
}
