package joptsimple;

import java.util.Collections;

class MultipleArgumentsForOptionException extends OptionException {
   private static final long serialVersionUID = -1L;

   MultipleArgumentsForOptionException(OptionSpec<?> var1) {
      super(Collections.singleton(var1));
   }

   @Override
   Object[] messageArguments() {
      return new Object[]{this.singleOptionString()};
   }
}
