package org.apache.logging.log4j.core.appender.db.jdbc;

import java.io.Serializable;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLTransactionRollbackException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.StringLayout;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseManager;
import org.apache.logging.log4j.core.appender.db.ColumnMapping;
import org.apache.logging.log4j.core.appender.db.DbAppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.convert.DateTypeConverter;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.core.util.Log4jThread;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.spi.ThreadContextMap;
import org.apache.logging.log4j.spi.ThreadContextStack;
import org.apache.logging.log4j.util.IndexedReadOnlyStringMap;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.Strings;

public final class JdbcDatabaseManager extends AbstractDatabaseManager {
   private static final JdbcDatabaseManager.JdbcDatabaseManagerFactory INSTANCE = new JdbcDatabaseManager.JdbcDatabaseManagerFactory();
   private final List<ColumnConfig> columnConfigs;
   private final String sqlStatement;
   private final JdbcDatabaseManager.FactoryData factoryData;
   private volatile Connection connection;
   private volatile PreparedStatement statement;
   private volatile JdbcDatabaseManager.Reconnector reconnector;
   private volatile boolean isBatchSupported;
   private volatile Map<String, JdbcDatabaseManager.ResultSetColumnMetaData> columnMetaData;

   private static void appendColumnName(int var0, String var1, StringBuilder var2) {
      if (var0 > 1) {
         var2.append(',');
      }

      var2.append(var1);
   }

   private static void appendColumnNames(String var0, JdbcDatabaseManager.FactoryData var1, StringBuilder var2) {
      int var3 = 1;
      String var4 = "Appending {} {}[{}]: {}={} ";
      if (var1.columnMappings != null) {
         for (ColumnMapping var8 : var1.columnMappings) {
            String var9 = var8.getName();
            appendColumnName(var3, var9, var2);
            logger().trace("Appending {} {}[{}]: {}={} ", var0, var8.getClass().getSimpleName(), Integer.valueOf(var3), var9, var8);
            var3++;
         }
      }

      if (var1.columnConfigs != null) {
         for (ColumnConfig var13 : var1.columnConfigs) {
            String var14 = var13.getColumnName();
            appendColumnName(var3, var14, var2);
            logger().trace("Appending {} {}[{}]: {}={} ", var0, var13.getClass().getSimpleName(), Integer.valueOf(var3), var14, var13);
            var3++;
         }
      }
   }

   private static JdbcDatabaseManager.JdbcDatabaseManagerFactory getFactory() {
      return INSTANCE;
   }

   @Deprecated
   public static JdbcDatabaseManager getJDBCDatabaseManager(String var0, int var1, ConnectionSource var2, String var3, ColumnConfig[] var4) {
      return getManager(var0, new JdbcDatabaseManager.FactoryData(var1, null, var2, var3, var4, ColumnMapping.EMPTY_ARRAY, false, 5000L, true), getFactory());
   }

   @Deprecated
   public static JdbcDatabaseManager getManager(
      String var0, int var1, Layout<? extends Serializable> var2, ConnectionSource var3, String var4, ColumnConfig[] var5, ColumnMapping[] var6
   ) {
      return getManager(var0, new JdbcDatabaseManager.FactoryData(var1, var2, var3, var4, var5, var6, false, 5000L, true), getFactory());
   }

   @Deprecated
   public static JdbcDatabaseManager getManager(
      String var0,
      int var1,
      Layout<? extends Serializable> var2,
      ConnectionSource var3,
      String var4,
      ColumnConfig[] var5,
      ColumnMapping[] var6,
      boolean var7,
      long var8
   ) {
      return getManager(var0, new JdbcDatabaseManager.FactoryData(var1, null, var3, var4, var5, var6, false, 5000L, true), getFactory());
   }

   public static JdbcDatabaseManager getManager(
      String var0,
      int var1,
      Layout<? extends Serializable> var2,
      ConnectionSource var3,
      String var4,
      ColumnConfig[] var5,
      ColumnMapping[] var6,
      boolean var7,
      long var8,
      boolean var10
   ) {
      return getManager(var0, new JdbcDatabaseManager.FactoryData(var1, var2, var3, var4, var5, var6, var7, var8, var10), getFactory());
   }

   private JdbcDatabaseManager(String var1, String var2, List<ColumnConfig> var3, JdbcDatabaseManager.FactoryData var4) {
      super(var1, var4.getBufferSize());
      this.sqlStatement = var2;
      this.columnConfigs = var3;
      this.factoryData = var4;
   }

   private void checkConnection() {
      boolean var1 = true;

      try {
         var1 = this.isClosed(this.connection);
      } catch (SQLException var5) {
      }

      boolean var2 = true;

      try {
         var2 = this.isClosed(this.statement);
      } catch (SQLException var4) {
      }

      if (!this.isRunning() || var1 || var2) {
         this.closeResources(false);
         if (this.reconnector != null && !this.factoryData.immediateFail) {
            this.reconnector.latch();
            if (this.connection == null) {
               throw new AppenderLoggingException(
                  "Error writing to JDBC Manager '%s': JDBC connection not available [%s]", this.getName(), this.fieldsToString()
               );
            }

            if (this.statement == null) {
               throw new AppenderLoggingException(
                  "Error writing to JDBC Manager '%s': JDBC statement not available [%s].", this.getName(), this.connection, this.fieldsToString()
               );
            }
         }
      }
   }

   protected void closeResources(boolean var1) {
      PreparedStatement var2 = this.statement;
      this.statement = null;

      try {
         Closer.close(var2);
      } catch (Exception var6) {
         if (var1) {
            this.logWarn("Failed to close SQL statement logging event or flushing buffer", var6);
         }
      }

      Connection var3 = this.connection;
      this.connection = null;

      try {
         Closer.close(var3);
      } catch (Exception var5) {
         if (var1) {
            this.logWarn("Failed to close database connection logging event or flushing buffer", var5);
         }
      }
   }

   @Override
   protected boolean commitAndClose() {
      boolean var1 = true;

      try {
         if (this.connection != null && !this.connection.isClosed()) {
            if (this.isBuffered() && this.isBatchSupported && this.statement != null) {
               logger().debug("Executing batch PreparedStatement {}", this.statement);

               int[] var2;
               try {
                  var2 = this.statement.executeBatch();
               } catch (SQLTransactionRollbackException var8) {
                  logger().debug("{} executing batch PreparedStatement {}, retrying.", var8, this.statement);
                  var2 = this.statement.executeBatch();
               }

               logger().debug("Batch result: {}", Arrays.toString(var2));
            }

            logger().debug("Committing Connection {}", this.connection);
            this.connection.commit();
         }
      } catch (SQLException var9) {
         throw new DbAppenderLoggingException(var9, "Failed to commit transaction logging event or flushing buffer [%s]", this.fieldsToString());
      } finally {
         this.closeResources(true);
      }

      return true;
   }

   private boolean commitAndCloseAll() {
      if (this.connection != null || this.statement != null) {
         try {
            this.commitAndClose();
            return true;
         } catch (AppenderLoggingException var4) {
            Throwable var2 = var4.getCause();
            Object var3 = var2 == null ? var4 : var2;
            logger().debug("{} committing and closing connection: {}", var3, var3.getClass().getSimpleName(), var4.toString(), var4);
         }
      }

      if (this.factoryData.connectionSource != null) {
         this.factoryData.connectionSource.stop();
      }

      return true;
   }

   private void connectAndPrepare() throws SQLException {
      logger().debug("Acquiring JDBC connection from {}", this.getConnectionSource());
      this.connection = this.getConnectionSource().getConnection();
      logger().debug("Acquired JDBC connection {}", this.connection);
      logger().debug("Getting connection metadata {}", this.connection);
      DatabaseMetaData var1 = this.connection.getMetaData();
      logger().debug("Connection metadata {}", var1);
      this.isBatchSupported = var1.supportsBatchUpdates();
      logger().debug("Connection supportsBatchUpdates: {}", Boolean.valueOf(this.isBatchSupported));
      this.connection.setAutoCommit(false);
      logger().debug("Preparing SQL {}", this.sqlStatement);
      this.statement = this.connection.prepareStatement(this.sqlStatement);
      logger().debug("Prepared SQL {}", this.statement);
      if (this.factoryData.truncateStrings) {
         this.initColumnMetaData();
      }
   }

   @Override
   protected void connectAndStart() {
      this.checkConnection();
      synchronized (this) {
         try {
            this.connectAndPrepare();
         } catch (SQLException var4) {
            this.reconnectOn(var4);
         }
      }
   }

   private JdbcDatabaseManager.Reconnector createReconnector() {
      JdbcDatabaseManager.Reconnector var1 = new JdbcDatabaseManager.Reconnector();
      var1.setDaemon(true);
      var1.setPriority(1);
      return var1;
   }

   private String createSqlSelect() {
      StringBuilder var1 = new StringBuilder("select ");
      appendColumnNames("SELECT", this.factoryData, var1);
      var1.append(" from ");
      var1.append(this.factoryData.tableName);
      var1.append(" where 1=0");
      return var1.toString();
   }

   private String fieldsToString() {
      return String.format(
         "columnConfigs=%s, sqlStatement=%s, factoryData=%s, connection=%s, statement=%s, reconnector=%s, isBatchSupported=%s, columnMetaData=%s",
         this.columnConfigs,
         this.sqlStatement,
         this.factoryData,
         this.connection,
         this.statement,
         this.reconnector,
         this.isBatchSupported,
         this.columnMetaData
      );
   }

   public ConnectionSource getConnectionSource() {
      return this.factoryData.connectionSource;
   }

   public String getSqlStatement() {
      return this.sqlStatement;
   }

   public String getTableName() {
      return this.factoryData.tableName;
   }

   private void initColumnMetaData() throws SQLException {
      String var1 = this.createSqlSelect();
      logger().debug("Getting SQL metadata for table {}: {}", this.factoryData.tableName, var1);

      try (PreparedStatement var2 = this.connection.prepareStatement(var1)) {
         ResultSetMetaData var4 = var2.getMetaData();
         logger().debug("SQL metadata: {}", var4);
         if (var4 != null) {
            int var5 = var4.getColumnCount();
            this.columnMetaData = new HashMap<>(var5);
            int var6 = 0;

            for (int var7 = 1; var6 < var5; var7++) {
               JdbcDatabaseManager.ResultSetColumnMetaData var8 = new JdbcDatabaseManager.ResultSetColumnMetaData(var4, var7);
               this.columnMetaData.put(var8.getNameKey(), var8);
               var6++;
            }
         } else {
            logger()
               .warn(
                  "{}: truncateStrings is true and ResultSetMetaData is null for statement: {}; manager will not perform truncation.",
                  this.getClass().getSimpleName(),
                  var2
               );
         }
      }
   }

   private boolean isClosed(Statement var1) throws SQLException {
      return var1 == null || var1.isClosed();
   }

   private boolean isClosed(Connection var1) throws SQLException {
      return var1 == null || var1.isClosed();
   }

   private void reconnectOn(Exception var1) {
      if (!this.factoryData.retry) {
         throw new AppenderLoggingException("Cannot connect and prepare", var1);
      } else {
         if (this.reconnector == null) {
            this.reconnector = this.createReconnector();

            try {
               this.reconnector.reconnect();
            } catch (SQLException var3) {
               logger()
                  .debug(
                     "Cannot reestablish JDBC connection to {}: {}; starting reconnector thread {}", this.factoryData, var3, this.reconnector.getName(), var3
                  );
               this.reconnector.start();
               this.reconnector.latch();
               if (this.connection == null || this.statement == null) {
                  throw new AppenderLoggingException(var1, "Error sending to %s for %s [%s]", this.getName(), this.factoryData, this.fieldsToString());
               }
            }
         }
      }
   }

   private void setFields(MapMessage<?, ?> var1) throws SQLException {
      IndexedReadOnlyStringMap var2 = var1.getIndexedReadOnlyStringMap();
      String var3 = this.statement.getClass().getName();
      int var4 = 1;
      if (this.factoryData.columnMappings != null) {
         for (ColumnMapping var8 : this.factoryData.columnMappings) {
            if (var8.getLiteralValue() == null) {
               String var9 = var8.getSource();
               String var10 = Strings.isEmpty(var9) ? var8.getName() : var9;
               Object var11 = var2.getValue(var10);
               if (logger().isTraceEnabled()) {
                  String var12 = var11 instanceof String ? "\"" + var11 + "\"" : Objects.toString(var11, null);
                  logger().trace("{} setObject({}, {}) for key '{}' and mapping '{}'", var3, Integer.valueOf(var4), var12, var10, var8.getName());
               }

               this.setStatementObject(var4, var8.getNameKey(), var11);
               var4++;
            }
         }
      }
   }

   private void setStatementObject(int var1, String var2, Object var3) throws SQLException {
      if (this.statement == null) {
         throw new AppenderLoggingException("Cannot set a value when the PreparedStatement is null.");
      } else {
         if (var3 == null) {
            if (this.columnMetaData == null) {
               throw new AppenderLoggingException("Cannot set a value when the column metadata is null.");
            }

            this.statement.setNull(var1, this.columnMetaData.get(var2).getType());
         } else {
            this.statement.setObject(var1, this.truncate(var2, var3));
         }
      }
   }

   @Override
   protected boolean shutdownInternal() {
      if (this.reconnector != null) {
         this.reconnector.shutdown();
         this.reconnector.interrupt();
         this.reconnector = null;
      }

      return this.commitAndCloseAll();
   }

   @Override
   protected void startupInternal() throws Exception {
   }

   private Object truncate(String var1, Object var2) {
      if (var2 != null && this.factoryData.truncateStrings && this.columnMetaData != null) {
         JdbcDatabaseManager.ResultSetColumnMetaData var3 = this.columnMetaData.get(var1);
         if (var3 != null) {
            if (var3.isStringType()) {
               var2 = var3.truncate(var2.toString());
            }
         } else {
            logger().error("Missing ResultSetColumnMetaData for {}, connection={}, statement={}", var1, this.connection, this.statement);
         }
      }

      return var2;
   }

   @Override
   protected void writeInternal(LogEvent var1, Serializable var2) {
      StringReader var3 = null;

      try {
         if (!this.isRunning() || this.isClosed(this.connection) || this.isClosed(this.statement)) {
            throw new AppenderLoggingException(
               "Cannot write logging event; JDBC manager not connected to the database, running=%s, [%s]).", this.isRunning(), this.fieldsToString()
            );
         }

         this.statement.clearParameters();
         if (var2 instanceof MapMessage) {
            this.setFields((MapMessage<?, ?>)var2);
         }

         int var4 = 1;
         if (this.factoryData.columnMappings != null) {
            for (ColumnMapping var8 : this.factoryData.columnMappings) {
               if (ThreadContextMap.class.isAssignableFrom(var8.getType()) || ReadOnlyStringMap.class.isAssignableFrom(var8.getType())) {
                  this.statement.setObject(var4++, var1.getContextData().toMap());
               } else if (ThreadContextStack.class.isAssignableFrom(var8.getType())) {
                  this.statement.setObject(var4++, var1.getContextStack().asList());
               } else if (Date.class.isAssignableFrom(var8.getType())) {
                  this.statement.setObject(var4++, DateTypeConverter.fromMillis(var1.getTimeMillis(), var8.getType().asSubclass(Date.class)));
               } else {
                  StringLayout var9 = var8.getLayout();
                  if (var9 != null) {
                     if (Clob.class.isAssignableFrom(var8.getType())) {
                        this.statement.setClob(var4++, new StringReader(var9.toSerializable(var1)));
                     } else if (NClob.class.isAssignableFrom(var8.getType())) {
                        this.statement.setNClob(var4++, new StringReader(var9.toSerializable(var1)));
                     } else {
                        Object var10 = TypeConverters.convert(var9.toSerializable(var1), var8.getType(), null);
                        this.setStatementObject(var4++, var8.getNameKey(), var10);
                     }
                  }
               }
            }
         }

         for (ColumnConfig var22 : this.columnConfigs) {
            if (var22.isEventTimestamp()) {
               this.statement.setTimestamp(var4++, new Timestamp(var1.getTimeMillis()));
            } else if (var22.isClob()) {
               var3 = new StringReader(var22.getLayout().toSerializable(var1));
               if (var22.isUnicode()) {
                  this.statement.setNClob(var4++, var3);
               } else {
                  this.statement.setClob(var4++, var3);
               }
            } else if (var22.isUnicode()) {
               this.statement.setNString(var4++, Objects.toString(this.truncate(var22.getColumnNameKey(), var22.getLayout().toSerializable(var1)), null));
            } else {
               this.statement.setString(var4++, Objects.toString(this.truncate(var22.getColumnNameKey(), var22.getLayout().toSerializable(var1)), null));
            }
         }

         if (this.isBuffered() && this.isBatchSupported) {
            logger().debug("addBatch for {}", this.statement);
            this.statement.addBatch();
         } else {
            int var21 = this.statement.executeUpdate();
            logger().debug("executeUpdate = {} for {}", Integer.valueOf(var21), this.statement);
            if (var21 == 0) {
               throw new AppenderLoggingException("No records inserted in database table for log event in JDBC manager [%s].", this.fieldsToString());
            }
         }
      } catch (SQLException var18) {
         throw new DbAppenderLoggingException(var18, "Failed to insert record for log event in JDBC manager: %s [%s]", var18, this.fieldsToString());
      } finally {
         try {
            if (this.statement != null) {
               this.statement.clearParameters();
            }
         } catch (SQLException var17) {
         }

         Closer.closeSilently(var3);
      }
   }

   @Override
   protected void writeThrough(LogEvent var1, Serializable var2) {
      this.connectAndStart();

      try {
         try {
            this.writeInternal(var1, var2);
         } finally {
            this.commitAndClose();
         }
      } catch (DbAppenderLoggingException var13) {
         this.reconnectOn(var13);

         try {
            this.writeInternal(var1, var2);
         } finally {
            this.commitAndClose();
         }
      }
   }

   private static final class FactoryData extends AbstractDatabaseManager.AbstractFactoryData {
      private final ConnectionSource connectionSource;
      private final String tableName;
      private final ColumnConfig[] columnConfigs;
      private final ColumnMapping[] columnMappings;
      private final boolean immediateFail;
      private final boolean retry;
      private final long reconnectIntervalMillis;
      private final boolean truncateStrings;

      protected FactoryData(
         int var1,
         Layout<? extends Serializable> var2,
         ConnectionSource var3,
         String var4,
         ColumnConfig[] var5,
         ColumnMapping[] var6,
         boolean var7,
         long var8,
         boolean var10
      ) {
         super(var1, var2);
         this.connectionSource = var3;
         this.tableName = var4;
         this.columnConfigs = var5;
         this.columnMappings = var6;
         this.immediateFail = var7;
         this.retry = var8 > 0L;
         this.reconnectIntervalMillis = var8;
         this.truncateStrings = var10;
      }

      @Override
      public String toString() {
         return String.format(
            "FactoryData [connectionSource=%s, tableName=%s, columnConfigs=%s, columnMappings=%s, immediateFail=%s, retry=%s, reconnectIntervalMillis=%s, truncateStrings=%s]",
            this.connectionSource,
            this.tableName,
            Arrays.toString((Object[])this.columnConfigs),
            Arrays.toString((Object[])this.columnMappings),
            this.immediateFail,
            this.retry,
            this.reconnectIntervalMillis,
            this.truncateStrings
         );
      }
   }

   private static final class JdbcDatabaseManagerFactory implements ManagerFactory<JdbcDatabaseManager, JdbcDatabaseManager.FactoryData> {
      private static final char PARAMETER_MARKER = '?';

      private JdbcDatabaseManagerFactory() {
      }

      public JdbcDatabaseManager createManager(String var1, JdbcDatabaseManager.FactoryData var2) {
         StringBuilder var3 = new StringBuilder("insert into ").append(var2.tableName).append(" (");
         JdbcDatabaseManager.appendColumnNames("INSERT", var2, var3);
         var3.append(") values (");
         int var4 = 1;
         if (var2.columnMappings != null) {
            for (ColumnMapping var8 : var2.columnMappings) {
               String var9 = var8.getName();
               if (Strings.isNotEmpty(var8.getLiteralValue())) {
                  JdbcDatabaseManager.logger()
                     .trace("Adding INSERT VALUES literal for ColumnMapping[{}]: {}={} ", Integer.valueOf(var4), var9, var8.getLiteralValue());
                  var3.append(var8.getLiteralValue());
               } else if (Strings.isNotEmpty(var8.getParameter())) {
                  JdbcDatabaseManager.logger()
                     .trace("Adding INSERT VALUES parameter for ColumnMapping[{}]: {}={} ", Integer.valueOf(var4), var9, var8.getParameter());
                  var3.append(var8.getParameter());
               } else {
                  JdbcDatabaseManager.logger()
                     .trace("Adding INSERT VALUES parameter marker for ColumnMapping[{}]: {}={} ", Integer.valueOf(var4), var9, Character.valueOf('?'));
                  var3.append('?');
               }

               var3.append(',');
               var4++;
            }
         }

         int var11 = var2.columnConfigs == null ? 0 : var2.columnConfigs.length;
         ArrayList var12 = new ArrayList(var11);
         if (var2.columnConfigs != null) {
            for (ColumnConfig var10 : var2.columnConfigs) {
               if (Strings.isNotEmpty(var10.getLiteralValue())) {
                  var3.append(var10.getLiteralValue());
               } else {
                  var3.append('?');
                  var12.add(var10);
               }

               var3.append(',');
            }
         }

         var3.setCharAt(var3.length() - 1, ')');
         String var14 = var3.toString();
         return new JdbcDatabaseManager(var1, var14, var12, var2);
      }
   }

   private final class Reconnector extends Log4jThread {
      private final CountDownLatch latch = new CountDownLatch(1);
      private volatile boolean shutdown;

      private Reconnector() {
         super("JdbcDatabaseManager-Reconnector");
      }

      public void latch() {
         try {
            this.latch.await();
         } catch (InterruptedException var2) {
         }
      }

      void reconnect() throws SQLException {
         JdbcDatabaseManager.this.closeResources(false);
         JdbcDatabaseManager.this.connectAndPrepare();
         JdbcDatabaseManager.this.reconnector = null;
         this.shutdown = true;
         JdbcDatabaseManager.logger().debug("Connection reestablished to {}", JdbcDatabaseManager.this.factoryData);
      }

      @Override
      public void run() {
         while (!this.shutdown) {
            try {
               sleep(JdbcDatabaseManager.this.factoryData.reconnectIntervalMillis);
               this.reconnect();
            } catch (SQLException | InterruptedException var5) {
               JdbcDatabaseManager.logger()
                  .debug("Cannot reestablish JDBC connection to {}: {}", JdbcDatabaseManager.this.factoryData, var5.getLocalizedMessage(), var5);
            } finally {
               this.latch.countDown();
            }
         }
      }

      public void shutdown() {
         this.shutdown = true;
      }

      @Override
      public String toString() {
         return String.format("Reconnector [latch=%s, shutdown=%s]", this.latch, this.shutdown);
      }
   }

   private static final class ResultSetColumnMetaData {
      private final String schemaName;
      private final String catalogName;
      private final String tableName;
      private final String name;
      private final String nameKey;
      private final String label;
      private final int displaySize;
      private final int type;
      private final String typeName;
      private final String className;
      private final int precision;
      private final int scale;
      private final boolean isStringType;

      public ResultSetColumnMetaData(ResultSetMetaData var1, int var2) throws SQLException {
         this(
            var1.getSchemaName(var2),
            var1.getCatalogName(var2),
            var1.getTableName(var2),
            var1.getColumnName(var2),
            var1.getColumnLabel(var2),
            var1.getColumnDisplaySize(var2),
            var1.getColumnType(var2),
            var1.getColumnTypeName(var2),
            var1.getColumnClassName(var2),
            var1.getPrecision(var2),
            var1.getScale(var2)
         );
      }

      private ResultSetColumnMetaData(
         String var1, String var2, String var3, String var4, String var5, int var6, int var7, String var8, String var9, int var10, int var11
      ) {
         this.schemaName = var1;
         this.catalogName = var2;
         this.tableName = var3;
         this.name = var4;
         this.nameKey = ColumnMapping.toKey(var4);
         this.label = var5;
         this.displaySize = var6;
         this.type = var7;
         this.typeName = var8;
         this.className = var9;
         this.precision = var10;
         this.scale = var11;
         this.isStringType = var7 == 1 || var7 == -16 || var7 == -1 || var7 == -9 || var7 == 12;
      }

      public String getCatalogName() {
         return this.catalogName;
      }

      public String getClassName() {
         return this.className;
      }

      public int getDisplaySize() {
         return this.displaySize;
      }

      public String getLabel() {
         return this.label;
      }

      public String getName() {
         return this.name;
      }

      public String getNameKey() {
         return this.nameKey;
      }

      public int getPrecision() {
         return this.precision;
      }

      public int getScale() {
         return this.scale;
      }

      public String getSchemaName() {
         return this.schemaName;
      }

      public String getTableName() {
         return this.tableName;
      }

      public int getType() {
         return this.type;
      }

      public String getTypeName() {
         return this.typeName;
      }

      public boolean isStringType() {
         return this.isStringType;
      }

      @Override
      public String toString() {
         return String.format(
            "ColumnMetaData [schemaName=%s, catalogName=%s, tableName=%s, name=%s, nameKey=%s, label=%s, displaySize=%s, type=%s, typeName=%s, className=%s, precision=%s, scale=%s, isStringType=%s]",
            this.schemaName,
            this.catalogName,
            this.tableName,
            this.name,
            this.nameKey,
            this.label,
            this.displaySize,
            this.type,
            this.typeName,
            this.className,
            this.precision,
            this.scale,
            this.isStringType
         );
      }

      public String truncate(String var1) {
         return this.precision > 0 ? Strings.left(var1, this.precision) : var1;
      }
   }
}
