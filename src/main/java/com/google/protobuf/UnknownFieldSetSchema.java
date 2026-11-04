package com.google.protobuf;

import java.io.IOException;

class UnknownFieldSetSchema extends UnknownFieldSchema<UnknownFieldSet, UnknownFieldSet.Builder> {
   private final boolean proto3;

   public UnknownFieldSetSchema(boolean var1) {
      this.proto3 = var1;
   }

   @Override
   boolean shouldDiscardUnknownFields(Reader var1) {
      return var1.shouldDiscardUnknownFields();
   }

   UnknownFieldSet.Builder newBuilder() {
      return UnknownFieldSet.newBuilder();
   }

   void addVarint(UnknownFieldSet.Builder var1, int var2, long var3) {
      var1.mergeField(var2, UnknownFieldSet.Field.newBuilder().addVarint(var3).build());
   }

   void addFixed32(UnknownFieldSet.Builder var1, int var2, int var3) {
      var1.mergeField(var2, UnknownFieldSet.Field.newBuilder().addFixed32(var3).build());
   }

   void addFixed64(UnknownFieldSet.Builder var1, int var2, long var3) {
      var1.mergeField(var2, UnknownFieldSet.Field.newBuilder().addFixed64(var3).build());
   }

   void addLengthDelimited(UnknownFieldSet.Builder var1, int var2, ByteString var3) {
      var1.mergeField(var2, UnknownFieldSet.Field.newBuilder().addLengthDelimited(var3).build());
   }

   void addGroup(UnknownFieldSet.Builder var1, int var2, UnknownFieldSet var3) {
      var1.mergeField(var2, UnknownFieldSet.Field.newBuilder().addGroup(var3).build());
   }

   UnknownFieldSet toImmutable(UnknownFieldSet.Builder var1) {
      return var1.build();
   }

   void writeTo(UnknownFieldSet var1, Writer var2) throws IOException {
      var1.writeTo(var2);
   }

   void writeAsMessageSetTo(UnknownFieldSet var1, Writer var2) throws IOException {
      var1.writeAsMessageSetTo(var2);
   }

   UnknownFieldSet getFromMessage(Object var1) {
      return ((GeneratedMessageV3)var1).unknownFields;
   }

   void setToMessage(Object var1, UnknownFieldSet var2) {
      ((GeneratedMessageV3)var1).unknownFields = var2;
   }

   UnknownFieldSet.Builder getBuilderFromMessage(Object var1) {
      return ((GeneratedMessageV3)var1).unknownFields.toBuilder();
   }

   void setBuilderToMessage(Object var1, UnknownFieldSet.Builder var2) {
      ((GeneratedMessageV3)var1).unknownFields = var2.build();
   }

   @Override
   void makeImmutable(Object var1) {
   }

   UnknownFieldSet merge(UnknownFieldSet var1, UnknownFieldSet var2) {
      return var1.toBuilder().mergeFrom(var2).build();
   }

   int getSerializedSize(UnknownFieldSet var1) {
      return var1.getSerializedSize();
   }

   int getSerializedSizeAsMessageSet(UnknownFieldSet var1) {
      return var1.getSerializedSizeAsMessageSet();
   }
}
