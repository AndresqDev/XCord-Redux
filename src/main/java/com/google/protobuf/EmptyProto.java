package com.google.protobuf;

public final class EmptyProto {
   static final Descriptors.Descriptor internal_static_google_protobuf_Empty_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Empty_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_google_protobuf_Empty_descriptor, new String[0]
   );
   private static Descriptors.FileDescriptor descriptor;

   private EmptyProto() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite var0) {
   }

   public static void registerAllExtensions(ExtensionRegistry var0) {
      registerAllExtensions((ExtensionRegistryLite)var0);
   }

   public static Descriptors.FileDescriptor getDescriptor() {
      return descriptor;
   }

   static {
      String[] var0 = new String[]{
         "\n\u001bgoogle/protobuf/empty.proto\u0012\u000fgoogle.protobuf\"\u0007\n\u0005EmptyBv\n\u0013com.google.protobufB\nEmptyProtoP\u0001Z'github.com/golang/protobuf/ptypes/emptyø\u0001\u0001¢\u0002\u0003GPBª\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(var0, new Descriptors.FileDescriptor[0]);
   }
}
