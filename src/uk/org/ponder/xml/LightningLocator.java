package uk.org.ponder.xml;

import org.xml.sax.Locator;

public class LightningLocator implements Locator {
  public int linenumber;
  public int columnnumber;
  
  public void clear() {
    linenumber = 1;
    columnnumber = 0;
    }
  
  public int getColumnNumber() {
    return columnnumber;
    }

  public int getLineNumber() {
    return linenumber;
    }

  public String getPublicId() {
    return null;
    }

  public String getSystemId() {
    return null;
    }

  }
