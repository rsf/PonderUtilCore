package uk.org.ponder.saxalizer;

import java.util.Date;

import java.text.ParseException;

import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.stringutil.LocalSDF;
import uk.org.ponder.util.UniversalRuntimeException;

class DateParser implements SAXLeafTypeParser {
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
  public CharWrap render(Object torendero, CharWrap renderinto) {
    Date torender = (Date)torendero;
    String rendered = format.format(torender);
    return renderinto.append(rendered);
    }
  }
