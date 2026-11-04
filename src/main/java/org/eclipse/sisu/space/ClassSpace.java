package org.eclipse.sisu.space;

import java.net.URL;
import java.util.Enumeration;
import org.eclipse.sisu.inject.DeferredClass;

public interface ClassSpace {
   Class<?> loadClass(String var1) throws TypeNotPresentException;

   DeferredClass<?> deferLoadClass(String var1);

   URL getResource(String var1);

   Enumeration<URL> getResources(String var1);

   Enumeration<URL> findEntries(String var1, String var2, boolean var3);
}
