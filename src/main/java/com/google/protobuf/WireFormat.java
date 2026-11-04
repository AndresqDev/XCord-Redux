package com.google.protobuf;

import java.io.IOException;

public final class WireFormat {
   static final int FIXED32_SIZE = 4;
   static final int FIXED64_SIZE = 8;
   static final int MAX_VARINT32_SIZE = 5;
   static final int MAX_VARINT64_SIZE = 10;
   static final int MAX_VARINT_SIZE = 10;
   public static final int WIRETYPE_VARINT = 0;
   public static final int WIRETYPE_FIXED64 = 1;
   public static final int WIRETYPE_LENGTH_DELIMITED = 2;
   public static final int WIRETYPE_START_GROUP = 3;
   public static final int WIRETYPE_END_GROUP = 4;
   public static final int WIRETYPE_FIXED32 = 5;
   static final int TAG_TYPE_BITS = 3;
   static final int TAG_TYPE_MASK = 7;
   static final int MESSAGE_SET_ITEM = 1;
   static final int MESSAGE_SET_TYPE_ID = 2;
   static final int MESSAGE_SET_MESSAGE = 3;
   static final int MESSAGE_SET_ITEM_TAG = makeTag(1, 3);
   static final int MESSAGE_SET_ITEM_END_TAG = makeTag(1, 4);
   static final int MESSAGE_SET_TYPE_ID_TAG = makeTag(2, 0);
   static final int MESSAGE_SET_MESSAGE_TAG = makeTag(3, 2);

   private WireFormat() {
   }

   public static int getTagWireType(int var0) {
      return var0 & 7;
   }

   public static int getTagFieldNumber(int var0) {
      return var0 >>> 3;
   }

   static int makeTag(int var0, int var1) {
      return var0 << 3 | var1;
   }

   static Object readPrimitiveField(CodedInputStream var0, WireFormat.FieldType var1, WireFormat.Utf8Validation var2) throws IOException {
      switch (var1) {
         case DOUBLE:
            return var0.readDouble();
         case FLOAT:
            return var0.readFloat();
         case INT64:
            return var0.readInt64();
         case UINT64:
            return var0.readUInt64();
         case INT32:
            return var0.readInt32();
         case FIXED64:
            return var0.readFixed64();
         case FIXED32:
            return var0.readFixed32();
         case BOOL:
            return var0.readBool();
         case BYTES:
            return var0.readBytes();
         case UINT32:
            return var0.readUInt32();
         case SFIXED32:
            return var0.readSFixed32();
         case SFIXED64:
            return var0.readSFixed64();
         case SINT32:
            return var0.readSInt32();
         case SINT64:
            return var0.readSInt64();
         case STRING:
            return var2.readString(var0);
         case GROUP:
            throw new IllegalArgumentException("readPrimitiveField() cannot handle nested groups.");
         case MESSAGE:
            throw new IllegalArgumentException("readPrimitiveField() cannot handle embedded messages.");
         case ENUM:
            throw new IllegalArgumentException("readPrimitiveField() cannot handle enums.");
         default:
            throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
      }
   }

   public static enum FieldType {
      DOUBLE(WireFormat.JavaType.DOUBLE, 1),
      FLOAT(WireFormat.JavaType.FLOAT, 5),
      INT64(WireFormat.JavaType.LONG, 0),
      UINT64(WireFormat.JavaType.LONG, 0),
      INT32(WireFormat.JavaType.INT, 0),
      FIXED64(WireFormat.JavaType.LONG, 1),
      FIXED32(WireFormat.JavaType.INT, 5),
      BOOL(WireFormat.JavaType.BOOLEAN, 0),
      STRING(WireFormat.JavaType.STRING, 2) {
         @Override
         public boolean isPackable() {
            return false;
         }
      },
      GROUP(WireFormat.JavaType.MESSAGE, 3) {
         @Override
         public boolean isPackable() {
            return false;
         }
      },
      MESSAGE(WireFormat.JavaType.MESSAGE, 2) {
         @Override
         public boolean isPackable() {
            return false;
         }
      },
      BYTES(WireFormat.JavaType.BYTE_STRING, 2) {
         @Override
         public boolean isPackable() {
            return false;
         }
      },
      UINT32(WireFormat.JavaType.INT, 0),
      ENUM(WireFormat.JavaType.ENUM, 0),
      SFIXED32(WireFormat.JavaType.INT, 5),
      SFIXED64(WireFormat.JavaType.LONG, 1),
      SINT32(WireFormat.JavaType.INT, 0),
      SINT64(WireFormat.JavaType.LONG, 0);

      private final WireFormat.JavaType javaType;
      private final int wireType;

      private FieldType(WireFormat.JavaType var3, int var4) {
         this.javaType = var3;
         this.wireType = var4;
      }

      public WireFormat.JavaType getJavaType() {
         return this.javaType;
      }

      public int getWireType() {
         return this.wireType;
      }

      public boolean isPackable() {
         return true;
      }
   }

   public static enum JavaType {
      INT(0),
      LONG(0L),
      FLOAT(0.0F),
      DOUBLE(0.0),
      BOOLEAN(false),
      STRING(""),
      BYTE_STRING(ByteString.EMPTY),
      ENUM(null),
      MESSAGE(null);

      private final Object defaultDefault;

      private JavaType(Object var3) {
         this.defaultDefault = var3;
      }

      Object getDefaultDefault() {
         return this.defaultDefault;
      }
   }

   static enum Utf8Validation {
      LOOSE {
         @Override
         Object readString(CodedInputStream var1) throws IOException {
            return var1.readString();
         }
      },
      STRICT {
         @Override
         Object readString(CodedInputStream var1) throws IOException {
            return var1.readStringRequireUtf8();
         }
      },
      LAZY {
         @Override
         Object readString(CodedInputStream var1) throws IOException {
            return var1.readBytes();
         }
      };

      private Utf8Validation() {
      }

      abstract Object readString(CodedInputStream var1) throws IOException;
   }
}
