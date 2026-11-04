package org.apache.http.params;

import java.util.HashSet;
import java.util.Set;
import org.apache.http.util.Args;

@Deprecated
public final class DefaultedHttpParams extends AbstractHttpParams {
   private final HttpParams local;
   private final HttpParams defaults;

   public DefaultedHttpParams(HttpParams var1, HttpParams var2) {
      this.local = Args.notNull(var1, "Local HTTP parameters");
      this.defaults = var2;
   }

   @Override
   public HttpParams copy() {
      HttpParams var1 = this.local.copy();
      return new DefaultedHttpParams(var1, this.defaults);
   }

   @Override
   public Object getParameter(String var1) {
      Object var2 = this.local.getParameter(var1);
      if (var2 == null && this.defaults != null) {
         var2 = this.defaults.getParameter(var1);
      }

      return var2;
   }

   @Override
   public boolean removeParameter(String var1) {
      return this.local.removeParameter(var1);
   }

   @Override
   public HttpParams setParameter(String var1, Object var2) {
      return this.local.setParameter(var1, var2);
   }

   public HttpParams getDefaults() {
      return this.defaults;
   }

   @Override
   public Set<String> getNames() {
      HashSet var1 = new HashSet<>(this.getNames(this.defaults));
      var1.addAll(this.getNames(this.local));
      return var1;
   }

   public Set<String> getDefaultNames() {
      return new HashSet<>(this.getNames(this.defaults));
   }

   public Set<String> getLocalNames() {
      return new HashSet<>(this.getNames(this.local));
   }

   private Set<String> getNames(HttpParams var1) {
      if (var1 instanceof HttpParamsNames) {
         return ((HttpParamsNames)var1).getNames();
      } else {
         throw new UnsupportedOperationException("HttpParams instance does not implement HttpParamsNames");
      }
   }
}
