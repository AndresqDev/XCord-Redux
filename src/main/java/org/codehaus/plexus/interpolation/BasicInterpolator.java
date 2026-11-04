package org.codehaus.plexus.interpolation;

public interface BasicInterpolator {
   String interpolate(String var1) throws InterpolationException;

   String interpolate(String var1, RecursionInterceptor var2) throws InterpolationException;
}
