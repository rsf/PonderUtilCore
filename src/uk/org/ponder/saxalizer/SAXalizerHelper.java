package uk.org.ponder.saxalizer;

import org.xml.sax.HandlerBase;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import org.xml.sax.InputSource;
import org.xml.sax.AttributeList;
import org.xml.sax.SAXParseException;
import org.xml.sax.Parser;

import uk.org.ponder.saxalizer.mapping.ClassNameManager;
import uk.org.ponder.streamutil.StreamCloseUtil;
import uk.org.ponder.util.Logger;
import uk.org.ponder.util.UniversalRuntimeException;

// QQQQQ interface to be supplied in the CORRECT direction!

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

/** This useful helper class can be used if the XML
 * root tag itself is required to be the root of the deserialised
 * object tree. In this case, we can avoid direct use of the SAXalizer and
 * the SAX API itself, and merely pass in an InputStream attached to the
 * XML document. The SAXalizerHelper class parses the stream and
 * returns the single object corresponding to the XML root node,
 * at the head of the tree of deserialised Java objects.
 */

public class SAXalizerHelper extends HandlerBase {
  private Object rootobjstash;
  private Parser parserstash;
  private EntityResolverStash entityresolverstash;
  private SAXalizer saxer;
  private ClassNameManager classnamemanager;

  public SAXalizerHelper() {
    this(SAXalizerMappingContext.instance());
  }

  public SAXalizerHelper(SAXalizerMappingContext mappingcontext) {
    saxer = new SAXalizer(mappingcontext);
    parserstash = SAXParserFactory.newParser();
    classnamemanager = mappingcontext.classnamemanager;
  }
  
  private ParseCompleteCallback callback;
  private int callbackindex;

  void setParseCompleteCallback(ParseCompleteCallback callback, int index) {
    this.callback = callback;
    this.callbackindex = index;
  }

  public void setEntityResolverStash(EntityResolverStash entityresolverstash) {
    this.entityresolverstash = entityresolverstash;
    saxer.setEntityResolverStash(entityresolverstash);
  }

  public Object produceSubtree(Object rootobj, Reader reader) throws SAXException {
    InputSource i = new InputSource(reader);
    //i.setSystemId("SAXalizing page");
    try {
    return produceSubtreeInternal(rootobj, i);
    }
    finally {
      StreamCloseUtil.closeReader(reader);
    }
  }
  
  /** This method parses a stream attached to an XML document, and returns
   * a deserialised object tree corresponding to its root node.
   * @param rootobj The required root object to receive the SAXalized subtree. This
   * class must implement (at least) the SAXalizable interface.
   * @param stream A stream attached to an XML document.
   This stream WILL be closed by this call.
   * @exception SAXException if the stream could not be parsed as XML.
   * @return An object representing the XML root node.
   */
  // Currently closes the stream
  public Object produceSubtree(Object rootobj, InputStream stream) throws SAXException {
    InputSource i = new InputSource(stream);
    //i.setSystemId("SAXalizing page");
    try {
      return produceSubtreeInternal(rootobj, i);
    }
    finally {
      StreamCloseUtil.closeInputStream(stream);
    }
  }

  
  private Object produceSubtreeInternal(Object rootobj, InputSource i) throws SAXException {
    parserstash.setDocumentHandler(this);
    parserstash.setEntityResolver(this);
    try {
      rootobjstash = rootobj;
      parserstash.parse(i); // begin to parse at this point, asynchronous events arrive
    }
    catch (SAXParseException spe) {
      Logger.println(
        "SaxParseException occured at line "
          + spe.getLineNumber()
          + " column number "
          + spe.getColumnNumber(),
        Logger.DEBUG_SEVERE);
      Logger.println(spe.getMessage(), Logger.DEBUG_SEVERE);
      Exception e = spe.getException();
      if (e != null) {
        Logger.println("Embedded exception was: ", Logger.DEBUG_SEVERE);
        Logger.printStackTrace(e, Logger.DEBUG_SEVERE);
      }
      throw spe;
    }
    catch (SAXException se) {
      Logger.println("SAXException received");
      Exception e = se.getException();
      if (e != null) {
        Logger.println("Embedded exception was: ", Logger.DEBUG_SEVERE);
        Logger.printStackTrace(e, Logger.DEBUG_SEVERE);
      }
      else {
        Logger.println("No embedded exception", Logger.DEBUG_SEVERE);
      }
      throw se;
    }
    catch (Exception e) {
      // This branch should never be reached
      Logger.printStackTrace(e, Logger.DEBUG_SEVERE);
      throw new SAXException(e.toString());
    }
    finally {
      saxer.blastState();
      if (callback != null) {
        callback.parseComplete(callbackindex);
        callback = null;
      }
    }
    return rootobjstash;
  }
  
  public void setDocumentLocator(Locator l) {
    saxer.setDocumentLocator(l);
  }

  /** Implements the DocumentHandler interface. This method is only implemented to
   * intercept the very first opening tag for the root object, at which point
   * handling is forwarded to the internal SAXalizer object.
   * @param tagname The tag name for the element just seen in the SAX stream.
   * @param attrlist The attribute list of the tag just seen in the SAX stream.
   * @exception SAXException If any exception requires to be propagated from this
   * interface.
   */

  public void startElement(String tagname, AttributeList attrlist) throws SAXException {
    //    System.out.println("ELEMENT received: "+tagname);
    if (rootobjstash == null) {
      try {
        Class objclass = classnamemanager.findClazz(tagname);
        rootobjstash = objclass.newInstance();
      }
      catch (Throwable t) {
        throw UniversalRuntimeException.accumulate(t, "Tag name " + tagname + 
            " has not been mapped onto a default constructible object");
      }
    }
    // remember that the following production occurs asynchronously, driven
    // by events from the SAX stream.
    saxer.produceSubtree(rootobjstash, attrlist, null);
    // hand over ownership of the SAX stream to the SAXalizer
    parserstash.setDocumentHandler(saxer);
  }

  public InputSource resolveEntity(String publicID, String systemID) {
    //    System.out.println("Helper was asked to resolve public ID "+publicID+" systemID "+systemID);
    return entityresolverstash == null ? null : entityresolverstash.resolve(publicID);
  }

}
