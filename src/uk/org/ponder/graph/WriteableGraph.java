/*
 * Created on Oct 15, 2004
 */
package uk.org.ponder.graph;

import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (amb26@ponder.org.uk)
 * 
 */
public interface WriteableGraph extends Graph {
  /** Clears this Graph object, returning it to its original condition empty of
   * vertices and edges.
   */
  public abstract void clear();

  /** Creates a new vertex in the graph, initially not connected to any others. Returns the
   * index which has been allocated to the vertex.
   * @return The new vertex index.
   */
  public abstract int createVertex();

  /** Creates a new edge in the graph, joining the two specified vertices. The vertices
   * must not already be joined by an edge. Returns the index of the newly created edge. 
   * @param vertex1 The first vertex to be joined by the edge
   * @param vertex2 The second vertex to be joined by the edge
   * @return The new edge index
   */
  public abstract int createEdge(int vertex1, int vertex2)
      throws UniversalRuntimeException;

  /** Deletes the specified vertex, and all the edges that meet it. This will leave gaps in the
   * ranges of valid vertices and edges.
   * @param todelete The index of the vertex to be deleted.
   */
  public abstract void deleteVertex(int todelete);

  /** Deletes the edge with the specified index. This will leave a gap in the range
   * of valid edge indices. A GraphModifiedEvent will be fired
   * to all observers of this graph.
   * @param todelete The index of the edge to be deleted.*/
  public abstract void deleteEdge(int todelete);
}