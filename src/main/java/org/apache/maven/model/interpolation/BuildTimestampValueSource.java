package org.apache.maven.model.interpolation;

import java.util.Date;
import java.util.Properties;
import org.codehaus.plexus.interpolation.AbstractValueSource;

class BuildTimestampValueSource extends AbstractValueSource {
   private final MavenBuildTimestamp mavenBuildTimestamp;

   BuildTimestampValueSource(Date var1, Properties var2) {
      super(false);
      this.mavenBuildTimestamp = new MavenBuildTimestamp(var1, var2);
   }

   @Override
   public Object getValue(String var1) {
      return !"build.timestamp".equals(var1) && !"maven.build.timestamp".equals(var1) ? null : this.mavenBuildTimestamp.formattedTimestamp();
   }
}
