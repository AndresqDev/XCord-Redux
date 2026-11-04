package org.apache.maven.model.building;

import org.apache.maven.building.StringSource;

@Deprecated
public class StringModelSource extends StringSource implements ModelSource {
   public StringModelSource(CharSequence var1) {
      this(var1, null);
   }

   public StringModelSource(CharSequence var1, String var2) {
      super(var1, var2);
   }
}
