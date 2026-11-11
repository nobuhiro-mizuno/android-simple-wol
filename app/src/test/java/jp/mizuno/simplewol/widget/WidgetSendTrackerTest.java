package jp.mizuno.simplewol.widget;

import org.junit.Test;
import static org.junit.Assert.*;

public class WidgetSendTrackerTest {
    @Test public void newerTapKeepsItsOwnStatus() {
        WidgetSendTracker tracker = new WidgetSendTracker();
        long first = tracker.begin(10);
        long second = tracker.begin(10);
        assertFalse(tracker.isCurrent(10, first));
        assertFalse(tracker.finish(10, first));
        assertTrue(tracker.isCurrent(10, second));
        assertTrue(tracker.finish(10, second));
        assertFalse(tracker.isCurrent(10, second));
    }

    @Test public void widgetsHaveIndependentStatus() {
        WidgetSendTracker tracker = new WidgetSendTracker();
        long first = tracker.begin(10);
        long second = tracker.begin(20);
        tracker.forget(10);
        assertFalse(tracker.isCurrent(10, first));
        assertTrue(tracker.isCurrent(20, second));
    }
}
