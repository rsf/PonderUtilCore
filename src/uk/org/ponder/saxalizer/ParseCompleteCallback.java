package uk.org.ponder.saxalizer;

/** This is an interface used internally by the SAXHPool and SAXalizerHelper classes and
    should not be used elsewhere.
*/

interface ParseCompleteCallback {
  public void parseComplete(int returningindex);
  }
