/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer;

import java.util.Map;

import uk.org.ponder.conversion.StaticLeafParser;
import uk.org.ponder.reflect.ReflectiveCache;
import uk.org.ponder.saxalizer.mapping.ClassNameManager;
import uk.org.ponder.saxalizer.mapping.DefaultMapperInferrer;
import uk.org.ponder.saxalizer.mapping.SAXalizerMapper;
import uk.org.ponder.saxalizer.mapping.SAXalizerMapperInferrer;

/**
 * The complete context for serialisation and deserialisation of objects
 * to and from XML using the SAXalizer and DeSAXalizer.
 * It includes a mapping context
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
  public StaticLeafParser saxleafparser = StaticLeafParser.instance();
  public ClassNameManager classnamemanager = ClassNameManager.instance();
  private ReflectiveCache reflectivecache;

  public void setReflectiveCache(ReflectiveCache reflectivecache) {
    this.reflectivecache = reflectivecache;
    mapper = new SAXalizerMapper(reflectivecache.getConcurrentMap(1));
    methodanalysers = reflectivecache.getConcurrentMap(1);
  }
  
  public SAXalizerMapper mapper;
// this is a Hashtable of Classes to MethodAnalysers
  private Map methodanalysers; 
  public MethodAnalyser getAnalyser(Class clazz) {
    return (MethodAnalyser) methodanalysers.get(clazz); 
  }
  public void putAnalyser(Class clazz, MethodAnalyser analyser) {
    methodanalysers.put(clazz, analyser);
  }
  private SAXalizerMappingContext(boolean systemwide) {
    saxleafparser = StaticLeafParser.instance();
    classnamemanager = ClassNameManager.instance();
  }
  public SAXalizerMappingContext() {
    saxleafparser = new StaticLeafParser();
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
