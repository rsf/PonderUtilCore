/*
 * Created on 3 Aug 2007
 */
package uk.org.ponder.mapping;

import java.beans.PropertyEditor;

import uk.org.ponder.mapping.support.DataConverterRegistry;

/** A factory interface returning PropertyEditors. This is recognised by the
 * {@link DataConverterRegistry} as a possible convertor source for a particular
 * model modification. The factory is required since PropertyEditors are stateful
 * and must be generated afresh for each application.
 * */

public interface PropertyEditorFactory {
  public PropertyEditor getPropertyEditor();
}
