package io.netty.handler.codec;

import io.netty.util.internal.ObjectUtil;

public final class ProtocolDetectionResult<T> {
   private static final ProtocolDetectionResult NEEDS_MORE_DATA = new ProtocolDetectionResult(ProtocolDetectionState.NEEDS_MORE_DATA, null);
   private static final ProtocolDetectionResult INVALID = new ProtocolDetectionResult(ProtocolDetectionState.INVALID, null);
   private final ProtocolDetectionState state;
   private final T result;

   public static <T> ProtocolDetectionResult<T> needsMoreData() {
      return NEEDS_MORE_DATA;
   }

   public static <T> ProtocolDetectionResult<T> invalid() {
      return INVALID;
   }

   public static <T> ProtocolDetectionResult<T> detected(T var0) {
      return new ProtocolDetectionResult<>(ProtocolDetectionState.DETECTED, ObjectUtil.checkNotNull((T)var0, "protocol"));
   }

   private ProtocolDetectionResult(ProtocolDetectionState var1, T var2) {
      this.state = var1;
      this.result = (T)var2;
   }

   public ProtocolDetectionState state() {
      return this.state;
   }

   public T detectedProtocol() {
      return this.result;
   }
}
