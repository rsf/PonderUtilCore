package uk.org.ponder.saxalizer;

import java.util.TimeZone;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.xml.sax.SAXException;

import uk.org.ponder.stringutil.CharWrap;

class DateParser implements SAXLeafTypeParser {
  private SimpleDateFormat format;
  public DateParser() {
    // this represents w3c standard dates as defined in 
    // http://www.w3.org/TR/NOTE-datetime
    format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    format.setLenient(false);
    }
  public Object parse(String bulk) throws SAXException {
    try {
      return format.parse(bulk); 
      }
    catch (ParseException pe) {
      throw new SAXException(pe.getMessage());
      }
    }
  public CharWrap render(Object torendero, CharWrap renderinto) {
    Date torender = (Date)torendero;
    String rendered = format.format(torender);
    return renderinto.append(rendered);
    }
  }
