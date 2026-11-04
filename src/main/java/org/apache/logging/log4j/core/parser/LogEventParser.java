package org.apache.logging.log4j.core.parser;

import org.apache.logging.log4j.core.LogEvent;

public interface LogEventParser {
   LogEvent parseFrom(byte[] var1) throws ParseException;

   LogEvent parseFrom(byte[] var1, int var2, int var3) throws ParseException;
}
