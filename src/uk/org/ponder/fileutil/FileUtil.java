/*
 * Created on Oct 4, 2005
 */
package uk.org.ponder.fileutil;

import java.io.File;
import java.io.IOException;

import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.util.UniversalRuntimeException;

public class FileUtil {
  public static int FILE_MASK = 1;
  public static int DIRECTORY_MASK = 2;
  
  static public StringList getListing(File aStartingDir, int mask) {
    StringList result = new StringList();

    File[] filesAndDirs = aStartingDir.listFiles();
    if (filesAndDirs == null) {
      return result;
    }
   
    for (int i = 0; i < filesAndDirs.length; ++ i) {
      File file = filesAndDirs[i];
      if (!file.isFile()) {
        if ((mask & DIRECTORY_MASK) != 0) {
          result.add(file.toString());
        }
        StringList deeperList = getListing(file, mask);
        result.addAll(deeperList);
      }
      else {
        if ((mask & FILE_MASK) != 0) {
          result.add(file.toString());
        }
      }

    }
    //Collections.sort(result);
    return result;
  }
  
  public static final String getCanonicalPath(String path) {    
    File f = new File(path);
    try {
      return f.getCanonicalPath();
    }
    catch (IOException e) {
      throw UniversalRuntimeException.accumulate(e, "Unable to convert " + path + " to a canonical path");
    }
  }
}
