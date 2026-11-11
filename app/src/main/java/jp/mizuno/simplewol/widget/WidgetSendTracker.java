package jp.mizuno.simplewol.widget;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class WidgetSendTracker {
    private final AtomicLong next = new AtomicLong();
    private final ConcurrentHashMap<Integer, Long> active = new ConcurrentHashMap<>();

    public long begin(int widgetId) {
        long sequence = next.incrementAndGet();
        active.put(widgetId, sequence);
        return sequence;
    }

    public boolean isCurrent(int widgetId, long sequence) {
        return Long.valueOf(sequence).equals(active.get(widgetId));
    }

    public boolean finish(int widgetId, long sequence) {
        return active.remove(widgetId, sequence);
    }

    public void forget(int widgetId) {
        active.remove(widgetId);
    }
}
