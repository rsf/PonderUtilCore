package uk.org.ponder.xml;

import java.util.Enumeration;

import uk.org.ponder.util.Logger;

import uk.org.ponder.arrayutil.ArrayUtil;
import uk.org.ponder.arrayutil.ArrayEnumeration;

import uk.org.ponder.saxalizer.SAXalizable;
import uk.org.ponder.saxalizer.DeSAXalizable;
import uk.org.ponder.saxalizer.SAXAccessMethodSpec;

/** An XMLAccessManifest holds an index to all the entries in an XMLAccessFile.
 * Each of these entries is in the form of an XMLAccessManifestEntry, which holds the
 * tagname, key and byte offset of each entry.
 */

public class XMLAccessManifest implements SAXalizable, DeSAXalizable {
  public SAXAccessMethodSpec[] getSAXSetMethods() {
    return new SAXAccessMethodSpec[] {
      new SAXAccessMethodSpec("manifestentry", "addManifestEntry", XMLAccessManifestEntry.class)};
    }
  
  public SAXAccessMethodSpec[] getSAXGetMethods() {
    return new SAXAccessMethodSpec[] {
      new SAXAccessMethodSpec("manifestentry", "getManifestEntries", Enumeration.class)};
    }

  private XMLAccessManifestEntry[] entries = new XMLAccessManifestEntry[10];
  private int filled = 0;
  public XMLAccessManifest() {}

  /** Adds the specified XMLAccessManifestEntry to this collection. This is a SAXalization method.
   * @param entry The entry to add.
   */

  public void addManifestEntry(XMLAccessManifestEntry entry) {
    Logger.println("Manifest entry added with offset "+entry.getOffsetLong()+
		   " at index "+filled, Logger.DEBUG_INFORMATIONAL);
    if (filled == entries.length) {
      entries = (XMLAccessManifestEntry[]) ArrayUtil.expand(entries, 2.0);
      }
    entries[filled] = entry;
    filled++;
    }

  /** Returns an enumeration of all XMLAccessManifestEntries in this collection. This is
   * a DeSAXalization method.
   * @return The required enumeration of entries.
   */

  public Enumeration getManifestEntries() {
    return new ArrayEnumeration(entries, 0, filled);
    }

  /** Finds the index number of the entry with the specified key.
   * @param entrykey The entry key to be found.
   * @return The required index number, or -1 if the key could not be found.
   */

  public int getEntryNumber(String entrykey) {
    for (int i = 0; i < filled; ++ i) {
      if (entries[i].getKey().equals(entrykey)) 
	return i;
      }
    return -1;
    }

  /** Returns the number of entries in this collection.
   * @return The number of entries in this collection.
   */

  public int size() {
    return filled;
    }

  /** Returns the entry at the supplied index.
   * @param i The index of the required entry.
   * @return The required entry.
   */

  public XMLAccessManifestEntry manifestEntryAt(int i) {
    return entries[i];
    }

  /** Removes all entries from this collection.
   */

  public void blastState() {
    filled = 0;
    }

  }
