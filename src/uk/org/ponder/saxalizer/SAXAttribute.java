package uk.org.ponder.saxalizer;

// This is like the DOM class Attr!
/** This class represents an XML attribute. */

public class SAXAttribute {
  String type;
  String value;
  SAXAttribute(String type, String value) {
    this.type = type; this.value = value;
    }
  /** Returns the attribute's value.
   * @return The required attribute value.
   */
  public String getValue() {
    return value;
    }
  }
