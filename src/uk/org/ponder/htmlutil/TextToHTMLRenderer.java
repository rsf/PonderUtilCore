/*
 * Created on 11-Sep-2006
 */
package uk.org.ponder.htmlutil;

import java.io.BufferedReader;
import java.io.Reader;

import uk.org.ponder.streamutil.ReaderCopier;
import uk.org.ponder.streamutil.StreamCloseUtil;
import uk.org.ponder.streamutil.write.PrintOutputStream;
import uk.org.ponder.util.UniversalRuntimeException;
import uk.org.ponder.xml.XMLWriter;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class TextToHTMLRenderer implements ReaderCopier {

  /**
   * Copies text from input to output, converting newlines into XHTML
   * &lt;br/&gt; elements. The supplied streams WILL be closed!
   */
  public void copyReader(Reader r, PrintOutputStream pos) {
    BufferedReader br = new BufferedReader(r);
    XMLWriter xmlw = new XMLWriter(pos);
    try {
      while (true) {
        String line = br.readLine();
      
        if (line == null)
          break;
        xmlw.write(line);
// TODO: make some kind of "XMLFilterWriter" architecture if necessary        
//        writeEncodeLinks(xmlw, line);
        xmlw.writeRaw("<br/>");
      }
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t,
          "Error rendering text as HTML");
    }
    finally {
      StreamCloseUtil.closeReader(r);
      pos.close();
    }
  }

  public static void writeEncodeLinks(XMLWriter xmlw, String line) {
    int linkpos = line.indexOf("://");
    if (linkpos == -1) {
      xmlw.write(line);
      return;
    }
    int backpos = linkpos - 1;
    for (; backpos >= 0; --backpos) {
      if (Character.isWhitespace(line.charAt(backpos))) break;
    }
    ++ backpos;
    if (backpos == linkpos - 1) { // require non-empty protocol
      xmlw.write(line);
      return;
    }
    
    int frontpos = linkpos + 3;
    for (; frontpos < line.length(); ++ frontpos) {
      if (Character.isWhitespace(line.charAt(backpos))) break;
    }
    String url = line.substring(backpos, frontpos);
    xmlw.write(line.substring(0, backpos));
    xmlw.writeRaw("<a target=\"_top\" href=\"");
    xmlw.write(url);
    xmlw.writeRaw("\">");
    xmlw.write(url);
    xmlw.writeRaw("</a>");
    xmlw.write(line.substring(frontpos));
  }

}
