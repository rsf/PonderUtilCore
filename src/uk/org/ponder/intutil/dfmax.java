package uk.org.ponder.intutil;

/** Class dfmaxstate embodies the state of the clique-finding algorithm, together
 * with lower-level logic.
 */

class dfmaxstate {
  private intVector vertexstack;
  private intVector stacklevellimits;
  private intVector activepointers;
  private intVector currentclique;

  /** Initialize state with a complete level of all vertices at the bottom of the stack.
   * @param size the number of vertices in the graph.
   */
  dfmaxstate(int size) {
    vertexstack = new intVector(size * size / 4);
    stacklevellimits = new intVector(size);
    activepointers = new intVector(size);
    currentclique = new intVector(size);

    for (int i = 0; i < size; ++ i) {
      vertexstack.addElement(i);
      }
    stacklevellimits.addElement(size);
    activepointers.addElement(0);
    currentclique.addElement(0);
    }

  /** Dump the stack to System.out for debugging purposes
   */

  void diagnose() {
    for (int depth = 0; depth < stacklevellimits.size(); ++ depth) {
      System.out.print("Depth "+depth+": ");
      int vertexstart = depth == 0? 0 : stacklevellimits.intAt(depth - 1);
      int vertexend = stacklevellimits.intAt(depth);
      int activevertex = activepointers.intAt(depth);
      for (int vertex = vertexstart; vertex < vertexend; ++vertex) {
	System.out.print( (vertex == activevertex? "*":"") + vertexstack.intAt(vertex)+" ");
	}
      System.out.println();
      }
    }
  
  /** Attempts to push a new frame on the stack, composed of vertices that
   * form a clique together with currentclique. If none are found, state is
   * left unchanged.
   * @param adjacent The adjacency matrix specifying the graph. Must be square
   * and symmetric.
   * @return <code>true</code> if any new vertices were found.
   */

  boolean push(boolean[][] adjacent) {
    int topvertex = vertexstack.intAt(activepointers.peek());
    int currentcliquesize = currentclique.size();
    int oldstacktop = vertexstack.size();
    int size = adjacent.length;
    System.out.print("Push scanning from current clique: "+currentclique+": ");
    // seek out all new vertices that can form a clique with the existing currentclique,
    // and push them onto the stack one by one.
    for (int scan = topvertex + 1; scan < size; ++ scan) {
      boolean formedclique = true; // this ensures that 1 vertex is always a clique.
      for (int check = 0; check < currentcliquesize; ++ check) {
	if (!adjacent[scan][currentclique.intAt(check)]) {
	  formedclique = false;
	  break;
	  }
	}
      if (formedclique) {
	System.out.print(scan + " ");
	vertexstack.addElement(scan);
	}
      }
    System.out.println();
    // if we found no vertices, push failed.
    if (vertexstack.size() == oldstacktop) return false;
    // otherwise, adjust the stack pointers to the new state and return.
    stacklevellimits.addElement(vertexstack.size());
    activepointers.addElement(oldstacktop);
    currentclique.addElement(vertexstack.intAt(oldstacktop));
    return true;
    }

  /** Attempt to find a new clique by incrementing the active pointer into the top
   * stack frame. 
   * @return <code>true</code> if a new clique was found in this way. Otherwise,
   * return <code>false</code> if the top stack frame was exhausted.
   */
  boolean shove() {
    if (activepointers.peek() < vertexstack.size() - 1) {
      int topindex = activepointers.size() - 1;
      activepointers.setIntAt(topindex, activepointers.intAt(topindex) + 1);
      currentclique.setIntAt(topindex, vertexstack.intAt(activepointers.intAt(topindex)));
      return true;
      }
    else return false;
    }
  
  /** Removes a frame from the stack, reducing clique size by one.
   * @return the new stack frame level.
   */
  
  int pop() {
    stacklevellimits.popElement();
    activepointers.popElement();
    currentclique.popElement();
    vertexstack.setSize(stacklevellimits.isEmpty()? 0 : stacklevellimits.peek());
    return currentclique.size();
    }
    

  /** Tests whether the current clique is a new best clique, and also determines whether
   * the pruning criterion has been met.
   * @param bestcliquesofar The best clique that has been found so far. If the newly presented
   * clique is larger, this vector will be overwritten with the new clique.
   * @return <code>true</code> if it is worthwhile continuing to expand the newly
   * presented clique
   */

  boolean check(intVector bestcliquesofar) {
    if (currentclique.size() > bestcliquesofar.size()) {
      System.out.println("New biggest clique found of size: "+currentclique.size());
      System.out.println(currentclique);
      bestcliquesofar.assign(currentclique);
      return true;
      }
    // the pruning condition is d + ( m - i ) <= bestcliquesize.
    else return currentclique.size() + 
	   (stacklevellimits.peek() - activepointers.peek()) > bestcliquesofar.size();
    }

  }

/** Class dfmax embodies the main logic loop of the clique-finding algorithm,
 * implemented as a state machine with 4 states.
 */

class dfmax {
  private static final int STATE_SHOVE = 0;
  private static final int STATE_POP = 1;
  private static final int STATE_CHECK = 2;
  private static final int STATE_PUSH = 3;

  public static intVector bestClique(boolean[][] adjacent) {
    if (adjacent.length != adjacent[0].length) {
      System.out.println("Supply a square adjacency matrix you numskull");
      System.exit(1); // since I am no longer writing WaX I can use this fine exception
      // handling strategy.
      }

    dfmaxstate dfmaxstate = new dfmaxstate(adjacent.length);
    intVector bestcliquesofar = new intVector(adjacent.length);
    int state = STATE_CHECK;
    outer:
    while (true) {
      dfmaxstate.diagnose();
      switch(state) {
      case STATE_CHECK:
	System.out.println("STATE_CHECK: ");
	if (dfmaxstate.check(bestcliquesofar)) {
	  // check says we may continue trying to expand the clique.
	  state = STATE_PUSH;
	  }
	else {
	  System.out.println("PRUNE!!!");
	  // clique was pruned - pop off this stack frame.
	  // in order to revert to exhaustive search, convert this line to
	  // state = STATE_PUSH.
	  state = STATE_POP;
	  }
	break;
      case STATE_PUSH:
	System.out.println("STATE_PUSH: ");
	if (dfmaxstate.push(adjacent)) { 
	  // we got a new stack frame, check it.
	  state = STATE_CHECK;
	  }
	else {
	  // we didn't, try to step along one in this one.
	  state = STATE_SHOVE;
	  }
	break;
      case STATE_POP:
	System.out.println("STATE_POP: ");
	if (dfmaxstate.pop() != 0) {
	  // popped off a stack frame, and still something left.
	  state = STATE_SHOVE;
	  }
	else break outer;
      case STATE_SHOVE:
	System.out.println("STATE_SHOVE: ");
	if (dfmaxstate.shove()) {
	  // we got a new clique, check it.
	  state = STATE_CHECK;
	  }
	else {
	  // no more cliques left in this frame, pop.
	  state = STATE_POP;
	  }
	}
      }
    System.out.println("Clique found: "+bestcliquesofar);
    System.out.println("===============================");
    return bestcliquesofar;
    }
  public static void main(String[] argv) {
    boolean[][] simple02 = new boolean[][] { {true, false, true},
					     {false, true, false},
					     {true, false, true}};
    dfmax.bestClique(simple02);
    boolean[][] simple012 = new boolean[][] { {true, true, true},
					      {true, true, true},
					      {true, true, true}};
    dfmax.bestClique(simple012);
    boolean[][] simpledis = new boolean[][] { {true, false, false},
					      {false, true, false},
					      {false, false, true}};
    dfmax.bestClique(simpledis);


    boolean[][] example = new boolean[][] {
      { true , true , false, false, true, true, false, false},
      { true , true, false, false, false, true, true, false},
      { false, false, true, true, false, true, true, false},
      { false, false, true, true, true, true, false, true},
      { true, false, false, true, true, true, true, true},
      { true, true, true, true, true, true, true, true},
      {false, true, true, false, true, true, true, true},
      {false, false, false, true, true, true, true, true}};
    dfmax.bestClique(example);
    }
  }

/* Index mapping table:
 * Example  Ours       Ours  Example
 *    1      0           0      1
 *    5      1           4      2
 *    8      2           3      3
 *    3      3           5      4
 *    2      4           1      5
 *    4      5           6      6
 *    6      6           7      7
 *    7      7           2      8 
 *
 * 1 ->245 = 0 ->451    2 ->17643 = 4 ->07653    3 ->2874 = 3 ->4275
 * 4 ->3218765 = 5 ->3402761    5 ->416 = 1 ->506    6 ->54827 = 6 ->15247
 * 7 ->6432 = 7 ->6534   8 ->346 = 2 ->356
 *
 *   01234567   Symmetric, checked.
 *  0@@..@@..
 *  1@@...@@.
 *  2..@@.@@.
 *  3..@@@@.@
 *  4@..@@@@@
 *  5@@@@@@@@
 *  6.@@.@@@@
 *  7...@@@@@

 * 3457 = 3247, correct.
 */

/**   D               a b   0111 dual graph
      g               g d   1011
  A a B b C                 1101
      d                     1111
      E
*/

/*  AB C DE   matches    AB Q DE, but AB and DE are not connected. */
