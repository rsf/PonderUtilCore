/*
 * Created on Feb 8, 2005
 */
package uk.org.ponder.webapputil;

/**
 * Request-static information stored about a consumer of this service. 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ConsumerInfo {
  public String consumerid; // also "consumerprefix"
  public String consumername;
  /** The FULL URL for requests made to the consumer's Information Servlet */
  public String informationbase;
  public String consumertype;
  /** The URL base required
   * for URLs holding dynamic content written to a remote consumer during 
   * this request cycle. This URL includes a trailing slash.
   */
  public String urlbase;
  /** Any other parameters required, together with urlbase, to render a 
   * correct consumer-side URL. This begins with an ampersand and 
   * consists of fully-encoded parameter pairs ready to be postpended
   * to a parameter string.*/
  public String extraparameters;
  /** The URL base required
   * for statically served resource URLs written to a remote consumer 
   * during this request cycle. Note that this may not refer to the same
   * webapp or machine as the URL base above. We don't expect requests
   * for these URLs to pass through the dispatch of the remote consumer,
   * but be resolved directly.
   */
  public String resourceurlbase;
}
