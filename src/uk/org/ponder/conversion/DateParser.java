package uk.org.ponder.conversion;

import java.util.Date;

import java.text.ParseException;

import uk.org.ponder.stringutil.LocalSDF;
import uk.org.ponder.util.UniversalRuntimeException;

class DateParser implements LeafObjectParser {
  private LocalSDF format;
  public DateParser() {
    format = LocalSDF.w3cformat;
    }
  public Object parse(String bulk) {
    try {
      return format.get().parse(bulk); 
      }
    catch (ParseException pe) {
      throw UniversalRuntimeException.accumulate(pe, "Error parsing date");
      }
    }
  public String render(Object torendero) {
    Date torender = (Date)torendero;
    String rendered = format.format(torender);
    return rendered;
    }
  public Object copy(Object tocopy) {
    return new Date(((Date) tocopy).getTime());
  }
}
