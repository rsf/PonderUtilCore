/*
 * Created on Feb 2, 2005
 */
package uk.org.ponder.webapputil;


/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ConsumerRequestInfo {
  private static ThreadLocal consumerrequestinfo = new ThreadLocal();
  public static ConsumerRequestInfo getConsumerRequestInfo() {
    return (ConsumerRequestInfo)consumerrequestinfo.get();
  }
  public static ConsumerRequestInfo initConsumerRequestInfo() {
    ConsumerRequestInfo newinfo = new ConsumerRequestInfo();
    consumerrequestinfo.set(newinfo);
    return newinfo;
  }
  public static void clearConsumerRequestInfo() {
    consumerrequestinfo.set(null);
  }
  
  public static String getInstallationName(String defaultname) {
    String useinstallationname = defaultname;
    
    ConsumerRequestInfo cri = getConsumerRequestInfo();
    if (cri != null && cri.ci.consumername != null) {
      useinstallationname = cri.ci.consumername;
    }
    return useinstallationname;
  }
  
  public ConsumerInfo ci;
  /** A view template provided by the remote consumer with which this
   * response is to be framed.
   */
  public String viewtemplate;
  /** A URL returned by the system to be the target of a redirect issued
   * to the client. In fact, this will be delivered as the body of the
   * POST response, which otherwise is traditionally blank.
   */
  //public String returnredirect;
}
