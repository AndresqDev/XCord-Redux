package com.mysql.cj.xdevapi;

import com.mysql.cj.protocol.x.StatementExecuteOk;
import java.util.Iterator;

public class UpdateResult implements Result {
   protected StatementExecuteOk ok;

   public UpdateResult(StatementExecuteOk ok) {
      this.ok = ok;
   }

   @Override
   public long getAffectedItemsCount() {
      return this.ok.getAffectedItemsCount();
   }

   @Override
   public int getWarningsCount() {
      return this.ok.getWarningsCount();
   }

   @Override
   public Iterator<Warning> getWarnings() {
      return this.ok.getWarnings();
   }
}
