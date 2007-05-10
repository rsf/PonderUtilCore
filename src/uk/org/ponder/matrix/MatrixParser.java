/*
 * Created on 21-Feb-2005
 */
package uk.org.ponder.matrix;

import java.io.IOException;
import java.io.StringReader;

import uk.org.ponder.conversion.LeafObjectParser;
import uk.org.ponder.streamutil.read.LexReader;
import uk.org.ponder.streamutil.read.LexUtil;
import uk.org.ponder.streamutil.read.PushbackRIS;
import uk.org.ponder.streamutil.read.StringRIS;
import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (amb26@ponder.org.uk)
 * 
 */
public class MatrixParser implements LeafObjectParser{

  public Object parse(String matrixstring) {
    PushbackRIS lr = new PushbackRIS(new StringRIS(matrixstring));
    try {
      return parseMatrix(lr);
    }
    catch (Exception ioe) {
      throw UniversalRuntimeException.accumulate(ioe, "Error parsing matrix: " + ioe.getMessage());
    }
  }
  
  public Matrix parseMatrix(PushbackRIS lr)  {
    LexUtil.skipWhite(lr);
    LexUtil.expect(lr, "dimensions:");
    LexUtil.skipWhite(lr);
    int rows = LexUtil.readInt(lr);
    LexUtil.expect(lr, "x");
    int cols = LexUtil.readInt(lr);
    Matrix mat = new Matrix(rows, cols);
    for (int row = 0; row < rows; ++ row) {
      for (int col = 0; col < cols; ++ col) {
        LexUtil.skipWhite(lr);
        double val = LexUtil.readDouble(lr);
        mat.setMval(row, col, val);
      }
    }
    LexUtil.expectEmpty(lr);
    return mat;
  }
 

  public String render(Object torendero) {
    Matrix torender = (Matrix)torendero;
    int rows = torender.rows;
    int cols = torender.cols;
    CharWrap renderinto = new CharWrap();
    renderinto.append("dimensions: ").append(rows).append("x").append(cols);
    renderinto.append("\n");
    for (int row = 0; row < rows; ++ row) {
      for (int col = 0; col < cols; ++ col) {
        renderinto.append(Double.toString(torender.getMval(row, col))).append(" ");
      }
      renderinto.append("\n");
    }
    return renderinto.toString();
  }

  public Object copy(Object tocopy) {
    // TODO Auto-generated method stub
    return null;
  }

}
