/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer;

import java.util.Map;

import uk.org.ponder.saxalizer.mapping.ClassNameManager;
import uk.org.ponder.saxalizer.mapping.DefaultMapperInferrer;
import uk.org.ponder.saxalizer.mapping.SAXalizerMapper;
import uk.org.ponder.saxalizer.mapping.SAXalizerMapperInferrer;
import uk.org.ponder.util.ReflectiveCache;

/**
 * The complete context for serialisation and deserialisation of objects
 * to and from XML using the SAXalizer and DeSAXalizer.
 * It includes a
 * <ol>
 * <li>
 * <code>SAXalizerMapperInferrer</code> has been 
 * provided, it will be asked to synthesize a "default" mapping
 * based on reflection of the class.
 * </ol>
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class SAXalizerMappingContext {
  public SAXalizerMapperInferrer inferrer = new DefaultMapperInferrer();
  public SAXLeafParser saxleafparser = SAXLeafParser.instance();
  public ClassNameManager classnamemanager = ClassNameManager.instance();
  public SAXalizerMapper mapper = new SAXalizerMapper();
// this is a Hashtable of Classes to MethodAnalysers
  private Map methodanalysers = ReflectiveCache.getConcurrentMap();
  public MethodAnalyser getAnalyser(Class clazz) {
    return (MethodAnalyser) methodanalysers.get(clazz); 
  }
  public void putAnalyser(Class clazz, MethodAnalyser analyser) {
    methodanalysers.put(clazz, analyser);
  }
  private SAXalizerMappingContext(boolean systemwide) {
    saxleafparser = SAXLeafParser.instance();
    classnamemanager = ClassNameManager.instance();
  }
  public SAXalizerMappingContext() {
    saxleafparser = new SAXLeafParser();
    classnamemanager = new ClassNameManager();
  }
  private static SAXalizerMappingContext instance = new SAXalizerMappingContext(true);
  /** Returns a JVM-wide context to be used where no specialised
   * context is supplied to the SAXalizer or DeSAXalizer. This
   * context may NOT be used in conjunction with any dynamic mapping 
   * information, only static or default-inferred mappings are permitted.
   * @return
   */
  public static SAXalizerMappingContext instance() {
    return instance;
  }
}
