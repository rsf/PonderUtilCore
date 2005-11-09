/*
 * Created on Oct 15, 2004
 */
package uk.org.ponder.graph;

import uk.org.ponder.intutil.Algorithms;
import uk.org.ponder.intutil.constIntIterator;
import uk.org.ponder.intutil.intPair;

/**
 * MappedGraph allows an arbitrary subset of a graph to be remapped
 * into a graph view. The vertex and edge indices supplied by this
 * view are those of the target graph.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *  
 */
public class MappedGraph implements Graph {
  private Graph target;
  // these map indices of the subgraph into the target
  int[] vertices;
  int[] edges;
  // these map target indices back to subgraph, where they exist
  int[] vertexmapped;
  int[] edgemapped;


  public ConcreteGraph getTarget() {
    return target.getTarget();
  }
  
  /** Return the forward (i.e. mapped graph) index of the supplied
   * target vertex. 
   * 
   * @param vertexindex
   * @return
   */
  public int getForwardVertex(int vertexindex) {
    return vertexmapped[vertexindex];
  }
  
  public int getForwardEdge(int edgeindex) {
    return edgemapped[edgeindex];
  }
  /**
   * Construct a graph mapping a portion of the specified graph.
   * 
   * @param target The target graph
   * @param vertices The vertex numbers of the target graph to be extracted, in
   *          order.
   * @param edges The edge numbers of the target graph to be extracted, in
   *          order.
   */
  public MappedGraph(Graph target, int[] vertices, int[] edges) {
    this.target = target;
    this.vertices = vertices;
    this.edges = edges;

    vertexmapped = Algorithms.fill(target.vertexSize(), INVALID_VERTEX);
    for (int i = 0; i < vertices.length; ++i) {
      vertexmapped[vertices[i]] = i;
    }
    edgemapped = Algorithms.fill(target.edgeSize(), INVALID_EDGE);
    for (int i = 0; i < edges.length; ++i) {
      edgemapped[edges[i]] = i;
    }
  }

  public ConcreteGraph copy() {
    // TODO Auto-generated method stub
    return null;
  }

  public int numVertices() {
    return vertices.length;
  }

  public int numEdges() {
    return edges.length;
  }

  public int vertexSize() {
    return target.vertexSize();
  }

  public int edgeSize() {
    return target.edgeSize();
  }

  public constIntIterator beginEdge() {
    return new constIntIterator() {
      int index = 0;

      public boolean valid() {
        return index < edges.length;
      }
      public void next() {
        ++index;
      }
      public int getInt() {
        return edges[index];
      }
    };
  }

  public constIntIterator beginVertex() {
    return new constIntIterator() {
      int index = 0;
      public boolean valid() {
        return index < vertices.length;
      }
      public void next() {
        ++index;
      }
      public int getInt() {
        return vertices[index];
      }
    };
  }

  public int vertexDegree(int vertexindex) {
    int count = 0;
    for (constIntIterator edgit = getConnectedEdges(vertexindex); 
       edgit.valid(); edgit.next()) {
      ++count;
    }
    return count;
  }

  public constIntIterator getConnectedEdges(final int vertexindex) {
    return new constIntIterator() {
      constIntIterator inner = target.getConnectedEdges(vertexindex);
      private void hopalong() {
        while (true) {
          if (!inner.valid() || edgemapped[inner.getInt()] != INVALID_EDGE)
            break;
          inner.next();
        } 
      }
      public boolean valid() {
        hopalong();
        return inner.valid();
      }
      public void next() {
        inner.next();
        hopalong();
      }
      public int getInt() {
        return inner.getInt();
      }
    };
  }

  public intPair getConnectedVertices(int edgeindex) {
    intPair targetpair = target.getConnectedVertices(edgeindex);
    return targetpair;
  }

  public constIntIterator getAdjacentVertices(final int vertexindex) {
    return new constIntIterator() {
      constIntIterator inner = target.getAdjacentVertices(vertexindex);
      private void hopalong() {
        while (true) {
          if (!inner.valid() || vertexmapped[inner.getInt()] != INVALID_VERTEX)
            break;
          inner.next();
        }
      }

      public boolean valid() {
        // TODO: SURELY THIS IS BROKEN!!!!
        hopalong();
        return inner.valid();
      }
      public void next() {
        inner.next();
        hopalong();
      }
      public int getInt() {
        return inner.getInt();
      }
    };
  }

  public int getOtherVertex(int vertex, int edge) {
    return target.getOtherVertex(vertex, edge);
  }


}