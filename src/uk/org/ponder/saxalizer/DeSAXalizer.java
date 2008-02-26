package uk.org.ponder.saxalizer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uk.org.ponder.arrayutil.ListUtil;
import uk.org.ponder.iterationutil.EnumerationConverter;
import uk.org.ponder.saxalizer.support.MethodAnalyser;
import uk.org.ponder.saxalizer.support.SAMIterator;
import uk.org.ponder.saxalizer.support.SAXAccessMethod;
import uk.org.ponder.util.Logger;
import uk.org.ponder.util.UniversalRuntimeException;
import uk.org.ponder.xml.XMLWriter;

/**
 * The DeSAXalizer takes a tree of Java objects and writes them to an XML
 * stream. Architectural note - this used to be a stateless class with only
 * static methods. This resulted in a slightly cleaner design since all state
 * alterations were transparent, but in the end the number of arguments to
 * internal methods began to snowball. Constructing a DeSAXalizer costs
 * virtually nothing in the modern world.
 */
public class DeSAXalizer {
  // one of these objects is allocated in a stack for each tag level.
  static class SerialContext {
    Object object; // The object being serialised
    MethodAnalyser ma;
    // the current index through the get methods
    SAMIterator getenum;
    // enumeration for a vector tag, while enum is active, currentgetindex
    // progress
    // is suspended. enum will be set to null when it expires.
    Enumeration enumm;
    // The (original) tag name of the enum in progress
    // String enumtagname;
    String stashedclosingtag;
    boolean writtenchild = false;

    SerialContext(Object object, SAXalizerMappingContext mappingcontext) {
      this.object = object;
      this.ma = mappingcontext.getAnalyser(object.getClass());
      // for generic objects, currentgetindex has an honorary value of -1
      // representing
      // getChildEnum()
      // currentgetindex = object instanceof GenericSAX ? -1 : 0;
      stashedclosingtag = null;
      getenum = ma.tagmethods.getGetEnumeration();
    }
  }

  // These five members constitute the state of the DeSAXalizer.
  // This is a Stack of serialcontexts.
  private List desaxingobjects;

  public SerialContext getDeSAXingObject() {
    return (SerialContext) ListUtil.peek(desaxingobjects);
  }

  private XMLWriter xmlw;
  private SAXalizerMappingContext mappingcontext;
  private DeSAXalizerForbidder forbidder = null;

  public DeSAXalizer() {
    this(SAXalizerMappingContext.instance());
  }

  public DeSAXalizer(SAXalizerMappingContext mappingcontext) {
    this.mappingcontext = mappingcontext;
  }

  private int indentlevel;

  public int getIndent() {
    return indentlevel == COMPACT_MODE ? COMPACT_MODE
        : indentlevel + desaxingobjects.size();
  }

  public void setForbidder(DeSAXalizerForbidder forbidder) {
    this.forbidder = forbidder;
  }

  private void blastState() {
    xmlw = null;
    desaxingobjects.clear();
    forbidder = null;
  }

  /**
   * Render a <code>DeSAXalizable</code> object as a string.
   * 
   * @param root The object to be rendered as a string.
   * @param roottag The root tag of the rendered tree
   * @return The supplied object rendered as a <code>String</code> of XML
   *         tags, minus declaration.
   */

  public String toString(Object root, String roottag, boolean compactmode) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    if (root == null)
      return "null";
    try {
      serializeSubtree(root, roottag, baos, compactmode? COMPACT_MODE : 0);
      baos.close();
      return baos.toString(XMLWriter.DEFAULT_ENCODING);
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t, "Error writing object for "
          + roottag);
    }
  }

  /**
   * Write a serialized representation of an object subtree as XML to an output
   * stream. This method DOES NOT close the supplied output stream.
   * 
   * @param root The root object of the tree to be serialized. Must implement
   *          the <code>DeSAXalizable</code> interface, as must all
   *          descendents, or have types registered with the
   *          <code>SAXLeafParser</code>.
   * @param roottag The tag to be supplied to the root object (all subobjects
   *          have their tags supplied through the <code>DeSAXalizable</code>
   *          interface.
   * @param os The output stream to receive the serialized data. The data will
   *          be written in UTF-8 format.
   */

  public void serializeSubtree(Object root, String roottag, OutputStream os) {
    serializeSubtree(root, roottag, os, 0);
  }

  private void appendAttr(String attrname, Object attrvalue) {
    xmlw.writeRaw(" ");
    xmlw.writeRaw(attrname); // attribute names may not contain escapes
    xmlw.writeRaw("=\"");
    xmlw.write(mappingcontext.generalLeafParser.render(attrvalue));
    xmlw.writeRaw("\"");
  }

  private void renderAttrs(Object torender, SAMIterator getattrenum) {
    for (; getattrenum.valid(); getattrenum.next()) {
      SAXAccessMethod getattr = getattrenum.get();
      Object attrvalue = getattr.getChildObject(torender);
      Logger.println("Attr " + getattr.tagname + ": returned object "
          + attrvalue, Logger.DEBUG_SUBATOMIC);
      if (attrvalue != null) {
        appendAttr(getattr.tagname, attrvalue);
      }
    }
    // now find and write any extra attributes
    if (torender instanceof SAXalizableExtraAttrs) {
      Map extraattrs = ((SAXalizableExtraAttrs) torender).getAttributes();
      if (extraattrs != null) {
        Iterator attrs = extraattrs.keySet().iterator();
        while (attrs.hasNext()) {
          String attributename = (String) attrs.next();
          String attributevalue = (String) extraattrs.get(attributename);
          appendAttr(attributename, attributevalue);
        }
      }
    }
  }

  private SerialContext writeOpeningTag(Object child, String childtagname,
      SAXAccessMethod topgetmethod) {
    SerialContext top = null;
    try {
      SerialContext oldtop = getDeSAXingObject();
      if (oldtop != null && !oldtop.writtenchild) {
        xmlw.writeRaw(">");
        if (indentlevel != COMPACT_MODE) {
          xmlw.writeRaw("\n");
        }
        oldtop.writtenchild = true;
      }

      xmlw.writeRaw("<" + childtagname, getIndent());
      String genericdata = null;
      if (child instanceof GenericSAX) {
        GenericSAX generic = (GenericSAX) child;
        SAXAccessMethodSpec[] getmethods = generic.getSAXGetMethods();
        // QQQQQ There may be getmethods defined by other means.
        // Generic needs review!
        if (generic.size() == 0
            && (getmethods == null || getmethods.length == 0))
          genericdata = generic.getData();
      }
      boolean isleaf = mappingcontext.generalLeafParser
          .isLeafType(child.getClass());
      // TODO: It may be a leaf as a result of a parent class (ViewParameters)
      top = isleaf ? null
          : new SerialContext(child, mappingcontext);
      // leaf is NOT DeSAXalizable OR we found some valid text
      boolean closenow = (isleaf || genericdata != null);
      Logger.println("Got generic data |" + genericdata + "|",
          Logger.DEBUG_SUBATOMIC);

      if (closenow) {
        // it is a leaf object
        xmlw.writeRaw(">", 0);
        // NB - 10/11/05 - final end of support for "Generic" objects. Check SVN
        // should it ever need to come back.
        // use leafparser to render it into text
        if (genericdata == null) {
          xmlw.write(mappingcontext.generalLeafParser.render(child));
        }
        // use writeRaw so that < is not deentitised
        xmlw.writeRaw("</" + childtagname + ">\n", 0);
        top = null;
      }
      else { // it is not a leaf object. writing it will require another pass
        // around
        Logger.println("Pushed", Logger.DEBUG_EXTRA_INFO);
        String polynick = mappingcontext.classnamemanager.getClassName(child
            .getClass());
        if (polynick != null && topgetmethod != null
            && topgetmethod.ispolymorphic) {
          appendAttr(Constants.TYPE_ATTRIBUTE_NAME, polynick);
        }
        SAMIterator getattrenum = top.ma.attrmethods.getGetEnumeration();
        if (getattrenum.valid() || child instanceof SAXalizableExtraAttrs) {
          Logger.println("Child has attributes", Logger.DEBUG_SUBATOMIC);
          // renderinto.clear();
          renderAttrs(child, getattrenum);
          // xmlw.write(renderinto.storage, renderinto.offset, renderinto.size);
        }

        // xmlw.writeRaw(">\n", 0);
        desaxingobjects.add(top);
      } // else not a leaf object
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t, "Error while writing tag "
          + childtagname);
    }
    return top;
  }

  /**
   * A value for indentlevel that will suppress writing of the XML declaration.
   */
  public static final int COMPACT_MODE = -1;

  /**
   * Write a serialized representation of an object subtree as XML to an output
   * stream. This method DOES NOT close the supplied output stream.
   * 
   * @param root The root object of the tree to be serialized. Must implement
   *          the <code>DeSAXalizable</code> interface, as must all
   *          descendents, or have types registered with the
   *          <code>SAXLeafParser</code>.
   * @param roottag The tag to be supplied to the root object (all subobjects
   *          have their tags supplied through the <code>DeSAXalizable</code>
   *          interface.
   * @param os The output stream to receive the serialized data. The data will
   *          be written in UTF-8 format.
   * @param forbidder An interface through which clients may countermand the
   *          serialization of a particular subobject of the root. Each object
   *          and tag will be supplied to this interface before serialization.
   *          <code>null</code> if all objects to be serialized without
   *          restriction.
   * @param maxdepth If this is not -1, subobjects below this depth from the
   *          root will not be serialized.
   * @param indentlevel The initial indent level in the XML output file to be
   *          applied to the tag representing the root object. If this is not 0,
   *          an XML declaration will not be written to the file. If this is
   *          COMPACT_MODE, newlines and indentation will not be written.
   */

  public void serializeSubtree(Object root, String roottagname,
      OutputStream os, int indentlevel) {
    // Store the stack of desaxing objects locally, to avoid having to allocate
    // thread-local
    // desaxalizers. This is a stack of SerialContexts.
    desaxingobjects = new ArrayList();
    this.indentlevel = indentlevel;

    try {
      this.xmlw = new XMLWriter(os);
      if (indentlevel == 0) {
        xmlw.writeDeclaration();
      }
      SerialContext top = writeOpeningTag(root, roottagname, null);
      SAXAccessMethod topgetmethod = null;

      // ONE ITERATION ROUND THE FOLLOWING LOOP CORRESPONDS TO ONE COMPLETE XML
      // TAG
      // at the top of this loop, the opening tag for the object on top of the
      // stack has just been written.
      Object child = null;
      String childtagname = null;
      while (true) {
        // traverse downwards
        if (Logger.passDebugLevel(Logger.DEBUG_EXTRA_INFO)) {
          Logger.println("At top", Logger.DEBUG_EXTRA_INFO);
        }
        if (top.enumm == null && !top.getenum.valid()) {
          // we have reached the last child node. pop the stack and write
          // closing tag.
          Logger.println("Popped", Logger.DEBUG_EXTRA_INFO);
          boolean writtenchild = top.writtenchild;
          ListUtil.pop(desaxingobjects);

          if (desaxingobjects.isEmpty())
            break; // GLOBAL EXIT POINT
          top = getDeSAXingObject();
          xmlw.closeTag(top.stashedclosingtag, getIndent(), writtenchild);
          continue;
        } // end if pop required

        // Step 2: get the child object that the index on top of stack refers to
        child = null;
        topgetmethod = top.getenum.get();
        childtagname = topgetmethod.tagname;

        // so long as the top object is not in an enum, get the enclosed object
        if (top.enumm == null) {
          child = topgetmethod.getChildObject(top.object);

          Logger.println("Object " + child + " delivered for tag "
              + topgetmethod.tagname, Logger.DEBUG_EXTRA_INFO);

          // if we discover it is an enum, begin chewing on it.
          if (child != null && topgetmethod.ismultiple) {
            top.enumm = EnumerationConverter.getEnumeration(child);
          }
          else {
            top.getenum.next();
          }
        }
        Logger.println("About to check enum: childtagname is " + childtagname,
            Logger.DEBUG_EXTRA_INFO);
        // if the top object IS within an enum, see whether it has another
        // element
        if (top.enumm != null) {
          if (top.enumm.hasMoreElements()) {
            child = top.enumm.nextElement();
            // if it is a GenericSAX enum, the tagname must be initialised with
            // the value reported by the GenericSAX
            if (child instanceof GenericSAX) {
              childtagname = ((GenericSAX) child).getTag();
            }
            // if it is a polymorphic enum, the tagname must be replaced with
            // the actual object class
            else if (topgetmethod.tagname.equals("*")) {
              childtagname = child.getClass().getName();
            }
            // otherwise, the value set from
            // top.getmethods[top.currentgetindex].tagname is correct
            Logger.println("Got next child " + child + " from enum",
                Logger.DEBUG_EXTRA_INFO);
          }
          else { // the enum is finished, leave child null so that following
            // branch will pass on
            Logger.println("enum finished", Logger.DEBUG_EXTRA_INFO);
            top.enumm = null;
            top.getenum.next();
            child = null; // this line deals with the case of 0-element
            // enumerations
          }
        }
        // Step 3: We have made all attempts to get a child object on this
        // iteration - child will be null if there is simply no object to be
        // delivered or an enum just finished. Otherwise, we have a child
        // object and so write out its opening tag.
        // If the child is a leaf object, we also write out the closing tag
        // here.
        if (child != null) {
          top.stashedclosingtag = childtagname;
          Logger.println("Tag name determined to be " + childtagname,
              Logger.DEBUG_EXTRA_INFO);
          if ((forbidder == null || forbidder.permitSerialization(childtagname,
              child))
          // maxdepth parameter removed - forbidders should do everything AMB
          // 1/10/04
          // && (maxdepth == -1 || desaxingobjects.size() <= maxdepth)
          ) {
            // Once we have got the child object, we can write the opening tag
            // name. If it is a leaf, write closing tag as well.
            SerialContext returnedtop = writeOpeningTag(child, childtagname,
                topgetmethod);
            if (returnedtop != null)
              top = returnedtop;
            // prettySpaces(desaxingobjects.size(), w);
          } // end if the writing was not forbidden
          else
            Logger.println("Tag writing forbidden", Logger.DEBUG_EXTRA_INFO);
        } // end if there was a child to write
      } // end enormous loop

      xmlw.closeTag(roottagname, indentlevel, top.writtenchild);
    }
    finally {
      if (xmlw != null) {
        xmlw.close();
      }
      blastState();
    }
  } // end method serializeSubtree
}