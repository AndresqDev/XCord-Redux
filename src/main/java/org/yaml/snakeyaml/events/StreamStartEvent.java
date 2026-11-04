package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public final class StreamStartEvent extends Event {
   public StreamStartEvent(Mark var1, Mark var2) {
      super(var1, var2);
   }

   @Override
   public Event.ID getEventId() {
      return Event.ID.StreamStart;
   }
}
