package uk.org.ponder.saxalizer;

import java.util.Enumeration;
import java.util.Stack;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import uk.org.ponder.util.EnumerationConverter;
import uk.org.ponder.util.Logger;
import uk.org.ponder.util.UniversalRuntimeException;

import uk.org.ponder.stringutil.CharWrap;

import uk.org.ponder.xml.XMLWriter;

public class DeSAXalizer {
  static class SerialContext {
    Object object; // The object being serialised
    MethodAnalyser ma;
    // the current index through the get methods
    SAXAccessMethodHash.SAMIterator getenum;
    // enumeration for a vector tag, while enum is active, currentgetindex
    // progress
    // is suspended. enum will be set to null when it expires.
    Enumeration enum;
    // The (original) tag name of the enum in progress
    String enumtagname;
    //int totalgetmethods;
    String stashedclosingtag;

    SerialContext(Object object, SAXalizerMappingContext mappingcontext) {
      this.object = object;
      this.ma = MethodAnalyser.getMethodAnalyser(object.getClass(), mappingcontext);
      // for generic objects, currentgetindex has an honorary value of -1
      // representing
      // getChildEnum()
      //currentgetindex = object instanceof GenericSAX ? -1 : 0;
      stashedclosingtag = null;
      getenum = ma.attrmethods.getGetEnumeration();

      //totalgetmethods = getmethods == null ? 0
          //: getmethods.length;
    }
  }

  private static SAXLeafParser leafparser = SAXLeafParser.instance();

  /**
   * Render a <code>DeSAXalizable</code> object as a string. This method is
   * intended primarily for debugging purposes.
   * 
   * @param root
   *          The object to be rendered as a string.
   * @param roottag
   *          The root tag of the rendered tree
   * @return The supplied object rendered as a <code>String</code> of XML
   *         tags.
   */

  public static String toString(DeSAXalizable root, String roottag) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    if (root == null)
      return "null";
    try {
      serializeSubtree(root, roottag, baos);
      baos.close();
      return baos.toString(XMLWriter.DEFAULT_ENCODING) + " of "
          + root.getClass();
    }
    catch (Throwable t) {
      Logger.printStackTrace(t);
      return null;
    }
  }

  /**
   * Write a serialized representation of an object subtree as XML to an output
   * stream. This method DOES NOT close the supplied output stream.
   * 
   * @param root
   *          The root object of the tree to be serialized. Must implement the
   *          <code>DeSAXalizable</code> interface, as must all descendents,
   *          or have types registered with the <code>SAXLeafParser</code>.
   * @param roottag
   *          The tag to be supplied to the root object (all subobjects have
   *          their tags supplied through the <code>DeSAXalizable</code>
   *          interface.
   * @param os
   *          The output stream to receive the serialized data. The data will be
   *          written in UTF-8 format.
   */

  public static void serializeSubtree(DeSAXalizable root, String roottag,
      OutputStream os) throws IOException {
    serializeSubtree(root, roottag, os, null, -1, 0, null);
  }

  /**
   * Write a serialized representation of an object subtree as XML to an output
   * stream. This method DOES NOT close the supplied output stream.
   * 
   * @param root
   *          The root object of the tree to be serialized. Must implement the
   *          <code>DeSAXalizable</code> interface, as must all descendents,
   *          or have types registered with the <code>SAXLeafParser</code>.
   * @param roottag
   *          The tag to be supplied to the root object (all subobjects have
   *          their tags supplied through the <code>DeSAXalizable</code>
   *          interface.
   * @param os
   *          The output stream to receive the serialized data. The data will be
   *          written in UTF-8 format.
   * @param indentlevel
   *          The initial indent level in the XML output file to be applied to
   *          the tag representing the root object. If this is not 0, an XML
   *          declaration will not be written to the file.
   */

  public static void serializeSubtree(DeSAXalizable root, String roottag,
      OutputStream os, int indentlevel) throws IOException {
    serializeSubtree(root, roottag, os, null, -1, indentlevel, null);
  }

  /**
   * Write a serialized representation of an object subtree as XML to an output
   * stream. This method DOES NOT close the supplied output stream.
   * 
   * @param root
   *          The root object of the tree to be serialized. Must implement the
   *          <code>DeSAXalizable</code> interface, as must all descendents,
   *          or have types registered with the <code>SAXLeafParser</code>.
   * @param roottag
   *          The tag to be supplied to the root object (all subobjects have
   *          their tags supplied through the <code>DeSAXalizable</code>
   *          interface.
   * @param os
   *          The output stream to receive the serialized data. The data will be
   *          written in UTF-8 format.
   * @param forbidder
   *          An interface through which clients may countermand the
   *          serialization of a particular subobject of the root. Each object
   *          and tag will be supplied to this interface before serialization.
   *          <code>null</code> if all objects to be serialized without
   *          restriction.
   * @param maxdepth
   *          If this is not -1, subobjects below this depth from the root will
   *          not be serialized.
   */

  public static void serializeSubtree(DeSAXalizable root, String roottag,
      OutputStream os, DeSAXalizerForbidder forbidder, int maxdepth)
      throws IOException {
    serializeSubtree(root, roottag, os, forbidder, maxdepth, 0, null);
  }

  private static void appendAttr(String attrname, Object attrvalue,
      CharWrap renderinto, XMLWriter xmlw) throws IOException {
    xmlw.writeRaw(" ");
    xmlw.writeRaw(attrname); // attribute names may not contain escapes
    xmlw.writeRaw("=\"");
    renderinto.clear();
    leafparser.render(attrvalue, renderinto);
    xmlw.write(renderinto.storage, renderinto.offset, renderinto.size);
    xmlw.writeRaw("\"");
  }

  private static void renderAttrs(Object torender,
      SAXAccessMethodHash.SAMIterator getattrenum, CharWrap renderinto, XMLWriter xmlw)
      throws IOException {
   for (;getattrenum.valid(); getattrenum.next()) {
     SAXAccessMethod getattr = getattrenum.get();
        Object attrvalue = getattr.getChildObject(torender);
        Logger.println("Attr " + getattr.tagname + ": returned object " + attrvalue,
            Logger.DEBUG_SUBATOMIC);
        if (attrvalue != null) {
          appendAttr(getattr.tagname, attrvalue, renderinto, xmlw);
        }
      }
    // now find and write any extra attributes
    if (torender instanceof DeSAXalizableExtraAttrs) {
      SAXAttributeHash extraattrs = ((DeSAXalizableExtraAttrs) torender)
          .getExtraAttributes();
      if (extraattrs != null) {
        Enumeration attrs = extraattrs.keys();
        while (attrs.hasMoreElements()) {
          String attributename = (String) attrs.nextElement();
          String attributevalue = extraattrs.get(attributename).getValue();
          appendAttr(attributename, attributevalue, renderinto, xmlw);
        }
      }
    }
  }

  private static SerialContext writeOpeningTag(Object child,
      String childtagname, XMLWriter xmlw, Stack desaxingobjects,
      int indentlevel, CharWrap renderinto, SAXalizerMappingContext mappingcontext) {
    SerialContext top = null;
    try {
      xmlw.writeRaw("<" + childtagname, desaxingobjects.size() + indentlevel);
      String genericdata = null;
      if (child instanceof GenericSAX) {
        GenericSAX generic = (GenericSAX) child;
        SAXAccessMethodSpec[] getmethods = generic.getSAXGetMethods();
        if (generic.size() == 0
            && (getmethods == null || getmethods.length == 0))
          genericdata = generic.getData();
      }
      // leaf is NOT DeSAXalizable OR we found some valid text
      boolean closenow = (!(child instanceof DeSAXalizable) || genericdata != null);
      Logger.println("Got generic data |" + genericdata + "|",
          Logger.DEBUG_SUBATOMIC);
      top = child instanceof DeSAXalizable ? new SerialContext(child, mappingcontext)
          : null;

      Logger.println("Pushed", Logger.DEBUG_EXTRA_INFO);
      String polynick = PolymorphicManager.instance().getNick(child.getClass());
      if (polynick != null) {
        appendAttr("type", polynick, renderinto, xmlw);
      }
      SAXAccessMethodHash.SAMIterator getattrenum = top.ma.attrmethods.getGetEnumeration();
      if (getattrenum.valid() || child instanceof DeSAXalizableExtraAttrs) {
        Logger.println("Child has attributes", Logger.DEBUG_SUBATOMIC);
        //      renderinto.clear();
        renderAttrs(child, getattrenum, renderinto, xmlw);
        //      xmlw.write(renderinto.storage, renderinto.offset, renderinto.size);
      }

      if (closenow) {
        // it is a leaf object
        xmlw.writeRaw(">", 0);
        // use leafparser to render it into text
        renderinto.clear();
        if (genericdata == null) {
          leafparser.render(child, renderinto);
        }
        else
          renderinto.append(genericdata);
        xmlw.write(renderinto.storage, renderinto.offset, renderinto.size);
        // use writeRaw so that < is not deentitised
        xmlw.writeRaw("</" + childtagname + ">\n", 0);
        top = null;
      }
      else { // it is not a leaf object. writing it will require another pass
             // around.
        xmlw.writeRaw(">\n", 0);
        desaxingobjects.push(top);
      } // else not a leaf object
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t, "Error while writing tag "
          + childtagname);
    }
    return top;
  }

  /**
   * Write a serialized representation of an object subtree as XML to an output
   * stream. This method DOES NOT close the supplied output stream.
   * 
   * @param root
   *          The root object of the tree to be serialized. Must implement the
   *          <code>DeSAXalizable</code> interface, as must all descendents,
   *          or have types registered with the <code>SAXLeafParser</code>.
   * @param roottag
   *          The tag to be supplied to the root object (all subobjects have
   *          their tags supplied through the <code>DeSAXalizable</code>
   *          interface.
   * @param os
   *          The output stream to receive the serialized data. The data will be
   *          written in UTF-8 format.
   * @param forbidder
   *          An interface through which clients may countermand the
   *          serialization of a particular subobject of the root. Each object
   *          and tag will be supplied to this interface before serialization.
   *          <code>null</code> if all objects to be serialized without
   *          restriction.
   * @param maxdepth
   *          If this is not -1, subobjects below this depth from the root will
   *          not be serialized.
   * @param indentlevel
   *          The initial indent level in the XML output file to be applied to
   *          the tag representing the root object. If this is not 0, an XML
   *          declaration will not be written to the file.
   */

  public static void serializeSubtree(DeSAXalizable root, String roottagname,
      OutputStream os, DeSAXalizerForbidder forbidder, int maxdepth,
      int indentlevel, SAXalizerMappingContext mappingcontext) throws IOException {
    // Store the stack of desaxing objects locally, to avoid having to allocate
    // thread-local
    // desaxalizers. This is a stack of SerialContexts.
    Stack desaxingobjects = new Stack();

    // a convenience buffer for objects to render themselves into
    CharWrap renderinto = new CharWrap();
    XMLWriter w = null;

    try {
      w = new XMLWriter(os);
      if (indentlevel == 0) {
        w.writeDeclaration();
      }
      SerialContext top = writeOpeningTag(root, roottagname, w,
          desaxingobjects, indentlevel, renderinto, mappingcontext);
      //      w.writeRaw('<'+roottagname+">\n", indentlevel);

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

          // nb - THERE MAY BE NO GET METHODS!!! - in the case there are only
          // attributes eg
          //Logger.println("New top object " + top.object + " get method index "
              //+ top.currentgetindex, Logger.DEBUG_EXTRA_INFO);

          // Step 1: If there are no child objects left, write out the closing
          // tag
          // and pop object off the stack.
          //Logger.println("Top.enum: " + top.enum + " top.currentgetindex "
              //+ top.currentgetindex + " top.totalgetmethods "
              //+ top.totalgetmethods, Logger.DEBUG_EXTRA_INFO);
        }
        if (top.enum == null && !top.getenum.valid()) {
          // we have reached the last child node. pop the stack and write
          // closing tag.
          Logger.println("Popped", Logger.DEBUG_EXTRA_INFO);
          desaxingobjects.pop();

          if (desaxingobjects.empty())
            break; // GLOBAL EXIT POINT
          top = (SerialContext) desaxingobjects.peek();
          //	  prettySpaces(desaxingobjects.size(), w);
          //	  w.write("</"+top.stashedclosingtag+">\n");
          w.writeRaw("</" + top.stashedclosingtag + ">\n", desaxingobjects
              .size() + indentlevel);
          continue;
        } // end if pop required

        // Step 2: get the child object that the index on top of stack refers to
        child = null;
        // may be -1 because of genericsax, may be off the end because of
        // unfinished enum. What terrible, terrible logic....
        // PLAN: GenericSAX's getChildEnum will now be treated as a normal multiple get.
        if (top.getenum.valid()) 
          childtagname = top.getenum.get().tagname;

        //	String childtagname = null;
        // so long as the top object is not in an enum, get the enclosed object
        if (top.enum == null) {
            // TODO: This used to reflect for the GenericSAX getChildEnum method.
            // we can probably handle this better by "automatic mapping", poly*.
            topgetmethod = top.getenum.get();
            child = topgetmethod.getChildObject(top.object);

            Logger.println("Object " + child + " delivered for tag "
                + topgetmethod.tagname, Logger.DEBUG_EXTRA_INFO);
            // IMPORTANT semantic point - during the processing of an enum,
            // currentgetindex already points to the NEXT sibling along from the
            // enum.
            top.getenum.next();
          // if we discover it is an enum, begin chewing on it.
          // this may be an enum delivered from GenericSAX getChildEnum().
          if (child != null && topgetmethod.ismultiple) {
            top.enum = EnumerationConverter.getEnumeration(child);
            top.enumtagname = childtagname;
          }
        }
        Logger.println("About to check enum: childtagname is " + childtagname,
            Logger.DEBUG_EXTRA_INFO);
        // if the top object IS within an enum, see whether it has another
        // element
        if (top.enum != null) {
          if (top.enum.hasMoreElements()) {
            child = top.enum.nextElement();
            // if it is a GenericSAX enum, the tagname must be initialised with
            // the value reported by the GenericSAX
            if (child instanceof GenericSAX) {
              childtagname = ((GenericSAX) child).getTag();
            }
            // if it is a polymorphic enum, the tagname must be replaced with
            // the actual object class
            else if (top.enumtagname.equals("*")) {
              childtagname = child.getClass().getName();
            }
            else
              childtagname = top.enumtagname;
            // otherwise, the value set from
            // top.getmethods[top.currentgetindex].tagname is correct
            Logger.println("Got next child " + child + " from enum",
                Logger.DEBUG_EXTRA_INFO);
          }
          else { // the enum is finished, leave child null so that following
                 // branch will pass on
            Logger.println("enum finished", Logger.DEBUG_EXTRA_INFO);
            top.enum = null;
            child = null; // this line deals with the case of 0-element
                          // enumerations
          }
        }
        // Step 3: We have made all attempts to get a child object on this
        // iteration -
        // child will be null if there is simply no object to be delivered or an
        // enum
        // just finished. Otherwise, we have a child object and so write out its
        // opening tag.
        // If the child is a leaf object, we also write out the closing tag
        // here.
        if (child != null) {
          //	  String currenttagname = getTagName(top, child);
          top.stashedclosingtag = childtagname;
          Logger.println("Tag name determined to be " + childtagname,
              Logger.DEBUG_EXTRA_INFO);
          if ((forbidder == null || forbidder.permitSerialization(childtagname,
              child))
              && (maxdepth == -1 || desaxingobjects.size() <= maxdepth)) {
            // Once we have got the child object, we can write the opening tag
            // name. If it is a leaf, write closing tag as well.
            SerialContext returnedtop = writeOpeningTag(child, childtagname, w,
                desaxingobjects, indentlevel, renderinto, mappingcontext);
            if (returnedtop != null)
              top = returnedtop;
            //	    prettySpaces(desaxingobjects.size(), w);
          } // end if the writing was not forbidden
          else
            Logger.println("Tag writing forbidden", Logger.DEBUG_EXTRA_INFO);
        } // end if there was a child to write
      } // end enormous loop

      w.writeRaw("</" + roottagname + ">\n", indentlevel);
    }
    finally {
      if (w != null)
        w.close();
    }
  } // end method serializeSubtree
}