package org.apache.logging.log4j.core.appender.nosql;

import java.io.Closeable;

public interface NoSqlConnection<W, T extends NoSqlObject<W>> extends Closeable {
   T createObject();

   T[] createList(int var1);

   void insertObject(NoSqlObject<W> var1);

   @Override
   void close();

   boolean isClosed();
}
