package org.eclipse.aether.spi.locator;

import java.util.List;

public interface ServiceLocator {
   <T> T getService(Class<T> var1);

   <T> List<T> getServices(Class<T> var1);
}
