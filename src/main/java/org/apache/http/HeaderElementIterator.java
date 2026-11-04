package org.apache.http;

import java.util.Iterator;

public interface HeaderElementIterator extends Iterator<Object> {
   @Override
   boolean hasNext();

   HeaderElement nextElement();
}
