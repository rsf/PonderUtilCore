package uk.org.ponder.saxalizer.support;


/**
 * The <code>LocalEntityResolver</code> maps a DTD reference onto a locally
 * cached copy. These objects are stored inside an
 * <code>EntityResolverStash</code>.
 */

public class LocalEntityResolver {
  int IDtype;
  String ID;
  String path;
  /**
   * Sets the type of the ID for this resolver. This should be either
   * <code>public</code> (recommended) or <code>system</code>.
   * 
   * @param IDtypestring The required ID type.
   */

  public void setIDType(String IDtypestring) {
    if (IDtypestring.equalsIgnoreCase("public")) {
      this.IDtype = EntityResolverStash.PUBLIC_ID;
    }
    else
      IDtype = EntityResolverStash.SYSTEM_ID;
  }

  /**
   * Sets the entity ID for this resolver.
   * 
   * @param ID The required entity ID.
   */
  public void setID(String ID) {
    this.ID = ID;
  }

  /**
   * Sets the URL from which the required entity may be resolved.
   * 
   * @param entityURL The required entity URL.
   */
  public void setPath(String path) {
    this.path = path;
  }

}