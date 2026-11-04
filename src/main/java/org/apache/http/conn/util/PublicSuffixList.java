package org.apache.http.conn.util;

import java.util.Collections;
import java.util.List;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public final class PublicSuffixList {
   private final DomainType type;
   private final List<String> rules;
   private final List<String> exceptions;

   public PublicSuffixList(DomainType var1, List<String> var2, List<String> var3) {
      this.type = Args.notNull(var1, "Domain type");
      this.rules = Collections.unmodifiableList(Args.notNull(var2, "Domain suffix rules"));
      this.exceptions = Collections.unmodifiableList(var3 != null ? var3 : Collections.emptyList());
   }

   public PublicSuffixList(List<String> var1, List<String> var2) {
      this(DomainType.UNKNOWN, var1, var2);
   }

   public DomainType getType() {
      return this.type;
   }

   public List<String> getRules() {
      return this.rules;
   }

   public List<String> getExceptions() {
      return this.exceptions;
   }
}
