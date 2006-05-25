/*
 * Created on Nov 29, 2005
 */
package uk.org.ponder.reflect;

import uk.org.ponder.mapping.DARApplier;
import uk.org.ponder.saxalizer.SAXalXMLProvider;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;

/** An "all-in-one" collection of the central PonderUtil reflective/mapping
 * helper objects. It seems inevitable that if you need one of these you will
 * need one or more of the others. Currently disused.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */
public class MappingKit {
  public SAXalizerMappingContext mappingcontext;
  public SAXalXMLProvider xmlprovider;
  public DeepBeanCloner deepbeancloner;
  public ReflectiveCache reflectivecache;
  public DARApplier darapplier;
}
