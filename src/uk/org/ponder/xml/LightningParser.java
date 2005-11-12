package uk.org.ponder.xml;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.DocumentHandler;
//import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

import uk.org.ponder.util.AssertionException;

import uk.org.ponder.arrayutil.ArrayUtil;

import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.stringutil.CharParser;
import uk.org.ponder.stringutil.EncodingErrorHandler;

import uk.org.ponder.streamutil.DirectInputStreamReader;
import uk.org.ponder.streamutil.StreamCopyUtil;

/** <code>LightningParser</code> implements a fast parser of a very
 * minimal subset of the XML language. This subset supports only
 * documents encoded in UTF-8, only the built-in entities
 * <code>&amp;amp;</code>, <code>&amp;quot;</code>,
 * <code>&amp;gt;</code>, <code>&amp;lt;</code>,
 * <code>&amp;apos;</code> and the character code entities, and
 * ignores all DTDs, processing instructions and declarations
 * encountered. Because of this, it can be safely directed into a
 * fragment of an XML stream at any position not breaking a tag or
 * entity reference and it will report the same events that a SAX
 * parser would report when passing through that portion of the
 * stream.

 * This class does not implement the <code>SAXParser</code> interface,
 * although it does supply SAX events through the usual
 * <code>DocumentHandler</code> interface. 
 *
 * The speed of the parser is currently limited by the need to create
 * <code>String</code> objects for tag names and attributes for the
 * <code>DocumentHandler</code>. This could be improved by a
 * pre-interning strategy for tag and attribute names, which are
 * frequently repeated, and raised to blistering levels by doing away
 * with <code>DocumentHandler</code> entirely and reporting all names
 * as character array chunks.
 * 
 * <br> Don't try to use this! It was never finished!
 * */

public class LightningParser {
  private DocumentHandler documenthandler;
  private EncodingErrorHandler errorhandler;
  private DirectInputStreamReader reader = new DirectInputStreamReader();
  private LightningAttributeList attributelist = new LightningAttributeList();
  private LightningLocator locator = new LightningLocator();

  public LightningParser(InputStream input, DocumentHandler handler) throws SAXException {
    parse(input, handler);
    }
  
  public void setEncodingErrorHandler(EncodingErrorHandler errorhandler) {
    this.errorhandler = errorhandler;
    }

  public void setDocumentHandler(DocumentHandler documenthandler) {
    this.documenthandler = documenthandler;
    }

  public void parse(InputStream input, DocumentHandler documenthandler) 
    throws SAXException {
    if (this.reader != null) {
      throw new AssertionException("Attempt to reuse LightningParser while parse in progress");
      }

    reader.setInputStream(input);
    reader.setEncodingErrorHandler(errorhandler);
    setDocumentHandler(documenthandler);

    blastState();
    documenthandler.setDocumentLocator(locator);
    try {
      parseInternal();
      }
    catch (IOException e) {
      throw new SAXException(e);
      }
    }
  
  private void blastState() {
    attributelist.clear();
    locator.clear();
    tagdepth = 0;
    inbufferpos = 0;
    inbufferlimit = 0;
    outbuffer.clear();
    state = FREE_TEXT;
    stashed_state = FREE_TEXT; // state is pushed during entity parsing
    }

  private void signalError(String errorstring) throws SAXParseException {
    // also have the option to use ErrorHandler here
    throw new SAXParseException(errorstring, locator);
    }
  
  private static boolean isXMLWhitespace(char c) {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }
  private static boolean isXMLNameStart(char c) {
    // to check: whether XML definition of "letter" agrees with Java's (could take forever)
    return Character.isLetter(c) || c == '_' || c == ':';
    }
  private static boolean isXMLNameChar(char c) {
    return Character.isUnicodeIdentifierPart(c) || c == '.' || c == '-' || c == '_'
      || c == ':';
    }
 
  private void acceptInput() throws IOException {
    int charsread = reader.read(inbuffer, 0, inbuffer.length);
    if (charsread != -1) {
      inbufferlimit = charsread;
      inbufferpos = 0;
      eof = false;
      }
    else {
      inbufferlimit = 0;
      inbufferpos = 0;
      eof = true;
      }
    }
  
  private void emitCharacters() throws SAXException {
    if (outbuffer.size() > 0) {
      documenthandler.characters(outbuffer.storage, 0, outbuffer.size());
      outbuffer.clear();
      }
    }

  private void outputChar(char tooutput) throws SAXException {
    if (outbuffer.size() == outbuffer.capacity()) {
      emitCharacters();
      }
    outbuffer.append(tooutput);
    }

  private void outputEntity(CharWrap entitybuffer) throws SAXException {
    boolean recognised = false;
    if (entitybuffer.size() < 2) {
      recognised = false;
      }
    else {
      char c0 = entitybuffer.charAt(0);
      char c1 = entitybuffer.charAt(1);
      if (c0 == '#') { // character entity
	int charvalue = 0xfffd;
	try {
	  if (c1 == 'x') { // hex char entity
	    charvalue = CharParser.parseHexInt(entitybuffer.storage, 
					       2, entitybuffer.size() - 2);
	    }
	  else {
	    charvalue = CharParser.parsePositiveInt(entitybuffer.storage, 
						    1, entitybuffer.size() - 1);
	    }
	  }
	catch (NumberFormatException nfe) {
	  signalError(nfe.getMessage());
	  }
	if (charvalue > 65535) {
	  signalError("Character value out of Unicode 3.0 range: "+charvalue);
	  }
	outputChar( (char)charvalue);
	}
      else {
	if (c0 == 'a' && c1 == 'p' && 
	    entitybuffer.size() == 4 && entitybuffer.charAt(2) == 'o' &&
	    entitybuffer.charAt(3) == 's') {
	  recognised = true;
	  outputChar('\'');
	  }
	else if (c0 == 'q' && c1 == 'u' &&
		 entitybuffer.size() == 4 && entitybuffer.charAt(2) == 'o' &&
		 entitybuffer.charAt(3) == 't') {
	  recognised = true;
	  outputChar('"');
	  }
	else if (c0 == 'a' && c1 == 'm' &&
		 entitybuffer.size() == 3 && entitybuffer.charAt(2) == 'p') {
	  recognised = true;
	  outputChar('&');
	  }
	else if (c0 == 'g' && c1 == 't' && entitybuffer.size() == 2) {
	  recognised = true;
	  outputChar('>');
	  }
	else if (c0 == 'l' && c1 == 't' && entitybuffer.size() == 2) {
	  recognised = true;
	  outputChar('>');
	  }
	} // end if non-numeric entity
      } // end if long enough
    if (!recognised)
      signalError("Unrecognised entity "+entitybuffer+" found");
    else {
      state = stashed_state; // pop the state back
      }
    }

  private void beginEntity() {
    entitybuffer.clear();
    stashed_state = state;
    state = ENTITY_START;
    }

  private String stashedattributename;

  private void stashAttributeName() {
    stashedattributename = outbuffer.toString().intern();
    }

  private void stashAttributeValue() {
    attributelist.addAttribute(stashedattributename, null, outbuffer.toString());
    attributelist = null;
    }

  private void emitStartElement() throws SAXException {
    documenthandler.startElement(tagstack[tagdepth], attributelist);
    // destroy this as soon as possible
    attributelist.clear();
    }
  
  private void emitEndElement(boolean empty) throws SAXException {
    String openingtagname = tagstack[tagdepth - 1];
    String closingtagname = empty? openingtagname: outbuffer.toString();
    outbuffer.clear();
    if ( !(closingtagname.equals(openingtagname))) {
      signalError("Closing tag name " + closingtagname + " does not match" +
		  " opening tag name " + openingtagname);
      }
    -- tagdepth;
    documenthandler.endElement(closingtagname);
    }

  private String[] tagstack = new String[32];
  int tagdepth;

  private void stashTagName() {
    if (tagdepth == tagstack.length) {
      tagstack = (String[]) ArrayUtil.expand(tagstack, 2.0);
      }
    // intern better here
    String tagname = outbuffer.toString().intern();
    outbuffer.clear();
    tagstack[tagdepth++] = tagname;
    }

  private static final int FREE_TEXT = 0;
  private static final int OPEN_ANGLE_BRACKET = 1;
  private static final int OPENING_TAG_TEXT  = 2;
  private static final int AFTER_OPENING_TAG = 3;
  private static final int ATTR_NAME_TEXT = 4;
  private static final int BEFORE_EQUALS = 5;
  private static final int AFTER_EQUALS = 6;
  private static final int ATTR_VAL_TEXT = 7;
  private static final int CLOSING_TAG_TEXT = 8;
  private static final int AFTER_CLOSING_TAG = 9;
  private static final int POSSIBLE_EMPTY_TAG = 10;
  private static final int ENTITY_START = 11;
  private static final int ENTITY_TEXT = 12;
  // top line is state BEFORE examining character
  // 01222344444566777777777334444456677777777730000000000001888890
  // <tag attr1 = "attrval1" attr2 = "attrval2"> free text </tag >

  private boolean eof;

  private char[] inbuffer = new char[StreamCopyUtil.PROCESS_BUFFER_SIZE];
  private int inbufferpos;
  private int inbufferlimit;
  private CharWrap outbuffer = new CharWrap(256);

  private CharWrap entitybuffer = new CharWrap(8);
  private int state;
  private int stashed_state;

  private void parseInternal() throws IOException, SAXException {
    char quotingchar = ' '; // the character used to quote the current attribute value

    acceptInput();
    while (true) {
      char c = inbuffer[inbufferpos];
      switch (state) {
      case FREE_TEXT:
	if (c == '<') {
	  emitCharacters();
	  state = OPEN_ANGLE_BRACKET;
	  }
	else if (c == '&') {
	  stashed_state = state;
	  state = ENTITY_START;
	  }
	else {
	  outputChar(c);
	  }
	break;
      case OPEN_ANGLE_BRACKET:
	if (c == '/') {
	  state = CLOSING_TAG_TEXT;
	  }
	else if (isXMLNameStart(c)) {
	  state = OPENING_TAG_TEXT;
	  outputChar(c);
	  }
	else {
	  signalError("Invalid character "+c+" starting XML tag name");
	  }
	break;
      case OPENING_TAG_TEXT:
      case CLOSING_TAG_TEXT:
	if (isXMLNameChar(c)) {
	  outputChar(c);
	  }
	else if (isXMLWhitespace(c)) {
	  state = (state == OPENING_TAG_TEXT? AFTER_OPENING_TAG: AFTER_CLOSING_TAG);
	  }
	else if (c == '>') {
	  if (state == OPENING_TAG_TEXT) emitStartElement(); else emitEndElement(false);
	  state = FREE_TEXT;
	  }
	else if (c == '/' && state == OPENING_TAG_TEXT) {
	  state = POSSIBLE_EMPTY_TAG;
	  }
	else {
	  signalError("Invalid character "+c+" within XML tag name");
	  }
	break;
      case AFTER_OPENING_TAG:
	if (isXMLNameStart(c)) {
	  outputChar(c);
	  state = ATTR_NAME_TEXT;
	  }
	else if (c == '>') {
	  emitStartElement();
	  state = FREE_TEXT;
	  }
	else if (c == '/') {
	  state = POSSIBLE_EMPTY_TAG;
	  }
	else if (!isXMLWhitespace(c)) {
	  signalError("Invalid character "+c+" instead of attribute name");
	  }
	break;
      case POSSIBLE_EMPTY_TAG:
	if (c == '>') {
	  emitStartElement();
	  emitEndElement(true);
	  state = FREE_TEXT;
	  }
	else {
	  signalError("Character > expected terminating empty tag");
	  }
	break;
      case AFTER_CLOSING_TAG:
	if (c == '>') {
	  emitEndElement(false);
	  state = FREE_TEXT;
	  }
	else if (!isXMLWhitespace(c)) {
	  signalError("Invalid character "+c+" after closing tag name");
	  }
	break;
      case ATTR_NAME_TEXT:
	if (isXMLNameChar(c)) {
	  outputChar(c);
	  }
	else if (isXMLWhitespace(c)) {
	  stashAttributeName();
	  state = BEFORE_EQUALS;
	  }
	else if (c == '=') {
	  stashAttributeName();
	  state = AFTER_EQUALS;
	  }
	else {
	  signalError("Invalid character "+c+" found within attribute name");
	  }
	break;
      case BEFORE_EQUALS:
	if (c == '=') {
	  state = AFTER_EQUALS;
	  }
	else if (!isXMLWhitespace(c)) {
	  signalError("Invalid character "+c+" found instead of =");
	  }
	break;
      case AFTER_EQUALS:
	if (c == '"' || c == '\'') {
	  quotingchar = c;
	  state = ATTR_VAL_TEXT;
	  }
	else if (!isXMLWhitespace(c)) {
	  signalError("Invalid character "+c+" found instead of attribute value quote");
	  }
	break;
      case ATTR_VAL_TEXT:
	if (c == quotingchar) {
	  stashAttributeValue();
	  state = AFTER_OPENING_TAG;
	  }
	else if (c == '&') {
	  beginEntity();
	  }
	break;
      case ENTITY_START:
	if (c == '#' || isXMLNameStart(c)) {
	  entitybuffer.append(c);
	  state = ENTITY_TEXT;
	  }
	else {
	  signalError("Invalid character "+c+" found beginning entity reference");
	  }
	break;
      case ENTITY_TEXT:
	if (c == ';') {
	  outputEntity(entitybuffer);
	  state = stashed_state;
	  }
	break;
	}
      ++ inbufferpos;
      if (inbufferpos == inbufferlimit) {
	if (eof) {
	  emitCharacters();
	  reader = null;
	  return;
	  }
	else {
	  acceptInput();
	  }
	}
      } // end big loop
    }

  }
