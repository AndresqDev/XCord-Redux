package org.apache.logging.log4j.core.parser;

import org.apache.logging.log4j.core.LogEvent;

public interface TextLogEventParser extends LogEventParser {
   LogEvent parseFrom(String var1) throws ParseException;
}
