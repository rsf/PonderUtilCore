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
 * A reflective mapping context used to infer mappings of Java objects
 * to and from serial representations, for example to XML or EL expressions.
 * <p>If a <code>SAXalizerMapperInferrer</code> has been 
 * provided, it will be asked to synthesize a "default" mapping
 * based on reflection of the class.
 * </ol>
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class SAXalizerMappingContext {
  public SAXalizerMapperInferrer inferrer = new DefaultMapperInferrer();
  public StaticLeafParser saxleafparser;
  public ClassNameManager classnamemanager = ClassNameManager.instance();
  private ReflectiveCache reflectivecache;

  public void setStaticLeafParser(StaticLeafParser saxleafparser) {
    this.saxleafparser = saxleafparser;
  }
  
  public void setDefaultInferrer(SAXalizerMapperInferrer inferrer) {
    this.inferrer = inferrer;
  }
  
  public void setChainedInferrer(SAXalizerMapperInferrer inferrer) {
    inferrer.setChainedInferrer(this.inferrer);
    this.inferrer = inferrer;
  }
  
  public void setReflectiveCache(ReflectiveCache reflectivecache) {
    this.reflectivecache = reflectivecache;
    mapper = new SAXalizerMapper(reflectivecache.getConcurrentMap(1));
    methodanalysers = reflectivecache.getConcurrentMap(1);
  }
  
  public ReflectiveCache getReflectiveCache() {
    return reflectivecache;
  }
  
  public SAXalizerMapper mapper;
// this is a Hashtable of Classes to MethodAnalysers
  private Map methodanalysers; 
  public MethodAnalyser getAnalyser(Class clazz) {
    MethodAnalyser togo = (MethodAnalyser) methodanalysers.get(clazz);
    if (togo == null) {
      togo = MethodAnalyser.constructMethodAnalyser(clazz, this);
      methodanalysers.put(clazz, togo);
    }
    return togo;
  }
  private void putAnalyser(Class clazz, MethodAnalyser analyser) {
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
