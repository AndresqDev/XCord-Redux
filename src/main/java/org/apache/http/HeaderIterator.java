package org.apache.http;

import java.util.Iterator;

public interface HeaderIterator extends Iterator<Object> {
   @Override
   boolean hasNext();

   Header nextHeader();
}
