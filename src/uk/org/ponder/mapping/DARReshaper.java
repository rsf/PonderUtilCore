/*
 * Created on 14-Feb-2006
 */
package uk.org.ponder.mapping;

/** Maps one DataAlterationRequest into another. This allows "filtering" of
 * an incoming train of DARs (probably resulting from a user request) into
 * a different form prior to being applied to the data model. This reshaping
 * might, for example, apply a remapping in order to unapply an 
 * Entity -> ID mapping, or apply a security policy by throwing an exception 
 * on a forbidden operation.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public interface DARReshaper {
  public DataAlterationRequest reshapeDAR(DataAlterationRequest toshape);
}
