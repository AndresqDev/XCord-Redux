package org.apache.logging.log4j.message;

public interface ThreadInformation {
   void printThreadInfo(StringBuilder var1);

   void printStack(StringBuilder var1, StackTraceElement[] var2);
}
