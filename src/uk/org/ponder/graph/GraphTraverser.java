/*
 * Created on 12-Sep-2003
 */
package uk.org.ponder.graph;

import java.util.ArrayList;

import uk.org.ponder.intutil.constIntIterator;
import uk.org.ponder.util.Logger;

/**
 * GraphTraverser encapsulates the logic for traversing a graph component, in either
 * depth-first or breadth-first order. The client supplies a callback interface, a 
 * <code>GraphVisitor</code> which is informed as the traversal progresses.
 * @author Bosmon
 */
public class GraphTraverser {
  private Graph g;
  private GraphVisitor gv;
 
  private ArrayList vertexstack;
  private boolean[] usedvertex;
  private boolean[] usededge;
  private void init(Graph g, GraphVisitor gv, int root) {
    this.g = g;
    this.gv = gv;
    usedvertex = new boolean[g.numVertices()];
    usededge = new boolean[g.numEdges()];
    vertexstack = new ArrayList();
    pushVertex(
      new TraverserStackEntry(root, Graph.INVALID_EDGE, 0));
  }
  private void pushVertex(TraverserStackEntry entry) {
    if (usedvertex[entry.vertexindex]) {
      // Logger.println("Ring already closed through vertex " + entry.vertexindex,
      // Logger.DEBUG_SUBATOMIC);
      return;
    }
    if (entry.leadingedge != Graph.INVALID_EDGE) {
      gv.pushVertex(entry);
    }
    int topush = entry.vertexindex;
    usedvertex[topush] = true;
    for (constIntIterator eit = g.getConnectedEdges(topush); eit.valid(); eit.next()) {
      int edgeindex = eit.getInt();
      if (edgeindex == entry.leadingedge)
        continue;
      int othervertex = g.getOtherVertex(topush, edgeindex);
      if (usedvertex[othervertex]) {
        gv.closeEdge(edgeindex);
      }
      else {
        TraverserStackEntry se = 
          new TraverserStackEntry(othervertex, edgeindex,
            entry.recursionlevel + 1);
        vertexstack.add(se);
        /*Logger.println("Pushed edge " + edgeindex + " " + topush
         + "->" + othervertex + " recursion " 
         + (entry.recursionlevel + 1), Logger.DEBUG_SUBATOMIC);*/
        gv.pushEdge(edgeindex);
      }
      //usededge[edgeindex] = true;
    }
  }
  /** Traverse the supplied graph in breadth-first order from the specified root 
   * vertex.
   * @param g The graph to be traversed.
   * @param rootvertex The root vertex at which traversal is to begin.
   * @param gv A GraphVisitor object which is to be informed as graph traversal proceeds.
   */
  public void traverseBFS(Graph g, int rootvertex, GraphVisitor gv) {
    init(g, gv, rootvertex);
    while (vertexstack.size() > 0) {
      TraverserStackEntry se = 
        (TraverserStackEntry)vertexstack.remove(0);
      pushVertex(se);
    }
  }
  /** Traverse the supplied graph in depth-first order from the specified root 
    * vertex.
    * @param g The graph to be traversed.
    * @param rootvertex The root vertex at which traversal is to begin.
    * @param gv A GraphVisitor object which is to be informed as graph traversal proceeds.
    */
  public void traverseDFS(Graph g, int rootvertex, GraphVisitor gv) {
    init(g, gv, rootvertex);
    while (vertexstack.size() > 0) {
      TraverserStackEntry se = 
        (TraverserStackEntry)vertexstack.remove(vertexstack.size() - 1);
      pushVertex(se);
    }
  }
}
