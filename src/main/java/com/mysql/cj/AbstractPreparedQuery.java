package com.mysql.cj;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.Util;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractPreparedQuery<T extends QueryBindings<?>> extends AbstractQuery implements PreparedQuery<T> {
   protected ParseInfo parseInfo;
   protected T queryBindings = (T)null;
   protected String originalSql = null;
   protected int parameterCount;
   protected RuntimeProperty<Boolean> autoClosePStmtStreams;
   protected int batchCommandIndex = -1;
   protected RuntimeProperty<Boolean> useStreamLengthsInPrepStmts;
   private byte[] streamConvertBuf = null;
   private boolean usingAnsiMode;

   public AbstractPreparedQuery(NativeSession sess) {
      super(sess);
      this.autoClosePStmtStreams = this.session.getPropertySet().getBooleanProperty(PropertyKey.autoClosePStmtStreams);
      this.useStreamLengthsInPrepStmts = this.session.getPropertySet().getBooleanProperty(PropertyKey.useStreamLengthsInPrepStmts);
      this.usingAnsiMode = !this.session.getServerSession().useAnsiQuotedIdentifiers();
   }

   @Override
   public void closeQuery() {
      this.streamConvertBuf = null;
      super.closeQuery();
   }

   @Override
   public ParseInfo getParseInfo() {
      return this.parseInfo;
   }

   @Override
   public void setParseInfo(ParseInfo parseInfo) {
      this.parseInfo = parseInfo;
   }

   @Override
   public String getOriginalSql() {
      return this.originalSql;
   }

   @Override
   public void setOriginalSql(String originalSql) {
      this.originalSql = originalSql;
   }

   @Override
   public int getParameterCount() {
      return this.parameterCount;
   }

   @Override
   public void setParameterCount(int parameterCount) {
      this.parameterCount = parameterCount;
   }

   @Override
   public T getQueryBindings() {
      return this.queryBindings;
   }

   @Override
   public void setQueryBindings(T queryBindings) {
      this.queryBindings = queryBindings;
   }

   @Override
   public int getBatchCommandIndex() {
      return this.batchCommandIndex;
   }

   @Override
   public void setBatchCommandIndex(int batchCommandIndex) {
      this.batchCommandIndex = batchCommandIndex;
   }

   @Override
   public int computeBatchSize(int numBatchedArgs) {
      long[] combinedValues = this.computeMaxParameterSetSizeAndBatchSize(numBatchedArgs);
      long maxSizeOfParameterSet = combinedValues[0];
      long sizeOfEntireBatch = combinedValues[1];
      return sizeOfEntireBatch < (long)(this.maxAllowedPacket.getValue() - this.originalSql.length())
         ? numBatchedArgs
         : (int)Math.max(1L, (long)(this.maxAllowedPacket.getValue() - this.originalSql.length()) / maxSizeOfParameterSet);
   }

   @Override
   public void checkNullOrEmptyQuery(String sql) {
      if (sql == null) {
         throw (WrongArgumentException)ExceptionFactory.createException(
            WrongArgumentException.class, Messages.getString("PreparedQuery.0"), this.session.getExceptionInterceptor()
         );
      } else if (sql.length() == 0) {
         throw (WrongArgumentException)ExceptionFactory.createException(
            WrongArgumentException.class, Messages.getString("PreparedQuery.1"), this.session.getExceptionInterceptor()
         );
      }
   }

   @Override
   public String asSql() {
      return this.asSql(false);
   }

   @Override
   public String asSql(boolean quoteStreamsAndUnknowns) {
   }

   protected abstract long[] computeMaxParameterSetSizeAndBatchSize(int var1);

   @Override
   public <M extends Message> M fillSendPacket() {
      synchronized (this) {
         return this.fillSendPacket(this.queryBindings);
      }
   }

   @Override
   public <M extends Message> M fillSendPacket(QueryBindings<?> bindings) {
   }

   private final void streamToBytes(NativePacketPayload packet, InputStream in, boolean escape, long streamLength, boolean useLength) {
      try {
         if (this.streamConvertBuf == null) {
            this.streamConvertBuf = new byte[4096];
         }

         boolean hexEscape = this.session.getServerSession().isNoBackslashEscapesSet();
         if (streamLength == -1L) {
            useLength = false;
         }

         int bc = useLength
            ? Util.readBlock(in, this.streamConvertBuf, (int)streamLength, this.session.getExceptionInterceptor())
            : Util.readBlock(in, this.streamConvertBuf, this.session.getExceptionInterceptor());
         int lengthLeftToRead = (int)streamLength - bc;
         packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, StringUtils.getBytes(hexEscape ? "x" : "_binary"));
         if (escape) {
            packet.writeInteger(NativeConstants.IntegerDataType.INT1, 39L);
         }

         while (bc > 0) {
            if (hexEscape) {
               ((AbstractQueryBindings)this.queryBindings).hexEscapeBlock(this.streamConvertBuf, packet, bc);
            } else if (escape) {
               this.escapeblockFast(this.streamConvertBuf, packet, bc);
            } else {
               packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, this.streamConvertBuf, 0, bc);
            }

            if (useLength) {
               bc = Util.readBlock(in, this.streamConvertBuf, lengthLeftToRead, this.session.getExceptionInterceptor());
               if (bc > 0) {
                  lengthLeftToRead -= bc;
               }
            } else {
               bc = Util.readBlock(in, this.streamConvertBuf, this.session.getExceptionInterceptor());
            }
         }

         if (escape) {
            packet.writeInteger(NativeConstants.IntegerDataType.INT1, 39L);
         }
      } finally {
         if (this.autoClosePStmtStreams.getValue()) {
            try {
               in.close();
            } catch (IOException var15) {
            }

            InputStream var17 = null;
         }
      }
   }

   private final void escapeblockFast(byte[] buf, NativePacketPayload packet, int size) {
      int lastwritten = 0;

      for (int i = 0; i < size; i++) {
         byte b = buf[i];
         if (b == 0) {
            if (i > lastwritten) {
               packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, buf, lastwritten, i - lastwritten);
            }

            packet.writeInteger(NativeConstants.IntegerDataType.INT1, 92L);
            packet.writeInteger(NativeConstants.IntegerDataType.INT1, 48L);
            lastwritten = i + 1;
         } else if (b == 92 || b == 39 || !this.usingAnsiMode && b == 34) {
            if (i > lastwritten) {
               packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, buf, lastwritten, i - lastwritten);
            }

            packet.writeInteger(NativeConstants.IntegerDataType.INT1, 92L);
            lastwritten = i;
         }
      }

      if (lastwritten < size) {
         packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, buf, lastwritten, size - lastwritten);
      }
   }
}
