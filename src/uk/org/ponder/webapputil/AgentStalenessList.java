/*
 * Created on Dec 23, 2004
 */
package uk.org.ponder.webapputil;

import java.util.ArrayList;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class AgentStalenessList extends ArrayList {
  public AgentStaleness stalenessAt(int i) {
    return (AgentStaleness) get(i);
  }
}
