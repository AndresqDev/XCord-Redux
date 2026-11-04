package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.error.Mark;

public class DuplicateKeyException extends ConstructorException {
   protected DuplicateKeyException(Mark var1, Object var2, Mark var3) {
      super("while constructing a mapping", var1, "found duplicate key " + String.valueOf(var2), var3);
   }
}
