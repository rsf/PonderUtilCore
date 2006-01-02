package uk.org.ponder.saxalizer;

import org.xml.sax.InputSource;

import uk.org.ponder.streamutil.StreamResolver;
import uk.org.ponder.util.Logger;

import java.util.Hashtable;
import java.util.List;

import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;

/** The <code>LocalEntityResolver</code> is used to map public DTD references
 * onto locally cached copies of the files.
 */

public class EntityResolverStash {
  public static final int PUBLIC_ID = 0;
  public static final int SYSTEM_ID = 1;
  private StreamResolver streamresolver;

  public void setStreamResolver(StreamResolver streamresolver) {
    this.streamresolver = streamresolver;
  }
  
  /** Adds the specified entityresolver into this stash.
   * @param ler A LocalEntityResolver capable of resolving a single DTD entity
   * reference. Aren't these things tedious.
   */

  public void addEntityResolver(LocalEntityResolver ler) {
    entityresolvers.put(ler.ID, ler);
    }
  
  public void setEntityResolvers(List resolvers) {
    for (int i = 0; i < resolvers.size(); ++ i) {
      addEntityResolver((LocalEntityResolver) resolvers.get(i));
    }
  }
  
  // This is a hashtable of id (String) to LocalEntityResolvers
  private Hashtable entityresolvers = new Hashtable();
  /** Resolves the given public DTD ID onto an <code>InputSource</code>
   * derived from a locally cached copy.
   * @param publicID The public ID of the DTD.
   * @return An <code>InputSource</code> with the contents of the DTD.
   */
  public InputSource resolve(String publicID) {
    if (publicID == null) return null;
    LocalEntityResolver wer = (LocalEntityResolver) entityresolvers.get(publicID);
    if (wer == null) {
      Logger.log.warn("Entity with public ID " + publicID + " could not be resolved");
      return null;
    }
    try {
      InputStream is = streamresolver.openStream(wer.path);
      //      System.out.println("Resolved "+publicID+" to "+wer.entityURL);
      Reader r = new InputStreamReader(is);
      InputSource i = new InputSource(r);
      i.setSystemId("Local entity resolver stashed page");
      return i;
      }
    catch (Exception e) {
      e.printStackTrace();
      return null;
      }
    }

  }
