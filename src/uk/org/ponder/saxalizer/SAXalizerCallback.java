package uk.org.ponder.saxalizer;

/** The user of the <code>SAXalizer</code> class
 *   calls the <code>produceSubTree method supplies a callback object
 *   to be notified of the root object created as a result of the
 *   SAX parsing process. That callback implements this interface,
 */

interface SAXalizerCallback {
 /** @param results The object created as a result of SAXalizing a SAX
   * event stream.*/
  void productionComplete(Object results);
  }
