package com.google.common.eventbus;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@ElementTypesAreNonnullByDefault
public class DeadEvent {
   private final Object source;
   private final Object event;

   public DeadEvent(Object var1, Object var2) {
      this.source = Preconditions.checkNotNull(var1);
      this.event = Preconditions.checkNotNull(var2);
   }

   public Object getSource() {
      return this.source;
   }

   public Object getEvent() {
      return this.event;
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("source", this.source).add("event", this.event).toString();
   }
}
