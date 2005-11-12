/*
 * Created on Oct 11, 2004
 */
package uk.org.ponder.conversion;

import java.io.IOException;
import java.io.StringReader;

import uk.org.ponder.streamutil.read.LexReader;
import uk.org.ponder.streamutil.read.LexUtil;
import uk.org.ponder.streamutil.read.PushbackRIS;
import uk.org.ponder.streamutil.read.StringRIS;
import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (amb26@ponder.org.uk)
 *  
 */

//QQQQQ diabolically inefficient. Need to replace parse method with reader
//from CharWrap directly.
public class intArrayParser implements LeafObjectParser {
  public static intArrayParser instance = new intArrayParser();
  public Object parse(String string) {
    try {
      PushbackRIS lr = new PushbackRIS(new StringRIS(string));
      int size = LexUtil.readInt(lr);
      int[] togo = new int[size];
      LexUtil.expect(lr, ":");
      for (int i = 0; i < size; ++i) {
        LexUtil.skipWhite(lr);
        try {
          togo[i] = LexUtil.readInt(lr);
        }
        catch (Exception e) {
          UniversalRuntimeException.accumulate(e,
              "Error reading integer vector at position " + i + " of expected "
                  + size);
        }
      }
      LexUtil.skipWhite(lr);
      LexUtil.expectEmpty(lr);
      return togo;
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e,
          "Error reading integer vector");
    }

  }

  public String render(Object torendero) {
    int[] torender = (int[]) torendero;
    CharWrap renderinto = new CharWrap(torender.length * 5);
    renderinto.append(Integer.toString(torender.length) + ": ");
    for (int i = 0; i < torender.length; ++i) {
      renderinto.append(Integer.toString(torender[i]) + " ");
    }
    return renderinto.toString();
  }
}

