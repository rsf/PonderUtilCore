package uk.org.ponder.saxalizer;

import uk.org.ponder.saxalizer.support.EntityResolverStash;
import uk.org.ponder.util.Logger;

import uk.org.ponder.intutil.intVector;

import uk.org.ponder.arrayutil.ArrayUtil;

/** The SAXHPool implements a global pool of SAXalizerHelper (and
 * hence SAXalizer) objects that may be dipped into by all clients in
 * a threadsafe manner. SAXalizerHelpers are supplied from the
 * getSAXalizerHelper method, and are automatically returned to the
 * pool when their parse is complete. The purpose of this class is to
 * avoid frequent construction of these highly expensive SAX parser
 * objects, and also to avoid the overhead (and danger) of the silly
 * situation we once had when each major class kept its own pet
 * SAXalizer for its personal use.  
 * <p> Typical usage of this class is as 
 * <code>SAXHPool.getSAXalizerHelper().produceSubtree(rootobj, stream)</code>;
 */

public class SAXHPool {
  private static SAXalizerHelper[] saxers = new SAXalizerHelper[10];
  private static int saxersfilled = 0;
  private static intVector freeindices = new intVector(10);

  private static ParseCompleteCallback callback = new ParseCompleteCallback() {
      public void parseComplete(int returningindex) {
	synchronized (saxers) {
	  Logger.println("Returning SAXH to element "+returningindex);
	  freeindices.addElement(returningindex);
	  }
	}
      };

  /** Returns a SAXalizerHelper object.
   * @return The required SAXalizerHelper.
   */
  public static SAXalizerHelper getSAXalizerHelper() {
    return getSAXalizerHelper(null);
    }

  /* Returns a SAXalizerHelper object bound to a particular entity resolver.
   * @param ers The entity resolver to be used by the supplied SAXalizerHelper.
   * @return The required SAXalizerHelper.
   */
  public static SAXalizerHelper getSAXalizerHelper(EntityResolverStash ers) {
    synchronized (saxers) {
      int indextogo;
      if (freeindices.size() > 0) {
	indextogo = freeindices.popElement();
	}
      else {
	if (saxersfilled == saxers.length) {
	  saxers = (SAXalizerHelper[])ArrayUtil.expand(saxers, 2.0);
	  }
	saxers[saxersfilled] = new SAXalizerHelper();
	++ saxersfilled;
	indextogo = saxersfilled - 1;
	}
      Logger.println("Allocating SAXH at element "+indextogo);
      SAXalizerHelper togo = saxers[indextogo];
      togo.setEntityResolverStash(ers);
      togo.setParseCompleteCallback(callback, indextogo);
      return togo;
      }
    }

  }
