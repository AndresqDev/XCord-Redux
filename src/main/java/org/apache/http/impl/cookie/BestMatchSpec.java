package org.apache.http.impl.cookie;

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Deprecated
@Contract(
   threading = ThreadingBehavior.SAFE
)
public class BestMatchSpec extends DefaultCookieSpec {
   public BestMatchSpec(String[] var1, boolean var2) {
      super(var1, var2);
   }

   public BestMatchSpec() {
      this(null, false);
   }

   @Override
   public String toString() {
      return "best-match";
   }
}
