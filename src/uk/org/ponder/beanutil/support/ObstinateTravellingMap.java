/*
 * Created on Dec 3, 2006
 */
package uk.org.ponder.beanutil.support;

import java.util.Iterator;
import java.util.Set;

import uk.org.ponder.beanutil.BeanLocator;
import uk.org.ponder.beanutil.BeanModelAlterer;
import uk.org.ponder.beanutil.IterableBeanLocator;
import uk.org.ponder.conversion.GeneralLeafParser;
import uk.org.ponder.util.ObstinateMap;
import uk.org.ponder.util.ObstinateSet;
import uk.org.ponder.util.UniversalRuntimeException;

/** Converts arbitrary EL-traversable material to standard Maps - that is,
 * abstracts away the mechanism of property access, via by BeanLocator or
 * physical bean properties/fields, and renders them all as Map key/values.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */

public class ObstinateTravellingMap extends ObstinateMap {
  private Object seed;
  private BeanModelAlterer bma;
  private GeneralLeafParser generalLeafParser;
  private boolean wraptrunks;
  private String seedpath;

  public void init() {
    if (seedpath != null) {
      seed = bma.getBeanValue(seedpath, seed, null);
    }
  }

  public void setSeed(Object seed) {
    this.seed = seed;
  }

  public void setSeedPath(String seedpath) {
    this.seedpath = seedpath;
  }

  public void setBeanModelAlterer(BeanModelAlterer bma) {
    this.bma = bma;
  }

  public void setGeneralLeafParser(GeneralLeafParser generalLeafParser) {
    this.generalLeafParser = generalLeafParser;
  }

  public void setWrapTrunks(boolean wraptrunks) {
    this.wraptrunks = wraptrunks;
  }

  public boolean containsKey(Object key) {
    Object child = bma.getBeanValue((String) key, seed, null);
    return child != null;
  }

  public Set keySet() {
    return new ObstinateSet() {

      public boolean contains(final Object o) {
        return containsKey(o);
      }

      public Iterator iterator() {
        if (seed instanceof IterableBeanLocator) {
          return ((IterableBeanLocator) seed).iterator();
        }
        else
          throw new UniversalRuntimeException("Wrapped bean of "
              + seed.getClass() + " is not iterable");
      }
    };
  }

  public Object get(Object key) {
    Object child = bma.getBeanValue((String) key, seed, null);
    return wrapChild(child);
  }

  private Object wrapChild(Object child) {
    boolean wrap = false;
    if (child instanceof BeanLocator) {
      wrap = true;
    }
    else if (generalLeafParser.isLeafType(child.getClass())) {
      wrap = false;
    }
    else
      wrap = wraptrunks;

    if (wrap) {
      ObstinateTravellingMap togo = new ObstinateTravellingMap();
      togo.setBeanModelAlterer(bma);
      togo.setGeneralLeafParser(generalLeafParser);
      togo.setWrapTrunks(wraptrunks);
      togo.setSeed(child);
      togo.init();
      return togo;
    }
    else
      return child;
  }

}
