/*
 * Created on Oct 11, 2004
 */
package uk.org.ponder.saxalizer.leafparsers;

import java.io.IOException;
import java.io.StringReader;

import uk.org.ponder.saxalizer.SAXLeafTypeParser;
import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.util.LexReader;
import uk.org.ponder.util.LexUtil;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (amb26@ponder.org.uk)
 *  
 */

//QQQQQ diabolically inefficient. Need to replace parse method with reader
//from CharWrap directly.
public class intArrayParser implements SAXLeafTypeParser {
  public static intArrayParser instance = new intArrayParser();
  public Object parse(String string) {
    try {
      LexReader lr = new LexReader(new StringReader(string));
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
    catch (IOException ioe) {
      throw UniversalRuntimeException.accumulate(ioe,
          "Error reading integer vector");
    }

  }

  public CharWrap render(Object torendero, CharWrap renderinto) {
    int[] torender = (int[]) torendero;
    renderinto.append(Integer.toString(torender.length) + ": ");
    for (int i = 0; i < torender.length; ++i) {
      renderinto.append(Integer.toString(torender[i]) + " ");
    }
    return renderinto;
  }
}

