package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
interface PatternCompiler {
   CommonPattern compile(String var1);

   boolean isPcreLike();
}
