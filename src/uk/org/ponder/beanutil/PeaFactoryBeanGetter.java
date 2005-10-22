/*
 * Created on Aug 4, 2005
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.saxalizer.SAXalizerMappingContext;

/**
 * A wrapper that upgrades a BeanLocator to a full-fledged bean container,
 * by using reflective operations to navigate a chain of properties to arrive
 * at any leaf bean. This class is currently disused - BeanContainerWrapper 
 * provides more functionality, including censoring and method invokation.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class PeaFactoryBeanGetter implements BeanGetter {

  private BeanLocator rbl;
  private SAXalizerMappingContext mappingcontext;

  public Object getBean(String beanname) {
    String rootpath = PathUtil.getHeadPath(beanname);
    Object rootbean = rbl.locateBean(rootpath);
    String restpath = PathUtil.getFromHeadPath(beanname);
    return BeanUtil.navigate(rootbean, restpath, mappingcontext);
  }

  public void setMappingContext(SAXalizerMappingContext mappingcontext) {
    this.mappingcontext = mappingcontext;
  }
  
  public void setRootBeanLocator(BeanLocator rbl) {
    this.rbl = rbl;
  }
}
