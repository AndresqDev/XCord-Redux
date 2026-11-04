package org.apache.http;

import java.util.Iterator;

public interface TokenIterator extends Iterator<Object> {
   @Override
   boolean hasNext();

   String nextToken();
}
