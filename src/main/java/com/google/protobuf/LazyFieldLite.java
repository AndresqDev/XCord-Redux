package com.google.protobuf;

import java.io.IOException;

public class LazyFieldLite {
   private static final ExtensionRegistryLite EMPTY_REGISTRY = ExtensionRegistryLite.getEmptyRegistry();
   private ByteString delayedBytes;
   private ExtensionRegistryLite extensionRegistry;
   protected volatile MessageLite value;
   private volatile ByteString memoizedBytes;

   public LazyFieldLite(ExtensionRegistryLite var1, ByteString var2) {
      checkArguments(var1, var2);
      this.extensionRegistry = var1;
      this.delayedBytes = var2;
   }

   public LazyFieldLite() {
   }

   public static LazyFieldLite fromValue(MessageLite var0) {
      LazyFieldLite var1 = new LazyFieldLite();
      var1.setValue(var0);
      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof LazyFieldLite)) {
         return false;
      } else {
         LazyFieldLite var2 = (LazyFieldLite)var1;
         MessageLite var3 = this.value;
         MessageLite var4 = var2.value;
         if (var3 == null && var4 == null) {
            return this.toByteString().equals(var2.toByteString());
         } else if (var3 != null && var4 != null) {
            return var3.equals(var4);
         } else {
            return var3 != null ? var3.equals(var2.getValue(var3.getDefaultInstanceForType())) : this.getValue(var4.getDefaultInstanceForType()).equals(var4);
         }
      }
   }

   @Override
   public int hashCode() {
      return 1;
   }

   public boolean containsDefaultInstance() {
      return this.memoizedBytes == ByteString.EMPTY || this.value == null && (this.delayedBytes == null || this.delayedBytes == ByteString.EMPTY);
   }

   public void clear() {
      this.delayedBytes = null;
      this.value = null;
      this.memoizedBytes = null;
   }

   public void set(LazyFieldLite var1) {
      this.delayedBytes = var1.delayedBytes;
      this.value = var1.value;
      this.memoizedBytes = var1.memoizedBytes;
      if (var1.extensionRegistry != null) {
         this.extensionRegistry = var1.extensionRegistry;
      }
   }

   public MessageLite getValue(MessageLite var1) {
      this.ensureInitialized(var1);
      return this.value;
   }

   public MessageLite setValue(MessageLite var1) {
      MessageLite var2 = this.value;
      this.delayedBytes = null;
      this.memoizedBytes = null;
      this.value = var1;
      return var2;
   }

   public void merge(LazyFieldLite var1) {
      if (!var1.containsDefaultInstance()) {
         if (this.containsDefaultInstance()) {
            this.set(var1);
         } else {
            if (this.extensionRegistry == null) {
               this.extensionRegistry = var1.extensionRegistry;
            }

            if (this.delayedBytes != null && var1.delayedBytes != null) {
               this.delayedBytes = this.delayedBytes.concat(var1.delayedBytes);
            } else if (this.value == null && var1.value != null) {
               this.setValue(mergeValueAndBytes(var1.value, this.delayedBytes, this.extensionRegistry));
            } else if (this.value != null && var1.value == null) {
               this.setValue(mergeValueAndBytes(this.value, var1.delayedBytes, var1.extensionRegistry));
            } else {
               this.setValue(this.value.toBuilder().mergeFrom(var1.value).build());
            }
         }
      }
   }

   public void mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException {
      if (this.containsDefaultInstance()) {
         this.setByteString(var1.readBytes(), var2);
      } else {
         if (this.extensionRegistry == null) {
            this.extensionRegistry = var2;
         }

         if (this.delayedBytes != null) {
            this.setByteString(this.delayedBytes.concat(var1.readBytes()), this.extensionRegistry);
         } else {
            try {
               this.setValue(this.value.toBuilder().mergeFrom(var1, var2).build());
            } catch (InvalidProtocolBufferException var4) {
            }
         }
      }
   }

   private static MessageLite mergeValueAndBytes(MessageLite var0, ByteString var1, ExtensionRegistryLite var2) {
      try {
         return var0.toBuilder().mergeFrom(var1, var2).build();
      } catch (InvalidProtocolBufferException var4) {
         return var0;
      }
   }

   public void setByteString(ByteString var1, ExtensionRegistryLite var2) {
      checkArguments(var2, var1);
      this.delayedBytes = var1;
      this.extensionRegistry = var2;
      this.value = null;
      this.memoizedBytes = null;
   }

   public int getSerializedSize() {
      if (this.memoizedBytes != null) {
         return this.memoizedBytes.size();
      } else if (this.delayedBytes != null) {
         return this.delayedBytes.size();
      } else {
         return this.value != null ? this.value.getSerializedSize() : 0;
      }
   }

   public ByteString toByteString() {
      if (this.memoizedBytes != null) {
         return this.memoizedBytes;
      } else if (this.delayedBytes != null) {
         return this.delayedBytes;
      } else {
         synchronized (this) {
            if (this.memoizedBytes != null) {
               return this.memoizedBytes;
            } else {
               if (this.value == null) {
                  this.memoizedBytes = ByteString.EMPTY;
               } else {
                  this.memoizedBytes = this.value.toByteString();
               }

               return this.memoizedBytes;
            }
         }
      }
   }

   void writeTo(Writer var1, int var2) throws IOException {
      if (this.memoizedBytes != null) {
         var1.writeBytes(var2, this.memoizedBytes);
      } else if (this.delayedBytes != null) {
         var1.writeBytes(var2, this.delayedBytes);
      } else if (this.value != null) {
         var1.writeMessage(var2, this.value);
      } else {
         var1.writeBytes(var2, ByteString.EMPTY);
      }
   }

   protected void ensureInitialized(MessageLite var1) {
      if (this.value == null) {
         synchronized (this) {
            if (this.value == null) {
               try {
                  if (this.delayedBytes != null) {
                     MessageLite var3 = var1.getParserForType().parseFrom(this.delayedBytes, this.extensionRegistry);
                     this.value = var3;
                     this.memoizedBytes = this.delayedBytes;
                  } else {
                     this.value = var1;
                     this.memoizedBytes = ByteString.EMPTY;
                  }
               } catch (InvalidProtocolBufferException var5) {
                  this.value = var1;
                  this.memoizedBytes = ByteString.EMPTY;
               }
            }
         }
      }
   }

   private static void checkArguments(ExtensionRegistryLite var0, ByteString var1) {
      if (var0 == null) {
         throw new NullPointerException("found null ExtensionRegistry");
      } else if (var1 == null) {
         throw new NullPointerException("found null ByteString");
      }
   }
}
