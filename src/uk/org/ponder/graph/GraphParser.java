/*
 * Created on Feb 10, 2004
 */
package uk.org.ponder.graph;

import java.io.IOException;
import java.io.StringReader;

import uk.org.ponder.saxalizer.SAXLeafTypeParser;
import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.util.LexReader;
import uk.org.ponder.util.LexUtil;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * Parses a graph object from a textual representation.
 * 
 * @author Bosmon
 */
// QQQQQQQ SaxLeaf mechanism is unbelievably inefficient!!!
public class GraphParser implements SAXLeafTypeParser {
  WriteableGraph graph;
  public Object parse(String graphstring) {
    LexReader lr = new LexReader(new StringReader(graphstring));
    try {
    return parseGraph(lr);
    }
    catch (IOException ioe) {
      throw UniversalRuntimeException.accumulate(ioe, "Error parsing graph: " + ioe.getMessage());
    }
  }
  public Graph parseGraph(LexReader lr) throws IOException {
    graph = new ConcreteGraph();
    LexUtil.skipWhite(lr);
    LexUtil.expect(lr, "vertices:");
    LexUtil.skipWhite(lr);
    int nodecount = LexUtil.readInt(lr);
    LexUtil.skipWhite(lr);
    LexUtil.expect(lr, "edges:");
    LexUtil.skipWhite(lr);
    int edgecount = LexUtil.readInt(lr);
    LexUtil.skipWhite(lr);
    for (int i = 0; i < nodecount; ++ i) {
      graph.createVertex();
    }
    for (int i = 0; i < edgecount; ++ i) {
      int node1 = LexUtil.readInt(lr);
      LexUtil.expect(lr, "->");
      int node2 = LexUtil.readInt(lr);
      graph.createEdge(node1, node2);
      LexUtil.skipWhite(lr);
    }
    LexUtil.expectEmpty(lr);
    return graph;
  }
  public CharWrap render(Object grapho, CharWrap renderinto) {
    // QQQQQ to be implemented
    return renderinto;
  }
}
