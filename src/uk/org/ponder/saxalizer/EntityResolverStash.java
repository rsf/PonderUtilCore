package uk.org.ponder.saxalizer;

import org.xml.sax.InputSource;

import java.util.Hashtable;

import java.net.URL;

import java.io.Reader;
import java.io.InputStreamReader;

/** The <code>LocalEntityResolver</code> is used to map public DTD references
 * onto locally cached copies of the files.
 */

public class EntityResolverStash implements SAXalizable {
  public static final int PUBLIC_ID = 0;
  public static final int SYSTEM_ID = 1;

  private static SAXAccessMethodSpec[] setmethods = {
    new SAXAccessMethodSpec("entityresolver", "addEntityResolver", LocalEntityResolver.class)
    };

  private static EntityResolverStash instance;


  /** Set the global (singleton) instance of the EntityResolverStash to the specified instance.
   * @param instance The required global instance.
   */
  public static void setInstance(EntityResolverStash instance) {
    EntityResolverStash.instance = instance;
    }

  /** Returns the global (singleton) EntityResolverStash instance.
   */

  public static EntityResolverStash instance() {
    return instance;
    }

  public SAXAccessMethodSpec[] getSAXSetMethods() {
    return setmethods;
    }

  /** Adds the specified entityresolver into this stash.
   * @param ler A LocalEntityResolver capable of resolving a single DTD entity
   * reference. Aren't these things tedious.
   */

  public void addEntityResolver(LocalEntityResolver ler) {
    entityresolvers.put(ler.ID, ler);
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
    if (wer == null) return null;
    try {
      URL u = new URL(wer.entityURL);
      //      System.out.println("Resolved "+publicID+" to "+wer.entityURL);
      Reader r = new InputStreamReader(u.openStream());
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
