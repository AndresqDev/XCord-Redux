package com.google.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

final class ExtensionSchemaLite extends ExtensionSchema<GeneratedMessageLite.ExtensionDescriptor> {
   @Override
   boolean hasExtensions(MessageLite var1) {
      return var1 instanceof GeneratedMessageLite.ExtendableMessage;
   }

   @Override
   FieldSet<GeneratedMessageLite.ExtensionDescriptor> getExtensions(Object var1) {
      return ((GeneratedMessageLite.ExtendableMessage)var1).extensions;
   }

   @Override
   void setExtensions(Object var1, FieldSet<GeneratedMessageLite.ExtensionDescriptor> var2) {
      ((GeneratedMessageLite.ExtendableMessage)var1).extensions = var2;
   }

   @Override
   FieldSet<GeneratedMessageLite.ExtensionDescriptor> getMutableExtensions(Object var1) {
      return ((GeneratedMessageLite.ExtendableMessage)var1).ensureExtensionsAreMutable();
   }

   @Override
   void makeImmutable(Object var1) {
      this.getExtensions(var1).makeImmutable();
   }

   @Override
   <UT, UB> UB parseExtension(
      Reader var1, Object var2, ExtensionRegistryLite var3, FieldSet<GeneratedMessageLite.ExtensionDescriptor> var4, UB var5, UnknownFieldSchema<UT, UB> var6
   ) throws IOException {
      GeneratedMessageLite.GeneratedExtension var7 = (GeneratedMessageLite.GeneratedExtension)var2;
      int var8 = var7.getNumber();
      if (var7.descriptor.isRepeated() && var7.descriptor.isPacked()) {
         Object var12 = null;
         switch (var7.getLiteType()) {
            case DOUBLE:
               ArrayList var28 = new ArrayList();
               var1.readDoubleList(var28);
               var12 = var28;
               break;
            case FLOAT:
               ArrayList var27 = new ArrayList();
               var1.readFloatList(var27);
               var12 = var27;
               break;
            case INT64:
               ArrayList var26 = new ArrayList();
               var1.readInt64List(var26);
               var12 = var26;
               break;
            case UINT64:
               ArrayList var25 = new ArrayList();
               var1.readUInt64List(var25);
               var12 = var25;
               break;
            case INT32:
               ArrayList var24 = new ArrayList();
               var1.readInt32List(var24);
               var12 = var24;
               break;
            case FIXED64:
               ArrayList var23 = new ArrayList();
               var1.readFixed64List(var23);
               var12 = var23;
               break;
            case FIXED32:
               ArrayList var22 = new ArrayList();
               var1.readFixed32List(var22);
               var12 = var22;
               break;
            case BOOL:
               ArrayList var21 = new ArrayList();
               var1.readBoolList(var21);
               var12 = var21;
               break;
            case UINT32:
               ArrayList var20 = new ArrayList();
               var1.readUInt32List(var20);
               var12 = var20;
               break;
            case SFIXED32:
               ArrayList var19 = new ArrayList();
               var1.readSFixed32List(var19);
               var12 = var19;
               break;
            case SFIXED64:
               ArrayList var18 = new ArrayList();
               var1.readSFixed64List(var18);
               var12 = var18;
               break;
            case SINT32:
               ArrayList var17 = new ArrayList();
               var1.readSInt32List(var17);
               var12 = var17;
               break;
            case SINT64:
               ArrayList var16 = new ArrayList();
               var1.readSInt64List(var16);
               var12 = var16;
               break;
            case ENUM:
               ArrayList var15 = new ArrayList();
               var1.readEnumList(var15);
               var5 = SchemaUtil.filterUnknownEnumList(var8, var15, var7.descriptor.getEnumType(), var5, var6);
               var12 = var15;
               break;
            default:
               throw new IllegalStateException("Type cannot be packed: " + var7.descriptor.getLiteType());
         }

         var4.setField(var7.descriptor, var12);
      } else {
         Object var9 = null;
         if (var7.getLiteType() == WireFormat.FieldType.ENUM) {
            int var10 = var1.readInt32();
            Internal.EnumLite var11 = var7.descriptor.getEnumType().findValueByNumber(var10);
            if (var11 == null) {
               return SchemaUtil.storeUnknownEnum(var8, var10, (UB)var5, var6);
            }

            var9 = var10;
         } else {
            switch (var7.getLiteType()) {
               case DOUBLE:
                  var9 = var1.readDouble();
                  break;
               case FLOAT:
                  var9 = var1.readFloat();
                  break;
               case INT64:
                  var9 = var1.readInt64();
                  break;
               case UINT64:
                  var9 = var1.readUInt64();
                  break;
               case INT32:
                  var9 = var1.readInt32();
                  break;
               case FIXED64:
                  var9 = var1.readFixed64();
                  break;
               case FIXED32:
                  var9 = var1.readFixed32();
                  break;
               case BOOL:
                  var9 = var1.readBool();
                  break;
               case UINT32:
                  var9 = var1.readUInt32();
                  break;
               case SFIXED32:
                  var9 = var1.readSFixed32();
                  break;
               case SFIXED64:
                  var9 = var1.readSFixed64();
                  break;
               case SINT32:
                  var9 = var1.readSInt32();
                  break;
               case SINT64:
                  var9 = var1.readSInt64();
                  break;
               case ENUM:
                  throw new IllegalStateException("Shouldn't reach here.");
               case BYTES:
                  var9 = var1.readBytes();
                  break;
               case STRING:
                  var9 = var1.readString();
                  break;
               case GROUP:
                  var9 = var1.readGroup(var7.getMessageDefaultInstance().getClass(), var3);
                  break;
               case MESSAGE:
                  var9 = var1.readMessage(var7.getMessageDefaultInstance().getClass(), var3);
            }
         }

         if (var7.isRepeated()) {
            var4.addRepeatedField(var7.descriptor, var9);
         } else {
            switch (var7.getLiteType()) {
               case GROUP:
               case MESSAGE:
                  Object var14 = var4.getField(var7.descriptor);
                  if (var14 != null) {
                     var9 = Internal.mergeMessage(var14, var9);
                  }
               default:
                  var4.setField(var7.descriptor, var9);
            }
         }
      }

      return (UB)var5;
   }

   @Override
   int extensionNumber(Entry<?, ?> var1) {
      GeneratedMessageLite.ExtensionDescriptor var2 = (GeneratedMessageLite.ExtensionDescriptor)var1.getKey();
      return var2.getNumber();
   }

   @Override
   void serializeExtension(Writer var1, Entry<?, ?> var2) throws IOException {
      GeneratedMessageLite.ExtensionDescriptor var3 = (GeneratedMessageLite.ExtensionDescriptor)var2.getKey();
      if (var3.isRepeated()) {
         switch (var3.getLiteType()) {
            case DOUBLE:
               SchemaUtil.writeDoubleList(var3.getNumber(), (List<Double>)var2.getValue(), var1, var3.isPacked());
               break;
            case FLOAT:
               SchemaUtil.writeFloatList(var3.getNumber(), (List<Float>)var2.getValue(), var1, var3.isPacked());
               break;
            case INT64:
               SchemaUtil.writeInt64List(var3.getNumber(), (List<Long>)var2.getValue(), var1, var3.isPacked());
               break;
            case UINT64:
               SchemaUtil.writeUInt64List(var3.getNumber(), (List<Long>)var2.getValue(), var1, var3.isPacked());
               break;
            case INT32:
               SchemaUtil.writeInt32List(var3.getNumber(), (List<Integer>)var2.getValue(), var1, var3.isPacked());
               break;
            case FIXED64:
               SchemaUtil.writeFixed64List(var3.getNumber(), (List<Long>)var2.getValue(), var1, var3.isPacked());
               break;
            case FIXED32:
               SchemaUtil.writeFixed32List(var3.getNumber(), (List<Integer>)var2.getValue(), var1, var3.isPacked());
               break;
            case BOOL:
               SchemaUtil.writeBoolList(var3.getNumber(), (List<Boolean>)var2.getValue(), var1, var3.isPacked());
               break;
            case UINT32:
               SchemaUtil.writeUInt32List(var3.getNumber(), (List<Integer>)var2.getValue(), var1, var3.isPacked());
               break;
            case SFIXED32:
               SchemaUtil.writeSFixed32List(var3.getNumber(), (List<Integer>)var2.getValue(), var1, var3.isPacked());
               break;
            case SFIXED64:
               SchemaUtil.writeSFixed64List(var3.getNumber(), (List<Long>)var2.getValue(), var1, var3.isPacked());
               break;
            case SINT32:
               SchemaUtil.writeSInt32List(var3.getNumber(), (List<Integer>)var2.getValue(), var1, var3.isPacked());
               break;
            case SINT64:
               SchemaUtil.writeSInt64List(var3.getNumber(), (List<Long>)var2.getValue(), var1, var3.isPacked());
               break;
            case ENUM:
               SchemaUtil.writeInt32List(var3.getNumber(), (List<Integer>)var2.getValue(), var1, var3.isPacked());
               break;
            case BYTES:
               SchemaUtil.writeBytesList(var3.getNumber(), (List<ByteString>)var2.getValue(), var1);
               break;
            case STRING:
               SchemaUtil.writeStringList(var3.getNumber(), (List<String>)var2.getValue(), var1);
               break;
            case GROUP:
               List var5 = (List)var2.getValue();
               if (var5 != null && !var5.isEmpty()) {
                  SchemaUtil.writeGroupList(var3.getNumber(), (List<?>)var2.getValue(), var1, Protobuf.getInstance().schemaFor(var5.get(0).getClass()));
               }
               break;
            case MESSAGE:
               List var4 = (List)var2.getValue();
               if (var4 != null && !var4.isEmpty()) {
                  SchemaUtil.writeMessageList(var3.getNumber(), (List<?>)var2.getValue(), var1, Protobuf.getInstance().schemaFor(var4.get(0).getClass()));
               }
         }
      } else {
         switch (var3.getLiteType()) {
            case DOUBLE:
               var1.writeDouble(var3.getNumber(), (Double)var2.getValue());
               break;
            case FLOAT:
               var1.writeFloat(var3.getNumber(), (Float)var2.getValue());
               break;
            case INT64:
               var1.writeInt64(var3.getNumber(), (Long)var2.getValue());
               break;
            case UINT64:
               var1.writeUInt64(var3.getNumber(), (Long)var2.getValue());
               break;
            case INT32:
               var1.writeInt32(var3.getNumber(), (Integer)var2.getValue());
               break;
            case FIXED64:
               var1.writeFixed64(var3.getNumber(), (Long)var2.getValue());
               break;
            case FIXED32:
               var1.writeFixed32(var3.getNumber(), (Integer)var2.getValue());
               break;
            case BOOL:
               var1.writeBool(var3.getNumber(), (Boolean)var2.getValue());
               break;
            case UINT32:
               var1.writeUInt32(var3.getNumber(), (Integer)var2.getValue());
               break;
            case SFIXED32:
               var1.writeSFixed32(var3.getNumber(), (Integer)var2.getValue());
               break;
            case SFIXED64:
               var1.writeSFixed64(var3.getNumber(), (Long)var2.getValue());
               break;
            case SINT32:
               var1.writeSInt32(var3.getNumber(), (Integer)var2.getValue());
               break;
            case SINT64:
               var1.writeSInt64(var3.getNumber(), (Long)var2.getValue());
               break;
            case ENUM:
               var1.writeInt32(var3.getNumber(), (Integer)var2.getValue());
               break;
            case BYTES:
               var1.writeBytes(var3.getNumber(), (ByteString)var2.getValue());
               break;
            case STRING:
               var1.writeString(var3.getNumber(), (String)var2.getValue());
               break;
            case GROUP:
               var1.writeGroup(var3.getNumber(), var2.getValue(), Protobuf.getInstance().schemaFor(var2.getValue().getClass()));
               break;
            case MESSAGE:
               var1.writeMessage(var3.getNumber(), var2.getValue(), Protobuf.getInstance().schemaFor(var2.getValue().getClass()));
         }
      }
   }

   @Override
   Object findExtensionByNumber(ExtensionRegistryLite var1, MessageLite var2, int var3) {
      return var1.findLiteExtensionByNumber(var2, var3);
   }

   @Override
   void parseLengthPrefixedMessageSetItem(Reader var1, Object var2, ExtensionRegistryLite var3, FieldSet<GeneratedMessageLite.ExtensionDescriptor> var4) throws IOException {
      GeneratedMessageLite.GeneratedExtension var5 = (GeneratedMessageLite.GeneratedExtension)var2;
      Object var6 = var1.readMessage(var5.getMessageDefaultInstance().getClass(), var3);
      var4.setField(var5.descriptor, var6);
   }

   @Override
   void parseMessageSetItem(ByteString var1, Object var2, ExtensionRegistryLite var3, FieldSet<GeneratedMessageLite.ExtensionDescriptor> var4) throws IOException {
      GeneratedMessageLite.GeneratedExtension var5 = (GeneratedMessageLite.GeneratedExtension)var2;
      MessageLite var6 = var5.getMessageDefaultInstance().newBuilderForType().buildPartial();
      BinaryReader var7 = BinaryReader.newInstance(ByteBuffer.wrap(var1.toByteArray()), true);
      Protobuf.getInstance().mergeFrom(var6, var7, var3);
      var4.setField(var5.descriptor, var6);
      if (var7.getFieldNumber() != Integer.MAX_VALUE) {
         throw InvalidProtocolBufferException.invalidEndTag();
      }
   }
}
