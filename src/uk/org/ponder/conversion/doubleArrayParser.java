/*
 * Created on Nov 25, 2005
 */
package uk.org.ponder.conversion;

import uk.org.ponder.streamutil.read.LexUtil;
import uk.org.ponder.streamutil.read.PushbackRIS;
import uk.org.ponder.streamutil.read.StringRIS;
import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.util.UniversalRuntimeException;

// QQQQQ diabolically inefficient. Need to replace parse method with reader
// from CharWrap directly.
public class doubleArrayParser implements LeafObjectParser {
  public Object parse(String string) {
    try {
      PushbackRIS lr = new PushbackRIS(new StringRIS(string));
      int size = LexUtil.readInt(lr);
      double[] togo = new double[size];
      LexUtil.expect(lr, ":");
      for (int i = 0; i < size; ++i) {
        LexUtil.skipWhite(lr);
        try {
          togo[i] = LexUtil.readDouble(lr);
        }
        catch (Exception e) {
          UniversalRuntimeException.accumulate(e,
              "Error reading double vector at position " + i + " of expected "
                  + size);
        }
      }
      LexUtil.skipWhite(lr);
      LexUtil.expectEmpty(lr);
      return togo;
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e,
          "Error reading double vector");
    }
  }

  public String render(Object torendero) {
    double[] torender = (double[]) torendero;
    CharWrap renderinto = new CharWrap(torender.length * 15);
    renderinto.append(Integer.toString(torender.length) + ": ");
    for (int i = 0; i < torender.length; ++i) {
      renderinto.append(Double.toString(torender[i]) + " ");
    }
    return renderinto.toString();
  }

  public Object copy(Object tocopyo) {
    double[] tocopy = (double[]) tocopyo;
    double[] copy = new double[tocopy.length];
    System.arraycopy(tocopy, 0, copy, 0, tocopy.length);
    return copy;
  }
}