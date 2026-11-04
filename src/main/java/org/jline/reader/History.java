package org.jline.reader;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Iterator;
import java.util.ListIterator;

public interface History extends Iterable<History.Entry> {
   void attach(LineReader var1);

   void load() throws IOException;

   void save() throws IOException;

   void write(Path var1, boolean var2) throws IOException;

   void append(Path var1, boolean var2) throws IOException;

   void read(Path var1, boolean var2) throws IOException;

   void purge() throws IOException;

   int size();

   default boolean isEmpty() {
      return this.size() == 0;
   }

   int index();

   int first();

   int last();

   String get(int var1);

   default void add(String var1) {
      this.add(Instant.now(), var1);
   }

   void add(Instant var1, String var2);

   default boolean isPersistable(History.Entry var1) {
      return true;
   }

   ListIterator<History.Entry> iterator(int var1);

   default ListIterator<History.Entry> iterator() {
      return this.iterator(this.first());
   }

   default Iterator<History.Entry> reverseIterator() {
      return this.reverseIterator(this.last());
   }

   default Iterator<History.Entry> reverseIterator(final int var1) {
      return new Iterator<History.Entry>() {
         private final ListIterator<History.Entry> it = History.this.iterator(var1 + 1);

         @Override
         public boolean hasNext() {
            return this.it.hasPrevious();
         }

         public History.Entry next() {
            return this.it.previous();
         }
      };
   }

   String current();

   boolean previous();

   boolean next();

   boolean moveToFirst();

   boolean moveToLast();

   boolean moveTo(int var1);

   void moveToEnd();

   public interface Entry {
      int index();

      Instant time();

      String line();
   }
}
