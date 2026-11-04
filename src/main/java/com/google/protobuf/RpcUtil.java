package com.google.protobuf;

public final class RpcUtil {
   private RpcUtil() {
   }

   public static <Type extends Message> RpcCallback<Type> specializeCallback(RpcCallback<Message> var0) {
      return var0;
   }

   public static <Type extends Message> RpcCallback<Message> generalizeCallback(final RpcCallback<Type> var0, final Class<Type> var1, final Type var2) {
      return new RpcCallback<Message>() {
         public void run(Message var1x) {
            Message var2x;
            try {
               var2x = (Message)var1.cast(var1x);
            } catch (ClassCastException var4) {
               var2x = RpcUtil.copyAsType(var2, var1x);
            }

            var0.run(var2x);
         }
      };
   }

   private static <Type extends Message> Type copyAsType(Type var0, Message var1) {
      return (Type)var0.newBuilderForType().mergeFrom(var1).build();
   }

   public static <ParameterType> RpcCallback<ParameterType> newOneTimeCallback(final RpcCallback<ParameterType> var0) {
      return new RpcCallback<ParameterType>() {
         private boolean alreadyCalled = false;

         @Override
         public void run(ParameterType var1) {
            synchronized (this) {
               if (this.alreadyCalled) {
                  throw new RpcUtil.AlreadyCalledException();
               }

               this.alreadyCalled = true;
            }

            var0.run(var1);
         }
      };
   }

   public static final class AlreadyCalledException extends RuntimeException {
      private static final long serialVersionUID = 5469741279507848266L;

      public AlreadyCalledException() {
         super("This RpcCallback was already called and cannot be called multiple times.");
      }
   }
}
