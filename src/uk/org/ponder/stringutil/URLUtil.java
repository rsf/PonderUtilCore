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
  
  public static boolean isAbsolute(String url) {
    int slashpos = url.indexOf('/');
    int protpos = url.indexOf("://");
    if (slashpos == -1 || protpos == -1) return false;
    return slashpos == protpos + 1;
  }
  /** Append the supplied name/value pair to the end of the supplied URL, 
   * after URLencoding name and value.
   */
  
  public static String appendAttribute(String url, String name, String value) {
    int qpos = url.indexOf('?');
    char sep = qpos == -1? '?' : '&';
    return url + sep + URLEncoder.encode(name) + '=' + URLEncoder.encode(value);
  }
  
  /** Convert list of URL-form name/value pairs into a Map representation */
  // TODO: backport vector values code
  public static Map paramsToMap(String extraparams,
        Map target) {
      if (Logger.log.isDebugEnabled()) {
        Logger.log
          .debug("Action link requires extra parameters from " + extraparams);
      }
      StringTokenizer st = new StringTokenizer(extraparams, "&");
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        int eqpos = token.indexOf("=");
        String key = token.substring(0, eqpos);
        String value = token.substring(eqpos + 1);
        target.put(key, value);
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
  
  /** URL-encodes only the whitespace characters in a URL (necessary for some
   * faulty incomplete encodings).
   */
  
  public static String deSpace(String URL) {   
    CharWrap togo = new CharWrap(URL.length());
    for (int i = 0; i < URL.length(); ++ i) {
      char c = URL.charAt(i);
      if (Character.isWhitespace(c)) {
        URLEncoder.appendURLHex(c, togo);
      }
      else togo.append(c);
    }
    return togo.toString();
  }
  
}
