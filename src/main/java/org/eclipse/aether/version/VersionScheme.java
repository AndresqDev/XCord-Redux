package org.eclipse.aether.version;

public interface VersionScheme {
   Version parseVersion(String var1) throws InvalidVersionSpecificationException;

   VersionRange parseVersionRange(String var1) throws InvalidVersionSpecificationException;

   VersionConstraint parseVersionConstraint(String var1) throws InvalidVersionSpecificationException;
}
