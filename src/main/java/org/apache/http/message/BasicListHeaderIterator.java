package org.apache.http.message;

import java.util.List;
import java.util.NoSuchElementException;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

public class BasicListHeaderIterator implements HeaderIterator {
   protected final List<Header> allHeaders;
   protected int currentIndex;
   protected int lastIndex;
   protected String headerName;

   public BasicListHeaderIterator(List<Header> var1, String var2) {
      this.allHeaders = Args.notNull(var1, "Header list");
      this.headerName = var2;
      this.currentIndex = this.findNext(-1);
      this.lastIndex = -1;
   }

   protected int findNext(int var1) {
      int var2 = var1;
      if (var1 < -1) {
         return -1;
      } else {
         int var3 = this.allHeaders.size() - 1;
         boolean var4 = false;

         while (!var4 && var2 < var3) {
            var4 = this.filterHeader(++var2);
         }

         return var4 ? var2 : -1;
      }
   }

   protected boolean filterHeader(int var1) {
      if (this.headerName == null) {
         return true;
      } else {
         String var2 = this.allHeaders.get(var1).getName();
         return this.headerName.equalsIgnoreCase(var2);
      }
   }

   @Override
   public boolean hasNext() {
      return this.currentIndex >= 0;
   }

   @Override
   public Header nextHeader() throws NoSuchElementException {
      int var1 = this.currentIndex;
      if (var1 < 0) {
         throw new NoSuchElementException("Iteration already finished.");
      } else {
         this.lastIndex = var1;
         this.currentIndex = this.findNext(var1);
         return this.allHeaders.get(var1);
      }
   }

   @Override
   public final Object next() throws NoSuchElementException {
      return this.nextHeader();
   }

   @Override
   public void remove() throws UnsupportedOperationException {
      Asserts.check(this.lastIndex >= 0, "No header to remove");
      this.allHeaders.remove(this.lastIndex);
      this.lastIndex = -1;
      this.currentIndex--;
   }
}
