package uk.org.ponder.util;

import java.util.Enumeration;

/** An enumeration that enumerates over nothing */

public class TrivialEnumeration implements Enumeration {
  public boolean hasMoreElements() {
    return false;
    }
  public Object nextElement() {
    return null;
    }
  public TrivialEnumeration() {}
  public static final TrivialEnumeration instance = new TrivialEnumeration();
  }
