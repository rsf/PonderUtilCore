/*
 * Created on Aug 4, 2005
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.saxalizer.SAXalizerMappingContext;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class PeaFactoryBeanGetter implements BeanGetter {

  private RootBeanLocator rbl;
  private SAXalizerMappingContext mappingcontext;

  public Object getBean(String beanname) {
    String rootpath = PathUtil.getHeadPath(beanname);
    Object rootbean = rbl.locateRootBean(rootpath);
    String restpath = PathUtil.getFromHeadPath(beanname);
    return BeanUtil.navigate(rootbean, restpath, mappingcontext);
  }

  public void setMappingContext(SAXalizerMappingContext mappingcontext) {
    this.mappingcontext = mappingcontext;
  }
  
  public void setRootBeanLocator(RootBeanLocator rbl) {
    this.rbl = rbl;
  }
}
