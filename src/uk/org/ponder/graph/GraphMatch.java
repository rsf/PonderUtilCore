/*
 * Created on 19-Jan-2004
 */
package uk.org.ponder.graph;

import uk.org.ponder.intutil.intVector;
import uk.org.ponder.util.AssertionException;

/**
 * Represents a match between (portions of) two graphs.
 * 
 * @author Bosmon
 */
public class GraphMatch {
  public intVector vertices1 = new intVector(10);
  public intVector vertices2 = new intVector(10);
  public intVector edges1 = new intVector(10);
  public intVector edges2 = new intVector(10);

  public GraphMatch() {
    clear();
  }

  /**
   * Clear this match to an empty condition
   */
  public void clear() {
    vertices1.clear();
    vertices2.clear();
    edges1.clear();
    edges2.clear();
  }

  public static final int VERTEX_FORWARD = 0;
  public static final int VERTEX_REVERSE = 1;
  public static final int EDGE_FORWARD = 2;
  public static final int EDGE_REVERSE = 3;

  /**
   * Search through this graph match for the specified vertex or edge index,
   * returning the corresponding index from the other part of the match.
   * 
   * @param code A code taken from the four above specifying the sort of search
   *          to be performed.
   * @param findindex The edge or vertex index to be found.
   * @return The required corresponding edge or vertex index.
   */
  public int find(int code, int findindex) {
    intVector source = null;
    intVector target = null;
    switch (code) {
    case VERTEX_FORWARD:
      source = vertices1;
      target = vertices2;
      break;
    case VERTEX_REVERSE:
      source = vertices2;
      target = vertices1;
      break;
    case EDGE_FORWARD:
      source = edges1;
      target = edges2;
      break;
    case EDGE_REVERSE:
      source = edges2;
      target = edges1;
      break;
    default:
      throw new AssertionException("Unknown code " + code
          + " supplied to GraphMatch.find");
    }
    int size = source.size();
    int[] ints = source.getBackingStore();
    for (int i = size - 1; i >= 0; --i) {
      if (ints[i] == findindex)
        return target.intAt(i);
    }
    return Graph.INVALID_VERTEX;
  }
}