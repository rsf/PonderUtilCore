/*
 * Created on Dec 23, 2004
 */
package uk.org.ponder.webapputil;

import java.util.Date;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class AgentStaleness {
  public String agentid;
  public Date staleness;
  public AgentStaleness() {}
  public AgentStaleness(String agentid, Date staleness) {
    this.agentid = agentid;
    this.staleness = staleness;
  }
  public AgentStaleness copy() {
    AgentStaleness togo = new AgentStaleness();
    togo.agentid = agentid;
    togo.staleness = staleness;
    return togo;
  }
}
