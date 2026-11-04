package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import java.lang.annotation.Annotation;

public interface AnnotatedSource {
   <T extends Annotation> T getAnnotation(Binding<?> var1, Class<T> var2);
}
