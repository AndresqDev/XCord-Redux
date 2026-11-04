package org.codehaus.plexus.interpolation;

public abstract class AbstractFunctionValueSourceWrapper implements ValueSource {
   private final ValueSource valueSource;

   protected AbstractFunctionValueSourceWrapper(ValueSource var1) {
      this.valueSource = var1;
   }

   @Override
   public Object getValue(String var1) {
      Object var2 = this.valueSource.getValue(var1);
      String var3 = var1;
      if (this.valueSource instanceof QueryEnabledValueSource) {
         var3 = ((QueryEnabledValueSource)this.valueSource).getLastExpression();
      }

      return this.executeFunction(var3, var2);
   }

   protected ValueSource getValueSource() {
      return this.valueSource;
   }

   protected abstract Object executeFunction(String var1, Object var2);
}
