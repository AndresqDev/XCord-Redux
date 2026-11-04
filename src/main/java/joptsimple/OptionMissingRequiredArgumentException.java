package joptsimple;

import java.util.Arrays;

class OptionMissingRequiredArgumentException extends OptionException {
   private static final long serialVersionUID = -1L;

   OptionMissingRequiredArgumentException(OptionSpec<?> var1) {
      super(Arrays.asList(var1));
   }

   @Override
   Object[] messageArguments() {
      return new Object[]{this.singleOptionString()};
   }
}
