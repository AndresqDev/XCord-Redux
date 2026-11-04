package com.google.protobuf;

public final class AnyProto {
   static final Descriptors.Descriptor internal_static_google_protobuf_Any_descriptor = getDescriptor().getMessageTypes().get(0);
   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Any_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
      internal_static_google_protobuf_Any_descriptor, new String[]{"TypeUrl", "Value"}
   );
   private static Descriptors.FileDescriptor descriptor;

   private AnyProto() {
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
         "\n\u0019google/protobuf/any.proto\u0012\u000fgoogle.protobuf\"&\n\u0003Any\u0012\u0010\n\btype_url\u0018\u0001 \u0001(\t\u0012\r\n\u0005value\u0018\u0002 \u0001(\fBo\n\u0013com.google.protobufB\bAnyProtoP\u0001Z%github.com/golang/protobuf/ptypes/any¢\u0002\u0003GPBª\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"
      };
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(var0, new Descriptors.FileDescriptor[0]);
   }
}
