/* Generated By:JavaCC: Do not edit this line. DefccConstants.java */
package com.playground.javacc.modelgen.compiler.generated;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface DefccConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int MODULE_TKN = 11;
  /** RegularExpression Id. */
  int CLASS_TKN = 12;
  /** RegularExpression Id. */
  int INCLUDE_TKN = 13;
  /** RegularExpression Id. */
  int BYTE_TKN = 14;
  /** RegularExpression Id. */
  int BOOLEAN_TKN = 15;
  /** RegularExpression Id. */
  int INT_TKN = 16;
  /** RegularExpression Id. */
  int LONG_TKN = 17;
  /** RegularExpression Id. */
  int FLOAT_TKN = 18;
  /** RegularExpression Id. */
  int DOUBLE_TKN = 19;
  /** RegularExpression Id. */
  int STRING_TKN = 20;
  /** RegularExpression Id. */
  int BUFFER_TKN = 21;
  /** RegularExpression Id. */
  int VECTOR_TKN = 22;
  /** RegularExpression Id. */
  int MAP_TKN = 23;
  /** RegularExpression Id. */
  int LBRACE_TKN = 24;
  /** RegularExpression Id. */
  int RBRACE_TKN = 25;
  /** RegularExpression Id. */
  int LT_TKN = 26;
  /** RegularExpression Id. */
  int GT_TKN = 27;
  /** RegularExpression Id. */
  int SEMICOLON_TKN = 28;
  /** RegularExpression Id. */
  int COMMA_TKN = 29;
  /** RegularExpression Id. */
  int DOT_TKN = 30;
  /** RegularExpression Id. */
  int CSTRING_TKN = 31;
  /** RegularExpression Id. */
  int IDENT_TKN = 32;
  /** RegularExpression Id. */
  int IDENT_TKN_W_DOT = 33;

  /** Lexical state. */
  int DEFAULT = 0;
  /** Lexical state. */
  int WithinOneLineComment = 1;
  /** Lexical state. */
  int WithinMultiLineComment = 2;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"//\"",
    "<token of kind 6>",
    "<token of kind 7>",
    "\"/*\"",
    "\"*/\"",
    "<token of kind 10>",
    "\"module\"",
    "\"class\"",
    "\"include\"",
    "\"byte\"",
    "\"boolean\"",
    "\"int\"",
    "\"long\"",
    "\"float\"",
    "\"double\"",
    "\"string\"",
    "\"buffer\"",
    "\"vector\"",
    "\"map\"",
    "\"{\"",
    "\"}\"",
    "\"<\"",
    "\">\"",
    "\";\"",
    "\",\"",
    "\".\"",
    "<CSTRING_TKN>",
    "<IDENT_TKN>",
    "<IDENT_TKN_W_DOT>",
  };

}
