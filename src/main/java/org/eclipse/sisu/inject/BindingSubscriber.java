package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.TypeLiteral;

public interface BindingSubscriber<T> {
   TypeLiteral<T> type();

   void add(Binding<T> var1, int var2);

   void remove(Binding<T> var1);

   Iterable<Binding<T>> bindings();
}
