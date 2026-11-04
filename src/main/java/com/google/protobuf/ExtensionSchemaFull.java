package com.google.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

final class ExtensionSchemaFull extends ExtensionSchema<Descriptors.FieldDescriptor> {
   private static final long EXTENSION_FIELD_OFFSET = getExtensionsFieldOffset();

   private static <T> long getExtensionsFieldOffset() {
      try {
         java.lang.reflect.Field var0 = GeneratedMessageV3.ExtendableMessage.class.getDeclaredField("extensions");
         return UnsafeUtil.objectFieldOffset(var0);
      } catch (Throwable var1) {
         throw new IllegalStateException("Unable to lookup extension field offset");
      }
   }

   @Override
   boolean hasExtensions(MessageLite var1) {
      return var1 instanceof GeneratedMessageV3.ExtendableMessage;
   }

   @Override
   public FieldSet<Descriptors.FieldDescriptor> getExtensions(Object var1) {
      return (FieldSet<Descriptors.FieldDescriptor>)UnsafeUtil.getObject(var1, EXTENSION_FIELD_OFFSET);
   }

   @Override
   void setExtensions(Object var1, FieldSet<Descriptors.FieldDescriptor> var2) {
      UnsafeUtil.putObject(var1, EXTENSION_FIELD_OFFSET, var2);
   }

   @Override
   FieldSet<Descriptors.FieldDescriptor> getMutableExtensions(Object var1) {
      FieldSet var2 = this.getExtensions(var1);
      if (var2.isImmutable()) {
         var2 = var2.clone();
         this.setExtensions(var1, var2);
      }

      return var2;
   }

   @Override
   void makeImmutable(Object var1) {
      this.getExtensions(var1).makeImmutable();
   }

   @Override
   <UT, UB> UB parseExtension(
      Reader var1, Object var2, ExtensionRegistryLite var3, FieldSet<Descriptors.FieldDescriptor> var4, UB var5, UnknownFieldSchema<UT, UB> var6
   ) throws IOException {
      ExtensionRegistry.ExtensionInfo var7 = (ExtensionRegistry.ExtensionInfo)var2;
      int var8 = var7.descriptor.getNumber();
      if (var7.descriptor.isRepeated() && var7.descriptor.isPacked()) {
         Object var15 = null;
         switch (var7.descriptor.getLiteType()) {
            case DOUBLE:
               ArrayList var31 = new ArrayList();
               var1.readDoubleList(var31);
               var15 = var31;
               break;
            case FLOAT:
               ArrayList var30 = new ArrayList();
               var1.readFloatList(var30);
               var15 = var30;
               break;
            case INT64:
               ArrayList var29 = new ArrayList();
               var1.readInt64List(var29);
               var15 = var29;
               break;
            case UINT64:
               ArrayList var28 = new ArrayList();
               var1.readUInt64List(var28);
               var15 = var28;
               break;
            case INT32:
               ArrayList var27 = new ArrayList();
               var1.readInt32List(var27);
               var15 = var27;
               break;
            case FIXED64:
               ArrayList var26 = new ArrayList();
               var1.readFixed64List(var26);
               var15 = var26;
               break;
            case FIXED32:
               ArrayList var25 = new ArrayList();
               var1.readFixed32List(var25);
               var15 = var25;
               break;
            case BOOL:
               ArrayList var24 = new ArrayList();
               var1.readBoolList(var24);
               var15 = var24;
               break;
            case UINT32:
               ArrayList var23 = new ArrayList();
               var1.readUInt32List(var23);
               var15 = var23;
               break;
            case SFIXED32:
               ArrayList var22 = new ArrayList();
               var1.readSFixed32List(var22);
               var15 = var22;
               break;
            case SFIXED64:
               ArrayList var21 = new ArrayList();
               var1.readSFixed64List(var21);
               var15 = var21;
               break;
            case SINT32:
               ArrayList var20 = new ArrayList();
               var1.readSInt32List(var20);
               var15 = var20;
               break;
            case SINT64:
               ArrayList var19 = new ArrayList();
               var1.readSInt64List(var19);
               var15 = var19;
               break;
            case ENUM:
               ArrayList var18 = new ArrayList();
               var1.readEnumList(var18);
               ArrayList var32 = new ArrayList();

               for (int var13 : var18) {
                  Descriptors.EnumValueDescriptor var14 = var7.descriptor.getEnumType().findValueByNumber(var13);
                  if (var14 != null) {
                     var32.add(var14);
                  } else {
                     var5 = SchemaUtil.storeUnknownEnum(var8, var13, var5, var6);
                  }
               }

               var15 = var32;
               break;
            default:
               throw new IllegalStateException("Type cannot be packed: " + var7.descriptor.getLiteType());
         }

         var4.setField(var7.descriptor, var15);
      } else {
         Object var9 = null;
         if (var7.descriptor.getLiteType() == WireFormat.FieldType.ENUM) {
            int var10 = var1.readInt32();
            Descriptors.EnumValueDescriptor var11 = var7.descriptor.getEnumType().findValueByNumber(var10);
            if (var11 == null) {
               return SchemaUtil.storeUnknownEnum(var8, var10, (UB)var5, var6);
            }

            var9 = var11;
         } else {
            switch (var7.descriptor.getLiteType()) {
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
                  var9 = var1.readGroup(var7.defaultInstance.getClass(), var3);
                  break;
               case MESSAGE:
                  var9 = var1.readMessage(var7.defaultInstance.getClass(), var3);
            }
         }

         if (var7.descriptor.isRepeated()) {
            var4.addRepeatedField(var7.descriptor, var9);
         } else {
            switch (var7.descriptor.getLiteType()) {
               case GROUP:
               case MESSAGE:
                  Object var17 = var4.getField(var7.descriptor);
                  if (var17 != null) {
                     var9 = Internal.mergeMessage(var17, var9);
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
      Descriptors.FieldDescriptor var2 = (Descriptors.FieldDescriptor)var1.getKey();
      return var2.getNumber();
   }

   @Override
   void serializeExtension(Writer var1, Entry<?, ?> var2) throws IOException {
      Descriptors.FieldDescriptor var3 = (Descriptors.FieldDescriptor)var2.getKey();
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
               List var4 = (List)var2.getValue();
               ArrayList var5 = new ArrayList();

               for (Descriptors.EnumValueDescriptor var7 : var4) {
                  var5.add(var7.getNumber());
               }

               SchemaUtil.writeInt32List(var3.getNumber(), var5, var1, var3.isPacked());
               break;
            case BYTES:
               SchemaUtil.writeBytesList(var3.getNumber(), (List<ByteString>)var2.getValue(), var1);
               break;
            case STRING:
               SchemaUtil.writeStringList(var3.getNumber(), (List<String>)var2.getValue(), var1);
               break;
            case GROUP:
               SchemaUtil.writeGroupList(var3.getNumber(), (List<?>)var2.getValue(), var1);
               break;
            case MESSAGE:
               SchemaUtil.writeMessageList(var3.getNumber(), (List<?>)var2.getValue(), var1);
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
               var1.writeInt32(var3.getNumber(), ((Descriptors.EnumValueDescriptor)var2.getValue()).getNumber());
               break;
            case BYTES:
               var1.writeBytes(var3.getNumber(), (ByteString)var2.getValue());
               break;
            case STRING:
               var1.writeString(var3.getNumber(), (String)var2.getValue());
               break;
            case GROUP:
               var1.writeGroup(var3.getNumber(), var2.getValue());
               break;
            case MESSAGE:
               var1.writeMessage(var3.getNumber(), var2.getValue());
         }
      }
   }

   @Override
   Object findExtensionByNumber(ExtensionRegistryLite var1, MessageLite var2, int var3) {
      return ((ExtensionRegistry)var1).findExtensionByNumber(((Message)var2).getDescriptorForType(), var3);
   }

   @Override
   void parseLengthPrefixedMessageSetItem(Reader var1, Object var2, ExtensionRegistryLite var3, FieldSet<Descriptors.FieldDescriptor> var4) throws IOException {
      ExtensionRegistry.ExtensionInfo var5 = (ExtensionRegistry.ExtensionInfo)var2;
      if (ExtensionRegistryLite.isEagerlyParseMessageSets()) {
         Object var6 = var1.readMessage(var5.defaultInstance.getClass(), var3);
         var4.setField(var5.descriptor, var6);
      } else {
         var4.setField(var5.descriptor, new LazyField(var5.defaultInstance, var3, var1.readBytes()));
      }
   }

   @Override
   void parseMessageSetItem(ByteString var1, Object var2, ExtensionRegistryLite var3, FieldSet<Descriptors.FieldDescriptor> var4) throws IOException {
      ExtensionRegistry.ExtensionInfo var5 = (ExtensionRegistry.ExtensionInfo)var2;
      Message var6 = var5.defaultInstance.newBuilderForType().buildPartial();
      if (ExtensionRegistryLite.isEagerlyParseMessageSets()) {
         BinaryReader var7 = BinaryReader.newInstance(ByteBuffer.wrap(var1.toByteArray()), true);
         Protobuf.getInstance().mergeFrom(var6, var7, var3);
         var4.setField(var5.descriptor, var6);
         if (var7.getFieldNumber() != Integer.MAX_VALUE) {
            throw InvalidProtocolBufferException.invalidEndTag();
         }
      } else {
         var4.setField(var5.descriptor, new LazyField(var5.defaultInstance, var3, var1));
      }
   }
}
