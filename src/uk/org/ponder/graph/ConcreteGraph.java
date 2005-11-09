package uk.org.ponder.graph;

import uk.org.ponder.intutil.constIntIterator;
import uk.org.ponder.intutil.intPair;
import uk.org.ponder.intutil.intVector;
import uk.org.ponder.saxalizer.SAXLeafParser;
import uk.org.ponder.util.AssertionException;
import uk.org.ponder.util.UniversalRuntimeException;

import java.util.ArrayList;

/**
 * The class Graph represents an (undirected) graph using an adjacency list
 * representation. Vertices and edges are represented by integers, which are
 * intended to be "almost" contiguous. This means that it should not be
 * enormously wasteful to allocate an array with indices covering the range of
 * vertex and edge indices, but there may be "gaps" where an edge or vertex has
 * been deleted. It is the responsibility of clients to stop edge and vertex
 * indices from becoming too fragmented.
 * <p>
 * The Graph class will never alter the index number of an edge or vertex once
 * it has been allocated.
 * <p>
 * Index numbers start at 0, with negative numbers representing invalid indices.
 * Clients must always use the supplied iterator methods beginEdge(),
 * beginVertex() to iterate through edge and vertex indices, and never iterate
 * though a range directly in case it contains deleted entries.
 * 
 * @author Bosmon
 */
public class ConcreteGraph implements WriteableGraph {
  static {
    SAXLeafParser.instance().registerParser(ConcreteGraph.class,
        new GraphParser());
  }

  public ConcreteGraph getTarget() {
    return this;
  }

  private static final int INITIAL_VERTEXSIZE = 10;
  private static final int INITIAL_EDGESIZE = 4;
  private intVector vertexindices; // kept sorted in ascending order.
  private intVector edgeindices; // kept sorted in ascending order.
  private intVector[] edgetovertex; // source and target vertices, with gaps
  private ArrayList vertextoedge; // a list of intVectors, with gaps.
  
  private intVector getEdgeList(int vertexindex) {
    return (intVector) vertextoedge.get(vertexindex);
  }

  /** Constructs a new empty graph */
  public ConcreteGraph() {
    vertexindices = new intVector(INITIAL_VERTEXSIZE);
    edgeindices = new intVector(INITIAL_VERTEXSIZE);
    edgetovertex = new intVector[2];
    edgetovertex[0] = new intVector(INITIAL_VERTEXSIZE);
    edgetovertex[1] = new intVector(INITIAL_VERTEXSIZE);
    vertextoedge = new ArrayList();
  }
  
  // a private constructor to be used for copying graphs
  private ConcreteGraph(int ignore) {
  }

  /**
   * Clears this Graph object, returning it to its original condition empty of
   * vertices and edges.
   */
  public void clear() {
    vertexindices.clear();
    edgeindices.clear();
    edgetovertex[0].clear();
    edgetovertex[1].clear();
    vertextoedge.clear();
  }

  /**
   * Returns an identical copy of this Graph which shares no storage with it.
   * Vertex and edge numbering will be preserved.
   * @return A copy of this Graph object.
   */
  public ConcreteGraph copy() {
    ConcreteGraph togo = new ConcreteGraph(0);
    togo.vertexindices = vertexindices.copy();
    togo.edgeindices = edgeindices.copy();
    togo.edgetovertex = new intVector[2];
    togo.edgetovertex[0] = edgetovertex[0].copy();
    togo.edgetovertex[1] = edgetovertex[1].copy();
    togo.vertextoedge = new ArrayList();
    for (int i = 0; i < vertextoedge.size(); ++i) {
      intVector thisv2e = getEdgeList(i);
      if (thisv2e != null) {
        togo.vertextoedge.add(thisv2e.copy());
      }
      else {
        togo.vertextoedge.add(null);
      }
    }
    return togo;
  }

  /**
   * Returns the number of vertices in this graph
   * @return The number of vertices in this graph
   */
  public int numVertices() {
    return vertexindices.size();
  }

  /**
   * Returns the number of edges in this graph
   * @return The number of edges in this graph
   */
  public int numEdges() {
    return edgeindices.size();
  }

  /**
   * Returns the size of the array required to cover all the vertex indices in
   * this graph, that is, the largest vertex index plus one.
   * 
   * @return The required array size.
   */
  public int vertexSize() {
    return vertextoedge.size();
  }

  /**
   * Returns the size of the array required to cover all the edge indices in
   * this graph, that is, the largest edge index plus one.
   * @return The required array size.
   */
  public int edgeSize() {
    return edgetovertex[0].size();
  }

  /**
   * Returns an iterator through all the edges in this graph.
   * @return An edge iterator
   */
  public constIntIterator beginEdge() {
    return edgeindices.beginIterator();
  }

  /**
   * Returns an iterator through all the vertices in this graph.
   * @return A vertex iterator.
   */
  public constIntIterator beginVertex() {
    return vertexindices.beginIterator();
  }

  /**
   * Returns the degree (number of connected edges) of the specified vertex.
   * @param vertexindex The index of the vertex for which the degree is
   *          required.
   * @return The vertex degree.
   */
  public int vertexDegree(int vertexindex) {
    return getEdgeList(vertexindex).size();
  }

  /**
   * Returns an iterator through all the edges connected to the supplied graph
   * vertex.
   * @param vertexindex The index of the vertex for which the connected edges
   *          are required.
   * @return An edge iterator.
   */
  public constIntIterator getConnectedEdges(int vertexindex) {
    return getEdgeList(vertexindex).beginIterator();
  }

  /**
   * Returns the pair of vertices connected by this edge.
   * @param edgeindex The index of the edge for which the end vertices are
   *          required.
   * @return An intPair object containing the indices of the two end vertices.
   */
  // This could be Hollinghurstized if allocation is a problem.
  public intPair getConnectedVertices(int edgeindex) {
    return new intPair(edgetovertex[0].intAt(edgeindex), edgetovertex[1]
        .intAt(edgeindex));
  }

  /**
   * Returns an iterator through all of the vertices adjacent to the specified
   * vertex, that is all those directly joined to it via an edge.
   * @param vertexindex The index of the vertex for which the adjecent vertices
   *          are required.
   * @return An iterator through the neighbouring vertex indices.
   */
  public constIntIterator getAdjacentVertices(final int vertexindex) {
    final constIntIterator edgeit = getConnectedEdges(vertexindex);
    return new constIntIterator() {
      public boolean valid() {
        return edgeit.valid();
      }

      public void next() {
        edgeit.next();
      }

      public int getInt() {
        return getOtherVertex(vertexindex, edgeit.getInt());
      }

    };
  }

  /**
   * Returns the vertex at the other end of an edge, given a vertex index at one
   * end.
   * 
   * @param vertex The index of the vertex at one end of the edge.
   * @param edge The edge to the other vertex.
   * @return The vertex at the other end of the edge
   * @throws AssertionException if the vertex is not at one end of the edge.
   */
  public int getOtherVertex(int vertex, int edge) {
    int vertex1 = edgetovertex[0].intAt(edge);
    int vertex2 = edgetovertex[1].intAt(edge);
    if (vertex == vertex1)
      return vertex2;
    else if (vertex == vertex2)
      return vertex1;
    else
      throw new AssertionException("Vertex " + vertex
          + " supplied to getOtherVertex which is not joined to edge " + edge);
  }

  /**
   * Creates a new vertex in the graph, initially not connected to any others.
   * Returns the index which has been allocated to the vertex.
   * 
   * @return The new vertex index.
   */
  public int createVertex() {
    int nextindex = vertexindices.size() == 0 ? 0
        : vertexindices.peek() + 1;
    //int vertices = vertexindices.size() + 1;
    vertexindices.addElement(nextindex);
    vertextoedge.add(new intVector(INITIAL_EDGESIZE));
    return nextindex;
  }

  /**
   * Creates a new edge in the graph, joining the two specified vertices. The
   * vertices must not already be joined by an edge. Returns the index of the
   * newly created edge.
   * 
   * @param vertex1 The first vertex to be joined by the edge
   * @param vertex2 The second vertex to be joined by the edge
   * @return The new edge index
   * @throws ChoracleRuntimeException if the two vertex indices are the same, or
   *           they are already joined by an edge
   */
  public int createEdge(int vertex1, int vertex2)
      throws UniversalRuntimeException {
    if (vertex1 == vertex2) {
      throw new UniversalRuntimeException("Cannot create loop at vertex "
          + vertex1);
    }
    int nextindex = edgeindices.size() == 0 ? 0
        : edgeindices.peek() + 1;
    intVector el1 = getEdgeList(vertex1);
    for (int i = 0; i < el1.size(); ++i) {
      int v1 = edgetovertex[0].intAt(el1.intAt(i));
      int v2 = edgetovertex[1].intAt(el1.intAt(i));
      if (vertex1 == v1 && vertex2 == v2 || vertex1 == v2 && vertex2 == v1) {
        throw new UniversalRuntimeException("Edge between vertices " + vertex1
            + " and " + vertex2 + " already exists in createEdge");
      }
    }
    el1.addElement(nextindex);
    intVector el2 = getEdgeList(vertex2);
    el2.addElement(nextindex);
    edgeindices.addElement(nextindex);
    // expand/contract edgetovertex to avoid bug where graph is cleared, setting
    // nextindex
    // to 0, but edgetovertex is still full.
    edgetovertex[0].setSize(nextindex + 1);
    edgetovertex[0].setIntAt(nextindex, vertex1);
    edgetovertex[1].setSize(nextindex + 1);
    edgetovertex[1].setIntAt(nextindex, vertex2);

    return nextindex;
  }

  /**
   * Deletes the specified vertex, and all the edges that meet it. This will
   * leave gaps in the ranges of valid vertices and edges.
   * 
   * @param todelete The index of the vertex to be deleted.
   */
  public void deleteVertex(int todelete) {
    int deleteindex = vertexindices.findInt(todelete);
    if (deleteindex == -1) {
      throw new AssertionException("Tried to delete nonexistent vertex "
          + todelete);
    }
    vertexindices.removeElementAt(deleteindex);
    // for each outgoing edge, delete it!
    intVector outedges = getEdgeList(todelete);
    // do not use a for loop since the edge entry itself will be deleted
    while (outedges.size() > 0) {
      deleteEdge(outedges.intAt(outedges.size() - 1));
    }
    outedges.clear(); // a placeholder is left in the ArrayList
  }

  /**
   * Deletes the edge with the specified index. This will leave a gap in the
   * range of valid edge indices. A GraphModifiedEvent will be fired to all
   * observers of this graph.
   * 
   * @param todelete The index of the edge to be deleted.
   */
  public void deleteEdge(int todelete) {
    int deleteindex = edgeindices.findInt(todelete);
    if (deleteindex == -1) {
      throw new AssertionException("Tried to delete nonexistent edge "
          + todelete);
    }
    // first remove the entry for the edge itself
    edgeindices.removeElementAt(deleteindex);
    // then remove entries from the two vertextoedge lists for the end vertices
    int vertex1 = edgetovertex[0].intAt(todelete);
    intVector v1list = getEdgeList(vertex1);
    int v1eindex = v1list.findInt(todelete);
    if (v1eindex == -1) {
      throw new AssertionException("deleteEdge: Edge " + todelete
          + " did not have entry in edgelist of vertex " + vertex1);
    }
    v1list.removeElementAt(v1eindex);
    int vertex2 = edgetovertex[1].intAt(todelete);
    intVector v2list = getEdgeList(vertex2);
    int v2eindex = v2list.findInt(todelete);
    if (v2eindex == -1) {
      throw new AssertionException("deleteEdge: Edge " + todelete
          + " did not have entry in edgelist of vertex " + vertex2);
    }
    v2list.removeElementAt(v2eindex);
    // finally clear the vertex entries for the edge itself
    edgetovertex[0].setIntAt(todelete, INVALID_VERTEX);
    edgetovertex[1].setIntAt(todelete, INVALID_VERTEX);
  }

  /**
   * Returns a string representation of this graph, primarily for debugging
   * purposes
   */
  public String toString() {
    StringBuffer togo = new StringBuffer();
    togo.append("Vertices:\n");
    for (constIntIterator vit = beginVertex(); vit.valid(); vit.next()) {
      togo.append(vit.getInt() + " with adjacent vertices ");
      for (constIntIterator adjit = getAdjacentVertices(vit.getInt()); adjit
          .valid(); adjit.next()) {
        togo.append(adjit.getInt() + " ");
      }
      togo.append("\n");
    }
    togo.append("Edges:\n");
    for (constIntIterator eit = beginEdge(); eit.valid(); eit.next()) {
      intPair vertices = getConnectedVertices(eit.getInt());
      togo.append(eit.getInt() + " (" + vertices.first + "->" + vertices.second
          + ")\n");
    }

    return togo.toString();
  }

}