package org.apache.http.message;

import java.util.NoSuchElementException;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class BasicHeaderElementIterator implements HeaderElementIterator {
   private final HeaderIterator headerIt;
   private final HeaderValueParser parser;
   private HeaderElement currentElement = null;
   private CharArrayBuffer buffer = null;
   private ParserCursor cursor = null;

   public BasicHeaderElementIterator(HeaderIterator var1, HeaderValueParser var2) {
      this.headerIt = Args.notNull(var1, "Header iterator");
      this.parser = Args.notNull(var2, "Parser");
   }

   public BasicHeaderElementIterator(HeaderIterator var1) {
      this(var1, BasicHeaderValueParser.INSTANCE);
   }

   private void bufferHeaderValue() {
      this.cursor = null;
      this.buffer = null;

      while (this.headerIt.hasNext()) {
         Header var1 = this.headerIt.nextHeader();
         if (var1 instanceof FormattedHeader) {
            this.buffer = ((FormattedHeader)var1).getBuffer();
            this.cursor = new ParserCursor(0, this.buffer.length());
            this.cursor.updatePos(((FormattedHeader)var1).getValuePos());
            break;
         }

         String var2 = var1.getValue();
         if (var2 != null) {
            this.buffer = new CharArrayBuffer(var2.length());
            this.buffer.append(var2);
            this.cursor = new ParserCursor(0, this.buffer.length());
            break;
         }
      }
   }

   private void parseNextElement() {
      while (this.headerIt.hasNext() || this.cursor != null) {
         if (this.cursor == null || this.cursor.atEnd()) {
            this.bufferHeaderValue();
         }

         if (this.cursor != null) {
            while (!this.cursor.atEnd()) {
               HeaderElement var1 = this.parser.parseHeaderElement(this.buffer, this.cursor);
               if (!var1.getName().isEmpty() || var1.getValue() != null) {
                  this.currentElement = var1;
                  return;
               }
            }

            if (this.cursor.atEnd()) {
               this.cursor = null;
               this.buffer = null;
            }
         }
      }
   }

   @Override
   public boolean hasNext() {
      if (this.currentElement == null) {
         this.parseNextElement();
      }

      return this.currentElement != null;
   }

   @Override
   public HeaderElement nextElement() throws NoSuchElementException {
      if (this.currentElement == null) {
         this.parseNextElement();
      }

      if (this.currentElement == null) {
         throw new NoSuchElementException("No more header elements available");
      } else {
         HeaderElement var1 = this.currentElement;
         this.currentElement = null;
         return var1;
      }
   }

   @Override
   public final Object next() throws NoSuchElementException {
      return this.nextElement();
   }

   @Override
   public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Remove not supported");
   }
}
