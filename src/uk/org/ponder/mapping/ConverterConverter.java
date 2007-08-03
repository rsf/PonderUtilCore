/*
 * Created on 3 Aug 2007
 */
package uk.org.ponder.mapping;

import java.beans.PropertyEditor;

import uk.org.ponder.beanutil.BeanResolver;
import uk.org.ponder.conversion.LeafObjectParser;

/** Converts between various forms of data converters **/

public class ConverterConverter {
  public static DARReshaper toReshaper(Object o) {
    if (o instanceof DARReshaper) {
      return (DARReshaper) o;
    }
    else if (o instanceof LeafObjectParser) {
      return new LeafObjectDARReshaper((LeafObjectParser) o);
    }
    if (o instanceof PropertyEditorFactory) {
      o = ((PropertyEditorFactory)o).getPropertyEditor();
    }
    if (o instanceof PropertyEditor) {
      final PropertyEditor pe = (PropertyEditor) o;
      return new DARReshaper() {

        public DataAlterationRequest reshapeDAR(DataAlterationRequest toshape) {
          String data = DataAlterationRequest.flattenData(toshape.data);
          pe.setAsText(data);
          DataAlterationRequest togo = new DataAlterationRequest(toshape.path, 
              pe.getValue(), toshape.type);
          return togo;
        }};
    }
    else return null;
  }
  
  public static BeanResolver toResolver(Object o) {
    if (o instanceof BeanResolver) {
      return (BeanResolver) o;
    }
    else if (o instanceof LeafObjectParser) {
      final LeafObjectParser parser = (LeafObjectParser) o;
      return new BeanResolver() {
        public String resolveBean(Object bean) {
          return parser.render(bean);
        }
      };
    }
    if (o instanceof PropertyEditorFactory) {
      o = ((PropertyEditorFactory)o).getPropertyEditor();
    }
    if (o instanceof PropertyEditor) {
      final PropertyEditor pe = (PropertyEditor) o;
      return new BeanResolver() {
        public String resolveBean(Object bean) {
          pe.setValue(bean);
          return pe.getAsText();
        }};
    }
    else return null;
  }
}
