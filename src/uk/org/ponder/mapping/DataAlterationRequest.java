/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class DataAlterationRequest {
  public static final String ADD = "add";
  public static final String DELETE = "delete";
  public String path;
  public Object data;
  public String type = ADD;
  
  public DataAlterationRequest(String path, Object data) {
    this.path = path;
    this.data = data;
  }
  public DataAlterationRequest(String path, Object data, String type) {
    this.path = path;
    this.data = data;
    this.type = type;
  }
}
