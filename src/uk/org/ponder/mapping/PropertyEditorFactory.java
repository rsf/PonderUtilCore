/*
 * Created on 3 Aug 2007
 */
package uk.org.ponder.mapping;

import java.beans.PropertyEditor;

/** A factory interface returning PropertyEditors*/

public interface PropertyEditorFactory {
  public PropertyEditor getPropertyEditor();
}
