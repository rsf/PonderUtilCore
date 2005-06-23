/*
 * Created on Nov 26, 2004
 */
package uk.org.ponder.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

import uk.org.ponder.stringutil.StringList;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ReflectUtils {
  public static final boolean PREFIX = true;
  public static final boolean SUFFIX = false;
  
  public static boolean isPublicStatic(int modifiers) {
    return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers);
  }
  
  public static void addStaticStrings(Object provider, String xfix, boolean prefix, StringList strings) {
    Class provclass = provider instanceof Class ? (Class) provider
        : provider.getClass();
    Field[] fields = provclass.getFields();
    for (int j = 0; j < fields.length; ++j) {
      Field field = fields[j];
      try {
        int modifiers = field.getModifiers();
        if (!isPublicStatic(modifiers))
          continue;
        String fieldname = field.getName();
        if ((prefix? fieldname.startsWith(xfix) : 
          fieldname.endsWith(xfix)) && field.getType() == String.class) {
          strings.add(field.get(provider));
        }
      }
      catch (Throwable t) {
        Logger.log.fatal("Error reflecting for static names ", t);
      }
    }
  }
}
