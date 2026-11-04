package org.apache.logging.log4j.core.appender.nosql;

public interface NoSqlObject<W> {
   void set(String var1, Object var2);

   void set(String var1, NoSqlObject<W> var2);

   void set(String var1, Object[] var2);

   void set(String var1, NoSqlObject<W>[] var2);

   W unwrap();
}
