package org.apache.logging.log4j.jul;

import org.apache.logging.log4j.Level;

public interface LevelConverter {
   Level toLevel(java.util.logging.Level var1);

   java.util.logging.Level toJavaLevel(Level var1);
}
