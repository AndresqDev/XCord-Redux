package org.eclipse.sisu.wire;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import java.net.MalformedURLException;
import java.net.URL;

final class URLTypeConverter extends AbstractTypeConverter<URL> {
   public Object convert(String var1, TypeLiteral<?> var2) {
      try {
         return new URL(var1);
      } catch (MalformedURLException var4) {
         throw new ProvisionException(var4.toString());
      }
   }
}
