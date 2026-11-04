package org.apache.http.protocol;

@Deprecated
public class SyncBasicHttpContext extends BasicHttpContext {
   public SyncBasicHttpContext(HttpContext var1) {
      super(var1);
   }

   public SyncBasicHttpContext() {
   }

   @Override
   public synchronized Object getAttribute(String var1) {
      return super.getAttribute(var1);
   }

   @Override
   public synchronized void setAttribute(String var1, Object var2) {
      super.setAttribute(var1, var2);
   }

   @Override
   public synchronized Object removeAttribute(String var1) {
      return super.removeAttribute(var1);
   }

   @Override
   public synchronized void clear() {
      super.clear();
   }
}
