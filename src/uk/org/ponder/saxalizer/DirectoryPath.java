package uk.org.ponder.saxalizer;

import uk.org.ponder.util.Logger;

import uk.org.ponder.stringutil.CharWrap;

// Terrible, terrible temporary class to allow file paths relative
// to applet codebase. SAXLeafParser must be reformed to allow the
// public to add classes from the outside

/** Directory path is a SAX leaf type representing a local filesystem path.
 * The purpose of this class is to allow variable substitution within the path.
 * The only currently supported variable is <code>$CODEBASE</code>, which 
 * expands to the directory on the <code>CLASSPATH</code> from which the
 * executing code was read.
 */
public class DirectoryPath implements SAXLeafTypeParser {
  static {
    SAXLeafParser.instance().registerParser(DirectoryPath.class, new DirectoryPath());
    }
  public String directoryspec;
  public String path;
  private static String codebase;
  private static String codebase_var = "($CODEBASE)";
  private static String fileURL = "file:/";
  /** Set the codebase path to the specified path. This is the string to which the
   * variable <code>$CODEBASE</code> will expand.
   * @param codebase The required codebase path.
   */
  public static void setCodeBase(String codebase) {
    DirectoryPath.codebase = codebase;
    if (codebase.startsWith(fileURL)) {
      DirectoryPath.codebase = codebase.substring(fileURL.length(), codebase.length());
      }
    else DirectoryPath.codebase = codebase;
    }
  public Object parse(String toparse) {
    DirectoryPath togo = new DirectoryPath();    
    togo.directoryspec = toparse;
    // replace any leading copy of the codebase variable with the actual codebase
    int i = toparse.indexOf(codebase_var);
    if (i == -1) togo.path = toparse;
    else {
      CharWrap sb = new CharWrap();
      sb.append(toparse.substring(0, i));
      sb.append(codebase);
      sb.append(toparse.substring(i + codebase_var.length()));
      togo.path = sb.toString();
      Logger.println("Replacement var found: setting dir "+togo.path,
		     Logger.DEBUG_INFORMATIONAL);
      }
    return togo;
    }

  public CharWrap render(Object torendero, CharWrap renderinto) {
    DirectoryPath torender = (DirectoryPath) torendero;
    return renderinto.append(torender.directoryspec);
    }
  }
