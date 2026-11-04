package org.apache.http.impl.cookie;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public abstract class AbstractCookieSpec implements CookieSpec {
   private final Map<String, CookieAttributeHandler> attribHandlerMap;

   public AbstractCookieSpec() {
      this.attribHandlerMap = new ConcurrentHashMap<>(10);
   }

   protected AbstractCookieSpec(HashMap<String, CookieAttributeHandler> var1) {
      Asserts.notNull(var1, "Attribute handler map");
      this.attribHandlerMap = new ConcurrentHashMap<>(var1);
   }

   protected AbstractCookieSpec(CommonCookieAttributeHandler... var1) {
      this.attribHandlerMap = new ConcurrentHashMap<>(var1.length);

      for (CommonCookieAttributeHandler var5 : var1) {
         this.attribHandlerMap.put(var5.getAttributeName(), var5);
      }
   }

   @Deprecated
   public void registerAttribHandler(String var1, CookieAttributeHandler var2) {
      Args.notNull(var1, "Attribute name");
      Args.notNull(var2, "Attribute handler");
      this.attribHandlerMap.put(var1, var2);
   }

   protected CookieAttributeHandler findAttribHandler(String var1) {
      return this.attribHandlerMap.get(var1);
   }

   protected CookieAttributeHandler getAttribHandler(String var1) {
      CookieAttributeHandler var2 = this.findAttribHandler(var1);
      Asserts.check(var2 != null, "Handler not registered for " + var1 + " attribute");
      return var2;
   }

   protected Collection<CookieAttributeHandler> getAttribHandlers() {
      return this.attribHandlerMap.values();
   }
}
