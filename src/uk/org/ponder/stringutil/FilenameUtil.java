/*
 * Created on 03-Jan-2004
 */
package uk.org.ponder.stringutil;

/**
 * @author Bosmon
 *
 * The class FilenameUtil contains various useful utility methods for operating on filenames.
 */
public class FilenameUtil {
  public static final String filesep = System.getProperty("file.separator");
  public static String getDirectory(String filename) {
    int lastslashpos = filename.lastIndexOf(filesep);
    return lastslashpos == -1? "" : filename.substring(0, lastslashpos + 1);
  }
  public static String getExtension(String filename) {
    int lastdotpos = filename.lastIndexOf('.');
    return lastdotpos == -1? "" : filename.substring(lastdotpos + 1, filename.length());
  }
  /** Return the stem of the supplied filename, containing the portion of the filename
   * up to the last "." character, or the entire filename if no "." character is 
   * found.
   * @param filename The filename for which the stem is required.
   * @return The stem of the filename.
   */
  public static String getStem(String filename) {
    int lastdotpos = filename.lastIndexOf('.');
    return lastdotpos == -1? filename : filename.substring(0, lastdotpos);
  }
}
