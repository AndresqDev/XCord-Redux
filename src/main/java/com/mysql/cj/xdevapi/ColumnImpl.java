package com.mysql.cj.xdevapi;

import com.mysql.cj.CharsetMapping;
import com.mysql.cj.MysqlType;
import com.mysql.cj.result.Field;

public class ColumnImpl implements Column {
   private Field field;

   public ColumnImpl(Field f) {
      this.field = f;
   }

   @Override
   public String getSchemaName() {
      return this.field.getDatabaseName();
   }

   @Override
   public String getTableName() {
      return this.field.getOriginalTableName();
   }

   @Override
   public String getTableLabel() {
      return this.field.getTableName();
   }

   @Override
   public String getColumnName() {
      return this.field.getOriginalName();
   }

   @Override
   public String getColumnLabel() {
      return this.field.getName();
   }

   @Override
   public Type getType() {
      switch (this.field.getMysqlType()) {
         case BIT:
            return Type.BIT;
         case BIGINT:
            int lenxx = (int)this.field.getLength();
            if (lenxx < 5) {
               return Type.TINYINT;
            } else if (lenxx < 7) {
               return Type.SMALLINT;
            } else if (lenxx < 10) {
               return Type.MEDIUMINT;
            } else if (lenxx < 12) {
               return Type.INT;
            } else {
               if (lenxx < 21) {
                  return Type.BIGINT;
               }

               throw new IllegalArgumentException("Unknown field length `" + this.field.getLength() + "` for signed int");
            }
         case BIGINT_UNSIGNED:
            int lenx = (int)this.field.getLength();
            if (lenx < 4) {
               return Type.TINYINT;
            } else if (lenx < 6) {
               return Type.SMALLINT;
            } else if (lenx < 9) {
               return Type.MEDIUMINT;
            } else if (lenx < 11) {
               return Type.INT;
            } else {
               if (lenx < 21) {
                  return Type.BIGINT;
               }

               throw new IllegalArgumentException("Unknown field length `" + this.field.getLength() + "` for unsigned int");
            }
         case FLOAT:
         case FLOAT_UNSIGNED:
            return Type.FLOAT;
         case DECIMAL:
         case DECIMAL_UNSIGNED:
            return Type.DECIMAL;
         case DOUBLE:
         case DOUBLE_UNSIGNED:
            return Type.DOUBLE;
         case CHAR:
         case VARCHAR:
            return Type.STRING;
         case JSON:
            return Type.JSON;
         case VARBINARY:
            return Type.BYTES;
         case TIME:
            return Type.TIME;
         case DATETIME:
            int len = (int)this.field.getLength();
            if (len == 10) {
               return Type.DATE;
            } else {
               if (len > 18 && len < 27) {
                  return Type.DATETIME;
               }

               throw new IllegalArgumentException("Unknown field length `" + this.field.getLength() + "` for datetime");
            }
         case TIMESTAMP:
            return Type.TIMESTAMP;
         case SET:
            return Type.SET;
         case ENUM:
            return Type.ENUM;
         case GEOMETRY:
            return Type.GEOMETRY;
         default:
            throw new IllegalArgumentException("Unknown type in metadata: " + this.field.getMysqlType());
      }
   }

   @Override
   public long getLength() {
      return this.field.getLength();
   }

   @Override
   public int getFractionalDigits() {
      return this.field.getDecimals();
   }

   @Override
   public boolean isNumberSigned() {
      return MysqlType.isSigned(this.field.getMysqlType());
   }

   @Override
   public String getCollationName() {
      return CharsetMapping.getStaticCollationNameForCollationIndex(this.field.getCollationIndex());
   }

   @Override
   public String getCharacterSetName() {
      return CharsetMapping.getStaticMysqlCharsetNameForCollationIndex(this.field.getCollationIndex());
   }

   @Override
   public boolean isPadded() {
      return this.field.isZeroFill() || this.field.getMysqlType() == MysqlType.CHAR;
   }

   @Override
   public boolean isNullable() {
      return !this.field.isNotNull();
   }

   @Override
   public boolean isAutoIncrement() {
      return this.field.isAutoIncrement();
   }

   @Override
   public boolean isPrimaryKey() {
      return this.field.isPrimaryKey();
   }

   @Override
   public boolean isUniqueKey() {
      return this.field.isUniqueKey();
   }

   @Override
   public boolean isPartKey() {
      return this.field.isMultipleKey();
   }
}
