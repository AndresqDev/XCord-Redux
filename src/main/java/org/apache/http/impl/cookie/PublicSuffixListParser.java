package org.apache.http.impl.cookie;

import java.io.IOException;
import java.io.Reader;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.util.PublicSuffixList;

@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class PublicSuffixListParser {
   private final PublicSuffixFilter filter;
   private final org.apache.http.conn.util.PublicSuffixListParser parser;

   PublicSuffixListParser(PublicSuffixFilter var1) {
      this.filter = var1;
      this.parser = new org.apache.http.conn.util.PublicSuffixListParser();
   }

   public void parse(Reader var1) throws IOException {
      PublicSuffixList var2 = this.parser.parse(var1);
      this.filter.setPublicSuffixes(var2.getRules());
      this.filter.setExceptions(var2.getExceptions());
   }
}
