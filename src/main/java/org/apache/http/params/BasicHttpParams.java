package org.apache.http.params;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Deprecated
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class BasicHttpParams extends AbstractHttpParams implements Serializable, Cloneable {
   private static final long serialVersionUID = -7086398485908701455L;
   private final Map<String, Object> parameters = new ConcurrentHashMap<>();

   @Override
   public Object getParameter(String var1) {
      return this.parameters.get(var1);
   }

   @Override
   public HttpParams setParameter(String var1, Object var2) {
      if (var1 == null) {
         return this;
      } else {
         if (var2 != null) {
            this.parameters.put(var1, var2);
         } else {
            this.parameters.remove(var1);
         }

         return this;
      }
   }

   @Override
   public boolean removeParameter(String var1) {
      if (this.parameters.containsKey(var1)) {
         this.parameters.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   public void setParameters(String[] var1, Object var2) {
      for (String var6 : var1) {
         this.setParameter(var6, var2);
      }
   }

   public boolean isParameterSet(String var1) {
      return this.getParameter(var1) != null;
   }

   public boolean isParameterSetLocally(String var1) {
      return this.parameters.get(var1) != null;
   }

   public void clear() {
      this.parameters.clear();
   }

   @Override
   public HttpParams copy() {
      try {
         return (HttpParams)this.clone();
      } catch (CloneNotSupportedException var2) {
         throw new UnsupportedOperationException("Cloning not supported");
      }
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      BasicHttpParams var1 = (BasicHttpParams)super.clone();
      this.copyParams(var1);
      return var1;
   }

   public void copyParams(HttpParams var1) {
      for (Entry var3 : this.parameters.entrySet()) {
         var1.setParameter((String)var3.getKey(), var3.getValue());
      }
   }

   @Override
   public Set<String> getNames() {
      return new HashSet<>(this.parameters.keySet());
   }

   @Override
   public String toString() {
      return "[parameters=" + this.parameters + "]";
   }
}
