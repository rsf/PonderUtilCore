package uk.org.ponder.stringutil;

/** An interface used by a parser to report "words" it discovers.
 */
// ONLY KNOWN IMPLEMENTOR: com.waxinfo.search.WordStreamParser
// used to be one in jdiff.RapidWordParser, but replaced by WordBreaker infrastructure.

interface WordParseCallback {
  /** A callback reporting that a word has been seen in the input data.
   * @param characters An array containing the word data.
   * @param start The index at which the word data begins in the array.
   * @param length The length of the word data.
   */
  void reportWord(char[] characters, int start, int length);
  }
