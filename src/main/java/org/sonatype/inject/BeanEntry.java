package org.sonatype.inject;

import java.lang.annotation.Annotation;

@Deprecated
public interface BeanEntry<Q extends Annotation, T> extends org.eclipse.sisu.BeanEntry<Q, T> {
}
