package uk.org.ponder.swingutil;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Container;
import java.awt.Component;

/** The GBLWrap class wraps up the functionality of the complex GridBagLayout and 
 * GridBagConstraints classes into a convenient package, saving large amounts of
 * repetitive client code*/

public class GBLWrap {
  GridBagLayout gbl;
  public GridBagConstraints gbc = new GridBagConstraints();
  Container target;
	/// Create a new GBL wrapper targetted at a particular container. The layout of the target container is set to a GridBagLayout and the wrapper object is initialised ready to begin adding subcomponents to the container at its top left corner.
  public GBLWrap(Container target) {
    this.target = target;
    target.setLayout(gbl = new GridBagLayout());
    gbc.weightx = gbc.weighty = 1;
    }
	/// Adds a new component to the container layout with the specified alignment. The component is added in the next available position in the left-to-right, top-to-bottom scanning order, with either left alignment ("l"), centered alignment ("c") or right alignment ("r").
  public GBLWrap apply(Component toadd, String align) {
    gbc.anchor = GridBagConstraints.CENTER;
    if (align.equals("l")) {
      gbc.anchor = GridBagConstraints.WEST;
      }
    else if (align.equals("c")) {
      gbc.anchor = GridBagConstraints.CENTER;
      }
    else if (align.equals("r")) {
      gbc.anchor = GridBagConstraints.EAST;
      }
    gbl.setConstraints(toadd, gbc);
    target.add(toadd);
    reset();
    return this;
    }
	/// This call is issued by the client immediately prior to an apply() call to indicate the next added component will be the last in its row.
  public GBLWrap endrow() {
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    return this;
    }
	/// This call is issued by the client immediately prior to an apply() call to indicate the next component will be the last in its column.
  public GBLWrap endcol() {
    gbc.gridheight = GridBagConstraints.REMAINDER;
    return this;
    }
	/// This call is issued by the client to clear any state in the GBLWrap object set by a prior call to endrow() or endcol(). This method is called automatically by apply().
  public GBLWrap reset() {
    gbc.gridwidth = gbc.gridheight = 1;
    return this;
    }
  }


