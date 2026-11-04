package org.apache.http.params;

import java.util.Set;

@Deprecated
public abstract class AbstractHttpParams implements HttpParams, HttpParamsNames {
   protected AbstractHttpParams() {
   }

   @Override
   public long getLongParameter(String var1, long var2) {
      Object var4 = this.getParameter(var1);
      return var4 == null ? var2 : (Long)var4;
   }

   @Override
   public HttpParams setLongParameter(String var1, long var2) {
      this.setParameter(var1, Long.valueOf(var2));
      return this;
   }

   @Override
   public int getIntParameter(String var1, int var2) {
      Object var3 = this.getParameter(var1);
      return var3 == null ? var2 : (Integer)var3;
   }

   @Override
   public HttpParams setIntParameter(String var1, int var2) {
      this.setParameter(var1, Integer.valueOf(var2));
      return this;
   }

   @Override
   public double getDoubleParameter(String var1, double var2) {
      Object var4 = this.getParameter(var1);
      return var4 == null ? var2 : (Double)var4;
   }

   @Override
   public HttpParams setDoubleParameter(String var1, double var2) {
      this.setParameter(var1, Double.valueOf(var2));
      return this;
   }

   @Override
   public boolean getBooleanParameter(String var1, boolean var2) {
      Object var3 = this.getParameter(var1);
      return var3 == null ? var2 : (Boolean)var3;
   }

   @Override
   public HttpParams setBooleanParameter(String var1, boolean var2) {
      this.setParameter(var1, var2 ? Boolean.TRUE : Boolean.FALSE);
      return this;
   }

   @Override
   public boolean isParameterTrue(String var1) {
      return this.getBooleanParameter(var1, false);
   }

   @Override
   public boolean isParameterFalse(String var1) {
      return !this.getBooleanParameter(var1, false);
   }

   @Override
   public Set<String> getNames() {
      throw new UnsupportedOperationException();
   }
}
