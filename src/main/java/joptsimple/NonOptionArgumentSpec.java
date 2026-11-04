package joptsimple;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import joptsimple.internal.Reflection;

public class NonOptionArgumentSpec<V> extends AbstractOptionSpec<V> {
   static final String NAME = "[arguments]";
   private ValueConverter<V> converter;
   private String argumentDescription = "";

   NonOptionArgumentSpec() {
      this("");
   }

   NonOptionArgumentSpec(String var1) {
      super(Arrays.asList("[arguments]"), var1);
   }

   public <T> NonOptionArgumentSpec<T> ofType(Class<T> var1) {
      this.converter = Reflection.findConverter(var1);
      return (NonOptionArgumentSpec<T>)this;
   }

   public final <T> NonOptionArgumentSpec<T> withValuesConvertedBy(ValueConverter<T> var1) {
      if (var1 == null) {
         throw new NullPointerException("illegal null converter");
      } else {
         this.converter = var1;
         return (NonOptionArgumentSpec<T>)this;
      }
   }

   public NonOptionArgumentSpec<V> describedAs(String var1) {
      this.argumentDescription = var1;
      return this;
   }

   @Override
   protected final V convert(String var1) {
      return this.convertWith(this.converter, var1);
   }

   @Override
   void handleOption(OptionParser var1, ArgumentList var2, OptionSet var3, String var4) {
      var3.addWithArgument(this, var4);
   }

   @Override
   public List<?> defaultValues() {
      return Collections.emptyList();
   }

   @Override
   public boolean isRequired() {
      return false;
   }

   @Override
   public boolean acceptsArguments() {
      return false;
   }

   @Override
   public boolean requiresArgument() {
      return false;
   }

   @Override
   public String argumentDescription() {
      return this.argumentDescription;
   }

   @Override
   public String argumentTypeIndicator() {
      return this.argumentTypeIndicatorFrom(this.converter);
   }

   @Override
   public boolean representsNonOptions() {
      return true;
   }
}
