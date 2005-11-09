/*
 * Created on 29-Sep-2003
 */
package uk.org.ponder.graph;

import java.util.ArrayList;

import uk.org.ponder.intutil.constIntIterator;
import uk.org.ponder.intutil.intPair;
import uk.org.ponder.intutil.intVector;
import uk.org.ponder.matrix.Matrix;
import uk.org.ponder.util.Logger;

/**
 * Contains useful utility methods for operating on graphs.
 * 
 * @author Bosmon
 */
public class GraphUtil {
  public static final String IN_EDGES = "IN_EDGES";
  public static final String OUT_EDGES = "OUT_EDGES";
  /** Returns a vector of alternating int indices in pairs (good 70s-style
   * programming). Even numbers represent the "other" vertex, odd numbers
   * represent the "edge number".
   */
  public static intVector getDirectedEdges(final Graph g, int vertex, String direction) {
    int degree = g.vertexDegree(vertex);
    intVector togo = new intVector(degree * 2);
    for (constIntIterator eit = g.getConnectedEdges(vertex); eit.valid(); eit.next()) {
      int edgeno = eit.getInt();
      intPair pair = g.getConnectedVertices(edgeno);
      if (vertex == (direction == IN_EDGES? pair.second: pair.first)) {
        togo.addElement(direction == IN_EDGES? pair.first : pair.second);
        togo.addElement(edgeno);
      }
    }
    return togo;
  }
  
  public static int directedEdgeDegree(final Graph g, int vertex, String direction) {
    int count = 0;
    for (constIntIterator eit = g.getConnectedEdges(vertex); eit.valid(); eit.next()) {
      int edgeno = eit.getInt();
      intPair pair = g.getConnectedVertices(edgeno);
      if (vertex == (direction == IN_EDGES? pair.second: pair.first)) {
        ++count;
      }
    }
    return count;
  }
  
  public static int[] topologicalSort(WriteableGraph g) {
    int[] togo = new int[g.numVertices()];
    int outpos = 0;
    intVector vertexstack = new intVector(g.numVertices());
    for (constIntIterator vit = g.beginVertex(); vit.valid(); vit.next()) {
      int vertex = vit.getInt();
      if (directedEdgeDegree(g, vertex, IN_EDGES) == 0) {
        vertexstack.addElement(vertex);
      }
    }
    intVector destructEdge = new intVector(5);
    while (vertexstack.size() > 0) {
      destructEdge.clear();
      // we will run the queue BACKWARDS since we expect edges to be RARE.
      int pop = vertexstack.popElement();
      togo[outpos++] = pop;
      for (constIntIterator eit = g.getConnectedEdges(pop); eit.valid(); eit.next()) {
        int edge = eit.getInt();
        intPair connected = g.getConnectedVertices(edge);
        if (connected.second == pop) {
          destructEdge.addElement(edge);
        }
      }
      for (int i = 0; i < destructEdge.size(); ++ i) {
        int edgeno = destructEdge.intAt(i);
        int othervertex = g.getOtherVertex(pop, edgeno);
        g.deleteEdge(edgeno);
        if (directedEdgeDegree(g, othervertex, IN_EDGES) == 0) {
          vertexstack.addElement(othervertex);
        }
      }
    }
    return togo;
  }
    
  // a utility method for the use of allPointsShortest
  private static void extendEdges(Graph g, int v1, int v2, Matrix sps,
      intVector newsource, intVector newdest, int pathlength) {
    for (constIntIterator eit2 = g.getConnectedEdges(v2); eit2.valid(); eit2
        .next()) {
      int edge2 = eit2.getInt();
      int v02 = g.getOtherVertex(v2, edge2);
      if (v02 != v1 && sps.getValue(v1 + 1, v02 + 1) == 0) {
        sps.setSymm(v1 + 1, v02 + 1, pathlength);
        newsource.addElement(v1);
        newdest.addElement(v02);
      }
    }
  }

  /**
   * Compute the matrix of shortest distances between each vertex. This uses a
   * special algorithm for constant edge weight graphs that is O(EV).
   * 
   * @param g The graph for which all shortest paths are required.
   * @return A matrix of shortest distances between graph vertices.
   */
  public static Matrix allPointsShortest(Graph g) {
    Matrix togo = new Matrix(g.vertexSize());
    int pathlength = 1;
    intVector prevsource = new intVector(10);
    intVector prevdest = new intVector(10);
    for (constIntIterator eit = g.beginEdge(); eit.valid(); eit.next()) {
      int thisedge = eit.getInt();
      intPair vs = g.getConnectedVertices(thisedge);
      togo.setSymm(vs.first + 1, vs.second + 1, pathlength);
      prevsource.addElement(vs.first);
      prevdest.addElement(vs.second);
    }
    while (prevsource.size() > 0) {
      ++pathlength;
      intVector newsource = new intVector(10);
      intVector newdest = new intVector(10);
      for (int i = 0; i < prevsource.size(); ++i) {
        // this loop body executes once for each vertex pair - O(V^2).
        int v1 = prevsource.intAt(i);
        int v2 = prevdest.intAt(i);
        // we must extend edges in both directions to be sure of
        // not "missing" the correct extension. This could be avoided
        // by some kind of cunning "parity" scheme..
        extendEdges(g, v1, v2, togo, newsource, newdest, pathlength);
        extendEdges(g, v2, v1, togo, newsource, newdest, pathlength);
      }
      prevsource = newsource;
      prevdest = newdest;
    }
    return togo;
  }

  /**
   * Extract a subgraph of a specified graph.
   * 
   * @param source The graph from which the subgraph is to be extracted
   * @param vertexindices The vertex indices, in order, which are to form the
   *          vertices of the new graph.
   * @param edgeindices The edge indices, in order, which are to form the edges
   *          of the new graph.
   * @param returnmatch A graph match corresponding vertices and edges of the
   *          old graph to the new. The vertices of the old graph appear on the
   *          left, the new graph on the right. This match object will be cleared
   * 	      prior to use.
   * @return The newly extracted graph object.
   */
  public static ConcreteGraph mangleGraph(Graph source, int[] vertexindices,
      int[] edgeindices, GraphMatch returnmatch) {
    ConcreteGraph togo = new ConcreteGraph();
    returnmatch.clear();
    for (int i = 0; i < vertexindices.length; ++i) {
      int newvi = togo.createVertex();
      returnmatch.vertices1.addElement(vertexindices[i]);
      returnmatch.vertices2.addElement(newvi);
    }
    for (int i = 0; i < edgeindices.length; ++i) {
      int ei = edgeindices[i];
      intPair oldv = source.getConnectedVertices(ei);
      int newv1 = returnmatch.find(GraphMatch.VERTEX_FORWARD, oldv.first);
      int newv2 = returnmatch.find(GraphMatch.VERTEX_FORWARD, oldv.second);
      int newei = togo.createEdge(newv1, newv2);
      returnmatch.edges1.addElement(ei);
      returnmatch.edges2.addElement(newei);
    }
    return togo;
  }

  /**
   * Finds one of the vertices in the graph with minimal degree.
   * 
   * @param g The graph to be searched.
   * @return The index of a vertex with minimal degree.
   */
  public static int findMinDegreeVertex(Graph g) {
    int togo = Graph.INVALID_VERTEX;
    int mindegree = Integer.MAX_VALUE;
    // start at a vertex with minimal degree.
    for (constIntIterator vit = g.beginVertex(); vit.valid(); vit.next()) {
      int vertexindex = vit.getInt();
      int degree = g.vertexDegree(vertexindex);
      if (degree == 1) {
        togo = vertexindex;
        break;
      }
      if (degree < mindegree) {
        mindegree = degree;
        togo = vertexindex;
      }
    }
    return togo;
  }

  /**
   * Finds the edge in the specified graph joining two supplied vertices.
   * 
   * @param g The graph to be searched for the specified edge.
   * @param vertex1 The first vertex on the edge to be found.
   * @param vertex2 The second vertex on the edge to be found.
   * @return The required graph edge, or Graph.INVALID_VERTEX if there is no
   *         such edge.
   */
  public static int findEdge(Graph g, int vertex1, int vertex2) {
    for (constIntIterator eit = g.getConnectedEdges(vertex1); eit.valid(); eit
        .next()) {
      int edgeindex = eit.getInt();
      if (g.getOtherVertex(vertex1, edgeindex) == vertex2)
        return edgeindex;
    }
    return Graph.INVALID_VERTEX;
  }

  /**
   * Labels one of the connected components of the supplied graph.
   * 
   * @param rootvertex A vertex in the component of the graph to be labelled.
   *          The vertex must not already have been labelled.
   * @param g The graph holding the component to be labelled.
   * @param visited A boolean array with an entry for each vertex in the graph.
   *          Vertices which are visited as part of this component will be
   *          labelled as <code>true</code> in this array.
   * @return An array of vertex indices as an intVector labelling the required
   *         connected component of the graph.
   */
  public static intVector[] labelComponent(int rootvertex, Graph g,
      boolean[] visited, boolean[] visitededge) {
    Logger.println("Labelling from root " + rootvertex, Logger.DEBUG_SUBATOMIC);
    intVector vertexstack = new intVector(10);
    intVector vertexcomponent = new intVector(10);
    intVector edgecomponent = new intVector(10);
    vertexcomponent.addElement(rootvertex);
    vertexstack.addElement(rootvertex);
    visited[rootvertex] = true;
    while (vertexstack.size() != 0) {
      int topvertex = vertexstack.popElement();
      for (constIntIterator eit = g.getConnectedEdges(topvertex); eit.valid(); eit
          .next()) {
        int edge = eit.getInt();
        int adjv = g.getOtherVertex(topvertex, edge);
        if (!visited[adjv]) {
          visited[adjv] = true;
          //Logger.println("Added vertex " + adjv, Logger.DEBUG_SUBATOMIC);
          vertexstack.addElement(adjv);
          vertexcomponent.addElement(adjv);
        }
        if (!visitededge[edge]) {
          visitededge[edge] = true;
          edgecomponent.addElement(edge);   
        }
      }
    }
    return new intVector[] {vertexcomponent, edgecomponent};
  }

  /**
   * Label all of the connected components of this graph.
   * 
   * @param g The graph for which the components are to be found.
   * @return An <code>ArrayList</code> of intVector[] objects, where the 0th
   * element each lists vertices in one graph component, and the 1st
   * lists the edges.
   */
  public static ArrayList labelComponents(Graph g) {
    ArrayList components = new ArrayList(); // an ArrayList of intVectors
    boolean[] visited = new boolean[g.vertexSize()];
    boolean[] visitededge = new boolean[g.edgeSize()];
    for (constIntIterator vit = g.beginVertex(); vit.valid(); vit.next()) {
      int rootvertex = vit.getInt();
      if (!visited[rootvertex]) {
        intVector[] newcomp = labelComponent(rootvertex, g, visited, visitededge);
        components.add(newcomp);
      }
    }
    return components;
  }
}