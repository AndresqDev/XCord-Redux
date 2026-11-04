package com.google.protobuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class StructuralMessageInfo implements MessageInfo {
   private final ProtoSyntax syntax;
   private final boolean messageSetWireFormat;
   private final int[] checkInitialized;
   private final FieldInfo[] fields;
   private final MessageLite defaultInstance;

   StructuralMessageInfo(ProtoSyntax var1, boolean var2, int[] var3, FieldInfo[] var4, Object var5) {
      this.syntax = var1;
      this.messageSetWireFormat = var2;
      this.checkInitialized = var3;
      this.fields = var4;
      this.defaultInstance = Internal.checkNotNull((MessageLite)var5, "defaultInstance");
   }

   @Override
   public ProtoSyntax getSyntax() {
      return this.syntax;
   }

   @Override
   public boolean isMessageSetWireFormat() {
      return this.messageSetWireFormat;
   }

   public int[] getCheckInitialized() {
      return this.checkInitialized;
   }

   public FieldInfo[] getFields() {
      return this.fields;
   }

   @Override
   public MessageLite getDefaultInstance() {
      return this.defaultInstance;
   }

   public static StructuralMessageInfo.Builder newBuilder() {
      return new StructuralMessageInfo.Builder();
   }

   public static StructuralMessageInfo.Builder newBuilder(int var0) {
      return new StructuralMessageInfo.Builder(var0);
   }

   public static final class Builder {
      private final List<FieldInfo> fields;
      private ProtoSyntax syntax;
      private boolean wasBuilt;
      private boolean messageSetWireFormat;
      private int[] checkInitialized = null;
      private Object defaultInstance;

      public Builder() {
         this.fields = new ArrayList<>();
      }

      public Builder(int var1) {
         this.fields = new ArrayList<>(var1);
      }

      public void withDefaultInstance(Object var1) {
         this.defaultInstance = var1;
      }

      public void withSyntax(ProtoSyntax var1) {
         this.syntax = Internal.checkNotNull(var1, "syntax");
      }

      public void withMessageSetWireFormat(boolean var1) {
         this.messageSetWireFormat = var1;
      }

      public void withCheckInitialized(int[] var1) {
         this.checkInitialized = var1;
      }

      public void withField(FieldInfo var1) {
         if (this.wasBuilt) {
            throw new IllegalStateException("Builder can only build once");
         } else {
            this.fields.add(var1);
         }
      }

      public StructuralMessageInfo build() {
         if (this.wasBuilt) {
            throw new IllegalStateException("Builder can only build once");
         } else if (this.syntax == null) {
            throw new IllegalStateException("Must specify a proto syntax");
         } else {
            this.wasBuilt = true;
            Collections.sort(this.fields);
            return new StructuralMessageInfo(
               this.syntax, this.messageSetWireFormat, this.checkInitialized, this.fields.toArray(new FieldInfo[0]), this.defaultInstance
            );
         }
      }
   }
}
