/*
 * Created on Oct 15, 2004
 */
package uk.org.ponder.graph;

import uk.org.ponder.intutil.constIntIterator;
import uk.org.ponder.intutil.intPair;

/**
 * @author Antranig Basman (amb26@ponder.org.uk)
 * 
 */
public interface Graph {
  public static final int INVALID_VERTEX = -1;

  public static final int INVALID_EDGE = -1;
  
  public ConcreteGraph getTarget();
  /** Returns an identical copy of this Graph which shares no storage
   * with it. Vertex and edge numbering will be preserved.
   * @return A copy of this Graph object.
   */
  public abstract ConcreteGraph copy();

  /** Returns the number of vertices in this graph
   * @return The number of vertices in this graph
   */
  public abstract int numVertices();

  /** Returns the number of edges in this graph
   * @return The number of edges in this graph
   */
  public abstract int numEdges();

  /** Returns the size of the array required to cover all the vertex indices in this graph,
   * that is, the largest vertex index plus one.
   * @return The required array size.
   */
  public abstract int vertexSize();

  /** Returns the size of the array required to cover all the edge indices in this graph,
   * that is, the largest edge index plus one.
   * @return The required array size.
   */
  public abstract int edgeSize();

  /** Returns an iterator through all the edges in this graph. 
   * @return An edge iterator
   */
  public abstract constIntIterator beginEdge();

  /** Returns an iterator through all the vertices in this graph.
   * @return A vertex iterator.
   */
  public abstract constIntIterator beginVertex();

  /** Returns the degree (number of connected edges) of the specified vertex.
   * 
   * @param vertexindex The index of the vertex for which the degree is required.
   * @return The vertex degree.
   */
  public abstract int vertexDegree(int vertexindex);

  /** Returns an iterator through all the edges connected to the supplied graph vertex.
   * @param vertexindex The index of the vertex for which the connected edges are required.
   * @return An edge iterator.
   */
  public abstract constIntIterator getConnectedEdges(int vertexindex);

  /** Returns the pair of vertices connected by this edge.
   * @param edgeindex The index of the edge for which the end vertices are required.
   * @return An intPair object containing the indices of the two end vertices.
   */
  public abstract intPair getConnectedVertices(int edgeindex);

  /** Returns an iterator through all of the vertices adjacent to the specified vertex,
   * that is all those directly joined to it via an edge.
   * @param vertexindex The index of the vertex for which the adjecent vertices are required.
   * @return An iterator through the neighbouring vertex indices.
   */
  public abstract constIntIterator getAdjacentVertices(final int vertexindex);

  /** Returns the vertex at the other end of an edge, given a vertex index at one end.
   * @param vertex The index of the vertex at one end of the edge.
   * @param edge The edge to the other vertex.
   * @return The vertex at the other end of the edge
   * @throws AssertionException if the vertex is not at one end of the edge.
   */
  public abstract int getOtherVertex(int vertex, int edge);

  /** Returns a string representation of this graph, primarily for debugging purposes
   */
  public abstract String toString();
}