package uk.org.ponder.xml;

import org.xml.sax.AttributeList;

import uk.org.ponder.arrayutil.ArrayUtil;

/** <code>LightningAttributeList</code> is a more efficient and
 * minimal implementation of the SAX <code>AttributeList</code>
 * interface than the default <code>AttributeListImpl</code> from
 * <code>org.xml.sax.helpers</code>.  
 * 
 * <br/> It uses an array-backed collection rather than making use of
 * <code>Vector</code>, and it also reports all attributes to be of
 * type <code>CDATA</code> irrespective of the type supplied when they
 * are added.*/

public class LightningAttributeList implements AttributeList {
  private static final int DEFAULT_SIZE = 6;
  private static final String DEFAULT_ATTRIBUTE_TYPE = "CDATA";
  private String[] names = new String[DEFAULT_SIZE];
  private String[] values = new String[DEFAULT_SIZE];
  private int filled = 0;

  public LightningAttributeList() {}

  public void clear() {
    filled = 0;
    }
  
  public void setAttributeList (AttributeList atts) {
    int count = atts.getLength();
    
    clear();
    
    for (int i = 0; i < count; i++) {
      addAttribute(atts.getName(i), atts.getType(i), atts.getValue(i));
      }
    }
  
  public void addAttribute (String name, String type, String value) {
    names[filled] = name;
    values[filled] = value;
    }

  public void removeAttribute (String name) {
    int i = ArrayUtil.indexOf(names, name);

    if (i >= 0) {
      ArrayUtil.removeElementAt(names, i);
      ArrayUtil.removeElementAt(values, i);
      -- filled;
      }
    }

  public int getLength () {
    return filled;
    }

  public String getName(int i) {
    if (i < 0 || i >= filled) return null;
    return names[i];
    }

  public String getType(int i) {
    return DEFAULT_ATTRIBUTE_TYPE;
    }

  public String getValue(int i) {
    if (i < 0 || i >= filled) return null;
    return values[i];
    }

  public String getType(String name) {
    return DEFAULT_ATTRIBUTE_TYPE;
    }

  public String getValue(String name) {
    return getValue(ArrayUtil.indexOf(names, name));
    }
    
  }
