package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.LiteralMessage;

public class BuiltInExceptions implements BuiltInExceptionProvider {
   private static final Dynamic2CommandExceptionType DOUBLE_TOO_SMALL = new Dynamic2CommandExceptionType(
      (var0, var1) -> new LiteralMessage("Double must not be less than " + var1 + ", found " + var0)
   );
   private static final Dynamic2CommandExceptionType DOUBLE_TOO_BIG = new Dynamic2CommandExceptionType(
      (var0, var1) -> new LiteralMessage("Double must not be more than " + var1 + ", found " + var0)
   );
   private static final Dynamic2CommandExceptionType FLOAT_TOO_SMALL = new Dynamic2CommandExceptionType(
      (var0, var1) -> new LiteralMessage("Float must not be less than " + var1 + ", found " + var0)
   );
   private static final Dynamic2CommandExceptionType FLOAT_TOO_BIG = new Dynamic2CommandExceptionType(
      (var0, var1) -> new LiteralMessage("Float must not be more than " + var1 + ", found " + var0)
   );
   private static final Dynamic2CommandExceptionType INTEGER_TOO_SMALL = new Dynamic2CommandExceptionType(
      (var0, var1) -> new LiteralMessage("Integer must not be less than " + var1 + ", found " + var0)
   );
   private static final Dynamic2CommandExceptionType INTEGER_TOO_BIG = new Dynamic2CommandExceptionType(
      (var0, var1) -> new LiteralMessage("Integer must not be more than " + var1 + ", found " + var0)
   );
   private static final Dynamic2CommandExceptionType LONG_TOO_SMALL = new Dynamic2CommandExceptionType(
      (var0, var1) -> new LiteralMessage("Long must not be less than " + var1 + ", found " + var0)
   );
   private static final Dynamic2CommandExceptionType LONG_TOO_BIG = new Dynamic2CommandExceptionType(
      (var0, var1) -> new LiteralMessage("Long must not be more than " + var1 + ", found " + var0)
   );
   private static final DynamicCommandExceptionType LITERAL_INCORRECT = new DynamicCommandExceptionType(var0 -> new LiteralMessage("Expected literal " + var0));
   private static final SimpleCommandExceptionType READER_EXPECTED_START_OF_QUOTE = new SimpleCommandExceptionType(
      new LiteralMessage("Expected quote to start a string")
   );
   private static final SimpleCommandExceptionType READER_EXPECTED_END_OF_QUOTE = new SimpleCommandExceptionType(new LiteralMessage("Unclosed quoted string"));
   private static final DynamicCommandExceptionType READER_INVALID_ESCAPE = new DynamicCommandExceptionType(
      var0 -> new LiteralMessage("Invalid escape sequence '" + var0 + "' in quoted string")
   );
   private static final DynamicCommandExceptionType READER_INVALID_BOOL = new DynamicCommandExceptionType(
      var0 -> new LiteralMessage("Invalid bool, expected true or false but found '" + var0 + "'")
   );
   private static final DynamicCommandExceptionType READER_INVALID_INT = new DynamicCommandExceptionType(
      var0 -> new LiteralMessage("Invalid integer '" + var0 + "'")
   );
   private static final SimpleCommandExceptionType READER_EXPECTED_INT = new SimpleCommandExceptionType(new LiteralMessage("Expected integer"));
   private static final DynamicCommandExceptionType READER_INVALID_LONG = new DynamicCommandExceptionType(
      var0 -> new LiteralMessage("Invalid long '" + var0 + "'")
   );
   private static final SimpleCommandExceptionType READER_EXPECTED_LONG = new SimpleCommandExceptionType(new LiteralMessage("Expected long"));
   private static final DynamicCommandExceptionType READER_INVALID_DOUBLE = new DynamicCommandExceptionType(
      var0 -> new LiteralMessage("Invalid double '" + var0 + "'")
   );
   private static final SimpleCommandExceptionType READER_EXPECTED_DOUBLE = new SimpleCommandExceptionType(new LiteralMessage("Expected double"));
   private static final DynamicCommandExceptionType READER_INVALID_FLOAT = new DynamicCommandExceptionType(
      var0 -> new LiteralMessage("Invalid float '" + var0 + "'")
   );
   private static final SimpleCommandExceptionType READER_EXPECTED_FLOAT = new SimpleCommandExceptionType(new LiteralMessage("Expected float"));
   private static final SimpleCommandExceptionType READER_EXPECTED_BOOL = new SimpleCommandExceptionType(new LiteralMessage("Expected bool"));
   private static final DynamicCommandExceptionType READER_EXPECTED_SYMBOL = new DynamicCommandExceptionType(
      var0 -> new LiteralMessage("Expected '" + var0 + "'")
   );
   private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_COMMAND = new SimpleCommandExceptionType(new LiteralMessage("Unknown command"));
   private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_ARGUMENT = new SimpleCommandExceptionType(
      new LiteralMessage("Incorrect argument for command")
   );
   private static final SimpleCommandExceptionType DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR = new SimpleCommandExceptionType(
      new LiteralMessage("Expected whitespace to end one argument, but found trailing data")
   );
   private static final DynamicCommandExceptionType DISPATCHER_PARSE_EXCEPTION = new DynamicCommandExceptionType(
      var0 -> new LiteralMessage("Could not parse command: " + var0)
   );

   @Override
   public Dynamic2CommandExceptionType doubleTooLow() {
      return DOUBLE_TOO_SMALL;
   }

   @Override
   public Dynamic2CommandExceptionType doubleTooHigh() {
      return DOUBLE_TOO_BIG;
   }

   @Override
   public Dynamic2CommandExceptionType floatTooLow() {
      return FLOAT_TOO_SMALL;
   }

   @Override
   public Dynamic2CommandExceptionType floatTooHigh() {
      return FLOAT_TOO_BIG;
   }

   @Override
   public Dynamic2CommandExceptionType integerTooLow() {
      return INTEGER_TOO_SMALL;
   }

   @Override
   public Dynamic2CommandExceptionType integerTooHigh() {
      return INTEGER_TOO_BIG;
   }

   @Override
   public Dynamic2CommandExceptionType longTooLow() {
      return LONG_TOO_SMALL;
   }

   @Override
   public Dynamic2CommandExceptionType longTooHigh() {
      return LONG_TOO_BIG;
   }

   @Override
   public DynamicCommandExceptionType literalIncorrect() {
      return LITERAL_INCORRECT;
   }

   @Override
   public SimpleCommandExceptionType readerExpectedStartOfQuote() {
      return READER_EXPECTED_START_OF_QUOTE;
   }

   @Override
   public SimpleCommandExceptionType readerExpectedEndOfQuote() {
      return READER_EXPECTED_END_OF_QUOTE;
   }

   @Override
   public DynamicCommandExceptionType readerInvalidEscape() {
      return READER_INVALID_ESCAPE;
   }

   @Override
   public DynamicCommandExceptionType readerInvalidBool() {
      return READER_INVALID_BOOL;
   }

   @Override
   public DynamicCommandExceptionType readerInvalidInt() {
      return READER_INVALID_INT;
   }

   @Override
   public SimpleCommandExceptionType readerExpectedInt() {
      return READER_EXPECTED_INT;
   }

   @Override
   public DynamicCommandExceptionType readerInvalidLong() {
      return READER_INVALID_LONG;
   }

   @Override
   public SimpleCommandExceptionType readerExpectedLong() {
      return READER_EXPECTED_LONG;
   }

   @Override
   public DynamicCommandExceptionType readerInvalidDouble() {
      return READER_INVALID_DOUBLE;
   }

   @Override
   public SimpleCommandExceptionType readerExpectedDouble() {
      return READER_EXPECTED_DOUBLE;
   }

   @Override
   public DynamicCommandExceptionType readerInvalidFloat() {
      return READER_INVALID_FLOAT;
   }

   @Override
   public SimpleCommandExceptionType readerExpectedFloat() {
      return READER_EXPECTED_FLOAT;
   }

   @Override
   public SimpleCommandExceptionType readerExpectedBool() {
      return READER_EXPECTED_BOOL;
   }

   @Override
   public DynamicCommandExceptionType readerExpectedSymbol() {
      return READER_EXPECTED_SYMBOL;
   }

   @Override
   public SimpleCommandExceptionType dispatcherUnknownCommand() {
      return DISPATCHER_UNKNOWN_COMMAND;
   }

   @Override
   public SimpleCommandExceptionType dispatcherUnknownArgument() {
      return DISPATCHER_UNKNOWN_ARGUMENT;
   }

   @Override
   public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
      return DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR;
   }

   @Override
   public DynamicCommandExceptionType dispatcherParseException() {
      return DISPATCHER_PARSE_EXCEPTION;
   }
}
