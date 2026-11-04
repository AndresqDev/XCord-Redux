package joptsimple;

import java.util.List;

class UnavailableOptionException extends OptionException {
   private static final long serialVersionUID = -1L;

   UnavailableOptionException(List<? extends OptionSpec<?>> var1) {
      super(var1);
   }

   @Override
   Object[] messageArguments() {
      return new Object[]{this.multipleOptionString()};
   }
}
