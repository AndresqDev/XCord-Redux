package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Map;

@DoNotMock("Use ImmutableClassToInstanceMap or MutableClassToInstanceMap")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface ClassToInstanceMap<B> extends Map<Class<? extends B>, B> {
   <T extends B> T getInstance(Class<T> var1);

   @CanIgnoreReturnValue
   <T extends B> T putInstance(Class<T> var1, T var2);
}
