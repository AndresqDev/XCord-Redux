package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public final class AliasEvent extends NodeEvent {
   public AliasEvent(String var1, Mark var2, Mark var3) {
      super(var1, var2, var3);
      if (var1 == null) {
         throw new NullPointerException("anchor is not specified for alias");
      }
   }

   @Override
   public Event.ID getEventId() {
      return Event.ID.Alias;
   }
}
