package uk.org.ponder.util;

/** A fully synchronized queue of objects, which is dynamically expandable.
 */
public class SynchroQueue {
  private Object[] items;
  private int head; // points to the position a new object will be added
  private int tail; // points to the position object will be read and removed
  /** Construct a new SynchroQueue with the specified initial buffer size.
   * @param size The initial size for the buffer of queued objects.
   */
  public SynchroQueue(int size) {
    items = new Object[size];
    }
  private void reallocate() {
    int newsize = items.length * 2;
    Object[] newarray = new Object[newsize];
    if (tail < head) { // right way round
      System.arraycopy(items, tail, newarray, 0, head - tail);
      head = head - tail;
      }
    else { // wrapped
      System.arraycopy(items, tail, newarray, 0, items.length - tail);
      System.arraycopy(items, 0, newarray, items.length - tail, head);
      head = head - tail + items.length;
      }
    items = newarray;
    tail = 0;
    }
  // multiple threads putting, exclude each other with puts, but not with gets
  // which can continue concurrently??? What benefit this, since takes so little
  // time in these methods...
  /** Place an object on this queue, and wake up all threads (by notifyAll()) that
   * are blocking on ourselves.
   * @param toput The object to be placed on the queue.
   */
  public synchronized void put(Object toput) {
    Logger.println("Synchroqueue: item "+toput+" at index "+head
		       +", queue size "+queued(), Logger.DEBUG_INFORMATIONAL);
    int nexthead = (head + 1) % items.length;
    if (nexthead == tail) {
      reallocate();
      nexthead = head + 1;
      }
    items[head] = toput;
    head = nexthead;
    notifyAll();
    }

  /** Return the number of objects currently in the queue.
   * @return The number of objects in the queue.
   */

  public synchronized int queued() {
    Logger.println("queued: head "+head+" tail "+tail, Logger.DEBUG_INFORMATIONAL);
    int togo = (head - tail);
    return togo < 0? togo + items.length : togo;
    }

  /** Return the next object at the head of the queue, but block indefinitely, 
   * synchronized on ourselves, if none is available, until any are available.
   * @return The next object on the queue.
   */
  public synchronized Object getBlock() {
    if (head == tail) {
      try {
	wait();
	}
      catch (InterruptedException ie) {}
      }
    Object togo = items[tail];
    tail = (tail + 1) % items.length;
    return togo;
    }
  }
