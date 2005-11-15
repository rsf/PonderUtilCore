/*
 * Created on Nov 11, 2005
 */
package uk.org.ponder.conversion;

import uk.org.ponder.streamutil.read.LexUtil;
import uk.org.ponder.streamutil.read.PushbackRIS;
import uk.org.ponder.streamutil.read.StringRIS;
import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.util.UniversalRuntimeException;

public class StringArrayParser implements LeafObjectParser {
  public static StringArrayParser instance = new StringArrayParser();
  
  public Object parse(String toparse) {
    PushbackRIS lr = new PushbackRIS(new StringRIS(toparse));
    int size = LexUtil.readInt(lr);
    if (size > toparse.length() / 2) {
      throw UniversalRuntimeException.accumulate(new IllegalArgumentException(), 
          "Possible DOS attack! Impossibly long string array encoded");
    }
    String[] togo = new String[size];
    LexUtil.expect(lr, ":");
    CharWrap readbuffer = new CharWrap();
    for (int i = 0; i < size; ++i) {
      try {
        int length = LexUtil.readInt(lr);
        if (size > toparse.length()) {
          throw UniversalRuntimeException.accumulate(new IllegalArgumentException(), 
              "Possible DOS attack! Impossibly long string encoded");
        }
        LexUtil.expect(lr, ":");
        readbuffer.ensureCapacity(length);
        lr.read(readbuffer.storage, 0, length);
        togo[i] = new String(readbuffer.storage, 0, length);
      }
      catch (Exception e) {
        UniversalRuntimeException.accumulate(e,
            "Error reading integer vector at position " + i + " of expected "
                + size);
      }
    }
    return togo;
  }

  public String render(Object torendero) {
    String[] torender = (String[]) torendero;
    CharWrap togo = new CharWrap();
    togo.append(torender.length).append(':');
    for (int i = 0; i < torender.length; ++ i) {
      String string = torender[i];
      togo.append(string.length()).append(':').append(string);
    }
    return togo.toString();
  }

}
