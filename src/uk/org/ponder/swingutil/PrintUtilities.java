/*
 * Created on 02-Oct-2003
 */
package uk.org.ponder.swingutil;

import java.awt.*;
import javax.swing.*;
import java.awt.print.*;

// written by Marty Hall, Applied Physics Lab, Johns Hopkins University.
// http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-Printing.html

/** A class automating the printing of the complete contents of a SWING component, including
 * scaling optimally to fill a page.
 * All that is required is a simple call to the printComponent() method.
 * Why didn't SUN supply this? 
 * 
 * @author Bosmon
 */
public class PrintUtilities implements Printable {
  private Component componentToBePrinted;

  /** Opens a print dialog to the user allowing the printing of the specified component.
   * @param c The component to be printed.
   */ 
  public static void printComponent(Component c) {
    new PrintUtilities(c).print();
  }

  public PrintUtilities(Component componentToBePrinted) {
    this.componentToBePrinted = componentToBePrinted;
  }

  public void print() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(this);
    if (printJob.printDialog())
      try {
        printJob.print();
      }
      catch (PrinterException pe) {
        System.out.println("Error printing: " + pe);
      }
  }

  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
    if (pageIndex > 0) {
      return (NO_SUCH_PAGE);
    }
    else {
      Graphics2D g2d = (Graphics2D) g;
      scalePage(g2d, pageFormat);
      disableDoubleBuffering(componentToBePrinted);
      componentToBePrinted.paint(g2d);
      enableDoubleBuffering(componentToBePrinted);
      return (PAGE_EXISTS);
    }
  }

  private void scalePage(Graphics2D g2, PageFormat pageformat) {
    Rectangle componentBounds = this.componentToBePrinted.getBounds(null);
    double scaleX = pageformat.getImageableWidth() / componentBounds.width;
    double scaleY = pageformat.getImageableHeight() / componentBounds.height;
    System.out.println("Scale: " + scaleX + " " + scaleY);
    if (scaleX < 1 || scaleY < 1) {
      if (scaleX < scaleY) {
        scaleY = scaleX;
      }
      else {
        scaleX = scaleY;
      }
      g2.translate(pageformat.getImageableX(), pageformat.getImageableY());
      g2.scale(scaleX, scaleY);
    }

  }

  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }
}
