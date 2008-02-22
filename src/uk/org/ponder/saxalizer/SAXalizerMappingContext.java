/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer;

import java.util.Map;

import uk.org.ponder.beanutil.support.IndexedPropertyAccessor;
import uk.org.ponder.conversion.GeneralLeafParser;
import uk.org.ponder.reflect.JDKReflectiveCache;
import uk.org.ponder.reflect.ReflectiveCache;
import uk.org.ponder.saxalizer.mapping.ClassNameManager;
import uk.org.ponder.saxalizer.mapping.ContainerTypeRegistry;
import uk.org.ponder.saxalizer.mapping.DefaultMapperInferrer;
import uk.org.ponder.saxalizer.mapping.SAXalizerMapper;
import uk.org.ponder.saxalizer.mapping.SAXalizerMapperInferrer;
import uk.org.ponder.saxalizer.support.MethodAnalyser;

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
  public SAXalizerMapperInferrer inferrer;
  public GeneralLeafParser saxleafparser;
  public ClassNameManager classnamemanager = ClassNameManager.instance();
  private ReflectiveCache reflectivecache;
  private IndexedPropertyAccessor indexedPropertyAccessor;
  
  public IndexedPropertyAccessor getIndexedPropertyAccessor() {
    return indexedPropertyAccessor;
  }

  public void setIndexedPropertyAccessor(
      IndexedPropertyAccessor indexedPropertyAccessor) {
    this.indexedPropertyAccessor = indexedPropertyAccessor;
  }

  public void setGeneralLeafParser(GeneralLeafParser saxleafparser) {
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
  
  private SAXalizerMappingContext(boolean systemwide) {
    saxleafparser = GeneralLeafParser.instance();
    classnamemanager = ClassNameManager.instance();
    DefaultMapperInferrer definferrer = new DefaultMapperInferrer();
    definferrer.setContainerTypeRegistry(new ContainerTypeRegistry());
    inferrer = definferrer;
    setReflectiveCache(new JDKReflectiveCache());
  }
  
  public SAXalizerMappingContext() {
    saxleafparser = new GeneralLeafParser();
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
