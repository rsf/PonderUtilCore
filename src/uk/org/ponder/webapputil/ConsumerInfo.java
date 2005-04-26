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
  // NB - urlbase, extraparameters and externalURL may vary between resources
  // for the same consumer. We expect everything else to be consumer-static.
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
  
  /** The URL to be issued by a user out in the wild to navigate the hosting
   * environment to the point where the tool instance corresponding to this
   * resource is visible. If this is null, the tool URL itself is presumed
   * to be valid (as per Coursework) */
  public String externalURL;
  /** The URL base required
   * for statically served resource URLs written to a remote consumer 
   * during this request cycle. Note that this may not refer to the same
   * webapp or machine as the URL base above. We don't expect requests
   * for these URLs to pass through the dispatch of the remote consumer,
   * but be resolved directly.
   */
  public String resourceurlbase;
}
