package joptsimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptionSpecBuilder extends NoArgumentOptionSpec {
   private final OptionParser parser;

   OptionSpecBuilder(OptionParser var1, List<String> var2, String var3) {
      super(var2, var3);
      this.parser = var1;
      this.attachToParser();
   }

   private void attachToParser() {
      this.parser.recognize(this);
   }

   public ArgumentAcceptingOptionSpec<String> withRequiredArg() {
      RequiredArgumentOptionSpec var1 = new RequiredArgumentOptionSpec(this.options(), this.description());
      this.parser.recognize(var1);
      return var1;
   }

   public ArgumentAcceptingOptionSpec<String> withOptionalArg() {
      OptionalArgumentOptionSpec var1 = new OptionalArgumentOptionSpec(this.options(), this.description());
      this.parser.recognize(var1);
      return var1;
   }

   public OptionSpecBuilder requiredIf(String var1, String... var2) {
      for (String var5 : this.validatedDependents(var1, var2)) {
         this.parser.requiredIf(this.options(), var5);
      }

      return this;
   }

   public OptionSpecBuilder requiredIf(OptionSpec<?> var1, OptionSpec<?>... var2) {
      this.parser.requiredIf(this.options(), var1);

      for (OptionSpec var6 : var2) {
         this.parser.requiredIf(this.options(), var6);
      }

      return this;
   }

   public OptionSpecBuilder requiredUnless(String var1, String... var2) {
      for (String var5 : this.validatedDependents(var1, var2)) {
         this.parser.requiredUnless(this.options(), var5);
      }

      return this;
   }

   public OptionSpecBuilder requiredUnless(OptionSpec<?> var1, OptionSpec<?>... var2) {
      this.parser.requiredUnless(this.options(), var1);

      for (OptionSpec var6 : var2) {
         this.parser.requiredUnless(this.options(), var6);
      }

      return this;
   }

   public OptionSpecBuilder availableIf(String var1, String... var2) {
      for (String var5 : this.validatedDependents(var1, var2)) {
         this.parser.availableIf(this.options(), var5);
      }

      return this;
   }

   public OptionSpecBuilder availableIf(OptionSpec<?> var1, OptionSpec<?>... var2) {
      this.parser.availableIf(this.options(), var1);

      for (OptionSpec var6 : var2) {
         this.parser.availableIf(this.options(), var6);
      }

      return this;
   }

   public OptionSpecBuilder availableUnless(String var1, String... var2) {
      for (String var5 : this.validatedDependents(var1, var2)) {
         this.parser.availableUnless(this.options(), var5);
      }

      return this;
   }

   public OptionSpecBuilder availableUnless(OptionSpec<?> var1, OptionSpec<?>... var2) {
      this.parser.availableUnless(this.options(), var1);

      for (OptionSpec var6 : var2) {
         this.parser.availableUnless(this.options(), var6);
      }

      return this;
   }

   private List<String> validatedDependents(String var1, String... var2) {
      ArrayList var3 = new ArrayList();
      var3.add(var1);
      Collections.addAll(var3, var2);

      for (String var5 : var3) {
         if (!this.parser.isRecognized(var5)) {
            throw new UnconfiguredOptionException(var5);
         }
      }

      return var3;
   }
}
