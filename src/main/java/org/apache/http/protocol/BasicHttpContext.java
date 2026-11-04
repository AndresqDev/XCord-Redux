package org.apache.http.protocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE_CONDITIONAL
)
public class BasicHttpContext implements HttpContext {
   private final HttpContext parentContext;
   private final Map<String, Object> map = new ConcurrentHashMap<>();

   public BasicHttpContext() {
      this(null);
   }

   public BasicHttpContext(HttpContext var1) {
      this.parentContext = var1;
   }

   @Override
   public Object getAttribute(String var1) {
      Args.notNull(var1, "Id");
      Object var2 = this.map.get(var1);
      if (var2 == null && this.parentContext != null) {
         var2 = this.parentContext.getAttribute(var1);
      }

      return var2;
   }

   @Override
   public void setAttribute(String var1, Object var2) {
      Args.notNull(var1, "Id");
      if (var2 != null) {
         this.map.put(var1, var2);
      } else {
         this.map.remove(var1);
      }
   }

   @Override
   public Object removeAttribute(String var1) {
      Args.notNull(var1, "Id");
      return this.map.remove(var1);
   }

   public void clear() {
      this.map.clear();
   }

   @Override
   public String toString() {
      return this.map.toString();
   }
}
