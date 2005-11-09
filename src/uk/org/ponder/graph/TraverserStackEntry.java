/*
 * Created on 16-Sep-2003
 */
package uk.org.ponder.graph;

/**
 * Represents a stack frame in the <code>GraphVisitor</code> interface.
 * @author Bosmon
 */
public class TraverserStackEntry {
  public int vertexindex;
  public int leadingedge;
  public int recursionlevel;
  TraverserStackEntry(int vertexindex, int leadingedge, int recursionlevel) {
    this.vertexindex = vertexindex;
    this.leadingedge = leadingedge;
    this.recursionlevel = recursionlevel;
    }
  }
