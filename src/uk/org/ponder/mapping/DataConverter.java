/*
 * Created on 2 Aug 2007
 */
package uk.org.ponder.mapping;

import uk.org.ponder.mapping.support.DARApplier;


/** Declaration of a policy on data conversion, applicable by the 
 * {@link DARApplier} for incoming requests, as well as for calls to
 * {@link DARApplier#getFlattenedValue(String, Object, Class, uk.org.ponder.beanutil.BeanResolver).
 * Note that this is somewhat similar to the Spring structure PropertyEditorRegistry,
 * but has slightly different semantics, as well as accepting other forms of
 * converters than PropertyEditors.
 * 
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public class DataConverter {
  private Class targetClass;
  
  private String targetPath;

  private Object converter;
  
  private String converterEL;

   public Class getTargetClass() {
    return targetClass;
  }
   /** If set, the converter will apply to all classes of the specified type
    * targetted within the model. At least one of this property and {@link #setTargetPath(String)} must be set.
    * If they are <i>both</i> set, the interpretation of targetPath is of a 
    * <i>relative</i> path to any instance of the specified class discovered. 
    */
  public void setTargetClass(Class targetClass) {
    this.targetClass = targetClass;
  }

  public String getTargetPath() {
    return targetPath;
  }

  /** If set, the converter will apply to beans at the specified path
   * expression which are targetted within the model. This path expression
   * may contain wildcard segments such as .*.
   * @param targetPath
   */
  public void setTargetPath(String targetPath) {
    this.targetPath = targetPath;
  }

  public Object getConverter() {
    return converter;
  }
  /** Some Object which can be converted to a DARReshaper or BeanResolver - 
   * supported are PropertyEditor and LeafObjectParser.
   * @param converter
   */
  public void setConverter(Object converter) {
    this.converter = converter;
  }

  public String getConverterEL() {
    return converterEL;
  }

  /** An EL path from which the converter can be fetched **/
  public void setConverterEL(String converterEL) {
    this.converterEL = converterEL;
  }
}
