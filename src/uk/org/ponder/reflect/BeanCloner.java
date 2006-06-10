/*
 * Created on Nov 18, 2005
 */
package uk.org.ponder.reflect;

import uk.org.ponder.saxalizer.AccessMethod;
import uk.org.ponder.saxalizer.MethodAnalyser;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;

public class BeanCloner {
  // Cost of m-invoking bean factory:
  // 1 reflect construct for factory
  // 1 list construct, Spring filling overhead for each argument
  // 1 object array construct, conversion
  // 1 reflect invocation for factory method
  
  // alternatively, cost of clone:
  // 1 reflective construct for object
  // 1 reflective invoke for each "spare" property
  // code clients need to use "set" syntax rather than function calls
  // slight oddness of programming model - bean must be truly stateless sans deps.
  // Probably these last two are the killer...
  private SAXalizerMappingContext mappingcontext;
  private ReflectiveCache reflectivecache;
  public void setMappingContext(SAXalizerMappingContext mappingcontext) {
    this.mappingcontext = mappingcontext;
  }
  public void setReflectiveCache(ReflectiveCache reflectivecache) {
    this.reflectivecache = reflectivecache;
  }
  
  public Object cloneBean(Object bean) {
   return null; 
  }
  // A shallow clone, "with respect to" a given class, i.e. cloning all
  // properties which are NOT PRESENT in the supplied class!
  // We would really like to FastClass this stuff, but a) it is nasty to write, 
  // and b) clone-based RSAC operations seem to have a tendency of smelling
  // a little bad after a bit of experience.
  public Object cloneWithRespect(Object bean, Class respectful) {
    Class beanclz = bean.getClass();
    MethodAnalyser targetma = mappingcontext.getAnalyser(beanclz);
    MethodAnalyser respectma = mappingcontext.getAnalyser(respectful);
    
    Object togo = reflectivecache.construct(beanclz);
    
    for (int i = 0; i < targetma.allgetters.length; ++i) {
      AccessMethod pot = targetma.allgetters[i];
      if (!pot.canGet() || !pot.canSet()) continue;
      String propname = pot.getPropertyName();
      if (respectma.getAccessMethod(propname) != null) {
        Object getit = pot.getChildObject(bean);
        pot.setChildObject(togo, getit);
      }
    }
    return togo;
  }
}
