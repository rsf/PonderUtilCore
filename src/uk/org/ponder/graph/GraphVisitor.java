/*
 * Created on 12-Sep-2003
 */
package uk.org.ponder.graph;

/**
 * A callback interface implemented by clients of the GraphTraverser class. They
 * are informed of the progress of the graph traversal by calls to these methods.
 * @author Bosmon
 */
public interface GraphVisitor {
  /** The traverser has walked along an edge to a new vertex.
   * @param stackentry A TraverserStackEntry object holding details of the edge walked along, 
   * the new vertex index and the recursion depth at this point.
   */
  public void pushVertex(TraverserStackEntry stackentry);
  /** The traverser has seen new edges available from a vertex, and planned to visit them
   * later, either through pushVertex or closeEdge.
   * @param edgeindex The index of the edge which will be visited.
   */
  public void pushEdge(int edgeindex);
  /** The traverser has seen an edge forming a ring closure.
   * @param edgeindex The index of an edge forming a ring.
   */
  public void closeEdge(int edgeindex);
}
