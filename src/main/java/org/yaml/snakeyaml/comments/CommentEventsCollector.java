package org.yaml.snakeyaml.comments;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import org.yaml.snakeyaml.events.CommentEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.parser.Parser;

public class CommentEventsCollector {
   private List<CommentLine> commentLineList;
   private Queue<Event> eventSource;
   private CommentType[] expectedCommentTypes;

   public CommentEventsCollector(final Parser var1, CommentType... var2) {
      this.eventSource = new AbstractQueue<Event>() {
         public boolean offer(Event var1x) {
            throw new UnsupportedOperationException();
         }

         public Event poll() {
            return var1.getEvent();
         }

         public Event peek() {
            return var1.peekEvent();
         }

         @Override
         public Iterator<Event> iterator() {
            throw new UnsupportedOperationException();
         }

         @Override
         public int size() {
            throw new UnsupportedOperationException();
         }
      };
      this.expectedCommentTypes = var2;
      this.commentLineList = new ArrayList<>();
   }

   public CommentEventsCollector(Queue<Event> var1, CommentType... var2) {
      this.eventSource = var1;
      this.expectedCommentTypes = var2;
      this.commentLineList = new ArrayList<>();
   }

   private boolean isEventExpected(Event var1) {
      if (var1 != null && var1.is(Event.ID.Comment)) {
         CommentEvent var2 = (CommentEvent)var1;

         for (CommentType var6 : this.expectedCommentTypes) {
            if (var2.getCommentType() == var6) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public CommentEventsCollector collectEvents() {
      this.collectEvents(null);
      return this;
   }

   public Event collectEvents(Event var1) {
      if (var1 != null) {
         if (!this.isEventExpected(var1)) {
            return var1;
         }

         this.commentLineList.add(new CommentLine((CommentEvent)var1));
      }

      while (this.isEventExpected(this.eventSource.peek())) {
         this.commentLineList.add(new CommentLine((CommentEvent)this.eventSource.poll()));
      }

      return null;
   }

   public Event collectEventsAndPoll(Event var1) {
      Event var2 = this.collectEvents(var1);
      return var2 != null ? var2 : this.eventSource.poll();
   }

   public List<CommentLine> consume() {
      List var1;
      try {
         var1 = this.commentLineList;
      } finally {
         this.commentLineList = new ArrayList<>();
      }

      return var1;
   }

   public boolean isEmpty() {
      return this.commentLineList.isEmpty();
   }
}
