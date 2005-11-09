/*
 * Created on Nov 16, 2004
 */
package uk.org.ponder.graph;

import uk.org.ponder.intutil.Algorithms;
import uk.org.ponder.intutil.constIntIterator;
import uk.org.ponder.intutil.intPair;

/**
 * @author Antranig Basman (amb26@ponder.org.uk)
 * 
 */
public class CachedGraph {
  public int numvertices;
  public int numedges;
  
  public int vertexsize;
  public int edgesize;
  
  public int[] vertices;
  public int[] edges;
  
  public int[] backvertex;
  int[] backedge;
  
  // index is compacted index
  public int[] vertexdegree;
  
  public int[][] vertextoedge;
  
  // index is compacted index
  public int[] edgetovertex1;
  public int[] edgetovertex2;
  
  public CachedGraph() {}
  public CachedGraph(Graph g) {
    cache(g);
  }
  
  public void cache(Graph g) {
    numvertices = g.numVertices();
    vertexsize = g.vertexSize();
    vertices = Algorithms.ensure_size(vertices, numvertices);
    backvertex = Algorithms.ensure_size(backvertex, vertexsize);
    vertexdegree = Algorithms.ensure_size(vertexdegree, numvertices);
    if (vertextoedge == null || vertextoedge.length < numvertices) {
      vertextoedge = new int[numvertices * 3 / 2][];
    }
    int vertexind = 0;
    for (constIntIterator vit = g.beginVertex(); vit.valid(); vit.next()) {
      int vertex = vit.getInt();
      vertices[vertexind] = vertex;
      backvertex[vertex] = vertexind;
      vertexdegree[vertexind] = g.vertexDegree(vertex);
      vertextoedge[vertexind] = Algorithms.ensure_size(vertextoedge[vertexind], vertexdegree[vertexind]);
      int adjeno = 0;
      for (constIntIterator eit = g.getConnectedEdges(vertex); eit.valid(); eit.next()) {
        vertextoedge[vertexind][adjeno++] = eit.getInt();
      }
      ++ vertexind;
    }
    numedges = g.numEdges();
    edgesize = g.edgeSize();
    edges = Algorithms.ensure_size(edges, numedges);
    backedge = Algorithms.ensure_size(backedge, edgesize);
    edgetovertex1 = Algorithms.ensure_size(edgetovertex1, numedges);
    edgetovertex2 = Algorithms.ensure_size(edgetovertex2, numedges);
    int edgeno = 0;
    for (constIntIterator eit = g.beginEdge(); eit.valid(); eit.next()) {
      int edge = eit.getInt();
      intPair vpair = g.getConnectedVertices(edge);
      edges[edgeno] = edge;
      backedge[edge] = edgeno;
      edgetovertex1[edgeno] = vpair.first;
      edgetovertex2[edgeno] = vpair.second;
      edgeno++;
    }
  }
  // Accepts true graph indices.
  public int getOtherVertex(int vertex, int edge) {
    int edgeno = backedge[edge];
    return edgetovertex1[edgeno] == vertex? edgetovertex2[edgeno] : edgetovertex1[edgeno]; 
  }
}
