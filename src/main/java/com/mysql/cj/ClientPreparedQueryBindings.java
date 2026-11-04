package com.mysql.cj;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.TimeUtil;
import com.mysql.cj.util.Util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Calendar;

public class ClientPreparedQueryBindings extends AbstractQueryBindings<ClientPreparedQueryBindValue> {
   private CharsetEncoder charsetEncoder;
   private SimpleDateFormat ddf;
   private SimpleDateFormat tdf;
   private SimpleDateFormat tsdf = null;

   public ClientPreparedQueryBindings(int parameterCount, Session sess) {
      super(parameterCount, sess);
      if (((NativeSession)this.session).getServerSession().getCharsetSettings().getRequiresEscapingEncoder()) {
         this.charsetEncoder = Charset.forName(this.charEncoding).newEncoder();
      }
   }

   @Override
   protected void initBindValues(int parameterCount) {
      this.bindValues = new ClientPreparedQueryBindValue[parameterCount];

      for (int i = 0; i < parameterCount; i++) {
         this.bindValues[i] = new ClientPreparedQueryBindValue();
      }
   }

   public ClientPreparedQueryBindings clone() {
      ClientPreparedQueryBindings newBindings = new ClientPreparedQueryBindings(this.bindValues.length, this.session);
      ClientPreparedQueryBindValue[] bvs = new ClientPreparedQueryBindValue[((ClientPreparedQueryBindValue[])this.bindValues).length];

      for (int i = 0; i < ((ClientPreparedQueryBindValue[])this.bindValues).length; i++) {
         bvs[i] = this.bindValues[i].clone();
      }

      newBindings.setBindValues(bvs);
      newBindings.isLoadDataQuery = this.isLoadDataQuery;
      return newBindings;
   }

   @Override
   public void checkParameterSet(int columnIndex) {
      if (!this.bindValues[columnIndex].isSet()) {
         throw ExceptionFactory.createException(
            Messages.getString("PreparedStatement.40") + (columnIndex + 1), "07001", 0, true, null, this.session.getExceptionInterceptor()
         );
      }
   }

   @Override
   public void setAsciiStream(int parameterIndex, InputStream x) {
      this.setAsciiStream(parameterIndex, x, -1);
   }

   @Override
   public void setAsciiStream(int parameterIndex, InputStream x, int length) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         this.setBinaryStream(parameterIndex, x, length);
      }
   }

   @Override
   public void setAsciiStream(int parameterIndex, InputStream x, long length) {
      this.setAsciiStream(parameterIndex, x, (int)length);
      this.bindValues[parameterIndex].setMysqlType(MysqlType.TEXT);
   }

   @Override
   public void setBigDecimal(int parameterIndex, BigDecimal x) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         this.setValue(parameterIndex, StringUtils.fixDecimalExponent(x.toPlainString()), MysqlType.DECIMAL);
      }
   }

   @Override
   public void setBigInteger(int parameterIndex, BigInteger x) {
      this.setValue(parameterIndex, x.toString(), MysqlType.BIGINT_UNSIGNED);
   }

   @Override
   public void setBinaryStream(int parameterIndex, InputStream x) {
      this.setBinaryStream(parameterIndex, x, -1);
   }

   @Override
   public void setBinaryStream(int parameterIndex, InputStream x, int length) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         this.bindValues[parameterIndex].setNull(false);
         this.bindValues[parameterIndex].setIsStream(true);
         this.bindValues[parameterIndex].setMysqlType(MysqlType.BLOB);
         this.bindValues[parameterIndex].setStreamValue(x, (long)length);
      }
   }

   @Override
   public void setBinaryStream(int parameterIndex, InputStream x, long length) {
      this.setBinaryStream(parameterIndex, x, (int)length);
   }

   @Override
   public void setBlob(int parameterIndex, InputStream inputStream) {
      this.setBinaryStream(parameterIndex, inputStream);
   }

   @Override
   public void setBlob(int parameterIndex, InputStream inputStream, long length) {
      this.setBinaryStream(parameterIndex, inputStream, (int)length);
   }

   @Override
   public void setBlob(int parameterIndex, Blob x) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         try {
            this.setBinaryStream(parameterIndex, x.getBinaryStream(), x.length());
         } catch (Throwable var4) {
            throw ExceptionFactory.createException(var4.getMessage(), var4, this.session.getExceptionInterceptor());
         }
      }
   }

   @Override
   public void setBoolean(int parameterIndex, boolean x) {
      this.setValue(parameterIndex, x ? "1" : "0", MysqlType.BOOLEAN);
   }

   @Override
   public void setByte(int parameterIndex, byte x) {
      this.setValue(parameterIndex, String.valueOf((int)x), MysqlType.TINYINT);
   }

   @Override
   public void setBytes(int parameterIndex, byte[] x) {
      this.setBytes(parameterIndex, x, true, true);
      if (x != null) {
         this.bindValues[parameterIndex].setMysqlType(MysqlType.BINARY);
      }
   }

   @Override
   public synchronized void setBytes(int parameterIndex, byte[] x, boolean checkForIntroducer, boolean escapeForMBChars) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         if (this.session.getServerSession().isNoBackslashEscapesSet()
            || escapeForMBChars && this.session.getServerSession().getCharsetSettings().isMultibyteCharset(this.charEncoding)) {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream(x.length * 2 + 3);
            bOut.write(120);
            bOut.write(39);

            for (int i = 0; i < x.length; i++) {
               int lowBits = (x[i] & 255) / 16;
               int highBits = (x[i] & 255) % 16;
               bOut.write(HEX_DIGITS[lowBits]);
               bOut.write(HEX_DIGITS[highBits]);
            }

            bOut.write(39);
            this.setValue(parameterIndex, bOut.toByteArray(), MysqlType.BINARY);
            this.setOrigValue(parameterIndex, x);
            return;
         }

         int numBytes = x.length;
         int pad = 2;
         if (checkForIntroducer) {
            pad += 7;
         }

         ByteArrayOutputStream bOut = new ByteArrayOutputStream(numBytes + pad);
         if (checkForIntroducer) {
            bOut.write(95);
            bOut.write(98);
            bOut.write(105);
            bOut.write(110);
            bOut.write(97);
            bOut.write(114);
            bOut.write(121);
         }

         bOut.write(39);

         for (int i = 0; i < numBytes; i++) {
            byte b = x[i];
            switch (b) {
               case 0:
                  bOut.write(92);
                  bOut.write(48);
                  break;
               case 10:
                  bOut.write(92);
                  bOut.write(110);
                  break;
               case 13:
                  bOut.write(92);
                  bOut.write(114);
                  break;
               case 26:
                  bOut.write(92);
                  bOut.write(90);
                  break;
               case 34:
                  bOut.write(92);
                  bOut.write(34);
                  break;
               case 39:
                  bOut.write(92);
                  bOut.write(39);
                  break;
               case 92:
                  bOut.write(92);
                  bOut.write(92);
                  break;
               default:
                  bOut.write(b);
            }
         }

         bOut.write(39);
         this.setValue(parameterIndex, bOut.toByteArray(), MysqlType.BINARY);
      }
   }

   @Override
   public void setBytesNoEscape(int parameterIndex, byte[] parameterAsBytes) {
      byte[] parameterWithQuotes = StringUtils.quoteBytes(parameterAsBytes);
      this.setValue(parameterIndex, parameterWithQuotes, MysqlType.BINARY);
   }

   @Override
   public void setBytesNoEscapeNoQuotes(int parameterIndex, byte[] parameterAsBytes) {
      this.setValue(parameterIndex, parameterAsBytes, MysqlType.BINARY);
   }

   @Override
   public void setCharacterStream(int parameterIndex, Reader reader) {
      this.setCharacterStream(parameterIndex, reader, -1);
   }

   @Override
   public void setCharacterStream(int parameterIndex, Reader reader, int length) {
      try {
         if (reader == null) {
            this.setNull(parameterIndex);
         } else {
            char[] c = null;
            int len = 0;
            boolean useLength = this.useStreamLengthsInPrepStmts.getValue();
            String forcedEncoding = this.session.getPropertySet().getStringProperty(PropertyKey.clobCharacterEncoding).getStringValue();
            if (useLength && length != -1) {
               c = new char[length];
               int numCharsRead = Util.readFully(reader, c, length);
               if (forcedEncoding == null) {
                  this.setString(parameterIndex, new String(c, 0, numCharsRead));
               } else {
                  this.setBytes(parameterIndex, StringUtils.getBytes(new String(c, 0, numCharsRead), forcedEncoding));
               }
            } else {
               c = new char[4096];
               StringBuilder buf = new StringBuilder();

               while ((len = reader.read(c)) != -1) {
                  buf.append(c, 0, len);
               }

               if (forcedEncoding == null) {
                  this.setString(parameterIndex, buf.toString());
               } else {
                  this.setBytes(parameterIndex, StringUtils.getBytes(buf.toString(), forcedEncoding));
               }
            }

            this.bindValues[parameterIndex].setMysqlType(MysqlType.TEXT);
         }
      } catch (UnsupportedEncodingException var9) {
         throw (WrongArgumentException)ExceptionFactory.createException(
            WrongArgumentException.class, var9.toString(), var9, this.session.getExceptionInterceptor()
         );
      } catch (IOException var10) {
         throw ExceptionFactory.createException(var10.toString(), var10, this.session.getExceptionInterceptor());
      }
   }

   @Override
   public void setCharacterStream(int parameterIndex, Reader reader, long length) {
      this.setCharacterStream(parameterIndex, reader, (int)length);
   }

   @Override
   public void setClob(int parameterIndex, Reader reader) {
      this.setCharacterStream(parameterIndex, reader);
   }

   @Override
   public void setClob(int parameterIndex, Reader reader, long length) {
      this.setCharacterStream(parameterIndex, reader, length);
   }

   @Override
   public void setClob(int i, Clob x) {
      if (x == null) {
         this.setNull(i);
      } else {
         try {
            String forcedEncoding = this.session.getPropertySet().getStringProperty(PropertyKey.clobCharacterEncoding).getStringValue();
            if (forcedEncoding == null) {
               this.setString(i, x.getSubString(1L, (int)x.length()));
            } else {
               this.setBytes(i, StringUtils.getBytes(x.getSubString(1L, (int)x.length()), forcedEncoding));
            }

            this.bindValues[i].setMysqlType(MysqlType.TEXT);
         } catch (Throwable var4) {
            throw ExceptionFactory.createException(var4.getMessage(), var4);
         }
      }
   }

   @Override
   public void setDate(int parameterIndex, Date x) {
      this.setDate(parameterIndex, x, null);
   }

   @Override
   public void setDate(int parameterIndex, Date x, Calendar cal) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else if (cal != null) {
         this.setValue(parameterIndex, TimeUtil.getSimpleDateFormat("''yyyy-MM-dd''", cal).format(x), MysqlType.DATE);
      } else {
         this.ddf = TimeUtil.getSimpleDateFormat(this.ddf, "''yyyy-MM-dd''", this.session.getServerSession().getDefaultTimeZone());
         this.setValue(parameterIndex, this.ddf.format(x), MysqlType.DATE);
      }
   }

   @Override
   public void setDouble(int parameterIndex, double x) {
      if (this.session.getPropertySet().getBooleanProperty(PropertyKey.allowNanAndInf).getValue()
         || x != Double.POSITIVE_INFINITY && x != Double.NEGATIVE_INFINITY && !Double.isNaN(x)) {
         this.setValue(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)), MysqlType.DOUBLE);
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(
            WrongArgumentException.class, Messages.getString("PreparedStatement.64", new Object[]{x}), this.session.getExceptionInterceptor()
         );
      }
   }

   @Override
   public void setFloat(int parameterIndex, float x) {
      this.setValue(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)), MysqlType.FLOAT);
   }

   @Override
   public void setInt(int parameterIndex, int x) {
      this.setValue(parameterIndex, String.valueOf(x), MysqlType.INT);
   }

   @Override
   public void setLocalDate(int parameterIndex, LocalDate x, MysqlType targetMysqlType) {
      this.setValue(parameterIndex, "'" + x + "'", targetMysqlType);
   }

   @Override
   public void setLocalTime(int parameterIndex, LocalTime x, MysqlType targetMysqlType) {
      if (targetMysqlType == MysqlType.DATE) {
         this.setValue(parameterIndex, "'" + DEFAULT_DATE + "'", MysqlType.DATE);
      } else {
         if (this.session.getServerSession().getCapabilities().serverSupportsFracSecs() && this.sendFractionalSeconds.getValue()) {
            int fractLen = 6;
            if (this.columnDefinition != null && parameterIndex <= this.columnDefinition.getFields().length && parameterIndex >= 0) {
               fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
            }

            x = TimeUtil.adjustNanosPrecision(x, fractLen, !this.session.getServerSession().isServerTruncatesFracSecs());
         } else if (x.getNano() > 0) {
            x = x.withNano(0);
         }

         DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 6, true)
            .toFormatter();
         switch (targetMysqlType) {
            case TIME: {
               StringBuilder sb = new StringBuilder("'");
               sb.append(x.format(formatter));
               sb.append("'");
               this.setValue(parameterIndex, sb.toString(), targetMysqlType);
               break;
            }
            case DATETIME:
            case TIMESTAMP: {
               StringBuilder sb = new StringBuilder("'");
               sb.append(DEFAULT_DATE);
               sb.append(" ");
               sb.append(x.format(formatter));
               sb.append("'");
               this.setValue(parameterIndex, sb.toString(), targetMysqlType);
            }
         }
      }
   }

   @Override
   public void setDuration(int parameterIndex, Duration x, MysqlType targetMysqlType) {
      if (this.session.getServerSession().getCapabilities().serverSupportsFracSecs() && this.sendFractionalSeconds.getValue()) {
         int fractLen = 6;
         if (this.columnDefinition != null && parameterIndex <= this.columnDefinition.getFields().length && parameterIndex >= 0) {
            fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
         }

         x = TimeUtil.adjustNanosPrecision(x, fractLen, !this.session.getServerSession().isServerTruncatesFracSecs());
      } else if (x.getNano() > 0) {
         x = x.isNegative() ? x.plusSeconds(1L).withNanos(0) : x.withNanos(0);
      }

      switch (targetMysqlType) {
         case TIME:
            StringBuilder sb = new StringBuilder("'");
            sb.append(TimeUtil.getDurationString(x));
            sb.append("'");
            this.setValue(parameterIndex, sb.toString(), targetMysqlType);
      }
   }

   @Override
   public void setLocalDateTime(int parameterIndex, LocalDateTime x, MysqlType targetMysqlType) {
      if (targetMysqlType == MysqlType.DATE) {
         this.setValue(parameterIndex, "'" + x.toLocalDate() + "'", MysqlType.DATE);
      } else {
         int fractLen;
         switch (targetMysqlType) {
            case CHAR:
            case VARCHAR:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
               fractLen = 9;
               break;
            default:
               fractLen = 6;
         }

         if (this.columnDefinition != null && parameterIndex <= this.columnDefinition.getFields().length && parameterIndex >= 0) {
            fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
         }

         if (this.session.getServerSession().getCapabilities().serverSupportsFracSecs() && this.sendFractionalSeconds.getValue()) {
            x = TimeUtil.adjustNanosPrecision(x, fractLen, !this.session.getServerSession().isServerTruncatesFracSecs());
         } else if (x.getNano() > 0) {
            x = x.withNano(0);
         }

         switch (targetMysqlType) {
            case TIME: {
               DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                  .appendPattern("HH:mm:ss")
                  .appendFraction(ChronoField.NANO_OF_SECOND, 0, 6, true)
                  .toFormatter();
               StringBuilder sb = new StringBuilder("'");
               sb.append(x.toLocalTime().format(formatter));
               sb.append("'");
               this.setValue(parameterIndex, sb.toString(), targetMysqlType);
               break;
            }
            case DATETIME:
            case TIMESTAMP: {
               DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                  .appendPattern("yyyy-MM-dd HH:mm:ss")
                  .appendFraction(ChronoField.NANO_OF_SECOND, 0, 6, true)
                  .toFormatter();
               StringBuilder sb = new StringBuilder("'");
               sb.append(x.format(formatter));
               sb.append("'");
               this.setValue(parameterIndex, sb.toString(), targetMysqlType);
            }
         }
      }
   }

   @Override
   public void setLong(int parameterIndex, long x) {
      this.setValue(parameterIndex, String.valueOf(x), MysqlType.BIGINT);
   }

   @Override
   public void setNCharacterStream(int parameterIndex, Reader value) {
      this.setNCharacterStream(parameterIndex, value, -1L);
   }

   @Override
   public void setNCharacterStream(int parameterIndex, Reader reader, long length) {
      if (reader == null) {
         this.setNull(parameterIndex);
      } else {
         try {
            char[] c = null;
            int len = 0;
            boolean useLength = this.useStreamLengthsInPrepStmts.getValue();
            if (useLength && length != -1L) {
               c = new char[(int)length];
               int numCharsRead = Util.readFully(reader, c, (int)length);
               this.setNString(parameterIndex, new String(c, 0, numCharsRead));
            } else {
               c = new char[4096];
               StringBuilder buf = new StringBuilder();

               while ((len = reader.read(c)) != -1) {
                  buf.append(c, 0, len);
               }

               this.setNString(parameterIndex, buf.toString());
            }

            this.bindValues[parameterIndex].setMysqlType(MysqlType.TEXT);
         } catch (Throwable var9) {
            throw ExceptionFactory.createException(var9.getMessage(), var9, this.session.getExceptionInterceptor());
         }
      }
   }

   @Override
   public void setNClob(int parameterIndex, Reader reader) {
      this.setNCharacterStream(parameterIndex, reader);
   }

   @Override
   public void setNClob(int parameterIndex, Reader reader, long length) {
      if (reader == null) {
         this.setNull(parameterIndex);
      } else {
         this.setNCharacterStream(parameterIndex, reader, length);
      }
   }

   @Override
   public void setNClob(int parameterIndex, NClob value) {
      if (value == null) {
         this.setNull(parameterIndex);
      } else {
         try {
            this.setNCharacterStream(parameterIndex, value.getCharacterStream(), value.length());
         } catch (Throwable var4) {
            throw ExceptionFactory.createException(var4.getMessage(), var4, this.session.getExceptionInterceptor());
         }
      }
   }

   @Override
   public void setNString(int parameterIndex, String x) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         if (this.charEncoding.equalsIgnoreCase("UTF-8") || this.charEncoding.equalsIgnoreCase("utf8")) {
            this.setString(parameterIndex, x);
            return;
         }

         int stringLength = x.length();
         StringBuilder buf = new StringBuilder((int)((double)x.length() * 1.1 + 4.0));
         buf.append("_utf8");
         buf.append('\'');

         for (int i = 0; i < stringLength; i++) {
            char c = x.charAt(i);
            switch (c) {
               case '\u0000':
                  buf.append('\\');
                  buf.append('0');
                  break;
               case '\n':
                  buf.append('\\');
                  buf.append('n');
                  break;
               case '\r':
                  buf.append('\\');
                  buf.append('r');
                  break;
               case '\u001a':
                  buf.append('\\');
                  buf.append('Z');
                  break;
               case '"':
                  if (this.session.getServerSession().useAnsiQuotedIdentifiers()) {
                     buf.append('\\');
                  }

                  buf.append('"');
                  break;
               case '\'':
                  buf.append('\\');
                  buf.append('\'');
                  break;
               case '\\':
                  buf.append('\\');
                  buf.append('\\');
                  break;
               default:
                  buf.append(c);
            }
         }

         buf.append('\'');
         byte[] parameterAsBytes = this.isLoadDataQuery ? StringUtils.getBytes(buf.toString()) : StringUtils.getBytes(buf.toString(), "UTF-8");
         this.setValue(parameterIndex, parameterAsBytes, MysqlType.VARCHAR);
      }
   }

   @Override
   public synchronized void setNull(int parameterIndex) {
      this.setValue(parameterIndex, "null", MysqlType.NULL);
      this.bindValues[parameterIndex].setNull(true);
   }

   @Override
   public void setShort(int parameterIndex, short x) {
      this.setValue(parameterIndex, String.valueOf((int)x), MysqlType.SMALLINT);
   }

   @Override
   public void setString(int parameterIndex, String x) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         int stringLength = x.length();
         if (this.session.getServerSession().isNoBackslashEscapesSet()) {
            boolean needsHexEscape = this.isEscapeNeededForString(x, stringLength);
            if (!needsHexEscape) {
               StringBuilder quotedString = new StringBuilder(x.length() + 2);
               quotedString.append('\'');
               quotedString.append(x);
               quotedString.append('\'');
               byte[] parameterAsBytes = this.isLoadDataQuery
                  ? StringUtils.getBytes(quotedString.toString())
                  : StringUtils.getBytes(quotedString.toString(), this.charEncoding);
               this.setValue(parameterIndex, parameterAsBytes, MysqlType.VARCHAR);
            } else {
               byte[] parameterAsBytes = this.isLoadDataQuery ? StringUtils.getBytes(x) : StringUtils.getBytes(x, this.charEncoding);
               this.setBytes(parameterIndex, parameterAsBytes);
            }

            return;
         }

         String parameterAsString = x;
         boolean needsQuoted = true;
         if (this.isLoadDataQuery || this.isEscapeNeededForString(x, stringLength)) {
            needsQuoted = false;
            StringBuilder buf = new StringBuilder((int)((double)x.length() * 1.1));
            buf.append('\'');

            for (int i = 0; i < stringLength; i++) {
               char c = x.charAt(i);
               switch (c) {
                  case '\u0000':
                     buf.append('\\');
                     buf.append('0');
                     break;
                  case '\n':
                     buf.append('\\');
                     buf.append('n');
                     break;
                  case '\r':
                     buf.append('\\');
                     buf.append('r');
                     break;
                  case '\u001a':
                     buf.append('\\');
                     buf.append('Z');
                     break;
                  case '"':
                     if (this.session.getServerSession().useAnsiQuotedIdentifiers()) {
                        buf.append('\\');
                     }

                     buf.append('"');
                     break;
                  case '\'':
                     buf.append('\'');
                     buf.append('\'');
                     break;
                  case '\\':
                     buf.append('\\');
                     buf.append('\\');
                     break;
                  case '¥':
                  case '₩':
                     if (this.charsetEncoder != null) {
                        CharBuffer cbuf = CharBuffer.allocate(1);
                        ByteBuffer bbuf = ByteBuffer.allocate(1);
                        cbuf.put(c);
                        ((Buffer)cbuf).position(0);
                        this.charsetEncoder.encode(cbuf, bbuf, true);
                        if (bbuf.get(0) == 92) {
                           buf.append('\\');
                        }
                     }

                     buf.append(c);
                     break;
                  default:
                     buf.append(c);
               }
            }

            buf.append('\'');
            parameterAsString = buf.toString();
         }

         byte[] parameterAsBytes = this.isLoadDataQuery
            ? StringUtils.getBytes(parameterAsString)
            : (
               needsQuoted
                  ? StringUtils.getBytesWrapped(parameterAsString, '\'', '\'', this.charEncoding)
                  : StringUtils.getBytes(parameterAsString, this.charEncoding)
            );
         this.setValue(parameterIndex, parameterAsBytes, MysqlType.VARCHAR);
      }
   }

   private boolean isEscapeNeededForString(String x, int stringLength) {
      boolean needsHexEscape = false;

      for (int i = 0; i < stringLength; i++) {
         char c = x.charAt(i);
         switch (c) {
            case '\u0000':
            case '\n':
            case '\r':
            case '\u001a':
            case '"':
            case '\'':
            case '\\':
               needsHexEscape = true;
         }

         if (needsHexEscape) {
            break;
         }
      }

      return needsHexEscape;
   }

   @Override
   public void setTime(int parameterIndex, Time x, Calendar cal) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         String formatStr = this.session.getServerSession().getCapabilities().serverSupportsFracSecs()
               && this.sendFractionalSeconds.getValue()
               && this.sendFractionalSecondsForTime.getValue()
               && TimeUtil.hasFractionalSeconds(x)
            ? "''HH:mm:ss.SSS''"
            : "''HH:mm:ss''";
         if (cal != null) {
            this.setValue(parameterIndex, TimeUtil.getSimpleDateFormat(formatStr, cal).format(x), MysqlType.TIME);
         } else {
            this.tdf = TimeUtil.getSimpleDateFormat(this.tdf, formatStr, this.session.getServerSession().getDefaultTimeZone());
            this.setValue(parameterIndex, this.tdf.format(x), MysqlType.TIME);
         }
      }
   }

   @Override
   public void setTime(int parameterIndex, Time x) {
      this.setTime(parameterIndex, x, null);
   }

   @Override
   public void bindTimestamp(int parameterIndex, Timestamp x, Calendar targetCalendar, int fractionalLength, MysqlType targetMysqlType) {
      if (fractionalLength < 0) {
         fractionalLength = 6;
      }

      x = TimeUtil.adjustNanosPrecision(x, fractionalLength, !this.session.getServerSession().isServerTruncatesFracSecs());
      StringBuffer buf = new StringBuffer();
      if (targetCalendar != null) {
         buf.append(TimeUtil.getSimpleDateFormat("''yyyy-MM-dd HH:mm:ss", targetCalendar).format(x));
      } else {
         this.tsdf = TimeUtil.getSimpleDateFormat(
            this.tsdf,
            "''yyyy-MM-dd HH:mm:ss",
            targetMysqlType == MysqlType.TIMESTAMP && this.preserveInstants.getValue()
               ? this.session.getServerSession().getSessionTimeZone()
               : this.session.getServerSession().getDefaultTimeZone()
         );
         buf.append(this.tsdf.format(x));
      }

      if (this.session.getServerSession().getCapabilities().serverSupportsFracSecs() && x.getNanos() > 0) {
         buf.append('.');
         buf.append(TimeUtil.formatNanos(x.getNanos(), 6));
      }

      buf.append('\'');
      this.setValue(parameterIndex, buf.toString(), targetMysqlType);
   }
}
