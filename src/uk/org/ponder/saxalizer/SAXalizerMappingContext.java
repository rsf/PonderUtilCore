/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer;

import java.util.HashMap;

import uk.org.ponder.saxalizer.mapping.SAXalizerMapper;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class SAXalizerMappingContext {
  public SAXLeafParser saxleafparser = SAXLeafParser.instance();
  public SAXalizerMapper mapper = new SAXalizerMapper();
// this is a Hashtable of Classes to MethodAnalysers
  private HashMap methodanalysers = new HashMap();
  public MethodAnalyser getAnalyser(Class clazz) {
    return (MethodAnalyser) methodanalysers.get(clazz); 
  }
  public void putAnalyser(Class clazz, MethodAnalyser analyser) {
    methodanalysers.put(clazz, analyser);
  }
}
