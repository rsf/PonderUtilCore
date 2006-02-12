package uk.org.ponder.conversion;

import java.util.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import uk.org.ponder.dateutil.LocalSDF;
import uk.org.ponder.util.UniversalRuntimeException;

public class DateParser implements LeafObjectParser {
  // one of these is null
  private LocalSDF format;
  private SimpleDateFormat sdf;

  public DateParser() {
    format = LocalSDF.w3cformat;
  }

  public DateParser(String dateformat) {
    sdf = new SimpleDateFormat(dateformat);
  }

  public Object parse(String bulk) {
    try {
      return sdf == null? format.get().parse(bulk): sdf.parse(bulk);
    }
    catch (ParseException pe) {
      throw UniversalRuntimeException.accumulate(pe, "Error parsing date "
          + bulk);
    }
  }

  public String render(Object torendero) {
    Date torender = (Date) torendero;
    return sdf == null ? format.format(torender): sdf.format(torender);
  }

  public Object copy(Object tocopy) {
    return new Date(((Date) tocopy).getTime());
  }
  
  public String toString() {
    return sdf == null? null : sdf.toPattern();
  }
}
