package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

@DoNotMock("Use ImmutableTypeToInstanceMap or MutableTypeToInstanceMap")
@Beta
public interface TypeToInstanceMap<B> extends Map<TypeToken<? extends B>, B> {
   @Nullable
   <T extends B> T getInstance(Class<T> var1);

   @Nullable
   <T extends B> T getInstance(TypeToken<T> var1);

   @CanIgnoreReturnValue
   @Nullable
   <T extends B> T putInstance(Class<T> var1, @Nullable T var2);

   @CanIgnoreReturnValue
   @Nullable
   <T extends B> T putInstance(TypeToken<T> var1, @Nullable T var2);
}
