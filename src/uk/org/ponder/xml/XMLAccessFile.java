package uk.org.ponder.xml;

import java.io.InputStream;
import java.io.IOException;

import org.xml.sax.SAXException;

import uk.org.ponder.util.AssertionException;
import uk.org.ponder.util.Logger;

import uk.org.ponder.stringutil.CharToByteUTF8;

import uk.org.ponder.streamutil.RandomAccessRead;
import uk.org.ponder.streamutil.RandomAccessWrite;
import uk.org.ponder.streamutil.RandomAccessInputStream;
import uk.org.ponder.streamutil.RandomAccessOutputStream;
import uk.org.ponder.streamutil.StreamClosedCallback;

import uk.org.ponder.saxalizer.SAXHPool;
import uk.org.ponder.saxalizer.DeSAXalizer;

// an XML Access file uses UTF-8 and has the last section in fixed format

/** An XMLAccessFile stores a hashtable in a special format XML file which allows
 * random access. The hashtable is keyed by a string value, and the value is an
 * arbitrary block of XML. The file is encoded in UTF-8, and the final section
 * of the file (with tag &lt;manifestpointer&gt; holds an index to the file which 
 * is stored in a fixed-width format, and so should not be hand-edited to change its length.
 */
public class XMLAccessFile {
  private static String MANIFEST_TAG = "manifest";
  private RandomAccessRead rafreader;
  private RandomAccessWrite rafwriter;
  private String roottagname = "randomaccessxml";
  private byte[] roottagbytes;

  private StreamClosedCallback closingcallback;

  private XMLAccessManifestPointer manifestpointer = new XMLAccessManifestPointer();

  private XMLAccessManifest manifest = new XMLAccessManifest();

  /** Constructs an XMLAccessFile in read mode, wrapping the supplied readable
   * RandomAccessFile, and with the specified manifest type.
   * @param reader A RandomAccessRead object corresponding to a file which
   * has already been opened. The random access structure for the file must
   * already have been initialised.
   * @param manifesttype A String representing the manifest type for this
   * XMLAccessFile, provided for type safety.
   * @exception IOException If an I/O error occurs reading the file.
   * @exception SAXException If the file format is invalid, or contains an
   * inconsistent manifest type.
   */
  public XMLAccessFile(RandomAccessRead reader, String manifesttype) 
    throws IOException, SAXException {
    init(reader, null, manifesttype);
    }

  /** Constructs an XMLAccessFile in write mode, wrapping the supplied writeable
   * RandomAccessFile, and with the specified manifest type.
   * @param writer A RandomAccessWrite object corresponding to a file which has
   * already been opened. If this corresponds to a file with 0 length, the random
   * access structure will be initialised by this call.
   * @param manifesttype A String representing the manifest type for this XMLAccessFile.
   * This is provided purely for type safety - different XMLAccessFile clients can 
   * provide different manifest types. Clients will receive a SAXException 
   * if they supply a manifest type to a constructor different to the existing type.
   * @exception IOException If an I/O error occurs reading or writing the file.
   * @exception SAXException If the file format is invalid, or contains an inconsistent
   * manifest type.
   */

  public XMLAccessFile(RandomAccessWrite writer, String manifesttype) 
    throws IOException, SAXException {
    init(writer, writer, manifesttype);
    }

  private void init(RandomAccessRead rafreader, RandomAccessWrite rafwriter,
		    String manifesttype) throws IOException, SAXException {
    this.rafreader = rafreader;
    this.rafwriter = rafwriter;
    imbue(manifesttype);
    closingcallback = new StreamClosedCallback() {
	public void streamClosed(Object closingstream) throws IOException {
	  // strange rules prohibits overloading across inner classes
	  XMLAccessFile.this.streamClosed();
	  }
	};

    }

  private void imbue(String manifesttype) throws IOException, SAXException {
    manifestpointer.blastState();
    manifestpointer.setManifestType(manifesttype);
    manifest.blastState();
    roottagbytes = CharToByteUTF8.convert(roottagname);
    if (rafreader.length() == 0) {
      if (rafwriter == null) {
	throw new IOException("XMLAccessFile called on 0-length file without write access");
	}
      Logger.println("File discovered with length 0, initialising", Logger.DEBUG_INFORMATIONAL);
      initialise();
      }
    else {
      Logger.println("File discovered with length "+rafreader.length()+", reading manifest",
		     Logger.DEBUG_INFORMATIONAL);
      readManifest();
      }
    }


  private void initialise() throws IOException {
    RandomAccessOutputStream out = new RandomAccessOutputStream(rafwriter);
    XMLWriter writer = new XMLWriter(out);
    writer.writeDeclaration();
    writer.writeRaw("<"+roottagname+">\n", 0);
    writer.close();
    writeManifest();
    }

  /** Closes this XMLAccessFile. The underlying random access file is also closed by
   * this call.
   * @exception IOException If an I/O error occurs closing the file.
   */

  public void close() throws IOException {
    synchronized(this) {
      while (state != IDLE) {
	try {
	  wait();
	  } catch (InterruptedException ie) {}
	}
      }
    rafreader.close();
    rafreader = null;
    rafwriter = null;
    }

  // </roottag>\n                      4 + roottaglength               = 4 + roottaglength
  private long getManifestPointerOffset() throws IOException {
    long filelength = rafreader.length();
    long togo = filelength - (XMLAccessManifestPointer.getEncodedPointerLength() 
			      + roottagbytes.length + 4);
    Logger.println("Manifest pointer determined to be at "+togo,
		   Logger.DEBUG_INFORMATIONAL);
    return togo;
    }

  private InputStream getManifestPointerInputStream() throws IOException {
    return new RandomAccessInputStream(rafreader, getManifestPointerOffset(), 
				       XMLAccessManifestPointer.getEncodedPointerLength());
    }

  private InputStream getManifestInputStream(long manifestoffset) throws IOException {
    return new RandomAccessInputStream(rafreader, manifestoffset, 
				       getManifestPointerOffset() - manifestoffset);
    }

  /** Returns the manifest for the entire XMLAccessFile.
   * @return The required manifest.
   * @exception IOException If an I/O error occurred while reading the manifest.
   * @exception SAXException If an error occurred parsing the XMLAccessFile contents as XML.
   */

  public XMLAccessManifest getManifest() throws IOException, SAXException {
    return manifest;
    }

  private void readManifest() throws IOException, SAXException {
    synchronized(this) {
      if (state != IDLE) {
	try {
	  wait();
	  } catch (InterruptedException ie) {}
	}
      }
    InputStream manifestpointerstream = getManifestPointerInputStream();
    SAXHPool.getSAXalizerHelper().produceSubtree(manifestpointer, manifestpointerstream);
    //    raf.seek(manifestpointer.getOffset());
    InputStream manifeststream = getManifestInputStream
      (manifestpointer.getManifestOffsetLong());
    SAXHPool.getSAXalizerHelper().produceSubtree(manifest, manifeststream);
    }

  private static final int IDLE              = 0;
  private static final int READ_IN_PROGRESS  = 1;
  private static final int WRITE_IN_PROGRESS = 2;

  private int state = IDLE;

  private synchronized void acquireLock(int requiredstate) {
    while (state != IDLE) {
      Logger.println("XMLAccessFile.acquireLock waiting for state "
		     +requiredstate+" with state " + state +
		     " in "+this, Logger.DEBUG_INFORMATIONAL);
      try {
	wait();
	} catch (InterruptedException ie) {}
      }
    Logger.println("XMLAccessFile lock acquired", Logger.DEBUG_INFORMATIONAL);
    state = requiredstate;
    }

  private synchronized void releaseLock() {
    Logger.println("Lock released for "+this, Logger.DEBUG_INFORMATIONAL);
    state = IDLE;
    notifyAll();
    }

  /** Detects whether the supplied key is present in in this XMLAccessFile.
   * @param key The key required to be found.
   * @return <code>true</code> if the supplied key is present.
   */
  public boolean containsKey(String key) {
    acquireLock(READ_IN_PROGRESS);
    boolean togo = manifest.getEntryNumber(key) != -1;
    releaseLock();
    return togo;
    }
 
 /** Returns a stream to which clients may write in order to append
   * one or more entries to the data portion of the XML file. The
   * sequence of events must be as follows: 
   * 
   * <br> i) Clients call getEntryOutputStream() to get the output stream for appending.
   * <br> ii) Clients call markManifestEntry() to mark the start of a tag pair section.
   * <br> iii) Clients write one or more complete tag pairs directly below root
   * level to the file.
   * <br> iv) Clients close the output stream. At this point the manifest and 
   * manifest pointer are updated and written to the file.
   *
   * <br> No client may call either <code>getEntryOutputStream</code> or 
   * <code>getEntryInputStream()</code> until the stream supplied from this
   * method has been closed. These calls will block until the file has been completely
   * updated.
   * @return The required output stream.
   * @exception IOException if an I/O error occurs.
   */

  public RandomAccessOutputStream getEntryOutputStream() throws IOException {
    acquireLock(WRITE_IN_PROGRESS);
    rafwriter.seek(manifestpointer.getManifestOffsetLong());
    RandomAccessOutputStream out = new RandomAccessOutputStream(rafwriter);
    out.setClosingCallback(closingcallback);
    return out;
    }

  /** Mark the current offset within the XML file as the start of the section identified 
   * by the given key.
   *
   * <br> Clients will call this method (only) while writing to a
   * stream provided by <code>getEntryOutputStream()</code>.
   * @param key The key to be used to identify the section starting at
   * this position
   * @param tagname The tagname that is about to be written at the current position.  
   * @exception IOException if an I/O error occurs.
   */
  
  public void markManifestEntry(String key, String tagname) throws IOException {
    synchronized(this) {
      if (state != WRITE_IN_PROGRESS) {
	throw new AssertionException("Attempt to add item to manifest while XMLAccessFile "+
				     "not being written");
	}
      }
    long offset = rafwriter.getFilePointer();
    Logger.println("Marking entry with key "+key+" at offset "+offset,
		   Logger.DEBUG_INFORMATIONAL);
    manifest.addManifestEntry(new XMLAccessManifestEntry(key, offset, tagname));
    }

  /** Returns a stream to read the entry in the file with the specified key.
   *
   * No client may call <code>getEntryInputStream</code> or
   * <code>getEntryOutputStream</code> until the stream supplied from
   * this method has been closed. Any such clients will block until
   * the stream closes.
   * @param key The key for the required entry.
   * @return An input stream from which the section of the XML file identified by the
   * given key can be read.  
   * @exception IOException if an I/O error occurs.
   */

  public RandomAccessInputStream getEntryInputStream(String key) throws IOException {
    acquireLock(READ_IN_PROGRESS);
 
    int index = manifest.getEntryNumber(key);
    Logger.println("Entry "+key+" found at index "+ index, Logger.DEBUG_INFORMATIONAL);
    if (index == -1) {
      releaseLock();
      return null;
      }
    XMLAccessManifestEntry thisentry = manifest.manifestEntryAt(index);
    long thisoffset = thisentry.getOffsetLong();
    long nextoffset;
    if (index == manifest.size() - 1) {
      nextoffset = manifestpointer.getManifestOffsetLong();
      }
    else {
      nextoffset = manifest.manifestEntryAt(index + 1).getOffsetLong();
      }
    Logger.println("Returning inputstream between offsets "+thisoffset+" and "+
		   nextoffset, Logger.DEBUG_INFORMATIONAL);
    RandomAccessInputStream in = 
      new RandomAccessInputStream (rafreader, thisoffset, nextoffset - thisoffset);
    in.setClosingCallback(closingcallback);
    return in;
    }

  /** Called by the inner class listener when any output or input stream is 
   * closed. */

  private synchronized void streamClosed() throws IOException {
    Logger.println("XMLAccessFile stream closed, state "+state,
		   Logger.DEBUG_INFORMATIONAL);
    try {
      if (state == WRITE_IN_PROGRESS) {
	writeManifest();
	}
      }
    finally {
      releaseLock();
      }
    }

  DeSAXalizer desaxalizer = new DeSAXalizer();
  
  private void writeManifest() throws IOException {
    // can only begin writing at current pos
    RandomAccessOutputStream out = new RandomAccessOutputStream(rafwriter);
    long manifestoffset = rafwriter.getFilePointer();
    // perhaps need a more efficient way of setting up DeSAXalizing than this
    desaxalizer.serializeSubtree(manifest, MANIFEST_TAG, out, 1); // indent param!
    manifestpointer.setManifestOffsetLong(manifestoffset);
    desaxalizer.serializeSubtree(manifestpointer, XMLAccessManifestPointer.MANIFEST_POINTER_TAG,
				 out, 1);
    out.write('<'); out.write ('/');
    out.write(roottagbytes);
    out.write('>'); out.write ('\n');
    out.close();
    }
  }
