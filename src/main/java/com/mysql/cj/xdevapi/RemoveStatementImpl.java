package com.mysql.cj.xdevapi;

import com.mysql.cj.Messages;
import com.mysql.cj.MysqlxSession;
import com.mysql.cj.protocol.x.XMessage;
import com.mysql.cj.protocol.x.XMessageBuilder;
import java.util.concurrent.CompletableFuture;

public class RemoveStatementImpl extends FilterableStatement<RemoveStatement, Result> implements RemoveStatement {
   RemoveStatementImpl(MysqlxSession mysqlxSession, String schema, String collection, String criteria) {
      super(new DocFilterParams(schema, collection, false));
      this.mysqlxSession = mysqlxSession;
      if (criteria != null && criteria.trim().length() != 0) {
         this.filterParams.setCriteria(criteria);
      } else {
         throw new XDevAPIError(Messages.getString("RemoveStatement.0", new String[]{"criteria"}));
      }
   }

   @Deprecated
   @Override
   public RemoveStatement orderBy(String... sortFields) {
      return (RemoveStatement)super.orderBy(sortFields);
   }

   public Result executeStatement() {
      return this.mysqlxSession.query(this.getMessageBuilder().buildDelete(this.filterParams), new UpdateResultBuilder<>());
   }

   @Override
   protected XMessage getPrepareStatementXMessage() {
      return this.getMessageBuilder().buildPrepareDelete(this.preparedStatementId, this.filterParams);
   }

   protected Result executePreparedStatement() {
      return this.mysqlxSession.query(this.getMessageBuilder().buildPrepareExecute(this.preparedStatementId, this.filterParams), new UpdateResultBuilder<>());
   }

   @Override
   public CompletableFuture<Result> executeAsync() {
      return this.mysqlxSession
         .queryAsync(((XMessageBuilder)this.mysqlxSession.<XMessage>getMessageBuilder()).buildDelete(this.filterParams), new UpdateResultBuilder<>());
   }

   @Deprecated
   public RemoveStatement where(String searchCondition) {
      return (RemoveStatement)super.where(searchCondition);
   }
}
