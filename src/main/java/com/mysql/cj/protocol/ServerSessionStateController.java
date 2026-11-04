package com.mysql.cj.protocol;

import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import java.util.ArrayList;
import java.util.List;

public interface ServerSessionStateController {
   int SESSION_TRACK_SYSTEM_VARIABLES = 0;
   int SESSION_TRACK_SCHEMA = 1;
   int SESSION_TRACK_STATE_CHANGE = 2;
   int SESSION_TRACK_GTIDS = 3;
   int SESSION_TRACK_TRANSACTION_CHARACTERISTICS = 4;
   int SESSION_TRACK_TRANSACTION_STATE = 5;

   default void setSessionStateChanges(ServerSessionStateController.ServerSessionStateChanges changes) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   default ServerSessionStateController.ServerSessionStateChanges getSessionStateChanges() {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   default void addSessionStateChangesListener(ServerSessionStateController.SessionStateChangesListener l) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   default void removeSessionStateChangesListener(ServerSessionStateController.SessionStateChangesListener l) {
      throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
   }

   public interface ServerSessionStateChanges {
      List<ServerSessionStateController.SessionStateChange> getSessionStateChangesList();
   }

   public static class SessionStateChange {
      private int type;
      private List<String> values = new ArrayList<>();

      public SessionStateChange(int type) {
         this.type = type;
      }

      public int getType() {
         return this.type;
      }

      public List<String> getValues() {
         return this.values;
      }

      public ServerSessionStateController.SessionStateChange addValue(String value) {
         this.values.add(value);
         return this;
      }
   }

   @FunctionalInterface
   public interface SessionStateChangesListener {
      void handleSessionStateChanges(ServerSessionStateController.ServerSessionStateChanges var1);
   }
}
