/*
 * Created on 16-Mar-2006
 */
package uk.org.ponder.fileutil;

public class StalenessEntry {
  public long lastchecked; // millisecond datestamp that filesystem freshness was checked
  public long modtime; // modification time of the file giving rise to current tree
}
