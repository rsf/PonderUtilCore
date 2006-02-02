/*
 * Created on 02-Feb-2006
 */
package uk.org.ponder.stringutil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.StringTokenizer;

import uk.org.ponder.util.Logger;
import uk.org.ponder.util.UniversalRuntimeException;

public class URLUtil {

  public static Map paramsToMap(String extraparams,
        Map target) {
      Logger.log
          .info("Action link requires extra parameters from " + extraparams);
      StringTokenizer st = new StringTokenizer(extraparams, "&");
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        int eqpos = token.indexOf("=");
        String key = token.substring(0, eqpos);
        String value = token.substring(eqpos + 1);
        target.put(key, value);
  //      target.add(new UIParameter(key, value));
        Logger.log.info("Added extra parameter key " + key + " value " + value
            + " to command link");
      }
      return target;
    }

  public static String[] splitPathInfo(String pathinfo) {
    return pathinfo.split("/");
  }

  /** Decodes a URL assuming UTF-8, and wrapping any tedious exceptions */
  public static String decodeURL(String url) {
    try {
      return URLDecoder.decode(url, "UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      throw UniversalRuntimeException.accumulate(e, "Error decoding URL " + 
          url + " using UTF-8");
    }
    
  }
  
}
