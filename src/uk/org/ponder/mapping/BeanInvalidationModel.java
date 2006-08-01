/*
 * Created on 25 Jul 2006
 */
package uk.org.ponder.mapping;

/** An interface to a generalised "Bean Invalidation Model" which will track
 * modifications applied via EL expressions, and match them against a
 * supplied specification.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */

public interface BeanInvalidationModel {
  public BeanInvalidationIterator iterator();
  public void clear();
  /** Returns a concrete path which was written to invalidate the specification.
   * Will be as long as the specification with wildcards replaced by their matches.
   * @param spec An EL path specification, which may contain wildcards for
   * path components, e.g. <code>entity.*.property</code>.
   * @return The actual match for an invalidated path, <code>null</code> if no match.
   */
  public String invalidPathMatch(String spec);
}