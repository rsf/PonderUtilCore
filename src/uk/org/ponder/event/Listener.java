package uk.org.ponder.event;

/** The interface Listener is implemented by classes who wish to receive events.
 * The only method specified by this interface enables Listeners to receive
 * the event fired.
 */

public interface Listener {
    /// Receive the Event fired.
    public void receiveEvent(EventTag base);
}
