package uk.org.ponder.xml;

import uk.org.ponder.byteutil.ByteWrap;
import uk.org.ponder.conversion.LeafObjectParser;
import uk.org.ponder.conversion.StaticLeafParser;

import uk.org.ponder.stringutil.ByteToCharBase64;
import uk.org.ponder.stringutil.CharToByteBase64;
import uk.org.ponder.stringutil.CharWrap;

/** RandomAccessPointer represents an 8-byte pointer into an XMLAccessFile. Since it
 * implements SAXLeafTypeParser, RandomAccessPointers may be used as leaf nodes and
 * attribute values together with the SAXalizer. The pointer value is serialized as
 * an 11-character Base64-encoded value.
 */

public class RandomAccessPointer implements LeafObjectParser {
  static {
    StaticLeafParser.instance().registerParser(RandomAccessPointer.class, new RandomAccessPointer());
    }

  public long offset;

  /** Constructs a RandomAccessPointer with initial offset 0. */
  public RandomAccessPointer() {}

  /** Constructs a RandomAccessPointer with the specified offset.
   * @param offset The required byte offset within the file.
   */
  public RandomAccessPointer(long offset) {
    this.offset = offset;
    }

  public Object parse(String toparse) {
    // all this will go away with the new parser
    int length = toparse.length();
    System.out.println("Parsing randomaccesspointer with length "+length);
    char[] unnecessarybuffer = new char[length];
    toparse.getChars(0, length, unnecessarybuffer, 0);
    byte[] bytes = CharToByteBase64.decode(unnecessarybuffer, 0, length);
    ByteWrap wrap = new ByteWrap(bytes, bytes.length - 8, 8);
    return new RandomAccessPointer(wrap.read_at8(0));
    }

  public String render(Object torendero) {
    RandomAccessPointer torender = (RandomAccessPointer)torendero;
    CharWrap renderinto = new CharWrap(12); 
    ByteToCharBase64.writeLong(renderinto, torender.offset, true);
    return renderinto.toString();
    }
  }
