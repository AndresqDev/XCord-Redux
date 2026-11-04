package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public abstract class AbstractParser<MessageType extends MessageLite> implements Parser<MessageType> {
   private static final ExtensionRegistryLite EMPTY_REGISTRY = ExtensionRegistryLite.getEmptyRegistry();

   private UninitializedMessageException newUninitializedMessageException(MessageType var1) {
      return var1 instanceof AbstractMessageLite ? ((AbstractMessageLite)var1).newUninitializedMessageException() : new UninitializedMessageException(var1);
   }

   private MessageType checkMessageInitialized(MessageType var1) throws InvalidProtocolBufferException {
      if (var1 != null && !var1.isInitialized()) {
         throw this.newUninitializedMessageException((MessageType)var1).asInvalidProtocolBufferException().setUnfinishedMessage(var1);
      } else {
         return (MessageType)var1;
      }
   }

   public MessageType parsePartialFrom(CodedInputStream var1) throws InvalidProtocolBufferException {
      return this.parsePartialFrom(var1, EMPTY_REGISTRY);
   }

   public MessageType parseFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      return this.checkMessageInitialized(this.parsePartialFrom(var1, var2));
   }

   public MessageType parseFrom(CodedInputStream var1) throws InvalidProtocolBufferException {
      return this.parseFrom(var1, EMPTY_REGISTRY);
   }

   public MessageType parsePartialFrom(ByteString var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      try {
         CodedInputStream var4 = var1.newCodedInput();
         MessageLite var3 = this.parsePartialFrom(var4, var2);

         try {
            var4.checkLastTagWas(0);
         } catch (InvalidProtocolBufferException var6) {
            throw var6.setUnfinishedMessage(var3);
         }

         return (MessageType)var3;
      } catch (InvalidProtocolBufferException var7) {
         throw var7;
      }
   }

   public MessageType parsePartialFrom(ByteString var1) throws InvalidProtocolBufferException {
      return this.parsePartialFrom(var1, EMPTY_REGISTRY);
   }

   public MessageType parseFrom(ByteString var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      return this.checkMessageInitialized(this.parsePartialFrom(var1, var2));
   }

   public MessageType parseFrom(ByteString var1) throws InvalidProtocolBufferException {
      return this.parseFrom(var1, EMPTY_REGISTRY);
   }

   public MessageType parseFrom(ByteBuffer var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      MessageLite var3;
      try {
         CodedInputStream var4 = CodedInputStream.newInstance(var1);
         var3 = this.parsePartialFrom(var4, var2);

         try {
            var4.checkLastTagWas(0);
         } catch (InvalidProtocolBufferException var6) {
            throw var6.setUnfinishedMessage(var3);
         }
      } catch (InvalidProtocolBufferException var7) {
         throw var7;
      }

      return this.checkMessageInitialized((MessageType)var3);
   }

   public MessageType parseFrom(ByteBuffer var1) throws InvalidProtocolBufferException {
      return this.parseFrom(var1, EMPTY_REGISTRY);
   }

   public MessageType parsePartialFrom(byte[] var1, int var2, int var3, ExtensionRegistryLite var4) throws InvalidProtocolBufferException {
      try {
         CodedInputStream var5 = CodedInputStream.newInstance(var1, var2, var3);
         MessageLite var6 = this.parsePartialFrom(var5, var4);

         try {
            var5.checkLastTagWas(0);
         } catch (InvalidProtocolBufferException var8) {
            throw var8.setUnfinishedMessage(var6);
         }

         return (MessageType)var6;
      } catch (InvalidProtocolBufferException var9) {
         throw var9;
      }
   }

   public MessageType parsePartialFrom(byte[] var1, int var2, int var3) throws InvalidProtocolBufferException {
      return this.parsePartialFrom(var1, var2, var3, EMPTY_REGISTRY);
   }

   public MessageType parsePartialFrom(byte[] var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      return this.parsePartialFrom(var1, 0, var1.length, var2);
   }

   public MessageType parsePartialFrom(byte[] var1) throws InvalidProtocolBufferException {
      return this.parsePartialFrom(var1, 0, var1.length, EMPTY_REGISTRY);
   }

   public MessageType parseFrom(byte[] var1, int var2, int var3, ExtensionRegistryLite var4) throws InvalidProtocolBufferException {
      return this.checkMessageInitialized(this.parsePartialFrom(var1, var2, var3, var4));
   }

   public MessageType parseFrom(byte[] var1, int var2, int var3) throws InvalidProtocolBufferException {
      return this.parseFrom(var1, var2, var3, EMPTY_REGISTRY);
   }

   public MessageType parseFrom(byte[] var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      return this.parseFrom(var1, 0, var1.length, var2);
   }

   public MessageType parseFrom(byte[] var1) throws InvalidProtocolBufferException {
      return this.parseFrom(var1, EMPTY_REGISTRY);
   }

   public MessageType parsePartialFrom(InputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      CodedInputStream var3 = CodedInputStream.newInstance(var1);
      MessageLite var4 = this.parsePartialFrom(var3, var2);

      try {
         var3.checkLastTagWas(0);
         return (MessageType)var4;
      } catch (InvalidProtocolBufferException var6) {
         throw var6.setUnfinishedMessage(var4);
      }
   }

   public MessageType parsePartialFrom(InputStream var1) throws InvalidProtocolBufferException {
      return this.parsePartialFrom(var1, EMPTY_REGISTRY);
   }

   public MessageType parseFrom(InputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      return this.checkMessageInitialized(this.parsePartialFrom(var1, var2));
   }

   public MessageType parseFrom(InputStream var1) throws InvalidProtocolBufferException {
      return this.parseFrom(var1, EMPTY_REGISTRY);
   }

   public MessageType parsePartialDelimitedFrom(InputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      int var3;
      try {
         int var4 = var1.read();
         if (var4 == -1) {
            return null;
         }

         var3 = CodedInputStream.readRawVarint32(var4, var1);
      } catch (IOException var5) {
         throw new InvalidProtocolBufferException(var5);
      }

      AbstractMessageLite.Builder.LimitedInputStream var6 = new AbstractMessageLite.Builder.LimitedInputStream(var1, var3);
      return this.parsePartialFrom(var6, var2);
   }

   public MessageType parsePartialDelimitedFrom(InputStream var1) throws InvalidProtocolBufferException {
      return this.parsePartialDelimitedFrom(var1, EMPTY_REGISTRY);
   }

   public MessageType parseDelimitedFrom(InputStream var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException {
      return this.checkMessageInitialized(this.parsePartialDelimitedFrom(var1, var2));
   }

   public MessageType parseDelimitedFrom(InputStream var1) throws InvalidProtocolBufferException {
      return this.parseDelimitedFrom(var1, EMPTY_REGISTRY);
   }
}
