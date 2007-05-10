/*
 * Created on 11-Feb-2006
 */
package uk.org.ponder.conversion;

/** This slightly peculiar class makes it easy to represent DateParsers
 * in an XML object tree.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public class DateParserParser implements LeafObjectParser {

  public Object parse(String toparse) {
    return new DateParser(toparse);
  }

  public String render(Object torendero) {
    DateParser torender = (DateParser) torendero;
    return torender.toString();
  }

  public Object copy(Object tocopyo) {
    DateParser tocopy = (DateParser) tocopyo;
    return new DateParser(tocopy.toString());
  }

}
